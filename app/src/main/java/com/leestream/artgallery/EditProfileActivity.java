package com.leestream.artgallery;

import static android.widget.Toast.LENGTH_SHORT;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.leestream.artgallery.Fragments.LottieDialogFragment;
import com.leestream.artgallery.Models.User;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private CircleImageView editImg;
    private Bitmap bitmap;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 11;
    private static final int REQUEST_CODE = 10;
    private String firstName,lastName,userName,Bio;
    private FirebaseUser fUser;
    private String imageUrl;
    private TextInputEditText edtFN, edtLN, edtUserName, edtBio;
    private LottieDialogFragment lottieDialogFragment;
    private HashMap<String,Object> map=new HashMap<>();
    private StorageTask uploadTask;
    private String myUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        fUser= FirebaseAuth.getInstance().getCurrentUser();
        lottieDialogFragment = new LottieDialogFragment(this);

        Intent intent = getIntent();
        if (intent != null) {
             firstName = intent.getStringExtra("fName");
             lastName = intent.getStringExtra("lName");
             userName = intent.getStringExtra("uName");
             Bio = intent.getStringExtra("Bio");
        }

        edtFN = findViewById(R.id.edtFN);
        edtLN = findViewById(R.id.edtLN);
        edtUserName = findViewById(R.id.edtUserName);
        edtBio = findViewById(R.id.edtBio);
        editImg = findViewById(R.id.editprofileImageView);

        FirebaseDatabase.getInstance().getReference().child("USERS").child(fUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user =snapshot.getValue(User.class);
                        if (user.getImageUrl().equals("default")){
                            editImg.setImageResource(R.mipmap.profile_foreground);
                        }else if (user.getImageUrl().equals("")){
                            editImg.setImageResource(R.mipmap.profile_foreground);
                        }else {
                            Picasso.get().load(user.getImageUrl()).into(editImg);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        edtFN.setText(firstName);
        edtLN.setText(lastName);
        edtUserName.setText(userName);
        edtBio.setText(Bio);

        findViewById(R.id.save).setOnClickListener(v -> {
            updateProfile();
            if (imageUrl != null){
                upDateImage(imageUrl);
            }else {
                lottieDialogFragment.dismiss();
                finish();
            }
            lottieDialogFragment.show();
        } );

        findViewById(R.id.editprofileImageView).setOnClickListener(v -> showMyDialog());

        findViewById(R.id.changePhoto).setOnClickListener(v -> showMyDialog());

        findViewById(R.id.imgClose).setOnClickListener(v -> finish());
    }

    private void updateProfile() {
        String fName = edtFN.getText().toString();
        String lName = edtLN.getText().toString();
        String userName1 = edtUserName.getText().toString();
        String bio = edtBio.getText().toString();

        map.put("FirstName", fName);
        map.put("LastName", lName);
        map.put("UserName", userName1);
        map.put("Bio", bio);

        lottieDialogFragment.dismiss();

        FirebaseDatabase.getInstance().getReference().child("USERS").
                child(fUser.getUid()).updateChildren(map);
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

    private void upDateImage(String imageUrl) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        uploadTask = null;

        StorageReference imagesRef = storageReference.child("USERS").child(String.valueOf(System.currentTimeMillis()));
//
        if (imageUrl != null) {
            uploadTask = imagesRef.putFile(Uri.parse(imageUrl));
        } else {
                Toast.makeText(this, "No image uploaded", LENGTH_SHORT).show();
        }

        if (uploadTask != null) {

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imagesRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                Uri downloadUri = (Uri) task.getResult();
                String imageUrl1 = downloadUri.toString();
                FirebaseDatabase.getInstance().getReference().child("USERS").
                        child(fUser.getUid()).child("imageUrl").setValue(imageUrl1);
            }).addOnFailureListener(e -> {
                startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
                Log.e("Firebase", "Upload failed: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Failed to Update", Toast.LENGTH_LONG).show();
                lottieDialogFragment.dismiss();
                finish();
            }).addOnSuccessListener(taskSnapshot -> {
                // Task completed successfully
                lottieDialogFragment.dismiss();
                Log.d("Firebase", "Upload successful");
                finish();
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            imageUrl= String.valueOf(uri);
            editImg.setImageURI(uri);

        } else if (requestCode == REQUEST_CODE_ASK_PERMISSIONS && resultCode == RESULT_OK && data != null) {
            bitmap = (Bitmap) data.getExtras().get("data");

            String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                    bitmap, "Title", null);
            imageUrl = path;

            editImg.setImageBitmap(bitmap);
        }
    }
    void CheckUserPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) !=
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