
/*
   This applet shows short line segments arranged into regular rows and
   columns.  One end of each line if fixed, and the line can rotate around
   that endpoint.  The lines rotate at randomly selected speeds, unless
   the mouse moves over the applet.  Then all the lines point at the
   mouse.  But if the mouse doesn't move for a while, the lines start
   drifting again.  Furthermore, if the mouse button is pressed, then
   the color of the lines changes.  The color depends on the exact mouse
   position.
   
   David Eck, August 21, 1996
   eck@hws.edu
   
   Modified August 5, 1998 to use a black background.
   Note that this applet uses Java 1.0 methods and event
   handling.
*/


import java.awt.*;
import java.applet.*;

public class TrackLines extends Applet implements Runnable {

   boolean used = false; // Set to true the first time the lines
                         //    point at the mouse; until then, a
                         //    message is also displayed.

   int ROWS = 5;     // how many rows of lines; can be set as an applet parameter
   int COLUMNS = 7;  // how many columns; can be set as an applet parameter

   int[][] angle;       // current angle of each line, in degrees
   int[][] driftSpeed;  // current rotation speed of each line, in degrees per frame

   long lastTime = 0;   // the time when the lines last pointed at the mouse (milliseconds)
   int delayToDrift  = 1500;    // how long will lines point at same spot before drifting
                                //        (in milliseconds)
   
   int width = -1;   // actual width of the applet (initially unknown; set in doResize())
   int height = -1;  // actual height of the applet
   int hSpace;  // horizontal space between lines ( = width / COLUMNS)
   int vSpace;  // vertical space between lines ( = height / ROWS)
   int space;   // length of each line ( = min(hSpace,vSpace) / 2)

   int last_x = -1;  // the lines were most recently pointed at (last_x, last_y)
   int last_y = -1;

   int sleepTime = 100;   // delay between frames when lines are drifting

   Thread runner = null;   // Thread for doing the drifing animation
   Image OSC = null;       // Off-screen canvas for double buffering, created in doResize()
   Graphics OSCGraphics;   // Graphics context for OSC
   
   
   public void init() {
      setBackground(Color.black);
      setForeground(Color.white);
      String param;
      param = getParameter("rows");                  // get the parameters
      if (param != null) {
         try {
            ROWS = Integer.parseInt(param);
          }
          catch (NumberFormatException e) { }
      }
      param = getParameter("columns");
      if (param != null) {
         try {
            COLUMNS = Integer.parseInt(param);
          }
          catch (NumberFormatException e) { }
      }
      angle = new int[ROWS][COLUMNS];              // set up random initial angles and driftspeeds
      driftSpeed = new int[ROWS][COLUMNS];
      for (int i = 0; i<ROWS; i++)
         for (int j=0; j<COLUMNS; j++) {
            angle[i][j] = (int)(360*Math.random());
            driftSpeed[i][j] = (int)(41*Math.random()) - 20;
         }
   }
   
   public void start() {
             // check values of height,width and start a thread to do the animation
      if (runner == null) {
         int h = size().height;
         int w = size().width;
         if (h != height || w != width)
            doResize(w,h);
         runner = new Thread(this);
         runner.start();
      }
   }
   
   public void stop() {
            // stop the animation
      if (runner != null) {
         runner.stop();
         runner = null;
      }
   }
   

   synchronized public void paint(Graphics g) {
             // just copy the offscreen canvas to the screen
      if (OSC != null) {
         g.drawImage(OSC,0,0,this);
      }
   }
   
   public void update(Graphics g) {
             // override update(), so that it doesn't erase before calling paint()
      paint(g);
   }
   
   void doResize(int w, int h) {
            // applet has changed size to w-by-h; get a new off-screen canvas of
            // the same size, and calculate some size parameters
      OSC = null;
      OSC = createImage(w,h);
      OSCGraphics = OSC.getGraphics();
      width = w;
      height = h;
      hSpace = (width + 2) / (COLUMNS);
      vSpace = (height + 2) / (ROWS);
      space = (hSpace < vSpace) ? hSpace/2 : vSpace/2;
   }
   
