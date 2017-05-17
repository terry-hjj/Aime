#! /usr/bin/env monkeyrunner

import sys
from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice

CMD_MAP = {
    "TOUCH": lambda dev, arg: dev.touch(**arg),
    "DRAG" : lambda dev, arg: dev.drag(**arg),
    "PRESS": lambda dev, arg: dev.press(**arg),
    "TYPE" : lambda dev, arg: dev.type(**arg),
    "WAIT" : lambda dev, arg: MonkeyRunner.sleep(**arg)
    }


def process_file(action_file, dev):
    '''
    aaa
    '''
    i = 1
    for line in action_file:
        print "line: %d" % i
        i += 1
        if line.startswith("#") or line.startswith(" "):
            continue
        ll = line.split("|")
        if len(ll) > 1:
            (cmd, rest) = ll
        else:
            (cmd, rest) = None, None
        try:
            # parse str to python dict
            rest = eval(rest)
        except Exception:
            print "unable to parse options"
            continue

        if cmd not in CMD_MAP:
            print "unknown command: " + cmd
            continue

        CMD_MAP[cmd](dev, rest)
        MonkeyRunner.sleep(0.5)


if __name__ == "__main__":
    file = sys.argv[1]
    fp = open(file, "r")

    device = MonkeyRunner.waitForConnection(10, "emulator-5554")
    device.startActivity(component="com.example.android.aime/com.example.android.aime.MainActivity")
    MonkeyRunner.sleep(2)

    process_file(fp, device)
    fp.close()

    result = device.takeSnapshot()
    result.writeToFile("D:/workspace/apptest/mr/aime_result.png", "png")
    
    std_pic = MonkeyRunner.loadImageFromFile("D:/workspace/apptest/mr/aime_result_std.png")
    if result.sameAs(std_pic, 0.9) :
        print("Success")
    else:
        print("Failure")

    device.press("KEYCODE_HOME", MonkeyDevice.DOWN_AND_UP)
    device.press("KEYCODE_HOME", MonkeyDevice.DOWN_AND_UP)
    device.press("KEYCODE_HOME", MonkeyDevice.DOWN_AND_UP)
    
