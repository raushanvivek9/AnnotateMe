package com.example.anno_tool.Model;

import java.io.Serializable;

public class GetDetailWorkTwo implements Serializable {
    public String l_name;
    public float dXmin;
    public float dXmax;
    public float dYmin;
    public float dYmax;

    public GetDetailWorkTwo() {
    }

    public GetDetailWorkTwo(String l_name, float dXmin, float dXmax, float dYmin, float dYmax) {
        this.l_name = l_name;
        this.dXmin = dXmin;
        this.dXmax = dXmax;
        this.dYmin = dYmin;
        this.dYmax = dYmax;
    }

    public String getL_name() {
        return l_name;
    }

    public void setL_name(String l_name) {
        this.l_name = l_name;
    }

    public float getdXmin() {
        return dXmin;
    }

    public void setdXmin(float dXmin) {
        this.dXmin = dXmin;
    }

    public float getdXmax() {
        return dXmax;
    }

    public void setdXmax(float dXmax) {
        this.dXmax = dXmax;
    }

    public float getdYmin() {
        return dYmin;
    }

    public void setdYmin(float dYmin) {
        this.dYmin = dYmin;
    }

    public float getdYmax() {
        return dYmax;
    }

    public void setdYmax(float dYmax) {
        this.dYmax = dYmax;
    }
}
