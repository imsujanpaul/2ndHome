package com.app.a2ndhome.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.a2ndhome.DashboardScreen;
import com.app.a2ndhome.ItemActivity;
import com.app.a2ndhome.R;
import com.app.a2ndhome.models.FeaturedItemModel;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sackcentury.shinebuttonlib.ShineButton;

import java.util.List;

public class FeaturedAdapter extends RecyclerView.Adapter<FeaturedAdapter.FeaturedViewHolder> {
    private Context ctx;
    private List<FeaturedItemModel> list;
    private DatabaseReference databaseReference;

    public FeaturedAdapter(Context ctx, List<FeaturedItemModel> list) {
        this.ctx = ctx;
        this.list = list;
    }

    @NonNull
    @Override
    public FeaturedAdapter.FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(ctx).inflate(R.layout.dashboard_items,parent,false);
        return new FeaturedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FeaturedAdapter.FeaturedViewHolder holder, int position) {
        final FeaturedItemModel backgrounds=list.get(position);
        databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        Glide.with(ctx).load(backgrounds.Image1).into(holder.imageView);
        holder.pgName.setText(backgrounds.Name);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(ctx, ItemActivity.class);
                ctx.startActivity(i);
            }
        });
        holder.shineButton.setOnCheckStateChangeListener(new ShineButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean checked) {
                if(checked){
                    Toast.makeText(ctx,"Succesfully added to wishlist!",Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(ctx,"Removed from wishlist!",Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class FeaturedViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CardView cardView;
        ShineButton shineButton;
        TextView pgName;
        TextView usernumber;
        public FeaturedViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
            cardView=itemView.findViewById(R.id.featureditemcard);
            shineButton=itemView.findViewById(R.id.addfav);
            pgName=itemView.findViewById(R.id.pgname);
            usernumber=itemView.findViewById(R.id.usernumber);
        }
    }
}
