package com.example.anno_tool.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.anno_tool.Model.AnnotatedNotetwo;
import com.example.anno_tool.Model.Create_Proj_Note;
import com.example.anno_tool.Model.GetDetailWorkTwo;
import com.example.anno_tool.Project_Work.BoundingBoxView;
import com.example.anno_tool.Project_Work.SelectLabel;
import com.example.anno_tool.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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


public class Work_label_2 extends Fragment {
    public BoundingBoxView boundingBoxView;
    TextView total_anno;
    Button undo,redo,addImage,save,discard;
    static String id, path, pname,holder_name;
    ImageView img_for_label;
    List<String> label = new ArrayList<>();
    ArrayList<String> items;
    static Uri imageUri;
    private String mUri;
    private static LayoutInflater layoutInflater;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    static String labelname;
    static LinearLayout label_layout;
    static float xmin,xmax,ymin,ymax;
    private static CollectionReference addref;
    private View WorkView2;
    static View v;
    String imageName;

    ViewGroup contain;
    static Context context;
    private StorageReference storageReference;
    private StorageTask uploadTask;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        WorkView2= inflater.inflate(R.layout.fragment_work_label_2, container, false);
        //setting Toolbar title
        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Bounding Box");
        contain = container;
        context=getContext();
        layoutInflater =getLayoutInflater();
        undo=WorkView2.findViewById(R.id.undo);
        redo=WorkView2.findViewById(R.id.redo);
        addImage=WorkView2.findViewById(R.id.add_image);
        img_for_label=WorkView2.findViewById(R.id.img_for_label);
        label_layout=WorkView2.findViewById(R.id.label_layout);
        boundingBoxView=WorkView2.findViewById(R.id.bounding_box);
        total_anno=WorkView2.findViewById(R.id.total_anno);
        save=WorkView2.findViewById(R.id.save);
        discard=WorkView2.findViewById(R.id.discard);
        Bundle bundle = getArguments();
        id = bundle.getString("id");
        path = bundle.getString("path");

