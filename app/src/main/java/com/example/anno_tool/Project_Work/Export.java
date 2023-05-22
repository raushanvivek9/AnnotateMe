package com.example.anno_tool.Project_Work;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.anno_tool.Model.AnnotatedNotetwo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.opencsv.CSVWriter;


import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Export {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog loadingbar;
    int rowIndex = 1;
    ByteArrayOutputStream outputStream;
    HSSFWorkbook hssfWorkbook;
    HSSFSheet hssfSheet;
    int t_size;
    HSSFCell hssfCell1, hssfCell2, hssfCell3, hssfCell4, hssfCell5, hssfCell6,hssfCell7, hssfCell, hssfCellA, hssfCellB, hssfCellC, hssfCellD, hssfCellE,hssfCellF;

    public Export(String path, String pname, String type, Context context, String ltype, String e_type) {
        loadingbar = new ProgressDialog(context);
        loadingbar.setTitle("Exporting..");
        loadingbar.setMessage("please wait, your file is exporting");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();
        if(e_type.equals("JSON")){
            json(path,pname,type,context);
        }else if(e_type.equals("Excel")){
            excel(path, pname, type, context, ltype,context);
        } else if (e_type.equals("CSV")) {
            csv(path, pname, type, context, ltype,context);
        } else if (e_type.equals("TXT")) {
            txt(path, pname, type, context, ltype,context);
        }


    }

    private void txt(String path, String pname, String type, Context context, String ltype, Context context1) {
        String fileName = pname+".txt";
        db.document(path).collection("AnnotatedImages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    String textData = "";
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> docData = document.getData();
                        // Convert docData to string format and append to textData
                        textData += docData.toString() + "\n";
                    }
                    byte[] bytes = textData.getBytes(Charset.forName("UTF-8"));
                    downloadfile(bytes,type,pname,fileName,context1);
                }
            }
        });
    }

    private void csv(String path, String pname, String type, Context context, String ltype, Context context1) {
        String fileName = pname+".csv";
        db.document(path).collection("AnnotatedImages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    CSVWriter writer=null;
                    try{
                        File file=new File(context.getCacheDir(),"data.csv");
                        FileWriter outputfile = new FileWriter(file);
                        writer = new CSVWriter(outputfile);
                        if (ltype.equals("Bounding Box")){
                            // write headers to the CSV file
                            String[] headers = { "ImageLink","ImageName","XMax","XMin","YMax","YMin","LableName"};
                            writer.writeNext(headers);
                            // iterate through the collection data and write each row to the CSV file
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                String[] data = {
                                        doc.getString("image_url"),
                                        doc.getString("imageName"),
                                        String.valueOf(doc.getDouble("xmax")),
                                        String.valueOf(doc.getDouble("xmin")),
                                        String.valueOf(doc.getDouble("ymax")),
                                        String.valueOf(doc.getDouble("ymin")),
                                        doc.getString("labelName")
                                };
                                Log.d("file","before");
                                writer.writeNext(data);
                            }

                        } else if (ltype.equals("Simple Classification") || ltype.equals("Semi-Automated Labeling")){
                            // write headers to the CSV file
                            String[] headers = { "ImageLink","ImageName","LableName"};
                            writer.writeNext(headers);
                            // iterate through the collection data and write each row to the CSV file
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                String[] data = {
                                        doc.getString("image_url"),
                                        doc.getString("imageName"),
                                        doc.getString("labelName")
                                };
                                Log.d("file","before");
                                writer.writeNext(data);
                            }

                        }



                        writer.close();
                        Log.d("file","after");
                        // upload the CSV file to Firebase Storage
                        Uri fileUri = Uri.fromFile(file);
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();
                        StorageReference fileRef = storageRef.child(type + "/" + pname).child(fileName);
                        fileRef.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // delete temporary file
                                file.delete();
                                startdownload(fileRef,fileName,context1);



                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // delete temporary file
                                file.delete();
                                loadingbar.dismiss();
                                Toast.makeText(context1, "file has exported successfully", Toast.LENGTH_LONG).show();

                            }
                        });


                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void excel(String path, String pname, String type, Context context, String ltype, Context context1) {


        String fileName = pname+".xls";

        CollectionReference collectionReference = db.document(path).collection("AnnotatedImages");
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                hssfWorkbook = new HSSFWorkbook();
                hssfSheet = hssfWorkbook.createSheet("sheet1");
                outputStream = new ByteArrayOutputStream();
                getDatafromFirebase(queryDocumentSnapshots, type, pname, fileName, ltype,context1);

            }
        });


    }

    public void getDatafromFirebase(@NonNull QuerySnapshot queryDocumentSnapshots, String type, String pname, String fileName, String ltype, Context context1) {
        t_size = queryDocumentSnapshots.size();
        if (ltype.equals("Bounding Box")) {
            HSSFRow hssfRow1 = hssfSheet.createRow(0);
            hssfCell1 = hssfRow1.createCell(0);
            hssfCell1.setCellValue("ImageLink");
            hssfCell2 = hssfRow1.createCell(1);
            hssfCell2.setCellValue("ImageName");
            hssfCell3 = hssfRow1.createCell(2);
            hssfCell3.setCellValue("XMax");
            hssfCell4 = hssfRow1.createCell(3);
            hssfCell4.setCellValue("XMin");
            hssfCell5 = hssfRow1.createCell(4);
            hssfCell5.setCellValue("YMax");
            hssfCell6 = hssfRow1.createCell(5);
            hssfCell6.setCellValue("YMin");
            hssfCell7 = hssfRow1.createCell(6);
            hssfCell7.setCellValue("LableName");

            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
//                    Map<String, Object> data = snapshot.getData();
//                    dataList.add(data);
                String path = snapshot.getReference().getPath();


                db.document(path).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        AnnotatedNotetwo annotatedNotetwo = documentSnapshot.toObject(AnnotatedNotetwo.class);
                        HSSFRow hssfRow = hssfSheet.createRow(rowIndex);
                        hssfCell = hssfRow.createCell(0);
                        hssfCell.setCellValue(annotatedNotetwo.getImage_url());
                        hssfCellA = hssfRow.createCell(1);
                        hssfCellA.setCellValue(annotatedNotetwo.getImageName());
                        hssfCellB = hssfRow.createCell(2);
                        hssfCellB.setCellValue(annotatedNotetwo.getXmax());
                        hssfCellC = hssfRow.createCell(3);
                        hssfCellC.setCellValue(annotatedNotetwo.getXmin());
                        hssfCellD = hssfRow.createCell(4);
                        hssfCellD.setCellValue(annotatedNotetwo.getYmax());
                        hssfCellE = hssfRow.createCell(5);
                        hssfCellE.setCellValue(annotatedNotetwo.getYmin());
                        hssfCellF = hssfRow.createCell(6);
                        hssfCellF.setCellValue(annotatedNotetwo.getLabelName());
                        rowIndex++;


                        t_size--;
                        if (t_size == 0) {
                            savefile(type,pname,fileName,context1);
                        }

                    }
                });

            }

        }else if(ltype.equals("Simple Classification") || ltype.equals("Semi-Automated Labeling")){
            HSSFRow hssfRow1 = hssfSheet.createRow(0);
            hssfCell1 = hssfRow1.createCell(0);
            hssfCell1.setCellValue("ImageLink");
            hssfCell2 = hssfRow1.createCell(1);
            hssfCell2.setCellValue("ImageName");
            hssfCell6 = hssfRow1.createCell(2);
            hssfCell6.setCellValue("LableName");
            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
//                    Map<String, Object> data = snapshot.getData();
//                    dataList.add(data);
                String path = snapshot.getReference().getPath();


                db.document(path).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        AnnotatedNotetwo annotatedNotetwo = documentSnapshot.toObject(AnnotatedNotetwo.class);
                        HSSFRow hssfRow = hssfSheet.createRow(rowIndex);
                        hssfCell = hssfRow.createCell(0);
                        hssfCell.setCellValue(annotatedNotetwo.getImage_url());
                        hssfCellA = hssfRow.createCell(1);
                        hssfCellA.setCellValue(annotatedNotetwo.getImageName());

                        hssfCellE = hssfRow.createCell(2);
                        hssfCellE.setCellValue(annotatedNotetwo.getLabelName());
                        rowIndex++;
                        t_size--;
                        if (t_size == 0) {
                            savefile(type,pname,fileName, context1);
                        }

                    }
                });

            }

        }
    }

    private void savefile(String type, String pname, String fileName, Context context1) {
        try {
            hssfWorkbook.write(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] data = outputStream.toByteArray();

        downloadfile(data,type,pname,fileName,context1);
    }

    private void downloadfile(byte[] data, String type, String pname, String fileName, Context context1) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference fileRef = storageRef.child(type + "/" + pname).child(fileName);
        fileRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("insertation", "Text written to file successfully!");
               startdownload(fileRef,fileName,context1);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingbar.dismiss();
                Log.e("insertation", "Error writing text to file", e);
            }
        });
    }

    private void startdownload(StorageReference fileRef, String fileName, Context context1) {
        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                try {
                    DownloadManager downloadManager = (DownloadManager) context1.getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri1 = Uri.parse(url);
                    DownloadManager.Request request = new DownloadManager.Request(uri1);
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                    downloadManager.enqueue(request);
                    loadingbar.dismiss();
                    Toast.makeText(context1, "file has exported successfully", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    loadingbar.dismiss();
                    Toast.makeText(context1, "Network error" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingbar.dismiss();

            }
        });
    }


    private void json(String path, String pname, String type, Context context) {
        db.document(path).collection("AnnotatedImages").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                List<Map<String, Object>> dataList = new ArrayList<>();

                for (DocumentSnapshot snapshot : snapshotList) {
                    Map<String, Object> data = snapshot.getData();
                    dataList.add(data);
                }

                Gson gson = new Gson();
                String jsonString = gson.toJson(dataList);
                String fileName = pname + ".json";

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference fileRef = storageRef.child(type + "/" + pname).child(fileName);
                byte[] bytes = jsonString.getBytes();
                downloadfile(bytes,type,pname,fileName,context);

            }
        });
    }
}
