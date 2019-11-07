package jaeho.jaehoserver.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import jaeho.jaehoserver.MainContract;
import jaeho.jaehoserver.R;
import jaeho.jaehoserver.data.JaehoDataSource;
import jaeho.jaehoserver.presentation.MainPresenter;

/**
 * View를 담당하는 MainActivity
 * Passive(수동적)하게 Presenter가 시키는 일만 하는 차칸친구 ^_^
 * 무슨 데이터가 날라오는지 알 필요가 없도록 구현되어있음
 */
public class MainActivity extends AppCompatActivity implements MainContract.View {
    /**
     * MainPresenter 는 MainActivity와 1:1 대응
     * 자기자신인 View와 DataSoruce를 내부에서 생성하지 않고 인자로 의존성을 주입받음으로
     * 집약관계를 유지한다. (두 객체간의 의존성을 느슨하게 만든다)
     * <p>
     * [참고]
     * 합성관계 (전체객체가 없어지면 부분객체도 없어진다.)
     * 집약관계 (전체객체가 없어져도 부분객체는 살아있다.)
     */

    private MainPresenter presenter = new MainPresenter(this, JaehoDataSource.getInstance());
    private TextView error_text;
    private TextView text;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        error_text = findViewById(R.id.error_text);
        text = findViewById(R.id.text);


        findViewById(R.id.button).setOnClickListener(//onCreate에서 한번만 호출 되서 리스너 붙을거니까 변수선언 안함
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.clickedButton();// Presenter에게 일을 시킨다.
                    }
                }
        );
    }

    @Override
    public void showText(String text) {
        this.text.setText(text);
    }

    @Override
    public void showError(String error) {
        error_text.setText(error);
    }

    @Override
    public void showToast(String string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        presenter.clearDisposable(); //super.onDestroy()는 앱 종료를 시키기 때문에 위에 선언해주는게 맞다.
        super.onDestroy();
    }
}

