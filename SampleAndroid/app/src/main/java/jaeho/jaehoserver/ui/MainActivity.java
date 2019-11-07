package jaeho.jaehoserver.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jaeho.jaehoserver.R;
import jaeho.jaehoserver.data.JaehoApi;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String url = "https://jaeho.dev/";

    private JaehoApi api = new Retrofit.Builder()
            .baseUrl(url)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JaehoApi.class);

    private Disposable disposable;
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
                        api.getSomething()
                                .subscribeOn(Schedulers.io())
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

