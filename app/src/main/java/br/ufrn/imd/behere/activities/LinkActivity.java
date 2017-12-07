package br.ufrn.imd.behere.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.adapters.LinkRecyclerAdapter;
import br.ufrn.imd.behere.model.UserLink;
import br.ufrn.imd.behere.utils.Constants;
import br.ufrn.imd.behere.utils.DatabaseInstance;
import br.ufrn.imd.behere.utils.RecyclerViewClickListener;
import br.ufrn.imd.behere.utils.WebService;

public class LinkActivity extends CustomActivity implements RecyclerViewClickListener {
    private ArrayList<UserLink> userLinks;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private String accessToken;
    private String jsonStr;
    private String TAG = LinkActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);
        setup();
    }

    private void setup() {
        if (DatabaseInstance.dbHelper == null) {
            DatabaseInstance.createDBInstance(getApplicationContext());
        }

        recyclerView = (RecyclerView) findViewById(R.id.rv_links);
        userLinks = new ArrayList<>();

        fetchDataDB();

        adapter = new LinkRecyclerAdapter(userLinks, this);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        accessToken = prefs.getString(Constants.KEY_ACCESS_TOKEN, null);

        if (accessToken != null) {
            new ServiceTask().execute("usuario/v0.1/usuarios/info", accessToken);
        }

        if (accessToken != null) {
            new LinkTask().execute(accessToken);
        }

    }

    private void fetchDataDB() {
        /*final int loggedUser = prefs.getInt("logged_user_id", 1);
        Cursor cursor = DatabaseInstance.database.rawQuery(
                "SELECT LINK_TYPE FROM USER_LINKS WHERE USER=" + loggedUser, null);

        while (cursor.moveToNext()) {
            final int link = cursor.getInt(0);
            if (link == 1) {
                userLinks.add(new UserLink("PROFESSOR", "Digital Metropolis Institute"));
            } else {
                userLinks.add(new UserLink("STUDENT", "Digital Metropolis Institute"));
            }
        }
        cursor.close();*/
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        Intent intent;
        SharedPreferences.Editor editor = prefs.edit();
        if (userLinks.get(position).getType().equals(UserLink.LinkType.STUDENT)) {
            intent = new Intent(this, StudentChooseActivity.class);
            editor.putInt("link_id", userLinks.get(position).getId());
        } else {
            intent = new Intent(this, ProfessorSubjectActivity.class);
            editor.putInt("link_id", userLinks.get(position).getId());
        }
        editor.apply();

        startActivity(intent);
    }

    public class LinkTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            //Long idUser = prefs.getLong("id_user", 0);
            String url =
                    Constants.BASE_URL + "vinculo/v0.1/vinculos?ativo=true&id-usuario=" + idUser;
            String accessToken = params[0];

            WebService get = new WebService();
            JSONArray resp = null;

            try {
                jsonStr = get.get(url, accessToken, apiKey);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonStr != null) {
                try {
                    resp = new JSONArray(jsonStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }
            return resp;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            if (jsonArray == null || jsonArray.length() == 0) {
                TextView txtEmptyLinks = (TextView) findViewById(R.id.tv_empty_links);
                txtEmptyLinks.setVisibility(View.VISIBLE);
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject respLink = jsonArray.getJSONObject(i);
                        if (respLink.getString("id-tipo-vinculo").equals("1")) {
                            UserLink links = new UserLink(respLink.getInt("id-vinculo"), getString(R.string.student_link), UserLink.LinkType.STUDENT, respLink.getString("lotacao"));
                            userLinks.add(links);
                        } else if (respLink.getString("id-tipo-vinculo").equals("2")) {
                            UserLink links = new UserLink(respLink.getInt("id-vinculo"), getString(R.string.professor_link), UserLink.LinkType.PROFESSOR, respLink.getString("lotacao"));
                            userLinks.add(links);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
