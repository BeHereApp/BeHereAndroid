package br.ufrn.imd.behere.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.utils.WebService;

public class ProfessorResultActivity extends CustomActivity {

    public static final String PASSWORD_RESULT = "password_result";
    public static final String TIMEOUT_RESULT = "timeout_result";
    private ImageView imgResult;
    private TextView txtResult;
    private Button btnResult;
    private String password;
    private String strTimeout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_result);
        setup();
    }

    public void setup() {
        Intent intent = getIntent();

        imgResult = findViewById(R.id.img_professor_result);
        txtResult = findViewById(R.id.txt_professor_result);
        btnResult = findViewById(R.id.btn_professor_result);

        password = intent.getStringExtra(PASSWORD_RESULT);
        strTimeout = intent.getStringExtra(TIMEOUT_RESULT);

        imgResult.setImageResource(R.drawable.positive_result);
        txtResult.setText(R.string.result_success);
        btnResult.setText(R.string.result_ok);

        new AttendanceTask().execute();
    }

    public void performProfessorResult(View v) {
        Intent intent = new Intent(this, ProfessorChooseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void performStudentList(View v) {
        Intent intent = new Intent(this, StudentListActivity.class);
        startActivity(intent);
        finish();
    }

    public class AttendanceTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            String url = "https://behereapi-eltonvs1.c9users.io/api/attendances/";
            Long currentDate = System.currentTimeMillis();
            Long class_id = prefs.getLong("selected_subject", 0);
            Long userId = prefs.getLong("user_id", 0);

            WebService post = new WebService();

            try {
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("date", currentDate.toString())
                        .appendQueryParameter("professor_id", userId.toString())
                        .appendQueryParameter("attendance_id", "4")
                        .appendQueryParameter("class_id", class_id.toString())
                        .appendQueryParameter("starting_date", currentDate.toString())
                        .appendQueryParameter("timeout", strTimeout)
                        .appendQueryParameter("password", password);
                post.sendPost(url, builder.build().getEncodedQuery(), ProfessorResultActivity.this);
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: ", e);
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
