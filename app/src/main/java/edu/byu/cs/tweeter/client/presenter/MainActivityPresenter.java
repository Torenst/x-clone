package edu.byu.cs.tweeter.client.presenter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.CountObserver;
import edu.byu.cs.tweeter.client.model.service.observer.FollowTypeObserver;
import edu.byu.cs.tweeter.client.model.service.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainActivityPresenter extends BasePresenter<MainActivityPresenter.MainView> {

    private AuthToken authToken;
    private User user;

    private int followeeCount;
    private int followerCount;

    private UserService userService;
    private StatusService statusService;
    private FollowService followService;

    public static interface MainView extends BaseView{
        void logoutUser();
        void updatedFollowCount();
        void isFollowingUpdate(boolean isFollower);
        void postedStatus();
    }

    @Override
    public void exceptionMessage(String failureType, Exception exception) {
        String exceptionMessage = "Failed to " + failureType + " because of exception: " + exception.getMessage();

        view.displayExceptionMessage(exceptionMessage, exception);
    }

    @Override
    public void failureMessage(String exceptionType, String message) {
        String errorMessage = "Failed to " + exceptionType + ": " + message;

        view.displayFailureMessage(errorMessage);
    }

    public MainActivityPresenter(MainView view, AuthToken authToken, User user) {
        super(view);
        this.authToken = authToken;
        this.user = user;

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getFolloweeCount() {
        return followeeCount;
    }

    public void setFolloweeCount(int followeeCount) {
        this.followeeCount = followeeCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public int getFollowerCountNum() {
        return followerCount;
    }

    // create instance of each of our services.
    public UserService getUserService(){
        if(userService == null){
            userService = new UserService();
        }
        return  userService;
    }

    public FollowService getFollowService(){
        if(followService == null) {
            followService = new FollowService();
        }
        return followService;
    }

    public StatusService getStatusService(){
        if(statusService == null) {
            statusService =  new StatusService();
        }
        return statusService;
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    public List<String> parseURLs(String post){
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    public Status createStatus(User user, String post) throws ParseException {
        return new Status(post, user, getFormattedDateTime(), parseURLs(post), parseMentions(post));
    }

    public void getFollowerCount(){
        getFollowService().getFollowerCount(authToken, user, new GetFollowerCountObserver());
    }

    public class GetFollowerCountObserver extends Observer implements CountObserver {

        @Override
        public void handleSuccess(int count) {
            setFollowerCount(count);
            view.updatedFollowCount();
        }

        @Override
        protected String getDescription() {
            return "follower Count";
        }
    }

    public void getFollowingCount(){
        getFollowService().getFollowingCount(authToken, user, new GetFollowingCountObserver());
    }

    public class GetFollowingCountObserver extends Observer implements CountObserver {

        @Override
        public void handleSuccess(int count) {
            setFolloweeCount(count);
            view.updatedFollowCount();
        }

        @Override
        protected String getDescription() {
            return "get following Count";
        }
    }

    public void isFollower(AuthToken authToken, User user, User followee){
        getFollowService().isFollower(authToken, user, followee, new IsFollowerObserver());
    }

    public class IsFollowerObserver extends Observer implements FollowTypeObserver {

        @Override
        public void handleSuccess(boolean isFollowing) {
            view.isFollowingUpdate(isFollowing);
            view.updatedFollowCount();
        }

        @Override
        protected String getDescription() {
            return "determine following relationship";
        }
    }


    public void logoutUser(AuthToken authToken){
        view.displayInfoMessage("Logging out...");
        getUserService().logoutUser(authToken, new LogoutObserver());
    }


    public class LogoutObserver extends Observer implements SimpleNotificationObserver{
        @Override
        public void handleSuccess() {
            //Clear user data (cached data).
            Cache.getInstance().clearCache();

            view.logoutUser();
        }

        @Override
        protected String getDescription() {
            return "logout";
        }
    }

    public void postStatus(AuthToken authToken, Status status){
        view.displayInfoMessage("Posting status...");
        getStatusService().postStatus(authToken,status, getCreatePostObserver());
    }

    public CreatePostObserver getCreatePostObserver(){
        return new CreatePostObserver();
    }

    public class CreatePostObserver extends Observer implements SimpleNotificationObserver{

        @Override
        public void handleSuccess() {
            view.postedStatus();
            view.displayInfoMessage("Successfully Posted!");
        }

        @Override
        protected String getDescription() {
            return "post status";
        }
    }

    public void followUser(AuthToken authToken, User followee, User currentUser){
        getFollowService().followUser(authToken, followee, currentUser, new FollowUserObserver());
    }

    public class FollowUserObserver extends Observer implements SimpleNotificationObserver{

        @Override
        public void handleSuccess() {
            view.isFollowingUpdate(true);
            view.updatedFollowCount();
        }

        @Override
        protected String getDescription() {
            return "follow";
        }
    }

    public void unfollowUser(AuthToken authToken, User followee, User currentUser){
        getFollowService().unfollowUser(authToken,followee,currentUser, new UnfollowObserver());
    }

    public class UnfollowObserver extends Observer implements SimpleNotificationObserver {

        @Override
        public void handleSuccess() {
            view.isFollowingUpdate(false);
            view.updatedFollowCount();
        }

        @Override
        protected String getDescription() {
            return "unfollow";
        }
    }
}
