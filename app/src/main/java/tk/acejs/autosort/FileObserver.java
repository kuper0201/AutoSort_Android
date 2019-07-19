package tk.acejs.autosort;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class FileObserver extends android.os.FileObserver {
    private Context context;
    private String mpath;
    private int mask;

    public FileObserver(String path) {
        super(path);
        this.mpath = path;
    }

    public FileObserver(Context context, String path, int mask) {
        super(path, mask);
        this.context = context;
        this.mpath = path;
        this.mask = mask;
    }

    @Override
    public void onEvent(int event, @Nullable String path) {
        WordDBHelper helper;
        ArrayList<MainListItem> items = new ArrayList<>();

        if (event == mask) {
            helper = new WordDBHelper(context);
            SQLiteDatabase db = helper.getWritableDatabase();
            Cursor cursor;
            cursor = db.rawQuery("select * from sortitem", null);
            items.clear();
            while (cursor.moveToNext()) {
                MainListItem item = new MainListItem();
                item.setTargetPath(cursor.getString(1));
                item.setResultPath(cursor.getString(2));
                item.setExtension(cursor.getString(3));
                items.add(item);
            }

            File root = new File(mpath);
            File[] files = root.listFiles();

            for(File file : files) {
                int index = file.toString().lastIndexOf(".");
                String ext = file.toString().substring(index + 1);

                for(int i = 0; i < items.size(); i++) {
                    if(items.get(i).getExtension().equals(ext)) {
                        int nameIndex = file.toString().lastIndexOf("/");
                        String fileName = file.toString().substring(nameIndex + 1);

                        moveFile(fileName, file.toString(), items.get(i).getResultPath());
                        System.out.println("Move! " +fileName +" " +file.toString() +" " +items.get(i).getResultPath());
                    }
                }
            }
        }
    }

    public void moveFile(String fileName, String beforeFilePath, String afterFilePath) {
        String path = afterFilePath + "/" + fileName;

        System.out.println(fileName +" " +beforeFilePath +" " +path);
        File file = new File(beforeFilePath);

        if(file.renameTo(new File(path))) {
            System.out.println("move");
        }
    }
}