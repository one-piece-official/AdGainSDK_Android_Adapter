package com.xm.demo.unit.ads;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xm.demo.R;
import com.yd.saas.base.interfaces.AdViewNativeListener;
import com.yd.saas.common.pojo.YdNativePojo;
import com.yd.saas.config.exception.YdError;
import com.yd.saas.ydsdk.YdNative;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FlowRecyclerViewActivity extends AppCompatActivity {

    YdNative ydNative;
    RecyclerView mRecyclerView;
    Button mBtnAdd;
    MyAdapter mAdapter;
    List<NormalItem> mDataList;
    int adCount = 0; //记录当前广告的数量
    private String key = "068b839dcdc084d5";

    public static void launch(Context context, String trim) {
        Intent intent = new Intent(context, FlowRecyclerViewActivity.class);
        intent.putExtra("media_id", trim);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mediatom_activity_flow_recycler);
        String mediaId = getIntent().getStringExtra("media_id");
        if (!TextUtils.isEmpty(mediaId)) {
            this.key = mediaId;
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mBtnAdd = (Button) findViewById(R.id.btn_add);
        initDataList();
        mAdapter = new MyAdapter(mDataList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int startPos = mDataList.size(); //添加数据后，也添加广告，设置广告位置
                int dataCount = mDataList.size() - adCount; //有效数据的数量
                for (int i = 0; i < 10; i++) {
                    mDataList.add(new NormalItem("No." + (i + dataCount) + " Normal Data"));
                }
                mAdapter.notifyDataSetChanged();
                requestFlowAD(startPos);
            }
        });
        requestFlowAD(0);
    }

    private void initDataList() {
        mDataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mDataList.add(new NormalItem("No." + i + " Normal Data"));
        }
    }

    /**
    * 请求广告
    */
    private void requestFlowAD(final int position) {
        ydNative = new YdNative.Builder(this)
                .setKey(key)
                .setAdCount(1)
                .setNativeListener(new AdViewNativeListener() {
                    @Override
                    public void onAdDisplay(List<YdNativePojo> ydNativeList) {
                        if (ydNativeList != null && ydNativeList.size() > 0) {
                            ++ adCount;
                            YdNativePojo ydNativePojo = ydNativeList.get(0);
                            if(mAdapter != null) {
                                mAdapter.addADViewToPosition(position, ydNativePojo);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onAdClick(int i) {

                    }

                    @Override
                    public void onAdShow(int i) {

                    }

                    @Override
                    public void onAdFailed(YdError error) {

                    }
                }).build();
        ydNative.requestNative();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ydNative != null) {
            ydNative.destroy();
        }
    }

    public class NormalItem {
        private String title;

        public NormalItem(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public class MyAdapter extends RecyclerView.Adapter {
        static final int TYPE_DATA = 0;
        static final int TYPE_AD = 1;
        private List<Object> mData;

        public MyAdapter(List list) {
            mData = list;
        }

        // 把返回的广告view添加到数据集里面去
        public void addADViewToPosition(int position, YdNativePojo pojo) {
            if (position >= 0 && position < mData.size() && pojo != null) {
                mData.add(position, pojo);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == TYPE_AD) {
                view = View.inflate(parent.getContext(), R.layout.mediatom_item_list_ad, null);
                return new ADViewHolder(view);
            } else {
                view = View.inflate(parent.getContext(), R.layout.mediatom_item_list_data, null);
                return new CustomViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ADViewHolder) {
                ADViewHolder viewHolder = (ADViewHolder) holder;
                YdNativePojo pojo = (YdNativePojo) mData.get(position);

                //以下5行代码是绑定数据与View
                pojo.bindViewGroup(viewHolder.rlContainer);
                List<View> clickViews = new ArrayList<>();
                clickViews.add(viewHolder.btnOperate);
                pojo.bindClickViews(clickViews);
                //切记！执行render()方法前必须先执行bindViewGroup()和bindClickViews()方法
                pojo.render();

                // 赋值
                viewHolder.tvTitle.setText(pojo.getTitle());
                viewHolder.tvDesc.setText(pojo.getDesc());
                Glide.with(getApplicationContext()).load(pojo.getIconUrl()).into(viewHolder.ivLogo);
                Glide.with(getApplicationContext()).load(pojo.getImgUrl()).into(viewHolder.pic);;
                viewHolder.btnOperate.setText(pojo.getBtnText());
            } else {
                CustomViewHolder viewHolder = (CustomViewHolder) holder;
                viewHolder.title.setText(((NormalItem) mData.get(position)).getTitle());
            }
        }

        @Override
        public int getItemViewType(int position) {
            return mData.get(position) instanceof YdNativePojo ? TYPE_AD : TYPE_DATA;
        }

        @Override
        public int getItemCount() {
            return mDataList == null ? 0 : mDataList.size();
        }

        class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView title;

            public CustomViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.title);
            }
        }

        class ADViewHolder extends RecyclerView.ViewHolder {
            public LinearLayout rlContainer;
            public ImageView ivLogo;
            public TextView tvTitle;
            public TextView tvDesc;
            public ImageView pic;
            public TextView btnOperate;

            public ADViewHolder(View itemView) {
                super(itemView);
                rlContainer = itemView.findViewById(R.id.rl_container);
                ivLogo = (ImageView) itemView.findViewById(R.id.img_logo);
                tvTitle = (TextView) itemView.findViewById(R.id.text_name);
                tvDesc = (TextView) itemView.findViewById(R.id.text_desc);
                pic = (ImageView) itemView.findViewById(R.id.img_poster);
                btnOperate = (TextView) itemView.findViewById(R.id.btn_text);
            }
        }
    }

}
