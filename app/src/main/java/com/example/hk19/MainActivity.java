package com.example.hk19;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    EditText myInputName, myNumber;
    ListView myListView;
    MyDBHandler dbHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myInputName=findViewById(R.id.myInputName);
        myNumber=findViewById(R.id.myNumber);
        myListView=findViewById(R.id.myListView);

        dbHandler = new MyDBHandler(this,null,null,1);
        printDatabase();
    }



    private void printDatabase() {
        PersonDetails p1=new PersonDetails("a",1);
        PersonDetails p2=new PersonDetails("b",2);
        PersonDetails[] persons = {p1,p2};//dbHandler.databaseToArray();
        ListAdapter myAdapter = new CustomAdapter(this,persons);
        myListView.setAdapter(myAdapter);
        myInputName.setText("");
        myNumber.setText("");
    }

    public void deleteButtonClick(View view) {
        String inputText = myInputName.getText().toString();
        int inputNum = Integer.valueOf(myNumber.getText().toString());
        if(inputNum!=0) {
            dbHandler.updatePerson(inputText, inputNum);
            printDatabase();
        }else {
            dbHandler.deletePerson(inputText);
        }
    }

    public void addButtonClick(View view) {
        if(!myInputName.getText().toString().equals("") && !myNumber.getText().toString().equals("")) {
            PersonDetails person = new PersonDetails(myInputName.getText().toString(), Integer.valueOf(myNumber.getText().toString()));
            dbHandler.addPerson(person);
            printDatabase();
        } else {
            Toast.makeText(this, "One or more fields are blank !!", Toast.LENGTH_SHORT).show();
        }
    }

    public void findRecord(View view) {
        String inputText = myInputName.getText().toString();
        if(!inputText.equals("")){
            int val = dbHandler.searchDatabase(inputText);
            myNumber.setText(val);
        } else{
            Toast.makeText(this, "Person Name is BLANK !!", Toast.LENGTH_SHORT).show();
        }
    }
}
