//package edu.byu.cs.tweeter.server.service;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.List;
//
//import edu.byu.cs.tweeter.model.domain.AuthToken;
//import edu.byu.cs.tweeter.model.domain.User;
//import edu.byu.cs.tweeter.model.net.request.FollowRequest;
//import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
//import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
//import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
//import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
//import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
//import edu.byu.cs.tweeter.model.net.request.LoginRequest;
//import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
//import edu.byu.cs.tweeter.model.net.response.FollowResponse;
//import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
//import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
//import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
//import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
//import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
//import edu.byu.cs.tweeter.model.net.response.LoginResponse;
//import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
//import edu.byu.cs.tweeter.server.dao.DAOFactory;
//import edu.byu.cs.tweeter.server.dao.FollowDAO;
//
//public class FollowServiceTest {
//
//    private FollowingRequest request;
//    private FollowingResponse expectedResponse;
//    private FollowDAO mockFollowDAO;
//    private FollowService followServiceSpy;
//
//    private DAOFactory factory = new DAOFactory();
//
//    private UserService userService = new UserService(factory);
//    private FollowService followService = new FollowService(factory);
//
//    @Before
//    public void setup() {
////        AuthToken authToken = new AuthToken();
////
////        User currentUser = new User("FirstName", "LastName", null);
////
////        User resultUser1 = new User("FirstName1", "LastName1",
////                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
////        User resultUser2 = new User("FirstName2", "LastName2",
////                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");
////        User resultUser3 = new User("FirstName3", "LastName3",
////                "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png");
////
////        // Setup a request object to use in the tests
////        request = new FollowingRequest(authToken, currentUser.getAlias(), 3, null);
////
////        // Setup a mock FollowDAO that will return known responses
////        expectedResponse = new FollowingResponse(Arrays.asList(resultUser1, resultUser2, resultUser3), false);
////        mockFollowDAO = Mockito.mock(FollowDAO.class);
////        Mockito.when(mockFollowDAO.getFollowees(request)).thenReturn(expectedResponse);
////
////        followServiceSpy = Mockito.spy(FollowServiceImpl.class);
////        Mockito.when(followServiceSpy.getFollowingDAO()).thenReturn(mockFollowDAO);
//    }
//
//    /**
//     * Verify that the {@link FollowService#getFollowees(FollowingRequest)}
//     * method returns the same result as the {@link FollowDAO} class.
//     */
//    @Test
//    public void testGetFollowees_validRequest_correctResponse() {
//        FollowingResponse response = followServiceSpy.getFollowees(request);
//        Assert.assertEquals(expectedResponse, response);
//    }
//
//    @Test
//    public void testGetFolloweeCount(){
//        AuthToken tempAuth = new AuthToken();
//        LoginRequest requestLogin = new LoginRequest("@1","1");
//        LoginResponse responseLogin = userService.login(requestLogin);
//
//        FollowingCountRequest request = new FollowingCountRequest(responseLogin.getAuthToken(), responseLogin.getUser());
//
//        FollowingCountResponse response = followService.getFollowingCount(request);
//        Assert.assertEquals(2,response.getCount());
//    }
//
//    @Test
//    public void testGetFollowersCount(){
//        AuthToken tempAuth = new AuthToken();
//        LoginRequest requestLogin = new LoginRequest("@1","1");
//        LoginResponse responseLogin = userService.login(requestLogin);
//
//        FollowersCountRequest request = new FollowersCountRequest(responseLogin.getAuthToken(), responseLogin.getUser());
//
//        FollowersCountResponse response = followService.getFollowersCount(request);
//        Assert.assertEquals(2,response.getCount());
//    }
//
//    @Test
//    public void testGetFollowees(){
//        AuthToken tempAuth = new AuthToken();
//        LoginRequest requestLogin = new LoginRequest("@1","1");
//        LoginResponse responseLogin = userService.login(requestLogin);
//
//        FollowingRequest request = new FollowingRequest(responseLogin.getAuthToken(),"@1",5,null);
//        FollowingResponse response = followService.getFollowees(request);
//
//        FollowingRequest request1 = new FollowingRequest(responseLogin.getAuthToken(),"@1",5,response.getFollowees().get(4).getAlias());
//        FollowingResponse response1 = followService.getFollowees(request1);
//
//        Assert.assertNotNull(response);
//    }
//
//    @Test
//    public void testGetAllFollowees(){
//        AuthToken tempAuth = new AuthToken();
//        LoginRequest requestLogin = new LoginRequest("@1","1");
//        LoginResponse responseLogin = userService.login(requestLogin);
//
//        List<String> followees = new FollowDAO().getAllFollowees("@1");
//        Assert.assertNotNull(followees);
//    }
//
//    @Test
//    public void testGetFollowers(){
//        AuthToken tempAuth = new AuthToken();
//        LoginRequest requestLogin = new LoginRequest("@1","1");
//        LoginResponse responseLogin = userService.login(requestLogin);
//
//        FollowerRequest request = new FollowerRequest(responseLogin.getAuthToken(), requestLogin.getUsername(), 10,null);
//        FollowerResponse response = followService.getFollowers(request);
//
//        FollowerRequest request1 = new FollowerRequest(responseLogin.getAuthToken(), requestLogin.getUsername(), 10,response.getFollowers().get(9).getAlias());
//        FollowerResponse response1 = followService.getFollowers(request1);
//
//        Assert.assertNotNull(response);
//    }
//
//    @Test
//    public void testFollow(){
//        AuthToken tempAuth = new AuthToken();
//        LoginRequest requestLogin = new LoginRequest("@1","1");
//        LoginResponse responseLogin = userService.login(requestLogin);
//
//        User resultUser1 = new User("john","doe","@johnDoe",null);
//
//        FollowRequest request = new FollowRequest(responseLogin.getAuthToken(),resultUser1,responseLogin.getUser());
//        FollowResponse response = followService.follow(request);
//    }
//
//
//    @Test
//    public void testUnfollow(){
//        AuthToken tempAuth = new AuthToken();
//        LoginRequest requestLogin = new LoginRequest("@1","1");
//        LoginResponse responseLogin = userService.login(requestLogin);
//
//        User resultUser1 = new User("jack","smith","@jack Smith",null);
//
//        FollowRequest followRequest = new FollowRequest(responseLogin.getAuthToken(),resultUser1,responseLogin.getUser());
//        FollowResponse followResponse = followService.follow(followRequest);
//
//        UnfollowRequest request = new UnfollowRequest(responseLogin.getAuthToken(), resultUser1, responseLogin.getUser());
//        UnfollowResponse response = followService.unfollow(request);
//
//        Assert.assertNotNull(response);
//
//    }
//
//    @Test
//    public void testIsFollower(){
//        AuthToken tempAuth = new AuthToken();
//        LoginRequest requestLogin = new LoginRequest("@1","1");
//        LoginResponse responseLogin = userService.login(requestLogin);
//
//        User resultUser1 = new User("john","doe","@johnDoe",null);
//
//        IsFollowerRequest request = new IsFollowerRequest(responseLogin.getAuthToken(),responseLogin.getUser(),resultUser1);
//        IsFollowerResponse response = followService.isFollower(request);
//
//        Assert.assertNotNull(response);
//    }
//
//    @Test
//    public void testIsFollower_NotFollowing(){
//        AuthToken tempAuth = new AuthToken();
//        LoginRequest requestLogin = new LoginRequest("@1","1");
//        LoginResponse responseLogin = userService.login(requestLogin);
//
//        User resultUser1 = new User("amy","somethingICantRemember","@amy",null);
//
//        IsFollowerRequest request = new IsFollowerRequest(responseLogin.getAuthToken(),responseLogin.getUser(),resultUser1);
//        IsFollowerResponse response = followService.isFollower(request);
//
//        Assert.assertNotNull(response);
//    }
//}
