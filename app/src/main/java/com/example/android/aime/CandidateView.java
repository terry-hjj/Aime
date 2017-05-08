package com.example.android.aime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


/**
 * Created by TRY on 2017/5/5.
 */

public class CandidateView extends LinearLayout {
    public View v; // 候选框整体
    public TextView input_text; // 暂时输入内容
    public TextView [] tv = new TextView[5]; // 每屏5个候选词
    public Button btLeft, btRight; // 左右翻页
    public int offset = 0; // 每屏5个候选词中的第一个词，在所有候选结果列表中的偏移
    public Aime listener; //  IME对象
    public List<String> suggestion; // 候选词列表
    public int sugg_len = 0; // 候选词列表长度

    public CandidateView(Context context){
        super(context);

        // 定位各个UI组件
        v = LayoutInflater.from(context).inflate(R.layout.candidate, this);
        input_text = (TextView) v.findViewById(R.id.input_text);
        tv[0] = (TextView) v.findViewById(R.id.tv0);
        tv[1] = (TextView) v.findViewById(R.id.tv1);
        tv[2] = (TextView) v.findViewById(R.id.tv2);
        tv[3] = (TextView) v.findViewById(R.id.tv3);
        tv[4] = (TextView) v.findViewById(R.id.tv4);
        btLeft = (Button) v.findViewById(R.id.bt_left);
        btRight = (Button) v.findViewById(R.id.bt_right);

        // 每屏5个候选词，被点击时，通过调用IME对象的pickSuggestionManually方法完成内容输入
        tv[0].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv[0].getText().toString().length() > 0) {
                    listener.pickSuggestionManually(offset + 0);
                }
            }
        });
        tv[1].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv[1].getText().toString().length() > 0) {
                    listener.pickSuggestionManually(offset + 1);
                }
            }
        });
        tv[2].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv[2].getText().toString().length() > 0) {
                    listener.pickSuggestionManually(offset + 2);
                }
            }
        });
        tv[3].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv[3].getText().toString().length() > 0) {
                    listener.pickSuggestionManually(offset + 3);
                }
            }
        });
        tv[4].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv[4].getText().toString().length() > 0) {
                    listener.pickSuggestionManually(offset + 4);
                }
            }
        });

        // 向左/右翻，需要重新设置offset值，以及5个候选词的内容
        btLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                offset = offset > 0 ? offset - 1 : 0;
                for(int i = 0; i < 5; i++){
                    if( offset + i < sugg_len ){
                        tv[i].setText(suggestion.get(offset + i).trim());
                    }else{
                        tv[i].setText("");
                    }
                }
            }
        });
        btRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                offset = offset + 4 < sugg_len - 1 ? offset + 1 : offset;
                for(int i = 0; i < 5; i++){
                    if( offset + i < sugg_len){
                        tv[i].setText(suggestion.get(offset + i).trim());
                    }else{
                        tv[i].setText("");
                    }
                }
            }
        });
    }

    // 初始化IME对象值
    public void setService(Aime listener){
        this.listener = listener;
    }

    // IME对象根据当前暂时输入的内容，查库得到候选词列表，调用该函数传给候选框视图
    // 候选框视图重置偏移值, 和5个候选词
    public void setSuggestion(List<String> suggestion){
        offset = 0;
        sugg_len = suggestion.size();
        this.suggestion = suggestion;

        for(int i = 0; i < 5; i++){
            if(offset + i < sugg_len){
                tv[i].setText(suggestion.get(offset + i).trim());
            }else{
                tv[i].setText("");
            }
        }
    }
}
