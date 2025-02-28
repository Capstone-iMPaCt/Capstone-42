package com.project.ilearncentral.Fragment;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.ilearncentral.Adapter.LCEducatorAdapter;
import com.project.ilearncentral.Model.Educator;
import com.project.ilearncentral.MyClass.Account;
import com.project.ilearncentral.MyClass.Utility;
import com.project.ilearncentral.R;

import java.util.List;

public class LCEducators extends Fragment {

    private List<Educator> educators;
    private RecyclerView recyclerView;
    private LCEducatorAdapter adapter;

    private View view;
    private Dialog dialog;

    private RadioGroup status, employmentType;
    private RadioButton active, inActive, fullTime, partTime, contractual;
    private ImageButton educatorsViewOption;
    private Button ok, clear;

    public LCEducators() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_educators, container, false);
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.fragment_dialog_lc_educators_search_option);
        Window window = dialog.getWindow();
        window.setLayout(Utility.dpToPx(getContext(), 300), LinearLayout.LayoutParams.WRAP_CONTENT);

        bindLayout();

        educators = Educator.getEducatorsByEmployment(true, Account.getCenterId());

        int spanCount = Utility.getScreenWidth() / Utility.dpToPx(getContext(), 150);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), spanCount);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new LCEducatorAdapter(getContext(), educators);
        recyclerView.setAdapter(adapter);

        educatorsViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setCancelable(true);
                dialog.show();
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String msg = "";
                        if (status.getCheckedRadioButtonId() == active.getId()) {
                            msg += "ACTIVE: ";
                        } else if (status.getCheckedRadioButtonId() == inActive.getId()) {
                            msg += "INACTIVE: ";
                        }

                        if (employmentType.getCheckedRadioButtonId() == fullTime.getId()) {
                            msg += "Full Time";
                        } else if (employmentType.getCheckedRadioButtonId() == partTime.getId()) {
                            msg += "Part Time";
                        } else if (employmentType.getCheckedRadioButtonId() == contractual.getId()) {
                            msg += "Contractual";
                        }
                        educators.clear();
                        educators.addAll(Educator.getFilteredEducators(Account.getCenterId(), status.getCheckedRadioButtonId(), employmentType.getCheckedRadioButtonId()));
                        adapter.notifyDataSetChanged();
                        Toast toast = Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0,0);
                        toast.show();
                        dialog.dismiss();
                    }
                });
                clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        status.clearCheck();
                        employmentType.clearCheck();
                        Toast toast = Toast.makeText(getContext(), "Cleared filters", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0,0);
                        toast.show();
                        dialog.dismiss();
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            int spanCount = Utility.getScreenHeight() / Utility.dpToPx(getContext(), 150);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), spanCount);
            recyclerView.setLayoutManager(layoutManager);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            int spanCount = Utility.getScreenWidth() / Utility.dpToPx(getContext(), 150);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), spanCount);
            recyclerView.setLayoutManager(layoutManager);
        }
    }

    private void bindLayout() {
        recyclerView = view.findViewById(R.id.lc_educators_recylerview);
        educatorsViewOption = view.findViewById(R.id.lc_educators_app_bar_option_button);
        status = dialog.findViewById(R.id.lc_educators_search_option_status_group);
        employmentType = dialog.findViewById(R.id.lc_educators_search_option_employment_type_group);
        active = dialog.findViewById(R.id.lc_educators_search_option_active);
        inActive = dialog.findViewById(R.id.lc_educators_search_option_inactive);
        fullTime = dialog.findViewById(R.id.lc_educators_search_option_fullTime);
        partTime = dialog.findViewById(R.id.lc_educators_search_option_partTime);
        contractual = dialog.findViewById(R.id.lc_educators_search_option_contractual);
        ok = dialog.findViewById(R.id.lc_educators_search_option_ok);
        clear = dialog.findViewById(R.id.lc_educators_search_option_clear);
    }
}