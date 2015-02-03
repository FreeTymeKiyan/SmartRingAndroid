package me.freetymekiyan.smartring.views;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Random;

import me.freetymekiyan.smartring.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment implements OnChartValueSelectedListener {

    private static HistoryFragment instance;

    private BarChart mChart;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment getInstance() {
        if (instance == null) {
            instance = new HistoryFragment();
        }
        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        mChart = (BarChart) view.findViewById(R.id.chart_history);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawLegend(true);
        mChart.setDescription("");
        mChart.setDrawYValues(false);
        mChart.setPinchZoom(true);
        mChart.setDrawBarShadow(false);
        mChart.setDrawGridBackground(false);
        mChart.setDrawHorizontalGrid(false);
        mChart.setDrawYValues(true);
        generateDataSet();
        mChart.animateXY(3000, 3000);
        return view;
    }

    private void generateDataSet() {
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < 7; i++) {
            xVals.add(i + "");
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
        Random r = new Random();
        for (int i = 0; i < 7; i++) {
            yVals1.add(new BarEntry(50 + r.nextInt(41), i));
            yVals2.add(new BarEntry(120 + r.nextInt(41), i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "Static");
        set1.setColor(getResources().getColor(R.color.BlueBkg));
        BarDataSet set2 = new BarDataSet(yVals2, "Workout");
        set2.setColor(getResources().getColor(R.color.AccentColor));

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);
        dataSets.add(set2);

        BarData data = new BarData(xVals, dataSets);
        data.setGroupSpace(110f);
        mChart.setData(data);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex) {

    }

    @Override
    public void onNothingSelected() {

    }
}
