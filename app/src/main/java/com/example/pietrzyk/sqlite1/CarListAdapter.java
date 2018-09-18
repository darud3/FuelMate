package com.example.pietrzyk.sqlite1;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CarListAdapter extends ArrayAdapter {

    List list = new ArrayList();

    public CarListAdapter(Context context, int resource) {
        super(context, resource);
    }

    static class LayoutHandler{
        //layouty widoków z itemu car LV Item
        TextView CAR_NAME, CAR_FUEL_TYPE, CAR_DIST_UNITS, CAR_FUEL_USAGE_UNITS, CAR_REG_NUM;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        View row = convertView;
        LayoutHandler layoutHandler;
        if (row == null) {
            //Nie mam pojecia co to robi....
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = layoutInflater.inflate(R.layout.car_list_item,parent,false);
            layoutHandler = new LayoutHandler();
            layoutHandler.CAR_NAME = (TextView)row.findViewById(R.id.Car_Name_Text);
            layoutHandler.CAR_FUEL_TYPE = (TextView)row.findViewById(R.id.Car_Fuel_Type_Text);
            //layoutHandler.CAR_DIST_UNITS = (TextView)row.findViewById(R.id.Car_Dist_Units);
            //layoutHandler.CAR_FUEL_USAGE_UNITS = (TextView)row.findViewById(R.id.Car_Fuel_Usage_Units);
            layoutHandler.CAR_REG_NUM = (TextView) row.findViewById(R.id.Car_Reg_Num_Text);

            row.setTag(layoutHandler);
        }
        else { layoutHandler = (LayoutHandler)row.getTag(); }

        CarListDataProvider carListDataProvider = (CarListDataProvider)this.getItem(position);

        layoutHandler.CAR_NAME.setText(carListDataProvider.getCar_name());
        layoutHandler.CAR_REG_NUM.setText(carListDataProvider.getCar_reg_num());
        layoutHandler.CAR_FUEL_TYPE.setText("Paliwo: " + carListDataProvider.getCar_fuel_type() + "");
        //layoutHandler.CAR_DIST_UNITS.setText(carListDataProvider.getCar_dist_units());
        //layoutHandler.CAR_FUEL_USAGE_UNITS.setText(carListDataProvider.getCar_fuel_usage_units());

        return row;
    }


}

