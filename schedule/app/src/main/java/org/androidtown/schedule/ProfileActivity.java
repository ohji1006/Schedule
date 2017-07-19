package org.androidtown.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private String uid,password,gid;
    private TextView textViewUserEmail;
    private Button buttonLogout;
    private String UID;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private EditText editTextName, editTextAddress,editGroupName;
    private Button buttonSave,deleteUser,buttonJoin;
    private FirebaseUser user;
    private String get_groupId_text_toString;
    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar=null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

    //    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    //    setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();

        uid = intent.getStringExtra("uid");

       // password = intent.getStringExtra("password");

        /*
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
*/

        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editGroupName = (EditText) findViewById(R.id.groupJoin);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonJoin = (Button) findViewById(R.id.join_button);
        deleteUser = (Button) findViewById(R.id.deleteuser);
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        textViewUserEmail.setText("welcome"+" "+user.getEmail());


        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    //You need to get here the token you saved at logging-in time.
                    String token = "userSavedToken";
                    //You need to get here the password you saved at logging-in time.
                    String npassword = password;

                    AuthCredential credential;

                    //This means you didn't have the token because user used like Facebook Sign-in method.

                    credential = EmailAuthProvider.getCredential(user.getEmail(), npassword);

                    //Doesn't matter if it was Facebook Sign-in or others. It will always work using GoogleAuthProvider for whatever the provider.
                    //  credential = GoogleAuthProvider.getCredential(token, null);


                    //We have to reauthenticate user because we don't know how long
                    //it was the sign-in. Calling reauthenticate, will update the
                    //user login and prevent FirebaseException (CREDENTIAL_TOO_OLD_LOGIN_AGAIN) on user.delete()
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //Calling delete to remove the user and wait for a result.
                                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Ok, user remove
                                            } else {
                                                //Handle the exception
                                                task.getException();
                                            }
                                        }
                                    });
                                }
                            });
                }

                // firebaseAuth.signOut();
                // finish();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });
        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_groupId_text_toString = editGroupName.getText().toString();
                //get_groupId_text_toString = gname.getText().toString();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/Users/" + uid + "/groups/" + get_groupId_text_toString , true);
                childUpdates.put("/Groups/" + get_groupId_text_toString + "/member/" + uid, true);



                databaseReference.updateChildren(childUpdates);
            }
        });
    }

    private void saveUserInformation(){
        String name = editTextName.getText().toString().trim();
        String add = editTextAddress.getText().toString().trim();


        UserInformation userInformation = new UserInformation(name, add);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        //    UID = user.getUid()+"";
        databaseReference.child("Users").child(user.getUid()).setValue(userInformation);

        Toast.makeText(this, "정보 저장완료", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.second, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){

            case R.id.nav_home:
                Intent h = new Intent(ProfileActivity.this, SecondActivity.class);
                startActivity(h);
                break;
            case R.id.nav_my_calendar:
                Intent m = new Intent(ProfileActivity.this , My_Calendar_Activity.class);
                startActivity(m);
                break;
            case R.id.nav_group_calendar:
                Intent g = new Intent(ProfileActivity.this , Show_groups_Activity.class);
                g.putExtra("uid", uid);
                startActivity(g);
                break;
            case R.id.nav_setting:
                Intent s = new Intent(ProfileActivity.this , ProfileActivity.class);
                s.putExtra("uid", uid);
                startActivity(s);
                break;
            case R.id.nav_logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}