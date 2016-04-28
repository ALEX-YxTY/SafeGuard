package com.exercise.yxty.safeguard.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.exercise.yxty.safeguard.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/1/23.
 */
public class ContactorChoose extends Activity {

    ListView lvContact;
    ArrayList<HashMap<String, String>> contacts;

    TextView tvName;
    TextView tvNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_choose);

        lvContact = (ListView) findViewById(R.id.lv_contact);

        lvContact.setAdapter(new MyAdapater());

        contacts = getContact();


//        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                Intent intent = new Intent();
//                intent.putExtra("number", (contacts.get(position).get("phone")));
////                intent.putExtra("number", 123456);
//                setResult(1, intent);
//                finish();
//            }
//        });
    }

    protected ArrayList<HashMap<String, String>> getContact() {

        ArrayList<HashMap<String, String>> contactTemp = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Uri rawUri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");

        Cursor rawCursor = cr.query(rawUri, new String[]{"contact_id"}, null, null, null);
        if (rawCursor != null) {
            while (rawCursor.moveToNext()) {
                String rawId = rawCursor.getString(rawCursor.getColumnIndex("contact_id"));
                if (rawId != null) {

                    Cursor dataCursor = cr.query(dataUri, new String[]{"mimetype", "data1"},
                            "contact_id = ?", new String[]{rawId}, null);
                    if (dataCursor != null) {
                        HashMap<String ,String > map = new HashMap<>();
                        while (dataCursor.moveToNext()) {
                            String mimetype = dataCursor.getString(dataCursor.
                                    getColumnIndex("mimetype"));
                            String data1 = dataCursor.getString(dataCursor.
                                    getColumnIndex("data1"));
                            if (mimetype.equals("vnd.android.cursor.item/name")) {
                                map.put("name", data1);
                            } else if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
                                map.put("number", data1);
                            }
                        }
                        contactTemp.add(map);
                    }
                    dataCursor.close();
                }
            }
        }

        rawCursor.close();

        return contactTemp;
    }

    class MyAdapater extends BaseAdapter {


        @Override
        public int getCount() {
            return 10;
        }


        @Override
        public Object getItem(int position) {
            return contacts.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            Object[] ob;
//
//            if (convertView == null) {
//                convertView = View.inflate(ContactorChoose.this,
//                        R.layout.contact_listview_item, null);
//                tvName = (TextView) view.findViewById(R.id.tv_contact);
//                tvNumber = (TextView) view.findViewById(R.id.tv_number);
//                ob = new Object[]{tvName, tvNumber};
//                convertView.setTag(ob);
//            }else {
//                ob = (Object[])convertView.getTag();
//            }
//
//            tvName = (TextView) ob[0];
//            tvNumber = (TextView) ob[1];
//
//            tvName.setText(contacts.get(position).get("name"));
//            tvNumber.setText(contacts.get(position).get("number"));
//
//            return converView;
            View view;
            view = View.inflate(ContactorChoose.this, R.layout.contact_listview_item, null);
            TextView tvName = (TextView) view.findViewById(R.id.tv_contact);
            TextView tvNum = (TextView) view.findViewById(R.id.tv_number);
            tvName.setText(contacts.get(position).get("name"));

            tvNum.setText(contacts.get(position).get("number"));

            return view;

        }
    }

}
