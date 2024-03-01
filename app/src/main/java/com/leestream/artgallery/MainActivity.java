package com.leestream.artgallery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leestream.artgallery.Fragments.CartHostFragment;
import com.leestream.artgallery.Fragments.HomeFragment;
import com.leestream.artgallery.Fragments.ProfileFragment;
import com.leestream.artgallery.Fragments.SearchFragment;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 11;
    private static final int REQUEST_CODE = 10;
    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        FloatingActionButton myFab=findViewById(R.id.myFab);
        myFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userType = getSharedPreferences("UserType", MODE_PRIVATE).getString("UserType",null);
//                assert userType != null;
                if (userType != null){
                    if (userType.equals("Client")){
                        Toast.makeText(MainActivity.this, "To Post,you must be logged in as an Artist", Toast.LENGTH_LONG).show();
                    }else {
                        showMyDialog();
                    }
                }else {
                    Toast.makeText(MainActivity.this, "To Post,you must be logged in as an Artist", Toast.LENGTH_LONG).show();

                }

            }
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                fragment = new HomeFragment();
            } else if (itemId == R.id.Search) {
                fragment = new SearchFragment();
            } else if (itemId == R.id.cart) {
                fragment = new CartHostFragment();
            } else if (itemId == R.id.profile) {
                fragment = new ProfileFragment();
            }
            if (fragment != null){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_Container,fragment).commit();
            }
            return true;
        });
        Bundle intent =getIntent().getExtras();
        if (intent!=null){
            String profileID= intent.getString("publisherID");
            getSharedPreferences("PROFILE",MODE_PRIVATE).edit().putString("profileID",profileID).apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_Container,new ProfileFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.profile);
        }else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_Container,new HomeFragment()).commit();
        }
    }

    private void showMyDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet);

        LinearLayout layoutCamera = dialog.findViewById(R.id.layoutCamera);
        LinearLayout layoutGallery = dialog.findViewById(R.id.layoutGallery);

        layoutCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                CheckUserPermissions();

            }
        });
        layoutGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //go and pick image
                Intent intent= new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.5f);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
              Uri uri = data.getData();

                Intent intent = new Intent(this, PostActivity.class);
                intent.putExtra("imageUri", uri.toString());
                startActivity(intent);

        } else if (requestCode == REQUEST_CODE_ASK_PERMISSIONS && resultCode == RESULT_OK && data != null) {
                bitmap = (Bitmap) data.getExtras().get("data");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Intent intent = new Intent(this, PostActivity.class);
            intent.putExtra("bitmap", byteArray);
            startActivity(intent);

        }
    }
    void CheckUserPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                            Manifest.permission.CAMERA},
                    REQUEST_CODE_ASK_PERMISSIONS);
        } else {
           //Goto Camera
            Intent intent= new Intent((MediaStore.ACTION_IMAGE_CAPTURE));
            startActivityForResult(intent, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length>0){
            if (grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                this.CheckUserPermissions();
            }
        }
    }
}