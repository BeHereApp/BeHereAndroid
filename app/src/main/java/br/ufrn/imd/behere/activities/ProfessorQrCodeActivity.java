package br.ufrn.imd.behere.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.utils.RandomString;

public class ProfessorQrCodeActivity extends CustomActivity {

    private static final String TAG = ProfessorQrCodeActivity.class.getName();
    private EditText etTimeout;
    private String strTimeout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_qr_code);

        etTimeout = (EditText) findViewById(R.id.et_timeout);

        // Adds back arrow to layout
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void setQrCode(View view) {
        strTimeout = etTimeout.getText().toString();

        if (!strTimeout.isEmpty()) {
            Intent intent = new Intent(this, ProfessorResultActivity.class);
            intent.putExtra(ProfessorResultActivity.CHOICE_RESULT, 1);
            startActivity(intent);
            finish();
        }
    }
}
