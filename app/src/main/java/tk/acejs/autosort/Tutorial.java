package tk.acejs.autosort;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class Tutorial extends AppCompatActivity {
    private LinearLayout[] linearLayouts;
    private Button nextBtn, prevBtn;

    private final int layoutLength = 6;
    int pageCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        linearLayouts = new LinearLayout[layoutLength];

        for(int i = 0; i < layoutLength; i++) {
            int temp = getResources().getIdentifier("linear" +i, "id", getPackageName());
            linearLayouts[i] = findViewById(temp);
        }

        visibleSetting();

        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pageCount >= layoutLength - 1) {
                    finish();
                } else if(pageCount >= layoutLength - 2){
                    nextBtn.setText(R.string.close);
                    pageCount++;
                } else {
                    pageCount++;
                }

                visibleSetting();
            }
        });

        prevBtn = findViewById(R.id.prevBtn);
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pageCount > 0) {
                    nextBtn.setText(R.string.next);
                    pageCount--;
                }

                visibleSetting();
            }
        });
    }

    public void visibleSetting() {
        for(int i = 0; i < layoutLength; i++) {
            if(i == pageCount)
                linearLayouts[i].setVisibility(View.VISIBLE);
            else
                linearLayouts[i].setVisibility(View.INVISIBLE);
        }
    }
}
