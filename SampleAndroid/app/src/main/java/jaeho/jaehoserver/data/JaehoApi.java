package jaeho.jaehoserver.data;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface JaehoApi {
    @GET("api/test/")
    Single<String> getResponse();
    /*TODO Single은 rxjava의 개념이므로 반환값을 변경하면 됨
       관련 사용법은 Retrofit2 검색하면 많이 나오니까 찾아보면 됨*/

}
