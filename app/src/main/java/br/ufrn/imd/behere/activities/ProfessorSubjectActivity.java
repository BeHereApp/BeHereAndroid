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
import java.util.Calendar;
import java.util.List;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.adapters.SubjectRecyclerAdapter;
import br.ufrn.imd.behere.model.Subject;
import br.ufrn.imd.behere.utils.Constants;
import br.ufrn.imd.behere.utils.DatabaseInstance;
import br.ufrn.imd.behere.utils.RecyclerViewClickListener;
import br.ufrn.imd.behere.utils.WebService;

public class ProfessorSubjectActivity extends CustomActivity implements RecyclerViewClickListener {

    public static final String TAG = LinkActivity.class.getSimpleName();
    private ArrayList<Subject> professorSubjects;
    private String jsonStr;
    private RecyclerView.Adapter adapter;
    private ProgressDialog progressDialog;
    private long idProfessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_subject);
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

        idProfessor = prefs.getLong("link_id", 0);

        RecyclerView recyclerView = findViewById(R.id.rv_subjects);
        professorSubjects = new ArrayList<>();

        // Fetch subjects from Database
        fetchDataDB();

        adapter = new SubjectRecyclerAdapter(professorSubjects, this);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        final String accessToken = prefs.getString(Constants.KEY_ACCESS_TOKEN, null);
        if (accessToken != null) {
            if (professorSubjects.isEmpty()) {
                progressDialog = ProgressDialog.show(this, "", "Loading", true);
            }
            new SubjectTask().execute(accessToken);
        }

        // Adds back arrow to layout
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void fetchDataDB() {
        Cursor cursor = DatabaseInstance.databaseRead.rawQuery(
                "SELECT SUBJECTS.ID, SUBJECTS.NAME, SUBJECTS.SCHEDULE, SUBJECTS.LOCATION FROM USER_SUBJECTS INNER JOIN SUBJECTS ON USER_SUBJECTS.SUBJECT=SUBJECTS.ID WHERE USER_SUBJECTS.USER=" +
                idProfessor, null);

        while (cursor.moveToNext()) {
            final Long id = cursor.getLong(0);
            final String name = cursor.getString(1);
            final String schedule = cursor.getString(2);
            final String location = cursor.getString(3);
            professorSubjects.add(new Subject(id, schedule, name, location));
        }
        Log.i(TAG, "fetchDataDB: " + professorSubjects.size() + " subjects fetched from database");
        cursor.close();
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("selected_subject", professorSubjects.get(position).getId());
        editor.apply();
        Intent intent = new Intent(this, ProfessorChooseActivity.class);
        startActivity(intent);
    }

    public class SubjectTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            int currYear = Calendar.getInstance().get(Calendar.YEAR);
            String url = Constants.BASE_URL + "turma/v0.1/turmas?id-docente=" + idProfessor + "&ano=" + currYear + "&id-situacao-turma=1";
            String accessToken = params[0];

            WebService get = new WebService();
            JSONArray resp = null;

            try {
                jsonStr = get.get(url, accessToken, API_KEY);
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

            ArrayList<Subject> newProfessorSubjects = new ArrayList<>();

            if (jsonArray.length() == 0) {
                TextView txtEmptySubjects = findViewById(R.id.tv_empty_subjects);
                txtEmptySubjects.setVisibility(View.VISIBLE);
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject respSubject = jsonArray.getJSONObject(i);
                        Subject subject = new Subject(respSubject.getLong("id-turma"), respSubject.getString("descricao-horario"),
                                respSubject.getString("codigo-componente") + " - " + respSubject.getString("nome-componente"),
                                respSubject.getString("local"));
                        newProfessorSubjects.add(subject);
                    } catch (JSONException e) {
                        Log.e(TAG, "onPostExecute: Error on JSON", e);
                    }
                }

                professorSubjects.clear();
                professorSubjects.addAll(newProfessorSubjects);
                adapter.notifyDataSetChanged();
                updateDatabase(newProfessorSubjects);
            }
        }

        private void updateDatabase(List<Subject> professorSubjects) {
            SQLiteDatabase db = DatabaseInstance.databaseWrite;

            // Clear current entries
            db.execSQL("DELETE FROM USER_SUBJECTS WHERE USER=" + idProfessor);

            // Add new entries
            for (Subject subject : professorSubjects) {
                ContentValues subjectValues = new ContentValues();
                subjectValues.put("ID", subject.getId());
                subjectValues.put("SCHEDULE", subject.getSchedule());
                subjectValues.put("NAME", subject.getName());
                subjectValues.put("LOCATION", subject.getLocation());
                db.insertWithOnConflict("SUBJECTS", null, subjectValues, SQLiteDatabase.CONFLICT_IGNORE);

                ContentValues userSubjectValues = new ContentValues();
                userSubjectValues.put("USER", idProfessor);
                userSubjectValues.put("SUBJECT", subject.getId());
                db.insertWithOnConflict("USER_SUBJECTS", null, userSubjectValues, SQLiteDatabase.CONFLICT_IGNORE);
            }
        }
    }
}
