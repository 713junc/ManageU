package me.jennachoi.to_doapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;



import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText useremail;
    private EditText userpassword;

    private Button login;
    private TextView signup;

    private ProgressDialog pDialog;


    SignInButton GoogleLogin;
    static final int GOOGLE_SIGN = 123;
    GoogleSignInClient mGoogleSignInClient;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

        useremail = findViewById(R.id.user_email);
        userpassword = findViewById(R.id.user_password);

        login = findViewById(R.id.user_login);
        signup = findViewById(R.id.user_signup);


        pDialog = new ProgressDialog(this);



        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                String input_email = useremail.getText().toString().trim();
                String input_password = userpassword.getText().toString().trim();

                if(TextUtils.isEmpty(input_email)){
                    useremail.setError("Please enter your email");
                    return;
                }
                if(TextUtils.isEmpty(input_password)){
                    userpassword.setError("Please enter your password");
                }

                pDialog.setMessage("Processing Your Login...");
                pDialog.show();

                mAuth.signInWithEmailAndPassword(input_email, input_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            pDialog.dismiss();

                        } else {
                            Toast.makeText(getApplicationContext(),"Something goes wrong", Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                        }
                    }
                });



            }
        }); //user login


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        }); //user sign-up



        //Google-signIn

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);


        GoogleLogin = (SignInButton)findViewById(R.id.btnGoogle);
        GoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signIntent, GOOGLE_SIGN);
            }
        });


    } //onCreate




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn
                    .getSignedInAccountFromIntent(data);

            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately

                // ...
            }
        }
    }




    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                            Toast.makeText(getApplicationContext(),"User Login Succesfully..", Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(),"Something goes wrong", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


} //MainActivity
