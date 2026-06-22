package com.meishu.sdkdemo.adactivity.feed;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.meishu.sdk.core.ad.MsAdSlot;
import com.meishu.sdk.core.ad.recycler.RecyclerAdData;
import com.meishu.sdk.core.ad.recycler.RecyclerAdEventListener;
import com.meishu.sdk.core.ad.recycler.RecyclerAdMediaListener;
import com.meishu.sdk.core.ad.recycler.RecyclerMixAdLoader;
import com.meishu.sdk.core.ad.recycler.RecylcerAdInteractionListener;
import com.meishu.sdk.core.utils.AdErrorInfo;
import com.meishu.sdk.core.utils.LogUtil;
import com.meishu.sdk.core.utils.MsAdPatternType;
import com.meishu.sdkdemo.R;
import com.meishu.sdkdemo.adid.IdProviderFactory;
import com.meishu.sdkdemo.utils.DimensionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;



import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE;

public class MixRecyclerActivity extends AppCompatActivity  {

    private static final String TAG = "MixRecyclerActivity_";
    private RecyclerMixAdLoader recyclerAdLoader;
    public static final String SHOW_DETAIL = "showDetail";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admate_activity_mix_recycler);
        initView();
        String pid  = getIntent().getStringExtra("alternativePlaceId");
        if (TextUtils.isEmpty(pid)) {
            pid = IdProviderFactory.getDefaultProvider().feedMix();
        }
        boolean showDetail = getIntent().getBooleanExtra(SHOW_DETAIL, false);
        String token = getIntent().getStringExtra("token");

        MsAdSlot adSlot = new MsAdSlot.Builder()
                .setPid(pid)
                .setIsshowDetail(showDetail)
                .build();
        recyclerAdLoader = new RecyclerMixAdLoader(this, adSlot, new RecyclerAdEventListener() {


            @Override
            public void onAdError(AdErrorInfo errorInfo) {
                LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                mIsLoading = false;
            }

            @Override
            public void onAdReady(List<RecyclerAdData> list) {
                LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                mIsLoading = false;
                loadedAdDatas.addAll(list);
                Message msg = mHandler.obtainMessage(MSG_REFRESH_LIST, list);
                mHandler.sendMessage(msg);
            }
        });//信息流
        if (TextUtils.isEmpty(token)){
            recyclerAdLoader.loadAd();
        }else {
            recyclerAdLoader.loadAd(token);
        }
    }

    private CustomAdapter mAdapter;
    private boolean mIsLoading = true;

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.activity_mix_recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        List<NormalItem> list = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            list.add(new NormalItem("No." + i + " Init Data"));
        }
        mAdapter = new CustomAdapter(this, list);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                if (!mIsLoading && newState == SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1)) {
                    mIsLoading = true;
                    recyclerAdLoader.loadAd();
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private List<RecyclerAdData> loadedAdDatas = new ArrayList<>();
    private static final int MSG_REFRESH_LIST = 1;
    private H mHandler = new H();


    class NormalItem {
        private String mTitle;

        public NormalItem(int index) {
            this("No." + index + " Normal Data");
        }

        public NormalItem(String title) {
            this.mTitle = title;
        }

        public String getTitle() {
            return mTitle;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.loadedAdDatas != null) {
            for (RecyclerAdData adData : loadedAdDatas) {
                adData.destroy();
            }
        }
    }

    private static final int TYPE_AD = 1;
    private static final int TYPE_PRE_RENDER = 2;
    private static final int TYPE_DATA = 0;

    class CustomAdapter extends RecyclerView.Adapter<CustomHolder> {

        private List<Object> mData;
        private Context mContext;

        public CustomAdapter(Context context, List list) {
            mContext = context;
            mData = list;
        }

        public void addNormalItem(NormalItem item) {
            mData.add(item);
        }

        public void addAdToPosition(RecyclerAdData recyclerAdData, int position) {
            if (position >= 0 && position < mData.size()) {
                mData.add(position, recyclerAdData);
            }
        }

        @Override
        public int getItemViewType(int position) {
            try {
                Object obj = mData.get(position);
                if (obj instanceof RecyclerAdData) {
                    RecyclerAdData ad = (RecyclerAdData) obj;
                    if (ad.isNativeExpress()) {
                        return TYPE_PRE_RENDER;
                    } else {
                        return TYPE_AD;
                    }
                } else {
                    return TYPE_DATA;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return TYPE_DATA;


        }

        @Override
        public CustomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case TYPE_AD:
                    view = LayoutInflater.from(mContext).inflate(R.layout.admate_item_ad_unified_large_img_or_video,null);
                    break;
                case TYPE_PRE_RENDER:
                    view = LayoutInflater.from(mContext).inflate(R.layout.admate_item_ad_unified_pre_render, null);
                    break;
                case TYPE_DATA:
                    view = LayoutInflater.from(mContext).inflate(R.layout.admate_item_data, null);
                    break;

                default:
                    view = null;
            }
            return new CustomHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(CustomHolder holder, int position) {
            switch (getItemViewType(position)) {
                case TYPE_AD:
                    initItemView(position, holder);
                    break;
                case TYPE_PRE_RENDER:
                    initPreRender(position,holder);
                    break;
                case TYPE_DATA:
                    holder.title.setText(((NormalItem) mData.get(position))
                            .getTitle());
                    break;
            }
        }

        private void initItemView(int position, final CustomHolder holder) {
            final RecyclerAdData ad = (RecyclerAdData) mData.get(position);
            holder.title_top.setText(ad.getTitle());
            holder.desc_top.setText(ad.getDesc());
            holder.closeBtn.setVisibility(VISIBLE);
            holder.closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    holder.container.removeAllViews();
                    v.setVisibility(GONE);
                    ad.destroy();
                    mData.remove(position);
                    notifyItemChanged(position);
                    notifyDataSetChanged();
                }
            });
            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(holder.container);
            ImageView imagePoster = holder.poster;
            // 视频广告
            hideAll(holder);
            if (ad.getAdPatternType() == MsAdPatternType.VIDEO) {
                showVideo(holder);
                showTop(holder);
            } else if (ad.getAdPatternType() == MsAdPatternType.IMAGE
                    || ad.getAdPatternType() == MsAdPatternType.LARGE_IMAGE) {
                showPoster(holder);
                if (ad.getImgUrls().length>0){
                    Glide.with(holder.poster.getContext())
                            .load(ad.getImgUrls()[0])
                            .into(imagePoster);

                }
                List<ImageView> imageViews = new ArrayList<>();
                imageViews.add(holder.poster);
                showTop(holder);

            } else if (ad.getAdPatternType() == MsAdPatternType.SMALL_IMAGE) {
                showTop(holder);
            } else if (ad.getAdPatternType() == MsAdPatternType.THREE_IMAGE) {
                ImageView img1 = holder.img1;
                ImageView img2 = holder.img2;
                ImageView img3 = holder.img3;

                String[] imgs = ad.getImgUrls();
                if (imgs != null && imgs.length == 1) {
                    showPoster(holder);
                    Glide.with(MixRecyclerActivity.this)
                            .load(ad.getImgUrls()[0])
                            .into(imagePoster);

                    showTop(holder);
                } else if (imgs != null && imgs.length > 1) {
                    holder.bottomTextTitle.setText(ad.getTitle());
                    holder.bottomTextDesc.setText(ad.getDesc());
                    if (imgs.length > 0 && !TextUtils.isEmpty(imgs[0])) {
                        holder.img1.setVisibility(View.VISIBLE);
                        Glide.with(MixRecyclerActivity.this)
                                .load(ad.getImgUrls()[0])
                                .into(img1);
                        showBottom(holder);
                    }
                    if (imgs.length > 1 && !TextUtils.isEmpty(imgs[1])) {
                        holder.img2.setVisibility(View.VISIBLE);
                        Glide.with(MixRecyclerActivity.this)
                                .load(ad.getImgUrls()[1])
                                .into(img2);
                    }
                    if (imgs.length > 2 && !TextUtils.isEmpty(imgs[2])) {
                        holder.img3.setVisibility(View.VISIBLE);
                        Glide.with(MixRecyclerActivity.this)
                                .load(ad.getImgUrls()[2])
                                .into(img3);
                    }

                } else {

                }
            }
            if (ad.getIconUrl() != null) {
                Glide.with(MixRecyclerActivity.this)
                        .load(ad.getIconUrl())
                        .into(holder.icon);

            } else {
                holder.icon.setVisibility(GONE);
            }
            ad.bindAdToView(holder.container.getContext(), holder.container,
                    clickableViews, new RecylcerAdInteractionListener() {
                        @Override
                        public void onAdClosed() {

                        }

                        @Override
                        public void onAdRenderFailed() {
                            LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                        }

                        @Override
                        public void onAdExposure() {
                            LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                        }

                        @Override
                        public void onAdClicked() {
                            LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                        }

                    });
//            if (ad instanceof RecyclerAdExtData) {
//                ((RecyclerAdExtData) ad).setOnAdExposureListener(new AdExposureListener() {
//                    @Override
//                    public void onAdExposure() {
//                        Log.d(TAG, "onAdExposure: 广告曝光 2");
//                    }
//                });
//            }

            setAdListener(holder, ad);

        }

        private void setAdListener(final CustomHolder holder, final RecyclerAdData ad) {
            // 视频广告
            if (ad.getAdPatternType() == MsAdPatternType.VIDEO) {
                ad.getVideoUrl();
                int width = ViewGroup.LayoutParams.MATCH_PARENT;
                int height = (int) DimensionUtils.dip2px(MixRecyclerActivity.this,180);

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                ad.bindMediaView(holder.mediaView, new RecyclerAdMediaListener() {

                    @Override
                    public void onVideoLoaded() {
                        Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                    }

                    @Override
                    public void onVideoStart() {
                        LogUtil.e(TAG,"duration="+ad.getDuration());
                        Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                    }

                    @Override
                    public void onVideoPause() {
                        Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                    }

                    @Override
                    public void onVideoCompleted() {
                        Log.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                        holder.replay.setEnabled(true);
                    }

                    @Override
                    public void onVideoError() {
                        Log.e(TAG, "onVideoError: ", new Exception("视频出错"));
                    }

                    @Override
                    public void onVideoResume() {

                    }
                });
                holder.replay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ad.replay();
                        holder.replay.setEnabled(false);
                    }
                });
                holder.mute.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ad.mute();
                    }
                });
                holder.unmute.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ad.unmute();
                    }
                });
            }
        }

        private void initPreRender(int position, CustomHolder holder) {
            final RecyclerAdData ad = (RecyclerAdData) mData.get(position);
            View adView;
//            if (ad.isNativeExpress()) {
//                adView = ad.getAdView();
//                if (adView == null) {
//                    TextView tv = new TextView(MixRecyclerActivity.this);
//                    tv.setText("渲染未完成或失败");
//                    holder.container.addView(tv);
//                    return;
//                }
//            } else {
//                TextView tv = new TextView(MixRecyclerActivity.this);
//                tv.setText("不是预渲染广告，请检查广告配置");
//                adView = tv;
//            }
            holder.container.removeAllViews();
//            holder.container.addView(adView);
            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(holder.container);
            ad.bindAdToView(MixRecyclerActivity.this, holder.container,
                    clickableViews, new RecylcerAdInteractionListener() {
                        @Override
                        public void onAdClosed() {
                            LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                        }

                        @Override
                        public void onAdRenderFailed() {
                            LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                        }

                        @Override
                        public void onAdExposure() {
                            LogUtil.e(TAG,"ecpm="+ad.getData().getEcpm());
                            LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                        }

                        @Override
                        public void onAdClicked() {
                            LogUtil.d(TAG, "DEMO ADEVENT " + (new Throwable().getStackTrace()[0].getMethodName()));
                        }
                    });
        }


        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    private void showVideo(CustomHolder holder) {
        holder.poster.setVisibility(View.INVISIBLE);
        holder.icon.setVisibility(View.GONE);
        holder.img1.setVisibility(View.GONE);
        holder.img2.setVisibility(View.GONE);
        holder.img3.setVisibility(View.GONE);
        holder.mediaView.setVisibility(View.VISIBLE);
        holder.layoutVideo.setVisibility(View.VISIBLE);
    }

    private void showPoster(CustomHolder holder) {
        holder.poster.setVisibility(View.VISIBLE);
        holder.icon.setVisibility(View.GONE);
        holder.img1.setVisibility(View.GONE);
        holder.img2.setVisibility(View.GONE);
        holder.img3.setVisibility(View.GONE);
        holder.mediaView.setVisibility(View.INVISIBLE);
    }

    private void showTop(CustomHolder holder) {
        holder.topAdInfoContainer.setVisibility(VISIBLE);
        holder.icon.setVisibility(VISIBLE);
        holder.title_top.setVisibility(VISIBLE);
        holder.desc_top.setVisibility(VISIBLE);

        holder.bottomAdInfoContainer.setVisibility(GONE);
        holder.bottomTextTitle.setVisibility(GONE);
        holder.bottomTextDesc.setVisibility(GONE);

    }

    private void showBottom(CustomHolder holder) {

        holder.topAdInfoContainer.setVisibility(GONE);
        holder.icon.setVisibility(GONE);
        holder.title_top.setVisibility(GONE);
        holder.desc_top.setVisibility(GONE);

        holder.bottomAdInfoContainer.setVisibility(VISIBLE);
        holder.bottomTextTitle.setVisibility(VISIBLE);
        holder.bottomTextDesc.setVisibility(VISIBLE);
    }

    private void hideAll(CustomHolder holder) {
        holder.topAdInfoContainer.setVisibility(View.GONE);
        holder.bottomAdInfoContainer.setVisibility(View.GONE);
        holder.poster.setVisibility(View.GONE);
        holder.img1.setVisibility(View.GONE);
        holder.img2.setVisibility(View.GONE);
        holder.img3.setVisibility(View.GONE);
        holder.mediaView.setVisibility(View.GONE);
        holder.layoutVideo.setVisibility(View.GONE);
    }


    class CustomHolder extends RecyclerView.ViewHolder {


        public TextView title;
        public ViewGroup mediaView;
        public ViewGroup topAdInfoContainer;
        public ViewGroup bottomAdInfoContainer;
        public TextView title_top;
        public TextView desc_top;
        public ImageView icon;
        public ImageView poster;
        public ImageView img1;
        public ImageView img2;
        public ImageView img3;
        public ViewGroup container;
        public ViewGroup layoutVideo;
        public Button replay;
        public Button mute;
        public Button unmute;
        public ImageView closeBtn;
        public TextView bottomTextTitle;
        public TextView bottomTextDesc;

        public CustomHolder(View itemView, int adType) {
            super(itemView);
            switch (adType) {
                case TYPE_AD:
                    mediaView = itemView.findViewById(R.id.api_media_view);
                    topAdInfoContainer = itemView.findViewById(R.id.ad_info_container_top);
                    bottomAdInfoContainer = itemView.findViewById(R.id.ad_info_container_bottom);
                    icon = itemView.findViewById(R.id.ad_info_container_top_icon);
                    poster = itemView.findViewById(R.id.img_poster);
                    img1 = itemView.findViewById(R.id.img_1);
                    img2 = itemView.findViewById(R.id.img_2);
                    img3 = itemView.findViewById(R.id.img_3);
                    title_top = itemView.findViewById(R.id.ad_info_container_top_text_title);
                    desc_top = itemView.findViewById(R.id.ad_info_container_top_text_desc);
                    container = itemView.findViewById(R.id.native_ad_container);
                    replay = itemView.findViewById(R.id.button_replay);
                    mute = itemView.findViewById(R.id.button_mute);
                    unmute = itemView.findViewById(R.id.button_unmute);
                    layoutVideo = itemView.findViewById(R.id.layout_video);
                    closeBtn = itemView.findViewById(R.id.close_button);

                    bottomTextTitle = itemView.findViewById(R.id.ad_info_container_bottom_text_title);
                    bottomTextDesc = itemView.findViewById(R.id.ad_info_container_bottom_text_desc);
                    break;
                case TYPE_PRE_RENDER:
                    container = itemView.findViewById(R.id.container);
                    break;
                case TYPE_DATA:
                    title = itemView.findViewById(R.id.title);
                    break;

            }
        }
    }

    private static final int ITEM_COUNT = 30;
    private static final int FIRST_AD_POSITION = 5;
    private static final int AD_DISTANCE = 10;

    private class H extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_LIST:

                    int count = mAdapter.getItemCount();
                    for (int i = 0; i < ITEM_COUNT; i++) {
                        mAdapter.addNormalItem(new NormalItem(count + i));
                    }

                    List<RecyclerAdData> ads = (List<RecyclerAdData>) msg.obj;
                    if (ads != null && ads.size() > 0 && mAdapter != null) {
                        for (int i = 0; i < ads.size(); i++) {
                            mAdapter.addAdToPosition(ads.get(i), count + i * AD_DISTANCE + FIRST_AD_POSITION);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    break;

                default:
            }
        }
    }
}