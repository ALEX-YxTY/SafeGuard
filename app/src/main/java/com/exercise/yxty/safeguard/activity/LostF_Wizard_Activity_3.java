package com.exercise.yxty.safeguard.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.exercise.yxty.safeguard.R;

/**
 * Created by Administrator on 2016/1/20.
 */
public class LostF_Wizard_Activity_3 extends BaseFlingActivity {

    Button btNext;
    Button btPreview;
    EditText etNum;
    Button btContactChoose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lost_found_wizard_3);
        btNext = (Button) findViewById(R.id.bt_next);
        btPreview = (Button) findViewById(R.id.bt_preview);
        etNum = (EditText) findViewById(R.id.et_num);
        btContactChoose = (Button) findViewById(R.id.bt_contact_choose);

        btContactChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动系统联系人界面
                Intent intent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 1);

            }
        });

        String saveNum = mSp.getString("saveContact", null);
        if (!TextUtils.isEmpty(saveNum)) {
            etNum.setText(saveNum);
        }





        btPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreviousPage();
            }
        });

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = etNum.getText().toString();
                if (TextUtils.isEmpty(number)) {
                    Toast.makeText(LostF_Wizard_Activity_3.this,"输入号码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                mSp.edit().putString("saveContact", number).commit();
                showNextPage();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        StringBuffer sb = new StringBuffer();
        String result = null;
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri resultUri = data.getData();
            Cursor cursor = getContentResolver().query(resultUri,
                    null, null, null, null);
            cursor.moveToFirst();
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null,
                    null);

            while (phone.moveToNext()) {
                result = phone.getString(phone.
                        getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)) ;
            }
            etNum.setText(result);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showNextPage() {
        startActivity(new Intent(LostF_Wizard_Activity_3.this, LostF_Wizard_Activity_4.class));
        finish();
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(LostF_Wizard_Activity_3.this, LostF_Wizard_Activity_2.class));
        finish();
        overridePendingTransition(R.anim.anim_preview_in, R.anim.anim_preview_out);
    }
}
