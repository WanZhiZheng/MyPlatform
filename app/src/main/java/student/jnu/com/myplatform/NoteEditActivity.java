package student.jnu.com.myplatform;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NoteEditActivity extends AppCompatActivity {
    String[] isOK = new String[]{"未掌握","已掌握"};
    CharSequence[] items = {"拍照","从相册中选择"};
    String TAG = "NoteEditActivity  ";
    Uri imageUri;
    String mTempPhotoPath;
    public final static int CAMERA_REQUEST_CODE=0;
    public final static int GALLERY_REQUEST_CODE=1;
    int present_isOKspinner_selection;
    boolean []selected;
    List<Note> noteList = new ArrayList<>();
    List<String> labelList = new ArrayList<>();
    Note note;

    ImageView note_pic_edit;
    EditText english_edit;
    EditText chinese_edit;
    EditText content_edit;
    Spinner isOK_spinner;
    EditText time_edit;
    TextView label;
    FloatingActionButton btn_addLabel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);


        //noteList的读入
        noteList = NoteListManager.read(NoteEditActivity.this);

        //labelList的读入
        SharedPreferences sp = this.getSharedPreferences("labelList", Activity.MODE_PRIVATE);
        String labelList_json = sp.getString("label_List_json","");
        if(!labelList_json.equals("")){
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>(){}.getType();
            labelList = gson.fromJson(labelList_json,listType);

        }



        //从intent中获取到信息
        Intent intent = getIntent();
        note = (Note)intent.getSerializableExtra("note_item");


        setSelected();


        //toolbar 的设置
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(NoteEditActivity.this);
                dialog.setTitle("提示");
                dialog.setMessage("图书信息未保存，请问是否继续？");
                dialog.setCancelable(false);
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Intent intent1=new Intent(BookEditActivity.this,MainActivity.class);
                        //startActivity(intent1);
                        finish();
                    }
                });
                dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        });

        //绑定控件
        boundView();
        //显示图书的各项信息
        setMessageFromNote();

        //添加标签按钮
        btn_addLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加标签
                final EditText label_name_edit=new EditText(NoteEditActivity.this);
                label_name_edit.setHint("请输入标签名称");
                AlertDialog.Builder dialog=new AlertDialog.Builder(NoteEditActivity.this);
                dialog.setTitle("添加标签");
                dialog.setCancelable(false);
                dialog.setView(label_name_edit);
                dialog.setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        labelList.add(label_name_edit.getText().toString());
                    }
                });

                dialog.show();

            }
        });

        //点击标签可以选择标签
        label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int label_count = labelList.size();
                final String[] labelList_str = new String[label_count];
                for(int i = 0;i < labelList.size(); i++){
                    labelList_str[i] = labelList.get(i);
                }

                setSelected();
                //要根据这个book的 labelList 设置这个selected
                //selected=new boolean[label_count];
