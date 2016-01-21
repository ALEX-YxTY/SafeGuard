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

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public SettingItemView(Context context) {
        super(context);
        initial();
    }


    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     * <p/>
     * <p/>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTitle = attrs.getAttributeValue(NameSpace, "setTitle");
        mDescOn = attrs.getAttributeValue(NameSpace, "setDescOn");
        mDescOff = attrs.getAttributeValue(NameSpace, "setDescOff");
        mChecked = attrs.getAttributeBooleanValue(NameSpace,"checked",false);

//        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.SettingItemView);
//        mTitle = ta.getString(R.styleable.SettingItemView_setTitle);
//        mDescOn = ta.getString(R.styleable.SettingItemView_setDescOn);
//        mDescOff = ta.getString(R.styleable.SettingItemView_setDescOff);
//        mChecked = ta.getBoolean(R.styleable.SettingItemView_checked, false);

        initial();

    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a
     * theme attribute. This constructor of View allows subclasses to use their
     * own base style when they are inflating. For example, a Button class's
     * constructor would call this version of the super class constructor and
     * supply <code>R.attr.buttonStyle</code> for <var>defStyleAttr</var>; this
     * allows the theme's button style to modify all of the base view attributes
     * (in particular its background) as well as the Button class's attributes.
     *
     * @param context      The Context the view is running in, through which it can
     *                     access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the view.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     */
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

        if (mDescOn != null) {
            setDesc(mDescOn);
        }
        if (mDescOff != null) {
            setDesc(mDescOff);
        }

        checked(mChecked);

    }

    public void setTitle (String text) {
        tvTitle.setText(text);
    }

    public void setDesc (String text) {
        tvDesc.setText(text);
    }

    public void checked (boolean check) {
        cbUpdate.setChecked(check);
    }

    public boolean isChecked() {
        return cbUpdate.isChecked();
    }

//    public void click(String descOn, String descOff) {
//        if (isChecked()) {
//            checked(false);
//            setDesc(descOff);
//        } else {
//            checked(true);
//            setDesc(descOn);
//        }
//    }

}
