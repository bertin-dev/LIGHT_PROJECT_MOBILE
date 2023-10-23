package com.adsyst.light_project_mobile.ui.register;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.adsyst.light_project_mobile.R;
import com.adsyst.light_project_mobile.config.Global;
import com.adsyst.light_project_mobile.local_translation.LocaleHelper;
import com.adsyst.light_project_mobile.ui.historical.AdapterHistorical;
import com.adsyst.light_project_mobile.web_service.ApiService;
import com.adsyst.light_project_mobile.web_service.RetrofitBuilder;
import com.adsyst.light_project_mobile.web_service.model.Historical;
import com.adsyst.light_project_mobile.web_service.model.HomeRoles;
import com.adsyst.light_project_mobile.web_service.model.RegisterResponse;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    private static final String TAG = "Register";
    int selectedYear = 2021;
    int selectedMonth = 6;
    int selectedDayOfMonth = 10;
    //recuperation des vues
    @BindView(R.id.til_nom)
    TextInputLayout til_nom;
    @BindView(R.id.tie_nom)
    TextInputEditText tie_nom;
    @BindView(R.id.til_prenom)
    TextInputLayout til_prenom;
    @BindView(R.id.tie_prenom)
    TextInputEditText tie_prenom;
    @BindView(R.id.til_numeroTel)
    TextInputLayout til_numeroTel;
    @BindView(R.id.tie_numeroTel)
    TextInputEditText tie_numeroTel;
    @BindView(R.id.til_cni)
    TextInputLayout til_cni;
    @BindView(R.id.tie_cni)
    TextInputEditText tie_cni;
    @BindView(R.id.sexe)
    Spinner sexe;
    @BindView(R.id.role)
    Spinner role;
    @BindView(R.id.til_password)
    TextInputLayout til_password;
    @BindView(R.id.tie_password)
    TextInputEditText tie_password;
    @BindView(R.id.til_email)
    TextInputLayout til_email;
    @BindView(R.id.tie_email)
    TextInputEditText tie_email;
    private Toolbar toolbar;
    private ProgressDialog progressDialog;
    private Context context;
    private String[] sexe1;
    /* Déclaration des objets liés à la communication avec le web service*/
    private ApiService service;
    private Call<HomeRoles> call;
    private List<String> roleList = new ArrayList<>();
    private AwesomeValidation validator;
    private Button button_delivrance_date;
    private Button button_exp_date;
    private EditText editText_delivrance_date;
    private EditText editText_exp_date;
    private String roleSelected;

    private static boolean isValid(String str) {
        boolean isValid = false;
        String expression = "^[a-z_A-Z0-9éèê'çà ]*$";
        CharSequence inputStr = str;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
        loadingRoles(Global.API_KEY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.register));
        toolbar.setSubtitle(getString(R.string.subTitle));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initItem();
        setupRulesValidatForm();
    }

    private void initItem() {
        context = this;
        //récupération des vues en lien dans notre activity
        ButterKnife.bind(this);
        service = RetrofitBuilder.createService(ApiService.class);
        validator = new AwesomeValidation(ValidationStyle.BASIC);

        progressDialog = new ProgressDialog(context);

        //Vérification si la langue du telephone est en Francais
        if (Locale.getDefault().getLanguage().contentEquals("fr")) {
            // Initializing a String Array
            sexe1 = new String[]{
                    "Masculin",
                    "Feminin"
            };
        } else {
            // Initializing a String Array
            sexe1 = new String[]{
                    "Male",
                    "Feminine"
            };
        }

        // Initializing an ArrayAdapter gender
        ArrayAdapter<String> spinnerArrayAdapter3 = new ArrayAdapter<String>(
                this, R.layout.spinner_item, sexe1);
        spinnerArrayAdapter3.setDropDownViewResource(android.R.layout.simple_list_item_1);
        sexe.setAdapter(spinnerArrayAdapter3);


        //selection des roles(utilisateur, accepteur, administrateur, agent)
        role.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                roleSelected = role.getSelectedItem().toString().toLowerCase().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        button_delivrance_date = findViewById(R.id.button_delivrance_date);
        editText_delivrance_date = findViewById(R.id.editText_delivrance_date);

        button_exp_date = findViewById(R.id.button_exp_date);
        editText_exp_date = findViewById(R.id.editText_exp_date);

        button_delivrance_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Date Select Listener.
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        editText_delivrance_date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                };

                // Create DatePickerDialog (Spinner Mode):
                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        dateSetListener, selectedYear, selectedMonth, selectedDayOfMonth);

                // Show
                datePickerDialog.show();
            }
        });
        button_exp_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Date Select Listener.
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        editText_exp_date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                };

                // Create DatePickerDialog (Spinner Mode):
                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        dateSetListener, selectedYear, selectedMonth, selectedDayOfMonth);

                // Show
                datePickerDialog.show();
            }
        });

    }

    /**
     * setupRulesValidatForm() méthode permettant de changer la couleur des champs de saisie en cas d'érreur et vérifi si les champs de saisie sont vides
     *
     * @since 2020
     */
    private void setupRulesValidatForm() {

        //coloration des champs lorsqu'il y a erreur
        til_nom.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135, 206, 250)));
        til_prenom.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135, 206, 250)));
        til_numeroTel.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135, 206, 250)));
        til_cni.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135, 206, 250)));
        til_email.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135, 206, 250)));
        til_password.setErrorTextColor(ColorStateList.valueOf(Color.rgb(135, 206, 250)));

        validator.addValidation(this, R.id.til_nom, RegexTemplate.NOT_EMPTY, R.string.veuillezInsererNom);
        validator.addValidation(this, R.id.til_prenom, RegexTemplate.NOT_EMPTY, R.string.veuillezInsererPrenom);
        validator.addValidation(this, R.id.til_numeroTel, RegexTemplate.NOT_EMPTY, R.string.veuillezInsererTelephone);
        validator.addValidation(this, R.id.til_numeroTel, RegexTemplate.TELEPHONE, R.string.verifierNumero);
        validator.addValidation(this, R.id.til_cni, RegexTemplate.NOT_EMPTY, R.string.veuillezInsererPJ);
        validator.addValidation(this, R.id.til_email, RegexTemplate.NOT_EMPTY, R.string.veuillezInsererAdresse);
        validator.addValidation(this, R.id.til_password, RegexTemplate.NOT_EMPTY, R.string.veuillezInsererNumCompte);
        validator.addValidation(this, R.id.til_email, Patterns.EMAIL_ADDRESS, R.string.invalidCararatere1);

        validator.addValidation(this, R.id.editText_delivrance_date, RegexTemplate.NOT_EMPTY, R.string.insertDateDeliv);
        validator.addValidation(this, R.id.editText_exp_date, RegexTemplate.NOT_EMPTY, R.string.insertDateExp);

        //validator.addValidation(this, R.id.til_numCarte, Patterns.PHONE, R.string.verifierNumero);
        //validator.addValidation(this, R.id.til_numCarte, "[a-zA-Z0-9]{6,0}", R.string.verifierNumero);
    }

    @OnClick(R.id.btnRegister)
    void register() {

        if (!validateNom(til_nom) | !validatePrenom(til_prenom) | !validateTel(til_numeroTel) | !validateCni(til_cni)
                | !validateEmail(til_email) | !validateRole(role) | !validatePassword(til_password) ||
                !validateDateDeliv(editText_delivrance_date) || !validateDateExp(editText_exp_date)) {
            return;
        }
        String nom = til_nom.getEditText().getText().toString().trim().toLowerCase();
        String prenom = til_prenom.getEditText().getText().toString().trim().toLowerCase();
        String telephone = til_numeroTel.getEditText().getText().toString().trim();
        String cni = til_cni.getEditText().getText().toString().trim().toLowerCase();
        String email = til_email.getEditText().getText().toString().trim().toLowerCase();
        String password = til_password.getEditText().getText().toString().trim().toLowerCase();
        String genre = sexe.getSelectedItem().toString().trim().toLowerCase().equalsIgnoreCase("masculin") || sexe.getSelectedItem().toString().trim().toLowerCase().equalsIgnoreCase("male") ? "MASCULIN" : "FEMININ";
        String start = editText_delivrance_date.getText().toString().trim();
        String end = editText_exp_date.getText().toString().trim();
        validator.clear();

        if (validator.validate()) {
            Intent intent = new Intent(getApplicationContext(), RegisterCniCard.class);
            intent.putExtra("NOM", nom);
            intent.putExtra("PRENOM", prenom);
            intent.putExtra("GENRE", genre);
            intent.putExtra("TELEPHONE", telephone);
            intent.putExtra("CNI", cni);
            intent.putExtra("EMAIL", email);
            intent.putExtra("PASSWORD", password);
            intent.putExtra("START", start);
            intent.putExtra("END", end);
            intent.putExtra("ROLE", roleSelected);
            startActivity(intent);
        }

    }

    /**
     * validatePassword() méthode permettant de verifier si le numéro de compte inséré est valide
     *
     * @param til_num_compte1
     * @return Boolean
     * @since 2019
     */
    private Boolean validatePassword(TextInputLayout til_num_compte1) {
        String my_numCompte = til_num_compte1.getEditText().getText().toString().trim();
        if (my_numCompte.isEmpty()) {
            til_num_compte1.setError(getString(R.string.veuillezInsererPassword));
            til_num_compte1.requestFocus();
            return false;
        } else if (my_numCompte.length() < 5) {
            til_num_compte1.setError(getString(R.string.mdpCourt));
            til_num_compte1.requestFocus();
            return false;
        } else {
            til_num_compte1.setError(null);
            return true;
        }
    }

    /**
     * validateNom() méthode permettant de verifier si le nom inséré est valide
     *
     * @param til_nom
     * @return Boolean
     * @since 2019
     */
    private Boolean validateNom(TextInputLayout til_nom) {
        String my_name = til_nom.getEditText().getText().toString().trim();
        if (my_name.isEmpty()) {
            til_nom.setError(getString(R.string.veuillezInserer) + " " + getString(R.string.nom));
            til_nom.requestFocus();
            return false;
        } else if (!isValid(my_name)) {
            til_nom.setError(getString(R.string.votre) + " " + getString(R.string.nom) + " " + getString(R.string.invalidCararatere));
            til_nom.requestFocus();
            return false;
        } else {
            til_nom.setError(null);
            return true;
        }
    }

    private Boolean validateDateDeliv(EditText myDate) {
        String date = myDate.getText().toString().trim();
        if (date.isEmpty()) {
            editText_delivrance_date.setError(getString(R.string.veuillezInserer) + " " + getString(R.string.dateDelivrance));
            editText_delivrance_date.requestFocus();
            return false;
        } else {
            editText_delivrance_date.setError(null);
            return true;
        }
    }

    private Boolean validateDateExp(EditText myDate) {
        String date = myDate.getText().toString().trim();
        if (date.isEmpty()) {
            editText_exp_date.setError(getString(R.string.veuillezInserer) + " " + getString(R.string.dateExp));
            editText_exp_date.requestFocus();
            return false;
        } else {
            editText_exp_date.setError(null);
            return true;
        }
    }

    /**
     * validateStatut() méthode permettant de verifier si le statut listé est chargé
     *
     * @param status
     * @return Boolean
     * @since 2019
     */
    private Boolean validateRole(Spinner status) {

        if (status.getCount() == 0) {
            Toast.makeText(this, getString(R.string.veuillezInserer) + " " + getString(R.string.AlertStatutListDeroulante), Toast.LENGTH_SHORT).show();
            status.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * validatePrenom() méthode permettant de verifier si le prenom inséré est valide
     *
     * @param til_prenom
     * @return Boolean
     * @since 2019
     */
    private Boolean validatePrenom(TextInputLayout til_prenom) {
        String my_surname = til_prenom.getEditText().getText().toString().trim();
        if (my_surname.isEmpty()) {
            til_prenom.setError(getString(R.string.veuillezInserer) + " " + getString(R.string.prenom));
            til_prenom.requestFocus();
            return false;
        } else if (!isValid(my_surname)) {
            til_prenom.setError(getString(R.string.votre) + " " + getString(R.string.prenom) + " " + getString(R.string.invalidCararatere));
            til_prenom.requestFocus();
            return false;
        } else {
            til_prenom.setError(null);
            return true;
        }
    }

    /**
     * validateCni() méthode permettant de verifier si le cni inséré est valide
     *
     * @param til_cni
     * @return Boolean
     * @since 2019
     */
    private Boolean validateCni(TextInputLayout til_cni) {
        String my_cni = til_cni.getEditText().getText().toString().trim();
        if (my_cni.isEmpty()) {
            til_cni.setError(getString(R.string.veuillezInserer) + " " + getString(R.string.numeroDe) + " CNI");
            til_cni.requestFocus();
            return false;
        } else if (!isValid(my_cni)) {
            til_cni.setError(getString(R.string.votre) + " CNI" + " " + getString(R.string.invalidCararatere));
            til_cni.requestFocus();
            return false;
        } else {
            til_cni.setError(null);
            return true;
        }
    }

    /**
     * validateEmail() méthode permettant de verifier si le cni inséré est valide
     *
     * @param til_adress
     * @return Boolean
     * @since 2019
     */
    private Boolean validateEmail(TextInputLayout til_adress) {
        String my_email = til_adress.getEditText().getText().toString().trim();
        if (my_email.isEmpty()) {
            til_adress.setError(getString(R.string.veuillezInserer) + " " + getString(R.string.email));
            til_adress.requestFocus();
            return false;
        } /*else if (Patterns.EMAIL_ADDRESS.matcher(my_email).matches()) {
            til_adress.setError(getString(R.string.votre) + " " + getString(R.string.email) + " " + getString(R.string.invalidCararatere));
            til_adress.requestFocus();
            return false;
        }*/ else {
            til_adress.setError(null);
            return true;
        }
    }

    /**
     * validateTel() méthode permettant de verifier si le telephone inséré est valide
     *
     * @param til_tel
     * @return Boolean
     * @since 2019
     */
    private Boolean validateTel(TextInputLayout til_tel) {
        String my_phone = til_tel.getEditText().getText().toString().trim();
        if (my_phone.isEmpty()) {
            til_tel.setError(getString(R.string.insererTelephone));
            til_tel.requestFocus();
            return false;
        } else if (my_phone.length() < 9) {
            til_tel.setError(getString(R.string.telephoneCourt));
            til_tel.requestFocus();
            return false;
        } else {
            til_tel.setError(null);
            return true;
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


    private void loadingRoles(String key) {

        call = service.get_role(key);
        call.enqueue(new Callback<HomeRoles>() {
            @Override
            public void onResponse(Call<HomeRoles> call, Response<HomeRoles> response) {
                Log.w(TAG, "onResponse: " + response.body());

                assert response.body() != null;
                if (response.body().getResultat() != null) {
                    roleList.clear();

                    List<HomeRoles.Roles> list = response.body().getResultat();
                    for(int i = 0; i<list.size(); i++){
                        roleList.add(list.get(i).getRoles());
                    }

                    ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, roleList);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                    role.setAdapter(spinnerAdapter);

                } else {
                    Log.w(TAG, "onResponse: une erreur est survenue lors du chargement des rôles ");
                }

            }

            @Override
            public void onFailure(Call<HomeRoles> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (call != null) {
            call.cancel();
            call = null;
        }
    }

}