package ann.example.airpollutionmonitor.View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ann.example.airpollutionmonitor.AppManager;
import ann.example.airpollutionmonitor.Model.Location;
import ann.example.airpollutionmonitor.R;

public class AddLocationActivity extends AppCompatActivity {
    boolean isAccepted = false;
    EditText serialEdtTxt, nameEdtTxt;
    String serialNumber="", placeName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  // 타이틀바 없애기
        setContentView(R.layout.activity_add_location);

        initView();
    }

    private void initView(){
        TextView cancelBtn = findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView okBtn = findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeName = nameEdtTxt.getText().toString();
                if(!isAccepted){
                    // 인증되지 않은 경우
                    Toast.makeText(getApplicationContext(), "인증되지 않은 시리얼 번호 입니다. 시리얼 번호를 입력 후 인증 버튼을 눌러주세요.", Toast.LENGTH_LONG).show();
                }else{
                    // 시리얼 번호가 인증된 경우
                    Toast.makeText(getApplicationContext(), "'" + placeName + "'를 모니터링 장소에 추가하였습니다.", Toast.LENGTH_LONG).show();
                    Location location = new Location(serialNumber, placeName);
                    AppManager.getInstance().getLocations().add(location);  // 장소 추가
                    finish();
                }
            }
        });

        final TextView message = findViewById(R.id.message);
        Button certificateBtn = findViewById(R.id.btn_cetificate);
        certificateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkSerialNumber();  // 인증 절차
                message.setVisibility(View.VISIBLE);
                if(isAccepted){
                    message.setText("인증되었습니다.");
                    message.setTextColor(getResources().getColor(R.color.colorBlue));
                } else{
                    message.setText("인증에 실패하였습니다.");
                    message.setTextColor(getResources().getColor(R.color.colorRed));
                    nameEdtTxt.setText("");  // 장소명 초기화
                }
            }
        });

        serialEdtTxt = findViewById(R.id.edtxt_serial);
        nameEdtTxt = findViewById(R.id.edtxt_name);
    }

    void checkSerialNumber(){
        // 시리얼 넘버 인증
        serialNumber = serialEdtTxt.getText().toString();

        isAccepted = false;
        for(String[] serial : AppManager.getInstance().SERIALS){
            if(serialNumber.equals(serial[0])){
                isAccepted = true;
                nameEdtTxt.setText(serial[1]);  // 장소명 자동 기입
                break;
            }
        }
    }
}
