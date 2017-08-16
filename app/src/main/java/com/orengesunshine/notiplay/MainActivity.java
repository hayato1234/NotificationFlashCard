package com.orengesunshine.notiplay;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;

import com.google.gson.Gson;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MainRecyclerAdapter.OnButtonClickedListener {

    public static final String TAG = "tag";
    public static final String BUTTON_ID = "34857025";
    public static final int NOTIFICATION_ID = 7894883;
    public static final String SHOWING_CARDS = "playing_card_list";

    NotificationManager manager;
    Button startButton;
    Button endButton;
    private RecyclerView recyclerView;
    private MainRecyclerAdapter adapter;

    notiButtonListener mListener;
    static RemoteViews notificationViewB;
    static Context context;
    private CardDataBase db = new CardDataBase(this);
    private List<Folder> folders;
    private Folder lastDeletedFolder;
    private List<Card> lastDeletedCards;
    private List<Card> selectedCards;
    private static int currentNum;
    private static boolean showingBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
//        startButton = (Button)findViewById(R.id.startButton);
//        startButton.setOnClickListener(this);
//        endButton = (Button)findViewById(R.id.endButton);
//        endButton.setOnClickListener(this);

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        folders = db.getFolders();
        if (folders != null && folders.size()>0) {
            Log.d("tag", "main onCreate: " + folders.size());
        } else {
            folders = new ArrayList<>();
            Folder dummyFolder = new Folder("Please touch '+' button and add card ", 0, new Time(System.currentTimeMillis()));
            folders.add(dummyFolder);
        }

        adapter = new MainRecyclerAdapter(context, folders);
        adapter.setOnButtonClickedListener(this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText folderTitle = new EditText(MainActivity.this);
                folderTitle.setId(R.id.make_new_folder_input);
                folderTitle.setInputType(InputType.TYPE_CLASS_TEXT);
                AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
                ab.setTitle("Folder title: ")
                        .setView(folderTitle)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { // need to check if same name exists
                                db.makeNewFolder(folderTitle.getText().toString());
                                Intent i = new Intent(MainActivity.this, InputCardActivity.class);
                                i.putExtra(InputCardActivity.FOLDER_NAME, folderTitle.getText().toString());
                                startActivity(i);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                ab.show();
                //startActivity(new Intent(MainActivity.this,InputCardActivity.class));
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
//            case R.id.startButton:
//                startNotification();
//                break;
//            case R.id.endButton:
//                Log.d("tag", "end clicked");
//                endNotification();
//                break;
            default:
                break;
        }
    }

    private void startNotification() {
//        mListener = new notiButtonListener();
//        registerReceiver(mListener,new IntentFilter("action1"));
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this);

//        NotificationManager managerCompat = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        RemoteViews notificationView = new RemoteViews(getPackageName(),R.layout.notification);
        notificationViewB = new RemoteViews(getPackageName(), R.layout.notification_big);
        if (selectedCards!=null){
            notificationViewB.setTextViewText(R.id.flash_text, selectedCards.get(0).getFront());


            Intent resultIntent = new Intent("action1");
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);

            int exitButtonId = 10;
            Intent exitIntent = new Intent("exit_clicked");
            exitIntent.putExtra(BUTTON_ID, exitButtonId);
            PendingIntent exitPI = PendingIntent.getBroadcast(this, 123, exitIntent, 0);
            notificationViewB.setOnClickPendingIntent(R.id.exit_button, exitPI);

            ArrayList<Card> intentCards = (ArrayList<Card>) selectedCards;
            Log.d(TAG, "main startNotification: "+intentCards.get(0).getBack());
            int preButtonId = 11;
            Intent preIntent = new Intent("previous_clicked");
            preIntent.putExtra(BUTTON_ID, preButtonId);
            preIntent.putParcelableArrayListExtra(SHOWING_CARDS,intentCards);
            PendingIntent prePI = PendingIntent.getBroadcast(this, 123, preIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationViewB.setOnClickPendingIntent(R.id.previous_button, prePI);

            int flipButtonId = 12;
            Intent flipIntent = new Intent("flip_clicked");
            flipIntent.putExtra(BUTTON_ID, flipButtonId);
            flipIntent.putParcelableArrayListExtra(SHOWING_CARDS,(ArrayList<Card>) selectedCards);
            PendingIntent flipPI = PendingIntent.getBroadcast(this, 123,flipIntent , PendingIntent.FLAG_UPDATE_CURRENT);
            notificationViewB.setOnClickPendingIntent(R.id.flip_button, flipPI);

//        int nextButtonId = 13;
            int nextButtonId = 13;
            Intent buttonIntent2 = new Intent("next_clicked");
            buttonIntent2.putExtra(BUTTON_ID,nextButtonId);
            buttonIntent2.putParcelableArrayListExtra(SHOWING_CARDS,(ArrayList<Card>) selectedCards);
            PendingIntent pI2 = PendingIntent.getBroadcast(this, 123, buttonIntent2, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationViewB.setOnClickPendingIntent(R.id.next_button, pI2);

            Intent notiIntent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notiIntent, 0);
            notifBuilder.setSmallIcon(R.drawable.ic_play)
                    .setAutoCancel(true)
                    .setPriority(-1)
                    .setCustomBigContentView(notificationViewB)
                    .setContentIntent(pendingIntent);
            //.addAction(R.drawable.ic_play,"next",pendingIntent);
            manager.notify(NOTIFICATION_ID, notifBuilder.build());
        }else {
            Log.d(TAG, "main startNotification: list is null");
        }
    }

    private void endNotification() {
        manager.cancel(NOTIFICATION_ID);
    }

    @Override
    public void onButtonClicked(int actionNum, int position) {
        //actionNum: delete=0, edit=1, play=2
        switch (actionNum) {
            case 0:
                Log.d(TAG, "main onButtonClicked: " + position + " titile:" + folders.get(position).getTitle());
                lastDeletedFolder = folders.get(position);
                lastDeletedCards = db.getCards(lastDeletedFolder.getTitle());
                db.deleteFolder(lastDeletedFolder.getTitle());
                db.deleteCards(lastDeletedFolder.getTitle());
                folders.remove(position);
                adapter.notifyDataSetChanged();
                Snackbar.make(recyclerView, "Folder deleted", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        folders.add(lastDeletedFolder);
                        db.putBackFolder(lastDeletedFolder);
                        db.saveCards(lastDeletedFolder.getTitle(), lastDeletedCards);
                        adapter.notifyDataSetChanged();
                    }
                }).show();

                break;
            case 1:
                Intent i = new Intent(this, InputCardActivity.class);
                i.putExtra(InputCardActivity.FOLDER_NAME, folders.get(position).getTitle());
                startActivity(i);
                break;
            case 2:
                Log.d(TAG, "main onButtonClicked start: " + position + " titile:" + folders.get(position).getTitle());
                selectedCards = new ArrayList<>();
                selectedCards = db.getCards(folders.get(position).getTitle());
                startNotification();
                break;
            default:
                break;
        }
    }

    public static class notiButtonListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//
            if (intent.getExtras() != null) {
                Log.d("tag", "receved "+intent.getExtras().getInt(BUTTON_ID));
                switch (intent.getExtras().getInt(BUTTON_ID)) {
                    case 10: //exit
                        Log.d("tag", " exit called ");
                        showingBack = false;
                        manager.cancel(NOTIFICATION_ID);
                        break;
                    case 11: //previous
                        if (intent.getParcelableArrayListExtra(SHOWING_CARDS)!=null){
                            if (0<currentNum){
                                notificationViewB.setTextViewText(R.id.flash_text, ((Card)intent.getParcelableArrayListExtra(SHOWING_CARDS).get(--currentNum)).getFront());
                                Intent notiIntent = new Intent(context, MainActivity.class);
                                NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context);
                                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notiIntent, 0);
                                notifBuilder.setSmallIcon(R.drawable.ic_play)
                                        .setAutoCancel(true)
                                        .setPriority(-1)
                                        .setCustomBigContentView(notificationViewB)
                                        .setContentIntent(pendingIntent);
                                manager.notify(NOTIFICATION_ID, notifBuilder.build());
                            }
                        }else {
                            Log.d(TAG, "mian onReceive: parceble is null");
                        }
                        break;
                    case 12: //flip
                        if (intent.getParcelableArrayListExtra(SHOWING_CARDS)!=null){
                            Log.d(TAG, "main onReceive: "+((Card)intent.getParcelableArrayListExtra(SHOWING_CARDS).get(0)).getFront());
                            if (showingBack){
                                notificationViewB.setTextViewText(R.id.flash_text, ((Card)intent.getParcelableArrayListExtra(SHOWING_CARDS).get(currentNum)).getFront());
                                showingBack = false;
                            }else {
                                notificationViewB.setTextViewText(R.id.flash_text, ((Card)intent.getParcelableArrayListExtra(SHOWING_CARDS).get(currentNum)).getBack());
                                showingBack = true;
                            }
                            Intent notiIntent = new Intent(context, MainActivity.class);
                            NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context);
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notiIntent, 0);
                            notifBuilder.setSmallIcon(R.drawable.ic_play)
                                    .setAutoCancel(true)
                                    .setPriority(-1)
                                    .setCustomBigContentView(notificationViewB)
                                    .setContentIntent(pendingIntent);
                            manager.notify(NOTIFICATION_ID, notifBuilder.build());
                        }else {
                            Log.d(TAG, "mian onReceive: parceble is null");
                        }
                        break;
                    case 13: //next
                        if (intent.getParcelableArrayListExtra(SHOWING_CARDS)!=null){
                            if (intent.getParcelableArrayListExtra(SHOWING_CARDS).size()-1>currentNum){
                                notificationViewB.setTextViewText(R.id.flash_text, ((Card)intent.getParcelableArrayListExtra(SHOWING_CARDS).get(++currentNum)).getFront());
                                Intent notiIntent = new Intent(context, MainActivity.class);
                                NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context);
                                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notiIntent, 0);
                                notifBuilder.setSmallIcon(R.drawable.ic_play)
                                        .setAutoCancel(true)
                                        .setPriority(-1)
                                        .setCustomBigContentView(notificationViewB)
                                        .setContentIntent(pendingIntent);
                                manager.notify(NOTIFICATION_ID, notifBuilder.build());
                            }
                        }else {
                            Log.d(TAG, "mian onReceive: parceble is null");
                        }
                        break;
                }
            } else {
                Log.d("tag", " extra is null ");
            }

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    @Override
    protected void onDestroy() {
        if (mListener != null) {
            unregisterReceiver(mListener);
        }
        super.onDestroy();
    }
}
