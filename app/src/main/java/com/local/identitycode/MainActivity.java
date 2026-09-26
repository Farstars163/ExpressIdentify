package com.local.identitycode;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String PDD_PAGE_URL =
            "https://mdkd.pinduoduo.com/weixin/package?station_code=A029765297";
    private static final String PDD_DEEP_LINK =
            "pinduoduo://com.xunmeng.pinduoduo/web?url=" + Uri.encode(PDD_PAGE_URL);
    private static final String TAOBAO_URL =
            "https://pages-fast.m.taobao.com/wow/z/uniapp/1100410/last-mile-fe/m-end-identity-code/home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            setTitle(versionName);
        } catch (Exception e) {
            setTitle(R.string.app_name);
        }

        findViewById(R.id.btn_pdd).setOnClickListener(v -> openPinduoduo());
        findViewById(R.id.btn_pdd).setOnLongClickListener(v -> {
            copyLink("拼多多身份码链接", PDD_PAGE_URL);
            return true;
        });

        findViewById(R.id.btn_taobao).setOnClickListener(v -> openTaobao());
        findViewById(R.id.btn_taobao).setOnLongClickListener(v -> {
            copyLink("淘宝链接", TAOBAO_URL);
            return true;
        });
    }

    private void openPinduoduo() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(PDD_DEEP_LINK));
        intent.setPackage("com.xunmeng.pinduoduo");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException | SecurityException error) {
            Toast.makeText(this, "请先安装或更新拼多多", Toast.LENGTH_LONG).show();
        }
    }

    private void openTaobao() {
        if (openUrlInPackage(TAOBAO_URL, "com.taobao.taobao")) {
            return;
        }
        String taobaoScheme = "taobao://m.taobao.com/tbopen/index.html"
                + "?action=ali.open.nav&module=h5&h5Url=" + Uri.encode(TAOBAO_URL);
        if (openUrlInPackage(taobaoScheme, "com.taobao.taobao")) {
            return;
        }
        copyLink("淘宝链接", TAOBAO_URL);
        Toast.makeText(this, "请先安装淘宝；页面链接已复制", Toast.LENGTH_LONG).show();
    }

    private boolean openUrlInPackage(String url, String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        try {
            startActivity(intent);
            return true;
        } catch (ActivityNotFoundException | SecurityException ignored) {
            return false;
        }
    }

    private void copyLink(String label, String value) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText(label, value));
        Toast.makeText(this, label + "已复制", Toast.LENGTH_SHORT).show();
    }
}
