package com.example.android.aime;

import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.IBinder;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Aime extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private InputMethodManager mInputMethodManager; // IMF管理器
    private KeyboardView mKeyboardView; // 软键盘主视图
    private CandidateView mCandView;  // 候选词视图

    private Keyboard mQwertyKeyboard; // 字母键盘
    private Keyboard mSymbolsKeyboard; // 符号小键盘
    private Keyboard mNumbersKeyboard; // 数字小键盘
    private boolean isShift = false;  // 当前是否按下了shift
    private boolean isCn = false; // 当前是否中文输入

    private MyDbHelper dbHelper; // 数据库帮助对象
    private SQLiteDatabase db; // 数据库读写
    private String input_text = ""; // 暂时输入的内容, 据此查询中英文字词, 并填入候选词视图中待选
    private List<String> suggestionList = new ArrayList<String>(); // 候选词列表
    private InputConnection ic; // 输入法连接,通过该连接将手选的字词上屏

    private final int MAX_CANDIDATE_LEN = 100; // 限制最大候选词数量


    @Override
    public void onCreate(){ // 启动输入法服务
        super.onCreate();
        dbHelper = new MyDbHelper(this, "ime.db", null, 2); // 初始化数据库帮助对象
    }


    @Override
    public void onPress(int primaryCode){}
    @Override
    public void onRelease(int primaryCode){}
    @Override
    public void onText(CharSequence text){}
    @Override
    public void swipeUp(){}
    @Override
    public void swipeDown(){}
    @Override
    public void swipeLeft(){}
    @Override
    public void swipeRight(){}


    public View onCreateInputView(){ // 启动输入法视图

        // 根据布局文件展开视图,并开启事件监听
        mKeyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        mKeyboardView.setOnKeyboardActionListener(this);

        // 初始化三种键盘, 以便今后相互切换, 默认先设置为字母主键盘
        mQwertyKeyboard = new Keyboard(this, R.xml.qwerty);
        mSymbolsKeyboard = new Keyboard(this, R.xml.symbols);
        mNumbersKeyboard = new Keyboard(this, R.xml.numbers);
        mKeyboardView.setKeyboard(mQwertyKeyboard);

        return mKeyboardView;
    }

    public View onCreateCandidatesView(){ // 启动候选视图
        mCandView = new CandidateView(this);
        mCandView.setService(this); // 设置所依赖的IME对象
        return mCandView;
    }

    public void onKey(int primaryCode, int[] keyCodes){
        ic = getCurrentInputConnection();
        switch(primaryCode){
            case 10: // ENTER
                if(input_text.length() > 0){
                    // 按回车时, 若暂时输入内容不为空, 则直接把暂时输入内容上屏
                    ic.commitText(input_text.trim(), 0);
                    setCandidatesViewShown(false);
                    input_text = "";
                    mCandView.input_text.setText("");
                    break;
                }
                // 按回车是, 若暂时输入内容为空, 则直接输入一个回车上屏
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            case -1: // SHIFT
                isShift = !isShift;
                mKeyboardView.setShifted(isShift);
                break;
            case -3: // CANCEL
                requestHideSelf(0); // 隐藏软键盘
                mKeyboardView.closing();
                break;
            case -5: // DEL
                // 若暂时输入区内还有内容, 则将其中最后一个字符删去
                if(input_text.length() > 1){ // 删去一个字符后,仍有内容
                    input_text = input_text.substring(0, input_text.length() - 1);
                    String low_input_text = input_text.toLowerCase(); // 转成小写, 再重新查询词库
                    Cursor cursor;
                    if(isCn) { // 查询中文字词库
                        cursor = db.rawQuery("select word from cn where txt like ? order by txt", new String[]{low_input_text + "%"});
                    }else{ // 查询英文词库
                        cursor = db.rawQuery("select txt from en where txt like ? order by txt", new String[]{low_input_text + "%"});
                    }

                    // 先清空上次的查询记录, 再把本次查询结果放进候选词列表
                    suggestionList.clear();
                    int i = MAX_CANDIDATE_LEN;
                    while(cursor.moveToNext() && i > 0){  // 中英文表字段不同
                        if(isCn) {
                            suggestionList.add(cursor.getString(cursor.getColumnIndex("word")) + "\n");
                        }else{
                            suggestionList.add(cursor.getString(cursor.getColumnIndex("txt")) + "\n");
                        }
                        i -= 1;
                    }
                    cursor.close();

                    // 让候选词视图显示暂时输入内容, 传递候选词列表给候选词视图
                    mCandView.input_text.setText(input_text);
                    mCandView.setSuggestion(suggestionList);
                    setCandidatesViewShown(true);
                }else if(input_text.length() == 1) { // 删去一个字符后, 暂时输入内容为空, 隐藏候选视图
                    input_text = "";
                    suggestionList.clear();
                    mCandView.input_text.setText(input_text);
                    setCandidatesViewShown(false);
                } else {
                    ic.deleteSurroundingText(1, 0); // 暂时输入内容为空, 则直接删除屏幕上的一个字符
                }
                break;
            case -101: // LANGUAGE_SWITCH
                System.out.println("primaryCode: " + primaryCode );
                mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                mInputMethodManager.switchToNextInputMethod(getToken(), false);
                break;
            case -123: // KEYBOARD_NUM_MODE
                // 切换到数字小键盘时, 若暂时输入内容不为空, 则直接把暂时输入内容上屏
                ic.commitText(input_text.trim(), 0);
                setCandidatesViewShown(false);
                input_text = "";
                mCandView.input_text.setText("");
                mKeyboardView.setKeyboard(mNumbersKeyboard); // 切换为数字小键盘
                break;
            case -124: // KEYBOARD_CHAR_MODE
                // 切换到符号小键盘时, 若暂时输入内容不为空, 则直接把暂时输入内容上屏
                ic.commitText(input_text.trim(), 0);
                setCandidatesViewShown(false);
                input_text = "";
                mCandView.input_text.setText("");
                mKeyboardView.setKeyboard(mSymbolsKeyboard); // 切换为符号小键盘
                break;
            case -125: // KEYBOARD_BACK_MODE : back to qwerty
                // 切换回字母主键盘时, 若暂时输入内容不为空, 则直接把暂时输入内容上屏
                ic.commitText(input_text.trim(), 0);
                setCandidatesViewShown(false);
                input_text = "";
                mCandView.input_text.setText("");
                mKeyboardView.setKeyboard(mQwertyKeyboard); // 切换回字母主键盘
                break;
            case -126: // CN 中文输入
                isCn = !isCn;
                break;
            default:
                if((mKeyboardView.getKeyboard() == mNumbersKeyboard)
                        ||
                   (mKeyboardView.getKeyboard() == mSymbolsKeyboard)){
                    // 数字小键盘 或 符号小键盘的输入直接上屏, 不使用候选词视图
                    ic.commitText(String.valueOf((char)primaryCode), 1);
                    break;
                }
                if(primaryCode == 32 && input_text.length() > 0){
                    // 按空格时, 若暂时输入内容不为空, 则直接把暂时输入内容上屏
                    ic.commitText(input_text.trim(), 0);
                    setCandidatesViewShown(false);
                    input_text = "";
                    mCandView.input_text.setText("");
                    break;
                } else if (primaryCode == 32 && input_text.length() == 0){
                    // 按空格时, 若暂时输入内容为空, 则直接把一个空格上屏
                    ic.commitText(String.valueOf((char)primaryCode), 1);
                    break;
                }
                db = dbHelper.getReadableDatabase();
                if(isShift){ // 若按下shift, 则变大写
                    primaryCode = Character.toUpperCase(primaryCode);
                }
                //if((primaryCode >= 97 && primaryCode <= 122) || (primaryCode >= 65 && primaryCode <= 90)){
                    input_text = input_text + String.valueOf((char)primaryCode);
                    System.out.println("input_text: "+ input_text);
                    String low_input_text = input_text.toLowerCase(); // 权转为小写,再查词库
                    Cursor cursor;
                    if(isCn) { // 中文
                        cursor = db.rawQuery("select word from cn where txt like ? order by txt", new String[]{low_input_text + "%"});
                    }else{  // 英文
                        cursor = db.rawQuery("select txt from en where txt like ? order by txt", new String[]{low_input_text + "%"});
                    }

                // 清空后把本次查询结果放进候选词列表
                    suggestionList.clear();
                    int i = MAX_CANDIDATE_LEN;
                    while(cursor.moveToNext() && i > 0){ // 中英文表字段不同
                        if(isCn) {
                            suggestionList.add(cursor.getString(cursor.getColumnIndex("word")) + "\n");
                        }else{
                            suggestionList.add(cursor.getString(cursor.getColumnIndex("txt")) + "\n");
                        }
                        i -= 1;
                    }
                    cursor.close();

                // 将暂时输入内容和候选列表传给候选视图供其显示
                    mCandView.input_text.setText(input_text);
                    mCandView.setSuggestion(suggestionList);
                    setCandidatesViewShown(true);
                    break;
                //}
                //if(isShift){
                //    primaryCode = Character.toUpperCase(primaryCode);}
                //
                //char code = (char) primaryCode;
                //ic.commitText(String.valueOf(code), 1);
        }
    }


    // 切换输入法时会用到该函数
    public IBinder getToken(){
        final Dialog dialog = getWindow();
        if(dialog == null){
            return null;
        }
        final Window window = dialog.getWindow();
        if(window == null){
            return null;
        }

        return window.getAttributes().token;
    }

    // 候选视图收到用户的选择后, 调用该函数将选择的序号传回给IME
    // 由IME将用户选择的词上屏, 然后隐藏候选词视图
    public void pickSuggestionManually(int index){
        getCurrentInputConnection().commitText(suggestionList.get(index).trim(), 0);
        setCandidatesViewShown(false);
        input_text = "";
        mCandView.input_text.setText("");
    }
}
