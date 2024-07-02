package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status>{

    public StoryPresenter(PagedView<Status> view, User targetUser, AuthToken authToken) {
        super(view, targetUser, authToken);
    }

    @Override
    public void getItems(AuthToken authToken, User targetUser, int pageSize, Status lastItem) {
        statusService().getStory(authToken,targetUser,pageSize,lastItem, new GetStoryObserver() );
    }

    public class GetStoryObserver extends PagedObserver {
        @Override
        protected String getDescription() {
            return "retrieve statuses";
        }
    }
}
