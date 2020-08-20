package com.zeroner.bledemo.setting.repeat;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.socks.library.KLog;
import com.zeroner.bledemo.BaseActivity;
import com.zeroner.bledemo.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WeakDaySelectActivity extends BaseActivity {
    public static final int Type_Add_Clock=1;
    public static final int Type_Sendentary=2;

    @BindView(R.id.blood_recycler)
    ListView mBloodRecycler;
    private byte mWeekRept;
    private int[] selectedItmePos = new int[]{0, 0, 0, 0, 0, 0, 0};
    private ModeAdapter modeAdapter;
    private String[] mDayOfWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weak_day_select);
        ButterKnife.bind(this);

        mWeekRept = (byte) getIntent().getIntExtra("day_of_week", (byte) 0x00);
        mDayOfWeek = getResources().getStringArray(R.array.day_of_week_complete);
        initData();
        initView();
        initEvent();

    }

    private void initEvent() {
        mBloodRecycler.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItmePos[position] = selectedItmePos[position]==1 ? 0 : 1;
                modeAdapter.notifyDataSetChanged();
                getNewWeekRepeat();
            }
        });
    }

    private void getNewWeekRepeat() {
        mWeekRept = (byte) 0x80;
        for (int i = 0; i < selectedItmePos.length; i++) {
            switch (i) {
                case 0:
                    if (selectedItmePos[i]==1) {
                        mWeekRept |= 0x40;
                    }
                    break;
                case 1:
                    if (selectedItmePos[i]==1) {
                        mWeekRept |= 0x20;
                    }
                    break;
                case 2:
                    if (selectedItmePos[i]==1) {
                        mWeekRept |= 0x10;
                    }
                    break;
                case 3:
                    if (selectedItmePos[i]==1) {
                        mWeekRept |= 0x08;
                    }
                    break;
                case 4:
                    if (selectedItmePos[i]==1) {
                        mWeekRept |= 0x04;
                    }
                    break;
                case 5:
                    if (selectedItmePos[i]==1) {
                        mWeekRept |= 0x02;
                    }
                    break;
                case 6:
                    if (selectedItmePos[i]==1) {
                        mWeekRept |= 0x01;
                    }
                    break;
            }
        }
        KLog.e(TAG, "Get new duplicate bytes："+mWeekRept);
    }


    private void initView() {
        setLeftBackTo();
        switch (getIntent().getIntExtra("what_activity", -1)) {
            case Type_Sendentary:
                setTitleText(R.string.sedentary_reminder);
                break;
            case Type_Add_Clock:
                setTitleText(R.string.add_clock);
                break;
        }

        setRightText(getString(R.string.common_save), new ActionOnclickListener() {
            @Override
            public void onclick() {

                if (mWeekRept == -128) {
                    //A user did not choose
                    Toast.makeText(WeakDaySelectActivity.this, getString(R.string.select_least_one), Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("week_repeat", mWeekRept);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        modeAdapter = new ModeAdapter(this);
        mBloodRecycler.setAdapter(modeAdapter);
        modeAdapter.notifyDataSetChanged();
    }

    private void initData() {
        getWeekReptArr(mWeekRept);
    }


    public void getWeekReptArr(byte weakRepeat){
        StringBuilder sb = new StringBuilder();
        if ((weakRepeat & 0x40) != 0) {
            selectedItmePos[0] = 1;
        }
        if ((weakRepeat & 0x20) != 0) {
            selectedItmePos[1] = 1;
        }
        if ((weakRepeat & 0x10) != 0) {
            selectedItmePos[2] = 1;
        }
        if ((weakRepeat & 0x08) != 0) {
            selectedItmePos[3] = 1;
        }
        if ((weakRepeat & 0x04) != 0) {
            selectedItmePos[4] = 1;
        }
        if ((weakRepeat & 0x02) != 0) {
            selectedItmePos[5] = 1;
        }
        if ((weakRepeat & 0x01) != 0) {
            selectedItmePos[6] = 1;
        }
    }

    public void freshWeekRepeat(int num){
        switch (num) {
            case 0:
                mWeekRept |= 0x40;
                break;
            case 1:
                mWeekRept |= 0x20;
                break;
            case 2:
                mWeekRept |= 0x10;
                break;
            case 3:
                mWeekRept |= 0x08;
                break;
            case 4:
                mWeekRept |= 0x04;
                break;
            case 5:
                mWeekRept |= 0x02;
                break;
            case 6:
                mWeekRept |= 0x01;
                break;
        }
    }

    class ModeAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ModeAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return selectedItmePos.length;
        }

        @Override
        public Object getItem(int position) {
            return selectedItmePos[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            WeakDaySelectActivity.ViewHolder viewHolder;
            if(convertView==null){
                viewHolder = new WeakDaySelectActivity.ViewHolder();
                convertView = mInflater.inflate(R.layout.shake_mode_item,null);
                viewHolder.tvName=(TextView)convertView.findViewById(R.id.blood_item_text);
                viewHolder.check=(ImageView)convertView.findViewById(R.id.blood_item_img);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (WeakDaySelectActivity.ViewHolder)convertView.getTag();
            }
            viewHolder.tvName.setText(mDayOfWeek[position]);
            viewHolder.check.setVisibility(selectedItmePos[position] == 1 ? View.VISIBLE : View.INVISIBLE);
            return convertView;
        }
    }

    static class ViewHolder{
        public TextView tvName;
        public ImageView check;
    }


}
