package application.greyhats.flashchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class activity_register extends AppCompatActivity {

    public static final String CHAT_PREFS = "chatPrefs";
    public static final String DISPLAY_NAME_KEY = "username";

    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mUsernameView;
    private TextView mPasswordView;
    private TextView mConfirmPasswordView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailView = findViewById(R.id.register_email);
        mUsernameView = findViewById(R.id.register_username);
        mPasswordView = findViewById(R.id.register_password);
        mConfirmPasswordView = findViewById(R.id.register_confirm_password);

        mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId==R.integer.register_form_finished || actionId == EditorInfo.IME_NULL){
                    attemptRegistration();
                    return true;
                }
                return false;
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }

    public void signUp (View view){
        attemptRegistration();
    }

    public void attemptRegistration(){

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String Email = mEmailView.getText().toString();
        String Password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(Password) || !isPasswordValid(Password)){
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(Email)){
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(Email)){
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel){
            focusView.requestFocus();
        }else {
            // to call function to register user to firebase
            createFirebaseUser();
        }

    }

    private boolean isPasswordValid( String Password){
        String confirmPassword = mConfirmPasswordView.getText().toString();
        return confirmPassword.equals(Password) && Password.length()>=4;
    }

    private boolean isEmailValid (String Email){
        return Email.contains("@");
    }

    // function to register user to firebase
    private void createFirebaseUser(){
        String Email = mEmailView.getText().toString();
        String Password = mPasswordView.getText().toString();
        mAuth.createUserWithEmailAndPassword(Email , Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("FlashChat" , "createUser onComplete " +task.isSuccessful());
                if(!task.isSuccessful()){
                    Log.d("FlashChat" , "user creation failed ");
                    showErrorDialog("Registration attempt failed");
                } else {
                    saveDisplayName();
                    Intent myintent = new Intent(activity_register.this , MainActivity.class);
                    finish();
                    startActivity(myintent);
                }
            }
        });
    }


    // save display name to shared preferences
    private void saveDisplayName(){
        String displayName = mUsernameView.getText().toString();
        SharedPreferences prefs = getSharedPreferences(CHAT_PREFS , 0);
        prefs.edit().putString(DISPLAY_NAME_KEY , displayName).apply();
    }


    //to create an alert dialog box in case registration fails
    private void showErrorDialog( String message){

        new AlertDialog.Builder(this )
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok , null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}
