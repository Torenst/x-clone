package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.AuthenticatedUserHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetUserHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.model.service.observer.AuthenticationObserver;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.client.model.service.observer.UserObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;

public class UserService {

    public UserService() {
    }

    public void GetUsers(AuthToken authToken,String alias, UserObserver observer) {
        BackgroundTaskUtils.runTask(new GetUserTask(authToken, alias, new GetUserHandler(observer)));
    }

    public void LoginUser( String username, String password, AuthenticationObserver observer){
        BackgroundTaskUtils.runTask(new LoginTask(username,password,new AuthenticatedUserHandler(observer)));
    }

    public void registerUser(String firstName, String lastName, String username, String password,
                             String image, AuthenticationObserver observer){
        BackgroundTaskUtils.runTask(new RegisterTask(firstName,lastName,username,password,image, new AuthenticatedUserHandler(observer)));
    }

    public void logoutUser(AuthToken authToken, SimpleNotificationObserver observer){
        BackgroundTaskUtils.runTask(new LogoutTask(authToken, new SimpleNotificationHandler(observer)));
    }

}
