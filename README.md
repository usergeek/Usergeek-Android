# Usergeek Android SDK

## Setup

Download via Gradle:

```gradle
implementation 'com.usergeek:usergeek-android:1.0.1'
```

Download via Maven:

```xml
<dependency>
  <groupId>com.usergeek</groupId>
  <artifactId>usergeek-android</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
</dependency>
```

## Usage

#### Kotlin

```kotlin
Usergeek.initialize(context, "<API_KEY>", 
    InitConfig()
        .enableStartAppEvent()
        .enableSessionTracking(app)
        .enableFlushOnClose(app)
        .setDevicePropertyConfig(
            DevicePropertyConfig()
                .trackPlatform()
                .trackManufacturer()
                .trackBrand()
                .trackModel()
                .trackOsVersion()
                .trackCountry()
                .trackAppVersion()
                .trackCarrier()
                .trackLanguage()
        )
)

Usergeek.getClient().logUserProperties(
    UserProperties().set("gender", "male"))

Usergeek.getClient().setUserId("123123-341231")

Usergeek.getClient().logEvent("StartConversation",
    EventProperties().set("type", "private"))

Usergeek.getClient().logEvent("EndConversation")
Usergeek.getClient().flush()
```

#### Java

```java
Usergeek.INSTANCE.initialize(context, "<API_KEY>",
        new InitConfig()
                .enableStartAppEvent()
                .enableSessionTracking(app)
                .enableFlushOnClose(app)
                .setDevicePropertyConfig(
                        new DevicePropertyConfig()
                                .trackPlatform()
                                .trackManufacturer()
                                .trackBrand()
                                .trackModel()
                                .trackOsVersion()
                                .trackCountry()
                                .trackAppVersion()
                                .trackCarrier()
                                .trackLanguage()
                )
);

Usergeek.INSTANCE.getClient().logUserProperties(new UserProperties().set("gender", "male"));

UsergeekClient client = Usergeek.INSTANCE.getClient();
client.setUserId("123123-341231");

client.logEvent("StartConversation", new EventProperties().set("type", "private"));

client.logEvent("EndConversation");

client.flush();
```

## License 

`Usergeek-Android` is distributed under the terms and conditions of the [MIT license](https://github.com/ruby-light/RLT-Android/blob/master/LICENSE).