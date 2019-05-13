package me.jennachoi.to_doapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;

    private Button signup;
    private TextView login;

    private FirebaseAuth mAuth;
    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        pDialog = new ProgressDialog(this);

        email = findViewById(R.id.new_email);
        password = findViewById(R.id.new_password);

        signup = findViewById(R.id.new_signup);
        login = findViewById(R.id.back_signin);


        signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                String newEmail = email.getText().toString().trim();
                String newPassword = password.getText().toString().trim();

                if(TextUtils.isEmpty(newEmail)){
                    email.setError("You need to provide your Email");
                    return;
                }

                if(TextUtils.isEmpty(newPassword)){
                    password.setError("You need to provide your Password");
                    return;
                }

                pDialog.setMessage("Processing Your Sign-Up...");
                pDialog.show();

                mAuth.createUserWithEmailAndPassword(newEmail,newPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            pDialog.dismiss();

                        } else {

                            Toast.makeText(getApplicationContext(), "Somthing goes wrong", Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                        }
                    }
                });
            }
        } );


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

            }
        });


    }
}
