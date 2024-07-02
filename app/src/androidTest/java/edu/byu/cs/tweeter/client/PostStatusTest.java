package edu.byu.cs.tweeter.client;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.observer.PagedItemObserver;
import edu.byu.cs.tweeter.client.presenter.AuthenticationPresenter;
import edu.byu.cs.tweeter.client.presenter.LoginPresenter;
import edu.byu.cs.tweeter.client.presenter.MainActivityPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class PostStatusTest {

    /**
     * Using JUnit and Mockito, write an automated integration test to verify that when a user sends a status, the status is correctly appended to the user's story.
     * Your test should do the following:
     * 1. Login a user.
     * 2. Post a status from the user to the server by calling the "post status" operation on the relevant Presenter.
     * 3. Verify that the "Successfully Posted!" message was displayed to the user.
     * 4. Retrieve the user's story from the server to verify that the new status was correctly appended to the user's story, and that all status details are correct.
     * */


    private LoginPresenter loginPresenterSpy;
    private MainActivityPresenter mainPresenterSpy;

    private MainActivityPresenter.MainView mainViewMock;
    private MainActivityPresenter.CreatePostObserver postObserver;

    private AuthenticationPresenter.AuthView authViewMock;
    private AuthenticationPresenter.AuthenticatedObserver loginObserver;

    private User loggedInUser;
    private AuthToken authToken;
    private boolean isPostSuccessful = false;
    private List<Status> returnedStatuses;

    private final User userToSignIn = new User("first","one","@1","https://tweeter-image-storage.s3.us-west-2.amazonaws.com/%401.png");
    private final Status statusToPost = new Status("This is a test post", userToSignIn, getFormattedDateTime(), new ArrayList<>(), new ArrayList<>());

    private CountDownLatch countDownLatch;

    private Answer<Void> errorAnswer;
    private Answer<Void> exceptionAnswer;

    @Before
    public void setup(){

        mainViewMock = Mockito.mock(MainActivityPresenter.MainView.class);
        authViewMock = Mockito.mock(AuthenticationPresenter.AuthView.class);

        loginObserver = Mockito.mock(AuthenticationPresenter.AuthenticatedObserver.class);
        postObserver = Mockito.mock(MainActivityPresenter.CreatePostObserver.class);

        loginPresenterSpy = Mockito.spy(new LoginPresenter(authViewMock,"@1","1"));

        Mockito.when(loginPresenterSpy.getAuthenticatedObserver()).thenReturn(loginObserver);

        createErrorAnswerAndException();
        resetCountDownLatch();


    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    @Test
    public void postStatusTest() throws InterruptedException {

        // set the countdown latch while we grab the stuff from the background thread.
        Answer<Void> getNameAndAuth = invocation -> {
            setLoggedInUser(invocation.getArgument(0));
            setAuthToken(invocation.getArgument(1));
            countDownLatch.countDown();
            return null;
        };
        Mockito.doAnswer(getNameAndAuth).when(loginObserver).handleSuccess(Mockito.any(),Mockito.any());
        // these are here to catch any failures or exceptions so the test doesn't just hang forever waiting on the countdown latch.
        Mockito.doAnswer(exceptionAnswer).when(loginObserver).handleException(Mockito.any());
        Mockito.doAnswer(errorAnswer).when(loginObserver).handleFailure(Mockito.any());

        loginPresenterSpy.loginUser("@1","1");
        awaitCountDownLatch();

        // verify the user logged in was the same one we expected.
        Assert.assertEquals(userToSignIn, loggedInUser);


        // set up the spy for the mainPresenter so we can post a status to it.
        mainPresenterSpy = Mockito.spy(new MainActivityPresenter(mainViewMock, authToken, loggedInUser));
        Mockito.when(mainPresenterSpy.getCreatePostObserver()).thenReturn(postObserver);

        // set the countdown latch while we grab the stuff from the background thread.
        Answer<Void> postStatusResponse = invocation -> {
            countDownLatch.countDown();
            // if we had a success we print the true statement.
            isPostSuccessful = true;
            return null;
        };
        Mockito.doAnswer(postStatusResponse).when(postObserver).handleSuccess();
        // these are here to catch any failures or exceptions so the test doesn't just hang forever waiting on the countdown latch.
        Mockito.doAnswer(exceptionAnswer).when(postObserver).handleException(Mockito.any());
        Mockito.doAnswer(errorAnswer).when(postObserver).handleFailure(Mockito.any());

        resetCountDownLatch();
        mainPresenterSpy.postStatus(authToken,statusToPost);
        awaitCountDownLatch();

        Assert.assertTrue(isPostSuccessful);
        Mockito.verify(postObserver).handleSuccess();

        // set up the statusService so we can get the last story post from the logged in user.
        StatusService statusService = new StatusService();
        PagedItemObserver<Status> statusPagedItemObserver = Mockito.mock(PagedItemObserver.class);

        // set the countdown latch while we grab the stuff from the background thread.
        Answer<Void> getStoryResponse = invocation -> {
            countDownLatch.countDown();
            setReturnedStatuses(invocation.getArgument(0));
            // I think we can skip the hasMorePages for this test, we only want the post.
            return null;
        };
        Mockito.doAnswer(getStoryResponse).when(statusPagedItemObserver).handleSuccess(Mockito.any(), Mockito.anyBoolean());
        // these are here to catch any failures or exceptions so the test doesn't just hang forever waiting on the countdown latch.
        Mockito.doAnswer(exceptionAnswer).when(statusPagedItemObserver).handleException(Mockito.any());
        Mockito.doAnswer(errorAnswer).when(statusPagedItemObserver).handleFailure(Mockito.any());

        resetCountDownLatch();

        // get the last post which should be the one we just posted.
        statusService.getStory(authToken,loggedInUser,1,null,statusPagedItemObserver);
        awaitCountDownLatch();

        Assert.assertEquals(statusToPost,returnedStatuses.get(0));
    }

    private void setLoggedInUser(User user){
        this.loggedInUser = user;
    }

    private void setAuthToken(AuthToken token){
        this.authToken = token;
    }

    private void setReturnedStatuses(List<Status> statuses){
        this.returnedStatuses = statuses;
    }

    private String getFormattedDateTime() {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        try {
            return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
        }
        catch (Exception ex){
            ex.printStackTrace();
            throw new RuntimeException("Error getting formatted date time.");
        }
    }

    private void createErrorAnswerAndException(){
        errorAnswer = invocation -> {
            Assert.fail("Had an error running, try again.");
            System.out.println("Error was: " + invocation.getArgument(0));
            countDownLatch.countDown();
            return null;
        };

       exceptionAnswer = invocation -> {
            Assert.fail("Had an exception running, try again.");
            System.out.println("Exception was: " + invocation.getArgument(0));
            countDownLatch.countDown();
            return null;
        };
    }
}
