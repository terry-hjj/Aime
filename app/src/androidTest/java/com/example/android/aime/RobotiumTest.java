package com.example.android.aime;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.robotium.solo.Solo;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by TRY on 2017/5/12.
 */

public class RobotiumTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;

    public String zhongwen; // 为在内部类(使用runOnUiThread)中访问外部类变量而设

    public RobotiumTest(){
        super(MainActivity.class);
    }

    @Override
    public void setUp(){
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown(){
        solo.finishOpenedActivities();
    }

    // 插入一个新单词,再测试查询该单词
    public void testInsertABC(){
        Assert.assertTrue("没有进入主界面", solo.searchText("TEST PAGE"));
        solo.sleep(1000);

        EditText et_word = (EditText) solo.getCurrentActivity().findViewById(R.id.word);
        Button bt_insert = (Button) solo.getCurrentActivity().findViewById(R.id.insert);
        Button bt_search = (Button) solo.getCurrentActivity().findViewById(R.id.search);
        TextView tv_result = (TextView) solo.getCurrentActivity().findViewById(R.id.result);

        String[] c = new String[]{"a","b","c","d","e","f","g",
                "h","i","j","k","l","m","n",
                "o","p","q","r","s","t",
                "u","v","w","x","y","z"};
        // 查找一个现在词库中不存在的单词
        String test_word = "";
        outer:
        for(int i = 0; i < 26; i++){
            String w1 = c[i];
            for(int j = 0; j < 26; j++){
                String w2 = w1 + c[j];
                for(int k = 0; k < 26; k++){
                    String w3 = w2 + c[k];

                    // 查询该词, 存在则继续寻找, 不存在则结束查找过程
                    solo.clearEditText(et_word);
                    solo.typeText(et_word, w3);
                    solo.sleep(1000);
                    solo.clickOnButton("SEARCH");
                    solo.sleep(1000);
                    String res = tv_result.getText().toString();
                    if(res.length() >= w3.length()) {
                        if(w3.equals(res.substring(0, w3.length()))){
                            continue;
                        }
                    }else{ // 没查到该词, 则保存该词,结束查找
                        test_word = w3;
                        break outer;
                    }
                }
            }
        }
        Assert.assertFalse("没有找到可以测试插入的英文单词", test_word.equals(""));

        // 找到一个可以测试插入的词,开始验证
        // 插入
        solo.clearEditText(et_word);
        solo.sleep(1000);
        solo.typeText(et_word, test_word);
        solo.sleep(1000);
        solo.clickOnButton("INSERT");
        solo.sleep(1000);
        // 清空查询
        solo.clearEditText(et_word);
        solo.sleep(1000);
        solo.typeText(et_word, "zzz"); // 查询zzz, 一定没结果
        solo.sleep(1000);
        solo.clickOnButton("SEARCH");
        solo.sleep(1000);
        String res = tv_result.getText().toString();
        Assert.assertTrue("zzz 不应查到结果", res.equals(""));
        // 查询刚插入的单词
        solo.clearEditText(et_word);
        solo.sleep(1000);
        solo.typeText(et_word, test_word);
        solo.sleep(1000);
        solo.clickOnButton("SEARCH");
        solo.sleep(1000);
        res = tv_result.getText().toString();
        // 结果中第一个就应该是该词
        Assert.assertTrue("没有查到新插入的单词", test_word.equals(res.substring(0, test_word.length())));
    }

    // 插入一个新的中文词, 再测试查询该词的拼音
    public void testInsert甲乙丙(){
        Assert.assertTrue("没有进入主界面", solo.searchText("TEST PAGE"));
        solo.sleep(1000);

        EditText et_cn_word = (EditText) solo.getCurrentActivity().findViewById(R.id.cn_word);
        final EditText et_cn_zhongwen = (EditText) solo.getCurrentActivity().findViewById(R.id.cn_zhongwen);
        Button bt_cn_insert = (Button) solo.getCurrentActivity().findViewById(R.id.cn_insert);
        Button bt_cn_search = (Button) solo.getCurrentActivity().findViewById(R.id.cn_search);
        TextView tv_cn_result = (TextView) solo.getCurrentActivity().findViewById(R.id.cn_result);

        String[] c = new String[]{"jia","yi","bing","ding",
                "wu","ji","geng","xin",
                "ren","gui"};
        String[] z = new String[] {"甲","乙","丙","丁",
                "戊","己","庚","辛",
                "壬","癸"};
        // 查找一个现在库中不存在的中文拼音词
        String test_word = "";
        String test_zhongwen = "";
        outer:
        for(int i = 0; i < 10; i++){
            String w1 = c[i];
            for(int j = 0; j < 10; j++){
                String w2 = w1 + c[j];
                for(int k = 0; k < 10; k++){
                    String w3 = w2 + c[k];

                    // 查询该词, 存在则继续寻找, 不存在则结束查找过程
                    solo.clearEditText(et_cn_word);
                    solo.typeText(et_cn_word, w3);
                    solo.sleep(1000);
                    solo.clickOnButton("CN_SEARCH");
                    solo.sleep(1000);
                    String res = tv_cn_result.getText().toString();
                    if(! res.equals("")) { // 查到该拼音有对应中文词
                            continue;
                    }else{ // 没查到该中文拼音词, 则保存该中文拼音及中文词,结束查找
                        test_word = w3;
                        test_zhongwen = z[i] + z[j] + z[k];
                        break outer;
                    }
                }
            }
        }
        Assert.assertFalse("没有找到一个可用来测试的中文词, 用例失败", test_word.equals(""));

        // 找到一个可以测试的中文拼音及对应词, 开始测试
        // 插入
        solo.clearEditText(et_cn_word);
        solo.clearEditText(et_cn_zhongwen);
        solo.sleep(1000);
        solo.typeText(et_cn_word, test_word);

        // solo.typeText(et_cn_zhongwen, test_zhongwen); // NG! 不支持中文
        // et_cn_zhongwen.setText(test_zhongwen); // NG! 改变UI只能是在活动主线程中进行
        zhongwen = test_zhongwen; // 为外部类变量赋值,以使内部类可以访问
        System.out.println("zhongwen 1: " + zhongwen);
        solo.getCurrentActivity().runOnUiThread(new Runnable(){
            @Override
            public void run(){
                et_cn_zhongwen.setText(zhongwen); // 在内部类中访问外部类变量, 改变中文词显示内容
         }
        });
        solo.sleep(2000);

        solo.clickOnButton("CN_INSERT");
        solo.sleep(1000);
        zhongwen = ""; //
        // 清空查询
        solo.clearEditText(et_cn_word);
        solo.clearEditText(et_cn_zhongwen);
        solo.sleep(1000);
        solo.typeText(et_cn_word, "zzz"); // zzz 一定查不到结果
        solo.sleep(1000);
        solo.clickOnButton("CN_SEARCH");
        solo.sleep(1000);
        String res = tv_cn_result.getText().toString();
        Assert.assertTrue("zzz 不应查到词", res.equals(""));
        // 查询刚插入的中文词
        solo.clearEditText(et_cn_word);
        solo.clearEditText(et_cn_zhongwen);
        solo.sleep(1000);
        solo.typeText(et_cn_word, test_word);
        solo.sleep(1000);
        solo.clickOnButton("CN_SEARCH");
        solo.sleep(1000);
        res = tv_cn_result.getText().toString();
        // 结果中第一个就应该是该词
        Assert.assertTrue("没有查到新插入的单词", test_zhongwen.equals(res.substring(0, test_zhongwen.length())));
    }
}
