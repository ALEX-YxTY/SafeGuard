package com.exercise.yxty.safeguard.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.exercise.yxty.safeguard.R;

/**
 * Created by Administrator on 2016/1/20.
 */
public class SettingItemView extends RelativeLayout {


    String NameSpace = "http://schemas.android.com/apk/res-auto";

    TextView tvTitle;
    TextView tvDesc;
    CheckBox cbUpdate;
    String mTitle = null;
    String mDescOn = null;
    String mDescOff = null;
    boolean mChecked;


    public SettingItemView(Context context) {
        super(context);
        initial();
    }



    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        String[] choice = new String[]{"VISIBLE", "INVISIBLE"};
        mTitle = attrs.getAttributeValue(NameSpace, "setTitle");
        mDescOn = attrs.getAttributeValue(NameSpace, "setDescOn");
        mDescOff = attrs.getAttributeValue(NameSpace, "setDescOff");
        mChecked = attrs.getAttributeBooleanValue(NameSpace, "checked", false);
//        使用TypedArray的写法，和getAttributeValue比可以动态跟踪资源
//        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.SettingItemView);
//        mTitle = ta.getString(R.styleable.SettingItemView_setTitle);
//        mDescOn = ta.getString(R.styleable.SettingItemView_setDescOn);
//        mDescOff = ta.getString(R.styleable.SettingItemView_setDescOff);
//        mChecked = ta.getBoolean(R.styleable.SettingItemView_checked, false);
//        mDescVisibility = ta.getBoolean(R.styleable.SettingItemView_setDescVisibility, false);

        initial();

    }


    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initial();
    }


    private void initial() {
        View.inflate(getContext(), R.layout.setting_item, this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDesc = (TextView) findViewById(R.id.tv_desc);
        cbUpdate = (CheckBox) findViewById(R.id.cb_update);


        if (mTitle != null) {
            setTitle(mTitle);
        }

        checked(mChecked);

        if (isChecked()) {
            setDesc(mDescOn);
        } else {
            setDesc(mDescOff);
        }


    }

    public void setTitle (String text) {
        tvTitle.setText(text);
    }

    public void setDesc (String text) {
        tvDesc.setText(text);
    }

    public void checked (boolean check) {
        cbUpdate.setChecked(check);
        if (check) {
            setDesc(mDescOn);
        } else {
            setDesc(mDescOff);
        }
    }

    public boolean isChecked() {
        return cbUpdate.isChecked();
    }


}
