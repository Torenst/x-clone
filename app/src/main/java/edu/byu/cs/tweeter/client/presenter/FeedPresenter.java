package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.observer.PagedItemObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter<Status> {

    public FeedPresenter(PagedView<Status> view, User user, AuthToken authToken){
        super(view, user, authToken);
    }

    @Override
    public void getItems(AuthToken authToken, User targetUser, int pageSize, Status lastItem) {
        statusService().getFeed(authToken,targetUser,pageSize,lastItem,new GetFeedObserver());
    }

    public class GetFeedObserver extends PagedObserver implements PagedItemObserver<Status> {
        @Override
        protected String getDescription() {
            return "retrieve status";
        }
    }
}
