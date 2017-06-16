package co.foodvite.chat.twiliotest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ChannelListener;
import com.twilio.chat.Channels;
import com.twilio.chat.Member;
import com.twilio.chat.Message;
import com.twilio.chat.Messages;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity implements ChannelListener{
    // variables declaration
    final static String TAG = "MessageActivity";
    private static final Logger logger = Logger.getLogger(MessageActivity.class);
    private ListView messageListView;
    private EditText inputText;
    private Channel mChannel;
    private List<Message> mMessages = new ArrayList<>();
    private String identity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create the ui
        createUI();
    }

    @Override
    protected void onResume(){
        super.onResume();

        /*****************get the intent and set up the recycler view***********************/
        Intent intent = getIntent();
        if(intent != null){
            mChannel = intent.getParcelableExtra(Constants.EXTRA_CHANNEL);
            if(mChannel != null){
                // TODO: setup the recycler view
                setTitle(mChannel.getFriendlyName());

                Messages msg = mChannel.getMessages();
                if(msg != null){
                    msg.getLastMessages(4, new CallbackListener<List<Message>>() {
                        @Override
                        public void onSuccess(List<Message> messages) {

                            for(Message m : messages){
                                Log.d(TAG, m.getMessageBody());
                            }

                        }
                    });
                }
            }
        }
        /*************************************************************************************/

//        Messages messagesObject = mChannel.getMessages();
//        if(messagesObject != null){
//            Log.d(TAG, "Inside messageObject");
//            messagesObject.getLastMessages(50, new CallbackListener<List<Message>>() {
//                @Override
//                public void onSuccess(List<Message> messages) {
//                    for(Message msg : messages){
//                        // save the message
//                        mMessages.add(msg);
//                        Log.d(TAG, msg.getSid());
//
//                    }
//                }
//            });
//        }

        /*for(Message msg : mMessages){
            Log.d(TAG, msg.getMessageBody());
        }*/

    }


    private void createUI(){

        /***************************************************************************/
        // set the layout
        setContentView(R.layout.activity_message);
        /*TextView messageTextView = (TextView) findViewById(R.id.message_text_view);*/
        // get the toolbar
        Toolbar messageToolbar = (Toolbar) findViewById(R.id.message_toolbar);
        setSupportActionBar(messageToolbar);
        setTitle("My Channel Name");
        /***************************************************************************/

        if(getIntent() != null){

            String channelSID = getIntent().getStringExtra(Constants.EXTRA_CHANNEL_SID);
            Log.d(TAG, channelSID);
            /*messageTextView.setText(channelSID);*/
            // instanciate the basic chat client and get the identity and reterive the channels
            //BasicChatClient basicClient = TwilioApplication.get().getBasicClient();
            //identity = basicClient.getChatClient().getMyIdentity();
            // log the identity
            //Log.d(TAG, identity);
            /*Channels channelsObject = basicClient.getChatClient().getChannels();
            channelsObject.getChannel(channelSID, new CallbackListener<Channel>() {
                @Override
                public void onSuccess(Channel channel) {
                    mChannel = channel;
                    Log.d(TAG, mChannel.getFriendlyName());

                    *//*if(mChannel != null){
                        // TODO: setup the recycler view
                        setTitle(mChannel.getFriendlyName());

                        Messages msg = mChannel.getMessages();
                        msg.getLastMessages(4, new CallbackListener<List<Message>>() {
                            @Override
                            public void onSuccess(List<Message> messages) {
                                Log.d(TAG, String.valueOf(messages.size()));
                                for(Message m : messages){
                                    Log.d(TAG, m.getMessageBody());
                                }

                            }
                        });
                    }*//*
                }
            });*/
        }

        //Intent intent = getIntent();


    }

    @Override
    public void onMessageAdded(Message message) {

    }

    @Override
    public void onMessageUpdated(Message message) {

    }

    @Override
    public void onMessageDeleted(Message message) {

    }

    @Override
    public void onMemberAdded(Member member) {

    }

    @Override
    public void onMemberUpdated(Member member) {

    }

    @Override
    public void onMemberDeleted(Member member) {

    }

    @Override
    public void onTypingStarted(Member member) {

    }

    @Override
    public void onTypingEnded(Member member) {

    }

    @Override
    public void onSynchronizationChanged(Channel channel) {

    }
}
