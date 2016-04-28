package com.exercise.yxty.safeguard.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.exercise.yxty.safeguard.R;
import com.exercise.yxty.safeguard.db.AddressDAO;

/**
 * Created by Administrator on 2016/1/27.
 */
public class AddressQueryActivity extends Activity {

    EditText edAddress;
    Button btQuery;
    TextView tvAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_query);

        initialView();

        //给输入框增加改动监听，
        edAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String area = AddressDAO.sqlAddress(s.toString());
                tvAddress.setText(area);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    //初始化view对象，设置监听等
    private void initialView() {
        edAddress = (EditText) findViewById(R.id.et_input_address);
        btQuery = (Button) findViewById(R.id.bt_query_address);
        tvAddress = (TextView) findViewById(R.id.tv_show_address);

        btQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = edAddress.getText().toString();
                if (TextUtils.isEmpty(number)) {
                    //从资源文件加载animation的xml文件
                    Animation animation = AnimationUtils.loadAnimation(AddressQueryActivity.this,
                            R.anim.edittex_shake);
                    vibrate();
                    edAddress.startAnimation(animation);

                } else {
                    String address = AddressDAO.sqlAddress(number);
                    tvAddress.setText(address);
                }
            }
        });
    }

    //震动，需要permission VIBRATE
    private void vibrate() {
        //取得震动对象
        Vibrator vb = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //设置时间，也可设置long[] 完成节奏震动及 repeat
        vb.vibrate(500);
    }
}
