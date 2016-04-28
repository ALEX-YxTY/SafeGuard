package com.exercise.yxty.safeguard.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.exercise.yxty.safeguard.R;

/**
 * Created by Administrator on 2016/1/20.
 */
public class SettingChoiceView extends RelativeLayout {


    String NameSpace = "http://schemas.android.com/apk/res-auto";

    TextView tvTitle;
    TextView tvDesc;
    String mText = null;



    public SettingChoiceView(Context context) {
        super(context);
        initial();
    }



    public SettingChoiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initial();

    }


    public SettingChoiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initial();
    }


    private void initial() {
        View.inflate(getContext(), R.layout.setting_choice_item, this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
    }

    public void setTitle (String text) {
        tvTitle.setText(text);
    }

    public void setSubTitle (String text) {
        tvDesc.setText(text);
    }



}
