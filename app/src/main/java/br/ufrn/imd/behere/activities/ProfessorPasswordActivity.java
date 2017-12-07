package br.ufrn.imd.behere.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import br.ufrn.imd.behere.R;

public class ProfessorPasswordActivity extends CustomActivity {

    private EditText etPassword;
    private EditText etTimeout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_password);

        etPassword = (EditText) findViewById(R.id.et_password);
        etTimeout = (EditText) findViewById(R.id.et_timeout);

        // Adds back arrow to layout
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void setPassword(View v) {
        final String password = etPassword.getText().toString();
        final String strTimeout = etTimeout.getText().toString();

        if (!strTimeout.isEmpty() && !password.isEmpty()) {
            final Integer timeout = Integer.parseInt(strTimeout);
            etPassword.setText("");
            etTimeout.setText("");

            Intent intent = new Intent(this, ProfessorResultActivity.class);
            intent.putExtra(ProfessorResultActivity.PASSWORD_RESULT, password);
            intent.putExtra(ProfessorResultActivity.TIMEOUT_RESULT, strTimeout);
            intent.putExtra(ProfessorResultActivity.CHOICE_RESULT, 2);
            startActivity(intent);
            finish();
        }
    }
}
