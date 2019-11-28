package ann.example.airpollutionmonitor.View;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import ann.example.airpollutionmonitor.AppManager;
import ann.example.airpollutionmonitor.BaseActivity;
import ann.example.airpollutionmonitor.Controller.MonitorDataSource;
import ann.example.airpollutionmonitor.R;

public class MonitorTestActivity extends BaseActivity {
    private MonitorDataSource monitorDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_test);
        final TextView textView = findViewById(R.id.text);    // 임시로 응답 보여줄 텍뷰
        textView.setText(AppManager.getInstance().getSensorData().getStrDate());
        monitorDataSource = MonitorDataSource.getInstance();

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                monitorDataSource.getStreamJson()
                        .enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                // retrofit 통신이 성공했을 때
                                String str = response.body();
                                Log.d("test", str);
                                textView.setText(str);
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                // retrofit 통신이 실패했을 때
                                textView.setText("통신이 실패하였습니다.");
                            }
                        });
                 */
            }
        });

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

        TextView title = findViewById(R.id.title);
        title.setText(R.string.menu_monitor);
    }
}

