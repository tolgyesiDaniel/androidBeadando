package hu.pte.beadandoapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import hu.pte.beadandoapp.Adapter.ToDoAdapter;
import hu.pte.beadandoapp.Model.ToDoModel;
import hu.pte.beadandoapp.Utils.DataBaseHandler;

public class ModifyActivity extends AppCompatActivity {

    private EditText modifyTaskText;
    private EditText modifyTaskDescription;
    private Button modifyTaskSaveButton;
    private Button modifyTaskDeleteButton;
    private DataBaseHandler db;
    private ToDoAdapter tasksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        getSupportActionBar().hide();

        tasksAdapter = new ToDoAdapter(db,this);

        Bundle position = getIntent().getExtras();
        String id = position.getString("position");

        db = new DataBaseHandler(getApplicationContext());
        db.openDatabase();

        ToDoModel model = db.findById(id);

        modifyTaskText = findViewById(R.id.modifyTaskText);
        modifyTaskDescription = findViewById(R.id.modifyTaskDescription);

        modifyTaskText.setText(model.getTask());
        modifyTaskDescription.setText(model.getDescription());

        modifyTaskSaveButton = findViewById(R.id.modifyTaskButton);
        modifyTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                String text = modifyTaskText.getText().toString();
                String description = modifyTaskDescription.getText().toString();
                db.updateTask(Integer.parseInt(id), text, description);

                Intent i = new Intent(ModifyActivity.this, MainActivity.class);
                finish();
                overridePendingTransition(0,0);
                startActivity(i);
                overridePendingTransition(0,0);
            }
        });

        modifyTaskDeleteButton = findViewById(R.id.deleteTaskButton);
        modifyTaskDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(tasksAdapter.getContext());
                builder.setTitle("Feladat törlése");
                builder.setMessage("Biztosan törli ezt a feladatot?");
                builder.setPositiveButton("Törlés", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteTask(Integer.parseInt(id));

                        Intent i = new Intent(ModifyActivity.this, MainActivity.class);
                        finish();
                        overridePendingTransition(0,0);
                        startActivity(i);
                        overridePendingTransition(0,0);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

}
