package algonquin.cst2335.jiangliannilab7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.jiangliannilab7.data.ChatViewModel;
import algonquin.cst2335.jiangliannilab7.databinding.ActivityChatRoomBinding;
import algonquin.cst2335.jiangliannilab7.databinding.ReceiveMessageBinding;
import algonquin.cst2335.jiangliannilab7.databinding.SentMessageBinding;

public class ChatRoom extends AppCompatActivity {

    ActivityChatRoomBinding binding;
    ArrayList<ChatMessage> messages;
    private RecyclerView.Adapter myAdapter;
    ChatViewModel chatModel;
    ChatMessageDAO mDAO = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MessageDatabase db = Room.databaseBuilder(getApplicationContext(), MessageDatabase.class, "database-name").build();
        mDAO = db.cmDAO();

        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());

        chatModel = new ViewModelProvider(this).get(ChatViewModel.class);
        messages = chatModel.messages.getValue();

        if(messages == null)
        {
            chatModel.messages.setValue(messages = new ArrayList<>());
            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                messages.addAll( mDAO.getAllMessages() ); //Once you get the data from database
                runOnUiThread( () ->  binding.recycleView.setAdapter( myAdapter )); //You can then load the RecyclerView
            });
        }

        setContentView(binding.getRoot());

        binding.sendButton.setOnClickListener(click->{
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
            String currentDateandTime = sdf.format(new Date());
            ChatMessage chm= new ChatMessage(binding.textInput.getText().toString(), currentDateandTime, true);
            messages.add(chm);

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                mDAO.insertMessage(chm);
                runOnUiThread( () ->  binding.recycleView.setAdapter( myAdapter )); //You can then load the RecyclerView
            });

            myAdapter.notifyItemInserted(messages.size()-1);
            //clear the previous text:
            binding.textInput.setText("");

        });

        binding.receiveButton.setOnClickListener(click->{
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh-mm-ss a");
            String currentDateandTime = sdf.format(new Date());
            ChatMessage chm= new ChatMessage(binding.textInput.getText().toString(), currentDateandTime, false);
            messages.add(chm);
            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() ->
            {
                mDAO.insertMessage(chm);
                runOnUiThread( () ->  binding.recycleView.setAdapter( myAdapter )); //You can then load the RecyclerView
            });
            myAdapter.notifyItemInserted(messages.size()-1);
            //clear the previous text:
            binding.textInput.setText("");

        });

        binding.recycleView.setAdapter(myAdapter = new RecyclerView.Adapter<MyRowHolder>() {
            @NonNull
            @Override
            public MyRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                if(viewType == 0) {
                    SentMessageBinding binding = SentMessageBinding.inflate(getLayoutInflater());
                    return new MyRowHolder(binding.getRoot());
                }
                else
                {
                    ReceiveMessageBinding binding = ReceiveMessageBinding.inflate(getLayoutInflater());
                    return new MyRowHolder(binding.getRoot());

                }
            }

            @Override
            public void onBindViewHolder(@NonNull MyRowHolder holder, int position) {
                String obj = messages.get(position).getMessage();
                holder.messageText.setText(obj);
                holder.timeText.setText(messages.get(position).getTimeSent());
            }

            @Override
            public int getItemCount() {
                return messages.size();
            }

            @Override
            public int getItemViewType(int position)
            {
                if(messages.get(position).getIsSentButton())
                {
                    return 0;
                }
                else
                {
                    return 1;
                }
            }

        });

        binding.recycleView.setLayoutManager(new LinearLayoutManager(this));
    }

    class MyRowHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timeText;
        public MyRowHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(clk ->{
                int position = getAbsoluteAdapterPosition();
                AlertDialog.Builder builder = new AlertDialog.Builder( ChatRoom.this );
                builder.setMessage("Do you want to delete the message:" + messageText.getText())
                        .setTitle("Qestion")
                        .setNegativeButton("No",(dialog,cl) ->{})
                        .setPositiveButton("Yes",(dialog,cl) ->{
                            ChatMessage m = messages.get(position);

                            Executor thread = Executors.newSingleThreadExecutor();
                            thread.execute(() ->
                            {
                                mDAO.deleteMessage(m);
                                runOnUiThread( () ->  binding.recycleView.setAdapter( myAdapter )); //You can then load the RecyclerView
                            });
                            messages.remove(position);
                            myAdapter.notifyItemRemoved(position);
                            Snackbar.make(messageText,"You deleted message#"+position, Snackbar.
                                    LENGTH_LONG).setAction("Undo",clk2->{
                                        messages.add(position,m);
                                        myAdapter.notifyItemInserted(position);
                            }).show();
                        }).create().show();

            });
            messageText = itemView.findViewById(R.id.message);
            timeText =itemView.findViewById(R.id.time);
        }
    }
}