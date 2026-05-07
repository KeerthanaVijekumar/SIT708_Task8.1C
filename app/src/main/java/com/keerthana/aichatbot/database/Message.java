package com.keerthana.aichatbot.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages")
public class Message {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String username;
    public String text;
    public String sender; // "USER" or "BOT"
    public long timestamp;

    public Message(String username, String text, String sender, long timestamp) {
        this.username = username;
        this.text = text;
        this.sender = sender;
        this.timestamp = timestamp;
    }
}