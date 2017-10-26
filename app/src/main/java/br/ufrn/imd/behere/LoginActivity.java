package br.ufrn.imd.behere;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends CustomActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void signIn(View v) {
        Intent intent = new Intent(LoginActivity.this, StudentChooseActivity.class);
        startActivity(intent);
    }
}

