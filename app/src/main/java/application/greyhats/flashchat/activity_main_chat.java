package application.greyhats.flashchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class activity_main_chat extends AppCompatActivity {

    private String mDisplayName;
    private ListView mChatListView;
    private EditText mInputText;
    private ImageButton mSendButton;

    private DatabaseReference mDatabaseReference;

    private ChatListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        setUpdisplayname();
        // setup display name and get the Firebase reference

        mChatListView = findViewById(R.id.chat_list_view);
        mInputText = findViewById(R.id.messageInput);
        mSendButton = findViewById(R.id.sendButton);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();


        // send message when enter button is pressed
        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sendMessage();
                return true;
            }
        });


        //add onClick listner to send messag ewhen button is pressed
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

    }


    private void setUpdisplayname (){
        SharedPreferences prefs = getSharedPreferences(activity_register.CHAT_PREFS , MODE_PRIVATE);
        mDisplayName = prefs.getString(activity_register.DISPLAY_NAME_KEY , null);
        if (mDisplayName == null) mDisplayName = "Anonymous";
    }

    private void sendMessage(){
        Log.d("FlashChat","message is ready to be sent");
        String input = mInputText.getText().toString();
        if (!input.equals("")){
            InstantMessage chat = new InstantMessage(input , mDisplayName);
            Log.d("FlashChat" , "entering to mDatabaseReference");
            mDatabaseReference.child("messages").push().setValue(chat);
            mInputText.setText("");
        }
    }

    // Override the onStart() lifecycle method. Setup the adapter here.
    @Override
    public void onStart(){
        super.onStart();

        mAdapter = new ChatListAdapter(this , mDatabaseReference , mDisplayName);
        mChatListView.setAdapter(mAdapter);
    }


    @Override
    public void onStop(){
        super.onStop();

        // Remove the Firebase event listener on the adapter.

        mAdapter.cleanup();
    }

}