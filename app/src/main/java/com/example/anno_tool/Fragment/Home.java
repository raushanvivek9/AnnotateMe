package com.example.anno_tool.Fragment;


import static android.view.View.GONE;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anno_tool.Adapterss.CreateProjAdapter;
import com.example.anno_tool.Email.JavaMailAPI;
import com.example.anno_tool.Model.Create_Proj_Note;
import com.example.anno_tool.Model.GetLabelNote;
import com.example.anno_tool.Model.SharedDataNote;
import com.example.anno_tool.Model.UserDetailNote;
import com.example.anno_tool.Notification.ApiUtils;
import com.example.anno_tool.Notification.NotificationDatas;
import com.example.anno_tool.Notification.NotificationPush;
import com.example.anno_tool.Project_Work.Export;
import com.example.anno_tool.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends Fragment {
    private View homeView;
    Button create;
    RecyclerView project_title;
    private FirebaseAuth mAuth;
    private FirebaseUser currentuser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference colref;
    private ProgressDialog loadingbar;
    private Query query;
    CreateProjAdapter adapter;
    LinearLayout linearLayout;
    int max_label;
    String size;
    Button btwn_add_label, btwn_add_label1;
    ArrayList<GetLabelNote> lbl_name = new ArrayList<>();
    List<String> lable_Name = new ArrayList<String>();
    String holder_name,usrmail,sendermail;
    int random_num;
    NavigationView navigationView;
    boolean result = true;

    public Home() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeView = inflater.inflate(R.layout.fragment_home, container, false);
        //setting Toolbar title
        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("AnnotateMe");
        navigationView=getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_home);
        mAuth = FirebaseAuth.getInstance();
        currentuser = mAuth.getCurrentUser();
        create = homeView.findViewById(R.id.create);
        project_title = homeView.findViewById(R.id.project_title);
        loadingbar = new ProgressDialog(getActivity());
        colref = db.collection("Users").document(currentuser.getUid()).collection("Projects");
        //recycleview for project name
        project_title = homeView.findViewById(R.id.project_title);
        try {

            viewproject();
            //delete items from cart
            ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    // Take action for the swiped item
                    adapter.deleteItem(viewHolder.getAdapterPosition());
                    Toast.makeText(getContext(), "Project Deleted Successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                    new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                            .addBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red))
                            .addActionIcon(R.drawable.ic_delete_black_24dp)
                            .create()
                            .decorate();

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
            itemTouchHelper.attachToRecyclerView(project_title);
            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    create_project();
                }
            });
            db.collection("Users").document(currentuser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                UserDetailNote userDetailNote=documentSnapshot.toObject(UserDetailNote.class);
                                holder_name = userDetailNote.getUname();
                                usrmail=userDetailNote.getUsername();
                            } else {
                                Toast.makeText(getActivity(), "Not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(getActivity(), "Fail to fetch:Network error", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
        }

        return homeView;
    }

    private void viewproject() {
        query = colref.orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Create_Proj_Note> options = new FirestoreRecyclerOptions.Builder<Create_Proj_Note>()
                .setQuery(query, Create_Proj_Note.class)
                .build();
        adapter = new CreateProjAdapter(options);
        project_title.setLayoutManager(new LinearLayoutManager(getContext()));
        project_title.setAdapter(adapter);
        // recycler view position
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                project_title.smoothScrollToPosition(0);
            }
        });

        adapter.setOnItemClickListener(new CreateProjAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Create_Proj_Note create_proj_note = documentSnapshot.toObject(Create_Proj_Note.class);
                String id = documentSnapshot.getReference().getPath();
                String path = documentSnapshot.getReference().getPath();
                String label_type = create_proj_note.getLabel_type();
                String pname=create_proj_note.getProject_name();
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
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
                } else if (label_type.equals("Semi-Automated Labeling")) {
                    SemiAutomated semiAutomated = new SemiAutomated();
                    semiAutomated.setArguments(bundle);
                    //changing trans
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, semiAutomated);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                } else{
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
                Create_Proj_Note create_proj_note = documentSnapshot.toObject(Create_Proj_Note.class);
                String path = documentSnapshot.getReference().getPath();
                String pname=create_proj_note.getProject_name();
                String ltype=create_proj_note.getLabel_type();
                final String[] e_type = new String[1];
                e_type[0]="";
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.activity_export_view, null);
                RadioGroup Export_type=(RadioGroup)v.findViewById(R.id.Export_type);
                RadioButton Excel=(RadioButton)v.findViewById(R.id.Excel);
                RadioButton Json=(RadioButton)v.findViewById(R.id.JSON);
                RadioButton csv=(RadioButton)v.findViewById(R.id.csv);
                RadioButton txt=(RadioButton)v.findViewById(R.id.txt);

                // Set a listener for the RadioGroup to handle selections
                Export_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // Check which radio button was selected
                        if (checkedId == R.id.Excel) {
                            e_type[0] =Excel.getText().toString();

                        } else if (checkedId == R.id.JSON) {
                            e_type[0] =Json.getText().toString();
                        }else  if(checkedId==R.id.csv){
                            e_type[0]=csv.getText().toString();
                        } else if (checkedId==R.id.txt) {
                            e_type[0]=txt.getText().toString();
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
                                if (e_type[0].equals("")){
                                    Toast.makeText(getContext(), "Select File type and Try again", Toast.LENGTH_SHORT).show();
                                }else {

                                    //call export constructor for exporting
                                    Export export=new Export(path,pname,holder_name,getContext(),ltype,e_type[0]);
                                }



                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setCancelable(false);
                AlertDialog alert = builder.create();
                alert.show();



            }
            @Override
            public void onshare(DocumentSnapshot snapshot, int position) {
                Create_Proj_Note create_proj_note = snapshot.toObject(Create_Proj_Note.class);
                String path = snapshot.getReference().getPath();
                String proj=create_proj_note.getProject_name();
                String lbl_type=create_proj_note.getLabel_type();

                final String[] s_type = new String[1];
                s_type[0]="";
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.activity_share_project, null);
                TextInputEditText Share_user = (TextInputEditText) v.findViewById(R.id.share_user);
                RadioGroup share_type=(RadioGroup)v.findViewById(R.id.share_type);
                RadioButton public_radio=(RadioButton)v.findViewById(R.id.public_radio);
                RadioButton personal_radio=(RadioButton)v.findViewById(R.id.personal_radio);
                // Set a listener for the RadioGroup to handle selections
                share_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // Check which radio button was selected
                        if (checkedId == R.id.public_radio) {
                            s_type[0] =public_radio.getText().toString();
                            Share_user.setVisibility(GONE);
                        } else if (checkedId == R.id.personal_radio) {
                            Share_user.setVisibility(View.VISIBLE);
                            s_type[0] =personal_radio.getText().toString();
                        }

                    }
                });
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Select Sharing Type")
                        .setCancelable(false)
                        .setView(v)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (s_type[0].equals("")){
                                    Toast.makeText(getContext(), "Select Share Type and Try again", Toast.LENGTH_SHORT).show();
                                }else {
                                    randomNumber();
                                    if(s_type[0].equals("Public")){
                                        loadingbar.setTitle("Uploading...");
                                        loadingbar.setMessage("please wait, your dataset is uploading in public repository");
                                        loadingbar.setCanceledOnTouchOutside(false);
                                        loadingbar.show();
//
                                        CollectionReference colcref=db.collection("Public_Project");
                                        colcref.whereEqualTo("owner_id",currentuser.getUid())
                                                .whereEqualTo("project_name",proj)
                                                .whereEqualTo("label_type",lbl_type).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.getResult().size() > 0){
                                                    Toast.makeText(getContext(), "You have already shared this Dataset", Toast.LENGTH_SHORT).show();
                                                    loadingbar.dismiss();

                                                }else{
                                                    db.document(path).update("proj_share_Type",s_type[0]);
                                                    SharedDataNote sharedDataNote= new SharedDataNote(proj,lbl_type,holder_name,path,currentuser.getUid());
                                                    colcref.add(sharedDataNote);
                                                    db.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                            for(DocumentSnapshot snapshot1: queryDocumentSnapshots){
                                                                String path=snapshot1.getReference().getPath();
                                                                db.document(path).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                        String token=documentSnapshot.getString("deviceToken");
                                                                        sendNotification(token,"New Dataset","New Dataset added in the Public repository");
                                                                    }
                                                                });
                                                            }
                                                            loadingbar.dismiss();
                                                            Toast.makeText(getContext(), "Dataset uploaded in Public repository", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });



                                    }else if(s_type[0].equals("Personal")){
                                        String shareUsername=Share_user.getText().toString();
                                        db.collection("Users").whereEqualTo("username",shareUsername).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            if(task.getResult().isEmpty()){
                                                                Toast.makeText(getContext(),"username does not Exist,Please try again.",Toast.LENGTH_SHORT).show();
                                                            }else {
                                                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                                    if (documentSnapshot.exists()) {
                                                                        String documentId = documentSnapshot.getId();
                                                                        String deviceToken=documentSnapshot.getString("deviceToken");
                                                                        CollectionReference colref = db.collection("Users").document(documentId).collection("SharedDataSet");
                                                                        colref.whereEqualTo("owner_id",currentuser.getUid())
                                                                                .whereEqualTo("project_name",proj)
                                                                                .whereEqualTo("label_type",lbl_type).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                        if(task.getResult().size() > 0){
                                                                                            Toast.makeText(getContext(), "You have already shared this Project", Toast.LENGTH_SHORT).show();

                                                                                        }else{
                                                                                            SharedDataNote sharedDataNote = new SharedDataNote(proj, lbl_type, holder_name, path,currentuser.getUid());
                                                                                            colref.add(sharedDataNote);
                                                                                            sendMail(shareUsername, usrmail, proj, holder_name);
                                                                                            Toast.makeText(getContext(), "Project Shared Successfully", Toast.LENGTH_SHORT).show();
                                                                                            sendNotification(deviceToken,"New Shared Project","A new project shared by "+holder_name);
                                                                                        }
                                                                                    }
                                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                });

                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();

                                                    }
                                                });
                                    }else{
                                        String username= Objects.requireNonNull(Share_user.getText()).toString();
                                        Toast.makeText(getActivity(),""+username,Toast.LENGTH_SHORT).show();
                                    }

                                }



                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setCancelable(false);
                AlertDialog alert = builder.create();
                alert.show();
            }

        });

    }

    private void sendNotification(String deviceToken, String title, String body) {
        ApiUtils.getClient().sendNotification(new NotificationPush(new NotificationDatas(title,body),deviceToken))
                .enqueue(new Callback<NotificationPush>() {
                    @Override
                    public void onResponse(Call<NotificationPush> call, Response<NotificationPush> response) {
                        if(response.isSuccessful()){
                            Log.d("Share","Success");
                        }
                    }

                    @Override
                    public void onFailure(Call<NotificationPush> call, Throwable t) {
                        Toast.makeText(getContext(),""+t.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
    }


    private void create_project() {
        lbl_name.clear();
        lable_Name.clear();
        max_label = 3;
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.activity_create_project, null);
        final TextInputEditText Proj_name = (TextInputEditText) v.findViewById(R.id.proj_name);
        Spinner Proj_cls_no = (Spinner) v.findViewById(R.id.proj_cls_name);
        TextView ClassText=(TextView) v.findViewById(R.id.classText);
        TextView addlabelText=(TextView) v.findViewById(R.id.addlabeltext);
        final Spinner label_type = (Spinner) v.findViewById(R.id.label_type);
        final ArrayList<String> items = new ArrayList<String>();

        items.add("---Select Label Type---");
        items.add("Simple Classification");
        items.add("Bounding Box");
        items.add("Semi-Automated Labeling");

        linearLayout = v.findViewById(R.id.layout_list);
        btwn_add_label = v.findViewById(R.id.btwn_add_label);
        btwn_add_label1 = v.findViewById(R.id.btwn_add_label1);
        ArrayAdapter<String> labeladapter = new ArrayAdapter<String>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, items);
        labeladapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        label_type.setAdapter(labeladapter);

        label_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String d=parent.getItemAtPosition(position).toString();
//                Toast.makeText(getContext(), ""+d, Toast.LENGTH_SHORT).show();
                if(position==3){
                    Proj_cls_no.setVisibility(GONE);
                    ClassText.setVisibility(GONE);
                    addlabelText.setVisibility(GONE);
                    btwn_add_label1.setVisibility(GONE);
                }else if(position==2 || position==1){
                    Proj_cls_no.setVisibility(View.VISIBLE);
                    ClassText.setVisibility(View.VISIBLE);
                    addlabelText.setVisibility(View.VISIBLE);
                    btwn_add_label1.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //adding dynamic editText for label name
        btwn_add_label1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                size = Proj_cls_no.getSelectedItem().toString();
                if (!size.equals("---Select No. of classes---")) {
                    max_label = Integer.parseInt(size);
                    if (max_label > 0) {
                        addView();
                        btwn_add_label1.setVisibility(GONE);
                        btwn_add_label.setVisibility(View.VISIBLE);
                        Proj_cls_no.setEnabled(false);
                        Proj_cls_no.setClickable(false);
                        max_label--;
                    }
                    if (max_label == 0)
                        btwn_add_label.setVisibility(GONE);
                }
            }
        });
        btwn_add_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (max_label > 0) {
                    addView();
                    max_label--;
                }
                if (max_label == 0)
                    btwn_add_label.setVisibility(GONE);

            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());



        builder.setTitle("New Project")
                .setCancelable(false)
                .setView(v)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String Project_name = Proj_name.getText().toString();
                        String Project_class = Proj_cls_no.getSelectedItem().toString();
                        String label = String.valueOf(label_type.getSelectedItem());

