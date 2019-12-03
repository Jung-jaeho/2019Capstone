package ann.example.airpollutionmonitor.View.Chart;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import ann.example.airpollutionmonitor.AppManager;
import ann.example.airpollutionmonitor.Controller.MonitorDataSource;
import ann.example.airpollutionmonitor.Model.Location;
import ann.example.airpollutionmonitor.Model.SensorData;
import ann.example.airpollutionmonitor.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MonitorFragment extends Fragment implements OnChartValueSelectedListener {
    private static final String TAG = "MonitorFragment";

    private String serial;
    private Spinner spinner;
    private LineChart chart;
    private TextView timerTextView, updateTextView,
            temTextView, humTextView, coTextView, ch4TextView;
    private Timer timer;

    private Thread thread;
    private Handler mHandler = new Handler();

    private int maxVisibleEntryCount = 9;

    public static MonitorFragment newInstance(Location location){
        MonitorFragment fragment = new MonitorFragment();
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
        View view = inflater.inflate(R.layout.fragment_monitor, container, false);
        initView(view);
        initChart(view);
        return view;
    }

    // Chart Initialization
    private void initChart(View view) {
        chart = view.findViewById(R.id.chart1);
        chart.setOnChartValueSelectedListener(this);

        // enable description text - description delete
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(true);

        // if disabled, scaling can be done on x- and y-axis separately
        //chart.setPinchZoom(true);
        chart.setAutoScaleMinMaxEnabled(false);  // y축 자동 보정

        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        // add empty data
        chart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();

        // modify the legend ...
        l.setForm(LegendForm.LINE);
        l.setTextColor(Color.BLACK);

        // X axis
        XAxis xl = chart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(false);
        xl.setEnabled(true);

        // Y axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(true);


        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        getRealTimeData();  // 실시간 데이터 받아오기
    }

    private void initView(View view) {
        temTextView = view.findViewById(R.id.value_tem);
        humTextView = view.findViewById(R.id.value_hum);
        coTextView = view.findViewById(R.id.value_co);
        ch4TextView = view.findViewById(R.id.value_ch4);
        updateTextView = view.findViewById(R.id.txt_update);

        // Timer text
        timerTextView = view.findViewById(R.id.txt_timer);
        MainTimerTask timerTask = new MainTimerTask();
        timer = new Timer();
        timer.schedule(timerTask, 500, 1000);

        // Spinner
        spinner = view.findViewById(R.id.spinner);
        String[] str = getResources().getStringArray(R.array.spinnerArray);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, str);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, spinner.getSelectedItem().toString() + "is selected");

                chart.clearValues();
                setLimitLine(position);
                //chart.clearAllViewportJobs();

                //chart.resetViewPortOffsets();

                //getRealTimeData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private String getCurrentDate() {
        Date rightNow = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd kk:mm:ss ");
        String dateString = formatter.format(rightNow);
        return dateString;
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            timerTextView.setText(getResources().getString(R.string.prompt_time) + " " + getCurrentDate());
        }
    };


    class MainTimerTask extends TimerTask {
        public void run() {
            mHandler.post(mUpdateTimeTask);
        }
    }

    private LineDataSet createSet(int index) {
        // 그래프 변수 하나 설정
        LineDataSet set = new LineDataSet(null, getLabel(index));
        set.setAxisDependency(AxisDependency.LEFT);
        set.setColor(getLineColor(index));
        set.setCircleColor(getLineColor(index));
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighlightEnabled(true);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(9f);
        set.setDrawValues(true);
        return set;
    }

    private int getLineColor(int index) {
        switch (index) {
            case 0:
                return ColorTemplate.MATERIAL_COLORS[0];
            case 1:
                return ColorTemplate.MATERIAL_COLORS[1];
            case 2:
                return ColorTemplate.MATERIAL_COLORS[2];
            case 3:
                return ColorTemplate.MATERIAL_COLORS[3];
        }
        return 0;
    }

    private String getLabel(int index) {
        switch (index) {
            case 0:
                return getResources().getString(R.string.prompt_co);
            case 1:
                return getResources().getString(R.string.prompt_ch4);
            case 2:
                return getResources().getString(R.string.prompt_tem);
            case 3:
                return getResources().getString(R.string.prompt_hum);
        }
        return null;
    }

    private double getYValue(int index, SensorData sensorData){
        switch (index){
            case 0:
                return sensorData.getCO();
            case 1:
                return sensorData.getCH4();
            case 2:
                return sensorData.getTem();
            case 3:
                return sensorData.getHum();
        }
        return 0;
    }
    // Add limit line
    private void setLimitLine(int index){
        YAxis leftAxis = chart.getAxisLeft();
        LimitLine ll1;
        switch (index){
            case 0:
                leftAxis.removeAllLimitLines();
                ll1 = new LimitLine(AppManager.getInstance().COUpperLimit, "");
                ll1.setLineWidth(4f);
                ll1.enableDashedLine(10f, 10f, 0f);
                ll1.setLabelPosition(LimitLabelPosition.RIGHT_TOP);
                ll1.setTextSize(10f);

                // draw limit lines behind data instead of on top
                leftAxis.setDrawLimitLinesBehindData(true);
                leftAxis.setDrawLimitLinesBehindData(true);

                // add limit lines
                leftAxis.addLimitLine(ll1);
                break;
            case 1:
                leftAxis.removeAllLimitLines();
                ll1 = new LimitLine(AppManager.getInstance().CH4UpperLimit, "");
                ll1.setLineWidth(4f);
                ll1.enableDashedLine(10f, 10f, 0f);
                ll1.setLabelPosition(LimitLabelPosition.RIGHT_TOP);
                ll1.setTextSize(10f);

                // draw limit lines behind data instead of on top
                leftAxis.setDrawLimitLinesBehindData(true);
                leftAxis.setDrawLimitLinesBehindData(true);

                // add limit lines
                leftAxis.addLimitLine(ll1);
                break;
        }
    }


    // 차트에 데이터 추가
    private void addEntry(int index, SensorData sensorData) {
        LineData data = chart.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet(index);
                data.addDataSet(set);
            }

            //data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 40) + 30f), 0);
            float yValue= (float) getYValue(index, sensorData);
            data.addEntry(new Entry(set.getEntryCount(), yValue), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            chart.notifyDataSetChanged();

            // limit the number of visible entries
            chart.setVisibleXRangeMaximum(maxVisibleEntryCount);
            // chart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            chart.moveViewToX(data.getEntryCount());

        }
    }

    private void setCurrentData(SensorData sensorData) {
        temTextView.setText(getResources().getString(R.string.prompt_tem) + ": "
                + sensorData.getTem() + getResources().getString(R.string.unit_tem));
        humTextView.setText(getResources().getString(R.string.prompt_hum) + ": "
                + sensorData.getHum() + getResources().getString(R.string.unit_hum));
        coTextView.setText(getResources().getString(R.string.prompt_co) + ": "
                + sensorData.getCO() + getResources().getString(R.string.unit_ppm));
        ch4TextView.setText(getResources().getString(R.string.prompt_ch4) + ": "
                + sensorData.getCH4() + getResources().getString(R.string.unit_ppm));
    }

    private void setUpdateTime(SensorData sensorData) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy.MM.dd kk:mm:ss ");
        String dateString = formatter.format(sensorData.getDate());
        updateTextView.setText(getResources().getString(R.string.prompt_update) + " " + dateString);
    }

    // 서버와 통신해서 데이터 차트에 추가
    private void addSensorData(int from, int size){
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
                            // 업데이트 시간 갱신
                            setUpdateTime(sensorData);
                            // 현재 가져온 데이터 갱신
                            setCurrentData(sensorData);

                            // 그래프에 데이터 추가
                            addEntry(spinner.getSelectedItemPosition(), sensorData);
                            Log.d(TAG, "addSensorData");

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

    private Runnable getRunnable(final int from) {
        final int size = 1;   // 항상 최신 데이터를 가져와서 넣음
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                addSensorData(from, size);
            }
        };
        return runnable;

    }

    // 주기적으로 스레드 돌려서 서버에서 데이터 가져옴
    private void getRealTimeData() {
        if (thread != null)
            thread.interrupt();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Don't generate garbage runnables inside the loop.
                int i=0;
                while (true) {
                    if(Thread.interrupted()) { break; }

                    if(getActivity() != null) {
                        //getActivity().runOnUiThread(getRunnable(i));
                        //i++;
                        getActivity().runOnUiThread(getRunnable(0));
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
        //timer.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

}
