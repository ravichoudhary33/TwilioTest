package co.foodvite.chat.twiliotest;

import android.app.AlertDialog;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.twilio.chat.CallbackListener;
import com.twilio.chat.Channel;
import com.twilio.chat.Channel.ChannelType;
import com.twilio.chat.ChannelDescriptor;
import com.twilio.chat.Channels;
import com.twilio.chat.ChatClient;
import com.twilio.chat.ChatClientListener;
import com.twilio.chat.ErrorInfo;

import com.twilio.chat.Paginator;
import com.twilio.chat.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class ChannelActivity extends AppCompatActivity implements ChatClientListener {

    private static final Logger logger = Logger.getLogger(ChannelActivity.class);
    private static final String TAG = "ChannelActivity";

    private ChannelsAdapter adapter;
    private RecyclerView channelRecyclerView;

    private BasicChatClient basicClient;

    private Map<String, ChannelModel> channels = new HashMap<>();
    private List<ChannelModel> adapterContents = new ArrayList<>();
    private Channels channelsObject;

    private static final Handler handler = new Handler();
    private AlertDialog incomingChannelInvite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        // toolbar setup
        Toolbar channelToolbar = (Toolbar) findViewById(R.id.channel_toolbar);
        setSupportActionBar(channelToolbar);
        setTitle("Channel Activity");

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);


        // instanciating
        basicClient = TwilioApplication.get().getBasicClient();
        basicClient.getChatClient().setListener(ChannelActivity.this);

        // setup recycler view
        setupRecyclerView();

    }

    private void setupRecyclerView(){

        // set up recycler view
        // afther getting public channels show it to the recycler view
        channelRecyclerView = (RecyclerView) findViewById(R.id.channel_recycler_view);

        // create adapter passing in the channel descripter
        adapter = new ChannelsAdapter(this, adapterContents);

        // attach the adapter to the recycler view
        channelRecyclerView.setAdapter(adapter);
        // set layout manager to position the item
        channelRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // that's all
    }

    @Override
    public void onResume(){
        super.onResume();
        getChannels();

    }

    public void getChannels(){

        if (channels == null) return;
        if (basicClient == null || basicClient.getChatClient() == null) return;

        channelsObject = basicClient.getChatClient().getChannels();

        channels.clear();

        channelsObject.getPublicChannelsList(new CallbackListener<Paginator<ChannelDescriptor>>() {
            @Override
            public void onSuccess(Paginator<ChannelDescriptor> channelDescriptorPaginator) {

                // get channels page and store the public channels
                getChannelsPage(channelDescriptorPaginator);

            }
        });

        channelsObject.getUserChannelsList(new CallbackListener<Paginator<ChannelDescriptor>>() {
            @Override
            public void onSuccess(Paginator<ChannelDescriptor> channelDescriptorPaginator) {

                // get channels pages and store user channels list
                getChannelsPage(channelDescriptorPaginator);
            }
        });

    }


    private class CustomChannelComparator implements Comparator<ChannelModel>
    {
        @Override
        public int compare(ChannelModel lhs, ChannelModel rhs)
        {
            return lhs.getFriendlyName().compareTo(rhs.getFriendlyName());
        }
    }

    private void refreshChannelList()
    {
        adapterContents.clear();
        adapterContents.addAll(channels.values());
        Collections.sort(adapterContents, new CustomChannelComparator());
        adapter.notifyDataSetChanged();
    }


    private void getChannelsPage(Paginator<ChannelDescriptor> paginator){
        for (ChannelDescriptor cd : paginator.getItems()) {
            logger.e("Adding channel descriptor for sid|"+cd.getSid()+"| friendlyName "+cd.getFriendlyName());
            channels.put(cd.getSid(), new ChannelModel(cd));
        }

        refreshChannelList();

        Log.e("HASNEXTPAGE", String.valueOf(paginator.getItems().size()));
        Log.e("HASNEXTPAGE", paginator.hasNextPage() ? "YES" : "NO");

        if (paginator.hasNextPage()) {
            paginator.requestNextPage(new CallbackListener<Paginator<ChannelDescriptor>>() {
                @Override
                public void onSuccess(Paginator<ChannelDescriptor> channelDescriptorPaginator) {
                    getChannelsPage(channelDescriptorPaginator);
                }
            });
        } else {
            // Get subscribed channels last - so their status will overwrite whatever we received
            // from public list. Ugly workaround for now.
            channelsObject = basicClient.getChatClient().getChannels();
            List<Channel> ch = channelsObject.getSubscribedChannels();
            for (Channel channel : ch) {
                channels.put(channel.getSid(), new ChannelModel(channel));
            }
            refreshChannelList();
        }
    }

    //=============================================================
    // ChatClientListener TODO: showIncomming invite methods needs to be completed
    //=============================================================

    @Override
    public void onChannelJoined(final Channel channel)
    {
        logger.d("Received onChannelJoined callback for channel |" + channel.getFriendlyName() + "|");
        channels.put(channel.getSid(), new ChannelModel(channel));
        refreshChannelList();
    }

    @Override
    public void onChannelAdded(final Channel channel)
    {
        logger.d("Received onChannelAdd callback for channel |" + channel.getFriendlyName() + "|");
        channels.put(channel.getSid(), new ChannelModel(channel));
        refreshChannelList();
    }

    @Override
    public void onChannelUpdated(final Channel channel, final Channel.UpdateReason reason)
    {
        logger.d("Received onChannelChange callback for channel |" + channel.getFriendlyName()
                + "| with reason " + reason.toString());
        channels.put(channel.getSid(), new ChannelModel(channel));
        refreshChannelList();
    }

    @Override
    public void onChannelDeleted(final Channel channel)
    {
        logger.d("Received onChannelDelete callback for channel |" + channel.getFriendlyName()
                + "|");
        channels.remove(channel.getSid());
        refreshChannelList();
    }

    @Override
    public void onChannelInvited(final Channel channel)
    {
        channels.put(channel.getSid(), new ChannelModel(channel));
        refreshChannelList();
        // TODO: showIncomingInvite(channel);
    }

    @Override
    public void onChannelSynchronizationChange(Channel channel)
    {
        logger.e("Received onChannelSynchronizationChange callback for channel |"
                + channel.getFriendlyName()
                + "| with new status " + channel.getStatus().toString());
        refreshChannelList();
    }

    @Override
    public void onClientSynchronization(ChatClient.SynchronizationStatus status)
    {
        logger.e("Received onClientSynchronization callback " + status.toString());
    }

    @Override
    public void onUserUpdated(User user, User.UpdateReason reason)
    {
        logger.e("Received onUserUpdated callback for "+reason.toString());
    }

    @Override
    public void onUserSubscribed(User user)
    {
        logger.e("Received onUserSubscribed callback");
    }

    @Override
    public void onUserUnsubscribed(User user)
    {
        logger.e("Received onUserUnsubscribed callback");
    }

    @Override
    public void onNotification(String channelId, String messageId)
    {
        logger.d("Received new push notification");
        TwilioApplication.get().showToast("Received new push notification");
    }

    @Override
    public void onNotificationSubscribed()
    {
        logger.d("Subscribed to push notifications");
        TwilioApplication.get().showToast("Subscribed to push notifications");
    }

    @Override
    public void onNotificationFailed(ErrorInfo errorInfo)
    {
        logger.d("Failed to subscribe to push notifications");
        TwilioApplication.get().showError("Failed to subscribe to push notifications", errorInfo);
    }

    @Override
    public void onError(ErrorInfo errorInfo)
    {
        TwilioApplication.get().showError("Received error", errorInfo);
    }

    @Override
    public void onConnectionStateChange(ChatClient.ConnectionState connectionState) {
        TwilioApplication.get().showToast("Transport state changed to "+connectionState.toString());
    }



    //=============================================================
    // on options menu selected functionality
    //=============================================================


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.channel_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //TODO: here configure the search info and add any event listeners..

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_new_group:
                // TODO: create new group
                createChannelWithType(ChannelType.PUBLIC);

                break;

            case R.id.action_search:
                // TODO: do the search
                break;

            case R.id.action_setting:
                // TODO: show the user setting
                break;

            case R.id.action_logout:
                // logout the user
                basicClient.shutdown();
                finish();
                break;
        }
        // If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }

    // create a channel with channel type
    private void createChannelWithType(ChannelType type){
        Random rand = new Random();
        int    value = rand.nextInt(50);

        final JSONObject attrs = new JSONObject();
        try {
            attrs.put("topic", "testing channel creation with options " + value);
        } catch (JSONException xcp) {
            logger.e("JSON exception", xcp);
        }

        String typ = type == ChannelType.PRIVATE ? "Priv" : "Pub";

        basicClient.getChatClient().getChannels()
                .channelBuilder()
                .withFriendlyName(typ + "_TestChannelF_" + value)
                .withUniqueName(typ + "_TestChannelU_" + value)
                .withType(type)
                .withAttributes(attrs)
                .build(new CallbackListener<Channel>() {
                    @Override
                    public void onSuccess(final Channel newChannel)
                    {
                        logger.d("Successfully created a channel with options.");
                        channels.put(newChannel.getSid(), new ChannelModel(newChannel));
                        refreshChannelList();
                    }

                    @Override
                    public void onError(ErrorInfo errorInfo)
                    {
                        logger.e("Error creating a channel");
                    }
                });
    }

}
