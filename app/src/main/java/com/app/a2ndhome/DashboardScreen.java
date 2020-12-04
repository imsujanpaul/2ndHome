package com.app.a2ndhome;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.app.a2ndhome.adapters.FeaturedAdapter;
import com.app.a2ndhome.models.FeaturedItemModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DashboardScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
TextView appname;
EditText searchText;
SearchView searchView;
ImageView menuIcon;
    private DatabaseReference mdbCategories;
    private List<FeaturedItemModel> imageList;
    private FeaturedAdapter adapter;;
    ProgressBar progressBar;
    //Variables
    static final float END_SCALE = 0.7f;
    ConstraintLayout contentView;
    //Drawer Menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_screen);
        Bundle extras = getIntent().getExtras();
        String phoneNumber=extras.getString("number");
        appname = findViewById(R.id.app_name);
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.navigation_view);
        progressBar=findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);
        menuIcon=findViewById(R.id.menuicon);
        View header = LayoutInflater.from(this).inflate(R.layout.activity_nav_header_main, null);
        navigationView.addHeaderView(header);
        naviagtionDrawer();
        TextView username = (TextView) header.findViewById(R.id.username);
        username.setText("Hi User");
        TextView usernumber = (TextView) header.findViewById(R.id.usernumber);
        usernumber.setText("+91"+phoneNumber);
        contentView=findViewById(R.id.contentview);
        recyclerView=findViewById(R.id.featured_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        imageList=new ArrayList<>();
        adapter=new FeaturedAdapter(this,imageList);
        recyclerView.setAdapter(adapter);
        mdbCategories= FirebaseDatabase.getInstance().getReference("Featured");
        mdbCategories.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    progressBar.setVisibility(View.GONE);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String Image1 = ds.child("Image1").getValue(String.class);
                        String Name = ds.child("Name").getValue(String.class);
                        FeaturedItemModel c = new FeaturedItemModel(Image1,Name);
                        imageList.add(c);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void naviagtionDrawer() {
//Naviagtion Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        animateNavigationDrawer();

    }

    private void animateNavigationDrawer() {
        //Add any color or remove it to use the default one!
        //To make it transparent use Color.Transparent in side setScrimColor();
        //drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });

    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navigationView.setCheckedItem(item);
        return false;
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DashboardScreen.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}