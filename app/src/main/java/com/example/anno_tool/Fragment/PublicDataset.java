package com.example.anno_tool.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.anno_tool.Adapterss.PublicDataAdapter;
import com.example.anno_tool.Model.SharedDataNote;
import com.example.anno_tool.Project_Work.Export;
import com.example.anno_tool.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PublicDataset} factory method to
 * create an instance of this fragment.
 */
public class PublicDataset extends Fragment {
    private View publicDatasetView;
    RecyclerView public_d_recycler;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference colref;
    private PublicDataAdapter adapter;
    private Query query;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    NavigationView navigationView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        publicDatasetView = inflater.inflate(R.layout.fragment_public_dataset, container, false);
        //setting Toolbar title
        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Public Datasets");
        navigationView=getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.public_proj);
        public_d_recycler = publicDatasetView.findViewById(R.id.pub_data);


        colref = db.collection("Public_Project");
//        query = colref.whereEqualTo("proj_share_Type","Public");
        query = colref.orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<SharedDataNote> options = new FirestoreRecyclerOptions.Builder<SharedDataNote>()
                .setQuery(query, SharedDataNote.class)
                .build();
        adapter = new PublicDataAdapter(options);
        public_d_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        public_d_recycler.setAdapter(adapter);

        // recycler view position
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                public_d_recycler.smoothScrollToPosition(0);
            }
        });
        adapter.setOnItemClickListener(new PublicDataAdapter.OnItemClickListener() {
            @Override
            public void onexport(DocumentSnapshot documentSnapshot, int position) {
                SharedDataNote proj_note = documentSnapshot.toObject(SharedDataNote.class);
                String path = proj_note.getProject_path();
                String pname=proj_note.getProject_name();
                String ltype=proj_note.getLabel_type();

                final String[] e_type = new String[1];
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.activity_export_view, null);
                RadioGroup Export_type=(RadioGroup)v.findViewById(R.id.Export_type);
                RadioButton Excel=(RadioButton)v.findViewById(R.id.Excel);
                RadioButton Json=(RadioButton)v.findViewById(R.id.JSON);
                // Set a listener for the RadioGroup to handle selections
                Export_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // Check which radio button was selected
                        if (checkedId == R.id.Excel) {
                            e_type[0] =Excel.getText().toString();

                        } else if (checkedId == R.id.JSON) {

                            e_type[0] =Json.getText().toString();
                        }
                    }
                });
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Select File Type")
                        .setCancelable(false)
                        .setView(v)
                        .setPositiveButton("Export", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //call export constructor for exporting
                                Export export=new Export(path,pname,"PublicDataSet",getContext(), ltype, e_type[0]);

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setCancelable(false);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        return publicDatasetView;
    }
    @Override
    public void onStart() {
        super.onStart();
        try {
            adapter.startListening();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Network Error:"+e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            adapter.stopListening();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
        }
    }

}