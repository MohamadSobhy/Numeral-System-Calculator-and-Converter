package com.example.mohamedsobhy.binarycalculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class PracesDialog extends Activity {

    private Button rightBrace , leftBrace ,minus , symbole;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.praces_dialog);

        rightBrace = (Button)findViewById(R.id.right_brace);
        leftBrace = (Button)findViewById(R.id.left_brace);
        minus = (Button)findViewById(R.id.six);
        symbole = (Button) findViewById(R.id.symoble);

        rightBrace.setOnClickListener(new Action());
        leftBrace.setOnClickListener(new Action());
        minus.setOnClickListener(new Action());
        symbole.setOnClickListener(new Action());

    }

    class Action implements View.OnClickListener{
        @Override
        public void onClick(View view){

            Intent i = new Intent();

            if(view == rightBrace) {
                i.putExtra("key" , "right");
                i.putExtra("right", ")");
            }
            else if(view == leftBrace) {
                i.putExtra("key" , "left");
                i.putExtra("left", "(");
            }
            else if(view == minus){
                i.putExtra("key" , "minus");
                i.putExtra("minus" , "|neg(");
            }
            else if(view == symbole){
                i.putExtra("key" , "|");
                i.putExtra("|" , "|");
            }

            setResult(RESULT_OK , i);
            finish();
        }
    }
}
