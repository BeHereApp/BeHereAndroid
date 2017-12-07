package br.ufrn.imd.behere.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.model.Student;


public class StudentRecyclerAdapter extends RecyclerView.Adapter<StudentRecyclerAdapter.RecyclerViewHolder> {

    private List<Student> students = new ArrayList<>();

    public StudentRecyclerAdapter(List<Student> students) {
        this.students = students;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_layout, parent, false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        Student student = students.get(position);
        holder.name.setText(student.getName());
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        RecyclerViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.student_name);
        }
    }
}

