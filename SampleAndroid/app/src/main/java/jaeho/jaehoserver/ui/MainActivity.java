package jaeho.jaehoserver.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import jaeho.jaehoserver.MainContract;
import jaeho.jaehoserver.R;
import jaeho.jaehoserver.data.JaehoDataSource;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private Disposable disposable;
    private TextView error_text;
    private TextView text;
    private JaehoDataSource dataSource = JaehoDataSource.getInstance();

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
                        dataSource.getResponse()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new SingleObserver<String>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        disposable = d;
                                    }

                                    @Override
                                    public void onSuccess(String s) {
                                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                                        text.setText(s);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        error_text.setText(e.getMessage());
                                    }
                                });
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        if (!disposable.isDisposed()) //super.onDestroy()는 앱 종료를 시키기 때문에 위에 선언해주는게 맞다.
            disposable.dispose();
        super.onDestroy();
    }
}

