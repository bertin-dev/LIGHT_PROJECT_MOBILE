package com.adsyst.light_project_mobile.web_service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BalanceResponse {

    @SerializedName("Message_connexion")
    @Expose
    private String Message_connexion = null;
    @SerializedName("Solde")
    @Expose
    private String Solde = null;

    public String getMessage_connexion() {
        return Message_connexion;
    }

    public void setMessage_connexion(String message_connexion) {
        Message_connexion = message_connexion;
    }

    public String getSolde() {
        return Solde;
    }

    public void setSolde(String solde) {
        Solde = solde;
    }
}
