package tk.acejs.autosort;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView mainList;
    private MainListAdapter mainAdapter;
    private WordDBHelper helper;
    private ArrayList<MainListItem> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                Toast.makeText(getApplicationContext(), R.string.permission_request, Toast.LENGTH_LONG).show();
                finish();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        SharedPreferences sharePre = getSharedPreferences("autosort_tutorial", MODE_PRIVATE);
        if (sharePre.getBoolean("autosort_tutorial", true) == true) {
            Intent intent = new Intent(getApplicationContext(), Tutorial.class);
            startActivity(intent);
            SharedPreferences.Editor editor = sharePre.edit();
            editor.putBoolean("autosort_tutorial", false);
            editor.commit();
        }

        new WordDBHelper(getApplicationContext());

        mainList = findViewById(R.id.main_list);
        mainAdapter = new MainListAdapter(getApplicationContext());
        mainAdapter.addItem();
        mainList.setAdapter(mainAdapter);
        mainList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                WordDBHelper helper = new WordDBHelper(getApplicationContext());
                TextView textView = (TextView) view.findViewById(R.id.extensionsItem);
                String ext = textView.getText().toString();
                helper.deleteItem(ext);
                mainAdapter.addItem();
                mainAdapter.notifyDataSetChanged();
                return false;
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        Toast.makeText(getApplicationContext(), R.string.permission_request, Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), AddSortRule.class);
                    startActivityForResult(intent, 3000);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == 3000) {
            //리스트뷰에 추가
            System.out.println("request success");
            mainAdapter.addItem();
            mainAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        Switch sw = (Switch) menu.findItem(R.id.on_off_switch).getActionView().findViewById(R.id.sw);

        if (isServiceRunningCheck()) sw.setChecked(true);
        else sw.setChecked(false);

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent(getApplicationContext(), AutoSortService.class);
                    startService(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), AutoSortService.class);
                    stopService(intent);
                }
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.start_clean) {
            init();
            for (int i = 0; i < items.size(); i++) {
                FileObserver observer = new FileObserver(getApplicationContext(), items.get(i).getTargetPath(), android.os.FileObserver.CLOSE_WRITE);
                observer.startWatching();
                observer.onEvent(android.os.FileObserver.CLOSE_WRITE, null);
                observer.stopWatching();
            }

            return true;
        } else if (id == R.id.on_off_switch) {
            //서비스 on off
            Switch sw = findViewById(R.id.on_off_switch);

            if (isServiceRunningCheck()) sw.setChecked(true);
            else sw.setChecked(false);
        } else if (id == R.id.tutorial) {
            Intent intent = new Intent(getApplicationContext(), Tutorial.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("tk.acejs.autosort.AutoSortService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
