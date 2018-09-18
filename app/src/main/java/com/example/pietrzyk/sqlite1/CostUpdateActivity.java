package com.example.pietrzyk.sqlite1;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CostUpdateActivity extends AppCompatActivity {

    DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    EditText update_CostDate, update_CostCashSpend, update_CostName, update_CostNote;
    Spinner update_CostTypeSpinner;
    ArrayAdapter<CharSequence> spinnerAdapter;
    Context context = this;
    public int id_row_for_update;

    Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cost_update);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.cost_update_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.cost_update_title_pl);

        //wyświetlenie strzalke back
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            //getSupportActionBar().setDisplayShowTitleEnabled(false); // usuwa tytuł activity z toolbara
        }

        update_CostDate = (EditText) findViewById(R.id.Update_Cost_Date_Input);
        update_CostCashSpend = (EditText) findViewById(R.id.Update_Cost_CashSpend_Input);
        update_CostName = (EditText) findViewById(R.id.Update_Cost_Name_Input);
        update_CostNote = (EditText) findViewById(R.id.Update_Cost_Note_Input);

        //deklarujemy spinner do wyboru typu kosztu
        update_CostTypeSpinner =(Spinner) findViewById(R.id.Update_Cost_Type_Spinner);
        spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.cost_types,android.R.layout.simple_spinner_dropdown_item);
        update_CostTypeSpinner.setAdapter(spinnerAdapter);

        //Tak wyciągamy ze spinnera Stringa
        //CostTypeSpinner.getSelectedItem().toString();

        //odbiera id wiersza który ma być zaktualizowany
        Intent mIntent = getIntent();
        id_row_for_update = mIntent.getIntExtra("idRowCost", 0);

        //autouzupełnienie daty
        updateLabel_Update_CostDate();
        popUpCostRowToUpdate();

        /*Automatyczna obsluga dodawania itemow do spinnera?

        ArrayAdapter<String> adapter;
        List<String> list;
        list = new ArrayList<String>();
        list.add("test1");
         */

        //Zapobiega wyskakiwaniu klawiatury przy edittext date ale nie wpisuje daty po wybraniu jej z DatePickerDialog
        update_CostDate.setFocusableInTouchMode(false);
        update_CostDate.setFocusable(false);

        //Tworzymy wyskakujacy kalendarz do wpisywania daty
        final DatePickerDialog.OnDateSetListener DTPListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR,year);
                myCalendar.set(Calendar.MONTH,monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateLabel_Update_CostDate();
            }
        };

        //wyswietla DataPickerDialog po kliknięciu w CostDate EditText
        update_CostDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CostUpdateActivity.this,
                        DTPListener,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    //aktualizuje CostDate edittext o aktualna lub wybrana przez nas date w odpowiednim formacie
    private void updateLabel_Update_CostDate(){
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.US);
        update_CostDate.setText(simpleDateFormat.format(myCalendar.getTime()));
    }

    public void popUpCostRowToUpdate(){

        dbHelper = new DBHelper(context); //DBHelper(getApplicationContext());
        sqLiteDatabase = dbHelper.getWritableDatabase();

        Cursor cursor = dbHelper.getSingleCostRowById(id_row_for_update,sqLiteDatabase);

        if (cursor.moveToFirst()){

            int cost_id;
            float cost_cash_spend;
            String cost_name, cost_date, cost_note, cost_type, cost_car;

            //tabela 0.id 1. cash_spend, 2.name, 3. date, 4.note, 5.type, 6.car

            cost_id = cursor.getInt(0);
            cost_cash_spend = cursor.getFloat(1);
            cost_name = cursor.getString(2);
            cost_date = cursor.getString(3);
            cost_note = cursor.getString(4);
            cost_type = cursor.getString(5);
            cost_car = cursor.getString(6);

            update_CostCashSpend.setText(String.valueOf(cost_cash_spend));
            update_CostName.setText(String.valueOf(cost_name));
            update_CostDate.setText(cost_date);
            update_CostNote.setText(String.valueOf(cost_note));

            if (!cost_type.equals(null)) {
                int spinnerPosition = spinnerAdapter.getPosition(cost_type);
                update_CostTypeSpinner.setSelection(spinnerPosition);
                //update_CostCar
            }
        }
    }

    public void updateCost(){ //View view

        Float cash_spend = Float.parseFloat(update_CostCashSpend.getText().toString());
        String name = update_CostName.getText().toString();
        String date = update_CostDate.getText().toString();
        String note = update_CostNote.getText().toString();
        String type = update_CostTypeSpinner.getSelectedItem().toString();

        dbHelper = new DBHelper(context); //DBHelper(getApplicationContext());
        sqLiteDatabase = dbHelper.getWritableDatabase();

        dbHelper.updateCostRow(id_row_for_update,cash_spend,name,date,type,note/*,null*/,sqLiteDatabase);

        Toast.makeText(getBaseContext(), "Koszt zaktualizowany", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cost_update_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_update_cost) {

            int state = 0;

            //6
            if(update_CostCashSpend.getText().length() == 0){state++;}
            if(update_CostDate.getText().length() == 0){state++;}
            if(update_CostName.getText().length() == 0){state++;}
            if(update_CostTypeSpinner.getSelectedItem().toString().length() == 0){state++;}

            switch (state) {

                case 0:
                    updateCost();
                    finish();
                    //Intent myIntent = new Intent(CostNewActivity.this,CostListActivity.class);
                    //startActivity(myIntent);
                    break;
                case 1:
                    if(update_CostCashSpend.getText().length() == 0){update_CostCashSpend.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    if(update_CostDate.getText().length() == 0){update_CostDate.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    if(update_CostName.getText().length() == 0){update_CostName.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! conajmniej jedno pole jest puste! Uzpełnij!", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    if(update_CostCashSpend.getText().length() == 0){update_CostCashSpend.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    if(update_CostDate.getText().length() == 0){update_CostDate.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    if(update_CostName.getText().length() == 0){update_CostName.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! conajmniej dwa pola są puste! Uzpełnij!", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    if(update_CostCashSpend.getText().length() == 0){update_CostCashSpend.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    if(update_CostDate.getText().length() == 0){update_CostDate.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    if(update_CostName.getText().length() == 0){update_CostName.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! conajmniej trzy pola są puste! Uzpełnij!", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    if(update_CostCashSpend.getText().length() == 0){update_CostCashSpend.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    if(update_CostDate.getText().length() == 0){update_CostDate.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    if(update_CostName.getText().length() == 0){update_CostName.setError(getString(R.string.cost_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! conajmniej cztery pola są puste! Uzpełnij!", Toast.LENGTH_SHORT).show();
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
