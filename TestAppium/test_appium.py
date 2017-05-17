# coding:utf-8

from time import sleep
import os
import unittest

from appium import webdriver

desired_caps = {}
drivers = [None]

class AddTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        print("setUpClass")
        # uninstall io.appium.settings and io.appium.unlock for
        # running this time smoothly
        print("Before running uninstall settings unlock status: " +
              os.popen("adb uninstall io.appium.settings").read().strip() + " " +
              os.popen("adb uninstall io.appium.unlock").read().strip() )
        #
        desired_caps["platformName"] = "Android"
        desired_caps["platformVersion"] = "7.1.1"
        desired_caps["deviceName"] = "Android Emulator"
        desired_caps["appPackage"] = "com.example.android.aime"
        desired_caps["appActivity"] = ".MainActivity"
        #
        #drivers[0] = webdriver.Remote("http://localhost:4723/wd/hub", desired_caps)
        #sleep(1)

    @classmethod
    def tearDownClass(cls):
        print("tearDownClass")
        sleep(1)
        #drivers[0].quit()
        
    
    def setUp(self):
        print("setUp")
        drivers[0] = webdriver.Remote("http://localhost:4723/wd/hub", desired_caps)
        sleep(1)
        driver = drivers[0]
        bt_test_page = driver.find_elements_by_class_name("android.widget.Button")[0]
        bt_test_page.click()
        sleep(1)

    def tearDown(self):
        print("tearDown")
        driver = drivers[0]
        driver.keyevent(4) # BACK
        sleep(1)
        driver.keyevent(4) # BACK
        sleep(1)
        drivers[0].quit()
        # uninstall io.appium.settings and io.appium.unlock
        # for running next time smoothly
        print("End running uninstall settings unlock status: " +
        os.popen("adb uninstall io.appium.settings").read().strip() + " " +
        os.popen("adb uninstall io.appium.unlock").read().strip() )

    def testENdream(self):
        sleep(1)
        driver = drivers[0]
        et1 = driver.find_elements_by_class_name("android.widget.EditText")[0]
        et1.click()
        sleep(1)
        # type : I have a dream
        driver.tap([(100, 1550),],)
        sleep(0.5)
        driver.tap([(820, 1250 ),],)
        sleep(0.5)
        driver.tap([(540 , 1700),],)
        sleep(0.5)
        driver.tap([( 100, 1550),],)
        sleep(0.5)
        driver.tap([( 540, 1700),],)
        sleep(0.5)
        driver.tap([( 650, 1400),],)
        sleep(0.5)
        driver.tap([( 100, 1400),],)
        sleep(0.5)
        driver.tap([( 540, 1550),],)
        sleep(0.5)
        driver.tap([( 270, 1250),],)
        sleep(0.5)
        driver.tap([( 540, 1700),],)
        sleep(0.5)
        driver.tap([( 540, 1700),],)
        sleep(0.5)
        driver.tap([( 100, 1400),],)
        sleep(0.5)
        driver.tap([( 540, 1700),],)
        sleep(0.5)
        driver.tap([( 540, 1700),],)
        sleep(0.5)
        driver.tap([( 320, 1400),],)
        sleep(0.5)
        driver.tap([( 380, 1250),],)
        sleep(0.5)
        driver.tap([( 270, 1250),],)
        sleep(0.5)
        driver.tap([( 100, 1450),],)
        sleep(0.5)
        driver.tap([( 870, 1550),],)
        sleep(0.5)
        driver.tap([( 180, 1100),],)
        sleep(0.5)
        #
        self.assertEqual("I have a dream", et1.text, "testENdream ng")

    def testCNdream(self):
        sleep(10)
        driver = drivers[0]
        et1 = driver.find_elements_by_class_name("android.widget.EditText")[0]
        et1.click()
        sleep(1)
        # type : woyou yige mengxiang
        driver.tap([( 100, 1700),],)
        sleep(0.5)
        driver.tap([( 160, 1250),],)
        sleep(0.5)
        driver.tap([( 930, 1250),],)
        sleep(0.5)
        driver.tap([( 600, 1250),],)
        sleep(0.5)
        driver.tap([( 930, 1250),],)
        sleep(0.5)
        driver.tap([( 710, 1250),],)
        sleep(0.5)
        driver.tap([( 180, 1100),],)
        sleep(0.5)
        driver.tap([( 600, 1250),],)
        sleep(0.5)
        driver.tap([( 820, 1250),],)
        sleep(0.5)
        driver.tap([( 540, 1400),],)
        sleep(0.5)
        driver.tap([( 270, 1250),],)
        sleep(0.5)
        driver.tap([( 180, 1100),],)
        sleep(0.5)
        driver.tap([( 870, 1550),],)
        sleep(0.5)
        driver.tap([( 270, 1250),],)
        sleep(0.5)
        driver.tap([( 760, 1550),],)
        sleep(0.5)
        driver.tap([( 540, 1400),],)
        sleep(0.5)
        driver.tap([( 320, 1550),],)
        sleep(0.5)
        driver.tap([( 820, 1250),],)
        sleep(0.5)
        driver.tap([( 100, 1400),],)
        sleep(0.5)
        driver.tap([( 760, 1550),],)
        sleep(0.5)
        driver.tap([( 540, 1400),],)
        sleep(0.5)
        driver.tap([( 180, 1100),],)
        sleep(0.5)
        driver.tap([( 100, 1700),],)
        #
        self.assertEqual(u"我有一个梦想", et1.text, "testCNdream ng")



if __name__ == "__main__":
    unittest.main()
