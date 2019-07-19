package tk.acejs.autosort;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;

public class AddSortRule extends AppCompatActivity {
    private WordDBHelper helper;
    private TextView TextView_targetPath, TextView_resultPath;
    private Button Btn_targetPath, Btn_resultPath, Btn_addExt, Btn_save, Btn_cancel;
    private EditText Edit_Extension;
    private ListView List_extensions;
    private String targetPath = null, resultPath = null;
    private ArrayList<String> extensions;
    private HashSet<String> extHash;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_sort_rule);

        extensions = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.extension_row, extensions);
        helper = new WordDBHelper(this);

        TextView_targetPath = findViewById(R.id.TextView_targetPath);
        TextView_resultPath = findViewById(R.id.TextView_resultPath);
        Edit_Extension = findViewById(R.id.Edit_extension);

        List_extensions = findViewById(R.id.List_sortRule);

        Btn_save = findViewById(R.id.Btn_save);
        Btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //저장, 이전 액티비티에 데이터 전송 후 종료
                boolean dialogflag = true;
                if (targetPath != null && resultPath != null) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);

                    SQLiteDatabase db = helper.getWritableDatabase();
                    for (int i = 0; i < extensions.size(); i++) {
                        if (!helper.searchSql(extensions.get(i))) {
                            dialogflag = false;
                            break;
                        }
                    }

                    if (dialogflag) {
                        for (int i = 0; i < extensions.size(); i++)
                            db.execSQL("insert into sortitem values(null" + ",'" + targetPath + "','" + resultPath + "','" + extensions.get(i) + "')");
                        finish();
                    } else {
                        new AlertDialog.Builder(AddSortRule.this)
                                .setIcon(null)
                                .setTitle(R.string.already_item)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub
                                    }
                                }).show();
                    }

                } else {
                    new AlertDialog.Builder(AddSortRule.this)
                            .setIcon(null)
                            .setTitle("Please select path!!!")
                            .setPositiveButton(R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub
                                        }
                                    }).show();
                }
            }
        });

        Btn_cancel = findViewById(R.id.Btn_cancel);
        Btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //취소, 액티비티 종료
                finish();
            }
        });

        Btn_targetPath = findViewById(R.id.Btn_targetPath);
        Btn_targetPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //타겟 경로 지정
                Intent intent = new Intent(getApplicationContext(), FileExplorer.class);
                startActivityForResult(intent, 1111);
            }
        });

        Btn_resultPath = findViewById(R.id.Btn_resultPath);
        Btn_resultPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //결과 경로 지정
                Intent intent = new Intent(getApplicationContext(), FileExplorer.class);
                startActivityForResult(intent, 2222);
            }
        });

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
                if (!ps.matcher(source).matches()) {
                    return "";
                }
                return null;
            }
        };

        Edit_Extension = findViewById(R.id.Edit_extension);
        Edit_Extension.setFilters(new InputFilter[]{filter});
        Btn_addExt = findViewById(R.id.Btn_addExt);
        Btn_addExt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Edit_Extension.getText().toString().matches("") && (extensions.indexOf(Edit_Extension.getText().toString()) == -1)) {
                    extensions.add(Edit_Extension.getText().toString());
                    Edit_Extension.setText("");
                    List_extensions.setAdapter(arrayAdapter);
                    List_extensions.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            extensions.remove(position);
                            List_extensions.setAdapter(arrayAdapter);
                            return false;
                        }
                    });
                } else {
                    Edit_Extension.setText("");
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

        if (requestCode == 1111) {
            targetPath = data.getStringExtra("path");
            TextView_targetPath.setText(targetPath);
        } else if (requestCode == 2222) {
            resultPath = data.getStringExtra("path");
            TextView_resultPath.setText(resultPath);
        }
    }
}
