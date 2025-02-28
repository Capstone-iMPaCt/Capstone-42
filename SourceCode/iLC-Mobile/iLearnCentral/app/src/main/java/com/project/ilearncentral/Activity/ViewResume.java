package com.project.ilearncentral.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.project.ilearncentral.Adapter.MainAdapter;
import com.project.ilearncentral.CustomBehavior.CustomAppBarLayoutBehavior;
import com.project.ilearncentral.Fragment.ViewResumeFragment;
import com.project.ilearncentral.Model.Educator;
import com.project.ilearncentral.Model.JobApplication;
import com.project.ilearncentral.MyClass.Account;
import com.project.ilearncentral.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewResume extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "VIEW RESUME";

    private Toolbar toolbar;
    private CircleImageView userImage;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Button messageButton, hireButton, rejectButton;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout toolbarLayout;
    private CoordinatorLayout.LayoutParams clLayoutParams;
    private TextView usernameDisplay, fieldDisplay;
    private Educator edu;
    private JobApplication applicant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_resume);

        toolbar = (Toolbar) findViewById(R.id.view_resume_toolbar);
        userImage = (CircleImageView) findViewById(R.id.view_resume_image);
        hireButton = (Button) findViewById(R.id.view_resume_hire_button);
        rejectButton = (Button) findViewById(R.id.view_resume_reject_button);
        messageButton = (Button) findViewById(R.id.view_resume_message_button);
        appBarLayout = (AppBarLayout) findViewById(R.id.view_resume_app_bar);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.view_resume_toolbar_layout);
        clLayoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        ((CustomAppBarLayoutBehavior) clLayoutParams.getBehavior()).setScrollBehavior(true);
        usernameDisplay = findViewById(R.id.view_resume_full_name);
        fieldDisplay = findViewById(R.id.view_resume_expertise);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        toolbar.setTitle("");
//        toolbar.setSubtitle("");

        hireButton.setOnClickListener(this);
        rejectButton.setOnClickListener(this);
        messageButton.setOnClickListener(this);
        userImage.setOnClickListener(this);

        viewPager = (ViewPager) findViewById(R.id.view_resume_htab_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.view_resume_sliding_tabs);
//        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        Intent i = getIntent();
        if (i.hasExtra("educator")) {
            edu = Educator.getEduByUsername(i.getStringExtra("educator"));
            getImage(userImage, edu.getUsername());
        }
        if (i.hasExtra("jobApplication")) {
            applicant = JobApplication.getJAById(i.getStringExtra("jobApplication"));
        } else {
            hireButton.setVisibility(View.GONE);
            rejectButton.setVisibility(View.GONE);
        }

        generateTabs();
        setDetails();
    }

    private void setDetails() {
        usernameDisplay.setText(edu.getFullname());
        fieldDisplay.setText(Account.Type.Educator.toString());
        try {
            setStatus(applicant.getApplicationStatus());
        } catch (Exception e) {
        }
    }

    private void generateTabs() {
        MainAdapter adapter = new MainAdapter(getSupportFragmentManager());
        adapter.addFragment(new ViewResumeFragment(edu), "");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.getTabAt(0).setIcon(R.drawable.bell_icon);
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL|TabLayout.GRAVITY_CENTER);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        appBarLayout.setExpanded(true);
                        ((CustomAppBarLayoutBehavior) clLayoutParams.getBehavior()).setScrollBehavior(true);
                        userImage.setVisibility(View.VISIBLE);
                        break;
                    default:
                        appBarLayout.setExpanded(false);
                        ((CustomAppBarLayoutBehavior) clLayoutParams.getBehavior()).setScrollBehavior(false);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        DialogInterface.OnClickListener dialogClickListener;
        android.app.AlertDialog.Builder builder;
        switch (view.getId()) {
            case R.id.view_resume_hire_button:
                dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if (!applicant.getApplicationStatus().equalsIgnoreCase(JobApplication.HIRED)) {
                                    applicant.setApplicationStatus(JobApplication.HIRED);
                                    FirebaseFirestore.getInstance().collection("JobApplication").document(applicant.getJobApplicationId())
                                            .update("ApplicationStatus", JobApplication.HIRED);
                                    setStatus(JobApplication.HIRED);
                                    Educator.hireEducator(edu, Account.getCenterId(), applicant.getJobVacancy().getJobTypes(), applicant.getJobVacancy().getPosition());
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure in hiring " + edu.getFullname() + "?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                break;
            case R.id.view_resume_reject_button:
                dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if (!applicant.getApplicationStatus().equalsIgnoreCase(JobApplication.REJECTED)) {
                                    applicant.setApplicationStatus(JobApplication.REJECTED);
                                    FirebaseFirestore.getInstance().collection("JobApplication").document(applicant.getJobApplicationId())
                                            .update("ApplicationStatus", JobApplication.REJECTED);
                                    setStatus(JobApplication.REJECTED);
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure in rejecting " + edu.getFullname() + "?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                break;
            case R.id.view_resume_message_button:
//                startActivity(new Intent(getApplicationContext(), Chat.class));
                Intent intent = new Intent(getApplicationContext(), Messages.class);
                intent.putExtra("USER_NAME", edu.getUsername());
                intent.putExtra("FULL_NAME", edu.getFullname());
                startActivity(intent);
                break;
        }
    }

    private void setStatus(String status) {
        if (status.equalsIgnoreCase(JobApplication.HIRED)) {
            hireButton.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            hireButton.setEnabled(false);
            hireButton.setVisibility(View.VISIBLE);
            hireButton.setText(JobApplication.HIRED.toUpperCase());
            rejectButton.setEnabled(false);
            rejectButton.setVisibility(View.INVISIBLE);
        } else if (status.equalsIgnoreCase(JobApplication.REJECTED)) {
            rejectButton.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
            rejectButton.setEnabled(false);
            rejectButton.setText(JobApplication.REJECTED.toUpperCase());
            rejectButton.setVisibility(View.VISIBLE);
            hireButton.setEnabled(false);
            hireButton.setVisibility(View.INVISIBLE);
        }
    }

    private void getImage(final ImageView imageView, String filename) {
        FirebaseStorage.getInstance().getReference().child("images").child(filename).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext()).load(uri.toString()).fitCenter().into(imageView);
//                Picasso.get().load(uri.toString()).centerCrop().fit().into(imageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageView.setImageDrawable(getApplicationContext().getDrawable(R.drawable.user));
            }
        });
    }
}
