package student.jnu.com.myplatform;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 2020/4/3.
 */

public class NoteListManager {
    public static String NOTELISTNAME = "noteList";

    public static boolean save(Context context, List<Note> noteList){
        String dirsrc = context.getFilesDir()+"";
        File dir = new File(dirsrc);
        if(!dir.exists()){
            dir.mkdirs();
        }

        String src = context.getFilesDir() + "/" + NOTELISTNAME;
        File outputFile = new File(src);

        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            Log.e("BookShelf", "save: 创建文件失败");
            return false;
        }

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(outputFile);
            oos = new ObjectOutputStream(fos);
            for(int i=0; i<noteList.size();i++){
                oos.writeObject(noteList.get(i));
            }
            oos.flush();
            return true;
        } catch (FileNotFoundException e) {
            Log.e("BookShelf", "save: 文件不存在");
            return false;
        } catch (IOException e) {
            Log.e("BookShelf", "save: 输入输出错误");
            return false;
        } finally {
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e("BookShelf", "save: 关闭fos失败");
                }
            }
            if(oos != null){
                try {
                    oos.close();
                } catch (IOException e) {
                    Log.e("BookShelf", "save: 关闭oos失败");
                }
            }
        }
    }

    public static List<Note> read(Context context){
        List<Note> noteList = new ArrayList<>();
        String src = context.getFilesDir() + "/" + NOTELISTNAME;
        File inputFile = new File(src);
        if(!inputFile.exists()) return noteList;

        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(inputFile);
            ois = new ObjectInputStream(fis);
            Note note = null;

            while( (note = (Note) ois.readObject()) != null){
                noteList.add(note);
            }
            return noteList;

        } catch (FileNotFoundException e) {
            Log.e("Note", "read: 文件未找到");
            //return false;
        } catch (IOException e) {
            Log.e("Note", "read: 输入输出错误");
            //return false;
        } catch (ClassNotFoundException e) {
            Log.e("Note", "read: 类没有找到");
            //return false;
        } finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    Log.e("Note", "read: 关闭fis失败");
                }
            }
            if(ois != null){
                try {
                    ois.close();
                } catch (IOException e) {
                    Log.e("Note", "read: 关闭ois失败");
                }
            }
        }
        return noteList;
    }
}
