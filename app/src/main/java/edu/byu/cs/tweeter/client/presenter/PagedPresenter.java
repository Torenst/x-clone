package edu.byu.cs.tweeter.client.presenter;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.PagedItemObserver;
import edu.byu.cs.tweeter.client.model.service.observer.UserObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends BasePresenter<PagedPresenter.PagedView<T>> {

    public static final String USER_KEY = "UserKey";
    public static final int PAGE_SIZE = 10;

    protected AuthToken authToken;
    protected User targetUser;

    protected T lastItem;
    protected boolean hasMorePages = true;
    protected boolean isLoading = false;

    public static interface PagedView<T> extends BaseView {
        void setLoading(boolean value);

        void addItems(List<T> items);

        void userRetrievedSuccess(User user);
    }

    public PagedPresenter(PagedView<T> view, User targetUser, AuthToken authToken) {
        super(view);
        this.targetUser = targetUser;
        this.authToken = authToken;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public void setLastItem(T lastFollower) {
        this.lastItem = lastFollower;
    }

    public boolean isHasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    // intellij said this was always inverted so I accepted it, just in case this causes issues later.
    // Ok I changed it to not have the Inverted and now it still works??
    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public abstract void getItems(AuthToken authToken, User targetUser, int pageSize, T lastItem);

    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            setLoading(true);
            view.setLoading(true);
            getItems(authToken, targetUser, PAGE_SIZE, lastItem);
        }
    }

    public StatusService statusService(){
        return new StatusService();
    }

    public FollowService followService() {
        return new FollowService();
    }

    @Override
    public void failureMessage(String failureType, String message) {
        String errorMessage = "Failed to " + failureType + ": " + message;

        view.displayFailureMessage(errorMessage);
        setNotLoading();
    }

    @Override
    public void exceptionMessage(String exceptionType, Exception exception) {
        String exceptionMessage = "Failed to " + exceptionType + " because of exception: " + exception.getMessage();

        view.displayExceptionMessage(exceptionMessage, exception);
        setNotLoading();
    }

    private void setNotLoading(){
        view.setLoading(false);
        setLoading(false);
    }

    public abstract class PagedObserver extends Observer implements PagedItemObserver<T> {

        public void handleSuccess(List<T> items, boolean hasMorePages){
            setLastItem((items.size() > 0) ? items.get(items.size() - 1) : null);
            setHasMorePages(hasMorePages);

            view.setLoading(false);
            view.addItems(items);
            setLoading(false);
        }
    }

    public class GetUserObserver extends Observer implements UserObserver {

        @Override
        public void handleSuccess(User user) {
            Bundle msgBundle = new Bundle();
            msgBundle.putSerializable(USER_KEY, user);

            view.userRetrievedSuccess(user);
        }

        @Override
        protected String getDescription() {
            return "get user's profile";
        }
    }

    // this is here for mocking.
    public UserService userService(){
        return new UserService();
    }

    public void getUser(AuthToken authToken, String alias){
        userService().GetUsers(authToken, alias, new GetUserObserver());
    }
}
