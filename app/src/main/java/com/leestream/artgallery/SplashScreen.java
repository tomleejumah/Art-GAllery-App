package com.leestream.artgallery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {
private Button btn_register,btn_Login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(getResources().getColor(R.color.myColor));
        setContentView(R.layout.activity_splash_screen);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);

        btn_register = findViewById(R.id.btn_register);
        btn_Login = findViewById(R.id.btn_Login);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            btn_register.setVisibility(View.GONE);
            btn_Login.setVisibility(View.GONE);

            new Handler().postDelayed(() -> {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
            },3000);

        }

        btn_register.setOnClickListener(v -> {
            startActivity(new Intent(SplashScreen.this,Register.class));

        });
      btn_Login.setOnClickListener(v -> {
            startActivity(new Intent(SplashScreen.this,Login.class));

        });

    }
    public void setStatusBarColor(final int color) {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(color);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(color);
        }
    }

}