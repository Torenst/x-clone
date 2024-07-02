package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.AttributeUpdate;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;

import java.time.LocalDateTime;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class AuthTokenDAO implements AuthTokenInterface {

    private static final String tableName = "authtoken";

    private static final String tokenAttr = "token_value";
    private static final String lastAccessedAttr = "last_accessed";

    private static final int TIMEOUT_SECONDS = 1800;

    private static final AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.standard().withRegion("us-west-2").build();
    private static final DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    private static final Table table = dynamoDB.getTable(tableName);

    @Override
    public void createAuthToken(AuthToken authToken) {
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(TIMEOUT_SECONDS);

        Item item = new Item().withPrimaryKey(tokenAttr, authToken.getToken())
                .withString(lastAccessedAttr, expireTime.toString());

        table.putItem(item);
    }

    @Override
    public void updateAuthToken(AuthToken authToken) {
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(TIMEOUT_SECONDS);

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey(tokenAttr, authToken.getToken())
                .withAttributeUpdate(new AttributeUpdate(lastAccessedAttr).put(expireTime.toString()));

        try {
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
        } catch (Exception ex) {
            System.out.println("error updating AuthToken ex: " + ex.getMessage());
            throw new RuntimeException("[Server Error] unable update authentication.");
        }
    }

    @Override
    public boolean authenticateUser(AuthToken currentAuthToken) {
        try {
            Item item = table.getItem(tokenAttr, currentAuthToken.getToken());

            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime authTime = LocalDateTime.parse(item.getString(lastAccessedAttr));

            AuthToken authToken = new AuthToken(item.getString(tokenAttr));

            if (currentTime.isAfter(authTime)) {
                return false;
            } else {
                authToken.datetime = currentTime.plusSeconds(TIMEOUT_SECONDS).toString();
                updateAuthToken(authToken);
                return true;
            }
        } catch (Exception ex) {
            System.out.println("Unable to authenticate user ex: " + ex.getMessage());
            throw new RuntimeException("[Server Error] unable to authenticate user");
        }
    }

    @Override
    public void deleteAuthToken(AuthToken authToken) {
        table.deleteItem(tokenAttr, authToken.getToken());
    }
}
