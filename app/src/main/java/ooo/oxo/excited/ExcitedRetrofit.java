package ooo.oxo.excited;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import ooo.oxo.excited.model.NewData;
import ooo.oxo.excited.utils.PreferenceManager;
import ooo.oxo.excited.utils.UUIDUtils;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static ooo.oxo.excited.LoginActivity.RANDOM_UUID;
import static ooo.oxo.excited.LoginActivity.TOKEN;

/**
 * Created by zsj on 2016/10/17.
 */

public class ExcitedRetrofit {

    private Map<Class, Object> apis = new HashMap<>();

    private Retrofit retrofit;

    private Gson gson;

    public ExcitedRetrofit(final Context context) {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.addInterceptor(chain -> {
            Request originalRequest = chain.request();

            String token = PreferenceManager.getValue(context, TOKEN);
            String uuid = PreferenceManager.getValue(context, RANDOM_UUID);

            if (uuid == null) {
                uuid = UUIDUtils.saveUUID(context);
            }

            Request.Builder authorisedBuilder = originalRequest.newBuilder()
                    .header("Accept", "application/vnd.api+json")
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("X-Device-Token", uuid)
                    .header("Accept-Language", getHttpAcceptLanguage());

            if (token != null) {
                authorisedBuilder.header("X-TOKEN", token);
            }

            return chain.proceed(authorisedBuilder.build());
        });

        gson = new GsonBuilder()
                .registerTypeAdapter(NewData.class, new NewDataDeserializer())
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    private class NewDataDeserializer implements JsonDeserializer<NewData> {

        @Override
        public NewData deserialize(JsonElement json, Type typeOfT,
                                   JsonDeserializationContext context) throws JsonParseException {
            String jsonString = json.getAsJsonObject().get("data").getAsJsonObject().toString();
            return new Gson().fromJson(jsonString, NewData.class);
        }
    }

    private static String getHttpAcceptLanguage() {
        Locale locale = Locale.getDefault();
        StringBuilder builder = new StringBuilder();
        addLocaleToHttpAcceptLanguage(builder, locale);
        if (!locale.equals(Locale.US)) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            addLocaleToHttpAcceptLanguage(builder, Locale.US);
        }
        return builder.toString();
    }

    private static void addLocaleToHttpAcceptLanguage(
            StringBuilder builder, Locale locale) {
        String language = locale.getLanguage();
        if (language != null) {
            builder.append(language);
            String country = locale.getCountry();
            if (country != null) {
                builder.append("-");
                builder.append(country);
            }
        }
    }

    public <T> T createApi(Class<T> service) {
        if (!apis.containsKey(service)) {
            T instance = retrofit.create(service);
            apis.put(service, instance);
        }

        return (T) apis.get(service);
    }
}
