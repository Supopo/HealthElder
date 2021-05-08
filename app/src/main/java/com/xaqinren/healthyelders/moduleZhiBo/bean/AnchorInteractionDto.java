package com.xaqinren.healthyelders.moduleZhiBo.bean;

/**
 * Created by Lee. on 2021/5/7.
 * PK设置
 */
public class AnchorInteractionDto {

    public int pkDuration;
    public boolean canGiftToPkAnchor;    //允许观众给pk主播送礼 true: 允许 false: 不允许
    public boolean canPk;               //接受pk true: 接受 false: 不接受
    public boolean canFriendsPk;        // 接受互关主播Pk true: 接受 false: 不接受
    public boolean canSearchPk;         //是否接受推荐或搜索pk true: 接受 false: 不接受
    public boolean closedPkAnchorVoice;     //是否关闭对方主播声音 true: 关闭 false: 不关闭

}

