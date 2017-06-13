
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * BuddyChat client program.  When started, it puts up a window to get info
 * from the user.  It then connects to a BuddyChatServer.  It puts up a new
 * window that displays the list of clients available on the server; the user
 * can select one of these client and open a chat connection to that user.
 * Furthremore, the BuddyChat program listens for incoming connection requests
 * from other clients of the server.  A new window, of type BuddyChatWindow,
 * is opened for each incoming or outgoing connection.  If the user closes this
 * window (or if it closes because the connection to the server dies), the user
 * can no longer open new connections, but connections that are in place will
 * continue and the program will not end until all BuddyChatWindows are closed.
 * <p>This program maintains a connection to the server as long as it is
 * running (unless the connection closes becuase of error or because the
 * server shuts down).  It gets a notice from the server whenever a client
 * is added to or removed from the list of clients registered with the
 * server, and the list that is displayed to the user is modified accordingly.
 */
public class BuddyChat {

   private static final String DEFAULT_SERVER_HOST = "localhost";
   private static final int DEFAULT_SERVER_PORT = 12001;
   
   private static Socket connectionToServer;
   private static ServerSocket listeningSocket;
   private static String secret;  // This client's secret, provided by the server.
   private static String handle;  // This client's handle.
   
   private static boolean running;  // Is the connection to the server still in place?


   /**
    * The main routine just creates a window of type IntroWindow, which
    * is a static nested class.  That window gets info from the user
    * and does all remaining setup.
    */
   public static void main(String[] args) {
      new IntroWindow();
   }
   
   
   /**
    * Returns true if the client list window is still open.
    * This is called by BuddyChatWindow when the last window
    * of that type closes, to see whether the program should end.
    * If the client list window is still open, the program does not end.
    */
   public static boolean isRunning() {
      return running;
   }
   
   
   /**
    * A window of this type is opened by the main program. It gets
    * the host name (or ip) of the server, the port on which the server
    * is listening, and the "handle" that will identify this user
    * in the list of clients on the server.  The user can click
    * "Cancel" or "Connect".  If the user clicks "Connect", tries
    * to establishe a connection to the server.  If that succeeds,
    * the IntroWindow is closed and a window of type BuddyListWindow
    * is opened.
    */
   private static class IntroWindow extends JFrame implements ActionListener {

      JButton connectButton, cancelButton;

      JTextField serverInput, portInput, handleInput;  // For getting info from user.

      /**
       * Constructor shows a window with input boxes for server name/ip, 
       * server port number, and user's handle.  All boxes have default
       * values filled in.
       */
      IntroWindow() {
         super("Connect to BuddyChat server...");
         cancelButton = new JButton("Cancel");
         cancelButton.addActionListener(this);
         connectButton = new JButton("Connect");
         connectButton.addActionListener(this);
         serverInput = new JTextField(DEFAULT_SERVER_HOST, 18);
         portInput = new JTextField("" + DEFAULT_SERVER_PORT, 5);
         handleInput = new JTextField("user" + (int)(10000*Math.random()), 10);
         JPanel content = new JPanel();
         setContentPane(content);
         content.setBorder(BorderFactory.createLineBorder(Color.GRAY,3));
         content.setLayout(new GridLayout(4,1,3,3));
         content.setBackground(Color.GRAY);
         JPanel row;
         row = new JPanel();
         row.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
         row.add(new JLabel("Server name or IP:"));
         row.add(serverInput);
         content.add(row);
         row = new JPanel();
         row.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
         row.add(new JLabel("Port number on server:"));
         row.add(portInput);
         content.add(row);
         row = new JPanel();
         row.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
         row.add(new JLabel("Your \"handle\":"));
         row.add(handleInput);
         content.add(row);
         row = new JPanel();
         row.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
         row.add(cancelButton);
         row.add(connectButton);
         content.add(row);
         pack();
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         setLocation( (screenSize.width - getWidth())/2, (screenSize.height - getHeight())/2);
         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         setVisible(true);
         handleInput.selectAll();
         handleInput.requestFocus();
      }
      
      /**
       * Respond to "Cancel" button by ending program, and to
       * "Connect" button by calling the doConnect() method.
       */
      public void actionPerformed(ActionEvent evt) {
         if (evt.getSource() == cancelButton)
            System.exit(0);
         else if (evt.getSource() == connectButton || evt.getSource() == handleInput)
            doConnect();
      }
      
