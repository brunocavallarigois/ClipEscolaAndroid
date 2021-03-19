package com.brunogois.clipescolatest.Data;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.brunogois.clipescolatest.MainActivity;
import com.brunogois.clipescolatest.Model.ProviderLoginEnum;
import com.brunogois.clipescolatest.Model.Shared;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class FirebaseManager {

    public static void alterarSenhaUsuario(String novaSenha, FirebaseUser currentUser) {
        currentUser.updatePassword(novaSenha)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("ALTERACAO DE SENHA", "User password updated.");
                        }
                    }
                });
    }


    public static void removerImagemStorage(StorageReference storageReference) {
        // Delete the file
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }

    public static void downloadImagemStorage(StorageReference storageReference) {
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public static void uploadBasicoImagemStorage(StorageReference storageReference, Uri file) {
        // Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
        // StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
        // uploadTask = riversRef.putFile(file);

        UploadTask uploadTask = storageReference.putFile(file);
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });

    }

    public static void uploadBitmapImageToStorage(Bitmap bitmap, StorageReference storageReference) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }

    public static void preencherUserAuthProvider(FirebaseUser currentUser) {
        if (Shared.getInstance().user.authprovider.equals("")) {
            for (UserInfo user : currentUser.getProviderData()) {
                if (user.getProviderId().equals("facebook.com")) {
                    Shared.getInstance().user.authprovider = ProviderLoginEnum.facebook.toString();
                } else if (user.getProviderId().equals("google.com")) {
                    Shared.getInstance().user.authprovider = ProviderLoginEnum.google.toString();
                } else { //user.getProviderId().equals("password")
                    Shared.getInstance().user.authprovider = ProviderLoginEnum.password.toString();
                }
            }
        }
    }

    public static void salvaFotoPerfilUsuario(final Activity activity, final FirebaseUser currentUser) {
        //salvar no firebase
        new Thread(new Runnable() {
            public void run() {
                StorageReference storageProfileImageRef = Shared.getInstance().mainStorageReference
                        .child("users").child(currentUser.getUid()).child("perfil").child("profile");
                final DatabaseReference databaseProfileImageRef = Shared.getInstance().mainDataBaseReference
                        .child("users").child(currentUser.getUid()).child("foto");

                storageProfileImageRef.putFile(Shared.getInstance().imgProfileCarregado).addOnCompleteListener(activity,
                        new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    task.getResult().getMetadata().getReference().getDownloadUrl()
                                            .addOnCompleteListener(activity,
                                                    new OnCompleteListener<Uri>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Uri> task) {
                                                            if (task.isSuccessful()) {
                                                                databaseProfileImageRef.setValue(task.getResult().toString());
                                                                Shared.getInstance().user.foto = task.getResult().toString();
                                                            }
                                                        }
                                                    });
                                } else {
                            Log.w("Data", "Image upload task was not successful.",
                                    task.getException());
                                }
                            }
                        });

            }
        }).start();
    }





}
