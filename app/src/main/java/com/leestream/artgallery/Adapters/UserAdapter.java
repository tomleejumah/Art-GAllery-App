package com.leestream.artgallery.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leestream.artgallery.Fragments.ProfileFragment;
import com.leestream.artgallery.MainActivity;
import com.leestream.artgallery.Models.User;
import com.leestream.artgallery.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private  Context context;
    private  List<User> mUsers;
    private  Boolean isFragment;
    private FirebaseUser firebaseUser;
    private UserNameListener firstNameListener;

    public UserAdapter(Context context, List<User> mUsers, Boolean isFragment) {
        this.context = context;
        this.mUsers = mUsers;
        this.isFragment = isFragment;

    }

    public UserAdapter(UserNameListener firstNameListener) {
        this.firstNameListener = firstNameListener;
    }

    public interface UserNameListener {
        void onUserNameReceived(String firstName);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        User user=mUsers.get(position);
        holder.btnFollow.setVisibility(View.VISIBLE);
        holder.edtFirstNameItem.setText(user.getFirstName());
        holder.edtLastNameItem.setText(user.getLastName());


        String  publisher = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("USERS").child(publisher)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);

//                        if (user.getImageUrl().equals("default")){
//                            holder.profileImg.setImageResource(R.mipmap.profile_foreground);
//                        }else if (user.getImageUrl().equals("")){
//                            holder.profileImg.setImageResource(R.mipmap.profile_foreground);
//                        }else {
//                            Picasso.get().load(user.getImageUrl()).into(holder.profileImg);
//                        }

                        if (firstNameListener != null) {
                            firstNameListener.onUserNameReceived(user.getUserName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        Picasso.get().load(user.getImageUrl()).placeholder(R.mipmap.profile_foreground)
                .into(holder.profilePIc);
        isFollowed(user.getID(), holder.btnFollow);


        if (user.getID().equals(firebaseUser.getUid())){
            FirebaseDatabase.getInstance().getReference().child("follow")
                    .child(firebaseUser.getUid())  // Get the current user's UID
                    .child("following")
                    .child(firebaseUser.getUid())
                    .setValue(true);
            holder.btnFollow.setVisibility(View.GONE);
        }
        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.btnFollow.getText().equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("follow").
                            child(firebaseUser.getUid()).child("following").
                            child(user.getID()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("follow").child(user.getID())
                            .child("followers").child(firebaseUser.getUid())
                            .setValue(true);
                    addNotif(user.getID());

                }else {
                    FirebaseDatabase.getInstance().getReference().child("follow").
                            child(firebaseUser.getUid()).child("following").
                            child(user.getID()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("follow").child(user.getID())
                            .child("followers").child(firebaseUser.getUid())
                            .removeValue();

                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFragment){
                    context.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit()
                            .putString("profileID", user.getID()).apply();

                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().
                            replace(R.id.fragment_Container,new ProfileFragment()).commit();
                }else {
                    Intent intent=new Intent(context, MainActivity.class);
                    intent.putExtra("publisherID",user.getID());
                    context.startActivity(intent);
                }
            }
        });
    }

    private void addNotif(String id) {
        HashMap<String,Object> map=new HashMap<>();
        map.put("userID",id);
        map.put("text"," Started following you ");
        map.put("postID","");
        map.put("isPost",false);

        FirebaseDatabase.getInstance().getReference().child("Notification").
                child(firebaseUser.getUid()).push().setValue(map);
    }

    private void isFollowed(String id, Button btnFollow) {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference()
                .child("follow").child(firebaseUser.getUid()).child("following");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(id).exists()){
                    btnFollow.setText("Following");
                }else {
                    btnFollow.setText("Follow");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
    public CircleImageView profilePIc;
    public TextView edtFirstNameItem,edtLastNameItem;
    public Button btnFollow;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePIc=itemView.findViewById(R.id.profilePIc);
            edtFirstNameItem=itemView.findViewById(R.id.edtFirstNameItem);
            edtLastNameItem=itemView.findViewById(R.id.edtLastNameItem);
            btnFollow=itemView.findViewById(R.id.btnFollow);
        }
    }
}
