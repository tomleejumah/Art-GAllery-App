package com.leestream.artgallery.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leestream.artgallery.Fragments.ProfileFragment;
import com.leestream.artgallery.Models.Notification;
import com.leestream.artgallery.Models.Posts;
import com.leestream.artgallery.Models.User;
import com.leestream.artgallery.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
    private final List<Notification>mNotifications;
    private final Context mContext;

    public NotificationAdapter(List<Notification> mNotifications, Context mContext) {
        this.mNotifications = mNotifications;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.notification_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification=mNotifications.get(position);

        getUser(holder.imageProfile,holder.txtUserName,notification.getUserID());
        holder.txtComment.setText(notification.getText());

            holder.postImg.setVisibility(View.VISIBLE);
                getPostImg(holder.postImg,notification.getPostID());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
          public void onClick(View view) {
//                if (notification.isPost()){
//                    mContext.getSharedPreferences("PREF",Context.MODE_PRIVATE).edit().
//                            putString("postID", notification.getPostID()).apply();
//
////                    ((FragmentActivity)mContext).getSupportFragmentManager().
////                            beginTransaction().replace(R.id.fragment_Container,new PostDetailFragment()).commit();
//                }else {
//                    mContext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().
//                            putString("profileID",notification.getUserID()).apply();
//
//                    ((FragmentActivity)mContext).getSupportFragmentManager().
//                            beginTransaction().replace(R.id.fragment_Container,new ProfileFragment()).commit();
//                }
            }
        });
    }

    private void getPostImg(ImageView postImg, String postID) {
        FirebaseDatabase.getInstance().getReference().child("POSTS").child(postID).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Posts posts=snapshot.getValue(Posts.class);
                Picasso.get().load(posts.getImageUrl()).placeholder(R.mipmap.profile_foreground).into(postImg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUser(CircleImageView imageProfile, TextView txtUserName, String userID) {
        FirebaseDatabase.getInstance().getReference().child("USERS").child(userID).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if (user.getImageUrl().equals("default")){
                    imageProfile.setImageResource(R.mipmap.profile_foreground);
                }else {
                    Picasso.get().load(user.getImageUrl()).into(imageProfile);
                }
                txtUserName.setText(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (mNotifications!=null){
           return mNotifications.size();
        }else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final CircleImageView imageProfile;
        private final TextView txtUserName;
        private final TextView txtComment;
        private final ImageView postImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            postImg=itemView.findViewById(R.id.postImg);
            imageProfile=itemView.findViewById(R.id.imageProfile);
            txtUserName=itemView.findViewById(R.id.txtUserName);
            txtComment=itemView.findViewById(R.id.txtComment);
        }
    }
}
