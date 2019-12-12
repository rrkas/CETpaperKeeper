package com.example.hk19;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hk19.decorator.SpacingItemDecorator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements CustomAdapter.OnRecordClickListener{

    public static int counter =0;
    public static long lastClick;

    TextView creator;
    List<PersonDetails> persons;
    TextInputEditText myInputName, myInputNumber;
    RecyclerView myList;
    MyDBHandler dbHandler;
    RecyclerView.Adapter myAdapter;
    TextView version;
    FloatingActionButton undo;
    String userName;
    View sadReactor;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences = getSharedPreferences("undoShow",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if(undo.isShown())
            editor.putBoolean("undoShow",true);
        else
            editor.putBoolean("undoShow",false);
        editor.apply();
    }
    @Override
    public void onBackPressed() {
        if(counter==1){
            lastClick=System.currentTimeMillis();
            Toast.makeText(this, "Click again to EXIT", Toast.LENGTH_SHORT).show();
            myInputName.setText("");
            myInputNumber.setText("");
            myInputNumber.clearFocus();
            myInputName.clearFocus();
            printDatabase();
            counter=2;
        }else{
            if (System.currentTimeMillis()-lastClick>=2000){
                lastClick=System.currentTimeMillis();
                counter=1;
                return;
            }
            moveTaskToBack(false);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        myInputName=findViewById(R.id.myInputName);
        myInputNumber=findViewById(R.id.myNumber);
        myList=findViewById(R.id.myList);

        sadReactor = findViewById(R.id.sad_reactor);

        userName = getIntent().getStringExtra("userNames").toUpperCase();
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Record Keeper of "+userName);


        persons=new ArrayList<>();
        creator = findViewById(R.id.creator);
        version=findViewById(R.id.version);
        undo=findViewById(R.id.undo);

        SharedPreferences undoShow = getSharedPreferences("undoShow",MODE_PRIVATE);
        if(undoShow.getBoolean("undoShow",false))
            undo.show();
        else
            undo.hide();

        undo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                PersonDetails graved = getDeletedBack();
                if(graved == null){
                    Toast.makeText(MainActivity.this, "Last delete had been undone !!!", Toast.LENGTH_SHORT).show();
                    undo.hide();
                }else{
                    boolean found = false;
                    PersonDetails p = new PersonDetails();
                    String name = graved.getPersonName();
                    for(PersonDetails personDetails:persons){
                        if(personDetails.getPersonName().toLowerCase().equals(name)){
                            found=true;
                            p = personDetails;
                            break;
                        }
                    }
                    if(found){
                        dbHandler.updatePerson(graved.getPersonName(),p.getPersonNumber() + graved.getPersonNumber());
                    }else{
                        dbHandler.addPerson(graved);
                    }
                    printDatabase();
                    undo.hide();
                }
            }
        });
        SpacingItemDecorator spacingItemDecorator = new SpacingItemDecorator(10);
        myList.addItemDecoration(spacingItemDecorator);

        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(myList);

        version.setText(LoginActivity.VERSION);

        myList.setHasFixedSize(true);
        myList.setLayoutManager(new LinearLayoutManager(this));

        dbHandler = new MyDBHandler(this,null,null,1);
        printDatabase();
        myInputName.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                printSearch();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        creator.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Contact me : rrka79wal@gmail.com", Toast.LENGTH_LONG).show();
            }
        });

    }
    public void printSearch(){
        if(!myInputName.getText().toString().equals("")){
            List<PersonDetails> searchPersons =new ArrayList<>();
            for(PersonDetails personDetails:persons){
                if(personDetails.getPersonName().toLowerCase().contains(myInputName.getText().toString().toLowerCase())){
                    searchPersons.add(personDetails);
                }
            }
            myAdapter = new CustomAdapter(searchPersons,MainActivity.this,MainActivity.this);
            myList.setAdapter(myAdapter);
        }else{
            myAdapter = new CustomAdapter(persons,MainActivity.this,MainActivity.this);
            myList.setAdapter(myAdapter);
        }
    }
    private void printDatabase(){
        persons=dbHandler.databaseToList();
        Collections.sort(persons, new Comparator<PersonDetails>() {
            @Override
            public int compare(PersonDetails personDetails, PersonDetails t1) {
                return personDetails.getPersonName().compareToIgnoreCase(t1.getPersonName());
            }
        });
        myAdapter = new CustomAdapter(persons,this,this);
        myList.setAdapter(myAdapter);
        myInputName.setText("");
        myInputName.clearFocus();
        myInputNumber.setText("");
        myInputNumber.clearFocus();
        if(persons.size()==0){
            sadReactor.setVisibility(View.VISIBLE);
            myList.setVisibility(View.GONE);
        }else{
            sadReactor.setVisibility(View.GONE);
            myList.setVisibility(View.VISIBLE);
        }
    }
    public void updateButtonClick(View view){
        String inputText = myInputName.getText().toString();
        String inputNum = myInputNumber.getText().toString();
        PersonDetails delPerson=new PersonDetails();
        boolean found = false;
        if(!inputText.equals("")){
            for(PersonDetails personDetails:persons){
                if(personDetails.getPersonName().toLowerCase().equals(inputText.toLowerCase())){
                    inputText=personDetails.getPersonName();
                    delPerson=personDetails;
                    found=true;
                    break;
                }
            }
            if(found){
                try{
                    double d = Double.valueOf(inputNum);
                    if (d!=0) {
                        dbHandler.updatePerson(inputText, inputNum);
                        Toast.makeText(this, "Record updated.", Toast.LENGTH_SHORT).show();
                    } else {
                        SharedPreferences del = getSharedPreferences("deleted",MODE_PRIVATE);
                        SharedPreferences.Editor editor = del.edit();
                        editor.putString("name",delPerson.getPersonName());
                        editor.putString("num",delPerson.getPersonNumber());
                        editor.apply();
                        undo.show();
                        dbHandler.deletePerson(inputText);
                        Toast.makeText(this, "Record deleted.", Toast.LENGTH_SHORT).show();
                    }
                    printDatabase();
                }catch(Exception e){
                    Toast.makeText(this, "Invalid quantity !!!", Toast.LENGTH_SHORT).show();
                }
            }else{
                try{
                    double d = Double.valueOf(inputNum);
                    if(d!=0) {
                        dbHandler.addPerson(new PersonDetails(inputText, inputNum));
                        Toast.makeText(this, "Record added.", Toast.LENGTH_SHORT).show();
                        printDatabase();
                    }else{
                        Toast.makeText(this, "No quantity to update!!", Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e){
                    Toast.makeText(this, "Invalid quantity !!!", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            Toast.makeText(this, "Blank field(s) !!!", Toast.LENGTH_SHORT).show();
        }
    }
    public void addButtonClick(View view){
        String name=myInputName.getText().toString();
        String num=myInputNumber.getText().toString();
        if(!name.equals("") && !num.equals("")) {
            PersonDetails person = new PersonDetails(name,num);
            boolean found = false;
            for (PersonDetails per:persons){
                if (per.getPersonName().toLowerCase().equals(person.getPersonName().toLowerCase())){
                    person.setPersonName(per.getPersonName());
                    found=true;
                    break;
                }
            }
            if(!found) {
                try{
                    double val = Double.valueOf(person.getPersonNumber());
                    if(val!=0) {
                        Toast.makeText(this, "Record added.", Toast.LENGTH_SHORT).show();
                        dbHandler.addPerson(person);
                        printDatabase();
                    }else{
                        Toast.makeText(this, "Number is ZERO.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    Toast.makeText(this, "Invalid number !!!", Toast.LENGTH_SHORT).show();
                    myInputNumber.setText("");
                }
            }else{
                double prevValue=Double.valueOf(dbHandler.searchDatabase(person.getPersonName()));
                try {
                    double newValue = Double.valueOf(person.getPersonNumber());
                    double updatedValue = prevValue + newValue;
                    if(updatedValue!=0) {
                        Toast.makeText(this, "Record updated.", Toast.LENGTH_SHORT).show();
                        dbHandler.updatePerson(person.getPersonName(), String.valueOf(updatedValue));
                    }else{
                        Toast.makeText(this, "Record deleted.", Toast.LENGTH_SHORT).show();
                        dbHandler.deletePerson(person.getPersonName());
                    }
                } catch (Exception e){
                    Toast.makeText(this, "Invalid quantity !!!", Toast.LENGTH_SHORT).show();
                    myInputNumber.setText("");
                }
                printDatabase();
            }
        } else {
            if(myInputName.getText().toString().equals(""))
                Toast.makeText(this, "Name is blank !!", Toast.LENGTH_SHORT).show();
            else if(myInputNumber.getText().toString().equals(""))
                Toast.makeText(this, "Number is blank !!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRecordClick(int position){
        PersonDetails personDetails=persons.get(position);
        myInputName.setText(personDetails.getPersonName());
        myInputNumber.setText(personDetails.getPersonNumber());

    }
    public void clearButtonClick(View view){
        myInputName.setText("");
        myInputNumber.setText("");
        myInputName.clearFocus();
        myInputNumber.clearFocus();
        Toast.makeText(this, "Fields cleared.", Toast.LENGTH_SHORT).show();
    }
    public void deleteButtonClick(View view){
        String inputText = myInputName.getText().toString();
        boolean found = false;
        PersonDetails delPerson = new PersonDetails();
        if(!inputText.equals("")){
            for(PersonDetails personDetails:persons){
                if(personDetails.getPersonName().toLowerCase().equals(inputText.toLowerCase())){
                    inputText=personDetails.getPersonName();
                    found=true;
                    delPerson=personDetails;
                    break;
                }
            }
            if(found){
                SharedPreferences del = getSharedPreferences("deleted",MODE_PRIVATE);
                SharedPreferences.Editor editor = del.edit();
                editor.putString("name",delPerson.getPersonName());
                editor.putString("num",delPerson.getPersonNumber());
                editor.apply();
                undo.show();
                dbHandler.deletePerson(inputText);
                Toast.makeText(this, "Record deleted.", Toast.LENGTH_SHORT).show();
                undo.show();
                printDatabase();
            }else{
                Toast.makeText(this, "No such record to delete !!!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Field name EMPTY !!!", Toast.LENGTH_SHORT).show();
        }
    }
    ItemTouchHelper.SimpleCallback itemTouchHelper = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            PersonDetails delPerson = persons.get(viewHolder.getAdapterPosition());
            SharedPreferences del = getSharedPreferences("deleted",MODE_PRIVATE);
            SharedPreferences.Editor editor = del.edit();
            editor.putString("name",delPerson.getPersonName());
            editor.putString("num",delPerson.getPersonNumber());
            editor.apply();
            undo.show();
            dbHandler.deletePerson(delPerson.getPersonName());
            printDatabase();
        }
    };
    public void getInfo(View view){
        Intent intent = new Intent(this,InfoActivity.class);
        startActivity(intent);
    }
    public void guideMe(){
        Intent intent = new Intent(this,GuideActivity.class);
        startActivity(intent);
    }
    public PersonDetails getDeletedBack(){
        PersonDetails personDetails=new PersonDetails();
        SharedPreferences del = getSharedPreferences("deleted",MODE_PRIVATE);
        if(del.getString("name","del").equals("del")){
            return null;
        }else{
            personDetails.setPersonName(del.getString("name","defName"));
            personDetails.setPersonNumber(del.getString("num","defNum"));
            SharedPreferences.Editor editor=del.edit();
            editor.putString("name","del");
            editor.apply();
            return personDetails;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.main_menu_help)
            guideMe();
        return super.onOptionsItemSelected(item);
    }
}
