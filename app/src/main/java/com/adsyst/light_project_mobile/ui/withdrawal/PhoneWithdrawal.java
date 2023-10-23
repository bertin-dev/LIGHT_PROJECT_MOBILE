package com.adsyst.light_project_mobile.ui.withdrawal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.adsyst.light_project_mobile.R;
import com.adsyst.light_project_mobile.config.Global;
import com.adsyst.light_project_mobile.local_storage.provider.PrefManager;
import com.adsyst.light_project_mobile.local_translation.LocaleHelper;
import com.adsyst.light_project_mobile.web_service.ApiService;
import com.adsyst.light_project_mobile.web_service.RetrofitBuilder;
import com.adsyst.light_project_mobile.web_service.model.WithdrawalResponse;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneWithdrawal extends AppCompatActivity {

    private static final String TAG = "PhoneWithdrawal";
    private Toolbar toolbar;
    private Context context;
    private ProgressDialog progressDialog;
    private AwesomeValidation validator;
    private Call<WithdrawalResponse> call;
    private ApiService service;
    private PrefManager prf;
    private AlertDialog.Builder build_error;

    @BindView(R.id.til_phone_callbox)
    TextInputLayout til_phone_callbox;
    @BindView(R.id.tie_phone_callbox)
    TextInputEditText tie_phone_callbox;

    @BindView(R.id.til_phone_client)
    TextInputLayout til_phone_client;
    @BindView(R.id.tie_phone_client)
    TextInputEditText tie_phone_client;

    @BindView(R.id.til_montant)
    TextInputLayout til_montant;
    @BindView(R.id.tie_montant)
    TextInputEditText tie_montant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_withdrawal);

        initView();
        setupRulesValidatForm();
    }

    private void setupRulesValidatForm() {
        til_phone_callbox.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135, 206, 250)));
        validator.addValidation(this, R.id.til_phone_callbox, RegexTemplate.NOT_EMPTY, R.string.phone_exp);

        til_phone_client.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135, 206, 250)));
        validator.addValidation(this, R.id.til_phone_client, RegexTemplate.NOT_EMPTY, R.string.phone_benef);

        til_montant.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135, 206, 250)));
        validator.addValidation(this, R.id.til_montant, RegexTemplate.NOT_EMPTY, R.string.insererMontant);
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.homeCodeQR));
        toolbar.setSubtitle(getString(R.string.subTitle));
        //toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //initialisation des objets qui seront manipulés
        ButterKnife.bind(this);
        context = this;
        progressDialog = new ProgressDialog(context);
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        service = RetrofitBuilder.createService(ApiService.class);
        build_error = new AlertDialog.Builder(context);
        prf = new PrefManager(context);
        if(!prf.getString("key_phone").equalsIgnoreCase("")){
            tie_phone_client.setText(prf.getString("key_phone"));
        }
    }

    /**
     * attachBaseContext(Context newBase) methode callback permet de verifier la langue au demarrage de la page login
     *
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



    /*                    GESTION DU MENU DROIT                  */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.valider_withdrawal)
    void valider_withdrawal(){

        if (!validatePhoneCallbox(til_phone_callbox) || !validatePhoneClient(til_phone_client) || !validateMontant(til_montant)) {
            return;
        }

        String phone_callbox = til_phone_callbox.getEditText().getText().toString();
        String phone_client = til_phone_client.getEditText().getText().toString();
        String montant = til_montant.getEditText().getText().toString();

        validator.clear();
        /*Action à poursuivre si tous les champs sont remplis*/
        if (validator.validate()) {
            openDialog(phone_callbox, phone_client, montant);
        }

    }


    private void openDialog(String tel_callbox, String tel_client, String montant) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_withdrawal_password, null);
        EditText pass = (EditText) view.findViewById(R.id.edit_password);
        Button btnWithdrawal = (Button) view.findViewById(R.id.btnWithdrawal);

        btnWithdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!pass.getText().toString().equalsIgnoreCase("")){

                    String mdp_client = pass.getText().toString().trim();
                    /*Action à poursuivre si tous les champs sont remplis*/
                    if(validator.validate()){
                        //********************DEBUT***********
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // On ajoute un message à notre progress dialog
                                progressDialog.setMessage(getString(R.string.connexionserver));
                                // On donne un titre à notre progress dialog
                                progressDialog.setTitle(getString(R.string.attenteReponseServer));
                                // On spécifie le style
                                //  progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                // On affiche notre message
                                progressDialog.show();
                                //build.setPositiveButton("ok", new View.OnClickListener()
                            }
                        });
                        //*******************FIN*****
                        phoneWithdrawal(tel_callbox, tel_client, mdp_client, montant);
                    }

                } else{
                    Toast.makeText(context, getString(R.string.veuillezInsererPassword), Toast.LENGTH_SHORT).show();
                }

            }
        });
        build_error.setCancelable(true);
        build_error.setView(view);
        build_error.show();
    }

    private void phoneWithdrawal(String tel_callbox, String tel_client, String password, String amount) {
        call = service.retrait_client(Global.API_KEY, tel_callbox, tel_client, Float.parseFloat(amount), password);
        call.enqueue(new Callback<WithdrawalResponse>() {
            @Override
            public void onResponse(@NonNull Call<WithdrawalResponse> call, @NonNull Response<WithdrawalResponse> response) {
                Log.w(TAG, "onResponse: " + response);
                progressDialog.dismiss();

                assert response.body() != null;
                if(response.body().getMessage() != null){
                    Global.response(context, getString(R.string.retraitTel), response.body().getMessage(), R.drawable.call_received_24);
                }
            }
            @Override
            public void onFailure(@NonNull Call<WithdrawalResponse> call, Throwable t) {
                Log.w(TAG, "onFailure " + t.getMessage());
                progressDialog.dismiss();
                Global.response(context, getString(R.string.retraitTel), t.getMessage(), R.drawable.ic_baseline_cancel_presentation_24);

            }
        });
    }


    /**
     * validatePhoneCallbox() méthode permettant de verifier si le numéro de téléphone inséré est valide
     *
     * @param til_telephone1
     * @return Boolean
     * @since 2019
     */
    private Boolean validatePhoneCallbox(TextInputLayout til_telephone1) {
        String myTel = til_telephone1.getEditText().getText().toString().trim();
        if (myTel.isEmpty()) {
            til_telephone1.setError(getString(R.string.insererPhoneCallbox));
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
     * validatePhoneClient() méthode permettant de verifier si le numéro de téléphone inséré est valide
     *
     * @param til_telephone1
     * @return Boolean
     * @since 2019
     */
    private Boolean validatePhoneClient(TextInputLayout til_telephone1) {
        String myTel = til_telephone1.getEditText().getText().toString().trim();
        if (myTel.isEmpty()) {
            til_telephone1.setError(getString(R.string.insererPhoneClient));
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
     * validateMontant() méthode permettant de verifier si le montant inséré est valide
     * @param til_montant1
     * @return Boolean
     * @since 2019
     * */
    private boolean validateMontant(TextInputLayout til_montant1){
        String montant = til_montant1.getEditText().getText().toString().trim();
        if(montant.isEmpty()){
            til_montant1.setError(getString(R.string.insererMontant));
            return false;
        } else {
            til_montant1.setError(null);
            return true;
        }
    }
}