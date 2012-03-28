The AdDash Android SDK is designed to be an easy-to-use way to get analytics out of your apps.

The quickest way in:

1) Take all the dependencies and put them in your project's lib/ directory:

- [commons-codec-1.6.jar](https://github.com/btrzupek/adDash-mobile-android/blob/master/addash-android/lib/commons-codec-1.6.jar)
- [jackson-code-asl-1.9.2.jar](https://github.com/btrzupek/adDash-mobile-android/blob/master/addash-android/lib/jackson-core-asl-1.9.2.jar)
- [jackson-mapper-asl-1.9.2.jar](https://github.com/btrzupek/adDash-mobile-android/blob/master/addash-android/lib/jackson-mapper-asl-1.9.2.jar)
- [addash-android.jar](https://github.com/btrzupek/adDash-mobile-android/blob/master/addash-android-0.1.jar)

2) Go to your project properties, to "Java Build Path", and add those four JAR files to the "Libraries" tab using the "Add JARs..." button

3) In the onCreate() method of your app's main Activity:

```
private AdDash mAdDash;

...

mAdDash = AdDash.getInstance(getApplicationContext());
mAdDash.newSession();
mAdDash.setAdvertiserIdentifier(<advertiser id>, <app private key>);
```

That will get you first-run events and app upgrade events, which will show up in your Events dashboard.

**Note:** In any other Activities, you don't need to call newSession() or setAdvertiserIdentifier(). All you need is ```AdDash.getInstance(getApplicationContext());``` and as long as you've set your advertiser identifier previously, you're good.

4) If you want your own custom events, put in one of these wherever you want to dispatch one:

```
mAdDash.reportCustomEvent(<event name>, <event detail>);
```

5) Enjoy all your analytics!
