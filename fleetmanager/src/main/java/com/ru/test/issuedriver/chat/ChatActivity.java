/**
 * Copyright Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ru.test.issuedriver.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ru.test.issuedriver.MyActivity;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.data.FriendlyMessage;
import com.ru.test.issuedriver.data.chat_doc;
import com.ru.test.issuedriver.data.order;
import com.ru.test.issuedriver.helpers.firestoreHelper;
import com.ru.test.issuedriver.helpers.fsm.sender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatActivity extends MyActivity {

    private ChatViewModel chatViewModel;


    private static final String TAG = "ChatActivity";
    public static final String MESSAGES_CHILD_OLD = "messages";
    private static final int REQUEST_INVITE = 1;
    private static final int REQUEST_IMAGE = 2;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private String mUsername;
    private String mPhotoUrl;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;
    private static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";

    private ImageView mSendButton;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;
    private ImageView mAddMessageImageView;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>
            mFirebaseAdapter;

    private order currentOrder;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

            mUsername = CurrentUser.fio;
            if (CurrentUser.photoPath.length() != 0) {
                mPhotoUrl = CurrentUser.photoPath;
            }

            currentOrder = getIntent().getParcelableExtra("order");
            initChatData();

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);

        actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Чат");
        }
    }

    private void initChatData() {
        chatViewModel =
                ViewModelProviders.of(ChatActivity.this).get(ChatViewModel.class);
        chatViewModel.Init(currentOrder);

        chatViewModel.getCurrentChat_roomLiveData().observe(this, new Observer<chat_doc>() {
            @Override
            public void onChanged(chat_doc chat_room) {
                Log.d("TAG", "DocumentSnapshot successfully written!");
                if(chat_room != null) {
                    startChat(chat_room);
                    return;
                }

                chatViewModel.createChatRoom(currentOrder);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                 finish();
                actionBar.setDisplayHomeAsUpEnabled(false);
                //Toast.makeText(getApplicationContext(),"Back button clicked", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private void startChat(chat_doc chat_room) {

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        SnapshotParser<FriendlyMessage> parser = new SnapshotParser<FriendlyMessage>() {
            @Override
            public FriendlyMessage parseSnapshot(DataSnapshot dataSnapshot) {
                FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                if (friendlyMessage != null) {
                    friendlyMessage.setId(dataSnapshot.getKey());
                }
                return friendlyMessage;
            }
        };

        //DatabaseReference messagesRef = mFirebaseDatabaseReference.child(MESSAGES_CHILD);
        DatabaseReference messagesRef = mFirebaseDatabaseReference.child("chats").child(chat_room.id);
        FirebaseRecyclerOptions<FriendlyMessage> options =
                new FirebaseRecyclerOptions.Builder<FriendlyMessage>()
                        .setQuery(messagesRef, parser)
                        .build();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(options) {
            @Override
            public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new MessageViewHolder(inflater.inflate(R.layout.item_message, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(final MessageViewHolder viewHolder,
                                            int position,
                                            FriendlyMessage friendlyMessage) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                if(chatViewModel.CurrentChat_room.getIsSenderMember1(friendlyMessage.getSender_id())){
                    fillMember1Message(viewHolder, friendlyMessage);
                } else {
                    fillMember2Message(viewHolder, friendlyMessage);
                }
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mSendButton = (ImageView) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chatViewModel.CurrentChat_room == null)
                    return;

                FriendlyMessage friendlyMessage = new
                        FriendlyMessage(mMessageEditText.getText().toString(),
                        MyActivity.CurrentUser.UUID, //  mUsername,
                        //mPhotoUrl,
                        null /* no image */);
