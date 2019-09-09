package com.example.mohamedsobhy.binarycalculator;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.math.BigInteger;

public class ConverterActivity extends AppCompatActivity {

    private EditText oldNumber;
    private TextView convertedNumber;
    private Button convert;
    private Spinner fromSystem , toSystem;
    private TextView mConvertedNumberTextView;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.converter_activity);
        setTitle(getResources().getString(R.string.converter_title));


        ///////////////////////////////////////////////////////////////

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

/*
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-5461582702541198/2748761864");
        AdRequest adRequest1 = new AdRequest.Builder().build();

        mInterstitialAd.loadAd(adRequest1);
        mInterstitialAd.setAdListener(new AdListener(){
            public void onAdLoaded(){
                mInterstitialAd.show();
            }
        });
        //////////////////////////////////////////////////////////////////////////////
*/


        getSupportActionBar().hide();

        oldNumber = (EditText) findViewById(R.id.oldNumber);
        convertedNumber = (TextView) findViewById(R.id.converted);
        convert = (Button) findViewById(R.id.convert);
        fromSystem = (Spinner) findViewById(R.id.fromsystem);
        toSystem = (Spinner) findViewById(R.id.toSystem);
        mConvertedNumberTextView = findViewById(R.id.converted);

        mConvertedNumberTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("convertedNumber", mConvertedNumberTextView.getText());
                cm.setPrimaryClip(clip);
                Snackbar.make(mConvertedNumberTextView, "Copied to clipboard", Snackbar.LENGTH_SHORT).show();

                return true;
            }
        });



        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldSystem = fromSystem.getSelectedItem().toString();
                String desiredSystem = toSystem.getSelectedItem().toString();
                String number = oldNumber.getText().toString();

                if( number.isEmpty() || number == getResources().getString(R.string.empty_string)){
                    Toast.makeText(getBaseContext() , "Enter Valid " + oldSystem + " Number " , Toast.LENGTH_SHORT).show();
                    return;
                }

                try{
                    double x = 0;
                    if(oldSystem.equals("Decimal"))
                        x  = Double.parseDouble(number);
                    else if(oldSystem.equals("Binary") && number.contains("."))
                        x = NumeralCalculator.convertPointNumberToDecimal(number.split("[.]") ,oldSystem);
                    else if(oldSystem.equals("Binary") && !number.contains("."))
                        x = NumeralCalculator.convertNumberToDecimal(number.toCharArray() , oldSystem);
                    else if(oldSystem.equals("Octal") && number.contains("."))
                        x = NumeralCalculator.convertPointNumberToDecimal(number.split("[.]") ,oldSystem);
                    else if(oldSystem.equals("Octal") && !number.contains("."))
                        x = NumeralCalculator.convertNumberToDecimal(number.toCharArray() , oldSystem);
                    else if(oldSystem.equals("Hex") && number.contains("."))
                        x = NumeralCalculator.convertPointHexNumberToDecimal(number.split("[.]"));
                    else if(oldSystem.equals("Hex") && !number.contains("."))
                        x = NumeralCalculator.convertHexNumberToDecimal(number.toCharArray());

                    if(x > 2147483647){
                        Toast.makeText(getBaseContext(), "Enter Numbers less Than " + Integer.MAX_VALUE, Toast.LENGTH_LONG).show();
                        return;
                    }

                }
                catch (Exception e){
                }


                if( checkValidation(number) == -1 || number.startsWith("-")){
                    Toast.makeText(getBaseContext() ,"Invalid Number!!" , Toast.LENGTH_LONG).show();
                    return;
                }



                if(oldSystem.equals("Binary")){
                    int count = 0;

                    for(int i = 0 ; i < number.length() ; i++){
                        if(number.charAt(i) == '.' && count == 0 )
                        {
                            count++;
                            continue;
                        }
                        if(number.charAt(i) != '0' && number.charAt(i) != '1' ){
                            Toast.makeText(getBaseContext() , "Invalid Binary Number !!" , Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }

                if(oldSystem.equals("Decimal")){
                    if(desiredSystem.equals("Binary")){

                        if(number.contains(".")){
                            convertedNumber.setText(NumeralCalculator.convertPointDecimalToBinary(number.split("[.]") , desiredSystem));
                        }
                        else {
                            convertedNumber.setText(NumeralCalculator.convertDecimalToOtherSystems( Integer.parseInt(number) , desiredSystem));
                        }
                    }
                    else if(desiredSystem.equals("Octal")){

                        if(number.contains(".")){
                            convertedNumber.setText(NumeralCalculator.convertPointBinaryToOctal(NumeralCalculator.convertPointDecimalToBinary(number.split("[.]") , "Binary").split("[.]")));
                        }
                        else {
                            convertedNumber.setText(NumeralCalculator.convertBinaryToOctal(NumeralCalculator.convertDecimalToOtherSystems( Integer.parseInt(number) , "Binary")));
                        }
                    }
                    else if(desiredSystem.equals("Hex")){

                        if(number.contains(".")){
                            convertedNumber.setText(NumeralCalculator.convertPointBinaryToHex(NumeralCalculator.convertPointDecimalToBinary(number.split("[.]") , "Binary").split("[.]")));
                        }
                        else{
                            convertedNumber.setText(NumeralCalculator.convertBinaryToHex(NumeralCalculator.convertDecimalToOtherSystems(Integer.parseInt(number) , "Binary")));
                        }
                    }
                    else {
                        convertedNumber.setText(number);
                    }
                }
                else if(oldSystem.equals("Binary")){
                    if(desiredSystem.equals("Decimal")){

                        if(number.contains(".")){
                            convertedNumber.setText(String.valueOf(NumeralCalculator.convertPointNumberToDecimal(number.split("[.]") ,oldSystem)));
                        }
                        else{
                            convertedNumber.setText(String.valueOf(NumeralCalculator.convertNumberToDecimal(number.toCharArray() , oldSystem)));
                        }
                    }
                    else if(desiredSystem.equals("Octal")){

                        if(number.contains(".")){
                            convertedNumber.setText(NumeralCalculator.convertPointBinaryToOctal(number.split("[.]")));
                        }
                        else {
                            convertedNumber.setText(NumeralCalculator.convertBinaryToOctal(number));
                        }
                    }
                    else if(desiredSystem.equals("Hex")){

                        if(number.contains(".")){
                            convertedNumber.setText(NumeralCalculator.convertPointBinaryToHex(number.split("[.]")));
                        }
                        else{
                            convertedNumber.setText(NumeralCalculator.convertBinaryToHex(number));
                        }
                    }
                    else {
                        convertedNumber.setText(number);
                    }
                }
                else if(oldSystem.equals("Octal")){

                    if(desiredSystem.equals("Decimal")){

                        if(number.contains(".")){
                            convertedNumber.setText(String.valueOf(NumeralCalculator.convertPointNumberToDecimal(number.split("[.]") , oldSystem)));
                        }
                        else {
                            convertedNumber.setText(String.valueOf(NumeralCalculator.convertNumberToDecimal(number.toCharArray() , oldSystem)));
                        }
                    }
                    else if(desiredSystem.equals("Binary")){

                        if( !number.contains(".")){
                            convertedNumber.setText(NumeralCalculator.convertDecimalToOtherSystems(NumeralCalculator.convertNumberToDecimal(number.toCharArray() , oldSystem) , "Binary"));
                        }
                        else {
                            convertedNumber.setText( NumeralCalculator.convertPointDecimalToBinary(String.valueOf(NumeralCalculator.convertPointNumberToDecimal(number.split("[.]") , oldSystem)).split("[.]") , "Binary"));
                        }
                    }
                    else if(desiredSystem.equals("Hex")){

                        if(number.contains(".")){
                            convertedNumber.setText(NumeralCalculator.convertPointBinaryToHex(NumeralCalculator.convertPointDecimalToBinary(String.valueOf(NumeralCalculator.convertPointNumberToDecimal(number.split("[.]") , oldSystem)).split("[.]") , "Binary").split("[.]")));
                        }
                        else {
                            convertedNumber.setText(NumeralCalculator.convertBinaryToHex(NumeralCalculator.convertDecimalToOtherSystems(NumeralCalculator.convertNumberToDecimal(number.toCharArray() , oldSystem) , "Binary")));
                        }
                    }
                    else {
                        convertedNumber.setText(number);
                    }

                }
                else if(oldSystem.equals("Hex")){
                    if(desiredSystem.equals("Decimal")){

                        if(number.contains(".")){
                            convertedNumber.setText(String.valueOf(NumeralCalculator.convertPointHexNumberToDecimal(number.split("[.]"))));
                        }
                        else {
                            convertedNumber.setText(String.valueOf(NumeralCalculator.convertHexNumberToDecimal(number.toCharArray())));
                        }
                    }
                    else if(desiredSystem.equals("Binary")){

                        if(number.contains(".")){
                            convertedNumber.setText(NumeralCalculator.convertPointDecimalToBinary( String.valueOf(NumeralCalculator.convertPointHexNumberToDecimal(number.split("[.]"))).split("[.]") , "Binary"));
                        }
                        else {
                            convertedNumber.setText(NumeralCalculator.convertDecimalToOtherSystems( NumeralCalculator.convertHexNumberToDecimal(number.toCharArray())  , "Binary"));
                        }
                    }
                    else if(desiredSystem.equals("Octal")){

                        if(number.contains(".")){
                            convertedNumber.setText(NumeralCalculator.convertPointBinaryToOctal(NumeralCalculator.convertPointDecimalToBinary( String.valueOf(NumeralCalculator.convertPointHexNumberToDecimal(number.split("[.]"))).split("[.]") , "Binary").split("[.]")));
                        }
                        else {
                            convertedNumber.setText(NumeralCalculator.convertBinaryToOctal(NumeralCalculator.convertDecimalToOtherSystems( NumeralCalculator.convertHexNumberToDecimal(number.toCharArray())  , "Binary")));
                        }
                    }
                    else {
                        convertedNumber.setText(number);
                    }

                }


            }
        });



    }

    private int checkValidation(String fnum ){

        String oldSystem = fromSystem.getSelectedItem().toString();
        try{

            if(!oldSystem.equals("Hex")) {
                double x = Double.parseDouble(fnum);
            }

        }catch (Exception e){
            return -1;
        }

        if(oldSystem.equals("Octal")) {
            if ( (!fnum.contains(".") && NumeralCalculator.convertNumberToDecimal(fnum.toCharArray(), oldSystem) == -1) || (fnum.contains(".") && NumeralCalculator.convertPointNumberToDecimal(fnum.split("[.]"), oldSystem) == -1)) {
                return -1;
            }
        }

        if(oldSystem.equals("Hex")) {
            if ( (!fnum.contains(".") && NumeralCalculator.convertHexNumberToDecimal(fnum.toCharArray()) == -1) || (fnum.contains(".") && NumeralCalculator.convertPointHexNumberToDecimal(fnum.split("[.]")) == -1)) {
                return -1;
            }
        }

        return 0;
    }

    }
