package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.FollowCountHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.IsFollowerHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PagedHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.observer.CountObserver;
import edu.byu.cs.tweeter.client.model.service.observer.FollowTypeObserver;
import edu.byu.cs.tweeter.client.model.service.observer.PagedItemObserver;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {

    public FollowService() {
    }

    public void getFollowees(AuthToken authToken, User targetUser, int limit, User lastFollowee, PagedItemObserver<User> observer) {
        BackgroundTaskUtils.runTask(new GetFollowingTask(authToken, targetUser, limit, lastFollowee, new PagedHandler<>(observer)));
    }

    public void getFollowers(AuthToken authToken, User targetUser, int limit, User lastFollowee, PagedItemObserver<User> observer) {
        BackgroundTaskUtils.runTask(new GetFollowersTask(authToken, targetUser, limit, lastFollowee, new PagedHandler<>(observer)));
    }

    public void followUser(AuthToken authToken, User followee, User currentUser, SimpleNotificationObserver observer){
        BackgroundTaskUtils.runTask(new FollowTask(authToken, followee, currentUser, new SimpleNotificationHandler(observer)));
    }

    public void unfollowUser(AuthToken authToken, User followee,User currentUser, SimpleNotificationObserver observer){
        BackgroundTaskUtils.runTask(new UnfollowTask(authToken, followee, currentUser, new SimpleNotificationHandler(observer)));
    }

    public void getFollowerCount(AuthToken authToken, User user, CountObserver observer){
        BackgroundTaskUtils.runTask(new GetFollowersCountTask(authToken, user, new FollowCountHandler(observer)));
    }


    public void getFollowingCount(AuthToken authToken, User user, CountObserver observer){
        BackgroundTaskUtils.runTask(new GetFollowingCountTask(authToken, user, new FollowCountHandler(observer)));
    }

    public void isFollower(AuthToken authToken, User user, User followee, FollowTypeObserver observer){
        BackgroundTaskUtils.runTask(new IsFollowerTask(authToken, user, followee, new IsFollowerHandler(observer)));
    }

}

