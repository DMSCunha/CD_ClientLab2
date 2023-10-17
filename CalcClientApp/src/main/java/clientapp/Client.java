package clientapp;

import calcstubs.*;
import calcstubs.Number;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.*;

import static java.lang.Thread.sleep;


public class Client {

    private static String svcIP = "localhost";
    //private static String svcIP = "35.246.73.129";
    private static int svcPort = 8500;
    private static ManagedChannel channel;
    private static CalcServiceGrpc.CalcServiceBlockingStub blockingStub;
    private static CalcServiceGrpc.CalcServiceStub noBlockStub;


    public static void main(String[] args) {
        try {
            if (args.length == 2) {
                svcIP = args[0];
                svcPort = Integer.parseInt(args[1]);
            }
            System.out.println("connect to "+svcIP+":"+svcPort);
            channel = ManagedChannelBuilder.forAddress(svcIP, svcPort)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build();
            blockingStub = CalcServiceGrpc.newBlockingStub(channel);
            noBlockStub = CalcServiceGrpc.newStub(channel);

            while (true) {
                switch (Menu()) {
                    case 1:  // adicionar dois numeros
                        Result res = blockingStub.add(AddOperands.newBuilder()
                                .setId("50+25")
                                .setOp1(50).setOp2(25)
                                .build());
                        System.out.println("add " + res.getId() + "= " + res.getRes());
                        break;
                    case 2: // calcular as potencias de x^y (receber por stream)
                        StreamObserverResult response = new StreamObserverResult(2);
                        noBlockStub.generatePowers(NumberAndMaxExponent.newBuilder()
                                        .setId("numberrrrs")
                                        .setBaseNumber(2)
                                        .setMaxExponent(4)
                                        .build(),response);
                        break;
                    case 3: //somar a sequencia dos numeros de x a y (enviar stream)
                        StreamObserverResult respcase3 = new StreamObserverResult(3);
                        StreamObserver<Number> streamNumber = noBlockStub.addSeqOfNumbers(respcase3);
                        for(int i=1; i<=10;i++)
                            streamNumber.onNext(Number.newBuilder().setNum(i).build());
                        streamNumber.onCompleted();
                        break;
                    case 4: //sequencia de operacões de soma x + y (receber e enviar por stream)
                        StreamObserverResult respcase4 = new StreamObserverResult(4);
                        StreamObserver<AddOperands> streamNumb = noBlockStub.multipleAdd(respcase4);
                        for(int i=0; i<10;i++){
                            streamNumb.onNext(AddOperands.newBuilder().setId("ADD PLS")
                                    .setOp1(i)
                                    .setOp2(i+1)
                                    .build());
                        }
                        break;
                    case 99:
                        System.exit(0);
                    default:
                        break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static int Menu() {
        int op;
        Scanner scan = new Scanner(System.in);
        do {
            System.out.println();
            System.out.println("    MENU");
            System.out.println(" 1 - Case1 - chamada unária: add two numbers");
            System.out.println(" 2 - Case 2 - chamada com stream de servidor: generate powers");
            System.out.println(" 3 - Case 3 - chamada com stream de cliente: add a sequence of numbers");
            System.out.println(" 4 - stream de cliente e de servidor: Multiple add operations ");
            System.out.println("99 - Exit");
            System.out.println();
            System.out.println("Choose an Option?");
            op = scan.nextInt();
        } while (!((op >= 1 && op <= 4) || op == 99));
        return op;
    }


}
