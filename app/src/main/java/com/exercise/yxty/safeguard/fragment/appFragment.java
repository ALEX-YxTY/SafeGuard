package com.exercise.yxty.safeguard.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.exercise.yxty.safeguard.R;
import com.exercise.yxty.safeguard.beans.LockAppInfo;
import com.exercise.yxty.safeguard.db.LockAppDAO;

import java.util.List;

/**
 * Created by Administrator on 2016/2/26.
 * 创建抽象类，提取两个Fragment的共同部分，特有部分抽取为abstract方法，让实现fragment继承
 */
public abstract class appFragment extends Fragment {
    DataCommunicationInterface dataCommunication;
    ListView lvLock;
    TextView tvBulletin;

    List<LockAppInfo> appInfos;
    LockAppAdapter appAdapter;
    LockAppDAO lockAppDAO;

    Handler handler = new Handler();
    TranslateAnimation ta;
    public appFragment() {
        // Required empty public constructor
    }

    public abstract List<LockAppInfo> getAppInfos();
    public abstract String setBulletinText(int appCount);
    protected abstract void changeDateBase(LockAppInfo lockAppInfo);
    protected abstract Drawable getDrawable();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DataCommunicationInterface) {
            dataCommunication = (DataCommunicationInterface) context;
        }else {
            //RuntimeException可以不作处理
            throw new RuntimeException(context.toString()
                    + " must implement DataCommunicationInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //initialUI
        View view = inflater.inflate(R.layout.fragment_unlockapp_list, container, false);
        lvLock = (ListView) view.findViewById(R.id.list);
        tvBulletin = (TextView) view.findViewById(R.id.tv_bulletin);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //initialDate
        lockAppDAO = new LockAppDAO(getActivity());
        if (dataCommunication != null) {
            //通过回调确定数据集内容及显示信息
            appInfos = getAppInfos();

            appAdapter = new LockAppAdapter();
            lvLock.setAdapter(appAdapter);

        } else {
            tvBulletin.setText("数据传输错误");
        }

        //创建移除动画对象
        ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
        ta.setDuration(200);
    }

    private class LockAppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            String text = setBulletinText(appInfos.size());
            tvBulletin.setText(text);
            return appInfos.size();
        }


        @Override
        public Object getItem(int position) {
            return appInfos.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            View view;
            if (convertView == null) {
                view = View.inflate(getActivity(), R.layout.fragment_lockapp_list_item, null);
                holder = new ViewHolder();
                holder.ivIcon = (ImageView) view.findViewById(R.id.iv_app_icon);
                holder.tvName = (TextView) view.findViewById(R.id.tv_app_name);
                holder.ivLock = (ImageView) view.findViewById(R.id.iv_lock);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            //setBackground可以设置drawable对象
            holder.ivIcon.setBackground(appInfos.get(position).getIcon());
            holder.tvName.setText(appInfos.get(position).getAppName());
            //通过回调获取drawable对象
            Drawable lockDrawable = getDrawable();
            holder.ivLock.setBackground(lockDrawable);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        //开始动画
                        v.startAnimation(ta);
                        //通过回调确定对数据库的操作
                        changeDateBase(appInfos.get(position));
                        appInfos.remove(position);
                        //延时500毫秒执行，为和动画同步
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                appAdapter.notifyDataSetChanged();
                            }
                        }, 200);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            return view;
        }

    }


    static class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        ImageView ivLock;
    }

    public interface DataCommunicationInterface {
        List<LockAppInfo> getLockAppInfos();
        List<LockAppInfo> getUnlockAppInfos();
    }
}
