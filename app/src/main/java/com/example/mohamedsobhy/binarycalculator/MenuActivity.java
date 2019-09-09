package com.example.mohamedsobhy.binarycalculator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class MenuActivity extends AppCompatActivity {

    private TextView mSimpleCalculatorTextView;
    private TextView mNumberSystemsCalculatorTextView;
    private TextView mNumberSystemsConverterTextView;

    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);



        /////////////////////////////////////////////////////////////////////

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        /////////////////////////////////////////////////////////////////


        mSimpleCalculatorTextView = (TextView) findViewById(R.id.calculator);
        mNumberSystemsCalculatorTextView = (TextView) findViewById(R.id.nCalculator);
        mNumberSystemsConverterTextView = (TextView) findViewById(R.id.converter);



        mSimpleCalculatorTextView.setOnClickListener(new Action());
        mNumberSystemsCalculatorTextView.setOnClickListener(new Action());
        mNumberSystemsConverterTextView.setOnClickListener(new Action());

    }

    private class Action implements View.OnClickListener{
        @Override
        public void onClick(View view){

            Intent i = null;
            if(view == mSimpleCalculatorTextView){
                i = new Intent(MenuActivity.this , MainActivity.class);
            }
            else if(view == mNumberSystemsCalculatorTextView){
                i = new Intent(MenuActivity.this , NumeralCalculator.class);
            }
            else if(view == mNumberSystemsConverterTextView){
                i = new Intent(MenuActivity.this , ConverterActivity.class);
            }
            startActivity(i);
        }
    }
}
