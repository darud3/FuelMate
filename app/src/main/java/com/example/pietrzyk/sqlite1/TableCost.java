package com.example.pietrzyk.sqlite1;

/**
 * Created by pietrzyk on 2016-06-21.
 */
public class TableCost {
    //dodane w wersji 2 bazy
    public static abstract class NewCost{
        public static final String TABLE_NAME = "cost";
        public static final String COST_ID = "id"; //int
        public static final String COST_CASH_SPEND = "cost_cash_spend"; //float
        public static final String COST_NAME = "cost_name"; //varchar
        public static final String COST_DATE = "cost_date"; //varchar
        public static final String COST_NOTE = "cost_note"; //varchar
        public static final String COST_TYPE = "cost_type"; //varchar
        public static final String COST_CAR = "car"; //varchar
    }

}
