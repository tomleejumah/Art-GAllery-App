package com.leestream.artgallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class Register extends AppCompatActivity {
    private TextInputLayout inputLayout;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;
    private RadioGroup radioGroup;
    private  String option;
    private TextInputEditText edtFirstName,edtLastname,edtEmail,edtPassword,edtPassword1,edtUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        mAuth= FirebaseAuth.getInstance();
        mRootRef= FirebaseDatabase.getInstance().getReference();

        edtFirstName=findViewById(R.id.edtFirstName);
        radioGroup = findViewById(R.id.cardContainer2);
        edtLastname=findViewById(R.id.edtLastname);
        edtEmail=findViewById(R.id.edtIdNo);
        edtPassword=findViewById(R.id.edtPassword);
        edtPassword1=findViewById(R.id.edtPassword1);
        edtUserName=findViewById(R.id.edtUserName1);
        Button btnRegister = findViewById(R.id.btnRegister);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                // A radio button is selected
                btnRegister.setVisibility(View.VISIBLE);
                RadioButton selectedRadioButton = findViewById(checkedId);
                 option = selectedRadioButton.getText().toString();
//                getSharedPreferences("User",MODE_PRIVATE).edit().putString("User",option).apply();
//                Toast.makeText(this, " " + option, Toast.LENGTH_SHORT).show();
            } else {
                // No radio button is selected
                btnRegister.setVisibility(View.GONE);
            }
        });



        findViewById(R.id.tvAlreadyUser1).setOnClickListener(view -> {
            Intent intent=new Intent(Register.this,Login.class);
            startActivity(intent);
        });
        findViewById(R.id.btnRegister).setOnClickListener(view -> {
            checkIfUtilsEmpty();
        });
    }

    private void checkIfUtilsEmpty() {
        TextInputEditText[] editTexts = {edtFirstName, edtLastname, edtEmail, edtPassword, edtPassword1,edtUserName};
        String email = Objects.requireNonNull(edtEmail.getText()).toString();
        String password = Objects.requireNonNull(edtPassword.getText()).toString();
//        String UserName = Objects.requireNonNull(edtUserName.getText()).toString();
        boolean anyFieldEmpty = false;
        // Iterate over the EditText fields
        for (TextInputEditText editText : editTexts) {
            String text = editText.getText().toString();
            inputLayout = (TextInputLayout) editText.getParent().getParent();

            if (TextUtils.isEmpty(text)) {
                // Field is empty, set red highlight
                inputLayout.setError("Field cannot be empty");
                anyFieldEmpty = true;
            } else {
                inputLayout.setError(null);
                String password1 = Objects.requireNonNull(edtPassword1.getText()).toString();

                if (!ValidationUtils.isValidEmail(email)){
                    edtEmail.setError("Invalid email address");
                    anyFieldEmpty = true;
                } else if (!ValidationUtils.isValidPassword(password)) {
                    edtPassword.setError("Password Cant Be less than 6 characters");
                    Toast.makeText(Register.this, "Password is weak \n " +
                            "should contain 6 or more characters", Toast.LENGTH_LONG).show();
                    anyFieldEmpty = true;
                } else if (!password.equals(password1)) {
                    edtPassword.setError("Password Does Not Match");
                    edtPassword1.setError("Password Does Not Match");
                    Toast.makeText(Register.this, "Password Does Not Match",
                            Toast.LENGTH_SHORT).show();
                    anyFieldEmpty = true;
                }else {
                    anyFieldEmpty = false;
                }

            }
        }
        if (anyFieldEmpty) {
            Toast.makeText(Register.this,
                    "correct the errors to continue", Toast.LENGTH_SHORT).show();
        } else {
            if (NetworkUtils.isNetworkConnected(this)) {
                registerUser(email,password);
            }else {
                Toast.makeText(this, "Internet Unavailable!... ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registerUser(String email, String password) {
        String fName= Objects.requireNonNull(edtFirstName.getText()).toString();
        String lName= Objects.requireNonNull(edtLastname.getText()).toString();
        String userName= Objects.requireNonNull(edtUserName.getText()).toString();
        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            String id = mAuth.getCurrentUser().getUid();
            HashMap<String,Object> map=new HashMap<>();
            map.put("FirstName",fName);
            map.put("Email",email);
            map.put("LastName",lName);
            map.put("UserName",userName);
            map.put("ID",id);
            map.put("imageUrl","default");
            map.put("Usertype",option);
            map.put("Bio","");

            MyAsyncTask myAsyncTask = new MyAsyncTask(id, userName,option);
            myAsyncTask.execute();

            mRootRef.child("USERS").child(mAuth.getCurrentUser().getUid()).setValue(map).
                    addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Toast.makeText(Register.this, "Welcome to Art Gallery " +
                                    "now set up your profile", Toast.LENGTH_LONG).show();
                            // Main Activity
                            Intent intent=new Intent(Register.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    });
        }).addOnFailureListener(e -> Toast.makeText(Register.this, "Error" +
                e.getMessage()+ "Retry", Toast.LENGTH_SHORT).show());

    }
    private class MyAsyncTask extends AsyncTask<String, Void, String> {
        private String id;
        private String userName;
        private String userType;
        public MyAsyncTask(String id, String userName,String userType) {
            this.id = id;
            this.userName = userName;
            this.userType = userType;
        }
        @Override
        protected String doInBackground(String... params) {
            // Store id and userName in SharedPreferences
            getSharedPreferences("UID", MODE_PRIVATE).edit().putString("myUID", id).apply();
            getSharedPreferences("UserName", MODE_PRIVATE).edit().putString("UserName", userName).apply();
            getSharedPreferences("UserType", MODE_PRIVATE).edit().putString("UserType", userType).apply();
            return "Task complete";
        }
    }
}