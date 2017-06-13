
// class MosaicCanvas is for use with MosaicFrame or MosaicApplet;
// see those classes for details.
//
// David Eck (eck@hws.edu), 17 January 1998
// (Note added July 24, 1998:  This class uses two methods that are
//  deprecated in Java 1.1:  preferredSize and size.  This seems harmless,
//  but for full compliance with Java 1.1 the names should be changed to
//  getPreferredSize and getSize.


import java.awt.*;

class MosaicCanvas extends Canvas {

   protected int rows, columns;
   protected int blockWidth, blockHeight;

   protected Color defaultColor = Color.black;

   private Color[][] grid;
   private boolean blemished = false;

   public MosaicCanvas(int rows, int columns) {
      this(rows,columns,0,0);
   }

   public MosaicCanvas(int rows, int columns, int blockWidth, int blockHeight) {
      this.rows = rows;
      this.columns = columns;
      this.blockWidth = blockWidth;
      this.blockHeight = blockHeight;
      grid = new Color[rows][columns];
      for (int i = 0; i < rows; i++)
         for (int j = 0; j < columns; j++)
            grid[i][j] = defaultColor;
      setBackground(defaultColor);
   }

   public Dimension preferredSize() {
      if (blockWidth <= 0)
         blockWidth = 10;
      if (blockHeight <= 0)
         blockHeight = 10;
      return new Dimension(columns*blockWidth, rows*blockHeight);
         // fixed July 24, 1998: order of parameters was reversed.
   }

   public synchronized void paint(Graphics g) {
      if (!blemished) {
         g.setColor(grid[0][0]);
         g.fillRect(0,0,size().width,size().height);
      }
      else {
         double rowHeight = (double)size().height / rows;
         double colWidth = (double)size().width / columns;
         for (int i = 0; i < rows; i++) {
            int y = (int)Math.round(rowHeight*i);
            int h = (int)Math.round(rowHeight*(i+1)) - y;
            for (int j = 0; j < columns; j++) {
               int x = (int)Math.round(colWidth*j);
               int w = (int)Math.round(colWidth*(j+1)) - x;
               g.setColor(grid[i][j]);
               g.fillRect(x,y,w,h);
            }
         }
         
      }
   }

   public synchronized void drawSquare(int row, int col) {
      double rowHeight = (double)size().height / rows;
      double colWidth = (double)size().width / columns;
      int y = (int)Math.round(rowHeight*row);
      int h = (int)Math.round(rowHeight*(row+1)) - y;
      int x = (int)Math.round(colWidth*col);
      int w = (int)Math.round(colWidth*(col+1)) - x;
      Graphics g = getGraphics();
      g.setColor(grid[row][col]);
      g.fillRect(x,y,w,h);
   }

   public void update(Graphics g) {
      paint(g);
   }

   public Color getColor(int row, int col) {
      if (row >=0 && row < rows && col >= 0 && col < columns)
         return grid[row][col];
      else
         return defaultColor;
   }

   public int getRed(int row, int col) {
      return getColor(row,col).getRed();
   }

   public int getGreen(int row, int col) {
      return getColor(row,col).getGreen();
   }

   public int getBlue(int row, int col) {
      return getColor(row,col).getBlue();
   }

   public void setColor(int row, int col, Color c) {
      if (row >=0 && row < rows && col >= 0 && col < columns && c != null) {
         grid[row][col] = c;
         blemished = true;
         drawSquare(row,col);
      }
   }

   public void setColor(int row, int col, int red, int green, int blue) {
      if (row >=0 && row < rows && col >= 0 && col < columns) {
         red = (red < 0)? 0 : ( (red > 255)? 255 : red);
         green = (green < 0)? 0 : ( (green > 255)? 255 : green);
         blue = (blue < 0)? 0 : ( (blue > 255)? 255 : blue);
         grid[row][col] = new Color(red,green,blue);
         drawSquare(row,col);
         blemished = true;
      }
   }

   public void fill(Color c) {
      if (c == null)
         return;
      for (int i = 0; i < rows; i++)
         for (int j = 0; j < columns; j++)
            grid[i][j] = c;
      blemished = false;
      repaint();      
   }

   public void fill(int red, int green, int blue) {
      red = (red < 0)? 0 : ( (red > 255)? 255 : red);
      green = (green < 0)? 0 : ( (green > 255)? 255 : green);
      blue = (blue < 0)? 0 : ( (blue > 255)? 255 : blue);
      fill(new Color(red,green,blue));
   }

   public void fillRandomly() {
      for (int i = 0; i < rows; i++)
         for (int j = 0; j < columns; j++) {
            int r = (int)(256*Math.random());
            int g = (int)(256*Math.random());
            int b = (int)(256*Math.random());
            grid[i][j] = new Color(r,g,b);
      }
      blemished = true;
      repaint();
   }

   public void delay(int milliseconds) {
      if (milliseconds > 0) {
         try { Thread.sleep(milliseconds); }
         catch (InterruptedException e) { }
      }
   }

}