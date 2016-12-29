package ooo.oxo.excited.api;

import okhttp3.MultipartBody;
import ooo.oxo.excited.model.ImageResult;
import retrofit2.Response;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by seasonyuu on 2016/12/7.
 */

public interface ImageAPI {

    @Multipart
    @POST("image")
    Observable<Response<ImageResult>> post(@Part MultipartBody.Part body);
}
