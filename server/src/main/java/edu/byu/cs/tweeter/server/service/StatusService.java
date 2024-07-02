package edu.byu.cs.tweeter.server.service;


import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.JsonSerializer;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenInterface;
import edu.byu.cs.tweeter.server.dao.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.dao.FeedInterface;
import edu.byu.cs.tweeter.server.dao.FollowInterface;
import edu.byu.cs.tweeter.server.dao.StoryInterface;
import edu.byu.cs.tweeter.server.dto.postDTO;

public class StatusService {

    private final FeedInterface feedDAO;
    private final StoryInterface storyDAO;
    private final FollowInterface followDAO;
    private final AuthTokenInterface authTokenDAO;

    public StatusService(DAOFactoryInterface factory) {
        this.feedDAO = factory.createFeedDAO();
        this.storyDAO = factory.createStoryDAO();
        this.followDAO = factory.createFollowDAO();
        this.authTokenDAO = factory.createAuthTokenDAO();
    }

    public StoryResponse getStory(StoryRequest request){
        if(request.getTargetUser() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a target user");
        }else if(request.getLimit() <= 0){
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        validateAuthToken(request.getAuthToken());

        StoryResponse response = storyDAO.getStory(request);

        List<Status> statuses = response.getStatusList();
        boolean hasMorePages = response.getHasMorePages();

        // update all the statuses to include urls and mentions
        for(Status status : statuses) {
            status.urls = parseURLs(status.getPost());
            status.mentions = parseMentions(status.getPost());
        }

        return new StoryResponse(statuses, hasMorePages);
    }

    public FeedResponse getFeed(FeedRequest request){
        if(request.getTargetUser() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a target user");
        }
        else if(request.getLimit() <= 0){
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }

        validateAuthToken(request.getAuthToken());

        FeedResponse response = feedDAO.getFeed(request);

        List<Status> statuses = response.getStatusList();
        boolean hasMorePages = response.getHasMorePages();

        //parse the mentions and urls
        for(Status status : statuses) {
            status.urls = parseURLs(status.getPost());
            status.mentions = parseMentions(status.getPost());
        }
        return new FeedResponse(statuses,hasMorePages);
    }

    public PostStatusResponse postStatus(PostStatusRequest request){
        if(request.getStatus() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a status");
        }
        else if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a authToken");
        }

        validateAuthToken(request.getAuthToken());

        //write to the story table
        PostStatusResponse response = storyDAO.createPost(request.getStatus().getUser(), request.getStatus(), request.getStatus().getDate());

        return response;
    }

    public void feedBatchPost(String messageBody){
        postDTO dto = JsonSerializer.deserialize(messageBody, postDTO.class);
        Status status = dto.getStatus();
        List<String> followers = dto.getFollowers();
        long epochTime = dto.getEpochTime();

        feedDAO.batchPost(status,followers,epochTime);

    }

    private List<String> parseMentions(String post) {
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

    private List<String> parseURLs(String post){
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

    private int findUrlEndIndex(String word) {
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

    private void validateAuthToken(AuthToken authToken){
        // Verify AuthToken
        if(!authTokenDAO.authenticateUser(authToken)){
            throw new RuntimeException("[Bad Request] Authentication expired");
        }
    }
}
