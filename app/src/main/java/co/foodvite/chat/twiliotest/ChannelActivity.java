package co.foodvite.chat.twiliotest;

import android.app.AlertDialog;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;

import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.ChannelDescriptor;
import com.twilio.chat.Channels;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ChatClientListener;
import com.twilio.chat.ErrorInfo;
import com.twilio.chat.Member;
import com.twilio.chat.Members;
import com.twilio.chat.Message;
import com.twilio.chat.Paginator;
import com.twilio.chat.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.ribot.easyadapter.EasyAdapter;

public class ChannelActivity extends AppCompatActivity implements ChatClientListener {

    private static final Logger logger = Logger.getLogger(ChannelActivity.class);
    private static final String TAG = "ChannelActivity";

    private ListView listView;
    private BasicChatClient                  basicClient;
    private Map<String, ChannelModel> channels = new HashMap<String, ChannelModel>();
    private List<ChannelModel> adapterContents = new ArrayList<>();
    private EasyAdapter<ChannelModel> adapter;
    private AlertDialog createChannelDialog;
    private Channels channelsObject;

    private static final Handler handler = new Handler();
    private AlertDialog          incomingChannelInvite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        // toolbar setup
        Toolbar channelToolbar = (Toolbar) findViewById(R.id.channel_toolbar);
        setSupportActionBar(channelToolbar);
        setTitle("Channel Activity");

        // instanciating
        basicClient = TwilioApplication.get().getBasicClient();
        basicClient.getChatClient().setListener(ChannelActivity.this);

        // get list of public channels
//        basicClient.getChatClient().getChannels().getPublicChannelsList(new CallbackListener<Paginator<ChannelDescriptor>>() {
//            @Override
//            public void onSuccess(Paginator<ChannelDescriptor> channelPaginator) {
//                for (ChannelDescriptor channel : channelPaginator.getItems()) {
//                    Log.d(TAG, "Channel named: " + channel.getFriendlyName());
//
//                }
//            }
//        });

        // get list of user channels
//        basicClient.getChatClient().getChannels().getUserChannelsList(new CallbackListener<Paginator<ChannelDescriptor>>() {
//            @Override
//            public void onSuccess(Paginator<ChannelDescriptor> channelPaginator) {
//                for (ChannelDescriptor channel : channelPaginator.getItems()) {
//                    Log.d(TAG, "Channel named: " + channel.getFriendlyName());
//                }
//            }
//        });

        // create a public channel
//        basicClient.getChatClient().getChannels().channelBuilder().withFriendlyName("Study")
//                .withType(Channel.ChannelType.PUBLIC)
//                .build(new CallbackListener<Channel>() {
//                    @Override
//                    public void onSuccess(Channel channel) {
//                        if (channel != null) {
//                            Log.d(TAG,"Success creating channel");
//                        }
//                    }
//
//                    @Override
//                    public void onError(ErrorInfo errorInfo) {
//                        Log.e(TAG,"Error creating channel: " + errorInfo.getMessage());
//                    }
//                });

        // get more recent message from the channel
        // first latest messages from general channels
        basicClient.getChatClient().getChannels().getPublicChannelsList(new CallbackListener<Paginator<ChannelDescriptor>>() {
            @Override
            public void onSuccess(Paginator<ChannelDescriptor> channelPaginator) {
                for (ChannelDescriptor channel : channelPaginator.getItems()) {

                    Log.d(TAG, "Channel named: " + channel.getFriendlyName());
                    channel.getChannel(new CallbackListener<Channel>() {
                        @Override
                        public void onSuccess(Channel channel) {
                            Log.d(TAG, channel.getSid());
                        }
                    });

                }
            }
        });






    }

    @Override
    public void onChannelJoined(Channel channel) {

    }

    @Override
    public void onChannelInvited(Channel channel) {

    }

    @Override
    public void onChannelAdded(Channel channel) {

    }

    @Override
    public void onChannelUpdated(Channel channel, Channel.UpdateReason updateReason) {

    }

    @Override
    public void onChannelDeleted(Channel channel) {

    }

    @Override
    public void onChannelSynchronizationChange(Channel channel) {

    }

    @Override
    public void onError(ErrorInfo errorInfo) {

    }

    @Override
    public void onUserUpdated(User user, User.UpdateReason updateReason) {

    }

    @Override
    public void onUserSubscribed(User user) {

    }

    @Override
    public void onUserUnsubscribed(User user) {

    }

    @Override
    public void onClientSynchronization(ChatClient.SynchronizationStatus synchronizationStatus) {

    }

    @Override
    public void onNotification(String s, String s1) {

    }

    @Override
    public void onNotificationSubscribed() {

    }

    @Override
    public void onNotificationFailed(ErrorInfo errorInfo) {

    }

    @Override
    public void onConnectionStateChange(ChatClient.ConnectionState connectionState) {

    }
}
