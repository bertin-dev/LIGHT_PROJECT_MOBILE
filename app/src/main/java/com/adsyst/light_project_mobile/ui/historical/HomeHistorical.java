package com.adsyst.light_project_mobile.ui.historical;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adsyst.light_project_mobile.R;
import com.adsyst.light_project_mobile.config.Global;
import com.adsyst.light_project_mobile.local_storage.provider.PrefManager;
import com.adsyst.light_project_mobile.local_translation.LocaleHelper;
import com.adsyst.light_project_mobile.ui.fragments.HomeFragmentUser;
import com.adsyst.light_project_mobile.web_service.ApiService;
import com.adsyst.light_project_mobile.web_service.RetrofitBuilder;
import com.adsyst.light_project_mobile.web_service.model.Historical;
import com.adsyst.light_project_mobile.web_service.model.ResultsHistoric;
import com.adsyst.light_project_mobile.web_service.model.TransfertDepositResponse;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.adsyst.light_project_mobile.R.string.historique;

public class HomeHistorical extends AppCompatActivity {

    private static final String TAG = "HomeHistorical";
    private Toolbar toolbar;
    private Context context;
    private String phone;
    private ApiService service;
    private AwesomeValidation validator;
    private Call<Historical> call;
    private PrefManager prf;
    @BindView(R.id.rcv_historical)
    RecyclerView rcv_historical;
    @BindView(R.id.pgbar)
    ProgressBar pgbar;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.historiqueVide)
    LinearLayout historiqueVide;

    private AdapterHistorical adapterHistorical;
    private List<ResultsHistoric> historicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical);
        initView();


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadingData(context, phone, Global.API_KEY);

                if(!historicList.isEmpty()){
                    adapterHistorical.notifyDataSetChanged();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }


    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(historique));
        toolbar.setSubtitle(getString(R.string.subTitle));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        context = this;
        ButterKnife.bind(this);
        service = RetrofitBuilder.createService(ApiService.class);
        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        prf = new PrefManager(context);
        if (!prf.getString("key_tel_client").equals("")){
            phone = prf.getString("key_tel_client");
        }
        loadingData(context, phone, Global.API_KEY);
    }

    private void loadingData(Context context, String phone, String key) {

        call = service.get_hist(key, phone);
        call.enqueue(new Callback<Historical>() {
            @Override
            public void onResponse(Call<Historical> call, Response<Historical> response) {
                Log.w(TAG, "onResponse: " + response.body() );

                assert response.body() != null;
                historicList = response.body().getResultats();


                if(historicList == null){
                    historiqueVide.setVisibility(View.VISIBLE);
                    rcv_historical.setVisibility(View.GONE);
                } else {
                    adapterHistorical = new AdapterHistorical(context, historicList, historicList);
                    rcv_historical.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                    rcv_historical.setAdapter(adapterHistorical);
                }

                pgbar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Historical> call, Throwable t) {
                pgbar.setVisibility(View.GONE);
                Log.w(TAG, "onFailure: " + t.getMessage() );
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        //search listener
        MenuItem item = menu.findItem(R.id.search);
        //SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        //search listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(TextUtils.isEmpty(query)){
                    if (!historicList.isEmpty()) {
                        adapterHistorical.getFilter().filter("");
                        //rcv_historical.clearOnChildAttachStateChangeListeners();
                    }
                } else {
                    if (!historicList.isEmpty()) {
                        adapterHistorical.getFilter().filter(query);
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                if(TextUtils.isEmpty(query)){
                    if (!historicList.isEmpty()){
                        adapterHistorical.getFilter().filter("");
                        //rcv_historical.clearOnChildAttachStateChangeListeners();
                    }
                } else {
                    if (!historicList.isEmpty()) {
                        adapterHistorical.getFilter().filter(query);
                    }
                }
                return false;
            }
        });

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