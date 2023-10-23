package com.adsyst.light_project_mobile.ui.authentication;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.adsyst.light_project_mobile.ui.starting.MainActivity;
import com.adsyst.light_project_mobile.R;
import com.adsyst.light_project_mobile.config.Global;
import com.adsyst.light_project_mobile.local_storage.provider.PrefManager;
import com.adsyst.light_project_mobile.local_translation.LocaleHelper;
import com.adsyst.light_project_mobile.web_service.ApiService;
import com.adsyst.light_project_mobile.web_service.RetrofitBuilder;
import com.adsyst.light_project_mobile.web_service.model.LoginResponse;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.material.textfield.TextInputLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginStep2 extends AppCompatActivity {

    private static final String TAG = "LoginStep2";
    @BindView(R.id.til_password)
    TextInputLayout til_password;
    private String pass_crypt;
    private String phone;
    @BindView(R.id.pgbar)
    ProgressBar pgbar;
    @BindView(R.id.btnloginStep2)
    Button btnloginStep2;
    private Context context;
    /* Déclaration des objets liés à la communication avec le web service*/
    private ApiService service;
    private AwesomeValidation validator;
    private Call<LoginResponse> call;
    private PrefManager prf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_step2);
        /*Appels de toutes les méthodes qui seront utilisées*/
        initView();
        setupRulesValidatForm();
    }

    private void initView() {
        /*Initialisation de tous les objets qui seront manipulés*/
        ButterKnife.bind(this);
        service = RetrofitBuilder.createService(ApiService.class);
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        context = this;
        prf = new PrefManager(context);
        Intent intent = getIntent();
        pass_crypt = intent.getStringExtra("pass_crypt");
        phone = intent.getStringExtra("phone_number");
    }


    @OnClick(R.id.btnloginStep2)
    void loginStep2(){

        if (!validatePassword(til_password)) {
            return;
        }

        String psw = til_password.getEditText().getText().toString();
        validator.clear();
        /*Action à poursuivre si tous les champs sont remplis*/
        if (validator.validate()) {
            btnloginStep2.setEnabled(false);
            pgbar.setVisibility(View.VISIBLE);
            Authentication(phone ,psw, pass_crypt);
        }

    }

    private void Authentication(String phone, String pass, String pass_crypt) {

        call = service.auth_client(Global.API_KEY, phone, pass, pass_crypt);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.w(TAG, "onResponse: " + response.body() );

                LoginResponse loginResponse = response.body();
                if(loginResponse != null){
                    //successful response
                    if(loginResponse.getMessage() == null){

                        if(!loginResponse.getResultat().isEmpty()){

                            if (prf.getString("key_tel_client").equals("") || !prf.getString("key_tel_client").equals(phone)) {
                             //store data
                                for(int i=0; i<loginResponse.getResultat().size(); i++){
                                    prf.setString("key_id_user", loginResponse.getResultat().get(i).getId_user());
                                    prf.setString("key_nom_client", loginResponse.getResultat().get(i).getNom_client());
                                    prf.setString("key_prenom_client", loginResponse.getResultat().get(i).getPrenom_client());
                                    prf.setString("key_num_cni_client", loginResponse.getResultat().get(i).getNum_cni_client());
                                    prf.setString("key_tel_client", loginResponse.getResultat().get(i).getTel_client());
                                    prf.setString("key_pass_client", loginResponse.getResultat().get(i).getPass_client());
                                    prf.setString("key_sex_client", loginResponse.getResultat().get(i).getSex_client());
                                    prf.setString("key_email_client", loginResponse.getResultat().get(i).getEmail_client());
                                    prf.setString("key_cni_start_date", loginResponse.getResultat().get(i).getCni_start_date());
                                    prf.setString("key_cni_end_date", loginResponse.getResultat().get(i).getCni_end_date());
                                    prf.setString("key_id_role", loginResponse.getResultat().get(i).getId_role());
                                    prf.setString("key_id_cpt", loginResponse.getResultat().get(i).getId_cpt());
                                }
                                btnloginStep2.setEnabled(true);
                                pgbar.setVisibility(View.GONE);
                                Intent intent = new Intent(LoginStep2.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                btnloginStep2.setEnabled(true);
                                pgbar.setVisibility(View.GONE);
                                Intent intent = new Intent(LoginStep2.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }
                        btnloginStep2.setEnabled(true);
                        pgbar.setVisibility(View.GONE);
                    } else {
                        btnloginStep2.setEnabled(true);
                        pgbar.setVisibility(View.GONE);
                        Global.errorResponse(context, "login step 2", loginResponse.getMessage());
                    }

                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnloginStep2.setEnabled(true);
                pgbar.setVisibility(View.GONE);
                Log.w(TAG, "onFailure: " + t.getMessage() );
                Global.errorResponse(context, getString(R.string.echecAuthenfication), t.getMessage());
            }
        });
    }


    /**
     * validateTelephone() méthode permettant de verifier si le mot de passe inséré est valide
     *
     * @param til_password1
     * @return Boolean
     * @since 2019
     */
    private boolean validatePassword(TextInputLayout til_password1) {
        String psw = til_password1.getEditText().getText().toString().trim();
        if (psw.isEmpty()) {
            til_password1.setError(getString(R.string.insererPassword));
            til_password1.requestFocus();
            return false;
        } else if (psw.length() < 3) {
            til_password1.setError(getString(R.string.passwordCourt));
            til_password1.requestFocus();
            return false;
        } else {
            til_password1.setError(null);
            return true;
        }
    }

    /**
     * setupRulesValidatForm() méthode permettant de changer la couleur des champs de saisie en cas d'érreur et vérifi si les champs de saisie sont vides
     *
     * @since 2020
     */
    private void setupRulesValidatForm() {

        //coloration des champs lorsqu'il y a erreur
        til_password.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135, 206, 250)));
        validator.addValidation(this, R.id.til_password, RegexTemplate.NOT_EMPTY, R.string.insererPassword);
    }

    /**
     * attachBaseContext(Context newBase) methode callback permet de verifier la langue au demarrage de la page login
     * @param newBase
     * @since 2020
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(call != null){
            call.cancel();
            call = null;
        }
    }
}