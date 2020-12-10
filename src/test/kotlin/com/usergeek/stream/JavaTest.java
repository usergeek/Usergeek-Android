package com.usergeek.stream;

import android.content.Context;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class JavaTest {

    private Context context = ApplicationProvider.getApplicationContext();

    @Test
    public void test() {
        Logger.INSTANCE.setLevel(Log.DEBUG);

        InitConfig initConfig = new InitConfig();
        initConfig.setDevicePropertyConfig(
                new DevicePropertyConfig()
                        .trackPlatform()
                        .trackModel()
                        .trackOsVersion());

        Usergeek.INSTANCE.initialize(context, "APi_KEY", initConfig);

        String property1 = "property1";
        String value1 = "value1";

        Usergeek.INSTANCE.getClient().logEvent("MyEvent", new EventProperties()
                .set(property1, value1)
                .set("", true)
                .set(property1, null));

//        Statistics.INSTANCE.getClient().logUserProperties(new UserProperties().set("age", "old"));

    }

}
