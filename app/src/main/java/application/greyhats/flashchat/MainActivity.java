package application.greyhats.flashchat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
    private TextView mPasswordView;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmailView = findViewById(R.id.login_email);
        mPasswordView = findViewById(R.id.login_password);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == 100 || actionId == EditorInfo.IME_NULL){
                    attemptLogin();
                    return true;
                }

                return false;
            }
        });

        mAuth = FirebaseAuth.getInstance();
    }

    public void signInExistingUser(View view) {
        attemptLogin();
    }

    public void registerNewUser(View view) {
        Intent my_intent = new Intent(MainActivity.this,activity_register.class);
        finish();
        startActivity(my_intent);
    }

    public void attemptLogin(){
        String Email = mEmailView.getText().toString();
        String Password = mPasswordView.getText().toString();

        if ( Email.equals("") || Password.equals("")) return ;
        else {
            Toast.makeText(this , "Login in progress..." , Toast.LENGTH_SHORT).show();

            mAuth.signInWithEmailAndPassword(Email , Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d("FlashChat" , "Oncomplete listener called " + task.isSuccessful());
                    if(!task.isSuccessful()){
                        Log.d("FlashChat" , "Login attempt failed");
                        showErrorDialog("There was an error signing in");
                    } else {
                        Intent myItntent = new Intent(MainActivity.this , activity_main_chat.class);
                        finish();
                        startActivity(myItntent);
                    }
                }
            });
        }
    }

    private void showErrorDialog( String message){
        new AlertDialog.Builder(this)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok , null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
