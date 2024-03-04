package com.leestream.artgallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.squareup.picasso.Picasso;

import java.util.regex.Pattern;

public class AboutArt extends AppCompatActivity {
    private  int chosenValue;
    private int price;
    private String selectedSize;
    private String postID;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.myColor));
        }
        //todo work on price
        setContentView(R.layout.activity_about_art);

        postID=getSharedPreferences("PREF", Context.MODE_PRIVATE).getString("postID","none");

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
                            String publisher = snapshot.child("PublisherID").getValue(String.class);
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

        showMyDialog();
//todo use it on make order
    }
    private void showMyDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_order);

        TextView txtTTprice = dialog.findViewById(R.id.txtTTprice);
        RelativeLayout cardBuy = dialog.findViewById(R.id.cardBuy);
        EditText txtCodeBottomSheet = dialog.findViewById(R.id.txtCodeBottomSheet);
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
           String mail = txtCodeBottomSheet.getText().toString();

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
    public static boolean isValidEmail(String email) {
        // email validation logic
        return Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)" +
                "*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$").matcher(email).matches();
    }
}