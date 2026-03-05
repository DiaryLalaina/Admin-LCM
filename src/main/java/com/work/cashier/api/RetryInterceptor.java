package com.work.cashier.api;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RetryInterceptor implements Interceptor {
    private final int maxRetries;

    public RetryInterceptor(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        IOException lastException = null;

        for (int tryCount = 0; tryCount < maxRetries; tryCount++) {
            try {
                return chain.proceed(request);
            } catch (IOException e) {
                lastException = e;
                System.out.println("Tentative " + (tryCount + 1) + " échouée : " + e.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }
        }

        throw lastException;
    }
}

