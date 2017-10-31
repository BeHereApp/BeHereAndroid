package br.ufrn.imd.behere.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import br.ufrn.imd.behere.R;

public class LoginActivity extends CustomActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void signIn(View v) {
        Intent intent = new Intent(LoginActivity.this, LinkActivity.class);
        startActivity(intent);
    }
}

