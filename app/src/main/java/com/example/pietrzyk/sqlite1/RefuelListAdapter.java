package com.example.pietrzyk.sqlite1;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RefuelListAdapter extends ArrayAdapter{ //implements Filterable {

    List list = new ArrayList();
    DBHelper dbHelper;

    public RefuelListAdapter(Context context, int resource) {
        super(context, resource);
    }

    static class LayoutHandler{

        //tu umieszczamy vidoki z naszego layoutu pojedynczego itemu z list view
        TextView KM_COUNTER, FUEL_AMOUNT, FUEL_PRICE, CASH_SPEND, FUEL_DATE, FUEL_FULL, FUEL_KM_DIFF, FUEL_USAGE;
        LinearLayout LEFT_BAR_LAYOUT;
    }

    @Override
    public void add(Object object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    //przed dodaniem tego musimy utworzyc klase statyczna dla przechowywania layoutu
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //getSettings();




        View row = convertView;
        LayoutHandler layoutHandler;
        if (row == null){
            //Nie mam pojecia co to robi....
            LayoutInflater layoutInflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = layoutInflater.inflate(R.layout.refuel_list_item,parent,false);
            layoutHandler = new LayoutHandler();
            layoutHandler.KM_COUNTER = (TextView)row.findViewById(R.id.lv_refuel_km_counter_text);
            layoutHandler.FUEL_AMOUNT = (TextView)row.findViewById(R.id.lv_refuel_fuel_amount_text);
            layoutHandler.FUEL_PRICE = (TextView)row.findViewById(R.id.lv_refuel_fuel_price_text);
            layoutHandler.CASH_SPEND = (TextView)row.findViewById(R.id.lv_refuel_cost_text);
            layoutHandler.FUEL_DATE = (TextView)row.findViewById(R.id.lv_refuel_data_text);
            layoutHandler.FUEL_FULL = (TextView)row.findViewById(R.id.lv_refuel_fullx_text);
            layoutHandler.FUEL_KM_DIFF = (TextView)row.findViewById(R.id.lv_refuel_km_diff_text);
            layoutHandler.FUEL_USAGE = (TextView)row.findViewById(R.id.lv_refuel_fuel_usage_text);
            layoutHandler.LEFT_BAR_LAYOUT = (LinearLayout)row.findViewById(R.id.lv_refuel_left_bar_layout);

            row.setTag(layoutHandler);
        }
        else { layoutHandler = (LayoutHandler)row.getTag(); }

        RefuelListDataProvider refuelListDataProvider = (RefuelListDataProvider)this.getItem(position);

        layoutHandler.KM_COUNTER.setText(String.valueOf(refuelListDataProvider.getKm_counter()) + " " + refuelListDataProvider.getDisplayed_dist_units());
        layoutHandler.FUEL_AMOUNT.setText(String.valueOf(refuelListDataProvider.getFuel_amount()) + " " + refuelListDataProvider.getDisplayed_fuel_cap());
        layoutHandler.FUEL_PRICE.setText(String.valueOf(refuelListDataProvider.getFuel_price()) + " " + refuelListDataProvider.getDisplayed_currency()+ "/" + refuelListDataProvider.getDisplayed_fuel_cap());
        layoutHandler.CASH_SPEND.setText(String.valueOf(refuelListDataProvider.getCash_spend()) + " " + refuelListDataProvider.getDisplayed_currency());
        layoutHandler.FUEL_DATE.setText(refuelListDataProvider.getFuel_date());

        if(refuelListDataProvider.getFuel_missed() == 1){
            layoutHandler.FUEL_USAGE.setText("Poprzednie pominięte");
        }
        else {
            if (refuelListDataProvider.getFuel_usage() == 0) {
                layoutHandler.FUEL_USAGE.setText("");
            }
            else {
                layoutHandler.FUEL_USAGE.setText(String.valueOf(refuelListDataProvider.getFuel_usage()) + " " + refuelListDataProvider.getDisplayed_fuel_usage_units());
            }
        }

        if(refuelListDataProvider.getFuel_full().equals("1")){
            layoutHandler.FUEL_FULL.setText(R.string.PL_refuel_list_item_fuel_full);
            layoutHandler.LEFT_BAR_LAYOUT.setBackgroundColor(Color.parseColor("#ff0000"));
        }
        else {
            //layoutHandler.FUEL_FULL.setVisibility(View.GONE);
            layoutHandler.FUEL_FULL.setText(R.string.PL_refuel_list_item_fuel_NOTfull);
            layoutHandler.LEFT_BAR_LAYOUT.setBackgroundColor(Color.parseColor("#3F51B5"));
        }

        if(Integer.valueOf(refuelListDataProvider.getFuel_km_diff()) > 0){
            layoutHandler.FUEL_KM_DIFF.setText("+ " + refuelListDataProvider.getFuel_km_diff() + " " + refuelListDataProvider.getDisplayed_dist_units());
        }
        else{
            layoutHandler.FUEL_KM_DIFF.setText("");
        }

        return row;
    }

}