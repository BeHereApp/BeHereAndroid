package br.ufrn.imd.behere.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.utils.DatabaseInstance;

public class LoginActivity extends CustomActivity {

    private EditText txtUsername;
    private EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setup();
    }

    public void setup() {
        if (DatabaseInstance.dbHelper == null) {
            DatabaseInstance.createDBInstance(getApplicationContext());
        }

        txtUsername = (EditText) findViewById(R.id.username);
        txtPassword = (EditText) findViewById(R.id.password);
    }

    public void signIn(View v) {
        Intent intent = new Intent(LoginActivity.this, LinkActivity.class);
        Cursor result = DatabaseInstance.database.rawQuery("SELECT id, name, password FROM users", null);

        String inputUsername = txtUsername.getText().toString();
        String inputPassword = txtPassword.getText().toString();

        while (result.moveToNext()) {
            int id = result.getInt(0);
            String username = result.getString(1);
            String password = result.getString(2);

            if (inputUsername.equals(username) && inputPassword.equals(password)) {
                txtUsername.setText("");
                txtPassword.setText("");
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("logged_user_id", id);
                editor.apply();
                startActivity(intent);
            }
        }
        result.close();
    }
}

