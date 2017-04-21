package com.game01.maple.ag01;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.game01.maple.ag01.utils.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String checkVersionURL="http://211.149.234.199:23335/AndroidVersionManager/GetNewVersion?action=checkNewestVersion";
    long new_version_code=-1;

    private Button button;
    private final int SUCCESS = 1;
    private final int FAILURE = 0;
    private final int ERRORCODE = 2;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    /**
                     * 获取信息成功后，对该信息进行JSON解析，得到所需要的信息，然后在textView上展示出来。
                     */
                    try {
                        JSONObject serverJson=new JSONObject(msg.obj.toString());
                        new_version_code=serverJson.getLong("verCode");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, Common.getVersionCode(MainActivity.this)+"获取数据成功"+new_version_code, Toast.LENGTH_SHORT)
                            .show();
                    break;

                case FAILURE:
                    Toast.makeText(MainActivity.this, "获取数据失败", Toast.LENGTH_SHORT)
                            .show();
                    break;

                case ERRORCODE:
                    Toast.makeText(MainActivity.this, "获取的CODE码不为200！",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        };
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.checkVersion);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkVersion:
                /**
                 * 点击按钮事件，在主线程中开启一个子线程进行网络请求
                 * （因为在4.0只有不支持主线程进行网络请求，所以一般情况下，建议另开启子线程进行网络请求等耗时操作）。
                 */
                new Thread() {
                    public void run() {
                        int code;
                        try {
                            URL url = new URL(checkVersionURL);
                            /**
                             * 这里网络请求使用的是类HttpURLConnection，另外一种可以选择使用类HttpClient。
                             */
                            HttpURLConnection conn = (HttpURLConnection) url
                                    .openConnection();
                            conn.setRequestMethod("GET");//使用GET方法获取
                            conn.setConnectTimeout(5000);
                            code = conn.getResponseCode();
                            if (code == 200) {
                                /**
                                 * 如果获取的code为200，则证明数据获取是正确的。
                                 */
                                InputStream is = conn.getInputStream();
                                String result = HttpUtils.readMyInputStream(is);

                                /**
                                 * 子线程发送消息到主线程，并将获取的结果带到主线程，让主线程来更新UI。
                                 */
                                Message msg = new Message();
                                msg.obj = result;
                                msg.what = SUCCESS;
                                handler.sendMessage(msg);

                            } else {

                                Message msg = new Message();
                                msg.what = ERRORCODE;
                                handler.sendMessage(msg);
                            }
                        } catch (Exception e) {

                            e.printStackTrace();
                            /**
                             * 如果获取失败，或出现异常，那么子线程发送失败的消息（FAILURE）到主线程，主线程显示Toast，来告诉使用者，数据获取是失败。
                             */
                            Message msg = new Message();
                            msg.what = FAILURE;
                            handler.sendMessage(msg);
                        }
                    };
                }.start();
                break;

            default:
                break;
        }
    }
}
