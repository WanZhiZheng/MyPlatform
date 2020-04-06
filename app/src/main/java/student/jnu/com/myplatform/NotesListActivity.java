package student.jnu.com.myplatform;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NotesListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    RecyclerView recyclerView_notes;
    List<Note> mNotesList = new ArrayList<>();
    List<Note> noteList_show = new ArrayList<>();
    NoteAdapter noteAdapter;
    SearchView searchView;
    FloatingActionMenu fab;
    static int present_label_selection;
    static boolean isShowLabel;
    List<String> labelList = new ArrayList<>();
    Spinner mTopSpinner;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //读取NoteList
        mNotesList = NoteListManager.read(NotesListActivity.this);
        for (Note temp : mNotesList) {
            noteList_show.add(temp);
        }

        //读取laelList
        SharedPreferences sp = this.getSharedPreferences("labelList", Activity.MODE_PRIVATE);
        String labelList_json = sp.getString("label_List_json", "");
        if (!labelList_json.equals("")) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>() {
            }.getType();
            labelList = gson.fromJson(labelList_json, listType);

        }
        if(mNotesList.size() == 0)
            initList();
        //mNotesList.add(mNotesList.size()-1,new Note("It's up to you", "这由你决定","这是一个固定搭配"));

        //读取noteList


        //加载主列表
        recyclerView_notes = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView_notes.setLayoutManager(layoutManager);
        noteAdapter = new NoteAdapter(noteList_show, NotesListActivity.this);
        recyclerView_notes.setAdapter(noteAdapter);

        //悬浮按钮的设置
        fab = (FloatingActionMenu) findViewById(R.id.fab);
        fab.setClosedOnTouchOutside(true);
        com.github.clans.fab.FloatingActionButton btn_add = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_add);
        com.github.clans.fab.FloatingActionButton btn_getToTop = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_get_to_top);
        //添加单个笔记
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotesListActivity.this, NoteEditActivity.class);
                intent.putExtra("note_item", new Note("", "", ""));
                startActivity(intent);
                fab.close(true);
            }
        });
        //回到顶部
        btn_getToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView_notes.scrollToPosition(0);
                fab.close(true);
            }
        });

        //加载上方标签的spinner
        int count = labelList.size();
        String[] labelListNames = new String[count + 1];
        labelListNames[0] = "所有";
        for (int i = 0; i < count; i++) {
            labelListNames[i + 1] = labelList.get(i);
        }
        mTopSpinner = new Spinner(getSupportActionBar().getThemedContext());
        ArrayAdapter<String> bookshelflist_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelListNames);
        bookshelflist_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTopSpinner.setAdapter(bookshelflist_adapter);
        toolbar.addView(mTopSpinner, 0);
        mTopSpinner.setSelection(present_label_selection);

        //设置标签的点击事件
        mTopSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                present_label_selection = position;
                if (present_label_selection == 0) {
                    isShowLabel = false;
                    toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                } else {
                    isShowLabel = true;
                    toolbar.setBackgroundColor(getResources().getColor(R.color.LabelorBookshelf));
                }
                invalidateOptionsMenu();
                refreshList();
                //sort()
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mune_list, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    private void refreshList() {
        noteList_show.clear();
        if (present_label_selection == 0) {
            for (Note temp : mNotesList) {
                noteList_show.add(temp);
            }
        } else {
            String label_selected = labelList.get(present_label_selection - 1);
            for (int i = 0; i < mNotesList.size(); i++) {
                if (mNotesList.get(i).getLabelList().contains(label_selected)) {
                    noteList_show.add(mNotesList.get(i));
                }
            }
        }
        noteAdapter.notifyDataSetChanged();

    }

    private void initList() {
        Bitmap bitmap = ImageManager.decodeSampledBitmapFromResource(getResources(), R.drawable.note_image);

        Note note1 = new Note("It's up to you.", "这由你决定。", "这是一个固定搭配", 0);
        ImageManager.SaveImage(getApplicationContext(), bitmap, note1.getUuid());
        mNotesList.add(note1);
        //mNotesList.add(new Note("It is a nice day.", "今天天气真好。","表达天气的一种方法",1));
        Note note2 = new Note("Nice to meet you.", "很高兴遇见你。", "问候语", 0);
        ImageManager.SaveImage(getApplicationContext(), bitmap, note2.getUuid());
        mNotesList.add(note2);
        //mNotesList.add(new Note("Nice to meet you.", "很高兴遇见你。","问候语",0));
        Note note3 = new Note("Is this a river?", "这是一条河吗？", "一般疑问句", 1);
        ImageManager.SaveImage(getApplicationContext(), bitmap, note3.getUuid());
        mNotesList.add(note3);

        //mNotesList.add(new Note("Is this a river?", "这是一条河吗？","一般疑问句",1));
        Note note4 = new Note("It is Tom who can make a decision.", "只有汤姆才能做决定。", "强调语态");
        ImageManager.SaveImage(getApplicationContext(), bitmap, note4.getUuid());
        mNotesList.add(note4);

        //mNotesList.add(new Note("It is Tom who can make a decision.", "只有汤姆才能做决定。","强调语态"));
        Note note5 = new Note("What's wrong with you", "你怎么了？", "特殊疑问句");
        ImageManager.SaveImage(getApplicationContext(), bitmap, note5.getUuid());
        mNotesList.add(note5);


        //mNotesList.add(new Note("What's wrong with you", "你怎么了？","特殊疑问句"));

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        List<Note> filter_noteList;
        filter_noteList = filter(newText);
        noteList_show.clear();
        for (Note note : filter_noteList)
            noteList_show.add(note);
        noteAdapter.notifyDataSetChanged();
        return true;
    }

    //对搜索内容进行匹配，返回一个匹配关键字成功 过滤后的一组书籍
    private List<Note> filter(String text) {
        List<Note> filter_bookList = new ArrayList<Note>();
        if (present_label_selection == 0) {
            for (Note note : mNotesList) {
                if (note.getChinese().contains(text) || note.getEnglish().contains(text))
                    //这里实现书名和出版社的搜索关键字
                    filter_bookList.add(note);
            }
        } else {
            String label_selected = labelList.get(present_label_selection - 1);
            for (Note note : mNotesList) {
                if (note.getLabelList().contains(label_selected) &&
                        (note.getChinese().contains(text) || note.getEnglish().contains(text)))
                    //这里实现书名和出版社的搜索关键字
                    filter_bookList.add(note);
            }
        }
        return filter_bookList;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem renameLabelItem=menu.findItem(R.id.rename_label);
        MenuItem deleteLabelItem=menu.findItem(R.id.delete_label);
        //MenuItem renameBookshelfItem=menu.findItem(R.id.rename_bookshelf);
        //MenuItem deleteBookshelfItem=menu.findItem(R.id.delete_bookshelf);

        renameLabelItem.setVisible(isShowLabel);
        deleteLabelItem.setVisible(isShowLabel);
        //renameBookshelfItem.setVisible(isShow_bookshelfitem);
        //deleteBookshelfItem.setVisible(isShow_bookshelfitem);

        if(isShowLabel){
            fab.setVisibility(View.GONE);
            fab.hideMenuButton(true);
        }else{
            fab.setVisibility(View.VISIBLE);
            fab.showMenuButton(true);
        }
        return true;

        //return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.rename_label:
                final EditText rename_label=new EditText(NotesListActivity.this);
                AlertDialog.Builder dialog_renameLabel=new AlertDialog.Builder(NotesListActivity.this);
                dialog_renameLabel.setTitle("更改标签名称");
                dialog_renameLabel.setView(rename_label);
                dialog_renameLabel.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newLabelName=rename_label.getText().toString();
                        refreshLabelInNoteList(newLabelName);  //In BookShelfList
                        refreshLabelSpinner(false);
                    }
                });

                dialog_renameLabel.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


                dialog_renameLabel.show();
                break;

            case R.id.delete_label:
                AlertDialog.Builder dialog_deleteLabel=new AlertDialog.Builder(NotesListActivity.this);
                dialog_deleteLabel.setTitle("更改标签名称");
                dialog_deleteLabel.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteLabelInNoteList();
                        refreshLabelSpinner(true);

                    }
                });

                dialog_deleteLabel.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


                dialog_deleteLabel.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteLabelInNoteList(){
        String deleteLabelName = labelList.get(present_label_selection-1);
        labelList.remove(present_label_selection-1);
        for(int i=0;i<mNotesList.size();i++){
            if(mNotesList.get(i).getLabelList().contains(deleteLabelName)){
                for(int j = 0; j<mNotesList.get(i).getLabelList().size(); j++){
                    if(mNotesList.get(i).getLabelList().get(j).equals(deleteLabelName)){
                        mNotesList.get(i).getLabelList().remove(j);
                        break;
                    }

                }
            }
        }


    }

    private void refreshLabelInNoteList(String newLabelName){
        String oldLabelName=labelList.get(present_label_selection - 1);
        labelList.set(present_label_selection - 1,newLabelName);
        //在所有书架里面 改变书的那个labelList  其他书架会跟着改， 因为这里应用是一样的
        for(int i=0;i<mNotesList.size();i++){
            if(mNotesList.get(i).getLabelList().contains(oldLabelName)){
                for(int j = 0; j<mNotesList.get(i).getLabelList().size(); j++){
                    if(mNotesList.get(i).getLabelList().get(j).equals(oldLabelName)){
                        mNotesList.get(i).getLabelList().set(j, newLabelName);
                        break;
                    }

                }
            }
        }

    }

    private void refreshLabelSpinner(boolean isDelete){
        //加载上方标签的spinner
        int count = labelList.size();
        String[] labelListNames = new String[count + 1];
        labelListNames[0] = "所有";
        for (int i = 0; i < count; i++) {
            labelListNames[i + 1] = labelList.get(i);
        }
        ArrayAdapter<String> bookshelflist_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labelListNames);
        bookshelflist_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTopSpinner.setAdapter(bookshelflist_adapter);

        if(!isDelete)
            mTopSpinner.setSelection(present_label_selection);
        else
            mTopSpinner.setSelection(0);
    }

}
