package com.example.anno_tool.Model;

public class AnnotatedNote {
    private String image_url;
    private String labelName;
    private String imageName;

    public AnnotatedNote() {
    }

    public AnnotatedNote(String image_url, String labelName, String imageName) {
        this.image_url = image_url;
        this.labelName = labelName;
        this.imageName = imageName;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
