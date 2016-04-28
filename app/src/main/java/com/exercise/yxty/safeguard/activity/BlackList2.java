package com.exercise.yxty.safeguard.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.exercise.yxty.safeguard.R;
import com.exercise.yxty.safeguard.beans.BlackListInfo;
import com.exercise.yxty.safeguard.db.BlackListDAO;

import java.util.List;

/**
 * 分批加载blacklist页面
 */
public class BlackList2 extends AppCompatActivity {

    private List<BlackListInfo> blackListInfos;
    private BlackListDAO blackListDAO = null;
    private BlacklistAdapter adapter = null;

    private ListView lvBlacklist;
    private LinearLayout llLoading;


    private final int limits = 9;
    private int startIndex;
    private int lastPage;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //adapter复用
                    if (adapter == null) {
                        adapter = new BlacklistAdapter(blackListInfos);
                        lvBlacklist.setAdapter(adapter);
                    } else {
                        //如果数据源发生变化则刷新adapter界面
                        adapter.notifyDataSetChanged();
                    }
                    llLoading.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.black_list2);

        blackListDAO = new BlackListDAO(BlackList2.this);
        lastPage = 1;

        initialView();
        initialData();

    }

    //初始化控件
    private void initialView() {
        lvBlacklist = (ListView) findViewById(R.id.lv_black_list);
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);

        lvBlacklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showManageDialog(position);
            }
        });
        //设置滚动监听，当滚至最后一条时添加新条目
        lvBlacklist.setOnScrollListener(new AbsListView.OnScrollListener() {
            /**
             *当滚动状态发生变化时调用
             * @param scrollState 滚动状态
             *                    STATE_IDLE  闲置
             *                    STATE_TOUCH_SCROLL 在划动
             *                    STATE_FLING 飞起
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE:
                        int lastPosition = lvBlacklist.getLastVisiblePosition();
                        //判断滚动位置，以及判断是不是全部添加完毕了
                        // （blackListInfos.size() == blackListDAO.count()）
                        if ((lastPosition == blackListInfos.size() - 1) &&
                                blackListInfos.size() < blackListDAO.count()) {
                            llLoading.setVisibility(View.VISIBLE);
                            initialData();
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }


    //异步加载数据
    private void initialData() {
        llLoading.setVisibility(View.VISIBLE);
        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                //重新计算初始量
                startIndex = limits * (lastPage - 1);
                //数据变化后必须先清空原list再添加新值，不能用直接赋值的方式更新
                //这样adapter会使用原引用，不刷新界面
                if (blackListInfos != null) {
                    blackListInfos.addAll(blackListDAO.findByPage(limits, startIndex));
                } else {
                    blackListInfos = blackListDAO.findByPage(limits, startIndex);
                }
                //每次显示完页面后将页面加1，为后续显示服务
                lastPage ++;
                handler.sendEmptyMessage(1);
            }
        };
        t.start();
    }

    //定义Adapter
    class BlacklistAdapter extends BaseAdapter {

        private List<BlackListInfo> list;

        public BlacklistAdapter(List<BlackListInfo> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }


        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int id = position;
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(BlackList2.this, R.layout.black_list_item, null);
                holder = new ViewHolder();
                TextView tvNumber = (TextView) convertView.findViewById(R.id.tv_number);
                TextView tvMode = (TextView) convertView.findViewById(R.id.tv_mode);
                ImageView ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
                holder.ivD = ivDelete;
                holder.tvN = tvNumber;
                holder.tvM = tvMode;
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tvN.setText(list.get(position).getNumber());
            String mode = list.get(position).getMode();
            if (mode.equals("2")) {
                holder.tvM.setText("短信拦截");
            } else if (mode.equals("1")) {
                holder.tvM.setText("电话拦截");
            } else if (mode.equals("3")) {
                holder.tvM.setText("电话+短信拦截");
            }
            holder.ivD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteDialog(id);
                }
            });

            return convertView;
        }


    }

    static class ViewHolder {
        private TextView tvN;
        private TextView tvM;
        private ImageView ivD;
    }

    //添加黑名单dialog
    public void add(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(this, R.layout.dialog_add_blacklist, null);

        final EditText etAddNumber = (EditText) dialogView.findViewById(R.id.et_add_blacklist);
        final CheckBox cbPhone = (CheckBox) dialogView.findViewById(R.id.cb_phone);
        final CheckBox cbSMS = (CheckBox) dialogView.findViewById(R.id.cb_sms);
        Button btAdd = (Button) dialogView.findViewById(R.id.bt_add);
        Button btCancel = (Button) dialogView.findViewById(R.id.bt_cancel);

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //记录拦截模式 0-不拦截  1-电话 2-短信  3-两者都
                int mode = 0;
                if (cbPhone.isChecked()) {
                    mode += 1;
                }
                if (cbSMS.isChecked()) {
                    mode += 2;
                }
                String number = etAddNumber.getText().toString();
                if (TextUtils.isEmpty(number)) {
                    Toast.makeText(BlackList2.this, "请输入号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mode == 0) {
                    Toast.makeText(BlackList2.this, "请选择拦截项目", Toast.LENGTH_SHORT).show();
                    return;
                }
                BlackListInfo blInfo = new BlackListInfo(number, mode + "");
                //将数据加入List中，放在当前第一条可见位置+1（当前第1位置看不见
                blackListInfos.add(lvBlacklist.getFirstVisiblePosition()+1, blInfo);
                //将数据写入数据库
                blackListDAO.add(number, mode + "");
                //根据list变动 刷新数据显示
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setView(dialogView);
        dialog.show();
    }

    //管理黑名单dialog
    private void showManageDialog(int position) {
        final int id = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(this, R.layout.dialog_add_blacklist, null);

        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
        final EditText etAddNumber = (EditText) dialogView.findViewById(R.id.et_add_blacklist);
        final CheckBox cbPhone = (CheckBox) dialogView.findViewById(R.id.cb_phone);
        final CheckBox cbSMS = (CheckBox) dialogView.findViewById(R.id.cb_sms);
        Button btOk = (Button) dialogView.findViewById(R.id.bt_add);
        Button btCancel = (Button) dialogView.findViewById(R.id.bt_cancel);

        //根据list信息初始化弹框
        tvTitle.setText("管理黑名单");
        etAddNumber.setText(blackListInfos.get(position).getNumber());
        etAddNumber.setEnabled(false);
        int mode = Integer.parseInt(blackListInfos.get(position).getMode());
        if (mode == 1) {
            cbPhone.setChecked(true);
        } else if (mode == 2) {
            cbSMS.setChecked(true);
        } else {
            cbSMS.setChecked(true);
            cbPhone.setChecked(true);
        }

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //记录拦截模式 0-不拦截  1-电话 2-短信  3-两者都
                int mode = 0;
                if (cbPhone.isChecked()) {
                    mode += 1;
                }
                if (cbSMS.isChecked()) {
                    mode += 2;
                }
                if (mode == 0) {
                    Toast.makeText(BlackList2.this, "拦截项目不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //修改list中数据
                blackListInfos.get(id).setMode(mode + "");
                //修改数据库中数据
                blackListDAO.changeMode(blackListInfos.get(id).getNumber(), mode + "");
                //根据list变动 刷新数据显示
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setView(dialogView);
        dialog.show();
    }

    //删除黑名单dialog
    private void showDeleteDialog(int position) {

        final int id = position;
        final AlertDialog.Builder builder = new AlertDialog.Builder(BlackList2.this);
        builder.setTitle("删除黑名单");
        builder.setMessage("是否要删除：" + blackListInfos.get(position).getNumber());
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //从数据库中删除
                blackListDAO.delete(blackListInfos.get(id).getNumber());
                //从list中删除
                blackListInfos.remove(id);
                //刷新界面
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}
