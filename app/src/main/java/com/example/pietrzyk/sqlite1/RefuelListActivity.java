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
import android.util.Log;
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

import java.math.BigDecimal;

 public class RefuelListActivity extends AppCompatActivity {

     ListView refuelListView;
     SQLiteDatabase sqLiteDatabase;
     DBHelper dbHelper;
     Cursor cursor;
     RefuelListAdapter refuelListAdapter;
     Context context = this;
     int selected_car_id = 0;
     Conventers conventer = new Conventers();
     String sett_currency,sett_dist_unit,sett_fuel_cap , sett_fuel_usage_units ;
     String displayed_currency = "" , displayed_dist_units = "", displayed_fuel_cap = "", displayed_fuel_usage_units = "";


     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.refuel_list);

         android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.refuel_list_toolbar);
         setSupportActionBar(toolbar);
         getSupportActionBar().setTitle(null);

             //wyświetlenie strzalke back
             if (getSupportActionBar() != null){
                 getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                 getSupportActionBar().setDisplayShowHomeEnabled(true);
                 //getSupportActionBar().setDisplayShowTitleEnabled(true); // ukrywa tytuł aplikacji z toolbar
             }

         getSettings();

         refuelListView = (ListView)findViewById(R.id.refuel_list_view);
         refuelListView.setEmptyView(findViewById(R.id.refuel_list_view_empty));

         //Deklarujemy adapter dla Listview i przypisujemy mu layout pojedynczego obiektu w ListView
         refuelListAdapter = new RefuelListAdapter(this,R.layout.refuel_list_item);
         refuelListView.setAdapter(refuelListAdapter);

         //rejestrujemy ListView dla contextmenu
         registerForContextMenu(refuelListView);
         //refuelListView.setLongClickable(isRestricted());

         dbHelper = new DBHelper(getApplicationContext());
         sqLiteDatabase = dbHelper.getWritableDatabase();

         Spinner CarRefuelListSpinner = (Spinner) findViewById(R.id.refuel_list_car_spinner);

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
             CarRefuelListSpinner.setAdapter(simpleCursorAdapter);

             CarRefuelListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                 public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                     //((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE); //wywala aplikacje, ustawienie własnego layoutu listy spinera i wybranego elementu rozwiazuje problem
                     //((TextView) parent.getChildAt(0)).setTextSize(18);

                     selected_car_id = Integer.valueOf(String.valueOf(id));
                     //nasłuchuje na zmianę id samochodu.
                     showOnListView(String.valueOf(id));
                     //Log.e("SPINNER NEW CAR", "Selected Car id: " + list_refuel_selected_car_id);
                 }

                 public void onNothingSelected(AdapterView<?> parent) {
                     //cos tam
                 }
             });
         }
         else {
             Log.e("SPINNER NEW CAR", "Kursor jest pusty");
             Intent myIntent = new Intent(RefuelListActivity.this, CarListActivity.class);
             startActivity(myIntent);
             Toast.makeText(context, "Nie masz jeszcze żadnego Pojazdu. Przed dodaniem tankowania musisz dodać pierwszy Pojazd!", Toast.LENGTH_LONG).show();
         }

             FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_refuel_list);
             assert fab != null;
             fab.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     finish();
                     Intent myIntent = new Intent(RefuelListActivity.this, RefuelNewActivity.class);
                     startActivity(myIntent);
                 }
             });

         }//onCreate end

     @Override
     protected void onResume() {
         showOnListView(String.valueOf(selected_car_id));
         super.onResume();
     }

     //Metoda aktualizująca listview na podstawie wybranej pozycji w spinnerze który jest na toolbarze
     private void showOnListView(String car_id) {
         //czyścimy wszystko
         refuelListAdapter = new RefuelListAdapter(this, R.layout.refuel_list_item);

         //zapisuje do kursora wszystkie aktualne dane z bazy danych
         cursor = dbHelper.getAllRefuelInformations(sqLiteDatabase, car_id);
         Cursor cur_settings = dbHelper.getSettings();
         cur_settings.moveToFirst();

         int fuel_id, km_counter, fuel_km_diff, fuel_missed;
         float fuel_amount, fuel_price, cash_spend, fuel_usage;
         String fuel_date, fuel_full, fuel_note;

         //sprawdzamy czy cos jest w kursorze
         if (cursor.moveToFirst() && cur_settings.moveToFirst()) { //zwroci true jesli cos jest i false jeslni nie
             do {

                 fuel_id = cursor.getInt(0);
                 fuel_amount = cursor.getFloat(1);
                 fuel_price = cursor.getFloat(2);
                 cash_spend = cursor.getFloat(3);
                 km_counter = cursor.getInt(4);
                 fuel_date = cursor.getString(5);
                 fuel_full = cursor.getString(6);
                 fuel_km_diff = cursor.getInt(7);
                 fuel_usage = cursor.getFloat(8);
                 fuel_note = cursor.getString(9);
                 fuel_missed =  cursor.getInt(10);

                 if (displayed_dist_units.equals("mi")){
                     double tmp;
                     tmp  = conventer.kilometersToMiles(km_counter);
                     km_counter = (int) tmp;
                 }

                 if (sett_fuel_cap.equals("Galony (UK)")){
                     double tmp;
                     tmp = conventer.litresToGalUK(fuel_amount);
                     fuel_amount = (float) tmp;
                 }
                 if (sett_fuel_cap.equals("Galony (US)")){
                     double tmp;
                     tmp = conventer.litresToGalUS(fuel_amount);
                     fuel_amount = (float) tmp;
                 }

                 if (sett_fuel_usage_units.equals("mpg (UK)")){
                     if(fuel_usage > 0) {
                         double tmp;
                         tmp = conventer.lp100ToMpgUK(fuel_usage);
                         fuel_usage = (float) tmp;
                     }
                 }

                 if (sett_fuel_usage_units.equals("mpg (US)")){
                     if(fuel_usage > 0) {
                         double tmp;
                         tmp = conventer.lp100ToMpgUS(fuel_usage);
                         fuel_usage = (float) tmp;
                     }
                 }



                 RefuelListDataProvider refuelListDataProvider = new RefuelListDataProvider(fuel_id, roundFloat(fuel_amount,2), fuel_price, cash_spend, km_counter,
                         fuel_date, fuel_full, fuel_km_diff, roundFloat(fuel_usage,2), fuel_note, fuel_missed,
                         displayed_currency,displayed_dist_units,displayed_fuel_cap,displayed_fuel_usage_units);
                 refuelListAdapter.add(refuelListDataProvider);

             } while (cursor.moveToNext()); //zwraca true jesli dostepny jest kolejny wiersz
         }


         //aktualizujemy liste;
         refuelListView.setAdapter(refuelListAdapter);
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

     //ContextMenu z automatu nadaje LongClick do itemu.
     @Override
     public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
             super.onCreateContextMenu(menu, v, menuInfo);

             MenuInflater inflater = getMenuInflater();
             inflater.inflate(R.menu.context_menu_refuel_list_item, menu);

     }

     //Deklarujemy co się sstanie jeśli ContextMenu Item zostanie kliknięte
     @Override
     public boolean onContextItemSelected(MenuItem item) {

             AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
             switch (item.getItemId()){

                 case R.id.refuelList_item_delete:

                     //wskazujemy który wiersz ma być usunięty na podstawie id wiersza id_db_row poprzez refuelListDataProvider
                     final int id_db_row = ((RefuelListDataProvider)refuelListAdapter.getItem(info.position)).getFuel_id();

                     AlertDialog.Builder adb = new AlertDialog.Builder(RefuelListActivity.this);
                     adb.setTitle("Usunąć?");
                     adb.setMessage("Czy na pewno chcesz usunąć wybrane tankowanie?");
                     adb.setNegativeButton("Anuluj", null);
                     adb.setPositiveButton("Tak", new AlertDialog.OnClickListener() {
                         public void onClick(DialogInterface dialog, int which) {
                             dbHelper.deleteRefuelRow(id_db_row);
                             //odswierzamy listview ladujac activity na nowo... toporne ale dziala mozna poszukac innej drogi
                             recreate();
                             Toast.makeText(getBaseContext(),"Usunięto tankowanie", Toast.LENGTH_SHORT).show();
                         }
                     });
                     adb.show();
                     break;
                     //dbHelper.deleteRefuelRow(id_db_row);
                     //refuelListAdapter.notifyDataSetChanged();

                 case R.id.refuelList_item_edit:

                     //wskazujemy który wiersz ma być usunięty na podstawie id wiersza id_db_row poprzez refuelListDataProvider
                     final int id_db_row_2 = ((RefuelListDataProvider)refuelListAdapter.getItem(info.position)).getFuel_id();
                     final int km_counter = ((RefuelListDataProvider)refuelListAdapter.getItem(info.position)).getKm_counter();

                     Intent myIntent = new Intent(RefuelListActivity.this,RefuelUpdateActivity.class);

                     //przekazujemy id wiersza do myIntent
                     myIntent.putExtra("idRow", id_db_row_2);
                     myIntent.putExtra("KM", km_counter);
                     startActivity(myIntent);

                     //Toast.makeText(getBaseContext(),"Jeszcze niedostępne!", Toast.LENGTH_SHORT).show();
                     break;

                 case R.id.refuelList_item_note:

                     final int id_db_row_3 = ((RefuelListDataProvider)refuelListAdapter.getItem(info.position)).getFuel_id();

                     String note = "";
                     cursor = dbHelper.getSingleRefuelRowById(id_db_row_3,sqLiteDatabase);
                     if (cursor.moveToFirst()){
                         note = cursor.getString(9);
                     }

                     AlertDialog.Builder adb2 = new AlertDialog.Builder(RefuelListActivity.this);
                     adb2.setTitle("Notatka");
                     if (note.equals("") || note.equals(null)){
                         adb2.setMessage("Brak notki");
                     }
                     else { adb2.setMessage(note); }
                     adb2.setNegativeButton("OK", null);
                     adb2.show();

                 default:
                     return super.onContextItemSelected(item);
             }

             return super.onContextItemSelected(item);
     }

     @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         // Inflate the menu; this adds items to the action bar if it is present.
         getMenuInflater().inflate(R.menu.menu_refuel_list_activity, menu);

         //MenuItem carSpinner = menu.findItem(R.id.refuel_list_car_spinner);



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


