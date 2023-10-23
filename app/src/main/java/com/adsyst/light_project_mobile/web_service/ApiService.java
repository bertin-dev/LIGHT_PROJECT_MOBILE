package com.adsyst.light_project_mobile.web_service;

import com.adsyst.light_project_mobile.web_service.model.BalanceResponse;
import com.adsyst.light_project_mobile.web_service.model.Historical;
import com.adsyst.light_project_mobile.web_service.model.HomeRoles;
import com.adsyst.light_project_mobile.web_service.model.LoginResponse;
import com.adsyst.light_project_mobile.web_service.model.PhoneResponse;
import com.adsyst.light_project_mobile.web_service.model.RegisterResponse;
import com.adsyst.light_project_mobile.web_service.model.TotalSend;
import com.adsyst.light_project_mobile.web_service.model.TransfertDepositResponse;
import com.adsyst.light_project_mobile.web_service.model.WithdrawalResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    /*----------------------------------------------------------LIGHT-------------------------------------------------------*/

    /**
     * Authentification d'un client
     *
     * @param cle
     * @param tel
     * @param pass
     * @param pass_crypt
     * @return LoginResponse
     */
    @POST("authentification/auth_user.php")
    @FormUrlEncoded
    Call<LoginResponse> auth_client(@Field("cle") String cle,
                                    @Field("tel") String tel,
                                    @Field("pass") String pass,
                                    @Field("pass_crypt") String pass_crypt);


    /**
     * permet de recupérer les données utilisateur à partir de la clé et le telephone de l'utilisateur
     *
     * @param cle
     * @param tel
     * @return PhoneResponse
     */
    @POST("get/get_tel.php")
    @FormUrlEncoded
    Call<PhoneResponse> get_tel(@Field("cle") String cle,
                                @Field("tel") String tel);


    /**
     * permet d'effectuer les transferts et dépots aux utilisateurs
     *
     * @param cle
     * @param tel_exp
     * @param tel_ben
     * @param montant
     * @return TransfertDepositResponse
     */
    @POST("transaction/transfert.php")
    @FormUrlEncoded
    Call<TransfertDepositResponse> transfert(@Field("cle") String cle,
                                             @Field("tel_exp") String tel_exp,
                                             @Field("tel_ben") String tel_ben,
                                             @Field("montant") float montant);


    /**
     * permet de faire un retrait d'argent d'un compte
     *
     * @param cle
     * @param tel_call
     * @param tel_client
     * @param montant_
     * @param pass_client
     * @return WithdrawalResponse
     */
    @POST("transaction/retrait_client.php")
    @FormUrlEncoded
    Call<WithdrawalResponse> retrait_client(@Field("cle") String cle,
                                            @Field("tel_call") String tel_call,
                                            @Field("tel_client") String tel_client,
                                            @Field("montant_") float montant_,
                                            @Field("pass_client") String pass_client);


    @POST("get/get_solde.php")
    @FormUrlEncoded
    Call<BalanceResponse> get_solde(@Field("cle") String cle,
                                    @Field("tel") String tel);


    /**
     * permet d'enregister un client
     *
     * @param cle
     * @param nom
     * @param prenom
     * @param cni_start_date
     * @param cni_end_date
     * @param sex
     * @param num_cni
     * @param pass
     * @param email
     * @param tel
     * @param role
     * @param photo_1
     * @param photo_2
     * @return RegisterResponse
     */
    @Multipart
    @POST("utilisateur/add_client.php")
    Call<RegisterResponse> add_client(@Part("cle") RequestBody cle,
                                      @Part("nom") RequestBody nom,
                                      @Part("prenom") RequestBody prenom,
                                      @Part("cni_start_date") RequestBody cni_start_date,
                                      @Part("cni_end_date") RequestBody cni_end_date,
                                      @Part("sex") RequestBody sex,
                                      @Part("num_cni") RequestBody num_cni,
                                      @Part("pass") RequestBody pass,
                                      @Part("email") RequestBody email,
                                      @Part("tel") RequestBody tel,
                                      @Part("role") RequestBody role,
                                      @Part MultipartBody.Part photo_1,
                                      @Part MultipartBody.Part photo_2);


    /**
     * Liste des transactions des utilisateurs
     *
     * @param cle
     * @param tel
     * @return HomeHistorical
     */
    @POST("get/get_hist.php")
    @FormUrlEncoded
    Call<Historical> get_hist(@Field("cle") String cle,
                              @Field("tel") String tel);


    /**
     * Liste des transactions des callbox
     *
     * @param cle
     * @param tel
     * @return
     */
    @POST("get/get_hist_cb.php")
    @FormUrlEncoded
    Call<RegisterResponse> get_hist_cb(@Field("cle") String cle,
                                       @Field("tel") String tel);


    /**
     * permet de ressortir le total des envois
     *
     * @param cle
     * @param tel
     * @return TotalSend
     */
    @POST("get/get_total_env.php")
    @FormUrlEncoded
    Call<TotalSend> get_total_env(@Field("cle") String cle,
                                  @Field("tel") String tel);


    /**
     * permet de ressortir le total des recharges
     *
     * @param cle
     * @param tel
     * @return TotalSend
     */
    @POST("get/get_total_rech.php")
    @FormUrlEncoded
    Call<TotalSend> get_total_rech(@Field("cle") String cle,
                                   @Field("tel") String tel);


    /**
     * permet de charger tous les roles contenus dans la BD
     *
     * @param cle
     * @return
     */
    @POST("get/get_role.php")
    @FormUrlEncoded
    Call<HomeRoles> get_role(@Field("cle") String cle);

}
