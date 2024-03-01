package com.leestream.artgallery;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPswd extends AppCompatActivity {
    private TextInputEditText txtMail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pswd);
        txtMail=findViewById(R.id.txtMail);
        mAuth=FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();
        //todo load snackbasr and lottie fragment
        findViewById(R.id.ResetPwsd).setOnClickListener(view -> {
            if (NetworkUtils.isNetworkConnected(this)) {
                resetPwsd();
            }else {
                Toast.makeText(this, "No internet Connection", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void resetPwsd() {
        TextInputLayout inputLayout = (TextInputLayout) txtMail.getParent().getParent();
        String email=txtMail.getText().toString();
        if (email.isEmpty()){
            inputLayout.setError("Field cannot be empty");
        } else if (!ValidationUtils.isValidEmail(email)) {
            inputLayout.setError("Invalid email address");
        }else {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Password reset email sent successfully
                            Toast.makeText(this, "Password Resent Link sent to email", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // Password reset email failed to send
                            // Handle the failure (e.g., show an error message)
                            Toast.makeText(this, "failed please retry", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}