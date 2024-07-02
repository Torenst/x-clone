package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.observer.PagedItemObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagedPresenter<User>{

    public FollowersPresenter(PagedView<User> view, User targetUser, AuthToken authToken) {
        super(view, targetUser, authToken);
    }

    @Override
    public void getItems(AuthToken authToken, User targetUser, int pageSize, User lastItem) {
        followService().getFollowers(authToken, targetUser, pageSize, lastItem, new GetFollowerObserver());
    }

    public class GetFollowerObserver extends PagedObserver implements PagedItemObserver<User> {
        @Override
        protected String getDescription() {
            return "retrieve followers";
        }
    }
}

