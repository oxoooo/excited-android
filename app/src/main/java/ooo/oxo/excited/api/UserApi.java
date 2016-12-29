package ooo.oxo.excited.api;

import java.util.HashMap;

import ooo.oxo.excited.model.Notices;
import ooo.oxo.excited.model.User;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import rx.Observable;

/**
 * Created by zsj on 2016/10/17.
 */

public interface UserApi {

    @POST("/user")
    Observable<Response<Notices>> getCode(@Body HashMap<String, String> body);

    @PUT("/user")
    Observable<Response<User>> verifyUser(@Body HashMap<String, String> body);

}
