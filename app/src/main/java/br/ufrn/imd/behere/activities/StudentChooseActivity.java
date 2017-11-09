package br.ufrn.imd.behere.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import br.ufrn.imd.behere.R;

public class StudentChooseActivity extends CustomActivity {

    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_choose);
        setup();
    }

    public void setup() {
        qrScan = new IntentIntegrator(this);
    }

    public void chooseQRCode(View v) {
        //initiating the qr code scan
        qrScan.setOrientationLocked(false);
        qrScan.initiateScan();
    }

    public void choosePassword(View v) {
        Intent intent = new Intent(StudentChooseActivity.this, StudentPasswordActivity.class);
        startActivity(intent);
    }

    // Get the results:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(this, StudentResultActivity.class);
                intent.putExtra(StudentResultActivity.PASSWORD_RESULT, result.getContents());
                intent.putExtra(StudentResultActivity.CHOICE_TYPE, "qrcode");
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
