package com.adsyst.light_project_mobile.web_service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Resultat {

    @SerializedName("tel_client")
    @Expose
    private String tel_client;
    @SerializedName("pass_client")
    @Expose
    private String pass_client;

    public String getTel_client() {
        return tel_client;
    }

    public void setTel_client(String tel_client) {
        this.tel_client = tel_client;
    }

    public String getPass_client() {
        return pass_client;
    }

    public void setPass_client(String pass_client) {
        this.pass_client = pass_client;
    }
}
