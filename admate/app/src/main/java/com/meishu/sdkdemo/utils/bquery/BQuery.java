package com.meishu.sdkdemo.utils.bquery;

import android.app.Activity;
import android.content.Context;
import android.view.View;

public class BQuery extends AbstractBQuery<BQuery>{
    public BQuery(Activity act) {
        super(act);
    }

    public BQuery(View root) {
        super(root);
    }

    public BQuery(Activity act, View root) {
        super(act, root);
    }

    public BQuery(Context context) {
        super(context);
    }



}
