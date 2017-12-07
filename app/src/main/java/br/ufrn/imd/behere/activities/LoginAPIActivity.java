package br.ufrn.imd.behere.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.utils.Constants;
import br.ufrn.imd.behere.utils.WebService;
import ca.mimic.oauth2library.OAuth2Client;
import ca.mimic.oauth2library.OAuthResponse;

import static br.ufrn.imd.behere.utils.Constants.CLIENT_ID_VALUE;

public class LoginAPIActivity extends CustomActivity {

    public static final String QUESTION_MARK = "?";
    public static final String AMPERSAND = "&";
    public static final String EQUALS = "=";
    private WebView wvLoginAPI;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_api);
        setup();
    }

    public void setup() {
        wvLoginAPI = (WebView) findViewById(R.id.wv_login_api);
        wvLoginAPI.requestFocus(View.FOCUS_DOWN);

        progressDialog = ProgressDialog.show(this, "", "Loading", true);

        wvLoginAPI.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String authorizationUrl) {
                if (authorizationUrl.startsWith(Constants.REDIRECT_URI)) {
                    Uri uri = Uri.parse(authorizationUrl);

                    String authorizationToken = uri.getQueryParameter(Constants.RESPONSE_TYPE_VALUE);
                    if (authorizationToken == null) {
                        return true;
                    }

                    new PostRequestAsyncTask().execute(authorizationToken);
                } else {
                    wvLoginAPI.loadUrl(authorizationUrl);
                }

                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });

        String authUrl = getAuthorizationUrl();
        wvLoginAPI.loadUrl(authUrl);
    }

    private static String getAuthorizationUrl() {
        return Constants.AUTHORIZATION_URL + QUESTION_MARK + Constants.RESPONSE_TYPE_PARAM + EQUALS + Constants.RESPONSE_TYPE_VALUE +
               AMPERSAND + Constants.CLIENT_ID_PARAM + EQUALS + CLIENT_ID_VALUE + AMPERSAND + Constants.REDIRECT_URI_PARAM + EQUALS +
               Constants.REDIRECT_URI;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private class PostRequestAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            OAuth2Client client;
            Map<String, String> map = new HashMap<>();
            map.put(Constants.REDIRECT_URI_PARAM, Constants.REDIRECT_URI);
            map.put(Constants.RESPONSE_TYPE_VALUE, strings[0]);

            client = new OAuth2Client.Builder(Constants.CLIENT_ID_VALUE, Constants.SECRET_KEY, Constants.ACCESS_TOKEN_URL).grantType(
                    Constants.GRANT_TYPE).parameters(map).build();

            try {
                OAuthResponse response = client.requestAccessToken();
                if (response.isSuccessful()) {
                    savePreferences(response);
                    return true;
                }
            } catch (IOException e) {
                Log.e(TAG, "doInBackground: Error on requestAccessToken()", e);
            }

            return false;
        }        @Override
        protected void onPreExecute() {
            ProgressDialog.show(LoginAPIActivity.this, "", "Loading", true);
        }

        private void savePreferences(OAuthResponse response) {
            String accessToken = response.getAccessToken();
            String refreshToken = response.getRefreshToken();
            Long expiresIn = response.getExpiresIn();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Constants.KEY_ACCESS_TOKEN, accessToken);
            editor.putString(Constants.KEY_REFRESH_TOKEN, refreshToken);
            editor.putLong(Constants.KEY_EXPIRES_IN, expiresIn);
            editor.apply();
        }

        @Override
        protected void onPostExecute(Boolean status) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if (status) {
                final String accessToken = prefs.getString(Constants.KEY_ACCESS_TOKEN, null);
                new UserInfoTask().execute(accessToken);
            } else {
                toast("Some Error Occurred");
            }
        }


    }

    public class UserInfoTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = "usuario/v0.1/usuarios/info";
            String accessToken = params[0];

            WebService get = new WebService();

            String reqUrl = Constants.BASE_URL + url;
            String jsonStr = null;
            try {
                jsonStr = get.get(reqUrl, accessToken, API_KEY);
            } catch (IOException e) {
                Log.e(TAG, "doInBackground: Error on serviceCall", e);
            }

            JSONObject resp = null;
            if (jsonStr != null) {
                try {
                    resp = new JSONObject(jsonStr);
                } catch (JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }

            return resp;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                long userId = jsonObject.getLong("id-usuario");
                // toast(jsonObject.getString("nome-pessoa") + ": " + userId);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("user_id", userId);
                editor.apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(LoginAPIActivity.this, LinkActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
