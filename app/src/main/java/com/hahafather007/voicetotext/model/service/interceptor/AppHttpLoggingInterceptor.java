package com.hahafather007.voicetotext.model.service.interceptor;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.hahafather007.voicetotext.BuildConfig.DEBUG;

public class AppHttpLoggingInterceptor implements Interceptor {
    private final Interceptor delegate;

    public AppHttpLoggingInterceptor() {
        if (DEBUG) {
            final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            delegate = logging;
        } else {
            delegate = chain -> chain.proceed(chain.request());
        }
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        return delegate.intercept(chain);
    }
}
