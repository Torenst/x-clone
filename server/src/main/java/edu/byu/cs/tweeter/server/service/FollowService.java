package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenInterface;
import edu.byu.cs.tweeter.server.dao.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.FollowInterface;
import edu.byu.cs.tweeter.server.dao.UserInterface;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    private final FollowInterface followDAO;
    private final UserInterface userDAO;
    private final AuthTokenInterface authTokenDAO;

    public FollowService(DAOFactoryInterface factory) {
        followDAO = factory.createFollowDAO();
        userDAO = factory.createUserDAO();
        authTokenDAO = factory.createAuthTokenDAO();
    }

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        validateAuthToken(request.getAuthToken());

        return followDAO.getFollowees(request);
    }

    public FollowerResponse getFollowers(FollowerRequest request){
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        validateAuthToken(request.getAuthToken());

        return followDAO.getFollowers(request);
    }

    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */

    public FollowResponse follow(FollowRequest request){
        if(request.getFollowUser() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a user to follow");
        }

        validateAuthToken(request.getAuthToken());

        userDAO.addRemoveFollower(request.getFollowUser().getAlias(),true);
        userDAO.addRemoveFollowing(request.getCurrentUser().getAlias(),true);

        return followDAO.follow(request);
    }

    public UnfollowResponse unfollow(UnfollowRequest request){
        if(request.getUnfollowUser() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a user to follow");
        }

        validateAuthToken(request.getAuthToken());

        userDAO.addRemoveFollower(request.getUnfollowUser().getAlias(),false);
        userDAO.addRemoveFollowing(request.getCurrentUser().getAlias(),false);

        return followDAO.unfollow(request);
    }

    public FollowingCountResponse getFollowingCount(FollowingCountRequest request){
        if(request.getTargetUser() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a user to get the count for");
        }

        validateAuthToken(request.getAuthToken());

        return new FollowingCountResponse(userDAO.getUserFollowingCount(request.getTargetUser().getAlias()));
    }

    public FollowersCountResponse getFollowersCount(FollowersCountRequest request){
        if(request.getTargetUser() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a user to get the count for");
        }

        validateAuthToken(request.getAuthToken());

        return new FollowersCountResponse(userDAO.getUserFollowerCount(request.getTargetUser().getAlias()));
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request){
        if(request.getFollower() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a follower user");
        }
        else if(request.getFollowee() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a followee user");
        }
        else if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a authToken");
        }

        // Verify AuthToken
        if(!authTokenDAO.authenticateUser(request.getAuthToken())){
            throw new RuntimeException("[Bad Request] Authentication expired");
        }
        return followDAO.isFollower(request);
    }

    private void validateAuthToken(AuthToken authToken){
        // Verify AuthToken
        if(!authTokenDAO.authenticateUser(authToken)){
            throw new RuntimeException("[Bad Request] Authentication expired");
        }
    }

    public List<String> getFollowersSQS(String alias){

        return followDAO.getAllFollowers(alias);
    }
}
