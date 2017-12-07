package br.ufrn.imd.behere.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.model.Subject;
import br.ufrn.imd.behere.utils.Constants;
import br.ufrn.imd.behere.utils.DatabaseInstance;
import br.ufrn.imd.behere.utils.WebService;

public class StudentPasswordActivity extends CustomActivity {

    private EditText etPassword;
    private Spinner spSubjects;
    private ProgressDialog progressDialog;
    private List<Subject> studentSubjects;
    private long linkId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_password);

        setup();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void setup() {
        // Adds back arrow to layout
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        spSubjects = findViewById(R.id.sp_subjects);
        etPassword = findViewById(R.id.et_password);
        linkId = prefs.getLong("link_id", 0);

        studentSubjects = new ArrayList<>();
        fetchDataDB();
        refreshSpinnerItems();

        final String accessToken = prefs.getString(Constants.KEY_ACCESS_TOKEN, null);
        if (accessToken != null) {
            if (studentSubjects.isEmpty()) {
                progressDialog = ProgressDialog.show(this, "", "Loading", true);
            }
            new SubjectTask().execute(accessToken);
        }
    }

    private void fetchDataDB() {
        Cursor cursor = DatabaseInstance.databaseRead.rawQuery(
                "SELECT SUBJECTS.ID, SUBJECTS.NAME, SUBJECTS.SCHEDULE, SUBJECTS.LOCATION FROM USER_SUBJECTS INNER JOIN SUBJECTS ON USER_SUBJECTS.SUBJECT=SUBJECTS.ID WHERE USER_SUBJECTS.USER=" +
                linkId, null);

        while (cursor.moveToNext()) {
            final Long id = cursor.getLong(0);
            final String name = cursor.getString(1);
            final String schedule = cursor.getString(2);
            final String location = cursor.getString(3);
            studentSubjects.add(new Subject(id, schedule, name, location));
        }
        Log.i(TAG, "fetchDataDB: " + studentSubjects.size() + " subjects fetched from database");
        cursor.close();
    }

    private void refreshSpinnerItems() {
        final ArrayAdapter<Subject> subjectsAdapter = new ArrayAdapter<Subject>(this, R.layout.spinner_item, android.R.id.text1,
                studentSubjects) {
            @Override
            public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
                final View v = super.getDropDownView(position, convertView, parent);
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        ((TextView) v.findViewById(android.R.id.text1)).setSingleLine(false);
                    }
                });
                return v;
            }
        };
        subjectsAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spSubjects.setAdapter(subjectsAdapter);
    }

    public void enterPassword(View v) {
        Subject subject = (Subject) spSubjects.getSelectedItem();
        String userPassword = etPassword.getText().toString();

        if (!userPassword.isEmpty() && subject != null) {
            etPassword.setText("");
            Intent intent = new Intent(this, StudentResultActivity.class);
            intent.putExtra(StudentResultActivity.RESULT_PASS, userPassword);
            intent.putExtra(StudentResultActivity.CHOICE_TYPE, StudentResultActivity.TYPE_PASSWORD);
            intent.putExtra(StudentResultActivity.RESULT_SUBJECT, subject.getId());
            startActivity(intent);
            finish();
        }
    }

    public class SubjectTask extends AsyncTask<String, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... params) {
            int currYear = Calendar.getInstance().get(Calendar.YEAR);
            String url = Constants.BASE_URL + "turma/v0.1/turmas?id-discente=" + linkId + "&ano=" + currYear + "&id-situacao-turma=1";
            String accessToken = params[0];

            WebService get = new WebService();
            JSONArray resp = null;
            String jsonStr = null;

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

            if (jsonArray.length() > 0) {
                studentSubjects.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject respSubject = jsonArray.getJSONObject(i);
                        Subject subject = new Subject(respSubject.getLong("id-turma"), respSubject.getString("descricao-horario"),
                                respSubject.getString("codigo-componente") + " - " + respSubject.getString("nome-componente"),
                                respSubject.getString("local"));
                        studentSubjects.add(subject);
                    } catch (JSONException e) {
                        Log.e(TAG, "onPostExecute: Error on JSON", e);
                    }
                }

                refreshSpinnerItems();
                updateDatabase(studentSubjects);
            }
        }

        private void updateDatabase(List<Subject> subjects) {
            SQLiteDatabase db = DatabaseInstance.databaseWrite;

            // Clear current entries
            db.execSQL("DELETE FROM USER_SUBJECTS WHERE USER=" + linkId);

            // Add new entries
            for (Subject subject : subjects) {
                ContentValues subjectValues = new ContentValues();
                subjectValues.put("ID", subject.getId());
                subjectValues.put("SCHEDULE", subject.getSchedule());
                subjectValues.put("NAME", subject.getName());
                subjectValues.put("LOCATION", subject.getLocation());
                db.insertWithOnConflict("SUBJECTS", null, subjectValues, SQLiteDatabase.CONFLICT_IGNORE);

                ContentValues userSubjectValues = new ContentValues();
                userSubjectValues.put("USER", linkId);
                userSubjectValues.put("SUBJECT", subject.getId());
                db.insertWithOnConflict("USER_SUBJECTS", null, userSubjectValues, SQLiteDatabase.CONFLICT_IGNORE);
            }
        }
    }
}
