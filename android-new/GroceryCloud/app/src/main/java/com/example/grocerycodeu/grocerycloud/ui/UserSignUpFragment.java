package com.example.grocerycodeu.grocerycloud.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.grocerycodeu.grocerycloud.R;

public class UserSignUpFragment extends Fragment{

    EditText txtUsername;
    EditText txtPassword;
    EditText txtPasswordRetype;
    EditText txtEmail;

    Button btnSignUp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Get a reference to this fragment
        final Fragment thisFragment = this;

        //Get the root view
        View rootView = inflater.inflate(R.layout.fragment_signup, container, false);

        //Find the edit text views
        txtUsername = (EditText) rootView.findViewById(R.id.username_text_view);
        txtPassword = (EditText) rootView.findViewById(R.id.password_text_view);
        txtPasswordRetype = (EditText) rootView.findViewById(R.id.password_retype_text_view);
        txtEmail = (EditText) rootView.findViewById(R.id.email_text_view);

        //Find the login button
        btnSignUp = (Button) rootView.findViewById(R.id.sign_up_button);

        //Go to the sign up activity
        btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Get the user data
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();
                String email = txtEmail.getText().toString();

                //Notify the user if the passwords do not match
                if (!password.equals(txtPasswordRetype.getText().toString())){
                    return;
                }

            }
        });

        return rootView;
    }
}
