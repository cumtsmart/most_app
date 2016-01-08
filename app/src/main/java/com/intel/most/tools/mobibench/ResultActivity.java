package com.intel.most.tools.mobibench;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.intel.most.tools.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import esos.MobiBench.MobiBenchExe;

public class ResultActivity extends ListActivity {

    private ListAdapter mAdapter;
    public static List<DataItem> mData = new ArrayList<DataItem>();

    private DatabaseHelper mDBHelper;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");

    public static String ACTION_DATABASE_CHANGE = "action.database.change";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDBHelper = new DatabaseHelper(this);

        String caller = getIntent().getStringExtra("from");
        if ("mobiexe".equals(caller)) {
            Log.e("yangjun", "from mobiexe");
            SQLiteDatabase dbW = mDBHelper.getWritableDatabase();
            SQLiteDatabase dbR = mDBHelper.getWritableDatabase();
            int maxID = 0;
            Cursor c = dbR.rawQuery("select max(_id) from history;", null);
            if (c.moveToFirst()) {
                maxID = c.getInt(0);
                Log.e("yangjun", "current historyNum:" + maxID);
                maxID++;
            }
            c.close();
            for (DataItem item : mData) {
                ContentValues values = new ContentValues();
                String date = dateFormat.format(calendar.getTime());
                values.put(DatabaseHelper.HistoryColumns._ID, maxID); // 0
                values.put(DatabaseHelper.HistoryColumns.COLUMN_DATE, date); // 1

                values.put(DatabaseHelper.HistoryColumns.COLUMN_TYPE, item.result_type); // 2

                values.put(DatabaseHelper.HistoryColumns.COLUMN_ACTION, item.cpu_act);  // 3
                values.put(DatabaseHelper.HistoryColumns.COLUMN_IO, item.cpu_iow); // 4
                values.put(DatabaseHelper.HistoryColumns.COLUMN_IDL, item.cpu_idl); // 5
                values.put(DatabaseHelper.HistoryColumns.COLUMN_CT_TOTAL, item.cs_tot); // 6
                values.put(DatabaseHelper.HistoryColumns.COLUMN_CT_VOL, item.cs_vol); // 7
                switch (item.exp_id) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        values.put(DatabaseHelper.HistoryColumns.COLUMN_THRP, item.throughput); // 8
                        break;
                    case 4:
                    case 5:
                    case 6:
                        values.put(DatabaseHelper.HistoryColumns.COLUMN_THRP, item.db_tps); // 8
                        break;
                }
                values.put(DatabaseHelper.HistoryColumns.COLUMN_EXP_NAME, MobiBenchExe.ExpName[item.exp_id]); // 9
                values.put(DatabaseHelper.HistoryColumns.COLUMN_EXP_ID, item.exp_id); // 10
                long id = dbW.insert(DatabaseHelper.HistoryColumns.TABLE_NAME, null, values);
                Log.e("yangjun", "insert table item:" + id);
            }
            dbW.close();
            dbR.close();
            Intent intent = new Intent(ACTION_DATABASE_CHANGE);
            sendBroadcast(intent);
        } else if ("history".equals(caller)){
            Log.e("yangjun", "from history");
            mData.clear();
            String id = getIntent().getStringExtra("db_index");
            Log.e("yangjun", "intent db _id:" + id);
            SQLiteDatabase dbR = mDBHelper.getWritableDatabase();
            Cursor c = dbR.rawQuery("select * from history where _id=?;", new String[]{id});
            while (c.moveToNext()) {
                DataItem item = new DataItem();
                item.result_type = c.getString(2);
                item.cpu_act = c.getString(3);
                item.cpu_iow = c.getString(4);
                item.cpu_idl = c.getString(5);
                item.cs_tot = c.getString(6);
                item.cs_vol = c.getString(7);

                item.exp_id = c.getInt(10);
                switch (item.exp_id) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        item.throughput = c.getString(8);
                        break;
                    case 4:
                    case 5:
                    case 6:
                        item.db_tps = c.getString(8);
                        break;
                }
                mData.add(item);
            }
        }

        mAdapter = new ResultAdapter(this);
        setListAdapter(mAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    public void setData(List<DataItem> data) {
        mData = data;
    }

    class ViewHolder {
        ImageView typeImg;
        TextView  cpuText;
        TextView  ctxText;
        TextView titleText;
    }

    class ResultAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ResultAdapter(Context context) {
            mInflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int i) {
            return mData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder = new ViewHolder();
            if(convertView == null) {
                convertView = mInflater.inflate(R.layout.result_list_item, null, false);
                holder.typeImg = (ImageView)convertView.findViewById(R.id.type_img);
                holder.titleText = (TextView)convertView.findViewById(R.id.tx_title);
                holder.cpuText = (TextView)convertView.findViewById(R.id.tx_cpu);
                holder.ctxText = (TextView)convertView.findViewById(R.id.tx_ctx);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            // set holder info
            DataItem item = mData.get(position);
            String title1 = MobiBenchExe.ExpName[item.exp_id] + ":" + item.throughput;
            String title2 = MobiBenchExe.ExpName[item.exp_id] + ":" + item.db_tps;
            switch (item.exp_id) {
                case 0:
                    holder.typeImg.setImageResource(R.drawable.icon_sw);
                    holder.titleText.setText(title1);
                    break;
                case 1:
                    holder.typeImg.setImageResource(R.drawable.icon_sr);
                    holder.titleText.setText(title1);
                    break;
                case 2:
                    holder.typeImg.setImageResource(R.drawable.icon_rw);
                    holder.titleText.setText(title1);
                    break;
                case 3:
                    holder.typeImg.setImageResource(R.drawable.icon_rr);
                    holder.titleText.setText(title1);
                    break;
                case 4:
                    holder.typeImg.setImageResource(R.drawable.icon_insert);
                    holder.titleText.setText(title2);
                    break;
                case 5:
                    holder.typeImg.setImageResource(R.drawable.icon_update);
                    holder.titleText.setText(title2);
                    break;
                case 6:
                    holder.typeImg.setImageResource(R.drawable.icon_delete);
                    holder.titleText.setText(title2);
                    break;
            }

            String cpu = "CPU:" + "Busy("+ item.cpu_act + ")" + "Iow(" + item.cpu_iow +")" + "Idle(" + item.cpu_idl + ")";
            String ctx = "Ctx sw:" + item.cs_tot + "(" + item.cs_vol + ")";
            holder.cpuText.setText(cpu);
            holder.ctxText.setText(ctx);
            return convertView;
        }
    }
}
