package br.ufrn.imd.behere.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import br.ufrn.imd.behere.R;

public class ProfessorChooseActivity extends CustomActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        // Adds back arrow to layout
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void chooseQRCode(View v) {
        Intent intent = new Intent(this, ProfessorQrCodeActivity.class);
        startActivity(intent);
    }

    public void choosePassword(View v) {
        Intent intent = new Intent(this, ProfessorPasswordActivity.class);
        startActivity(intent);
    }

}
