package jaeho.jaehoserver.presentation;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import jaeho.jaehoserver.MainContract;
import jaeho.jaehoserver.data.JaehoDataSource;

/**
 * Presenter
 * View와 1:1로 강한 의존관계를 보이지만 Model과는 완벽하게 분리된다.
 * 비즈니스 로직이 포함되는 부분
 */
public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;
    private JaehoDataSource dataSource; //이놈이 Model이라고 보면 됨 나중에 통신이 늘어나면 Repository로 감싸서 관리
    //즉 Repository 안에 여러개의 Datasource가 들어간다고 생각하면 댐
    private Disposable disposable;//Rxjava 개념이라 알 필요 없음

    public MainPresenter(MainContract.View view, JaehoDataSource dataSource) {
        this.view = view;
        this.dataSource = dataSource;
    }

    @Override
    public void clickedButton() {
        //Model에 데이터를 요청후 데이터가 어케 날라오는지 알 필요 없이 받아서 VIew에게 알려만 준다.
        dataSource.getResponse()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d; //알 필요없음
                    }

                    @Override
                    public void onSuccess(String s) {
                        //성공시 데이터 날라오는 콜백
                        view.showText(s);
                        view.showToast(s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        //오류시 데이터 날라오는 콜백
                        view.showError(e.getMessage());
                    }
                });
    }

    @Override
    public void clearDisposable() {
        if (!disposable.isDisposed())
            disposable.dispose();
    }
}
