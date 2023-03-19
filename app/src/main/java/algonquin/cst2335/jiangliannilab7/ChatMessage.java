package algonquin.cst2335.jiangliannilab7;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ChatMessage {
    @ColumnInfo(name="message")
    String message;
    @ColumnInfo(name="timeSent")
    String timeSent;
    @ColumnInfo(name="isSentButton")
    boolean isSentButton;

    @PrimaryKey(autoGenerate=true)
    @ColumnInfo(name="id")
    public int id;

    ChatMessage(String m, String t, boolean sent)
    {
        message = m;
        timeSent = t;
        isSentButton = sent;
    }

    public ChatMessage(){}

    public String getMessage()
    {
        return this.message;
    }

    public String getTimeSent()
    {
        return this.timeSent;
    }

    public boolean getIsSentButton()
    {
        return this.isSentButton;
    }
}
