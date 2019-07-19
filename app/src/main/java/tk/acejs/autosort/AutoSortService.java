package tk.acejs.autosort;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;

public class AutoSortService extends Service {
    public static final ArrayList<FileObserver> observers = new ArrayList<>();
    WordDBHelper helper;
    private ArrayList<MainListItem> items = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Service Start!");
    }

    @Override
    public void onDestroy() {
        System.out.println("Service Destroy!");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //DB추가
        init();

        for (int i = 0; i < items.size(); i++) {
            FileObserver observer = new FileObserver(getApplicationContext(), items.get(i).getTargetPath(), android.os.FileObserver.CLOSE_WRITE);
            observer.startWatching();
            observers.add(observer);
        }
        System.out.println("Service Running...");

        return super.onStartCommand(intent, flags, startId);
    }

    public void init() {
        helper = new WordDBHelper(getApplicationContext());
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
    }
}
