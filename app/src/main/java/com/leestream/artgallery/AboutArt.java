package com.leestream.artgallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leestream.artgallery.Fragments.LottieDialogFragment;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.regex.Pattern;

public class AboutArt extends AppCompatActivity {
    private  int chosenValue;
    private int price;
    private String selectedSize,postPublisher;
    private String postID;
    private Context context;
    private LottieDialogFragment lottieDialogFragment;
    private PendingIntent pendingIntent;
    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_art);

        context = this;

        postID=getSharedPreferences("PREF", Context.MODE_PRIVATE).getString("postID","none");

        notificationManager = NotificationManagerCompat.from(this);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragmentToLoad", MainActivity.class.getSimpleName());
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        lottieDialogFragment = new LottieDialogFragment(this);
        findViewById(R.id.fabdone).setOnClickListener(v -> {
            startActivity(new Intent(AboutArt.this, MainActivity.class));
            finish();
        });

        RadioButton sizeSmall = findViewById(R.id.sizeSmall);
        sizeSmall.setText("S");

        RadioButton sizeMedium = findViewById(R.id.sizeMedium);
        sizeMedium.setText("M");

        RadioButton sizeLarge = findViewById(R.id.sizeLarge);
        sizeLarge.setText("L");

        RadioButton sizeXL = findViewById(R.id.sizeXL);
        sizeXL.setText("XL");

        // Set the default selection
        sizeSmall.setChecked(true);

        FirebaseDatabase.getInstance().getReference().child("POSTS").child(postID).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                             postPublisher = snapshot.child("PublisherID").getValue(String.class);
                            String description = snapshot.child("description").getValue(String.class);
                            String imageUrl = snapshot.child("imageUrl").getValue(String.class);
                            String priceString = snapshot.child("price").getValue(String.class);
                            if (priceString != null) {
                                price = Integer.valueOf(priceString);
                            } else {
                                price=0;
                            }

                            TextView txtDescription = findViewById(R.id.txtDescriptionq);
                            txtDescription.setText(description);

                            ImageView imgPostq=findViewById(R.id.imgPostq);
                            Picasso.get().load(imageUrl).into(imgPostq);

                            TextView txtPriceq = findViewById(R.id.txtPriceq);
                            txtPriceq.setText("Ksh: "+price);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public void onSizeSelected(android.view.View view) {
        RadioButton selectedRadioButton = (RadioButton) view;
         selectedSize = selectedRadioButton.getText().toString();
    }
    public void decreaseValue(View view) {
        TextView textView = findViewById(R.id.txtValue);
        int value = Integer.parseInt(textView.getText().toString());
        if (value > 0) {
            value--;
            textView.setText(String.valueOf(value));
        }
    }

    public void increaseValue(View view) {
        TextView textView = findViewById(R.id.txtValue);
        int value = Integer.parseInt(textView.getText().toString());
        value++;
        textView.setText(String.valueOf(value));
    }
    public void useChosenValue(View view) {
        TextView textView = findViewById(R.id.txtValue);
         chosenValue = Integer.parseInt(textView.getText().toString());

         if (chosenValue == 0){
             Toast.makeText(this, "Select at-least one Item", Toast.LENGTH_SHORT).show();
         }else {
             showMyDialog();
         }
    }
    private void showMyDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_order);

        TextView txtTTprice = dialog.findViewById(R.id.txtTTprice);
        RelativeLayout cardBuy = dialog.findViewById(R.id.cardBuy);
        EditText txtCodeBottomSheet = dialog.findViewById(R.id.txtCodeBottomSheet);
        EditText txtContactBottomSheet = dialog.findViewById(R.id.txtContactBottomSheet);
        EditText txtAdressBottomSheet = dialog.findViewById(R.id.txtAdressBottomSheet);

        Spinner spinnerRegions = dialog.findViewById(R.id.spinnerRegions);
        Spinner spinnerTowns = dialog.findViewById(R.id.spinnerTowns);

        ArrayAdapter<CharSequence> regionsAdapter = ArrayAdapter.createFromResource(this,
                R.array.kenyan_regions, android.R.layout.simple_spinner_item);
        regionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRegions.setAdapter(regionsAdapter);

        int ttPrice = chosenValue*price;

        txtTTprice.setText("Ksh: "+ttPrice);

        spinnerRegions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get selected region
                String selectedRegion = (String) parent.getItemAtPosition(position);

                // Update towns spinner based on selected region
                ArrayAdapter<CharSequence> townsAdapter;
                switch (selectedRegion) {
                    case "Nairobi":
                        townsAdapter = ArrayAdapter.createFromResource(AboutArt.this,
                                R.array.nairobi_towns, android.R.layout.simple_spinner_item);
                        break;
                    case "Central":
                        townsAdapter = ArrayAdapter.createFromResource(AboutArt.this,
                                R.array.central_towns, android.R.layout.simple_spinner_item);
                        break;
                    case "Coast":
                        townsAdapter = ArrayAdapter.createFromResource(AboutArt.this,
                                R.array.coast_towns, android.R.layout.simple_spinner_item);
                        break;
                    case "Eastern":
                        townsAdapter = ArrayAdapter.createFromResource(AboutArt.this,
                                R.array.Eastern_towns, android.R.layout.simple_spinner_item);
                        break;
                    case "Western":
                        townsAdapter = ArrayAdapter.createFromResource(AboutArt.this,
                                R.array.Western_towns, android.R.layout.simple_spinner_item);
                        break;
                    case "Nyanza":
                        townsAdapter = ArrayAdapter.createFromResource(AboutArt.this,
                                R.array.Nyanza_towns, android.R.layout.simple_spinner_item);
                        break;
                    case "Great Rift":
                        townsAdapter = ArrayAdapter.createFromResource(AboutArt.this,
                                R.array.Rift_towns, android.R.layout.simple_spinner_item);
                        break;
                    case "North":
                        townsAdapter = ArrayAdapter.createFromResource(AboutArt.this,
                                R.array.North_towns, android.R.layout.simple_spinner_item);
                        break;
                    // Add cases for other regions if needed
                    default:
                        townsAdapter = new ArrayAdapter<>(AboutArt.this, android.R.layout.simple_spinner_item);
                }
                townsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTowns.setAdapter(townsAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        cardBuy.setOnClickListener(v -> {
            String  publisher = FirebaseAuth.getInstance().getCurrentUser().getUid();
           String code = txtCodeBottomSheet.getText().toString();
            String address = txtAdressBottomSheet.getText().toString();
            String contact = txtContactBottomSheet.getText().toString();


            if (TextUtils.isEmpty(code) || TextUtils.isEmpty(address) || TextUtils.isEmpty(contact)) {
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(context, "Please enter code", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(context, "Please enter address", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(contact)) {
                    Toast.makeText(context, "Please enter contact", Toast.LENGTH_SHORT).show();
                }
            } else {
                lottieDialogFragment.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lottieDialogFragment.dismiss();
                        showSuccessDialog();
                        addNotificationForSeller(postID,postPublisher,"Has bought your Art");
                        addNotificationForBuyer(postID,publisher,"You have successfully bought the art");
                        AppLaunchNotification();
                        dialog.dismiss();
                    }
                },3000);

            }

//           if (!isValidEmail(mail)){
//               Toast.makeText(AboutArt.this, "Enter A valid Mail", Toast.LENGTH_SHORT).show();
//           }else {
//
//               FirebaseDatabase.getInstance().getReference().child("USERS").child(publisher).
//                       addValueEventListener(new ValueEventListener() {
//                           @Override
//                           public void onDataChange(@NonNull DataSnapshot snapshot) {
//                               if (snapshot.exists()) {
//                                   String Email = snapshot.child("Email").getValue(String.class);
//                                   if (mail.equalsIgnoreCase(Email)){
//                        //todo  fragment for processing then successful
//                                       dialog.dismiss();
//                                   }else {
//                                       Toast.makeText(AboutArt.this, "Use your Registered Mail", Toast.LENGTH_SHORT).show();
//                                   }
//                               }
//
//                           }
//
//                           @Override
//                           public void onCancelled(@NonNull DatabaseError error) {
//
//                           }
//                       });
//           }

        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setDimAmount(0.5f);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    final private int REQUEST_CODE_ASK_PERMISSIONS = 333;
    void AppLaunchNotification() {
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.notif_large);
        String message = "You have Successfully Purchased the art.It will be delivered soon";
//        String name = getSharedPreferences("MyPref", Context.MODE_PRIVATE).getString("FirstName", null);
        Notification notification = new NotificationCompat.Builder(this
                , "channel1")
                .setSmallIcon(R.drawable.no_notif)
                .setLargeIcon(icon)
                .setContentTitle("Purchase Complete!")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setTimeoutAfter(18000000)
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_ASK_PERMISSIONS);
            } else {
                notificationManager.notify(1, notification);
            }
        } else {
            notificationManager.notify(1, notification);
        }
    }

    private void addNotificationForBuyer(String postID, String publisher,String text) {
        HashMap<String,Object> map=new HashMap<>();
        map.put("userID",publisher);
        map.put("text",text);
        map.put("postID",postID);

        FirebaseDatabase.getInstance().getReference().child("Notification").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().setValue(map);

    }
    private void addNotificationForSeller(String postID, String publisher,String text){
        HashMap<String,Object> map=new HashMap<>();
        map.put("userID",publisher);
        map.put("text",text);
        map.put("postID",postID);

        FirebaseDatabase.getInstance().getReference().child("Notification").
                child(publisher).push().setValue(map);
    }

    private void showSuccessDialog() {
        ConstraintLayout successContraintLayout = findViewById(R.id.succesLayout);
        View view = LayoutInflater.from(AboutArt.this).inflate(R.layout.succes_dialog,successContraintLayout);
        Button btnSucces = view.findViewById(R.id.successbtn);

        AlertDialog.Builder builder = new AlertDialog.Builder(AboutArt.this);
        builder.setView(view);
        final  AlertDialog alertDialog = builder.create();

        btnSucces.findViewById(R.id.successbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                Toast.makeText(AboutArt.this, "Done", Toast.LENGTH_SHORT).show();
            }
        });
        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }

    public static boolean isValidEmail(String email) {
        // email validation logic
        return Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)" +
                "*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").matcher(email).matches();
    }
}