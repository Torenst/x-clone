package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.JsonSerializer;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.dto.getUserDTO;
import edu.byu.cs.tweeter.server.dto.postDTO;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowersSqsHandler implements RequestHandler<SQSEvent, Void> {

    // we want our sqs to be static so we don't have to remake connections.
    private static final String SQS_URL = "https://sqs.us-west-2.amazonaws.com/228987914955/PostStatusQueue";
    private static final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

    private static final DAOFactoryInterface factory = new DAOFactory();
    private static final FollowService service = new FollowService(factory);

    @Override
    public Void handleRequest(SQSEvent input, Context context) {
        for(SQSEvent.SQSMessage msg : input.getRecords()) {
            getUserDTO dto = JsonSerializer.deserialize(msg.getBody(), getUserDTO.class);
            Status status = dto.getStatus();
            long epochTime = dto.getEpochTime();

            List<String> followers = service.getFollowersSQS(status.getUser().getAlias());
            while (followers.size() != 0) {
                // if we have more then 25 entries split at 25
                if (followers.size() >= 25) {
                    List<String> followerSub = followers.subList(0, 25);
                    postToFeedSQS(status, followerSub, epochTime);
                    followers.subList(0, 25).clear();
                }
                // if we have less then 25 we just sent the rest and clear.
                else {
                    postToFeedSQS(status, followers, epochTime);
                    followers.clear();
                }
            }
        }
        return null;
    }

    private void postToFeedSQS(Status status, List<String> followers, long epochTime){
        postDTO dto = new postDTO(status,followers, epochTime);
        String messageBody = JsonSerializer.serialize(dto);

        SendMessageRequest request = new SendMessageRequest()
                .withQueueUrl(SQS_URL)
                .withMessageBody(messageBody);

        sqs.sendMessage(request);
    }
}
