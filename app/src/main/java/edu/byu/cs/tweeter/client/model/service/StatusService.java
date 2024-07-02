package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PagedHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.observer.PagedItemObserver;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService {

    public StatusService() {
    }

    public void getFeed(AuthToken authToken, User targetUser, int limit, Status lastStatus, PagedItemObserver<Status> observer) {
        BackgroundTaskUtils.runTask(new GetFeedTask(authToken, targetUser, limit, lastStatus, new PagedHandler<>(observer)));
    }

    public void getStory(AuthToken authToken, User targetUser, int limit, Status lastStatus, PagedItemObserver<Status> observer) {
        BackgroundTaskUtils.runTask(new GetStoryTask(authToken, targetUser, limit, lastStatus, new PagedHandler<>(observer)));
    }

    public void postStatus(AuthToken authToken,Status status, SimpleNotificationObserver observer){
        BackgroundTaskUtils.runTask(new PostStatusTask(authToken, status, new SimpleNotificationHandler(observer)));
    }

}
