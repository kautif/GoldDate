package com.example.golddate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.golddate.adapter.ChatRecyclerAdapter;
import com.example.golddate.util.Chat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Distribution;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mChatRecyclerView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private EditText mChatText;
    private ImageView mSendBtn;
    private String docID;
    private ChatRecyclerAdapter mChatRecyclerAdapter;
    List<Chat> mChatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mChatList = new ArrayList<>();

        docID = getIntent().getStringExtra("doc_id");

        mChatRecyclerView = findViewById(R.id.chat_display_recyclerView);
        mChatRecyclerView.setHasFixedSize(true);
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mChatText = findViewById(R.id.chat_editText);
        mSendBtn = findViewById(R.id.chat_button);

        mChatRecyclerAdapter = new ChatRecyclerAdapter(this, mChatList);
        mChatRecyclerView.setAdapter(mChatRecyclerAdapter);

        mStore.collection("Messages").orderBy("time_stamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange doc : value.getDocumentChanges()) {
                    DocumentSnapshot snapshot = doc.getDocument();
                    Chat chat = snapshot.toObject(Chat.class);
                    if (chat.getFrom().equals(mAuth.getCurrentUser().getUid()) || chat.getFrom().equals(docID) &&
                            chat.getTo().equals(mAuth.getCurrentUser().getUid()) || chat.getTo().equals(docID)) {
                        mChatList.add(chat);
                        mChatRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mChatText.getText().toString().isEmpty()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("message", mChatText.getText().toString());
                    map.put("from", mAuth.getCurrentUser().getUid());
                    map.put("to", docID);
                    map.put("time_stamp", new Date());
                    Date time = Calendar.getInstance().getTime();
                    SimpleDateFormat date = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                    SimpleDateFormat militaryTime = new SimpleDateFormat("HH:mm:ss");

//                    map.put("date", date.format(time));
//                    map.put("time", militaryTime.format(new Date()));

                    mStore.collection("Messages").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                mChatText.setText("");
                                Toast.makeText(ChatActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        Toast.makeText(this, getIntent().getStringExtra("doc_id"), Toast.LENGTH_SHORT).show();
    }
}
