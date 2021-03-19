package com.brunogois.clipescolatest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.brunogois.clipescolatest.Data.FirebaseManager;
import com.brunogois.clipescolatest.Model.Shared;
import com.brunogois.clipescolatest.Utils.MaskManager;
import com.bumptech.glide.Glide;


import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    CircleImageView editar_avatar;
    EditText editar_nome, editar_email, editar_telefone;
    Spinner editar_estadocivil, editar_sexo;
    public ProgressBar progress;

    public Boolean atualizouImagemPerfil = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        progress = findViewById(R.id.progress);
        editar_avatar = findViewById(R.id.editar_avatar);

        editar_nome = findViewById(R.id.editar_nome);
        editar_email = findViewById(R.id.editar_email);
        editar_telefone = findViewById(R.id.editar_telefone);
        editar_estadocivil = findViewById(R.id.editar_estadocivil);
        editar_sexo = findViewById(R.id.editar_sexo);

        editar_telefone.addTextChangedListener(MaskManager.insertMaskTelefone(editar_telefone));

        ArrayAdapter<CharSequence> adapterEstadoCivil = ArrayAdapter.createFromResource(this,
                R.array.tipos_estado_civil, android.R.layout.simple_spinner_item);
        adapterEstadoCivil.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editar_estadocivil.setAdapter(adapterEstadoCivil);

        ArrayAdapter<CharSequence> adapterSexo = ArrayAdapter.createFromResource(this,
                R.array.tipos_sexo, android.R.layout.simple_spinner_item);
        adapterSexo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editar_sexo.setAdapter(adapterSexo);

        editar_nome.setText(Shared.getInstance().user.nome);
        editar_email.setText(Shared.getInstance().user.email);
        editar_telefone.setText(Shared.getInstance().user.telefone);
        if (!Shared.getInstance().user.estadoCivil.equals("")) {
            int spinnerPosition = adapterEstadoCivil.getPosition(Shared.getInstance().user.estadoCivil);
            editar_estadocivil.setSelection(spinnerPosition);
        }
        if (!Shared.getInstance().user.sexo.equals("")) {
            int spinnerPosition = adapterSexo.getPosition(Shared.getInstance().user.sexo);
            editar_sexo.setSelection(spinnerPosition);
        }

        if (Shared.getInstance().user.foto != null && Shared.getInstance().user.foto.equals("") == false) {
            Glide.with(this)
                    .load(Shared.getInstance().user.foto)
                    .into(editar_avatar);

        } else if (Shared.getInstance().imgProfileCarregado != null) {
            editar_avatar.setImageURI(null);
            editar_avatar.setImageURI(Shared.getInstance().imgProfileCarregado);
        } else {
            editar_avatar.setImageResource(R.drawable.ic_person_24dp);
        }

    }

    public void onClick(View view) {
        try {
            progress.setVisibility(View.VISIBLE);

            Shared.getInstance().user.nome = editar_nome.getText().toString();
            Shared.getInstance().user.email = editar_email.getText().toString();
            Shared.getInstance().user.telefone = editar_telefone.getText().toString();
            Shared.getInstance().user.estadoCivil = editar_estadocivil.getSelectedItem().toString();
            Shared.getInstance().user.sexo = editar_sexo.getSelectedItem().toString();

            //salvar no firebase
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Shared.getInstance().mainDataBaseReference.child("users").child(Shared.getInstance().mainFirebaseUser.getUid()).setValue(Shared.getInstance().user);

                        //parte da imagem
                        if (atualizouImagemPerfil) {
                            FirebaseManager.salvaFotoPerfilUsuario(EditActivity.this, Shared.getInstance().mainFirebaseUser);
                        } else {

                            EditActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress.setVisibility(View.INVISIBLE);
                                    Toast.makeText(EditActivity.this, "Salvo com sucesso!!!",
                                            Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(EditActivity.this, MainActivity.class);
                                    startActivity(intent);

                                    finish();
                                }
                            });

                        }
                    } catch (final Exception ex) {
                        EditActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(EditActivity.this, ex.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            }).start();
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onVoltar(View v) {
        Intent intent = new Intent(EditActivity.this, MainActivity.class);
        startActivity(intent);

        finish();
    }


    public void onPickImage(View view) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
            View dialog = getLayoutInflater().inflate(R.layout.mensagem_dialog, null);
            final TextView tituloMensagem = (TextView) dialog.findViewById(R.id.titulo_mensagem);
            tituloMensagem.setText("Selecionar Foto");
            final TextView mensagem = (TextView) dialog.findViewById(R.id.mensagem);
            mensagem.setText("Selecione uma foto de perfil da sua galeria ou use sua câmera");
            builder.setView(dialog);
            builder.setPositiveButton("Galeria", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(intent, 1);
                    } catch (Exception ex) {
                        Log.e("ERROR", ex.getMessage() );
                    }
                }
            });
            builder.setNeutralButton("Câmera", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        if (ContextCompat.checkSelfPermission(EditActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(EditActivity.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                        } else {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        }
                    } catch (Exception ex) {
                        Log.e("ERROR", ex.getMessage() );
                    }
                }
            });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                   dialog.dismiss();
                } catch (Exception ex) {
                    Log.e("ERROR", ex.getMessage() );
                }
            }
        });
            builder.show();

    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        final Uri uri = data.getData();
                        Shared.getInstance().imgProfileCarregado = uri;
                        editar_avatar.setImageURI(null);
                        editar_avatar.setImageURI(uri);

                        FirebaseManager.salvaFotoPerfilUsuario(EditActivity.this, Shared.getInstance().mainFirebaseUser);

                    }
                }
            } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                editar_avatar.setImageBitmap(photo);

                FirebaseManager.uploadBitmapImageToStorage(photo, Shared.getInstance().mainFirebaseUser);
            }

        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == MY_CAMERA_PERMISSION_CODE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}