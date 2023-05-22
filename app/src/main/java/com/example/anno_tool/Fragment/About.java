package com.example.anno_tool.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.anno_tool.R;
import com.google.android.material.navigation.NavigationView;

public class About extends Fragment {
    private View aboutView;
    NavigationView navigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        aboutView= inflater.inflate(R.layout.fragment_about, container, false);
        navigationView=getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_about);
        //setting Toolbar title
        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("About");


        return aboutView;
    }
}