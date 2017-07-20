package org.androidtown.schedule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SecTempLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSignup;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignin;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  Toast.makeText(this, "Here2", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.secactivity_temp_login);

      //  Toast.makeText(this, "Here2", Toast.LENGTH_SHORT).show();
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            //profile activity here
            finish();
            startActivity(new Intent(getApplicationContext(),SettingActivity.class));
        }

        progressDialog = new ProgressDialog(this);

        buttonSignup = (Button) findViewById(R.id.buttonSignup);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        textViewSignin = (TextView) findViewById(R.id.textViewSignin);

    //    Toast.makeText(this, "Here3", Toast.LENGTH_SHORT).show();

        buttonSignup.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
    }


    private void registerUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            //email is empty
           // Toast.makeText(this, "please enter email", Toast.LENGTH_SHORT).show();
            //stopping the function execution further
            return;

        }
        if(TextUtils.isEmpty(password)){
            //password is empty

          //  Toast.makeText(this, "please enter Password", Toast.LENGTH_SHORT).show();

            //stopping the function execution further
            return;
        }

        //if validations are ok we will first show a progressbar

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

                                finish();

                                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));

                        //    Toast.makeText(SecTempLoginActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                        }else{
                        //    Toast.makeText(SecTempLoginActivity.this, "Could not register..please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    @Override
    public void onClick(View v) {

        if(v == buttonSignup){
            registerUser();
        }

        if(v == textViewSignin)
        {
            startActivity(new Intent(this, LoginActivity.class));
        }


    }
}
