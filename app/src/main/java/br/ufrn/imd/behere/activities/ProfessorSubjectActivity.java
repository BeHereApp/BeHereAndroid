package br.ufrn.imd.behere.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.adapters.SubjectRecyclerAdapter;
import br.ufrn.imd.behere.model.Subject;
import br.ufrn.imd.behere.utils.DatabaseInstance;
import br.ufrn.imd.behere.utils.RecyclerViewClickListener;

public class ProfessorSubjectActivity extends CustomActivity implements RecyclerViewClickListener {

    private ArrayList<Subject> professorSubjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_subject);
        setup();
    }

    private void setup() {
        if (DatabaseInstance.dbHelper == null) {
            DatabaseInstance.createDBInstance(getApplicationContext());
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_subjects);
        professorSubjects = new ArrayList<>();

        // Fetch subjects from Database
        fetchDataDB();

        RecyclerView.Adapter adapter = new SubjectRecyclerAdapter(professorSubjects, this);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Adds back arrow to layout
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void fetchDataDB() {
        final int loggedUser = prefs.getInt("logged_user_id", 0);
        Cursor cursor = DatabaseInstance.database.rawQuery(
                "SELECT SUBJECTS.ID, SUBJECTS.NAME, SUBJECTS.SCHEDULE, SUBJECTS.LOCATION FROM USER_SUBJECTS INNER JOIN SUBJECTS ON USER_SUBJECTS.SUBJECT=SUBJECTS.ID WHERE USER_SUBJECTS.USER=" +
                loggedUser, null);

        while (cursor.moveToNext()) {
            final int id = cursor.getInt(0);
            final String name = cursor.getString(1);
            final String schedule = cursor.getString(2);
            final String location = cursor.getString(3);
            professorSubjects.add(new Subject(id, schedule, name, location));
        }
        cursor.close();
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("selected_subject", professorSubjects.get(position).getId());
        editor.apply();
        Intent intent = new Intent(this, ProfessorChooseActivity.class);
        startActivity(intent);
    }

}
