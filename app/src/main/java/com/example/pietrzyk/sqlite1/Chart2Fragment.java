package com.example.pietrzyk.sqlite1;

//import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class Chart2Fragment extends Fragment{

    Context context = getActivity();
    DBHelper dbHelper;
    BarChart barChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chart2_fragment, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        barChart = (BarChart) getActivity().findViewById(R.id.chart2);

        // PRZERZUCAM TUTAJ WARTOSC Z ACTIVITY
        context = getActivity();
        int car_id = ((ChartsAcivity)context).selected_car_id;

        setBarChartData2(String.valueOf(car_id));
    }

    public void setBarChartData2(String car_id){

        dbHelper = new DBHelper(context);

        Cursor curChartMonthlyFuelCost = dbHelper.getChartMonthlyFuelCost(car_id);
        curChartMonthlyFuelCost.moveToFirst();

        int count = curChartMonthlyFuelCost.getCount();
        final String[] x_data = new String[count];
        Float[] y_cost = new Float[count];

        barChart.setNoDataText("Brak danych dla tego wykresu. Dodaj więcej wpisów.");

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
            barChart.setData(barData);


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
            barChart.setDescription(description);


            barChart.getXAxis().setValueFormatter(formatter);
            barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            barChart.getXAxis().setDrawGridLines(false);

            //podpisy pod osia X dokladnie pod kazdym wpisem z osi Y
            barChart.getXAxis().setGranularity(1);

            // usuwa puste miejsce pomiedzy poczatkiem osi y a pierwsza wartoscia
            barChart.getAxisLeft().setAxisMinimum(0f);
            barChart.getAxisRight().setAxisMinimum(0f);
            barChart.setFitBars(true);
            barChart.invalidate();


        }else Toast.makeText(context, "Brak Danych dla wskazanego pojazdu. Najdpierw dodaj pełne tankowania.", Toast.LENGTH_LONG).show();

    }

}
