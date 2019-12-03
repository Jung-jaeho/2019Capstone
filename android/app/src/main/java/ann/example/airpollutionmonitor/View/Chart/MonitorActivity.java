package ann.example.airpollutionmonitor.View.Chart;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import ann.example.airpollutionmonitor.AppManager;
import ann.example.airpollutionmonitor.BaseActivity;
import ann.example.airpollutionmonitor.Model.Location;
import ann.example.airpollutionmonitor.R;

public class MonitorActivity extends BaseActivity {
    private static final String TAG = "MonitorActivity";

    private ArrayList<Location> locations = AppManager.getInstance().getLocations();
    FragmentPagerAdapter adapterViewPager;
    TextView placeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        initView();
    }

    private void initView() {
        ImageView backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 상단 제목
        TextView title = findViewById(R.id.title);
        title.setText(R.string.menu_monitor);

        // 장소명
        placeName = findViewById(R.id.location);
        placeName.setText(locations.get(0).getName());

        ViewPager vpPager = findViewById(R.id.vpPager_monitor);
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

    public static class PagerAdapter extends FragmentPagerAdapter {
        ArrayList<Location> locations;

        public PagerAdapter(@NonNull FragmentManager fm, ArrayList<Location> locations) {
            super(fm);
            this.locations = locations;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return MonitorFragment.newInstance(locations.get(position));
        }

        @Override
        public int getCount() {
            return locations.size();
        }

    }

}
