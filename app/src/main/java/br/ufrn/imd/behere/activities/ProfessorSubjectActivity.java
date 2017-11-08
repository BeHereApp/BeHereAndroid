package br.ufrn.imd.behere.activities;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
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
    private SharedPreferences prefs;

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
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

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

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setSubtitle("My Subjects");
        }
    }

    private void fetchDataDB() {
        final int loggedUser = prefs.getInt("logged_user_id", 0);
        Cursor cursor = DatabaseInstance.database.rawQuery(
                "SELECT SUBJECTS.NAME, SUBJECTS.SCHEDULE, SUBJECTS.LOCATION FROM USER_SUBJECTS INNER JOIN SUBJECTS ON USER_SUBJECTS.SUBJECT=SUBJECTS.ID WHERE USER_SUBJECTS.USER=" +
                loggedUser, null);

        while (cursor.moveToNext()) {
            final String name = cursor.getString(0);
            final String schedule = cursor.getString(1);
            final String location = cursor.getString(2);
            professorSubjects.add(new Subject(schedule, name, location));
        }
        cursor.close();
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
       /* if (professorSubjects.get(position).getType() == UserLink.LinkType.STUDENT) {
            Intent intent = new Intent(this, StudentChooseActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "ProfessorChooseActivity", Toast.LENGTH_SHORT).show();
        }*/
    }
}
