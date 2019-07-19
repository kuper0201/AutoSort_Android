package tk.acejs.autosort;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MainListAdapter extends BaseAdapter {
    private WordDBHelper helper;
    private Context context;
    private ArrayList<MainListItem> items = new ArrayList<>();

    public MainListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.main_item_row, parent, false);
        }

        TextView targetItem = convertView.findViewById(R.id.targetItem);
        TextView resultItem = convertView.findViewById(R.id.resultItem);
        TextView extensionItem = convertView.findViewById(R.id.extensionsItem);

        MainListItem mItem = items.get(position);

        targetItem.setText(mItem.getTargetPath());
        resultItem.setText(mItem.getResultPath());
        extensionItem.setText(mItem.getExtension());

        return convertView;
    }

    public void addItem() {
        helper = new WordDBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor;
        cursor = db.rawQuery("select * from sortitem",null);
        items.clear();
        while(cursor.moveToNext()) {
            MainListItem item = new MainListItem();
            item.setTargetPath(cursor.getString(1));
            item.setResultPath(cursor.getString(2));
            item.setExtension(cursor.getString(3));
            items.add(item);
        }
    }
}
