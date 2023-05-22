package com.example.anno_tool.Fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

import com.example.anno_tool.Model.AnnotatedNote;
import com.example.anno_tool.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class SemiAutomated extends Fragment {
    private View SemiView;
    ImageView imagelabel, remove_label;
    Button selectImgBtn, usraddLabel,save,discard;
    String id, path, pname,holder_name;
    TextView  label_txt,total_anno;
    Bitmap bitmap;
    private ImageLabeler imageLabeler;
    Uri imageUri;
    DecimalFormat decimalFormat;
    Spinner select_label;
    ArrayList<String> items;
    MaterialCardView labelView, forlabel;
    TextInputEditText labelByusr;
    LinearLayout labeltxtView, label_layout;
    String imageName;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference addref;
    private StorageReference storageReference;
    private StorageTask uploadTask;
    private String mUri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        SemiView = inflater.inflate(R.layout.fragment_semi_automated, container, false);
        //setting Toolbar title
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Semi Automated");
        // Create a DecimalFormat object with the desired format
        decimalFormat = new DecimalFormat("#.##");
        imagelabel = SemiView.findViewById(R.id.imagelabel);
        selectImgBtn = SemiView.findViewById(R.id.selectImgBtn);
        select_label = SemiView.findViewById(R.id.select_label);
        labelView = SemiView.findViewById(R.id.labelView);
        labeltxtView = SemiView.findViewById(R.id.labeltxtView);
        labelByusr = SemiView.findViewById(R.id.labelByUsr);
        labelView = SemiView.findViewById(R.id.labelView);
        forlabel = SemiView.findViewById(R.id.forlabel);
        label_layout = SemiView.findViewById(R.id.label_layout);
        label_txt = SemiView.findViewById(R.id.label_txt);
        remove_label = SemiView.findViewById(R.id.remove_label);
        usraddLabel = SemiView.findViewById(R.id.usraddlabel);
        save = SemiView.findViewById(R.id.save);
        discard = SemiView.findViewById(R.id.discard);
        total_anno = SemiView.findViewById(R.id.total_anno);
        Bundle bundle = getArguments();
        id = bundle.getString("id");
        path = bundle.getString("path");
        pname = bundle.getString("pname");
        holder_name = bundle.getString("holder_name");
        addref = db.document(path).collection("AnnotatedImages");
        count_total();
        //creating file on firebase storage
        storageReference = FirebaseStorage.getInstance().getReference().child(holder_name+"/"+pname);
        imageLabeler = ImageLabeling.getClient(new ImageLabelerOptions.Builder()
                .setConfidenceThreshold(0.7f)
                .build());
        items = new ArrayList<String>();

        selectImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage();
            }
        });

        // to add label
        select_label.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String lbl = parent.getItemAtPosition(position).toString();
                if (imageUri == null) {
                    Toast.makeText(getActivity(), "Select Image", Toast.LENGTH_SHORT).show();
                } else {

                    if (lbl.equals("Select Label")) {
                        label_txt.setText("");
                        label_layout.setVisibility(View.GONE);
                        forlabel.setVisibility(View.GONE);
                        labeltxtView.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Select label first", Toast.LENGTH_SHORT).show();
                    } else if (lbl.equals("Other")) {

                        label_txt.setText("");
                        label_layout.setVisibility(View.GONE);
                        forlabel.setVisibility(View.GONE);
                        labeltxtView.setVisibility(View.VISIBLE);
                    } else {
                        labeltxtView.setVisibility(View.GONE);
                        String[] lb=lbl.split(":");
                        label_txt.setText(lb[0]);
                        forlabel.setVisibility(View.VISIBLE);
                        label_layout.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
        remove_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                label_txt.setText("");
                labeltxtView.setVisibility(View.GONE);
                label_layout.setVisibility(View.GONE);
                forlabel.setVisibility(View.GONE);
            }
        });
        usraddLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                label_txt.setText(""+labelByusr.getText().toString());
                forlabel.setVisibility(View.VISIBLE);
                label_layout.setVisibility(View.VISIBLE);
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
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedetails();
            }
        });

        return SemiView;
    }

    private void savedetails() {
        if (imageUri == null | label_txt.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Invalid Data", Toast.LENGTH_SHORT).show();
        } else {

            upload_image();

        }
    }

    private void upload_image() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading...");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        imageName=System.currentTimeMillis()+"."+ MimeTypeMap.getFileExtensionFromUrl(String.valueOf(imageUri));
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
                                    items.clear();
                                    count_total();
                                    labelView.setVisibility(View.GONE);
                                    label_layout.setVisibility(View.GONE);
                                    forlabel.setVisibility(View.GONE);
                                    label_txt.setText("");
                                    labeltxtView.setVisibility(View.GONE);
                                    labelByusr.setText("");
                                    imagelabel.setImageResource(R.drawable.add_photo);
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

    private void setImage() {
        ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }
    private void count_total() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageUri = data.getData();
        imagelabel.setImageURI(imageUri);
        if (imageUri != null) {
            bitmap = BitmapFactory.decodeFile(imageUri.getPath());
            runDetection(bitmap);
        }else {
            imagelabel.setImageResource(R.drawable.add_photo);
        }

    }

    private void runDetection(Bitmap bitmap) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading...");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        labelView.setVisibility(View.VISIBLE);
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
        imageLabeler.process(inputImage).addOnSuccessListener(imageLabels -> {
            items.clear();
            items.add("Select Label");
            for (ImageLabel label : imageLabels) {
                items.add(label.getText() + ": " + decimalFormat.format(label.getConfidence() * 100) + " %");
            }
            if (imageLabels.isEmpty()) {
                items.add("Could not classify select other!!");
            } else {
                ArrayAdapter<String> selecctLabelAdapter = new ArrayAdapter<String>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, items);
                selecctLabelAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                select_label.setAdapter(selecctLabelAdapter);
            }
            items.add("Other");
            progressDialog.dismiss();

        }).addOnFailureListener(e -> {
            e.printStackTrace();
        });
    }

}