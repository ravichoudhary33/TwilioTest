package co.foodvite.chat.twiliotest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

/**
 * Created by ravi on 17/6/17.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {


    // store channels Descriptor list
    private List<MessageItem> mMessageItemList;
    // store context for easy access
    private Context mContext;

    // pass in the channels Descriptor into the constructor
    public MessagesAdapter(Context context, List<MessageItem> messageItemList){
        this.mContext = context;
        this.mMessageItemList = messageItemList;
    }

    private Context getContext(){
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // infalte message view
        View messageView = inflater.inflate(R.layout.item_message, parent, false);

        // return a new view holder instance
        ViewHolder viewHolder = new ViewHolder(context, messageView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MessageItem messageItem = mMessageItemList.get(position);
        TextView authorNameTextView = holder.messageAuthorName;
        authorNameTextView.setText(messageItem.getMessage().getAuthor());
        TextView messageBodyTextView = holder.messageBody;
        messageBodyTextView.setText(messageItem.getMessage().getMessageBody());
    }

    @Override
    public int getItemCount() {
        return mMessageItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView messageAuthorName;
        public TextView messageBody;
        private Context context;

        public ViewHolder(Context context, View itemView) {
            super(itemView);

            messageAuthorName = (TextView) itemView.findViewById(R.id.author_name_text_field);
            messageBody = (TextView) itemView.findViewById(R.id.message_text_view);
            this.context = context;
            // add a click listener to the entire row view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition(); // get item position
            if(position != RecyclerView.NO_POSITION){
                MessageItem messageItem = mMessageItemList.get(position);
                Toast.makeText(context, messageItem.getMessage().getAuthor(), Toast.LENGTH_SHORT).show();
            }

        }
    }
}
