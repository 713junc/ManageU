package me.jennachoi.to_doapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;



import me.jennachoi.to_doapp.Model.Data;

public class HomeActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Toolbar toolbar;
    private FloatingActionButton addData;

    //Firebase
    private DatabaseReference rdatabase;
    private FirebaseAuth mAuth;

    // Recycler
    private RecyclerView recyclerView;


    //Update input field
    private EditText update_title;
    private EditText update_note;
    private EditText update_process;

    private Button update_delete;
    private Button update_update;

    private TextView currentDate;


    //Variable
    private String title;
    private String note;
    private String post_key;
    private String process;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        // Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uId = user.getUid();

        rdatabase = FirebaseDatabase.getInstance().getReference().child("TaskNote").child(uId);
        rdatabase.keepSynced(true);


        // Recycle
        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);



        // Toolbar
        toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ManageU");


        // Today Date
        currentDate = (TextView) findViewById(R.id.test_text);
        String todayDate = DateFormat.getDateInstance().format(new Date());
        currentDate.setText(todayDate);


        // Calendar
        Button date = findViewById(R.id.cal_btn);
        date.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new GoalDate();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });







        addData = findViewById(R.id.add_button);

        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder myDialog = new AlertDialog.Builder(HomeActivity.this);

                LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
                View view = inflater.inflate(R.layout.new_data, null);

                myDialog.setView(view);


                final AlertDialog dialog = myDialog.create();

                final EditText title = view.findViewById(R.id.content_title);
                final EditText note = view.findViewById(R.id.content_note);
                final EditText process = view.findViewById(R.id.content_process);



                Button save = view.findViewById(R.id.save_button);

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        String get_title = title.getText().toString().trim();
                        String get_note = note.getText().toString().trim();
                        String get_process = process.getText().toString().trim();

                        if(TextUtils.isEmpty(get_title)){
                            title.setError("Add Title");
                            return;
                        }
                        if(TextUtils.isEmpty(get_note)){
                            note.setError("Add Note");
                            return;
                        }
                        if(TextUtils.isEmpty(get_process)){
                            process.setError("Add Your Start Point to reach Your Goal");
                            return;
                        }

                        String get_id = rdatabase.push().getKey();
                        String get_date = DateFormat.getDateInstance().format(new Date());

                        Data data = new Data(get_title, get_note, get_date, get_id, get_process);
                        rdatabase.child(get_id).setValue(data);

                        Toast.makeText(getApplicationContext(),"Data Insert", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    }
                });

                dialog.show();

            }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                Data.class,
                R.layout.recycle_data,
                MyViewHolder.class,
                rdatabase
        ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, final Data model, final int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());
                viewHolder.setProcess(model.getProcess());

                viewHolder.myview.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        post_key = getRef(position).getKey();
                        title = model.getTitle();
                        note = model.getNote();
                        process = model.getProcess();
                        updateData();

                    }
                });

            }
        };

        recyclerView.setAdapter(adapter);
    }




    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View myview;

        public MyViewHolder(View itemView){
            super(itemView);
            myview = itemView;
        }

        public void setTitle(String title){
            TextView mTitle = myview.findViewById(R.id.title);
            mTitle.setText(title);
        }

        public void setNote(String note){
            TextView mNote = myview.findViewById(R.id.note);
            mNote.setText(note);
        }

        public void setDate(String date){
            TextView mDate = myview.findViewById(R.id.date);
            mDate.setText(date);
        }

        public void setProcess(String process){
            TextView mProcess = myview.findViewById(R.id.process_goal);
            mProcess.setText(process);
        }

    }

    public void updateData() {

        AlertDialog.Builder mydialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);

        View myview = inflater.inflate(R.layout.update_data, null);
        mydialog.setView(myview);

        final AlertDialog dialog = mydialog.create();


        update_title = myview.findViewById(R.id.edt_title_upd);
        update_note = myview.findViewById(R.id.edt_note_upd);
        update_process = myview.findViewById(R.id.edt_process);


        update_title.setText(title);
        update_title.setSelection(title.length());

        update_note.setText(note);
        update_note.setSelection(note.length());

        update_process.setText(process);
        update_process.setSelection(process.length());



        update_delete = myview.findViewById(R.id.btn_delete_upd);
        update_update = myview.findViewById(R.id.btn_update_upd);

        update_update.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                title=update_title.getText().toString().trim();
                note=update_note.getText().toString().trim();
                process=update_process.getText().toString().trim();

                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(title, note, mDate, post_key, process);

                rdatabase.child(post_key).setValue(data);


                dialog.dismiss();
            }
        });

        update_delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                rdatabase.child(post_key).removeValue();

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        TextView textView = (TextView) findViewById(R.id.test_text);
        textView.setText(currentDateString);
    }
}
