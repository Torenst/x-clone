package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.util.Pair;

public interface UserInterface {
    User register(RegisterRequest request);

    User getUser(String alias);

    Pair<String, String> getUserPasswordSalt(String alias);

    void addRemoveFollower(String userAlias, boolean addRemove);

    void addRemoveFollowing(String userAlias, boolean addRemove);

    int getUserFollowerCount(String alias);

    int getUserFollowingCount(String alias);

    boolean isNameTaken(String alias);

    void addUserBatch(List<User> users);
}
