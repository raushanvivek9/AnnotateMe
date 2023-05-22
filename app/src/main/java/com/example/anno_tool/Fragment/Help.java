package com.example.anno_tool.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.anno_tool.R;
import com.google.android.material.navigation.NavigationView;

public class Help extends Fragment {
    NavigationView navigationView;
    private View HelpView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        HelpView= inflater.inflate(R.layout.fragment_help, container, false);
        navigationView=getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_help);
        //setting Toolbar title
        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Help");

        return HelpView;
    }
}