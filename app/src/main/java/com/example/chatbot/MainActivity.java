package com.example.chatbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView chatRV;
    private EditText userMsgEDT;
    private FloatingActionButton sendMsgFAB;
    private final String BOT_KEY = "bot";
    private final String USER_KEY = "user";
    private ArrayList<ChatsModal> chatsModalArrayList;
    private ChatRVAdapter chatRVAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatRV = findViewById(R.id.idRVChats);
        userMsgEDT = findViewById(R.id.idEdtMessage);
        sendMsgFAB = findViewById(R.id.idFABSend);
        chatsModalArrayList = new ArrayList<>();
        chatRVAdapter = new ChatRVAdapter(chatsModalArrayList, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        chatRV.setLayoutManager(manager);
        chatRV.setAdapter(chatRVAdapter);

        sendMsgFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userMsgEDT.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter your message", Toast.LENGTH_SHORT).show();
                    return;
                }
                getResponse(userMsgEDT.getText().toString());
                userMsgEDT.setText("");
            }
        });
    }
    private  void   getResponse(String message)
    {
        chatsModalArrayList.add(new ChatsModal(message,USER_KEY));
        chatRVAdapter.notifyDataSetChanged();
        String url = "http://api.brainshop.ai/get?bid=162205&key=wts0ivW3GFZyZuW6&uid=[uid]&msg=[msg]" + message;
        String BASE_url = "http://api.brainshop.ai/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<msgModal> call = retrofitAPI.getMessage(url);
        call.enqueue(new Callback<msgModal>() {
            @Override
            public void onResponse(Call<msgModal> call, Response<msgModal> response) {
                if(response.isSuccessful())
                {
                    msgModal modal = response.body();
                    chatsModalArrayList.add(new ChatsModal(modal.getCnt(),BOT_KEY));
                    chatRVAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<msgModal> call, Throwable throwable) {
                chatsModalArrayList.add(new ChatsModal("Please revert your questions",BOT_KEY));
                chatRVAdapter.notifyDataSetChanged();
            }
        });
    }
}
