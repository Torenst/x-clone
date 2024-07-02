package edu.byu.cs.tweeter.server.dao;

public interface DAOFactoryInterface {
    public AuthTokenDAO createAuthTokenDAO();
    public FeedDAO createFeedDAO();
    public FollowDAO createFollowDAO();
    public S3DAO createS3DAO();
    public StoryDAO createStoryDAO();
    public UserDAO createUserDAO();
}
