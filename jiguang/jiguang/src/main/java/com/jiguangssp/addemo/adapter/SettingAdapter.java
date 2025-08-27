package com.jiguangssp.addemo.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jiguangssp.addemo.entity.AdSettingData;
import com.jiguangssp.addemo.util.SPUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jiguang.jgssp.ad.data.ADJgAdType;

import com.jiguangssp.addemo.R;
import com.jiguangssp.addemo.activity.PositionSettingActivity;
import com.jiguangssp.addemo.activity.SettingActivity;
import com.jiguangssp.addemo.constant.ADJgDemoConstant;

/**
 * @author songzi
 * @date 2021/4/29
 */
public class SettingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * 标题数据类型
     */
    public static final int ITEM_VIEW_TYPE_TITLE_DATA = 0;

    /**
     * 广告设置类型
     */
    public static final int ITEM_VIEW_TYPE_AD_DATA = 1;

    /**
     * 渠道设置类型
     */
    public static final int ITEM_VIEW_TYPE_PLATFORM_DATA = 2;


    private Context mContext;
    private List<AdSettingData> dataList = new ArrayList<>();

    public SettingAdapter(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemViewType) {
        switch (itemViewType) {
            case ITEM_VIEW_TYPE_TITLE_DATA:
                return new TitleViewHolder(viewGroup);
            case ITEM_VIEW_TYPE_AD_DATA:
                return new SettingAdapter.AdViewHolder(viewGroup);
            default:
                return new SettingAdapter.PlatformViewHolder(viewGroup);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        AdSettingData settingData = dataList.get(position);
        if (viewHolder instanceof SettingAdapter.AdViewHolder) {
            ((SettingAdapter.AdViewHolder) viewHolder).setData(settingData, mContext);
        } else if (viewHolder instanceof SettingAdapter.PlatformViewHolder) {
            ((SettingAdapter.PlatformViewHolder) viewHolder).setData(settingData, mContext);
        } else if (viewHolder instanceof SettingAdapter.TitleViewHolder) {
            ((SettingAdapter.TitleViewHolder) viewHolder).setData(settingData);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type = dataList.get(position).getType();
        return type;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setDataList(List<AdSettingData> dataList) {
        this.dataList = dataList;
    }

    private static class TitleViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;

        TitleViewHolder(ViewGroup viewGroup) {
            super(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_setting_title, viewGroup, false));
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }

        public void setData(AdSettingData settingData) {
            tvTitle.setText(settingData.getTitle());
        }

    }

    private static class AdViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final TextView tvPlatform;

        AdViewHolder(ViewGroup viewGroup) {
            super(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_setting_ad, viewGroup, false));
            tvName = itemView.findViewById(R.id.tvName);
            tvPlatform = itemView.findViewById(R.id.tvPlatform);

        }

        public void setData(AdSettingData settingData, Context context) {
            String title = settingData.getTitle();
            tvPlatform.setVisibility(View.INVISIBLE);
            tvName.setText(title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String adType = null;
                    ArrayList<String> posIdList = new ArrayList<>();
                    switch (title) {
                        case "开屏广告":
                            adType = ADJgAdType.TYPE_SPLASH;
                            posIdList.add(ADJgDemoConstant.SPLASH_AD_POS_ID1);
                            break;
                        case "Banner广告":
                            adType = ADJgAdType.TYPE_BANNER;
                            posIdList.add(ADJgDemoConstant.BANNER_AD_POS_ID1);
                            posIdList.add(ADJgDemoConstant.BANNER_AD_POS_ID2);
                            posIdList.add(ADJgDemoConstant.BANNER_AD_POS_ID3);
                            break;
                        case "信息流广告":
                            adType = ADJgAdType.TYPE_FLOW;
                            posIdList.add(ADJgDemoConstant.NATIVE_AD_POS_ID1);
                            posIdList.add(ADJgDemoConstant.NATIVE_AD_POS_ID2);
                            break;
                        case "激励视频广告":
                            adType = ADJgAdType.TYPE_REWARD_VOD;
                            posIdList.add(ADJgDemoConstant.REWARD_VOD_AD_POS_ID1);
                            posIdList.add(ADJgDemoConstant.REWARD_VOD_AD_POS_ID2);
                            break;
                        case "插屏广告":
                            adType = ADJgAdType.TYPE_INTERSTITIAL;
                            posIdList.add(ADJgDemoConstant.INTERSTITIAL_AD_POS_ID1);
                            posIdList.add(ADJgDemoConstant.INTERSTITIAL_AD_POS_ID2);
                            break;
                        default:
                            break;
                    }
                    if (!TextUtils.isEmpty(adType)) {
                        PositionSettingActivity.start(context, adType, posIdList);
                    }
                }
            });
        }
    }


    private static class PlatformViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvPlatform;
        private HashMap<String, String> platformMap = new HashMap<>();

        PlatformViewHolder(ViewGroup viewGroup) {
            super(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_setting_ad, viewGroup, false));
            tvName = itemView.findViewById(R.id.tvName);
            tvPlatform = itemView.findViewById(R.id.tvPlatform);
            tvPlatform.setVisibility(View.VISIBLE);
            initPlatformData();
        }

        public void setData(AdSettingData settingData, Context context) {
            tvName.setText(settingData.getTitle());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPlatformSelectDialog(context);
                }
            });
        }

        private void initPlatformData() {
            platformMap.put("所有(null或空字符串)", "");
            platformMap.put("天目(tianmu)", "tianmu");
            platformMap.put("极光Ads(jgads)", "jgads");
            platformMap.put("优量汇(gdt)", "gdt");
            platformMap.put("头条/穿山甲(toutiao)", "toutiao");
            platformMap.put("百度/百青藤(baidu)", "baidu");
            platformMap.put("快手(ksad)", "ksad");
            platformMap.put("汇量/Mobvsita(mintegral)", "mintegral");
            String currentFinalPlat = ADJgDemoConstant.SPLASH_AD_ONLY_SUPPORT_PLATFORM;
            String displayPlat = getPlatformKey(platformMap, currentFinalPlat);
            tvPlatform.setText(displayPlat);

        }

        private String getPlatformKey(Map<String, String> map, String value) {
            if (TextUtils.isEmpty(value)) {
                value = "";
            }
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (value.equals(entry.getValue())) {
                    return entry.getKey();
                }
            }
            return "";
        }

        private void showPlatformSelectDialog(Context context) {
            new AlertDialog.Builder(context)
                    .setItems(R.array.platforms_zh, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String platformKey = context.getResources().getStringArray(R.array.platforms_zh)[which];
                            tvPlatform.setText(platformKey);
                            String onlySupportPlatform = platformMap.get(platformKey);
                            SPUtil.putString(context, SettingActivity.KEY_ONLY_SUPPORT_PLATFORM, onlySupportPlatform);
                            ADJgDemoConstant.SPLASH_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
                            ADJgDemoConstant.BANNER_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
                            ADJgDemoConstant.NATIVE_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
                            ADJgDemoConstant.REWARD_VOD_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
                            ADJgDemoConstant.INTERSTITIAL_AD_ONLY_SUPPORT_PLATFORM = onlySupportPlatform;
                        }
                    })
                    .create()
                    .show();
        }
    }
}
