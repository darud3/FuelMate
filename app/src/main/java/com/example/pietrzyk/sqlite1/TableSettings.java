package com.example.pietrzyk.sqlite1;

public class TableSettings {

    public static abstract class Settings {

        //0 id 1 currency 2 dist units 3 fuel cap 4 fuel usage units
        public static final String TABLE_NAME = "settings";
        public static final String ID = "id_setting";
        public static final String CURRENCY = "currency";
        public static final String DISTANCE_UNITS = "dist_units";
        public static final String FUEL_CAP = "fuel_cap";
        public static final String FUEL_USAGE_UNITS = "fuel_usage_units";
    }
}
