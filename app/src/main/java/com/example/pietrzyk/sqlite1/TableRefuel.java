package com.example.pietrzyk.sqlite1;

public class TableRefuel {

    public static abstract class NewRefuel
    {
        //FUEL_AMOUNT to klucz uzywany w programie
        //fuel_amount to nazwa w tabeli
        public static final String TABLE_NAME = "refuel";
        public static final String FUEL_ID = "id"; //int
        public static final String FUEL_AMOUNT = "fuel_amount"; //float
        public static final String FUEL_PRICE = "fuel_price"; //float
        public static final String FUEL_CASH_SPEND = "fuel_cash_spend"; //float
        public static final String KM_COUNTER = "km_counter"; //float
        public static final String FUEL_DATE = "fuel_date"; //varchar
        public static final String FUEL_TYPE = "fuel_type"; //varchar
        public static final String FUEL_CAR = "car"; //varchar
        public static final String FUEL_FULL = "fuel_full"; //char
        public static final String FUEL_PER_100 = "fuel_p100"; //float
        public static final String FUEL_MISSED = "fuel_missed"; //char
        public static final String FUEL_KM_DIFF = "fuel_km_diff"; //int
        public static final String FUEL_NOTE = "note"; //varchar

    }
}
