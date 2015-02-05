package me.freetymekiyan.smartring.views;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

import me.freetymekiyan.smartring.R;
import me.freetymekiyan.smartring.models.MySqlDbHelper;

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
                break;
            case R.id.btn_delete:
                break;
            default:
                break;
        }
    }
}
