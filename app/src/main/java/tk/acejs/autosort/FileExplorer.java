package tk.acejs.autosort;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class FileExplorer extends ListActivity {
    private List<String> item = null;
    private List<String> path = null;
    private String root = "/sdcard/";
    private TextView myPath;
    private Button Btn_select, Btn_cancel;
    private String tempDir = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_explorer);

        myPath = (TextView) findViewById(R.id.path);
        Btn_select = findViewById(R.id.Btn_select);
        Btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //반환
                Intent intent = new Intent();
                intent.putExtra("path", tempDir);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Btn_cancel = findViewById(R.id.Btn_cancel);
        Btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getDir(root);
    }

    private void getDir(String dirPath) {
        tempDir = dirPath;
        myPath.setText("Location: " + dirPath);

        item = new ArrayList<String>();
        path = new ArrayList<String>();

        File f = new File(dirPath);
        File[] files = f.listFiles();

        item.add(root);
        path.add(root);

        item.add("..");
        path.add(f.getParent());

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            path.add(file.getPath());

            if (file.isDirectory())
                item.add(file.getName() + "/");
            else
                item.add(file.getName());
        }

        ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.file_explorer_row, item);
        setListAdapter(fileList);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        File file = new File(path.get(position));

        if (file.isDirectory()) {
            if (file.canRead())
                getDir(path.get(position));
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(null)
                    .setTitle(R.string.not_dir)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                }
                            }).show();
        }
    }
}