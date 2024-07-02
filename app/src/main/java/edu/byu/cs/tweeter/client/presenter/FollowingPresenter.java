package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.observer.PagedItemObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PagedPresenter<User>{

    public FollowingPresenter(PagedView<User> view, User user, AuthToken authToken) {
        super(view, user, authToken);
    }

    @Override
    public void getItems(AuthToken authToken, User targetUser, int pageSize, User lastItem) {
        followService().getFollowees(authToken, targetUser, pageSize, lastItem, new GetFollowerObserver());
    }

    public class GetFollowerObserver extends PagedObserver implements PagedItemObserver<User> {
        @Override
        protected String getDescription() {
            return "retrieve followees";
        }
    }
}
