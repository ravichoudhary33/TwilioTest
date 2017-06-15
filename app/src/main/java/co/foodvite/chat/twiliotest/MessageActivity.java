package co.foodvite.chat.twiliotest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.twilio.chat.Channel;
import com.twilio.chat.ChannelListener;
import com.twilio.chat.Member;
import com.twilio.chat.Message;

import org.w3c.dom.Text;

public class MessageActivity extends AppCompatActivity implements ChannelListener{
    // variables declaration
    private static final Logger logger = Logger.getLogger(MessageActivity.class);
    private ListView messageListView;
    private EditText inputText;
    private Channel channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create the ui
        createUI();
    }

    @Override
    protected void onResume(){
        super.onResume();
         Intent i = getIntent();
        if(i != null){
            Channel channel = getIntent().getParcelableExtra(Constants.EXTRA_CHANNEL);
            if(channel != null){
                // then set the title to channel name
                setTitle(channel.getFriendlyName());
            }
        }
    }

    private void createUI(){
        // set the layout
        setContentView(R.layout.activity_message);
        TextView messageTextView = (TextView) findViewById(R.id.message_text_view);

        // get the toolbar
        Toolbar messageToolbar = (Toolbar) findViewById(R.id.message_toolbar);
        setSupportActionBar(messageToolbar);
        setTitle("My Channel Name");

        // get the intent
        if(getIntent() != null){
            String channelSID = getIntent().getStringExtra(Constants.EXTRA_CHANNEL_SID);
            messageTextView.setText(channelSID);
        }

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
