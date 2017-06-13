
/*
   This code is, for the moment, totally uncommented.  Sorry.
   
   David Eck
   Department of Mathematics and Computer Science
   Hobart and William Smith Colleges
   Geneva, NY   14456
   E-mail:  eck@hws.edu
   WWW:     http://math.hws.edu/eck/
   
   June 18, 1996
   
   NOTE:  YOU CAN DO ANYTHING YOU WANT WITH THIS CODE, EXCEPT COPYRIGHT IT,
          PATENT IT, OR OTHERWISE TRY TO CLAIM CREDIT FOR IT.
          
*/

import java.awt.*;
import java.util.Random;

public class SimpleCA extends java.applet.Applet implements Runnable {

   CACanvas CA;
   Thread runner = null;
   
   public void init() {
      setLayout(new BorderLayout());
      CA = new CACanvas();
      add("Center",CA);
      setBackground(Color.black);
   }
   
   public Insets insets() {
      return new Insets(2,2,2,2);
   }
   
   public void run() {
      CA.properties(2,3,null,null,true);
      CA.set(null);
      while (true) {
         CA.next();
         try { Thread.sleep(100); }
         catch (InterruptedException e) { }
      }
   }
   
   public void start() {
      if (runner == null) {
         runner = new Thread(this);
         runner.start();
      }
   }
   
   public void stop() {
      if (runner != null) {
         runner.stop();
         runner = null;
         CA.disposeOSC();
     }
   }
   
   public boolean mouseDown(Event evt, int x, int y) {
      if (evt.shiftDown())
         CA.reset();
      CA.next();
      return true;
   }
   
}
   
