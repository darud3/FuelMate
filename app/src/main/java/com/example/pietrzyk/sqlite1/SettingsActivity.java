package com.example.pietrzyk.sqlite1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.example.pietrzyk.sqlite1.R.id.settings_activity_fuel_units_selected;

public class SettingsActivity extends AppCompatActivity {

    //TODO: opcja litrow/galonow
    LinearLayout llayoutCurrency, llayoutDistanceUnits, llayoutFuelCap, llayoutFuelUnits, llayoutLicenses;
    TextView currencyTxt, currencySelected, distanceTxt, distanceSelected, fuelCapTxt, fuelCapSelected, fuelUsageTxt, fuelUsageSelected;
    String selectedCurrency, selectedDistance, selectedFuelCap, selectedFuelUsage;
    int idSelectedCurrency = 0, idSelectedDistance = 0, idSelectedFuelCap = 0, idSelectedFuelUnits = 0;
    Context context = this;
    DBHelper dbHelper;

    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_activ_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ustawienia");

        currencyTxt = (TextView) findViewById(R.id.settings_activity_currency_txt);
        currencySelected = (TextView) findViewById(R.id.settings_activity_currency_selected);
        distanceTxt = (TextView) findViewById(R.id.settings_activity_distance_units_txt);
        distanceSelected = (TextView) findViewById(R.id.settings_activity_distance_units_selected);
        fuelCapTxt = (TextView) findViewById(R.id.settings_activity_fuel_cap_txt);
        fuelCapSelected = (TextView) findViewById(R.id.settings_activity_fuel_cap_selected);
        fuelUsageTxt = (TextView) findViewById(R.id.settings_activity_fuel_units_txt);
        fuelUsageSelected = (TextView) findViewById(settings_activity_fuel_units_selected);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            //getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        setCurrentSettingsID();

