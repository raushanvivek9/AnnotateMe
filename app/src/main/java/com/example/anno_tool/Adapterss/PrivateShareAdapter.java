package com.example.anno_tool.Adapterss;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anno_tool.Model.SharedDataNote;
import com.example.anno_tool.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class PrivateShareAdapter extends FirestoreRecyclerAdapter<SharedDataNote,PrivateShareAdapter.PublicViewHolder> {
    private OnItemClickListener listener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public PrivateShareAdapter(@NonNull FirestoreRecyclerOptions<SharedDataNote> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PrivateShareAdapter.PublicViewHolder holder, int position, @NonNull SharedDataNote model) {
        holder.prj_name.setText(model.getProject_name());
        holder.prj_label_type.setText("Label Type: " + model.getLabel_type());
        holder.shareby.setText("Share By: " + model.getShareBy());
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
        String path = documentSnapshot.getReference().getPath();
//count total annotate from Annotate coolection
        db.document(path).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String prj_path=documentSnapshot.getString("project_path");
                    db.document(prj_path).collection("AnnotatedImages").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            holder.tia.setText("" + queryDocumentSnapshots.size());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            holder.tia.setText("0");

                        }
                    });

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                holder.tia.setText("0");
                Log.e("Error", "" + e.getMessage());

            }
        });
    }


    @NonNull
    @Override
    public PrivateShareAdapter.PublicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dataset_list, parent, false);
        return new PublicViewHolder(view);

    }

    public class PublicViewHolder extends RecyclerView.ViewHolder {
        TextView prj_name, prj_label_type, shareby, tia;
        ImageView export;

        public PublicViewHolder(@NonNull View itemView) {
            super(itemView);
            prj_name = itemView.findViewById(R.id.prj_name);
            prj_label_type = itemView.findViewById(R.id.prj_label_type);
            shareby = itemView.findViewById(R.id.shareby);
            export = itemView.findViewById(R.id.export);
            tia = itemView.findViewById(R.id.tia);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION && listener!=null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
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
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
        void onexport(DocumentSnapshot documentSnapshot, int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}

