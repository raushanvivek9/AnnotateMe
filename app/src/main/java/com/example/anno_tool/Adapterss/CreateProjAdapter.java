package com.example.anno_tool.Adapterss;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anno_tool.Model.Create_Proj_Note;
import com.example.anno_tool.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class CreateProjAdapter extends FirestoreRecyclerAdapter<Create_Proj_Note, CreateProjAdapter.ViewHolder> {
    private OnItemClickListener listener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CreateProjAdapter(FirestoreRecyclerOptions<Create_Proj_Note> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int position, Create_Proj_Note model) {
        holder.prj_name.setText(model.getProject_name());
        holder.prj_label_type.setText("Label Type: " + model.getLabel_type());
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
        String path = documentSnapshot.getReference().getPath();
        db.document(path).collection("AnnotatedImages")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        holder.tia.setText("" + queryDocumentSnapshots.size());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.tia.setText("0");
                        Log.e("Error", "" + e.getMessage());
                    }
                });

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_proj_list, parent, false);
        return new ViewHolder(view);
    }

    public void deleteItem(int position) {
        DocumentReference reference = getSnapshots().getSnapshot(position).getReference();
        reference.delete();
        String path = reference.getPath();
        db.collection("Public_Project").whereEqualTo("project_path", path).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String path1 = document.getReference().getPath();
                        db.document(path1).delete();
                    }

                }

            }
        });
        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String path1 = document.getReference().getPath();
                        db.document(path1).collection("SharedDataSet").whereEqualTo("project_path", path).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String path2 = document.getReference().getPath();
                                                db.document(path2).delete();
                                            }
                                        }
                                    }
                                });
                    }
                }
            }
        });


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView prj_name, prj_label_type, tia;
        ImageView export, share_prj;

        public ViewHolder(View itemView) {
            super(itemView);
            prj_name = itemView.findViewById(R.id.prj_name);
            prj_label_type = itemView.findViewById(R.id.prj_label_type);
            export = itemView.findViewById(R.id.export);
            share_prj = itemView.findViewById(R.id.share_prj);
            tia = itemView.findViewById(R.id.tia);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
            export.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onexport(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
            share_prj.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onshare(getSnapshots().getSnapshot(position), position);
                    }
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);

        void onexport(DocumentSnapshot documentSnapshot, int position);

        void onshare(DocumentSnapshot snapshot, int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
