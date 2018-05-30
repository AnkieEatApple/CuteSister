package com.example.ankie.drysister;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.ankie.drysister.common.Constans;
import com.example.ankie.drysister.service.MCenterDownloaderService;
import com.example.ankie.drysister.sister.Sister;
import com.example.ankie.drysister.sister.SisterApi;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    /**
     * TAG
     */
    private static final String TAG = "MainActivity";
    /**
     * 图片显示
     */
    private ImageView showImg;
    /**
     * 当前显示的
     */
    private int curPos = 0;     // 当前显示的是哪一张
    /**
     * 那一页开始
     */
    private int page = 1;       // 当前页数
    /**
     * 获取sisterList
     */
    private SisterApi sisterApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        setup();
    }

    private void initUI() {
        Button showBtn = findViewById(R.id.btn_show);
        Button refreshBtn = findViewById(R.id.btn_refresh);
        showImg = findViewById(R.id.img_show);

        showBtn.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);
    }

    private void setup () {
        sisterApi = new SisterApi();
        MCenterDownloaderService.initDownloaderService(this);
        MCenterDownloaderService.getInstance().init();
        sisterApi.fetchSister(Constans.Param.NUMBER, page);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_show:
                if (curPos > 9) {
                    curPos = 0;
                }
                ArrayList<Sister> mSisList = sisterApi.getSisterList();
                if (mSisList == null) {
                    Log.e(TAG, "onClick: " + "(data == null)" );
                    return ;
                }
                String url = mSisList.get(curPos).getUrl();
                if (url == null) {
                    Log.e(TAG, "onClick: " + "(url == null)" );
                    return;
                }
                ImageLoader.getInstance().displayImage(url, null, showImg);
                curPos++;
                break;
            case R.id.btn_refresh:
                page++;
                sisterApi.fetchSister(Constans.Param.NUMBER, page);
                curPos = 0;
                break;
        }
    }
}
