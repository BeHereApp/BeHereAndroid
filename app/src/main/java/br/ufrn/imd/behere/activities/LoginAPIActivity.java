package br.ufrn.imd.behere.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.utils.Constants;
import ca.mimic.oauth2library.OAuth2Client;
import ca.mimic.oauth2library.OAuthResponse;

import static br.ufrn.imd.behere.utils.Constants.CLIENT_ID_VALUE;

public class LoginAPIActivity extends CustomActivity {

    private WebView wvLoginAPI;
    private ProgressDialog progressDialog;
    public static final String QUESTION_MARK = "?";
    public static final String AMPERSAND = "&";
    public static final String EQUALS = "=";

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
            public void onPageFinished(WebView view, String url) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

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
        });

        String authUrl = getAuthorizationUrl();
        wvLoginAPI.loadUrl(authUrl);
    }

    private static String getAuthorizationUrl() {
        return Constants.AUTHORIZATION_URL + QUESTION_MARK + Constants.RESPONSE_TYPE_PARAM +
               EQUALS + Constants.RESPONSE_TYPE_VALUE + AMPERSAND + Constants.CLIENT_ID_PARAM +
               EQUALS + CLIENT_ID_VALUE + AMPERSAND + Constants.REDIRECT_URI_PARAM + EQUALS +
               Constants.REDIRECT_URI;
    }

    private class PostRequestAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            ProgressDialog.show(LoginAPIActivity.this, "", "Loading", true);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                OAuth2Client client;
                Map<String, String> map = new HashMap<>();
                map.put(Constants.REDIRECT_URI_PARAM, Constants.REDIRECT_URI);
                map.put(Constants.RESPONSE_TYPE_VALUE, strings[0]);

                client = new OAuth2Client.Builder(Constants.CLIENT_ID_VALUE, Constants.SECRET_KEY, Constants.ACCESS_TOKEN_URL).grantType(Constants.GRANT_TYPE).parameters(map).build();

                OAuthResponse response = client.requestAccessToken();
                if (response.isSuccessful()) {
                    savePreferences(response);
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean status) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if (status) {
                Intent intent = new Intent(LoginAPIActivity.this, LinkActivity.class);
                startActivity(intent);
                finish();
            }
        }
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
}
