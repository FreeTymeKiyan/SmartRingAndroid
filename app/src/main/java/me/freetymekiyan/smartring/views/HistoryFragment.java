package me.freetymekiyan.smartring.views;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.LimitLine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import me.freetymekiyan.smartring.R;
import me.freetymekiyan.smartring.utils.Utils;
import me.freetymekiyan.smartring.models.MySqlDbHelper;
import me.freetymekiyan.smartring.models.Pulse;

public class HistoryFragment extends Fragment implements OnChartValueSelectedListener {

    private static final String X_VAL_FORMAT = "MM/dd";

    private BarChart mChart;

    private MySqlDbHelper db;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(String title) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(MainActivity.KEY_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new MySqlDbHelper(getActivity());
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
        mChart.animateXY(1500, 1500);
        return view;
    }

    /**
     * Read last 7 days data from database
     */
    private void generateDataSet() {
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<BarEntry> rest = new ArrayList<BarEntry>();
        ArrayList<BarEntry> active = new ArrayList<BarEntry>();

        final Calendar c = Calendar.getInstance();
        final Calendar c2 = Calendar.getInstance();
        c.clear();
        c.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH), c2.get(Calendar.DAY_OF_MONTH));
        c.add(Calendar.DATE, -6); // 7 days ago
        final DateFormat format = new SimpleDateFormat(X_VAL_FORMAT, Locale.ENGLISH);

        final List<Pulse> rawRest = db.getLast7DaysRest();
        final List<Pulse> rawActive = db.getLast7DaysActive();
        for (int i = 0; i < 7; i++) {
            xVals.add(format.format(c.getTime()));

            Pulse p = new Pulse();
            p.setDate(c.getTime());
            if (rawRest.contains(p)) {
                rest.add(new BarEntry(rawRest.get(rawRest.indexOf(p)).getValue(), i));
            } else {
                rest.add(new BarEntry(0, i));
            }
            if (rawActive.contains(p)) {
                active.add(new BarEntry(rawActive.get(rawActive.indexOf(p)).getValue(), i));
            } else {
                active.add(new BarEntry(0, i)); // insert 0 if no measurement for that day
            }

            c.add(Calendar.DATE, 1);
        }

        BarDataSet set1 = new BarDataSet(rest, getString(R.string.rest));
        set1.setColor(getResources().getColor(R.color.BlueBkg));
        BarDataSet set2 = new BarDataSet(active, getString(R.string.active));
        set2.setColor(getResources().getColor(R.color.AccentColor));

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);
        dataSets.add(set2);

        BarData data = new BarData(xVals, dataSets);
        data.setGroupSpace(110f);

        LimitLine lowerLimit = new LimitLine(60f);
        lowerLimit.setDrawValue(false);
        lowerLimit.setLineColor(getResources().getColor(R.color.PrimaryColor));
        lowerLimit.setLineWidth(0.5f);
        lowerLimit.enableDashedLine(12f, 2f, 0f);
        data.addLimitLine(lowerLimit);

        LimitLine lowerLimit2 = new LimitLine(90f);
        lowerLimit2.setDrawValue(false);
        lowerLimit2.setLineColor(getResources().getColor(R.color.PrimaryColor));
        lowerLimit2.setLineWidth(0.5f);
        lowerLimit2.enableDashedLine(12f, 2f, 0f);
        data.addLimitLine(lowerLimit2);

        final float v = 0.9f * Utils.getUpperLimitValue(getActivity()); // 0.9 * mxr
        if (v != 0) {
            LimitLine upperLimit = new LimitLine(v);
            upperLimit.setLineColor(getResources().getColor(R.color.PrimaryColor));
            upperLimit.setLineWidth(0.5f);
            upperLimit.enableDashedLine(12f, 2f, 0f);
            upperLimit.setLabelPosition(LimitLine.LimitLabelPosition.LEFT);
            data.addLimitLine(upperLimit);
        }
        mChart.setData(data);
    }



    /**
     * Generate test data for the graph
     */
    private void generateTestDataSet() {
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
