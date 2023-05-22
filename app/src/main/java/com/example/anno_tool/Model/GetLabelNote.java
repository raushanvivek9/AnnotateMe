package com.example.anno_tool.Model;

import java.io.Serializable;

public class GetLabelNote implements Serializable {
    public String labelName;

    public GetLabelNote() {

    }

    public GetLabelNote(String labelName) {
        this.labelName = labelName;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
}
