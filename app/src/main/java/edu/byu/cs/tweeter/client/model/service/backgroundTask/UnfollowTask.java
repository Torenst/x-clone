package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask extends AuthenticatedTask {

    static final String URL_PATH = "/unfollow";

    /**
     * The user that is being followed.
     */
    private final User followee;

    private final User currentuser;

    public UnfollowTask(AuthToken authToken, User followee, User currentUser, Handler messageHandler) {
        super(authToken, messageHandler);
        this.followee = followee;
        this.currentuser = currentUser;
    }

    @Override
    protected void runTask() {

        try {
            UnfollowRequest request = new UnfollowRequest(getAuthToken(), followee, currentuser);
            UnfollowResponse response = getServerFacade().unfollow(request, URL_PATH);

            if(response.isSuccess()) {
                sendSuccessMessage();
            }
            else {
                sendFailedMessage(response.getMessage());
            }

        }catch (IOException |TweeterRemoteException e) {
            sendFailedMessage(e.getMessage());
            e.printStackTrace();
        }
    }


}
