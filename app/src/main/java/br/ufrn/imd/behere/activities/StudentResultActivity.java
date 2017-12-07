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
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.utils.WebService;

public class StudentResultActivity extends CustomActivity {

    public static final String PASSWORD_RESULT = "password_result";
    public static final String CHOICE_TYPE = "choice_type";
    private ImageView imgResult;
    private TextView txtResult;
    private Button btnResult;
    private boolean resultSuccess;
    private String password;
    private String type;
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
        password = intent.getStringExtra(PASSWORD_RESULT);
        type = intent.getStringExtra(CHOICE_TYPE);

        imgResult = (ImageView) findViewById(R.id.imgResult);
        txtResult = (TextView) findViewById(R.id.txt_result);
        btnResult = (Button) findViewById(R.id.btn_result);
        new AttendeeTask().execute();
    }

    public void performResultAction(View v) {
        Intent intent;
        if (resultSuccess == true) {
            intent = new Intent(this, StudentChooseActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            if (type.equals("password")) {
                intent = new Intent(this, StudentPasswordActivity.class);
                startActivity(intent);
                finish();
            } else {
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
                type = "qrcode";
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public class AttendeeTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String url = "https://behereapi-eltonvs1.c9users.io/api/attendee/4";
            Long userId = prefs.getLong("user_id", 0);

            WebService post = new WebService();

            try {
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("user_id", userId.toString())
                        .appendQueryParameter("password", password);
                responseData = post.sendPost(url, builder.build().getEncodedQuery(), StudentResultActivity.this);

            } catch (Exception e) {
                Log.e(TAG, "doInBackground: ", e);
            }
            return responseData;
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
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
