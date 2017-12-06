package br.ufrn.imd.behere.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.utils.Constants;
import br.ufrn.imd.behere.utils.Get;

/**
 * An Abstract Activity with common configurations for all activities.
 */
public abstract class CustomActivity extends AppCompatActivity {
    protected final String apiKey = "YPxnOscwdxcBmsd2cVioAHmRRLk6lfBgmmnpBk3d";
    protected SharedPreferences prefs;
    protected Long idUser;
    protected Long linkValue;
    //protected boolean isLogged = false;
    private String TAG = LinkActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.action_bar_layout);
        }
        setup();
    }

    private void setup() {

    }

    protected void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.menu_logout) {
            clearPrefs();
            clearCookies();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void clearPrefs() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    public void clearCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(null);
    }

    public class ServiceTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = params[0];
            String accessToken = params[1];

            Get get = new Get();

            String req_url = Constants.BASE_URL + url;
            String jsonStr = null;
            try {
                jsonStr = get.serviceCall(req_url, accessToken, apiKey);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonStr != null) {
                try {
                    JSONObject resp = new JSONObject(jsonStr);
                    return resp;
                } catch (JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                idUser = jsonObject.getLong("id-usuario");
                toast(jsonObject.getString("nome-pessoa"));
                        /*jsonObject.getLong("id-unidade"),
                        jsonObject.getLong("id-foto"),
                        jsonObject.getBoolean("ativo"),
                        jsonObject.getString("login"),
                        jsonObject.getString("nome-pessoa"),
                        jsonObject.getString("cpf-cnpj"),
                        jsonObject.getString("email"),
                        jsonObject.getString("chave-foto"));*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
