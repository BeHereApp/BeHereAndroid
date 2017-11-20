package br.ufrn.imd.behere.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.utils.Constants;

public class LoginActivity extends CustomActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setup();
    }

    public void setup() {
        String accessToken = prefs.getString(Constants.KEY_ACCESS_TOKEN, null);
        if(accessToken != null) {
            Intent intent = new Intent(LoginActivity.this, LoginAPIActivity.class);
            startActivity(intent);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
        }
    }

    public void signIn(View v) {
        Intent intent = new Intent(LoginActivity.this, LoginAPIActivity.class);
        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
    }
}

