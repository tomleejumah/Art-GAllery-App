package com.leestream.artgallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.leestream.artgallery.Fragments.LottieDialogFragment;
import com.leestream.artgallery.databinding.ActivityMapsBinding;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback ,GoogleMap.OnMarkerClickListener{

    private LottieDialogFragment lottieDialogFragment;
    private Map<LatLng, String> locationDataSet;
    private Dialog dialog;
    private Button btnConfirm,btnCancel;
    private TextView txtLocation;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private String title,postID,publisher,postPublisher;
    private PendingIntent pendingIntent;
    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lottieDialogFragment = new LottieDialogFragment(this);
//        lottieDialogFragment.show();

        notificationManager = NotificationManagerCompat.from(this);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragmentToLoad", MainActivity.class.getSimpleName());
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);


        if (getIntent() != null && getIntent().getExtras() != null) {
            // Retrieve data from intent
            if (getIntent().hasExtra("postID")) {
                 postID = getIntent().getStringExtra("postID");
            }

            if (getIntent().hasExtra("publisher")) {
                 publisher = getIntent().getStringExtra("publisher");
            }

            if (getIntent().hasExtra("postPublisher")) {
                 postPublisher = getIntent().getStringExtra("postPublisher");
            }
        } else {
            Toast.makeText(this, "No extra data found!", Toast.LENGTH_SHORT).show();
        }

        dialog = new Dialog(MapsActivity.this);
        dialog.setContentView(R.layout.custom_dialog_location);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.location_dialo_bg));
        dialog.setCancelable(false);

        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnConfirm = dialog.findViewById(R.id.btnConfirm);
        txtLocation = dialog.findViewById(R.id.txtLocation);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lottieDialogFragment.show();
                dialog.dismiss();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                     AboutArt aboutArt = new AboutArt();
                     lottieDialogFragment.dismiss();
                     AppNotification();
                     aboutArt.addNotificationForSeller(postID, postPublisher, "Has bought your Art");
                     aboutArt.addNotificationForBuyer(postID, publisher, "You have successfully bought the art");
                     showSuccessDialog();
                    }
                },3000);
            }
        });

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
//        lottieDialogFragment.dismiss();
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        LatLng NairobiLocation = new LatLng(-1.2921, 36.8219);
        mMap.addMarker(new MarkerOptions().position(NairobiLocation)
                .title("Nairobi").snippet("Pick up station")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.warehouse)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NairobiLocation,5));

        LatLng MombasaLocation = new LatLng(-4.0435, 39.6682);
        mMap.addMarker(new MarkerOptions().position(MombasaLocation)
                .title("Mombasa").snippet("Pick up station")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.warehouse)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MombasaLocation,5));

        LatLng KisumuLocation = new LatLng(0.0917, 34.7680);
        mMap.addMarker(new MarkerOptions().position(KisumuLocation)
                .title("Kisumu").snippet("Pick up station")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.warehouse)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(KisumuLocation,5));

        LatLng EldoretLocation = new LatLng(0.5143, 35.2698);
        mMap.addMarker(new MarkerOptions().position(EldoretLocation)
                .title("Eldoret").snippet("Pick up station")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.warehouse)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(EldoretLocation,5));

        LatLng ManderaLocation = new LatLng(3.94, 41.86);
        mMap.addMarker(new MarkerOptions().position(ManderaLocation)
                .title("Mandera").snippet("Pick up station")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.warehouse)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ManderaLocation,5));

        LatLng NakuruLocation = new LatLng(-0.303099, 36.080025);
        mMap.addMarker(new MarkerOptions().position(NakuruLocation)
                .title("Nakuru").snippet("Pick up station")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.warehouse)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NakuruLocation,5));

        LatLng TurkanaLocation = new LatLng(3.31, 35.57);
        mMap.addMarker(new MarkerOptions().position(TurkanaLocation)
                .title("Turkana").snippet("Pick up station")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.warehouse)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(TurkanaLocation,5));

        LatLng KerichoLocation = new LatLng(-0.37, 35.29);
        mMap.addMarker(new MarkerOptions().position(KerichoLocation)
                .title("Kericho").snippet("Pick up station")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.warehouse)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(KerichoLocation,5));

        LatLng GarissaLocation = new LatLng(-0.45, 39.65);
        mMap.addMarker(new MarkerOptions().position(GarissaLocation)
                .title("Garissa").snippet("Pick up station")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.warehouse)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(GarissaLocation,5));

        LatLng MachakosLocation = new LatLng(1.3304, 37.4681);
        mMap.addMarker(new MarkerOptions().position(MachakosLocation)
                .title("Machakos ").snippet("Pick up station")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.warehouse)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MachakosLocation,5));

    }

    void AppNotification() {
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
    final private int REQUEST_CODE_ASK_PERMISSIONS = 333;

    private void showSuccessDialog() {
        ConstraintLayout successContraintLayout = findViewById(R.id.succesLayout);
        View view = LayoutInflater.from(MapsActivity.this).inflate(R.layout.succes_dialog, successContraintLayout);
        Button btnSucces = view.findViewById(R.id.successbtn);

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        btnSucces.findViewById(R.id.successbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(MapsActivity.this, "Done", Toast.LENGTH_SHORT).show();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }


    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
         title = marker.getTitle();
         new Handler().postDelayed(new Runnable() {
             @Override
             public void run() {
                 txtLocation.setText("Your Art Will be delivered at: "+title);
                 dialog.show();
             }
         },1500);
        return true;
    }
}