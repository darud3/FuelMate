package com.example.pietrzyk.sqlite1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    //TODO: RefuelList, CostList i CarLListView item z guzikiem opcji zamiast longClick, przejscie do Edycji po kliknięciu w Item

    //Zmiany wyglądu UI
    //TODO: Poprawić Spinnery, przenieść z oncreate options menu (np ChartsActivity) do onCreate (np MainActivity)
    //TODO: FUTURE:BACKUP BAZY
    //TODO: FUTURE:Opcje językowe
    //TODO: FUTURE:Opcje wyboru motywów

    //test git commit

    FloatingActionMenu fab_menu;
    FloatingActionButton fab_menu_button1, fab_menu_button2, fab_menu_button3;
    Button btn_refuel_more, btn_costs_more;
    TextView tv_fuel_usage, tv_last_fuel_usage, tv_last_refuel_date, tv_day_since_last_refuel
            , tv_fuel_this_mth, tv_costs_this_mth, tv_fuel_last_mth, tv_costs_last_mth;
    String sett_currency,sett_dist_unit,sett_fuel_cap , sett_fuel_usage_units ;
    String displayed_currency = "PLN", displayed_fuel_cap = "l", displayed_fuel_usage_units = "l/100km", displayed_dist_units = "km";

    Context context = this;
    int selected_car_id = 0;
    DBHelper dbHelper;
    Conventers conventer = new Conventers();
    SimpleCursorAdapter simpleCursorAdapter;

    private static final int TIME_LIMIT = 1500;
    private static long backPressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activ_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        Spinner CarRefuelListSpinner = (Spinner) findViewById(R.id.main_activ_car_spinner);
        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();

        //Inicializacja spinnera z bazy danych
        Cursor spinner_cursor = db.rawQuery("SELECT " + TableCar.NewCar.CAR_ID + " AS _id, " + TableCar.NewCar.CAR_NAME + " FROM " + TableCar.NewCar.TABLE_NAME, null);
        spinner_cursor.moveToFirst();

        if(spinner_cursor.moveToFirst()) {

            String[] from = new String[]{TableCar.NewCar.CAR_NAME};
            int[] to = new int[]{android.R.id.text1};

            //SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, spinner_cursor, from, to);
            //simpleCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //SimpleCursorAdapter
            simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.my_spinner_item, spinner_cursor, from, to);
            simpleCursorAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
            CarRefuelListSpinner.setAdapter(simpleCursorAdapter);


            CarRefuelListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    /*
                    if (view != null){
                        _selectedItemView = view;
                        ((TextView) view).setTextColor(Color.WHITE);
                    } else {
                        ((TextView) _selectedItemView).setTextColor(Color.WHITE);
                    }
                    */
                    //((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                    //((TextView) parent.getChildAt(0)).setTextSize(18);

                    selected_car_id = Integer.valueOf(String.valueOf(id));
                    popUpData(String.valueOf(selected_car_id));
                }

                public void onNothingSelected(AdapterView<?> parent) {
                    //cos tam
                }
            });
            //spinner_cursor.close(); //powoduje, ze spiner nie wyswietla danych
        }
        else {

            SharedPreferences sharedPreferences = getSharedPreferences("ShaPreferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            boolean  firstTime=sharedPreferences.getBoolean("first", true);
            if(firstTime) {
                editor.putBoolean("first",false);
                editor.commit();
                Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                startActivity(intent);
            }
            else
            {
                //Intent intent = new Intent(MainActivity.this, MainActivity.class);
                //startActivity(intent);
                Toast.makeText(context, "Nie masz jeszcze żadnego Pojazdu. Przed dodaniem tankowania musisz dodać pierwszy Pojazd!", Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(MainActivity.this, CarListActivity.class);
                startActivity(myIntent);

            }
            //Toast.makeText(context, "Nie masz jeszcze żadnego Pojazdu. Przed dodaniem tankowania musisz dodać pierwszy Pojazd!", Toast.LENGTH_LONG).show();
        }

        getSettings();

        /// FAB menu
        fab_menu = (FloatingActionMenu) findViewById(R.id.fab_menu_layout);
        fab_menu_button1 = (FloatingActionButton) findViewById(R.id.fab_menu_button1_layout);
        fab_menu_button2 = (FloatingActionButton) findViewById(R.id.fab_menu_button2_layout);
        fab_menu_button3 = (FloatingActionButton) findViewById(R.id.fab_menu_button3_layout);

        //Zamyka menu po kliknięciu na zewnątrz - zeby dzialalo: w xml dla fab menu ustawic width i height na match parent
        fab_menu.setClosedOnTouchOutside(true);

        fab_menu_button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Aby działało trzeba dodać wpis w AndroidManifest - android:name=".RefuelNewActivity"
                Intent myIntent = new Intent(MainActivity.this, RefuelNewActivity.class);
                startActivity(myIntent);
                fab_menu.close(true);
            }
        });

        fab_menu_button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, CostNewActivity.class);
                startActivity(myIntent);
                fab_menu.close(true);
            }
        });

        fab_menu_button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, ReminderNewActivity.class);
                startActivity(myIntent);
                fab_menu.close(true);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        btn_refuel_more = (Button) findViewById(R.id.main_activ_refuel_button);
        btn_refuel_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this,RefuelListActivity.class);
                startActivity(myIntent);
            }
        });

        btn_costs_more = (Button) findViewById(R.id.main_activ_costs_button);
        btn_costs_more.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this,CostListActivity.class);
                startActivity(myIntent);
            }
        });



    } // onCreate() END

    @Override
    public void onBackPressed() {

        /*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            if (TIME_LIMIT + backPressed > System.currentTimeMillis()){
                super.onBackPressed();
            }
            else {
                Toast.makeText(context,"Kliknij Cofnij ponownie aby wyjść",Toast.LENGTH_SHORT).show();
            }
            backPressed = System.currentTimeMillis();
        }

        if (fab_menu.isOpened()) {
            fab_menu.close(true);
        }
        else {
            if (TIME_LIMIT + backPressed > System.currentTimeMillis()){
                super.onBackPressed();
            }
            else {
                Toast.makeText(context,"Kliknij Cofnij ponownie aby wyjść",Toast.LENGTH_SHORT).show();
            }
            backPressed = System.currentTimeMillis();
        }
        */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        if (fab_menu.isOpened()) {
            fab_menu.close(true);
        }

        if (TIME_LIMIT + backPressed > System.currentTimeMillis()){
            super.onBackPressed();
        }
        else {
            Toast.makeText(context,"Kliknij Cofnij ponownie aby wyjść",Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        //po tej metodzie zostaje wywołane onCreateOptionsMenu() co pozala na przeładowanie zawartości car spinnera po dodaniu nowego samochodu

        //simpleCursorAdapter.notifyDataSetChanged();

        if (fab_menu.isOpened()) {
            fab_menu.close(true);
            popUpData(String.valueOf(selected_car_id));
            //invalidateOptionsMenu();

            //simpleCursorAdapter.notifyDataSetChanged();
            super.onResume();
        }
        else {
            popUpData(String.valueOf(selected_car_id));
            //invalidateOptionsMenu();
            //simpleCursorAdapter.notifyDataSetChanged();
            super.onResume();
        }
    }

    public void popUpData (String car_id){

        tv_fuel_usage = (TextView) findViewById(R.id.main_activ_avg_fuel_usage);
        tv_last_fuel_usage = (TextView) findViewById(R.id.main_activ_last_fuel_usage);
        tv_last_refuel_date = (TextView) findViewById(R.id.main_activ_last_refuel_date);
        tv_day_since_last_refuel = (TextView) findViewById(R.id.main_activ_day_since_last_refuel);
        tv_fuel_this_mth = (TextView)findViewById(R.id.main_activ_fuel_this_mth);
        tv_costs_this_mth = (TextView)findViewById(R.id.main_activ_costs_this_mth);
        tv_fuel_last_mth = (TextView)findViewById(R.id.main_activ_fuel_last_mth);
        tv_costs_last_mth = (TextView)findViewById(R.id.main_activ_costs_last_mth);

        dbHelper = new DBHelper(context);

        // #### Obsługa danych dla Ostatniego tankowania
        double fuel_usage = 0;
        Cursor cur_fuel_usage = dbHelper.getAvgFuelUsage(car_id);
        cur_fuel_usage.moveToFirst();

        if (cur_fuel_usage.moveToFirst()){

            fuel_usage = cur_fuel_usage.getDouble(0);

            if (fuel_usage > 0) {

                if (displayed_fuel_usage_units.equals("mpg(uk)")){
                    fuel_usage = conventer.lp100ToMpgUK(fuel_usage);
                    tv_fuel_usage.setText(String.valueOf(roundDouble(fuel_usage,2) + " " + displayed_fuel_usage_units));
                }
                if (displayed_fuel_usage_units.equals("mpg(us)")){
                    fuel_usage = conventer.lp100ToMpgUS(fuel_usage);
                    tv_fuel_usage.setText(String.valueOf(roundDouble(fuel_usage,2) + " " + displayed_fuel_usage_units));
                }
                if (displayed_fuel_usage_units.equals("l/100km")) {
                    tv_fuel_usage.setText(String.valueOf(roundDouble(fuel_usage, 2) + " " + displayed_fuel_usage_units));
                }
            }
            else tv_fuel_usage.setText("Dodaj więcej wpisów");
        }
        else tv_fuel_usage.setText("Dodaj więcej wpisów");
        cur_fuel_usage.close();

        double last_fuel_usage = 0;
        Cursor cur_last_fuel_usage = dbHelper.getLastFuelUsage(car_id);
        cur_last_fuel_usage.moveToFirst();

        if (cur_last_fuel_usage.moveToFirst()){

            last_fuel_usage = cur_last_fuel_usage.getDouble(0);

            if (last_fuel_usage > 0){

                if (displayed_fuel_usage_units.equals("mpg(uk)")){
                    last_fuel_usage = conventer.lp100ToMpgUK(last_fuel_usage);
                    tv_last_fuel_usage.setText(String.valueOf(roundDouble(last_fuel_usage,2) + " " + displayed_fuel_usage_units));
                }
                if (displayed_fuel_usage_units.equals("mpg(us)")){
                    last_fuel_usage = conventer.lp100ToMpgUS(last_fuel_usage);
                    tv_last_fuel_usage.setText(String.valueOf(roundDouble(last_fuel_usage,2) + " " + displayed_fuel_usage_units));
                }
                if (displayed_fuel_usage_units.equals("l/100km")) {
                    tv_last_fuel_usage.setText(String.valueOf(roundDouble(last_fuel_usage, 2) + " " + displayed_fuel_usage_units));
                }

            }
            else tv_last_fuel_usage.setText("Dodaj więcej wpisów");
        }
        else tv_last_fuel_usage.setText("Dodaj więcej wpisów");
        cur_last_fuel_usage.close();

        String last_refuel_date;
        Cursor cur_last_refuel_date = dbHelper.getLastRefuelDate(car_id);
        cur_last_refuel_date.moveToFirst();

        if (cur_last_refuel_date.moveToFirst()){

            last_refuel_date = cur_last_refuel_date.getString(0);
            tv_last_refuel_date.setText(last_refuel_date);
        }
        else tv_last_refuel_date.setText("Dodaj więcej wpisów");
        cur_last_refuel_date.close();

        String day_since_last_refuel;
        Cursor cur_day_since_last_refuel = dbHelper.getDaysSinceLastRefuel(car_id);
        cur_day_since_last_refuel.moveToFirst();

        if (cur_day_since_last_refuel.moveToFirst()){

            day_since_last_refuel = cur_day_since_last_refuel.getString(0);

            if (day_since_last_refuel != null) {

                if (Integer.valueOf(day_since_last_refuel) > 1) {

                    tv_day_since_last_refuel.setText(day_since_last_refuel + " dni temu");
                }
                else tv_day_since_last_refuel.setText(day_since_last_refuel + " dzień temu");
            }
            else tv_day_since_last_refuel.setText("Dodaj więcej wpisów");
        }
        else tv_day_since_last_refuel.setText("Dodaj więcej wpisów");
        cur_day_since_last_refuel.close();

        // #### Obsluga kosztow na ten i poprzedni miesiac
        // ten miesiac
        float costsCostThisMth = 0;
        float refuelCostThisMth= 0;

        Cursor cur_CostCostsThisMth = dbHelper.getCostCostsThisMth(car_id);
        cur_CostCostsThisMth.moveToFirst();

        if (cur_CostCostsThisMth.moveToFirst()){
            costsCostThisMth = cur_CostCostsThisMth.getFloat(0);
        }
        cur_CostCostsThisMth.close();

        Cursor cur_RefuelCostsThisMth = dbHelper.getRefuelCostsThisMth(car_id);
        cur_RefuelCostsThisMth.moveToFirst();

        if (cur_RefuelCostsThisMth.moveToFirst()){
            refuelCostThisMth = cur_RefuelCostsThisMth.getFloat(0);
        }
        cur_RefuelCostsThisMth.close();

        if (refuelCostThisMth > 0) {
            tv_fuel_this_mth.setText(String.valueOf(refuelCostThisMth) + " " + displayed_currency);
        }
        else tv_fuel_this_mth.setText("Brak danych");

        if (costsCostThisMth > 0) {
            tv_costs_this_mth.setText(String.valueOf(costsCostThisMth) + " " + displayed_currency);
        }
        else tv_costs_this_mth.setText("Brak danych");


        // poprzedni miesiac
        float costsCostLastMth = 0;
        float refuelCostLastMth = 0;

        Cursor cur_CostCostsPrevMth = dbHelper.getCostCostsPrevMth(car_id);
        cur_CostCostsPrevMth.moveToFirst();

        if (cur_CostCostsPrevMth.moveToFirst()){
            costsCostLastMth = cur_CostCostsPrevMth.getFloat(0);
        }
        cur_CostCostsPrevMth.close();

        Cursor cur_whoheRefuelCostsPrevMth = dbHelper.getRefuelCostsPrevMth(car_id);
        cur_whoheRefuelCostsPrevMth.moveToFirst();

        if (cur_whoheRefuelCostsPrevMth.moveToFirst()){
            refuelCostLastMth = cur_whoheRefuelCostsPrevMth.getFloat(0);
        }
        cur_whoheRefuelCostsPrevMth.close();

        if (refuelCostLastMth > 0){
            tv_fuel_last_mth.setText(String.valueOf(refuelCostLastMth) + " " + displayed_currency);
        }
        else tv_fuel_last_mth.setText("Brak danych");

        if(costsCostLastMth > 0) {
            tv_costs_last_mth.setText(String.valueOf(costsCostLastMth) + " " + displayed_currency);
        }
        else tv_costs_last_mth.setText("Brak danych");
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
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_cost){
            Intent myIntent = new Intent(MainActivity.this,CostNewActivity.class);
            startActivity(myIntent);
        }
        /*
        if (id == R.id.action_refuel_list) {
            Intent myIntent = new Intent(MainActivity.this,RefuelListActivity.class);
            startActivity(myIntent);
        }

        if (id == R.id.action_add_refuel){
            Intent myIntent = new Intent(MainActivity.this,RefuelNewActivity.class);
            startActivity(myIntent);
        }

        if (id == R.id.action_cost_list){
            Intent myIntent = new Intent(MainActivity.this,CostListActivity.class);
            startActivity(myIntent);
        }


        if (id == R.id.action_settings){
            Intent myIntent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(myIntent);
        }
        */

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.drawer_menu_main) {
            //Intent myIntent = new Intent(this,MainActivity.class);
            //startActivity(myIntent);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }

        if (id == R.id.drawer_menu_refuel_list) {
            Intent myIntent = new Intent(MainActivity.this,RefuelListActivity.class);
            startActivity(myIntent);
        }

        if (id == R.id.drawer_menu_add_refuel) {
            Intent myIntent = new Intent(MainActivity.this,RefuelNewActivity.class);
            startActivity(myIntent);

        }

        if (id == R.id.drawer_menu_costs_list) {
            Intent myIntent = new Intent(MainActivity.this,CostListActivity.class);
            startActivity(myIntent);
        }

        if (id == R.id.drawer_menu_add_cost) {
            Intent myIntent = new Intent(MainActivity.this,CostNewActivity.class);
            startActivity(myIntent);
        }

        if (id == R.id.drawer_menu_stats) {
            Intent myIntent = new Intent(MainActivity.this,StatisticsActivity.class);
            startActivity(myIntent);
        }

        if (id == R.id.drawer_menu_charts) {
            Intent myIntent = new Intent(MainActivity.this,ChartsAcivity.class);
            startActivity(myIntent);
        }

        if (id == R.id.drawer_menu_settings_reminders) {
            Intent myIntent = new Intent(MainActivity.this, ReminderListActivity.class);
            startActivity(myIntent);
        }

        if (id == R.id.drawer_menu_settings_cars) {
            Intent myIntent = new Intent(MainActivity.this, CarListActivity.class);
            startActivity(myIntent);
            finish();
        }

        if (id == R.id.drawer_menu_settings) {
            Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(myIntent);
        }

        if (id == R.id.drawer_menu_about) {

            //tmp

            //Intent myIntent = new Intent(MainActivity.this, WelcomeActivity.class);
            //startActivity(myIntent);

            AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
            adb.setTitle("O aplikacji");
            adb.setMessage("Aplikacja stworzona na potrzebę pracy inżynierskiej w WSTI w Katowicach.\nKierunek Informatyka,\nSpecjalizacja Inżynieria Systemowa \n\nAutor:\nMateusz Pietrzyk\n7ISI\n\nKatowice 2017");
            adb.setNegativeButton("OK", null);
            adb.show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}
