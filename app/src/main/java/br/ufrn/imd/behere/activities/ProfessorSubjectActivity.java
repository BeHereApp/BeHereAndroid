package br.ufrn.imd.behere.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.adapters.SubjectRecyclerAdapter;
import br.ufrn.imd.behere.model.Subject;
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
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_subjects);
        professorSubjects = new ArrayList<>();

        // Add example links
        professorSubjects.add(new Subject("35T12", "Grafos", "A306 - CIVT"));

        RecyclerView.Adapter adapter = new SubjectRecyclerAdapter(professorSubjects, this);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setSubtitle("My Subjects");
        }
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
