package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.util.Pair;

public class UserDAO implements UserInterface {
    private static final String tableName = "users";

    private static final String userAttr = "user_alias";
    private static final String firstNameAttr = "first_name";
    private static final String lastNameAttr = "last_name";
    private static final String profileImageAttr = "profile_image";
    private static final String passwordAttr = "password";
    private static final String saltAttr = "salt";

    private static final String numFollowersAttr = "num_followers";
    private static final String numFollowingAttr = "num_following";

    private static final AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
    private static final DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    private static final Table table = dynamoDB.getTable(tableName);

    @Override
    public User register(RegisterRequest request) {
        Item item = new Item().withPrimaryKey(userAttr, request.getUsername())
                .withString(firstNameAttr, request.getFirstName())
                .withString(lastNameAttr, request.getLastName())
                .withString(profileImageAttr, request.getImage())
                .withString(passwordAttr, request.getPassword())
                .withString(saltAttr, request.getSalt())
                .withNumber(numFollowingAttr, 0)
                .withNumber(numFollowersAttr, 0);

        table.putItem(item);

        return getUser(request.getUsername());
    }

    @Override
    public User getUser(String alias) {
        Item item = table.getItem(userAttr, alias);

        if (item == null) {
            System.out.println("User didn't exist / returned null.");
            throw new RuntimeException("[Bad Request] user did not exist");
        }

        User tempUser = new User(item.getString(firstNameAttr), item.getString(lastNameAttr), item.getString(profileImageAttr));
        tempUser.setAlias(item.getString(userAttr));
        return tempUser;
    }

    @Override
    public Pair<String, String> getUserPasswordSalt(String alias) {
        Item item = table.getItem(userAttr, alias);

        if (item == null) {
            return null;
        }

        String salt = item.getString(saltAttr);
        String hash = item.getString(passwordAttr);

        return new Pair<>(salt, hash);
    }

    @Override
    public void addRemoveFollower(String userAlias, boolean addRemove) {
        // if true we add 1, if false we remove 1
        int count = addRemove ? 1 : -1;

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey(userAttr, userAlias)
                .withUpdateExpression("set " + numFollowersAttr + " = " + numFollowersAttr + " + :val")
                .withValueMap(new ValueMap().withNumber(":val", count))
                .withReturnValues(ReturnValue.UPDATED_NEW);

        try {
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
        } catch (Exception ex) {
            System.out.println("Unable to update follower count : ex" + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("[Server Error] unable to update follower count");
        }
    }

    @Override
    public void addRemoveFollowing(String userAlias, boolean addRemove) {
        // if true we add 1, if false we remove 1
        int count = addRemove ? 1 : -1;

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey(userAttr, userAlias)
                .withUpdateExpression("set " + numFollowingAttr + " = " + numFollowingAttr + " + :val")
                .withValueMap(new ValueMap().withNumber(":val", count))
                .withReturnValues(ReturnValue.UPDATED_NEW);

        try {
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
        } catch (Exception ex) {
            System.out.println("Unable to update follower count : ex" + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("[Server Error] unable to update follower count");
        }
    }

    @Override
    public int getUserFollowerCount(String alias) {
        Item item = table.getItem(userAttr, alias);

        if (item == null) {
            return 0;
        }
        return item.getNumber(numFollowersAttr).intValue();
    }

    @Override
    public int getUserFollowingCount(String alias) {
        Item item = table.getItem(userAttr, alias);

        if (item == null) {
            return 0;
        }
        return item.getNumber(numFollowingAttr).intValue();
    }

    @Override
    public boolean isNameTaken(String alias) {
        Item item = table.getItem(userAttr, alias);

        return item != null;
    }


    @Override
    public void addUserBatch(List<User> users) {

        // Constructor for TableWriteItems takes the name of the table, which I have stored in TABLE_USER
        TableWriteItems items = new TableWriteItems(tableName);

        // Add each user into the TableWriteItems object
        for (User user : users) {
            Item item = new Item()
                    .withPrimaryKey(userAttr, user.getAlias())
                    .withString(firstNameAttr, user.getName())
                    .withString(lastNameAttr, user.getLastName())
                    .withString(profileImageAttr, user.getImageUrl())
                    .withNumber(numFollowersAttr, 0)
                    .withNumber(numFollowingAttr, 1);
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
        System.out.println("Wrote User Batch");

        // Check the outcome for items that didn't make it onto the table
        // If any were not added to the table, try again to write the batch
        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
            System.out.println("Wrote more Users");
        }
    }
}
