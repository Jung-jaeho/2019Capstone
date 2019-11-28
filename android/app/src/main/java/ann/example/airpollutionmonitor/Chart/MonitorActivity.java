package ann.example.airpollutionmonitor.Chart;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ann.example.airpollutionmonitor.AppManager;
import ann.example.airpollutionmonitor.BaseActivity;
import ann.example.airpollutionmonitor.Controller.MonitorDataSource;
import ann.example.airpollutionmonitor.Model.Location;
import ann.example.airpollutionmonitor.Model.SensorData;
import ann.example.airpollutionmonitor.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MonitorActivity extends BaseActivity implements OnChartValueSelectedListener {
    private static final String TAG = "MonitorActivity";
    private ArrayList<Location> locations = AppManager.getInstance().getLocations();
    private String serial = locations.get(0).getSerialNumber();
    private ArrayList<SensorData> currentDatas;

    public static final int TYPE_CO = 0;
    public static final int TYPE_CH4 = 1;
    private int dataType = TYPE_CO;

    private LineChart chart;
    private TextView locationTextView, timerTextView, updateTextView,
            temTextView, humTextView, coTextView, ch4TextView;
    private Timer timer;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Full Screen Mode Setting
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_monitor);

        initView();
        initChart();
    }

    private void initChart() {
        // Chart Initialization
        setTitle("MonitorActivity");

        chart = findViewById(R.id.chart1);
        chart.setOnChartValueSelectedListener(this);

        // enable description text - description delete
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);

        // set an alternative background color
        //chart.setBackgroundColor(Color.LTGRAY);

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
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        // Y axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);


        getRealTimeData();  // 실시간 데이터 받아오기

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
        locationTextView = findViewById(R.id.location);
        locationTextView.setText(locations.get(0).getName());

        temTextView = findViewById(R.id.value_tem);
        humTextView = findViewById(R.id.value_hum);
        coTextView = findViewById(R.id.value_co);
        ch4TextView = findViewById(R.id.value_ch4);
        updateTextView = findViewById(R.id.txt_update);

        // Timer text
        timerTextView = findViewById(R.id.txt_timer);
        MainTimerTask timerTask = new MainTimerTask();
        timer = new Timer();
        timer.schedule(timerTask, 500, 1000);
    }

    private String getCurrentDate() {
        Date rightNow = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy.MM.dd hh:mm:ss ");
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

    // 실시간 데이터 안올때를 대비해서 만든 클래스
    class AddEntryTask implements Runnable {
        int from;
        final int size = 1;

        public AddEntryTask(int from) {
            this.from = from;
        }

        @Override
        public void run() {
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

                                // 그래프에 데이터 추가
                                switch (dataType) {
                                    case TYPE_CO:
                                       //addEntry(sensorData.getCO());
                                        break;
                                    case TYPE_CH4:
                                        break;
                                    default:
                                        break;
                                }

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
    }

    private LineDataSet createSet() {
        // 그래프 변수 하나 설정
        LineDataSet set = new LineDataSet(null, "일산화탄소(CO)");
        set.setAxisDependency(AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(Color.LTGRAY);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    private void addEntry(double yValue) {
        LineData data = chart.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }


            //data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 40) + 30f), 0);
            data.addEntry(new Entry(set.getEntryCount(), (float) yValue), 0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            chart.notifyDataSetChanged();

            // limit the number of visible entries
            chart.setVisibleXRangeMaximum(120);
            // chart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            chart.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            // chart.moveViewTo(data.getXValCount()-7, 55f,
            // AxisDependency.LEFT);
        }
    }

    private void setCurrentData(SensorData sensorData) {
        temTextView.setText(getResources().getString(R.string.prompt_tem) + " "
                + sensorData.getTem() + getResources().getString(R.string.unit_tem));
        humTextView.setText(getResources().getString(R.string.prompt_hum) + " "
                + sensorData.getHum() + getResources().getString(R.string.unit_hum));
        coTextView.setText(getResources().getString(R.string.prompt_co) + " "
                + sensorData.getCO() + getResources().getString(R.string.unit_ppm));
        ch4TextView.setText(getResources().getString(R.string.prompt_ch4) + " "
                + sensorData.getCH4() + getResources().getString(R.string.unit_ppm));
    }

    private void setUpdateTime(SensorData sensorData) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy.MM.dd hh:mm:ss ");
        String dateString = formatter.format(sensorData.getDate());
        updateTextView.setText(getResources().getString(R.string.prompt_update) + " " + dateString);
    }

    private Thread thread;

    private Runnable getRunnable(final int from) {
        final int size = 1;   // 항상 최신 데이터를 가져와서 넣음
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
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
                                    switch (dataType) {
                                        case TYPE_CO:
                                            addEntry(sensorData.getCO());
                                            break;
                                        case TYPE_CH4:
                                            break;
                                        default:
                                            break;
                                    }

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
        };
        return runnable;

    }

    private void getRealTimeData() {
        if (thread != null)
            thread.interrupt();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Don't generate garbage runnables inside the loop.
                for (int i = 60; i >= 0; i--) {
                    //runOnUiThread(getRunnable(i));
                    runOnUiThread(getRunnable(i));
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    private void showValue(boolean showValue){
        List<ILineDataSet> sets = chart.getData()
                .getDataSets();

        for (ILineDataSet iSet : sets) {
            LineDataSet set = (LineDataSet) iSet;
            set.setDrawValues(showValue);
        }

        chart.invalidate();
    }

    /*
    @Override
    protected void saveToGallery() {
        saveToGallery(chart, "RealtimeLineChartActivity");
    }
*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
        showValue(true);
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
        showValue(false);
    }

}
