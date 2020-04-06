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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

/**
 * Created by ASUS on 2020/3/30.
 */

public class Note implements Serializable {

    private String english; //英文
    private String chinese; //翻译后的中文
    private String content; //我对这个翻译想要记录的 一些笔记
    private String time;    //这个记录创建的时间
    private List<String> labelList;   //标签列表， eg;长短句，高考的内容， 中考内容，短语，单词等等，用来标注这个english有什么性质，便于用户筛选
    private int isOK;      //这个笔记对于用户来说是否已经熟悉了
    private String uuid;

    public Note(String english, String chinese, String content){

        this.english = english;
        this.chinese = chinese;
        this.content = content;
        this.labelList = new ArrayList<>();
        this.isOK = 0;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        this.time = df.format(new Date());
        this.uuid= UUID.randomUUID().toString().replaceAll("-","");

    }

    public Note(String english, String chinese, String content, int isOK){
        this.english = english;
        this.chinese = chinese;
        this.content = content;
        this.labelList = new ArrayList<>();
        this.isOK = isOK;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        this.time = df.format(new Date());
        this.uuid= UUID.randomUUID().toString().replaceAll("-","");

    }

    public String getisOK_txt(){
        return ( isOK == 1?"已掌握":"未掌握" );
    }

    public String getTime() {
        return time;
    }

    public String getChinese() {
        return chinese;
    }

    public String getEnglish() {
        return english;
    }

    public int getIsOK() {
        return isOK;
    }

    public String getContent() {
        return content;
    }

    public void setChinese(String chinese) {
        this.chinese = chinese;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public void setIsOK(int isOK) {
        this.isOK = isOK;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getLabelList() {
        return labelList;
    }

    public void setLabelList(List<String> labelList) {
        this.labelList = labelList;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
