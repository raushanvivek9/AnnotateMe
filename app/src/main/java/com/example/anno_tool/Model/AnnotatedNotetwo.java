package com.example.anno_tool.Model;

public class AnnotatedNotetwo {
    private String image_url;
    private String labelName;
    private float Xmin;
    private float Xmax;
    private float Ymin;
    private float Ymax;
    private String imageName;

    public AnnotatedNotetwo() {
    }

    public AnnotatedNotetwo(String image_url, String labelName, float xmin, float xmax, float ymin, float ymax, String imageName) {
        this.image_url = image_url;
        this.labelName = labelName;
        Xmin = xmin;
        Xmax = xmax;
        Ymin = ymin;
        Ymax = ymax;
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

    public float getXmin() {
        return Xmin;
    }

    public void setXmin(float xmin) {
        Xmin = xmin;
    }

    public float getXmax() {
        return Xmax;
    }

    public void setXmax(float xmax) {
        Xmax = xmax;
    }

    public float getYmin() {
        return Ymin;
    }

    public void setYmin(float ymin) {
        Ymin = ymin;
    }

    public float getYmax() {
        return Ymax;
    }

    public void setYmax(float ymax) {
        Ymax = ymax;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
