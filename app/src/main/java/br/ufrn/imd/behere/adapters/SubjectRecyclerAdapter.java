package br.ufrn.imd.behere.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import br.ufrn.imd.behere.R;
import br.ufrn.imd.behere.model.Subject;
import br.ufrn.imd.behere.utils.RecyclerViewClickListener;


public class SubjectRecyclerAdapter extends RecyclerView.Adapter<SubjectRecyclerAdapter.RecyclerViewHolder> {

    private static RecyclerViewClickListener itemListener;
    private ArrayList<Subject> professorSubjects = new ArrayList<>();

    public SubjectRecyclerAdapter(ArrayList<Subject> professorSubjects, RecyclerViewClickListener itemListener) {
        this.professorSubjects = professorSubjects;
        this.itemListener = itemListener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_layout, parent, false);

        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        Subject subject = professorSubjects.get(position);
        holder.schedule.append(subject.getSchedule());
        holder.name.setText(subject.getName());
        holder.location.append(subject.getLocation());
    }

    @Override
    public int getItemCount() {
        return professorSubjects.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView schedule, name, location;

        public RecyclerViewHolder(View view) {
            super(view);
            schedule = view.findViewById(R.id.subject_schedule);
            name = view.findViewById(R.id.subject_name);
            location = view.findViewById(R.id.subject_location);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemListener.recyclerViewListClicked(view, this.getLayoutPosition());
        }
    }
}