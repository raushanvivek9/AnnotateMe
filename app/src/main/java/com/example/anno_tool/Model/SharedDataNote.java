package com.example.anno_tool.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class SharedDataNote {
    private String project_name;
    private String label_type;
    private String ShareBy;
    private String project_path;
    private String owner_id;
    private @ServerTimestamp Date timestamp;

    public SharedDataNote() {
    }

    public SharedDataNote(String project_name, String label_type, String shareBy, String project_path, String owner_id) {
        this.project_name = project_name;
        this.label_type = label_type;
        ShareBy = shareBy;
        this.project_path = project_path;
        this.owner_id = owner_id;
    }

    public SharedDataNote(String project_name, String label_type, String shareBy, String project_path, String owner_id, Date timestamp) {
        this.project_name = project_name;
        this.label_type = label_type;
        ShareBy = shareBy;
        this.project_path = project_path;
        this.owner_id = owner_id;
        this.timestamp = timestamp;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getLabel_type() {
        return label_type;
    }

    public void setLabel_type(String label_type) {
        this.label_type = label_type;
    }

    public String getShareBy() {
        return ShareBy;
    }

    public void setShareBy(String shareBy) {
        ShareBy = shareBy;
    }

    public String getProject_path() {
        return project_path;
    }

    public void setProject_path(String project_path) {
        this.project_path = project_path;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }
}
