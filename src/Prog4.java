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
	
	private static int privateOrGroup(Connection dbconn, Statement stmt, Scanner scanner, String query) {
		int retVal = -999;
		try {
			ResultSet rs = null;
			System.out.println("What day of the week would you like your first lesson to be?");
	        System.out.println("Enter an integer: Sun=1, Mon=2, Tue=3, Wed=4, Thur=5, Fri=6, Sat=7");
	 	   	System.out.println();
	        int lessonCode = Integer.parseInt(scanner.nextLine());
	        if(lessonCode < 1 || lessonCode > 7) {
	 	    	while(true) {
	 	    		System.out.println("Invalid input for lesson\nEnter an integer: Sun=1, Mon=2, Tue=3, Wed=4, Thur=5, Fri=6, Sat=7");
	         	    System.out.println();
	         	    lessonCode = Integer.parseInt(scanner.nextLine());
	 	    	}
	 	    }
	        
	        System.out.println("Would you like the lesson to be private or with a group? (enter p or g)");
	    	System.out.println();
	        String input1 = scanner.nextLine();
	        if(input1.equals("p")) {
	        	lessonCode *= -1;
	        }
	        
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
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);  // use to get user input

        // the string to display to users when they run the program
        String menuStr = "Options: \n"
				+ "1 - Add Member\n"
        		+ "13 - Add Lesson Purchase Record\n"
				+ "14 - Update Lesson Purchase Record\n"
        		+ "15 - Delete Lesson Purchase Record\n";
				
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
           else if (input.strip().equals("13") || (input.strip().equals("14")) || (input.strip().equals("15"))) {
        	   lessonRecord(input.strip(), scanner);          
           }
           else {
        	   System.out.println("Invalid option");
        	   System.out.println();
           }

        }

       scanner.close();

	}

}
