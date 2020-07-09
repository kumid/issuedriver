package com.ru.test.issuedriver.chat;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ru.test.issuedriver.R;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    TextView messageTextView, messageTextView2;
    ImageView messageImageView, messageImageView2;
    TextView messengerTextView, messengerTextView2;
    CircleImageView messengerImageView, messengerImageView2;
    View member1msg, member2msg;

    public MessageViewHolder(View v) {
        super(v);
        member1msg =  itemView.findViewById(R.id.member1msg);
        messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
        messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
        messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
        messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);

        member2msg =  itemView.findViewById(R.id.member2msg);
        messageTextView2 = (TextView) itemView.findViewById(R.id.messageTextView2);
        messageImageView2 = (ImageView) itemView.findViewById(R.id.messageImageView2);
        messengerTextView2 = (TextView) itemView.findViewById(R.id.messengerTextView2);
        messengerImageView2 = (CircleImageView) itemView.findViewById(R.id.messengerImageView2);
    }


}
