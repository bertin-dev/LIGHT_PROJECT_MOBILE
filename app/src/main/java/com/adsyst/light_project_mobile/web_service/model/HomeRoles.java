package com.adsyst.light_project_mobile.web_service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HomeRoles {

    @SerializedName("Message_connexion")
    @Expose
    private String Message_connexion;
    @SerializedName("resultat")
    @Expose
    private List<Roles> resultat = null;

    public String getMessage_connexion() {
        return Message_connexion;
    }

    public void setMessage_connexion(String message_connexion) {
        Message_connexion = message_connexion;
    }

    public List<Roles> getResultat() {
        return resultat;
    }

    public void setResultat(List<Roles> resultat) {
        this.resultat = resultat;
    }

    public static class Roles {

        @SerializedName("id_role")
        @Expose
        private String id_role = null;
        @SerializedName("roles")
        @Expose
        private String roles = null;

        public String getId_role() {
            return id_role;
        }

        public void setId_role(String id_role) {
            this.id_role = id_role;
        }

        public String getRoles() {
            return roles;
        }

        public void setRoles(String roles) {
            this.roles = roles;
        }
    }
}
