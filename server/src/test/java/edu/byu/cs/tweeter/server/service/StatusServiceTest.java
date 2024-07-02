package edu.byu.cs.tweeter.server.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.util.FakeData;

public class StatusServiceTest {
    /**
     * The server-side Service that returns user stories should have an operation that returns the next page of a user's story.
     * This operation should use a DAO to retrieve the data from the database.
     * Using JUnit and Mockito, write automated UNIT tests to verify that the Service correctly returns story pages.
     * Use mocking to isolate the Service class from its dependencies (specifically its dependencies on DAO classes).
     * */

    private StatusService statusServiceSpy;
    private StoryDAO storyDAOMock;
    private AuthTokenDAO authTokenDAOMock;

    // I'm just going to use fake data to make the story list of items.
    FakeData fakeData = new FakeData();

    User currentUser = new User("first","last","@1","https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png");
    AuthToken authToken = new AuthToken();

    StoryRequest storyRequest1 = new StoryRequest(authToken,currentUser,10,null,0);
    StoryResponse storyResponse1 = new StoryResponse(fakeData.getPageOfStatus(null,10).getFirst(), fakeData.getPageOfStatus(null,10).getSecond());

    Status tempLastStatus = storyResponse1.getStatusList().get(9);

    StoryRequest storyRequest2 = new StoryRequest(authToken,currentUser,10,storyResponse1.getStatusList().get(9),0);
    StoryResponse storyResponse2 = new StoryResponse(fakeData.getPageOfStatus(tempLastStatus,10).getFirst(), fakeData.getPageOfStatus(tempLastStatus, 10).getSecond());


    @Before
    public void setup(){
        DAOFactoryInterface factoryMock = Mockito.mock(DAOFactory.class);
        storyDAOMock = Mockito.mock(StoryDAO.class);
        authTokenDAOMock = Mockito.mock(AuthTokenDAO.class);

        Mockito.when(factoryMock.createStoryDAO()).thenReturn(storyDAOMock);
        Mockito.when(factoryMock.createAuthTokenDAO()).thenReturn(authTokenDAOMock);

        statusServiceSpy = Mockito.spy(new StatusService(factoryMock));
    }

    @Test
    public void getStoryTest_firstPageGet(){
        // this will test getting the first page of items from the story.

        // when we call for the page of items in the statusService just return a pre-made fakedata one.
        Mockito.when(storyDAOMock.getStory(Mockito.any())).thenReturn(storyResponse1);
        // when the authToken validate is called in the statusService just return true for this test.
        Mockito.when(authTokenDAOMock.authenticateUser(Mockito.any())).thenReturn(true);

        StoryResponse response = statusServiceSpy.getStory(storyRequest1);

        Mockito.verify(storyDAOMock).getStory(Mockito.any());
        Assert.assertEquals(storyResponse1, response);
    }

    @Test
    public void getStoryTest_secondPageGet(){
        // this will test getting the second page of items from the story.

        // when we call for the page of items in the statusService just return a pre-made fakedata one.
        Mockito.when(storyDAOMock.getStory(Mockito.any())).thenReturn(storyResponse2);
        // when the authToken validate is called in the statusService just return true for this test.
        Mockito.when(authTokenDAOMock.authenticateUser(Mockito.any())).thenReturn(true);

        StoryResponse response = statusServiceSpy.getStory(storyRequest2);

        Mockito.verify(storyDAOMock).getStory(Mockito.any());
        Assert.assertEquals(storyResponse2, response);
    }













//        Answer<Void> storyPage = new Answer<Void>() {
//            @Override
//            public Void answer(InvocationOnMock invocation) throws Throwable {
//                return null;
//            }
//        };
//        Mockito.doAnswer(storyPage).when(storyDAOMock).getStory(storyRequest1);


//    private DAOFactoryInterface factory = new DAOFactory();
//    private UserService userService = new UserService(factory);
//    private StatusService statusService = new StatusService(factory);
//
//    @Test
//    public void testGetStory(){
//        //login a user to test
//        AuthToken tempAuth = new AuthToken();
//        LoginRequest requestLogin = new LoginRequest("@1","1");
//        LoginResponse responseLogin = userService.login(requestLogin);
//
//        // we don't initially have a last status so we send null?
//        StoryRequest request = new StoryRequest(responseLogin.getAuthToken(), responseLogin.getUser(), 10,null, 0);
//        StoryResponse response = statusService.getStory(request);
//
//        StoryRequest request1 = new StoryRequest(responseLogin.getAuthToken(), responseLogin.getUser(), 10, response.getStatusList().get(9), response.getStatusList().get(9).getEpochTime());
//        StoryResponse response1 = statusService.getStory(request1);
//
//        Assert.assertNotNull(responseLogin);
//        Assert.assertNotNull(response);
//    }
//
//    @Test
//    public void postStatusTest(){
//        AuthToken tempAuth = new AuthToken();
//        LoginRequest requestLogin = new LoginRequest("@johnDoe","12345");
//        LoginResponse responseLogin = userService.login(requestLogin);
//
//        String dateTime = "";
//        try {
//            dateTime = getFormattedDateTime();
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//
//        Status status = new Status("this is a cool post.", responseLogin.getUser(),dateTime,null,null);
//
//        PostStatusRequest request = new PostStatusRequest(responseLogin.getAuthToken(), status);
//        PostStatusResponse postStatusResponse = statusService.postStatus(request);
//    }
//
//    @Test
//    public void getFeedTest(){
//        AuthToken tempAuth = new AuthToken();
//        LoginRequest requestLogin = new LoginRequest("@amy","12345");
//        LoginResponse responseLogin = userService.login(requestLogin);
//
//        FeedRequest request = new FeedRequest(responseLogin.getAuthToken(), responseLogin.getUser(), 10, null,0);
//        FeedResponse response = statusService.getFeed(request);
//
//        FeedRequest request1 = new FeedRequest(responseLogin.getAuthToken(), responseLogin.getUser(), 10, response.getStatusList().get(9),response.getStatusList().get(9).getEpochTime());
//        FeedResponse response1 = statusService.getFeed(request1);
//
//        Assert.assertNotNull(response);
//    }
//
//    public String getFormattedDateTime() throws ParseException {
//        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");
//
//        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
//    }
//
//    @Test
//    public void getAllFollowersTest(){
//        AuthToken tempAuth = new AuthToken();
//        LoginRequest requestLogin = new LoginRequest("@1","1");
//        LoginResponse responseLogin = userService.login(requestLogin);
//
//        FollowDAO dao = new FollowDAO();
//        List<String> temp = dao.getAllFollowees("@1");
//
//        Assert.assertEquals(21, temp.size());
//    }
}
