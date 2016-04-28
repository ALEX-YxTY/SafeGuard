package com.exercise.yxty.safeguard.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.exercise.yxty.safeguard.R;
import com.exercise.yxty.safeguard.beans.ContactInfo;
import com.exercise.yxty.safeguard.db.ShortcutDAO;

import java.util.ArrayList;
import java.util.List;

public class ShortcutManagerActivity extends AppCompatActivity {

    List<ContactInfo> contactInfos;
    //待删除shortcut列表
    List<String[]> listDelete;
    ShortcutDAO shortcutDAO;
    ListView lvShortcut;
    ShortcutAdapter adapter;
    TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortcut_manager);

        shortcutDAO = new ShortcutDAO(this);
        listDelete = new ArrayList<>();

        initialUI();
        initialData();
    }

    private void initialData() {
        new Thread() {
            @Override
            public void run() {
                contactInfos = shortcutDAO.findAll();
                ShortcutManagerActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new ShortcutAdapter();
                        lvShortcut.setAdapter(adapter);
                    }
                });
            }
        }.start();
    }

    private void initialUI() {
        lvShortcut = (ListView) findViewById(R.id.lv_shortcut);
        tvEmpty = (TextView) findViewById(R.id.tv_list_empty);

        if (shortcutDAO.count() != 0) {
            tvEmpty.setVisibility(View.INVISIBLE);
        }

    }

    public void add(View view) {
        //启动系统联系人页面
        Intent intent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    public void delete(View view) {
        for (String[] list : listDelete) {
            Log.i("test", list[0] + ":" + list[1]);
            ContactInfo infoDelete = new ContactInfo();
            infoDelete.setContactName(list[0]);
            infoDelete.setContactNumer(list[1]);

            //显示列表中去除
            contactInfos.remove(infoDelete);
            //数据库中去除
            shortcutDAO.delete(list[1]);

            uninstallShortcut(list);
            }
        //清空删除列表
        listDelete.clear();
        //重新显示
        adapter.notifyDataSetChanged();
        if (contactInfos.size() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        String phoneNumber = "";
        //判断requestCode 和 resultCode
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //从结果中取出URI
            Uri resultUri = data.getData();
            Cursor cursor = getContentResolver().query(resultUri,
                    null, null, null, null);
            cursor.moveToFirst();
            //获取联系人姓名、联系人ID
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            //根据联系人ID，查询联系人号码集合
            Cursor phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null,
                    null);
            //
            while (phone.moveToNext()) {
                phoneNumber = phone.getString(phone.
                        getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            ContactInfo contactInfo = new ContactInfo();
            contactInfo.setContactName(contactName);
            contactInfo.setContactNumer(phoneNumber);
            //分别加入显示列表、数据库，并更新显示
            contactInfos.add(contactInfo);
            shortcutDAO.add(contactName, phoneNumber);
            installShortcut(new String[]{contactName, phoneNumber});
            adapter.notifyDataSetChanged();
            if (contactInfos.size() == 1) {
                tvEmpty.setVisibility(View.INVISIBLE);
            }
        }
    }

    private class ShortcutAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return contactInfos.size();
        }


        @Override
        public Object getItem(int position) {
            return contactInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ShortcutViewHolder holder;
            final int id = position;
            if (convertView == null) {
                holder = new ShortcutViewHolder();
                convertView = View.inflate(ShortcutManagerActivity.this,
                        R.layout.shortcut_list_item, null);
                holder.tvName = (TextView) convertView.findViewById(R.id.tv_shortcut_name);
                holder.tvNumber = (TextView) convertView.findViewById(R.id.tv_number);
                holder.cbDelete = (CheckBox) convertView.findViewById(R.id.cb_delete);
                convertView.setTag(holder);
            } else {
                holder = (ShortcutViewHolder) convertView.getTag();
            }
            final String name = contactInfos.get(id).getContactName();
            final String number = contactInfos.get(id).getContactNumer();
            //创建一个待删除项，包括姓名，号码，contactInfo中的位置（用于删除）
            final String[] list = new String[]{name, number};

            holder.tvName.setText(name);
            holder.tvNumber.setText(number);
            holder.cbDelete.setChecked(false);
            holder.cbDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (holder.cbDelete.isChecked()) {
                        listDelete.add(list);
                    } else {
                        if (!listDelete.remove(list)) {
                            Log.i("test", "删除失败");
                        }

                    }
                }
            });

            return convertView;
        }
    }

    static class ShortcutViewHolder {
        TextView tvName;
        TextView tvNumber;
        CheckBox cbDelete;
    }

    /**
     * 添加桌面快捷方式
     *
     * @param list list[0] = name, list[1] = phoneNumber
     */
    private void installShortcut(String[] list) {
        //建立快捷方式意图
        Intent intent = new Intent();
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        //设定快捷方式的指向内容
        Intent shortcutIntent = new Intent();
        shortcutIntent.setAction(Intent.ACTION_CALL);
        shortcutIntent.addCategory(Intent.CATEGORY_DEFAULT);
        shortcutIntent.setData(Uri.parse("tel://" + list[1]));
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        //设定快捷方式名称
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, list[0]);
        //设定快捷方式图标
        //drawable不是Parcelable，所以用Bitmap类型
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON,
                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        //设置不可重复添加
        intent.putExtra("duplicate", false);
        sendBroadcast(intent);
    }

    /**
     * 删除桌面快捷方式
     * 方法与添加类似，action变为UNINSTALL_SHORTCUT
     * 不需要设置icon和duplicate属性
     *
     * @param list list[0] = name, list[1] = phoneNumber
     */
    private void uninstallShortcut(String[] list) {
        //建立快捷方式意图
        Intent intent = new Intent();
        intent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
        //设定快捷方式的指向内容
        Intent shortcutIntent = new Intent();
        shortcutIntent.setAction(Intent.ACTION_CALL);
        shortcutIntent.addCategory(Intent.CATEGORY_DEFAULT);
        shortcutIntent.setData(Uri.parse("tel://" + list[1]));
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        //设定快捷方式名称
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, list[0]);
        //设置不可重复添加
        sendBroadcast(intent);
    }
}
