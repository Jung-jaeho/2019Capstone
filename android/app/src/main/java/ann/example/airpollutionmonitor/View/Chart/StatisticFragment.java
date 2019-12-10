package ann.example.airpollutionmonitor.View.Chart;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ann.example.airpollutionmonitor.AppManager;
import ann.example.airpollutionmonitor.Controller.MonitorDataSource;
import ann.example.airpollutionmonitor.Model.Location;
import ann.example.airpollutionmonitor.Model.SensorData;
import ann.example.airpollutionmonitor.R;
import ann.example.airpollutionmonitor.View.Chart.ListViewItems.BarChartItem;
import ann.example.airpollutionmonitor.View.Chart.ListViewItems.ChartItem;
import ann.example.airpollutionmonitor.View.Chart.ListViewItems.LineChartItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatisticFragment extends Fragment {
    private static final String TAG = "StatisticFragment";
    private String serial;

    LineChartItem lineChartItem;
    BarChartItem barChartItem;
    int n;

    ArrayList<SensorData> dailySensorData, weeklySensorData;

    public static StatisticFragment newInstance(Location location) {
        StatisticFragment fragment = new StatisticFragment();
        Bundle args = new Bundle();
        args.putSerializable("location", location);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        Location location = (Location) bundle.getSerializable("location");
        serial = location.getSerialNumber();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        initView(view);
        return view;
    }

    public void initView(View view) {
        // 여러 그래프 추가
        ListView lv = view.findViewById(R.id.listView1);

        ArrayList<ChartItem> list = new ArrayList<>();
        lineChartItem = new LineChartItem(generateDataLine(), getContext());
        list.add(lineChartItem);
        barChartItem = new BarChartItem(generateDataBar(), getContext());
        list.add(barChartItem);

        ChartDataAdapter cda = new ChartDataAdapter(getContext(), list);
        lv.setAdapter(cda);
    }

    private String getTodayDate() {
        Date rightNow = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = formatter.format(rightNow);

        //Log.d(TAG, dateString);

        return dateString;
    }

    /**
     * adapter that supports 3 different item types
     */
    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {

        ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            //noinspection ConstantConditions
            return getItem(position).getView(position, convertView, getContext());
        }

        @Override
        public int getItemViewType(int position) {
            // return the views type
            ChartItem ci = getItem(position);
            return ci != null ? ci.getItemType() : 0;
        }

        @Override
        public int getViewTypeCount() {
            return 3; // we have 3 different item-types
        }
    }

    private LineData generateDataLine() {

        //String today=getTodayDate();
        //getSensorData(today, today);

        ArrayList<Entry> values1 = AppManager.getInstance().getValues1();
        if(values1== null) {
            values1 = new ArrayList<Entry>();
            for (int i = 0; i < 12; i++) {
                values1.add(new Entry(i, (float) (Math.random() * 0.3 + 0.3)));
            }

            AppManager.getInstance().setValues1(values1);
        }

        LineDataSet d1 = new LineDataSet(values1, "Avg");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setColor(ColorTemplate.LIBERTY_COLORS[0]);
        d1.setCircleColor(ColorTemplate.LIBERTY_COLORS[0]);
        d1.setDrawValues(true);

        ArrayList<Entry> values2 = AppManager.getInstance().getValues2();

        if(values2== null) {
            values2 = new ArrayList<Entry>();
            for (int i = 0; i < 12; i++) {
                values2.add(new Entry(i, (float) (values1.get(i).getY() + (float) (Math.random()) * 0.1)));
            }
            AppManager.getInstance().setValues2(values2);
        }

        LineDataSet d2 = new LineDataSet(values2, "Max");
        d2.setLineWidth(2.5f);
        d2.setCircleRadius(4.5f);
        d2.setHighLightColor(Color.rgb(244, 117, 117));
        d2.setColor(ColorTemplate.LIBERTY_COLORS[1]);
        d2.setCircleColor(ColorTemplate.LIBERTY_COLORS[1]);
        d2.setDrawValues(true);

        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(d1);
        sets.add(d2);

        return new LineData(sets);
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return Bar data
     */
    private SensorData getMaxData(ArrayList<SensorData> list){
        // 그래프에 데이터 추가
        SensorData max = list.get(0);
        for(int j=0; j<list.size(); j++){
            if(list.get(j).getHum() > max.getHum()){
                max.setHum(list.get(j).getHum());
            }
            if(list.get(j).getTem() > max.getTem()){
                max.setTem(list.get(j).getTem());
            }
            if(list.get(j).getCH4() > max.getCH4()){
                max.setCH4(list.get(j).getCH4());
            }
            if(list.get(j).getCO() > max.getCO()){
                max.setCO(list.get(j).getCO());
            }
        }
        return max;
    }

    private SensorData getAvgData(ArrayList<SensorData> list){
        SensorData avg = new SensorData();
        for(int j=0; j<list.size(); j++){
            avg.setCO(avg.getCO() + list.get(j).getCO());
            avg.setCH4(avg.getCH4() + list.get(j).getCH4());
            avg.setTem(avg.getTem() + list.get(j).getTem());
            avg.setHum(avg.getHum() + list.get(j).getHum());
        }

        avg.setCO(avg.getCO()/list.size());
        avg.setCH4(avg.getCH4()/list.size());
        avg.setTem(avg.getTem()/list.size());
        avg.setHum(avg.getHum()/list.size());

        return avg;
    }

    private BarData generateDataBar() {
        String dates[] = {"20191204", "20191203", "20191202", "20191201", "20191131", "20191130", "20191129", "20191128"};
        final ArrayList<BarEntry> entries = new ArrayList<>();
        n = 0;

        for (int i = 0; i < dates.length; i++) {
            MonitorDataSource monitorDataSource = MonitorDataSource.getInstance();
            monitorDataSource.getJsonByDate(serial, dates[i], dates[i])
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            // retrofit 통신이 성공했을 때
                            String str = response.body();
                            //Log.d(TAG, str);

                            if (str != null) {
                                // 데이터 model 객체 생성
                                try {
                                    JSONObject jsonObject = new JSONObject(str);
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                                    ArrayList<SensorData> list = new ArrayList<>();
                                    for (int j = 0; j < jsonArray.length(); j++) {
                                        JSONObject dataJsonObject = jsonArray.getJSONObject(j);
                                        //Log.d(TAG, jsonArray.toString());

                                        SensorData sensorData = new SensorData(dataJsonObject.getString("time_slot"), dataJsonObject.getDouble("TEM")
                                                , dataJsonObject.getDouble("HUM"), dataJsonObject.getDouble("CO"), dataJsonObject.getDouble("CH4"));
                                        Log.d(TAG, sensorData.toString());

                                        list.add(sensorData);
                                    }

                                    // 그래프에 데이터 추가
                                    switch (barChartItem.holder.spinner.getSelectedItemPosition()) {
                                        case 0:
                                            entries.add(new BarEntry((float) n++, (float) getMaxData(list).getCO()));
                                            entries.add(new BarEntry((float) n++, (float) getMaxData(list).getCH4()));
                                            entries.add(new BarEntry((float) n++, (float) getMaxData(list).getTem()));
                                            entries.add(new BarEntry((float) n++, (float) getMaxData(list).getHum()));
                                            break;
                                        case 1:
                                            entries.add(new BarEntry((float) n++, (float) getAvgData(list).getCO()));
                                            entries.add(new BarEntry((float) n++, (float) getAvgData(list).getCH4()));
                                            entries.add(new BarEntry((float) n++, (float) getAvgData(list).getTem()));
                                            entries.add(new BarEntry((float) n++, (float) getAvgData(list).getHum()));
                                            break;
                                    }

                                    Log.d(TAG, "addSensorData");


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                // 그래프에 데이터 추가
                                entries.add(new BarEntry((float) n++, 0));
                                entries.add(new BarEntry((float) n++, 0));
                                entries.add(new BarEntry((float) n++, 0));
                                entries.add(new BarEntry((float) n++, 0));
                            }

                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            // retrofit 통신이 실패했을 때
                            Log.d(TAG, "통신이 실패하였습니다.");
                        }
                    });

        }

        /*
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            entries.add(new BarEntry(i, (int) (Math.random() * 70) + 30));
        }

         */
        BarDataSet d = new BarDataSet(entries, "New DataSet ");
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setHighLightAlpha(255);

        BarData cd = new BarData(d);
        cd.setBarWidth(0.9f);
        return cd;
    }
}
