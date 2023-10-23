package com.adsyst.light_project_mobile.ui.authentication;

import android.app.AlertDialog;
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

import com.adsyst.light_project_mobile.R;
import com.adsyst.light_project_mobile.config.Global;
import com.adsyst.light_project_mobile.local_storage.provider.PrefManager;
import com.adsyst.light_project_mobile.local_translation.LocaleHelper;
import com.adsyst.light_project_mobile.web_service.ApiService;
import com.adsyst.light_project_mobile.web_service.RetrofitBuilder;
import com.adsyst.light_project_mobile.web_service.model.PhoneResponse;
import com.adsyst.light_project_mobile.web_service.model.Resultat;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *   <b>Login est la classe représentant l'activity d'authentification.</b>
 *   <p>
 *   cette page permet à l'utilisateur de faire 3 chose:
 *   <ul>
 *   <li>s'authentifier</li>
 *   <li>s'auto-enregistrer</li>
 *   <li>Reinitialiser le mot de passe</li>
 *   </ul>
 *   </p>
 *
 * Classe Login qui hérite AppCompatActivity et implemente ModalDialog_PasswordForgot.ExampleDialogListener, ConnectivityReceiver.ConnectivityReceiverListener
 *
 * @see LoginStep1
 * @see AppCompatActivity
 *
 * @author Bertin-dev
 * @powered by smopaye sarl
 * @version 1.4.0
 * @since 2019
 * @Copyright 2019-2021
 */
public class LoginStep1 extends AppCompatActivity {

    private static final String TAG = "LoginStep1";
    @BindView(R.id.til_telephone)
    TextInputLayout til_telephone;
    @BindView(R.id.tie_telephone)
    TextInputEditText tie_telephone;
    @BindView(R.id.pgbar)
    ProgressBar pgbar;
    @BindView(R.id.btnloginStep1)
    Button btnloginStep1;

    private AlertDialog.Builder build_error;
    /* Déclaration des objets liés à la communication avec le web service*/
    private ApiService service;
    private AwesomeValidation validator;
    private Call<PhoneResponse> call;
    private Context context;
    private PrefManager prf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_step1);

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
        build_error = new AlertDialog.Builder(context);
        prf = new PrefManager(context);
        if(!prf.getString("key_phone").equalsIgnoreCase("")){
            tie_telephone.setText(prf.getString("key_phone"));
        }
    }

    /**
     * setupRulesValidatForm() méthode permettant de changer la couleur des champs de saisie en cas d'érreur et vérifi si les champs de saisie sont vides
     *
     * @since 2020
     */
    private void setupRulesValidatForm() {
        //coloration des champs lorsqu'il y a erreur
        til_telephone.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135, 206, 250)));
        validator.addValidation(this, R.id.til_telephone, RegexTemplate.NOT_EMPTY, R.string.verifierNumero);
    }


    @OnClick(R.id.btnloginStep1)
    void loginStep1(){

        if (!validateTelephone(til_telephone)) {
            return;
        }

        String numero = til_telephone.getEditText().getText().toString();
        validator.clear();
        /*Action à poursuivre si tous les champs sont remplis*/
        if (validator.validate()) {
            btnloginStep1.setEnabled(false);
            pgbar.setVisibility(View.VISIBLE);
            getPhoneNumber(numero);

        }

    }

    private void getPhoneNumber(String phone) {

        call = service.get_tel(Global.API_KEY, phone);
        call.enqueue(new Callback<PhoneResponse>() {
            @Override
            public void onResponse(Call<PhoneResponse> call, Response<PhoneResponse> response) {
                btnloginStep1.setEnabled(true);
                pgbar.setVisibility(View.GONE);
                Log.w(TAG, "onResponse: " + response.body() );

                PhoneResponse retour = response.body();
                if(retour != null){

                    if(retour.getResultat() != null){
                        List<Resultat> response_msg = retour.getResultat();
                            // Storing data
                            prf.setString("key_phone", response_msg.get(0).getTel_client());
                            prf.setString("key_message_connexion", response_msg.get(0).getPass_client());
                            Intent intent = new Intent(LoginStep1.this, LoginStep2.class);
                            intent.putExtra("pass_crypt", response_msg.get(0).getPass_client());
                            intent.putExtra("phone_number", response_msg.get(0).getTel_client());
                            startActivity(intent);

                    } else {
                        Global.errorResponse(context, "login step 1", retour.getMessage_connexion());
                    }

                }

                /*Converter<ResponseBody, PhoneResponse> converter = RetrofitBuilder.getRetrofit().responseBodyConverter(PhoneResponse.class, new Annotation[0]);
                PhoneResponse phoneResponse = converter.convert(response.body());*/
            }

            @Override
            public void onFailure(Call<PhoneResponse> call, Throwable t) {
                btnloginStep1.setEnabled(true);
                pgbar.setVisibility(View.GONE);
                Log.w(TAG, "onFailure: " + t.getMessage() );
                Global.errorResponse(context, getString(R.string.echecAuthenfication), t.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(call != null){
            call.cancel();
            call = null;
        }
    }

    /**
     * validateTelephone() méthode permettant de verifier si le numéro de téléphone inséré est valide
     *
     * @param til_telephone1
     * @return Boolean
     * @since 2019
     */
    private Boolean validateTelephone(TextInputLayout til_telephone1) {
        String myTel = til_telephone1.getEditText().getText().toString().trim();
        if (myTel.isEmpty()) {
            til_telephone1.setError(getString(R.string.insererTelephone));
            til_telephone1.requestFocus();
            return false;
        } else if (myTel.length() < 9) {
            til_telephone1.setError(getString(R.string.telephoneCourt));
            til_telephone1.requestFocus();
            return false;
        } else {
            til_telephone1.setError(null);
            return true;
        }
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
}