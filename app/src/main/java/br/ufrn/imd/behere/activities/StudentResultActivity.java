package br.ufrn.imd.behere.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.utils.WebService;

public class StudentResultActivity extends CustomActivity {

    public static final String RESULT_PASS = "password_result";
    public static final String RESULT_SUBJECT = "subject_result";
    public static final String CHOICE_TYPE = "choice_type";
    public static final int TYPE_PASSWORD = 1;
    public static final int TYPE_QR_CODE = 2;

    private RelativeLayout layoutResult;
    private ImageView imgResult;
    private TextView txtResult;
    private Button btnResult;
    private boolean resultSuccess;
    private String password;
    private int type;
    private long subjectId;
    private IntentIntegrator qrScan;
    private String responseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_result);
        setup();
    }

    public void setup() {
        Intent intent = getIntent();
        resultSuccess = false;
        password = intent.getStringExtra(RESULT_PASS);
        type = intent.getIntExtra(CHOICE_TYPE, 0);
        subjectId = intent.getLongExtra(RESULT_SUBJECT, 0);

        layoutResult = findViewById(R.id.layout_student_result);
        imgResult = findViewById(R.id.imgResult);
        txtResult = findViewById(R.id.txt_result);
        btnResult = findViewById(R.id.btn_result);
        new AttendeeTask().execute();
    }

    public void performResultAction(View v) {
        Intent intent;
        if (resultSuccess) {
            intent = new Intent(this, StudentChooseActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            if (type == TYPE_PASSWORD) {
                intent = new Intent(this, StudentPasswordActivity.class);
                startActivity(intent);
                finish();
            } else if (type == TYPE_QR_CODE) {
                qrScan = new IntentIntegrator(this);
                qrScan.setOrientationLocked(false);
                qrScan.initiateScan();
            }
        }
    }

    // WebService the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                password = result.getContents();
                type = TYPE_QR_CODE;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public class AttendeeTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Long userId = prefs.getLong("user_id", 0);

            if (type == TYPE_QR_CODE) {
                String[] qrText = password.split(";");
                subjectId = Long.parseLong(qrText[0]);
                password = qrText[1];
            }

            String url = "https://behereapi-eltonvs1.c9users.io/api/attendee/" + subjectId;
            WebService post = new WebService();

            try {
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("user_id", userId.toString())
                        .appendQueryParameter("password", password);
                responseData = post.sendPost(url, builder.build().getEncodedQuery(), StudentResultActivity.this);

            } catch (Exception e) {
                Log.e(TAG, "doInBackground: ", e);
            }
            return responseData;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            layoutResult.setVisibility(View.VISIBLE);
            if (string != null) {
                resultSuccess = true;
                imgResult.setImageResource(R.drawable.positive_result);
                txtResult.setText(R.string.result_success);
                btnResult.setText(R.string.result_ok);
            } else {
                resultSuccess = false;
                imgResult.setImageResource(R.drawable.negative_result);
                txtResult.setText(R.string.result_failed);
                btnResult.setText(R.string.result_try_again);
            }
        }
    }
}
