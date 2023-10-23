package com.adsyst.light_project_mobile.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.adsyst.light_project_mobile.R;
import com.adsyst.light_project_mobile.config.Global;
import com.adsyst.light_project_mobile.local_storage.provider.PrefManager;
import com.adsyst.light_project_mobile.local_translation.LocaleHelper;
import com.adsyst.light_project_mobile.web_service.ApiService;
import com.adsyst.light_project_mobile.web_service.RetrofitBuilder;
import com.adsyst.light_project_mobile.web_service.model.BalanceResponse;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Balance extends AppCompatActivity {

    private static final String TAG = "Balance";
    private Toolbar toolbar;
    @BindView(R.id.til_numeroTel)
    TextInputLayout til_numeroTel;
    @BindView(R.id.tie_numeroTel)
    TextInputEditText tie_numeroTel;

    private Context context;
    private ProgressDialog progressDialog;
    /* Déclaration des objets liés à la communication avec le web service*/
    private ApiService service;
    private AwesomeValidation validator;
    private Call<BalanceResponse> call;
    private PrefManager prf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solde);

        initView();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.solde));
        toolbar.setSubtitle(getString(R.string.subTitle));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        context = this;
        ButterKnife.bind(this);
        service = RetrofitBuilder.createService(ApiService.class);
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        progressDialog = new ProgressDialog(context);
        prf = new PrefManager(context);
        if(!prf.getString("key_phone").equalsIgnoreCase("")){
            tie_numeroTel.setText(prf.getString("key_phone"));
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
     * consulter() méthode permettant de démarrer l'opération de consultation
     * @since 2019
     * */
    @OnClick(R.id.btnBalance)
    void consulter(){
        if(!validateTelephone(til_numeroTel)){
            return;
        }

        validator.clear();

        /*Action à poursuivre si tous les champs sont remplis*/
        if(validator.validate()){
            String phone = til_numeroTel.getEditText().getText().toString();
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
            consulterSolde(phone);
        }
    }

    private void consulterSolde(String tel){

        call = service.get_solde(Global.API_KEY, tel);
        call.enqueue(new Callback<BalanceResponse>() {
            @Override
            public void onResponse(Call<BalanceResponse> call, Response<BalanceResponse> response) {
                Log.w(TAG, "SMOPAYE_SERVER onResponse: " + response);
                progressDialog.dismiss();
                BalanceResponse balanceResponse = response.body();

                if(balanceResponse != null){
                    if(balanceResponse.getMessage_connexion() != null && balanceResponse.getSolde() != null){
                        tie_numeroTel.setText("");
                        Global.successResponse(context, "Balance", getString(R.string.mysolde) + " " + balanceResponse.getSolde() + " Fcfa");
                    }
                }else {
                    Global.errorResponse(context, "Balance", "Votre compte n'existe pas");
                }

            }

            @Override
            public void onFailure(Call<BalanceResponse> call, Throwable t) {
                Log.w(TAG, "SMOPAYE_SERVER onFailure " + t.getMessage());
                progressDialog.dismiss();
                Global.errorResponse(context, "Balance", "Une erreur est survenue");
            }
        });
    }


    /**
     * onDestroy() methode Callback qui permet de détruire une activity et libérer l'espace mémoire
     * @since 2020
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(call != null){
            call.cancel();
            call = null;
        }
    }

}