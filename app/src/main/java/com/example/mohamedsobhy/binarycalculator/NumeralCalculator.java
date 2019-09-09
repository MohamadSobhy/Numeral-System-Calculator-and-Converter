package com.example.mohamedsobhy.binarycalculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.vision.text.Text;

import java.util.Stack;
import java.util.regex.Pattern;

public class NumeralCalculator extends AppCompatActivity {

    private Button calculate;
    private EditText firstNumber , secondNumber;
    TextView result;
    private Spinner systemType , operator;
    String fnumber , snumber , system , optr;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.numeral_calculator);
        setTitle(getResources().getString(R.string.binary_calculator));


/////////////////////////////////////////////////////////////////////////////

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
//////////////////////////////////////////////////////////////////////////////////////////////
*/
        calculate = (Button) findViewById(R.id.calculate);
        firstNumber = (EditText) findViewById(R.id.firstNumber);
        secondNumber = (EditText) findViewById(R.id.secondNumber);
        result = (TextView) findViewById(R.id.result);
        systemType = (Spinner) findViewById(R.id.system);
        operator = (Spinner) findViewById(R.id.operator);


        firstNumber.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        secondNumber.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);



        calculate.setOnClickListener(new CalculateAction());

        result.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("convertedNumber", result.getText());
                cm.setPrimaryClip(clip);
                Snackbar.make(result, "Copied to clipboard", Snackbar.LENGTH_SHORT).show();

                return true;
            }
        });

    }

    private class CalculateAction implements View.OnClickListener{

        @Override
        public void onClick(View view){
            fnumber = firstNumber.getText().toString();
            snumber = secondNumber.getText().toString();
            optr = operator.getSelectedItem().toString();
            system = systemType.getSelectedItem().toString();

            if(checkValidation(fnumber , snumber) == -1 || snumber.startsWith("-") || fnumber.startsWith("-")){
                Toast.makeText(getBaseContext() ,"Invalid Number!!" , Toast.LENGTH_LONG).show();
                return;
            }

            int count = 0 , counts = 0;
            if(system.equals("Binary")){

                for(int i = 0 ; i < fnumber.length() ; i++){

                    if( fnumber.charAt(i) == '.'  && count == 0 )
                    {
                        count++;
                        continue;
                    }

                    if(fnumber.charAt(i) != '0' && fnumber.charAt(i) != '1'){
                        Toast.makeText(getBaseContext() , "Invalid Binary Number !!" , Toast.LENGTH_LONG).show();
                        return;
                    }

                }
                for(int i = 0 ; i < snumber.length() ; i++){
                    if( snumber.charAt(i) =='.' && counts ==0){
                        counts++;
                        continue;
                    }
                    if(snumber.charAt(i) != '0' && snumber.charAt(i) != '1'){
                        Toast.makeText(getBaseContext() , "Invalid Binary Number !!" , Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }

            if(fnumber.contains(".") && snumber.contains(".")){
                if(system.equals("Binary")) {
                    try {
                        result.setText(convertPointDecimalToBinary(String.valueOf(getResult(convertPointNumberToDecimal(fnumber.split("[.]"), system), convertPointNumberToDecimal(snumber.split("[.]"), system), optr)).split("[.]"), system));
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "Enter Number Less Than " + Integer.MAX_VALUE, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                else if(system.equals("Octal")) {
                    try {
                        result.setText(convertPointBinaryToOctal(String.valueOf(convertPointDecimalToBinary(String.valueOf(getResult(convertPointNumberToDecimal(fnumber.split("[.]"), system), convertPointNumberToDecimal(snumber.split("[.]"), system), optr)).split("[.]"), "Binary")).split("[.]")));
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "Enter Number Less Than " + Integer.MAX_VALUE, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                else if(system.equals("Hex")) {
                    try {
                        result.setText(convertPointBinaryToHex(String.valueOf(convertPointDecimalToBinary(String.valueOf(getResult(convertPointHexNumberToDecimal(fnumber.split("[.]")), convertPointHexNumberToDecimal(snumber.split("[.]")), optr)).split("[.]"), "Binary")).split("[.]")));
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "Enter Number Less Than " + Integer.MAX_VALUE, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }
            else if(fnumber.contains(".") && !snumber.contains(".")){
                if(system.equals("Binary")) {
                    try {
                        result.setText(convertPointDecimalToBinary(String.valueOf(getResult(convertPointNumberToDecimal(fnumber.split("[.]"), system), convertNumberToDecimal(snumber.toCharArray(), system), optr)).split("[.]"), system));
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "Enter Number Less Than " + Integer.MAX_VALUE, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                else if(system.equals("Octal")) {
                    try {
                        result.setText(convertPointBinaryToOctal(String.valueOf(convertPointDecimalToBinary(String.valueOf(getResult(convertPointNumberToDecimal(fnumber.split("[.]"), system), convertNumberToDecimal(snumber.toCharArray(), system), optr)).split("[.]"), "Binary")).split("[.]")));
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "Enter Number Less Than " + Integer.MAX_VALUE, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                else if(system.equals("Hex")) {
                    try {
                        result.setText(convertPointBinaryToHex(String.valueOf(convertPointDecimalToBinary(String.valueOf(getResult(convertPointHexNumberToDecimal(fnumber.split("[.]")), convertHexNumberToDecimal(snumber.toCharArray()), optr)).split("[.]"), "Binary")).split("[.]")));
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "Enter Number Less Than " + Integer.MAX_VALUE, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }
            else if( !fnumber.contains(".") && snumber.contains(".") ){
                if(system.equals("Binary")) {
                    try {
                        result.setText(convertPointDecimalToBinary(String.valueOf(getResult(convertNumberToDecimal(fnumber.toCharArray(), system), convertPointNumberToDecimal(snumber.split("[.]"), system), optr)).split("[.]"), system));
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "Enter Number Less Than " + Integer.MAX_VALUE, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                else if(system.equals("Octal")) {
                    try {
                        result.setText(convertPointBinaryToOctal(String.valueOf(convertPointDecimalToBinary(String.valueOf(getResult(convertNumberToDecimal(fnumber.toCharArray(), system), convertPointNumberToDecimal(snumber.split("[.]"), system), optr)).split("[.]"), "Binary")).split("[.]")));
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "Enter Number Less Than " + Integer.MAX_VALUE, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                else if(system.equals("Hex")) {
                    try {
                        result.setText(convertPointBinaryToHex(String.valueOf(convertPointDecimalToBinary(String.valueOf(getResult(convertHexNumberToDecimal(fnumber.toCharArray()), convertPointHexNumberToDecimal(snumber.split("[.]")), optr)).split("[.]"), "Binary")).split("[.]")));
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "Enter Number Less Than " + Integer.MAX_VALUE, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }
            else {
                if(system.equals("Binary")) {
                    try {
                        result.setText(convertDecimalToOtherSystems((int) getResult(convertNumberToDecimal(fnumber.toCharArray(), system), convertNumberToDecimal(snumber.toCharArray(), system), optr), system));
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "Enter Number Less Than " + Integer.MAX_VALUE, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                else if(system.equals("Octal")) {
                    try {
                        result.setText(convertBinaryToOctal(String.valueOf(convertDecimalToOtherSystems((int) getResult(convertNumberToDecimal(fnumber.toCharArray(), system), convertNumberToDecimal(snumber.toCharArray(), system), optr), "Binary"))));
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "Enter Number Less Than " + Integer.MAX_VALUE, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                else if(system.equals("Hex")) {
                    try {
                        result.setText(String.valueOf(convertDecimalToHex((int) getResult(convertHexNumberToDecimal(fnumber.toCharArray()), convertHexNumberToDecimal(snumber.toCharArray()), optr))));
                    } catch (Exception e) {
                        Toast.makeText(getBaseContext(), "Enter Number Less Than " + Integer.MAX_VALUE, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }


        }

        private int checkValidation(String fnum , String snum){

            try{

                if(!system.equals("Hex")) {
                    double x = Double.parseDouble(fnum);
                    x = Double.parseDouble(snum);
                }

            }catch (Exception e){
                return -1;
            }

            if(system.equals("Octal")){
                if( ( !fnum.contains(".") && convertNumberToDecimal(fnum.toCharArray() ,system) == -1 ) || ( fnum.contains(".") && convertPointNumberToDecimal(fnum.split("[.]") , system) == -1)){
                    return -1;
                }
                if( ( !snum.contains(".") && convertNumberToDecimal(snum.toCharArray() ,system) == -1 ) || ( snum.contains(".") && convertPointNumberToDecimal(snum.split("[.]") , system) == -1 )){
                    return -1;
                }
            }

            if(system.equals("Hex")){
                if( ( !fnum.contains(".") )) {
                    if ((convertHexNumberToDecimal(fnum.toCharArray()) == -1)) {
                        return -1;
                    }
                }
                else {
                        if( convertPointHexNumberToDecimal(fnum.split("[.]")) == -1)
                            return -1;
                }

                if( ( !snum.contains(".") && convertHexNumberToDecimal(snum.toCharArray()) == -1 ) || ( snum.contains(".") && convertPointHexNumberToDecimal(snum.split("[.]")) == -1 )){
                    return -1;
                }
            }


            return 0;
        }

        private double getResult(double fnum , double snum , String operator){

            switch(operator){

                case "+":
                    return fnum + snum;
                case "-":
                    return fnum - snum;
                case "*":
                    return fnum * snum;
                case "/":
                    return fnum / snum;
            }
            return -1;
        }

    }

    /**
     * this func converts from any number system to decimal number.
     * @param number --->  the number you want to convert.
     * @param system --->  the system of the number that will be converted.
     * @return it's returns an integer number represents the decimal value of @param number.
     */
    public static int convertNumberToDecimal(char [] number , String system){


        int base , decimal = 0;
        Stack<Integer> stack = new Stack<Integer>();

        switch (system){
            case "Binary":
            {
                base = 2;
                break;
            }
            case "Octal":
            {
                base = 8;
                break;
            }
            case "Hex":
            {
                base = 16;
                break;
            }
            default:
            {
                return -1;
            }
        }

        for(int i = 0 ; i < number.length ; i++) {
            if(number[i] == '9' && system.equals("Octal")){
                return -1;
            }
            stack.push(Integer.parseInt(String.valueOf(number[i])));
        }

        for(int i = 0 ; i < number.length ; i++){
            decimal += stack.pop() * (int)Math.pow(base , i);
        }

        return decimal;

    }
    /**
     * this func converts from any number system ( number have decimal point ) to decimal number.
     * @param number --->  the number you want to convert.
     * @param system --->  the system of the number that will be converted.
     * @return it's returns an double number represents the decimal value of @param number.
     */
    public static double convertPointNumberToDecimal(String [] number , String system){

        if(number[0].equals("0") && number[1].equals("0"))
            return 0.0;
        else if(number[0].equals("1") && number[1].equals("0"))
            return 1.0;

        int base ;
        double decimal = 0;
        char[] prePart , postPart;
        prePart = number[0].toCharArray();
        decimal = (double) convertNumberToDecimal(prePart ,system);

        if(number[1].equals("0")){
            return decimal+0.0;
        }

        postPart = number[1].toCharArray();
        int [] post = new int[number[1].length()];

        switch (system){
            case "Binary":
            {
                base = 2;
                break;
            }
            case "Octal":
            {
                base = 8;
                break;
            }
            case "Hex":
            {
                base = 16;
                break;
            }
            default:
            {
                return -1;
            }
        }

        for(int i = 0 ; i < postPart.length ; i++){
            if(postPart[i] == '9' && system.equals("Octal") ){
                return -1;
            }
            post[i] = Integer.parseInt(String.valueOf(postPart[i]));
        }

        for(int i = 1 ; i <= postPart.length ; i++){

            decimal += post[i - 1] * Math.pow(base, (-1 * i));

        }

        return decimal;

    }
    /**
     * this func converts from decimal to binary.
     * @param decimal --->  the number you want to convert.
     * @param system --->  the system of the number that will be converted.
     * @return it's returns an integer number represents the decimal value of @param number.
     */
    public static String convertDecimalToOtherSystems( int decimal , String system){

        if(decimal == 0 ){
            return "0";
        }

        int base;
        Stack<String> stack = new Stack<String>();

        switch (system){
            case "Binary":
            {
                base = 2;
                break;
            }
            case "Octal":
            {
                base = 8;
                break;
            }
            case "Hex":
            {
                base = 16;
                break;
            }
            default:
            {
                return "-1";
            }
        }

        while(decimal > 0){

            stack.push(String.valueOf(decimal % base));
            decimal /= base;
        }

        String convertedNumber = "";
        while( stack.size() > 0){
            convertedNumber += stack.pop();
        }


        return convertedNumber;
    }
    /**
     * this func converts from point decimal to binary.
     * @param decimal --->  the number you want to convert.
     * @param system --->  the system of the number that will be converted.
     * @return it's returns an integer number represents the decimal value of @param number.
     */
    public static String convertPointDecimalToBinary( String [] decimal , String system){

        if(decimal[0].equals("0") && decimal[1].equals("0"))
            return "0.0";
        else if(decimal[0].equals("1") && decimal[1].equals("0"))
            return "1.0";

        String convertedNumber = convertDecimalToOtherSystems(Integer.parseInt(decimal[0]) , system ) + ".";

        if(decimal[1].equals("0")){
            return convertedNumber + "0";
        }

        double postPart = Double.parseDouble("0." + decimal[1]);


        while(postPart > 0){

            postPart *= 2;
            decimal[1] = String.valueOf(postPart);
            if(decimal[1].startsWith("1")){
                convertedNumber += "1";
                postPart -= 1;
            }
            else {
                convertedNumber += "0";
            }
        }

        return convertedNumber;
    }
    /**
     * this func converts from binary to octal.
     * @param binary --->  the number you want to convert.
     * @return it's returns an integer number represents the decimal value of @param number.
     */
    public static String convertBinaryToOctal(String binary){

        int len = binary.length();
        String part = "" , octal = "";


        if(len % 3 != 0){
            int rem = 3 - (len % 3);

            for(int i = 0 ; i < rem ; i++)
                binary = "0" + binary;
        }
        len = binary.length();
        char [] bin = binary.toCharArray();

        for(int i = 1 ; i <= len ; i++){
            part += bin[i - 1];

            if( i % 3 == 0){
                octal += String.valueOf(convertNumberToDecimal(part.toCharArray() , "Binary"));
                part = "";
            }
        }

        return octal;
    }
    /**
     * this func converts from point binary to octal.
     * @param binary --->  the number you want to convert.
     * @return it's returns an integer number represents the decimal value of @param number.
     */
    public static String convertPointBinaryToOctal(String [] binary){

        if(binary[0].equals("0") && binary[1].equals("0"))
            return "0.0";
        else if(binary[0].equals("1") && binary[1].equals("0"))
            return "1.0";



        String part = "" , octal = "";
        octal = convertBinaryToOctal(binary[0]) + ".";

        if(binary[1].equals("0")){
            return octal + "0";
        }

        int len = binary[1].length();

        if(len % 3 != 0){
            int rem = 3 - (len % 3);

            for(int i = 0 ; i < rem ; i++)
                binary[1] += "0";
        }
        len = binary[1].length();
        char [] bin = binary[1].toCharArray();

        for(int i = 1 ; i <= len ; i++){
            part += bin[i - 1];

            if( i % 3 == 0){
                octal += String.valueOf(convertNumberToDecimal(part.toCharArray() , "Binary"));
                part = "";
            }
        }

        return octal;
    }
    /**
     * this func converts from binary to hexadecimal.
     * @param binary --->  the number you want to convert.
     * @return it's returns an integer number represents the decimal value of @param number.
     */
    public static String convertBinaryToHex(String binary){

        int len = binary.length();
        String part = "" , hex = "";


        if(len % 4 != 0){
            int rem = 4 - (len % 4);

            for(int i = 0 ; i < rem ; i++)
                binary = "0" + binary;
        }
        len = binary.length();
        char [] bin = binary.toCharArray();

        for(int i = 1 ; i <= len ; i++){
            part += bin[i - 1];

            if( i % 4 == 0){
                if(convertNumberToDecimal(part.toCharArray() , "Binary") == 10){
                    hex += "A";
                }
                else if(convertNumberToDecimal(part.toCharArray() , "Binary") == 11){
                    hex += "B";
                }
                else if(convertNumberToDecimal(part.toCharArray() , "Binary") == 12){
                    hex += "C";
                }
                else if(convertNumberToDecimal(part.toCharArray() , "Binary") == 13){
                    hex += "D";
                }
                else if(convertNumberToDecimal(part.toCharArray() , "Binary") == 14){
                    hex += "E";
                }
                else if(convertNumberToDecimal(part.toCharArray() , "Binary") == 15){
                    hex += "F";
                }
                else {
                    hex += String.valueOf(convertNumberToDecimal(part.toCharArray(), "Binary"));
                }
                part = "";
            }
        }

        return hex;
    }
    /**
     * this func converts from point binary to hexadecimal.
     * @param binary --->  the number you want to convert.
     * @return it's returns an integer number represents the decimal value of @param number.
     */
    public static String convertPointBinaryToHex(String [] binary){

        if(binary[0].equals("0") && binary[1].equals("0"))
            return "0.0";
        else if(binary[0].equals("1") && binary[1].equals("0"))
            return "1.0";



        String part = "" , hex = "";
        hex = convertBinaryToHex(binary[0]) + ".";

        if(binary[1].equals("0")){
            return hex + "0";
        }

        int len = binary[1].length();

        if(len % 4 != 0){
            int rem = 4 - (len % 4);

            for(int i = 0 ; i < rem ; i++)
                binary[1] += "0";
        }
        len = binary[1].length();
        char [] bin = binary[1].toCharArray();

        for(int i = 1 ; i <= len ; i++){
            part += bin[i - 1];

            if( i % 4 == 0){
                if(convertNumberToDecimal(part.toCharArray() , "Binary") == 10){
                    hex += "A";
                }
                else if(convertNumberToDecimal(part.toCharArray() , "Binary") == 11){
                    hex += "B";
                }
                else if(convertNumberToDecimal(part.toCharArray() , "Binary") == 12){
                    hex += "C";
                }
                else if(convertNumberToDecimal(part.toCharArray() , "Binary") == 13){
                    hex += "D";
                }
                else if(convertNumberToDecimal(part.toCharArray() , "Binary") == 14){
                    hex += "E";
                }
                else if(convertNumberToDecimal(part.toCharArray() , "Binary") == 15){
                    hex += "F";
                }
                else {
                    hex += String.valueOf(convertNumberToDecimal(part.toCharArray(), "Binary"));
                }
                part = "";
            }
        }

        return hex;
    }

    public static int convertHexNumberToDecimal(char [] number){

        int decimal = 0;
        Stack<Integer> stack = new Stack<Integer>();

        for(int i = 0 ; i < number.length ; i++) {
            if(number[i] == 'A'){
                stack.push(10);
            }
            else if(number[i] == 'B'){
                stack.push(11);
            }
            else if(number[i] == 'C'){
                stack.push(12);
            }
            else if(number[i] == 'D'){
                stack.push(13);
            }
            else if(number[i] == 'E'){
                stack.push(14);
            }
            else if(number[i] == 'F'){
                stack.push(15);
            }
            else if(Character.isDigit(number[i])) {
                stack.push(Integer.parseInt(String.valueOf(number[i])));
            }
            else
            {
                return -1;
            }
        }

        for(int i = 0 ; i < number.length ; i++){
            decimal += stack.pop() * (int)Math.pow(16 , i);
        }

        return decimal;

    }

    public static double convertPointHexNumberToDecimal(String [] number ){

        if(number[0].equals("0") && number[1].equals("0"))
            return 0.0;
        else if(number[0].equals("1") && number[1].equals("0"))
            return 1.0;


        double decimal = 0;
        char[] prePart , postPart;
        prePart = number[0].toCharArray();
        decimal = (double) convertHexNumberToDecimal(prePart);

        if(number[1].equals("0")){
            return decimal+0.0;
        }

        postPart = number[1].toCharArray();
        int [] post = new int[number[1].length()];


        for(int i = 0 ; i < postPart.length ; i++){
            if(postPart[i] == 'A'){
                post[i] = 10;
            }
            else if(postPart[i] == 'B'){
                post[i] = 11;
            }
            else if(postPart[i] == 'C'){
                post[i] = 12;
            }
            else if(postPart[i] == 'D'){
                post[i] = 13;
            }
            else if(postPart[i] == 'E'){
                post[i] = 14;
            }
            else if(postPart[i] == 'F'){
                post[i] = 15;
            }
            else if(Character.isDigit(postPart[i])) {
                post[i] = Integer.parseInt(String.valueOf(postPart[i]));
            }
            else
            {
                return -1;
            }

        }

        for(int i = 1 ; i <= postPart.length ; i++){

            decimal += post[i - 1] * Math.pow(16, (-1 * i));

        }

        return decimal;

    }

    public static String convertDecimalToHex( int decimal ){

        if(decimal == 0 ){
            return "0";
        }

        int base;
        Stack<String> stack = new Stack<String>();

        while(decimal > 0){
            if((decimal % 16) == 10){
                stack.push("A");
            }
            else if((decimal % 16) == 11){
                stack.push("B");
            }
            else if((decimal % 16) == 12){
                stack.push("C");
            }
            else if((decimal % 16) == 13){
                stack.push("D");
            }
            else if((decimal % 16) == 14){
                stack.push("C");
            }
            else if((decimal % 16) == 15){
                stack.push("F");
            }
            else {
                stack.push(String.valueOf(decimal % 16));
            }
            decimal /= 16;
        }

        String convertedNumber = "";
        while( stack.size() > 0){
            convertedNumber += stack.pop();
        }


        return convertedNumber;
    }
}
