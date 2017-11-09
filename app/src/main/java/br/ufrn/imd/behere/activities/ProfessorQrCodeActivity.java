package br.ufrn.imd.behere.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import br.ufrn.imd.behere.R;

public class ProfessorQrCodeActivity extends CustomActivity {

    private EditText etTimeout;
    private ImageView ivQrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_qr_code);

        etTimeout = (EditText) findViewById(R.id.et_timeout);
        ivQrCode = (ImageView) findViewById(R.id.iv_qrcode);

        // Adds back arrow to layout
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void setQrCode(View view) {
        final String strTimeout = etTimeout.getText().toString();

        if (!strTimeout.isEmpty()) {
            final Integer timeout = Integer.parseInt(strTimeout);
            etTimeout.setText("");

            Intent intent = new Intent(this, ProfessorResultActivity.class);
            intent.putExtra(ProfessorResultActivity.TIMEOUT_RESULT, timeout);
            startActivity(intent);
            finish();
        }
    }
}
