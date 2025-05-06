/*
 *    Assignment:  Program #4 --  Database Design and Implementation
 *
 *      Authors:  Gabe Barros, Aarush Parvataneni, Dylan Carothers, Bronson Housmans
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
import java.util.Scanner;

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
    will determine the cost of the ski pass. Includes error checking if there is no
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
				cost = 99.99;
			}
			else if (passType.equals("2-day")) {
				cost = 149.99;
			}
			else if (passType.equals("4-day")) {
				cost = 215.49;
			}
			else {
				cost = 499.99;
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
    pass is expired, or has no remaining uses (20 uses has been picked for
    the max amount of uses, since it was not specified). The method utilizes
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
    or still has remaining uses left (20 use max). 
    
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
			String query =   // check if there are any tuples
				    "SELECT spid FROM bhousmans.skipass WHERE spid = " + spid + 
				    " AND notimesused > 20 AND expirydate < SYSDATE";
	
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
	
private static void editEInvRecord(Scanner scanner) {
		
		String menuStr = "What would you like to do?\n"
				+ "1 - Add Equipment Inventory Record\n"
				+ "2 - Update Equipment Inventory Record\n"
				+ "3 - Delete Equipment Inventory Record\n"
				+ "4 - Return to main menu\n";
		
		String input;
		
		while (true) {
			System.out.println(menuStr);
			input = scanner.nextLine();  // store user input
			
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
		
		Statement stmt = null;
		String query = null;
		ResultSet result = null;
		int equipId = -1;
		
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
		
		String prompt = "What equipment type would you like to add?\n"
				+ "1 - Boots\n"
				+ "2 - Poles\n"
				+ "3 - Alpine Skis\n"
				+ "4 - Snowboard\n"
				+ "5 - Protective Gear\n";
		
		String input;
		
		String[] types = {"boots", "poles", "skis", "snowboard", "protective"};
		String type;
				
		while (true) {
			System.out.println(prompt);
			
			input = scanner.nextLine();  // store user input
			
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
		
		String range;
		int slength = -1;
		
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
		
		if (type.equals("protective")) {
			slength = 0;
		}

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
		
		Statement stmt = null;
		String query;
		ResultSet result = null;
		String prompt = "What is the equipment ID of the item you wish to update?\n"
				+ "Enter '-1' to cancel\n";
		int equipId = -1;
		boolean continuePrompt = true;
		
		while (continuePrompt) {
			System.out.println(prompt);
				
			equipId = scanner.nextInt();
			scanner.nextLine();
			
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
				
				if (result.next()) {
					
					if (result.getInt("INUSE") == 1) {
						System.out.println("Equipment '" + equipId + "' is currently rented!\n"
								+ "Please mark as returned to update the equipment!\n");
					} else if (result.getInt("INUSE") < 0) {
						System.out.println("Equipment '" + equipId + "' has been retired!\n"
								+ "Retired equipment cannot be updated!\n");
					} else {

						System.out.println("   EQUIPID EQUIPTYPE               SLENGTH      INUSE");
						System.out.println("---------- -------------------- ---------- ----------");
	
						// Use next() to advance cursor through the result tuples and
						// print their attribute values
						String tuple = String.format("%10s ", result.getInt("EQUIPID"));
						tuple += String.format("%-20s ", result.getString("EQUIPTYPE"));
						tuple += String.format("%10s ", result.getInt("SLENGTH"));
						tuple += String.format("%10s ", result.getInt("INUSE"));
						System.out.println(tuple);
						
						System.out.println("Is this the Equipment you wish to update?\n"
								+ "A new EQUIPID will be assigned and the previous EQUIPID will be retired!\n"
								+ "y - continue\n"
								+ "n - Enter a new Equipment ID\n");
						
						String input;
						
						while (true) {
							input = scanner.nextLine();  // store user input
							
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

			stmt = dbconn.createStatement();
			
			query = "UPDATE bhousmans.equipment SET INUSE = -2 WHERE EQUIPID = " + equipId;
			
			addEInvRecord(scanner);

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
		
		Statement stmt = null;
		String query;
		ResultSet result = null;
		String prompt = "What is the equipment ID of the item you wish to delete?\n"
				+ "Enter '-1' to cancel\n";
		int equipId = -1;
		
		while (true) {
			System.out.println(prompt);
				
			equipId = scanner.nextInt();
			scanner.nextLine();
			
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
				
				if (result.next()) {
					
					if (result.getInt("INUSE") == 1) {
						System.out.println("Equipment '" + equipId + "' is currently rented!\n"
								+ "Please mark as returned to delete the equipment!\n");
					} else if (result.getInt("INUSE") < 0) {
						System.out.println("Equipment '" + equipId + "' has already been retired!\n"
								+ "Retired equipment cannot be deleted!\n");
					} else {

						System.out.println("   EQUIPID EQUIPTYPE               SLENGTH      INUSE");
						System.out.println("---------- -------------------- ---------- ----------");
	
						// Use next() to advance cursor through the result tuples and
						// print their attribute values
						String tuple = String.format("%10s ", result.getInt("EQUIPID"));
						tuple += String.format("%-20s ", result.getString("EQUIPTYPE"));
						tuple += String.format("%10s ", result.getInt("SLENGTH"));
						tuple += String.format("%10s ", result.getInt("INUSE"));
						System.out.println(tuple);
						
						System.out.println("Is this the Equipment you wish to delete?\n"
								+ "y - continue\n"
								+ "n - Enter a new Equipment ID\n");
						
						String input;
						
						while (true) {
							input = scanner.nextLine();  // store user input
							
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
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);  // use to get user input

        // the string to display to users when they run the program
        String menuStr = "Options: \n"
				+ "1 - Add Member \n"
        		+ "2 - Update Member \n"
        		+ "3 - Delete Member \n"
        		+ "4 - Add a ski pass \n"
        		+ "5 - Update ski pass \n"
        		+ "6 - Delete a ski pass\n"
        		+ "7 - Add, update, or delete an equipment inventory record\n";
				
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
           else {
        	   System.out.println("Invalid option");
        	   System.out.println();
           }

        }

       scanner.close();

	}
}