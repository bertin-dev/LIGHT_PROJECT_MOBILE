package com.adsyst.light_project_mobile.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adsyst.light_project_mobile.R;
import com.adsyst.light_project_mobile.config.Global;
import com.adsyst.light_project_mobile.local_storage.provider.PrefManager;
import com.adsyst.light_project_mobile.ui.graph.DayAxisValueFormatter;
import com.adsyst.light_project_mobile.ui.graph.MyValueFormatter;
import com.adsyst.light_project_mobile.ui.graph.XYMarkerView;
import com.adsyst.light_project_mobile.web_service.ApiService;
import com.adsyst.light_project_mobile.web_service.RetrofitBuilder;
import com.adsyst.light_project_mobile.web_service.model.Historical;
import com.adsyst.light_project_mobile.web_service.model.ResultsHistoric;
import com.adsyst.light_project_mobile.web_service.model.TotalSend;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class StatistiquesFragment extends Fragment {

    private static final String TAG = "StatistiquesFragment";
    @BindView(R.id.pgbarRecharge)
    ProgressBar pgbarRecharge;
    @BindView(R.id.pgbarSend)
    ProgressBar pgbarSend;
    @BindView(R.id.totalSend)
    TextView totalSend;
    @BindView(R.id.totalRecharge)
    TextView totalRecharge;

    private Context context;
    private String phone;
    private ApiService service;
    private Call<TotalSend> call;
    private String totalMontantSend, totalMontantRecharge;
    private PrefManager prf;
    private List<ResultsHistoric> historicList;

    private BarChart chart;
    protected Typeface tfLight;

    private Call<Historical> call2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /*getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        //Showing the title

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistiques, container, false);



        initView(view);
        loadingTotalRechargeData(phone, Global.API_KEY);



        initGraph(view);
        loadingDataGraph(phone, Global.API_KEY);
        return view;
    }

    private void initGraph(View view) {

        tfLight = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");
        chart = view.findViewById(R.id.chart1);
        //chart.setOnChartValueSelectedListener(getActivity());

        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);
        // chart.setDrawYLabels(false);

        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);
        chart.getXAxis().setEnabled(false);

        ValueFormatter custom = new MyValueFormatter(" Fcfa");

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(tfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(tfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        //chart.getAxisRight().setDrawLabels(false);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        XYMarkerView mv = new XYMarkerView(getActivity(), xAxisFormatter);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart
    }

    private void setData(int count, float range, List<ResultsHistoric> historics) {

        ArrayList<BarEntry> values = new ArrayList<>();

        for(int i = 0; i<count; i++){
            String num_trans = historicList.get(i).getNum_trans();
            int montant = Integer.parseInt(historicList.get(i).getMontant_trans());
            String date = historicList.get(i).getDate_trans();
            String frais = historicList.get(i).getFrais_trans();
            Log.w(TAG, "result-------------: N°" + num_trans + " montant=" + montant + " date=" + date + " frais=" + frais );

            if(montant < 10){
                values.add(new BarEntry(i, montant, getResources().getDrawable(R.drawable.star)));
            }else {
                values.add(new BarEntry(i, montant));
            }
        }

        /*for (int i = (int) start; i < start + count; i++) {
            float val = (float) (Math.random() * (range + 1));

            if (Math.random() * 100 < 25) {
                values.add(new BarEntry(i, val, getResources().getDrawable(R.drawable.star)));
            } else {
                values.add(new BarEntry(i, val));
            }
        }*/

        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.FRANCE).format(new Date()));

            set1.setDrawIcons(false);

            /*int startColor1 = ContextCompat.getColor(getActivity(), android.R.color.holo_orange_light);
            int startColor2 = ContextCompat.getColor(getActivity(), android.R.color.holo_blue_light);
            int startColor3 = ContextCompat.getColor(getActivity(), android.R.color.holo_orange_light);
            int startColor4 = ContextCompat.getColor(getActivity(), android.R.color.holo_green_light);
            int startColor5 = ContextCompat.getColor(getActivity(), android.R.color.holo_red_light);
            int endColor1 = ContextCompat.getColor(getActivity(), android.R.color.holo_blue_dark);
            int endColor2 = ContextCompat.getColor(getActivity(), android.R.color.holo_purple);
            int endColor3 = ContextCompat.getColor(getActivity(), android.R.color.holo_green_dark);
            int endColor4 = ContextCompat.getColor(getActivity(), android.R.color.holo_red_dark);
            int endColor5 = ContextCompat.getColor(getActivity(), android.R.color.holo_orange_dark);

            List<Fill> gradientFills = new ArrayList<>();
            gradientFills.add(new Fill(startColor1, endColor1));
            gradientFills.add(new Fill(startColor2, endColor2));
            gradientFills.add(new Fill(startColor3, endColor3));
            gradientFills.add(new Fill(startColor4, endColor4));
            gradientFills.add(new Fill(startColor5, endColor5));*/

            //set1.setFills(gradientFills);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(tfLight);
            data.setBarWidth(0.9f);

            chart.setData(data);
        }
    }

    private void initView(View view) {
        getActivity().setTitle(getString(R.string.transactions));
        context = getActivity();
        ButterKnife.bind(this, view);
        service = RetrofitBuilder.createService(ApiService.class);
        prf = new PrefManager(context);
        if (!prf.getString("key_tel_client").equals("")){
            phone = prf.getString("key_tel_client");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadingTotalSendData(phone, Global.API_KEY);
        chart.invalidate();
    }

    private void loadingDataGraph(String phone, String key) {

        call2 = service.get_hist(key, phone);
        call2.enqueue(new Callback<Historical>() {
            @Override
            public void onResponse(Call<Historical> call, Response<Historical> response) {
                Log.w(TAG, "onResponse: " + response.body() );

                assert response.body() != null;
                historicList = response.body().getResultats();

                if(historicList != null){
                    int transactionsTotale = historicList.size();

                    setData(transactionsTotale, 1, historicList);
                }

            }

            @Override
            public void onFailure(Call<Historical> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(call != null){
            call.cancel();
            call = null;
        }
        if(call2 != null){
            call2.cancel();
            call2 = null;
        }
    }

    private void loadingTotalRechargeData(String phone, String key) {

        call = service.get_total_rech(key, phone);
        call.enqueue(new Callback<TotalSend>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<TotalSend> call, Response<TotalSend> response) {
                Log.w(TAG, "success recharge: " + response.body() );

                assert response.body() != null;
                totalMontantRecharge = response.body().getTotal_des_recharges_des_30_derniers_jours();
                if(totalMontantRecharge == null){
                    totalRecharge.setText("0 Fcfa / 30 Jours");
                }else {
                    totalRecharge.setText(totalMontantRecharge + " Fcfa / 30 Jours");
                }
                pgbarRecharge.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<TotalSend> call, Throwable t) {
                pgbarRecharge.setVisibility(View.GONE);
                Log.w(TAG, "recharge onFailure: " + t.getMessage() );
            }
        });

    }

    private void loadingTotalSendData(String phone, String key) {

        call = service.get_total_env(key, phone);
        call.enqueue(new Callback<TotalSend>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<TotalSend> call, Response<TotalSend> response) {
                Log.w(TAG, "onResponse: " + response.body() );

                assert response.body() != null;
                totalMontantSend = response.body().getTotal_des_envoies_des_30_derniers_jours();
                if(totalMontantSend == null){
                    totalSend.setText("0 Fcfa / 30 Jours");
                }else {
                    totalSend.setText(totalMontantSend + " Fcfa / 30 Jours");
                }
                pgbarSend.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<TotalSend> call, Throwable t) {
                pgbarSend.setVisibility(View.GONE);
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });

    }


}