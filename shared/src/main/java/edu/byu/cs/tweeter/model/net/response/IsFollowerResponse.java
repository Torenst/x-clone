package edu.byu.cs.tweeter.model.net.response;

public class IsFollowerResponse extends Response{

    private Boolean isFollower;

    public IsFollowerResponse(String message){
        super(false, message);
    }

    public IsFollowerResponse(boolean state){
        super(true);
        this.isFollower = state;
    }

    public boolean getIsFollower() {
        return isFollower;
    }
}
