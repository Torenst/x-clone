package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;

/**
 * Background task that establishes a following relationship between two users.
 */
public class FollowTask extends AuthenticatedTask {

    static final String URL_PATH = "/follow";

    /**
     * The user that is being followed.
     */
    private final User followee;

    private final User currentUser;

    public FollowTask(AuthToken authToken, User followee, User currentUser, Handler messageHandler) {
        super(authToken, messageHandler);
        this.followee = followee;
        this.currentUser = currentUser;
    }

    @Override
    protected void runTask() {

        try {
            FollowRequest request = new FollowRequest(getAuthToken(), followee, currentUser);
            FollowResponse response = getServerFacade().follow(request, URL_PATH);

            if(response.isSuccess()) {
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }

        }catch (IOException | TweeterRemoteException e) {
            sendFailedMessage(e.getMessage());
            e.printStackTrace();
        }
    }

}
