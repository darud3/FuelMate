package com.example.pietrzyk.sqlite1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class ReminderListActivity extends AppCompatActivity {

    Context context = this;
    ListView reminderListView;
    ReminderListAdapter reminderListAdapter;
    SQLiteDatabase sqLiteDatabase;
    DBHelper dbHelper;
    int selected_car_id = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.reminder_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.reminder_list_activity_title_pl);

        //wyświetlenie strzalke back
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            //getSupportActionBar().setDisplayShowTitleEnabled(true); // ukrywa tytuł aplikacji z toolbar
        }

        reminderListView = (ListView)findViewById(R.id.reminder_list_view);
        reminderListView.setEmptyView(findViewById(R.id.reminder_list_view_empty));

        //Deklarujemy adapter dla Listview i przypisujemy mu layout pojedynczego obiektu w ListView
        reminderListAdapter = new ReminderListAdapter(this,R.layout.refuel_list_item);
        reminderListView.setAdapter(reminderListAdapter);

        //rejestrujemy ListView dla contextmenu
        registerForContextMenu(reminderListView);

        dbHelper = new DBHelper(getApplicationContext());
        sqLiteDatabase = dbHelper.getWritableDatabase();

        showOnListView();

        // FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_reminder_list);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent myIntent = new Intent(ReminderListActivity.this, ReminderNewActivity.class);
                startActivity(myIntent);
            }
        });


    } //onCreate

    @Override
    protected void onPostResume() {
        showOnListView();
        super.onPostResume();
    }

    private void showOnListView(){

        //czyscimy wszystko
        reminderListAdapter = new ReminderListAdapter(this, R.layout.reminder_list_item);

        Cursor cursor = dbHelper.getAllReminders();

        if (cursor.moveToFirst()){

            do {
                String title,category,date,hour;
                boolean repeat = false;
                int  repeatInt = 0,repeat_cycle = 0, repeat_kmcounter = 0, notification_id = 0, id_row = 0;


                // 0 id_row 1 category 2 title 3 date 4 hour 5 repeat 6 repeat cycle 7 repeat kmcounter 8 reminder id
                id_row = cursor.getInt(0);
                category = cursor.getString(1);
                title = cursor.getString(2);
                date = cursor.getString(3);
                hour = cursor.getString(4);
                repeatInt = cursor.getInt(5);
                repeat_cycle = cursor.getInt(6);
                repeat_kmcounter = cursor.getInt(7);
                notification_id = cursor.getInt(8);

                if(repeatInt == 1) repeat = true;
                else repeat = false;

                ReminderListDataProvider reminderListDataProvider = new ReminderListDataProvider(id_row,category,title,date,hour,repeat,repeat_cycle,repeat_kmcounter,notification_id);

                reminderListAdapter.add(reminderListDataProvider);
            } while (cursor.moveToNext());
        }
        //aktualizujemy liste
        reminderListView.setAdapter(reminderListAdapter);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_reminder_list_item, menu);
    }

    //Deklarujemy co się sstanie jeśli ContextMenu Item zostanie kliknięte
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){

            case R.id.reminderList_item_delete:

                final int id_db_row = ((ReminderListDataProvider)reminderListAdapter.getItem(info.position)).getId_row();
                int notification_id = 0;

                Cursor notif_id_cursor = dbHelper.getNotificationIDRowById(id_db_row);
                notif_id_cursor.moveToFirst();

                if (notif_id_cursor.moveToFirst()){
                    notification_id = notif_id_cursor.getInt(0);
                }
                final int id_notification = notification_id;

                AlertDialog.Builder adb = new AlertDialog.Builder(ReminderListActivity.this);
                adb.setTitle("Usunąć?");
                adb.setMessage("Czy na pewno chcesz usunąć wybrane powiadomienie?");
                adb.setNegativeButton("Anuluj",null);
                adb.setPositiveButton("Tak", new AlertDialog.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deleteReminder(id_db_row);

                        Toast.makeText(getBaseContext(),"Usunięto powiadomienie", Toast.LENGTH_SHORT).show();

                        //usuwamy powiadomienie tworząc takie samie pendinintent jak przy tworzeniu i kasujemy go.
                        //boolean alarmSet = false;
                        ReminderNewActivity reminderNewActivity = new ReminderNewActivity();
                        //alarmSet = reminderNewActivity.getIsAlarmSet();
                        //Log.e("Reminder IsAlarmSet", "Alarm set: " + alarmSet);

                        //if (alarmSet == true){
                        Intent alertIntent = new Intent(ReminderListActivity.this, AlarmReciver.class);

                        PendingIntent pendIntent = PendingIntent.getBroadcast(ReminderListActivity.this, id_notification, alertIntent,PendingIntent.FLAG_UPDATE_CURRENT);

                        pendIntent.cancel();

                        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

                        alarmManager.cancel(pendIntent);
                        //}

                        recreate();
                        //ReminderNewActivity reminderNewActivity = new ReminderNewActivity();
                        //reminderNewActivity.stopNotif(id_notification);
                    }
                });
                adb.show();
                break;

            default: super.onContextItemSelected(item);
        }

        return super.onContextItemSelected(item);
    }
}
