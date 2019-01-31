package com.myhost.spyros.mywallchat;

import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword, editTextUserName;
    private ProgressBar progressBar;
    private TextView actionTextView;
    private Button action_btn;

    private boolean usernameFound = false;
    private boolean userFound = false;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextName = findViewById(R.id.edit_text_name);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        editTextUserName = findViewById(R.id.edit_text_username);
        actionTextView = findViewById(R.id.action_lbl);
        action_btn = findViewById(R.id.action_button);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.action_button).setOnClickListener(actionListener());
        actionTextView.setOnClickListener(action_lbl_Listener());

    }


    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            //handle the already login user
        }
    }




    private View.OnClickListener actionListener(){
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String current_text = action_btn.getText().toString();
                if(current_text.equals("Register")){
                    registerUser();
                }
                else{
                    loginUser();
                }
            }
        };
    }

    private View.OnClickListener action_lbl_Listener(){
        return new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String current_text = actionTextView.getText().toString();
                if(current_text.equals("Already have an account? Login.")){
                    action_btn.setText("Login");
                    actionTextView.setText("Register");
                    editTextName.setVisibility(View.INVISIBLE);
                    editTextUserName.setVisibility(View.INVISIBLE);
                }
                else{
                    action_btn.setText("Register");
                    actionTextView.setText("Already have an account? Login.");
                    editTextName.setVisibility(View.VISIBLE);
                    editTextUserName.setVisibility(View.VISIBLE);
                }
            }
        };
    }



    private void loginUser(){


        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please insert a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Please insert a password");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        //userFound = false;
        FirebaseDatabase.getInstance().getReference().child("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            if(user.email.equals(email) && user.password.equals(password)){
                                Intent intent = new Intent(MainActivity.this,WallChatActivity.class);
                                intent.putExtra("username",user.username);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(),"Welcome "+user.username,Toast.LENGTH_SHORT).show();
                                editTextEmail.setText("");
                                editTextEmail.requestFocus();
                                editTextPassword.setText("");
                                progressBar.setVisibility(View.INVISIBLE);
                                //userFound = true;
                                break;
                            }
                            else if(user.email.equals(email) && !user.password.equals(password)){
                                Toast.makeText(getApplicationContext(),"Wrong password",Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
//        if(!userFound){
//            Toast.makeText(getApplicationContext(),"Login failed. Try again.",Toast.LENGTH_SHORT).show();
//        }
    }


    private void registerUser(){
        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String username = editTextUserName.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError("Please insert name");
            editTextName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Please insert email");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please insert a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Please insert a password");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must have at least 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            editTextUserName.setError("Please insert a username");
            editTextUserName.requestFocus();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);

        if(checkUsernames(username)){
            //Toast.makeText(getApplicationContext(),username,Toast.LENGTH_SHORT).show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                User user = new User(
                                        name,
                                        email,
                                        username,
                                        password
                                );
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressBar.setVisibility(View.GONE);
                                        //if(checkUsernames(username)){
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Registration Successfull", Toast.LENGTH_LONG).show();
                                                editTextName.setText("");
                                                editTextEmail.setText("");
                                                editTextPassword.setText("");
                                                editTextUserName.setText("");
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Something went wrong. Try again", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        //}
//                                        else {
//                                            Toast.makeText(getApplicationContext(), "fsdfwefwe", Toast.LENGTH_LONG).show();
//                                        }

                                    }
                                });
                            }
                            else{
                                progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                Toast.makeText(getApplicationContext(), "Email or Username already exists", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else{
            progressBar.setVisibility(View.GONE);
        }

    }



    private boolean checkUsernames(final String username){
        usernameFound = false;
        FirebaseDatabase.getInstance().getReference().child("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            User user = snapshot.getValue(User.class);
                            if(user.username.equals(username)){
                                usernameFound = true;
                                //Toast.makeText(getApplicationContext(),"to vrika",Toast.LENGTH_SHORT).show();
                                break;
                            }else
                                continue;
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
        if(!usernameFound){

            return true;
        }
        else{
            //Toast.makeText(getApplicationContext(),"Edwwww",Toast.LENGTH_SHORT).show();

            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            return false;
        }


    }





}


