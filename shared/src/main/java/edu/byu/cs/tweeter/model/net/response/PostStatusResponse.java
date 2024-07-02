package edu.byu.cs.tweeter.model.net.response;

public class PostStatusResponse extends Response{

    private long epocTime;

    public PostStatusResponse(String message){
        super(false, message);
    }

    public PostStatusResponse(long epochTime){
        super(true);
        this.epocTime = epochTime;
    }

    public long getEpocTime() {
        return epocTime;
    }

    public void setEpocTime(long epocTime) {
        this.epocTime = epocTime;
    }
}
