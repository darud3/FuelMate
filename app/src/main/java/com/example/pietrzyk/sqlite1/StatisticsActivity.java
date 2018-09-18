package com.example.pietrzyk.sqlite1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

public class StatisticsActivity extends AppCompatActivity {

    Context context = this;
    //SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;
    String sett_currency,sett_dist_unit,sett_fuel_cap , sett_fuel_usage_units ;
    String displayed_currency = "PLN", displayed_fuel_cap = "l", displayed_fuel_usage_units = "l/100km", displayed_dist_units = "km";
    Conventers conventer = new Conventers();
    long selected_car_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_activity);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.statistics_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        //wyświetlenie strzalke back
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            //getSupportActionBar().setDisplayShowTitleEnabled(false); // usuwa tytuł activity z toolbara
        }

        getSettings();

        Spinner CarRefuelListSpinner = (Spinner) findViewById(R.id.statistic_car_spinner);

        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();

        //Inicializacja spinnera z bazy danych
        Cursor spinner_cursor = db.rawQuery("SELECT " + TableCar.NewCar.CAR_ID + " AS _id, " + TableCar.NewCar.CAR_NAME + " FROM " + TableCar.NewCar.TABLE_NAME, null);

        if(spinner_cursor.moveToFirst()) {

            Log.e("SPINNER NEW CAR", "Kursor nie jest pusty.");

            String[] from = new String[]{TableCar.NewCar.CAR_NAME};
            int[] to = new int[]{android.R.id.text1};

            //SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, spinner_cursor, from, to);
            SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.my_spinner_item, spinner_cursor, from, to);
            //simpleCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            simpleCursorAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
            CarRefuelListSpinner.setAdapter(simpleCursorAdapter);

            CarRefuelListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    //((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                    //((TextView) parent.getChildAt(0)).setTextSize(18);
                    //nasłuchuje na zmianę id samochodu.
                    selected_car_id = id;
                    showCarStats(String.valueOf(selected_car_id));

                }

                public void onNothingSelected(AdapterView<?> parent) {
                    //cos tam
                }
            });
        }
        else {
            Log.e("SPINNER NEW CAR", "Kursor jest pusty");
            Intent myIntent = new Intent(StatisticsActivity.this, CarListActivity.class);
            startActivity(myIntent);
            Toast.makeText(context, "Nie masz jeszcze żadnego Pojazdu. Przed dodaniem tankowania musisz dodać pierwszy Pojazd!", Toast.LENGTH_SHORT).show();
        }

    } // onCreate end


    public void showCarStats(String car_id){

        //Ogolne
        TextView StatOvKMcounter = (TextView)findViewById(R.id.stat_ov_kmCounter);
        TextView StatOvKMcounterTotalCount = (TextView)findViewById(R.id.stat_ov_kmCounter_totalCount);
        TextView StatOvFuelTotalCount =(TextView)findViewById(R.id.stat_ov_fuel_totalCount);
        TextView StatOvFuelCostTotalCount = (TextView)findViewById(R.id.stat_ov_fuelCost_totalCount);
        TextView StatOvCostCostsTotalCount = (TextView)findViewById(R.id.stat_ov_costCosts_totalCount);
        TextView StatOvRefuelQuantity = (TextView)findViewById(R.id.stat_ov_refuel_quantity);
        TextView StatOvCostQuantity = (TextView)findViewById(R.id.stat_ov_cost_quantity);
        TextView StatOvWholeCashCost = (TextView)findViewById(R.id.stat_ov_whole_cash_cost);

        //Tankowania
        TextView StatRfThisMthQuantity = (TextView)findViewById(R.id.stat_rf_this_mth_quantity);
        TextView StatRfPrevMthQuantity = (TextView)findViewById(R.id.stat_rf_prev_mth_quantity);
        TextView StatRfThisYrQuantity = (TextView)findViewById(R.id.stat_rf_this_yr_quantity);
        TextView StatRfPrevYrQuantity = (TextView)findViewById(R.id.stat_rf_prev_yr_quantity);
        TextView StatRfBiggest = (TextView)findViewById(R.id.stat_rf_biggest);
        TextView StatRfSmallest = (TextView)findViewById(R.id.stat_rf_smallest);

        //Paliwo
        TextView StatFlThisMth = (TextView)findViewById(R.id.stat_fl_this_mth);
        TextView StatFlPrevMth = (TextView)findViewById(R.id.stat_fl_prev_mth);
        TextView StatFlThisYr = (TextView)findViewById(R.id.stat_fl_this_yr);
        TextView StatFlPrevYr = (TextView)findViewById(R.id.stat_fl_prev_yr);
        TextView StatFlLast12Mth = (TextView)findViewById(R.id.stat_fl_last_12mth);

        //Wydatki (calosc)
        TextView StatCaWholeCashCost = (TextView)findViewById(R.id.stats_ca_whole_cash_cost);
        TextView StatCaThisMth = (TextView)findViewById(R.id.stats_ca_this_mth);
        TextView StatCaPrevMth = (TextView)findViewById(R.id.stats_ca_prev_mth);
        TextView StatCaThisYr = (TextView)findViewById(R.id.stats_ca_this_yr);
        TextView StatCaPrevYr = (TextView)findViewById(R.id.stats_ca_prev_yr);
        TextView StatCaLast12Mth = (TextView)findViewById(R.id.stats_ca_last_12mth);

        //Wydatki (paliwo)
        TextView StatCfThisMth = (TextView)findViewById(R.id.stats_cf_this_mth);
        TextView StatCfPrevMth = (TextView)findViewById(R.id.stats_cf_prev_mth);
        TextView StatCfThisYr = (TextView)findViewById(R.id.stat_cf_this_yr);
        TextView StatCfPrevYr = (TextView)findViewById(R.id.stat_cf_prev_yr);
        TextView StatCfLast12Mth = (TextView)findViewById(R.id.stat_cf_last_12_mth);
        TextView StatCfHighestBill = (TextView)findViewById(R.id.stats_cf_highest_bill);
        TextView StatCfLowestBill = (TextView)findViewById(R.id.stats_cf_lowest_bill);
        TextView StatCfHighestFuelPrice = (TextView)findViewById(R.id.stats_cf_highest_fuel_price);
        TextView StatCfLowestFuelPrice = (TextView)findViewById(R.id.stats_cf_lowest_fuel_price);

        //Wszystko (koszty)
        TextView StatCcThisMth = (TextView)findViewById(R.id.stats_cc_this_mth);
        TextView StatCcPrevMth = (TextView)findViewById(R.id.stats_cc_prev_mth);
        TextView StatCcThisYr = (TextView)findViewById(R.id.stats_cc_this_yr);
        TextView StatCcPrevYr = (TextView)findViewById(R.id.stats_cc_prev_yr);
        TextView StatCcLast12Mth = (TextView)findViewById(R.id.stats_cc_last_12_mth);

        //Srednie
        TextView StatAvgDialyCost = (TextView)findViewById(R.id.stats_avg_dialy_cost);
        TextView StatAvgMonthlyCost = (TextView)findViewById(R.id.stats_avg_monthly_cost);
        TextView StatAvgDialyMileage = (TextView)findViewById(R.id.stats_avg_dialy_mileage);
        TextView StatAvgMonthlyMileage = (TextView)findViewById(R.id.stats_avg_monthly_mileage);
        TextView StatAvgFuelUsage = (TextView)findViewById(R.id.stats_avg_fuel_use);
        TextView StatAvgRefuelCap = (TextView)findViewById(R.id.stats_avg_refuel_cap);
        TextView StatAvgRefuelBill = (TextView)findViewById(R.id.stats_avg_refuel_cost);

        dbHelper = new DBHelper(context);

        // Ogolne @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

        //Licznik
        int stat_kmCounter = 0;
        Cursor cursor1 = dbHelper.getLastKMCounter(car_id);
        cursor1.moveToFirst();

        if (cursor1.moveToFirst()){

            stat_kmCounter = cursor1.getInt(0);

            if (displayed_dist_units.equals("mi")) {
                double tmp;
                tmp = conventer.kilometersToMiles(stat_kmCounter);
                stat_kmCounter =(int) tmp;
                StatOvKMcounter.setText(String.valueOf(stat_kmCounter) + " " + displayed_dist_units);
            }
            else {
                StatOvKMcounter.setText(String.valueOf(stat_kmCounter) + " " + displayed_dist_units);
            }
        }
        else {
            StatOvKMcounter.setText(R.string.statistic_empty_cursor_error_PL);
        }
        cursor1.close();

        //Calkowity zliczony dystans
        int stat_KMcounterTotalCount = 0;
        Cursor cursor2 = dbHelper.getTotalKMcounted(car_id);
        cursor2.moveToFirst();
        if (cursor2.moveToFirst()){

            stat_KMcounterTotalCount = cursor2.getInt(0);

            if (displayed_dist_units.equals("mi")){
                double tmp;
                tmp = conventer.kilometersToMiles(stat_KMcounterTotalCount);
                stat_KMcounterTotalCount =(int) tmp;
                StatOvKMcounterTotalCount.setText(String.valueOf(stat_KMcounterTotalCount) + " " + displayed_dist_units);
            }
            else StatOvKMcounterTotalCount.setText(String.valueOf(stat_KMcounterTotalCount) + " " + displayed_dist_units);
        }
        else {
            StatOvKMcounterTotalCount.setText("Brak Danych");
        }
        cursor2.close();

        //Całkowita ilosc paliwa
        float stat_fuelTotalCount = 0;
        Cursor cursor3 = dbHelper.getTotalFuelCount(car_id);
        cursor3.moveToFirst();
        if (cursor3.moveToFirst()){

            stat_fuelTotalCount = cursor3.getFloat(0);

            if (displayed_fuel_cap.equals("l")) { StatOvFuelTotalCount.setText(String.valueOf(roundFloat(stat_fuelTotalCount,2)) + " " + displayed_fuel_cap); }
            if (sett_fuel_cap.equals("Galony (UK)")){
                double tmp;
                tmp = conventer.litresToGalUK(stat_fuelTotalCount);
                stat_fuelTotalCount =(int) tmp;
                StatOvFuelTotalCount.setText(String.valueOf(roundFloat(stat_fuelTotalCount,2)) + " " + displayed_fuel_cap);
            }
            if (sett_fuel_cap.equals("Galony (US)")){
                double tmp;
                tmp = conventer.litresToGalUS(stat_fuelTotalCount);
                stat_fuelTotalCount =(int) tmp;
                StatOvFuelTotalCount.setText(String.valueOf(roundFloat(stat_fuelTotalCount,2)) + " " + displayed_fuel_cap);
            }
        }
        else {
            StatOvFuelTotalCount.setText("Brak Danych");
        }
        cursor3.close();

        //Całkowity koszt paliwa
        float stat_fuelTotalCostCount = 0;
        Cursor cursor4 = dbHelper.getTotalFuelCostCount(car_id);
        cursor4.moveToFirst();
        if (cursor4.moveToFirst()){
            stat_fuelTotalCostCount = cursor4.getFloat(0);
            StatOvFuelCostTotalCount.setText(String.valueOf(roundFloat(stat_fuelTotalCostCount,2)) + " " + displayed_currency);
        }
        else {
            StatOvFuelCostTotalCount.setText("Brak Danych");
        }
        cursor4.close();

        //Suma wydatków na 'koszt'
        float stat_costsTotalCostCount = 0;
        Cursor cursor5 = dbHelper.getTotalCostsCostCount(car_id);
        cursor5.moveToFirst();
        if (cursor5.moveToFirst()){
            stat_costsTotalCostCount = cursor5.getFloat(0);
            StatOvCostCostsTotalCount.setText(String.valueOf(roundFloat(stat_costsTotalCostCount,2)) + " " + displayed_currency);
        }
        else {
            StatOvCostCostsTotalCount.setText("Brak Danych");
        }
        cursor5.close();

        //Ilosc tankowan
        int stat_refuelQuantity = 0;
        Cursor cursor6 = dbHelper.getRefuelQuantity(car_id);
        cursor6.moveToFirst();
        if (cursor6.moveToFirst()){
            stat_refuelQuantity = cursor6.getInt(0);
            StatOvRefuelQuantity.setText(String.valueOf(stat_refuelQuantity));
        }
        else {
            StatOvRefuelQuantity.setText("Brak Danych");
        }
        cursor6.close();

        //Ilosc kosztow
        int stat_costQuantity = 0;
        Cursor cursor7 = dbHelper.getCostQuantity(car_id);
        cursor7.moveToFirst();
        if (cursor7.moveToFirst()){
            stat_costQuantity = cursor7.getInt(0);
            StatOvCostQuantity.setText(String.valueOf(stat_costQuantity));
        }
        else {
            StatOvCostQuantity.setText("Brak Danych");
        }
        cursor7.close();

        //Całkowity koszt
        StatOvWholeCashCost.setText("0");
        float stat_wholeCashCost = stat_fuelTotalCostCount + stat_costsTotalCostCount;
        if (stat_wholeCashCost != 0 ){
            StatOvWholeCashCost.setText(String.valueOf(roundFloat(stat_wholeCashCost,2)) + " " + displayed_currency);
        }

        // Tankowania @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

        //tankowania ten miesiac
        int stat_refuelQuantitiThisMth = 0;
        Cursor cursor9 = dbHelper.getRefuelQuantityThisMth(car_id);
        cursor9.moveToFirst();
        if (cursor9.moveToFirst()){
            stat_refuelQuantitiThisMth = cursor9.getInt(0);
            StatRfThisMthQuantity.setText(String.valueOf(stat_refuelQuantitiThisMth));
        }
        else{
            StatRfThisMthQuantity.setText("Brak Danych");
        }
        cursor9.close();

        //tankowanie poprzedni miesiac
        int stat_refuelQuantitiPrevMth = 0;
        Cursor cursor10 = dbHelper.getRefuelQuantityPrevMth(car_id);
        cursor10.moveToFirst();
        if (cursor10.moveToFirst()){
            stat_refuelQuantitiPrevMth = cursor10.getInt(0);
            StatRfPrevMthQuantity.setText(String.valueOf(stat_refuelQuantitiPrevMth));
        }
        else {
            StatRfPrevMthQuantity.setText(R.string.statistic_empty_cursor_error_PL);
        }
        cursor10.close();

        //tankowanie ten rok
        int stat_refuelQuantitiThisYr = 0;
        Cursor cursor11 = dbHelper.getRefuelQuantityThisYr(car_id);
        cursor11.moveToFirst();
        if (cursor11.moveToFirst()){
            stat_refuelQuantitiThisYr = cursor11.getInt(0);
            StatRfThisYrQuantity.setText(String.valueOf(stat_refuelQuantitiThisYr));
        }
        else {
            StatRfThisYrQuantity.setText(R.string.statistic_empty_cursor_error_PL);
        }
        cursor11.close();

        //tankowanie poprzedni rok
        int stat_refuelQuantitiPrevYr = 0;
        Cursor cursor12 = dbHelper.getRefuelQuantityPrevYr(car_id);
        cursor12.moveToFirst();
        if (cursor12.moveToFirst()){
            stat_refuelQuantitiPrevYr = cursor12.getInt(0);
            StatRfPrevYrQuantity.setText(String.valueOf(stat_refuelQuantitiPrevYr));
        }
        else {
            StatRfPrevYrQuantity.setText(R.string.statistic_empty_cursor_error_PL);
        }
        cursor12.close();

        //Największe tankowanie
        float stat_refuelBiggest = 0;
        Cursor cursor13 = dbHelper.getRefuelBiggest(car_id);
        cursor13.moveToFirst();
        if (cursor13.moveToFirst()){

            stat_refuelBiggest = cursor13.getFloat(0);

            if (displayed_fuel_cap.equals("l")) { StatRfBiggest.setText(String.valueOf(roundFloat(stat_refuelBiggest,2)) + " " + displayed_fuel_cap); }
            if (sett_fuel_cap.equals("Galony (UK)")){
                double tmp;
                tmp = conventer.litresToGalUK(stat_refuelBiggest);
                stat_refuelBiggest = (int) tmp;
                StatRfBiggest.setText(String.valueOf(roundFloat(stat_refuelBiggest,2)) + " " + displayed_fuel_cap);
            }
            if (sett_fuel_cap.equals("Galony (US)")){
                double tmp;
                tmp = conventer.litresToGalUS(stat_refuelBiggest);
                stat_refuelBiggest = (int) tmp;
                StatRfBiggest.setText(String.valueOf(roundFloat(stat_refuelBiggest,2)) + " " + displayed_fuel_cap);
            }



        }
        else {
            StatRfBiggest.setText(R.string.statistic_empty_cursor_error_PL);
        }
        cursor13.close();

        //Najmniejsze tankowanie
        float stat_refuelSmallest = 0;
        Cursor cursor14 = dbHelper.getRefuelSmallest(car_id);
        cursor14.moveToFirst();
        if (cursor14.moveToFirst()){

            stat_refuelSmallest = cursor14.getFloat(0);

            if (displayed_fuel_cap.equals("l")) { StatRfSmallest.setText(String.valueOf(roundFloat(stat_refuelSmallest,2)) + " " + displayed_fuel_cap); }
            if (sett_fuel_cap.equals("Galony (UK)")) {
                double tmp;
                tmp = conventer.litresToGalUK(stat_refuelSmallest);
                stat_refuelSmallest = (int) tmp;
                StatRfSmallest.setText(String.valueOf(roundFloat(stat_refuelSmallest,2)) + " " + displayed_fuel_cap);
            }
            if (sett_fuel_cap.equals("Galony (US)")) {
                double tmp;
                tmp = conventer.litresToGalUS(stat_refuelSmallest);
                stat_refuelSmallest = (int) tmp;
                StatRfSmallest.setText(String.valueOf(roundFloat(stat_refuelSmallest,2)) + " " + displayed_fuel_cap);
            }
        }
        else {
            StatRfSmallest.setText(R.string.statistic_empty_cursor_error_PL);
        }
        cursor14.close();

        // Paliwo @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

        //Paliwo ten miesiac
        float stat_fuelThisMth = 0;
        Cursor cursor15 = dbHelper.getFuelAmountThisMth(car_id);
        cursor15.moveToFirst();
        if (cursor15.moveToFirst()){

            stat_fuelThisMth = cursor15.getFloat(0);

            if (displayed_fuel_cap.equals("l")) { StatFlThisMth.setText(String.valueOf(roundFloat(stat_fuelThisMth,2)) + " " + displayed_fuel_cap); }
            if (sett_fuel_cap.equals("Galony (UK)")) {
                double tmp;
                tmp = conventer.litresToGalUK(stat_fuelThisMth);
                stat_fuelThisMth = (int) tmp;
                StatFlThisMth.setText(String.valueOf(roundFloat(stat_fuelThisMth,2)) + " " + displayed_fuel_cap);
            }
            if (sett_fuel_cap.equals("Galony (US)")) {
                double tmp;
                tmp = conventer.litresToGalUS(stat_fuelThisMth);
                stat_fuelThisMth = (int) tmp;
                StatFlThisMth.setText(String.valueOf(roundFloat(stat_fuelThisMth,2)) + " " + displayed_fuel_cap);
            }
        }
        else {
            StatFlThisMth.setText(R.string.statistic_empty_cursor_error_PL);
        }
        cursor15.close();

        //Paliwo poprzedni miesiac
        float stat_fuelPrevMth = 0;
        Cursor cursor16 = dbHelper.getFuelAmountPrevMth(car_id);
        cursor16.moveToFirst();
        if (cursor16.moveToFirst()){

            stat_fuelPrevMth = cursor16.getFloat(0);

            if (displayed_fuel_cap.equals("l")) { StatFlPrevMth.setText(String.valueOf(roundFloat(stat_fuelPrevMth,2)) + " " + displayed_fuel_cap); }
            if (sett_fuel_cap.equals("Galony (UK)")) {
                double tmp;
                tmp = conventer.litresToGalUK(stat_fuelPrevMth);
                stat_fuelPrevMth = (int) tmp;
                StatFlPrevMth.setText(String.valueOf(roundFloat(stat_fuelPrevMth,2)) + " " + displayed_fuel_cap);
            }
            if (sett_fuel_cap.equals("Galony (US)")) {
                double tmp;
                tmp = conventer.litresToGalUS(stat_fuelPrevMth);
                stat_fuelPrevMth = (int) tmp;
                StatFlPrevMth.setText(String.valueOf(roundFloat(stat_fuelPrevMth,2)) + " " + displayed_fuel_cap);
            }
        }
        else {
            StatFlPrevMth.setText(R.string.statistic_empty_cursor_error_PL);
        }
        cursor16.close();

        //Paliwo ten rok
        float stat_fuelThisYr = 0;
        Cursor cursor17 = dbHelper.getFuelAmountThisYr(car_id);
        cursor17.moveToFirst();
        if (cursor17.moveToFirst()){

            stat_fuelThisYr = cursor17.getFloat(0);

            if (displayed_fuel_cap.equals("l")) { StatFlThisYr.setText(String.valueOf(roundFloat(stat_fuelThisYr,2)) + " " + displayed_fuel_cap); }
            if (sett_fuel_cap.equals("Galony (UK)")) {
                double tmp;
                tmp = conventer.litresToGalUK(stat_fuelThisYr);
                stat_fuelThisYr = (int) tmp;
                StatFlThisYr.setText(String.valueOf(roundFloat(stat_fuelThisYr,2)) + " " + displayed_fuel_cap);
            }
            if (sett_fuel_cap.equals("Galony (US)")) {
                double tmp;
                tmp = conventer.litresToGalUS(stat_fuelThisYr);
                stat_fuelThisYr = (int) tmp;
                StatFlThisYr.setText(String.valueOf(roundFloat(stat_fuelThisYr,2)) + " " + displayed_fuel_cap);
            }
        }
        else {
            StatFlThisYr.setText(R.string.statistic_empty_cursor_error_PL);
        }
        cursor17.close();

        //Paliwo poprzedni rok
        float stat_fuelPrevYr = 0;
        Cursor cursor18 = dbHelper.getFuelAmountPrevYr(car_id);
        cursor18.moveToFirst();
        if (cursor18.moveToFirst()){

            stat_fuelPrevYr = cursor18.getFloat(0);

            if (displayed_fuel_cap.equals("l")) { StatFlPrevYr.setText(String.valueOf(roundFloat(stat_fuelPrevYr,2)) + " " + displayed_fuel_cap); }
            if (sett_fuel_cap.equals("Galony (UK)")) {
                double tmp;
                tmp = conventer.litresToGalUK(stat_fuelPrevYr);
                stat_fuelPrevYr = (int) tmp;
                StatFlPrevYr.setText(String.valueOf(roundFloat(stat_fuelPrevYr,2)) + " " + displayed_fuel_cap);
            }
            if (sett_fuel_cap.equals("Galony (US)")) {
                double tmp;
                tmp = conventer.litresToGalUS(stat_fuelPrevYr);
                stat_fuelPrevYr = (int) tmp;
                StatFlPrevYr.setText(String.valueOf(roundFloat(stat_fuelPrevYr,2)) + " " + displayed_fuel_cap);
            }
        }
        else {
            StatFlPrevYr.setText(R.string.statistic_empty_cursor_error_PL);
        }
        cursor18.close();

        //Paliwo ostatnie 12 miesięcy
        float stat_fuelLast12Mth = 0;
        //StatFlLast12Mth.setText("0.0 " + displayed_fuel_cap);
        Cursor cursor30 = dbHelper.getFuelAmountLast12Mth(car_id);
        cursor30.moveToFirst();
        if (cursor30.moveToFirst()){

            stat_fuelLast12Mth = cursor30.getFloat(0);

            if (displayed_fuel_cap.equals("l")) { StatFlLast12Mth.setText(String.valueOf(roundFloat(stat_fuelLast12Mth,2)) + " " + displayed_fuel_cap); }
            if (sett_fuel_cap.equals("Galony (UK)")) {
                double tmp;
                tmp = conventer.litresToGalUK(stat_fuelLast12Mth);
                stat_fuelLast12Mth = (int) tmp;
                StatFlLast12Mth.setText(String.valueOf(roundFloat(stat_fuelLast12Mth,2)) + " " + displayed_fuel_cap);
            }
            if (sett_fuel_cap.equals("Galony (US)")) {
                double tmp;
                tmp = conventer.litresToGalUS(stat_fuelLast12Mth);
                stat_fuelLast12Mth = (int) tmp;
                StatFlLast12Mth.setText(String.valueOf(roundFloat(stat_fuelLast12Mth,2)) + " " + displayed_fuel_cap);
            }
        }
        else StatFlLast12Mth.setText(R.string.statistic_empty_cursor_error_PL);
        cursor30.close();


        // Koszty (wszystkie) @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

        //Wszystkie zliczone wydatki
        StatCaWholeCashCost.setText("0 " + displayed_currency);
        float stat_CaWholeCashCost = stat_fuelTotalCostCount + stat_costsTotalCostCount;
        if (stat_wholeCashCost != 0 ){
            StatCaWholeCashCost.setText(String.valueOf(roundFloat(stat_CaWholeCashCost,2)) + " " + displayed_currency);
        }

        //Wszystkie zliczone wydatki w tym miesiącu
        float stat_CaWholeCashCostThisMth = 0;
        float stat_CaWholeRefuelCostThisMth= 0;

        Cursor cur_wholeCostCostsThisMth = dbHelper.getCostCostsThisMth(car_id);
        cur_wholeCostCostsThisMth.moveToFirst();

        if (cur_wholeCostCostsThisMth.moveToFirst()){
            stat_CaWholeCashCostThisMth = cur_wholeCostCostsThisMth.getFloat(0);
        }
        cur_wholeCostCostsThisMth.close();

        Cursor cur_whoheRefuelCostsThisMth = dbHelper.getRefuelCostsThisMth(car_id);
        cur_whoheRefuelCostsThisMth.moveToFirst();

        if (cur_whoheRefuelCostsThisMth.moveToFirst()){
            stat_CaWholeRefuelCostThisMth = cur_whoheRefuelCostsThisMth.getFloat(0);
        }
        cur_whoheRefuelCostsThisMth.close();

        StatCaThisMth.setText(String.valueOf(stat_CaWholeCashCostThisMth + stat_CaWholeRefuelCostThisMth + " " + displayed_currency));

        //Wszystkie zliczone wydatki w poprz miesiącu
        float stat_CaWholeCashCostPrevMth = 0;
        float stat_CaWholeRefuelCostPrevMth= 0;

        Cursor cur_wholeCostCostsPrevMth = dbHelper.getCostCostsPrevMth(car_id);
        cur_wholeCostCostsPrevMth.moveToFirst();

        if (cur_wholeCostCostsPrevMth.moveToFirst()){
            stat_CaWholeCashCostPrevMth = cur_wholeCostCostsPrevMth.getFloat(0);
        }
        cur_wholeCostCostsPrevMth.close();

        Cursor cur_whoheRefuelCostsPrevMth = dbHelper.getRefuelCostsPrevMth(car_id);
        cur_whoheRefuelCostsPrevMth.moveToFirst();

        if (cur_whoheRefuelCostsPrevMth.moveToFirst()){
            stat_CaWholeRefuelCostPrevMth = cur_whoheRefuelCostsPrevMth.getFloat(0);
        }
        cur_whoheRefuelCostsPrevMth.close();

        StatCaPrevMth.setText(String.valueOf(stat_CaWholeCashCostPrevMth + stat_CaWholeRefuelCostPrevMth + " " + displayed_currency));

        //Wszystkie zliczone wydatki w tym roku
        float stat_CaWholeCashCostThisYr = 0;
        float stat_CaWholeRefuelCostThisYr= 0;

        Cursor cur_wholeCostCostsThisYr = dbHelper.getCostCostsThisYr(car_id);
        cur_wholeCostCostsThisYr.moveToFirst();

        if (cur_wholeCostCostsThisYr.moveToFirst()){
            stat_CaWholeCashCostThisYr = cur_wholeCostCostsThisYr.getFloat(0);
        }
        cur_wholeCostCostsThisYr.close();

        Cursor cur_whoheRefuelCostsThisYr = dbHelper.getRefuelCostsThisYr(car_id);
        cur_whoheRefuelCostsThisYr.moveToFirst();

        if (cur_whoheRefuelCostsThisYr.moveToFirst()){
            stat_CaWholeRefuelCostThisYr = cur_whoheRefuelCostsThisYr.getFloat(0);
        }
        cur_whoheRefuelCostsThisYr.close();

        StatCaThisYr.setText(String.valueOf(stat_CaWholeCashCostThisYr + stat_CaWholeRefuelCostThisYr + " " + displayed_currency));

        //Wszystkie zliczone wydatki w poprzednim roku
        float stat_CaWholeCashCostPrevYr = 0;
        float stat_CaWholeRefuelCostPrevYr= 0;

        Cursor cur_wholeCostCostsPrevYr = dbHelper.getCostCostsPrevYr(car_id);
        cur_wholeCostCostsPrevYr.moveToFirst();

        if (cur_wholeCostCostsPrevYr.moveToFirst()){
            stat_CaWholeCashCostPrevYr = cur_wholeCostCostsPrevYr.getFloat(0);
        }
        cur_wholeCostCostsPrevYr.close();

        Cursor cur_whoheRefuelCostsPrevYr = dbHelper.getRefuelCostsPrevYr(car_id);
        cur_whoheRefuelCostsPrevYr.moveToFirst();

        if (cur_whoheRefuelCostsPrevYr.moveToFirst()){
            stat_CaWholeRefuelCostPrevYr = cur_whoheRefuelCostsPrevYr.getFloat(0);
        }
        cur_whoheRefuelCostsPrevYr.close();

        StatCaPrevYr.setText(String.valueOf(stat_CaWholeCashCostPrevYr + stat_CaWholeRefuelCostPrevYr + " " + displayed_currency));

        //Wszystkie zliczone wydatki ostatnie 12 miesiecy
        float stat_CaWholeCashCostLast12Mth = 0, stat_CaWholeRefuelCostLast12Mth = 0;

        Cursor cur_wholeCostCostsLast12Mth = dbHelper.getCostCostsLast12Mth(car_id);
        cur_wholeCostCostsLast12Mth.moveToFirst();

        if (cur_wholeCostCostsLast12Mth.moveToFirst()){
            stat_CaWholeCashCostLast12Mth = cur_wholeCostCostsLast12Mth.getFloat(0);
        }
        cur_wholeCostCostsLast12Mth.close();

        Cursor cur_whoheRefuelCostsLast12Mth = dbHelper.getRefuelCostsLast12Mth(car_id);
        cur_whoheRefuelCostsLast12Mth.moveToFirst();

        if (cur_whoheRefuelCostsLast12Mth.moveToFirst()){
            stat_CaWholeRefuelCostLast12Mth = cur_whoheRefuelCostsLast12Mth.getFloat(0);
        }
        cur_whoheRefuelCostsLast12Mth.close();

        StatCaLast12Mth.setText(String.valueOf(stat_CaWholeCashCostLast12Mth + stat_CaWholeRefuelCostLast12Mth + " " + displayed_currency));


        // Wydatki (paliwo) @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

        //Paliwo ten miesiac
        StatCfThisMth.setText(String.valueOf(roundFloat(stat_CaWholeRefuelCostThisMth,2)) + " " + displayed_currency);

        //Paliwo poprzedni miesiac
        StatCfPrevMth.setText(String.valueOf(roundFloat(stat_CaWholeRefuelCostPrevMth,2)) + " " + displayed_currency);

        //Paliwo ten Rok
        StatCfThisYr.setText(String.valueOf(roundFloat(stat_CaWholeRefuelCostThisYr,2)) + " " + displayed_currency);

        //Paliwo Poprzedni rok
        StatCfPrevYr.setText(String.valueOf(roundFloat(stat_CaWholeRefuelCostPrevYr,2)) + " " + displayed_currency);

        //Paliwo ostatnie 12 miesiecy
        StatCfLast12Mth.setText(String.valueOf(roundFloat(stat_CaWholeRefuelCostLast12Mth,2)) + " " + displayed_currency );

        //19 Najwyższy rachunek
        float stat_fuelHighestBill = 0;
        Cursor cursor19 = dbHelper.getHighestRefuelBill(car_id);
        cursor19.moveToFirst();
        if (cursor19.moveToFirst()){
            stat_fuelHighestBill = cursor19.getFloat(0);
            StatCfHighestBill.setText(String.valueOf(roundFloat(stat_fuelHighestBill,2)) + " " + displayed_currency);
        }
        else {
            StatCfHighestBill.setText(R.string.statistic_empty_cursor_error_PL);
        }
        cursor19.close();

        //20 Najwyższy rachunek
        float stat_fuelLowestBill = 0;
        Cursor cursor20 = dbHelper.getLowestRefuelBill(car_id);
        cursor20.moveToFirst();
        if (cursor20.moveToFirst()){
            stat_fuelLowestBill = cursor20.getFloat(0);
            StatCfLowestBill.setText(String.valueOf(roundFloat(stat_fuelLowestBill,2)) + " " + displayed_currency);
        }
        else {
            StatCfLowestBill.setText(R.string.statistic_empty_cursor_error_PL);
        }
        cursor20.close();

        //21 Najwyższa cena paliwa
        float stat_fuelHighestFuelPrice = 0;
        Cursor cursor21 = dbHelper.getHighestRefuelFuelPrice(car_id);
        cursor21.moveToFirst();
        if (cursor21.moveToFirst()){
            stat_fuelHighestFuelPrice = cursor21.getFloat(0);
            StatCfHighestFuelPrice.setText(String.valueOf(roundFloat(stat_fuelHighestFuelPrice,2)) + " " + displayed_currency);
        }
        else {
            StatCfHighestFuelPrice.setText(R.string.statistic_empty_cursor_error_PL);
        }
        cursor21.close();

        //22 Najwyższa cena paliwa
        float stat_fuelLowestFuelPrice = 0;
        Cursor cursor22 = dbHelper.getLowestRefuelFuelPrice(car_id);
        cursor22.moveToFirst();
        if (cursor22.moveToFirst()){
            stat_fuelLowestFuelPrice = cursor22.getFloat(0);
            StatCfLowestFuelPrice.setText(String.valueOf(roundFloat(stat_fuelLowestFuelPrice,2)) + " " + displayed_currency);
        }
        else {
            StatCfLowestFuelPrice.setText(R.string.statistic_empty_cursor_error_PL);
        }
        cursor22.close();

        //WYDATKI (KOSZT) @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

        //Koszt ten miesiąc
        StatCcThisMth.setText(String.valueOf(roundFloat(stat_CaWholeCashCostThisMth, 2)) + " " + displayed_currency);

        //Koszt poprzedni miesiac
        StatCcPrevMth.setText(String.valueOf(roundFloat(stat_CaWholeCashCostPrevMth, 2)) + " " + displayed_currency);

        //Koszt ten rok
        StatCcThisYr.setText(String.valueOf(roundFloat(stat_CaWholeCashCostThisYr, 2)) + " " + displayed_currency);

        //Koszt poprzedni rok
        StatCcPrevYr.setText(String.valueOf(roundFloat(stat_CaWholeCashCostPrevYr, 2)) + " " + displayed_currency);

        //Koszt ostatnie 12 miesiecy
        StatCcLast12Mth.setText(String.valueOf(roundFloat(stat_CaWholeCashCostLast12Mth, 2)) + " " + displayed_currency);

        //SREDNIE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

        //wyciągamy min i max datę tankowania dla danego samochodu
        String max_date, min_date;
        Cursor max_date_cur = dbHelper.getMaxRefuelDate(car_id);
        max_date_cur.moveToFirst();
        if (max_date_cur.moveToFirst()){
            max_date = max_date_cur.getString(0);
        }
        else {
            max_date = "error";
        }

        Cursor min_date_cur = dbHelper.getMinRefuelDate(car_id);
        min_date_cur.moveToFirst();
        if (min_date_cur.moveToFirst()){
            min_date = min_date_cur.getString(0);
        }
        else {
            min_date = "error";
        }
        max_date_cur.close();
        min_date_cur.close();

        //23 Sredeni dzienny koszt
        float stat_avgDialyFuelCost = 0;
        Cursor cursor23 = dbHelper.getAvgDialyFuelCost(car_id);
        cursor23.moveToFirst();
        if (cursor23.moveToFirst()){
            stat_avgDialyFuelCost = cursor23.getFloat(0);
            StatAvgDialyCost.setText(String.valueOf(roundFloat(stat_avgDialyFuelCost,2)) + " " + displayed_currency);
        }
        else {
            StatAvgDialyCost.setText(R.string.statistic_empty_cursor_error_PL);
        }
        cursor23.close();

        //24 Sredni miesięczny koszt
        float stat_whole_refuel_cost = 0, stat_avgMthFuelCost = 0;

        stat_whole_refuel_cost = stat_fuelTotalCostCount;

        if (max_date != "error" && min_date != "error") {
            Cursor cursor24 = dbHelper.getAvgMonthlyFuelCost(max_date, min_date, stat_whole_refuel_cost);
            cursor24.moveToFirst();

            if (cursor24.moveToFirst()){
                stat_avgMthFuelCost = cursor24.getFloat(0);
                StatAvgMonthlyCost.setText(String.valueOf(roundFloat(stat_avgMthFuelCost,2)) + " " + displayed_currency);
            }
            else StatAvgMonthlyCost.setText(R.string.statistic_empty_cursor_error_PL);

        }
        else StatAvgMonthlyCost.setText("error");

        //25 Sredni dzienny przebieg
        float stat_avg_dialyMileage = 0, day_bettwen_dates = 0;

        Cursor cur_day_bettwen_dates = dbHelper.getDayBettwenDates(max_date,min_date);
        cur_day_bettwen_dates.moveToFirst();
        if (cur_day_bettwen_dates.moveToFirst()){

            day_bettwen_dates = cur_day_bettwen_dates.getFloat(0);
            if (day_bettwen_dates > 0){

                //TODO: Error przy day_bettwen_dates = 0
                if (displayed_dist_units.equals("mi")) {
                    double tmp;
                    tmp = conventer.kilometersToMiles(stat_KMcounterTotalCount);
                    stat_KMcounterTotalCount = (int) tmp;
                    stat_avg_dialyMileage = stat_KMcounterTotalCount / day_bettwen_dates;
                    StatAvgDialyMileage.setText(roundFloat(stat_avg_dialyMileage, 2) + " " + displayed_dist_units);
                }
                else {
                    stat_avg_dialyMileage = stat_KMcounterTotalCount / day_bettwen_dates;
                    StatAvgDialyMileage.setText(roundFloat(stat_avg_dialyMileage, 2) + " " + displayed_dist_units);
                }
            }
            else StatAvgDialyMileage.setText(R.string.statistic_empty_cursor_error_PL);
        }
        else StatAvgDialyMileage.setText(R.string.statistic_empty_cursor_error_PL);
        cur_day_bettwen_dates.close();

        //26 Sredni miesieczny przebieg
        double stat_avg_monthlyMileage = 0;
        if (cur_day_bettwen_dates.moveToFirst()){
            if (day_bettwen_dates > 0) {

                //TODO: Error przy day_bettwen_dates = 0
                if (displayed_dist_units.equals("mi")) {
                    double tmp;
                    tmp = conventer.kilometersToMiles(stat_KMcounterTotalCount);
                    stat_KMcounterTotalCount = (int) tmp;
                    stat_avg_monthlyMileage = stat_KMcounterTotalCount / (day_bettwen_dates / 30.4);
                    StatAvgMonthlyMileage.setText(roundDouble(stat_avg_monthlyMileage, 2) + " " + displayed_dist_units);
                }
                else {
                    stat_avg_monthlyMileage = stat_KMcounterTotalCount / (day_bettwen_dates / 30.4);
                    StatAvgMonthlyMileage.setText(roundDouble(stat_avg_monthlyMileage, 2) + " " + displayed_dist_units);
                }
            }
            else StatAvgMonthlyMileage.setText(R.string.statistic_empty_cursor_error_PL);
        }
        else StatAvgMonthlyMileage.setText(R.string.statistic_empty_cursor_error_PL);

        //27 Srednie spalanie
        float stat_avgFuelUsage = 0;
        Cursor cursor27 = dbHelper.getAvgFuelUsage(car_id);
        cursor27.moveToFirst();
        if (cursor27.moveToFirst()){

            stat_avgFuelUsage = cursor27.getFloat(0);

            if(stat_avgFuelUsage > 0) {

                if (displayed_fuel_usage_units.equals("l/100km")){ StatAvgFuelUsage.setText(String.valueOf(roundFloat(stat_avgFuelUsage, 2)) + " " + displayed_fuel_usage_units); }
                if (displayed_fuel_usage_units.equals("mpg(uk)")){
                    double tmp;
                    tmp = conventer.lp100ToMpgUK(stat_avgFuelUsage);
                    stat_avgFuelUsage = (float) tmp;
                    StatAvgFuelUsage.setText(String.valueOf(roundFloat(stat_avgFuelUsage, 2)) + " " + displayed_fuel_usage_units);
                }
                if (displayed_fuel_usage_units.equals("mpg(uS)")){
                    double tmp;
                    tmp = conventer.lp100ToMpgUS(stat_avgFuelUsage);
                    stat_avgFuelUsage = (float) tmp;
                    StatAvgFuelUsage.setText(String.valueOf(roundFloat(stat_avgFuelUsage, 2)) + " " + displayed_fuel_usage_units);
                }
            }
            else StatAvgFuelUsage.setText("Brak danych");
        }
        else StatAvgFuelUsage.setText("Brak danych");


        //28 Srednie tankowanie
        float stat_avgRefuelCap = 0;
        Cursor cursor28 = dbHelper.getAvgRefuelCap(car_id);
        cursor28.moveToFirst();
        if (cursor28.moveToFirst()){
            stat_avgRefuelCap = cursor28.getFloat(0);
            StatAvgRefuelCap.setText(roundFloat(stat_avgRefuelCap,2) + " " + displayed_fuel_cap);
        }
        else StatAvgRefuelCap.setText(R.string.statistic_empty_cursor_error_PL);

        //29 Sredni rachunek za tankowanie
        float stat_avgRefuelBill = 0;
        Cursor cursor29 = dbHelper.getAvgRefuelBill(car_id);
        cursor29.moveToFirst();
        if (cursor29.moveToFirst()){
            stat_avgRefuelBill = cursor29.getFloat(0);
            StatAvgRefuelBill.setText(roundFloat(stat_avgRefuelBill,2) + " " + displayed_currency);
        }
    } //showCarStats() end

    //metoda skracajaca miejsca po przecinku w floatach (liczba, miejsca_po_przecinku)
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
        getMenuInflater().inflate(R.menu.menu_statistics_activity, menu);

        //MenuItem carSpinner = menu.findItem(R.id.statistic_car_spinner);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //if (id == R.id.action_add_car){ //do sth here }

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
