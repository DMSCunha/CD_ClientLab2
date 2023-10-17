package clientapp;

import calcstubs.Result;
import io.grpc.stub.StreamObserver;

public class StreamObserverResult implements StreamObserver<Result> {

    private final int request;
    private boolean isCompleted=false;
    public boolean isCompleted(){
        return isCompleted;
    }


    public StreamObserverResult(int request){
        this.request = request;
    }

    @Override
    public void onNext(Result result) {
        System.out.println("Received["+this.request+"]: result = "+result.getRes());
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onCompleted() {
        System.out.println("Connection "+this.request+" DONE!!! All data received!");
        isCompleted = true;
    }
}
