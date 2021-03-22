# Usergeek Android SDK

## Setup

Download via Gradle:

```gradle
implementation 'com.usergeek:usergeek-android:1.0.7'
```

Download via Maven:

```xml
<dependency>
  <groupId>com.usergeek</groupId>
  <artifactId>usergeek-android</artifactId>
  <version>1.0.7</version>
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

Usergeek.getClient()
    .setUserProperty("gender", "male")
    .setUserProperty("supportedLanguages", "en")

Usergeek.getClient().setUserId("123123-341231")

Usergeek.getClient().logEvent("StartConversation",
    EventProperties().set("type", "private"))

Usergeek.getClient()
    .appendUserProperty("supportedLanguages", "ru")

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

Usergeek.INSTANCE.getClient()
    .setUserProperty("gender", "male")
    .setUserProperty("supportedLanguages", "en");

UsergeekClient client = Usergeek.INSTANCE.getClient();
client.setUserId("123123-341231");

client.logEvent("StartConversation", new EventProperties().set("type", "private"));

client.appendUserProperty("supportedLanguages", "ru");

client.logEvent("EndConversation");

client.flush();
```

## License 

`Usergeek-Android` is distributed under the terms and conditions of the [MIT license](https://github.com/usergeek/Usergeek-Android/blob/master/LICENSE).