package com.leestream.artgallery;

import static android.widget.Toast.LENGTH_SHORT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import com.leestream.artgallery.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostActivity extends AppCompatActivity {
    private String imageUrl;
    private String publisher;
    private Spinner spinner;
    private EditText txtDescription,txtSetPrice;
    private String selectedSpinnerItem;
    private LottieDialogFragment lottieDialogFragment;
    private String UserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        publisher = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserName = getSharedPreferences("UserName", MODE_PRIVATE).getString("UserName",null);

        lottieDialogFragment = new LottieDialogFragment(this);
        txtDescription = findViewById(R.id.txtDescription);
        txtSetPrice = findViewById(R.id.txtSetPrice);
        ImageView imgClose = findViewById(R.id.imgClose);
        ImageView imageView = findViewById(R.id.imgAdded);
        Button btnUpload = findViewById(R.id.btn_Login);

        spinner = findViewById(R.id.spinner1);

        List<String> list = new ArrayList<String>();
        list.add("Select Category");
        list.add("Music");
        list.add("Sculpture");
        list.add("Drawings");
        list.add("Literature");
        list.add("Film");
        list.add("Architecture");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        imgClose.setOnClickListener(v -> {
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 selectedSpinnerItem = parent.getSelectedItem().toString();
//                Toast.makeText(PostActivity.this, selectedItem, LENGTH_SHORT).show();
//                Toast.makeText(PostActivity.this, "YOUR SELECTION IS : " + selectedItem, LENGTH_SHORT).show();
//                Toast.makeText(PostActivity.this, "YOUR SELECTION IS : " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSpinnerItem = "Select Category";
            }
        });
        Uri imageUri;
        byte[] byteArray;

        txtDescription.requestFocus();
        if (getIntent().hasExtra("imageUri")) {
            byteArray = null;
            // Image URI intent
            imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));
            if (imageUri != null) {
                imageUrl = String.valueOf(imageUri);
                imageView.setImageURI(imageUri);
            }
        } else {
            imageUri = null;
            if (getIntent().hasExtra("bitmap")) {
                // Bitmap intent
                byteArray = getIntent().getByteArrayExtra("bitmap");
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    new Thread(() -> {
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
                        Uri.parse(path);
                        imageUrl = String.valueOf(path);
                    }).start();

                }
            } else {
                byteArray = null;
            }
        }
        btnUpload.setOnClickListener(v -> {
            lottieDialogFragment.show();
            upload(imageUri, byteArray);
        });

    }

    private void upload(Uri imageUri, byte[] byteArray) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        UploadTask uploadTask = null;

        StorageReference imagesRef = storageReference.child("POSTS").child(String.valueOf(System.currentTimeMillis()));
//
        if (imageUri != null) {
            uploadTask = imagesRef.putFile(imageUri);
        } else if (byteArray != null) {
            uploadTask = imagesRef.putBytes(byteArray);
        } else {
            Toast.makeText(this, "No image uploaded", LENGTH_SHORT).show();
        }

        if (uploadTask!=null) {

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return  imagesRef.getDownloadUrl();

            }).addOnCompleteListener(task -> {
                Uri downloadUri = task.getResult();
                imageUrl = downloadUri.toString();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("POSTS");
                String postID = ref.push().getKey();
                HashMap<String, Object> map = new HashMap<>();
                map.put("PublisherID", publisher);
                map.put("postID", postID);
                map.put("imageUrl", imageUrl);
                map.put("description", txtDescription.getText().toString());
                map.put("price", txtSetPrice.getText().toString());
                map.put("Category",selectedSpinnerItem);
//                map.put("UserName",UserName);

                ref.child(postID).setValue(map);
            }).addOnFailureListener(e -> {
                startActivity(new Intent(PostActivity.this, MainActivity.class));
                Log.e("Firebase", "Upload failed: " + e.getMessage());
                Toast.makeText(getApplicationContext(),"Failed to Upload",Toast.LENGTH_LONG).show();
                finish();
            }).addOnSuccessListener(taskSnapshot -> {
                // Task completed successfully
                lottieDialogFragment.dismiss();
                Log.d("Firebase", "Upload successful");
                Intent intent = new Intent(PostActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });

        }
    }

    private String getFileExtension(Object fileObject) {
        if (fileObject == null) {
            return "jpeg"; // Default to JPEG if the file object is null
        }
        if (fileObject instanceof Uri) {
            Uri uri = (Uri) fileObject;
            return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
        }
        if (fileObject instanceof byte[]) {
            return "jpeg";
        }
        throw new IllegalArgumentException("Unsupported file object type");
    }
}