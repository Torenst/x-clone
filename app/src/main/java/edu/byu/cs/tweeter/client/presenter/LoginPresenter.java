package edu.byu.cs.tweeter.client.presenter;

public class LoginPresenter extends AuthenticationPresenter {

    public LoginPresenter(AuthView view, String username, String password){
        super(view, username, password);
    }

    public void validateLogin(String alias, String password){
        if (alias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
    }

    public void loginUser(String username, String password){
        userService().LoginUser(username,password, getAuthenticatedObserver());
    }

    public AuthenticatedObserver getAuthenticatedObserver(){
        return new AuthenticatedObserver();
    }
}
