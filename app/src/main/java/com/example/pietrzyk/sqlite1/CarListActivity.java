package com.example.pietrzyk.sqlite1;

import android.app.AlertDialog;
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
import android.widget.Toast;

public class CarListActivity extends AppCompatActivity {

    ListView CarListView;
    SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;
    Cursor cursor;
    CarListAdapter carListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_list);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.car_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.car_list_activity_title_pl);

        //wyświetlenie strzalke back
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            //getSupportActionBar().setDisplayShowTitleEnabled(false); // usuwa tytuł activity z toolbara
        }

        CarListView = (ListView) findViewById(R.id.car_list_view);
        CarListView.setEmptyView(findViewById(R.id.car_list_view_empty));

        carListAdapter = new CarListAdapter(this, R.layout.car_list_item);
        CarListView.setAdapter(carListAdapter);

        //deklarujemu dla czego ma wystepowac ContextMenu
        registerForContextMenu(CarListView);

        dbHelper = new DBHelper(getApplicationContext());
        sqLiteDatabase = dbHelper.getWritableDatabase();

        showOnListView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_car);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(CarListActivity.this, CarNewActivity.class);
                startActivity(myIntent);
            }
        });

    } //onCreate end

    @Override
    protected void onResume() {
        showOnListView();
        super.onResume();
    }

    private void showOnListView(){

        //czyścimy wszystko
        carListAdapter = new CarListAdapter(this, R.layout.car_list_item);

        //zapisuje do kursora wszystkie aktualne dane z bazy danych
        cursor = dbHelper.getAllCarInformations(sqLiteDatabase);

        //sprawdzamy czy cos jest w kursorze
        if (cursor.moveToFirst()) {
            do {
                int car_id, car_mfg_date;
                float car_start_counter, car_engine_cap;
                String car_name, car_model, car_note, car_fuel_type, car_brand,
                        car_fuel_units, car_dist_units, car_fuel_usage_units, car_reg_num;

                //Tabela Car 0.id 1.name 2.model 3.mfg_date 4.note 5.fuel_type 6.start_counter
                // 7.brand 8.fuel_units 9.dist_units 10.fuel_usage_units 11.engine_cap

                car_id = cursor.getInt(0);
                car_name = cursor.getString(1);
                car_model = cursor.getString(2);
                car_mfg_date = cursor.getInt(3);
                car_note = cursor.getString(4);
                car_fuel_type = cursor.getString(5);
                car_start_counter = cursor.getFloat(6);
                car_brand = cursor.getString(7);
                car_fuel_units = cursor.getString(8);
                car_dist_units = cursor.getString(9);
                car_fuel_usage_units = cursor.getString(10);
                car_engine_cap = cursor.getFloat(11);
                car_reg_num = cursor.getString(12);

                //wyciagniete z bazy dane przesylamy do posrednika danych tj. data providera
                CarListDataProvider carListDataProvider =
                        new CarListDataProvider(car_id, car_mfg_date, car_start_counter,car_engine_cap,
                                car_name,car_model,car_note,car_fuel_type,car_brand,car_fuel_units,
                                car_dist_units,car_fuel_usage_units, car_reg_num);

                carListAdapter.add(carListDataProvider);

            } while (cursor.moveToNext());
        }
        CarListView.setAdapter(carListAdapter);
    }

    //TODO: Edycja po zwykłym kliknięciu a nie przytrzymaniu
    //ContextMenu z automatu nadaje LongClick do itemu.
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_car_list_item, menu);
    }

    //Deklarujemy co się sstanie jeśli ContextMenu Item zostanie kliknięte
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()){
            case R.id.carList_item_edit:

                final int id_db_row_2 = ((CarListDataProvider)carListAdapter.getItem(info.position)).getCar_id();

                Intent myIntent = new Intent(CarListActivity.this,CarUpdateActivity.class);
                myIntent.putExtra("idRowCar",id_db_row_2);
                startActivity(myIntent);

                break;

            case R.id.carList_item_delete:

                final int id_db_row = ((CarListDataProvider)carListAdapter.getItem(info.position)).getCar_id();

                AlertDialog.Builder adb = new AlertDialog.Builder(CarListActivity.this);
                adb.setTitle("Usunąć?");
                adb.setMessage("Czy na pewno chcesz usunąć wybrany pojazd? Spowoduje to usunięcie wszystkich przypisanych dla pojazdu tankowań i kosztów!");
                adb.setNegativeButton("Anuluj", null);
                adb.setPositiveButton("Tak", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deleteCarRow(id_db_row);
                        dbHelper.deleteAllRefuelDataByCarId(id_db_row);
                        dbHelper.deleteAllCostDataByCarId(id_db_row);
                        //odswierzamy listview ladujac activity na nowo... toporne ale dziala mozna poszukac innej drogi
                        recreate();
                        Toast.makeText(getBaseContext(), "Pojazd został usunięty", Toast.LENGTH_SHORT).show();
                    }
                });
                adb.show();
                break;

            case R.id.carList_item_note:

                final int id_db_row_3 = ((CarListDataProvider)carListAdapter.getItem(info.position)).getCar_id();

                String note = "";
                cursor = dbHelper.getSingleCarRowById(id_db_row_3,sqLiteDatabase);
                if (cursor.moveToFirst()){
                    note = cursor.getString(4);
                }

                AlertDialog.Builder adb2 = new AlertDialog.Builder(CarListActivity.this);
                adb2.setTitle("Notatka");
                if (note.equals("") || note.equals(null)){
                    adb2.setMessage("Brak notki");
                }
                else{ adb2.setMessage(note); }
                adb2.setNegativeButton("OK", null);
                adb2.show();

                break;


            default:
                return super.onContextItemSelected(item);
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_car_list_activity, menu);
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
                Intent myIntent = new Intent(CarListActivity.this, MainActivity.class);
                startActivity(myIntent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        //return super.onOptionsItemSelected(item);
    }

}
