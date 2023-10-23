package com.adsyst.light_project_mobile.web_service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneResponse {

    @SerializedName("Message_connexion")
    @Expose
    private String Message_connexion;
    @SerializedName("resultat")
    @Expose
    private List<Resultat> resultat = null;

    public String getMessage_connexion() {
        return Message_connexion;
    }

    public void setMessage_connexion(String message_connexion) {
        Message_connexion = message_connexion;
    }

    public List<Resultat> getResultat() {
        return resultat;
    }

    public void setResultat(List<Resultat> resultat) {
        this.resultat = resultat;
    }


}
