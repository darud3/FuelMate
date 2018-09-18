package com.example.pietrzyk.sqlite1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class CostListActivity extends AppCompatActivity {

    ListView CostListView;
    SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;
    Cursor cursor;
    CostListAdapter costListAdapter;
    Context context = this;
    int selected_car_id = 0;
    String sett_currency,sett_dist_unit,sett_fuel_cap , sett_fuel_usage_units ;
    String displayed_currency = "PLN", displayed_fuel_cap = "l", displayed_fuel_usage_units = "l/100km", displayed_dist_units = "km";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cost_list);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.cost_update_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        //wyświetlenie strzalke back
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            //getSupportActionBar().setDisplayShowTitleEnabled(false); // ukrywa tytuł aplikacji z toolbar
        }

        getSettings();

        CostListView = (ListView)findViewById(R.id.cost_list_view);
        CostListView.setEmptyView(findViewById(R.id.cost_list_view_empty));

        //Deklarujemy adapter dla Listview i przypisujemy mu layout pojedynczego obiektu w ListView
        costListAdapter = new CostListAdapter(this,R.layout.cost_list_item);
        CostListView.setAdapter(costListAdapter);
        registerForContextMenu(CostListView);

        dbHelper = new DBHelper(getApplicationContext());
        sqLiteDatabase = dbHelper.getWritableDatabase();

        Spinner CostCarListSpinner = (Spinner) findViewById(R.id.cost_list_car_spinner);

        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();
        //Inicializacja spinnera z bazy danych
        Cursor spinner_cursor = db.rawQuery("SELECT " + TableCar.NewCar.CAR_ID + " AS _id, " + TableCar.NewCar.CAR_NAME + " FROM " + TableCar.NewCar.TABLE_NAME, null);

        if(spinner_cursor.moveToFirst()) {

            String[] from = new String[]{TableCar.NewCar.CAR_NAME};
            int[] to = new int[]{android.R.id.text1};

            //SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, spinner_cursor, from, to);
            //simpleCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.my_spinner_item, spinner_cursor, from, to);
            simpleCursorAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
            CostCarListSpinner.setAdapter(simpleCursorAdapter);

            CostCarListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    //((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                    //((TextView) parent.getChildAt(0)).setTextSize(18);
                    selected_car_id = Integer.valueOf(String.valueOf(id));
                    //nasłuchuje na zmianę id samochodu.
                    showOnListView(String.valueOf(id));
                }

                public void onNothingSelected(AdapterView<?> parent) {
                    //cos tam
                }
            });
        }
        else {
            Intent myIntent = new Intent(CostListActivity.this, CarListActivity.class);
            startActivity(myIntent);
            Toast.makeText(context, "Nie masz jeszcze żadnego Pojazdu. Przed dodaniem kosztu musisz dodać pierwszy Pojazd!", Toast.LENGTH_LONG).show();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_cost_list);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent myIntent = new Intent(CostListActivity.this, CostNewActivity.class);
                startActivity(myIntent);
            }
        });

    } //onCreate end

    @Override
    protected void onResume() {
        showOnListView(String.valueOf(selected_car_id));
        super.onResume();
    }

    //Metoda aktualizująca listview na podstawie wybranej pozycji w spinnerze który jest na toolbarze
    private void showOnListView(String car_id) {
        //czyścimy wszystko
        costListAdapter = new CostListAdapter(this, R.layout.cost_list_item);

        //pobieramy dane z bazy
        cursor = dbHelper.getAllCostInformations(sqLiteDatabase,car_id);

        //sprawdzamy czy cos jest w kursorze
        if (cursor.moveToFirst() ) { //zwroci true jesli cos jest i false jeslni nie
            do {

                int cost_id;
                float cost_cash_spend;
                String cost_name, cost_date, cost_note, cost_type, cost_car;

                cost_id = cursor.getInt(0);
                cost_cash_spend = cursor.getFloat(1);
                cost_name = cursor.getString(2);
                cost_date = cursor.getString(3);
                cost_note = cursor.getString(4);
                cost_type = cursor.getString(5);
                cost_car = cursor.getString(6);

                //wyciagniete z bazy dane przesylamy do posrednika danych tj. data providera
                CostListDataProvider costListDataProvider = new CostListDataProvider(cost_id, cost_cash_spend, cost_name, cost_date, cost_note, cost_type, cost_car, displayed_currency);
                //zas przeslane do posrednika dane przesylamy do adaptera listviev
                costListAdapter.add(costListDataProvider);

            } while (cursor.moveToNext()); //zwraca true jesli dostepny jest kolejny wiersz

        }

        //aktualizujemy liste;
        CostListView.setAdapter(costListAdapter);

    }

    //ContextMenu z automatu nadaje LongClick do itemu.
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_cost_item_list, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {

            case R.id.costList_item_note:
                final int id_db_row2 = ((CostListDataProvider)costListAdapter.getItem(info.position)).getCost_id();

                String note = "";
                cursor = dbHelper.getSingleCostRowById(id_db_row2,sqLiteDatabase);
                if (cursor.moveToFirst()){
                    note = cursor.getString(4);
                }

                AlertDialog.Builder adb2 = new AlertDialog.Builder(CostListActivity.this);
                adb2.setTitle("Notatka");
                if (note.equals("") || note.equals(null)){
                    adb2.setMessage("Brak notki");
                }
                else{ adb2.setMessage(note); }
                adb2.setNegativeButton("OK", null);
                adb2.show();

                break;

            case R.id.costList_item_delete:

                //wskazujemy który wiersz ma być usunięty na podstawie id wiersza id_db_row poprzez costListDataProvider
                final int id_db_row = ((CostListDataProvider)costListAdapter.getItem(info.position)).getCost_id();

                AlertDialog.Builder adb = new AlertDialog.Builder(CostListActivity.this);
                adb.setTitle("Usunąć?");
                adb.setMessage("Czy na pewno chcesz usunąć wybrany element?");
                adb.setNegativeButton("Anuluj", null);
                adb.setPositiveButton("Tak", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deleteCostRow(id_db_row);
                        //odswierzamy listview ladujac activity na nowo... toporne ale dziala mozna poszukac innej drogi
                        recreate();
                        Toast.makeText(getBaseContext(), "Usunięto koszt ", Toast.LENGTH_SHORT).show();
                    }
                });
                adb.show();
                break;

            case R.id.costlList_item_edit:

                final int id_db_row_2 = ((CostListDataProvider)costListAdapter.getItem(info.position)).getCost_id();

                Intent myIntent = new Intent(CostListActivity.this,CostUpdateActivity.class);

                myIntent.putExtra("idRowCost", id_db_row_2);
                startActivity(myIntent);
                break;

            default:
                return super.onContextItemSelected(item);
        }

        return super.onContextItemSelected(item);
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
        getMenuInflater().inflate(R.menu.menu_cost_list_activity, menu);

        //MenuItem carSpinner = menu.findItem(R.id.cost_list_car_spinner);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



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

        //return super.onOptionsItemSelected(item);
    }
}
