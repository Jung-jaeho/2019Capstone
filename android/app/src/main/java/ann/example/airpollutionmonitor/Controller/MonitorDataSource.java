package ann.example.airpollutionmonitor.Controller;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 재영 주석:
 * Singleton Class
 * DataSource 란 데이터를 가져오는 클래스로 데이터를 가져와서 위임만 해주고 그 이후에는 관여하지 않는다.
 * 원래는 api도 분리해서 외부에서 주입받아야 함
 */
public class MonitorDataSource {
    private static final String url = "https://jaeho.dev/"; //서버로 요청할 url
    private static MonitorDataSource instance = null;

    private MonitorDataSource() {
    }

    public static MonitorDataSource getInstance(){
        if(instance == null)
            instance = new MonitorDataSource();
        return instance;
    }

    //TODO 응답 내용을 원하는 데이터 타입으로 변경
    public Call<String> getResponse(){
        // Retrofit Builder 생성 및 url, 변환 타입 설정
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        // Client 생성
        MonitorAPI api = retrofit.create(MonitorAPI.class);
        Call<String> call = api.getResponse();  // 통신

        return call;
    }

    public Call<String> getJsonByIndex(String serial, int from, int size){
        // Retrofit Builder 생성 및 url, 변환 타입 설정
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        // Client 생성
        MonitorAPI api = retrofit.create(MonitorAPI.class);
        Call<String> call = api.getJsonByIndex(serial, from, size);  // 통신

        return call;
    }

    public Call<String> getJsonByDate(String serial, String start_date, String end_date){
        // Retrofit Builder 생성 및 url, 변환 타입 설정
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        // Client 생성
        MonitorAPI api = retrofit.create(MonitorAPI.class);
        Call<String> call = api.getJsonByDate(serial, start_date, end_date);  // 통신

        return call;
    }
}
