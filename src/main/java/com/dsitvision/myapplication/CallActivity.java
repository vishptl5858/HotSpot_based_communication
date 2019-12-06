package com.dsitvision.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.rtp.AudioCodec;
import android.net.rtp.AudioGroup;
import android.net.rtp.AudioStream;
import android.net.rtp.RtpStream;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static com.dsitvision.myapplication.R.id.button1;

public class CallActivity extends AppCompatActivity {

    AudioGroup m_AudioGroup;
    AudioStream m_AudioStream;

    public static final String ACTION_CHAT_RECEIVED = "org.drulabs.localdash.chatreceived";
    public static final String KEY_CHAT_DATA = "chat_data_key";

    public static final String KEY_CHATTING_WITH = "chattingwith";
    public static final String KEY_CHAT_IP = "chatterip";
    public static final String KEY_CHAT_PORT = "chatterport";



    private List<ChatDTO> chatList;


    private String chattingWith;
    private String destIP;
    private int destPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        Button b= (Button) findViewById(button1);
        Button b1= (Button) findViewById(R.id.button2);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    m_AudioStream.release();
                }catch (Exception e){
                    Toast.makeText(CallActivity.this, e+"", Toast.LENGTH_SHORT).show();
                }

            }
        });
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audio.setMode(AudioManager.MODE_IN_COMMUNICATION);
        m_AudioGroup = new AudioGroup();
        m_AudioGroup.setMode(AudioGroup.MODE_NORMAL);
        try {
            m_AudioStream = new AudioStream(InetAddress.getByAddress(getLocalIPAddress()));
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        final int localPort = m_AudioStream.getLocalPort();
        m_AudioStream.setCodec(AudioCodec.PCMU);
        m_AudioStream.setMode(RtpStream.MODE_NORMAL);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendChatInfo(localPort);
            }
        });


        chatList = new ArrayList<>();




    }

    private void initialize() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CHAT_RECEIVED);
        LocalBroadcastManager.getInstance(CallActivity.this).registerReceiver(chatReceiver, filter);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            NotificationToast.showToast(CallActivity.this, "Invalid arguments to open chat");
            finish();
        }

        chattingWith = extras.getString(KEY_CHATTING_WITH);
        destIP = extras.getString(KEY_CHAT_IP);
        destPort = extras.getInt(KEY_CHAT_PORT);

        setToolBarTitle("Call with " + chattingWith);
    }

    public void SendChatInfo(int localPort) {


        ChatDTO myChat = new ChatDTO();
        myChat.setPort(ConnectionUtils.getPort(CallActivity.this));
        myChat.setFromIP(Utility.getString(CallActivity.this, "myip"));
        myChat.setLocalTimestamp(System.currentTimeMillis());
        try {
            myChat.setMessage(""+InetAddress.getByAddress(getLocalIPAddress()));

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        myChat.setSentBy(chattingWith);
        myChat.setMyChat(true);;
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        myChat.setSendip(ip);
        myChat.setSendport(localPort+"");

        DataSender.sendChatInfo(CallActivity.this, destIP, destPort, myChat);


//        chatListHolder.smoothScrollToPosition(chatList.size() - 1);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private BroadcastReceiver chatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_CHAT_RECEIVED:
                    final ChatDTO chat = (ChatDTO) intent.getSerializableExtra(KEY_CHAT_DATA);
                    chat.setMyChat(false);
                    try {
                        m_AudioStream.associate(InetAddress.getByName(chat.getSendip()), Integer.parseInt(chat.getSendport()));
                    } catch (NumberFormatException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (UnknownHostException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    m_AudioStream.join(m_AudioGroup);
                    break;
                default:
                    break;
            }
        }
    };





    private void setToolBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }
    private byte[] getLocalIPAddress() {
        byte[] bytes = null;

        try {
            // get the string ip
            WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

            // convert to bytes
            InetAddress inetAddress = null;
            try {
                inetAddress = InetAddress.getByName(ip);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            bytes = new byte[0];
            if (inetAddress != null) {
                bytes = inetAddress.getAddress();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "no", Toast.LENGTH_SHORT).show();
        }

        return bytes;
    }
}
