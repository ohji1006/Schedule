package org.androidtown.schedule;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import utils.CheckNetwork;
import utils.Constants;
import utils.ValidateUserInfo;

/**
 * Created by Ei Seok on 2017-07-19.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edit_email, edit_password;
    TextView txt_alreadyHave;
    Button btn_registrar;
    private CreateUserTask mCreateTask = null;


    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.inearLayout);
        linearLayout.setBackgroundResource(R.drawable.six);

        String email;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            email = extras == null ? "" : extras.getString(Constants.TAG_EMAIL);
        } else {
            email = savedInstanceState.getString(Constants.TAG_EMAIL);
        }

        firebaseAuth = FirebaseAuth.getInstance();

//        if(firebaseAuth.getCurrentUser() != null){
//            //profile activity here
//            finish();
//            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
//        }

        progressDialog = new ProgressDialog(this);

        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_email.setText(email);
        edit_password = (EditText) findViewById(R.id.edit_password);
        txt_alreadyHave = (TextView) findViewById(R.id.txt_already_have);
        txt_alreadyHave.setOnClickListener(this);

        btn_registrar = (Button) findViewById(R.id.btn_register);
        btn_registrar.setOnClickListener(this);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //buttonSignup = (Button) findViewById(buttonSignup);

        //editTextEmail = (EditText) findViewById(editTextEmail);
        //editTextPassword = (EditText) findViewById(editTextPassword);

        //textViewSignin = (TextView) findViewById(textViewSignin);



        //buttonSignup.setOnClickListener(this);
        //textViewSignin.setOnClickListener(this);
    }


    private void attemptCreate(){
        String email = edit_email.getText().toString();
        String password = edit_password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        ValidateUserInfo validate = new ValidateUserInfo();

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            edit_email.setError(getString(R.string.error_field_required));
            focusView = edit_email;
            cancel = true;
        } else if (!validate.isEmailValid(email)) {
            edit_email.setError(getString(R.string.error_invalid_email));
            focusView = edit_email;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            edit_password.setError(getString(R.string.error_field_required));
            focusView = edit_password;
            cancel = true;
        } else if (!validate.isPasswordValid(password)) {
            edit_password.setError(getString(R.string.error_invalid_password));
            focusView = edit_password;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            //TODO Create account logic
            // Show a progress spinner, and kick off a background task to
            // perform the user registration attempt.
            mCreateTask = new CreateUserTask( email, password);
            mCreateTask.execute((Void) null);




        }
        if(TextUtils.isEmpty(email)){
            //email is empty
        //    Toast.makeText(this, "please enter email", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;

        }
        if(TextUtils.isEmpty(password)){
            //password is empty

        //    Toast.makeText(this, "please enter Password", Toast.LENGTH_SHORT).show();

            //stopping the function execution further
            return;
        }

        //if validations are ok we will first show a progressbar

        //progressDialog.setMessage("Resigtering User...");
        //progressDialog.show();
/*
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //user is successfully register and logged in
                            //we will start the profile activity here
                            //righ now let display a toast only

                            finish();

                            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));

                            Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(RegisterActivity.this, "Could not register..please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/

    }
    @Override
    public void onClick(View v) {

        if(v == btn_registrar){
            attemptCreate();
        }

        if(v == txt_alreadyHave)
        {
            startActivity(new Intent(RegisterActivity.this, LoginActivity2.class));
            finish();
        }


    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class CreateUserTask extends AsyncTask<Void, Void, Boolean> {
        // private final String mName;
        private final String mEmail;
        private final String mPassword;

        CreateUserTask(String email, String password) {
            // mName = name;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: check if account already exists against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            // TODO: if there's no account registered, register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mCreateTask = null;
            CheckNetwork checkNetwork = new CheckNetwork();
            if (checkNetwork.isConnected(RegisterActivity.this) && success) {

                userCreate();
            //    Toast.makeText(RegisterActivity.this, "Account created", Toast.LENGTH_SHORT).show();


            } else {
            //    Toast.makeText(RegisterActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mCreateTask = null;
        }
    }



    private void userCreate(){

        String email = edit_email.getText().toString();
        String password = edit_password.getText().toString();


        progressDialog.setMessage("Resigtering User...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //user is successfully register and logged in
                            //we will start the profile activity here
                            //righ now let display a toast only

                            startActivity(new Intent(getApplicationContext(),LoginActivity2.class));
                            finish();

                       //     Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                        }else{
                       //     Toast.makeText(RegisterActivity.this, "Could not register..please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}



