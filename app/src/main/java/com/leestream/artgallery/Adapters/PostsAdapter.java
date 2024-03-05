package com.leestream.artgallery.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.leestream.artgallery.AboutArt;
import com.leestream.artgallery.FollowersActivity;
import com.leestream.artgallery.Fragments.ProfileFragment;
import com.leestream.artgallery.Models.Posts;
import com.leestream.artgallery.Models.User;
import com.leestream.artgallery.R;
import com.leestream.artgallery.UserPostActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>{
    private final Context mContext;
    private final List<Posts> mPosts;

    private static final String TAG = "PostsAdapter";
    private final FirebaseUser firebaseUser;


    public PostsAdapter(Context mContext, List<Posts> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.art_item,parent,false);
        return new PostsAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Posts posts= mPosts.get(position);
        User user = new User();
        Picasso.get().load(posts.getImageUrl()).into(holder.postImage, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Log.e("Picasso", "Error loading image: " + e.getMessage());
            }
        });

        holder.tvtDescription.setText(posts.getDescription());
//        holder.txtUsername.setText(posts.getUserName());

//        String  publisher = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        posts.getPublisher()
//        child(publisher)
        FirebaseDatabase.getInstance().getReference().child("USERS").child(posts.getPublisherID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);

                        if (user.getImageUrl().equals("default")){
                            holder.profileImg.setImageResource(R.mipmap.profile_foreground);
                        }else if (user.getImageUrl().equals("")){
                            holder.profileImg.setImageResource(R.mipmap.profile_foreground);
                        }else {
                            Picasso.get().load(user.getImageUrl()).into(holder.profileImg);
                        }
                        holder.txtUsername.setText(user.getUserName());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        isLiked(posts.getPostID(), holder.likeBtn);
        likesNumbers(posts.getPostID(), holder.txtLikes);
        isSaved(posts.getPostID(),holder.savesBtn);
        isAddedToCart(posts.getPostID(),holder.ImgOrder);

        holder.ImgOrder.setOnClickListener(v -> {
            if (holder.ImgOrder.getTag().equals("NoCart")){
                FirebaseDatabase.getInstance().getReference().child("Cart").
                        child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(posts.getPostID()).setValue(true);

                holder.ImgOrder.setTag("Cart");

                addToCartFragment(posts.getPostID(), posts.getUserName(), posts.getDescription(),
                        posts.getPrice(), posts.getImageUrl(), posts.getPublisherID());

                Toast.makeText(mContext, "Added to Cart", Toast.LENGTH_SHORT).show();
            }else {
                FirebaseDatabase.getInstance().getReference().child("Cart").
                        child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();

                removeFromCart(posts.getPostID(), firebaseUser.getUid());

                holder.ImgOrder.setTag("NoCart");

            }
        });
        holder.likeBtn.setOnClickListener(view -> {
            // checking if image has been liked then adding like if not
            if (holder.likeBtn.getTag().equals("Like")){
                FirebaseDatabase.getInstance().getReference().child("Likes").
                        child(posts.getPostID()).child(firebaseUser.getUid()).setValue(true);

                addNotification(posts.getPostID(),firebaseUser.getUid(),"Liked your Post", posts.getPublisherID());

                saveLikedPost(posts.getPostID(), posts.getUserName(), posts.getDescription(),
                        posts.getPrice(), posts.getImageUrl(), posts.getPublisherID());
            }else {
                FirebaseDatabase.getInstance().getReference().child("Likes").
                        child(posts.getPostID()).child(firebaseUser.getUid()).removeValue();
                removeLiked(posts.getPostID(), firebaseUser.getUid());
            }
        });
        holder.savesBtn.setOnClickListener(view -> {
            if (holder.savesBtn.getTag().equals("Save")){
                FirebaseDatabase.getInstance().getReference().child("Saves").
                        child(firebaseUser.getUid()).child(posts.getPostID()).setValue(true);

                addNotification(posts.getPostID(),posts.getPublisherID(),"Saved your Post",posts.getPublisherID());
                savePost(posts.getPostID(), posts.getUserName(), posts.getDescription(),
                        posts.getPrice(), posts.getImageUrl(), posts.getPublisherID());

                Toast.makeText(mContext, "Post is Saved", Toast.LENGTH_SHORT).show();
            }else {
                removeSave(posts.getPostID(), firebaseUser.getUid());

            }
        });
        holder.profileImg.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, UserPostActivity.class);
            mContext.getSharedPreferences("USER",Context.MODE_PRIVATE).edit().
                    putString("userID", posts.getPublisherID()).apply();
            mContext.startActivity(intent);
