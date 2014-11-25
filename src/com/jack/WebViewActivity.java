package com.jack;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {

	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.layout.ptr_header);
		setContentView(R.layout.activity_webview);
		mWebView = (WebView) findViewById(R.id.webview_1);
		mWebView.setVisibility(WebView.VISIBLE);
		// webView.zoomOut();//1209
		// webView.zoomOut();//1209
		WebSettings ws = mWebView.getSettings();
		// ws.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		ws.setUseWideViewPort(true);
		ws.setLoadWithOverviewMode(false);
		ws.setBuiltInZoomControls(false);
		ws.setSupportZoom(false);
		mWebView.setInitialScale(100);
		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) { // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				view.loadUrl(url);
				return true;
			}
		});
		ws.setJavaScriptEnabled(true);
		// ws.setJavaScriptCanOpenWindowsAutomatically(true);
		// webView.addJavascriptInterface(new ContactsPlugin(),
		// "contactsAction");

		// 设置可以支持缩放
		// ws.setSupportZoom(true);

		// 设置默认缩放方式尺寸是far
		// 设置出现缩放工具

		// 让网页自适应屏幕宽度

		ws.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);

		mWebView.loadUrl("http://www.taobao.com");
	}

}
