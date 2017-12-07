package br.ufrn.imd.behere.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.utils.WebService;

public class ProfessorPasswordActivity extends CustomActivity {

    private EditText etPassword;
    private EditText etTimeout;
    private String password;
    private String strTimeout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_password);

        etPassword = (EditText) findViewById(R.id.et_password);
        etTimeout = (EditText) findViewById(R.id.et_timeout);

        // Adds back arrow to layout
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void setPassword(View v) {
        password = etPassword.getText().toString();
        strTimeout = etTimeout.getText().toString();

        if (!strTimeout.isEmpty() && !password.isEmpty()) {
            final Integer timeout = Integer.parseInt(strTimeout);
            etPassword.setText("");
            etTimeout.setText("");
            new AttendanceTask().execute();

            Intent intent = new Intent(this, ProfessorResultActivity.class);
            intent.putExtra(ProfessorResultActivity.PASSWORD_RESULT, password);
            intent.putExtra(ProfessorResultActivity.TIMEOUT_RESULT, timeout);
            startActivity(intent);
            finish();
        }
    }

    public class AttendanceTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            String url = "https://behereapi-eltonvs1.c9users.io/api/attendances/";
            long currentDate = System.currentTimeMillis();
            String class_id = prefs.getString("selected_subject", "");
            String idUser = prefs.getString("id_user", "");

            WebService post = new WebService();

            try {
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("date", String.valueOf(currentDate))
                        .appendQueryParameter("professor_id", idUser)
                        .appendQueryParameter("attendance_id", "2")
                        .appendQueryParameter("class_id", class_id)
                        .appendQueryParameter("starting_date", String.valueOf(currentDate))
                        .appendQueryParameter("timeout", strTimeout)
                        .appendQueryParameter("password", password);
                post.sendPost(url, builder.build().getEncodedQuery(), ProfessorPasswordActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