        //Currency
        llayoutCurrency = (LinearLayout)findViewById(R.id.settings_layout_currency);
        llayoutCurrency.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                final String[] items = getResources().getStringArray(R.array.settings_activity_currency_array);

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle(getResources().getString(R.string.settings_activity_currency_dialog_title_pl)).setSingleChoiceItems(items, idSelectedCurrency, new DialogInterface.OnClickListener() { // 0 sprawia ze domyslnie jest ustawiona pierwsza opcja
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        selectedCurrency = items[which];

                    }

                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        setCurrencySelected(selectedCurrency);
                        saveSettings(selectedCurrency, distanceSelected.getText().toString(), fuelCapSelected.getText().toString(), fuelUsageSelected.getText().toString());
                        setCurrentSettingsID();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do sth
                    }

                });
                builder.show();

            }
        });

        //Disctance
        llayoutDistanceUnits = (LinearLayout) findViewById(R.id.settings_layout_distance_units);
        llayoutDistanceUnits.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                final String[] items = getResources().getStringArray(R.array.settings_activity_distance_array);

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle(getResources().getString(R.string.settings_activity_distance_dialog_title_pl)).setSingleChoiceItems(items, idSelectedDistance, new DialogInterface.OnClickListener() { // 0 sprawia ze domyslnie jest ustawiona pierwsza opcja
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        selectedDistance = items[which];

                    }

                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        setDistanceSelected(selectedDistance);
                        saveSettings(currencySelected.getText().toString(), selectedDistance, fuelCapSelected.getText().toString(), fuelUsageSelected.getText().toString());
                        setCurrentSettingsID();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do sth
                    }

                });
                builder.show();

            }
        });

        //Fuel Cap
        llayoutFuelCap = (LinearLayout) findViewById(R.id.settings_layout_fuel_cap);
        llayoutFuelCap.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                final String[] items = getResources().getStringArray(R.array.settings_activity_fuel_cap_array);

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle(getResources().getString(R.string.settings_activity_distance_dialog_title_pl)).setSingleChoiceItems(items, idSelectedFuelCap, new DialogInterface.OnClickListener() { // 0 sprawia ze domyslnie jest ustawiona pierwsza opcja
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        selectedFuelCap = items[which];

                    }

                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        setFuelCapSelected(selectedFuelCap);
                        saveSettings(currencySelected.getText().toString(), selectedDistance, selectedFuelCap, fuelUsageSelected.getText().toString());
                        setCurrentSettingsID();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do sth
                    }

                });
                builder.show();

            }
        });

        //Fuel Units
        llayoutFuelUnits = (LinearLayout) findViewById(R.id.settings_layout_fuel_units);
        llayoutFuelUnits.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                final String[] items = getResources().getStringArray(R.array.settings_activity_fuel_usage_array);

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle(getResources().getString(R.string.settings_activity_fuel_units_dialog_title_pl)).setSingleChoiceItems(items, idSelectedFuelUnits, new DialogInterface.OnClickListener() { // 0 sprawia ze domyslnie jest ustawiona pierwsza opcja
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        selectedFuelUsage = items[which];

                    }

                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        setFuelUsageSelected(selectedFuelUsage);
                        saveSettings(currencySelected.getText().toString(), distanceSelected.getText().toString(), fuelCapSelected.getText().toString(), selectedFuelUsage);
                        setCurrentSettingsID();

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do sth
                    }

                });
                builder.show();
            }
        });

        //Licenses
        llayoutLicenses = (LinearLayout) findViewById(R.id.settings_layout_licenses);
        llayoutLicenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, LicensesActivity.class);
                startActivity(myIntent);
            }
        });

        popUpSettingsFromDB();
    } //onCreate END

    public void setCurrencySelected(String selected){

        if(selected != null) {
            currencySelected.setText(selected);
        }
    }

    public void setDistanceSelected (String selected){

        if(selected != null){
            distanceSelected.setText(selected);
        }
    }

    public void setFuelCapSelected (String selected){

        if(selected != null){
            fuelCapSelected.setText(selected);
        }
    }

    public void setFuelUsageSelected(String selected){

        if(selected != null){
            fuelUsageSelected.setText(selected);
        }
    }

    public void popUpSettingsFromDB(){

        dbHelper = new DBHelper(context);

        Cursor cursor = dbHelper.getSettings();
        cursor.moveToFirst();

        if (cursor.moveToFirst()){

            currencySelected.setText(cursor.getString(0));
            distanceSelected.setText(cursor.getString(1));
            fuelCapSelected.setText(cursor.getString(2));
            fuelUsageSelected.setText(cursor.getString(3));
        }

        cursor.close();
        dbHelper.close();

    }

    public void setCurrentSettingsID(){

        dbHelper = new DBHelper(context);

        Cursor cursor = dbHelper.getSettings();
        cursor.moveToFirst();

        String currency, distance, FCap, FUsage;

        if (cursor.moveToFirst()){

            currency = cursor.getString(0);
            distance = cursor.getString(1);
            FCap = cursor.getString(2);
            FUsage = cursor.getString(3);
            if (currency.equals("PLN")) idSelectedCurrency = 0;
            if (currency.equals("EUR")) idSelectedCurrency = 1;
            if (currency .equals("USD")) idSelectedCurrency = 2;
            if (currency.equals("GBP")) idSelectedCurrency = 3;
            if (currency.equals("CZK")) idSelectedCurrency = 4;

            if (distance.equals("Kilometry")) idSelectedDistance = 0;
            if (distance.equals("Mile")) idSelectedDistance = 1;

            if(FCap.equals("Litry")) idSelectedFuelCap = 0;
            if(FCap.equals("Galony (UK)")) idSelectedFuelCap = 1;
            if(FCap.equals("Galony (US)")) idSelectedFuelCap = 2;

            if (FUsage.equals("l/100km")) idSelectedFuelUnits = 0;
            if (FUsage.equals("mpg (UK)")) idSelectedFuelUnits = 1;
            if (FUsage.equals("mpg (US)")) idSelectedFuelUnits = 2;
            //Toast.makeText(context,"Curr: "+idSelectedCurrency+", Dist: "+idSelectedDistance+", FUse: "+idSelectedFuelUnits,Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        dbHelper.close();
    }

    public void saveSettings(String currency, String distances, String fuel_cap, String fuel_usage){

        currency = currencySelected.getText().toString();
        distances = distanceSelected.getText().toString();
        fuel_cap = fuelCapSelected.getText().toString();
        fuel_usage = fuelUsageSelected.getText().toString();

        dbHelper = new DBHelper(context);
        sqLiteDatabase = dbHelper.getWritableDatabase();
        dbHelper.updateSettings(currency, distances, fuel_cap, fuel_usage, sqLiteDatabase);
        dbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        //tutaj deklarujemy działanie guzika back
        switch (item.getItemId()) {
            case android.R.id.home:

                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
