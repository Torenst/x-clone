package edu.byu.cs.tweeter.server.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenInterface;
import edu.byu.cs.tweeter.server.dao.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.dao.S3Interface;
import edu.byu.cs.tweeter.server.dao.UserInterface;
import edu.byu.cs.tweeter.util.Pair;

public class UserService {

    private final S3Interface s3DAO;
    private final AuthTokenInterface authTokenDAO;
    private final UserInterface userDAO;

    public UserService(DAOFactoryInterface factory) {
        this.userDAO = factory.createUserDAO();
        this.authTokenDAO = factory.createAuthTokenDAO();
        this.s3DAO = factory.createS3DAO();
    }

    public LoginResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        Pair<String, String> temp = userDAO.getUserPasswordSalt(request.getUsername());

        String salt = temp.getFirst();
        String storedPassword = temp.getSecond();

        String regeneratedPassword = generateHash(request.getPassword(), salt);

        if(validatePassword(regeneratedPassword, storedPassword)){
            AuthToken authToken = createAuthentication();
            return new LoginResponse(userDAO.getUser(request.getUsername()), authToken);
        }

        throw new RuntimeException("[Bad Request] Password was incorrect");
    }

    public RegisterResponse register(RegisterRequest request){
        //System.out.println("firstname: " + request.getFirstName() +" lastname: " + request.getLastName() +" username: " + request.getUsername() +"password:" + request.getPassword() +" image:" + request.getImage());
        if(request.getFirstName() == null){
           throw new RuntimeException("[Bad Request] Missing first name");
        } else if(request.getLastName() == null) {
            throw new RuntimeException("[Bad Request] Missing last name");
        } else if(request.getUsername() == null) {
        throw new RuntimeException("[Bad Request] Missing user name");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }else if(request.getImage() == null) {
            throw new RuntimeException("[Bad Request] Missing a profile image");
        }

        //verify that the alias is not already in use.
        if(userDAO.isNameTaken(request.getUsername())) {
            throw new RuntimeException("[Bad Request] alias is already in use");
        }

        // upload our request image to s3
        String imageURL = s3DAO.uploadImage(Base64.getDecoder().decode(request.getImage()),request.getUsername());
        request.setImage(imageURL);

        // set our salt
        request.setSalt(getSalt());

        // hash the password and set it as the request
        String tempPass = generateHash(request.getPassword(), request.getSalt());
        request.setPassword(tempPass);

        // send request
        User user = userDAO.register(request);

        AuthToken authToken = createAuthentication();

        return new RegisterResponse(user, authToken);
    }

    public GetUserResponse getUser(GetUserRequest request){
        if(request.getTargetAlias() == null){
            throw new RuntimeException("[Bad Request] Missing a user in request");
        }

        validateAuthToken(request.getAuthToken());

        User user = userDAO.getUser(request.getTargetAlias());
        return new GetUserResponse(user);
    }

    public LogoutResponse logout(LogoutRequest request){
        if(request.getAuthToken() == null){
            throw new RuntimeException("[Bad Request] Missing authToken for logging out user");
        }

        validateAuthToken(request.getAuthToken());

        authTokenDAO.deleteAuthToken(request.getAuthToken());

        return new LogoutResponse();
    }

    private AuthToken createAuthentication(){
        UUID uuid = UUID.randomUUID();
        AuthToken authToken = new AuthToken(uuid.toString());
        authTokenDAO.createAuthToken(authToken);
        //right now the authtoken doesn't hold the datetime on the returned authtoken, but it checks it in the backend.
        if(authTokenDAO.authenticateUser(authToken)){
            return authToken;
        }
        else {
            throw new RuntimeException("[Bad Request] Authentication expired");
        }
    }

    private String getSalt(){
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
            byte[] salt = new byte[16];
            sr.nextBytes(salt);
            return Base64.getEncoder().encodeToString(salt);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("[Server Error] Unable to get salt");
    }

    private String generateHash(String password, String salt){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("[Server Error] Unable to hash password");
    }

    private boolean validatePassword(String password, String storedPassword){
        return storedPassword.equals(password);
    }

    private void validateAuthToken(AuthToken authToken){
        // Verify AuthToken
        if(!authTokenDAO.authenticateUser(authToken)){
            throw new RuntimeException("[Bad Request] Authentication expired");
        }
    }
}
