package com.mattmerr.jautoddc;

import com.sun.jna.platform.win32.Dxva2;
import com.sun.jna.platform.win32.PhysicalMonitorEnumerationAPI.PHYSICAL_MONITOR;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.BYTE;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.LPARAM;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinUser.HMONITOR;
import com.sun.jna.platform.win32.WinUser.MONITORENUMPROC;
import com.sun.jna.platform.win32.WinUser.MONITORINFO;
import java.util.function.Predicate;

public class DisplayInputSwapper {

  public static void swapToHdmi() {
    swapToSource(17);
  }
  public static void swapToHdmi(Predicate<Integer> byX) {
    swapToSource(17, byX);
  }

  public static void swapToDisplayPort() {
    swapToSource(15);
  }
  public static void swapToDisplayPort(Predicate<Integer> byX) {
    swapToSource(15, byX);
  }

  public static void swapToSource(int sourceValue) {
    swapToSource(sourceValue, $ -> true);
  }


    /**
     * @param sourceValue https://github.com/kfix/ddcctl#input-sources
     * VGA-1 	1
     * VGA-2 	2
     * DVI-1 	3
     * DVI-2 	4
     * Composite video 1 	5
     * Composite video 2 	6
     * S-Video-1 	7
     * S-Video-2 	8
     * Tuner-1 	9
     * Tuner-2 	10
     * Tuner-3 	11
     * Component video (YPrPb/YCrCb) 1 	12
     * Component video (YPrPb/YCrCb) 2 	13
     * Component video (YPrPb/YCrCb) 3 	14
     * DisplayPort-1 	15
     * DisplayPort-2 	16
     * HDMI-1 	17
     * HDMI-2 	18
     * USB-C 	27
     */
  public static void swapToSource(int sourceValue, Predicate<Integer> byX) {
    User32.INSTANCE.EnumDisplayMonitors(null, null, new MONITORENUMPROC() {

      @Override
      public int apply(HMONITOR hMonitor, HDC hdc, RECT rect, LPARAM lparam) {
        if (!byX.test(rect.left)) {
          return 1;
        }
        DWORDByReference numMonitors = new DWORDByReference();
        Dxva2.INSTANCE.GetNumberOfPhysicalMonitorsFromHMONITOR(hMonitor, numMonitors);

        MONITORINFO lpmi = new MONITORINFO();
        User32.INSTANCE.GetMonitorInfo(hMonitor, lpmi);

        PHYSICAL_MONITOR[] monitors = new PHYSICAL_MONITOR[numMonitors.getValue().intValue()];
        Dxva2.INSTANCE.GetPhysicalMonitorsFromHMONITOR(hMonitor, monitors.length, monitors);

        for (var monitor : monitors) {
          Dxva2.INSTANCE
              .SetVCPFeature(monitor.hPhysicalMonitor, new BYTE(0x60), new DWORD(sourceValue));
        }
        return 1;
      }
    }, new LPARAM(0));

  }

}
