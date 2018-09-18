package com.example.pietrzyk.sqlite1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

public class CarUpdateActivity extends AppCompatActivity {

    EditText update_CarName, update_CarRegNum, update_CarBrand, update_CarModel, update_CarMfgDate, update_CarStartCounter, update_CarEngineCap, update_CarNote;
    Spinner update_CarFuelTypeSpinner;
    ArrayAdapter<CharSequence> spinnerAdapter_FuelType;
    Context context = this;
    DBHelper dbHelper;
    SQLiteDatabase sqLiteDatabase;
    public int id_row_for_update;
    Calendar myCalendar = Calendar.getInstance();

    String edited_car_name, edited_car_nr_reg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_update);


        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.car_update_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.car_update_activity_title_pl);

        //wyświetlenie strzalke back
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            //getSupportActionBar().setDisplayShowTitleEnabled(false); // usuwa tytuł activity z toolbara
        }

        update_CarName = (EditText) findViewById(R.id.UpdateCar_Name_Input);
        update_CarRegNum = (EditText) findViewById(R.id.UpdateCar_Reg_Num_Input);
        update_CarBrand = (EditText) findViewById(R.id.UpdateCar_Brand_Input);
        update_CarModel = (EditText) findViewById(R.id.UpdateCar_Model_Input);
        update_CarMfgDate = (EditText) findViewById(R.id.UpdateCar_Mfg_Date_Input);
        update_CarStartCounter = (EditText) findViewById(R.id.UpdateCar_Start_Counter_Input);
        update_CarEngineCap = (EditText) findViewById(R.id.UpdateCar_Engine_Cap_Input);
        update_CarNote = (EditText) findViewById(R.id.UpdateCar_Note_Input);

        update_CarRegNum.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        update_CarFuelTypeSpinner = (Spinner) findViewById(R.id.UpdateCar_Fuel_Type_Spinner);

        spinnerAdapter_FuelType = ArrayAdapter.createFromResource(this,R.array.fuel_type,android.R.layout.simple_spinner_dropdown_item);
        //do wylaczenia w przyszlosci
        update_CarFuelTypeSpinner.setEnabled(true);
        update_CarFuelTypeSpinner.setClickable(true);
        update_CarFuelTypeSpinner.setAdapter(spinnerAdapter_FuelType);

        //odbiera id wiersza który ma być zaktualizowany
        Intent mIntent = getIntent();
        id_row_for_update = mIntent.getIntExtra("idRowCar", 0);

        popUpCarRowToUpdate();


    }

    public void popUpCarRowToUpdate(){

        dbHelper = new DBHelper(context);
        sqLiteDatabase = dbHelper.getWritableDatabase();

        Cursor cursor = dbHelper.getSingleCarRowById(id_row_for_update,sqLiteDatabase);

        if (cursor.moveToFirst()){

            // 0.id 1.name 2.model 3.mfg_date 4.note 5.fuel_type 6.startCoun
            // 7.brand 8.fuelUnits 9.distUnits 10.fuelUsagUnits 11.engineCap
            int car_id;
            String car_start_counter, car_engine_cap, car_name, car_mfg_date, car_model, car_note,
                    car_fuel_type, car_brand, car_reg_num; //, car_fuel_units, car_dist_units, car_fuel_usage_units;

            car_id = cursor.getInt(0);
            car_name = cursor.getString(1); //wymagane
            car_model = cursor.getString(2); //wymagane
            car_mfg_date = cursor.getString(3);
            car_note = cursor.getString(4);
            car_fuel_type = cursor.getString(5); //wymagane
            car_start_counter = cursor.getString(6);
            car_brand = cursor.getString(7);
            //car_fuel_units = cursor.getString(8);
            //car_dist_units = cursor.getString(9);
            //car_fuel_usage_units = cursor.getString(10);
            car_engine_cap = cursor.getString(11);
            car_reg_num = cursor.getString(12);

            update_CarName.setText(car_name);
            update_CarRegNum.setText(car_reg_num);
            edited_car_name = car_name;
            edited_car_nr_reg = car_reg_num;

            if (!car_fuel_type.equals(null)) {
                int spinnerPosition = spinnerAdapter_FuelType.getPosition(car_fuel_type);
                update_CarFuelTypeSpinner.setSelection(spinnerPosition);
            }
            if (car_mfg_date == "null" || car_mfg_date == "") update_CarMfgDate.setText("");
            else update_CarMfgDate.setText(car_mfg_date);

            if (car_model == "null" || car_model == "") update_CarModel.setText("");
            else update_CarModel.setText(car_model);

            if (car_brand == "null" || car_brand == "") update_CarBrand.setText("");
            else update_CarBrand.setText(car_brand);

            if (car_engine_cap == "null" || car_engine_cap == "") update_CarEngineCap.setText("");
            else update_CarEngineCap.setText(car_engine_cap);

            if (car_start_counter == "null" || car_start_counter == "") update_CarStartCounter.setText("");
            else update_CarStartCounter.setText(car_start_counter);

            if (car_note == "null" || car_note == "") update_CarNote.setText("");
            else update_CarNote.setText(car_note);

        }
    }

    public void updateCar(){
        // 0.id 1.name 2.model 3.mfg_date 4.note 5.fuel_type 6.startCoun
        // 7.brand 8.fuelUnits 9.distUnits 10.fuelUsagUnits 11.engineCap

        String name = update_CarName.getText().toString();
        String model = update_CarModel.getText().toString();
        String mfg_date = update_CarMfgDate.getText().toString();
        String note = update_CarNote.getText().toString();
        String fuel_type = update_CarFuelTypeSpinner.getSelectedItem().toString();
        String start_counter = update_CarStartCounter.getText().toString();
        String brand = update_CarBrand.getText().toString();
        String engine_cap = update_CarEngineCap.getText().toString();
        String reg_num = update_CarRegNum.getText().toString();

        dbHelper = new DBHelper(context);
        sqLiteDatabase = dbHelper.getWritableDatabase();

        dbHelper.updateCarRow(id_row_for_update,name,model,mfg_date,note,fuel_type,start_counter,
                brand,null,null,null,engine_cap,reg_num,sqLiteDatabase);

        Toast.makeText(getBaseContext(), "Pojazd zaktualizowany", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_car_update_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_update_car) {

            int flag = 0;
            int count = 0;
            String car_name, car_nr_reg;
            boolean car_name_flag = false;
            boolean car_nr_reg_flag = false;

            if (update_CarName.getText().length() == 0) { flag++; }
            if (update_CarRegNum.getText().length() == 0) { flag++; }

            dbHelper = new DBHelper(context);
            sqLiteDatabase = dbHelper.getWritableDatabase();

            Cursor cur_car_info = dbHelper.getAllCarInformations(sqLiteDatabase);
            count = cur_car_info.getCount();

            final String[] cars_names = new String[count];
            final String[] cars_nr_regs= new String[count];

            //sprawdzamy czy edytowany pojazd po zmianie nazwy/nr reg nie dubluje jej z juz istniejącą w bazie
            if(cur_car_info.moveToFirst()) {


                for (int i = 0; i < count; i++){

                    cars_names[i] = cur_car_info.getString(1);
                    cars_nr_regs[i] = cur_car_info.getString(12);

                    if (update_CarName.getText().toString().equals(cars_names[i])) car_name_flag = true;
                    if (update_CarRegNum.getText().toString().equals(cars_nr_regs[i])) car_nr_reg_flag = true;

                    cur_car_info.moveToNext();
                }

                car_name = update_CarName.getText().toString();
                car_nr_reg = update_CarRegNum.getText().toString();

                if (!car_name.equals(edited_car_name) && car_name_flag) flag = 3;
                if (!car_nr_reg.equals(edited_car_nr_reg) && car_nr_reg_flag) flag = 4;
                if (!car_nr_reg.equals(edited_car_nr_reg) && car_nr_reg_flag &&
                        !car_name.equals(edited_car_name) && car_name_flag) flag = 5;

            }

            switch (flag) {

                case 0:
                    updateCar();
                    finish();
                    break;
                case 1:
                    if (update_CarName.getText().length() == 0) { update_CarName.setError(getString(R.string.refuel_new_update_editext_empty_error_PL)); }
                    if (update_CarRegNum.getText().length() == 0) { update_CarRegNum.setError(getString(R.string.refuel_new_update_editext_empty_error_PL)); }
                    Toast.makeText(getBaseContext(),"Błąd! Pola 'Nazwa' i 'Opis' są obowiązkowe!", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    if (update_CarName.getText().length() == 0) { update_CarName.setError(getString(R.string.refuel_new_update_editext_empty_error_PL)); }
                    if (update_CarRegNum.getText().length() == 0) { update_CarRegNum.setError(getString(R.string.refuel_new_update_editext_empty_error_PL)); }
                    Toast.makeText(getBaseContext(),"Błąd! Pola 'Nazwa' i 'Opis' są obowiązkowe!", Toast.LENGTH_SHORT).show();
                    break;
                //przy edycji nie da się zapisać pojazdu z tą samą nazwą.
                case 3:
                    if (update_CarName.getText().length() == 0) { update_CarName.setError(getString(R.string.refuel_new_update_editext_empty_error_PL)); }
                    if (update_CarRegNum.getText().length() == 0) { update_CarRegNum.setError(getString(R.string.refuel_new_update_editext_empty_error_PL)); }
                    update_CarName.setError(getString(R.string.car_new_update_name_exists_PL));
                    Toast.makeText(getBaseContext(),"Błąd! Pojazd o takiej samej nazwie już istnieje!", Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    if (update_CarName.getText().length() == 0) { update_CarName.setError(getString(R.string.refuel_new_update_editext_empty_error_PL)); }
                    if (update_CarRegNum.getText().length() == 0) { update_CarRegNum.setError(getString(R.string.refuel_new_update_editext_empty_error_PL)); }
                    update_CarRegNum.setError(getString(R.string.car_new_update_reg_num_exists_PL));
                    Toast.makeText(getBaseContext(),"Błąd! Pojazd o tym samym numerze rejestracji już istnieje!", Toast.LENGTH_SHORT).show();
                    break;

                case 5:
                    if (update_CarName.getText().length() == 0) { update_CarName.setError(getString(R.string.refuel_new_update_editext_empty_error_PL)); }
                    if (update_CarRegNum.getText().length() == 0) { update_CarRegNum.setError(getString(R.string.refuel_new_update_editext_empty_error_PL)); }
                    update_CarName.setError(getString(R.string.car_new_update_name_exists_PL));
                    update_CarRegNum.setError(getString(R.string.car_new_update_reg_num_exists_PL));
                    Toast.makeText(getBaseContext(),"Błąd! Pojazd o tej samej nazwie i numerze rejestracji już istnieje!", Toast.LENGTH_SHORT).show();
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
