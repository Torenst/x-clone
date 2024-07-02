package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends PagedStatusTask {

    static final String URL_PATH = "/getfeed";

    public GetFeedTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                       Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
    }

    @Override
    protected Pair<List<Status>, Boolean> getItems() {

        try{
            FeedRequest request = new FeedRequest(getAuthToken(), getTargetUser(),getLimit(),getLastItem(),0);
            FeedResponse response = getServerFacade().getFeed(request, URL_PATH);

            if(response.isSuccess()){
                return new Pair<>(response.getStatusList(),response.getHasMorePages());
            }else {
                sendFailedMessage(response.getMessage());
            }
        } catch (Exception ex){
            sendExceptionMessage(ex);
        }

        return null;
    }
}
