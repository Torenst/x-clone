package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.observer.FollowTypeObserver;

public class IsFollowerHandler extends BackgroundTaskHandler<FollowTypeObserver> {

    public IsFollowerHandler(FollowTypeObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(FollowTypeObserver observer, Bundle data) {
        boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
        observer.handleSuccess(isFollower);
    }
}
