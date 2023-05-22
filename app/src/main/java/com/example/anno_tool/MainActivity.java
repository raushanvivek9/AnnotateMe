package com.example.anno_tool;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


import com.example.anno_tool.Fragment.About;
import com.example.anno_tool.Fragment.Help;
import com.example.anno_tool.Fragment.Home;
import com.example.anno_tool.Fragment.Profile;
import com.example.anno_tool.Fragment.PublicDataset;
import com.example.anno_tool.Fragment.SharedDataSet;
import com.example.anno_tool.Fragment.Work_label_2;
import com.example.anno_tool.Project_Work.SelectLabel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements SelectLabel.SelectLabelListener {
    DrawerLayout drawerLayout;
    public NavigationView navigationView;
    Toolbar toolbar;
    private FirebaseAuth mAuth;
    private FirebaseUser currentuser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        currentuser=mAuth.getCurrentUser();
        NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
                =new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Fragment selectedFragment=null;
                switch(item.getItemId()){
                    case R.id.nav_home:
                        selectedFragment=new Home();
                        break;
                    case R.id.nav_profile:
                        selectedFragment=new Profile();
                        break;
                    case R.id.nav_help:
                        selectedFragment=new Help();
                        break;
                    case R.id.nav_about:
                        selectedFragment=new About();
                        break;
                    case R.id.public_proj:
                        selectedFragment=new PublicDataset();
                        break;
                    case R.id.Shared_Dataset:
                        selectedFragment=new SharedDataSet();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).addToBackStack(null).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        };
//        Hooks
        drawerLayout=findViewById(R.id.drawer_laylout);
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("AnnotateMe");
        navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);
//          toolbar
        setSupportActionBar(toolbar);
//        Navi
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //navigationView.setNavigationItemSelectedListener(this);

//        navigationView.setCheckedItem(R.id.nav_home);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Home()).commit();
        navigationView.setCheckedItem(R.id.nav_home);
    }

    @Override
    public void onBackPressed() {

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);

        }else{

            super.onBackPressed();
        }

    }

    @Override
    public void applylabel(String label) {
        Work_label_2 workLabel2=new Work_label_2();
        workLabel2.sendlabel(label);
    }

}