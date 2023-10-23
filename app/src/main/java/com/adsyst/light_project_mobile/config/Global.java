package com.adsyst.light_project_mobile.config;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.adsyst.light_project_mobile.R;
import com.adsyst.light_project_mobile.local_storage.provider.DbHandler;
import com.adsyst.light_project_mobile.ui.register.GenerateQRCode;
import java.text.DateFormat;
import java.util.Date;


public class Global {

    public static String API_KEY = "$2y$10$ODhn8,xcHj3uG2O9q7mls/.Dzug5CIUUNLglSkVjtbvAQBsV2r4pHu2OZ0HyWZyafwvsqpeDcJ9Ov@bdPCMhc/Rtv2t?GXowwd?nlm9eJzFUzCpetkMsVME/WTLaSbryP0,<5w6X3gi";

    public static final double Latitude_marche_soa = 3.9756296;
    public static final double Longitude_marche_soa = 11.5935448;

    public static final double Latitude_soa_campus = 3.9660964;
    public static final double Longitude_soa_campus = 11.5935347;

    public static final double Latitude_omnisport = 3.8906481;
    public static final double Longitude_omnisport = 11.544921;

    public static final double Latitude_camair = 3.8654263;
    public static final double Longitude_camair = 11.5205789;

    public static void errorResponse(Context context, String typeOperation, String response){
        ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
        AlertDialog.Builder build_error = new AlertDialog.Builder(context);
        DbHandler dbHandler = new DbHandler(context);
        Date currentDate = new Date();
        DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        dbHandler.insertUserDetails(typeOperation, response, "0", R.drawable.ic_baseline_cancel_presentation_24, shortDateFormat.format(currentDate));

        View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_error, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
        title.setText(context.getString(R.string.information));
        imageButton.setImageResource(R.drawable.ic_baseline_cancel_presentation_24);
        statutOperation.setText(response);
        build_error.setPositiveButton("OK", null);
        build_error.setCancelable(false);
        build_error.setView(view);
        build_error.show();
    }

    public static void successResponse(Context context, String typeOperation, String response) {
        ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
        AlertDialog.Builder build_error = new AlertDialog.Builder(context);
        DbHandler dbHandler = new DbHandler(context);
        Date currentDate = new Date();
        DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        dbHandler.insertUserDetails(typeOperation, response, "0", R.drawable.ic_baseline_check_circle_outline_24, shortDateFormat.format(currentDate));


        View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_success, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
        title.setText(context.getString(R.string.information));
        imageButton.setImageResource(R.drawable.ic_baseline_check_circle_outline_24);
        statutOperation.setText(response);
        build_error.setPositiveButton("OK", null);
        build_error.setCancelable(false);
        build_error.setView(view);
        build_error.show();
    }

    public static void response(Context context, String typeOperation, String response, int icon) {
        ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
        AlertDialog.Builder build_error = new AlertDialog.Builder(context);
        DbHandler dbHandler = new DbHandler(context);
        Date currentDate = new Date();
        DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        dbHandler.insertUserDetails(typeOperation, response, "0", icon, shortDateFormat.format(currentDate));


        View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_success, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
        title.setText(context.getString(R.string.information));
        imageButton.setImageResource(icon);
        statutOperation.setText(response);
        build_error.setPositiveButton("OK", null);
        build_error.setCancelable(false);
        build_error.setView(view);
        build_error.show();
    }

    public static void responseRegister(Context context, String typeOperation, String response, int icon, String phone) {
        ////////////////////INITIALISATION DE LA BASE DE DONNEES LOCALE/////////////////////////
        AlertDialog.Builder build_error = new AlertDialog.Builder(context);
        DbHandler dbHandler = new DbHandler(context);
        Date currentDate = new Date();
        DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        dbHandler.insertUserDetails(typeOperation, response, "0", icon, shortDateFormat.format(currentDate));


        View view = LayoutInflater.from(context).inflate(R.layout.alert_dialog_success, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView statutOperation = (TextView) view.findViewById(R.id.statutOperation);
        ImageButton imageButton = (ImageButton) view.findViewById(R.id.image);
        title.setText(context.getString(R.string.information));
        imageButton.setImageResource(icon);
        statutOperation.setText(response);
        build_error.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, GenerateQRCode.class);
                intent.putExtra("phone", phone);
                context.startActivity(intent);
            }
        });
        build_error.setCancelable(false);
        build_error.setView(view);
        build_error.show();
    }

}
