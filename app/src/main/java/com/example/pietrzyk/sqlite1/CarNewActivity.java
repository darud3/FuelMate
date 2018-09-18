package com.example.pietrzyk.sqlite1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CarNewActivity extends AppCompatActivity {

    EditText CarName, CarRegNum, CarBrand, CarModel, CarMfgDate, CarStartCounter, CarEngineCap, CarNote;
    Spinner CarFuelTypeSpinner;
    ArrayAdapter<CharSequence> spinnerAdapter_FuelType;
    Context context = this;
    DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_new);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.car_new_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.car_new_activity_title_pl);

        //wyświetlenie strzalke back
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            //getSupportActionBar().setDisplayShowTitleEnabled(false); // usuwa tytuł activity z toolbara
        }

        CarName = (EditText) findViewById(R.id.NewCar_Name_Input);
        CarRegNum = (EditText) findViewById(R.id.NewCar_Reg_Num_Input);
        CarBrand = (EditText) findViewById(R.id.NewCar_Brand_Input);
        CarModel = (EditText) findViewById(R.id.NewCar_Model_Input);
        CarMfgDate = (EditText) findViewById(R.id.NewCar_Mfg_Date_Input);
        CarStartCounter = (EditText) findViewById(R.id.NewCar_Start_Counter_Input);
        CarEngineCap = (EditText) findViewById(R.id.NewCar_Engine_Cap_Input);
        CarNote = (EditText) findViewById(R.id.NewCar_Note_Input);

        CarRegNum.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        CarFuelTypeSpinner = (Spinner) findViewById(R.id.NewCar_Fuel_Type_Spinner);

        spinnerAdapter_FuelType = ArrayAdapter.createFromResource(this,R.array.fuel_type,android.R.layout.simple_spinner_dropdown_item);
        CarFuelTypeSpinner.setAdapter(spinnerAdapter_FuelType);
    }

    public void addNewCar(){

        String car_mfg_date = CarMfgDate.getText().toString();
        String car_start_counter = CarStartCounter.getText().toString();
        String car_name = CarName.getText().toString();
        String car_reg_num = CarRegNum.getText().toString();
        String car_model = CarModel.getText().toString();
        String car_brand = CarBrand.getText().toString();
        String car_fuel_type = CarFuelTypeSpinner.getSelectedItem().toString();
        String car_engine_cap = CarEngineCap.getText().toString();
        String car_note = CarNote.getText().toString();

        dbHelper = new DBHelper(context);
        sqLiteDatabase = dbHelper.getWritableDatabase();

        dbHelper.addCar(car_name,car_model,car_mfg_date,car_note,car_fuel_type,car_start_counter,car_brand,null,null,null,car_engine_cap,car_reg_num,sqLiteDatabase);
        Toast.makeText(getBaseContext(), "Samochód dodany", Toast.LENGTH_SHORT).show();
        Log.e("DATABASE OPERATIONS", "Car added.");
        dbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_car_new_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add_car){

            int flag = 0;

            if (CarName.getText().length() == 0) { flag++; }
            if (CarRegNum.getText().length() == 0) { flag++; }

            dbHelper = new DBHelper(context);
            sqLiteDatabase = dbHelper.getWritableDatabase();

            Cursor cur_car_info = dbHelper.getAllCarInformations(sqLiteDatabase);

            if(cur_car_info.moveToFirst()){
                String car_name, car_nr_reg;
                do {
                    car_name = cur_car_info.getString(1);
                    car_nr_reg = cur_car_info.getString(12);

                    if (CarName.getText().toString().equals(car_name)) flag = 3;
                    if (CarRegNum.getText().toString().equals(car_nr_reg)) flag = 4;
                    if (CarRegNum.getText().toString().equals(car_nr_reg) && CarName.getText().toString().equals(car_name)) flag = 5;

                }while (cur_car_info.moveToNext());
            }


            switch (flag) {

                case 0:
                    addNewCar();
                    Intent myIntent = new Intent(CarNewActivity.this, CarListActivity.class);
                    startActivity(myIntent);
                    finish();
                    break;
                case 1:
                    if (CarName.getText().length() == 0) { CarName.setError(getString(R.string.refuel_new_update_editext_empty_error_PL)); }
                    if (CarRegNum.getText().length() == 0) { CarRegNum.setError(getString(R.string.refuel_new_update_editext_empty_error_PL)); }
                    Toast.makeText(getBaseContext(),"Błąd! Pola 'Nazwa' i 'Numer rejestracyjny' są obowiązkowe!", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    if (CarName.getText().length() == 0) { CarName.setError(getString(R.string.refuel_new_update_editext_empty_error_PL)); }
                    if (CarRegNum.getText().length() == 0) { CarRegNum.setError(getString(R.string.refuel_new_update_editext_empty_error_PL)); }
                    Toast.makeText(getBaseContext(),"Błąd! Pola 'Nazwa' i 'Numer rejestracyjny' są obowiązkowe!", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    if (CarName.getText().length() == 0) { CarName.setError(getString(R.string.refuel_new_update_editext_empty_error_PL)); }
                    if (CarRegNum.getText().length() == 0) { CarRegNum.setError(getString(R.string.refuel_new_update_editext_empty_error_PL)); }
                    CarName.setError(getString(R.string.car_new_update_name_exists_PL));
                    Toast.makeText(getBaseContext(),"Błąd! Pojazd o takiej samej nazwie już istnieje!", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    if (CarName.getText().length() == 0) { CarName.setError(getString(R.string.refuel_new_update_editext_empty_error_PL)); }
                    if (CarRegNum.getText().length() == 0) { CarRegNum.setError(getString(R.string.refuel_new_update_editext_empty_error_PL)); }
                    CarRegNum.setError(getString(R.string.car_new_update_reg_num_exists_PL));
                    Toast.makeText(getBaseContext(),"Błąd! Pojazd o tym samym numerze rejestracji już istnieje!", Toast.LENGTH_SHORT).show();
                    break;

                case 5:
                    if (CarName.getText().length() == 0) { CarName.setError(getString(R.string.refuel_new_update_editext_empty_error_PL)); }
                    if (CarRegNum.getText().length() == 0) { CarRegNum.setError(getString(R.string.refuel_new_update_editext_empty_error_PL)); }
                    CarName.setError(getString(R.string.car_new_update_name_exists_PL));
                    CarRegNum.setError(getString(R.string.car_new_update_reg_num_exists_PL));
                    Toast.makeText(getBaseContext(),"Błąd! Pojazd o tej samej nazwie i numerze rejestracji już istnieje!", Toast.LENGTH_SHORT).show();
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
