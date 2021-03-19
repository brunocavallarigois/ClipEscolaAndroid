package com.brunogois.clipescolatest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.brunogois.clipescolatest.Model.Shared;
import com.brunogois.clipescolatest.Model.User;
import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    TextView nome, email, telefone, estadoCivil, sexo;
    CircleImageView profile_avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        nome = findViewById(R.id.profile_nome_desc);
        email = findViewById(R.id.profile_email_desc);
        telefone = findViewById(R.id.profile_telefone_desc);
        estadoCivil = findViewById(R.id.profile_estadoCivil_desc);
        sexo = findViewById(R.id.profile_sexo_desc);

        profile_avatar = findViewById(R.id.profile_avatar);


        nome.setText(Shared.getInstance().user.nome);
        email.setText(Shared.getInstance().user.email);
        telefone.setText(Shared.getInstance().user.telefone);
        estadoCivil.setText(Shared.getInstance().user.estadoCivil);
        sexo.setText(Shared.getInstance().user.sexo);

        if (Shared.getInstance().user.foto != null && Shared.getInstance().user.foto.equals("") == false) {
            Glide.with(this)
                    .load(Shared.getInstance().user.foto)
                    .into(profile_avatar);

        } else if (Shared.getInstance().imgProfileCarregado != null) {
            profile_avatar.setImageURI(null);
            profile_avatar.setImageURI(Shared.getInstance().imgProfileCarregado);
        } else {
            profile_avatar.setImageResource(R.drawable.ic_person_24dp);
        }
    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.profile_btn_editardados) {
            Intent intent = new Intent(MainActivity.this, EditActivity.class);
            startActivity(intent);
            finish();
        } else if (i == R.id.sair){
            try {
                Shared.getInstance().mainFirebaseAuth.signOut();

                Shared.getInstance().user = new User();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            } catch (Exception ex) {
                Toast.makeText(this, ex.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Shared.getInstance().user.foto != null && Shared.getInstance().user.foto.equals("") == false) {
            Glide.with(this)
                    .load(Shared.getInstance().user.foto)
                    .into(profile_avatar);

        } else if (Shared.getInstance().imgProfileCarregado != null) {
            profile_avatar.setImageURI(null);
            profile_avatar.setImageURI(Shared.getInstance().imgProfileCarregado);
        } else {
            profile_avatar.setImageResource(R.drawable.ic_person_24dp);
        }
    }
}