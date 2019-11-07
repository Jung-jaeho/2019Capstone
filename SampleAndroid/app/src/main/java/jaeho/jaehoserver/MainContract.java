package jaeho.jaehoserver;

/**
 * MVP Pattern의 Contract
 * interface의 집합으로 이거만 보면 대충 뭘 하는지 알 수 있음
 * 해당 인터페이스를 구현하여 접근함으로 결합을 느슨하게 한다.
 */
public interface MainContract {
    interface View {
        void showText(String text);

        void showError(String error);

        void showToast(String string);
    }

    interface Presenter {
        void clickedButton();

        void clearDisposable();
    }
}
