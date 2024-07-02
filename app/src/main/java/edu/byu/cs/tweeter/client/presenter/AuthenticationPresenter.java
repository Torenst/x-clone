package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.AuthenticationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class AuthenticationPresenter extends BasePresenter<AuthenticationPresenter.AuthView>{

    private String username;
    private String password;

    @Override
    public void failureMessage(String failureType, String message) {
        String errorMessage = "Failed to " + failureType + ": " + message;

        view.displayFailureMessage(errorMessage);
    }

    @Override
    public void exceptionMessage(String exceptionType, Exception exception) {
        String exceptionMessage = "Failed to " + exceptionType + " because of exception: " + exception.getMessage();

        view.displayExceptionMessage(exceptionMessage, exception);
    }

    public static interface AuthView extends BaseView {
        void authenticated(User user);
    }

    public AuthenticationPresenter(AuthView view, String username, String password) {
        super(view);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public class AuthenticatedObserver extends Observer implements AuthenticationObserver {

        @Override
        public void handleSuccess(User user, AuthToken authToken) {

            // cache the user and auth
            Cache.getInstance().setCurrUser(user);
            Cache.getInstance().setCurrUserAuthToken(authToken);

            view.authenticated(user);
        }

        @Override
        protected String getDescription() {
            return "authenticate";
        }
    }

    public UserService userService(){
        return new UserService();
    }
}
