package br.ufrn.imd.behere;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

public class StudentChooseActivity extends CustomActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_choose);
    }

    public void chooseQRCode(View v) {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivity(camera);
    }

    public void choosePassword(View v) {
        Intent intent = new Intent(StudentChooseActivity.this, StudentPasswordActivity.class);
        startActivity(intent);
    }

}