        pname = bundle.getString("pname");
        holder_name = bundle.getString("holder_name");
        SelectLabel selectLabel=new SelectLabel();
        v=boundingBoxView.getRootView();
        items = new ArrayList<String>();
        //add spinner
        items.clear();
        items.add("Select Label");
        addref = db.document(path).collection("AnnotatedImages");
        db.document(path).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Create_Proj_Note create_proj_note = documentSnapshot.toObject(Create_Proj_Note.class);
                    label = create_proj_note.getLable_Name();
                    for (int i = 0; i < label.size(); i++) {
                        items.add(label.get(i));
                    }
                    selectLabel.loadSpinner(items);
                }
            }
        });
        totalAnnotate();

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
                boundingBoxView.setVisibility(View.VISIBLE);
            }
        });
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boundingBoxView.undo();
            }
        });
        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boundingBoxView.redo();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDetails();
            }
        });
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


        return WorkView2;
    }

    private void totalAnnotate() {
        //to show the total annotate img
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

    private void saveDetails() {
        if (imageUri == null | labelname.equals("")) {
            Toast.makeText(getActivity(), "Invalid Data", Toast.LENGTH_SHORT).show();
        } else {
            //get details from dynamic label
            ArrayList<GetDetailWorkTwo> detailList=new ArrayList<>();
            detailList.clear();
            boolean result=true;
            GetDetailWorkTwo getDetailWorkTwo;
            for (int j = 0; j < label_layout.getChildCount(); j++) {
                View add_label = label_layout.getChildAt(j);
                final TextView textLablel = add_label.findViewById(R.id.lbl_name);
                final TextView dxmin = add_label.findViewById(R.id.dimXmin);
                final TextView dxmax = add_label.findViewById(R.id.dimXmax);
                final TextView dYmin = add_label.findViewById(R.id.dimYmin);
                final TextView dYmax = add_label.findViewById(R.id.dimYmax);

                getDetailWorkTwo = new GetDetailWorkTwo();
                if (!textLablel.getText().toString().equals("")) {
                    getDetailWorkTwo.setL_name(textLablel.getText().toString());
                } else {
                    result = false;
                    break;
                }
                if (!dxmin.getText().toString().equals("")) {
                    getDetailWorkTwo.setdXmin(Float.parseFloat(dxmin.getText().toString()));
                } else {
                    result = false;
                    break;
                }
                if (!dxmax.getText().toString().equals("")) {
                    getDetailWorkTwo.setdXmax(Float.parseFloat(dxmax.getText().toString()));
                } else {
                    result = false;
                    break;
                }
                if (!dYmin.getText().toString().equals("")) {
                    getDetailWorkTwo.setdYmin(Float.parseFloat(dYmin.getText().toString()));
                } else {
                    result = false;
                    break;
                }
                if (!dYmax.getText().toString().equals("")) {
                    getDetailWorkTwo.setdYmax(Float.parseFloat(dYmax.getText().toString()));
                } else {
                    result = false;
                    break;
                }
                detailList.add(getDetailWorkTwo);
            }
            if(detailList.size()==0 || !result){
                Toast.makeText(getActivity(),"add label",Toast.LENGTH_SHORT).show();
            }else {
                boolean alertshow=false;
                for (int i=0; i<detailList.size();i++){
                    if(i==detailList.size()-1)
                        alertshow=true;
                    upload_image(detailList.get(i).getL_name(),detailList.get(i).getdXmin(),detailList.get(i).getdXmax(),detailList.get(i).getdYmin(),detailList.get(i).getdYmax(),alertshow);

                }

            }

        }
    }

    private void upload_image(String l_name, float getdXmin, float getdXmax, float getdYmin, float getdYmax, boolean alertshow) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading...");
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        imageName=System.currentTimeMillis()+"."+ MimeTypeMap.getFileExtensionFromUrl(String.valueOf(imageUri));
        storageReference = FirebaseStorage.getInstance().getReference().child(holder_name+"/"+pname);
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
                    progressDialog.setCanceledOnTouchOutside(false);
                    Uri downloadUri = task.getResult();
                    mUri = downloadUri.toString();
                    AnnotatedNotetwo annotatedNote = new AnnotatedNotetwo(mUri,l_name,getdXmin,getdXmax,getdYmin,getdYmax,imageName);
                    addref.add(annotatedNote);
                    progressDialog.dismiss();
                    if(alertshow==true){
                        AlertDialog.Builder buldier=new AlertDialog.Builder(getActivity());
                        buldier.setTitle("Label Saved Successfully")
                                .setMessage("Do you want to annotate another image?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        boundingBoxView.boundingBoxes.clear();
                                        addImage.setText("Add Image");
                                        label_layout.removeAllViews();

//                                        boundingBoxView.redoStack.clear();
//                                        boundingBoxView.undoStack.clear();
                                        boundingBoxView.invalidate();
                                        boundingBoxView.setVisibility(View.GONE);
                                        totalAnnotate();

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
                    }

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

    private void setBoundingBox() {
        boundingBoxView.setVisibility(View.VISIBLE);
        boundingBoxView.setActivity(getActivity());

    }
    private void selectImage() {
        ImagePicker.with(this)
                .crop()//Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();
        setBoundingBox();
    }
    public void senddataCordinate(float xmax, float xmin, float ymax, float ymin) {
        this.xmax=xmax;
        this.xmin=xmin;
        this.ymin=ymin;
        this.ymax=ymax;
        Log.d("data2",","+this.xmin+","+this.xmax);
    }

    public void sendlabel(String label) {
        labelname=label;
        Log.d("Label",","+labelname);
        addLabelView();

    }

    private void addLabelView() {
        View add_label = layoutInflater.inflate(R.layout.add_label_for_work_label2, contain, false);
//        View addView=getLayoutInflater().inflate(R.layout.label_work2,null,false);
//        View v = LayoutInflater.from(getActivity()).inflate(R.layout.work_label, null);
        TextView lname=add_label.findViewById(R.id.lbl_name);
        TextView DXmin=add_label.findViewById(R.id.dimXmin);
        TextView DXmax=add_label.findViewById(R.id.dimXmax);
        TextView DYmin=add_label.findViewById(R.id.dimYmin);
        TextView DYmax=add_label.findViewById(R.id.dimYmax);
        ImageView removeText = add_label.findViewById(R.id.lable_remove);
        if(labelname.equals("Select Label")){
            Toast.makeText(context, "Please Select Label", Toast.LENGTH_SHORT).show();

        }else{
            lname.setText(labelname);
            DXmin.setText(""+xmin);
            DXmax.setText(""+xmax);
            DYmax.setText(""+ymax);
            DYmin.setText(""+ymin);

            label_layout.addView(add_label);

        }
        removeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(add_label);
            }
        });

    }

    private void removeView(View add_label) {

        label_layout.removeView(add_label);
//        String r=boundingBoxView.boundingBoxes.get(1).c;
//        Log.e("error",r);
//        removeBox();

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        imageUri = data.getData();
        img_for_label.setImageURI(imageUri);

        addImage.setText("Change Image");
    }

}