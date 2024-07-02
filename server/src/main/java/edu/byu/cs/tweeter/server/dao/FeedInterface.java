package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;

public interface FeedInterface {
    FeedResponse getFeed(FeedRequest request);
    void batchPost(Status status, List<String> followers, long epochTime);

}
