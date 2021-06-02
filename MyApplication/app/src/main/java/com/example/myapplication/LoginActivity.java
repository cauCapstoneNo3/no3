package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText login_email, login_password;
    private Button login_button, join_button;
    public String UserEmail = null;
    public String UserPwd = null;
    public String UserName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        login_email = findViewById( R.id.login_email );
        login_password = findViewById( R.id.login_password );

        join_button = findViewById( R.id.join_button );
        join_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( LoginActivity.this, registerActivity.class );
                startActivity( intent );
            }
        });

        UserEmail = login_email.getText().toString();
        UserPwd = login_password.getText().toString();

        login_button = findViewById( R.id.login_button );
        login_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("test",UserEmail);

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean( "success" );

                            if(success) {//로그인 성공시

                                //String UserEmail = jsonObject.getString( "UserEmail" );
//                                String UserPwd = jsonObject.getString( "UserPwd" );
//                                String UserName = jsonObject.getString( "UserName" );
//                                Log.d("test", jsonObject.toString());


                                //Toast.makeText( getApplicationContext(), String.format("%s님 환영합니다.", UserName), Toast.LENGTH_SHORT ).show();
//                                Intent intent = new Intent( LoginActivity.this, MainActivity.class );
//                                intent.putExtra( "UserEmail", UserEmail );
//                                intent.putExtra( "UserPwd", UserPwd );
//                                intent.putExtra( "UserName", UserName );
//                                startActivity( intent );

                                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", 0);
                                SharedPreferences.Editor editor;
                                editor = sharedPreferences.edit();
                                editor.putString("existanceUserName",UserEmail);
                                editor.commit();


                                Intent intent = new Intent(LoginActivity.this, CalendarActivity.class);
                                startActivity(intent);

                            } else {//로그인 실패시
                                Toast.makeText( getApplicationContext(), "로그인에 실패하셨습니다.", Toast.LENGTH_SHORT ).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest( UserEmail, UserPwd, responseListener );
                RequestQueue queue = Volley.newRequestQueue( LoginActivity.this );
                queue.add( loginRequest );
            }
        });
    }
}