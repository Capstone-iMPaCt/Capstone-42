package com.project.ilearncentral.Fragment.SubSystem;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.ilearncentral.Activity.Enrollees;
import com.project.ilearncentral.Activity.NveCourse;
import com.project.ilearncentral.Adapter.CourseAdapter;
import com.project.ilearncentral.CustomBehavior.ObservableBoolean;
import com.project.ilearncentral.CustomInterface.OnBooleanChangeListener;
import com.project.ilearncentral.Model.Course;
import com.project.ilearncentral.MyClass.Account;
import com.project.ilearncentral.R;

import java.util.ArrayList;
import java.util.List;

public class EnrolmentSystem extends Fragment {

    private CourseAdapter adapter;
    private RecyclerView recyclerView;
    private List<Course> course;
    private List<Course> retrieved;

    private ObservableBoolean show;

    private FloatingActionButton addNewCourseBtn;
    private final int NEW_COURSE = 1, UPDATE_COURSE = 2;

    private SearchView searchView;
    private TextView noCoursesText;
    private ImageButton searchOption;

    public EnrolmentSystem() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subsystem_enrolment, container, false);
        super.onCreate(savedInstanceState);

        if (Account.isType("Student")){
            view.findViewById(R.id.enrolment_app_bar_vertical_line_divider).setVisibility(View.GONE);
            view.findViewById(R.id.enrolment_app_bar_option_button).setVisibility(View.GONE);
        }

        course = new ArrayList<>();
        noCoursesText = view.findViewById(R.id.fragment_courses_none);

        searchOption = view.findViewById(R.id.enrolment_app_bar_option_button);

        addNewCourseBtn = view.findViewById(R.id.enrolment_add_fab);
        addNewCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getContext(), NveCourse.class), NEW_COURSE);
            }
        });

        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.enrolment_pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                if (!searchView.isIconified()) {
//                    searchView.setQuery("", false);
//                    searchView.setIconified(true);
//                }
//                searchView.clearFocus();
//                JobPosts.retrievePostsFromDB(done);
                pullToRefresh.setRefreshing(false);
            }
        });
        show = new ObservableBoolean();
        show.setOnBooleanChangeListener(new OnBooleanChangeListener() {
            @Override
            public void onBooleanChanged(boolean newValue) {
                if (newValue) {
                    course.clear();
                    course.addAll(retrieved);
                    if (course.isEmpty()) {
                        noCoursesText.setVisibility(View.VISIBLE);
                    } else {
                        noCoursesText.setVisibility(View.GONE);
                    }
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    System.out.println("~~~~~~~~~~~Adapter item count after datachanged " + adapter.getItemCount());
                }
            }
        });

        recyclerView = view.findViewById(R.id.enrolment_recylerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CourseAdapter(getContext(), course);
        recyclerView.setAdapter(adapter);
        System.out.println("~~~~~~~~~~~Adapter item count " + adapter.getItemCount());
        retrieveCourses();

        return view;
    }

    private void retrieveCourses() {
        retrieved = Course.getRetrieved();
        if (Account.getType()== Account.Type.Student) {
            retrieved = Course.filterCourses(retrieved, "status", "open");
        } else if (Account.getType()== Account.Type.Educator) {
            retrieved = Course.filterCourses(retrieved, "instructor", Account.getName());
        } else {
            retrieved = Course.getCoursesByCenterId(Account.getCenterId());
        }
        show.set(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        retrieveCourses();
    }
}
