package br.ufrn.imd.behere.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import br.ufrn.imd.behere.R;

public class ProfessorResultActivity extends CustomActivity {

    public static final String PASSWORD_RESULT = "password_result";
    public static final String TIMEOUT_RESULT = "timeout_result";
    private ImageView imgResult;
    private TextView txtResult;
    private Button btnResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_result);
        setup();
    }

    public void setup() {
        Intent intent = getIntent();

        imgResult = (ImageView) findViewById(R.id.img_professor_result);
        txtResult = (TextView) findViewById(R.id.txt_professor_result);
        btnResult = (Button) findViewById(R.id.btn_professor_result);

        imgResult.setImageResource(R.drawable.positive_result);
        txtResult.setText(R.string.result_success);
        btnResult.setText(R.string.result_ok);
    }

    public void performProfessorResult(View v) {
        Intent intent = new Intent(this, ProfessorChooseActivity.class);
        startActivity(intent);
        finish();
    }
}
