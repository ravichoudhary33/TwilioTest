package co.foodvite.chat.twiliotest;

import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ChannelListener;
import com.twilio.chat.Channels;
import com.twilio.chat.Member;
import com.twilio.chat.Members;
import com.twilio.chat.Message;
import com.twilio.chat.Messages;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity implements ChannelListener{
    // variables declaration
    final static String TAG = "MessageActivity";
    private static final Logger logger = Logger.getLogger(MessageActivity.class);

    private RecyclerView messageRecyclerView;
    private MessagesAdapter adapter;

    private EditText inputText;
    private Channel channel;

    private String identity;
    private ArrayList<MessageItem> messageItemList = new ArrayList<>();
    private ArrayList<MessageItem> adapterContents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create the ui

        setContentView(R.layout.activity_message);

        Toolbar messageToolbar = (Toolbar) findViewById(R.id.message_toolbar);
        setSupportActionBar(messageToolbar);
        setTitle("My Channel Name");

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        createUI();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Intent intent = getIntent();
        if (intent != null) {
            channel = intent.getParcelableExtra(Constants.EXTRA_CHANNEL);
            if (channel != null) {
                setupRecyclerView(channel);
            }
        }

    }

    private void loadAndShowMessages()
    {
        final Messages messagesObject = channel.getMessages();
        if (messagesObject != null) {
            messagesObject.getLastMessages(50, new CallbackListener<List<Message>>() {
                @Override
                public void onSuccess(List<Message> messagesArray) {
                    messageItemList.clear();
                    Members  members = channel.getMembers();
                    if (messagesArray.size() > 0) {
                        for (int i = 0; i < messagesArray.size(); i++) {
                            messageItemList.add(new MessageItem(messagesArray.get(i), members, identity));
                        }
                    }
                    adapterContents.clear();
                    adapterContents.addAll(messageItemList);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }


    // setup the recycler view
    private void setupRecyclerView(Channel channel){

        messageRecyclerView = (RecyclerView) findViewById(R.id.message_recycler_view);
        adapter = new MessagesAdapter(this, adapterContents);
        messageRecyclerView.setAdapter(adapter);
        // set up layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(linearLayoutManager);
        //messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadAndShowMessages();
    }

    private void createUI(){

        if (getIntent() != null) {
            BasicChatClient basicClient = TwilioApplication.get().getBasicClient();
            identity = basicClient.getChatClient().getMyIdentity();
            String   channelSid = getIntent().getStringExtra(Constants.EXTRA_CHANNEL_SID);
            Channels channelsObject = basicClient.getChatClient().getChannels();
            channelsObject.getChannel(channelSid, new CallbackListener<Channel>() {
                @Override
                public void onSuccess(final Channel foundChannel)
                {
                    channel = foundChannel;
                    channel.addListener(MessageActivity.this);
                    MessageActivity.this.setTitle(
                            ((channel.getType() == Channel.ChannelType.PUBLIC) ? "PUB " : "PRIV ")
                                    + channel.getFriendlyName());

                    setupRecyclerView(channel);

                    /*messageRecyclerView = (RecyclerView)findViewById(R.id.message_recycler_view);
                    if (messageRecyclerView != null) {
                        messageRecyclerView.setsetTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                        messageRecyclerView.setStackFromBottom(true);
                        adapter.registerDataSetObserver(new DataSetObserver() {
                            @Override
                            public void onChanged()
                            {
                                super.onChanged();
                                messageRecyclerView.setSelection(adapter.getCount() - 1);
                            }
                        });
                    }*/
                    setupInput();
                }
            });
        }

    }

    private void setupInput()
    {
        // Setup our input methods. Enter key on the keyboard or pushing the send button
        EditText inputText = (EditText)findViewById(R.id.message_input_edit_text);
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }
            @Override
            public void afterTextChanged(Editable s)
            {
                if (channel != null) {
                    channel.typing();
                }
            }
        });

        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent)
            {
                if (actionId == EditorInfo.IME_NULL
                        && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        findViewById(R.id.message_send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                sendMessage();
            }
        });
    }

    private void sendMessage()
    {
        inputText = (EditText)findViewById(R.id.message_input_edit_text);
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            final Messages messagesObject = this.channel.getMessages();

            messagesObject.sendMessage(input, new ToastStatusListener(
                    "Successfully sent message",
                    "Error sending message") {
                @Override
                public void onSuccess()
                {
                    super.onSuccess();
                    loadAndShowMessages();
                    // adapter.notifyDataSetChanged();
                    inputText.setText("");
                }
            });
        }

        inputText.requestFocus();
    }



    @Override
    public void onMessageAdded(Message message) {
        setupRecyclerView(this.channel);

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
        logger.d("Received onSynchronizationChanged callback " + channel.getFriendlyName());
    }

}
