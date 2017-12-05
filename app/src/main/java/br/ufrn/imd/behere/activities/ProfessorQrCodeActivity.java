package br.ufrn.imd.behere.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

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

        createQrCode();

        // Adds back arrow to layout
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void createQrCode() {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            String s = "Presenca confirmada";
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
            ivQrCode.setImageBitmap(bmp);

        } catch (WriterException e) {
            e.printStackTrace();
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
