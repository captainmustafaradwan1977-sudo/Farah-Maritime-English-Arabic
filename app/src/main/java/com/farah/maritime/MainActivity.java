package com.farah.maritime;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.speech.tts.TextToSpeech;
import android.webkit.JavascriptInterface;
import android.graphics.Color;
import android.view.Window;
import java.util.Locale;

public class MainActivity extends Activity {
    private TextToSpeech tts;
    private WebView web;
    private boolean ttsReady = false;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        Window window = getWindow();
        window.setStatusBarColor(Color.rgb(6, 43, 58));
        window.setNavigationBarColor(Color.rgb(6, 43, 58));
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.US);
                ttsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED;
            }
        });
        web = new WebView(this);
        web.setBackgroundColor(Color.rgb(243, 247, 248));
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(false);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        web.addJavascriptInterface(new VoiceBridge(), "FarahVoice");
        web.setWebViewClient(new WebViewClient());
        setContentView(web);
        if (state == null) web.loadUrl("file:///android_asset/index.html");
        else web.restoreState(state);
    }

    private class VoiceBridge {
        @JavascriptInterface public void speak(String word) {
            runOnUiThread(() -> {
                if (tts != null && ttsReady) {
                    tts.setLanguage(Locale.US);
                    tts.speak(word, TextToSpeech.QUEUE_FLUSH, null, "farah-word");
                }
            });
        }
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        if (web != null) web.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }

    @Override protected void onDestroy() {
        if (tts != null) { tts.stop(); tts.shutdown(); }
        if (web != null) web.destroy();
        super.onDestroy();
    }
}