   synchronized void drift() {
            // all the lines rotate a bit, but this is only done if
            // its been at least delayToDrift milliseconds since the
            // last time the lines pointed at the mouse
      if (System.currentTimeMillis() - lastTime < delayToDrift)
         return;
      int w = size().width;
      int h = size().height;
      if (w != width || h != height)
         doResize(w,h);
      OSCGraphics.setColor(getBackground());
      OSCGraphics.fillRect(0,0,w,h);
      OSCGraphics.setColor(getForeground());
      for (int i=0; i<ROWS; i++)
         for (int j=0; j<COLUMNS; j++) {
            angle[i][j] += driftSpeed[i][j];  // rotate the line a bit
            if (Math.random() < 0.05)  // a 1 out of 20 chance that drift speed changes
               driftSpeed[i][j] = (int)(41*Math.random()) - 20;
            int x = j*hSpace + space;  // fixed endpoint of line
            int y = i*vSpace + space;
            int x1 = x + (int)(space*Math.cos((Math.PI/180.0)*angle[i][j]));  // other endpoint
            int y1 = y + (int)(space*Math.sin((Math.PI/180.0)*angle[i][j]));
            OSCGraphics.drawLine(x,y,x1,y1);
         }
      if (!used)
         putMessage();
      repaint();  // arrange for screen updating
   }
   
   synchronized void point(int x1, int y1) {
            // all the lines point at (x1,y1)
      if (last_x == x1 && last_y == y1)
         return;  // if they are already pointing there, there's nothing to do
      int w = size().width;
      int h = size().height;
      if (w != width || h != height)
         doResize(w,h);
      OSCGraphics.setColor(getBackground());
      OSCGraphics.fillRect(0,0,w,h);
      OSCGraphics.setColor(getForeground());
      for (int i=0; i<ROWS; i++)
         for (int j=0; j<COLUMNS; j++) {
            int x = j*hSpace + space;  // fixed endpoint of line
            int y = i*vSpace + space;
            int dx = x1 - x;  // (dx,dy) is a vector pointing in the right direction
            int dy = y1 - y;
            if (dx != 0 || dy != 0) {              
               double angl = Math.atan2(dy,dx);   // computes angle of vector (dx,dy)
               angle[i][j] = (int)(angl*180.0/Math.PI);
               int x2 = x + (int)(space*Math.cos((Math.PI/180.0)*angle[i][j]));   // other endpoint
               int y2 = y + (int)(space*Math.sin((Math.PI/180.0)*angle[i][j]));
               OSCGraphics.drawLine(x,y,x2,y2);
            }
         }
      last_x = x1;   // save info about this event
      last_y = y1;
      lastTime = System.currentTimeMillis();
      repaint();  // arrange for screen updating
   }
   
   synchronized void selectColor(int x) {
           // change the drawing color, depending on the value of x.
           // x gives the hue of the color, ranging from violet to red.
      if (x < 0)
         x = 0;
      else if (x > width)
         x = width;
      float hue = ((float)(width-x)) / ((float)width);
      setForeground(Color.getHSBColor(hue,1.0F,1.0F));
   }
   
   void putMessage() {
          // This message is displayed until the mouse moves over the applet
      if (OSC == null)
         return;
      OSCGraphics.setColor(Color.red);
      OSCGraphics.drawString("Point your mouse at me!", 25, 40);
      OSCGraphics.drawString("Click on me!", 25, 80);
   }
   
   public boolean mouseMove(Event evt, int x, int y) {
          // point at the mouse each time it moves
      used = true;
      point(x,y);
      return true;
   }   
   
   public boolean mouseDown(Event evt, int x, int y) {
          // point at the mouse and select a color based on its position
      selectColor(x);
      last_x = -1;  // this forces the lines to point at (x,y), even if its the
                    //    same point as last time
      point(x,y);
      return true;
   }   
   
   public boolean mouseDrag(Event evt, int x, int y) {
          // point at the mouse and select a color based on its position
      used = true;
      selectColor(x);
      point(x,y);
      return true;
   }
      
   public void run() {
          // run the animation
      while (true) {
         try { Thread.sleep(sleepTime); }
         catch (InterruptedException e) { }
         drift();
      }
   }   

}