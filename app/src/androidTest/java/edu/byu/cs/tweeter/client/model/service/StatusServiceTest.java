//package edu.byu.cs.tweeter.client.model.service;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mockito;
//
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//
//import edu.byu.cs.tweeter.client.model.service.observer.PagedItemObserver;
//import edu.byu.cs.tweeter.model.domain.AuthToken;
//import edu.byu.cs.tweeter.model.domain.Status;
//import edu.byu.cs.tweeter.model.domain.User;
//import edu.byu.cs.tweeter.util.FakeData;
//
//public class StatusServiceTest {
//
//    private User currentUser;
//    private AuthToken currentAuthToken;
//
//
//    private StatusService statusServiceSpy;
//    private StatusServiceObserver observer;
//
//    private CountDownLatch countDownLatch;
//
//    @Before
//    public void setup(){
//        currentUser = new User("FirstName", "LastName", null);
//        currentAuthToken = new AuthToken();
//
//        statusServiceSpy = Mockito.spy(new StatusService());
//
//        observer = new StatusServiceObserver();
//
//        resetCountDownLatch();
//    }
//
//    private void resetCountDownLatch() {
//        countDownLatch = new CountDownLatch(1);
//    }
//
//    private void awaitCountDownLatch() throws InterruptedException {
//        countDownLatch.await();
//        resetCountDownLatch();
//    }
//
//
//    /**
//     * Returned by the asynchronous call from the followService. Counts down on the countDownLatch
//     * so tests can wait for the background thread to call a method on the observer.
//     * this is a {@link PagedItemObserver<Status>} implementation for the observer.
//     *
//     * */
//    private class StatusServiceObserver implements PagedItemObserver<Status>{
//
//        private boolean success;
//        private String message;
//        private List<Status> statuses;
//        private boolean hasMorePages;
//        private Exception exception;
//
//        @Override
//        public void handleSuccess(List<Status> statuses, boolean hasMorePages) {
//            this.success = true;
//            this.message = null;
//            this.statuses = statuses;
//            this.hasMorePages = hasMorePages;
//            this.exception = null;
//
//            countDownLatch.countDown();
//        }
//
//        @Override
//        public void handleFailure(String message) {
//            this.success = false;
//            this.message = null;
//            this.statuses = null;
//            this.hasMorePages = false;
//            this.exception = null;
//
//            countDownLatch.countDown();
//        }
//
//        @Override
//        public void handleException(Exception exception) {
//            this.success = false;
//            this.message = null;
//            this.statuses = null;
//            this.hasMorePages = false;
//            this.exception = exception;
//
//            countDownLatch.countDown();
//        }
//
//        public boolean isSuccess(){
//            return success;
//        }
//
//        public String getMessage(){
//            return message;
//        }
//
//        public List<Status> getStatuses(){
//            return statuses;
//        }
//
//        public boolean getHasMorePages(){
//            return hasMorePages;
//        }
//
//        public Exception getException(){
//            return exception;
//        }
//    }
//
//    @Test
//    public void testGetStory_validRequest_CorrectResponse() throws InterruptedException {
//        statusServiceSpy.getStory(currentAuthToken,currentUser,5,null,observer);
//        awaitCountDownLatch();
//
//        List<Status> expectedStatuses = new FakeData().getFakeStatuses().subList(0,5);
//
//        List<Status> statuses = observer.getStatuses();
//
//        Assert.assertTrue(observer.isSuccess());
//        Assert.assertNull(observer.getMessage());
//        Assert.assertTrue(statuses.size() > 0);
//        Assert.assertTrue(observer.getHasMorePages());
//        Assert.assertNull(observer.getException());
//
//        for(int i = 0; i < 5; i++){
//            //check everything besides the time stamps since aws will return a different timestamp.
//            Assert.assertEquals(expectedStatuses.get(i).user, statuses.get(i).user);
//            Assert.assertEquals(expectedStatuses.get(i).post, statuses.get(i).post);
//            Assert.assertEquals(expectedStatuses.get(i).mentions, statuses.get(i).mentions);
//            Assert.assertEquals(expectedStatuses.get(i).urls, statuses.get(i).urls);
//        }
//    }
//}
