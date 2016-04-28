package com.exercise.yxty.safeguard.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.exercise.yxty.safeguard.R;

/**
 * Created by Administrator on 2016/1/29.
 */
public class SettingDragActivity extends Activity {

    TextView tvHintTop;
    TextView tvHintBottom;
    TextView tvDrag;
    int tvWidth, tvHeight;
    int screenWidth, screenHeight;
    SharedPreferences mPref;

    //记录初始坐标值及变化坐标
    int startX, startY, x, y;
    //记录图片四维
    int tvL, tvT;
    //记录偏移量
    int deltX, deltY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_address_position);
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        initialView();
    }


    private void initialView() {
        tvHintTop = (TextView) findViewById(R.id.tv_hint_top);
        tvHintBottom = (TextView) findViewById(R.id.tv_hint_bottom);

        tvDrag = (TextView) findViewById(R.id.tv_drag);


        //拿到屏幕初始宽高和图片宽高
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        tvWidth = tvDrag.getWidth();
        tvHeight = tvDrag.getHeight();


        int lastLeft = mPref.getInt("AddressHintX", (screenWidth - tvWidth) / 2);
        int lastTop = mPref.getInt("AddressHintY", (screenHeight - tvHeight) / 2);

        //根据图片框位置判断提示框显示与否
        if (lastTop > (screenHeight/2)) {
            tvHintTop.setVisibility(View.VISIBLE);
            tvHintBottom.setVisibility(View.INVISIBLE);
        } else {
            tvHintTop.setVisibility(View.INVISIBLE);
            tvHintBottom.setVisibility(View.VISIBLE);
        }

        /*初始化dragView上次保存的位置
          因为view对象要经历onMeasure()-> onLayout()-> onDraw() 过程才成像
          而在Activity的onCreate() 阶段view在执行onMeasure()过程，此时设置view.layout()
          是无效的
          可通过getLayoutParams()拿到layout参数，修改参数后再setLayoutParams()
          待onLayout（）阶段生效
         *//*初始化dragView上次保存的位置
          因为view对象要经历onMeasure()-> onLayout()-> onDraw() 过程才成像
          而在Activity的onCreate() 阶段view在执行onMeasure()过程，此时设置view.layout()
          是无效的
          可通过getLayoutParams()拿到layout参数，修改参数后再setLayoutParams()
          待onLayout（）阶段生效
         */
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) tvDrag.getLayoutParams();
        layoutParams.leftMargin = lastLeft;
        layoutParams.topMargin = lastTop;
        tvDrag.setLayoutParams(layoutParams);


        tvDrag.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                tvWidth = tvDrag.getWidth();
                tvHeight = tvDrag.getHeight();
                tvL = tvDrag.getLeft();
                tvT = tvDrag.getTop();
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        x = (int) event.getRawX();
                        y = (int) event.getRawY();
                        //判断是否越界，如果越界，不作操作
                        if (x < 0 || x > screenWidth || y < 0 || y > screenHeight) {
                            break;
                        }
                        //根据图片框位置判断提示框显示与否
                        if (y > (screenHeight / 2)) {
                            tvHintTop.setVisibility(View.VISIBLE);
                            tvHintBottom.setVisibility(View.INVISIBLE);
                        } else {
                            tvHintTop.setVisibility(View.INVISIBLE);
                            tvHintBottom.setVisibility(View.VISIBLE);
                        }
                        deltX = x - startX;
                        deltY = y - startY;
                        tvDrag.layout(tvL + deltX, tvT + deltY,
                                tvL + deltX + tvWidth, tvT + deltY + tvHeight);
                        startX = x;
                        startY = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        mPref.edit().putInt("AddressHintX", (tvL + deltX)).commit();
                        mPref.edit().putInt("AddressHintY", (tvT + deltY)).commit();
                        break;
                    default:
                        break;
                }
                //返回值显示此event是否被消费掉，return false说明未被消费，其他动作侦听还能接收这次动作
                return false;
            }
        });
    }


}
