package tk.acejs.autosort;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class WordDBHelper extends SQLiteOpenHelper {
    Context con;

    public WordDBHelper(Context context){
        super(context, "sortitem.db",null,1);
        // TODO Auto-generated constructor stub
        con = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //테이블 생성
        db.execSQL("create table sortitem(id integer primary key autoincrement,targetPath text,resultPath text,extension text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldViersion, int newVersion) {
        //테이블을 지우는 구문을 수행
        db.execSQL("drop table if exists sortitem");
        //테이블 다시 생성
        onCreate(db);

    }

    public void deleteItem(String extension) {
        WordDBHelper helper = new WordDBHelper(con);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("DELETE FROM sortitem WHERE extension='" +extension +"'");
    }

    public boolean searchSql(String data) {
        boolean flag = false;
        WordDBHelper helper = new WordDBHelper(con);

        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("select EXISTS (select * from sortitem where extension=" +"'" +data +"')",null);

        cursor.moveToNext();
        if(cursor.getInt(0) != 1) {
            System.out.println(cursor.getInt(0));
            flag = true;
        }
        return flag;
    }
}
