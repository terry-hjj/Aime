package com.example.android.aime;

import android.support.test.uiautomator.UiAutomatorTestCase;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by TRY on 2017/5/12.
 */

// 测试前需要先把系统自带的默认输入法禁用
// 点击输入法键盘的坐标是根据分辨率1080 * 1920的屏幕而定
public class UiAutomatorTest extends UiAutomatorTestCase {
    private final static String TAG = "Aime";

    public UiDevice device;
    public UiObject app;
    public UiObject bt_test_page; // TEST PAGE 按钮
    public UiObject tv_normal_et; // Normal标签
    public UiObject tv_decimal_et; // Decimal标签
    public UiObject tv_password_et; // Password标签
    public UiObject et_normal; // Normal输入框
    public UiObject et_decimal; // Decimal输入框
    public UiObject et_password; // Password输入框

    @Override
    public void setUp() throws UiObjectNotFoundException{
        Log.d(TAG, "setUp: ");
        device = getUiDevice();
        // 在主界面上找到Aime, 然后点击打开它
        device.pressHome();
        app = new UiObject(new UiSelector().description("Aime"));
        assertTrue("Aime: 没找到Aime图标", app.exists());
        app.clickAndWaitForNewWindow();
        // 点击按钮进入测试输入页面
        bt_test_page = new UiObject(new UiSelector().resourceId("com.example.android.aime:id/test"));
        bt_test_page.click();
        sleep(500);
        // 定位界面元素
        tv_normal_et = new UiObject(new UiSelector().className("android.widget.TextView").instance(1));
        tv_decimal_et = new UiObject(new UiSelector().className("android.widget.TextView").index(3));
        tv_password_et = new UiObject(new UiSelector().className("android.widget.TextView").index(6));
        et_normal = new UiObject(new UiSelector().className("android.widget.EditText").instance(0));
        et_decimal = new UiObject(new UiSelector().className("android.widget.EditText").instance(1));
        et_password = new UiObject(new UiSelector().className("android.widget.EditText").instance(2));
        // 验证是否进入了测试输入页面
        String testStr = tv_normal_et.getText();
        assertTrue("Aime: 没有进入输入测试页 - 找不到Normal EditText", testStr.equals("Normal EditText:"));
    }

    @Override
    public void tearDown(){
        Log.d(TAG, "tearDown: ");
    }

    // 测试是否测试输入页面
    public void testVerifyPage() throws UiObjectNotFoundException {
        Log.d(TAG, "testVerifyPage: ");
        // 验证测试输入页面上应有的元素
        String testStr = tv_normal_et.getText();
        assertTrue("Aime: 没有进入输入测试页 - 找不到Normal EditText", testStr.equals("Normal EditText:"));
        testStr = tv_decimal_et.getText();
        assertTrue("Aime: 没有进入输入测试页 - 找不到Decimal EditText", testStr.equals("Decimal EditText:"));
        testStr = tv_password_et.getText();
        assertTrue("Aime: 没有进入输入测试页 - 找不到password EditText", testStr.equals("Password EditText:"));
    }

    // 测试关闭测试页面
    public void testClosePage() throws UiObjectNotFoundException{
        Log.d(TAG, "testClosePage: ");
        // 后退两次
        device.pressBack();
        device.pressBack();
        // 检查主角界面上是否存在Aime
        UiObject obj = new UiObject(new UiSelector().description("Aime"));
        assertTrue("Aime: 没找到Aime图标", app.exists());
    }

    // 测试在测试页, 向NormalEditText中输入一个a
    public void testInput_a() throws UiObjectNotFoundException{
        Log.d(TAG, "testInput_a: ");
        try { testVerifyPage(); }
        catch(Exception e){ assertTrue("Aime: 没有进入测试输入界面", false); }

        // 在NormalEditText输入a
        et_normal.click();
        sleep(500);
        device.click(100, 1400); // 按'a'
        sleep(500);
        device.click(180, 1100); // 按第一个候选词
        sleep(500);
        assertEquals("Aime: 输入a不对", "a", et_normal.getText());
    }

    // 向NormalEditText中输入一个abroad
    public void testInput_abroad() throws UiObjectNotFoundException{
        Log.d(TAG, "testInput_abroad: ");

        et_normal.click(); sleep(500);
        device.click(100, 1400); sleep(500); // a
        device.click(650, 1550); sleep(500); // b
        device.click(380, 1250); sleep(500); // r
        device.click(930, 1250); sleep(500); // o
        device.click(100, 1400); sleep(500); // a
        device.click(320, 1400); sleep(500); // d
        device.click(180, 1100); sleep(500); // cw0
        assertEquals("Aime: 输入abroad不对", "abroad", et_normal.getText());
    }
}
/* 根据分辨率1080 * 1920 确定以下键位

** qwerty:
        40      180     360     540     720     900     1040
1100    <       cw0     cw1     cw2     cw3     cw4     >

        50      160     270     380     490     600     710     820     930     1040
1250    q       w       e       r       t       y       u       i       o       p

            100     210     320     430     540     650     760     870     980
1400        a       s       d       f       g       h       j       k       l

            100     210     320     430     540     650     760     870     980
1550        shift   z       x       c       v       b       n       m       del

            100     210     320             540             760             980
1700        中      123     (#)             space           .*=             enter


** numbers:
        100     320     540     760     980
1250    *       1       2       3       del
1400    /       4       5       6       space
1550    +       7       8       9       .*=
1700    -       .       0       abc     enter


** symbols:
        140     400     660     920
950     ,       .       ?       del
1100    :       /       @       space
1250    "       ;       '       123
1400    (       )       &       abc
1550    *       =       -       !
1700    %       +       #       $


 */
