package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingCountRequest {

    private AuthToken authToken;
    private User targetUser;

    public FollowingCountRequest(){
    }

    public FollowingCountRequest(AuthToken authToken, User targetUser){
        this.authToken = authToken;
        this.targetUser = targetUser;
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

    public void setTargetUser(User unfollowUser) {
        this.targetUser = unfollowUser;
    }
}
