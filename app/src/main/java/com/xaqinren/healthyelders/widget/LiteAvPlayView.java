package com.xaqinren.healthyelders.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import com.tencent.qcloud.xiaoshipin.mainui.list.TCVideoInfo;
import com.tencent.qcloud.xiaoshipin.play.PlayerInfo;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.xaqinren.healthyelders.R;


public class LiteAvPlayView extends RelativeLayout {

    RelativeLayout rlContainer;
    TCVideoInfo videoInfo;
    TXCloudVideoView playView;
    ImageView playerIvCover;
    TextView videoStatus;
    TextView authorTv;
    TextView describeTv;
    TextView musicDesTv;
    CircleImageView userAvatarIv;
    CircleImageView attentionBtn;
    LinearLayout likeGroup;
    ImageView likeIv;
    TextView likeCountTv;

    LinearLayout plGroup;
    ImageView plIv;
    TextView plCountTv;

    LinearLayout shareGroup;
    ImageView shareIv;
    TextView shareCountTv;

    ImageView playBtn;

    de.hdodenhof.circleimageview.CircleImageView musicBtn;

    OnItemClickListener onItemClickListener;

    public TXCloudVideoView getPlayView() {
        return playView;
    }

    public de.hdodenhof.circleimageview.CircleImageView getMusicBtn() {
        return musicBtn;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public LiteAvPlayView(Context context) {
        super(context);
        init();
    }

    public LiteAvPlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LiteAvPlayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_lite_av_player, this);
        rlContainer = findViewById(R.id.rl_container);
        playView =  findViewById(R.id.player_cloud_view);
        shareGroup = findViewById(R.id.share_group);
        plGroup = findViewById(R.id.pl_group);
        musicBtn = findViewById(R.id.music_btn);

        shareGroup.setOnClickListener(view -> {
            if (onItemClickListener!=null) onItemClickListener.onShareClick(videoInfo.fileid);
        });
        plGroup.setOnClickListener(view -> {
            if (onItemClickListener!=null) onItemClickListener.onCommentClick(videoInfo.fileid);
        });
    }

    public void setVideoInfo(TCVideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    public void initView() {
        //????????????
    }

    public void attachVideoView(PlayerInfo info) {
        info.playerView = playView;
        info.vodPlayer.setPlayerView(playView);
    }

    public interface OnItemClickListener{
        void onShareClick(String videoId);

        void onLikeClick(String videoId);

        void onCommentClick(String videoId);

        void onAvatarClick(String videoId);

        void onLookClick(String videoId);
    }

}
