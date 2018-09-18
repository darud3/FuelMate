package com.example.pietrzyk.sqlite1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;


public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "refuelDB";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_QUERY_1_REFUEL =
            "CREATE TABLE " + TableRefuel.NewRefuel.TABLE_NAME +
                    "(" + TableRefuel.NewRefuel.FUEL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + TableRefuel.NewRefuel.FUEL_AMOUNT + " FLOAT(10,2) NOT NULL,"
                    + TableRefuel.NewRefuel.FUEL_PRICE + " FLOAT(10,2) NOT NULL,"
                    + TableRefuel.NewRefuel.FUEL_CASH_SPEND + " FLOAT(10,2) NOT NULL,"
                    + TableRefuel.NewRefuel.KM_COUNTER + " INTEGER NOT NULL,"
                    + TableRefuel.NewRefuel.FUEL_DATE + " VARCHAR(10) NOT NULL,"
                    + TableRefuel.NewRefuel.FUEL_TYPE + " VARCHAR(10) NULL,"
                    + TableRefuel.NewRefuel.FUEL_CAR + " VARCHAR(5) NULL,"
                    + TableRefuel.NewRefuel.FUEL_FULL + " CHAR(2) NULL,"
                    + TableRefuel.NewRefuel.FUEL_PER_100 + " FLOAT NULL,"
                    + TableRefuel.NewRefuel.FUEL_MISSED + " CHAR(2) NULL DEFAULT 0,"
                    + TableRefuel.NewRefuel.FUEL_KM_DIFF + " INTEGER NULL,"
                    + TableRefuel.NewRefuel.FUEL_NOTE + " VARCHAR(255) NULL);";

    private static final String CREATE_QUERY_2_COSTS =
            "CREATE TABLE " + TableCost.NewCost.TABLE_NAME +
                    "(" + TableCost.NewCost.COST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + TableCost.NewCost.COST_CASH_SPEND + " FLOAT(10,2) NOT NULL,"
                    + TableCost.NewCost.COST_NAME + " VARCHAR(100) NOT NULL,"
                    + TableCost.NewCost.COST_DATE + " VARCHAR(10) NOT NULL,"
                    + TableCost.NewCost.COST_TYPE + " VARCHAR(20) NOT NULL,"
                    + TableCost.NewCost.COST_NOTE + " VARCHAR(255) NULL,"
                    + TableCost.NewCost.COST_CAR + " VARCHAR(5) NULL);";

    // 0.id 1.name 2.model 3.mfg_date 4.note 5.fuel_type 6.startCoun 7.brand 8.fuelUnits 9.distUnits 10.fuelUsagUnits 11.engineCap
    private static final String CREATE_QUERY_3_CAR =
            "CREATE TABLE " + TableCar.NewCar.TABLE_NAME +
                    "(" + TableCar.NewCar.CAR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TableCar.NewCar.CAR_NAME + " VARCHAR(50) NOT NULL, "
                    + TableCar.NewCar.CAR_MODEL + " VARCHAR(50) NULL, "
                    + TableCar.NewCar.CAR_MFG_DATE + " VARCHAR(30) NULL ,"
                    + TableCar.NewCar.CAR_NOTE + " VARCHAR(255) NULL, "
                    + TableCar.NewCar.CAR_FUEL_TYPE + " VARCHAR(10) NOT NULL, "
                    + TableCar.NewCar.CAR_START_COUNTER + " VARCHAR(30) NULL, "
                    + TableCar.NewCar.CAR_BRAND + " VARCHAR(50) NULL, "
                    + TableCar.NewCar.CAR_FUEL_UNITS + " VARCHAR(10) NULL, "
                    + TableCar.NewCar.CAR_DIST_UNITS + " VARCHAR(10) NULL, "
                    + TableCar.NewCar.CAR_FUEL_USAGE_UNITS + " VARCHAR(10) NULL, "
                    + TableCar.NewCar.CAR_ENGINE_CAP + " VARCHAR(10) NULL,"
                    + TableCar.NewCar.CAR_REG_NUM + " VARCHAR(20) NOT NULL);";

    private static final String CREATE_QUERY_4_SETTINGS =
            "CREATE TABLE " + TableSettings.Settings.TABLE_NAME +
                    "(" + TableSettings.Settings.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TableSettings.Settings.CURRENCY + " VARCHAR(40) NOT NULL, "
                    + TableSettings.Settings.DISTANCE_UNITS + " VARCHAR(40) NOT NULL, "
                    + TableSettings.Settings.FUEL_CAP + " VARCHAR(40) NOT NULL, "
                    + TableSettings.Settings.FUEL_USAGE_UNITS + " VARCHAR(40) NOT NULL);";

    private static final String INSERT_QUERY_5_DEFAULT_SETTINGS =
            "INSERT INTO " + TableSettings.Settings.TABLE_NAME +
                    "(" + TableSettings.Settings.CURRENCY + ", "
                    + TableSettings.Settings.DISTANCE_UNITS + ", "
                    + TableSettings.Settings.FUEL_CAP + ", "
                    + TableSettings.Settings.FUEL_USAGE_UNITS + ") "
            + "VALUES (\"PLN\", \"Kilometry\", \"Litry\", \"l/100km\");";

    private static final String CREATE_QUERY_6_REMINDER =
            "CREATE TABLE "+ TableReminder.NewReminder.TABLE_NAME +
                    "(" + TableReminder.NewReminder.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TableReminder.NewReminder.CATEGORY + " VARCHAR(40) NULL, "
                    + TableReminder.NewReminder.TITLE + " VARCHAR(60) NULL, "
                    + TableReminder.NewReminder.DATE + " VARCHAR(30) NULL, "
                    + TableReminder.NewReminder.HOUR + " VARCHAR(5) NULL, "
                    + TableReminder.NewReminder.REPEAT + " BIT NULL, "
                    + TableReminder.NewReminder.REPEAT_CYCLE + " INTEGER NULL, "
                    + TableReminder.NewReminder.REPEAT_KMCOUNTER + " INTEGER NULL, "
                    + TableReminder.NewReminder.REMINDER_ID + " INTEGER NULL);";


    //Tu tworzymy bazę 'chyba'
    public DBHelper(Context context){

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //tworzymy pierwotną table
        db.execSQL(CREATE_QUERY_1_REFUEL);
        db.execSQL(CREATE_QUERY_2_COSTS);
        db.execSQL(CREATE_QUERY_3_CAR);
        db.execSQL(CREATE_QUERY_4_SETTINGS);
        db.execSQL(INSERT_QUERY_5_DEFAULT_SETTINGS);
        db.execSQL(CREATE_QUERY_6_REMINDER);
    }

    /*
    Obsługa tabeli Tankowan @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
     */

    public void addRefuel (Float fuel_amount, Float fuel_price, Float fuel_cash_spend, Integer km_counter, String fuel_date,
                           String fuel_car, String fuel_full, String fuel_missed, Float fuel_per100, String fuel_note, SQLiteDatabase db){

        ContentValues contentValues = new ContentValues();
        contentValues.put(TableRefuel.NewRefuel.FUEL_AMOUNT,fuel_amount);
        contentValues.put(TableRefuel.NewRefuel.FUEL_PRICE,fuel_price);
        contentValues.put(TableRefuel.NewRefuel.FUEL_CASH_SPEND,fuel_cash_spend);
        contentValues.put(TableRefuel.NewRefuel.KM_COUNTER, km_counter);
        contentValues.put(TableRefuel.NewRefuel.FUEL_DATE, fuel_date);
        contentValues.put(TableRefuel.NewRefuel.FUEL_CAR,fuel_car);
        contentValues.put(TableRefuel.NewRefuel.FUEL_FULL,fuel_full);
        contentValues.put(TableRefuel.NewRefuel.FUEL_MISSED,fuel_missed);
        contentValues.put(TableRefuel.NewRefuel.FUEL_PER_100,fuel_per100);
        contentValues.put(TableRefuel.NewRefuel.FUEL_NOTE,fuel_note);

        db.insert(TableRefuel.NewRefuel.TABLE_NAME, null, contentValues);
    }

    public void deleteRefuelRow (long id){

        SQLiteDatabase db = getWritableDatabase();
        String string = String.valueOf(id);
        //int _id = Integer.valueOf(string);
        db.execSQL("DELETE FROM " + TableRefuel.NewRefuel.TABLE_NAME + " WHERE id = " + string + "");
    }

    public void deleteAllRefuelDataByCarId (long car_id){

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TableRefuel.NewRefuel.TABLE_NAME + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + "");
    }

    public int updateRefuelRow (int id_row, Float new_fuel_amount, Float new_fuel_price, Float new_fuel_cash_spend, Integer new_km_counter, String new_fuel_date,
                                String new_fuel_full, String new_fuel_missed, String new_fuel_note, SQLiteDatabase db){

        ContentValues contentValues = new ContentValues();
        contentValues.put(TableRefuel.NewRefuel.FUEL_AMOUNT,new_fuel_amount);
        contentValues.put(TableRefuel.NewRefuel.FUEL_PRICE,new_fuel_price);
        contentValues.put(TableRefuel.NewRefuel.FUEL_CASH_SPEND,new_fuel_cash_spend);
        contentValues.put(TableRefuel.NewRefuel.KM_COUNTER, new_km_counter);
        contentValues.put(TableRefuel.NewRefuel.FUEL_DATE, new_fuel_date);
        contentValues.put(TableRefuel.NewRefuel.FUEL_FULL, new_fuel_full);
        contentValues.put(TableRefuel.NewRefuel.FUEL_MISSED, new_fuel_missed);
        contentValues.put(TableRefuel.NewRefuel.FUEL_NOTE,new_fuel_note);

        String selection = TableRefuel.NewRefuel.FUEL_ID + " LIKE ?";
        String[] selection_args = {String.valueOf(id_row)}; //wskakuje za znak zapytania '?' z selection

        int count = db.update(TableRefuel.NewRefuel.TABLE_NAME, contentValues, selection, selection_args);
        return count;
    }

    public Cursor getAllRefuelInformations(SQLiteDatabase db, String car_id) {

        String[] columns ={TableRefuel.NewRefuel.FUEL_ID, TableRefuel.NewRefuel.FUEL_AMOUNT, TableRefuel.NewRefuel.FUEL_PRICE,
                TableRefuel.NewRefuel.FUEL_CASH_SPEND, TableRefuel.NewRefuel.KM_COUNTER, TableRefuel.NewRefuel.FUEL_DATE,
                TableRefuel.NewRefuel.FUEL_FULL, TableRefuel.NewRefuel.FUEL_KM_DIFF, TableRefuel.NewRefuel.FUEL_PER_100,
                TableRefuel.NewRefuel.FUEL_NOTE, TableRefuel.NewRefuel.FUEL_MISSED};

        String where = TableRefuel.NewRefuel.FUEL_CAR + "=?";
        String[] whereArgs = {car_id};

        Cursor cursor = db.query(TableRefuel.NewRefuel.TABLE_NAME, columns, where, whereArgs, null, null, TableRefuel.NewRefuel.FUEL_ID + " DESC");

            return cursor;

    }

        //pobieramy tylko JEDNĄ OSTATNIA vartosc tabeli z wiersza km_counter
    public Cursor getLastKMCounter (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT MAX(" + TableRefuel.NewRefuel.KM_COUNTER + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME + " WHERE "
                + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + "";

        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    public Cursor getPrevKmCounter (int km_counter){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT MAX(" + TableRefuel.NewRefuel.KM_COUNTER + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME + " WHERE "
                + TableRefuel.NewRefuel.KM_COUNTER + " < " + km_counter + "";

        Cursor cursor = db.rawQuery(query, null);

        return cursor;

    }

    public Cursor getSingleRefuelRowById(Integer id_row, SQLiteDatabase db){

        String[] columns ={TableRefuel.NewRefuel.FUEL_ID, TableRefuel.NewRefuel.FUEL_AMOUNT, TableRefuel.NewRefuel.FUEL_PRICE,
                TableRefuel.NewRefuel.FUEL_CASH_SPEND, TableRefuel.NewRefuel.KM_COUNTER, TableRefuel.NewRefuel.FUEL_DATE,
                TableRefuel.NewRefuel.FUEL_FULL, TableRefuel.NewRefuel.FUEL_MISSED, TableRefuel.NewRefuel.FUEL_CAR, TableRefuel.NewRefuel.FUEL_NOTE};

        String selection = TableRefuel.NewRefuel.FUEL_ID + " LIKE ?"; //like ?
        String[] selection_args = {String.valueOf(id_row)};

        Cursor cursor = db.query(TableRefuel.NewRefuel.TABLE_NAME, columns, selection, selection_args, null, null, null);
        return cursor;
    }

    public Cursor getLastFuelUsage (String car_id, Integer km_counter){

        SQLiteDatabase db = this.getReadableDatabase();

        if (Build.VERSION.SDK_INT > 21) {
            String query = "Select printf(\"%.2f\", (((select fuel_amount from refuel where km_counter = " + km_counter + " and car = " + car_id
                    + " and fuel_full = 1)/(Select " + km_counter + " - (Select min(km_counter) from(select km_counter, fuel_full, fuel_missed from refuel where km_counter <= " + km_counter
                    + " and car = " + car_id + " order by km_counter desc limit 2) where fuel_missed != 1 and fuel_full = 1 and km_counter < " + km_counter + "))) * 100))";
            Cursor cursor = db.rawQuery(query, null);
            return cursor;
        }
        else {

            String query = "Select (((select fuel_amount from refuel where km_counter = " + km_counter + " and car = " + car_id
                    + " and fuel_full = 1)/(Select " + km_counter + " - (Select min(km_counter) from(select km_counter, fuel_full, fuel_missed from refuel where km_counter <= " + km_counter
                    + " and car = " + car_id + " order by km_counter desc limit 2) where fuel_missed != 1 and fuel_full = 1 and km_counter < " + km_counter + "))) * 100)";
            Cursor cursor = db.rawQuery(query, null);
            return cursor;

        }

        //return cursor;
    }

    public Cursor getDiffBettwenLast2KMC (String car_id, Integer km_counter){

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select(select "+km_counter+" -(select km_counter from (select km_counter, fuel_missed from refuel where km_counter <= "+km_counter+" and car = "+car_id+" order by km_counter desc limit 2 )where fuel_missed = 0 and km_counter < "+km_counter+"))";

        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }

    public void updateRefuelFuelUsage (String car_id, Integer km_counter, Float fuel_per100, SQLiteDatabase db){

        ContentValues contentValues = new ContentValues();
        contentValues.put(TableRefuel.NewRefuel.FUEL_PER_100,fuel_per100);

        String selection = TableRefuel.NewRefuel.FUEL_CAR + " = ? and " + TableRefuel.NewRefuel.KM_COUNTER + " = ?";
        String[] selection_args = {car_id, String.valueOf(km_counter)}; //wskakuje za znak zapytania '?' z selection

        //int count = db.update(TableRefuel.NewRefuel.TABLE_NAME, contentValues, selection, selection_args);
        db.update(TableRefuel.NewRefuel.TABLE_NAME, contentValues, selection, selection_args);

        //return count;
    }

    public void updateRefuelKmDiff(String car_id, Integer km_counter, Integer km_diff, SQLiteDatabase db){

        ContentValues contentValues = new ContentValues();
        contentValues.put(TableRefuel.NewRefuel.FUEL_KM_DIFF,km_diff);

        String selection = TableRefuel.NewRefuel.FUEL_CAR + " = ? and " + TableRefuel.NewRefuel.KM_COUNTER + " = ?";
        String[] selection_args = {car_id,String.valueOf(km_counter)};
        db.update(TableRefuel.NewRefuel.TABLE_NAME, contentValues, selection, selection_args);
    }

    public Cursor getNextKmCounter (Integer km_counter, String car_id){

        SQLiteDatabase db = this.getReadableDatabase();

        //select Odo from log where Odo > 147634 limit 1
        String query = "SELECT " + TableRefuel.NewRefuel.KM_COUNTER + " FROM " + TableRefuel.NewRefuel.TABLE_NAME + " WHERE " + TableRefuel.NewRefuel.KM_COUNTER + " > " + km_counter + " AND "
                + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + " LIMIT 1";

        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }

    /*
    Obsługa tabeli Kosztow @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    */

    public void addCost (Float cash_spend, String cash_name, String cash_date, String cost_type, String cost_note, String cost_car, SQLiteDatabase db){

        ContentValues contentValues = new ContentValues();
        contentValues.put(TableCost.NewCost.COST_CASH_SPEND,cash_spend);
        contentValues.put(TableCost.NewCost.COST_NAME,cash_name);
        contentValues.put(TableCost.NewCost.COST_DATE,cash_date);
        contentValues.put(TableCost.NewCost.COST_TYPE,cost_type);
        contentValues.put(TableCost.NewCost.COST_NOTE,cost_note);
        contentValues.put(TableCost.NewCost.COST_CAR,cost_car);

        db.insert(TableCost.NewCost.TABLE_NAME, null, contentValues);
    }

    public Cursor getAllCostInformations(SQLiteDatabase db, String car_id) {

        String[] columns = {TableCost.NewCost.COST_ID,TableCost.NewCost.COST_CASH_SPEND,TableCost.NewCost.COST_NAME,
                TableCost.NewCost.COST_DATE,TableCost.NewCost.COST_NOTE,TableCost.NewCost.COST_TYPE,
                TableCost.NewCost.COST_CAR};
        String where = TableCost.NewCost.COST_CAR + "= ?";
        String[] whereArgs = {car_id};

        Cursor cursor = db.query(TableCost.NewCost.TABLE_NAME, columns, where, whereArgs, null, null, TableCost.NewCost.COST_ID + " DESC");

        return cursor;

    }

    public void deleteCostRow (long id){

        SQLiteDatabase db = getWritableDatabase();
        String string = String.valueOf(id);
        db.execSQL("DELETE FROM " + TableCost.NewCost.TABLE_NAME + " WHERE id = " + string + "");
    }

    public void deleteAllCostDataByCarId (long car_id){

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TableCost.NewCost.TABLE_NAME + " WHERE " + TableCost.NewCost.COST_CAR + " = " + car_id + "");
    }

    public int updateCostRow (int id_row, Float cash_spend, String cost_name, String cost_date, String cost_type, String cost_note/*, String cost_car*/, SQLiteDatabase db){

        ContentValues contentValues = new ContentValues();
        contentValues.put(TableCost.NewCost.COST_CASH_SPEND,cash_spend);
        contentValues.put(TableCost.NewCost.COST_NAME,cost_name);
        contentValues.put(TableCost.NewCost.COST_DATE,cost_date);
        contentValues.put(TableCost.NewCost.COST_TYPE,cost_type);
        contentValues.put(TableCost.NewCost.COST_NOTE,cost_note);
        //contentValues.put(TableCost.NewCost.COST_CAR,cost_car);

        String selection = TableCost.NewCost.COST_ID + " LIKE ?"; // nie powinno byc = ? zamiast " LIKE ? "
        String[] selection_args = {String.valueOf(id_row)};

        int count = db.update(TableCost.NewCost.TABLE_NAME, contentValues, selection, selection_args);

        return count;
    }

    public Cursor getSingleCostRowById(Integer id_row, SQLiteDatabase db){

        String[] columns ={TableCost.NewCost.COST_ID, TableCost.NewCost.COST_CASH_SPEND, TableCost.NewCost.COST_NAME,
                TableCost.NewCost.COST_DATE, TableCost.NewCost.COST_NOTE, TableCost.NewCost.COST_TYPE,TableCost.NewCost.COST_CAR};

        String selection = TableCost.NewCost.COST_ID + " LIKE ?"; //like ?
        String[] selection_args = {String.valueOf(id_row)};

        Cursor cursor = db.query(TableCost.NewCost.TABLE_NAME, columns, selection, selection_args, null, null, null);

        return cursor;
    }


    /*
    Obsługa tabeli Samochodów @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    */

    public void addCar (String car_name, String car_model, String car_mfg_date, String car_note, String car_fuel_type,
                        String car_start_counter, String car_brand, String car_fuel_units, String car_dist_units,
                        String car_fuel_usage_units, String car_engine_cap, String car_reg_num, SQLiteDatabase db){

        ContentValues contentValues = new ContentValues();
        contentValues.put(TableCar.NewCar.CAR_NAME,car_name);
        contentValues.put(TableCar.NewCar.CAR_MODEL,car_model);
        contentValues.put(TableCar.NewCar.CAR_MFG_DATE,car_mfg_date);
        contentValues.put(TableCar.NewCar.CAR_NOTE,car_note);
        contentValues.put(TableCar.NewCar.CAR_FUEL_TYPE,car_fuel_type);
        contentValues.put(TableCar.NewCar.CAR_START_COUNTER,car_start_counter);
        contentValues.put(TableCar.NewCar.CAR_BRAND,car_brand);
        contentValues.put(TableCar.NewCar.CAR_FUEL_UNITS,car_fuel_units);
        contentValues.put(TableCar.NewCar.CAR_DIST_UNITS,car_dist_units);
        contentValues.put(TableCar.NewCar.CAR_FUEL_USAGE_UNITS,car_fuel_usage_units);
        contentValues.put(TableCar.NewCar.CAR_ENGINE_CAP, car_engine_cap);
        contentValues.put(TableCar.NewCar.CAR_REG_NUM, car_reg_num);

        db.insert(TableCar.NewCar.TABLE_NAME, null, contentValues);
    }

    public Cursor getAllCarInformations (SQLiteDatabase db){

        String[] columns = {TableCar.NewCar.CAR_ID, TableCar.NewCar.CAR_NAME,TableCar.NewCar.CAR_MODEL,TableCar.NewCar.CAR_MFG_DATE,TableCar.NewCar.CAR_NOTE,
                TableCar.NewCar.CAR_FUEL_TYPE,TableCar.NewCar.CAR_START_COUNTER,TableCar.NewCar.CAR_BRAND,TableCar.NewCar.CAR_FUEL_UNITS,TableCar.NewCar.CAR_DIST_UNITS,
                TableCar.NewCar.CAR_FUEL_USAGE_UNITS,TableCar.NewCar.CAR_ENGINE_CAP, TableCar.NewCar.CAR_REG_NUM};

        Cursor cursor = db.query(TableCar.NewCar.TABLE_NAME, columns, null, null, null, null, null);//TableCar.NewCar.CAR_ID + " DESC");

        return cursor;
    }

    public void deleteCarRow(long id) {

        SQLiteDatabase db = getWritableDatabase();
        String string = String.valueOf(id);
        db.execSQL("DELETE FROM " + TableCar.NewCar.TABLE_NAME + " WHERE id = " + string + "");
    }

    public int updateCarRow (int id_row, String car_name, String car_model, String car_mfg_date, String car_note, String car_fuel_type,
                             String car_start_counter, String car_brand, String car_fuel_units, String car_dist_units,
                             String car_fuel_usage_units, String car_engine_cap, String car_reg_num, SQLiteDatabase db ){

        ContentValues contentValues = new ContentValues();
        contentValues.put(TableCar.NewCar.CAR_NAME,car_name);
        contentValues.put(TableCar.NewCar.CAR_MODEL,car_model);
        contentValues.put(TableCar.NewCar.CAR_MFG_DATE,car_mfg_date);
        contentValues.put(TableCar.NewCar.CAR_NOTE,car_note);
        contentValues.put(TableCar.NewCar.CAR_FUEL_TYPE,car_fuel_type);
        contentValues.put(TableCar.NewCar.CAR_START_COUNTER,car_start_counter);
        contentValues.put(TableCar.NewCar.CAR_BRAND,car_brand);
        contentValues.put(TableCar.NewCar.CAR_FUEL_UNITS,car_fuel_units);
        contentValues.put(TableCar.NewCar.CAR_DIST_UNITS,car_dist_units);
        contentValues.put(TableCar.NewCar.CAR_FUEL_USAGE_UNITS,car_fuel_usage_units);
        contentValues.put(TableCar.NewCar.CAR_ENGINE_CAP,car_engine_cap);
        contentValues.put(TableCar.NewCar.CAR_REG_NUM, car_reg_num);

        String selection = TableCar.NewCar.CAR_ID + " LIKE ?";
        String[] selection_args = {String.valueOf(id_row)};

        int count = db.update(TableCar.NewCar.TABLE_NAME, contentValues, selection, selection_args);

        return count;
    }

    public Cursor getSingleCarRowById (Integer id_row, SQLiteDatabase db){

        String[] columns = {TableCar.NewCar.CAR_ID, TableCar.NewCar.CAR_NAME, TableCar.NewCar.CAR_MODEL, TableCar.NewCar.CAR_MFG_DATE,
                TableCar.NewCar.CAR_NOTE, TableCar.NewCar.CAR_FUEL_TYPE, TableCar.NewCar.CAR_START_COUNTER, TableCar.NewCar.CAR_BRAND,
                TableCar.NewCar.CAR_FUEL_UNITS, TableCar.NewCar.CAR_DIST_UNITS, TableCar.NewCar.CAR_FUEL_USAGE_UNITS, TableCar.NewCar.CAR_ENGINE_CAP, TableCar.NewCar.CAR_REG_NUM};

        String selection = TableCar.NewCar.CAR_ID + " LIKE ?";
        String[] selection_args = {String.valueOf(id_row)};

        Cursor cursor = db.query(TableCar.NewCar.TABLE_NAME, columns, selection, selection_args, null, null, null);

        return cursor;
    }

    /*
    Statystyki @@@@@@@@@@@@@@@@@@@@@@@@@@@@@2
     */

    public String getCarFuelType (String car_id){

        String car_fuel_type;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + TableCar.NewCar.CAR_FUEL_TYPE + " FROM " + TableCar.NewCar.TABLE_NAME
                + " WHERE " + TableCar.NewCar.CAR_ID + " = " + car_id;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        if (cursor.moveToFirst()) {
            car_fuel_type = cursor.getString(0);
            return car_fuel_type;
        }
        else {
            car_fuel_type = "Error";
            return car_fuel_type;
        }
    }

    public String getCarFuelUnits (String car_id){

        String car_fuel_units;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + TableCar.NewCar.CAR_FUEL_UNITS + " FROM " + TableCar.NewCar.TABLE_NAME
                + " WHERE " + TableCar.NewCar.CAR_ID + " = " + car_id;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        if (cursor.moveToFirst()){
            car_fuel_units = cursor.getString(0);
            return car_fuel_units;
        }
        else {
            car_fuel_units = "Error";
            return car_fuel_units;
        }
    }

    public String getCarFuelUsageUnits (String car_id){

        String car_fuel_usage_units;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + TableCar.NewCar.CAR_FUEL_USAGE_UNITS + " FROM " + TableCar.NewCar.TABLE_NAME
                + " WHERE " + TableCar.NewCar.CAR_ID + " = " + car_id;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        if (cursor.moveToFirst()){
            car_fuel_usage_units = cursor.getString(0);
            return car_fuel_usage_units;
        }
        else {
            car_fuel_usage_units = "Error";
            return car_fuel_usage_units;
        }
    }

    public String getCarDistUnits (String car_id){

        String car_dist_units;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + TableCar.NewCar.CAR_DIST_UNITS + " FROM " + TableCar.NewCar.TABLE_NAME
                + " WHERE " + TableCar.NewCar.CAR_ID + " = " + car_id;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        if (cursor.moveToFirst()){
            car_dist_units = cursor.getString(0);
            return car_dist_units;
        }
        else {
            car_dist_units = "Error";
            return car_dist_units;
        }
    }

    // Ogolne @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    //2
    public Cursor getTotalKMcounted (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT MAX(" + TableRefuel.NewRefuel.KM_COUNTER + ") - MIN(" + TableRefuel.NewRefuel.KM_COUNTER
                + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + "";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //3
    public Cursor getTotalFuelCount (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + TableRefuel.NewRefuel.FUEL_AMOUNT + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + "";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //4
    public Cursor getTotalFuelCostCount (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + TableRefuel.NewRefuel.FUEL_CASH_SPEND + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + "";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //5
    public Cursor getTotalCostsCostCount (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + TableCost.NewCost.COST_CASH_SPEND + ") FROM " + TableCost.NewCost.TABLE_NAME
                + " WHERE " + TableCost.NewCost.COST_CAR + " = " + car_id + "";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //6
    public Cursor getRefuelQuantity (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + "";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //7
    public Cursor getCostQuantity (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TableCost.NewCost.TABLE_NAME
                + " WHERE " + TableCost.NewCost.COST_CAR + " = " + car_id + "";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }


    // Tankowania @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    //9
    public Cursor getRefuelQuantityThisMth (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + " AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " >= (date('now','start of month')) AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " <= (date('now'))";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //10
    public Cursor getRefuelQuantityPrevMth (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + " AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " >= (date('now','start of month','-1 month')) AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " <= (date('now','start of month','-1 day'))";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //11
    public Cursor getRefuelQuantityThisYr (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + " AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " >= (date('now','start of year')) AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " <= (date('now','start of year','+1 year','-1 day'))";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //12
    public Cursor getRefuelQuantityPrevYr (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + " AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " >= (date('now','start of year','-1 year')) AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " <= (date('now','start of year','-1 day'))";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //13
    public Cursor getRefuelBiggest (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT MAX(" + TableRefuel.NewRefuel.FUEL_AMOUNT + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id;
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //14
    public Cursor getRefuelSmallest (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT MIN(" + TableRefuel.NewRefuel.FUEL_AMOUNT + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id;
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //15
    public Cursor getFuelAmountThisMth (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + TableRefuel.NewRefuel.FUEL_AMOUNT + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + " AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " >= (date('now','start of month')) AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " <= (date('now'))";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //16
    public Cursor getFuelAmountPrevMth(String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + TableRefuel.NewRefuel.FUEL_AMOUNT + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + " AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " >= (date('now','start of month','-1 month')) AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " <= (date('now','start of month','-1 day'))";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //17
    public Cursor getFuelAmountThisYr(String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + TableRefuel.NewRefuel.FUEL_AMOUNT + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + " AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " >= (date('now','start of year')) AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " <= (date('now','start of year','+1 year','-1 day'))";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //18
    public Cursor getFuelAmountPrevYr(String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + TableRefuel.NewRefuel.FUEL_AMOUNT + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + " AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " >= (date('now','start of year','-1 year')) AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " <= (date('now','start of year','-1 day'))";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    // paliwo ostatnie 12 msc
    public Cursor getFuelAmountLast12Mth (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT SUM(" + TableRefuel.NewRefuel.FUEL_AMOUNT + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + " AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " <= (date('now')) AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " >= (date('now','-12 month'))";

        Cursor cursor = db.rawQuery(query,null);
        return cursor;
    }


    //WSZYSTKIE KOSZTY TANKOWAN (Miesiace i lata)
    //koszty za paliwo ten miesiąc
    public Cursor getRefuelCostsThisMth(String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + TableRefuel.NewRefuel.FUEL_CASH_SPEND + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + " AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " >= (date('now','start of month')) AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " <= (date('now'))";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //koszty paliwo poprzedni miesiąc
    public Cursor getRefuelCostsPrevMth(String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + TableRefuel.NewRefuel.FUEL_CASH_SPEND + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + " AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " >= (date('now','start of month','-1 month')) AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " <= (date('now','start of month','-1 day'))";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //Koszty paliwo ten rok
    public Cursor getRefuelCostsThisYr(String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + TableRefuel.NewRefuel.FUEL_CASH_SPEND + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + " AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " >= (date('now','start of year')) AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " <= (date('now','start of year','+1 year','-1 day'))";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //koszty paliwo poprzedni rok
    public Cursor getRefuelCostsPrevYr(String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + TableRefuel.NewRefuel.FUEL_CASH_SPEND + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + " AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " >= (date('now','start of year','-1 year')) AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " <= (date('now','start of year','-1 day'))";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    // koszty paliwo ostatnie 12 msc
    public Cursor getRefuelCostsLast12Mth (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT SUM(" + TableRefuel.NewRefuel.FUEL_CASH_SPEND + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + " AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " <= (date('now')) AND "
                + TableRefuel.NewRefuel.FUEL_DATE + " >= (date('now','-12 month'))";

        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }

    //WSZYSTKIE WYDATKI NA KOSZT (Miesiace i lata)
    //wydatki na koszt ten miesiąc
    public Cursor getCostCostsThisMth(String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + TableCost.NewCost.COST_CASH_SPEND + ") FROM " + TableCost.NewCost.TABLE_NAME
                + " WHERE " + TableCost.NewCost.COST_CAR + " = " + car_id + " AND "
                + TableCost.NewCost.COST_DATE + " >= (date('now','start of month')) AND "
                + TableCost.NewCost.COST_DATE + " <= (date('now'))";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //wydatki na koszt poprzedni miesiac
    public Cursor getCostCostsPrevMth(String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + TableCost.NewCost.COST_CASH_SPEND + ") FROM " + TableCost.NewCost.TABLE_NAME
                + " WHERE " + TableCost.NewCost.COST_CAR + " = " + car_id + " AND "
                + TableCost.NewCost.COST_DATE + " >= (date('now','start of month','-1 month')) AND "
                + TableCost.NewCost.COST_DATE + " <= (date('now','start of month','-1 day'))";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //wydatki na koszt ten rok
    public Cursor getCostCostsThisYr(String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + TableCost.NewCost.COST_CASH_SPEND + ") FROM " + TableCost.NewCost.TABLE_NAME
                + " WHERE " + TableCost.NewCost.COST_CAR + " = " + car_id + " AND "
                + TableCost.NewCost.COST_DATE + " >= (date('now','start of year')) AND "
                + TableCost.NewCost.COST_DATE + " <= (date('now','start of year','+1 year','-1 day'))";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //wydatki na koszt ten rok
    public Cursor getCostCostsPrevYr(String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + TableCost.NewCost.COST_CASH_SPEND + ") FROM " + TableCost.NewCost.TABLE_NAME
                + " WHERE " + TableCost.NewCost.COST_CAR + " = " + car_id + " AND "
                + TableCost.NewCost.COST_DATE + " >= (date('now','start of year','-1 year')) AND "
                + TableCost.NewCost.COST_DATE + " <= (date('now','start of year','-1 day'))";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //wytadki na koszt ostatnie 12 miesiecy

    public Cursor getCostCostsLast12Mth(String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + TableCost.NewCost.COST_CASH_SPEND + ") FROM " + TableCost.NewCost.TABLE_NAME
                + " WHERE " + TableCost.NewCost.COST_CAR + " = " + car_id + " AND "
                + TableCost.NewCost.COST_DATE + " <= (date('now')) AND "
                + TableCost.NewCost.COST_DATE + " >= (date('now','-12 month'))";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //WYDATKI (PALIWO)
    //Paliwo najwyższy rachunek (fuel_cash_spend)
    public Cursor getHighestRefuelBill(String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT MAX(" + TableRefuel.NewRefuel.FUEL_CASH_SPEND + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id;
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //Paliwo najniższy rachunek (fuel_cash_spend)
    public Cursor getLowestRefuelBill(String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT MIN(" + TableRefuel.NewRefuel.FUEL_CASH_SPEND + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id;
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //Paliwo najwyższy rachunek (fuel_cash_spend)
    public Cursor getHighestRefuelFuelPrice(String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT MAX(" + TableRefuel.NewRefuel.FUEL_PRICE + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id;
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //Paliwo najniższy rachunek (fuel_cash_spend)
    public Cursor getLowestRefuelFuelPrice(String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT MIN(" + TableRefuel.NewRefuel.FUEL_PRICE + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id;
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    //SREDNIE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    //Sredni dzienny koszt paliwa
    public Cursor getAvgDialyFuelCost(String car_id){

        SQLiteDatabase db = this.getReadableDatabase();

        //select ((select sum(fuel_cash_spend) from refuel where fuel_car = XX) / (select cas (julianday('now') - julianday((select min(fuel_date) from refuel where car = XX )) as integer));
        String query = "SELECT ((SELECT SUM(" + TableRefuel.NewRefuel.FUEL_CASH_SPEND + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + ") / (SELECT CAST (julianday('now') - julianday((SELECT MIN( "
                + TableRefuel.NewRefuel.FUEL_DATE + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR
                + " = " + car_id +" )) AS INTEGER)))";


        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    public Cursor getMaxRefuelDate (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String max_date_query = "SELECT MAX(" + TableRefuel.NewRefuel.FUEL_DATE + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id;

        Cursor max_date_cur = db.rawQuery(max_date_query,null);

        return max_date_cur;

    }

    public Cursor getMinRefuelDate (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();
        String min_date_query = "SELECT MIN(" + TableRefuel.NewRefuel.FUEL_DATE + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id;

        Cursor min_date_cur = db.rawQuery(min_date_query,null);

        return min_date_cur;

    }

    public Cursor getAvgMonthlyFuelCost(String max_date, String min_date, float sum_fuel_cash_spend){

        SQLiteDatabase db = this.getReadableDatabase();

        if (Build.VERSION.SDK_INT > 21) {
            String monthly_fuel_cost_query = "SELECT printf(\"%.2f\",(" + String.valueOf(sum_fuel_cash_spend) + " / (SELECT printf(\"%.2f\",(SELECT julianday('"
                    + max_date + "') - julianday('" + min_date + "')) / 30.4))))";

            Cursor cursor = db.rawQuery(monthly_fuel_cost_query, null);

            return cursor;
        }
        else {
                String monthly_fuel_cost_query = "SELECT (" + String.valueOf(sum_fuel_cash_spend) + " / (SELECT (SELECT julianday('"
                        + max_date + "') - julianday('" + min_date + "')) / 30.4))";

                Cursor cursor = db.rawQuery(monthly_fuel_cost_query, null);

                return cursor;
        }
    }

    public Cursor getDayBettwenDates(String max_date, String min_date){

        SQLiteDatabase db = this.getReadableDatabase();

        if(Build.VERSION.SDK_INT > 21) {
            String query = "SELECT printf(\"%.2f\", julianday('" + max_date + "') - julianday('" + min_date + "'))";

            Cursor cursor = db.rawQuery(query, null);

            return cursor;
        }
        else {
            String query = "SELECT julianday('" + max_date + "') - julianday('" + min_date + "')";

            Cursor cursor = db.rawQuery(query, null);

            return cursor;
        }
    }


    public Cursor getAvgRefuelCap (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT AVG(" + TableRefuel.NewRefuel.FUEL_AMOUNT + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id;

        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }

    public Cursor getAvgRefuelBill (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT AVG(" + TableRefuel.NewRefuel.FUEL_CASH_SPEND + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id;

        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }

    public Cursor getAvgFuelUsage (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT (SELECT SUM(" + TableRefuel.NewRefuel.FUEL_PER_100 + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME + " WHERE " + TableRefuel.NewRefuel.FUEL_PER_100 + " != 0 AND "
                + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + ") / (SELECT COUNT(" + TableRefuel.NewRefuel.FUEL_PER_100 + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME + " WHERE "
                + TableRefuel.NewRefuel.FUEL_PER_100 + " != 0 AND " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + ")";

        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }

    public Cursor getDaysSinceLastRefuel (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();

        //select cast (julianday('now') - julianday((Select Data from Log where CarID = 2  and LogId =(Select max(LogID)from Log where CarID = 2) )) as Integer)
        String query = "SELECT CAST (JULIANDAY('now') - JULIANDAY((SELECT " + TableRefuel.NewRefuel.FUEL_DATE + " FROM " + TableRefuel.NewRefuel.TABLE_NAME + " WHERE "
                + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + " AND " + TableRefuel.NewRefuel.FUEL_ID + " = (SELECT MAX(" + TableRefuel.NewRefuel.FUEL_ID + ") FROM "
                + TableRefuel.NewRefuel.TABLE_NAME + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + "))) AS INTEGER)";

        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }

    public Cursor getLastFuelUsage (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();

        //SELECT lp100 FROM Log WHERE CarID = 2 and LogID = (SELECT MAX(LogID) FROM Log WHERE CarID = 2)
        String query = "SELECT " + TableRefuel.NewRefuel.FUEL_PER_100 + " FROM " + TableRefuel.NewRefuel.TABLE_NAME + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = "
                + car_id + " AND " + TableRefuel.NewRefuel.FUEL_ID + " = (SELECT MAX(" + TableRefuel.NewRefuel.FUEL_ID + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME + " WHERE "
                + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + ")";

        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }

    public Cursor getLastRefuelDate (String car_id){


        SQLiteDatabase db = this.getReadableDatabase();

        //Select Data from Log where CarID = 2  and LogId =(Select max(LogID)from Log where CarID = 2)
        String query = "SELECT " + TableRefuel.NewRefuel.FUEL_DATE + " FROM " + TableRefuel.NewRefuel.TABLE_NAME + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR
                + " = " + car_id + " AND " + TableRefuel.NewRefuel.FUEL_ID + " = (SELECT MAX(" + TableRefuel.NewRefuel.FUEL_ID + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + ")";

        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }

    public Cursor getChartFuelUsage (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();

        //select data, lp100 from Log where car id = and lp100 is not null and lp100 > 0 order by data
        String query = "SELECT " + TableRefuel.NewRefuel.FUEL_DATE + ", " + TableRefuel.NewRefuel.FUEL_PER_100 + " FROM " + TableRefuel.NewRefuel.TABLE_NAME + " WHERE "
                + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + " AND " + TableRefuel.NewRefuel.FUEL_PER_100 + " IS NOT NULL AND " + TableRefuel.NewRefuel.FUEL_PER_100
                + " > 0 ORDER BY " + TableRefuel.NewRefuel.FUEL_DATE;

        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }

    public Cursor getChartMonthlyFuelCost (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();

        //select strftime('%Y/%m', Data), sum(Price) from log where CarID = 2 group by strftime('%Y-%m', Data)
        String query = "SELECT STRFTIME('%Y/%m'," + TableRefuel.NewRefuel.FUEL_DATE + "), SUM(" + TableRefuel.NewRefuel.FUEL_CASH_SPEND + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME
                + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + " GROUP BY STRFTIME('%Y/%m'," + TableRefuel.NewRefuel.FUEL_DATE + ")";

        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }

    public Cursor getChartFuelPrice (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();

        //select strftime('%Y/%m/%d', Data) as yr, volumeprice from log order by Data
        String query = "SELECT STRFTIME('%Y/%m/%d', " + TableRefuel.NewRefuel.FUEL_DATE + "), " + TableRefuel.NewRefuel.FUEL_PRICE + " FROM " + TableRefuel.NewRefuel.TABLE_NAME
                +" WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id + " ORDER BY " + TableRefuel.NewRefuel.FUEL_DATE;

        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }

    public Cursor getAvgFuelPrice (String car_id){

        SQLiteDatabase db = this.getReadableDatabase();

        //select printf("%.3f",(select sum(volumeprice) from log where CarID = 2) / (select count(volumeprice) from log where CarID = 2))
        String query = "SELECT PRINTF(\"%.3f\", (SELECT SUM(" + TableRefuel.NewRefuel.FUEL_PRICE + ") FROM " +TableRefuel.NewRefuel.TABLE_NAME + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = "
                + car_id + ") / (SELECT COUNT(" + TableRefuel.NewRefuel.FUEL_PRICE + ") FROM " + TableRefuel.NewRefuel.TABLE_NAME + " WHERE " + TableRefuel.NewRefuel.FUEL_CAR + " = " + car_id +  "))";

        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }

    // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ SETTINGS @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    public Cursor getSettings(){

        SQLiteDatabase db = this.getReadableDatabase();

        //0 id, 1 currency, 2 dist units, 3 fuel cap, 4 fuel usage units
        String query = "SELECT " + TableSettings.Settings.CURRENCY + ", " + TableSettings.Settings.DISTANCE_UNITS + ", "
                + TableSettings.Settings.FUEL_CAP + ", " + TableSettings.Settings.FUEL_USAGE_UNITS + " FROM "
                + TableSettings.Settings.TABLE_NAME;

        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }

    public int updateSettings (String currency, String distance, String fuel_cap, String fuel_units, SQLiteDatabase db){

        ContentValues contentValues = new ContentValues();
        contentValues.put(TableSettings.Settings.CURRENCY, currency);
        contentValues.put(TableSettings.Settings.DISTANCE_UNITS, distance);
        contentValues.put(TableSettings.Settings.FUEL_CAP, fuel_cap);
        contentValues.put(TableSettings.Settings.FUEL_USAGE_UNITS, fuel_units);

        //String selection = TableRefuel.NewRefuel.FUEL_ID + " LIKE ?";
        //String[] selection_args = {String.valueOf(id_row)}; //wskakuje za znak zapytania '?' z selection

        int count = db.update(TableSettings.Settings.TABLE_NAME, contentValues, null, null);
        return count;
    }

    // ############################### Reminders ###########################

    public void addRemind (String category, String title, String date, String hour, boolean repeat,
                           int repeat_cycle, int repeat_kmcounter, int id_reminder, SQLiteDatabase db){

        ContentValues contentValues = new ContentValues();
        contentValues.put(TableReminder.NewReminder.CATEGORY, category);
        contentValues.put(TableReminder.NewReminder.TITLE, title);
        contentValues.put(TableReminder.NewReminder.DATE, date);
        contentValues.put(TableReminder.NewReminder.HOUR, hour);
        contentValues.put(TableReminder.NewReminder.REPEAT, repeat);
        contentValues.put(TableReminder.NewReminder. REPEAT_CYCLE, repeat_cycle);
        contentValues.put(TableReminder.NewReminder. REPEAT_KMCOUNTER, repeat_kmcounter);
        contentValues.put(TableReminder.NewReminder.REMINDER_ID, id_reminder);

        db.insert(TableReminder.NewReminder.TABLE_NAME, null, contentValues);
    }

    public Cursor getAllReminders(){

        SQLiteDatabase db = this.getReadableDatabase();
        //0 category 1 title 2 date 3 hour 4 repeat 5 repeat cycle 6 repeat kmcounter 7 reminder id
        String[] columns = {TableReminder.NewReminder.ID, TableReminder.NewReminder.CATEGORY, TableReminder.NewReminder.TITLE, TableReminder.NewReminder.DATE,TableReminder.NewReminder.HOUR,
                TableReminder.NewReminder.REPEAT,TableReminder.NewReminder.REPEAT_CYCLE, TableReminder.NewReminder.REPEAT_KMCOUNTER,
                TableReminder.NewReminder.REMINDER_ID};

        Cursor cursor = db.query(TableReminder.NewReminder.TABLE_NAME, columns, null, null, null, null, TableReminder.NewReminder.ID + " DESC");

        return  cursor;
    }

    public void deleteReminder (long id){

        SQLiteDatabase db = getWritableDatabase();
        String string = String.valueOf(id);
        db.execSQL("DELETE FROM " + TableReminder.NewReminder.TABLE_NAME + " WHERE " + TableReminder.NewReminder.ID + " = " + string + "");
    }

    public Cursor getSingleReminderRowById (Integer id_row){

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + TableReminder.NewReminder.REMINDER_ID + " FROM " + TableReminder.NewReminder.TABLE_NAME
                + " WHERE " + TableReminder.NewReminder.ID + " = " + id_row;

        Cursor cursor = db.rawQuery(query,null);

        return cursor;
    }

    public Cursor getNotificationIDRowById(int id_row){

        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {TableReminder.NewReminder.REMINDER_ID};

        String selection = TableReminder.NewReminder.ID + " LIKE ?";
        String[] selection_args = {String.valueOf(id_row)};

        Cursor cursor = db.query(TableReminder.NewReminder.TABLE_NAME, columns, selection, selection_args, null, null, null);

        return cursor;
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
