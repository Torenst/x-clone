package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class FollowDAO implements FollowInterface {

    private static final String tableName = "follows";
    private static final String indexName = "follows_index";

    private static final String followerAttr = "follower_handle";
    private static final String followeeAttr = "followee_handle";

    private static final String followeeFirstNameAttr = "followee_first_name";
    private static final String followeeLastNameAttr = "followee_last_name";
    private static final String followeeProfileImageAttr = "followee_profile_image";

    private static final String followerFirstNameAttr = "follower_first_name";
    private static final String followerLastNameAttr = "follower_last_name";
    private static final String followerProfileImageAttr = "follower_profile_image";

    private static final AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
    private static final DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    private static final Table table = dynamoDB.getTable(tableName);

    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    @Override
    public FollowingResponse getFollowees(FollowingRequest request) {
        List<User> users = new ArrayList<>();

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":f", request.getFollowerAlias());

        QuerySpec querySpec = new QuerySpec().withScanIndexForward(true)
                .withKeyConditionExpression(followerAttr + "= :f")
                .withValueMap(valueMap)
                .withMaxResultSize(request.getLimit());

        ItemCollection<QueryOutcome> items;

        if (request.getLastFolloweeAlias() != null) {
            PrimaryKey primaryKey = new PrimaryKey(followerAttr, request.getFollowerAlias(),
                    followeeAttr, request.getLastFolloweeAlias());
            querySpec.withExclusiveStartKey(primaryKey);
        }

        try {
            items = table.query(querySpec);

            for (Item item : items) {
                User user = new User(item.getString(followeeFirstNameAttr), item.getString(followeeLastNameAttr),
                        item.getString(followeeAttr), item.getString(followeeProfileImageAttr));
                users.add(user);
            }

            boolean hasMorePages = users.size() >= request.getLimit();

            return new FollowingResponse(users, hasMorePages);
        } catch (Exception ex) {
            System.out.println("Unable to get followers. ex: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("[Server error] unable to get followees.");
        }
    }

    @Override
    public FollowerResponse getFollowers(FollowerRequest request) {
        Index index = table.getIndex(indexName);

        List<User> users = new ArrayList<>();

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":f", request.getFollowerAlias());

        QuerySpec querySpec = new QuerySpec().withScanIndexForward(true)
                .withKeyConditionExpression(followeeAttr + "= :f")
                .withValueMap(valueMap)
                .withMaxResultSize(request.getLimit());

        if (request.getLastFolloweeAlias() != null) {
            PrimaryKey primaryKey = new PrimaryKey(followerAttr, request.getLastFolloweeAlias(),
                    followeeAttr, request.getFollowerAlias());

            querySpec.withExclusiveStartKey(primaryKey);
        }

        ItemCollection<QueryOutcome> items;

        try {
            items = index.query(querySpec);

            for (Item item : items) {
                User user = new User(item.getString(followerFirstNameAttr), item.getString(followerLastNameAttr),
                        item.getString(followerAttr), item.getString(followerProfileImageAttr));
                users.add(user);
            }

            boolean hasMorePages = users.size() >= request.getLimit();
            return new FollowerResponse(users, hasMorePages);

        } catch (Exception ex) {
            System.out.println("Unable to get followers. ex: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("[Server error] unable to get followers.");
        }
    }

    @Override
    public FollowResponse follow(FollowRequest request) {
        Item item = new Item().withPrimaryKey(followerAttr, request.getCurrentUser().getAlias())
                .withString(followeeAttr, request.getFollowUser().getAlias())
                .withString(followeeFirstNameAttr,request.getFollowUser().getFirstName())
                .withString(followeeLastNameAttr,request.getFollowUser().getLastName())
                .withString(followeeProfileImageAttr,request.getFollowUser().getImageUrl())
                .withString(followerFirstNameAttr,request.getCurrentUser().getFirstName())
                .withString(followerLastNameAttr,request.getCurrentUser().getLastName())
                .withString(followerProfileImageAttr,request.getCurrentUser().getImageUrl());

        try {
            table.putItem(item);
            return new FollowResponse(true);
        } catch (Exception ex) {
            System.out.println("Unable to follow user ex: " + ex.getMessage());
            ex.printStackTrace();
            return new FollowResponse("[Server Error] unable to follow user");
        }
    }

    @Override
    public UnfollowResponse unfollow(UnfollowRequest request) {
        try {
            table.deleteItem(followerAttr, request.getCurrentUser().getAlias(), followeeAttr, request.getUnfollowUser().getAlias());
            return new UnfollowResponse(true);
        } catch (Exception ex) {
            System.out.println("Error unfollowing user ex: " + ex.getMessage());
            ex.printStackTrace();
            return new UnfollowResponse("[Server Error] unable to remove followed user");
        }
    }

    @Override
    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        Item item = table.getItem(followerAttr, request.getFollower().getAlias(), followeeAttr, request.getFollowee().getAlias());

        if (item == null) {
            return new IsFollowerResponse(false);
        } else {
            return new IsFollowerResponse(true);
        }
    }

    @Override
    public List<String> getAllFollowers(String alias) {
        List<String> followees = new ArrayList<>();

        Index index = table.getIndex(indexName);

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":f", alias);

        QuerySpec querySpec = new QuerySpec()
                .withScanIndexForward(true)
                .withKeyConditionExpression(followeeAttr + "= :f")
                .withValueMap(valueMap);

        ItemCollection<QueryOutcome> items;
        Iterator<Item> iterator;
        Item item;

        try {
            items = index.query(querySpec);

            iterator = items.iterator();

            while (iterator.hasNext()) {
                item = iterator.next();
                followees.add(item.getString(followerAttr));
            }
            return followees;

        } catch (Exception ex) {
            System.out.println("Unable to get all followers");
            ex.printStackTrace();
            throw new RuntimeException("[Server Error] Unable to get all followers");
        }
    }

    @Override
    public void addBatchFollow(List<User> users, User followee){
        // Constructor for TableWriteItems takes the name of the table, which I have stored in TABLE_USER
        TableWriteItems items = new TableWriteItems(tableName);

        // Add each user into the TableWriteItems object
        for (User user : users) {
            Item item = new Item().withPrimaryKey(followerAttr, user.getAlias())
                    .withString(followeeAttr, followee.getAlias())
                    .withString(followeeFirstNameAttr,followee.getFirstName())
                    .withString(followeeLastNameAttr,followee.getLastName())
                    .withString(followeeProfileImageAttr,followee.getImageUrl())
                    .withString(followerFirstNameAttr,user.getFirstName())
                    .withString(followerLastNameAttr,user.getLastName())
                    .withString(followerProfileImageAttr,user.getImageUrl());
            items.addItemToPut(item);

            // 25 is the maximum number of items allowed in a single batch write.
            // Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items);
                items = new TableWriteItems(tableName);
            }
        }

        // Write any leftover items
        if (items.getItemsToPut() != null && items.getItemsToPut().size() > 0) {
            loopBatchWrite(items);
        }
    }

    private void loopBatchWrite(TableWriteItems items) {

        // The 'dynamoDB' object is of type DynamoDB and is declared statically in this example
        BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(items);
        System.out.println("Wrote Follow Batch");

        // Check the outcome for items that didn't make it onto the table
        // If any were not added to the table, try again to write the batch
        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
            System.out.println("Wrote more follows");
        }
    }
}
