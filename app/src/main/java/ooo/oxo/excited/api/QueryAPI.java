package ooo.oxo.excited.api;

import ooo.oxo.excited.model.NewData;
import retrofit2.Response;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by seasonyuu on 2016/12/8.
 */

public interface QueryAPI {

    @POST("graphql")
    Observable<NewData> getData(@Query("query") String query);

    @POST("graphql")
    Observable<Response<NewData>> loadData(@Query("query") String query);

    @POST("graphql")
    Observable<NewData> mutation(@Query("query") String query);

}
