package application.greyhats.flashchat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ChatListAdapter extends BaseAdapter {

    private Activity mActivity;
    private DatabaseReference mDatabaseReference;
    private String mDisplayname;
    private ArrayList<DataSnapshot> mSnapshotList;

    private ChildEventListener mListner = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            mSnapshotList.add(dataSnapshot);
            notifyDataSetChanged();

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public ChatListAdapter (Activity activity , DatabaseReference ref , String name){
        mActivity = activity;
        mDisplayname = name;
        mDatabaseReference = ref.child("messages");
        mDatabaseReference.addChildEventListener(mListner);
        mSnapshotList = new ArrayList<>();
    }

    static class ViewHolder {
        TextView authorName;
        TextView body;
        LinearLayout.LayoutParams params1 , params2;
    }


    @Override
    public int getCount() {
        return mSnapshotList.size();
    }

    @Override
    public InstantMessage getItem(int position) {

        DataSnapshot snapshot = mSnapshotList.get(position);
        return snapshot.getValue(InstantMessage.class);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null ){
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chat_msg_row , parent , false);
            final ViewHolder holder = new ViewHolder();
            holder.authorName = (TextView) convertView.findViewById(R.id.author);
            holder.body = (TextView) convertView.findViewById(R.id.message);
            holder.params1 = (LinearLayout.LayoutParams) holder.authorName.getLayoutParams();
            holder.params2 = (LinearLayout.LayoutParams) holder.body.getLayoutParams();
            convertView.setTag(holder);
        }

        final InstantMessage message = getItem(position);
        final ViewHolder holder = (ViewHolder) convertView.getTag();

        String author = message.getAuthor();
        holder.authorName.setText(author);

        boolean isMe = message.getAuthor().equals(mDisplayname);
        setChatAppearance(isMe , holder);

        String msg = message.getMessage();
        holder.body.setText(msg);

        return convertView;
    }

    private void setChatAppearance (boolean isItMe , ViewHolder holder){

        if(isItMe){
            holder.params1.gravity = Gravity.END;
            holder.params2.gravity = Gravity.END;
            holder.authorName.setTextColor(Color.MAGENTA);
            holder.body.setBackgroundResource(R.drawable.bubble2);

        } else {

            holder.params1.gravity = Gravity.START;
            holder.params2.gravity = Gravity.START;
            holder.authorName.setTextColor(Color.BLUE);
            holder.body.setBackgroundResource(R.drawable.bubble1);

        }

    }

    public void cleanup(){
        mDatabaseReference.removeEventListener(mListner);
    }
}
