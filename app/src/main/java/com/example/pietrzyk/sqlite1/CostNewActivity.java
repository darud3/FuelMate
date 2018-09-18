package com.example.pietrzyk.sqlite1;

import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CostNewActivity extends AppCompatActivity {

    EditText CostDate, CostCashSpend, CostName, CostNote;
    Spinner CostTypeSpinner, CostCarSpinner;
    ArrayAdapter<CharSequence> spinnerAdapter;
    Context context = this;
    DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    Calendar myCalendar = Calendar.getInstance();
    String new_cost_selected_car_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cost_new);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.cost_new_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);

        //wyświetlenie strzalke back
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            //getSupportActionBar().setDisplayShowTitleEnabled(false); // usuwa tytuł activity z toolbara
        }

        CostDate = (EditText) findViewById(R.id.NewCost_Date_Input);
        CostCashSpend = (EditText) findViewById(R.id.NewCost_CashSpend_Input);
        CostName = (EditText) findViewById(R.id.NewCost_Name_Input);
        CostNote = (EditText) findViewById(R.id.NewCost_Note_Input);

        //deklarujemy spinner do wyboru typu kosztu
        CostTypeSpinner = (Spinner) findViewById(R.id.NewCost_Type_Spinner);
        spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.cost_types,android.R.layout.simple_spinner_dropdown_item);
        CostTypeSpinner.setAdapter(spinnerAdapter);


        CostCarSpinner = (Spinner) findViewById(R.id.NewCost_CarSpinner);

        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();

        Cursor spinner_cursor = db.rawQuery("SELECT " + TableCar.NewCar.CAR_ID + " AS _id, " + TableCar.NewCar.CAR_NAME + " FROM " + TableCar.NewCar.TABLE_NAME, null);

        if(spinner_cursor.moveToFirst()) {

            String[] from = new String[]{TableCar.NewCar.CAR_NAME};
            int[] to = new int[]{android.R.id.text1};

            //SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, spinner_cursor, from, to);
            //simpleCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.my_spinner_item, spinner_cursor, from, to);
            simpleCursorAdapter.setDropDownViewResource(R.layout.my_spinner_dropdown);
            CostCarSpinner.setAdapter(simpleCursorAdapter);

            CostCarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    //Kolor tekstu w spinerze
                    //((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                    //Zmienia text size w spinerze
                    //((TextView) parent.getChildAt(0)).setTextSize(18);

                    new_cost_selected_car_id = String.valueOf(id);
                }

                public void onNothingSelected(AdapterView<?> parent) {
                    //cos tam
                }
            });
        }
        else {
            Log.e("SPINNER NEW CAR", "Kursor jest pusty");
            finish();
            Intent myIntent = new Intent(CostNewActivity.this, CarListActivity.class);
            startActivity(myIntent);
            Toast.makeText(context, "Nie masz jeszcze żadnego Pojazdu. Przed dodaniem kozsztu musisz dodać pierwszy pojazd!", Toast.LENGTH_LONG).show();
        }

        //autouzupełnienie daty
        updateLabel_CostDate();

        //Zapobiega wyskakiwaniu klawiatury przy edittext date ale nie wpisuje daty po wybraniu jej z DatePickerDialog
        CostDate.setFocusableInTouchMode(false);
        CostDate.setFocusable(false);

        //Tworzymy wyskakujacy kalendarz do wpisywania daty
        final DatePickerDialog.OnDateSetListener DTPListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR,year);
                myCalendar.set(Calendar.MONTH,monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateLabel_CostDate();
            }
        };

        //wyswietla DataPickerDialog po kliknięciu w CostDate EditText
        CostDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CostNewActivity.this,
                        DTPListener,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    } // onCreate End

    //aktualizuje CostDate edittext o aktualna lub wybrana przez nas date w odpowiednim formacie
    private void updateLabel_CostDate(){
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.US);
        CostDate.setText(simpleDateFormat.format(myCalendar.getTime()));
    }

    public void clearCostEditText(){

        CostCashSpend.setText("");
        CostName.setText("");
        CostNote.setText("");
    }

    public void addNewCost(){

        // STRINGI: name, date, type, note, car
        // FLOATY: cashSpend
        Float cash_spend = Float.parseFloat(CostCashSpend.getText().toString());
        String cost_date = CostDate.getText().toString();
        String cost_name = CostName.getText().toString();
        String cost_type = CostTypeSpinner.getSelectedItem().toString();
        String cost_note = CostNote.getText().toString();


        String cost_car = new_cost_selected_car_id;

        dbHelper = new DBHelper(context);
        sqLiteDatabase = dbHelper.getWritableDatabase();

        dbHelper.addCost(cash_spend, cost_name, cost_date, cost_type, cost_note, cost_car, sqLiteDatabase);

        Toast.makeText(getBaseContext(), "Koszt dodany", Toast.LENGTH_SHORT).show();
        Log.e("DATABASE OPERATIONS", "Cost added.");
        dbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cost_new_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add_cost){

            //Sprawdzamy czy ktores z pol jest puste, jesli tak monit o bledzie
            int state = 0;

            //6
            if(CostCashSpend.getText().length() == 0){state++;}
            if(CostDate.getText().length() == 0){state++;}
            if(CostName.getText().length() == 0){state++;}
            if(CostTypeSpinner.getSelectedItem().toString().length() == 0){state++;}

            switch (state) {

                case 0:
                    addNewCost();
                    clearCostEditText();
                    finish();
                    Intent myIntent = new Intent(CostNewActivity.this,CostListActivity.class);
                    startActivity(myIntent);
                    break;
                case 1:
                    if(CostCashSpend.getText().length() == 0){CostCashSpend.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    if(CostDate.getText().length() == 0){CostDate.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    if(CostName.getText().length() == 0){CostName.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! conajmniej jedno pole jest puste! Uzpełnij!", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    if(CostCashSpend.getText().length() == 0){CostCashSpend.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    if(CostDate.getText().length() == 0){CostDate.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    if(CostName.getText().length() == 0){CostName.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! conajmniej dwa pola są puste! Uzpełnij!", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    if(CostCashSpend.getText().length() == 0){CostCashSpend.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    if(CostDate.getText().length() == 0){CostDate.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    if(CostName.getText().length() == 0){CostName.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! conajmniej trzy pola są puste! Uzpełnij!", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    if(CostCashSpend.getText().length() == 0){CostCashSpend.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    if(CostDate.getText().length() == 0){CostDate.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    if(CostName.getText().length() == 0){CostName.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! conajmniej cztery pola są puste! Uzpełnij!", Toast.LENGTH_SHORT).show();
                    break;
            }
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
