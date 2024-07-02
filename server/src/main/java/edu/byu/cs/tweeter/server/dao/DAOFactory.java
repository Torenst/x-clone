package edu.byu.cs.tweeter.server.dao;

public class DAOFactory implements DAOFactoryInterface {
    @Override
    public AuthTokenDAO createAuthTokenDAO() {
        return new AuthTokenDAO();
    }

    @Override
    public FeedDAO createFeedDAO() {
        return new FeedDAO();
    }

    @Override
    public FollowDAO createFollowDAO() {
        return new FollowDAO();
    }

    @Override
    public S3DAO createS3DAO() {
        return new S3DAO();
    }

    @Override
    public StoryDAO createStoryDAO() {
        return new StoryDAO();
    }

    @Override
    public UserDAO createUserDAO() {
        return new UserDAO();
    }
}
