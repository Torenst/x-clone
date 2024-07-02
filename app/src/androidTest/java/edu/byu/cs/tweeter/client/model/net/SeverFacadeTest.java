package edu.byu.cs.tweeter.client.model.net;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public class SeverFacadeTest {

    private final String GETFOLLOWER_URL_PATH = "/getfollowers";
    private final String REGISTER_URL_PATH = "/register";
    private final String GETFOLLOWERSCOUNT_URL_PATH = "/getfollowerscount";
    private final String GETFOLLOWINGCOUNT_URL_PATH = "/getfollowingcount";



    private User currentUser;
    private AuthToken currentAuthToken;

    private ServerFacade serverFacadeSpy;

    @Before
    public void setup(){
        currentUser = new User("FirstName", "LastName", "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        currentAuthToken = new AuthToken();

        serverFacadeSpy = new ServerFacade();//Mockito.spy(new ServerFacade());
    }

    //GetFollowers------------------------------------------------

    @Test
    public void testGetFollowers_Success() throws InterruptedException{
//        FollowerRequest request = new FollowerRequest(currentAuthToken,currentUser.getAlias(),10,null);
//        FollowerResponse response = null;
//        try {
//            response = serverFacadeSpy.getFollowers(request,GETFOLLOWER_URL_PATH);
//        }catch (IOException | TweeterRemoteException ex){
//            ex.printStackTrace();
//        }
//
//        Assert.assertNotNull(response);
//        Assert.assertNull(response.getMessage());
//        Assert.assertTrue(response.isSuccess());
//        // for 4c this isn't exactly true since it isn't fake data.
//        //Assert.assertTrue(response.getHasMorePages());
//        //Assert.assertTrue(response.getFollowers().size() == 10);


    }

    @Test
    public void testGetFollowers_Failure_badAlias(){
        User badRequestUser = new User("","",null,null);
        Exception exception = null;
        FollowerRequest request = new FollowerRequest(currentAuthToken, badRequestUser.getAlias(), 10,null);
        FollowerResponse response = null;


        try {
            response = serverFacadeSpy.getFollowers(request,GETFOLLOWER_URL_PATH);
        }catch (IOException | TweeterRemoteException ex){
            //ex.printStackTrace();
            exception = ex;
        }

        Assert.assertNull(response);
        Assert.assertEquals("[Bad Request] Request needs to have a follower alias", exception.getMessage());

    }

    @Test
    public void testGetFollowers_Failure_badLimit(){
        Exception exception = null;
        FollowerRequest request = new FollowerRequest(currentAuthToken, currentUser.getAlias(), -1,null);
        FollowerResponse response = null;


        try {
            response = serverFacadeSpy.getFollowers(request,GETFOLLOWER_URL_PATH);
        }catch (IOException | TweeterRemoteException ex){
            //ex.printStackTrace();
            exception = ex;
        }


        Assert.assertNull(response);
        Assert.assertEquals("[Bad Request] Request needs to have a positive limit", exception.getMessage());
    }

    //GetFollowingCount-------------------------------------------

    @Test
    public void TestGetFollowingCount_Success(){
//        FollowingCountRequest request = new FollowingCountRequest(currentAuthToken, currentUser);
//        FollowingCountResponse response = null;
//        try {
//            response = serverFacadeSpy.getFollowingCount(request,GETFOLLOWINGCOUNT_URL_PATH);
//        }catch (IOException | TweeterRemoteException ex){
//            ex.printStackTrace();
//        }
//
//        Assert.assertNotNull(response);
//        Assert.assertNull(response.getMessage());
//        Assert.assertTrue(response.isSuccess());
        // for 4c this isn't true any more because we changed from fake data.
        //Assert.assertEquals(20,response.getCount());
    }

    @Test
    public void TestGetFollowingCount_Failure_badUser(){
        Exception exception = null;
        // we will just pass a null in as the user.
        FollowingCountRequest request = new FollowingCountRequest(currentAuthToken, null);
        FollowingCountResponse response = null;
        try {
            response = serverFacadeSpy.getFollowingCount(request,GETFOLLOWINGCOUNT_URL_PATH);
        }catch (IOException | TweeterRemoteException ex){
            exception = ex;
            //ex.printStackTrace();
        }

        Assert.assertNull(response);
        Assert.assertEquals("[Bad Request] Request needs to have a user to get the count for",exception.getMessage());
    }

    //GetFollowersCount-------------------------------------------

    @Test
    public void TestGetFollowersCount_Success(){
        //4c made it so this doesn't work any more.
//        FollowersCountRequest request = new FollowersCountRequest(currentAuthToken, currentUser);
//        FollowersCountResponse response = null;
//        try {
//            response = serverFacadeSpy.getFollowersCount(request,GETFOLLOWERSCOUNT_URL_PATH);
//        }catch (IOException | TweeterRemoteException ex){
//            ex.printStackTrace();
//        }
//
//        Assert.assertNotNull(response);
//        Assert.assertNull(response.getMessage());
//        Assert.assertTrue(response.isSuccess());
//        Assert.assertEquals(20,response.getCount());
    }

    @Test
    public void TestGetFollowersCount_Failure_badUser(){
        Exception exception = null;
        // we will just pass a null in as the user.
        FollowersCountRequest request = new FollowersCountRequest(currentAuthToken, null);
        FollowersCountResponse response = null;
        try {
            response = serverFacadeSpy.getFollowersCount(request,GETFOLLOWERSCOUNT_URL_PATH);
        }catch (IOException | TweeterRemoteException ex){
            exception = ex;
            //ex.printStackTrace();
        }

        Assert.assertNull(response);
        Assert.assertEquals("[Bad Request] Request needs to have a user to get the count for",exception.getMessage());
    }

    //Register----------------------------------------------------

    //for 4c we don't want to test this because it just tries to write over the already made user.
    @Test
    public void TestRegister_Success(){
//        //"FirstName", "LastName", "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png"
//        RegisterRequest request = new RegisterRequest( "FirstName1","LastName1","@FirstNameLastName","12345","https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
//        RegisterResponse response = null;
//        try {
//            response = serverFacadeSpy.register(request,REGISTER_URL_PATH);
//        }catch (IOException | TweeterRemoteException ex){
//            ex.printStackTrace();
//        }
//
//        Assert.assertNotNull(response);
//        Assert.assertNull(response.getMessage());
//        Assert.assertTrue(response.isSuccess());
//        Assert.assertNotNull(response.getAuthToken());
//        Assert.assertNotNull(response.getUser());
    }

    @Test
    public void TestRegister_Failure_BadFirstName(){
        Exception exception = null;
        RegisterRequest request = new RegisterRequest( null,"LastName","@FirstNameLastName","12345","https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        RegisterResponse response = null;
        try {
            response = serverFacadeSpy.register(request,REGISTER_URL_PATH);
        }catch (IOException | TweeterRemoteException ex){
            exception = ex;
        }

        Assert.assertNull(response);
        Assert.assertEquals("[Bad Request] Missing first name",exception.getMessage());

    }

    @Test
    public void TestRegister_Failure_BadLastName(){
        Exception exception = null;
        RegisterRequest request = new RegisterRequest( "FirstName",null,"@FirstNameLastName","12345","https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        RegisterResponse response = null;
        try {
            response = serverFacadeSpy.register(request,REGISTER_URL_PATH);
        }catch (IOException | TweeterRemoteException ex){
            exception = ex;
        }

        Assert.assertNull(response);
        Assert.assertEquals("[Bad Request] Missing last name",exception.getMessage());

    }

    @Test
    public void TestRegister_Failure_BadUserName(){
        Exception exception = null;
        RegisterRequest request = new RegisterRequest( "FirstName","LastName",null,"12345","https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        RegisterResponse response = null;
        try {
            response = serverFacadeSpy.register(request,REGISTER_URL_PATH);
        }catch (IOException | TweeterRemoteException ex){
            exception = ex;
        }

        Assert.assertNull(response);
        Assert.assertEquals("[Bad Request] Missing user name",exception.getMessage());

    }

    @Test
    public void TestRegister_Failure_BadPassword(){
        Exception exception = null;
        RegisterRequest request = new RegisterRequest( "FirstName","LastName","@FirstNameLastName",null,"https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
        RegisterResponse response = null;
        try {
            response = serverFacadeSpy.register(request,REGISTER_URL_PATH);
        }catch (IOException | TweeterRemoteException ex){
            exception = ex;
        }

        Assert.assertNull(response);
        Assert.assertEquals("[Bad Request] Missing a password",exception.getMessage());

    }

    @Test
    public void TestRegister_Failure_BadImage(){
        Exception exception = null;
        RegisterRequest request = new RegisterRequest( "FirstName","LastName","@FirstNameLastName","12345",null);
        RegisterResponse response = null;
        try {
            response = serverFacadeSpy.register(request,REGISTER_URL_PATH);
        }catch (IOException | TweeterRemoteException ex){
            exception = ex;
        }

        Assert.assertNull(response);
        Assert.assertEquals("[Bad Request] Missing a profile image",exception.getMessage());

    }
    //------------------------------------------------------------
}
