package com.example.pietrzyk.sqlite1;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import static android.app.PendingIntent.getBroadcast;

public class ReminderNewActivity extends AppCompatActivity {

    EditText ReminderNote, ReminderDate, ReminderHours, ReminderKmCounter;
    Spinner ReminderCategorySpinner;
    ArrayAdapter<CharSequence> spinnerAdapter;
    Context context = this;
    Calendar myCalendar = Calendar.getInstance();
    boolean isAlarmSet = false;
    int alarmID = 0;
    SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_new);

        Toolbar toolbar = (Toolbar) findViewById(R.id.reminder_activ_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.reminder_new_activity_title_pl);

        //wyświetlenie strzalke back
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            //getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ReminderNote = (EditText) findViewById(R.id.reminder_new_title_edittext);
        ReminderDate = (EditText) findViewById(R.id.reminder_new_date_edittext);
        ReminderHours = (EditText) findViewById(R.id.reminder_new_hours_edittext);
        ReminderKmCounter = (EditText) findViewById(R.id.reminder_new_km_counter_edittext);

        ReminderCategorySpinner = (Spinner) findViewById(R.id.reminder_new_spinner_category);
        spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.reminder_type,android.R.layout.simple_spinner_dropdown_item);
        ReminderCategorySpinner.setAdapter(spinnerAdapter);

        //DATA
        // Latwiejsza wersja http://www.journaldev.com/9976/android-date-time-picker-dialog
        ReminderDate.setFocusableInTouchMode(false);
        ReminderDate.setFocusable(false);
        //Tworzymy wyskakujacy kalendarz do wpisywania daty
        final DatePickerDialog.OnDateSetListener DTPListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR,year);
                myCalendar.set(Calendar.MONTH,monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateLabel_ReminderDate();
            }
        };

        //wyswietla DataPickerDialog po kliknięciu w RefuelDate EditText
        ReminderDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ReminderNewActivity.this,
                        DTPListener,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //GODZINA
        ReminderHours.setFocusableInTouchMode(false);
        ReminderHours.setFocusable(false);
        ReminderHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mHour,mMinute;
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                ReminderHours.setText(hourOfDay + ":" + minute);
                                if (hourOfDay < 10 && minute < 10) ReminderHours.setText("0" + hourOfDay + ":0" + minute);
                                if (hourOfDay < 10 && minute >= 10) ReminderHours.setText("0" + hourOfDay + ":" + minute);
                                if (hourOfDay >=10 && minute < 10) ReminderHours.setText(hourOfDay + ":0" + minute);
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });

    } //onCreate


    public void setAlarm(){

        String category, date, time, title;
        int month, year, day_of_month, hour_of_day, minute, notification_id = -1;
        String str_month, str_year, str_day_of_month, str_hour_of_day, str_minute;
        int second = 0;

        category = ReminderCategorySpinner.getSelectedItem().toString();
        date = ReminderDate.getText().toString();
        time = ReminderHours.getText().toString();
        title = ReminderNote.getText().toString();

        str_month = date.substring(5,7);
        str_year = date.substring(0,4);
        str_day_of_month = date.substring(8);
        str_hour_of_day = time.substring(0,2);
        str_minute = time.substring(3);

        month = Integer.valueOf(str_month);
        year = Integer.valueOf(str_year);
        day_of_month = Integer.valueOf(str_day_of_month);
        hour_of_day = Integer.valueOf(str_hour_of_day);
        minute = Integer.valueOf(str_minute);

        //Log.e("DATE FORMAT",date + " " + time + " #### Month: " + month + " day: " + day_of_month + " year: " + year + " hour: " + hour_of_day + " minutes: " + minute + " ID: " + notification_id);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.clear();

        calendar.set(Calendar.DAY_OF_MONTH, day_of_month);
        calendar.set(Calendar.MONTH, month-1); //miesiace od 0-11; 0 -styczen
        calendar.set(Calendar.YEAR, year);

        calendar.set(Calendar.HOUR_OF_DAY, hour_of_day);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        Random rand = new Random();
        notification_id = rand.nextInt(100000)+1;

        dbHelper = new DBHelper(context);
        sqLiteDatabase = dbHelper.getWritableDatabase();


        Cursor is_notifID_exist = dbHelper.getAllReminders();

        is_notifID_exist.moveToFirst();

        if (is_notifID_exist.moveToFirst()){
            do {
                int id = 0;
                id = is_notifID_exist.getInt(8);

                if (notification_id == id){
                    do {
                        Random random = new Random();
                        notification_id = random.nextInt(1000)+1;

                    }while (notification_id == id);
                }
            }while (is_notifID_exist.moveToNext());
        }
        is_notifID_exist.close();


        dbHelper.addRemind(category,title,date,time,false,0,0,notification_id,sqLiteDatabase);
        Toast.makeText(getBaseContext(), "Dodano powiadomienie", Toast.LENGTH_SHORT).show();

        Intent alertIntent = new Intent(this, AlarmReciver.class);

        alertIntent.putExtra("category", category);
        alertIntent.putExtra("title", title);
        alertIntent.putExtra("notification_id", notification_id);

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), getBroadcast(this,notification_id,alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        isAlarmSet = true;
    }

    public void stopNotif(int notification_id) {

        if(isAlarmSet){

            Intent alertIntent = new Intent(this, AlarmReciver.class);

            PendingIntent pendIntent = PendingIntent.getBroadcast(this, notification_id, alertIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            pendIntent.cancel();

            AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

            alarmManager.cancel(pendIntent);

            isAlarmSet = false;
        }
    }

    public boolean getIsAlarmSet(){
        return isAlarmSet;
    }

    private void updateLabel_ReminderDate(){
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.US);
        ReminderDate.setText(simpleDateFormat.format(myCalendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reminder_new_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.set_reminder) {

            int state = 0;

            //6
            if(ReminderDate.getText().length() == 0){state++;}
            if(ReminderHours.getText().length() == 0){state++;}
            if(ReminderNote.getText().length() == 0){state++;}

            switch (state){
                case 0:
                    setAlarm();
                    finish();
                    Intent myIntent = new Intent(ReminderNewActivity.this, ReminderListActivity.class);
                    startActivity(myIntent);
                    break;

                case 1:
                    if(ReminderDate.getText().length() == 0){ReminderDate.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if(ReminderHours.getText().length() == 0){ReminderHours.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if(ReminderNote.getText().length() == 0){ReminderNote.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! conajmniej jedno pole jest puste! Uzpełnij!", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    if(ReminderDate.getText().length() == 0){ReminderDate.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if(ReminderHours.getText().length() == 0){ReminderHours.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if(ReminderNote.getText().length() == 0){ReminderNote.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! conajmniej dwa pola są puste! Uzpełnij!", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    if(ReminderDate.getText().length() == 0){ReminderDate.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if(ReminderHours.getText().length() == 0){ReminderHours.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    if(ReminderNote.getText().length() == 0){ReminderNote.setError(getString(R.string.refuel_new_update_editext_empty_error_PL));}
                    Toast.makeText(getBaseContext(),"Błąd! conajmniej trzy pola są puste! Uzpełnij!", Toast.LENGTH_SHORT).show();
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
