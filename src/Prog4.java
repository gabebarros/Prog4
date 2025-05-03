import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Prog4 {
		
	private static void addMember(String fname, String lname, String email, Date dob, String phone,
			String emergency, int status) {
		
		final String oracleURL =   // Magic lectura -> aloe access spell
                "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
	
		
		String username = "gabebarros",    // Oracle DBMS username
		       password = "a7693";    // Oracle DBMS password
		
		    // load the (Oracle) JDBC driver by initializing its base
		    // class, 'oracle.jdbc.OracleDriver'.
		
		try {
		
		        Class.forName("oracle.jdbc.OracleDriver");
		
		} catch (ClassNotFoundException e) {
		
		        System.err.println("*** ClassNotFoundException:  "
		            + "Error loading Oracle JDBC driver.  \n"
		            + "\tPerhaps the driver is not on the Classpath?");
		        System.exit(-1);
		
		}
		
		    // make and return a database connection to the user's
		    // Oracle database
		
		Connection dbconn = null;
		
		try {
		        dbconn = DriverManager.getConnection
		                       (oracleURL,username,password);
		
		} catch (SQLException e) {
		
		        System.err.println("*** SQLException:  "
		            + "Could not open JDBC connection.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());
		        System.exit(-1);
		
		}
		
		    // Send the query to the DBMS, and get and display the results
		
		Statement stmt = null;
		ResultSet rs = null;
		int answer;
		int mid = 0;
		
		try { 
            String query = "SELECT max(mid) FROM bhousmans.member";
            stmt = dbconn.createStatement();
		    rs = stmt.executeQuery(query);
		    
		    if (rs.next()) {
		        mid = rs.getInt(1) + 1;  
		    } 
		    else {
		        mid = 1;
		    }		    
		} catch (SQLException e) {
		
		        System.err.println("*** SQLException:  "
		            + "Could not fetch query results.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());
		        System.exit(-1);
		
		}
		
		try { 
            String query =       // our test query
            		"INSERT INTO bhousmans.member" + " (mid, firstname, lastname, email, dob, phone, emergency, status) " 
            		+ "VALUES (" + mid + ",'" + fname + "', '" + lname + "' , '" + email + "' , " + "TO_DATE('" + dob + "', 'YYYY-MM-DD')"
            		+ ", '" + phone + "' , '" + emergency + "', " + status + ")";

            stmt = dbconn.createStatement();
		    answer = stmt.executeUpdate(query);
	            
            System.out.println("Member inserted");
		
		        // Shut down the connection to the DBMS.
		
		    stmt.close();  
		    dbconn.close();
		
		} catch (SQLException e) {
		
		        System.err.println("*** SQLException:  "
		            + "Could not fetch query results.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());
		        System.exit(-1);
		
		}
		
	}
	
	private static void updateMember(String email, String category, String change) {
		
		final String oracleURL =   // Magic lectura -> aloe access spell
                "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
	
		
		String username = "gabebarros",    // Oracle DBMS username
		       password = "a7693";    // Oracle DBMS password
		
		    // load the (Oracle) JDBC driver by initializing its base
		    // class, 'oracle.jdbc.OracleDriver'.
		
		try {
		
		        Class.forName("oracle.jdbc.OracleDriver");
		
		} catch (ClassNotFoundException e) {
		
		        System.err.println("*** ClassNotFoundException:  "
		            + "Error loading Oracle JDBC driver.  \n"
		            + "\tPerhaps the driver is not on the Classpath?");
		        System.exit(-1);
		
		}
		
		    // make and return a database connection to the user's
		    // Oracle database
		
		Connection dbconn = null;
		
		try {
		        dbconn = DriverManager.getConnection
		                       (oracleURL,username,password);
		
		} catch (SQLException e) {
		
		        System.err.println("*** SQLException:  "
		            + "Could not open JDBC connection.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());
		        System.exit(-1);
		
		}
		
		    // Send the query to the DBMS, and get and display the results
		
		Statement stmt = null;
		int answer;
		
		try { 
            String query =       // our test query
            		"UPDATE bhousmans.member" 
            		+ " SET " + category + "='" + change + "'"
            		+ " WHERE email='" + email + "'";

            stmt = dbconn.createStatement();
		    answer = stmt.executeUpdate(query);
	            
            System.out.println("Member updated");
		
		        // Shut down the connection to the DBMS.
		
		    stmt.close();  
		    dbconn.close();
		
		} catch (SQLException e) {
		
		        System.err.println("*** SQLException:  "
		            + "Could not fetch query results.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());
		        System.exit(-1);
		
		}
		
	}
	
private static void deleteMember(String email) {
		
		final String oracleURL =   // Magic lectura -> aloe access spell
                "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
	
		
		String username = "gabebarros",    // Oracle DBMS username
		       password = "a7693";    // Oracle DBMS password
		
		    // load the (Oracle) JDBC driver by initializing its base
		    // class, 'oracle.jdbc.OracleDriver'.
		
		try {
		
		        Class.forName("oracle.jdbc.OracleDriver");
		
		} catch (ClassNotFoundException e) {
		
		        System.err.println("*** ClassNotFoundException:  "
		            + "Error loading Oracle JDBC driver.  \n"
		            + "\tPerhaps the driver is not on the Classpath?");
		        System.exit(-1);
		
		}
		
		    // make and return a database connection to the user's
		    // Oracle database
		
		Connection dbconn = null;
		
		try {
		        dbconn = DriverManager.getConnection
		                       (oracleURL,username,password);
		
		} catch (SQLException e) {
		
		        System.err.println("*** SQLException:  "
		            + "Could not open JDBC connection.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());
		        System.exit(-1);
		
		}
		
		    // Send the query to the DBMS, and get and display the results
		
		Statement stmt = null;
		int answer;
		int mid = getMidByEmail(email);
		
		if (!memberCanBeDeleted(mid)) {
			System.out.println("Member cannot be deleted because there are active ski passes, rental records, or lesson sessions."
					+ " Please complete or cancel these before deleting your membership.");
			return;
		}
		
		try { 
            String query =       // our test query
            		"DELETE FROM bhousmans.member" 
            		+ " WHERE mid=" + mid;

            stmt = dbconn.createStatement();
		    answer = stmt.executeUpdate(query);
		    	            
            System.out.println("Member deleted");
		
		        // Shut down the connection to the DBMS.
		
		    stmt.close();  
		    dbconn.close();
		
		} catch (SQLException e) {
		
		        System.err.println("*** SQLException:  "
		            + "Could not fetch query results.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());
		        System.exit(-1);
		
		}
		
	}

	private static int getMidByEmail(String email) {
		
		final String oracleURL =   // Magic lectura -> aloe access spell
	            "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
	
		String username = "gabebarros",    // Oracle DBMS username
		       password = "a7693";    // Oracle DBMS password
		
		    // load the (Oracle) JDBC driver by initializing its base
		    // class, 'oracle.jdbc.OracleDriver'.
		
		try {
		
		        Class.forName("oracle.jdbc.OracleDriver");
		
		} catch (ClassNotFoundException e) {
		
		        System.err.println("*** ClassNotFoundException:  "
		            + "Error loading Oracle JDBC driver.  \n"
		            + "\tPerhaps the driver is not on the Classpath?");
		        System.exit(-1);
		
		}
		
		    // make and return a database connection to the user's
		    // Oracle database
		
		Connection dbconn = null;
		
		try {
		        dbconn = DriverManager.getConnection
		                       (oracleURL,username,password);
		
		} catch (SQLException e) {
		
		        System.err.println("*** SQLException:  "
		            + "Could not open JDBC connection.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());
		        System.exit(-1);
		
		}
		
		    // Send the query to the DBMS, and get and display the results
		
		Statement stmt = null;
		int mid = -1;
		
		try { 
	        String query =       // our test query
	        		"SELECT mid FROM bhousmans.member" 
	        		+ " WHERE email='" + email + "'";
	
	        stmt = dbconn.createStatement();
	        ResultSet rs = stmt.executeQuery(query);
	        
	        if (rs.next()) {
	            mid = rs.getInt("mid");
	        }
		
		        // Shut down the connection to the DBMS.
		
	        rs.close();
		    stmt.close();  
		    dbconn.close();
		    
		
		} catch (SQLException e) {
		
		        System.err.println("*** SQLException:  "
		            + "Could not fetch query results.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());
		        System.exit(-1);
		
		}
		return mid;
		
	}

private static boolean memberCanBeDeleted(int mid) {
		
		final String oracleURL =   // Magic lectura -> aloe access spell
	            "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
	
		String username = "gabebarros",    // Oracle DBMS username
		       password = "a7693";    // Oracle DBMS password
		
		    // load the (Oracle) JDBC driver by initializing its base
		    // class, 'oracle.jdbc.OracleDriver'.
		
		try {
		
		        Class.forName("oracle.jdbc.OracleDriver");
		
		} catch (ClassNotFoundException e) {
		
		        System.err.println("*** ClassNotFoundException:  "
		            + "Error loading Oracle JDBC driver.  \n"
		            + "\tPerhaps the driver is not on the Classpath?");
		        System.exit(-1);
		
		}
		
		    // make and return a database connection to the user's
		    // Oracle database
		
		Connection dbconn = null;
		
		try {
		        dbconn = DriverManager.getConnection
		                       (oracleURL,username,password);
		
		} catch (SQLException e) {
		
		        System.err.println("*** SQLException:  "
		            + "Could not open JDBC connection.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());
		        System.exit(-1);
		
		}
		
		    // Send the query to the DBMS, and get and display the results
		
		Statement stmt = null;
		
		try { 
			String query =   // check if there are any tuples
				    "SELECT mid FROM bhousmans.skipass WHERE mid = " + mid + 
				    " UNION " +
				    "SELECT mid FROM bhousmans.lessonreg WHERE mid = " + mid;
	
	        stmt = dbconn.createStatement();
	        ResultSet rs = stmt.executeQuery(query);
	        
	        // return false if there are any tuples
	        if (rs.next()) {
	            return false;
	        }
		
		        // Shut down the connection to the DBMS.
		
	        rs.close();
		    stmt.close();  
		    dbconn.close();
		    
		
		} catch (SQLException e) {
		
		        System.err.println("*** SQLException:  "
		            + "Could not fetch query results.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());
		        System.exit(-1);
		
		}
		
		return true;
		
	}
	
	
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);  // use to get user input

        // the string to display to users when they run the program
        String menuStr = "Options: \n"
				+ "1 - Add Member \n"
        		+ "2 - Update Member \n"
        		+ "3 - Delete Member";
				
		// prompt for operations/queries until termination
        while (true) {
           System.out.println(menuStr);
           String input = scanner.nextLine();  // store user input
           
           if (input.equals("exit")) {
        	   break;
           }
           else if (input.strip().equals("1")) {
        	   System.out.println("What is your first name?");
        	   System.out.println();
               String fname = scanner.nextLine();  // store first name
               
               System.out.println("What is your last name?");
        	   System.out.println();
               String lname = scanner.nextLine();  // store last name
               
               System.out.println("What is your email?");
        	   System.out.println();
               String email = scanner.nextLine();  // store email
               
               System.out.println("What is your date of birth?");
        	   System.out.println();
               String dobStr = scanner.nextLine();  // store dob
               java.sql.Date dob = java.sql.Date.valueOf(dobStr);
               
               System.out.println("What is your phone number?");
        	   System.out.println();
               String phone = scanner.nextLine();  // store phone
               
               System.out.println("What is your emergency phone number?");
        	   System.out.println();
               String emergency = scanner.nextLine();  // store emergency
               
               addMember(fname, lname, email, dob, phone, emergency, 1);
               
           }
           else if (input.strip().equals("2")) {
        	   System.out.println("What is the member's email?");
        	   System.out.println();
               String email = scanner.nextLine();  // store email
               
               System.out.println("What would you like to update? (phone, email, emergency)");
        	   System.out.println();
               String category = scanner.nextLine();  // store category
               
               System.out.println("What would you like to change it to?");
        	   System.out.println();
               String change = scanner.nextLine();  // store change
               
               updateMember(email, category, change);
               
           }
           else if (input.strip().equals("3")) {
        	   System.out.println("What is the member's email?");
        	   System.out.println();
               String email = scanner.nextLine();  // store email
               
               deleteMember(email);
           }
           else {
        	   System.out.println("Invalid option");
        	   System.out.println();
           }

        }

       scanner.close();

	}
}
