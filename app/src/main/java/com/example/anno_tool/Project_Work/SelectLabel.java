package com.example.anno_tool.Project_Work;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.anno_tool.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class SelectLabel extends AppCompatDialogFragment {
    private SelectLabelListener listener;

    static ArrayList<String> item;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        return super.onCreateDialog(savedInstanceState);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.activity_select_label_work2, null);
        final Spinner spinner1 =  v.findViewById(R.id.select_label2);

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

        Log.d("item",""+item.size());
//        final Spinner spinner1=new Spinner(getContext());
        ArrayAdapter<String> selectLabelAdapter = new ArrayAdapter<String>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, item);
        selectLabelAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinner1.setAdapter(selectLabelAdapter);
        builder.setView(v)
                .setTitle("Select Label")
                .setCancelable(false)
                .setNegativeButton("Cancel",null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String label=spinner1.getSelectedItem().toString();
                        listener.applylabel(label);
                    }
                });

        return builder.create();
    }

    public void loadSpinner(ArrayList<String> items) {
        item=items;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof SelectLabelListener) {
            listener = (SelectLabelListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement SelectLabelListener");
        }
    }

    public interface SelectLabelListener{
        void applylabel(String label);
    }
}
