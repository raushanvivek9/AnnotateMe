package com.example.anno_tool.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.anno_tool.Adapterss.PrivateShareAdapter;
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

public class SharedDataSet extends Fragment {
    private View SharedDataSetView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference colref;
    private PrivateShareAdapter adapter;
    private Query query;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    NavigationView navigationView;
    RecyclerView shareData_recycler;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SharedDataSetView= inflater.inflate(R.layout.fragment_shared_data_set, container, false);
        //setting Toolbar title
        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Shared Projects");
        navigationView=getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.Shared_Dataset);

        shareData_recycler=SharedDataSetView.findViewById(R.id.share_data);




        colref = db.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("SharedDataSet");
        query = colref.orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<SharedDataNote> options = new FirestoreRecyclerOptions.Builder<SharedDataNote>()
                .setQuery(query, SharedDataNote.class)
                .build();
        adapter = new PrivateShareAdapter(options);
        shareData_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        shareData_recycler.setAdapter(adapter);

        // recycler view position
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                shareData_recycler.smoothScrollToPosition(0);
            }
        });
        adapter.setOnItemClickListener(new PrivateShareAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                SharedDataNote proj_note = documentSnapshot.toObject(SharedDataNote.class);
                String path = proj_note.getProject_path();
                String pname=proj_note.getProject_name();
                String holder_name=proj_note.getShareBy();
                String label_type=proj_note.getLabel_type();
                Bundle bundle = new Bundle();
                bundle.putString("path", path);
                bundle.putString("pname", pname);
                bundle.putString("holder_name",holder_name);
                if (label_type.equals("Simple Classification")) {
                    Work_label_1 work_label_1 = new Work_label_1();
//                    sending data
                    work_label_1.setArguments(bundle);
                    //changing trans
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, work_label_1);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }else if(label_type.equals("Semi-Automated Labeling")){
                    SemiAutomated semiAutomated = new SemiAutomated();
//                    sending data
                    semiAutomated.setArguments(bundle);
                    //changing trans
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, semiAutomated);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }else{
                    Work_label_2 work_label_2 = new Work_label_2();
                    work_label_2.setArguments(bundle);
                    //changing trans
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, work_label_2);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }

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
                                Export export=new Export(path,pname,"SharedProject",getContext(), ltype, e_type[0]);

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setCancelable(false);
                AlertDialog alert = builder.create();
                alert.show();

            }
        });


        return SharedDataSetView;
    }
    @Override
    public void onStart() {

        try {
            adapter.startListening();
//            adapter.notifyItemChanged(0,adapter.getItemCount());
//            shareData_recycler.smoothScrollToPosition(adapter.getItemCount());
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Network Error:"+e.getMessage(), Toast.LENGTH_LONG).show();
        }
        super.onStart();
    }

    @Override
    public void onStop() {

        try {
            adapter.stopListening();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
        }
        super.onStop();
    }

}