package com.orengesunshine.notiplay;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class InputCardActivity extends AppCompatActivity implements InputRecyclerAdapter.RecyclerActionListener {

    public static final String TAG = "tag";

    public static final String FOLDER_NAME = "akljdhfhaoiwefhb";

    private RecyclerView recyclerView;
    private Button addCardButton;
    private InputRecyclerAdapter adapter;

    private CardDataBase db = new CardDataBase(this);
    private List<Card> displayedCards;
    private boolean newFolder;
    private boolean changed;
    private boolean saved;
    private String folderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        folderName = getIntent().getExtras().getString(FOLDER_NAME);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle(folderName);
        }else {
            Log.d(TAG, "inputCA onCreate: abar null" + folderName);
        }
        addCardButton = (Button) findViewById(R.id.input_add_button);
        addCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Card emptyCard = new Card("", "", "", new Time(System.currentTimeMillis()), false);
                displayedCards.add(emptyCard);
                adapter.notifyDataSetChanged();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.edit_recycler_view);
        displayedCards = db.getCards(folderName);
        if (displayedCards == null || displayedCards.size() == 0) {
            Log.d(TAG, "inputCA onCreate: first time");
            newFolder = true;
            Card emptyCard1 = new Card("", "", "", new Time(System.currentTimeMillis()), false);
            Card emptyCard2 = new Card("", "", "", new Time(System.currentTimeMillis()), false);
            displayedCards = new ArrayList<>();
            displayedCards.add(emptyCard1);
            displayedCards.add(emptyCard2);
        } else {
            Log.d(TAG, "inputCA onCreate: not null");
            newFolder = false;
        }
        adapter = new InputRecyclerAdapter(this, displayedCards);
        adapter.setRecyclerActionListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        Log.d(TAG, "ICA onCreate: "+displayedCards.size());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAction(String action, String change, int position) {
        changed = true; // if any change then save all, otherwise no save
        switch (action) {
            case "text_change_front":
                displayedCards.get(position).setFront(change);
                break;
            case "text_change_back":
                displayedCards.get(position).setBack(change);
                break;
            case "check":
                displayedCards.get(position).setCheck(!displayedCards.get(position).isCheck());
                break;
            case "delete":
                displayedCards.remove(position);
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.input_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saved = true;
                db.saveFolder(new Folder(folderName, displayedCards.size(), new Time(System.currentTimeMillis())));
                db.saveCards(folderName, displayedCards);
                changed = false;
                Snackbar.make(recyclerView, R.string.saved, Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.edit_folder_name:
                final EditText newFolderName = new EditText(this);
                newFolderName.setInputType(InputType.TYPE_CLASS_TEXT);
                newFolderName.setId(R.id.change_folder_title);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(newFolderName)
                        .setTitle("New folder title: ")
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.editFolderName(folderName,newFolderName.getText().toString());
                                folderName = newFolderName.getText().toString();
                                if(getSupportActionBar()!=null){
                                    getSupportActionBar().setTitle(folderName);
                                }else {
                                    Log.d(TAG, "inputa onClick: abar null");
                                }

                                dialog.dismiss();
                                View view = getCurrentFocus();
                                if (view != null) { // force close keyboard
                                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                                }
                                Snackbar.make(recyclerView, R.string.folder_updated,Snackbar.LENGTH_SHORT);
                            }
                        });
                builder.show();
                break;
            case R.id.delete:
                db.deleteTable(CardDataBase.CARD_DB_TABLE);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (changed & !saved) { // changed but not saved
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.deleteFolder(folderName);
                            InputCardActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("Stay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setTitle("Do you want to leave witout saving changes?");
            builder.show();
        } else { // saved or not changed at all
            startActivity(new Intent(this,MainActivity.class));
        }
    }
}
