package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.JsonSerializer;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.dto.getUserDTO;
import edu.byu.cs.tweeter.server.service.StatusService;

public class PostStatusHandler implements RequestHandler<PostStatusRequest, PostStatusResponse> {

    // Start with a initialized connection so we don't have to remake connections each time the hanlder gets called.
    private static final String SQS_URL = "https://sqs.us-west-2.amazonaws.com/228987914955/GetFollowersQueue";
    private static final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

    private static final DAOFactoryInterface factory = new DAOFactory();
    private static final StatusService service = new StatusService(factory);

    @Override
    public PostStatusResponse handleRequest(PostStatusRequest request, Context context) {
        PostStatusResponse response = service.postStatus(request);

        if(response.isSuccess()){
            sendToSQS(request.getStatus(), response.getEpocTime());
        }
        return response;
    }

    private void sendToSQS(Status status, long epochTime){
        getUserDTO dto = new getUserDTO(status, epochTime);
        String messageBody = JsonSerializer.serialize(dto);

        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(SQS_URL)
                .withMessageBody(messageBody);

        SendMessageResult sendMessageResult = sqs.sendMessage(sendMessageRequest);
    }
}
