package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

public class StoryDAO implements StoryInterface {

    private static final String tableName = "story";

    private static final String senderAliasAttr = "sender_alias";
    private static final String timeStampAttr = "time_stamp";
    private static final String postAttr = "post";
    private static final String dateTimeAttr = "dateTime";

    private static final String firstNameAttr = "first_name";
    private static final String lastNameAttr = "last_name";
    private static final String profileImageAttr = "profile_image";

    private static final AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
    private static final DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    private static final Table table = dynamoDB.getTable(tableName);

    @Override
    public StoryResponse getStory(StoryRequest request) {
        List<Status> statuses = new ArrayList<>();

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":sa", request.getTargetUser().getAlias());

        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression(senderAliasAttr + " = :sa")
                .withScanIndexForward(false)
                .withValueMap(valueMap)
                .withMaxResultSize(request.getLimit());

        ItemCollection<QueryOutcome> items;

        if (request.getLastStatus() != null) {
            PrimaryKey primaryKey = new PrimaryKey(senderAliasAttr, request.getLastStatus().getUser().getAlias(),
                    timeStampAttr, request.getLastStatus().getEpochTime());
            querySpec.withExclusiveStartKey(primaryKey);
        }

        try {
            items = table.query(querySpec);

            for (Item item : items) {
                User user = new User(item.getString(firstNameAttr),item.getString(lastNameAttr),item.getString(senderAliasAttr),item.getString(profileImageAttr));
                user.setAlias(item.getString(senderAliasAttr));


                Status status = new Status(item.getString(postAttr), user, item.getString(dateTimeAttr), new ArrayList<>(), new ArrayList<>());
                status.setEpochTime(item.getLong(timeStampAttr));
                statuses.add(status);
            }

            boolean hasMorePages = statuses.size() >= request.getLimit();
            return new StoryResponse(statuses, hasMorePages);


        } catch (Exception ex) {
            System.out.println("Unable to get users story  ex: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("[Server Error] Unable to get users story");
        }

    }

    @Override
    public PostStatusResponse createPost(User currentUser, Status post, String dateTime) {
        long epoch = System.currentTimeMillis();
        // System.out.println(senderAliasAttr + ": " + currentUser.getAlias() + ", " + timeStampAttr +
        //        ": " + epoch + "\n Post text: " + post.getPost() + ", " + dateTimeAttr + ": " + dateTime);
        Item item = new Item().withPrimaryKey(senderAliasAttr, currentUser.getAlias())
                .withKeyComponent(timeStampAttr, epoch)
                .withString(postAttr, post.getPost())
                .withString(dateTimeAttr, dateTime)
                .withString(firstNameAttr, currentUser.getFirstName())
                .withString(lastNameAttr, currentUser.getLastName())
                .withString(profileImageAttr, currentUser.getImageUrl());

        table.putItem(item);

        return new PostStatusResponse(epoch);
    }
}
