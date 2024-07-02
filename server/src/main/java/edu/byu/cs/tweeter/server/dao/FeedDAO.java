package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;

public class FeedDAO implements FeedInterface {

    private static final String tableName = "feed";

    private static final String receiverAliasAttr = "reciver_alias";
    private static final String timeStampAttr = "time_stamp";
    private static final String postAttr = "post";
    private static final String dateTimeAttr = "dateTime";
    private static final String authorAttr = "author";

    private static final String firstNameAttr = "first_name";
    private static final String lastNameAttr = "last_name";
    private static final String profileImageAttr = "profile_image";

    private static final AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
    private static final DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);

    @Override
    public FeedResponse getFeed(FeedRequest request) {
        Table table = dynamoDB.getTable(tableName);

        List<Status> statuses = new ArrayList<>();

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":ra", request.getTargetUser().getAlias());

        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression(receiverAliasAttr + " = :ra")
                .withScanIndexForward(false)
                .withValueMap(valueMap)
                .withMaxResultSize(request.getLimit());

        if (request.getLastStatus() != null) {
            PrimaryKey primaryKey = new PrimaryKey(receiverAliasAttr, request.getTargetUser().getAlias(),
                    timeStampAttr, request.getLastStatus().getEpochTime());
            querySpec.withExclusiveStartKey(primaryKey);
        }

        ItemCollection<QueryOutcome> items;

        try {
            items = table.query(querySpec);

            for (Item item : items) {
                User user = new User(item.getString(firstNameAttr),item.getString(lastNameAttr),item.getString(authorAttr),item.getString(profileImageAttr));
                user.setAlias(item.getString(authorAttr));

                Status status = new Status(item.getString(postAttr), user, item.getString(dateTimeAttr), new ArrayList<>(), new ArrayList<>());
                status.setEpochTime(item.getLong(timeStampAttr));
                statuses.add(status);
            }

            boolean hasMorePages = statuses.size() >= request.getLimit();
            return new FeedResponse(statuses, hasMorePages);

        } catch (Exception ex) {
            System.out.println("Unable to get users story  ex: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("[Server Error] Unable to get users story");
        }
    }

    @Override
    public void batchPost(Status status, List<String> followers, long epochTime) {
        List<Item> toPutItems = new ArrayList<>();

        //create a item for each follower of the status to go into the table
        for (String follower : followers) {
            Item item = new Item().withPrimaryKey(receiverAliasAttr, follower)
                    .withNumber(timeStampAttr, epochTime)
                    .withString(postAttr, status.getPost())
                    .withString(dateTimeAttr, status.getDate())
                    .withString(authorAttr, status.getUser().getAlias())
                    .withString(firstNameAttr, status.getUser().getFirstName())
                    .withString(lastNameAttr, status.getUser().getLastName())
                    .withString(profileImageAttr, status.getUser().getImageUrl());
            toPutItems.add(item);
        }

        try {
            TableWriteItems tableWriteItems = new TableWriteItems(tableName)
                    .withItemsToPut(toPutItems);

            BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(tableWriteItems);
            toPutItems.clear();

            // attempt to write the failed writes if there are any.
            while (outcome.getUnprocessedItems().size() > 0) {
                Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
                outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
            }
        } catch (Exception ex) {
            System.out.println("Error batch writing the feed statuses ex:" + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("[Server Error] Unable to batch write feed statuses");
        }
    }

    private long convertToEpoch(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d yyyy h:mm aaa");
        long epoch = 0;
        try {
            Date date = formatter.parse(dateString);
            epoch = date.getTime();
            return epoch;
        } catch (Exception ex) {
            System.out.println("unable to parse date ex: " + ex.getMessage());
            throw new RuntimeException("[Server Error] unable to parse date");
        }
    }
}
