package com.adgain.amps.adapter;

import com.adgain.sdk.api.IBidding;

import java.util.HashMap;
import java.util.Map;

import xyz.adscope.amps.base.AMPSBidResult;

public class BiddingUtils {
    public static void sendWinNotice(IBidding gtBaseAd, AMPSBidResult ampsBidResult) {
        try {
            if (gtBaseAd != null) {
                Map<String, Object> map = new HashMap<>();
                map.put(IBidding.WIN_PRICE, ampsBidResult.getEcpm());
                map.put(IBidding.THIRD_MEDIATION, "bz");
                gtBaseAd.sendWinNotification(map);
            }
        } catch (Exception e) {
        }
    }

    public static void sendLossNotice(IBidding gtBaseAd, AMPSBidResult ampsBidResult) {
        try {
            if (gtBaseAd != null) {
                Map<String, Object> map = new HashMap<>();
                map.put(IBidding.EXPECT_COST_PRICE, ampsBidResult.getWinPrice());
                map.put(IBidding.HIGHEST_LOSS_PRICE, ampsBidResult.getEcpm());
                map.put(IBidding.THIRD_MEDIATION, "bz");
                gtBaseAd.sendLossNotification(map);
            }
        } catch (Exception e) {
        }
    }


}
