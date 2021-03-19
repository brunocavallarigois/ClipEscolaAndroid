package com.brunogois.clipescolatest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.brunogois.clipescolatest.Model.ProviderLoginEnum;
import com.brunogois.clipescolatest.Model.Shared;
import com.brunogois.clipescolatest.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccountActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private DatabaseReference mDatabase;

    EditText mail, pass;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);


        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("pt");
        // To apply the default app language instead of explicitly setting it.
        // auth.useAppLanguage();
        // [END initialize_auth]

        // Views
        mail = findViewById(R.id.editText3);
        pass = findViewById(R.id.editText4);
        progressBar = findViewById(R.id.progressBar);
    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.criar_conta_botao) {
            createAccount(mail.getText().toString(), pass.getText().toString());
        }
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        if (progressBar.getVisibility() == View.INVISIBLE) {
            progressBar.setVisibility(View.VISIBLE);
        }

        final String sMail = email;
        final String sPassword = password;

        // showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            sendEmailVerification();

                            FirebaseUser user = mAuth.getCurrentUser();
                            Shared.getInstance().user.email = user.getEmail();
                            Shared.getInstance().user.authprovider = ProviderLoginEnum.password.toString();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                            if (task.getException().getMessage().toLowerCase().indexOf("email already in use") > -1) {
//
                                updateUI(null);
                            } else {
                                System.out.println(task.getException());
                                System.out.println(task.getException().getMessage());
                                Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }


                    }
                });
        // [END create_user_with_email]
    }

    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        //findViewById(R.id.verifyEmailButton).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(CreateAccountActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(CreateAccountActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mail.setError("Obrigatório");
            valid = false;
        } else if (!Utils.isValidEmail(mail.getText())) {
            mail.setError("E-mail inválido");
            valid = false;
        } else {
            mail.setError(null);
        }


        String password = pass.getText().toString();
        if (TextUtils.isEmpty(password)) {
            pass.setError("Obrigatório");
            valid = false;
        } else if (password.length() < 4) {
            pass.setError("A senha deve ter pelo menos 4 dígitos");
            valid = false;
        } else {
            pass.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.INVISIBLE);
        }

        if (user != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
            mDatabase.setValue(Shared.getInstance().user);

            Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
            startActivity(intent);

            finish();
        }
    }
}