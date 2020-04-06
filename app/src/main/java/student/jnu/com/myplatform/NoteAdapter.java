package student.jnu.com.myplatform;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by ASUS on 2020/3/30.
 */

public class NoteAdapter extends RecyclerView.Adapter <NoteAdapter.ViewHolder> {
    private List<Note> mNoteList;
    NotesListActivity notesListActivity;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View noteView;
        ImageView noteImage;
        TextView txt_English;
        TextView txt_Chinese;
        TextView time;

        public ViewHolder(View view){
            super(view);
            noteView = view;
            noteImage = (ImageView)view.findViewById(R.id.book_image);
            txt_English = (TextView)view.findViewById(R.id.english);
            txt_Chinese = (TextView)view.findViewById(R.id.chinese);
            time = (TextView)view.findViewById(R.id.produce_time);
        }
    }

    public NoteAdapter(List<Note> noteList, NotesListActivity notesListActivity){
        this.notesListActivity = notesListActivity;
        this.mNoteList = noteList;
    }
    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.noteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Note note = mNoteList.get(position);
                Toast.makeText(v.getContext(), note.getEnglish()+"  -- "+note.getChinese(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(), NoteDetailActivity.class);
                intent.putExtra("note_item",note);
                NoteListManager.save(v.getContext(), notesListActivity.mNotesList);
                saveLabelList();

                notesListActivity.startActivity(intent);
            }
        });

        holder.noteView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                final int position = holder.getAdapterPosition();
                final Note note = mNoteList.get(position);
                AlertDialog.Builder dialog=new AlertDialog.Builder(v.getContext());
                dialog.setTitle("是否删除笔记");                //dialog标题
                dialog.setCancelable(false);                //按back键不可取消dialog
                       //把editText放入dialog中
                //确定按钮
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mNoteList.remove(note);
                        NoteListManager.save(v.getContext(), mNoteList);
                        //notesListActivity.mNotesList.remove(note);
                        //NoteListManager.save(v.getContext(), mNoteList);
                        notesListActivity.noteAdapter.notifyDataSetChanged();
                    }
                });
                //取消按钮
                dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();


                //mNoteList.remove(position);

                return false;
            }
        });

        return holder;
    }


    @Override
    public void onBindViewHolder(NoteAdapter.ViewHolder holder, int position) {
        Note note = mNoteList.get(position);
        Bitmap bitmap=ImageManager.GetLocalBitmap(notesListActivity,note.getUuid());
        holder.noteImage.setImageBitmap(bitmap);
        holder.txt_English.setText(note.getEnglish());
        holder.txt_Chinese.setText(note.getChinese());
        holder.time.setText(note.getTime());
    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }

    private void saveLabelList(){
        SharedPreferences sp = notesListActivity.getSharedPreferences("labelList", Activity.MODE_PRIVATE);
        Gson gson = new Gson();
        String data = gson.toJson(notesListActivity.labelList);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("label_List_json",data);
        editor.commit();
    }
}
