package br.ufrn.imd.behere.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.adapters.StudentRecyclerAdapter;
import br.ufrn.imd.behere.model.Student;
import br.ufrn.imd.behere.utils.Constants;
import br.ufrn.imd.behere.utils.WebService;

public class StudentListActivity extends CustomActivity {

    public static final String SUBJECT_EXTRA = "subject_extra";
    private static final String TAG = StudentListActivity.class.getName();
    private SwipeRefreshLayout swipeRefreshStudents;
    private ArrayList<Student> students;
    private RecyclerView.Adapter adapter;
    private long subjectId;

    void onItemsLoadComplete(List<Student> studentsList) {
        students.clear();
        students.addAll(studentsList);
        adapter.notifyDataSetChanged();

        // Stop refresh animation
        swipeRefreshStudents.setRefreshing(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        setup();
    }

    private void setup() {
        // Adds back arrow to layout
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        subjectId = getIntent().getLongExtra(SUBJECT_EXTRA, 0);

        final RecyclerView recyclerView = findViewById(R.id.rv_students);
        students = new ArrayList<>();

        adapter = new StudentRecyclerAdapter(students);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        swipeRefreshStudents = findViewById(R.id.sr_students);
        swipeRefreshStudents.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

        swipeRefreshStudents.setRefreshing(true);
        refreshItems();
    }

    void refreshItems() {
        final String accessToken = prefs.getString(Constants.KEY_ACCESS_TOKEN, null);
        new RefreshItemsTask().execute(accessToken);
    }

    public class RefreshItemsTask extends AsyncTask<String, Void, List<Student>> {

        @Override
        protected List<Student> doInBackground(String... strings) {
            String url1 = "https://behereapi-eltonvs1.c9users.io/api/attendances/" + subjectId;

            WebService get = new WebService();
            String response = null;

            try {
                response = get.sendGet(url1);
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: ", e);
            }

            JSONArray arr = null;
            if (response != null) {
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    arr = jsonObj.getJSONArray("students");
                    Log.i(TAG, "doInBackground: " + arr);
                } catch (JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }

            if (arr == null) {
                return new ArrayList<>();
            }

            ArrayList<Long> ids = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                try {
                    Long studentId = arr.getLong(i);
                    ids.add(studentId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            ArrayList<Student> studentsList = new ArrayList<>();

            for (Long id : ids) {
                String url = "usuario/v0.1/usuarios/" + id;
                String accessToken = strings[0];

                String reqUrl = Constants.BASE_URL + url;
                String jsonStr = null;
                try {
                    jsonStr = get.get(reqUrl, accessToken, API_KEY);
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: Error on serviceCall", e);
                }

                Log.i(TAG, "doInBackground: jsonStr = " + jsonStr);
                JSONObject obj;
                if (jsonStr != null) {
                    try {
                        obj = new JSONObject(jsonStr);
                        Long studentId = obj.getLong("id-usuario");
                        String studentName = obj.getString("nome-pessoa");
                        Log.i(TAG, "doInBackground: id = " + studentId + " name = " + studentName);
                        studentsList.add(new Student(studentId, studentName));
                    } catch (JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Couldn't get json from server.");
                }
            }

            return studentsList;
        }

        @Override
        protected void onPostExecute(List<Student> studentsList) {
            onItemsLoadComplete(studentsList);
        }
    }
}
