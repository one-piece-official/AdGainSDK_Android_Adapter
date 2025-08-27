package cn.jiguang.jgssp.adapter.adgain;

import com.adgain.sdk.api.IBidding;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BidPriceUtil {
    public static void sendWin(IBidding gtBaseAd, ArrayList<Double> hbPriceList) {
        // 媒体回传⼆价ecpm，竞价成功后，必须在展示前回传
        Map<String, Object> winMap = new HashMap<>();
        if (hbPriceList != null) {
            if (hbPriceList.size() > 1) {
                if (gtBaseAd != null) {
                    Double hPrice = hbPriceList.get(0);
                    Double secondPrice = hbPriceList.get(1);
                    //单位：元 转换为 单位：分
                    BigDecimal bigDecimal = BigDecimal.valueOf(secondPrice);
                    BigDecimal secBidPrice = bigDecimal.multiply(new BigDecimal(100));
                    if (secBidPrice != null) {
                        winMap.put(IBidding.EXPECT_COST_PRICE, hPrice * 100);
                        winMap.put(IBidding.HIGHEST_LOSS_PRICE, secBidPrice.intValue());
                    } else {
                        //兜底策略，一般不会执行，防止异常情况导致后续逻辑无法顺利执行
                        winMap.put(IBidding.EXPECT_COST_PRICE, hPrice);
                        winMap.put(IBidding.HIGHEST_LOSS_PRICE, 0);
                    }
                }
            } else if (hbPriceList.size() == 1) {
                winMap.put(IBidding.EXPECT_COST_PRICE, hbPriceList.get(0) * 100);
                winMap.put(IBidding.HIGHEST_LOSS_PRICE, 0);
            }
        } else {//单位：分
            winMap.put(IBidding.EXPECT_COST_PRICE, gtBaseAd);
            winMap.put(IBidding.HIGHEST_LOSS_PRICE, 0);
        }

        winMap.put(IBidding.THIRD_MEDIATION, "jiguang");
        if (gtBaseAd != null) {
            gtBaseAd.sendWinNotification(winMap);
        }
    }

    public static void sendLoss(IBidding gtBaseAd, int bidCode, ArrayList<Double> hbPriceList) {
        int highestPrice = 0;
        if (hbPriceList != null && hbPriceList.size() > 0) {
            Double price = hbPriceList.get(0);
            //单位：元 转换为 单位：分
            BigDecimal bigDecimal = BigDecimal.valueOf(price);
            BigDecimal multiplyPrice = bigDecimal.multiply(new BigDecimal(100));
            highestPrice = multiplyPrice.intValue();
        }
        Map<String, Object> map = new HashMap<>();
        map.put(IBidding.WIN_PRICE, highestPrice);
        map.put(IBidding.THIRD_MEDIATION, "jiguang");
        if (gtBaseAd != null) {
            gtBaseAd.sendLossNotification(map);
        }
    }
}
