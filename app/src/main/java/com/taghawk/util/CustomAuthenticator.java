package com.taghawk.util;

import com.taghawk.data.DataManager;
import com.taghawk.data.api.ApiManager;
import com.taghawk.model.refreshtoken.RefreshTokenResponse;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;

public class CustomAuthenticator implements Authenticator {
    private static int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        if (responseCount(response) >= 2) {
            // If both the original call and the call with refreshed token failed,
            // it will probably keep failing, so don't try again.
            return null;
        }
        // We need a new client, since we don't want to make another call using our client with access token
        HashMap<String, String> params = new HashMap<>();
        params.put("refresh_token", DataManager.getInstance().getRefreshToken());
        params.put("name", DataManager.getInstance().getUserName());
        Call<RefreshTokenResponse> call = ApiManager.getInstance().refreshToken(params);
        retrofit2.Response<RefreshTokenResponse> execute = call.execute();
        if (execute.code() == 200) {
            String newAccessToken = execute.body().getRefreshTokenResult().getToken();
            String newRefreshToken = execute.body().getRefreshTokenResult().getRefreshToken();
            DataManager.getInstance().saveAccessToken(newAccessToken);
            DataManager.getInstance().saveRefreshToken(newRefreshToken);
            return response.request().newBuilder()
                    .header("Authorization", "Bearer " + newAccessToken)
                    .build();
        } else
            return null;

    }
}
