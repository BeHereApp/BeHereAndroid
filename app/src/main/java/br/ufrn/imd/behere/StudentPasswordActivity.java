package br.ufrn.imd.behere;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class StudentPasswordActivity extends CustomActivity {

    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_password);

        etPassword = (EditText) findViewById(R.id.etPassword);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageView layout = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.imageToolBar);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        params.setMargins(0, 0, 150, 0);
        layout.setLayoutParams(params);
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
        Intent intent = new Intent(this, StudentResultActivity.class);
        intent.putExtra(StudentResultActivity.PASSWORD_RESULT, userPassword);
        startActivity(intent);
        etPassword.setText("");
        finish();
    }
}
