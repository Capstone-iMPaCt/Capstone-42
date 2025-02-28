package com.project.ilearncentral.Activity;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.project.ilearncentral.Activity.SignUp.CreateUser;
import com.project.ilearncentral.Activity.Update.UpdateAccount;
import com.project.ilearncentral.Activity.Update.UpdateLearningCenter;
import com.project.ilearncentral.Activity.Update.UpdateProfile;
import com.project.ilearncentral.Adapter.MainAdapter;
import com.project.ilearncentral.Adapter.NotificationAdapter;
import com.project.ilearncentral.CustomBehavior.CustomAppBarLayoutBehavior;
import com.project.ilearncentral.CustomBehavior.ObservableBoolean;
import com.project.ilearncentral.CustomInterface.OnBooleanChangeListener;
import com.project.ilearncentral.Fragment.Feed;
import com.project.ilearncentral.Fragment.JobPost;
import com.project.ilearncentral.Fragment.LCEducators;
import com.project.ilearncentral.Fragment.Profile.EducatorProfile;
import com.project.ilearncentral.Fragment.Profile.LearningCenterProfile;
import com.project.ilearncentral.Fragment.Profile.StudentProfile;
import com.project.ilearncentral.Fragment.SubSystem.EnrolmentSystem;
import com.project.ilearncentral.Fragment.SubSystem.SchedulingSystem;
import com.project.ilearncentral.Model.LearningCenter;
import com.project.ilearncentral.Model.Notification;
import com.project.ilearncentral.MyClass.Account;
import com.project.ilearncentral.MyClass.Connection;
import com.project.ilearncentral.MyClass.Resume;
import com.project.ilearncentral.MyClass.Utility;
import com.project.ilearncentral.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

//import com.project.ilearncentral.Fragment.UserActivitySchedules;

