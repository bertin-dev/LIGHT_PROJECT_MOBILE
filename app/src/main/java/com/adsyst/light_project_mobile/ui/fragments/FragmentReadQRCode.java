package com.adsyst.light_project_mobile.ui.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adsyst.light_project_mobile.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.ButterKnife;

public class FragmentReadQRCode extends Fragment {

    private static final String TAG = "FragmentReadQRCode";

    public FragmentReadQRCode() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_read_q_r_code, container, false);

        //initialisation des objets qui seront manipulés
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        IntentResult scanResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, intent);
        // handle scan result

        if(scanResult != null && scanResult.getContents() != null){

            int pos = scanResult.getContents().indexOf("E-ZPASS");
            if (pos >= 0) {

                String carteNumber = scanResult.getContents().substring(7, 15).toUpperCase();
                Toast.makeText(getActivity(), carteNumber, Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(getContext(), getString(R.string.erreurQRcode), Toast.LENGTH_SHORT).show();
            }
        }
    }
}