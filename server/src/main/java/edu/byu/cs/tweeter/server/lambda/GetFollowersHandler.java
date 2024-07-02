package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowersHandler implements RequestHandler<FollowerRequest, FollowerResponse> {

    @Override
    public FollowerResponse handleRequest(FollowerRequest request, Context context) {
        DAOFactoryInterface factory = new DAOFactory();
        FollowService service = new FollowService(factory);
        return service.getFollowers(request);
    }
}
