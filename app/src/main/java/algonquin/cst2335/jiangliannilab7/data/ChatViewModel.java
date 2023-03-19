package algonquin.cst2335.jiangliannilab7.data;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import algonquin.cst2335.jiangliannilab7.ChatMessage;

public class ChatViewModel extends ViewModel {
    public MutableLiveData<ArrayList<ChatMessage>> messages = new MutableLiveData<>();
}