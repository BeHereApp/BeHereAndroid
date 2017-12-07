package br.ufrn.imd.behere.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import br.ufrn.imd.behere.utils.Constants;
import br.ufrn.imd.behere.utils.Get;

/**
 * Created by elton on 07/12/17.
 */

public class LoadUserActivity extends CustomActivity {
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = ProgressDialog.show(LoadUserActivity.this, "", "Loading", true);
        final String accessToken = prefs.getString(Constants.KEY_ACCESS_TOKEN, null);
        new UserInfoTask().execute(accessToken);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public class UserInfoTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String url = "usuario/v0.1/usuarios/info";
            String accessToken = params[0];

            Get get = new Get();

            String reqUrl = Constants.BASE_URL + url;
            String jsonStr = null;
            try {
                jsonStr = get.serviceCall(reqUrl, accessToken, apiKey);
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject resp = null;
            if (jsonStr != null) {
                try {
                    resp = new JSONObject(jsonStr);
                    return resp;
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
                toast(jsonObject.getString("nome-pessoa") + ": " + userId);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("user_id", userId);
                editor.apply();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(LoadUserActivity.this, LinkActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
