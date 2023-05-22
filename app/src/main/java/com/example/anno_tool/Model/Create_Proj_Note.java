package com.example.anno_tool.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Create_Proj_Note {
    private String project_name;
    private String Proj_share_Type="Private";
    private Integer No_of_classes;
    private String label_type;
    private List<String> lable_Name;
    private @ServerTimestamp Date timestamp;

    public Create_Proj_Note() {
    }

    public Create_Proj_Note(String project_name, String proj_share_Type, Integer no_of_classes, String label_type, List<String> lable_Name, Date timestamp) {
        this.project_name = project_name;
        Proj_share_Type = proj_share_Type;
        No_of_classes = no_of_classes;
        this.label_type = label_type;
        this.lable_Name = lable_Name;
        this.timestamp = timestamp;
    }

    public Create_Proj_Note(String project_name, Integer no_of_classes, String label_type, List<String> lable_Name) {
        this.project_name = project_name;
        No_of_classes = no_of_classes;
        this.label_type = label_type;
        this.lable_Name = lable_Name;
    }

    public Create_Proj_Note(String project_name, String label_type) {
        this.project_name = project_name;
        this.label_type = label_type;
    }

    public Create_Proj_Note(String project_name, Integer no_of_classes, String label_type, Date timestamp) {
        this.project_name = project_name;
        No_of_classes = no_of_classes;
        this.label_type = label_type;
        this.timestamp = timestamp;
    }

    public String getLabel_type() {
        return label_type;
    }

    public void setLabel_type(String label_type) {
        this.label_type = label_type;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public Integer getNo_of_classes() {
        return No_of_classes;
    }

    public void setNo_of_classes(Integer no_of_classes) {
        No_of_classes = no_of_classes;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public List<String> getLable_Name() {
        return lable_Name;
    }

    public void setLable_Name(List<String> lable_Name) {
        this.lable_Name = lable_Name;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getProj_share_Type() {
        return Proj_share_Type;
    }

    public void setProj_share_Type(String proj_share_Type) {
        Proj_share_Type = proj_share_Type;
    }
}
