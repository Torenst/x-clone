package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UnfollowRequest {

    private AuthToken authToken;
    private User unfollowUser;
    private User currentUser;

    public UnfollowRequest(){
    }

    public UnfollowRequest(AuthToken authToken, User unfollowUser, User currentUser){
        this.authToken = authToken;
        this.unfollowUser = unfollowUser;
        this.currentUser = currentUser;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public User getUnfollowUser() {
        return unfollowUser;
    }

    public void setUnfollowUser(User unfollowUser) {
        this.unfollowUser = unfollowUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}
