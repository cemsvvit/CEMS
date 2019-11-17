package com.rgs.cems.Auth;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rgs.cems.R;

import java.util.Date;


public class Signup extends AppCompatActivity {
    public EditText emailId;
    public EditText password;
    public EditText username;
    Button buttom_signup;
    TextView signIn;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();
        firebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.email_signup);
        username = findViewById(R.id.username_signup);
        password = findViewById(R.id.password_signup);
        setTitle("SignUp");
        buttom_signup = findViewById(R.id.button_signup);
        signIn = findViewById(R.id.signin_signup);
        buttom_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String emailID = emailId.getText().toString();
                String paswd = password.getText().toString();
                final String name = username.getText().toString();

                if (emailID.isEmpty()) {
                    username.setError("Set your Username");
                    username.requestFocus();
                } else if (name.isEmpty()) {
                    emailId.setError("Provide your Email first!");
                    emailId.requestFocus();
                } else if (paswd.isEmpty()) {
                    password.setError("Set your password");
                    password.requestFocus();
                } else if (!(emailID.isEmpty() && paswd.isEmpty() && name.isEmpty())) {
                    firebaseAuth.createUserWithEmailAndPassword(emailID, paswd).addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(Signup.this.getApplicationContext(),
                                        "SignUp unsuccessful: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            } else {

                                //Data
                                Date d = new Date();
                                CharSequence s  = DateFormat.format("MMMM d, yyyy ", d.getTime());

                                //Storing data to display in the Nav bar and in the app
                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("sp",0);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("uid" , firebaseAuth.getUid());
                                editor.putString("name" , name);
                                editor.putString("email" , emailID);
                                editor.apply();

                                //To save data in Firebase Database
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users/" + firebaseAuth.getUid());
                                databaseReference.child("Name").setValue(name);
                                databaseReference.child("Email").setValue(emailID);
                                databaseReference.child("UID").setValue(firebaseAuth.getUid());
                                databaseReference.child("Date").setValue(s);
                                databaseReference.child("V1").setValue(0);
                                databaseReference.child("V2").setValue(0);
                                //TODO: The place where intent should be placed
                                showaccountcreatedDialog();
                                Log.d("signup" , "YYYYYYYYYYYYYYYY");
                            }
                        }
                    });
                } else {
                    Toast.makeText(Signup.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Signup.this, Login.class));
            }
        });

        if(isNetworkAvailable()){
            Log.d("hehehehe" , "net");
        } else {
            showCustomDialog();
            Log.d("hehehehe" , "nonet");
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showCustomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.nonet_warning);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    //TODO: Make this work
    public void showaccountcreatedDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.acc_confirmed);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Signup.this, "ITS pmONNNN", Toast.LENGTH_SHORT).show();
                Log.d("invoked" , "Itsnnn");
              //  startActivity( new Intent(Signup.this, MainActivity.class));
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

}

