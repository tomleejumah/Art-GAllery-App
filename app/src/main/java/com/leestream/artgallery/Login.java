package com.leestream.artgallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.leestream.artgallery.Fragments.LottieDialogFragment;

import java.util.Objects;

public class Login extends AppCompatActivity {
    private ImageView imageView;
    private TextView textView;
    private TextInputEditText edtMail, edtPwsd;
    private LottieDialogFragment lottieDialogFragment;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtMail=findViewById(R.id.edtMail);
        edtPwsd=findViewById(R.id.edtPassword);
        lottieDialogFragment= new LottieDialogFragment(this);
//        textView=findViewById(R.id.textView);
//        imageView=findViewById(R.id.imageView);

        mAuth=FirebaseAuth.getInstance();

//        String greeting = GreetingGeneratorUtils.getGreeting();
//        textView.setText(greeting);

//        int imageResource;
//        if (greeting.contains("Morning")) {
//            imageResource = R.drawable.good_morning_img;
//        } else if (greeting.contains("Afternoon")) {
//            imageResource = R.drawable.good_morning_img;
//        } else {
//            imageResource = R.drawable.good_night_img;
//        }
//        imageView.setImageResource(imageResource);

        findViewById(R.id.btnLogin).setOnClickListener(v->{
            if (NetworkUtils.isNetworkConnected(this)) {
                lottieDialogFragment.show();
                checkIfEmpty();
            }else {
                Toast.makeText(this, "No internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.tvRegister).setOnClickListener(v->{
            Intent intent=new Intent(Login.this,Register.class);
            startActivity(intent);
        });
        findViewById(R.id.btnFogorPwsd).setOnClickListener(v->{
            Intent intent=new Intent(Login.this,ForgotPswd.class);
            startActivity(intent);
        });
    }

    private void checkIfEmpty() {
        TextInputEditText[] editTexts = new TextInputEditText[]{edtMail, edtPwsd};
        boolean anyFieldEmpty = false;

        for (TextInputEditText editText : editTexts) {
            String text = editText.getText().toString().trim();
            TextInputLayout inputLayout = (TextInputLayout) editText.getParent().getParent();

            if (TextUtils.isEmpty(text)) {
                // Field is empty, set red highlight
                inputLayout.setError("Field cannot be empty");
                anyFieldEmpty = true;
            } else {
                // Field is not empty, clear error message
                inputLayout.setError(null);
//                anyFieldEmpty = false;
                String email = Objects.requireNonNull(edtMail.getText()).toString();
                if (!ValidationUtils.isValidEmail(email)) {
                    // Invalid email address format
                    edtMail.setError("Invalid email address");
                    anyFieldEmpty = true;
                }else{
                    edtMail.setError(null);
                    anyFieldEmpty=false;
                }
            }
        }

        if (anyFieldEmpty) {
            Toast.makeText(this, "correct the errors to login", Toast.LENGTH_SHORT).show();
        } else {
            // All fields are non-empty and valid, proceed with further actions
            if (NetworkUtils.isNetworkConnected(this)) {
                String email = edtMail.getText().toString();
                String password = edtPwsd.getText().toString();
                logInUser(email, password);
            }else {
                Toast.makeText(this, "Internet Unavailable!... ", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void logInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("login", "signInWithEmail:success");
//                        FirebaseUser user = mAuth.getCurrentUser();
                        // TODO load ads + lottie fragment
                        Toast.makeText(Login.this, "Welcome Back", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(Login.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.putExtra("UserLogin","UserLogin");
                        lottieDialogFragment.dismiss();
                        startActivity(intent);
                        finish();
                    } else {
                        Log.w("login", "signInWithEmail:failure", task.getException());

                        TextInputLayout emailInputLayout = (TextInputLayout) edtMail.getParent().getParent();
                        TextInputLayout passwordInputLayout = (TextInputLayout) edtPwsd.getParent().getParent();

                        emailInputLayout.setError("Invalid Login Credentials");
                        passwordInputLayout.setError("Invalid Login Credentials");

                    }
                }).addOnFailureListener(e -> Toast.makeText(Login.this, "Either password" +
                        " or email is invalid", Toast.LENGTH_LONG).show());
    }
}