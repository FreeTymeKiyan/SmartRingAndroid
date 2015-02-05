package me.freetymekiyan.smartring.views;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import me.freetymekiyan.smartring.R;
import me.freetymekiyan.smartring.models.MySqlDbHelper;
import me.freetymekiyan.smartring.models.Pulse;

public class TestDbFragment extends Fragment implements View.OnClickListener {


    private MySqlDbHelper mDbHelper;

    private TextView tvValues;

    public TestDbFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new MySqlDbHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_db, container, false);
        Button btnCreate = (Button) view.findViewById(R.id.btn_create);
        Button btnRead = (Button) view.findViewById(R.id.btn_read);
        Button btnUpdate = (Button) view.findViewById(R.id.btn_update);
        Button btnDelete = (Button) view.findViewById(R.id.btn_delete);
        btnCreate.setOnClickListener(this);
        btnRead.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        tvValues = (TextView) view.findViewById(R.id.tv_values);
        tvValues.setText(mDbHelper.getAllPulses());
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create:
                Random r = new Random();
                int lower = 0;
                int upper = 0;
                int state = r.nextInt(2);
                if (state == 0) {
                    lower = 50;
                    upper = 90;
                } else {
                    lower = 120;
                    upper = 170;
                }
                int rate = lower + r.nextInt(upper - lower + 1);
                mDbHelper.addPulseRate(rate, state);
                break;
            case R.id.btn_read:
                tvValues.setText(mDbHelper.getAllPulses());
                break;
            case R.id.btn_update:
                List<Pulse> list = mDbHelper.getLast7Days();
                final Calendar c = Calendar.getInstance();
                final Calendar c2 = Calendar.getInstance();
                c.clear();
                c.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH), c2.get(Calendar.DAY_OF_MONTH));
                c.add(Calendar.DATE, -6);
                ArrayList<Pulse> added = new ArrayList<>();
                for (int i = 0; i < 7; i++) {
                    Pulse p = new Pulse();
                    p.setDate(c.getTime());
                    if (!list.contains(p)) {
                        p.setValue(0);
                        p.setState(Pulse.State.REST);
                        added.add(p);
                    } else {
                        added.add(list.get(list.indexOf(p)));
                    }
                    c.add(Calendar.DATE, 1);
                }
                for (Pulse p : added) {
                    Log.d("DEBUG", p.getDate().toString() + " : " + p.getValue() + " | " + p.getState());
                }
                break;
            case R.id.btn_delete:

                break;
            default:
                break;
        }
    }
}
