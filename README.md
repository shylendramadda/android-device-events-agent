# android-device-events-agent
This is an agent app to listen for the events and capture the logs from the android device and save it in the local storage.

## Some useful commands using adb
1. To install the apk:
   ```sh
   adb install <your-apk-path>.apk
   ```

2. To uninstall the apk:
   ```sh
   adb uninstall app.mypackage.name
   ```
   Example:
   ```sh
   adb uninstall com.opentext.androidagent
   ```

3. To restart the device:
   ```sh
   adb reboot
   ```

4. To enable the service manually from Accessibility settings:
   ```sh
    adb shell settings put secure enabled_accessibility_services com.opentext.androidagent/com.opentext.androidagent.AgentAccessibilityService
    adb shell settings put secure accessibility_enabled 1
   ```

5. To get the list of services running:
   ```sh
    adb shell settings get secure enabled_accessibility_services
   ```

6. To see the logs:
   ```sh
    adb logcat -s EncryptionUtils
   ```

7. To get the detailed logs in a text file:
   ```sh
    adb logcat -d > log.txt
   ```

8. To check logs specific to service:
   ```sh
    adb logcat | grep -i "AgentAccessibilityService"
    adb shell dumpsys accessibility | grep "AgentAccessibilityService"
   ```

9. To check the list of files available in app internal storage:
   ```sh
    adb shell ls -l /sdcard/Android/data/com.opentext.androidagent/files/
   ```

10. To export the file from application internal memory:
    ```sh
    adb pull /sdcard/Android/data/com.opentext.androidagent/files/events_log.json
    ```

11. To capture a screenshot:
    ```sh
    adb shell screencap -p /sdcard/screenshot.png
    ```

12. To pull the screenshot to directory:
    ```sh
    adb shell pull /sdcard/screenshot.png
    ```

13. To pull with the timestamp:
    ```sh
    adb pull /sdcard/screenshot.png screenshot_$(date +%Y%m%d_%H%M%S).png
    ```

14. To capture video:
    ```sh
    adb shell screenrecord /sdcard/screen_video.mp4
    ```

15. To capture video with time limit:
    ```sh
    adb shell screenrecord --time-limit 10 /sdcard/screen_video.mp4
    ```

16. To pull video:
    ```sh
    adb pull /sdcard/screen_video.mp4
    ```