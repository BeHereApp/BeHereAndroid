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
import br.ufrn.imd.behere.adapters.LinkRecyclerAdapter;
import br.ufrn.imd.behere.model.UserLink;
import br.ufrn.imd.behere.utils.DatabaseInstance;
import br.ufrn.imd.behere.utils.RecyclerViewClickListener;

public class LinkActivity extends CustomActivity implements RecyclerViewClickListener {

    private ArrayList<UserLink> userLinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link);
        setup();
    }

    private void setup() {
        if (DatabaseInstance.dbHelper == null) {
            DatabaseInstance.createDBInstance(getApplicationContext());
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_links);
        userLinks = new ArrayList<>();

        // Fetch links from Database
        fetchDataDB();

        RecyclerView.Adapter adapter = new LinkRecyclerAdapter(userLinks, this);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void fetchDataDB() {
        final int loggedUser = prefs.getInt("logged_user_id", 1);
        Cursor cursor = DatabaseInstance.database.rawQuery(
                "SELECT LINK_TYPE FROM USER_LINKS WHERE USER=" + loggedUser, null);

        while (cursor.moveToNext()) {
            final int link = cursor.getInt(0);
            if (link == 1) {
                userLinks.add(new UserLink(UserLink.LinkType.PROFESSOR, "Digital Metropolis Institute"));
            } else {
                userLinks.add(new UserLink(UserLink.LinkType.STUDENT, "Digital Metropolis Institute"));
            }
        }
        cursor.close();
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        Intent intent;
        SharedPreferences.Editor editor = prefs.edit();
        if (userLinks.get(position).getType() == UserLink.LinkType.STUDENT) {
            intent = new Intent(this, StudentChooseActivity.class);
            editor.putInt("link_type", 2);
        } else {
            intent = new Intent(this, ProfessorSubjectActivity.class);
            editor.putInt("link_type", 1);
        }
        editor.apply();

        startActivity(intent);
    }
}