//                        boolean result = true;
                        for (int j = 0; j < linearLayout.getChildCount(); j++) {
                            View add_label = linearLayout.getChildAt(j);
                            final EditText editTextLablel = add_label.findViewById(R.id.label_name);
                            GetLabelNote getLabelNote = new GetLabelNote();
                            if (!editTextLablel.getText().toString().equals("")) {
                                getLabelNote.setLabelName(editTextLablel.getText().toString());
                            } else {
                                result = false;
                            }
                            lbl_name.add(getLabelNote);
                        }
                        if (Project_name.isEmpty() | Project_name.equals("") | label.equals("---Select Label Type---")) {
                            Toast.makeText(getActivity(), "Please Enter Valid Data,Try Again", Toast.LENGTH_SHORT).show();
                        }
//                        else if (lbl_name.size() == 0) {
//                            result = false;
//                            Toast.makeText(getContext(), "Label is requried,Try Again", Toast.LENGTH_SHORT).show();
//                        } else if (!result) {
//                            Toast.makeText(getContext(), "Must fill all the details,Try Again", Toast.LENGTH_SHORT).show();
//                        } else {
//                            int p_clsss = Integer.parseInt(Project_class);
//
//                            int temp = 0;
//                            for (int x = p_clsss; x > max_label; x--) {
//                                GetLabelNote g = lbl_name.get(temp);
//                                temp++;
//                                lable_Name.add(g.getLabelName());
//
//                            }
                            Query query1=colref.whereEqualTo("project_name",Project_name).whereEqualTo("label_type",label);
                            query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().size() > 0) {
                                            Toast.makeText(getContext(), "This Project has already present,Please try with another name", Toast.LENGTH_SHORT).show();
                                        }else if(label.equals("Semi-Automated Labeling")){
                                            Create_Proj_Note create_proj_note = new Create_Proj_Note(Project_name, label);
                                            colref.add(create_proj_note);
                                            Toast.makeText(getActivity(), "Project Created Successfully", Toast.LENGTH_LONG).show();
                                        }else {
                                            if(Project_class.equals("---Select No. of classes---")) {
                                                Toast.makeText(getActivity(), "Please Enter Valid Data,Try Again", Toast.LENGTH_SHORT).show();
                                            } else if (lbl_name.size() == 0) {
                                                result = false;
                                                Toast.makeText(getContext(), "Label is requried,Try Again", Toast.LENGTH_SHORT).show();
                                            } else if (!result) {
                                                Toast.makeText(getContext(), "Must fill all the details,Try Again", Toast.LENGTH_SHORT).show();
                                            } else {
                                                int p_clsss = Integer.parseInt(Project_class);

                                                int temp = 0;
                                                for (int x = p_clsss; x > max_label; x--) {
                                                    GetLabelNote g = lbl_name.get(temp);
                                                    temp++;
                                                    lable_Name.add(g.getLabelName());

                                                }
                                                Create_Proj_Note create_proj_note = new Create_Proj_Note(Project_name, Integer.parseInt(Project_class), label, lable_Name);
                                                colref.add(create_proj_note);
                                                Toast.makeText(getActivity(), "Project Created Successfully", Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    } else {
                                        Log.e("Error", "Error getting document: ", task.getException());
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });

//                        }


                    }
                })
                .setNegativeButton("Cancel", null)
                .setCancelable(false);
        AlertDialog alert = builder.create();
        // Adjust the window soft input mode
        Window window = alert.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        alert.show();
    }
//adding edit text dynamic
    private void addView() {
        final View add_label = getLayoutInflater().inflate(R.layout.add_lablel_edittext, null, false);
        EditText editText = add_label.findViewById(R.id.label_name);
        ImageView removeText = add_label.findViewById(R.id.image_remove_label);
        removeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(add_label);
            }
        });
        linearLayout.addView(add_label);

    }

    private void removeView(View view) {
        linearLayout.removeView(view);
        max_label++;
        if (max_label > 0)
            btwn_add_label.setVisibility(View.VISIBLE);
    }
    private void randomNumber() {
        Random random=new Random();
        random_num=random.nextInt(999999999);
    }
    private void sendMail(String shareUsername, String usrmail, String proj, String holder_name) {
        String message = "Dear Annotator,\n"+"\nA project has been shared with you in private mode. You can access the dataset from 'Shared Projects' option available in AnnotateMe.\n"+"\nFor further communication contact with Sender <"+holder_name+","+usrmail+">\n\nThank you";
        String subject = "AnnotateMe Project";

        //Send Mail
        JavaMailAPI javaMailAPI = new JavaMailAPI(getContext(),shareUsername,subject,message);
        javaMailAPI.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            adapter.startListening();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
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
