package br.ufrn.imd.behere.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.adapters.SubjectRecyclerAdapter;
import br.ufrn.imd.behere.model.Subject;
import br.ufrn.imd.behere.utils.Constants;
import br.ufrn.imd.behere.utils.DatabaseInstance;
import br.ufrn.imd.behere.utils.Get;
import br.ufrn.imd.behere.utils.RecyclerViewClickListener;

public class ProfessorSubjectActivity extends CustomActivity implements RecyclerViewClickListener {

    private ArrayList<Subject> professorSubjects;
    private String TAG = LinkActivity.class.getSimpleName();
    private String jsonStr;
    private String accessToken;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_subject);
        setup();
    }

    private void setup() {
        if (DatabaseInstance.dbHelper == null) {
            DatabaseInstance.createDBInstance(getApplicationContext());
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_subjects);
        professorSubjects = new ArrayList<>();

        // Fetch subjects from Database
        //fetchDataDB();

        adapter = new SubjectRecyclerAdapter(professorSubjects, this);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        accessToken = prefs.getString(Constants.KEY_ACCESS_TOKEN, null);
        if (accessToken != null) {
            new SubjectTask().execute(accessToken);
        }

        // Adds back arrow to layout
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void fetchDataDB() {
        final int loggedUser = prefs.getInt("logged_user_id", 0);
        Cursor cursor = DatabaseInstance.databaseRead.rawQuery(
                "SELECT SUBJECTS.ID, SUBJECTS.NAME, SUBJECTS.SCHEDULE, SUBJECTS.LOCATION FROM USER_SUBJECTS INNER JOIN SUBJECTS ON USER_SUBJECTS.SUBJECT=SUBJECTS.ID WHERE USER_SUBJECTS.USER=" +
                loggedUser, null);

        while (cursor.moveToNext()) {
            final int id = cursor.getInt(0);
            final String name = cursor.getString(1);
            final String schedule = cursor.getString(2);
            final String location = cursor.getString(3);
            professorSubjects.add(new Subject(id, schedule, name, location));
        }
        cursor.close();
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("selected_subject", professorSubjects.get(position).getId());
        editor.apply();
        Intent intent = new Intent(this, ProfessorChooseActivity.class);
        startActivity(intent);
    }

    public class SubjectTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            int idProfessor = prefs.getInt("link_id", 1);
            String url = Constants.BASE_URL + "turma/v0.1/turmas?id-docente=" + idProfessor +
                         "&ano=2017&id-situacao-turma=1";
            String accessToken = params[0];

            Get get = new Get();
            JSONArray resp = null;

            try {
                jsonStr = get.serviceCall(url, accessToken, apiKey);
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
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject respSubject = jsonArray.getJSONObject(i);
                    Subject subject = new Subject(i, respSubject.getString("descricao-horario"),
                            respSubject.getString("codigo-componente") + " - " +
                            respSubject.getString("nome-componente"), respSubject.getString("local"));
                    professorSubjects.add(subject);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