//                for(int i=0;i<label_count;i++){
//                    for(int j=0;j<note.getLabelList().size();j++){
//                        if(labelList_str[i].equals(note.getLabelList().get(j))){
//                            selected[i]=true;
//                            break;
//                        }
//                    }
//                }



                AlertDialog.Builder dialog=new AlertDialog.Builder(NoteEditActivity.this);
                dialog.setTitle("选择标签");
                dialog.setMultiChoiceItems(labelList_str, selected, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        //doing nothing
                    }
                });
                dialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String label_content="";
                        for(int i=0;i<label_count;i++){
                            if(selected[i]){
                                if(i==0){
                                    label_content=label_content+labelList_str[i];

                                }else{
                                    label_content=label_content+","+labelList_str[i];
                                }
                            }
                        }
                        Log.d("abc", String.valueOf(note.getLabelList().size()));
                        label.setText(label_content);
                        //这个book的 labelList要更新

                    }
                });


                dialog.show();
            }
        });


        //点击图片 换图片
        note_pic_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(NoteEditActivity.this);
                dialog.setTitle("选择图片");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0://拍照
                                if(ContextCompat.checkSelfPermission(NoteEditActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){

                                    ActivityCompat.requestPermissions(NoteEditActivity.this,new String[]{Manifest.permission.CAMERA},CAMERA_REQUEST_CODE);
                                }else{
                                    takePhoto();
                                }
                                break;
                            case 1://从相册中选择
                                if(ContextCompat.checkSelfPermission(NoteEditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

                                    ActivityCompat.requestPermissions(NoteEditActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},CAMERA_REQUEST_CODE);
                                }else{
                                    choosePhoto();
                                }
                                break;
                        }
                    }
                });
                dialog.show();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save:
                //保存labelList
                saveLabelList();
                //更新该Note的各项信息
                saveNote();
                //在noteList里更新这个note的信息
                saveNoteIntoNoteList();
                //保存noteList
                NoteListManager.save(NoteEditActivity.this, noteList);

                Log.d(TAG, "onOptionsItemSelected:  saveLabelList successfully");

                Intent intent = new Intent(NoteEditActivity.this, NotesListActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }
    //更新selected数组的已选项标记
    private void setSelected(){
        int label_count=labelList.size();
        selected=new boolean[label_count];
        for(int i=0;i<label_count;i++){
            for(int j=0;j<note.getLabelList().size();j++){
                if(labelList.get(i).equals(note.getLabelList().get(j))){
                    selected[i]=true;
                    break;
                }
            }
        }
    }

    //更新该Note的各项信息
    private void saveNote(){
        note.setChinese(chinese_edit.getText().toString());
        note.setEnglish(english_edit.getText().toString());
        note.setContent(content_edit.getText().toString());
        note.setTime(time_edit.getText().toString());
        note.setIsOK(present_isOKspinner_selection);

        List<String> labelList_new = new ArrayList<>();
        for(int i=0;i<labelList.size();i++){
            if(selected[i]){
                labelList_new.add(labelList.get(i));
            }
        }
        note.setLabelList(labelList_new);
    }

    //在noteList里更新这个note的信息
    private void saveNoteIntoNoteList(){
        int i ;
        for( i = 0; i < noteList.size(); i++){
            if(note.getUuid().equals(noteList.get(i).getUuid())){
                noteList.remove(i);
                noteList.add(i,note);
                break;
            }
        }
        if(i == noteList.size())
            noteList.add(note);

    }
    //保存labelList
    private void saveLabelList(){
        SharedPreferences sp = getApplicationContext().getSharedPreferences("labelList", Activity.MODE_PRIVATE);
        Gson gson = new Gson();
        String data = gson.toJson(labelList);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("label_List_json",data);
        editor.commit();
    }

    private void setMessageFromNote() {
        //图片设置
        Bitmap bitmap = ImageManager.GetLocalBitmap(NoteEditActivity.this,note.getUuid());
        if(bitmap == null){
            bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.note_image);
            ImageManager.SaveImage(getApplicationContext(),bitmap,note.getUuid());
        }
        note_pic_edit.setImageBitmap(bitmap);

        english_edit.setText(note.getEnglish());
        chinese_edit.setText(note.getChinese());
        content_edit.setText(note.getContent());
        time_edit.setText(note.getTime());

        //加载标签
        String label_content="";
        for(int i=0;i<note.getLabelList().size();i++){
            if(i==0){
                label_content = label_content+note.getLabelList().get(i);
            }else{
                label_content = label_content+","+note.getLabelList().get(i);
            }

        }
        label.setText(label_content);
        //加载 “是否熟悉” 下拉框
        ArrayAdapter<String> readingstate_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,isOK);
        readingstate_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        isOK_spinner.setAdapter(readingstate_adapter);

        //为下拉框设置 点击监听
        isOK_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //标记当前的选择的阅读状态
                present_isOKspinner_selection = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //设置下拉框的 初始值
        SpinnerAdapter readingstate_spinnerAdapter = isOK_spinner.getAdapter();
        int k_r=readingstate_spinnerAdapter.getCount();
        //readingstate_spinner.setSelection(2,true);
        for(int i=0;i<k_r;i++){
            if(note.getIsOK() == i){
                //标记当前书架选择
                present_isOKspinner_selection=i;
                isOK_spinner.setSelection(present_isOKspinner_selection,true);
                break;
            }
        }




    }

    private void takePhoto(){
        Intent intentToTakePhoto=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mTempPhotoPath= Environment.getExternalStorageDirectory()+ File.separator+"photo.jpeg";
        imageUri= FileProvider.getUriForFile(NoteEditActivity.this,NoteEditActivity.this.getApplicationContext().getPackageName()+".my.provider",new File(mTempPhotoPath));
        intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intentToTakePhoto,CAMERA_REQUEST_CODE);
    }

    private void choosePhoto(){
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型" 所有类型则写 "image/*"
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, GALLERY_REQUEST_CODE);
    }

    private void boundView() {
        note_pic_edit = (ImageView)findViewById(R.id.notepic_edit);
        english_edit = (EditText)findViewById(R.id.english_edit);
        chinese_edit = (EditText)findViewById(R.id.chinese_edit);
        content_edit = (EditText)findViewById(R.id.content_edit);
        isOK_spinner = (Spinner) findViewById(R.id.spinner_isOK_edit);
        time_edit = (EditText)findViewById(R.id.time_edit);
        label = (TextView)findViewById(R.id.label_edit);
        btn_addLabel = (FloatingActionButton)findViewById(R.id.btn_addLabel);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        note_pic_edit.findViewById(R.id.notepic_edit);
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case CAMERA_REQUEST_CODE:
                    try{
                        //Bitmap bit=BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        Bitmap bit=ImageManager.decodeSampledBitmapFromUri(getApplicationContext(),imageUri);
                        ImageManager.SaveImage(getApplicationContext(),bit,note.getUuid());
                        note_pic_edit.setImageBitmap(bit);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case GALLERY_REQUEST_CODE:
                    try {
                        //该uri就是照片文件夹对应的uri

                        imageUri=data.getData();
                        Bitmap bit =ImageManager.decodeSampledBitmapFromUri(getApplicationContext(),imageUri);
                        ImageManager.SaveImage(getApplicationContext(),bit,note.getUuid());
                        note_pic_edit.setImageBitmap(bit);
                        // 给相应的ImageView设置图片 未裁剪
                        //mImageView.setImageBitmap(bit);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
