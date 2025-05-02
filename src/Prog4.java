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
	            
            System.out.println("member inserted");
		
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
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);  // use to get user input

        // the string to display to users when they run the program
        String menuStr = "Options: \n"
				+ "1 - Add Member";
				
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
           else {
        	   System.out.println("Invalid option");
        	   System.out.println();
           }

        }

       scanner.close();

	}

}
