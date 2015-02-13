package me.freetymekiyan.smartring.views;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import me.freetymekiyan.smartring.R;

public class ReportFragment extends Fragment {

    public static ReportFragment newInstance(String title) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putString(MainActivity.KEY_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    public ReportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        final TableLayout tlReport = (TableLayout) view.findViewById(R.id.tl_report);

//        TableRow tr = new TableRow(getActivity());
//        tlReport.addView(tr);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
