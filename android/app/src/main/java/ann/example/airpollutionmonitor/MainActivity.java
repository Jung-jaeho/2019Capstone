package ann.example.airpollutionmonitor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import ann.example.airpollutionmonitor.Model.Location;
import ann.example.airpollutionmonitor.View.Chart.MonitorActivity;
import ann.example.airpollutionmonitor.View.Chart.StatisticActivity;
import ann.example.airpollutionmonitor.View.HomeFragment;
import ann.example.airpollutionmonitor.View.IconFragment;
import ann.example.airpollutionmonitor.View.RegisterActivity;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    BackPressCloseHandler backPressCloseHandler;
    FragmentPagerAdapter adapterViewPager;
    TextView placeName;
    LinearLayout layout;

    RelativeLayout exception;
    LinearLayout info;

    ArrayList<Location> locations = AppManager.getInstance().getLocations();

    public static final String SHARED_PREFERENCE_TAG = "connected locations";
    public static final int REGISTER_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppManager.getInstance().setMainActivity(this);
        backPressCloseHandler = new BackPressCloseHandler(this);

        initNavigationMenu();
        initHome();

        registerPushToken();
    }

    private void registerPushToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initHome() {
        layout = findViewById(R.id.layout);
        exception = findViewById(R.id.exception);
        info = findViewById(R.id.info);

        if (locations.size() == 0) {
            // 등록된 장소 없음 예외처리
            exception.setVisibility(View.VISIBLE);
            info.setVisibility(View.GONE);
        } else {
            // 등록된 장소의 현재 공기 정보 제공
            exception.setVisibility(View.GONE);
            info.setVisibility(View.VISIBLE);

            placeName = findViewById(R.id.arduino_name);
            placeName.setText(locations.get(0).getName());

            ViewPager vpPager = findViewById(R.id.vpPager_main);
            adapterViewPager = new PagerAdapter(getSupportFragmentManager(), locations);
            vpPager.setAdapter(adapterViewPager);
            vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    // 현재 장소 이름 표시
                    placeName.setText(locations.get(position).getName());

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }


    }

    public void setBackGroundColor(int level) {
        switch (level) {
            default:
                layout.setBackgroundColor(getResources().getColor(R.color.colorLevel1));
                break;
            case IconFragment.level2:
                layout.setBackgroundColor(getResources().getColor(R.color.colorLevel2));
                break;
            case IconFragment.level3:
                layout.setBackgroundColor(getResources().getColor(R.color.colorLevel3));
                break;
            case IconFragment.level4:
                layout.setBackgroundColor(getResources().getColor(R.color.colorLevel4));
                break;
            case IconFragment.level5:
                layout.setBackgroundColor(getResources().getColor(R.color.colorLevel5));
                break;
            case IconFragment.level6:
                layout.setBackgroundColor(getResources().getColor(R.color.colorLevel6));
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 연결된 아두이노 장치 저장
        Log.d(TAG, AppManager.getInstance().getLocations().toString());
        onSaveData(AppManager.getInstance().getLocations(), SHARED_PREFERENCE_TAG);

    }

    public void onSaveData(ArrayList<Location> list, String tag) {
        Gson gson = new GsonBuilder().create();
        Type listType = new TypeToken<ArrayList<Location>>() {
        }.getType();
        String json = gson.toJson(list, listType);  // arraylist -> json string

        SharedPreferences sp = getSharedPreferences(tag, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(tag, json); // JSON으로 변환한 객체를 저장한다.
        editor.commit(); // 완료한다.
    }

    private void initNavigationMenu() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);   // 툴바 사용 설정
        getSupportActionBar().setDisplayShowTitleEnabled(false);        // 타이틀 안보이게 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);          // 왼쪽 버튼 사용 여부 true
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu); // 왼쪽 버튼 아이콘 설정

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }


    public class PagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Location> locations;
        private ArrayList<Fragment> fragmentArrayList = new ArrayList();

        public PagerAdapter(@NonNull FragmentManager fm, ArrayList<Location> locations) {
            super(fm);
            this.locations = locations;
            for (int i = 0; i < locations.size(); i++) {
                fragmentArrayList.add(HomeFragment.newInstance(locations.get(i)));
            }
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentArrayList.get(position);
        }

        @Override
        public int getCount() {
            return locations.size();
        }

    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem toggleService = menu.findItem(R.id.nav_alarm);
        Switch actionView = (Switch) toggleService.getActionView();
        actionView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Toast.makeText(getApplicationContext(), "알람이 설정되었습니다", Toast.LENGTH_SHORT).show();
            }
        });
        */
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        Intent intent;
        switch (id) {
            case R.id.nav_register:
                intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent, REGISTER_CODE);
                break;
            case R.id.nav_monitor:
                intent = new Intent(getApplicationContext(), MonitorActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_statistic:
                intent = new Intent(getApplicationContext(), StatisticActivity.class);
                startActivity(intent);
                break;
                /*
            case R.id.nav_notice:
                intent = new Intent(getApplicationContext(), NoticeActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_info:
                intent = new Intent(getApplicationContext(), InfoActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_share:
                //intent = new Intent(getApplicationContext(), CreditView.class);
                //startActivity(intent);
                break;
            case R.id.nav_settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                break;
                 */
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REGISTER_CODE) {
            // 액티비티 갱신
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }
}

class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private Toast toast;
    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }

}
