package com.exercise.yxty.safeguard.activity;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;

import com.exercise.yxty.safeguard.R;
import com.exercise.yxty.safeguard.utils.MD5Utils;

/**
 * Created by Administrator on 2016/1/18.
 */
public class HomeActivity extends Activity {

    private GridView gvHome ;
    private ImageView ivItem;
    private TextView tvItem;
    private String[] itemName;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEd;

    private int[] itemPicID = new int[]{R.drawable.home_safe,R.drawable.home_callmsgsafe,
            R.drawable.home_apps,R.drawable.home_taskmanager,
            R.drawable.home_netmanager,R.drawable.home_trojan,R.drawable.home_sysoptimize,
            R.drawable.home_tools,R.drawable.home_settings};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        mEd = mPref.edit();
        gvHome = (GridView) findViewById(R.id.gv_home);
        itemName = this.getResources().getStringArray(R.array.itemString);
        gvHome.setAdapter(new MyAdapter());
        gvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        String passWord = mPref.getString("password", null);
                        if (TextUtils.isEmpty(passWord)) {
                            showSettingPasswordDialog();
                        } else {
                            showInputPasswordDialog();
                        }
                        break;
                    case 8:
                        Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    private void showInputPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.password_input_dialog, null);
        dialog.setView(view,0,0,0,0);
        dialog.show();

        final EditText etPass = (EditText) view.findViewById(R.id.et_pass);
        Button btOk = (Button) view.findViewById(R.id.bt_ok);
        Button btCancel = (Button) view.findViewById(R.id.bt_cancel);

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passInput = etPass.getText().toString();
                String passSaved = mPref.getString("password", null);
                if (MD5Utils.md5Encode(passInput).equals(passSaved)) {
                    startActivity(new Intent(HomeActivity.this, LostFoundActivity.class));
                    dialog.dismiss();
                } else {
                    Toast.makeText(HomeActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();

                }
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void showSettingPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.password_setting_dialog, null);
        dialog.setView(view,0,0,0,0);
        dialog.show();

        final EditText etPass = (EditText) view.findViewById(R.id.et_pass);
        final EditText etConfirm = (EditText) view.findViewById(R.id.et_pass_confirm);


        Button btOk = (Button) view.findViewById(R.id.bt_ok);
        Button btCancel = (Button) view.findViewById(R.id.bt_cancel);

        btOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String pass = etPass.getText().toString();
                String passConfirm = etConfirm.getText().toString();

                if (TextUtils.isEmpty(pass) || TextUtils.isEmpty(passConfirm)) {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!pass.equals(passConfirm)) {
                        Toast.makeText(HomeActivity.this, "两次输入不相等", Toast.LENGTH_SHORT).show();
                    } else {
                        mEd.putString("password", MD5Utils.md5Encode(pass));
                        mEd.commit();
                        startActivity(new Intent(HomeActivity.this, LostFoundActivity.class));
                        dialog.dismiss();
                    }
                }

            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return itemName.length;
        }

        @Override
        public Object getItem(int position) {
            return itemName[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(HomeActivity.this, R.layout.grid_item, null);
            ivItem = (ImageView) view.findViewById(R.id.iv_item);
            tvItem = (TextView) view.findViewById(R.id.tv_item);
            ivItem.setImageResource(itemPicID[position]);
            tvItem.setText(itemName[position]);
            return view;
        }
    }
}
