package com.brunogois.clipescolatest.Model;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

public class Shared {

    private static Shared instance;

    public static Shared getInstance() {
        if (instance == null) {
            instance = new Shared();
        }
        return instance;
    }

    public User user = new User();
    public Uri imgProfileCarregado = null;


    public DatabaseReference mainDataBaseReference;
    public StorageReference mainStorageReference;
    public FirebaseAuth mainFirebaseAuth;
    public FirebaseUser mainFirebaseUser;

}
