/*
 *    Assignment:  Program #4 --  Database Design and Implementation
 *
 *      Authors:  Gabe Barros, Aarush Parvataneni, Dylan Carothers II, Bronson Housmans
 *    Language:  Java 16
 *
 *       Class:  CSC 460
 *  Instructor:  Lester Mccann
 *          TAs: Xinyu (Joyce) Guo, Jianwei (James) Shen
 *      
 *    Due Date:  May 6th, 2025, at the beginning of class
 *
 * ---------------------------------------------------------------------
 * 
 * Description: This program uses JDBC to interact with an SQL Oracle database
 * that is used to store data regarding a fictional ski resort. The database schema
 * was fully designed by our group. Users can perform several operations 
 * on the database, such as adding/deleting/updating memberships, ski passes,
 * lesson registrations, etc. Users can also make 4 queries, and the output
 * of the queries will be displayed to the screen. The UI is a simple text-based
 * interface, which displays the actions that the user can take. The user is then 
 * prompted for input, until the program is terminated (the user types 'exit').
 * The program contains as much error checking/input validation as we were able to
 * include with the limited time.
 * 
 * Input: None (user is prompted for input)
 * 
 * Output: Displays the result of whichever query/operation the user has chosen to run.
 * 
 * Required Features Not Included:  The program includes all required features
 *
 * Known Bugs:  There are no known bugs remaining in this program.
 * 
 */

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Prog4 {
		
	/*
    Method addMember (fname, lname, email, dob, phone, emergency, status)
    
    Purpose: This method runs the SQL instructions to insert a new member into
    the database. For the memberID, the method finds the max  memberID number
    that currently exists in the DB, and adds 1 to this number, ensuring
    uniqueness. The main method of the program prompts the user for the 
    arguments.
    
    Pre-condition:  The current member has not been inserted into the DB
    
    Post-condition:  The member has been inserted into the DB
    
    Parameters:  fname -- the member's first name
    			 lnmae -- the member's last name
    			 email -- the member's email
    			 dob -- the member's date of birth
    			 phone -- the member's phone number
    			 emergency -- the member's emergency phone number
    			 status -- the status of the membership (1 for active, etc.)
    
    Returns:  none
    */
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
		int mid = 0;  // store memberID
		
		try { 
            String query = "SELECT max(mid) FROM bhousmans.member";  // query to get new mID
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
            String query =       // member insertion 
            		"INSERT INTO bhousmans.member" + " (mid, firstname, lastname, email, dob, phone, emergency, status) " 
            		+ "VALUES (" + mid + ",'" + fname + "', '" + lname + "' , '" + email + "' , " + "TO_DATE('" + dob + "', 'YYYY-MM-DD')"
            		+ ", '" + phone + "' , '" + emergency + "', " + status + ")";

            stmt = dbconn.createStatement();
		    answer = stmt.executeUpdate(query);
	            
            System.out.println("Member inserted");
            System.out.println();
		
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
	
	/*
    Method updateMember (email, category, change)
    
    Purpose: This method runs the SQL instructions to update the information
    of a member that is in the database. The user provides their email (because
    it is unique), the category they want to change, and the value they would like
    to change it to. If an email is given that does not correspond to a membership,
    the program will not crash.
    
    Pre-condition:  None
    
    Post-condition:  One of the membership's fields have been updated
    
    Parameters:  email -- the member's email
    			 category -- the field that the member would like to update (email, fname, etc)
    			 change -- what they would like the field changed to
    			 
    Returns:  none
    */
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
			if (getMidByEmail(email) == -1) {
				System.out.println("There is no member with this email");
				System.out.println();
				return;
			}
			
            String query =       // membership update
            		"UPDATE bhousmans.member" 
            		+ " SET " + category + "='" + change + "'"
            		+ " WHERE email='" + email + "'";

            stmt = dbconn.createStatement();
		    answer = stmt.executeUpdate(query);
	            
            System.out.println("Member updated");
            System.out.println();
		
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
	
	/*
    Method deleteMember (email)
    
    Purpose: This method runs the SQL instructions to delete a member from
    the database. This can only be done if the member does not hold
	any active ski passes, open rental records, or unused lesson sessions.
	If these conditions are not met, the user will receive a message, and 
	the operation will not be performed.
    
    Pre-condition:  None
    
    Post-condition:  The user either gets a message to delete their other
    				 records, or the user will be deleted
    
    Parameters:  email -- the member's email
    			 
    Returns:  none
    */
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
			int mid = getMidByEmail(email);  // get the user's mid from their email
			
			if (mid == -1) {
				System.out.println("There is no member with this email");
				System.out.println();
				return;
			}
			
			if (!memberCanBeDeleted(mid)) {
				System.out.println("Member cannot be deleted because there are active ski passes, rental records, or lesson sessions."
						+ " Please complete or cancel these before deleting your membership.");
				return;
			}
			
			try { 
	            String query =       // SQL deletion
	            		"DELETE FROM bhousmans.member" 
	            		+ " WHERE mid=" + mid;
	
	            stmt = dbconn.createStatement();
			    answer = stmt.executeUpdate(query);
			    	            
	            System.out.println("Member deleted");
	            System.out.println();
			
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

	/*
    Method getMidByEmail (email)
    
    Purpose: This method is given a string representing an email address,
    and returns the memberID number of the member with that email address. If
    there is no member with this email, -1 is returned. This method is
    basically a helper for other methods in this class.
    
    Pre-condition:  None
    
    Post-condition:  Returns either -1, or the mID associated with this email
    
    Parameters:  email -- the member's email
    			 
    Returns:  none
    */
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
	        String query =       // our query
	        		"SELECT mid FROM bhousmans.member" 
	        		+ " WHERE email='" + email + "'";
	
	        stmt = dbconn.createStatement();
	        ResultSet rs = stmt.executeQuery(query);
	        
	        // update the mid if any tuples are returned
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

	/*
    Method memberCanBeDeleted (mid)
    
    Purpose: This method determines whether or not a membership can be deleted
    from the database. It checks if the member does not hold
	any active ski passes, open rental records, or unused lesson sessions. If so,
	the member can be deleted and we return true. Otherwise, return false.
    
    Pre-condition:  None
    
    Post-condition:  Returns true or false if the member can be deleted
    
    Parameters:  mid -- the memberID of the member
    			 
    Returns:  true/false if the membership can be deleted
    */
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
				String query =   // check if there are any tuples for skipass and lessonreg with this mid
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

	/*
    Method addSkipass (email, passType)
    
    Purpose: This method adds a skipass into the database for a certain member.
    The member is identified by their email. The passType is also given, and this
    will help determine the cost of the ski pass. Includes error checking if there is no
    member with the given email
    
    Pre-condition:  None
    
    Post-condition:  Adds a skipass associated with the member to the database
    
    Parameters:  email -- the member's email
    			 passType -- the type of skipass that the member wants (1-day, season, etc.)
    			 
    Returns:  none
    */
	private static void addSkipass(String email, String passType) {
		
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
		int spid = 0;  // store ski pass ID
		int mid = 0;  // store memberID
		
		if (getMidByEmail(email) == -1) {
			System.out.println("There is no member with this email");
			System.out.println();
			return;
		}
		else {
			mid = getMidByEmail(email);
		}
		
		try { 
	        String query = "SELECT max(spid) FROM bhousmans.skipass";  // get current max skipassID
	        stmt = dbconn.createStatement();
		    rs = stmt.executeQuery(query);
		    
		    if (rs.next()) {
		        spid = rs.getInt(1) + 1;  
		    } 
		    else {
		        spid = 1;
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
			// assign costs 
			double cost;
			if (passType.equals("1-day")) {
				cost = ThreadLocalRandom.current().nextDouble(50.00, 100.00);
			}
			else if (passType.equals("2-day")) {
				cost = ThreadLocalRandom.current().nextDouble(120.00, 180.00); 
			}
			else if (passType.equals("4-day")) {
				cost = ThreadLocalRandom.current().nextDouble(200.00, 250.00);
			}
			else {
				cost = ThreadLocalRandom.current().nextDouble(400.00, 600.00);
			}
			
			String query =     // insertion instructions
				    "INSERT INTO bhousmans.skipass" + 
				    " (spid, mid, passtype, expirydate, notimesused, cost) " + 
				    "VALUES (" + spid + ", " + mid + ", '" + passType + "', SYSDATE + INTERVAL '1' YEAR, 0, " + cost + ")";
	
	        stmt = dbconn.createStatement();
		    answer = stmt.executeUpdate(query);
	            
	        System.out.println("Pass purchased");
	        System.out.println();
		
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
	
	/*
    Method updateSkipass (spid, count)
    
    Purpose: This method allows workers to manually adjust the usage count
    for a member's ski pass. The ski pass ID is required to use this method.
    The method changes the ski pass's 'number of times used' value to the 
    'count' parameter value
    
    Pre-condition:  The ski pass with the given spid exists
    
    Post-condition:  Updates the ski pass tuple's 'notimesused' field to the 'count' value
    
    Parameters:  spid -- the ski pass ID
    			 count -- the count to set 'notimesused' to
    			 
    Returns:  none
    */
	private static void updateSkipass(String spid, String count) {
			
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
            String query =       // our update instructions
            		"UPDATE bhousmans.skipass" 
            		+ " SET notimesused=" + count
            		+ " WHERE spid=" + spid;

            stmt = dbconn.createStatement();
		    answer = stmt.executeUpdate(query);
	            
            System.out.println("Ski pass updated");
            System.out.println();
		
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

	/*
    Method deleteSkipass (spid)
    
    Purpose: This method allows members to delete their ski passes if the 
    pass is expired, or has no remaining uses. The method utilizes
    a helper method to check if the ski pass can be deleted. The user will
    receive a message if the ski pass cannot be deleted or the ski pass with
    the given spid does not exist.
    
    Pre-condition:  None
    
    Post-condition:  Deletes the skipass if it can be deleted.
    
    Parameters:  spid -- the ski pass ID
    			 
    Returns:  none
    */
	private static void deleteSkipass(String spid) {
		
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
		
		if (!skipassCanBeDeleted(spid)) {
			System.out.println("Ski pass cannot be deleted because the pass has not expired or has remaining uses (Or there is no ski pass with that ID)");
			System.out.println();
			return;
		}
		
		try { 
	        String query =  // deletion instructions
	        		"DELETE FROM bhousmans.skipass" 
	        		+ " WHERE spid=" + spid;
	
	        stmt = dbconn.createStatement();
		    answer = stmt.executeUpdate(query);
		    	            
	        System.out.println("Ski pass deleted");
	        System.out.println();
		
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
	
	/*
    Method skipassCanBeDeleted (spid)
    
    Purpose: This method is a helper method for the delete skipass method. It
    determines if a ski pass can be deleted based on if the pass has expired,
    or still has remaining uses left. 
    
    Pre-condition:  None
    
    Post-condition:  Determines if the skipass can be deleted.
    
    Parameters:  spid -- the ski pass ID
    			 
    Returns:  True/false if the ski pass can be deleted or not
    */
	private static boolean skipassCanBeDeleted(String spid) {
		
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
			String query =  // check if there are any tuples
				    "SELECT spid FROM bhousmans.skipass WHERE spid = " + spid + 
				    " AND ( " +
				    " (passtype = '1-day' AND notimesused >= 1 AND expirydate < SYSDATE) OR " +
				    " (passtype = '2-day' AND notimesused >= 2 AND expirydate < SYSDATE) OR " +
				    " (passtype = '4-day' AND notimesused >= 4 AND expirydate < SYSDATE) OR " +
				    " expirydate < SYSDATE" +
				    ")";
	
	        stmt = dbconn.createStatement();
	        ResultSet rs = stmt.executeQuery(query);
	        
	        // return true if there are any tuples
	        if (rs.next()) {
	            return true;
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
		return false;
	}
	
	/*
    Method privateOrGroup (dbconn, stmt, scanner, query)
    
    Purpose: This method determines whether a person wants their lesson
             to be with a group or private and which day it will occur.
             The proper lesson code is returned for an open lesson time
             or the user will get a message saying that their lesson is
             not available.
    
    Pre-condition:  All objects passed are open and working, query set to 
                    valid SQL command
    
    Post-condition:  The lesson code is returned
    
    Parameters:  dbconn - the database connection
				 stmt - a sql statement object
				 scanner - a system.in scanner
				 query - a sting for the query to be executed
    
    Returns:  int - retVal which is a lessonCode value
    */
	private static int privateOrGroup(Connection dbconn, Statement stmt, Scanner scanner, String query) {
		int retVal = -999;
		try {
			ResultSet rs = null;
			System.out.println("What day of the week would you like your first lesson to be?");
	        System.out.println("Enter an integer: Sun=1, Mon=2, Tue=3, Wed=4, Thur=5, Fri=6, Sat=7");
	 	   	System.out.println();
	        int lessonCode = Integer.parseInt(scanner.nextLine());
	        // check that day is valid input
	        if(lessonCode < 1 || lessonCode > 7) {
	 	    	while(true) {
	 	    		System.out.println("Invalid input for lesson\nEnter an integer: Sun=1, Mon=2, Tue=3, Wed=4, Thur=5, Fri=6, Sat=7");
	         	    System.out.println();
	         	    lessonCode = Integer.parseInt(scanner.nextLine());
			    if(lessonCode >= 1 && lessonCode <= 7) {
	         		   break;
	         	    }
	 	    	}
	 	    }
	        // ask which type of lesson the user wants
	        System.out.println("Would you like the lesson to be private or with a group? (enter p or g)");
	    	System.out.println();
	        String input1 = scanner.nextLine();
	        if(input1.equals("p")) {
	        	lessonCode *= -1;
	        }
	        // determine if there is a private lesson slot available
	        if(lessonCode < 0) {
	        	query = "SELECT * FROM bhousmans.lessonreg WHERE lessonCode = " + lessonCode + " and remainingsessions > 0";
	        	stmt = dbconn.createStatement();
				rs = stmt.executeQuery(query);
				
				if(rs.next()) {
					System.out.println("Sorry, the private lesson for this day is already reserved\n");
				} else {
					return lessonCode;
				}
	        } else {
	        	// determine if the group class if full
	        	query = "SELECT count(mid) FROM bhousmans.lessonreg WHERE lessonCode = " + lessonCode + " and remainingsessions > 0";
	        	stmt = dbconn.createStatement();
				rs = stmt.executeQuery(query);
				if(rs.next()) {
					int count = rs.getInt(1);
					if(count > 7) {
						System.out.println("Sorry, the group lesson is full this day\n");
					} else {
						return lessonCode;
					}
				} else {
					return lessonCode;
				}
				
	        }
		} catch (SQLException e) {
			
	        System.err.println("*** SQLException:  "
	            + "Could not open JDBC connection.");
	        System.err.println("\tMessage:   " + e.getMessage());
	        System.err.println("\tSQLState:  " + e.getSQLState());
	        System.err.println("\tErrorCode: " + e.getErrorCode());
	        System.exit(-1);
	
	}
		return retVal;
	}
	
	/*
    Method privateOrGroup (input, scanner)
    
    Purpose: This method determines whether to add, update, or delete a
             lesson record from the DB. The input is passed to determine
             the operation and the scanner allows for the program to gather
             the necessary info from the user to perform the operation.
             Adding members is dependent on the MID and lesson code they
             want. Updating members is dependent on the MID already having
             registered for a lesson and the lesson code update. Deleting
             can only occur when no sessions have been used yet.
    
    Pre-condition:  Input is 13, 14, or 15. Scanner is open
    
    Post-condition:  The operation is performed or user is told that their
                     input was not valid
    
    Parameters:  input - a string that is 13, 14, or 15
				 scanner - a system.in scanner
    
    Returns:  none
    */
	private static void lessonRecord(String input, Scanner scanner) {
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
		
		
		Statement stmt = null;
		ResultSet rs = null;
		int orderId = 1;
		String query = "";
		boolean runQuery = false;
		
		if(input.equals("13")) {
			System.out.println("You are adding a lesson purchase record. Enter the following information:\n");
			try { 
				// query to get the largest orderid in the table
	            query = "SELECT max(orderid) FROM bhousmans.lessonreg";
	            stmt = dbconn.createStatement();
			    rs = stmt.executeQuery(query);
			    
			    if (rs.next()) {
			        orderId = rs.getInt(1) + 1;  
			    } 
			    else {
			        orderId = 1;
			    }		    
			} catch (SQLException e) {
			
			        System.err.println("*** SQLException:  "
			            + "Could not fetch query results.");
			        System.err.println("\tMessage:   " + e.getMessage());
			        System.err.println("\tSQLState:  " + e.getSQLState());
			        System.err.println("\tErrorCode: " + e.getErrorCode());
			        System.exit(-1);
			
			}
			// get necessary fields from the user to create lesson record
			System.out.println("What is your member ID?");
     	   	System.out.println();
            int mid = Integer.parseInt(scanner.nextLine());
            // determine if the id input is valid
            query = "SELECT * FROM bhousmans.member WHERE mid = " + mid;
            try {
				stmt = dbconn.createStatement();
				rs = stmt.executeQuery(query);
				
				if(rs.next()) {
					// get the lesson code from the method
					int pg = privateOrGroup(dbconn, stmt, scanner, query);
		            
		            if(pg >= -7) {
		            	System.out.println("How many sessions would you like to purchase (integer)");
			     	   	System.out.println();
			            int numsessions = Integer.parseInt(scanner.nextLine());
			            
			            // make the query for the lesson record
			            query =       // our test query
			            		"INSERT INTO bhousmans.lessonreg" + " (orderid, lessoncode, mid, numsessions, remainingsessions) " 
			            		+ "VALUES (" + orderId + "," + pg + ", " + mid + " , " + numsessions + " , " + 
			            		 + numsessions + ")";
			            runQuery = true;
		            }
		            
				} else {
					System.out.println("Enter a valid member ID\n");
				}
			} catch (SQLException e) {
				System.err.println("*** SQLException:  "
			            + "Could not fetch query results.");
			        System.err.println("\tMessage:   " + e.getMessage());
			        System.err.println("\tSQLState:  " + e.getSQLState());
			        System.err.println("\tErrorCode: " + e.getErrorCode());
			        System.exit(-1);
			}

		} else if(input.equals("14")) {
			System.out.println("You are updating a lesson purchase record. Enter the following information:\n");
			System.out.println("What is your member ID?");
     	   	System.out.println();
            int mid = Integer.parseInt(scanner.nextLine());
            
            try { 
	            query = "SELECT * FROM bhousmans.lessonreg WHERE mid = " + mid;
	            stmt = dbconn.createStatement();
			    rs = stmt.executeQuery(query);
			    // determine of the id input is in the table
			    if (rs.next()) {
			        orderId = rs.getInt(1);
			        int lessonCode = rs.getInt(2);
			        rs.getInt(3);
			        rs.getInt(4);
			        int remainingsessions = rs.getInt(5);
			        
			        // check if lessonCode needs to be updated
			        System.out.println("Would you like to update the lesson code? (enter y or n)");
		        	System.out.println();
		            String input1 = scanner.nextLine();
		            if(input1.equals("y")) {
		            	lessonCode = privateOrGroup(dbconn, stmt, scanner, query);
		            	if(lessonCode >= -7) {
		            		// check if remaining sessions needs to be decreased by 1
				            System.out.println("Would you like to decrement the remaining sessions by 1? (enter y or n)");
				        	System.out.println();
				            input1 = scanner.nextLine();
				            if(input1.equals("y")) {
				            	remainingsessions--;
				            }
				            query = 
				            		"UPDATE bhousmans.lessonreg SET lessoncode = " + lessonCode
				            		+ " , remainingsessions = " + remainingsessions
				            		+ " WHERE orderid = " + orderId;
				            runQuery = true;
		            	}
		            } else {
		            	// check if remaining sessions needs to be decreased by 1
			            System.out.println("Would you like to decrement the remaining sessions by 1? (enter y or n)");
			        	System.out.println();
			            input1 = scanner.nextLine();
			            if(input1.equals("y")) {
			            	remainingsessions--;
			            }
			            query = 
			            		"UPDATE bhousmans.lessonreg SET lessoncode = " + lessonCode
			            		+ " , remainingsessions = " + remainingsessions
			            		+ " WHERE orderid = " + orderId;
			            runQuery = true;
		            }
		            
			    }
			    else {
			        System.out.println("The member ID that you entered is not in our records. Try another ID\n");
			    }		    
			} catch (SQLException e) {
			
			        System.err.println("*** SQLException:  "
			            + "Could not fetch query results.");
			        System.err.println("\tMessage:   " + e.getMessage());
			        System.err.println("\tSQLState:  " + e.getSQLState());
			        System.err.println("\tErrorCode: " + e.getErrorCode());
			        System.exit(-1);
			
			}
            
		} else {
			System.out.println("You are deleting a lesson purchase record. Enter the following information:\n");
			System.out.println("What is your member ID?");
     	   	System.out.println();
            int mid = Integer.parseInt(scanner.nextLine());
            
            try { 
	            query = "SELECT * FROM bhousmans.lessonreg WHERE mid = " + mid;
	            stmt = dbconn.createStatement();
			    rs = stmt.executeQuery(query);
			    
			    if (rs.next()) {
			        orderId = rs.getInt(1);
			        rs.getInt(2);
			        rs.getInt(3);
			        int numsessions = rs.getInt(4);
			        int remainingsessions = rs.getInt(5);
			        // we can only delete the record if the session bought == num remaining
			        if(numsessions == remainingsessions) {
			        	query = 
			            		"DELETE FROM bhousmans.lessonreg "
			            		+ " WHERE orderid = " + orderId;
			            runQuery = true;
			        } else {
			        	System.out.println("Sorry, this record can not be deleted because"
			        			+ " at least one session from the purchase has been used\n");
			        }
			    }
			    else {
			        System.out.println("The member ID that you entered is not in our records. Try another ID\n");
			    }		    
			} catch (SQLException e) {
			
			        System.err.println("*** SQLException:  "
			            + "Could not fetch query results.");
			        System.err.println("\tMessage:   " + e.getMessage());
			        System.err.println("\tSQLState:  " + e.getSQLState());
			        System.err.println("\tErrorCode: " + e.getErrorCode());
			        System.exit(-1);
			
			}
		}
		// execute the query string if the user input meets the necessary conditions
		if(runQuery) {
			try { 

	            stmt = dbconn.createStatement();
			    stmt.executeUpdate(query);
		            
	            System.out.println("lesson record action completed\n");
			
			} catch (SQLException e) {
			
			        System.err.println("*** SQLException:  "
			            + "Could not fetch query results.");
			        System.err.println("\tMessage:   " + e.getMessage());
			        System.err.println("\tSQLState:  " + e.getSQLState());
			        System.err.println("\tErrorCode: " + e.getErrorCode());
			        System.exit(-1);
			
			}
		}
		
		try {
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
	
	
	/*---------------------------------------------------------------------
    |  Method editEInvRecord
    |
    |  Purpose:  To direct the user into the desired-action method. Serves as
    |		a "middle-man" between main() and the 3 separate add/update/delete
    |		methods.
    |
    |  Pre-condition:  None.
    |
    |  Post-condition: All confirmed actions made within the add/update/delete
    |		methods are reflected in the Oracle DB.
    |
    |  Parameters:
    |		scanner - A Scanner object cleared of input to be used within the
    |			add/update/delete methods to receive input from the user's
    |			input.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
	
	private static void editEInvRecord(Scanner scanner) {
		
		// Stores the prompt to be repeatedly printed
		String menuStr = "What would you like to do?\n"
				+ "1 - Add Equipment Inventory Record\n"
				+ "2 - Update Equipment Inventory Record\n"
				+ "3 - Delete Equipment Inventory Record\n"
				+ "4 - Return to main menu\n";
		
		// Stores the user's input
		String input;
		
		while (true) {
			System.out.println(menuStr);
			input = scanner.nextLine();
			
			if (input.strip().equals("1")) {
				addEInvRecord(scanner);
				return;
			}
			
			if (input.strip().equals("2")) {
				updateEInvRecord(scanner);
				return;
			}
			
			if (input.strip().equals("3")) {
				deleteEInvRecord(scanner);
				return;
			}
			
			if (input.strip().equals("4")) {
				return;
			}
			
			System.out.println("Invalid option\n");
		}
	}
	
	
	/*---------------------------------------------------------------------
    |  Method addEInvRecord
    |
    |  Purpose:  To prompt various inputs from the user necessary for the
    |		creation of a Equipment Inventory Record within the 'equipment'
    |		table in the Oracle Database. The method will first find the
    |		current highest equipId (primary key) within the table and add 1
    |		to for the new equipId. If there is no data in the table, the new
    |		Equipment is given an equipId of 1. The method then prompts the
    |		user for a type and then a size/length (if necessary). A new
    |		record is then added with the given data and an INUSE value of 0.
    |
    |  Pre-condition:  None.
    |
    |  Post-condition: An Inventory Record with the specified values is added
    |		to the 'equipment' table within the Oracle Database.
    |
    |  Parameters:
    |		scanner - A Scanner object cleared of input to be used to receive
    |			input from the user's keyboard.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
	
	private static void addEInvRecord(Scanner scanner) {
		
		// Magic lectura -> aloe access spell
		String oracleURL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
		// Will hold a database connection to the hard-coded Oracle database
		Connection dbconn = null;

		// Hard-coded Oracle login data for ease of access and lack of sensitive
		// information
		String username = "dylanacarothers";
		String password = "a5382";
		
		// Load the (Oracle) JDBC driver by initializing its base class
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("*** ClassNotFoundException: Error loading Oracle JDBC driver.");
			System.err.println("\tPerhaps the driver is not on the Classpath?");
			System.exit(-1);
		}

		// Make and return a database connection to the Oracle database
		try {
			dbconn = DriverManager.getConnection(oracleURL, username, password);
		} catch (SQLException e) {
			System.err.println("*** SQLException: Could not open JDBC connection.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			System.exit(-1);
		}
		
		// Used to store the statement connection
		Statement stmt = null;
		// Used to store the sql query
		String query = null;
		// Used to store the sql results
		ResultSet result = null;
		// Stores the next equipId not yet in the 'equipment' table
		int equipId = -1;
		
		// Calculate what the next primary key should be
		try { 
            query = "SELECT max(equipid) FROM bhousmans.equipment";
            stmt = dbconn.createStatement();
            result = stmt.executeQuery(query);
		    
		    if (result.next()) {
		    	equipId = result.getInt(1) + 1;  
		    } 
		    else {
		    	equipId = 1;
		    }		   
		    stmt.close();
		} catch (SQLException e) {
		        System.err.println("*** SQLException: Could not fetch query results.");
		        System.err.println("\tMessage:   " + e.getMessage());
		        System.err.println("\tSQLState:  " + e.getSQLState());
		        System.err.println("\tErrorCode: " + e.getErrorCode());
		        System.exit(-1);
		}
		
		// Stores the prompt to be repeatedly printed
		String prompt = "What equipment type would you like to add?\n"
				+ "1 - Boots\n"
				+ "2 - Poles\n"
				+ "3 - Alpine Skis\n"
				+ "4 - Snowboard\n"
				+ "5 - Protective Gear\n";
		
		// Stores the user's input
		String input;
		
		// Stores a list of possible acceptable input values
		String[] types = {"boots", "poles", "skis", "snowboard", "protective"};
		// Stores the selected input value
		String type;
		
		// Keep looping until valid input is given
		while (true) {
			System.out.println(prompt);
			
			input = scanner.nextLine();
			
			if (input.strip().equals("1")) {
				type = types[0];
				break;
			} 
			if (input.strip().equals("2")) {
				type = types[1];
				break;
			} 
			if (input.strip().equals("3")) {
				type = types[2];
				break;
			} 
			if (input.strip().equals("4")) {
				type = types[3];
				break;
			} 
			if (input.strip().equals("5")) {
				type = types[4];
				break;
			}
			
			System.out.println("Invalid option\n");
		}
		
		prompt = "What is the size/length of the " + type + "?\n";
		
		// Stores another prompt to be printed repeatedly dependent on type
		String range;
		// Stores the user's input as an int
		int slength = -1;
		
		// Get the size is type is boot
		if (type.equals("boots")) {
			range = "Enter a size between 4 and 14 (inclusive)\n"
					+ "Half sizes are allowed, enter in format 'XX.X'\n";
			
			while (true) {
				System.out.println(prompt);
				System.out.println(range);
				
				slength = (int) (scanner.nextFloat() * 10);
				scanner.nextLine();
				
				if (slength < 40 || slength > 140 || slength % 5 != 0) {
					System.out.println("Invalid size\n");
				} else {
					break;
				}
			}
		}
		
		// Get the length if type is pole
		if (type.equals("poles")) {
			range = "Enter a length between 100 and 140 (inclusive)\n";;
			
			while (true) {
				System.out.println(prompt);
				System.out.println(range);
				
				slength = scanner.nextInt();
				scanner.nextLine();
				
				if (slength < 100 || slength > 140) {
					System.out.println("Invalid length\n");
				} else {
					break;
				}
			}
		}
		
		// Get the length if type is skis
		if (type.equals("skis")) {
			range = "Enter a length between 115 and 200 (inclusive)\n";;
			
			while (true) {
				System.out.println(prompt);
				System.out.println(range);
				
				slength = scanner.nextInt();
				scanner.nextLine();
				
				if (slength < 115 || slength > 200) {
					System.out.println("Invalid length\n");
				} else {
					break;
				}
			}
		}
		
		// Get the length is type is snowboard
		if (type.equals("snowboard")) {
			range = "Enter a length between 90 and 178 (inclusive)\n";;
			
			while (true) {
				System.out.println(prompt);
				System.out.println(range);
				
				slength = scanner.nextInt();
				scanner.nextLine();
				
				if (slength < 90 || slength > 178) {
					System.out.println("Invalid length\n");
				} else {
					break;
				}
			}
		}
		
		// Set length to 0 if type is protective
		if (type.equals("protective")) {
			slength = 0;
		}

		// Add the Equipment Inventory Record to the Oracle Database
		try {
			stmt = dbconn.createStatement();
			query = "INSERT INTO bhousmans.equipment VALUES ( " + equipId + ", '" + type + "', " + slength + ", 0)";
			stmt.executeUpdate(query);

			// Shut down the connection to the DBMS.
			stmt.close();
			dbconn.close();
			
			System.out.println(type + " added successfully!\n"
					+ "Equipment ID is " + equipId + "\n");

		} catch (SQLException e) {
			System.err.println("*** SQLException: Could not fetch query results.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());			
			System.exit(-1);
		}
	}
	
	
	/*---------------------------------------------------------------------
    |  Method updateEInvRecord
    |
    |  Purpose:  To get the equipId from the user that is to be updated, and
    |		then the new values to be added. The Record is then updated and a
    |		new one is created. The method first prompts the user to enter the
    |		equipId that is to be updated. The method will verify that the
    |		Record can be updated, repeating the prompt otherwise. The method
    |		then calls the addEInvRecord to create the newly updated data, and
    |		once it is complete, the previous Record has its INUSE value set
    |		to -2, representing an updated item, but still retaining the log.
    |
    |  Pre-condition:  None.
    |
    |  Post-condition: The Equipment Inventory Record with the given equipId
    |		is updated within the 'equipment' table in the Oracle Database
    |		with the 'INUSE' value set to -2, representing a log of an item
    |		that has been updated. A new Equipment Inventory Record is created
    |		with a new equipId and the specified values.
    |
    |  Parameters:
    |		scanner - A Scanner object cleared of input to be used to receive
    |			input from the user's keyboard.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
	
	private static void updateEInvRecord(Scanner scanner) {
		
		// Magic lectura -> aloe access spell
		String oracleURL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
		// Will hold a database connection to the hard-coded Oracle database
		Connection dbconn = null;

		// Hard-coded Oracle login data for ease of access and lack of sensitive
		// information
		String username = "dylanacarothers";
		String password = "a5382";
				
		// Load the (Oracle) JDBC driver by initializing its base class
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("*** ClassNotFoundException: Error loading Oracle JDBC driver.");
			System.err.println("\tPerhaps the driver is not on the Classpath?");
			System.exit(-1);
		}

		// Make and return a database connection to the Oracle database
		try {
			dbconn = DriverManager.getConnection(oracleURL, username, password);
		} catch (SQLException e) {
			System.err.println("*** SQLException: Could not open JDBC connection.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			System.exit(-1);
		}
		
		// Used to store the statement connection
		Statement stmt = null;
		// Used to store the sql query
		String query;
		// Used to store the sql results
		ResultSet result = null;
		// Stores the prompt to be repeatedly printed
		String prompt = "What is the equipment ID of the item you wish to update?\n"
				+ "Enter '-1' to cancel\n";
		// Stores the equipId that is to be updated
		int equipId = -1;
		// Stores a boolean that controls if the loop should continue
		boolean continuePrompt = true;
		
		// Keep looping until the user has selected an Equipment to update
		while (continuePrompt) {
			System.out.println(prompt);
				
			equipId = scanner.nextInt();
			scanner.nextLine();
			
			// If the user entered '-1' close the connection and exit
			if (equipId == -1) {
				try {
					dbconn.close();
				} catch (SQLException e) {
					System.err.println("*** SQLException: Could not fetch query results.");
					System.err.println("\tMessage:   " + e.getMessage());
					System.err.println("\tSQLState:  " + e.getSQLState());
					System.err.println("\tErrorCode: " + e.getErrorCode());
					System.exit(-1);
				}	
				return;
			}
			
			query = "SELECT * FROM bhousmans.equipment WHERE EQUIPID = ";
			query += equipId;
			
			try {
				stmt = dbconn.createStatement();
				result = stmt.executeQuery(query);
				
				// If the query contained a result, then ask the user if it
				// is the Equipment item they want to update
				if (result.next()) {
					// If the Equipment is already in use, print a message and loop again
					if (result.getInt("INUSE") == 1) {
						System.out.println("Equipment '" + equipId + "' is currently rented!\n"
								+ "Please mark as returned to update the equipment!\n");
					// If the Equipment has already been updated or deleted,
					// print a message and loop again
					} else if (result.getInt("INUSE") < 0) {
						System.out.println("Equipment '" + equipId + "' has been retired!\n"
								+ "Retired equipment cannot be updated!\n");
					// The Equipment can be updated, ask the user if they want to continue
					} else {
						System.out.println("   EQUIPID EQUIPTYPE               SLENGTH      INUSE");
						System.out.println("---------- -------------------- ---------- ----------");
	
						// Print the attribute values
						String tuple = String.format("%10s ", result.getInt("EQUIPID"));
						tuple += String.format("%-20s ", result.getString("EQUIPTYPE"));
						tuple += String.format("%10s ", result.getInt("SLENGTH"));
						tuple += String.format("%10s ", result.getInt("INUSE"));
						System.out.println(tuple);
						
						System.out.println("Is this the Equipment you wish to update?\n"
								+ "A new EQUIPID will be assigned and the previous EQUIPID will be retired!\n"
								+ "y - Continue\n"
								+ "n - Enter a new Equipment ID\n");
						
						// Stores the user's input
						String input;
						
						// Loop until the user enters 'y' or 'n'
						while (true) {
							input = scanner.nextLine();
							
							if (input.strip().equals("y")) {
								continuePrompt = false;
								break;
							}
							if (input.strip().equals("n")) {
								break;
							}
							System.out.println("Invalid option\n");
						}
					}
				} else {
					System.out.println("\nError: No record found with that equipment ID!");
				}
				
				stmt.close();
				
			} catch (SQLException e) {
				System.err.println("*** SQLException: Could not fetch query results.");
				System.err.println("\tMessage:   " + e.getMessage());
				System.err.println("\tSQLState:  " + e.getSQLState());
				System.err.println("\tErrorCode: " + e.getErrorCode());
				System.exit(-1);
			}	
		}

		try {
			// Create a new Equipment Inventory Record
			addEInvRecord(scanner);

			// Update the old Equipment Inventory Record to have an INUSE value of -2
			stmt = dbconn.createStatement();
			query = "UPDATE bhousmans.equipment SET INUSE = -2 WHERE EQUIPID = " + equipId;
			stmt.executeUpdate(query);
			System.out.println("Update completed sucessfully!");

			// Shut down the connection to the DBMS.
			stmt.close();
			dbconn.close();

		} catch (SQLException e) {
			System.err.println("*** SQLException: Could not fetch query results.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());			
			System.exit(-1);
		}
	}
	
	
	/*---------------------------------------------------------------------
    |  Method deleteEInvRecord
    |
    |  Purpose:  To get the equipId from the user that is to be deleted. The
    |		Record is then updated. The method first prompts the user to enter
    |		the equipId that is to be deleted. The method will verify that the
    |		Record can be deleted, repeating the prompt otherwise. The Record
    |		then has its INUSE value set to -1, representing a deleted item,
    |		but still retaining the log.
    |
    |  Pre-condition:  None.
    |
    |  Post-condition: The Equipment Inventory Record with the given equipId
    |		is updated within the 'equipment' table in the Oracle Database
    |		with the 'INUSE' value set to -1 which represents a deleted item,
    |		but retains the log.
    |
    |  Parameters:
    |		scanner - A Scanner object cleared of input to be used to receive
    |			input from the user's keyboard.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
	
	private static void deleteEInvRecord(Scanner scanner) {
		
		// Magic lectura -> aloe access spell
		String oracleURL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
		// Will hold a database connection to the hard-coded Oracle database
		Connection dbconn = null;

		// Hard-coded Oracle login data for ease of access and lack of sensitive
		// information
		String username = "dylanacarothers";
		String password = "a5382";
				
		// Load the (Oracle) JDBC driver by initializing its base class
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("*** ClassNotFoundException: Error loading Oracle JDBC driver.");
			System.err.println("\tPerhaps the driver is not on the Classpath?");
			System.exit(-1);
		}

		// Make and return a database connection to the Oracle database
		try {
			dbconn = DriverManager.getConnection(oracleURL, username, password);
		} catch (SQLException e) {
			System.err.println("*** SQLException: Could not open JDBC connection.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			System.exit(-1);
		}
		
		// Used to store the statement connection
		Statement stmt = null;
		// Used to store the sql query
		String query;
		// Used to store the sql results
		ResultSet result = null;
		// Stores the prompt to be repeatedly printed
		String prompt = "What is the equipment ID of the item you wish to delete?\n"
				+ "Enter '-1' to cancel\n";
		// Stores the equipId that is to be deleted
		int equipId = -1;
		
		// Keep looping until the user has selected an Equipment to delete
		while (true) {
			System.out.println(prompt);
				
			equipId = scanner.nextInt();
			scanner.nextLine();
			
			// If the user entered '-1' close the connection and exit
			if (equipId == -1) {
				try {
					dbconn.close();
				} catch (SQLException e) {
					System.err.println("*** SQLException: Could not fetch query results.");
					System.err.println("\tMessage:   " + e.getMessage());
					System.err.println("\tSQLState:  " + e.getSQLState());
					System.err.println("\tErrorCode: " + e.getErrorCode());
					System.exit(-1);
				}	
				return;
			}
			
			query = "SELECT * FROM bhousmans.equipment WHERE EQUIPID = ";
			query += equipId;
			
			try {
				stmt = dbconn.createStatement();
				result = stmt.executeQuery(query);
				
				// If the query contained a result, then ask the user if it
				// is the Equipment item they want to delete
				if (result.next()) {
					// If the Equipment is in use, print a message and loop again
					if (result.getInt("INUSE") == 1) {
						System.out.println("Equipment '" + equipId + "' is currently rented!\n"
								+ "Please mark as returned to delete the equipment!\n");
					// If the Equipment has already been deleted or updated,
					// print a message and loop again
					} else if (result.getInt("INUSE") < 0) {
						System.out.println("Equipment '" + equipId + "' has already been retired!\n"
								+ "Retired equipment cannot be deleted!\n");
					// The Equipment can be deleted, ask the user if they want to continue
					} else {
						System.out.println("   EQUIPID EQUIPTYPE               SLENGTH      INUSE");
						System.out.println("---------- -------------------- ---------- ----------");
	
						// Print the attribute values
						String tuple = String.format("%10s ", result.getInt("EQUIPID"));
						tuple += String.format("%-20s ", result.getString("EQUIPTYPE"));
						tuple += String.format("%10s ", result.getInt("SLENGTH"));
						tuple += String.format("%10s", result.getInt("INUSE"));
						System.out.println(tuple);
						
						System.out.println("Is this the Equipment you wish to delete?\n"
								+ "y - Continue\n"
								+ "n - Enter a new Equipment ID\n");
						
						// Stores user's input
						String input;
						
						// Loop until the user enters 'y' or 'n'
						while (true) {
							input = scanner.nextLine();
							
							if (input.strip().equals("y")) {
								stmt.close();
								
								stmt = dbconn.createStatement();
								query = "UPDATE bhousmans.equipment SET INUSE = -1 WHERE EQUIPID = " + equipId;
								stmt.executeUpdate(query);
								System.out.println("Delete completed sucessfully!\n");

								// Shut down the connection to the DBMS.
								stmt.close();
								dbconn.close();
								return;
								
							}
							if (input.strip().equals("n")) {
								break;
							}
							
							System.out.println("Invalid option\n");
						}
					}
				} else {
					System.out.println("\nError: No record found with that equipment ID!");
				}
				
				stmt.close();
				
			} catch (SQLException e) {
				System.err.println("*** SQLException: Could not fetch query results.");
				System.err.println("\tMessage:   " + e.getMessage());
				System.err.println("\tSQLState:  " + e.getSQLState());
				System.err.println("\tErrorCode: " + e.getErrorCode());
				System.exit(-1);
			}	
		}
	}
	
	
	/*---------------------------------------------------------------------
    |  Method editERentRecord
    |
    |  Purpose:  To direct the user into the desired-action method. Serves as
    |		a "middle-man" between main() and the 3 separate add/update/delete
    |		methods.
    |
    |  Pre-condition:  None.
    |
    |  Post-condition: All confirmed actions made within the add/update/delete
    |		methods are reflected in the Oracle DB.
    |
    |  Parameters:
    |		scanner - A Scanner object cleared of input to be used within the
    |			add/update/delete methods to receive input from the user's
    |			input.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
	
	private static void editERentRecord(Scanner scanner) {
		
		// Stores the prompt to be repeatedly printed
		String menuStr = "What would you like to do?\n"
				+ "1 - Add Equipment Rental Record\n"
				+ "2 - Update Equipment Rental Record\n"
				+ "3 - Delete Equipment Rental Record\n"
				+ "4 - Return to main menu\n";
		
		// Stores the user's input
		String input;
		
		while (true) {
			System.out.println(menuStr);
			input = scanner.nextLine();  // store user input
			
			if (input.strip().equals("1")) {
				addERentRecord(scanner);
				return;
			}
			
			if (input.strip().equals("2")) {
				updateERentRecord(scanner);
				return;
			}
			
			if (input.strip().equals("3")) {
				deleteERentRecord(scanner);
				return;
			}
			
			if (input.strip().equals("4")) {
				return;
			}
			
			System.out.println("Invalid option\n");
		}
	}
	
	/*---------------------------------------------------------------------
    |  Method addERentRecord
    |
    |  Purpose:  To prompt various inputs from the user necessary for the
    |		creation of a Equipment Rental Record within the 'equipretnal'
    |		table in the Oracle Database. The method will first find the
    |		current highest rentalId (primary key) within the table and add 1
    |		to for the new retnalId. If there is no data in the table, the new
    |		Rental is given a rentalId of 1. The method then prompts the
    |		user for a Ski Pass ID which is to be used to rent the equipment.
    |		The expiration date of the Ski Pass is checked before it can be
    |		used. Once the Ski Pass has been verified, the method prompts the
    |		user for the Equipment ID of the item to be rented. Once the
    |		item's availability has been verified, the user is prompted to
    |		enter a date for when the rental will end. The rental end date is
    |		always at least one day, but may extend up to three days, or the
    |		date of the Ski Pass's expiration, whichever comes first. Only
    |		after all these entries have been obtained and verified is the new
    |		Equipment Rental Record added with a RETURNSTATUS value of 1.
    |
    |  Pre-condition:  None.
    |
    |  Post-condition: The Equipment Rental Record with the specified values
    |		is added to the 'equiprental' table within the Oracle Database.
    |		The corresponding Equipment Inventory Record is updated to have an
    |		INUSE value of 1.
    |
    |  Parameters:
    |		scanner - A Scanner object cleared of input to be used to receive
    |			input from the user's keyboard.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
	
	private static void addERentRecord(Scanner scanner) {
		
		// Magic lectura -> aloe access spell
		String oracleURL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
		// Will hold a database connection to the hard-coded Oracle database
		Connection dbconn = null;

		// Hard-coded Oracle login data for ease of access and lack of sensitive
		// information
		String username = "dylanacarothers";
		String password = "a5382";
				
		// Load the (Oracle) JDBC driver by initializing its base class
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("*** ClassNotFoundException: Error loading Oracle JDBC driver.");
			System.err.println("\tPerhaps the driver is not on the Classpath?");
			System.exit(-1);
		}

		// Make and return a database connection to the Oracle database
		try {
			dbconn = DriverManager.getConnection(oracleURL, username, password);
		} catch (SQLException e) {
			System.err.println("*** SQLException: Could not open JDBC connection.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			System.exit(-1);
		}

		// Used to store the statement connection
		Statement stmt = null;
		// Used to store the sql query
		String query = null;
		// Used to store the sql results
		ResultSet result = null;
		// Stores the next rentalId not yet in the 'equiprental' table
		int rentalId = -1;
		
		// Calculate what the next primary key should be
		try {
			query = "SELECT max(rentalid) FROM bhousmans.equiprental";
			stmt = dbconn.createStatement();
			result = stmt.executeQuery(query);
			
			if (result.next()) {
				rentalId = result.getInt(1) + 1;
			} else {
				rentalId = 1;
			}
			
			stmt.close();
			
		} catch (SQLException e) {
			System.err.println("*** SQLException: Could not fetch query results.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			System.exit(-1);
		}
		
		// Stores the prompt to be repeatedly printed
		String prompt = "What is the Ski Pass ID you wish to rent from?\n"
				+ "Enter '-1' to cancel\n";
		// Stores the spId that is to be used to rent the Equipment
		int spId = -1;
		// Stores a boolean that controls if the loop should continue
		boolean continuePrompt = true;
		
		// Keep looping until the user has selected a Ski Pass to use
		while (continuePrompt) {
			System.out.println(prompt);
				
			spId = scanner.nextInt();
			scanner.nextLine();
			
			// If the user entered '-1' close the connection and exit
			if (spId == -1) {
				try {
					dbconn.close();
				} catch (SQLException e) {
					System.err.println("*** SQLException: Could not fetch query results.");
					System.err.println("\tMessage:   " + e.getMessage());
					System.err.println("\tSQLState:  " + e.getSQLState());
					System.err.println("\tErrorCode: " + e.getErrorCode());
					System.exit(-1);
				}	
				return;
			}
			
			query = "SELECT * FROM bhousmans.skipass WHERE SPID = ";
			query += spId;
			
			try {
				stmt = dbconn.createStatement();
				result = stmt.executeQuery(query);
				
				// If the query contained a result, then make sure the
				// Ski Pass isn't expired
				if (result.next()) {
					
					stmt.close();
					
					stmt = dbconn.createStatement();
					query += " AND EXPIRYDATE > SYSDATE";
					result = stmt.executeQuery(query);
					
					// If the query contained a result, then ask the user if it
					// is the Ski Pass they want to use
					if (result.next()) {
						
						System.out.println("      SPID        MID PASSTYPE EXPIRYDATE NOTIMESUSED       COST");
						System.out.println("---------- ---------- -------- ---------- ----------- ----------");
	
						// Print the attribute values
						String tuple = String.format("%10s ", result.getInt("SPID"));
						tuple += String.format("%10s ", result.getInt("MID"));
						tuple += String.format("%8s ", result.getString("PASSTYPE"));
						tuple += String.format("%10s ", result.getString("EXPIRYDATE").substring(0, 11));
						tuple += String.format("%10s ", result.getInt("NOTIMESUSED"));
						tuple += String.format("%10s", result.getString("COST"));
						System.out.println(tuple);
												
						System.out.println("Is this the Ski Pass you wish to use?\n"
								+ "y - Continue\n"
								+ "n - Enter a new Ski Pass ID\n");
						
						// Stores the user's input
						String input;
						
						// Loop until the user enters 'y' or 'n'
						while (true) {
							input = scanner.nextLine();
							
							if (input.strip().equals("y")) {
								continuePrompt = false;
								break;
							}
							if (input.strip().equals("n")) {
								break;
							}
							
							System.out.println("Invalid option\n");
						}
					} else {
						System.out.println("\nError: Sorry the entered Ski Pass is expired!\n");
					}
				} else {
					System.out.println("\nError: No record found with that Ski Pass ID!\n");
				}
				
				stmt.close();
				
			} catch (SQLException e) {
				System.err.println("*** SQLException: Could not fetch query results.");
				System.err.println("\tMessage:   " + e.getMessage());
				System.err.println("\tSQLState:  " + e.getSQLState());
				System.err.println("\tErrorCode: " + e.getErrorCode());
				System.exit(-1);
			}	
		}
		
		prompt = "What is the equipment ID of the item you wish to rent?\n"
				+ "Enter '-1' to cancel\n";
		// Stores the equipId of the Equipment to be rented
		int equipId = -1;
		continuePrompt = true;
		
		// Keep looping until the user has selected an Equipment item to use
		while (continuePrompt) {
			System.out.println(prompt);
				
			equipId = scanner.nextInt();
			scanner.nextLine();
			
			// If the user entered '-1' close the connection and exit
			if (equipId == -1) {
				try {
					dbconn.close();
				} catch (SQLException e) {
					System.err.println("*** SQLException: Could not fetch query results.");
					System.err.println("\tMessage:   " + e.getMessage());
					System.err.println("\tSQLState:  " + e.getSQLState());
					System.err.println("\tErrorCode: " + e.getErrorCode());
					System.exit(-1);
				}	
				return;
			}
			
			query = "SELECT * FROM bhousmans.equipment WHERE EQUIPID = ";
			query += equipId;
			
			try {
				stmt = dbconn.createStatement();
				result = stmt.executeQuery(query);
				
				// If the query contained a result, then ask the user if it
				// is the Equipment item they want to rent
				if (result.next()) {
					// If the Equipment is already in use, print a message and loop again
					if (result.getInt("INUSE") == 1) {
						System.out.println("Equipment '" + equipId + "' is currently rented!\n"
								+ "Cannot rent an item currently in use!\n");
					// If the Equipment has been deleted or updated, print a message and loop again
					} else if (result.getInt("INUSE") < 0) {
						System.out.println("Equipment '" + equipId + "' has already been retired!\n"
								+ "Retired equipment cannot be rented!\n");
					// The Equipment can be rented, ask the user if they want to continue
					} else {
						System.out.println("   EQUIPID EQUIPTYPE               SLENGTH      INUSE");
						System.out.println("---------- -------------------- ---------- ----------");
	
						// Print the attribute values
						String tuple = String.format("%10s ", result.getInt("EQUIPID"));
						tuple += String.format("%-20s ", result.getString("EQUIPTYPE"));
						tuple += String.format("%10s ", result.getInt("SLENGTH"));
						tuple += String.format("%10s", result.getInt("INUSE"));
						System.out.println(tuple);
						
						System.out.println("Is this the Equipment you wish to rent?\n"
								+ "y - Continue\n"
								+ "n - Enter a new Equipment ID\n");
						
						// Stores the user's input
						String input;
						
						// Loop until the user enters 'y' or 'n'
						while (true) {
							input = scanner.nextLine();
							
							if (input.strip().equals("y")) {								
								continuePrompt = false;
								break;
							}
							if (input.strip().equals("n")) {
								break;
							}
							
							System.out.println("Invalid option\n");
						}
					}
				} else {
					System.out.println("\nError: No record found with that equipment ID!");
				}
				
				stmt.close();
				
			} catch (SQLException e) {
				System.err.println("*** SQLException: Could not fetch query results.");
				System.err.println("\tMessage:   " + e.getMessage());
				System.err.println("\tSQLState:  " + e.getSQLState());
				System.err.println("\tErrorCode: " + e.getErrorCode());
				System.exit(-1);
			}	
		}
		
		// Stores the max number of days the user can rent the item for
		int maxDays = 0;
		// Stores the chosen number of days the user will rent the item for
		int rentalDays = 0;
		
		// Calculate the number of days remaining on the chosen Ski Pass rounded up
		try {
			query = "SELECT (EXPIRYDATE - SYSDATE) FROM bhousmans.skipass WHERE SPID = " + spId;
			stmt = dbconn.createStatement();
			result = stmt.executeQuery(query);
			
			result.next();
			maxDays = result.getInt(1) + 1;
			stmt.close();
			
		} catch (SQLException e) {
			System.err.println("*** SQLException: Could not fetch query results.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			System.exit(-1);
		}
		
		// If the Ski Pass expires the day of rental, they can only rent the
		// Equipment item for one day
		if (maxDays < 1) {
			prompt = "Ski Pass expires today!\n"
					+ "Equipment can only be rented for 1 day\n"
					+ "would you like to continue?\n"
					+ "y - Continue\n"
					+ "n - Exit to main menu\n";
			rentalDays = 1;
		// If the user has a choice of rental days, get their input
		} else {
			if (maxDays > 3) {
				maxDays = 3;
			}
			prompt = "How many days would you like to rent the Equipment?\n"
					+ "Equipment can be rented for 3 days or until the Ski Pass expires "
					+ "(whichever comes first)\n"
					+ "Your range is 1 - " + maxDays + " (inclusive)\n"
					+ "y - Continue\n"
					+ "n - Exit to main menu\n";
			
			// Loop until a valid number has been entered
			while (true) {
				System.out.println(prompt);
				
				rentalDays = scanner.nextInt();
				scanner.nextLine();
				
				if (rentalDays < 1 || rentalDays > maxDays) {
					System.out.println("Invalid number of days\n");
				} else {
					break;
				}
			}
			
			prompt = "Equipment will be rented for " + rentalDays + " day(s)\n"
					+ "would you like to continue?\n"
					+ "y - Continue\n"
					+ "n - Exit to main menu\n";
		}
		
		// Stores the user's input
		String input;
		
		// Loop until the user enters 'y' or 'n'
		while (true) {
			System.out.println(prompt);
			input = scanner.nextLine();
			
			// If the user enters 'y' create the new Equipment Rental Record
			// with the entered values, and update the corresponding Equipment
			// Inventory Record to have an INUSE value of 1.
			if (input.strip().equals("y")) {
				query = "INSERT INTO bhousmans.equiprental VALUES( " + rentalId + ", "
						+ equipId + ", " + spId + ", SYSDATE, (SYSDATE + " + rentalDays
						+ "), 1)";
				
				try {
					stmt = dbconn.createStatement();
					stmt.executeUpdate(query);
					stmt.close();
					
					query = "UPDATE bhousmans.equipment SET INUSE = 1 WHERE EQUIPID = " + equipId;
					stmt = dbconn.createStatement();
					stmt.executeUpdate(query);
					
					System.out.println("Equipment Rental Record added sucessfully!"
							+ "Rental ID is " + rentalId + "\n");

					// Shut down the connection to the DBMS.
					stmt.close();
					dbconn.close();

				} catch (SQLException e) {
					System.err.println("*** SQLException: Could not fetch query results.");
					System.err.println("\tMessage:   " + e.getMessage());
					System.err.println("\tSQLState:  " + e.getSQLState());
					System.err.println("\tErrorCode: " + e.getErrorCode());			
					System.exit(-1);
				}
				break;
			}
			
			if (input.strip().equals("n")) {
				break;
			}
			
			System.out.println("Invalid option\n");
		}
		
		try {
			dbconn.close();
		} catch (SQLException e) {
			System.err.println("*** SQLException: Could not fetch query results.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			System.exit(-1);
		}	
		return;
	}
	
	/*---------------------------------------------------------------------
    |  Method updateERentRecord
    |
    |  Purpose:  To get the rentalId from the user that is to be updated. The
    |		Record is then updated. The method first prompts the user to enter
    |		the retnalId that is to be updated. The method will verify that the
    |		Record can be updated, repeating the prompt otherwise. The
    |		corresponding Equipment Inventory Record then has its INUSE value
    |		set to 0, showing that it may be rented. The Rental Record then
    |		has its RETURNSTATUS value set to 0, representing that the item
    |		has been returned and the rental is complete.
    |
    |  Pre-condition:  None.
    |
    |  Post-condition:  An Equipment Rental Record with the given rentalId is
    |		updated  within the 'equiprental' table in the Oracle Database 
    |		with the 'RETURNSTATUS' value set to 0. The corresponding
    |		Equipment Inventory Record is updated to have an INUSE value of 0.
    |
    |  Parameters:
    |		scanner - A Scanner object cleared of input to be used to receive
    |			input from the user's keyboard.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
	
	private static void updateERentRecord(Scanner scanner) {
		
		// Magic lectura -> aloe access spell
		String oracleURL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
		// Will hold a database connection to the hard-coded Oracle database
		Connection dbconn = null;

		// Hard-coded Oracle login data for ease of access and lack of sensitive
		// information
		String username = "dylanacarothers";
		String password = "a5382";
				
		// Load the (Oracle) JDBC driver by initializing its base class
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("*** ClassNotFoundException: Error loading Oracle JDBC driver.");
			System.err.println("\tPerhaps the driver is not on the Classpath?");
			System.exit(-1);
		}

		// Make and return a database connection to the Oracle database
		try {
			dbconn = DriverManager.getConnection(oracleURL, username, password);
		} catch (SQLException e) {
			System.err.println("*** SQLException: Could not open JDBC connection.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			System.exit(-1);
		}
		
		// Used to store the statement connection
		Statement stmt = null;
		// Used to store the sql query
		String query;
		// Used to store the sql results
		ResultSet result = null;
		// Stores the prompt to be repeatedly printed
		String prompt = "What is the rental ID you wish to update?\n"
				+ "Enter '-1' to cancel\n";
		// Stores the next rentalId not yet in the 'equiprental' table
		int rentalId = -1;
		// Stores the equipId that is to be updated
		int equipId;
		
		// Keep looping until the user has selected an Equipment Rental to update
		while (true) {
			System.out.println(prompt);
				
			rentalId = scanner.nextInt();
			scanner.nextLine();
			
			// If the user entered '-1' close the connection and exit
			if (rentalId == -1) {
				try {
					dbconn.close();
				} catch (SQLException e) {
					System.err.println("*** SQLException: Could not fetch query results.");
					System.err.println("\tMessage:   " + e.getMessage());
					System.err.println("\tSQLState:  " + e.getSQLState());
					System.err.println("\tErrorCode: " + e.getErrorCode());
					System.exit(-1);
				}	
				return;
			}
			
			query = "SELECT * FROM bhousmans.equiprental WHERE RENTALID = ";
			query += rentalId;
			
			try {
				stmt = dbconn.createStatement();
				result = stmt.executeQuery(query);
				
				// If the query contained a result, then ask the user if it
				// is the Equipment Rental they want to update
				if (result.next()) {
					// If the Rental has already been updated, print a message and loop again
					if (result.getInt("RETURNSTATUS") == 0) {
						System.out.println("Rental log '" + rentalId + "' has already been marked as returned!\n");
					// If the Rental had been deleted, print a message and loop again
					} else if (result.getInt("RETURNSTATUS") < 0) {
						System.out.println("Rental log '" + rentalId + "' has already been marked as deleted!\n");
					// The Rental can be updated, ask the user if they want to continue
					} else {
						System.out.println("  RENTALID    EQUIPID       SPID   DATEFROM     DATETO RETURNSTATUS");
						System.out.println("---------- ---------- ---------- ---------- ---------- ------------");
	
						// Print the attribute values
						String tuple = String.format("%10s ", result.getInt("RENTALID"));
						equipId = result.getInt("EQUIPID");
						tuple += String.format("%10s ", equipId);
						tuple += String.format("%10s ", result.getInt("SPID"));
						tuple += String.format("%10s ", result.getString("DATEFROM").substring(0, 10));
						tuple += String.format("%10s ", result.getString("DATETO").substring(0, 10));
						tuple += String.format("%12s", result.getInt("RETURNSTATUS"));
						System.out.println(tuple);
						
						System.out.println("Is this the Equipment Rental you wish to update?\n"
								+ "y - Continue\n"
								+ "n - Enter a new Equipment ID\n");
						
						// Stores the user's input
						String input;
						
						// Loop until the user enters 'y' or 'n'
						while (true) {
							input = scanner.nextLine();
							
							// If the user enters 'y', update the selected Equipment Rental Record
							// to have a RETURNSTATUS value of 0 and the corresponding Equipment
							// Inventory Record to have an INUSE value of 0
							if (input.strip().equals("y")) {
								stmt.close();
								
								stmt = dbconn.createStatement();
								query = "UPDATE bhousmans.equipment SET INUSE = 0 WHERE EQUIPID = " + equipId;
								stmt.executeUpdate(query);
								
								stmt.close();
								stmt = dbconn.createStatement();
								query = "UPDATE bhousmans.equiprental SET RETURNSTATUS = 0 WHERE RENTALID = " + rentalId;
								stmt.executeUpdate(query);
								
								System.out.println("Update completed sucessfully!\n");

								// Shut down the connection to the DBMS.
								stmt.close();
								dbconn.close();
								return;
							}
							
							if (input.strip().equals("n")) {
								break;
							}
							
							System.out.println("Invalid option\n");
						}
					}
				} else {
					System.out.println("\nError: No record found with that equipment ID!");
				}
				
				stmt.close();
				
			} catch (SQLException e) {
				System.err.println("*** SQLException: Could not fetch query results.");
				System.err.println("\tMessage:   " + e.getMessage());
				System.err.println("\tSQLState:  " + e.getSQLState());
				System.err.println("\tErrorCode: " + e.getErrorCode());
				System.exit(-1);
			}	
		}
	}

	/*---------------------------------------------------------------------
    |  Method deleteERentRecord
    |
    |  Purpose:  To get the rentalId from the user that is to be deleted. The
    |		Record is then updated. The method first prompts the user to enter
    |		the retnalId that is to be deleted. The method will verify that the
    |		Record can be deleted, repeating the prompt otherwise. The Record
    |		then has its RETURNSTATUS value set to -1, representing a deleted
    |		rental, but still retaining the log.
    |
    |  Pre-condition:  None.
    |
    |  Post-condition: The Equipment Rental Record with the given rentalId is
    |		updated within the 'equiprental' table in the Oracle Database with
    |		the 'RETURNVALUE' set to -1 which represents a deleted record, but
    |		retains the log.
    |
    |  Parameters:
    |		scanner - A Scanner object cleared of input to be used to receive
    |			input from the user's keyboard.
    |
    |  Returns:  None.
    *-------------------------------------------------------------------*/
	
	private static void deleteERentRecord(Scanner scanner) {
	
		// Magic lectura -> aloe access spell
		String oracleURL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
		// Will hold a database connection to the hard-coded Oracle database
		Connection dbconn = null;

		// Hard-coded Oracle login data for ease of access and lack of sensitive
		// information
		String username = "dylanacarothers";
		String password = "a5382";
				
		// Load the (Oracle) JDBC driver by initializing its base class
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			System.err.println("*** ClassNotFoundException: Error loading Oracle JDBC driver.");
			System.err.println("\tPerhaps the driver is not on the Classpath?");
			System.exit(-1);
		}

		// Make and return a database connection to the Oracle database
		try {
			dbconn = DriverManager.getConnection(oracleURL, username, password);
		} catch (SQLException e) {
			System.err.println("*** SQLException: Could not open JDBC connection.");
			System.err.println("\tMessage:   " + e.getMessage());
			System.err.println("\tSQLState:  " + e.getSQLState());
			System.err.println("\tErrorCode: " + e.getErrorCode());
			System.exit(-1);
		}		
		
		// Used to store the statement connection
		Statement stmt = null;
		// Used to store the sql query
		String query;
		// Used to store the sql results
		ResultSet result = null;
		// Stores the prompt to be repeatedly printed
		String prompt = "What is the rental ID you wish to delete?\n"
				+ "Enter '-1' to cancel\n";
		// Stores the rentalId that is to be deleted
		int rentalId = -1;
		// Stores the corresponding equipId of the Rental that is to be deleted
		int equipId;
		
		// Keep looping until the user has selected an Equipment Rental to delete
		while (true) {
			System.out.println(prompt);
				
			rentalId = scanner.nextInt();
			scanner.nextLine();
			
			// If the user entered '-1' close the connection and exit
			if (rentalId == -1) {
				try {
					dbconn.close();
				} catch (SQLException e) {
					System.err.println("*** SQLException: Could not fetch query results.");
					System.err.println("\tMessage:   " + e.getMessage());
					System.err.println("\tSQLState:  " + e.getSQLState());
					System.err.println("\tErrorCode: " + e.getErrorCode());
					System.exit(-1);
				}	
				return;
			}
			
			query = "SELECT * FROM bhousmans.equiprental WHERE RENTALID = ";
			query += rentalId;
			
			try {
				stmt = dbconn.createStatement();
				result = stmt.executeQuery(query);
				
				// If the query contained a result, then ask the user if it
				// is the Equipment Rental they want to delete
				if (result.next()) {
					// If the Rental is still in use, print a message and loop again
					if (result.getInt("RETURNSTATUS") == 1) {
						System.out.println("Rental log '" + rentalId + "' is currently in use!\n"
								+ "Mark as returned in order to delete\n");
					// If the Rental has already been deleted, print a message and loop again
					} else if (result.getInt("RETURNSTATUS") < 0) {
						System.out.println("Rental log '" + rentalId + "' has already been marked as deleted!\n");
					// The Rental can be deleted, ask the user if they want to continue
					} else {
						System.out.println("  RENTALID    EQUIPID       SPID   DATEFROM     DATETO RETURNSTATUS");
						System.out.println("---------- ---------- ---------- ---------- ---------- ------------");
	
						// Print the attribute values
						String tuple = String.format("%10s ", result.getInt("RENTALID"));
						equipId = result.getInt("EQUIPID");
						tuple += String.format("%10s ", equipId);
						tuple += String.format("%10s ", result.getInt("SPID"));
						tuple += String.format("%10s ", result.getString("DATEFROM").substring(0, 10));
						tuple += String.format("%10s ", result.getString("DATETO").substring(0, 10));
						tuple += String.format("%12s", result.getInt("RETURNSTATUS"));
						System.out.println(tuple);
						
						System.out.println("Is this the Equipment Rental you wish to delete?\n"
								+ "y - Continue\n"
								+ "n - Enter a new Equipment ID\n");
						
						// Stores the user's input
						String input;
						
						// Loop until the user enters 'y' or 'n'
						while (true) {
							input = scanner.nextLine();
							
							// If the user enters 'y', update the Rental Record to have a
							// RETURNSTATUS value of -1
							if (input.strip().equals("y")) {
								
								stmt.close();
								stmt = dbconn.createStatement();
								query = "UPDATE bhousmans.equiprental SET RETURNSTATUS = -1 WHERE RENTALID = " + rentalId;
								stmt.executeUpdate(query);
								
								System.out.println("Delete completed sucessfully!\n");

								// Shut down the connection to the DBMS.
								stmt.close();
								dbconn.close();
								return;
							}
							
							if (input.strip().equals("n")) {
								break;
							}
							
							System.out.println("Invalid option\n");
						}
					}
				} else {
					System.out.println("\nError: No record found with that equipment ID!");
				}
				
				stmt.close();
				
			} catch (SQLException e) {
				System.err.println("*** SQLException: Could not fetch query results.");
				System.err.println("\tMessage:   " + e.getMessage());
				System.err.println("\tSQLState:  " + e.getSQLState());
				System.err.println("\tErrorCode: " + e.getErrorCode());
				System.exit(-1);
			}	
		}
	}
	
	public static void main(String[] args) {
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

		
		Scanner scanner = new Scanner(System.in);  // use to get user input

        // the string to display to users when they run the program
        String menuStr = "Options: \n"
				+ "1 - Add Member\n"
				+ "2 - Update Member \n"
        		+ "3 - Delete Member \n"
        		+ "4 - Add a ski pass \n"
        		+ "5 - Update ski pass \n"
        		+ "6 - Delete a ski pass\n"
        		+ "7 - Add, update, or delete an equipment inventory record\n"
        		+ "8 - Add, update, or delete an equipment rental record\n"
        		+ "13 - Add Lesson Purchase Record\n"
				+ "14 - Update Lesson Purchase Record\n"
        		+ "15 - Delete Lesson Purchase Record\n 20 - Query Information\n";
				
		// prompt for operations/queries until termination
        while (true) {
           System.out.println(menuStr);
           System.out.println();
           
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
               
               System.out.println("What is your date of birth? (YYYY-MM-DD)");
        	   System.out.println();
               String dobStr = scanner.nextLine();  // store dob
               java.sql.Date dob;
               
               try {
            	   dob = java.sql.Date.valueOf(dobStr);
               }
               catch (Exception e) {
            	   System.out.println("Incorrect date format. Use (YYYY-MM-DD)");
            	   System.out.println();
            	   
            	   continue;
               }
               
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
           else if (input.strip().equals("4")) {
        	   System.out.println("What is the member's email?");
        	   System.out.println();
               String email = scanner.nextLine();  // store email
               
               if (getMidByEmail(email) == -1) {
            	   System.out.println("No member with this email");
            	   System.out.println();
               }
               else {
            	   System.out.println("What kind of pass do you want to purchase? \n"
            	   		+ "1 - 1 day \n"
            	   		+ "2 - 2 day \n"
            	   		+ "3 - 4 day \n"
            	   		+ "4 - season");
            	   System.out.println();
                   String passInput = scanner.nextLine();  // store user input
                   
                   if (passInput.strip().equals("1")) {
                	   addSkipass(email, "1-day");
                   }
                   else if (passInput.strip().equals("2")) {
                	   addSkipass(email, "2-day");
                   }
                   else if (passInput.strip().equals("3")) {
                	   addSkipass(email, "4-day");
                   }
                   else if (passInput.strip().equals("4")) {
                	   addSkipass(email, "season");
                   }
                   else {
                	   System.out.println("Invalid option");
                	   System.out.println();
                   }
               }
           }
           else if (input.strip().equals("5")) {
        	   System.out.println("What is the ski pass ID?");
        	   System.out.println();
               String spid = scanner.nextLine();  // store spid
               
               try {
                   Integer.parseInt(spid);
               } catch (NumberFormatException e) {
            	   System.out.println("Invalid ski pass ID");
            	   System.out.println();
            	   
            	   continue;
               }
               
               System.out.println("What do you want to update the number of uses to?");
        	   System.out.println();
               String count = scanner.nextLine();  // store usage count
               
               updateSkipass(spid, count);
           }
           else if (input.strip().equals("6")) {
        	   System.out.println("What is the ski pass ID?");
        	   System.out.println();
               String spid = scanner.nextLine();  // store spid
               
               try {
                   Integer.parseInt(spid);
               } catch (NumberFormatException e) {
            	   System.out.println("Invalid ski pass ID");
            	   System.out.println();
            	   
            	   continue;
               }
               
               deleteSkipass(spid);
           }
           else if (input.strip().equals("7")) {
        	   editEInvRecord(scanner);
           }
           else if (input.strip().equals("8")) {
        	   editERentRecord(scanner);
           }
           else if (input.strip().equals("13") || (input.strip().equals("14")) || (input.strip().equals("15"))) {
        	   lessonRecord(input.strip(), scanner);          
           } else if (input.strip().equals("20")) {
		askQueryInfo(scanner, dbconn);
	   }
           else {
        	   System.out.println("Invalid option");
        	   System.out.println();
           }

        }

       scanner.close();

	}

	private static void askQueryInfo(Scanner input, Connection dbconn) {
		System.out.println("Here is a list of queries that can be made. Please enter the number corresponding to the query to select it!");
		System.out.println();
		System.out.println("1. For a given member, list all the ski lessons they have purchased, including the number of remaining\r\n"
				+ "sessions, instructor name, and scheduled time");
		System.out.println();
		System.out.println("2. For a given ski pass, list all lift rides and equipment rentals associated with it, along with timestamps\r\n"
				+ "and return status.");
		System.out.println();
		System.out.println("3. List all open trails suitable for intermediate-level skiers, along with their category and connected lifts\r\n"
				+ "that are currently operational.");
		System.out.println();
		System.out.println("4. For a given lift, see what trails you can access, and whether they are active or not!");
		String choiceStr = input.nextLine();
		int choice;
		try {
			choice = Integer.parseInt(choiceStr);
		} catch (NumberFormatException e) {
			System.out.println("Invalid input - please enter a number between 1 - 4!");
			return;
		}
		if (choice == 1) {
			System.out.print("Please enter a valid member id: ");
			String mid = input.nextLine();
			getSkiLesson(dbconn, mid);
		} else if (choice == 2) {
			System.out.print("Please enter a valid ski pass id: ");
			String spid = input.nextLine();
			getSkiPassInfo(dbconn, spid);
		} else if (choice == 3) {
			getOpenTrails(dbconn);
		} else if (choice == 4) {
			System.out.print("Please enter a valid skilift name: ");
			String theInfo = input.nextLine();
			getTrivial(dbconn, theInfo);
		} else {
			System.out.println("Invalid input - please enter a number between 1 - 4!");
		}
		
	}
	
	private static void getSkiLesson(Connection dbconn, String member) {
		// bhousmans.member -> bhousmans.lessonreg (list of skilessons (orderid, lessoncode), remainingsessions) -> bhousmans.lesson (schedule) -> bhousmans.employee (firstname, lastname)          
		int mid;
		try {
			mid = Integer.parseInt(member);
		} catch (NumberFormatException e) {
			System.out.println("Invalid memberID entered. Please enter a valid member ID.");
			return;
		}
		
		String query = "SELECT count(*) from bhousmans.member where mid = " + mid;
		ResultSet answer = null;
		Statement stmt = null;
		try {
			stmt = dbconn.createStatement();
			answer = stmt.executeQuery(query);
			if (answer.next()) {
				if (answer.getInt(1) == 0) {
					System.out.println("Invalid memberID entered. Please enter a valid member ID.");
					return;
				}
			} else {throw new SQLException();}
			
			query = "select lg.orderid, lg.lessoncode, lg.remainingsessions, l.schedule, e.firstname, e.lastname "
					+ "from bhousmans.lessonreg lg join bhousmans.lesson l on lg.lessoncode = l.lessoncode "
					+ "left join bhousmans.employee e on l.eid = e.eid where lg.mid = " + mid;
			answer = stmt.executeQuery(query);
			int empty = 1;
			System.out.println("ORDERID\tLESSON\tSCHEDULE\tSESSIONS_REMAINING\tINSTRUCTOR");
			System.out.println("---------------------------------------");
			while (answer.next()) {
				empty--;
			    int orderId = answer.getInt("orderid");
			    int lessonCode = answer.getInt("lessoncode"); // would be better for lesson code to be String
			    int remainingSessions = answer.getInt("remainingsessions");
			    String schedule = answer.getString("schedule");
			    String firstName = answer.getString("firstname");
			    String lastName = answer.getString("lastname");

			    System.out.print(orderId + "\t");
			    System.out.print(lessonCode + "\t");
			    System.out.print(schedule + "\t");
			    System.out.print(remainingSessions + "\t");
			    System.out.print(firstName + " " + lastName + "\n");
			    
			}
			if (empty == 1) {
				System.out.println("Given member " + member + " doesn't have any purchased ski lessons.");
				return;
			}
			
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}	
		
	}
	
	
	private static void getSkiPassInfo(Connection dbconn, String spid) {
		// bhousmans.skipass -> bhousmans.liftuse (nouses) -> bhousmans.lift (lname, status)
		// bhousmans.skipass -> bhousmans.equiprental (datefrom, dateto) -> bhousmans.equipment (equiptype, slength, inUse)
	
		int sid;
		try {
			sid = Integer.parseInt(spid);
		} catch (NumberFormatException e) {
			System.out.println("Invalid memberID entered. Please enter a valid member ID.");
			return;
		}
		
		String query = "SELECT count(*) from bhousmans.skipass where mid = " + sid;
		ResultSet answer = null;
		Statement stmt = null;
		try {
			stmt = dbconn.createStatement();
			answer = stmt.executeQuery(query);
			if (answer.next()) {
				if (answer.getInt(1) == 0) {
					System.out.println("Invalid skipass number entered. Please enter a valid skipass ID.");
					return;
				}
			} else {throw new SQLException();}
			
			query = "select lu.nouses, l.lname, l.status from bhousmans.liftuse lu "
					+ "join bhousmans.lift l on l.lname = lu.lname where lu.spid = " + spid; // liftuse should have a timestamp value
			answer = stmt.executeQuery(query);
			System.out.println("LIFT_NAME\tTIMES_USED\tSTATUS");
			System.out.println("---------------------------------------");
			int empty = 1;
			while (answer.next()) {
				empty--;
				int uses = answer.getInt("nouses");
				String lname = answer.getString("lname");
				int status = answer.getInt("status");
				System.out.print(lname + "\t");
				System.out.print(uses + "\t");
				if (status == 1) {System.out.print("ACTIVE\n");}
				else {System.out.print("INACTIVE\n");}
				
			}
			if (empty == 1) {
				System.out.println("Given ski pass " + spid + " hasn't used the lift.");
			}
			empty = 1;
			query = "select er.rentalid, er.datefrom, er.dateto, eq.equiptype, eq.slength from bhousmans.equiprental er "
					+ "join bhousmans.equipment eq on er.equipid = eq.equipid where er.spid = " + spid;
			answer = stmt.executeQuery(query);
			System.out.println("RENTAL ID\tEQUIPMENT\tSIZE\tDATE_BORROWED\tRETURN_BY");
			System.out.println("---------------------------------------");
			while (answer.next()) {
				empty--;
				int rid = answer.getInt("rentalid");
			    Date dateFrom = answer.getDate("datefrom");
			    Date dateTo = answer.getDate("dateto");
			    String equipment = answer.getString("equiptype");
			    int size = answer.getInt("slength");
			    System.out.print(rid + "\t");
			    System.out.print(equipment + "\t");
			    if (equipment.toLowerCase().equals("boots")) {System.out.print(((float) size / 10)+"\t");}
			    else {System.out.print(size + "\t");}
			    System.out.print(dateFrom + "\t");
			    System.out.print(dateTo + "\n");
			    // might need field for due
			}
			if (empty == 1) {
				System.out.println("Given ski pass " + spid + " hasn't borrowed any equipment.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
	
	
	
	}
	
	private static class trailInfo {
		public int start, end;
		public String tname, category;
		public ArrayList<String> lname;
		
		public trailInfo(int s, int e, String t, String c) {
			start = s;
			end = e;
			tname = t;
			category = c;
			lname = new ArrayList<String>();
		}
		
		public void addLift(String l) {
			lname.add(l);
		}
	}
	
	private static void getOpenTrails(Connection dbconn) {
		// bhousmans.trail (where difficulty = intermediate & status = 1) (tname, startloc, endloc, category) ->
		// bhousmans.lifttrailconn -> bhousmans.lift (lname) where status = 1
		String query = "select t.tname, t.startloc, t.endloc, t.category, l.lname, l.status from bhousmans.trail t left join bhousmans.lifttrailconn lt"
				+ " on t.tname = lt.tname left join bhousmans.lift l on l.lname = lt.lname "
				+ "where t.status = 1 and t.difficulty = 'Intermediate'";
		Statement stmt;
		HashMap<String, trailInfo> map = new HashMap<>();
		try {
			stmt = dbconn.createStatement();
			ResultSet answer = stmt.executeQuery(query);
			int empty = 1;
			System.out.println("TRAIL\tSTART_LOCATION\tEND_LOCATION\tCATEGORY\tLIFT_ACCESSED_BY");
			System.out.println("---------------------------------------");
			while (answer.next()) {
				empty--;
				String tname = answer.getString("tname");
				String lname = answer.getString("lname");
				int start = answer.getInt("startloc");
				int end = answer.getInt("endloc");
				String cat = answer.getString("category");
				Integer status = answer.getObject("status", Integer.class);
				if (map.containsKey(tname)) {
					if (status != null && status.equals(1)) {
						map.get(tname).addLift(lname);
					}
				} else {
					trailInfo t = new trailInfo(start, end, tname, cat);
					map.put(tname, t);
					if (status != null && status.equals(1)) {
						t.addLift(lname);
					}
				}
			}
			if (empty == 1) {
				System.out.println("There are no active intermediate trails right now.");
				return;
			}
			
			for (Map.Entry<String, trailInfo> entry : map.entrySet()) {
				trailInfo t = entry.getValue();
				System.out.print(t.tname + "\t");
				System.out.print(t.start + "\t");
				System.out.print(t.end + "\t");
				System.out.print(t.category + "\t");
				int first = 0;
				for (String lname : t.lname) {
					if (first == 0) {System.out.print(lname + "\n");}
					else {System.out.print("\t\t\t\t" + lname + "\n");}
					first--;
					
				}
				System.out.println();
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private static void getTrivial(Connection dbconn, String lname) {
		// given a lift , list out all connected lifts/trails, their timings, status, and start end loc
		// bhousmans.lift -> lifttrailconn -> trail (tname, startloc, endloc, difficulty, category, status)
		String query = "SELECT count(*) from bhousmans.lift where lname = '" + lname + "'";
		ResultSet answer = null;
		Statement stmt = null;
		try {
			stmt = dbconn.createStatement();
			answer = stmt.executeQuery(query);
			if (answer.next()) {
				if (answer.getInt(1) == 0) {
					System.out.println("Lift doesn't exist. Please enter a valid skilift.");
					return;
				}
			} else {throw new SQLException();}
			
			query = "select l.opentime, l.closetime, l.status, t.tname, t.difficulty, t.category, t.status as tstat, t.startloc, t.endloc "
					+ "from bhousmans.lift l join bhousmans.lifttrailconn lt on l.lname = lt.lname "
					+ "join bhousmans.trail t on lt.tname = t.tname where UPPER(l.lname) = UPPER('" + lname + "')";
			answer = stmt.executeQuery(query);
			int empty = 1;
			while (answer.next()) {
				if (empty == 1) {
					int open = answer.getInt("opentime");
					int close = answer.getInt("closetime");
					int stat = answer.getInt("status");
					String statStr;
					if (stat == 1) {statStr = "ACTIVE";}
					else {statStr = "INACTIVE";}
					System.out.println("The chosed lift " + lname + " opens at " + open + " and closes at " + close + " and has the status " + statStr + "." );
					System.out.println("TRAIL\tSTART LOCATION\tEND LOCATION\tCATEGORY\tDIFFICULTY\tSTATUS");
					System.out.println("---------------------------------------");
				}
				empty--;
				String tname = answer.getString("tname");
				String cat = answer.getString("category");
				String diff = answer.getString("difficulty");
				int status = answer.getInt("tstat");
				int start = answer.getInt("startloc");
				int end = answer.getInt("endloc");
				System.out.print(tname + "\t");
				System.out.print(start + "\t");
				System.out.print(end + "\t");
				System.out.print(cat + "\t");
				System.out.print(diff + "\t");
				if (status == 1) {System.out.print("ACTIVE\n");}
				else {System.out.print("INACTIVE\n");}
			}
			if (empty == 1) {
				System.out.println("This lift doesn't currently service any trails.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
