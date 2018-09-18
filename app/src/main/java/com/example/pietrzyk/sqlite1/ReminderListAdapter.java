package com.example.pietrzyk.sqlite1;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ReminderListAdapter extends ArrayAdapter {

    List list = new ArrayList();

    public ReminderListAdapter(Context context, int resource){
        super(context, resource);
    }

    static class LayoutHandler{
        //layouty widoków z itemu reminder LV Item
        TextView CATEGORY, TITLE, HOUR, DATE, CATEGORY_LETTER;
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

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        LayoutHandler layoutHandler;
        if (row ==null){

            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = layoutInflater.inflate(R.layout.reminder_list_item,parent,false);
            layoutHandler = new LayoutHandler();
            layoutHandler.CATEGORY = (TextView)row.findViewById(R.id.reminder_item_category_tv);
            layoutHandler.DATE = (TextView)row.findViewById(R.id.reminder_item_date_tv);
            layoutHandler.HOUR = (TextView)row.findViewById(R.id.reminder_item_hours_tv);
            layoutHandler.TITLE = (TextView)row.findViewById(R.id.reminder_item_title_tv);
            layoutHandler.CATEGORY_LETTER = (TextView) row.findViewById(R.id.reminder_item_category_letter_tv);

            row.setTag(layoutHandler);
        }
        else { layoutHandler = (ReminderListAdapter.LayoutHandler)row.getTag(); }

        ReminderListDataProvider reminderListDataProvider = (ReminderListDataProvider)this.getItem(position);

        layoutHandler.CATEGORY.setText(reminderListDataProvider.getCategory());
        layoutHandler.DATE.setText(reminderListDataProvider.getDate());
        layoutHandler.HOUR.setText(reminderListDataProvider.getHour());
        layoutHandler.TITLE.setText(reminderListDataProvider.getTitle());

        String string = reminderListDataProvider.getCategory();
        String substring = string.substring(0,1);
        layoutHandler.CATEGORY_LETTER.setText(substring);

        return row;
    }
}
