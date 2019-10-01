package com.example.hk19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText userNameInput;
    Button btnLogin;
    TextView version;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userNameInput = findViewById(R.id.userNameInput);
        btnLogin = findViewById(R.id.btnLogin);
        version=findViewById(R.id.dispVersion);


        SharedPreferences sharedPreferences =  getSharedPreferences("userNames",MODE_PRIVATE);
        String userName = sharedPreferences.getString("userNames","def");
        if(!userName.equals("def")) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("userNames", userName);
            startActivity(intent);
            finish();
        }
        version.setText("v9");
    }

    public void loginMe(View view) {
        if(userNameInput.getText().toString().equals("")){
            Toast.makeText(this, "Empty User Name !!!", Toast.LENGTH_LONG).show();
        }
        else if(userNameInput.getText().toString().length()>16){
            Toast.makeText(this, "User Name Too Long !!!", Toast.LENGTH_LONG).show();
        }else{
            SharedPreferences sharedPreferences = getSharedPreferences("userNames",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userNames",userNameInput.getText().toString());
            editor.apply();
            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("userNames",userNameInput.getText().toString());
            startActivity(intent);
            finish();
        }
    }
}
