package com.example.hk19;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements CustomAdapter.OnRecordClickListener{

    public static int counter =0;
    public static long lastClick;

    TextView creator;
    List<PersonDetails> persons;
    TextInputEditText myInputName, myInputNumber;
    RecyclerView myList;
    MyDBHandler dbHandler;
    RecyclerView.Adapter myAdapter;
    TextView userName,version;

    @Override
    public void onBackPressed() {
        if(counter==1){
            lastClick=System.currentTimeMillis();
            Toast.makeText(this, "Click again to EXIT", Toast.LENGTH_SHORT).show();
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
        userName=findViewById(R.id.userName);
        persons=new ArrayList<>();
        creator = findViewById(R.id.creator);
        version=findViewById(R.id.version);

        version.setText("v9");

        Bundle bundle = getIntent().getExtras();
        if (bundle==null){return;}
        userName.setText(bundle.getString("userNames"));

        myList.setHasFixedSize(true);
        myList.setLayoutManager(new LinearLayoutManager(this));

        dbHandler = new MyDBHandler(this,null,null,1);
        printDatabase();

        myInputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                printSearch();
            }
        });

        creator.setOnClickListener(new View.OnClickListener() {
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

    private void printDatabase() {
        persons=dbHandler.databaseToList();
        myAdapter = new CustomAdapter(persons,this,this);
        myList.setAdapter(myAdapter);
        myInputName.setText("");
        myInputNumber.setText("");
    }

    public void updateButtonClick(View view) {
        String inputText = myInputName.getText().toString();
        String inputNum = myInputNumber.getText().toString();
        boolean found = false;
        if(!inputText.equals("")){
            for(PersonDetails personDetails:persons){
                if(personDetails.getPersonName().toLowerCase().equals(inputText.toLowerCase())){
                    inputText=personDetails.getPersonName();
                    found=true;
                    break;
                }
            }
            if(found){
                if (!inputNum.equals("0")) {
                    try{
                        double d = Double.valueOf(inputNum);
                        dbHandler.updatePerson(inputText, inputNum);
                        printDatabase();
                    }catch(Exception e){
                        Toast.makeText(this, "Invalid quantity !!!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    dbHandler.deletePerson(inputText);
                    printDatabase();
                }
            }else{
                try{
                    double d = Double.valueOf(inputNum);
                    dbHandler.addPerson(new PersonDetails(inputText,inputNum));
                    printDatabase();
                }catch(Exception e){
                    Toast.makeText(this, "Invalid quantity !!!", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            Toast.makeText(this, "Blank field(s) !!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void addButtonClick(View view) {
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
                    dbHandler.addPerson(person);
                    printDatabase();
                } catch (Exception e){
                    Toast.makeText(this, "Invalid number of pages !!!", Toast.LENGTH_SHORT).show();
                    myInputNumber.setText("");
                }
            }else{
                double prevValue=Double.valueOf(dbHandler.searchDatabase(person.getPersonName()));
                try {
                    double newValue = Double.valueOf(person.getPersonNumber());
                    double updatedValue=prevValue+newValue;
                    if(updatedValue!=0) {
                        dbHandler.updatePerson(person.getPersonName(), String.valueOf(updatedValue));
                    }else{
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
    public void onRecordClick(int position) {
        PersonDetails personDetails=persons.get(position);
        myInputName.setText(personDetails.getPersonName());
        myInputNumber.setText(personDetails.getPersonNumber());

    }

    public void clearButtonClick(View view) {
        myInputName.setText("");
        myInputNumber.setText("");

    }

    public void deleteButtonClick(View view) {
        String inputText = myInputName.getText().toString();
        boolean found = false;
        if(!inputText.equals("")){
            for(PersonDetails personDetails:persons){
                if(personDetails.getPersonName().toLowerCase().equals(inputText.toLowerCase())){
                    inputText=personDetails.getPersonName();
                    found=true;
                    break;
                }
            }
            if(found){
                    dbHandler.deletePerson(inputText);
                    printDatabase();
            }else{
                Toast.makeText(this, "No such record to delete !!!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Field name EMPTY !!!", Toast.LENGTH_SHORT).show();
        }
    }
}
