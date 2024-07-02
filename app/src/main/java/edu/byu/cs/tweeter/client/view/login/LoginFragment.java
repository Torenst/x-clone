package edu.byu.cs.tweeter.client.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import edu.byu.cs.tweeter.R;
import edu.byu.cs.tweeter.client.presenter.AuthenticationPresenter;
import edu.byu.cs.tweeter.client.presenter.LoginPresenter;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Implements the login screen.
 */
public class LoginFragment extends Fragment implements AuthenticationPresenter.AuthView {
    private static final String LOG_TAG = "LoginFragment";

    private Toast infoToast;
    private EditText alias;
    private EditText password;
    private TextView errorView;

    /**
     * Creates an instance of the fragment and places the user and auth token in an arguments
     * bundle assigned to the fragment.
     *
     * @return the fragment.
     */
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        alias = view.findViewById(R.id.loginUsername);
        password = view.findViewById(R.id.loginPassword);
        errorView = view.findViewById(R.id.loginError);
        Button loginButton = view.findViewById(R.id.loginButton);

        //infoToast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);

        // create the presenter.
        LoginPresenter presenter = new LoginPresenter(this,alias.getText().toString(),password.getText().toString());

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Login and move to MainActivity.
                try {
                    String username = alias.getText().toString();
                    String userPassword = password.getText().toString();

                    // validate the login.
                    presenter.validateLogin(username, userPassword);
                    errorView.setText(null);

                    //loginInToast = Toast.makeText(getContext(), "Logging In...", Toast.LENGTH_LONG);
                    //loginInToast.show();
                    displayInfoMessage("Logging In...");

                    presenter.loginUser(username,userPassword);

                } catch (Exception e) {
                    errorView.setText(e.getMessage());
                }
            }
        });

        return view;
    }

    @Override
    public void authenticated(User user) {
        //loginInToast.cancel();
        clearInfoMessage();
        Toast.makeText(getContext(), "Hello " + user.getName(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra(MainActivity.CURRENT_USER_KEY, user);
        startActivity(intent);
    }

    @Override
    public void displayFailureMessage(String message) {
        Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, message);
    }

    @Override
    public void displayInfoMessage(String message) {
        infoToast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        infoToast.show();
    }

    @Override
    public void displayExceptionMessage(String message, Exception exception) {
        Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
        Log.e(LOG_TAG, message, exception);
    }

    @Override
    public void clearInfoMessage() {
        infoToast.cancel();
    }
}
