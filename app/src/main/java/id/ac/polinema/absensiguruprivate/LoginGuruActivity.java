package id.ac.polinema.absensiguruprivate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import id.ac.polinema.absensiguruprivate.model.User;
import id.ac.polinema.absensiguruprivate.rest.ApiClient;
import id.ac.polinema.absensiguruprivate.rest.ApiInterface;
import id.ac.polinema.absensiguruprivate.helper.Session;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginGuruActivity extends AppCompatActivity {
    private EditText inputUsername, inputPassword;
    private TextView result;
    private Button loginButton;
    private ConstraintLayout loginForm;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_guru);

        session = new Session(getApplicationContext());
        inputUsername = findViewById(R.id.edt_username_guru);
        inputPassword = findViewById(R.id.edt_password_guru);
        result = findViewById(R.id.txt_login_admin);
        loginButton = findViewById(R.id.btn_login_guru);
        loginForm = findViewById(R.id.login_guru);

        loginButton = findViewById(R.id.btn_login_guru);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin(inputUsername.getText().toString(), inputPassword.getText().toString());
            }
        });
    }

    private void userLogin(String username, String password) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> call = apiInterface.loginGuru(new User(username, password));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONArray json = new JSONArray(response.body().string());
                        String username = json.getJSONObject(0).getString("username");
                        String password = json.getJSONObject(0).getString("password");
                        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                        session.setLoggedInStatus(true);
                        session.setUsername(username);
                        session.setPassword(password);
                        session.setLoginTime(currentTime);
                        session.setDate(currentDate);

                        Intent intent = new Intent(getApplicationContext(), GuruActivity.class);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Credentials are not Valid.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void switchLoginAdmin(View view) {
        Intent intent = new Intent(LoginGuruActivity.this, LoginAdminActivity.class);
        startActivity(intent);
    }
}
