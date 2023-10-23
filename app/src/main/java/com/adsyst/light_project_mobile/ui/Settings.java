package com.adsyst.light_project_mobile.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.adsyst.light_project_mobile.BuildConfig;
import com.adsyst.light_project_mobile.R;
import com.adsyst.light_project_mobile.local_translation.LocaleHelper;
import com.adsyst.light_project_mobile.ui.starting.SplashScreen;

import java.util.Calendar;
import java.util.Locale;

public class Settings extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listAllSetting;
    private String [] config;
    private Dialog myDialog;
    private TextView txtclose, titleLanguage, myVersion, copy;
    private RadioButton radioFr, radioEn;
    private  Boolean checked;
    String currentLanguage = (Locale.getDefault().getLanguage().contentEquals("fr")) ? "fr" : "en", currentLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.configuration));
        toolbar.setSubtitle(getString(R.string.subTitle));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        listAllSetting = (ListView)findViewById(R.id.listAllSetting);
        if(Locale.getDefault().getLanguage().contentEquals("fr")) {
            // Initializing a String Array
            config = new String[]{
                    "Rechercher les mises à jour",
                    "Langue par défaut",
                    "Vider la cache",
                    "Á propos de l'Application"
            };
        } else {
            // Initializing a String Array
            config = new String[]{
                    "Check for updates",
                    "Default language",
                    "Empty cache",
                    "About the App"
            };
        }

        // Initialize an array adapter
        ArrayAdapter<String> adapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, config){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Cast the list view each item as text view
                TextView item = (TextView) super.getView(position,convertView,parent);
                // Set the typeface/font for the current item
                //item.setTypeface(mTypeface);

                // Set the list view item's text color
                item.setTextColor(getResources().getColor(R.color.primary_color));

                // Set the item text style to bold
                item.setTypeface(item.getTypeface(), Typeface.BOLD);

                // Change the item text size
                item.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);

                // return the view
                return item;
            }
        };
        // Data bind the list view with array adapter items
        listAllSetting.setAdapter(adapter);

        listAllSetting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position){
                    //rechercher les mises à jours
                    case 0:
                        try {
                            Intent viewIntent =
                                    new Intent("android.intent.action.VIEW",
                                            Uri.parse("https://play.google.com/store/apps/details?id=com.adsyst.light_project&hl=fr"));
                            startActivity(viewIntent);
                        }catch(Exception e) {
                            Toast.makeText(getApplicationContext(),"Unable to Connect Try Again...",
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        //Change language
                        myDialog = new Dialog(Settings.this);
                        myDialog.setContentView(R.layout.layout_dialog_change_language);

                        radioFr = (RadioButton) myDialog.findViewById(R.id.radioFr);
                        radioEn = (RadioButton) myDialog.findViewById(R.id.radioEn);
                        titleLanguage = (TextView) myDialog.findViewById(R.id.titleLanguage);

                        radioFr.setChecked(true);
                        radioFr.setText(getString(R.string.francais));
                        radioEn.setText(getString(R.string.anglais));
                        titleLanguage.setText(getString(R.string.defaultlangue));


                        if(Locale.getDefault().getLanguage().contentEquals("fr")) {
                            radioFr.setChecked(true);
                        } else {
                            radioEn.setChecked(true);
                        }



                        radioFr.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checked = ((RadioButton) v).isChecked();
                                if(checked){
                                    setLocale("fr");
                                    myDialog.dismiss();
                                }

                            }
                        });
                        radioEn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checked = ((RadioButton) v).isChecked();
                                if(checked){
                                    setLocale("en");
                                    myDialog.dismiss();
                                }
                            }
                        });

                        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
                        txtclose.setText("X");
                        txtclose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialog.dismiss();
                            }
                        });
                        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        myDialog.show();
                        break;
                    case 2:
                        //Vider la cache
                        Toast.makeText(Settings.this, "Cache effacée avec succès.", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        //Apropos Application
                        myDialog = new Dialog(Settings.this);
                        myDialog.setContentView(R.layout.layout_dialog_apropos);
                        myVersion = (TextView) myDialog.findViewById(R.id.myVersion);
                        copy = (TextView) myDialog.findViewById(R.id.copy);
                        myVersion.setText("Version " + BuildConfig.VERSION_NAME);
                        copy.setText("© " + Calendar.getInstance().get(Calendar.YEAR) + " LIGHT");

                        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
                        txtclose.setText("X");
                        txtclose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialog.dismiss();
                            }
                        });
                        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        myDialog.show();
                        break;
                }
            }
        });

    }


    private void setLocale(String localeName) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Locale.Helper.Selected.Language", localeName);
        editor.apply();

        if (!localeName.equals(currentLanguage)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Locale locale = new Locale(localeName);
                Locale.setDefault(locale);

                Configuration configuration = this.getResources().getConfiguration();
                configuration.setLocale(locale);
                configuration.setLayoutDirection(locale);
                createConfigurationContext(configuration);
            } else {

                Locale locale = new Locale(localeName);
                Locale.setDefault(locale);

                Resources resources = getResources();

                Configuration configuration = resources.getConfiguration();
                configuration.locale = locale;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    configuration.setLayoutDirection(locale);
                }

                resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            }

            Intent refresh = new Intent(this, SplashScreen.class);
            refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);
            finish();

        } else{
            Toast.makeText(Settings.this, getString(R.string.selectedLanguage), Toast.LENGTH_SHORT).show();
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