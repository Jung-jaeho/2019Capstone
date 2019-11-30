package ann.example.airpollutionmonitor.Controller;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MonitorAPI {
    @GET("api/test/")
    Call<String> getResponse();

    @GET("api/search/{serial}/{from}/{size}")
    Call<String> getJsonByIndex(@Path("serial")String serial
            , @Path("from")int from, @Path("size")int size);

    @GET("api/period/{serial}/{start_date}/{end_date}")
    Call<String> getJsonByDate(@Path("serial")String serial
            , @Path("start_date")String start_date, @Path("end_date")String end_date);
}
