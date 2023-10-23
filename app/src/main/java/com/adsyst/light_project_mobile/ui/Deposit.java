package com.adsyst.light_project_mobile.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.adsyst.light_project_mobile.R;
import com.adsyst.light_project_mobile.config.Global;
import com.adsyst.light_project_mobile.local_storage.provider.PrefManager;
import com.adsyst.light_project_mobile.local_translation.LocaleHelper;
import com.adsyst.light_project_mobile.web_service.ApiService;
import com.adsyst.light_project_mobile.web_service.RetrofitBuilder;
import com.adsyst.light_project_mobile.web_service.model.TransfertDepositResponse;
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

public class Deposit extends AppCompatActivity {

    private static final String TAG = "Deposit";
    private Toolbar toolbar;
    private Context context;


    @BindView(R.id.til_phone_exp)
    TextInputLayout til_phone_exp;
    @BindView(R.id.tie_phone_exp)
    TextInputEditText tie_phone_exp;

    @BindView(R.id.til_phone_beneficiaire)
    TextInputLayout til_phone_beneficiaire;
    @BindView(R.id.tie_phone_beneficiaire)
    TextInputEditText tie_phone_beneficiaire;

    @BindView(R.id.til_montant)
    TextInputLayout til_montant;
    @BindView(R.id.tie_montant)
    TextInputEditText tie_montant;

    /* Déclaration des objets liés à la communication avec le web service*/
    private ApiService service;
    private AwesomeValidation validator;
    private Call<TransfertDepositResponse> call;
    private ProgressDialog progressDialog;
    private PrefManager prf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        initView();
        setupRulesValidatForm();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.depot));
        toolbar.setSubtitle(getString(R.string.subTitle));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        context = this;
        ButterKnife.bind(this);
        service = RetrofitBuilder.createService(ApiService.class);
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);

        progressDialog = new ProgressDialog(this);

        prf = new PrefManager(context);
        if(!prf.getString("key_phone").equalsIgnoreCase("")){
            tie_phone_exp.setText(prf.getString("key_phone"));
        }
    }



    private void setupRulesValidatForm() {
        til_phone_exp.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135, 206, 250)));
        validator.addValidation(this, R.id.til_phone_exp, RegexTemplate.NOT_EMPTY, R.string.phone_exp);

        til_phone_beneficiaire.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135, 206, 250)));
        validator.addValidation(this, R.id.til_phone_beneficiaire, RegexTemplate.NOT_EMPTY, R.string.phone_benef);

        til_montant.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135, 206, 250)));
        validator.addValidation(this, R.id.til_montant, RegexTemplate.NOT_EMPTY, R.string.insererMontant);
    }


    @OnClick(R.id.valider_deposit)
    void valider_deposit(){

        if (!validatePhone(til_phone_exp) || !validatePhone(til_phone_beneficiaire) || !validateMontant(til_montant)) {
            return;
        }

        String phone_exp = til_phone_exp.getEditText().getText().toString();
        String phone_benef = til_phone_beneficiaire.getEditText().getText().toString();
        String montant = til_montant.getEditText().getText().toString();

        validator.clear();
        /*Action à poursuivre si tous les champs sont remplis*/
        if (validator.validate()) {


            //********************DEBUT***********
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // On ajoute un message à notre progress dialog
                    progressDialog.setMessage(getString(R.string.connexionserver));
                    // On donne un titre à notre progress dialog
                    progressDialog.setTitle(getString(R.string.encoursTelechargement));
                    // On spécifie le style
                    //  progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    // On affiche notre message
                    progressDialog.show();
                    //build.setPositiveButton("ok", new View.OnClickListener()
                }
            });
            //*******************FIN*****

            deposite(phone_exp, phone_benef, montant);

        }

    }

    private void deposite(String phone_exp, String phone_benef, String montant) {

        call = service.transfert(Global.API_KEY, phone_exp, phone_benef, Float.parseFloat(montant));
        call.enqueue(new Callback<TransfertDepositResponse>() {
            @Override
            public void onResponse(Call<TransfertDepositResponse> call, Response<TransfertDepositResponse> response) {
                progressDialog.dismiss();
                Log.w(TAG, "onResponse: " + response.body() );

            }

            @Override
            public void onFailure(Call<TransfertDepositResponse> call, Throwable t) {
                progressDialog.dismiss();
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });
    }


    /**
     * validatePhone() méthode permettant de verifier si le numéro de téléphone inséré est valide
     *
     * @param til_telephone1
     * @return Boolean
     * @since 2019
     */
    private Boolean validatePhone(TextInputLayout til_telephone1) {
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


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(call != null){
            call.cancel();
            call = null;
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
}