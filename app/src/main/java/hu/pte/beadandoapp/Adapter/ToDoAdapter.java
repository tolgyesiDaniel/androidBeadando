package hu.pte.beadandoapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import hu.pte.beadandoapp.AddNewTask;
import hu.pte.beadandoapp.MainActivity;
import hu.pte.beadandoapp.Model.ToDoModel;
import hu.pte.beadandoapp.ModifyActivity;
import hu.pte.beadandoapp.R;
import hu.pte.beadandoapp.Utils.DataBaseHandler;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<ToDoModel> todoList;
    private Activity activity;
    private DataBaseHandler db;

    public ToDoAdapter(DataBaseHandler db, MainActivity activity){
        this.db = db;
        this.activity = activity;
    }

    public ToDoAdapter(DataBaseHandler db, ModifyActivity activity){
        this.db = db;
        this.activity = activity;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(ViewHolder holder, int position){
        db.openDatabase();
        ToDoModel item = todoList.get(position);
        holder.task.setText(item.getTask());
        holder.date.setText(item.getCreateDate());
        holder.description.setText(item.getDescription());
        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    db.updateStatus(item.getId(), 1);
                }
                else{
                    db.updateStatus(item.getId(), 0);
                }
            }
        });
        holder.task.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(v.getContext(), ModifyActivity.class);
                String id = String.valueOf(item.getId());
                intent.putExtra("position", id);
                v.getContext().startActivity(intent);
                return false;
            }
        });
    }

    public int getItemCount(){
        return todoList.size();
    }

    private boolean toBoolean(int n){
        return n != 0;
    }

    public void setTasks(List<ToDoModel> todoList){
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public Context getContext() { return activity; }

    public void deleteItem(int position){
        ToDoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox task;
        TextView date;
        TextView description;
        ViewHolder(View view){
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
            date = view.findViewById(R.id.tasksDate);
            description = view.findViewById(R.id.taskDescription);
        }
    }

}
