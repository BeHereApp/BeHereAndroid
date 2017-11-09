package br.ufrn.imd.behere.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import br.ufrn.imd.behere.R;

public class StudentPasswordActivity extends CustomActivity {

    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_password);

        etPassword = (EditText) findViewById(R.id.et_password);

        // Adds back arrow to layout
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void enterPassword(View v) {
        String userPassword = etPassword.getText().toString();
        Intent intent = new Intent(this, StudentResultActivity.class);
        intent.putExtra(StudentResultActivity.PASSWORD_RESULT, userPassword);
        intent.putExtra(StudentResultActivity.CHOICE_TYPE, "password");
        startActivity(intent);
        etPassword.setText("");
        finish();
    }
}
