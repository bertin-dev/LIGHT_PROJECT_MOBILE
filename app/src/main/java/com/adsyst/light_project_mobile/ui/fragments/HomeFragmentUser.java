package com.adsyst.light_project_mobile.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adsyst.light_project_mobile.R;
import com.adsyst.light_project_mobile.local_storage.provider.PrefManager;
import com.adsyst.light_project_mobile.ui.Deposit;
import com.adsyst.light_project_mobile.ui.Payment;
import com.adsyst.light_project_mobile.ui.Transfer;
import com.adsyst.light_project_mobile.ui.withdrawal.HomeWithdrawal;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFragmentUser extends Fragment {

    @BindView(R.id.idUser)
    TextView idUser;
    private PrefManager prf;
    private Context context;
    @BindView(R.id.tel)
    TextView tel;
    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.lastConnexion)
    TextView lastConnexion;

    private ImageView close;
    private RelativeLayout reportDashboad;

    public HomeFragmentUser() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_user, container, false);

        //tutorial_fragment(view);
        initItem(view);
        return view;
    }

    public void tutorial_fragment(View view) {

        // You don't always need a sequence, and for that there's a single time tap target
        final SpannableString spannedDesc = new SpannableString("This is the sample app for TapTargetView");
        spannedDesc.setSpan(new UnderlineSpan(), spannedDesc.length() - "TapTargetView".length(), spannedDesc.length(), 0);
        TapTargetView.showFor(getActivity(), TapTarget.forView(view.findViewById(R.id.ln_depot), "Hello, world!", spannedDesc)
                .cancelable(false)
                .drawShadow(true)
                .titleTextDimen(R.dimen.title_text_size)
                .tintTarget(false), new TapTargetView.Listener() {
            @Override
            public void onTargetClick(TapTargetView view) {
                super.onTargetClick(view);
                // .. which evidently starts the sequence we defined earlier
            }

            @Override
            public void onOuterCircleClick(TapTargetView view) {
                super.onOuterCircleClick(view);
                Toast.makeText(view.getContext(), "You clicked the outer circle!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                Log.d("TapTargetViewSample", "You dismissed me :(");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void initItem(View view) {
        ButterKnife.bind(this, view);
        context = getActivity();
        close = (ImageView) view.findViewById(R.id.close);
        reportDashboad = (RelativeLayout) view.findViewById(R.id.reportDashboad);
        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.accueil_callbox));
        prf = new PrefManager(context);
        if(!prf.getString("key_tel_client").equalsIgnoreCase("")){
            idUser.setText("ID user: " + prf.getString("key_id_user"));
            tel.setText("Tel: " + prf.getString("key_tel_client"));
            email.setText("Email: " + prf.getString("key_email_client"));
        }
        lastConnexion.setText(getString(R.string.lastconnexion) + " " + new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.FRANCE).format(new Date()));
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportDashboad.setVisibility(View.GONE);
            }
        });
    }


    //Deposit
    @OnClick(R.id.ln_depot)
    void depot(){
        Intent intent = new Intent(getActivity(), Deposit.class);
        startActivity(intent);
    }

    //Retrait
    @OnClick(R.id.ln_retrait)
    void Withdrawal(){
        Intent intent = new Intent(getActivity(), HomeWithdrawal.class);
        startActivity(intent);
    }

    //Payement
    @OnClick(R.id.ln_payement)
    void Payment(){
        Intent intent = new Intent(getActivity(), Payment.class);
        startActivity(intent);
    }

    //Transfer
    @OnClick(R.id.ln_transfert)
    void transfert(){
        Intent intent = new Intent(getActivity(), Transfer.class);
        startActivity(intent);
    }

}