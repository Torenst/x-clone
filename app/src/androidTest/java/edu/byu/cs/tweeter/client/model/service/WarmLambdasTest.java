//package edu.byu.cs.tweeter.client.model.service;
//
//import org.junit.Test;
//
//public class WarmLambdasTest {
//
//    @Test
//    public void warmLambdas(){
//        FollowService followService = new FollowService();
//        StatusService statusService = new StatusService();
//        UserService userService = new UserService();
//
//
//        // just call the services to call the lambdas. This will throw the bad request exceptions,
//        // but warm everything up! :)
//
//        //follow lambdas
//        followService.followUser(null,null,null,null);
//        followService.unfollowUser(null,null,null,null);
//        followService.getFollowees(null,null,0,null,null);
//        followService.getFollowers(null,null,0,null, null);
//        followService.getFollowingCount(null,null,null);
//        followService.getFollowerCount(null,null,null);
//        followService.isFollower(null,null,null,null);
//
//        //status lambdas
//        statusService.getStory(null,null,0,null,null);
//        statusService.getFeed(null,null,0,null, null);
//        statusService.postStatus(null,null,null);
//
//        //user lambdas
//        userService.logoutUser(null,null);
//        userService.GetUsers(null,null,null);
//        userService.LoginUser(null,null,null);
//        userService.registerUser(null,null,null,null,null,null);
//    }
//}
