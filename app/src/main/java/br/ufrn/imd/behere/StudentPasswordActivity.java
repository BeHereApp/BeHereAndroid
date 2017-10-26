package br.ufrn.imd.behere;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class StudentPasswordActivity extends CustomActivity {

    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_password);

        etPassword = (EditText) findViewById(R.id.etPassword);

        //adds back arrow to layout
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void enterPassword(View v) {
        String userPassword = etPassword.getText().toString();
        etPassword.setText("");
    }
}
