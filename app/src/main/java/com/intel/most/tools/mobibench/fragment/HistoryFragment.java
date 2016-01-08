package com.intel.most.tools.mobibench.fragment;

import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.intel.most.tools.R;
import com.intel.most.tools.mobibench.DataItem;
import com.intel.most.tools.mobibench.DatabaseHelper;
import com.intel.most.tools.mobibench.ResultActivity;


import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends ListFragment {

    private Cursor mCursor;
    private HistoryAdapter mAdapter;

    private List<DataItem> mData = new ArrayList<DataItem>();

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("yangjun", "receive data insert broadcast");
            mCursor.requery();
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHelper mDBHelper = new DatabaseHelper(getActivity());
        SQLiteDatabase dbR = mDBHelper.getWritableDatabase();
        mCursor = dbR.rawQuery("select DISTINCT _id,date,type from history;", null);
        mAdapter = new HistoryAdapter(getActivity(), mCursor, CursorAdapter.FLAG_AUTO_REQUERY);
        setListAdapter(mAdapter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ResultActivity.ACTION_DATABASE_CHANGE);
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // goto result activity
        Log.e("yangjun", "click item:" + position);
        Intent intent = new Intent(getActivity(), ResultActivity.class);
        intent.putExtra("from", "history");
        intent.putExtra("db_index", "" + (position + 1));
        startActivity(intent);
    }

    class HistoryAdapter extends CursorAdapter {

        public HistoryAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return LayoutInflater.from(getActivity()).inflate(R.layout.history_list_item, null, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView textView = (TextView)view.findViewById(R.id.history_item);
            String text = cursor.getString(2) + "(" + cursor.getString(1) + ")";
            textView.setText(text);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCursor.close();
        getActivity().unregisterReceiver(mReceiver);
    }
}
