package com.example.sagutdinov521522;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sagutdinov521522.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private final static String USERS_FILE_NAME = "users.txt";
    private final static String BOX_KEY = "box";
    SharedPreferences shared;
    File source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        source = new File(this.getExternalFilesDir(null), "source.txt");
        shared = getPreferences(MODE_PRIVATE);

        final EditText login = findViewById(R.id.login);
        final EditText password = findViewById(R.id.password);
        final CheckBox boxExternal = findViewById(R.id.boxExternal);

        if (shared.contains(BOX_KEY)) {
            boxExternal.setChecked(shared.getBoolean(BOX_KEY, false));
        }

        boxExternal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                shared.edit()
                        .putBoolean(BOX_KEY, isChecked)
                        .apply();
            }
        });

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean loginSuccess = false;
                String inName = login.getText().toString();
                String inPassword = password.getText().toString();
                if (boxExternal.isChecked()) {
                    try (FileReader fromSource = new FileReader(source)) {
                        BufferedReader br = new BufferedReader(fromSource);
                        loginSuccess = findUser(br, inName, inPassword);
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.fileEx),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    try (FileInputStream streamInUsers = openFileInput(USERS_FILE_NAME);
                         InputStreamReader ReadFromUsers = new InputStreamReader(streamInUsers);
                         BufferedReader brUsers = new BufferedReader(ReadFromUsers)) {
                        loginSuccess = findUser(brUsers, inName, inPassword);
                        brUsers.close();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.fileEx),
                                Toast.LENGTH_LONG).show();
                    }
                }
                if (!loginSuccess) {
                    Toast.makeText(getApplicationContext(), getString(R.string.loginFail),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        Button btnRegistration = findViewById(R.id.btnRegistration);
        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = login.getText().toString() + '\n' + password.getText().toString();
                if (boxExternal.isChecked()) {
                    try (FileWriter toSource = new FileWriter(source, true)) {
                        toSource.append(user);
                        Toast.makeText(getApplicationContext(), getString(R.string.okFileWrite),
                                Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.fileEx),
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    try (FileOutputStream streamToLogins = openFileOutput(USERS_FILE_NAME, MODE_PRIVATE);

                         OutputStreamWriter writeToUsers = new OutputStreamWriter(streamToLogins);

                         BufferedWriter bwUsers = new BufferedWriter(writeToUsers)) {
                        ;

                        bwUsers.write(user);

                        bwUsers.close();

                        Toast.makeText(getApplicationContext(), getString(R.string.okFileWrite),
                                Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.fileEx),
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private boolean findUser(BufferedReader brUsers, String inName, String inPassword) {
        String log = "";
        String pas = "";
        try {
            while ((log = brUsers.readLine()) != null && (pas = brUsers.readLine()) != null) {
                if (log.equals(inName) && pas.equals(inPassword)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.okLogin),
                            Toast.LENGTH_LONG).show();
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.fileEx),
                    Toast.LENGTH_LONG).show();
        }
        return false;

    }
}
