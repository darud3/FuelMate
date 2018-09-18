package com.example.pietrzyk.sqlite1;

//import android.app.Fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class Chart3Fragment extends Fragment{

    Context context = getActivity();
    DBHelper dbHelper;
    LineChart lineChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.chart3_fragment, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        lineChart = (LineChart) getActivity().findViewById(R.id.chart3);

        // PRZERZUCAM TUTAJ WARTOSC Z ACTIVITY
        context = getActivity();
        int car_id = ((ChartsAcivity)context).selected_car_id;

        setDataLineChart3(String.valueOf(car_id));

        //Toast.makeText(getActivity(), "car_id = " + car_id, Toast.LENGTH_SHORT).show();

    }

    public void setDataLineChart3(String car_id){

        dbHelper = new DBHelper(getActivity());

        Cursor curChartFuelPrice = dbHelper.getChartFuelPrice(car_id);
        curChartFuelPrice.moveToFirst();

        int count = curChartFuelPrice.getCount();
        final String[] x_data = new String[count];
        Float[] y_price = new Float[count];
        float min_y = 0, max_y = 0, tmp_max_min=0;

        lineChart.setNoDataText("Brak danych dla tego wykresu. Dodaj więcej tankowań.");

        if(count >= 2) {
            if (curChartFuelPrice.moveToFirst()) {

                tmp_max_min = curChartFuelPrice.getFloat(1);

                max_y = tmp_max_min;
                min_y = tmp_max_min;

                for (int i = 0; i < count; i++) {
                    x_data[i] = curChartFuelPrice.getString(0);
                    y_price[i] = curChartFuelPrice.getFloat(1);

                    if (y_price[i] > max_y) max_y = y_price[i];
                    if (y_price[i] < min_y) min_y = y_price[i];

                    curChartFuelPrice.moveToNext();
                }

                ArrayList<Entry> values = new ArrayList<Entry>();

                for (int i = 0; i < count; i++) {
                    values.add(new Entry(i, y_price[i]));
                }

                LineDataSet set1 = new LineDataSet(values, "Cena paliwa");
                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                set1.setColors(new int[]{R.color.chartRed}, getActivity());
                set1.setDrawCircles(false);

                ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                dataSets.add(set1);

                LineData lineData = new LineData(dataSets);
                //lineData.setDrawValues(false); // wyświetlanie wartości
                lineData.setHighlightEnabled(true);

                lineChart.setData(lineData);
                //lineChart.setMaxVisibleValueCount(3);

                // podpis
                Description description = new Description();
                description.setTextColor(ColorTemplate.VORDIPLOM_COLORS[2]);
                description.setText("");
                lineChart.setDescription(description);

                MyMakerView mv = new MyMakerView(context, R.layout.chart_highlight);
                mv.setChartView(lineChart);
                lineChart.setMarker(mv);
                lineChart.notifyDataSetChanged();

                // X-Axis
                IAxisValueFormatter formatter = new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        //return x_refuel_data[(int)value];
                        return x_data[(int) value];
                    }

                    @Override
                    public int getDecimalDigits() {
                        return 0;
                    }
                };
                lineChart.getXAxis().setValueFormatter(formatter);
                lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

                //podpisy pod osia X dokladnie pod kazdym wpisem z osi Y
                lineChart.getXAxis().setGranularity(1);
                //lineChart.getXAxis().setGranularityEnabled(true);

                //Y-Axis
                // usuwa puste miejsce pomiedzy poczatkiem osi y a pierwsza wartoscia
                //lineChart.getAxisLeft().setAxisMinimum(0f);
                //lineChart.getAxisRight().setAxisMinimum(0f);

                lineChart.getAxisLeft().setAxisMinimum(min_y);
                lineChart.getAxisRight().setAxisMinimum(max_y+1);

                // linia średniej ceny
                Cursor curAvgFuelPrice = dbHelper.getAvgFuelPrice(car_id);
                curAvgFuelPrice.moveToFirst();

                if (curAvgFuelPrice.moveToFirst()) {

                    YAxis leftAxis = lineChart.getAxisLeft();
                    LimitLine ll = new LimitLine(curAvgFuelPrice.getFloat(0), "Śr. cena");
                    ll.setLineColor(Color.BLUE);
                    ll.setLineWidth(0.3f);
                    ll.setTextColor(Color.BLACK);
                    ll.setTextSize(6f);
                    leftAxis.addLimitLine(ll);
                }

                lineChart.invalidate();

            } else Toast.makeText(context, "Brak Danych dla wskazanego pojazdu. Najdpierw dodaj pełne tankowania.", Toast.LENGTH_LONG).show();
        }
    }
}