//            mContext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().
//                    putString("profileID", posts.getPublisherID()).apply();
//            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().
//                    replace(R.id.fragment_Container,new ProfileFragment()).commit();
        });
        holder.txtUsername.setOnClickListener(view -> {
//            mContext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().
//                    putString("profileID", posts.getPublisherID()).apply();
//            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().
//                    replace(R.id.fragment_Container,new ProfileFragment()).commit();
        });
        holder.postImage.setOnClickListener(view -> {
            mContext.getSharedPreferences("PREF",Context.MODE_PRIVATE).edit().
                    putString("postID", posts.getPostID()).apply();
            openNextActivity(position, AboutArt.class);
        });
        holder.txtLikes.setOnClickListener(view -> {
            Intent intent=new Intent(mContext, FollowersActivity.class);
            intent.putExtra("ID",firebaseUser.getUid());
            intent.putExtra("ID",posts.getPostID());
            intent.putExtra("Likes","Likes");
            mContext.startActivity(intent);
        });

    }

    private void saveLikedPost(String postID, String UserName, String desc
            , String price, String imageUrl, String PublisherID) {
        HashMap<String,Object> map=new HashMap<>();
        map.put("UserName",UserName);
        map.put("text",desc);
        map.put("postID",postID);
        map.put("price",price);
        map.put("imageUrl",imageUrl);
        map.put("PublisherID",PublisherID);

        FirebaseDatabase.getInstance().getReference().child("LIKED").
                child(firebaseUser.getUid()).push().setValue(map);
    }

    private void savePost(String postID, String UserName, String desc
            , String price, String imageUrl, String PublisherID) {
        HashMap<String,Object> map=new HashMap<>();
        map.put("UserName",UserName);
        map.put("postID",postID);
        map.put("price",price);
        map.put("imageUrl",imageUrl);
        map.put("PublisherID",PublisherID);

        FirebaseDatabase.getInstance().getReference().child("SAVED").
                child(firebaseUser.getUid()).push().setValue(map);
    }

    private void addToCartFragment(String postID, String UserName,String desc
            ,String price,String imageUrl,String PublisherID) {
        HashMap<String,Object> map=new HashMap<>();
        map.put("UserName",UserName);
        map.put("text",desc);
        map.put("postID",postID);
        map.put("price",price);
        map.put("imageUrl",imageUrl);
        map.put("PublisherID",PublisherID);

        FirebaseDatabase.getInstance().getReference().child("CartItems").
                child(firebaseUser.getUid()).push().setValue(map);
    }
    private void removeFromCart(String postId,String PublisherID){
        FirebaseDatabase.getInstance().getReference().child("CartItems").
                child(PublisherID).child(postId).removeValue();

        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("CartItems").child(firebaseUser.getUid());

        Query query = cartRef.orderByChild("postID").equalTo(postId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    // Remove the item from the cart
                    itemSnapshot.getRef().removeValue();
                }
                Toast.makeText(mContext, "Removed From Cart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "onCancelled", databaseError.toException());
            }
        });
    }
    private void removeSave(String postId,String PublisherID) {
        FirebaseDatabase.getInstance().getReference().child("Saves").
                child(PublisherID).child(postId).removeValue();

        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("SAVED").child(firebaseUser.getUid());

        Query query = cartRef.orderByChild("postID").equalTo(postId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    // Remove the item from the cart
                    itemSnapshot.getRef().removeValue();
                }
                Toast.makeText(mContext, "Removed From Cart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "onCancelled", databaseError.toException());
            }
        });
    }
    private void removeLiked(String postId,String PublisherID){
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("LIKED").child(firebaseUser.getUid());

        Query query = cartRef.orderByChild("postID").equalTo(postId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    // Remove the item from the cart
                    itemSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "onCancelled", databaseError.toException());
            }
        });
    }

    void openNextActivity(int position,Class<?> targetClass){
        Intent intent=new Intent(mContext, targetClass);
//        String LongDes= String.valueOf(mJobItems.get(position).getLongDes());
//        String pay= String.valueOf(mJobItems.get(position).getSalary());
//        String ShortDes= String.valueOf(mJobItems.get(position).getShortDes());
//        String tittle= String.valueOf(mJobItems.get(position).getJobTittle());
//
//        intent.putExtra("LongDes",LongDes);
//        intent.putExtra("pay",pay);
//        intent.putExtra("ShortDes",ShortDes);
//        intent.putExtra("tittle",tittle);

        mContext.startActivity(intent);
    }

    private void addNotification(String postID, String publisher,String text,String postPublisher) {
        HashMap<String,Object> map=new HashMap<>();
        map.put("userID",publisher);
        map.put("text",text);
        map.put("postID",postID);

        if (!postPublisher.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            FirebaseDatabase.getInstance().getReference().child("Notification").
                    child(postPublisher).push().setValue(map);
        }

    }

    @Override
    public int getItemCount() {
        if (mPosts!=null){
            return mPosts.size();
        }else {
            return 0;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView profileImg;
        public ImageView ImgOrder,postImage,likeBtn,savesBtn;
        public TextView txtUsername,tvtDescription,txtLikes;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //user Model
            profileImg = itemView.findViewById(R.id.profileItemImg);
            txtUsername = itemView.findViewById(R.id.userName);
            //profile model
            ImgOrder = itemView.findViewById(R.id.ImgOrder);
            postImage = itemView.findViewById(R.id.imgPost);
            likeBtn = itemView.findViewById(R.id.imgLike);
            savesBtn = itemView.findViewById(R.id.imgSave);
            tvtDescription = itemView.findViewById(R.id.txtDescription);
            txtLikes = itemView.findViewById(R.id.txtLikes);
        }
    }

    private void isSaved(String postID, ImageView savesBtn) {
        FirebaseDatabase.getInstance().getReference().child("Saves").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(postID).exists()){
                            savesBtn.setImageResource(R.drawable.ic_saved_foreground);
//                            Toast.makeText(mContext, "Photo Saved", Toast.LENGTH_SHORT).show();
                            savesBtn.setTag("Saved");

                        }else {
                            savesBtn.setImageResource(R.drawable.ic_save_foreground);
                            savesBtn.setTag("Save");
//                            Toast.makeText(mContext, "Art not saved retry", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void isLiked(String postId,ImageView imageView){
        FirebaseDatabase.getInstance().getReference().child("Likes").
                child(postId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child(firebaseUser.getUid()).exists()){
                            imageView.setImageResource(R.drawable.ic_liked_foreground);
                            imageView.setTag("Liked");
                        }else {
                            imageView.setImageResource(R.drawable.ic_like_foreground);
                            imageView.setTag("Like");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
    private void likesNumbers(String postId,TextView textView) {
        FirebaseDatabase.getInstance().getReference().child("Likes").
                child(postId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        textView.setText((int) snapshot.getChildrenCount());
                        textView.setText(String.valueOf(snapshot.getChildrenCount()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void isAddedToCart(String postID, ImageView ImgOrder) {
        FirebaseDatabase.getInstance().getReference().child("Cart").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(postID).exists()){
                            ImgOrder.setImageResource(R.mipmap.shoped_foreground);
//                            Toast.makeText(mContext, "Added to Cart", Toast.LENGTH_SHORT).show();
                            ImgOrder.setTag("Cart");

                        }else {
                            ImgOrder.setImageResource(R.mipmap.orders_foreground);
                            ImgOrder.setTag("NoCart");
//                            Toast.makeText(mContext, "Removed from Cart", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
