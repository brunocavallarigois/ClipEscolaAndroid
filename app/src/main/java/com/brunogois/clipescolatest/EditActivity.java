package com.brunogois.clipescolatest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.brunogois.clipescolatest.Data.FirebaseManager;
import com.brunogois.clipescolatest.Model.ImagePicker;
import com.brunogois.clipescolatest.Model.Shared;
import com.brunogois.clipescolatest.Utils.MaskManager;


import de.hdodenhof.circleimageview.CircleImageView;

public class EditActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_ID = 234;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    CircleImageView editar_avatar;
    EditText editar_nome, editar_email, editar_telefone;
    Spinner editar_estadocivil, editar_sexo;
    ProgressBar progress;

    Boolean atualizouImagemPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        progress = findViewById(R.id.progress);
        editar_avatar = findViewById(R.id.editar_avatar);

        editar_nome = findViewById(R.id.editar_nome);
        editar_email = findViewById(R.id.editar_email);
        editar_telefone = findViewById(R.id.editar_nome);
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
                            progress.setVisibility(View.INVISIBLE);
                            Toast.makeText(EditActivity.this, "Salvo com sucesso!!!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        Toast.makeText(EditActivity.this, ex.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }).start();
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onVoltar(View v) {
        this.onBackPressed();
    }


    public void onPickImage(View view) {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case PICK_IMAGE_ID:
                Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                Uri uri = ImagePicker.getImageUriFromResult(this, resultCode, data);
                Shared.getInstance().imgProfileCarregado = uri;
                editar_avatar.setImageURI(uri);
                atualizouImagemPerfil = true;
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
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
                } else {

                }
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}