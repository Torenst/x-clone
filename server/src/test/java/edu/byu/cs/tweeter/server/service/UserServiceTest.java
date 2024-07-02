//package edu.byu.cs.tweeter.server.service;
//
//import org.junit.Assert;
//import org.junit.Test;
//
//import edu.byu.cs.tweeter.model.domain.AuthToken;
//import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
//import edu.byu.cs.tweeter.model.net.request.LoginRequest;
//import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
//import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
//import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
//import edu.byu.cs.tweeter.model.net.response.LoginResponse;
//import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
//import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
//import edu.byu.cs.tweeter.server.dao.DAOFactory;
//import edu.byu.cs.tweeter.server.dao.DAOFactoryInterface;
//
//public class UserServiceTest {
//    private DAOFactoryInterface factory = new DAOFactory();
//    private UserService userService = new UserService(factory);
//
//    @Test
//    public void RegisterTest(){
//
//        RegisterRequest request = new RegisterRequest("john","doe","@john1Doe","12345","");
//
//        RegisterResponse response = userService.register(request);
//
//        Assert.assertNotNull(response);
//    }
//
//    @Test
//    public void AuthenticateUser(){
//        AuthToken tempAuth = new AuthToken();
//
//        LoginRequest request = new LoginRequest("@johnDoe","12345");
//
//        LoginResponse response = userService.login(request);
//
//        Assert.assertNotNull(response);
//    }
//
//
//    @Test
//    public void AuthenticateUser_incorrect(){
//        AuthToken tempAuth = new AuthToken();
//
//        LoginRequest request = new LoginRequest("@wrongUserAndPass","12345");
//
//        LoginResponse response = userService.login(request);
//
//        Assert.assertNull(response);
//    }
//
//    @Test
//    public void GetUserTest(){
//        AuthToken tempAuth = new AuthToken();
//        LoginRequest requestLogin = new LoginRequest("@johnDoe","12345");
//        LoginResponse responseLogin = userService.login(requestLogin);
//
//        GetUserRequest request = new GetUserRequest(responseLogin.getAuthToken(), "@johnDoe");
//
//        GetUserResponse response = userService.getUser(request);
//
//        Assert.assertNotNull(response);
//    }
//
//    @Test
//    public void logoutTest(){
//        AuthToken tempAuth = new AuthToken();
//        LoginRequest request = new LoginRequest("@johnDoe","12345");
//        LoginResponse response = userService.login(request);
//        Assert.assertNotNull(response);
//
//        LogoutRequest requestLogout = new LogoutRequest(response.getAuthToken());
//        LogoutResponse responseLogout = userService.logout(requestLogout);
//
//        Assert.assertNotNull(responseLogout);
//    }
//}
