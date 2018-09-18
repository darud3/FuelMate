package com.example.pietrzyk.sqlite1;

public class TableCar {

    //dodane w wersji 2 bazy
    public static abstract class NewCar{
        public static final String TABLE_NAME = "car";
        public static final String CAR_ID = "id"; //int
        public static final String CAR_NAME = "car_name"; //varchar
        public static final String CAR_MODEL = "car_model"; //varchar
        public static final String CAR_MFG_DATE = "car_mfg_date"; //varchar
        public static final String CAR_NOTE = "car_note"; //varchar
        public static final String CAR_FUEL_TYPE = "car_fuel_type"; //varchar
        public static final String CAR_START_COUNTER = "car_start_counter"; //varchar
        public static final String CAR_BRAND = "car_brand"; //varchar
        public static final String CAR_FUEL_UNITS = "car_fuel_units"; //varchar
        public static final String CAR_DIST_UNITS = "car_dist_units"; //varchar
        public static final String CAR_FUEL_USAGE_UNITS = "car_fuel_usage_units"; //varchar
        public static final String CAR_ENGINE_CAP ="car_engine_cap"; //varchar
        public static final String CAR_REG_NUM ="car_reg_num";

    }

}
