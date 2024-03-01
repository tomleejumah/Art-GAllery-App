package com.leestream.artgallery.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leestream.artgallery.AboutArt;
import com.leestream.artgallery.Fragments.ProfileFragment;
import com.leestream.artgallery.Models.Cart;
import com.leestream.artgallery.Models.Posts;
import com.leestream.artgallery.Models.User;
import com.leestream.artgallery.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private final List<Cart> mCart;
    private final Context mContext;
    private int value;

    public CartAdapter(List<Cart> mNotifications, Context mContext) {
        this.mCart = mNotifications;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cart_item, parent, false);
        return new CartAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cart cart = mCart.get(position);
        getUser( holder.txtUserName, cart.getPublisherID());
        getPostImg(holder.cartImg, cart.getPostID());
        holder.txtTotal.setText(cart.getPrice());
        holder.txtDesc.setText(cart.getDescription());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, AboutArt.class);
            mContext.startActivity(intent);

            mContext.getSharedPreferences("PREF",Context.MODE_PRIVATE).edit().
                    putString("postID", cart.getPostID()).apply();
        });
        holder.decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseValue(view,holder.textView);
            }
        });
        holder.increaseButton.setOnClickListener(view -> increaseValue(view,holder.textView));

        // Initialize a map to store item prices based on their IDs
        Map<String, Integer> itemPrices = new HashMap<>();

//// Loop through each cart item to fetch the prices
//        for (Cart cartItem : mCart) {
//            String itemId = cartItem.getPostID();
//            String price = cartItem.getPrice();
//            if (itemId != null && !itemId.isEmpty() && price != null && !price.isEmpty()) {
//                int itemPrice = Integer.parseInt(price);
//                itemPrices.put(itemId, itemPrice);
//            }
//        }
//
//// Now you have a map containing item prices based on their IDs
//// You can use this map to set prices for each item in your RecyclerView
//        for (int i = 0; i < mCart.size(); i++) {
//            Cart cartItem = mCart.get(i);
//            String itemId = cartItem.getPostID();
//            int itemPrice = itemPrices.getOrDefault(itemId, 0);
//
//            // Set the price for the current item
//            holder.txtTotal.setText("Ksh: " + itemPrice);
//        }


    }

    private void getPostImg(ImageView postImg, String postID) {
        FirebaseDatabase.getInstance().getReference().child("POSTS").child(postID).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Posts posts = snapshot.getValue(Posts.class);
                        Picasso.get().load(posts.getImageUrl()).into(postImg);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getUser(TextView txtUserName, String userID) {
        FirebaseDatabase.getInstance().getReference().child("USERS").child(userID).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        txtUserName.setText(user.getUserName());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        if (mCart != null) {
            return mCart.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private  TextView txtUserName,txtDesc,txtTotal,textView;
        private  ImageView cartImg;
        private TextView increaseButton,decreaseButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cartImg = itemView.findViewById(R.id.txtCartImg);
            txtTotal = itemView.findViewById(R.id.txtTTCart);
            txtUserName = itemView.findViewById(R.id.txtUserNameCart);
            txtDesc = itemView.findViewById(R.id.txtDescCart);
            increaseButton = itemView.findViewById(R.id.btnIncrease);
            decreaseButton = itemView.findViewById(R.id.btnDecrease);
             textView = itemView.findViewById(R.id.txtValue);
        }

    }
    public void decreaseValue(View view,TextView textView) {
//        TextView textView = view.findViewById(R.id.txtValue);
         value = Integer.parseInt(textView.getText().toString());
        if (value > 0) {
            value--;
            textView.setText(String.valueOf(value));
        }
    }

    public void increaseValue(View view,TextView textView) {
//        TextView textView = view.findViewById(R.id.txtValue);
         value = Integer.parseInt(textView.getText().toString());
        value++;
        textView.setText(String.valueOf(value));
    }
}
