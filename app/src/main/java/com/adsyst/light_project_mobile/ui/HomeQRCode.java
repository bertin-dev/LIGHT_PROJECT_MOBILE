package com.adsyst.light_project_mobile.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.adsyst.light_project_mobile.config.AESUtils;
import com.adsyst.light_project_mobile.config.Global;
import com.adsyst.light_project_mobile.local_storage.provider.PrefManager;
import com.adsyst.light_project_mobile.local_translation.LocaleHelper;
import com.adsyst.light_project_mobile.ui.fragments.FragmentReadQRCode;
import com.adsyst.light_project_mobile.ui.fragments.FragmentShowQRCode;
import com.adsyst.light_project_mobile.web_service.ApiService;
import com.adsyst.light_project_mobile.web_service.RetrofitBuilder;
import com.adsyst.light_project_mobile.web_service.model.WithdrawalResponse;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.material.tabs.TabLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeQRCode extends AppCompatActivity {

    private static final String TAG = "HomeQRCode";
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Context context;
    private AlertDialog.Builder build_error;
    private ProgressDialog progressDialog;
    private AwesomeValidation validator;
    private Call<WithdrawalResponse> call;
    private ApiService service;
    private PrefManager prf;
    private String myPhone;
    private String phoneDecrypt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_q_r_code);

        initView();
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
        build_error = new AlertDialog.Builder(context);
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        service = RetrofitBuilder.createService(ApiService.class);

        prf = new PrefManager(context);
        if(!prf.getString("key_phone").equalsIgnoreCase("")){
            myPhone = prf.getString("key_phone");
        }

        viewPager = findViewById(R.id.viewPager);
        addTabs(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager( viewPager );

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 1:
                        IntentIntegrator intentIntegrator = new IntentIntegrator(HomeQRCode.this);
                        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                        intentIntegrator.setCameraId(0);
                        intentIntegrator.setOrientationLocked(false);
                        intentIntegrator.setPrompt(getString(R.string.lightScan));
                        intentIntegrator.setBeepEnabled(true);
                        intentIntegrator.setBarcodeImageEnabled(true);
                        intentIntegrator.initiateScan();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 1:
                        IntentIntegrator intentIntegrator = new IntentIntegrator(HomeQRCode.this);
                        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                        intentIntegrator.setCameraId(0);
                        intentIntegrator.setOrientationLocked(false);
                        intentIntegrator.setPrompt(getString(R.string.lightScan));
                        intentIntegrator.setBeepEnabled(true);
                        intentIntegrator.setBarcodeImageEnabled(true);
                        intentIntegrator.initiateScan();
                }
            }
        });
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



    private void addTabs(ViewPager viewPager) {
        ViewPagerAdapterQRCode adapter = new ViewPagerAdapterQRCode(getSupportFragmentManager());
        adapter.addFrag(new FragmentShowQRCode(), getString(R.string.monCode));
        adapter.addFrag(new FragmentReadQRCode(), getString(R.string.scanner));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        IntentResult scanResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, intent);
        // handle scan result

        if(scanResult != null && scanResult.getContents() != null){
            try {
                phoneDecrypt = AESUtils.decrypt(scanResult.getContents());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.w(TAG, "onActivityResult-------------: "+ phoneDecrypt );
            openDialog(phoneDecrypt);
        }
    }




    private void openDialog(String tel_callbox) {

        View view = LayoutInflater.from(context).inflate(R.layout.layout_dialog_withdrawal, null);
        EditText montant = (EditText) view.findViewById(R.id.edit_montant);
        EditText pass = (EditText) view.findViewById(R.id.edit_password);
        Button btnWithdrawal = (Button) view.findViewById(R.id.btnWithdrawal);

        btnWithdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!pass.getText().toString().equalsIgnoreCase("")){

                    String mdp_client = pass.getText().toString().trim();
                    String amount_client = montant.getText().toString().trim();

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
                        withdrawal(tel_callbox, myPhone, mdp_client, amount_client);
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



    private void withdrawal(String tel_callbox, String tel_client, String password, String amount) {
        call = service.retrait_client(Global.API_KEY, tel_callbox, tel_client, Float.parseFloat(amount), password);
        call.enqueue(new Callback<WithdrawalResponse>() {
            @Override
            public void onResponse(@NonNull Call<WithdrawalResponse> call, Response<WithdrawalResponse> response) {
                Log.w(TAG, "onResponse: " + response);
                progressDialog.dismiss();
                assert response.body() != null;
                if(response.body().getMessage() != null){
                    Global.response(context, getString(R.string.retraitqrcode), response.body().getMessage(), R.drawable.code_qr_bleu);
                }
            }
            @Override
            public void onFailure(Call<WithdrawalResponse> call, Throwable t) {
                Log.w(TAG, "onFailure " + t.getMessage());
                progressDialog.dismiss();
                Global.response(context, getString(R.string.retraitqrcode1), t.getMessage(), R.drawable.ic_baseline_cancel_presentation_24);
            }
        });
    }



    private class ViewPagerAdapterQRCode extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapterQRCode(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
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


    /**
     * onDestroy() methode Callback qui permet de détruire une activity et libérer l'espace mémoire
     * @since 2020
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        if(call != null){
            call.cancel();
            call = null;
        }
    }
}





