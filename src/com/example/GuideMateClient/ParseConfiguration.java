package com.example.GuideMateClient;
import com.parse.Parse;
import com.parse.ParseObject;

import android.app.Application;

/*
 * Parse Configuration class
 */
public class ParseConfiguration extends Application {
	
	public static final String APPLICATION_ID = "Dyny2Z1fkDYO867hRvTwBlAHxERizyAEKmqD7R7s";
    public static final String CLIENT_KEY = "yI9eSrprpY5gdpR2fgsF3gIcwgJETGn5k3KGmQkZ";
    @Override
    public void onCreate() {
        super.onCreate();
     // Register parse models here
        ParseObject.registerSubclass(Message.class);
     // Enables data storage for CRUD operations
        Parse.enableLocalDatastore(this);
     // Parse will identify the application using following key.
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
    }

}


