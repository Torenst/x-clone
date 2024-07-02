package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public interface AuthTokenInterface {
    void createAuthToken(AuthToken authToken);

    void updateAuthToken(AuthToken authToken);

    boolean authenticateUser(AuthToken currentAuthToken);

    void deleteAuthToken(AuthToken authToken);
}
