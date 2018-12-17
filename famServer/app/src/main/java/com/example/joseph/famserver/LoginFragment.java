package com.example.joseph.famserver;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.joseph.famserver.Models.UserModel;
import com.example.joseph.famserver.Models.registerSuccessModel;
import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;

public class LoginFragment extends Fragment implements canGetServerResponse {
    private EditText serverHost;
    private EditText serverPort;
    private EditText username;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private RadioGroup gender;
    private RadioButton male;
    private Button signin;
    private Button register;
    private Gson g;
    private registerSuccessModel loginCredentials;

    private MainActivity parent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("created!");
        super.onCreate(savedInstanceState);
        g = new Gson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.login_fragment, container, false);

        serverHost = v.findViewById(R.id.serverHost);
        serverPort = v.findViewById(R.id.serverPort);
        username = v.findViewById(R.id.username);
        password = v.findViewById(R.id.password);
        firstName = v.findViewById(R.id.firstName);
        lastName = v.findViewById(R.id.lastName);
        email = v.findViewById(R.id.email);
        gender = v.findViewById(R.id.gender);
        male = v.findViewById(R.id.male);
        signin = v.findViewById(R.id.singin);
        register = v.findViewById(R.id.register);
        serverPort.setText("8000");
        username.setText("dust");
        password.setText("dust");
        serverHost.setText("10.37.69.171");
        TextWatcher tw = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                checkIfWeAllGood();
            }
            @Override public void afterTextChanged(Editable editable) {
            }
        };

        serverHost.addTextChangedListener(tw);
        serverPort.addTextChangedListener(tw);
        username.addTextChangedListener(tw);
        password.addTextChangedListener(tw);
        firstName.addTextChangedListener(tw);
        lastName.addTextChangedListener(tw);
        email.addTextChangedListener(tw);
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedID) {
                checkIfWeAllGood();
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    URL serverLoc = new URL("http://" + serverHost.getText().toString() + ":" + serverPort.getText().toString()+"/user/login");
                    UserModel u = new UserModel(username.getText().toString(),password.getText().toString(),
                                            null,null,null,null, null);
                    String js = g.toJson(u,UserModel.class);
                    serverCall s = new serverCall(LoginFragment.this,serverLoc,js,"POST",null,0);
                    s.start();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                signin.setEnabled(false);
                register.setEnabled(false);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    URL serverLoc = new URL("http://" + serverHost.getText().toString() + ":" + serverPort.getText().toString()+"/user/register");
                    String gend;
                    if (male.isChecked()) {
                        gend = "m";
                    }
                    else {
                        gend = "f";
                    }
                    UserModel u = new UserModel(username.getText().toString(),
                                                password.getText().toString(),
                                                email.getText().toString(),
                                                firstName.getText().toString(),
                                                lastName.getText().toString(),
                                                gend,
                                                null);
                    String js = g.toJson(u,UserModel.class);
                    serverCall s = new serverCall(LoginFragment.this,serverLoc,js,"POST",null,0);
                    s.start();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                signin.setEnabled(false);
                register.setEnabled(false);
            }
        });

        signin.setEnabled(true);
        register.setEnabled(false);

        return v;
    }

    private void checkIfWeAllGood() {
        if (!serverHost.getText().toString().equals("") &&
                !serverPort.getText().toString().equals("") &&
                !username.getText().toString().equals("") &&
                !password.getText().toString().equals("")) {
            signin.setEnabled(true);
            if (!firstName.getText().toString().equals("") &&
                    !lastName.getText().toString().equals("") &&
                    !email.getText().toString().equals("") &&
                    gender.getCheckedRadioButtonId()!= -1) {
                register.setEnabled(true);
            }
        }
    }

    public void setParent(MainActivity parent) {
        this.parent = parent;
    }

    @Override
    public void getServerResponse(final serverCall s, final int endPoint) {
         parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loginCredentials = g.fromJson(s.getResponseBody(), registerSuccessModel.class);
                if (loginCredentials == null) {
                    Toast.makeText(parent, R.string.failedToast,Toast.LENGTH_LONG).show();
                    signin.setEnabled(true);
                    register.setEnabled(true);
                }
                else if (loginCredentials.getMessage()!=null) {
                    Toast.makeText(parent, loginCredentials.getMessage(),Toast.LENGTH_LONG).show();
                    signin.setEnabled(true);
                    register.setEnabled(true);
                }
                else {
                    String serverLoc = "http://" + serverHost.getText().toString() + ":" + serverPort.getText().toString();
                    parent.setLoginCredentials(serverLoc, loginCredentials.getAuthToken(), loginCredentials.getUserName());
                    parent.getPeople();
                    parent.getEvents();
                }
            }
         });
    }
}
