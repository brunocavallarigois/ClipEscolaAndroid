package com.brunogois.clipescolatest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.brunogois.clipescolatest.Data.FirebaseManager;
import com.brunogois.clipescolatest.Model.Shared;
import com.brunogois.clipescolatest.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        Shared.getInstance().mainDataBaseReference = FirebaseDatabase.getInstance().getReference();
        Shared.getInstance().mainStorageReference = FirebaseStorage.getInstance().getReference();
        Shared.getInstance().mainFirebaseAuth = FirebaseAuth.getInstance();
        Shared.getInstance().mainFirebaseAuth.setLanguageCode("pt");
        Shared.getInstance().mainFirebaseUser = Shared.getInstance().mainFirebaseAuth.getCurrentUser();



        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                if (currentUser != null) {
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
                    ValueEventListener userListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get Post object and use the values to update the UI
                            User usuario = dataSnapshot.getValue(User.class);
                            usuario.id = dataSnapshot.getKey();
                            Shared.getInstance().user = usuario;

                            FirebaseManager.preencherUserAuthProvider(currentUser);

                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Log.w("SplashActivity", "loadPost:onCancelled", databaseError.toException());
                            // ...
                        }
                    };
                    //TODO: Alterado para single por causa do erro de voltar pra home ao criar novo chat
                    //mDatabase.addValueEventListener(userListener);
                    mDatabase.addListenerForSingleValueEvent(userListener);

                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2500);
    }
}