//                mFirebaseDatabaseReference.child(MESSAGES_CHILD)
                mFirebaseDatabaseReference.child("chats").child(chatViewModel.CurrentChat_room.id)
                        .push().setValue(friendlyMessage);
                mMessageEditText.setText("");

                sender.send(chatViewModel.CurrentOrder, MyActivity.CurrentUser.is_performer ? sender.orderStateType.msg_from_performer : sender.orderStateType.msg_from_customer);
            }
        });

        mAddMessageImageView = (ImageView) findViewById(R.id.addMessageImageView);
        mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });

        mFirebaseAdapter.startListening();
    }

    private void fillMember1Message(MessageViewHolder viewHolder, FriendlyMessage friendlyMessage) {
        viewHolder.member1msg.setVisibility(View.VISIBLE);
        viewHolder.member2msg.setVisibility(View.GONE);

        if (friendlyMessage.getText() != null) {
            viewHolder.messageTextView.setText(friendlyMessage.getText());
            viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
            viewHolder.messageImageView.setVisibility(ImageView.GONE);
        } else if (friendlyMessage.getImageUrl() != null) {
            String imageUrl = friendlyMessage.getImageUrl();
            if (imageUrl.startsWith("gs://")) {
                StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReferenceFromUrl(imageUrl);
                storageReference.getDownloadUrl().addOnCompleteListener(
                        new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    String downloadUrl = task.getResult().toString();
                                    Glide.with(viewHolder.messageImageView.getContext())
                                            .load(downloadUrl)
                                            .into(viewHolder.messageImageView);
                                } else {
                                    Log.w(TAG, "Getting download url was not successful.",
                                            task.getException());
                                }
                            }
                        });
            } else {
                Glide.with(viewHolder.messageImageView.getContext())
                        .load(friendlyMessage.getImageUrl())
                        .into(viewHolder.messageImageView);
            }
            viewHolder.messageImageView.setVisibility(ImageView.VISIBLE);
            viewHolder.messageTextView.setVisibility(TextView.GONE);
        }



        viewHolder.messengerTextView.setText(chatViewModel.CurrentChat_room.getSenderName(friendlyMessage.getSender_id()));


        String photo = chatViewModel.CurrentChat_room.getSenderPhoto(friendlyMessage.getSender_id());
        if (photo == null || photo.length() == 0) {
            viewHolder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(ChatActivity.this,
                    R.drawable.ic_account_circle_black_36dp));
        } else {
            Glide.with(ChatActivity.this)
                    .load(photo)
                    .into(viewHolder.messengerImageView);
        }
    }
    private void fillMember2Message(MessageViewHolder viewHolder, FriendlyMessage friendlyMessage) {
        viewHolder.member2msg.setVisibility(View.VISIBLE);
        viewHolder.member1msg.setVisibility(View.GONE);

        if (friendlyMessage.getText() != null) {
            viewHolder.messageTextView2.setText(friendlyMessage.getText());
            viewHolder.messageTextView2.setVisibility(TextView.VISIBLE);
            viewHolder.messageImageView2.setVisibility(ImageView.GONE);
        } else if (friendlyMessage.getImageUrl() != null) {
            String imageUrl = friendlyMessage.getImageUrl();
            if (imageUrl.startsWith("gs://")) {
                StorageReference storageReference = FirebaseStorage.getInstance()
                        .getReferenceFromUrl(imageUrl);
                storageReference.getDownloadUrl().addOnCompleteListener(
                        new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    String downloadUrl = task.getResult().toString();
                                    Glide.with(viewHolder.messageImageView2.getContext())
                                            .load(downloadUrl)
                                            .into(viewHolder.messageImageView2);
                                } else {
                                    Log.w(TAG, "Getting download url was not successful.",
                                            task.getException());
                                }
                            }
                        });
            } else {
                Glide.with(viewHolder.messageImageView2.getContext())
                        .load(friendlyMessage.getImageUrl())
                        .into(viewHolder.messageImageView2);
            }
            viewHolder.messageImageView2.setVisibility(ImageView.VISIBLE);
            viewHolder.messageTextView2.setVisibility(TextView.GONE);
        }



        viewHolder.messengerTextView2.setText(chatViewModel.CurrentChat_room.getSenderName(friendlyMessage.getSender_id()));


        String photo = chatViewModel.CurrentChat_room.getSenderPhoto(friendlyMessage.getSender_id());
        if (photo == null || photo.length() == 0) {
            viewHolder.messengerImageView2.setImageDrawable(ContextCompat.getDrawable(ChatActivity.this,
                    R.drawable.ic_account_circle_black_36dp));
        } else {
            Glide.with(ChatActivity.this)
                    .load(photo)
                    .into(viewHolder.messengerImageView2);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
    }

    @Override
    public void onPause() {
        if(mFirebaseAdapter != null)
            mFirebaseAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mFirebaseAdapter != null)
            mFirebaseAdapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    Log.d(TAG, "Uri: " + uri.toString());

                    FriendlyMessage tempMessage = new FriendlyMessage(null, MyActivity.CurrentUser.UUID, //mPhotoUrl,
                            LOADING_IMAGE_URL);
                    if(chatViewModel.CurrentChat_room != null)
                        mFirebaseDatabaseReference.child("chats").child(chatViewModel.CurrentChat_room.id).push()   //mFirebaseDatabaseReference.child(MESSAGES_CHILD).push()
                            .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError,
                                                       DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        String key = databaseReference.getKey();
                                        StorageReference storageReference =
                                                FirebaseStorage.getInstance()
                                                        .getReference(mFirebaseUser.getUid())
                                                        .child(key)
                                                        .child(uri.getLastPathSegment());

                                        putImageInStorage(storageReference, uri, key);
                                    } else {
                                        Log.w(TAG, "Unable to write message to database.",
                                                databaseError.toException());
                                    }
                                }
                            });
                }
            }
        }
    }

    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
        storageReference.putFile(uri).addOnCompleteListener(ChatActivity.this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            task.getResult().getMetadata().getReference().getDownloadUrl()
                                    .addOnCompleteListener(ChatActivity.this,
                                            new OnCompleteListener<Uri>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Uri> task) {
                                                    if (task.isSuccessful()) {
                                                        FriendlyMessage friendlyMessage =
                                                                new FriendlyMessage(null, MyActivity.CurrentUser.UUID, //mPhotoUrl,
                                                                        task.getResult().toString());
                                                        mFirebaseDatabaseReference.child("chats").child(chatViewModel.CurrentChat_room.id).child(key)            //mFirebaseDatabaseReference.child(MESSAGES_CHILD).child(key)
                                                                .setValue(friendlyMessage);
                                                    }
                                                }
                                            });
                        } else {
                            Log.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }

}
