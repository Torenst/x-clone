package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.observer.AuthenticationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class AuthenticatedUserHandler extends BackgroundTaskHandler<AuthenticationObserver> {

    public AuthenticatedUserHandler(AuthenticationObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(AuthenticationObserver observer, Bundle data) {
        User user = (User) data.getSerializable(LoginTask.USER_KEY);
        AuthToken authToken = (AuthToken) data.getSerializable(LoginTask.AUTH_TOKEN_KEY);

        observer.handleSuccess(user, authToken);
    }
}
