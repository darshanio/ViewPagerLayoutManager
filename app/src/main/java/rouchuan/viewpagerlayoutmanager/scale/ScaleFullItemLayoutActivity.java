package rouchuan.viewpagerlayoutmanager.scale;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.leochuan.ScaleLayoutManager;
import com.leochuan.ScrollHelper;

import rouchuan.viewpagerlayoutmanager.BaseActivity;
import rouchuan.viewpagerlayoutmanager.DataAdapter;
import rouchuan.viewpagerlayoutmanager.R;

/**
 * Created by angcyo on 2020-3-19.
 */

public class ScaleFullItemLayoutActivity extends BaseActivity<ScaleLayoutManager, ScalePopUpWindow> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataAdapter dataAdapter = new DataAdapter();
        dataAdapter.itemLayoutId = R.layout.item_image_full;
        dataAdapter.setOnItemClickListener(new DataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Toast.makeText(v.getContext(), "clicked:" + pos, Toast.LENGTH_SHORT).show();
                ScrollHelper.smoothScrollToTargetView(getRecyclerView(), v);
            }
        });
        getRecyclerView().setAdapter(dataAdapter);
    }

    @Override
    protected ScaleLayoutManager createLayoutManager() {
        ScaleLayoutManager scaleLayoutManager = new ScaleLayoutManager(this, 0);
        scaleLayoutManager.setFullItem(true);
        return scaleLayoutManager;
    }

    @Override
    protected ScalePopUpWindow createSettingPopUpWindow() {
        return new ScalePopUpWindow(this, getViewPagerLayoutManager(), getRecyclerView());
    }
}
