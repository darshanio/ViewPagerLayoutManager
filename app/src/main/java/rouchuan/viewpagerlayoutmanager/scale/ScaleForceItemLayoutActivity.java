package rouchuan.viewpagerlayoutmanager.scale;

import com.leochuan.ScaleLayoutManager;

import rouchuan.viewpagerlayoutmanager.BaseActivity;
import rouchuan.viewpagerlayoutmanager.Util;

/**
 * Created by angcyo on 2020-3-19.
 */

public class ScaleForceItemLayoutActivity extends BaseActivity<ScaleLayoutManager, ScalePopUpWindow> {

    @Override
    protected ScaleLayoutManager createLayoutManager() {
        ScaleLayoutManager scaleLayoutManager = new ScaleLayoutManager(this, Util.Dp2px(this, -100));
        scaleLayoutManager.setFullItem(false);
        scaleLayoutManager.setForceSpaceMain(0);
        return scaleLayoutManager;
    }

    @Override
    protected ScalePopUpWindow createSettingPopUpWindow() {
        return new ScalePopUpWindow(this, getViewPagerLayoutManager(), getRecyclerView());
    }
}
