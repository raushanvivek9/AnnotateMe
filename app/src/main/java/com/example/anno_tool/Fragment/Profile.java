package com.example.anno_tool.Fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.anno_tool.Login.User_Login;
import com.example.anno_tool.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.nullness.qual.NonNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends Fragment {
    private View profile_view;
    CircleImageView profile_image;
    ImageButton profile_edit;
    TextView holder_name,a_email_id,phone,logout,n_proj,n_img_anno,pub_dataset;
    private FirebaseAuth mAuth;
    private FirebaseUser currentuser;
    Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private StorageTask uploadTask;
    private ProgressDialog loadingbar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference document;
    String userid;
    int img_anno=0,t_size,p_dataset=0;
    NavigationView navigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        profile_view= inflater.inflate(R.layout.fragment_profile, container, false);
        //setting Toolbar title
        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Profile");
        navigationView=getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_profile);
        profile_image=profile_view.findViewById(R.id.profile_image);
        holder_name=profile_view.findViewById(R.id.holder_name);
        a_email_id=profile_view.findViewById(R.id.a_email_id);
        phone=(TextView) profile_view.findViewById(R.id.phone);
        profile_edit=profile_view.findViewById(R.id.profile_edit);
        logout=profile_view.findViewById(R.id.logout);
        n_proj=profile_view.findViewById(R.id.n_proj);
        n_img_anno=profile_view.findViewById(R.id.n_img_anno);
        pub_dataset=profile_view.findViewById(R.id.p_data);
        mAuth=FirebaseAuth.getInstance();
        currentuser=mAuth.getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference().child("profile images");
        loadingbar=new ProgressDialog(getActivity());
        userid=currentuser.getUid();
        try {
            document=db.collection("Users").document(userid);
            display_data();
            profile_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   selectImage();
                }
            });
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userid=currentuser.getUid();
                    //generate device token
                    String deviceToken= FirebaseInstanceId.getInstance().getToken();
                    db.collection("Users").document(userid).update("deviceToken",deviceToken);
                    mAuth.getInstance().signOut();
                    Intent i=new Intent(getActivity(), User_Login.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                }
            });

        }catch (Exception e){
            Toast.makeText(getActivity(),"Network Error ",Toast.LENGTH_LONG).show();
        }
        return profile_view;
    }

    private void selectImage() {
        ImagePicker.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        loadingbar.setTitle("set profile image");
        loadingbar.setMessage("please wait, your profile image is updating");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();
        if(imageUri!=null){
            final StorageReference fileReference=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask=fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri=task.getResult();
                        String mUri=downloadUri.toString();
                        document.update("imageId",mUri);
                        loadingbar.dismiss();

                    }else{
                        String message=task.getException().toString();
                        Toast.makeText(getActivity(),""+message,Toast.LENGTH_LONG).show();
                        loadingbar.dismiss();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }
            });
        }else {
            Toast.makeText(getActivity(),"No Image selected",Toast.LENGTH_SHORT).show();
            loadingbar.dismiss();
        }

    }

    private void display_data() {
        document.collection("Projects").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                n_proj.setText(""+queryDocumentSnapshots.size());
                t_size = queryDocumentSnapshots.size();

                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    String path = snapshot.getReference().getPath();
                    if(snapshot.getString("proj_share_Type").equals("Public")){
                        p_dataset++;
                    }
                    db.document(path).collection("AnnotatedImages").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            img_anno=img_anno+ queryDocumentSnapshots.size();
                            t_size--;
                            if(t_size==0){
                                n_img_anno.setText(""+img_anno);
                                pub_dataset.setText(""+p_dataset);
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@androidx.annotation.NonNull Exception e) {
                            n_img_anno.setText(""+img_anno);
                            pub_dataset.setText(""+p_dataset);

                        }
                    });
                }



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                n_proj.setText("0");

            }
        });
        document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    holder_name.setText(documentSnapshot.getString("uname"));
                    a_email_id.setText(documentSnapshot.getString("username"));
                    phone.setText(documentSnapshot.getString("phone_no"));
                    String img_link=documentSnapshot.getString("imageId");
                    if(img_link!=null){
                        Picasso.get().load(img_link).into(profile_image);
                    }

                }else{
                    Toast.makeText(getActivity(),"Not found",Toast.LENGTH_SHORT).show();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getActivity(),"Fail to fetch:Network error",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageUri=data.getData();
        profile_image.setImageURI(imageUri);
        if(uploadTask!=null&& uploadTask.isInProgress())
        {
            Toast.makeText(getActivity(),"upload in progress",Toast.LENGTH_SHORT).show();
        }
        else
        {
            uploadImage();

        }
    }


}