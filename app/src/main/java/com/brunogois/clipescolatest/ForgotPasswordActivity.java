package com.brunogois.clipescolatest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.brunogois.clipescolatest.Model.Shared;
import com.brunogois.clipescolatest.Utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText campoEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        campoEmail = findViewById(R.id.esqueceusenha_mail);
    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.esqueceusenha_botao) { //botão enviar e-mail de recuperação de senha
            if (!validateForm()) {
                return;
            }
            sendEmailPasswordRecover();
        }
    }

    private void sendEmailPasswordRecover() {

        String emailAddress = campoEmail.getText().toString();
        Shared.getInstance().mainFirebaseAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showMessage();
                        } else {
                            String erroMsg = task.getException().getMessage();
                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                campoEmail.setError("Esse e-mail não está cadastrado");
                            } else {
                                System.out.println(task.getException());
                                System.out.println(erroMsg);

                                Toast.makeText(ForgotPasswordActivity.this,
                                        "Não foi possível enviar o e-mail. Tente novamente mais tarde",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = campoEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            campoEmail.setError("Esse campo é obrigatorio");
            valid = false;
        } else if (!Utils.isValidEmail(campoEmail.getText())) {
            campoEmail.setError("E-mail inválido");
            valid = false;
        } else {
            campoEmail.setError(null);
        }

        return valid;
    }

    private void showMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
        View dialog = getLayoutInflater().inflate(R.layout.mensagem_dialog, null);
        final TextView tituloMensagem = (TextView) dialog.findViewById(R.id.titulo_mensagem);
        tituloMensagem.setText("E-mail enviado!");
        final TextView mensagem = (TextView) dialog.findViewById(R.id.mensagem);
        mensagem.setText("Verifique sua caixa de correio de e-mail e siga as intruções para recuperar sua senha");
        builder.setView(dialog);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } catch (Exception ex) {

                }
            }
        });
        builder.show();
    }
}