public class Main extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "MAIN";
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    public static final String CHANNEL_ID = "ILearnCentral_Notif_Chanel";
    public int notificationCount;
    TextView notifBadge;
    private FirebaseUser user;
    private boolean tabGenerate, exit, notificationRetrieved;
    private LearningCenter lc;
    private List<Notification> notifications;
    private NotificationAdapter notifAdapter;
    private RecyclerView notificationRecycler;
    private Context mainContext;

    private Toolbar toolbar;
    private CircleImageView userImage;
    private ImageView lcLogo;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Button featuresButton, findUserButton, findCenterButton, notificationButton, messageButton;
    private AppBarLayout appBarLayout;
    private RelativeLayout loadingPage;
    private CollapsingToolbarLayout toolbarLayout;
    private CoordinatorLayout.LayoutParams clLayoutParams;
    private TextView usernameDisplay, fieldDisplay;
    private Dialog notifDialog;
    private final int UPDATE_PROFILE = 11, UPDATE_ACCOUNT = 12, UPDATE_CENTER = 13, CREATE_USER = 14, UPDATE_RESUME = 15;

    @Override
    public void onBackPressed() {
//        if (!exit) {
//            Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_SHORT).show();
//            exit = true;
//        } else
//        super.onBackPressed();
        Utility.setExit(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true).setTitle("Exit").setMessage("Do you want to exit iLearnCentral?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utility.setExit(true);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Utility.isExit())
            super.onBackPressed();

        user = FirebaseAuth.getInstance().getCurrentUser();
        tabGenerate = true;
        exit = false;
        notifications = new ArrayList<>();
        notificationCount = 0;
        notificationRetrieved = false;
        mainContext = this;

        notifBadge = findViewById(R.id.notification_button_count);
        toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        userImage = (CircleImageView) findViewById(R.id.view_user_image);
        lcLogo = (ImageView) findViewById(R.id.user_learning_center_logo);
        featuresButton = (Button) findViewById(R.id.main_subscription_button);
        findUserButton = (Button) findViewById(R.id.main_find_user_button);
        findCenterButton = (Button) findViewById(R.id.main_center_list_button);
        notificationButton = (Button) findViewById(R.id.notification_button);
        messageButton = (Button) findViewById(R.id.message_button);
        appBarLayout = (AppBarLayout) findViewById(R.id.home_app_bar);
        loadingPage = findViewById(R.id.user_loading_panel);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        clLayoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        ((CustomAppBarLayoutBehavior) clLayoutParams.getBehavior()).setScrollBehavior(true);
        usernameDisplay = findViewById(R.id.user_full_name);
        fieldDisplay = findViewById(R.id.user_expertise);

        notifDialog = new Dialog(this);
        notifDialog.setContentView(R.layout.dialog_notification_list);
        notifDialog.setCancelable(true);
        notificationRecycler = notifDialog.findViewById(R.id.notification_recycler);


        setSupportActionBar(toolbar);

//        if (Account.isType("learningcenter")){
//            userImage.getLayoutParams().width = Utility.getScreenWidth();
//            userImage.getLayoutParams().height = Utility.dpToPx(this,256);
//        }

        featuresButton.setOnClickListener(this);
        findUserButton.setOnClickListener(this);
        findCenterButton.setOnClickListener(this);
        notificationButton.setOnClickListener(this);
        messageButton.setOnClickListener(this);
        userImage.setOnClickListener(this);
        lcLogo.setOnClickListener(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Utility.setScreenHeight(displayMetrics.heightPixels);
        Utility.setScreenWidth(displayMetrics.widthPixels);

        viewPager = (ViewPager) findViewById(R.id.htab_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
//        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        if (tabGenerate)
            generateTabs();
        setDetails(1);

        notificationRecycler.setLayoutManager(new LinearLayoutManager(this));
        notifAdapter = new NotificationAdapter(this, notifications);
        notificationRecycler.setAdapter(notifAdapter);

        final ObservableBoolean notifRetrieved = new ObservableBoolean();
        notifRetrieved.setOnBooleanChangeListener(new OnBooleanChangeListener() {
            @Override
            public void onBooleanChanged(boolean success) {
                if (success) {
                    notificationRetrieved = true;
                    for (Notification notification : Notification.retrieved) {
                        notifications.add(notification);
                    }
                    newNotificationSet();
                }
            }
        });
        Notification.retrieveUnreadNotificationsOfUser(notifRetrieved, Account.getUsername());
        checkNotifications();
    }

    private void newNotificationSet() {
        notificationCount = Notification.countUnread(notifications);
        notifBadge.setVisibility(View.VISIBLE);
        notifBadge.setText(String.valueOf(notificationCount));
        notifAdapter.notifyDataSetChanged();
        if (notificationCount == 0) {
            notifBadge.setVisibility(View.GONE);
        } else {
            notifBadge.setVisibility(View.VISIBLE);
        }
    }

    private void setDetails(int code) {
        if (code != 3) {
            changeProfileImage();
            usernameDisplay.setText(Account.getName());
            if (Account.getType() == Account.Type.LearningCenter) {
                lc = LearningCenter.getLCById(Account.getCenterId());
                fieldDisplay.setText(Account.getType().toString() + " | " + Utility
                        .caps(Account.getStringData("accessLevel")));
            } else
                fieldDisplay.setText(Account.getType().toString());
        }
    }

    private void generateTabs() {
        tabGenerate = false;
        MainAdapter adapter = new MainAdapter(getSupportFragmentManager());
        if (Account.isType("LearningCenter")) {
            adapter.addFragment(new StudentProfile(), "Profile");
            adapter.addFragment(new LearningCenterProfile(), "Center");
            adapter.addFragment(new Feed(), "Feeds");
            adapter.addFragment(new JobPost(), "Job Posts");
            adapter.addFragment(new LCEducators(), "Educators");
            adapter.addFragment(new EnrolmentSystem(), "Enrolment");
            adapter.addFragment(new SchedulingSystem(), "Classes");
        } else if (Account.isType("Educator")) {
            adapter.addFragment(new EducatorProfile(), "Profile");
            adapter.addFragment(new Feed(), "Feeds");
            adapter.addFragment(new JobPost(), "Job Posts");
            adapter.addFragment(new SchedulingSystem(), "Classes");
        } else if (Account.isType("Student")) {
            adapter.addFragment(new StudentProfile(), "Profile");
            adapter.addFragment(new Feed(), "Feeds");
            adapter.addFragment(new EnrolmentSystem(), "Courses");
            adapter.addFragment(new SchedulingSystem(), "Classes");
        }

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.getTabAt(0).setIcon(R.drawable.bell_icon);
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL|TabLayout.GRAVITY_CENTER);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                notificationRecycler.setVisibility(View.GONE);
                switch (position) {
                    case 0:
                        appBarLayout.setExpanded(true);
                        ((CustomAppBarLayoutBehavior) clLayoutParams.getBehavior()).setScrollBehavior(true);
                        userImage.setVisibility(View.VISIBLE);
                        lcLogo.setVisibility(View.GONE);
                        break;
                    case 1:
                        if (Account.isType("learningcenter")) {
                            appBarLayout.setExpanded(true);
                            ((CustomAppBarLayoutBehavior) clLayoutParams.getBehavior()).setScrollBehavior(true);
                            userImage.setVisibility(View.GONE);
                            lcLogo.setVisibility(View.VISIBLE);
                            changeLCLogo();
                            break;
                        }
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
        switch (view.getId()) {
            case R.id.main_subscription_button:
                if (Account.isType("Student")) {
                    String accountType = "Student";
//                    if (Account.isType("Educator"))
//                        accountType = "Educator";
//                    else
//                        accountType = "Student";
                    android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Access denied!");
                    alertDialog.setCancelable(true);
                    alertDialog.setMessage("There are no subscription features for " + accountType + " accounts yet.");
                    alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    break;
                } else if (Account.isType("LearningCenter")) {
                    if (Account.getProfileData().get("AccessLevel").toString().compareToIgnoreCase("staff") == 0) {
                        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
                        alertDialog.setTitle("Access denied!");
                        alertDialog.setCancelable(true);
                        alertDialog.setMessage("Only administrator accounts have access to this feature.");
                        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                        break;
                    }
                }
                Intent intent = new Intent(getApplicationContext(), Subscription.class);
                if (user.getPhotoUrl() != null)
                    intent.putExtra("userPhotoUrl", user.getPhotoUrl().toString());
                startActivity(intent);
                break;
            case R.id.main_find_user_button:
                startActivity(new Intent(getApplicationContext(), SearchUser.class));
                break;
            case R.id.main_center_list_button:
                startActivity(new Intent(getApplicationContext(), SearchCenter.class));
                break;
            case R.id.notification_button:
                if (notificationCount != 0) {
                    notifDialog.show();
                    notifDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            newNotificationSet();
                        }
                    });
                }
                break;
            case R.id.message_button:
                startActivity(new Intent(getApplicationContext(), Chat.class));
                break;
            case R.id.view_user_image:
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Main.this);
                View mView = getLayoutInflater().inflate(R.layout.fragment_dialog_photoview, null);
                final PhotoView photoView = mView.findViewById(R.id.photoview);
                photoView.setMinimumHeight(Utility.getScreenHeight());
                FirebaseStorage.getInstance().getReference().child("images").child(Account.getUsername()).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
//                                Picasso.get().load(uri.toString()).centerCrop().into(userImage);
                                Glide.with(getApplicationContext()).load(uri.toString()).fitCenter().into(photoView);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
                builder.setView(mView);
                android.app.AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.user_learning_center_logo:
                Utility.viewImage(Main.this, Uri.parse(lc.getLogo()));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_activity_pages, menu);

