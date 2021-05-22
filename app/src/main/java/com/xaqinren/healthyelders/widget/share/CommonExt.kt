package com.xaqinren.healthyelders.widget.share

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject
import com.tencent.qcloud.ugckit.utils.ToastUtil
import com.xaqinren.healthyelders.global.AppApplication

/**
 * Created by Boyce
 * on 2020/11/21
 */


/**
 * 分享微信
 */
fun shareWxPage(context: Context, type: Int, id: Int, title: String, imageUrl: String, shareUrl: String) {
    if (AppApplication.mWXapi.isWXAppInstalled) {

        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(object : SimpleTarget<Bitmap>(200, 200) {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val wxObject = WXWebpageObject()
                        if (shareUrl.isEmpty()) {
                            wxObject.webpageUrl = "www.baidu.com"
                        } else {
                            wxObject.webpageUrl = shareUrl
                        }
                        val wxMediaMessage = WXMediaMessage()
                        wxMediaMessage.mediaObject = wxObject
                        wxMediaMessage.title = title
                        wxMediaMessage.setThumbImage(resource)
                        val wxRequest = SendMessageToWX.Req()
                        wxRequest.message = wxMediaMessage
                        wxRequest.scene = SendMessageToWX.Req.WXSceneSession;
                        AppApplication.mWXapi.sendReq(wxRequest)
                    }
                })
    } else {
        ToastUtil.toastShortMessage("您未安装微信")
    }
}
