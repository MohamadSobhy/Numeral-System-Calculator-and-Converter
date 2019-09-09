package com.example.mohamedsobhy.binarycalculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Stack;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private Button[] numbers = new Button[11];
    private Button[] operators = new Button[7];
    private Button equal , delete ;
    private TextView screen;
    private double []digits;
    private double []minusDigits;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
////////////////////////////////////////////////////////////////////////////

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

        ////////////////////////////////////////////////////////////////
*/

        getSupportActionBar().hide();

        prepareComponents();
        setOnActionListeners();
        //setTitle(getResources().getString(R.string.main_title));

        screen.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("calculatorResult", screen.getText());
                cm.setPrimaryClip(clip);
                Snackbar.make(screen, "Copied to clipboard", Snackbar.LENGTH_SHORT).show();

                return true;
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode , int resultCode , Intent data){

        String s = screen.getText().toString();

        if(requestCode == 1 && resultCode == RESULT_OK )
        {
            boolean negValid = data.getExtras().getString("key").startsWith("min") && s.length() >= 19 ;
            if(negValid)
                return;

            String key = data.getExtras().getString("key");
            if( !( (key.startsWith("r") || key.startsWith("|")) && ( s.isEmpty() || s == getResources().getString(R.string.empty_string) ) ) ) {
                screen.append(data.getExtras().getString(key));
                return;
            }

            if( !s.isEmpty() && s.substring( s.length() - 5 ) .equals("|neg(") ) {
                screen.setText(s.replace("|neg(", " ").trim());
            }
        }
    }


    class EqualAction implements View.OnClickListener{
        @Override
        public void onClick(View view){

            String infix = screen.getText().toString();

            //infix = infix.replace("[.]",".0");

            if(TextUtils.isEmpty(infix) || infix == getResources().getString(R.string.empty_string)){
                return;
            }
            if(isOperator(infix.charAt(infix.length() - 1 )) && infix.charAt(infix.length() - 1 ) != ')'){
                Snackbar.make(screen , "Invalid Ecpression!!" , Snackbar.LENGTH_LONG).show();
                return;
            }
            try{
                double result = Double.parseDouble(screen.getText().toString());
                screen.setText("" + result);
                return;
            }catch (Exception e){}

            if(infix.startsWith("-")){
                Snackbar.make(screen , "Invalid Ecpression!! . use |neg(num)| instead of '-' operator" , Snackbar.LENGTH_LONG).show();
                return;
            }

            checkValidation(infix.toString());

            if(infix.isEmpty() || infix.toString() == getResources().getString(R.string.empty_string)){
                Snackbar.make(screen , "Invalid Ecpression!!" , Snackbar.LENGTH_SHORT).show();
                return;
            }




            String [] s = screen.getText().toString().split("[-,+,*,/,%,^,|]");
            digits = new double[s.length];
            minusDigits = new double[s.length];


            int x = 0, y = 0;

            for(int i = 0 ; i < s.length ; i++){
                try {
                    if (!s[i].isEmpty() && s[i].startsWith("neg(")) {

                        String[] ss = s[i].split("[),(]");
                        minusDigits[x] = -1 * Double.parseDouble(ss[1]);
                        s[i] = "|" + s[i] + "|";
                        infix = infix.replaceFirst(Pattern.quote(s[i]), String.valueOf((char) ('a' + x)));
                        x++;

                    }

                }catch (Exception e){
                    Snackbar.make(screen , "Invalid Ecpression!!" , Snackbar.LENGTH_SHORT).show();
                }
            }

            if(infix.contains("|"))
            {
                Snackbar.make(screen , "Invalid Ecpression!!" , Snackbar.LENGTH_SHORT).show();
                return;
            }
            s = infix.split("[-,+,*,/,%,^,|]");
            try {

                for (int i = 0; i < s.length; i++) {
                    int count = 0;
                    char [] num = s[i].toCharArray();
                    for(int m = 0 ; m < num.length ; m++){
                        if(num[m] == '.')
                            count++;
                    }

                    if(count > 1)
                    {
                        Snackbar.make(screen , "Invalid Number with multiple decimal separator (.) !!" , Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    try {

                        s[i] = s[i].replace( '(' , ' ' );
                        s[i] = s[i].replace( ')' , ' ' );
                        s[i] = s[i].trim();

                    }catch (Exception e){}

                    try {
                        double check = Double.parseDouble(s[i]);
                    }catch (Exception e){
                        continue;
                    }

                        digits[i] = Double.parseDouble(s[i]);

                }
            }catch(Exception e){
                Snackbar.make(screen , "Invalid Expression!!" , Snackbar.LENGTH_SHORT).show();
                return;
                }


            for(int i = 0 ; i < digits.length ;i++) {

                if(digits[i] == 0) {
                    infix.replaceFirst("0.0", String.valueOf("A" + i));
                    continue;
                }

                String operand = String.valueOf(digits[i]);
                if(operand.endsWith(".0") && !infix.contains(".0")) {
                    infix = infix.replaceFirst(String.valueOf((int) digits[i]), String.valueOf((char) ('A' + i)));
                }
                else
                    infix = infix.replaceFirst( operand , String.valueOf((char) ('A' + i)));
            }



            if(convertInfixToPostfix(infix) == null)
                return;

            screen.setText(String.valueOf(postfixEvaluation(convertInfixToPostfix(infix).trim().toCharArray())));

        }

        private int operatorPriorty(char operator)
        {
            switch (operator)
            {
                case '(':
                    return 1;
                case ')':
                    return 2;
                case '+':
                case '-':
                    return 3;
                case '*':
                case '/':
                case '%':
                    return 4;
                case '^':
                    return 5;
                default:
                    return -1;
            }

        }

        private String convertInfixToPostfix(String infix)
        {
            Stack<Character> stack = new Stack<Character>();
            char [] postfix = new char[infix.length()];

            stack.push('(');
            infix = infix + ")";

            int x = 0;
            for(int i = 0 ; i < infix.length() ; i++){

                if( !isOperator(infix.charAt(i)) ){
                    postfix[x++] = infix.charAt(i);
                }
                else{

                    switch(operatorPriorty(infix.charAt(i))){

                        case 1:
                        {
                            stack.push(infix.charAt(i));
                            break;
                        }
                        case 2:
                        {
                            char ch = stack.pop();
                            while (ch != '(')
                            {
                                postfix[x++] = ch;
                                ch = stack.pop();
                            }
                            break;
                        }
                        case 3:
                        {
                            char ch = stack.pop();
                            while (operatorPriorty(ch) >= 3)
                            {
                                postfix[x++] = ch;
                                ch = stack.pop();
                            }
                            stack.push(ch);
                            stack.push(infix.charAt(i));
                            break;
                        }
                        case 4:
                        {
                            char ch = stack.pop();
                            while (operatorPriorty(ch) >= 4)
                            {
                                postfix[x++] = ch;
                                ch = stack.pop();
                            }
                            stack.push(ch);
                            stack.push(infix.charAt(i));
                            break;
                        }
                        case 5:
                        {
                            char ch = stack.pop();
                            while (operatorPriorty(ch) == 5)
                            {
                                postfix[x++] = ch;
                                ch = stack.pop();
                            }
                            stack.push(ch);
                            stack.push(infix.charAt(i));
                            break;
                        }
                        default:
                            Snackbar.make(screen , "Invalid Expression!!" , Snackbar.LENGTH_SHORT).show();

                    }
                }
            }

            if(!stack.isEmpty())
            {
                Snackbar.make(screen , "Invalid Expression!!" , Snackbar.LENGTH_SHORT).show();
                return null;
            }
            return String.valueOf(postfix);
        }


        private double postfixEvaluation(char [] postfix){

            Stack<Double> stack = new Stack<Double>();
            int i = 0;
            double operand1 = 0 , operand2 = 0 , result;

            while (i < postfix.length)
            {
                if( !isOperator(postfix[i]) )
                {
                    stack.push(operandValue(postfix[i]));
                }
                else
                {
                    operand2 = stack.pop();
                    operand1 = stack.pop();

                    switch (postfix[i])
                    {
                        case '+':
                        {
                            result = operand1 + operand2;
                            break;
                        }
                        case '-':
                        {
                            result = operand1 - operand2;
                            break;
                        }
                        case '*':
                        {
                            result = operand1 * operand2;
                            break;
                        }
                        case '/':
                        {
                            result = operand1 / operand2;
                            break;
                        }
                        case '%':
                        {
                            result = operand1 % operand2;
                            break;
                        }
                        case '^':
                        {
                            result =  Math.pow( operand1 , operand2 );
                            break;
                        }
                        default:
                        {
                            Snackbar.make(screen , "Invalid Expression!!" , Snackbar.LENGTH_SHORT).show();
                            screen.setText("");
                            return -1;
                        }

                    }

                    stack.push(result);
                }


                    i++;
            }

            return stack.pop();
        }

        private double operandValue(char operand){

            switch (operand){

                case 'A':
                    return digits[0];
                case 'B':
                    return digits[1];
                case 'C':
                    return digits[2];
                case 'D':
                    return digits[3];
                case 'E':
                    return digits[4];
                case 'F':
                    return digits[5];
                case 'G':
                    return digits[6];
                case 'H':
                    return digits[7];
                case 'I':
                    return digits[8];
                case 'J':
                    return digits[9];
                case 'K':
                    return digits[10];
                case 'L':
                    return digits[11];
                case 'M':
                    return digits[12];
                case 'N':
                    return digits[13];
                case 'O':
                    return digits[14];
                case 'a':
                    return minusDigits[0];
                case 'b':
                    return minusDigits[1];
                case 'c':
                    return minusDigits[2];
                case 'd':
                    return minusDigits[3];
                case 'e':
                    return minusDigits[4];
                case 'f':
                    return minusDigits[5];
                case 'g':
                    return minusDigits[6];
                case 'h':
                    return minusDigits[7];
                case 'i':
                    return minusDigits[8];
                case 'j':
                    return minusDigits[9];
                case 'k':
                    return minusDigits[10];
                case 'l':
                    return minusDigits[11];
                case 'm':
                    return minusDigits[12];
                case 'n':
                    return minusDigits[13];
                case 'o':
                    return minusDigits[14];
                default:
                {
                    return (double)(operand - '0');
                }

            }
        }

        private void checkValidation(String infix){
            try {
                if( ( isOperator(infix.charAt(0)) && infix.charAt(0) != '(' ) || ( isOperator(infix.charAt(infix.length() - 1 )) && infix.charAt(infix.length()-1) != ')') )
                    Snackbar.make(screen , "Invalid Ecpression!!" , Snackbar.LENGTH_SHORT).show();
            }catch (Exception e){
                Snackbar.make(screen , "Invalid Ecpression!!" , Snackbar.LENGTH_SHORT).show();
            }

        }


    }
    private boolean isOperator(char ch){
        return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^' || ch == '%' || ch == '(' || ch == ')';
    }


    class Action implements View.OnClickListener{

        @Override
        public void onClick(View view){

            String expr = screen.getText().toString();

            boolean full = false;
            if(screen.getText().toString().length() == 32)
                full = true;

            boolean notEmpty = !TextUtils.isEmpty(screen.getText().toString()) && screen.getText().toString() != getResources().getString(R.string.empty_string);

            if(view == numbers[0] && !full)
                screen.append("0");
            else if(view == numbers[1] && !full)
                screen.append("1");
            else if(view == numbers[2] && !full)
                screen.append("2");
            else if(view == numbers[3] && !full)
                screen.append("3");
            else if(view == numbers[4] && !full)
                screen.append("4");
            else if(view == numbers[5] && !full)
                screen.append("5");
            else if(view == numbers[6] && !full)
                screen.append("6");
            else if(view == numbers[7] && !full)
                screen.append("7");
            else if(view == numbers[8] && !full)
                screen.append("8");
            else if(view == numbers[9] && !full)
                screen.append("9");
            else if(view == numbers[10] && !full)
                screen.append(".");
            else if(view == delete) {
                if (notEmpty)
                    try {
                        screen.setText(screen.getText().subSequence(0, screen.getText().length() - 1));
                    } catch (Exception e) {}
            }
            else if(view == operators[0] && !full) {
                if(  notEmpty && expr.charAt(expr.length() - 1) == '(' )
                    return;
                if( notEmpty &&  ( isOperator(expr.charAt(expr.length() - 1 )) && expr.charAt(expr.length() - 1) != ')' )  ){
                    delete.performClick();
                }
                if(notEmpty)
                screen.append("+");
            }
            else if(view == operators[1] && !full) {
                if( notEmpty && ( expr.charAt(expr.length() - 1) == '+' || expr.charAt(expr.length() - 1 ) =='%' ) ){
                    delete.performClick();
                }
                if(notEmpty && expr.charAt(expr.length() - 1 ) == '-'){
                    delete.performClick();
                    screen.append("+");
                    return;
                }
                if( notEmpty && ( expr.charAt(expr.length() - 1 ) == '(' || expr.charAt(expr.length() - 1 ) == '*' || expr.charAt(expr.length() - 1 ) == '/' || expr.charAt(expr.length() - 1 ) == '^') ){

                    if( !( expr.length() >= 28 )) {
                        if( !(expr.length() < 5) && expr.substring( expr.length() - 5 ) .equals("|neg(") ) {
                            screen.setText(expr.replace("|neg(", " ").trim());
                        }
                        else {
                            screen.append("|neg(");
                        }
                    }
                }
                else {
                    if (notEmpty) {
                        screen.append("-");
                    }
                    else {
                            screen.append("|neg(");
                    }
                }
            }
            else if(view == operators[2] && !full) {

                if( notEmpty && expr.charAt(expr.length() - 1) == '(' )
                    return;

                if (notEmpty && ( isOperator(expr.charAt(expr.length() - 1)) && expr.charAt(expr.length() - 1) != '(' && expr.charAt(expr.length() - 1) != ')' ) ) {
                    delete.performClick();
                }
                if(notEmpty)
                screen.append("*");
            }
            else if(view == operators[3] && !full) {

                if( notEmpty && expr.charAt(expr.length() - 1) == '(' )
                    return;

                if( notEmpty &&  ( isOperator(expr.charAt(expr.length() - 1 )) && expr.charAt(expr.length() - 1) != '(' && expr.charAt(expr.length() - 1) != ')' )  ){
                    delete.performClick();
                }
                if(notEmpty)
                screen.append("/");
            }
            else if(view == operators[4] && !full) {

                if(  notEmpty && expr.charAt(expr.length() - 1) == '(' )
                    return;

                if( notEmpty && ( isOperator(expr.charAt(expr.length() - 1 )) && expr.charAt(expr.length() - 1) != '(' && expr.charAt(expr.length() - 1) != ')' )  ){
                    delete.performClick();
                }
                if(notEmpty)
                screen.append("^");
            }
            else if(view == operators[5] && !full) {

                if( notEmpty && expr.charAt(expr.length() - 1) == '(' )
                    return;

                if( notEmpty && ( isOperator(expr.charAt(expr.length() - 1 )) && expr.charAt(expr.length() - 1) != '(' && expr.charAt(expr.length() - 1) != ')' )  ){
                    delete.performClick();
                }
                if(notEmpty)
                screen.append("%");
            }
            else if(view == operators[6]) {
                try {
                    if (Double.parseDouble(expr) > 0)
                        screen.setText(String.valueOf(Math.sqrt(Double.parseDouble(screen.getText().toString()))));
                    else
                        Snackbar.make(screen , "Invalid Number!!", Snackbar.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Snackbar.make(screen , "Invalid Number!!", Snackbar.LENGTH_SHORT).show();
                }
            }



        }

    }

    class DelteAllAction implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View view){
            if(view == delete)
                screen.setText(R.string.empty_string);
            return true;
        }

    }
    class DotLongAction implements View.OnLongClickListener{
        @Override
        public boolean onLongClick(View view){
            Intent dialog = new Intent(MainActivity.this , PracesDialog.class);
            startActivityForResult(dialog , 1);

            return true;
        }
    }

    private void prepareComponents(){
        numbers[0] = (Button)findViewById(R.id.zero);
        numbers[1] = (Button)findViewById(R.id.one);
        numbers[2] = (Button)findViewById(R.id.two);
        numbers[3] = (Button)findViewById(R.id.three);
        numbers[4] = (Button)findViewById(R.id.four);
        numbers[5] = (Button)findViewById(R.id.five);
        numbers[6] = (Button)findViewById(R.id.six);
        numbers[7] = (Button)findViewById(R.id.seven);
        numbers[8] = (Button)findViewById(R.id.eight);
        numbers[9] = (Button)findViewById(R.id.nine);
        numbers[10] = (Button)findViewById(R.id.dot);

        operators[0] = (Button)findViewById(R.id.plus);
        operators[1] = (Button)findViewById(R.id.minus);
        operators[2] = (Button)findViewById(R.id.multiply);
        operators[3] = (Button)findViewById(R.id.divide);
        operators[4] = (Button)findViewById(R.id.power);
        operators[5] = (Button)findViewById(R.id.mudulus);
        operators[6] = (Button)findViewById(R.id.root);

        equal = (Button)findViewById(R.id.equal);
        screen = (TextView) findViewById(R.id.screen);
        delete = (Button) findViewById(R.id.clean);

    }
    private void setOnActionListeners(){
        for(int i=0 ; i < numbers.length ;i++){
            numbers[i].setOnClickListener(new Action());
            if(i < 7)
                operators[i].setOnClickListener(new Action());
        }


        delete.setOnClickListener(new Action());

        delete.setOnLongClickListener(new DelteAllAction());

        equal.setOnClickListener(new EqualAction());
        numbers[10].setOnLongClickListener(new DotLongAction());

    }

}
