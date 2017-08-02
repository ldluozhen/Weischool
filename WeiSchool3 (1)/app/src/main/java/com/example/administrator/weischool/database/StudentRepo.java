package com.example.administrator.weischool.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/6/28.
 */

public class StudentRepo {
    private SQLite_db dbHelper;

    public StudentRepo(Context context){
        dbHelper=new SQLite_db(context);
    }

    public int insert(Student student){
        //打开连接，写入数据
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(Student.KEY_age,student.age);
        values.put(Student.KEY_email,student.email);
        values.put(Student.KEY_name,student.name);
        //
        long student_Id=db.insert(Student.TABLE,null,values);
        db.close();
        return (int)student_Id;
    }

    public void delete(int student_Id){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        db.delete(Student.TABLE,Student.KEY_ID+"=?", new String[]{String.valueOf(student_Id)});
        db.close();
    }
    public void update(Student student){
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();

        values.put(Student.KEY_age,student.age);
        values.put(Student.KEY_email,student.email);
        values.put(Student.KEY_name,student.name);

        db.update(Student.TABLE,values,Student.KEY_ID+"=?",new String[] { String.valueOf(student.student_ID) });
        db.close();
    }

    public ArrayList<HashMap<String, String>> getStudentList(){
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        String selectQuery="SELECT "+
                Student.KEY_ID+","+
                Student.KEY_name+","+
                Student.KEY_email+","+
                Student.KEY_age+" FROM "+Student.TABLE;
        ArrayList<HashMap<String,String>> studentList=new ArrayList<HashMap<String, String>>();
        Cursor cursor=db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()){
            do{
                HashMap<String,String> student=new HashMap<String,String>();
                student.put("id",cursor.getString(cursor.getColumnIndex(Student.KEY_ID)));
                student.put("name",cursor.getString(cursor.getColumnIndex(Student.KEY_name)));
                studentList.add(student);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return studentList;
    }

    public Student getStudentById(int Id){
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        String selectQuery="SELECT "+
                Student.KEY_ID + "," +
                Student.KEY_name + "," +
                Student.KEY_email + "," +
                Student.KEY_age +
                " FROM " + Student.TABLE
                + " WHERE " +
                Student.KEY_ID + "=?";
        int iCount=0;
        Student student=new Student();
        Cursor cursor=db.rawQuery(selectQuery,new String[]{String.valueOf(Id)});
        if(cursor.moveToFirst()){
            do{
                student.student_ID =cursor.getInt(cursor.getColumnIndex(Student.KEY_ID));
                student.name =cursor.getString(cursor.getColumnIndex(Student.KEY_name));
                student.email  =cursor.getString(cursor.getColumnIndex(Student.KEY_email));
                student.age =cursor.getInt(cursor.getColumnIndex(Student.KEY_age));
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return student;
    }
}