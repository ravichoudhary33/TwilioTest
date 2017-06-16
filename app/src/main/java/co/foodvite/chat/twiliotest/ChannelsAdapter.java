package co.foodvite.chat.twiliotest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.provider.SyncStateContract;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.StatusListener;

import java.util.List;

/**
 * Created by ravi on 15/6/17.
 */

public class ChannelsAdapter extends RecyclerView.Adapter<ChannelsAdapter.ViewHolder> {

    // store channels Descriptor list
    private List<ChannelModel> mChannelModels;
    // store context for easy access
    private Context mContext;

    // pass in the channels Descriptor into the constructor
    public ChannelsAdapter(Context context, List<ChannelModel> mChannelModels){
        this.mContext = context;
        this.mChannelModels = mChannelModels;
    }

    // easy access to the context object in the recyclerview
    private Context getContext(){
        return mContext;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView channelNameTextView;
        public TextView channelsMembersInfoTextView;
        private Context context;

        public ViewHolder(Context context, View itemView) {
            super(itemView);

            channelNameTextView = (TextView) itemView.findViewById(R.id.channel_name_text_view);
            channelsMembersInfoTextView = (TextView) itemView.findViewById(R.id.channel_members_info_text_view);
            // store the context
            this.context = context;
            // add a click listener to the entire row view
            itemView.setOnClickListener(this);
        }

        // handles the row being clicked
        @Override
        public void onClick(View v) {

            int position = getAdapterPosition(); // get item position
            // Check if an item was deleted, but the user clicked it before the UI removed it
            if(position != RecyclerView.NO_POSITION){
                ChannelModel channelModel = mChannelModels.get(position);
                // we can access the data with in the view
                Toast.makeText(context, channelModel.getFriendlyName(), Toast.LENGTH_SHORT).show();

                // get channel from channels model
                channelModel.getChannel(new CallbackListener<Channel>() {
                    @Override
                    public void onSuccess(final Channel channel) {
                        // join the channel and send it to message activity
                        channel.join(new StatusListener() {
                            @Override
                            public void onSuccess() {
                                Log.d("ChannelsAdapter", channel.getSid());
                                // start Message Activity
                                Intent i = new Intent(context, MessageActivity.class); // instead of v.getContext()->context can also be used
                                i.putExtra(Constants.EXTRA_CHANNEL_SID, channel.getSid());
                                i.putExtra(Constants.EXTRA_CHANNEL, (Parcelable) channel);
                                // start the activity
                                context.startActivity(i);
                            }
                        });

                    }
                });


            }

        }
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ChannelsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate the custom layout
        View channelsView = inflater.inflate(R.layout.item_channel, parent, false);

        // return a new view holder instance
        ViewHolder viewHolder = new ViewHolder(context, channelsView);

        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ChannelsAdapter.ViewHolder holder, int position) {
        // get the data model based on position
        ChannelModel channelModel = mChannelModels.get(position);

        // set the item view based on views and data model
        TextView channelNameTextView = holder.channelNameTextView;
        channelNameTextView.setText(channelModel.getFriendlyName());
        TextView channelMemberInfoTextView = holder.channelsMembersInfoTextView;
        channelMemberInfoTextView.setText(channelModel.getSid());
    }

    @Override
    public int getItemCount() {
        return mChannelModels.size();
    }


}