package com.example.pietrzyk.sqlite1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class ChartBarActivity extends AppCompatActivity {

    Context context = this;
    int selected_car_id = 0;
    DBHelper dbHelper;
    BarChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_bar_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.chart_bar_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Wykres Bar");

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            //getSupportActionBar().setDisplayShowTitleEnabled(false); // usuwa tytuł activity z toolbara
        }

        chart = (BarChart) findViewById(R.id.bar_chart);
        chart.setDoubleTapToZoomEnabled(false);


    }

    public void serDataBarChart(String car_id){

        dbHelper = new DBHelper(context);

        Cursor curChartMonthlyFuelCost = dbHelper.getChartMonthlyFuelCost(car_id);
        curChartMonthlyFuelCost.moveToFirst();

        int count = curChartMonthlyFuelCost.getCount();
        final String[] x_data = new String[count];
        Float[] y_cost = new Float[count];

        if (curChartMonthlyFuelCost.moveToFirst()){

            for (int i = 0 ; i < count ; i++){

                x_data[i] = curChartMonthlyFuelCost.getString(0);
                y_cost[i] = curChartMonthlyFuelCost.getFloat(1);
                curChartMonthlyFuelCost.moveToNext();
            }

            List<BarEntry> values = new ArrayList<>();

            for (int i = 0 ; i < count ; i++){
                values.add(new BarEntry(i, y_cost[i]));
            }

            BarDataSet barDataSet = new BarDataSet(values, "Miesięczne koszty");

            barDataSet.setColors(new int[]{R.color.chartRed}, context);


            BarData barData = new BarData(barDataSet);
            barData.setBarWidth(0.3f);
            chart.setData(barData);


            IAxisValueFormatter formatter = new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return x_data[(int)value];
                }

                @Override
                public int getDecimalDigits() {
                    return 0;
                }
            };

            /* USUWA linie Y ------
            YAxis yAxis = lineChart.getAxisLeft();
            yAxis.setDrawGridLines(false);

            yAxis = lineChart.getAxisRight();
            yAxis.setDrawGridLines(false);
            */
            Description description = new Description();
            description.setTextColor(ColorTemplate.VORDIPLOM_COLORS[2]);
            description.setText("");
            chart.setDescription(description);


            chart.getXAxis().setValueFormatter(formatter);
            chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            chart.getXAxis().setDrawGridLines(false);

            //podpisy pod osia X dokladnie pod kazdym wpisem z osi Y
            chart.getXAxis().setGranularity(1);

            // usuwa puste miejsce pomiedzy poczatkiem osi y a pierwsza wartoscia
            chart.getAxisLeft().setAxisMinimum(0f);
            chart.getAxisRight().setAxisMinimum(0f);
            chart.setFitBars(true);
            chart.invalidate();


        }else Toast.makeText(context, "Brak Danych dla wskazanego pojazdu. Najdpierw dodaj pełne tankowania.", Toast.LENGTH_LONG).show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chart_activity, menu);

        MenuItem carSpinner = menu.findItem(R.id.chart_activ_car_spinner);
        Spinner CarRefuelListSpinner = (Spinner) MenuItemCompat.getActionView(carSpinner);

        SQLiteDatabase db = new DBHelper(this).getWritableDatabase();

        //Inicializacja spinnera z bazy danych
        Cursor spinner_cursor = db.rawQuery("SELECT " + TableCar.NewCar.CAR_ID + " AS _id, " + TableCar.NewCar.CAR_NAME + " FROM " + TableCar.NewCar.TABLE_NAME, null);

        if(spinner_cursor.moveToFirst()) {

            String[] from = new String[]{TableCar.NewCar.CAR_NAME};
            int[] to = new int[]{android.R.id.text1};

            SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, spinner_cursor, from, to);
            simpleCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            CarRefuelListSpinner.setAdapter(simpleCursorAdapter);

            CarRefuelListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                    selected_car_id = Integer.valueOf(String.valueOf(id));

                    serDataBarChart(String.valueOf(selected_car_id));
                }

                public void onNothingSelected(AdapterView<?> parent) {
                    //cos tam
                }
            });
        }
        else {
            Log.e("SPINNER NEW CAR", "Kursor jest pusty");
            Intent myIntent = new Intent(ChartBarActivity.this, CarListActivity.class);
            startActivity(myIntent);
            Toast.makeText(context, "Nie masz jeszcze żadnego Pojazdu. Przed dodaniem tankowania musisz dodać pierwszy Pojazd!", Toast.LENGTH_LONG).show();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //return super.onOptionsItemSelected(item);

        //tutaj deklarujemy działanie guzika back
        switch (item.getItemId()) {
            case android.R.id.home:
                // this takes the user 'back', as if they pressed the left-facing triangle icon on the main android toolbar.
                // if this doesn't work as desired, another possibility is to call `finish()` here.
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }






}
