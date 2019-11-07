package jaeho.jaehoserver.data;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface JaehoApi {
    @GET("api/test/")
    Single<String> getSomething();

}
