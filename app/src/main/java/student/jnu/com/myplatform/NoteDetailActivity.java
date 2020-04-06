package student.jnu.com.myplatform;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class NoteDetailActivity extends AppCompatActivity {
    TextView txt_english, txt_chinese, txt_content, txt_isOK, txt_label, txt_time;
    Note note;
    ImageView note_image;
    ImageButton edit_button;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);


        Intent intent = getIntent();
        note = (Note)intent.getSerializableExtra("note_item");
        //绑定控件
        boundView();
        //将获取的note信息 显示出来
        setMessageFromNote();

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoteDetailActivity.this,NoteEditActivity.class);
                intent.putExtra("note_item",note);
                startActivity(intent);
                finish();
            }
        });


    }

    private void setMessageFromNote() {
        //Bitmap bitmap=ImageManager.GetLocalBitmap(BookDetailActivity.this,book.getUuid());
        Bitmap bitmap=ImageManager.GetLocalBitmap(NoteDetailActivity.this,note.getUuid());
        note_image.setImageBitmap(bitmap);

        txt_english.setText(note.getEnglish());
        txt_chinese.setText(note.getChinese());
        txt_content.setText(note.getContent());
        txt_time.setText(note.getTime());
        txt_isOK.setText(note.getisOK_txt());
        //加载标签
        String label_content="";
        for(int i=0;i<note.getLabelList().size();i++){
            if(i==0){
                label_content=label_content+note.getLabelList().get(i);
            }else{
                label_content=label_content+","+note.getLabelList().get(i);
            }

        }
        txt_label.setText(label_content);
    }

    private void boundView(){
        note_image = (ImageView)findViewById(R.id.note_pic_detail);
        edit_button = (ImageButton)findViewById(R.id.btn_edit);
        txt_english = (TextView)findViewById(R.id.english_detail);
        txt_chinese = (TextView)findViewById(R.id.chinese_detail);
        txt_content = (TextView)findViewById(R.id.content_detail);
        txt_isOK = (TextView)findViewById(R.id.isOK_detail);
        txt_label = (TextView)findViewById(R.id.label_datail);
        txt_time = (TextView)findViewById(R.id.produce_time_detail);

    }
}
