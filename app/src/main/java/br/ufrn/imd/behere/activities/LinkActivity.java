package br.ufrn.imd.behere.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.List;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.adapters.LinkRecyclerAdapter;
import br.ufrn.imd.behere.model.UserLink;
import br.ufrn.imd.behere.utils.Constants;
import br.ufrn.imd.behere.utils.DatabaseInstance;
import br.ufrn.imd.behere.utils.Get;
import br.ufrn.imd.behere.utils.RecyclerViewClickListener;

public class LinkActivity extends CustomActivity implements RecyclerViewClickListener {
    public static final String TAG = LinkActivity.class.getName();
    private ArrayList<UserLink> userLinks;
    private RecyclerView.Adapter adapter;
    private ProgressDialog progressDialog;
    private String jsonStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);
        setup();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void setup() {
        if (DatabaseInstance.dbHelper == null) {
            DatabaseInstance.createDBInstance(getApplicationContext());
        }

        final RecyclerView recyclerView = findViewById(R.id.rv_links);
        userLinks = new ArrayList<>();

        fetchDataDB();

        adapter = new LinkRecyclerAdapter(userLinks, this);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        final String accessToken = prefs.getString(Constants.KEY_ACCESS_TOKEN, null);

        if (accessToken != null) {
            if (userLinks.isEmpty()) {
                progressDialog = ProgressDialog.show(this, "", "Loading", true);
            }
            new LinkTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, accessToken);
        }
    }

    private void fetchDataDB() {
        Cursor cursor = DatabaseInstance.databaseRead.rawQuery(
                "SELECT LINKS.ID, LINKS.TYPE, LINKS.DESCRIPTION FROM USER_LINKS INNER JOIN LINKS ON USER_LINKS.LINK=LINKS.ID WHERE USER_LINKS.USER=" +
                idUser, null);

        while (cursor.moveToNext()) {
            final int linkId = cursor.getInt(0);
            final UserLink.LinkType linkType =
                    cursor.getInt(1) == 1 ? UserLink.LinkType.STUDENT : UserLink.LinkType.PROFESSOR;
            final String linkDescription = cursor.getString(2);
            final String linkName = linkType ==
                                    UserLink.LinkType.PROFESSOR ? getString(R.string.professor_link) : getString(R.string.student_link);
            userLinks.add(new UserLink(linkId, linkName, linkType, linkDescription));
        }
        Log.i(TAG, "fetchDataDB: " + userLinks.size() + " links fetched from database");
        cursor.close();
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
        protected JSONArray doInBackground(String... params) {
            String url =
                    Constants.BASE_URL + "vinculo/v0.1/vinculos?ativo=true&id-usuario=" + idUser;
            String accessToken = params[0];

            Get get = new Get();
            JSONArray resp = null;

            try {
                jsonStr = get.serviceCall(url, accessToken, API_KEY);
            } catch (IOException e) {
                Log.e(TAG, "doInBackground: Error on serviceCall", e);
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

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if (jsonArray == null) {
                toast("Error");
                return;
            }

            ArrayList<UserLink> newUserLinks = new ArrayList<>();

            if (jsonArray.length() == 0) {
                TextView txtEmptyLinks = findViewById(R.id.tv_empty_links);
                txtEmptyLinks.setVisibility(View.VISIBLE);
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject respLink = jsonArray.getJSONObject(i);
                        if (respLink.getString("id-tipo-vinculo").equals("1")) {
                            UserLink links = new UserLink(respLink.getInt("id-vinculo"), getString(R.string.student_link), UserLink.LinkType.STUDENT, respLink.getString("lotacao"));
                            newUserLinks.add(links);
                        } else if (respLink.getString("id-tipo-vinculo").equals("2")) {
                            UserLink links = new UserLink(respLink.getInt("id-vinculo"), getString(R.string.professor_link), UserLink.LinkType.PROFESSOR, respLink.getString("lotacao"));
                            newUserLinks.add(links);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "onPostExecute: Error on JSON", e);
                    }
                }

                userLinks.clear();
                userLinks.addAll(newUserLinks);
                adapter.notifyDataSetChanged();
                updateDatabase(newUserLinks);
            }
        }

        private void updateDatabase(List<UserLink> userLinks) {
            SQLiteDatabase db = DatabaseInstance.databaseWrite;

            // Clear current entries
            db.execSQL("DELETE FROM USER_LINKS WHERE USER=" + idUser);

            // Add new entries
            for (UserLink link : userLinks) {
                ContentValues linkValues = new ContentValues();
                linkValues.put("ID", link.getId());
                linkValues.put("TYPE", link.getType() == UserLink.LinkType.PROFESSOR ? 2 : 1);
                linkValues.put("DESCRIPTION", link.getDescription());
                db.insertWithOnConflict("LINKS", null, linkValues, SQLiteDatabase.CONFLICT_IGNORE);

                ContentValues userLinkValues = new ContentValues();
                userLinkValues.put("USER", idUser);
                userLinkValues.put("LINK", link.getId());
                db.insertWithOnConflict("USER_LINKS", null, userLinkValues, SQLiteDatabase.CONFLICT_IGNORE);
            }

            Log.i(TAG, "updateDatabase: saving last update date -> " + System.currentTimeMillis());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong("last_update_links", System.currentTimeMillis());
            editor.apply();
        }
    }
}
