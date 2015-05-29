package com.example.GuideMateClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.simplechat.R;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class MainActivity extends Activity {
	 private static final String TAG = MainActivity.class.getName();
	 private static String sUserId;
	 private EditText etMessage;
	 private Button btSend;
	 public static final String USER_ID_KEY = "userId";
	 
	 private ListView lvChat;
	 private ArrayList<Message> mMessages;
	 private GuideMateAdapter mAdapter;
	 
	 private static final int MAX_MESSAGES = 100;
	 
	 int messageCount = 0;
	 //String joinKey = "";
	 private WebView mWebview ;
	 
	 //Text to speach
	 TextToSpeech ttobj;
	 String INITIATE_REQUEST = "ClientRequest";
	 
	 String workerSignalStrength = "";
	 Random rand = new Random();
	 int num = rand.nextInt(9000000) + 1000000;
	 String randNumber = Integer.toString(num);
	 String url = "https://apprtc.appspot.com/r/" + randNumber ;
	 
	 boolean startCommunication = false;
	 
	 boolean startSpeaking = false;
	// Create a handler which can run code periodically
	 private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
      
     // User login
        if (ParseUser.getCurrentUser() != null) { // start with existing user
        	// Initial working code: 
        	startWithCurrentUser();
        	
        	//Client Code
        	/*InitiateWithCurrentUser();
        	if(startCommunication){
        	startWithCurrentUser();
        	}*/
        } else { // If not logged in, login as a new anonymous user
            login();
        }
     // Run the runnable object defined every 100ms
        handler.postDelayed(runnable, 1000);
        
        ttobj=new TextToSpeech(getApplicationContext(), 
                new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                   if(status != TextToSpeech.ERROR){
                       ttobj.setLanguage(Locale.UK);
                      }				
                   }
                });
       
    }
    /*@Override
    public void onPause(){
       if(ttobj !=null){
          ttobj.stop();
          ttobj.shutdown();
       }
       super.onPause();
    }*/
    
   
 
 // Get the userId from the cached currentUser object
    private void startWithCurrentUser() {
        sUserId = ParseUser.getCurrentUser().getObjectId(); 
        setupMessagePosting();
    }
 // Setup button event handler which posts the entered message to Parse
 // Setup message field and posting
    private void setupMessagePosting() {
            etMessage = (EditText) findViewById(R.id.etMessage);
            btSend = (Button) findViewById(R.id.btSend);
            lvChat = (ListView) findViewById(R.id.lvChat);
            mMessages = new ArrayList<Message>();
            mAdapter = new GuideMateAdapter(MainActivity.this, sUserId, mMessages);
            lvChat.setAdapter(mAdapter);
            
            btSend.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    String body = etMessage.getText().toString();
                    // Use Message model to create new messages now      
                    Message message = new Message();
                    message.setUserId(sUserId);
                    message.setBody(body);
                    message.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            receiveMessage();
                        }
                    });
                    etMessage.setText("");
                }
        });
    }
 // Defines a runnable which is run every 100ms
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
           refreshMessages();
           handler.postDelayed(this, 1000);
        }
    };

    private void refreshMessages() {
        receiveMessage();       
    }
 // Query messages from Parse so we can load them into the chat adapter
    private void receiveMessage() {
    	
                    // Construct query to execute
            ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
                    // Configure limit and sort order
            query.setLimit(MAX_MESSAGES);
            query.orderByDescending("createdAt");
            // Execute query to fetch all messages from Parse asynchronously
                    // This is equivalent to a SELECT query with SQL
            query.findInBackground(new FindCallback<Message>() {
                public void done(List<Message> messages, ParseException e) {
                    if (e == null) {                    
                        mMessages.clear();
                        mMessages.addAll(messages);
                        mAdapter.notifyDataSetChanged(); // update adapter
                        lvChat.invalidate(); // redraw listview
                        
                		//Client Init code
                        for (Message message : messages) {
                        	
                        	if(message.getBody().equals("SignalStrength")){
                        		message.deleteInBackground();
                        		 Message messageUrl = new Message();
                        		 messageUrl.setUserId(sUserId);
                        		 messageUrl.setBody(url);
                        		 messageUrl.saveInBackground();
                        		 mWebview = (WebView) findViewById(R.id.id_web_view_browser);
     						     mWebview.loadUrl(url);
     						     startSpeaking = true;
                        	}
                        	if(startSpeaking){
                        	String toSpeak = message.getBody();
                        	
                            Toast.makeText(getApplicationContext(), toSpeak, 
                            Toast.LENGTH_SHORT).show();
                            ttobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                           
                           	message.deleteInBackground();
                        	}
						}
                        
                      //Worker Init code
					/*for (Message message : messages) {

						if (message.getBody().equals("ClientRequest")) {
							message.deleteInBackground();
							Message messageUrl = new Message();
							messageUrl.setUserId(sUserId);
							messageUrl.setBody("SignalStrength");
							messageUrl.saveInBackground();
						}

						if (message.getBody().contains("apprtc")) {
							url = message.getBody();
							message.deleteInBackground();
							mWebview = (WebView) findViewById(R.id.id_web_view_browser);
							mWebview.loadUrl(url);
							message.deleteInBackground();
						}
					}*/
                        
                        
                     // Commment below for worker: 
                       /*for (Message message : messages) {
                        	
                        	
                        	String toSpeak = message.getBody();
                        	
                            Toast.makeText(getApplicationContext(), toSpeak, 
                            Toast.LENGTH_SHORT).show();
                            ttobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                           
                           message.deleteInBackground();
                          // message.deleteEventually();
						}*/
                        
                        /*if(messages.size() > messageCount){
                        for (Message message : messages) {
                        	
                        	String toSpeak = message.getBody();
                        	
                            Toast.makeText(getApplicationContext(), toSpeak, 
                            Toast.LENGTH_SHORT).show();
                            ttobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                            break;
						}
                        } 	
                        messageCount = messages.size();*/
                           
						
                        
                    } else {
                        Log.d("message", "Error: " + e.getMessage());
                    }
                }
            });
           
    }
    
    
    
    //Initial Setup for Parse
    
 // Get the userId from the cached currentUser object
    private void InitiateWithCurrentUser() {
        sUserId = ParseUser.getCurrentUser().getObjectId(); 
        InitiatesetupMessagePosting();
    }
 // Setup button event handler which posts the entered message to Parse
 // Setup message field and posting
    private void InitiatesetupMessagePosting() {
           

        String body = INITIATE_REQUEST;
        // Use Message model to create new messages now      
        Message message = new Message();
        message.setUserId(sUserId);
        message.setBody(body);
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                receiveMessage();
            }
        });
        //etMessage.setText("");
    
    }
 // Defines a runnable which is run every 100ms
    private Runnable initiateRunnable = new Runnable() {
        @Override
        public void run() {
           InitiateRefreshMessages();
           handler.postDelayed(this, 1000);
        }
    };

    private void InitiateRefreshMessages() {
        InitiateReceiveMessage();       
    }
 // Query messages from Parse so we can load them into the chat adapter
    private void InitiateReceiveMessage() {
    	
                    // Construct query to execute
            ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
                    // Configure limit and sort order
            query.setLimit(MAX_MESSAGES);
            query.orderByDescending("createdAt");
            // Execute query to fetch all messages from Parse asynchronously
                    // This is equivalent to a SELECT query with SQL
            query.findInBackground(new FindCallback<Message>() {
                public void done(List<Message> messages, ParseException e) {
                    if (e == null) {                    
                        //mMessages.clear();
                       // mMessages.addAll(messages);
                      //  mAdapter.notifyDataSetChanged(); // update adapter
                       // lvChat.invalidate(); // redraw listview
                        
                     // Commment below for worker: 
                       for (Message message : messages) {
                        	
                        	if(message.getBody().equals("workerSignalStrength")){
                        		message.deleteInBackground();
                        		Message newMessage = new Message();
                        		newMessage.setUserId(sUserId);
                        		newMessage.setBody(url);
                        		newMessage.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        receiveMessage();
                                    }
                                });
                        	}
                           
                          // message.deleteEventually();
						}
                        
                                                
                    } else {
                        Log.d("message", "Error: " + e.getMessage());
                    }
                }
            });
           
    }
    
    
    
    
    
    
    // Create an anonymous user using ParseAnonymousUtils and set sUserId 
    private void login() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
        @Override
        public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Anonymous login failed: " + e.toString());
                } else {
                    startWithCurrentUser();
                }
            }
       });      
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
}
