package me.freetymekiyan.smartring.views;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import me.freetymekiyan.smartring.R;
import me.freetymekiyan.smartring.utils.Utils;
import me.freetymekiyan.smartring.controllers.PageChangedEvent;

public class ReportFragment extends Fragment {

    public ReportFragment() {
        // Required empty public constructor
    }

    public static ReportFragment newInstance(String title) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putString(MainActivity.KEY_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final TextView tvUsername = (TextView) view.findViewById(R.id.tv_username);
        String username = sp.getString(getString(R.string.key_name), "");
        tvUsername.setText(username.isEmpty() ? getString(R.string.ph_name) : username);
        final String age = sp.getString(getString(R.string.key_age), "");
        final Button btnGenerate = (Button) view.findViewById(R.id.btn_generate);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open settings page
                EventBus.getDefault().post(new PageChangedEvent(MainActivity.SETTINGS_FRAGMENT));
            }
        });
        if (!sp.contains(getString(R.string.key_gender)) || age.isEmpty()) {
            return view;
        }

        final boolean gender = sp.getBoolean(getString(R.string.key_gender), false);
        float mxr = Utils.getUpperLimitValue(getActivity());
        final TableLayout tlReport = (TableLayout) view.findViewById(R.id.tl_report);
        for (int i = 1; i < tlReport.getChildCount(); i++) {
            final TableRow row = (TableRow) tlReport.getChildAt(i);
            final TextView figure = (TextView) row.getChildAt(2);
            figure.setText(getFigureWithPosition(i, mxr));
        }

        btnGenerate.setVisibility(View.GONE);
        return view;
    }

    private String getFigureWithPosition(int i, float mxr) {
        i--;
        int lowerLimit = (int) ((0.5 + 0.1 * i) * mxr);
        int upperLimit = lowerLimit + (int) (0.1 * mxr);
        return lowerLimit + "-" + upperLimit;
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
