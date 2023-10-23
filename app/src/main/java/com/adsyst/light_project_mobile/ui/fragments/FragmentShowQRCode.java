package com.adsyst.light_project_mobile.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adsyst.light_project_mobile.R;
import com.adsyst.light_project_mobile.config.AESUtils;
import com.adsyst.light_project_mobile.config.Global;
import com.adsyst.light_project_mobile.local_storage.provider.PrefManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import static android.graphics.Color.WHITE;

public class FragmentShowQRCode extends Fragment {

    private static final String TAG = "FragmentShowQRCode";
    private ImageView qrcode;
    private TextView card_number;
    private Button btn_shared;
    private Bitmap bitmap;
    private PrefManager prf;
    private String carteCrypte;
    private Context context;
    public FragmentShowQRCode() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_q_r_code, container, false);

        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.pointMarchand));
        card_number = (TextView) view.findViewById(R.id.card_number);
        qrcode = (ImageView) view.findViewById(R.id.qrcode);
        btn_shared = (Button) view.findViewById(R.id.btn_shared);
        context = getActivity();

        prf = new PrefManager(context);
        if(!prf.getString("key_phone").equalsIgnoreCase("")){
            try {
                carteCrypte = AESUtils.encrypt(prf.getString("key_phone"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.w(TAG, "onCreateView-----------------: " + carteCrypte );
        } else {
            carteCrypte = "";
        }


        assert carteCrypte != null;
        if(!carteCrypte.equals("")){
            try {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                BitMatrix bitMatrix = multiFormatWriter.encode(carteCrypte, BarcodeFormat.QR_CODE, 500, 500);
                /*BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                bitmap = barcodeEncoder.createBitmap(bitMatrix);*/
                bitmap = createBitmapCustomized(bitMatrix);
                //qrcode.setImageBitmap(bitmap);
            } catch (WriterException e){
                e.printStackTrace();
            }
        } else{
            Toast.makeText(getContext(), getString(R.string.erreurSurvenue1), Toast.LENGTH_SHORT).show();
        }


        btn_shared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    shareBitmap(viewToBitmap(qrcode, qrcode.getWidth(), qrcode.getHeight()));
                    Log.w(TAG, "onClick: " + 1);
                } else {
                    startShare();
                    Log.w(TAG, "onClick: " + 2);
                }

            }
        });

        return view;
    }

    private static String encrypt(String strClearText,String strKey) throws Exception{
        String strData="";

        try {
            SecretKeySpec skeyspec=new SecretKeySpec(strKey.getBytes(),"Blowfish");
            Cipher cipher=Cipher.getInstance("Blowfish");
            cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
            byte[] encrypted=cipher.doFinal(strClearText.getBytes());
            strData=new String(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        return strData;
    }



    private Bitmap createBitmapCustomized(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                //pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
                pixels[offset + x] = matrix.get(x, y) ?
                ResourcesCompat.getColor(getResources(),R.color.primary_color,null) :WHITE;

            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        Bitmap overlay = BitmapFactory.decodeResource(getResources(), R.drawable.logo_qrcode);
        qrcode.setImageBitmap(mergeBitmaps(overlay,bitmap));

        return bitmap;
    }


    private Bitmap mergeBitmaps(Bitmap overlay, Bitmap bitmap) {

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        Bitmap combined = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(combined);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        canvas.drawBitmap(bitmap, new Matrix(), null);

        int centreX = (canvasWidth  - overlay.getWidth()) /2;
        int centreY = (canvasHeight - overlay.getHeight()) /2 ;
        canvas.drawBitmap(overlay, centreX, centreY, null);

        return combined;
    }


    //methode qui fonctionnement uniquement avec les versions inférieur à Android 7.0
    public void startShare() {
        Bitmap bitmap = viewToBitmap(qrcode, qrcode.getWidth(), qrcode.getHeight());
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "ImageDemo.jpg");
        try{
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/ImageDemo.jpg"));
        startActivity(Intent.createChooser(shareIntent, getString(R.string.lightPartarge)));
    }

    private static Bitmap viewToBitmap(View view, int width, int height){
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }


    private void shareBitmap(Bitmap bitmap) {

        final String shareText = " Solution" + " "
                + getString(R.string.app_name) + " developed by "
                + "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName() + ": \n\n";

        try {
            File file = new File(getActivity().getExternalCacheDir(), "share.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_TEXT, shareText);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, getString(R.string.lightPartarge1)));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}