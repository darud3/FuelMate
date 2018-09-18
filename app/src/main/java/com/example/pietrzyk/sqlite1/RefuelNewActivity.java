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
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RefuelNewActivity extends AppCompatActivity { //Zamiast extends Activity aby był widoczny action barr dajemy AppCompatActivity

    EditText KmCounter, FuelAmount, FuelPrice, CashSpend, RefuelDate, RefuelNote;
    CheckBox FuelFull, FuelMissed;
    Context context = this;
    DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    Spinner CarRefuelSpinner;
    Calendar myCalendar = Calendar.getInstance();
    String new_refuel_selected_car_id;
    String sett_currency,sett_dist_unit,sett_fuel_cap , sett_fuel_usage_units ;
    String displayed_currency = "PLN", displayed_fuel_cap = "l", displayed_fuel_usage_units = "l/100km", displayed_dist_units = "km";
    Conventers conventer = new Conventers();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refuel_new);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.refuel_new_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        //wyświetlenie strzalke back
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            //getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        getSettings();

        KmCounter = (EditText) findViewById(R.id.NewRefuel_KmCounter_Input);
        FuelAmount = (EditText) findViewById(R.id.NewRefuel_FuelAmount_Input);
        FuelPrice = (EditText) findViewById(R.id.NewRefuel_FuelPrice_Input);
        CashSpend = (EditText) findViewById(R.id.NewRefuel_CashSpend_Input);
        RefuelDate = (EditText) findViewById(R.id.NewRefuel_Date_Input);
        FuelFull = (CheckBox) findViewById(R.id.NewRefuel_FuelFull_CheckBox);
        FuelMissed = (CheckBox) findViewById(R.id.NewRefuel_FuelMissed_CheckBox);
        CarRefuelSpinner = (Spinner)findViewById(R.id.NewRefuel_CarSpinner);
        RefuelNote = (EditText) findViewById(R.id.NewRefuel_Note_Input);

        KmCounter.setHintTextColor(getResources().getColor(R.color.textHintGrayLight));
        FuelAmount.setHintTextColor(getResources().getColor(R.color.textHintGrayLight));
        FuelPrice.setHintTextColor(getResources().getColor(R.color.textHintGrayLight));
        CashSpend.setHintTextColor(getResources().getColor(R.color.textHintGrayLight));

        //Spinner z samochodem wyciaganym z bazy, jesli brak samochodu to cofa nas do tworzenia samochodu.
        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();

        Cursor spinner_cursor = db.rawQuery("SELECT " + TableCar.NewCar.CAR_ID + " AS _id, " + TableCar.NewCar.CAR_NAME + " FROM " + TableCar.NewCar.TABLE_NAME, null);
        spinner_cursor.moveToFirst();

        if(spinner_cursor.moveToFirst()) {

            String[] from = new String[]{TableCar.NewCar.CAR_NAME};
            int[] to = new int[]{android.R.id.text1};

            //SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, spinner_cursor, from, to);
            //simpleCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.my_spinner_item, spinner_cursor, from, to);
            simpleCursorAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
            CarRefuelSpinner.setAdapter(simpleCursorAdapter);

            CarRefuelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    //Kolor tekstu w spinerze
                    //((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                    new_refuel_selected_car_id = String.valueOf(id);
                    lastKMCounterHint(new_refuel_selected_car_id);

                }

                public void onNothingSelected(AdapterView<?> parent) {
                    //cos tam
                }
            });
        }
        else {
            Log.e("SPINNER NEW CAR", "Kursor jest pusty");
            finish();
            Intent myIntent = new Intent(RefuelNewActivity.this, CarListActivity.class);
            startActivity(myIntent);
            Toast.makeText(context, "Nie masz jeszcze żadnego Pojazdu. Przed dodaniem tankowania musisz dodać pierwszy ojazd!", Toast.LENGTH_LONG).show();
        }
        //spinner_cursor.close();

        updateLabel_RefuelDate();

        //Zapobiega wyskakiwaniu klawiatury przy edittext date ale nie wpisuje daty po wybraniu jej z DatePickerDialog
        RefuelDate.setFocusableInTouchMode(false);
        RefuelDate.setFocusable(false);

        //Tworzymy wyskakujacy kalendarz do wpisywania daty
        final DatePickerDialog.OnDateSetListener DTPListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR,year);
                myCalendar.set(Calendar.MONTH,monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateLabel_RefuelDate();
            }
        };

        //wyswietla DataPickerDialog po kliknięciu w RefuelDate EditText
        RefuelDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RefuelNewActivity.this,
                        DTPListener,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


            FuelPrice.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (s.length() != 0) {
                        if (FuelAmount.getText().toString().trim().length() != 0)
                        //&& CashSpend.getText().toString().trim().length() == 0)
                        {

                            float FA = Float.parseFloat(FuelAmount.getText().toString());
                            float FP = Float.parseFloat(FuelPrice.getText().toString());
                            float CS = FA * FP;

                            String CS_str = String.valueOf(roundFloat(CS, 2));

                            CashSpend.setText(CS_str);
                        } else if (CashSpend.getText().toString().trim().length() != 0)

                        //&& FuelAmount.getText().toString().trim().length() == 0)
                        {

                            float FP = Float.parseFloat(FuelPrice.getText().toString());
                            float CS = Float.parseFloat(CashSpend.getText().toString());
                            float FA = CS / FP;

                            String FA_str = String.valueOf(roundFloat(FA, 2));

                            FuelAmount.setText(FA_str);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
    } // onCreate() END


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

    //aktualizuje RefuelDate edittext o aktualna lub wybrana przez nas date w odpowiednim formacie
    private void updateLabel_RefuelDate(){
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.US);
        RefuelDate.setText(simpleDateFormat.format(myCalendar.getTime()));
    }

    public void addNewRefuel(){

        // licznik, wychodzący zawsze w kilometrach
        Integer km_counter = 0;
        if (displayed_dist_units.equals("km")){
            km_counter  = Integer.parseInt(KmCounter.getText().toString());
        }
        if (displayed_dist_units.equals("mi")){
            double tmp = 0;
            km_counter = Integer.parseInt(KmCounter.getText().toString());
            tmp = conventer.milesToKilometers(km_counter);
            km_counter = (int) tmp;
        }

        // ilosc paliwa, wychodząca zawsze w litrach.
        Float fuel_amount = null;
        if (displayed_fuel_cap.equals("l")) {
            fuel_amount = Float.parseFloat(FuelAmount.getText().toString());
        }
        if (sett_fuel_cap.equals("Galony (UK)")){
            double tmp;
            fuel_amount = Float.parseFloat(FuelAmount.getText().toString());
            tmp = conventer.galUKtoLitres(fuel_amount);
            fuel_amount = (float) tmp;
        }
        if (sett_fuel_cap.equals("Galony (US)")){
            double tmp;
            fuel_amount = Float.parseFloat(FuelAmount.getText().toString());
            tmp = conventer.galUStoLitres(fuel_amount);
            fuel_amount = (float) tmp;
        }

        Float fuel_price = Float.parseFloat(FuelPrice.getText().toString());
        Float cash_spend = Float.parseFloat(CashSpend.getText().toString());
        String fuel_date = RefuelDate.getText().toString();
        String car = new_refuel_selected_car_id;
        String fuel_full, fuel_missed;
        String fuel_note = RefuelNote.getText().toString();
        Float fuel_per100;
        Integer fuel_km_diff;

        if (FuelFull.isChecked()) fuel_full = "1";
        else fuel_full = "0";

        if  (FuelMissed.isChecked()) fuel_missed = "1";
        else fuel_missed = "0";

        dbHelper = new DBHelper(context);
        sqLiteDatabase = dbHelper.getWritableDatabase();

        // Wszystko istotne do obliczenia zuzycia paliwa dodane w kilometrach i litrach
        dbHelper.addRefuel(fuel_amount, fuel_price, cash_spend, km_counter, fuel_date, car, fuel_full,fuel_missed, null, fuel_note, sqLiteDatabase);

        Toast.makeText(getBaseContext(),"Tankowanie dodane", Toast.LENGTH_SHORT).show();

        //Aktualizujemy zuzycie paliwa dla tankowania ponizej podanego KM_countera
        Cursor cur_last_fuel_usage = dbHelper.getLastFuelUsage(String.valueOf(new_refuel_selected_car_id),km_counter);
        cur_last_fuel_usage.moveToFirst();

        if (cur_last_fuel_usage.moveToFirst()) {

            fuel_per100 = cur_last_fuel_usage.getFloat(0);

                    if (/*fuel_per100 == null ||*/ fuel_per100 == 0 || fuel_per100 == 0.0) {

                        dbHelper.updateRefuelFuelUsage(new_refuel_selected_car_id, km_counter, null, sqLiteDatabase);
                    } else {

                        dbHelper.updateRefuelFuelUsage(new_refuel_selected_car_id, km_counter, fuel_per100, sqLiteDatabase);
                    }
        }

        // Wyciagamy róznic w kilometrach miedzy aktualnym i poprzednim tankowaniem
        Cursor cur_km_diff = dbHelper.getDiffBettwenLast2KMC(new_refuel_selected_car_id, km_counter);
        cur_km_diff.moveToFirst();

        if(cur_km_diff.moveToFirst()){

            fuel_km_diff = cur_km_diff.getInt(0);

            if (/*fuel_km_diff == null ||*/ fuel_km_diff == 0 || fuel_km_diff == 0.0) {

                dbHelper.updateRefuelKmDiff(new_refuel_selected_car_id,km_counter,null,sqLiteDatabase);
            }
            else {

                dbHelper.updateRefuelKmDiff(new_refuel_selected_car_id, km_counter, fuel_km_diff, sqLiteDatabase);
            }
        }
        dbHelper.close();
    }


    public void lastKMCounterHint(String car_id){

        int last_km_counter;
        dbHelper = new DBHelper(context);
        Cursor cursor = dbHelper.getLastKMCounter(car_id);
        cursor.moveToFirst();

        if (cursor.moveToFirst() && cursor.moveToFirst()){

            cursor.moveToFirst();
            last_km_counter = cursor.getInt(0);

            if (displayed_dist_units.equals("km")) {
                KmCounter.setHint(String.valueOf(last_km_counter + " " + displayed_dist_units));
            }
            if (displayed_dist_units.equals("mi")){
                double last_km_counter_dbl = 0;
                last_km_counter_dbl = conventer.kilometersToMiles(last_km_counter);
                last_km_counter = (int) last_km_counter_dbl;
                //KmCounter.setHint(String.valueOf(roundDouble(last_km_counter_dbl,0)) + " " + displayed_dist_units);
                KmCounter.setHint(String.valueOf(last_km_counter) + " " + displayed_dist_units);
            }

            FuelAmount.setHint("0.00 " + displayed_fuel_cap);
            CashSpend.setHint("0.00 " + displayed_currency);
            FuelPrice.setHint("0.00 " + displayed_currency + "/" + displayed_fuel_cap);
        }
        else {
            KmCounter.setHint("Podaj licznik");
        }
        cursor.close();
    }

    //sprawdzanie czy podany licznik jest mniejszy lub rowny ostatniego zapisanego w bazie
    public int lastKMCounterCheck(int km_counter){

        int last_km_counter;
        int state = 0;
        dbHelper = new DBHelper(context);
        Cursor cursor = dbHelper.getLastKMCounter(new_refuel_selected_car_id);
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

    //sprawdzam czy jest juz wpisane jakieś tankowanie
    public boolean KMCounterIsNull(){
        dbHelper = new DBHelper(context);
        Cursor cursor = dbHelper.getLastKMCounter(new_refuel_selected_car_id);

        if (cursor.moveToFirst()){
            return true;
        }
        else return false;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_refuel_new_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*
        if (id == R.id.action_refuel_list) {
            return true;
        }
        */

        if (id == R.id.action_add_refuel){

            //Sprawdzamy czy ktores z pol jest puste, jesli tak monit o bledzie
            int state = 0;
            Boolean KMflag;
            KMflag = KMCounterIsNull();

            //Sprawdzam czy ostatni zapisany w bazie licznik jest mniejszy od podanego
            if(KMflag != null){
                if(KmCounter.getText().toString().length() > 0)
                state = lastKMCounterCheck(Integer.valueOf(KmCounter.getText().toString()));
            }

            if (KmCounter.getText().length() == 0 ){state++;}
            if (FuelAmount.getText().length() == 0){state++;}
            if (FuelPrice.getText().length() == 0){state++;}
            if (CashSpend.getText().length() == 0){state++;}
            if (RefuelDate.getText().length() ==0){state++;}

            switch (state) {

                case 0:
                    addNewRefuel();
                    //clearRefuelEditText();
                    finish();
                    Intent myIntent = new Intent(RefuelNewActivity.this,RefuelListActivity.class);
                    startActivity(myIntent);
                    break;
                case 1:
                    if (KmCounter.getText().length() == 0 ){KmCounter.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (FuelAmount.getText().length() == 0){FuelAmount.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (FuelPrice.getText().length() == 0){FuelPrice.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (CashSpend.getText().length() == 0){CashSpend.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (RefuelDate.getText().length() ==0){RefuelDate.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! co najmniej jedno pole jest puste! Uzpełnij!", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    if (KmCounter.getText().length() == 0 ){KmCounter.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (FuelAmount.getText().length() == 0){FuelAmount.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (FuelPrice.getText().length() == 0){FuelPrice.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (CashSpend.getText().length() == 0){CashSpend.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (RefuelDate.getText().length() ==0){RefuelDate.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! conajmniej dwa pola są puste! Uzpełnij!", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    if (KmCounter.getText().length() == 0 ){KmCounter.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (FuelAmount.getText().length() == 0){FuelAmount.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (FuelPrice.getText().length() == 0){FuelPrice.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (CashSpend.getText().length() == 0){CashSpend.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (RefuelDate.getText().length() ==0){RefuelDate.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! co najmniej trzy pola są puste! Uzpełnij!", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    if (KmCounter.getText().length() == 0 ){KmCounter.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (FuelAmount.getText().length() == 0){FuelAmount.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (FuelPrice.getText().length() == 0){FuelPrice.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (CashSpend.getText().length() == 0){CashSpend.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (RefuelDate.getText().length() ==0){RefuelDate.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! co najmniej cztery pola są puste! Uzpełnij!", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    if (KmCounter.getText().length() == 0 ){KmCounter.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (FuelAmount.getText().length() == 0){FuelAmount.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (FuelPrice.getText().length() == 0){FuelPrice.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (CashSpend.getText().length() == 0){CashSpend.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (RefuelDate.getText().length() ==0){RefuelDate.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! wszystkie pola są puste! Uzpełnij!", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    KmCounter.setError(getString(R.string.refuel_new_update_editext_kmcounter_too_low_PL));
                    if (FuelAmount.getText().length() == 0){FuelAmount.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (FuelPrice.getText().length() == 0){FuelPrice.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (CashSpend.getText().length() == 0){CashSpend.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (RefuelDate.getText().length() ==0){RefuelDate.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! Podałeś mniejszy licznik niż poprzednio!", Toast.LENGTH_SHORT).show();
                    break;
                case 7:
                    KmCounter.setError(getString(R.string.refuel_new_update_editext_kmcounter_too_low_PL));
                    if (FuelAmount.getText().length() == 0){FuelAmount.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (FuelPrice.getText().length() == 0){FuelPrice.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (CashSpend.getText().length() == 0){CashSpend.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (RefuelDate.getText().length() ==0){RefuelDate.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! Podałeś mniejszy licznik niż poprzednio! Oraz przynajmniej jedno puste pole!", Toast.LENGTH_SHORT).show();
                    break;
                case 8:
                    KmCounter.setError(getString(R.string.refuel_new_update_editext_kmcounter_too_low_PL));
                    if (FuelAmount.getText().length() == 0){FuelAmount.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (FuelPrice.getText().length() == 0){FuelPrice.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (CashSpend.getText().length() == 0){CashSpend.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (RefuelDate.getText().length() ==0){RefuelDate.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! Podałeś mniejszy licznik niż poprzednio! Oraz przynajmniej dwa puste pola!", Toast.LENGTH_SHORT).show();
                    break;
                case 9:
                    if (KmCounter.getText().length() == 0 ){KmCounter.setError(getString(R.string.refuel_new_update_editext_kmcounter_too_low_PL));}
                    if (FuelAmount.getText().length() == 0){FuelAmount.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (FuelPrice.getText().length() == 0){FuelPrice.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (CashSpend.getText().length() == 0){CashSpend.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (RefuelDate.getText().length() ==0){RefuelDate.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! Podałeś mniejszy licznik niż poprzednio! Oraz przynajmniej trzy puste pola!", Toast.LENGTH_SHORT).show();
                    break;
                case 10:
                    KmCounter.setError(getString(R.string.refuel_new_update_editext_kmcounter_too_low_PL));
                    if (FuelAmount.getText().length() == 0){FuelAmount.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (FuelPrice.getText().length() == 0){FuelPrice.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (CashSpend.getText().length() == 0){CashSpend.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (RefuelDate.getText().length() ==0){RefuelDate.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! Podałeś mniejszy licznik niż poprzednio! Oraz przynajmniej cztery puste pola!", Toast.LENGTH_SHORT).show();
                    break;
                case 11:
                    KmCounter.setError(getString(R.string.refuel_new_update_editext_kmcounter_too_low_PL));
                    if (FuelAmount.getText().length() == 0){FuelAmount.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (FuelPrice.getText().length() == 0){FuelPrice.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (CashSpend.getText().length() == 0){CashSpend.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if (RefuelDate.getText().length() ==0){RefuelDate.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! Podałeś mniejszy licznik niż poprzednio! Oraz wszystkie puste pola!", Toast.LENGTH_SHORT).show();
                    break;

            }
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
