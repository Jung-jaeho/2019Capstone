package jaeho.jaehoserver.data;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton Class
 * DataSource 란 데이터를 가져오는 클래스로 데이터를 가져와서 위임만 해주고 그 이후에는 관여하지 않는다.
 * 원래는 api도 분리해서 외부에서 주입받아야 함
 */
public class JaehoDataSource {

    private JaehoDataSource() { //Singleton을 위한 private 생성자

    }

    private static final String url = "https://jaeho.dev/"; //서버로 요청할 url
    private static JaehoDataSource INSTANCE = null;//싱글톤 인스턴스

    /**
     * Retrofit2 api를 사용
     * interface를 정의후 그 구현체를 구현하여 사용한다.
     * @see jaeho.jaehoserver.data.JaehoApi
     */

    private JaehoApi api = new Retrofit.Builder()
            .baseUrl(url)
            //TODO 여기서 Rxjava 없애게 되면 아래 이 친구만 지우면 됨
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //Rxjava를 사용했기 때문에 필요함
            .addConverterFactory(GsonConverterFactory.create())//이거 없으면 Json Data Parsing error (Gson Converter)
            .build()
            .create(JaehoApi.class); //해당 interface 구체화

    /**
     * 외부에서 DataSource로 데이터를 요청하기 위한 함수
     * @return 데이터가 담긴 Single<String> 스트림을 반환한다.
     */
    //TODO Single<String> 을 원하는 형태로 변경
    public Single<String> getResponse() {
        return api.getResponse()
                .subscribeOn(Schedulers.io());
    }

    public static JaehoDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JaehoDataSource();
        }
        return INSTANCE;
    }

}
