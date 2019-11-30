package ann.example.airpollutionmonitor.View.Chart;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ann.example.airpollutionmonitor.AppManager;
import ann.example.airpollutionmonitor.BaseActivity;
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

public class StatisticActivity extends BaseActivity implements OnChartValueSelectedListener {
    private static final String TAG = "StatisticActivity";
    private ArrayList<Location> locations = AppManager.getInstance().getLocations();
    private String serial = locations.get(0).getSerialNumber();

    private TextView locationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

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
        title.setText(R.string.menu_statistic);

        // 장소명
        locationTextView = findViewById(R.id.location);
        locationTextView.setText(locations.get(0).getName());

        // 여러 그래프 추가
        ListView lv = findViewById(R.id.listView1);

        ArrayList<ChartItem> list = new ArrayList<>();
        //list.add(new PieChartItem(generateDataPie(), getApplicationContext()));
        list.add(new LineChartItem(generateDataLine(1), getApplicationContext()));
        list.add(new BarChartItem(generateDataBar(1), getApplicationContext()));

        ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
        lv.setAdapter(cda);

        getSensorData(getTodayDate(), getTodayDate());
    }

    private String getTodayDate(){
        Date rightNow = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = formatter.format(rightNow);

        Log.d(TAG, dateString);

        return dateString;
    }

    private void getSensorData(String startDate, String endDate){
        MonitorDataSource monitorDataSource = MonitorDataSource.getInstance();
        monitorDataSource.getJsonByDate(serial, startDate, endDate)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        // retrofit 통신이 성공했을 때
                        String str = response.body();
                        //Log.d(TAG, str);

                        // 데이터 model 객체 생성
                        try {
                            JSONObject jsonObject = new JSONObject(str);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            JSONObject dataJsonObject = jsonArray.getJSONObject(0);
                            //Log.d(TAG, jsonArray.toString());

                            SensorData sensorData = new SensorData(dataJsonObject.getString("time_slot"), dataJsonObject.getDouble("TEM")
                                    , dataJsonObject.getDouble("HUM"), dataJsonObject.getDouble("CO"), dataJsonObject.getDouble("CH4"));
                            Log.d(TAG, sensorData.toString());
                            /*
                            // 현재 가져온 데이터 갱신
                            setCurrentData(sensorData);

                            // 그래프에 데이터 추가
                            addEntry(spinner.getSelectedItemPosition(), sensorData);
                            Log.d(TAG, "addSensorData");
                            */

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

    /** adapter that supports 3 different item types */
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

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return Line data
     */
    private LineData generateDataLine(int cnt) {

        getTodayDate();
       // getSensorData(getTodayDate(), getTodayDate());

        ArrayList<Entry> values1 = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            values1.add(new Entry(i, (int) (Math.random() * 65) + 40));
        }

        LineDataSet d1 = new LineDataSet(values1, "New DataSet " + cnt + ", (1)");
        d1.setLineWidth(2.5f);
        d1.setCircleRadius(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);

        ArrayList<Entry> values2 = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            values2.add(new Entry(i, values1.get(i).getY() - 30));
        }

        LineDataSet d2 = new LineDataSet(values2, "New DataSet " + cnt + ", (2)");
        d2.setLineWidth(2.5f);
        d2.setCircleRadius(4.5f);
        d2.setHighLightColor(Color.rgb(244, 117, 117));
        d2.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setCircleColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        d2.setDrawValues(false);

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
    private BarData generateDataBar(int cnt) {

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            entries.add(new BarEntry(i, (int) (Math.random() * 70) + 30));
        }

        BarDataSet d = new BarDataSet(entries, "New DataSet " + cnt);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d.setHighLightAlpha(255);

        BarData cd = new BarData(d);
        cd.setBarWidth(0.9f);
        return cd;
    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return Pie data
     */
    private PieData generateDataPie() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            entries.add(new PieEntry((float) ((Math.random() * 70) + 30), "Quarter " + (i+1)));
        }

        PieDataSet d = new PieDataSet(entries, "");

        // space between slices
        d.setSliceSpace(2f);
        d.setColors(ColorTemplate.VORDIPLOM_COLORS);

        return new PieData(d);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
