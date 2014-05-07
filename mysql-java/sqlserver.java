
import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;
import java.io.*;
import java.util.*;


///////////////////////////// Mutlithreaded Server /////////////////////////////

public class sqlserver
{
   final static int port = 9999;

   static void printUsage() {
   	System.out.println("In another window type:");
   	System.out.println("telnet sslabXX.cs.purdue.edu " + port);
	System.out.println("UPDATE-HS|user|password|table|name|moves|time");
	System.out.println("GET-HS-TIME|user|password|table");
	System.out.println("GET-HS-MOVES|user|password|table");
	System.out.println("table: 3-easy 5-medium 7-hard 10-dw");
	System.out.println();
   }

   public static void main(String[] args )
   {  
      try
      {  
         printUsage();
         int i = 1;
         ServerSocket s = new ServerSocket(port);
	 while (true)
         {  
            Socket incoming = s.accept();
            System.out.println("Spawning " + i);
            Runnable r = new ThreadedHandler(incoming);
            Thread t = new Thread(r);
            t.start();
            i++;
         }
      }
      catch (IOException e)
      {  
         e.printStackTrace();
      }
   }
}

/**
   This class handles the client input for one server socket connection. 
*/
class ThreadedHandler implements Runnable
{ 
   final static String ServerUser = "root";
   final static String ServerPassword = "jpic";

   public ThreadedHandler(Socket i)
   { 
      incoming = i; 
   }

   public static Connection getConnection() throws SQLException, IOException
   {
      Properties props = new Properties();
      FileInputStream in = new FileInputStream("database.properties");
      props.load(in);
      in.close();
      String drivers = props.getProperty("jdbc.drivers");
      if (drivers != null)
        System.setProperty("jdbc.drivers", drivers);
      String url = props.getProperty("jdbc.url");
      String username = props.getProperty("jdbc.username");
      String password = props.getProperty("jdbc.password");

      System.out.println("url="+url+" user="+username+" password="+password);

      return DriverManager.getConnection( url, username, password);
   }

   void updateHS( String [] args, PrintWriter out){
   		String difficulty = args[3];
   		
   		String table = null;
   		
   		if(difficulty.equals("3")){table = "easy";}
   		else if(difficulty.equals("5")){table = "medium";}
   		else if(difficulty.equals("7")){table = "hard";}
   		else if(difficulty.equals("10")){table = "dw";}
   		else{return;}
   		
		Connection conn = null;
		try{
			conn = getConnection();
			Statement stat = conn.createStatement();
			stat.executeUpdate("USE jpic");
			stat.executeUpdate("INSERT INTO "+table+" VALUES(\'"+args[4]+"\' , "+args[5]+" , "+args[6]+");");
		}
		catch (Exception e) {
			System.out.println(e.toString());
		}
		finally{
			try {
				if (conn!=null) conn.close();
			}
			catch (Exception e) {}
		}
   }
   
   /*void updatePicture( String [] args, PrintWriter out){
   		String table = args[3];
		Connection conn = null;
		try{
			conn = getConnection();
			Statement stat = conn.createStatement();
			stat.executeUpdate("USE jpic");
			stat.executeUpdate("INSERT INTO "+table+" (name, picture) VALUES");
		}
		catch (Exception e) {
			System.out.println(e.toString());
		}
		finally{
			try {
				if (conn!=null) conn.close();
			}
			catch (Exception e) {}
		}
   }*/
   
   void getHSTime( String [] args, PrintWriter out){
   		String difficulty = args[3];
   		
   		String table = null;
   		
   		if(difficulty.equals("3")){table = "easy";}
   		else if(difficulty.equals("5")){table = "medium";}
   		else if(difficulty.equals("7")){table = "hard";}
   		else if(difficulty.equals("10")){table = "dw";}
   		else{return;}
   		
		Connection conn = null;
		try{
			conn = getConnection();
			Statement stat = conn.createStatement();
			ResultSet result = stat.executeQuery( "SELECT * FROM " +table+ " ORDER BY time LIMIT 5;");
			
			while(result.next()) {
       			out.print(result.getString(1)+"|");
       			out.print(result.getString(2)+"|");
       			out.print(result.getString(3)+"|");
				out.println("");
			}
			result.close();
		}
		catch (Exception e) {
			System.out.println(e.toString());
		}
		finally{
			try {
				if (conn!=null) conn.close();
			}
			catch (Exception e) {}
		}
   }
   
   void getHSMoves( String [] args, PrintWriter out){
   		String difficulty = args[3];
   		
   		String table = null;
   		
   		if(difficulty.equals("3")){table = "easy";}
   		else if(difficulty.equals("5")){table = "medium";}
   		else if(difficulty.equals("7")){table = "hard";}
   		else if(difficulty.equals("10")){table = "dw";}
   		else{return;}
   		
		Connection conn = null;
		try{
			conn = getConnection();
			Statement stat = conn.createStatement();
			ResultSet result = stat.executeQuery( "SELECT * FROM " +table+ " ORDER BY moves LIMIT 5;");
			
			while(result.next()) {
       			out.print(result.getString(1)+"|");
       			out.print(result.getString(2)+"|");
       			out.print(result.getString(3)+"|");
				out.println("");
			}
			result.close();
		}
		catch (Exception e) {
			System.out.println(e.toString());
		}
		finally{
			try {
				if (conn!=null) conn.close();
			}
			catch (Exception e) {}
		}
   }

   void handleRequest( InputStream inStream, OutputStream outStream) {
        Scanner in = new Scanner(inStream);         
        PrintWriter out = new PrintWriter(outStream, true /* autoFlush */);

	// Get parameters of the call
	String request = in.nextLine();

	System.out.println("Request="+request);

	String requestSyntax = "Syntax: COMMAND|USER|PASSWORD|OTHER|ARGS";

	try {
		// Get arguments.
		// The format is COMMAND|USER|PASSWORD|OTHER|ARGS...
		String [] args = request.split("\\|");

		// Print arguments
		for (int i = 0; i < args.length; i++) {
			System.out.println("Arg "+i+": "+args[i]);
		}

		// Get command and password
		String command = args[0];
		String user = args[1];
		String password = args[2];

		// Check user and password. Now it is sent in plain text.
		// You should use Secure Sockets (SSL) for a production environment.
		if ( !user.equals(ServerUser) || !password.equals(ServerPassword)) {
			System.out.println("Bad user or password");
			out.println("Bad user or password");
			return;
		}

		//do operation
		if (command.equals("UPDATE-HS")) {
			updateHS(args, out);
		}
		/*else if (command.equals("UPDATE-PICTURE")){
			updatePicture(args, out);
		}*/
		/*else if (command.equals("GET-PICTURE")){
			getPicture(args, out);
		}*/
		else if (command.equals("GET-HS-TIME")){
			getHSTime(args, out);
		}
		else if (command.equals("GET-HS-MOVES")){
			getHSMoves(args, out);
		}
		
		
	}
	catch (Exception e) {		
		System.out.println(requestSyntax);
		out.println(requestSyntax);

		System.out.println(e.toString());
		out.println(e.toString());
	}
   }

   public void run() {  
      try
      {  
         try
         {
            InputStream inStream = incoming.getInputStream();
            OutputStream outStream = incoming.getOutputStream();
	    	handleRequest(inStream, outStream);

         }
      	 catch (IOException e)
         {  
            e.printStackTrace();
         }
         finally
         {
            incoming.close();
         }
      }
      catch (IOException e)
      {  
         e.printStackTrace();
      }
   }

   private Socket incoming;
}
