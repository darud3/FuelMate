package com.example.pietrzyk.sqlite1;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CostListAdapter extends ArrayAdapter {

    List list = new ArrayList();

    public CostListAdapter(Context context, int resource) {
        super(context, resource);
    }

    static class LayoutHandler{

        //tu umieszczamy vidoki z naszego layoutu pojedynczego itemu z list view
        TextView COST_NOTE, COST_NAME, COST_TYPE, CASH_SPEND, COST_DATE, COST_TYPE_LETTER;
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

        View row = convertView;
        LayoutHandler layoutHandler;
        if (row == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = layoutInflater.inflate(R.layout.cost_list_item, parent, false);
            layoutHandler = new LayoutHandler();
            //layoutHandler.COST_NOTE //uzupełnic jesli note ma być bezposrednio na itemie
            layoutHandler.COST_NAME = (TextView) row.findViewById(R.id.lv_cost_name);
            layoutHandler.COST_TYPE = (TextView) row.findViewById(R.id.lv_cost_type);
            layoutHandler.CASH_SPEND = (TextView) row.findViewById(R.id.lv_cost_cash_spend);
            layoutHandler.COST_DATE = (TextView) row.findViewById(R.id.lv_cost_date);
            layoutHandler.COST_TYPE_LETTER = (TextView) row.findViewById(R.id.lv_cost_type_letter);

            row.setTag(layoutHandler);
        }
        else { layoutHandler = (LayoutHandler)row.getTag(); }

        CostListDataProvider costListDataProvider = (CostListDataProvider)this.getItem(position);

        layoutHandler.COST_NAME.setText(String.valueOf(costListDataProvider.getCost_name()));
        layoutHandler.COST_TYPE.setText(String.valueOf(costListDataProvider.getCost_type()));
        layoutHandler.CASH_SPEND.setText(String.valueOf(costListDataProvider.getCost_cash_spend()) + " " + costListDataProvider.getDisplayed_currency());
        layoutHandler.COST_DATE.setText(costListDataProvider.getCost_date());

        String string = costListDataProvider.getCost_type();
        String substring = string.substring(0,1);
        layoutHandler.COST_TYPE_LETTER.setText(substring);

        return row;
    }
}