      /**
       * Try to establish a connection to the server using info from
       * the window.  If this fails, a message box is popped up to 
       * inform the user, and the IntroWindow stays on the screen.  If
       * it succeeds, the IntroWindow is closed and a BuddyListWindow
       * is opened.
       */
      void doConnect() {
         String server = serverInput.getText().trim();
         if (server.length() == 0) {
            JOptionPane.showMessageDialog(this,"Server name can't be empty.");
            return;
         }
         int port;
         try {
            port = Integer.parseInt(portInput.getText());
            if (port <= 0 || port > 65525)
               throw new NumberFormatException();
         }
         catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,"Illegal port number.");
            return;
         }
         handle = handleInput.getText().trim();
         if (handle.length() == 0) {
            JOptionPane.showMessageDialog(this,"Handle can't be empty.");
            return;
         }
         try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            connectionToServer = new Socket(server,port);
            PrintWriter out = new PrintWriter(connectionToServer.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(
                                       connectionToServer.getInputStream()));
            out.println("BuddyChatClient");
            out.flush();
            if (out.checkError())
               throw new Exception("Error while sending identification info to server.");
            String input = in.readLine();
            if (! "BuddyChatServer".equals(input))
               throw new Exception("Server did not properly identify itself.");
            out.println(handle);
            listeningSocket = new ServerSocket(0);  // For accepting chat connection requests
            out.println(listeningSocket.getLocalPort());
            out.flush();
            if (out.checkError())
               throw new Exception("Error while sending identification info to server.");
            secret = in.readLine();
            if (secret == null)
               throw new Exception("Connection closed unexpectedly by server.");
            new BuddyListWindow(in,out);
            dispose();
         }
         catch (Exception e) {
            if (listeningSocket != null) {
               try {
                  listeningSocket.close();
               }
               catch (Exception e2) {
               }
            }
            setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this,"Can't open connection to server:\n" + e);
         }
      }
      
   } // end nested class IntroWindow
   

   
   /**
    * A window that displays the client list from the BuddyChatServer.
    * The user can select one of these to connect to.  The user can click
    * a button in the window to close all windows (including BuddyChatWindows)
    * and end the program.  If the user just closes the BuddyListWindow
    * by clicking its closed box, existing BuddyChatWindows remain open.
    * The window maintains three threads: One for listening for connection
    * requests from other clients, one for sending messages to the BuddyChatServer,
    * and one for reading messages from the BuddyChatServer.
    * Note that the sockets used for listening and for the connection to
    * the server are static member variables in the main BuddyChat class.
    * This client's handle and secret are also static member variables in BuddyChat.
    */
   private static class BuddyListWindow extends JFrame 
                                 implements ActionListener, ListSelectionListener {
      
      JButton connectButton;
      JButton closeButton;
      
      JList buddyList;     // Holds the list of clients.
      volatile ArrayList<ClientInfo> clientInfo; // List of clients shown in the buddyList.
      
      PrintWriter out;    // For communicating with server.
      BufferedReader in;

      Thread readerThread;
      Thread writerThread;
      Thread listeningThread;
      volatile boolean closed;  // Set to true when window and connection to server close.
      volatile long lastRefreshTime;  // Time when client list was last modified.
      
      
      /**
       * Construct the BuddyListWindow and create its three threads.  The connection
       * to the server has already been opened by the IntroWindow before this
       * constructor is called.  Also, the listening socket for accepting connection
       * requests from other clients has also been opened.
       * @param in  stream for sending messages to the BuddyChatServer
       * @param out stream for reading message from the BuddyChatServer
       */
      BuddyListWindow(BufferedReader in, PrintWriter out) {
         super("BuddyChat: " + handle);
         this.in = in;
         this.out = out;
         connectButton = new JButton("Connect to Selected Buddy");
         connectButton.addActionListener(this);
         connectButton.setEnabled(false);
         closeButton = new JButton("Close all Windows and Quit");
         closeButton.addActionListener(this);
         buddyList = new JList();
         buddyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         buddyList.addListSelectionListener(this);
         JPanel content = new JPanel();
         content.setBackground(Color.GRAY);
         content.setBorder(BorderFactory.createLineBorder(Color.GRAY,3));
         content.setLayout(new BorderLayout(3,3));
         content.add( new JScrollPane(buddyList), BorderLayout.CENTER);
         JPanel bottom = new JPanel();
         bottom.setBackground(Color.GRAY);
         bottom.setLayout(new GridLayout(2,1,3,3));
         bottom.add(connectButton);
         bottom.add(closeButton);
         content.add(bottom, BorderLayout.SOUTH);
         setContentPane(content);
         pack();
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         setLocation( screenSize.width - getWidth() - 50, 50);
         setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
         addWindowListener( new WindowAdapter() {
               // Close down the connection to the server and the listener
               // socket when the user closes the window.  Also, program
               // will end if there are not BuddyChatWindows open.
            public void windowClosing(WindowEvent evt) {
               closeConnectionToServer();
            }
         });
         setVisible(true);
         readerThread = new ReaderThread();
         writerThread = new WriterThread();
         listeningThread = new ListeningThread();
         readerThread.start();
         writerThread.start();
         listeningThread.start();
      }
      
      /**
       * Responds to user click on "Connect to Selected Buddy" and
       * "Close all Windows and Quit" buttons.
       */
      public void actionPerformed(ActionEvent evt) {
         if (evt.getSource() == closeButton) {
            BuddyChatWindow.closeAll();
            closeConnectionToServer();
         }
         else if (evt.getSource() == connectButton) {
            doConnect();
         }
      }

      /**
       * Called when the selected item in the buddy list changes.  The
       * only reason for this method is to enable/disable the
       * "Connect to Selected Buddy" button, depending on whether 
       * or not one of the list items is selected.
       */
      public void valueChanged(ListSelectionEvent e) {
         int selectedIndex = buddyList.getSelectedIndex();
         connectButton.setEnabled( selectedIndex >= 0 );
      }

      /**
       * Holds the information about one client in the client list.
       * Constructing a ClientInfo object will throw an exception if
       * the info string is not of the correct form.  It should have the
       * form  handle~ip~port~secret.  The ip and port tell this program
       * where connection requests to that user should be sent.  The secret
       * has to be presented to the client in order to open the connection.
       */
      class ClientInfo {
         String info;    // The client's info string, as received from the server.
         String handle;  // The client's handle, read from the info string.
         String ip;      // The client's ip address, read from the info string.
         int port;       // The client's port number, read from the info string.
         String secret;  // The client's secret, read from the info string.
         ClientInfo(String info) {
            this.info = info;
            Scanner scanner = new Scanner(info); // For parsing the info string.
            scanner.useDelimiter("~"); // Pieces of info string are separated by "~".
            handle = scanner.next();
            ip = scanner.next();
            port = Integer.parseInt(scanner.next());
            secret = scanner.next();
         }
      }
      
      
      /**
       * Install a new client list in the BuddyListWindow.  This is called
       * whenever new information is received from the BuddyListServer.
       */
      synchronized void setClientList(ArrayList<ClientInfo> clientInfo) {
         this.clientInfo = clientInfo;
         String[] listStrings = new String[clientInfo.size()];
         for (int i = 0; i < listStrings.length; i++) {
            ClientInfo info = clientInfo.get(i);
            listStrings[i] = info.handle + " (" + info.ip + ")";
         }
         buddyList.setListData(listStrings);
      }
      
      /**
       * Add a client to the list.  This is called when an "addclient" message
       * is received from the server.
       */
      synchronized void addClient(ClientInfo info) {
         if (clientInfo == null) {
            ArrayList<ClientInfo> client = new ArrayList<ClientInfo>();
            client.add(info);
            setClientList(client);
         }
         else {
            clientInfo.add(info);
            setClientList(clientInfo);
         }
      }
      
      /**
       * Remove a client from the list.  This is called when a "removeclient" message
       * is received from the server.
       * @param info the info string for the client that was removed; this is used
       * to find the client in the list of ClientInfo.
       */
      synchronized void removeClient(String info) {
         if (clientInfo == null)
            return;
         for (int i = 0; i < clientInfo.size(); i++) {
            if (info.equals(clientInfo.get(i).info)) {
               clientInfo.remove(i);
               setClientList(clientInfo);
               return;
            }
         }
      }
      
      /**
       * Respond to a user request to open a connection to one of the clients
       * in the list.  This just opens a BuddyChatWindow with the appropriate
       * information to do all the work of opening and manageing the connection.
       */
      synchronized void doConnect() {
         int selectedIndex = buddyList.getSelectedIndex();
         if (selectedIndex < 0)
            return;
         buddyList.clearSelection();
         ClientInfo info = clientInfo.get(selectedIndex);
         new BuddyChatWindow(info.ip, info.port, handle, info.handle, info.secret);
      }

      /**
       * Shuts down the BuddyListWindow, the connection to the server,
       * and the listening socket.  The three threads associated with the
       * window will also be closed, and the program will terminate if there
       * are no BuddyListWindows open.  This method is called when the user
       * closes the BuddyListWindow or if the connection to the server shuts
       * down for any reason.
       */
      void closeConnectionToServer() {
         closed = true;
         running = false;
         dispose();
         try {
            listeningSocket.close();  // Also causes listening thread to exit.
         }
         catch (Exception e) {
         }
         try {
            connectionToServer.close();  // Also causes writer thread to exit.
         }
         catch (Exception e) {
         }
         synchronized(writerThread) {
            writerThread.notify();  // Wake up writer thread so it can exit.
         }
         try {
            Thread.sleep(1000);  // Give time for everything to shut down.
         }
         catch (InterruptedException e) {
         }
         if (BuddyChatWindow.openWindowCount() == 0)
            System.exit(0);
      }
      
      /**
       * Defines the thread that listens for connection requests from other clients.
       * When a connection request is received, this thread just opens a
       * BuddyChatWindow to handle the connection.
       */
      class ListeningThread extends Thread {
         public void run() {
            try {
               while (! closed) {
                  Socket socket = listeningSocket.accept();
                  new BuddyChatWindow(socket,secret);
               }
            }
            catch (Exception e) {
               if (! closed) {
                  JOptionPane.showMessageDialog(BuddyListWindow.this,
                        "Listening socket has closed because of an error.\n" +
                        "Can no longer accept incoming connection requests!\n" +
                        "Error: " + e);
               }
            }
         }
      }
      
      /**
       * Defines the thread that reads messages from the server and 
       * responds to them.
       */
      class ReaderThread extends Thread {
         public void run() {
            try {
               while (!closed) {
                  String command = in.readLine();
                  if (command == null)
                     throw new Exception();
                  else if (command.equals("addclient")) {  // A client was added.
                     String info = in.readLine();
                     addClient(new ClientInfo(info));
                     lastRefreshTime = System.currentTimeMillis();
                  }
                  else if (command.equals("removeclient")) { // A client was removed.
                     String info = in.readLine();
                     removeClient(info);
                     lastRefreshTime = System.currentTimeMillis();
                  }
                  else if (command.equals("clients")) { // Complete client list.
                     ArrayList<ClientInfo> clients = new ArrayList<ClientInfo>();
                     while (true) {
                        String line = in.readLine();
                        if (line.equals("endclients"))
                           break;
                        clients.add(new ClientInfo(line));
                     }
                     setClientList(clients);
                     lastRefreshTime = System.currentTimeMillis();
                  }
                  else if (command.equals("ping") || command.equals("pingresponse")) { // ignored
                  }
                  else
                     throw new Exception("Illegal data");
               }
            }
            catch (Exception e) {
               if (! closed) {
                  closed = true;
                  JOptionPane.showMessageDialog(BuddyListWindow.this,
                        "Error occurred while reading from BuddyChatServer.\n" +
                        "New connections are no longer possible.");
                  closeConnectionToServer();
               }
            }
         }
      }

      /**
       * Defines the thread that sends messages to the server.  Note that none
       * of these messages are strictly necessary.  A "ping" is sent occasionally
       * to make sure the connection is still open.  Also, a 'refresh'  message
       * is sent occasionally.  This requests that the server sends a new copy
       * of the complete list of clients; this is done only because I don't completely
       * trust that the add/remove mechanism will always work perfectly.
       */
      class WriterThread extends Thread {
         public void run() {
            try {
               while (!closed) {
                  synchronized(this) {
                     try {
                        wait(10*60*1000); // Wait 10 minutes or until notify() is called.
                     }
                     catch (InterruptedException e) {
                     }
                     if (! closed) {
                        String send;
                        if (System.currentTimeMillis() - lastRefreshTime > 25*60*1000)
                           send = "refresh";
                        else
                           send = "ping";
                        out.println(send);
                        out.flush();
                        if (out.checkError())
                           throw new Exception();
                     }
                  }
               }
            }
            catch (Exception e) {
               if (! closed) {
                  closed = true;
                  JOptionPane.showMessageDialog(BuddyListWindow.this,
                        "Error occurred while sending to BuddyChatServer.\n" +
                        "New connections are no longer possible.");
                  closeConnectionToServer();
               }
            }
         }
      }

   } // end nested class BuddyListWindow
   

}
