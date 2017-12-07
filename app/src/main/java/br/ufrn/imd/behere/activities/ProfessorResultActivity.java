package br.ufrn.imd.behere.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.utils.RandomString;
import br.ufrn.imd.behere.utils.WebService;

public class ProfessorResultActivity extends CustomActivity {

    public static final String PASSWORD_RESULT = "password_result";
    public static final String TIMEOUT_RESULT = "timeout_result";
    public static final String CHOICE_RESULT = "choice_result";
    private ImageView ivQrCode;
    private ImageView imgResult;
    private TextView txtResult;
    private Button btnResult;
    private String password;
    private String strTimeout;
    private int resultChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_result);
        setup();
    }

    public void setup() {
        Intent intent = getIntent();

        imgResult = findViewById(R.id.img_professor_result);
        ivQrCode = findViewById(R.id.iv_qrcode);
        txtResult = findViewById(R.id.txt_professor_result);
        btnResult = findViewById(R.id.btn_professor_result);

        password = intent.getStringExtra(PASSWORD_RESULT);
        strTimeout = intent.getStringExtra(TIMEOUT_RESULT);
        resultChoice = intent.getIntExtra(CHOICE_RESULT, -1);

        txtResult.setText(R.string.result_success);
        btnResult.setText(R.string.result_ok);

        if (resultChoice == 1) {
            createQrCode();
        } else if (resultChoice == 2) {
            imgResult.setVisibility(View.VISIBLE);
        }

        if (password != null) {
            new AttendanceTask().execute();
        }
    }

    private void createQrCode() {
        QRCodeWriter writer = new QRCodeWriter();
        long subjectId = prefs.getLong("selected_subject", 0);
        RandomString random = new RandomString(10);
        strTimeout = "200";

        try {
            password = random.nextString();
            String s = subjectId + ";" + password + ";" + strTimeout;
            BitMatrix bitMatrix = writer.encode(s, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp;
            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            ivQrCode.setVisibility(View.VISIBLE);
            ivQrCode.setImageBitmap(bmp);

        } catch (WriterException e) {
            Log.e(TAG, "createQrCode: WriterException", e);
        }
    }

    public void performProfessorResult(View v) {
        Intent intent = new Intent(this, ProfessorChooseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void performStudentList(View v) {
        Intent intent = new Intent(this, StudentListActivity.class);
        intent.putExtra(StudentListActivity.SUBJECT_EXTRA, prefs.getLong("selected_subject", 0));
        startActivity(intent);
    }

    public class AttendanceTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String url = "https://behereapi-eltonvs1.c9users.io/api/attendances/";
            Long currentDate = System.currentTimeMillis();
            Long classId = prefs.getLong("selected_subject", 0);
            Long userId = prefs.getLong("user_id", 0);

            WebService post = new WebService();

            try {
                Uri.Builder builder = new Uri.Builder().appendQueryParameter("date", currentDate.toString())
                        .appendQueryParameter("professor_id", userId.toString())
                        .appendQueryParameter("attendance_id", classId.toString())
                        .appendQueryParameter("class_id", classId.toString())
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
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
