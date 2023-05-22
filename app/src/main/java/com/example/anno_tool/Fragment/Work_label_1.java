package com.example.anno_tool.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.anno_tool.Model.AnnotatedNote;
import com.example.anno_tool.Model.Create_Proj_Note;
import com.example.anno_tool.Model.UserDetailNote;
import com.example.anno_tool.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class Work_label_1 extends Fragment {
    private View WorkView1;
    String id, path, pname;
    MaterialCardView forlabel;
    ImageView img_for_label, remove_label;
    Button save, discard, add_image;
    LinearLayout label_layout;
    TextView label_txt;
    TextView total_anno;
    Spinner select_label;
    private FirebaseAuth mAuth;
    String holder_name;
    private FirebaseUser currentuser;
    String imageName;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference addref;
    Uri imageUri;
    private String mUri;
    List<String> label = new ArrayList<>();
    ArrayList<String> items;
    ProgressDialog loadingbar;
    private StorageReference storageReference;
    private StorageTask uploadTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        WorkView1 = inflater.inflate(R.layout.fragment_work_label_1, container, false);
        //setting Toolbar title
        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Simple Classification");
        //get data from previous fragment (Home)
        Bundle bundle = getArguments();
        id = bundle.getString("id");
        path = bundle.getString("path");
        pname = bundle.getString("pname");

        mAuth = FirebaseAuth.getInstance();
        currentuser = mAuth.getCurrentUser();
        img_for_label = WorkView1.findViewById(R.id.img_for_label);
        remove_label = WorkView1.findViewById(R.id.remove_label);
        forlabel=WorkView1.findViewById(R.id.forlabel);
        save = WorkView1.findViewById(R.id.save);
        add_image = WorkView1.findViewById(R.id.add_image);
        discard = WorkView1.findViewById(R.id.discard);
        label_layout = WorkView1.findViewById(R.id.label_layout);
        label_txt = WorkView1.findViewById(R.id.label_txt);
        select_label = WorkView1.findViewById(R.id.select_label);
        total_anno=WorkView1.findViewById(R.id.total_anno);
        loadingbar = new ProgressDialog(getActivity());
        loadingbar.setTitle("Please wait");
        loadingbar.setMessage("Fetching Details....");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();

        items = new ArrayList<String>();
        //set spinner value
        adddatainspinner();
        //to fetch the holder name
        db.collection("Users").document(currentuser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            UserDetailNote userDetailNote=documentSnapshot.toObject(UserDetailNote.class);
                            holder_name = userDetailNote.getUname();
                            //creating file on firebase storage
                            storageReference = FirebaseStorage.getInstance().getReference().child(holder_name+"/"+pname);
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

        loadingbar.dismiss();
        // to add  img
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        // to discard project and go back to home
        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Home home = new Home();
                //changing trans
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, home);
                fragmentTransaction.commit();
            }
        });
        // to add label
        select_label.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String lbl = parent.getItemAtPosition(position).toString();;
                if (imageUri == null) {
                    Toast.makeText(getActivity(), "Select Image", Toast.LENGTH_SHORT).show();
                } else {

                    if (lbl.equals("Select Label")) {
                        label_txt.setText("");
                        label_layout.setVisibility(View.GONE);
                        forlabel.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Select label first", Toast.LENGTH_SHORT).show();
                    } else {
                        label_txt.setText(lbl);
                        forlabel.setVisibility(View.VISIBLE);
                        label_layout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedetails();
            }
        });
        remove_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                label_txt.setText("");
                label_layout.setVisibility(View.GONE);
                forlabel.setVisibility(View.GONE);
            }
        });

        return WorkView1;
    }

    private void count_total() {
        addref = db.document(path).collection("AnnotatedImages");
        addref.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int count = queryDocumentSnapshots.size();
                total_anno.setText(""+count);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();


            }
        });
    }

    private void adddatainspinner() {
        //to show the total annotate img
        count_total();
        items.clear();
        items.add("Select Label");
        db.document(path).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Create_Proj_Note create_proj_note = documentSnapshot.toObject(Create_Proj_Note.class);
                    label = create_proj_note.getLable_Name();
                    for (int i = 0; i < label.size(); i++) {
                        items.add(label.get(i));
                    }
                }
            }
        });
        ArrayAdapter<String> selecctLabelAdapter = new ArrayAdapter<String>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, items);
        selecctLabelAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        select_label.setAdapter(selecctLabelAdapter);
    }

    private void savedetails() {

        if (imageUri == null | label_txt.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Invalid Data", Toast.LENGTH_SHORT).show();
        } else {

            upload_image();

        }
    }

    private void selectImage() {
        ImagePicker.with(this)
                .crop()//Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }
    
    private void upload_image() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading...");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        imageName=System.currentTimeMillis()+"."+MimeTypeMap.getFileExtensionFromUrl(String.valueOf(imageUri));
        final StorageReference fileReference = storageReference.child(imageName);
        uploadTask = fileReference.putFile(imageUri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
//                        progressDialog.setCanceledOnTouchOutside(false);
                    Uri downloadUri = task.getResult();
                    mUri = downloadUri.toString();
                    String lb_name = label_txt.getText().toString();
                    AnnotatedNote annotatedNote = new AnnotatedNote(mUri,lb_name,imageName);
                    addref.add(annotatedNote);
                    progressDialog.dismiss();
                    AlertDialog.Builder buldier=new AlertDialog.Builder(getActivity());
                    buldier.setTitle("Label Saved Successfully")
                            .setMessage("Do you want to annotate another image?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    adddatainspinner();
                                    label_layout.setVisibility(View.GONE);
                                    label_txt.setText("");
                                    add_image.setText("Add Image");
                                    img_for_label.setImageResource(R.drawable.add_photo);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Home home = new Home();
                                    //changing trans
                                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.fragment_container, home);
                                    fragmentTransaction.commit();
                                }
                            })
                            .setCancelable(false);
                    AlertDialog alert= buldier.create();
                    alert.show();
                } else {
                    String message = task.getException().toString();
                    Toast.makeText(getActivity(), "" + message, Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageUri = data.getData();
        img_for_label.setImageURI(imageUri);
    }

}