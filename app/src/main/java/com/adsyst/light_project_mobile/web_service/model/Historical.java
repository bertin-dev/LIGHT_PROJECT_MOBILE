package com.adsyst.light_project_mobile.web_service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Historical {

    @SerializedName("Message_connexion")
    @Expose
    private String Message_connexion;
    @SerializedName("resultats")
    @Expose
    private List<ResultsHistoric> resultats = null;

    public String getMessage_connexion() {
        return Message_connexion;
    }

    public void setMessage_connexion(String message_connexion) {
        Message_connexion = message_connexion;
    }

    public List<ResultsHistoric> getResultats() {
        return resultats;
    }

    public void setResultats(List<ResultsHistoric> resultats) {
        this.resultats = resultats;
    }
}
