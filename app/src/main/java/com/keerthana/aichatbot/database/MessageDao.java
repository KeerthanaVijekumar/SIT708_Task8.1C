package com.keerthana.aichatbot.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface MessageDao {

    @Insert
    void insertMessage(Message message);

    @Query("SELECT * FROM messages WHERE username = :username ORDER BY timestamp ASC")
    List<Message> getMessagesForUser(String username);

    @Query("DELETE FROM messages WHERE username = :username")
    void clearMessagesForUser(String username);
}