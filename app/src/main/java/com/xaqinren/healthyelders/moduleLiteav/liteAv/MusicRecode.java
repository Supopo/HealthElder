package com.xaqinren.healthyelders.moduleLiteav.liteAv;

import android.util.Log;

import com.xaqinren.healthyelders.moduleLiteav.bean.MMusicItemBean;
import com.xaqinren.healthyelders.utils.LogUtils;

public class MusicRecode {
    private static MusicRecode musicRecode;
    public static int CURRENT_BOURN = 0;
    private MusicRecode(){}
    public static MusicRecode getInstance(){
        if (musicRecode == null) {
            musicRecode = new MusicRecode();
        }
        return musicRecode;
    }

    //使用的背景音乐
    private MMusicItemBean useMusicItem;

    public MMusicItemBean getUseMusicItem() {
        return useMusicItem;
    }

    public void setUseMusicItem(MMusicItemBean useMusicItem) {
        this.useMusicItem = useMusicItem;
        LogUtils.e("MusicRecode", "设置value = " + useMusicItem);
    }


}
