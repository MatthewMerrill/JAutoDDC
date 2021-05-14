package com.mattmerr.jautoddc;

import static com.google.common.io.Resources.getResource;

import java.awt.CheckboxMenuItem;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Window.Type;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.imageio.ImageIO;
import javax.swing.UIManager;

public class Main {

  private static Frame frame = new Frame();
  private static TrayIcon icon;
  private static PopupMenu menu = new PopupMenu();

  @SuppressWarnings("UnstableApiUsage")
  public static void main(String[] args) throws Exception {
    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

    frame.setType(Type.UTILITY);
    frame.setUndecorated(true);
    frame.setResizable(false);
    frame.setVisible(true);

    Image img = ImageIO.read(getResource("trayicon.png"));
    SystemTray tray = SystemTray.getSystemTray();
    icon = new TrayIcon(img);
    icon.setImage(img.getScaledInstance(icon.getSize().width, -1, Image.SCALE_AREA_AVERAGING));
    icon.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        frame.add(menu);
        menu.show(frame, e.getXOnScreen(), e.getYOnScreen());
      }
    });
    tray.add(icon);

    CheckboxMenuItem toggleLeftMenu = new CheckboxMenuItem("Toggle Left", true);
    menu.add(toggleLeftMenu);

    CheckboxMenuItem toggleRightMenu = new CheckboxMenuItem("Toggle Right", true);
    menu.add(toggleRightMenu);

    MenuItem exitMenu = new MenuItem("Exit");
    exitMenu.addActionListener(ae -> System.exit(0));
    menu.add(exitMenu);

    icon.setPopupMenu(menu);

    USBChangeWatcher.watch(isConnected -> {
      if (toggleLeftMenu.getState()) {
        if (isConnected) {
          DisplayInputSwapper.swapToDisplayPort(x -> x < 0);
        } else {
          DisplayInputSwapper.swapToHdmi(x -> x < 0);
        }
      }
      if (toggleRightMenu.getState()) {
        if (isConnected) {
          DisplayInputSwapper.swapToDisplayPort(x -> x >= 0);
        } else {
          DisplayInputSwapper.swapToHdmi(x -> x >= 0);
        }
      }
    });
  }

}
