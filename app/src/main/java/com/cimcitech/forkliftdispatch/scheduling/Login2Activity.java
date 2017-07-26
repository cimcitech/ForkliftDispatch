package com.cimcitech.forkliftdispatch.scheduling;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cimcitech.forkliftdispatch.LoginActivity;
import com.cimcitech.forkliftdispatch.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Login2Activity extends AppCompatActivity {

    @Bind(R.id.name_et)
    EditText nameEt;
    @Bind(R.id.password_et)
    EditText passwordEt;
    @Bind(R.id.login_btn)
    Button loginBtn;
    @Bind(R.id.driver_login)
    TextView driverLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.driver_login)
    public void driverLogin() {
        startActivity(new Intent(Login2Activity.this, LoginActivity.class));
        finish();

    }
}