//        if (menu instanceof MenuBuilder) {
//            MenuBuilder m = (MenuBuilder) menu;
//            m.setOptionalIconsVisible(true);
//        }

        return true;
//        getMenuInflater().inflate(R.menu.menu_activity_pages, menu);
//        menu
//        return super.onCreateOptionsMenu(menu);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem updateBusiness = menu.findItem(R.id.menu_update_business);
        if (Account.getType() == Account.Type.LearningCenter) {
            updateBusiness.setVisible(true);
            MenuItem createUser = menu.findItem(R.id.menu_create_user);
            if (Account.getStringData("accessLevel").equals("administrator"))
                createUser.setVisible(true);
            else
                createUser.setVisible(false);
        } else {
            updateBusiness.setVisible(false);
        }
        if (Account.getType() == Account.Type.Educator) {
            MenuItem resume = menu.findItem(R.id.menu_update_resume);
            resume.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_update_account:
                startActivityForResult(new Intent(getApplicationContext(), UpdateAccount.class), UPDATE_ACCOUNT);
                return true;
            case R.id.menu_update_profile:
                startActivityForResult(new Intent(getApplicationContext(), UpdateProfile.class), UPDATE_PROFILE);
                return true;
            case R.id.menu_update_resume:
                Intent intent = new Intent(getApplicationContext(), AddUpdateResume.class);
                if (!Resume.getId().isEmpty())
                    intent.putExtra("resumeId", Resume.getId());
                startActivityForResult(intent, UPDATE_RESUME);
                return true;
            case R.id.menu_update_business:
                startActivityForResult(new Intent(getApplicationContext(), UpdateLearningCenter.class), UPDATE_CENTER);
                return true;
            case R.id.menu_create_user:
                startActivity(new Intent(getApplicationContext(), CreateUser.class));
                return true;
            case R.id.menu_logout:
                Connection.logOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void changeProfileImage() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (user.getPhotoUrl() != null) {
                    FirebaseStorage.getInstance().getReference().child("images").child(Account.getUsername()).getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
//                                Picasso.get().load(uri.toString()).centerCrop().into(userImage);
                                    Glide.with(getApplicationContext()).load(uri.toString()).fitCenter().into(userImage);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                } else {
                }
            }
        });
    }

    private void changeLCLogo() {
        if (lc.getLogo() != null && !lc.getLogo().isEmpty()) {
            Glide.with(this).load(lc.getLogo()).error(R.drawable.logo_icon)
                    .apply(new RequestOptions().override(Utility.getScreenWidth(),
                            Utility.dpToPx(this, 256))).centerCrop().into(lcLogo);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setResult(RESULT_OK);

        Intent intent = new Intent(this, SplashScreen.class);
        intent.putExtra("update", true);
        int code = 0;
        if (requestCode == UPDATE_ACCOUNT && resultCode == RESULT_OK) {
            code = 1;
        } else if (requestCode == UPDATE_PROFILE && resultCode == RESULT_OK) {
            code = 2;
        } else if (requestCode == UPDATE_CENTER && resultCode == RESULT_OK) {
            code = 3;
        } else if (requestCode == UPDATE_RESUME && resultCode == RESULT_OK) {
            code = 0;
        }
        intent.putExtra("type", code);
        setDetails(code);
        startActivity(intent);
    }

    private void checkNotifications() {
        FirebaseFirestore.getInstance().collection("Notification")
                .whereEqualTo("Username", Account.getUsername())
                .whereEqualTo("Status", "new")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        for (QueryDocumentSnapshot doc : value) {
                            FirebaseFirestore.getInstance().collection("Notification").document(doc.getId()).update("Status", "unread");
                            Notification not = new Notification();
                            not.setNotifId(doc.getId());
                            not.setUsername(doc.getString("Username"));
                            not.setStatus("unread");
                            not.setMessage(doc.getString("Message"));
                            not.setLink(doc.getString("Link"));
                            not.setSubject(doc.getString("Subject"));
                            not.setDate(doc.getTimestamp("Date"));
                            createNotification(not);
                            notifications.add(not);
                            newNotificationSet();
                        }
                    }
                });
    }

    public void createNotification(Notification notif) {
        Intent notificationIntent = new Intent(getApplicationContext(), Main.class);
        notificationIntent.putExtra("fromNotification", true);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(Main.this, CHANNEL_ID);
        mBuilder.setContentTitle(notif.getSubject());
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setContentText(notif.getMessage());
        mBuilder.setSmallIcon(R.drawable.logo_icon);
        mBuilder.setAutoCancel(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "ILearnCentral", importance);
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }
}
