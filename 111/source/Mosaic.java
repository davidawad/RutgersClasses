
/* 
   The class Mosaic makes available a window made up of a grid
   of colored rectangles.  Routines are provided for opening and
   closing the window and for setting and testing the color of rectangles
   in the grid.

   Each rectangle in the grid has a color.  The color can be
   specified by red, green, and blue amounts in the range from
   0 to 255.  (It can also be given as an object belonging
   to the class Color.)

   David Eck (eck@hws.edu), 3 February 2000.
*/


import java.awt.*;
import java.awt.event.*;

public class Mosaic {

   private static Frame window;         // A mosaic window, null if no window is open.
   private static MosaicCanvas canvas;  // A component that actually manages and displays the rectangles
      
   public static void open() {
         // Open a mosaic window with a 20-by-20 grid of squares, where each
         // square is 15 pixels on a side.
      open(20,20,15,15);
   }
   
   public static void open(int rows, int columns) {
         // Open a mosaic window with the specified numbers or rows and columns
         // of squares, where each square is 15 pixels on a side.
      open(rows,columns,15,15);
   }
   
   synchronized public static void open(int rows, int columns, int blockWidth, int blockHeight) {
         // Open a window that shows a mosaic with the specified number of rows and
         // columns of rectangles.  blockWidth and blockHeight specify the
         // desired width and height of rectangles in the grid.  If a mosaic window
         // is already open, it will be closed before the new window is open.
      if (window != null)
         window.dispose();
      canvas = new MosaicCanvas(rows,columns,blockWidth,blockHeight);
      window = new Frame("Mosaic Window");
      window.add("Center",canvas);
      window.addWindowListener(
            new WindowAdapter() {  // close the window when the user clicks its close box
               public void windowClosing(WindowEvent evt) {
                  close();
               }
            });
      window.pack();
      window.setVisible(true);
   }
   
   synchronized public static void close() {
          // If there is a mosiac window, close it.
      if (window != null) {
         window.dispose();
         window = null;
         canvas = null;
      }
   }

   synchronized public static boolean isOpen() {
          // This method returns a boolean value that can be used to
          // test whether the window is still open.
      return (window != null);
   }
   
   public static void delay(int milliseconds) {
         // Calling this routine causes a delay of the specified number
         // of milliseconds in the program that calls the routine.  It is
         // provided here as a convenience.
      if (milliseconds > 0) {
        try { Thread.sleep(milliseconds); }
        catch (InterruptedException e) { }
      }
   }

   public static Color getColor(int row, int col) {
         // Returns the object of type Color that represents the color
         // of the grid in the specified row and column.
      if (canvas == null)
         return Color.black;
      return canvas.getColor(row, col);
   }

   public static int getRed(int row, int col) {
         // Returns the red component, in the range 0 to 255, of the
         // rectangle in the specified row and column.
      if (canvas == null)
         return 0;
      return canvas.getRed(row, col);
   }

   public static int getGreen(int row, int col) {
         // Returns the green component, in the range 0 to 255, of the
         // rectangle in the specified row and column.
      if (canvas == null)
         return 0;
      return canvas.getGreen(row, col);
   }

   public static int getBlue(int row, int col) {
         // Returns the blue component, in the range 0 to 255, of the
         // rectangle in the specified row and column.
      if (canvas == null)
         return 0;
      return canvas.getBlue(row, col);
   }

   public static void setColor(int row, int col, Color c) {
          // Set the rectangle in the specified row and column to have
          // the specified color.
      if (canvas == null)
         return;
      canvas.setColor(row,col,c);
   }

   public static void setColor(int row, int col, int red, int green, int blue) {
          // Set the rectangle in the specified row and column to have
          // the color with the specifed red, green, and blue components.
      if (canvas == null)
         return;
      canvas.setColor(row,col,red,green,blue);
   }

   public static void fill(Color c) {
          // Set all the rectangels in the grid to the color c.
      if (canvas == null)
         return;
      canvas.fill(c);
   }

   public static void fill(int red, int green, int blue) {
          // Set all the rectangles in the grid to the color with
          // the specified red, green, and blue components.
      if (canvas == null)
         return;
      canvas.fill(red,green,blue);
   }

   public static void fillRandomly() {
          // Sets each rectangle in the grid to a different randomly
          // chosen color.
      if (canvas == null)
         return;
      canvas.fillRandomly();
   }
   
}  // end of class Mosaic