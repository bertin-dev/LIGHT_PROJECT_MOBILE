package com.adsyst.light_project_mobile.web_service.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class LoginResponse {

    @SerializedName("Message_connexion")
    @Expose
    private String Message_connexion;
    @SerializedName("Message")
    @Expose
    private String Message = null;
    @SerializedName("resultat")
    @Expose
    private List<dynamicResult> resultat = null;


    protected LoginResponse(Parcel in) {
        Message_connexion = in.readString();
        Message = in.readString();
    }

    public String getMessage_connexion() {
        return Message_connexion;
    }

    public void setMessage_connexion(String message_connexion) {
        Message_connexion = message_connexion;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public List<dynamicResult> getResultat() {
        return resultat;
    }

    public void setResultat(List<dynamicResult> resultat) {
        this.resultat = resultat;
    }

    public static class dynamicResult{

        @SerializedName("id_user")
        @Expose
        private String id_user = null;

        @SerializedName("nom_client")
        @Expose
        private String nom_client = null;

        @SerializedName("prenom_client")
        @Expose
        private String prenom_client = null;
        @SerializedName("num_cni_client")
        @Expose
        private String num_cni_client = null;
        @SerializedName("tel_client")
        @Expose
        private String tel_client = null;
        @SerializedName("pass_client")
        @Expose
        private String pass_client = null;
        @SerializedName("sex_client")
        @Expose
        private String sex_client = null;
        @SerializedName("email_client")
        @Expose
        private String email_client;

        @SerializedName("cni_start_date")
        @Expose
        private String cni_start_date;

        @SerializedName("cni_end_date")
        @Expose
        private String cni_end_date;

        @SerializedName("id_role")
        @Expose
        private String id_role;

        @SerializedName("id_cpt")
        @Expose
        private String id_cpt;

        public String getId_user() {
            return id_user;
        }

        public void setId_user(String id_user) {
            this.id_user = id_user;
        }

        public String getNom_client() {
            return nom_client;
        }

        public void setNom_client(String nom_client) {
            this.nom_client = nom_client;
        }

        public String getPrenom_client() {
            return prenom_client;
        }

        public void setPrenom_client(String prenom_client) {
            this.prenom_client = prenom_client;
        }

        public String getNum_cni_client() {
            return num_cni_client;
        }

        public void setNum_cni_client(String num_cni_client) {
            this.num_cni_client = num_cni_client;
        }

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

        public String getSex_client() {
            return sex_client;
        }

        public void setSex_client(String sex_client) {
            this.sex_client = sex_client;
        }

        public String getEmail_client() {
            return email_client;
        }

        public void setEmail_client(String email_client) {
            this.email_client = email_client;
        }

        public String getCni_start_date() {
            return cni_start_date;
        }

        public void setCni_start_date(String cni_start_date) {
            this.cni_start_date = cni_start_date;
        }

        public String getCni_end_date() {
            return cni_end_date;
        }

        public void setCni_end_date(String cni_end_date) {
            this.cni_end_date = cni_end_date;
        }

        public String getId_role() {
            return id_role;
        }

        public void setId_role(String id_role) {
            this.id_role = id_role;
        }

        public String getId_cpt() {
            return id_cpt;
        }

        public void setId_cpt(String id_cpt) {
            this.id_cpt = id_cpt;
        }
    }


}
