package edu.byu.cs.tweeter.client.model.service.observer;

public interface FollowTypeObserver extends ServiceObserver{

    void handleSuccess(boolean success);
}
