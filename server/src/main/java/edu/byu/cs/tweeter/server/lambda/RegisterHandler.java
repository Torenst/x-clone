package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DAOFactoryInterface;
import edu.byu.cs.tweeter.server.service.UserService;

public class RegisterHandler implements RequestHandler<RegisterRequest, RegisterResponse> {

    @Override
    public RegisterResponse handleRequest(RegisterRequest registerRequest, Context context) {
        DAOFactoryInterface factory = new DAOFactory();
        UserService userService = new UserService(factory);
        return userService.register(registerRequest);
    }
}