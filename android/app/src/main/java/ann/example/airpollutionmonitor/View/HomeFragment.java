package ann.example.airpollutionmonitor.View;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ann.example.airpollutionmonitor.AppManager;
import ann.example.airpollutionmonitor.Controller.MonitorDataSource;
import ann.example.airpollutionmonitor.Model.Location;
import ann.example.airpollutionmonitor.Model.SensorData;
import ann.example.airpollutionmonitor.R;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private MonitorDataSource monitorDataSource;
    private ImagePagerAdapter adapterViewPager;
    private ListViewAdapter listViewAdapter;
    private LinearLayout outer;
    Location location;
    ListView listView;
    ViewPager vpPager;

    public static HomeFragment newInstance(Location location) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable("location", location);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        location = (Location) bundle.getSerializable("location");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        vpPager = view.findViewById(R.id.vpPager_home);
        outer = view.findViewById(R.id.outer);
        CircleIndicator indicator = view.findViewById(R.id.indicator);
        indicator.setViewPager(vpPager);

        listView = view.findViewById(R.id.details);
        setCurrentSensorData(location.getSerialNumber());

        // 리스트뷰에 데이터 적용
        listViewAdapter = new ListViewAdapter();
        listView.setAdapter(listViewAdapter);
        // 아이콘 이미지 변경

        adapterViewPager = new ImagePagerAdapter(getChildFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpPager.setAdapter(adapterViewPager);
        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                adapterViewPager.setBackgroundColor(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        return view;
    }

    private void setCurrentSensorData(String serial) {
        int from = 0, size = 1;

        MonitorDataSource monitorDataSource = MonitorDataSource.getInstance();
        monitorDataSource.getJsonByIndex(serial, from, size)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        // retrofit 통신이 성공했을 때
                        String str = response.body();
                        Log.d(TAG, str);

                        // 데이터 model 객체 생성
                        try {
                            JSONObject jsonObject = new JSONObject(str);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            JSONObject dataJsonObject = jsonArray.getJSONObject(0);
                            //Log.d(TAG, jsonArray.toString());
                            SensorData sensorData = new SensorData(dataJsonObject.getString("time_slot"), dataJsonObject.getDouble("TEM")
                                    , dataJsonObject.getDouble("HUM"), dataJsonObject.getDouble("CO"), dataJsonObject.getDouble("CH4"));
                            Log.d(TAG, sensorData.toString());
                            AppManager.getInstance().setSensorData(sensorData);
                            adapterViewPager.setSensorData(sensorData);
                            listViewAdapter.setData(sensorData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        // retrofit 통신이 실패했을 때
                        Log.d(TAG, "통신이 실패하였습니다.");
                    }
                });

    }


    public class ListViewAdapter extends BaseAdapter {
        private SensorData data = new SensorData("", 0, 0, 0, 0);
        ImageView icon;
        TextView value;
        TextView type;

        public void setData(SensorData data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();
            //TODO 원래 viewHodler parrtern을 써야함 여기서
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_detail, viewGroup, false);
            icon = view.findViewById(R.id.icon);
            value = view.findViewById(R.id.type);
            type = view.findViewById(R.id.value);

            switch (i) {
                case 0:
                    icon.setVisibility(View.INVISIBLE);
                    type.setText("온도");
                    value.setText(data.getTem() + " ℃");
                    break;
                case 1:
                    icon.setVisibility(View.INVISIBLE);
                    type.setText("습도");
                    value.setText(data.getHum() + " g/m3");
                    break;
                case 2:
                    type.setText("일산화탄소(CO)");
                    value.setText(data.getCO() + " ppm");
                    break;
                case 3:
                    type.setText("메테인(CH4)");
                    value.setText(data.getCH4() + " ppm");

                    break;
            }

            return view;
        }
    }

    public class ImagePagerAdapter extends FragmentPagerAdapter {
        // 데이터 받아 오면 수정해야함
        private int NUM_ITEMS = 2;
        private SensorData sensorData = new SensorData("", 0, 0, 0, 0);
        private static final int DEFAULT_LEVEL = 1;
        private ArrayList<IconFragment> fragmentArrayList = new ArrayList();
        private int level = 0;

        public ImagePagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
            fragmentArrayList.add(IconFragment.newInstance("일산화탄소(CO)", DEFAULT_LEVEL));
            fragmentArrayList.add(IconFragment.newInstance("메테인(CH4)", DEFAULT_LEVEL));
        }

        public void setSensorData(SensorData data) {
            this.sensorData = data;
            for (int i = 0; i < fragmentArrayList.size(); i++) {
                switch (i) {
                    case 0:
                        fragmentArrayList.get(i).setLevel(getCOLevel(sensorData.getCO()));
                        break;
                    case 1:
                        fragmentArrayList.get(i).setLevel(getCH4Level(sensorData.getCH4()));
                        break;
                }
            }
            notifyDataSetChanged();
        }

        public void setBackgroundColor(int pos) {
            switch (pos) {
                case 0:
                    AppManager.getInstance().getMainActivity().setBackGroundColor(getCOLevel(sensorData.getCO())); // 바탕색 변경
                    break;
                case 1:
                    AppManager.getInstance().getMainActivity().setBackGroundColor(getCOLevel(sensorData.getCH4())); // 바탕색 변경
                    break;
            }
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return fragmentArrayList.get(0);
                case 1:
                    return fragmentArrayList.get(1);
                default:
                    return null;

            }
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        private int getCOLevel(double value) {
            if (value < 2) {
                return IconFragment.level1;
            } else if (value < 5.5) {
                return IconFragment.level2;
            } else if (value < 9) {
                return IconFragment.level3;
            } else if (value < 12) {
                return IconFragment.level4;
            } else if (value < 32) {
                return IconFragment.level5;
            } else {
                return IconFragment.level6;
            }
        }

        private int getCH4Level(double value) {
            if (value < 60) {
                return IconFragment.level1;
            } else if (value < 120) {
                return IconFragment.level2;
            } else if (value < 180) {
                return IconFragment.level3;
            } else if (value < 280) {
                return IconFragment.level4;
            } else if (value < 400) {
                return IconFragment.level5;
            } else {
                return IconFragment.level6;
            }
        }
    }

}

