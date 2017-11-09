package br.ufrn.imd.behere.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import br.ufrn.imd.behere.R;

public class StudentResultActivity extends CustomActivity {

    public static final String PASSWORD_RESULT = "password_result";
    public static final String CHOICE_TYPE = "choice_type";
    private ImageView imgResult;
    private TextView txtResult;
    private Button btnResult;
    private boolean resultSuccess;
    private String choiceResult;
    private String choiceType;
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_result);
        setup();
    }

    public void setup() {
        Intent intent = getIntent();
        choiceResult = intent.getStringExtra(PASSWORD_RESULT);
        choiceType = intent.getStringExtra(CHOICE_TYPE);

        imgResult = (ImageView) findViewById(R.id.imgResult);
        txtResult = (TextView) findViewById(R.id.txt_result);
        btnResult = (Button) findViewById(R.id.btn_result);
        checkResult();
    }

    public void checkResult() {
        if (choiceResult.equals("password")) {
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

    public void performResultAction(View v) {
        Intent intent;
        if (resultSuccess == true) {
            intent = new Intent(this, StudentChooseActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else {
            if (choiceType.equals("password")) {
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

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                choiceResult = result.getContents();
                choiceType = "qrcode";
                checkResult();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
