package com.xiaopeng.systemui.carconfig;

import com.xiaopeng.speech.vui.utils.VuiUtils;
import com.xiaopeng.systemui.carconfig.config.CarConfig;
import com.xiaopeng.systemui.carconfig.config.D55CarConfig;
import com.xiaopeng.systemui.carconfig.config.E28CarConfig;
import com.xiaopeng.systemui.carconfig.config.E28VCarConfig;
import com.xiaopeng.systemui.carconfig.config.IConfig;
import com.xiaopeng.systemui.carconfig.feature.CarFeature;
import com.xiaopeng.systemui.carconfig.feature.IFeature;
import com.xiaopeng.systemui.carconfig.utils.CarStatusUtils;
import com.xiaopeng.util.FeatureOption;
/* loaded from: classes24.dex */
public class CarModelsManager {
    private static volatile IConfig instanceConfig;
    private static volatile IFeature instanceFeature;

    public static IConfig getConfig() {
        if (instanceConfig == null) {
            synchronized (IConfig.class) {
                if (instanceConfig == null) {
                    instanceConfig = createCarConfig();
                }
            }
        }
        return instanceConfig;
    }

    public static IFeature getFeature() {
        if (instanceFeature == null) {
            synchronized (IFeature.class) {
                if (instanceFeature == null) {
                    instanceFeature = createCarFeature();
                }
            }
        }
        return instanceFeature;
    }

    private static IConfig createCarConfig() {
        char c;
        String xpCduType = CarStatusUtils.getXpCduType();
        int hashCode = xpCduType.hashCode();
        if (hashCode != 2560) {
            if (hashCode == 2562 && xpCduType.equals(VuiUtils.CAR_PLATFORM_Q3)) {
                c = 1;
            }
            c = 65535;
        } else {
            if (xpCduType.equals(VuiUtils.CAR_PLATFORM_Q1)) {
                c = 0;
            }
            c = 65535;
        }
        if (c == 0) {
            return FeatureOption.FO_DEVICE_INTERNATIONAL_ENABLED ? new E28VCarConfig() : new E28CarConfig();
        } else if (c == 1) {
            return new D55CarConfig();
        } else {
            return new CarConfig();
        }
    }

    private static IFeature createCarFeature() {
        CarStatusUtils.getXpCduType().hashCode();
        return new CarFeature();
    }
}
