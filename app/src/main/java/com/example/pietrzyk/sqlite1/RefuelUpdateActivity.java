package com.example.pietrzyk.sqlite1;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RefuelUpdateActivity extends AppCompatActivity {

    DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    EditText update_kmCounter, update_fuelAmount, update_fuelPrice, update_cashSpend, update_date, update_note;
    CheckBox update_fuelFull, update_fuelMissed;
    Context context = this;
    public int id_row_for_update,kmCounter;
    Calendar myCalendar = Calendar.getInstance();
    String sett_currency,sett_dist_unit,sett_fuel_cap , sett_fuel_usage_units ;
    String displayed_currency = "PLN", displayed_fuel_cap = "l", displayed_fuel_usage_units = "l/100km", displayed_dist_units = "km";
    Conventers conventer = new Conventers();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refuel_update);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.refuel_update_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.refuel_update_tiitle_pl);

        //wyświetlenie strzalke back
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            //getSupportActionBar().setDisplayShowTitleEnabled(false); // usuwa tytuł activity z toolbara
        }
        //Toolbar toolbar = (Toolbar) findViewById(R.id.update_toolbar);
        //setSupportActionBar(toolbar);

        update_kmCounter = (EditText) findViewById(R.id.Update_Refuel_KmCounter_Input);
        update_fuelAmount = (EditText) findViewById(R.id.Update_Refuel_FuelAmount_Input);
        update_fuelPrice = (EditText) findViewById(R.id.Update_Refuel_FuelPrice_Input);
        update_cashSpend = (EditText) findViewById(R.id.Update_Refuel_CashSpend_Input);
        update_date = (EditText) findViewById(R.id.Update_Refuel_Date_Input);
        update_fuelFull = (CheckBox) findViewById(R.id.Update_Refuel_FuelFull_CheckBox);
        update_fuelMissed = (CheckBox) findViewById(R.id.Update_Refuel_FuelMissed_CheckBox);
        update_note = (EditText) findViewById(R.id.Update_Refuel_Note_Input);

        getSettings();

        //odbiera id wiersza który ma być zaktualizowany
        Intent mIntent = getIntent();
        id_row_for_update = mIntent.getIntExtra("idRow",0);
        kmCounter = mIntent.getIntExtra("KM",-1);

        updateLabel_Update_RefuelDate();
        popUpRefuelRowToUpdate();

        //update_kmCounter.setText(String.valueOf(id_row_for_update));

        //Zapobiega wyskakiwaniu klawiatury przy edittext date ale nie wpisuje daty po wybraniu jej z DatePickerDialog
        update_date.setFocusableInTouchMode(false);
        update_date.setFocusable(false);

        //Tworzymy wyskakujacy kalendarz do wpisywania daty
        final DatePickerDialog.OnDateSetListener DTPListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel_Update_RefuelDate();
            }
        };

        //wyswietla DataPickerDialog po kliknięciu w RefuelDate EditText
        update_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RefuelUpdateActivity.this,
                        DTPListener,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        update_fuelPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() != 0) {
                    if (update_fuelAmount.getText().toString().trim().length() != 0)
                    //&& CashSpend.getText().toString().trim().length() == 0)
                    {

                        float FA = Float.parseFloat(update_fuelAmount.getText().toString());
                        float FP = Float.parseFloat(update_fuelPrice.getText().toString());
                        float CS = FA * FP;

                        String CS_str = String.valueOf(roundFloat(CS, 2));

                        update_cashSpend.setText(CS_str);
                    } else if (update_cashSpend.getText().toString().trim().length() != 0)

                    //&& FuelAmount.getText().toString().trim().length() == 0)
                    {

                        float FP = Float.parseFloat(update_fuelPrice.getText().toString());
                        float CS = Float.parseFloat(update_cashSpend.getText().toString());
                        float FA = CS / FP;

                        String FA_str = String.valueOf(roundFloat(FA, 2));

                        update_fuelAmount.setText(FA_str);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    } // onCreate

        //aktualizuje RefuelDate edittext o aktualna lub wybrana przez nas date w odpowiednim formacie
    private void updateLabel_Update_RefuelDate(){
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.US);
        update_date.setText(simpleDateFormat.format(myCalendar.getTime()));
    }

    public void popUpRefuelRowToUpdate(){ //View view

        dbHelper = new DBHelper(context); //DBHelper(getApplicationContext());
        sqLiteDatabase = dbHelper.getWritableDatabase();

        Cursor cursor = dbHelper.getSingleRefuelRowById(id_row_for_update, sqLiteDatabase);

        if (cursor.moveToFirst()){

            int km_counter;
            float fuel_amount, fuel_price, cash_spend;
            String fuel_date, fuel_full, fuel_missed, fuel_car, fuel_note;

            //tabela wyglada tak (0.id, 1.fuel_amount, 2.fuel_price, 3.cash_spend, 4.km_counter, 5.fuel_date)

            //fuel_id = cursor.getInt(0);
            fuel_amount = cursor.getFloat(1);
            fuel_price = cursor.getFloat(2);
            cash_spend = cursor.getFloat(3);
            km_counter = cursor.getInt(4);
            fuel_date = cursor.getString(5);
            fuel_full = cursor.getString(6);
            fuel_missed = cursor.getString(7);
            //fuel_car = cursor.getString(8);
            fuel_note = cursor.getString(9);

            if (displayed_dist_units.equals("km")) { update_kmCounter.setText(String.valueOf(km_counter)); }
            if (displayed_dist_units.equals("mi")) {
                double tmp = conventer.kilometersToMiles(km_counter);
                km_counter = (int) tmp;
                update_kmCounter.setText(String.valueOf(km_counter));
            }

            if (displayed_fuel_cap.equals("l")) { update_fuelAmount.setText(String.valueOf(fuel_amount)); }
            if (sett_fuel_cap.equals("Galony (UK)")){
                double tmp;
                tmp = conventer.litresToGalUK(fuel_amount);
                fuel_amount = (float) tmp;
                update_fuelAmount.setText(String.valueOf(roundFloat(fuel_amount,2)));
            }
            if (sett_fuel_cap.equals("Galony (US)")){
                double tmp;
                tmp = conventer.litresToGalUS(fuel_amount);
                fuel_amount = (float) tmp;
                update_fuelAmount.setText(String.valueOf(roundFloat(fuel_amount,2)));
            }

            update_fuelPrice.setText(String.valueOf(fuel_price));
            update_cashSpend.setText(String.valueOf(cash_spend));
            update_date.setText(fuel_date);
            update_note.setText(fuel_note);

            if(fuel_full.equals("1")){
               update_fuelFull.setChecked(true);
            }
            else{
                update_fuelFull.setChecked(false);
            }

            if (fuel_missed.equals("1")){
                update_fuelMissed.setChecked(true);
            }
            else {
                update_fuelMissed.setChecked(false);
            }
        }
    }

    public void updateRefuel(){

        int km_counter = 0;
        if (displayed_dist_units.equals("km")){ km_counter = Integer.parseInt(update_kmCounter.getText().toString()); }
        if (displayed_dist_units.equals("mi")){
            double tmp;
            km_counter = Integer.parseInt(update_kmCounter.getText().toString());
            tmp = conventer.milesToKilometers(km_counter);
            km_counter = (int) tmp;
        }


        float fuel_amount =0;
        if (displayed_fuel_cap.equals("l")){ fuel_amount = Float.parseFloat(update_fuelAmount.getText().toString()); }
        if (sett_fuel_cap.equals("Galony (UK)")){
            double tmp;
            fuel_amount = Float.parseFloat(update_fuelAmount.getText().toString());
            tmp = conventer.galUKtoLitres(fuel_amount);
            fuel_amount = (float) tmp;
        }
        if (sett_fuel_cap.equals("Galony (US)")){
            double tmp;
            fuel_amount = Float.parseFloat(update_fuelAmount.getText().toString());
            tmp = conventer.galUStoLitres(fuel_amount);
            fuel_amount = (float) tmp;
        }

        float fuel_price = Float.parseFloat(update_fuelPrice.getText().toString());
        float cash_spend = Float.parseFloat(update_cashSpend.getText().toString());
        String fuel_date = update_date.getText().toString();
        String fuel_full, fuel_missed, fuel_car, fuel_note;
        Float fuel_per100;
        Integer fuel_km_diff, next_km_counter, next_km_diff;

        fuel_note = update_note.getText().toString();

        if (update_fuelFull.isChecked()){
            fuel_full = "1";
        }
        else fuel_full = "0";

        if (update_fuelMissed.isChecked()){
            fuel_missed = "1";
        }
        else fuel_missed = "0";

        dbHelper = new DBHelper(context); //DBHelper(getApplicationContext());

        sqLiteDatabase = dbHelper.getWritableDatabase();

        dbHelper.updateRefuelRow(id_row_for_update, fuel_amount, fuel_price, cash_spend, km_counter, fuel_date, fuel_full, fuel_missed, fuel_note, sqLiteDatabase);

        Toast.makeText(getBaseContext(), "Tankowanie zaktualizowane", Toast.LENGTH_SHORT).show();

        dbHelper = new DBHelper(context); //DBHelper(getApplicationContext());
        sqLiteDatabase = dbHelper.getWritableDatabase();

        Cursor cursor = dbHelper.getSingleRefuelRowById(id_row_for_update, sqLiteDatabase);
        cursor.moveToFirst();
        fuel_car = cursor.getString(8); // 9 wiersz to id samochodu

        Cursor cur_last_fuel_usage = dbHelper.getLastFuelUsage(fuel_car,km_counter);
        cur_last_fuel_usage.moveToFirst();

        if (cur_last_fuel_usage.moveToFirst()){

            fuel_per100 = cur_last_fuel_usage.getFloat(0);

            if (fuel_per100 == null || fuel_per100 == 0 || fuel_per100 == 0.0) {
                dbHelper.updateRefuelFuelUsage(fuel_car, km_counter, null, sqLiteDatabase);
            }
            else {
                dbHelper.updateRefuelFuelUsage(fuel_car, km_counter, fuel_per100, sqLiteDatabase);
            }
        }

        // update roznicy km od edytowanego rekordu i porpzednieg
        Cursor cur_km_diff = dbHelper.getDiffBettwenLast2KMC(fuel_car, km_counter);
        cur_km_diff.moveToFirst();

        if(cur_km_diff.moveToFirst()){

            fuel_km_diff = cur_km_diff.getInt(0);

            if (fuel_km_diff == null || fuel_km_diff == 0 || fuel_km_diff == 0.0) {
                dbHelper.updateRefuelKmDiff(fuel_car,km_counter,null,sqLiteDatabase);
            }
            else {
                dbHelper.updateRefuelKmDiff(fuel_car, km_counter, fuel_km_diff, sqLiteDatabase);
            }
        }

        // update roznicy km  rekordu edytowanego i następnego jeśli taki istnieje
        Cursor cur_next_km_counter = dbHelper.getNextKmCounter(km_counter,fuel_car);

        cur_next_km_counter.moveToFirst();

        if (cur_next_km_counter.moveToFirst()){

            next_km_counter= cur_next_km_counter.getInt(0);

            Cursor cur_next_km_diff = dbHelper.getDiffBettwenLast2KMC(fuel_car,next_km_counter);

            if (cur_next_km_diff.moveToFirst()) {

                next_km_diff = cur_next_km_diff.getInt(0);

                Log.e("TEST", "Next_KM_COUNTER = " + next_km_counter + ", NEXT_KM_DIFF = " + next_km_diff);
                if (next_km_diff == null || next_km_diff == 0 || next_km_diff == 0.0) {
                    dbHelper.updateRefuelKmDiff(fuel_car, next_km_counter, null, sqLiteDatabase);
                } else {
                    dbHelper.updateRefuelKmDiff(fuel_car, next_km_counter, next_km_diff, sqLiteDatabase);
                }
            }
        }
        dbHelper.close();
        //finish();
    }

    public void getSettings(){

        dbHelper = new DBHelper(context);
        Cursor cur_settings = dbHelper.getSettings();

        if (cur_settings.moveToFirst()){

            cur_settings.moveToFirst();

            sett_currency = cur_settings.getString(0);
            sett_dist_unit = cur_settings.getString(1);
            sett_fuel_cap = cur_settings.getString(2);
            sett_fuel_usage_units = cur_settings.getString(3);

            if (sett_currency.equals("PLN")) displayed_currency = "zł";
            if (sett_currency.equals("EUR")) displayed_currency = "€";
            if (sett_currency.equals("USD")) displayed_currency = "$";
            if (sett_currency.equals("GBP")) displayed_currency = "£";
            if (sett_currency.equals("CZK")) displayed_currency = "Kč";

            //dist units
            if (sett_dist_unit.equals("Kilometry")) displayed_dist_units = "km";
            if (sett_dist_unit.equals("Mile")) displayed_dist_units = "mi";

            //fuel cap
            if (sett_fuel_cap.equals("Litry")) displayed_fuel_cap = "l";
            if (sett_fuel_cap.equals("Galony (UK)")) displayed_fuel_cap = "gal";
            if (sett_fuel_cap.equals("Galony (US)")) displayed_fuel_cap = "gal";

            //fuel usage
            if (sett_fuel_usage_units.equals("l/100km")) displayed_fuel_usage_units = "l/100km";
            if (sett_fuel_usage_units.equals("mpg (UK)")) displayed_fuel_usage_units = "mpg(uk)";
            if (sett_fuel_usage_units.equals("mpg (US)")) displayed_fuel_usage_units = "mpg(us)";
        }

    }

    //sprawdzanie czy podany licznik jest mniejszy lub rowny ostatniego zapisanego w bazie
    public int lastKMCounterCheck(int km_counter){

        int last_km_counter;
        int state = 0;
        dbHelper = new DBHelper(context);
        Cursor cursor = dbHelper.getPrevKmCounter(kmCounter);
        cursor.moveToFirst();

        if (cursor.moveToFirst()){

            last_km_counter = cursor.getInt(0);

            if (displayed_dist_units.equals("km")){
                if(last_km_counter >= km_counter){
                    state = 6;
                }
            }

            if (displayed_dist_units.equals("mi")){
                if (last_km_counter >= conventer.milesToKilometers(km_counter)){
                    state = 6;
                }
            }
        }
        else {
            return 0;
        }
        cursor.close();
        return state;
    }

    public static float roundFloat (float number, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(number));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public static double roundDouble (double number, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(number));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_refuel_update_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_update_refuel) {

            int state = 0;


            //Sprawdzam czy ostatni zapisany w bazie licznik jest mniejszy od podanego
            if(update_kmCounter.getText().toString().length() > 0)
                state = lastKMCounterCheck(Integer.valueOf(update_kmCounter.getText().toString()));


            if (update_kmCounter.getText().length() == 0 ){state++;}
            if (update_fuelAmount.getText().length() == 0){state++;}
            if (update_fuelPrice.getText().length() == 0){state++;}
            if (update_cashSpend.getText().length() == 0){state++;}
            if (update_date.getText().length() ==0){state++;}

            switch (state) {

                case 0:
                    updateRefuel();
                    //clearRefuelEditText();
                    finish();
                    //Intent myIntent = new Intent(RefuelUpdateActivity.this,RefuelListActivity.class);
                    //startActivity(myIntent);
                    break;
                case 1:
                    if (update_kmCounter.getText().length() == 0 ){update_kmCounter.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_fuelAmount.getText().length() == 0){update_fuelAmount.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_fuelPrice.getText().length() == 0){update_fuelPrice.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_cashSpend.getText().length() == 0){update_cashSpend.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_date.getText().length() ==0){update_date.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! conajmniej jedno pole jest puste! Uzpełnij!", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    if (update_kmCounter.getText().length() == 0 ){update_kmCounter.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_fuelAmount.getText().length() == 0){update_fuelAmount.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_fuelPrice.getText().length() == 0){update_fuelPrice.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_cashSpend.getText().length() == 0){update_cashSpend.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_date.getText().length() ==0){update_date.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! conajmniej dwa pola są puste! Uzpełnij!", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    if (update_kmCounter.getText().length() == 0 ){update_kmCounter.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_fuelAmount.getText().length() == 0){update_fuelAmount.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_fuelPrice.getText().length() == 0){update_fuelPrice.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_cashSpend.getText().length() == 0){update_cashSpend.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_date.getText().length() ==0){update_date.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! conajmniej trzy pola są puste! Uzpełnij!", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    if (update_kmCounter.getText().length() == 0 ){update_kmCounter.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_fuelAmount.getText().length() == 0){update_fuelAmount.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_fuelPrice.getText().length() == 0){update_fuelPrice.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_cashSpend.getText().length() == 0){update_cashSpend.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_date.getText().length() ==0){update_date.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! conajmniej cztery pola są puste! Uzpełnij!", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    if (update_kmCounter.getText().length() == 0 ){update_kmCounter.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_fuelAmount.getText().length() == 0){update_fuelAmount.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_fuelPrice.getText().length() == 0){update_fuelPrice.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_cashSpend.getText().length() == 0){update_cashSpend.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_date.getText().length() ==0){update_date.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! wszystkie pola są puste! Uzpełnij!", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    update_kmCounter.setError(getString(R.string.refuel_new_update_editext_kmcounter_too_low_PL));
                    if (update_fuelAmount.getText().length() == 0){update_fuelAmount.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_fuelPrice.getText().length() == 0){update_fuelPrice.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_cashSpend.getText().length() == 0){update_cashSpend.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_date.getText().length() ==0){update_date.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! Podałeś mniejszy licznik niż poprzednio!", Toast.LENGTH_SHORT).show();
                    break;
                case 7:
                    update_kmCounter.setError(getString(R.string.refuel_new_update_editext_kmcounter_too_low_PL));
                    if (update_fuelAmount.getText().length() == 0){update_fuelAmount.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_fuelPrice.getText().length() == 0){update_fuelPrice.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_cashSpend.getText().length() == 0){update_cashSpend.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_date.getText().length() ==0){update_date.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! Podałeś mniejszy licznik niż poprzednio! Oraz przynajmniej jedno puste pole!", Toast.LENGTH_SHORT).show();
                    break;
                case 8:
                    update_kmCounter.setError(getString(R.string.refuel_new_update_editext_kmcounter_too_low_PL));
                    if (update_fuelAmount.getText().length() == 0){update_fuelAmount.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_fuelPrice.getText().length() == 0){update_fuelPrice.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_cashSpend.getText().length() == 0){update_cashSpend.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_date.getText().length() ==0){update_date.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! Podałeś mniejszy licznik niż poprzednio! Oraz przynajmniej dwa puste pola!", Toast.LENGTH_SHORT).show();
                    break;
                case 9:
                    update_kmCounter.setError(getString(R.string.refuel_new_update_editext_kmcounter_too_low_PL));
                    if (update_fuelAmount.getText().length() == 0){update_fuelAmount.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_fuelPrice.getText().length() == 0){update_fuelPrice.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_cashSpend.getText().length() == 0){update_cashSpend.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_date.getText().length() ==0){update_date.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! Podałeś mniejszy licznik niż poprzednio! Oraz przynajmniej trzy puste pola!", Toast.LENGTH_SHORT).show();
                    break;
                case 10:
                    update_kmCounter.setError(getString(R.string.refuel_new_update_editext_kmcounter_too_low_PL));
                    if (update_fuelAmount.getText().length() == 0){update_fuelAmount.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_fuelPrice.getText().length() == 0){update_fuelPrice.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_cashSpend.getText().length() == 0){update_cashSpend.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_date.getText().length() ==0){update_date.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! Podałeś mniejszy licznik niż poprzednio! Oraz przynajmniej cztery puste pola!", Toast.LENGTH_SHORT).show();
                    break;
                case 11:
                    update_kmCounter.setError(getString(R.string.refuel_new_update_editext_kmcounter_too_low_PL));
                    if (update_fuelAmount.getText().length() == 0){update_fuelAmount.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_fuelPrice.getText().length() == 0){update_fuelPrice.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_cashSpend.getText().length() == 0){update_cashSpend.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (update_date.getText().length() ==0){update_date.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! Podałeś mniejszy licznik niż poprzednio! Oraz wszystkie puste pola!", Toast.LENGTH_SHORT).show();
                    break;

            }

            //updateRefuel();
            return true;
        }

        //tutaj deklarujemy działanie guzika back
        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
