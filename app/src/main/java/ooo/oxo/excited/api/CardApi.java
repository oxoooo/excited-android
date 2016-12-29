package ooo.oxo.excited.api;

import ooo.oxo.excited.model.Message;
import retrofit2.Response;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by zsj on 2016/10/18.
 */

public interface CardApi {

    @POST("cards/{id}/report")
    Observable<Response<Message>> report(@Path("id") String itemId);

}
