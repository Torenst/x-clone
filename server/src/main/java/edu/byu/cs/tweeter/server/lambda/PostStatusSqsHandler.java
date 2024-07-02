package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.service.StatusService;

public class PostStatusSqsHandler implements RequestHandler<SQSEvent, Void> {

    private static final DAOFactoryInterface factory = new DAOFactory();
    private static final StatusService service = new StatusService(factory);

    @Override
    public Void handleRequest(SQSEvent input, Context context) {
        for(SQSEvent.SQSMessage msg : input.getRecords()) {
            service.feedBatchPost(msg.getBody());
        }
        return null;
    }
}
