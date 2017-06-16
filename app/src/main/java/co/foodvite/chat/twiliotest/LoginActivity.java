package co.foodvite.chat.twiliotest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.twilio.chat.CallbackListener;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ErrorInfo;

public class LoginActivity extends AppCompatActivity implements BasicChatClient.LoginListener {

    private static final Logger logger = Logger.getLogger(LoginActivity.class);

    private static final String TAG = "LoginActivity";
    private static final String ACCESS_TOKEN_SERVICE_URL = "http://c444cd9f.ngrok.io/token";  //ngrok url
    private static final String    DEFAULT_CLIENT_NAME = "TestUser";
    private ProgressDialog progressDialog;
    private Button login;
    private EditText clientNameTextBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // toolbar setup
        Toolbar loginToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(loginToolbar);
        setTitle("Login Activity");

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = sharedPreferences.getString("userName", DEFAULT_CLIENT_NAME);

        this.clientNameTextBox = (EditText)findViewById(R.id.client_name_edit_text);
        this.clientNameTextBox.setText(userName);

        this.login = (Button) findViewById(R.id.sign_in_button);
        this.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String idChosen = clientNameTextBox.getText().toString();
                sharedPreferences.edit().putString("userName", idChosen).apply();

                String url = Uri.parse(ACCESS_TOKEN_SERVICE_URL)
                        .buildUpon()
                        .appendQueryParameter("identity", idChosen)
                        .build()
                        .toString();
                // logger.d("url string : " + url);
                TwilioApplication.get().getBasicClient().login(idChosen, url, LoginActivity.this);
            }
        });
    }

    @Override
    public void onLoginStarted() {
        logger.d("Log in started");
        progressDialog = ProgressDialog.show(this, "", "Logging in. Please wait...", true);
    }

    @Override
    public void onLoginFinished() {
        progressDialog.dismiss();
        logger.d("Successfully Logged In!");
        Intent intent = new Intent(this, ChannelActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLoginError(String errorMessage) {
        progressDialog.dismiss();
        TwilioApplication.get().showToast("Error logging in : " + errorMessage, Toast.LENGTH_LONG);
    }

    @Override
    public void onLogoutFinished() {
        TwilioApplication.get().showToast("Log out finished");
    }
}
