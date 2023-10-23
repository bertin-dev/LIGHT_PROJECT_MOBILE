package com.adsyst.light_project_mobile.web_service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TotalSend {

    @SerializedName("Message_connexion")
    @Expose
    private String Message_connexion;
    @SerializedName("Total_des_envoies_des_30_derniers_jours")
    @Expose
    private String Total_des_envoies_des_30_derniers_jours = null;
    @SerializedName("Total_des_recharges_des_30_derniers_jours")
    @Expose
    private String Total_des_recharges_des_30_derniers_jours = null;

    public String getMessage_connexion() {
        return Message_connexion;
    }

    public void setMessage_connexion(String message_connexion) {
        Message_connexion = message_connexion;
    }

    public String getTotal_des_envoies_des_30_derniers_jours() {
        return Total_des_envoies_des_30_derniers_jours;
    }

    public void setTotal_des_envoies_des_30_derniers_jours(String total_des_envoies_des_30_derniers_jours) {
        Total_des_envoies_des_30_derniers_jours = total_des_envoies_des_30_derniers_jours;
    }

    public String getTotal_des_recharges_des_30_derniers_jours() {
        return Total_des_recharges_des_30_derniers_jours;
    }

    public void setTotal_des_recharges_des_30_derniers_jours(String total_des_recharges_des_30_derniers_jours) {
        Total_des_recharges_des_30_derniers_jours = total_des_recharges_des_30_derniers_jours;
    }
}
