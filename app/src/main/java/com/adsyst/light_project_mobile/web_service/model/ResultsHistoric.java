package com.adsyst.light_project_mobile.web_service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultsHistoric {

    @SerializedName("num_trans")
    @Expose
    private String num_trans = null;
    @SerializedName("date_trans")
    @Expose
    private String date_trans = null;
    @SerializedName("nom_call")
    @Expose
    private String nom_call = null;
    @SerializedName("nom_client")
    @Expose
    private String nom_client = null;
    @SerializedName("nom_ben")
    @Expose
    private String nom_ben = null;
    @SerializedName("nom_exp")
    @Expose
    private String nom_exp = null;
    @SerializedName("tel_ben")
    @Expose
    private String tel_ben = null;
    @SerializedName("tel_exp")
    @Expose
    private String tel_exp = null;
    @SerializedName("tel_call")
    @Expose
    private String tel_call = null;
    @SerializedName("tel_client")
    @Expose
    private String tel_client = null;
    @SerializedName("montant_trans")
    @Expose
    private String montant_trans = null;
    @SerializedName("frais_trans")
    @Expose
    private String frais_trans = null;

    public String getNum_trans() {
        return num_trans;
    }

    public void setNum_trans(String num_trans) {
        this.num_trans = num_trans;
    }

    public String getDate_trans() {
        return date_trans;
    }

    public void setDate_trans(String date_trans) {
        this.date_trans = date_trans;
    }

    public String getNom_call() {
        return nom_call;
    }

    public void setNom_call(String nom_call) {
        this.nom_call = nom_call;
    }

    public String getNom_client() {
        return nom_client;
    }

    public void setNom_client(String nom_client) {
        this.nom_client = nom_client;
    }

    public String getNom_ben() {
        return nom_ben;
    }

    public void setNom_ben(String nom_ben) {
        this.nom_ben = nom_ben;
    }

    public String getNom_exp() {
        return nom_exp;
    }

    public void setNom_exp(String nom_exp) {
        this.nom_exp = nom_exp;
    }

    public String getTel_ben() {
        return tel_ben;
    }

    public void setTel_ben(String tel_ben) {
        this.tel_ben = tel_ben;
    }

    public String getTel_exp() {
        return tel_exp;
    }

    public void setTel_exp(String tel_exp) {
        this.tel_exp = tel_exp;
    }

    public String getTel_call() {
        return tel_call;
    }

    public void setTel_call(String tel_call) {
        this.tel_call = tel_call;
    }

    public String getTel_client() {
        return tel_client;
    }

    public void setTel_client(String tel_client) {
        this.tel_client = tel_client;
    }

    public String getMontant_trans() {
        return montant_trans;
    }

    public void setMontant_trans(String montant_trans) {
        this.montant_trans = montant_trans;
    }

    public String getFrais_trans() {
        return frais_trans;
    }

    public void setFrais_trans(String frais_trans) {
        this.frais_trans = frais_trans;
    }
}
