// export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH}
// format date values as dd-mon-yr where dd is two digits for the day, mon is the 3 character
// abbreviation of the month, and yr is the last two digits of the year


import java.io.*;
import java.util.*;
import java.sql.*;

public class Prog4Insert {
	private static String queryStr(String fileName, String[] field) {
		// {"lift", "trail", "lifttrailconn", "employee",
		//"equiprental", "liftuse", "lessonreg", "equipment",
		//"skipass", "member", "lesson"};
		String query = "";
		
		switch(fileName) {
		case "lift":
			query = String.format("insert into bhousmans.%s values (%s, %d, %d, %d)",
					fileName.toLowerCase(),
					"'" + field[0] + "'", Integer.parseInt(field[1]),
					Integer.parseInt(field[2]), Integer.parseInt(field[3]));
			break;
		case "trail":
			query = String.format("insert into bhousmans.%s values (%s, %d, %d, %d, %s, %s)",
					fileName.toLowerCase(),
					"'" + field[0] + "'", Integer.parseInt(field[1]),
					Integer.parseInt(field[2]), Integer.parseInt(field[3]),
					"'" + field[4] + "'", "'" + field[5] + "'");
			break;
		case "lifttrailconn":
			query = String.format("insert into bhousmans.%s values (%s, %s)",
					fileName.toLowerCase(),
					"'" + field[0] + "'", "'" + field[1] + "'");
			break;
		case "liftuse": 
			query = String.format("insert into bhousmans.%s values (%s, %d, %d, %s)",
					fileName.toLowerCase(),
					"'" + field[0] + "'",
					Integer.parseInt(field[1]), Integer.parseInt(field[2]),
					"'" + field[3] + "'");
			break;
		case "equiprental":
			query = String.format("insert into bhousmans.%s values (%d, %d, %d, %s, %s)",
					fileName.toLowerCase(),
					Integer.parseInt(field[0].trim().replaceAll("[^\\d]", "")), Integer.parseInt(field[1]),
					Integer.parseInt(field[2]), "'" + field[3] + "'",
					"'" + field[4] + "'");
			break;
		case "employee":
			query = String.format("insert into bhousmans.%s values (%d, %s, %s, %s, %d, %s, %s, %d)",
					fileName.toLowerCase(),
					Integer.parseInt(field[0].trim().replaceAll("[^\\d]", "")), "'" + field[1] + "'",
					"'" + field[2] + "'", "'" + field[3] + "'",
					Integer.parseInt(field[4]), "'" + field[5] + "'",
					"'" + field[6] + "'", Integer.parseInt(field[7]));
			break;
		case "lessonreg":
			query = String.format("insert into bhousmans.%s values (%d, %d, %d, %d, %d)",
					fileName.toLowerCase(),
					Integer.parseInt(field[0].trim().replaceAll("[^\\d]", "")), Integer.parseInt(field[1]),
					Integer.parseInt(field[2]), Integer.parseInt(field[3]),
					Integer.parseInt(field[4]));
			break;
		case "equipment":
			query = String.format("insert into bhousmans.%s values (%d, %s, %d, %d)",
					fileName.toLowerCase(),
					Integer.parseInt(field[0].trim().replaceAll("[^\\d]", "")), "'" + field[1] + "'",
					Integer.parseInt(field[2]), Integer.parseInt(field[3]));
			break;
		case "skipass":
			query = String.format("insert into bhousmans.%s values (%d, %d, %s, %s, %d, %.2f)",
					fileName.toLowerCase(),
					Integer.parseInt(field[0].trim().replaceAll("[^\\d]", "")), Integer.parseInt(field[1]),
					"'" + field[2] + "'", "'" + field[3] + "'",
					Integer.parseInt(field[4]), Double.parseDouble(field[5]));
			break;
		case "member":
			query = String.format("insert into bhousmans.%s values (%d, %s, %s, %s, %s, %s, %s, %d)",
					fileName.toLowerCase(),
					Integer.parseInt(field[0].trim().replaceAll("[^\\d]", "")), "'" + field[1] + "'",
					"'" + field[2] + "'", "'" + field[3] + "'",
					"'" + field[4] + "'", "'" + field[5] + "'",
					"'" + field[6] + "'", Integer.parseInt(field[7]));
			break;
		case "lesson":
			query = String.format("insert into bhousmans.%s values (%d, %d, %.2f, %d, %s)",
					fileName.toLowerCase(),
					Integer.parseInt(field[0].trim().replaceAll("[^\\d]", "")) * -1, Integer.parseInt(field[1]),
					Double.parseDouble(field[2]), Integer.parseInt(field[3]),
					"'" + field[4] + "'");
			break;
		default:
			break;
		}
		
		return query;
	}
	
	private static void readCSVFile (String fileName, Connection dbconn)
	{
	    File fileRef = null;                     // provides exists() method
	    BufferedReader reader = null;            // provides buffered text I/O
	
	                // If the CSV file doesn't exist, we can't proceed.
	
	    try {
	        fileRef = new File(fileName + ".csv");
	        if (!fileRef.exists()) {
	            System.out.println("PROBLEM:  The input file"
	                             + "does not exist in the current directory.");
	            System.out.println("          Create or copy the file to the "
	                             + "current directory and try again.");
	            System.exit(-1);
	        }
	    } catch (Exception e) {
	        System.out.println("I/O ERROR: Something went wrong with the "
	                         + "detection of the CSV input file.");
	        System.exit(-1);
	    }
	
	                // Read the content of the CSV file into an ArrayList
	                // of DataRecord objects.
	
	    try {
	
	        reader = new BufferedReader(new FileReader(fileRef));
	
	        Statement stmt = null;
	        String line = null;  // content of one line/record of the CSV file
	        String query = null;
	        // reader.readLine();  // read the first line of the csv and ignore it
	        while((line = reader.readLine()) != null) {
	            String[] field = line.split(",");
	            try {
					stmt = dbconn.createStatement();
	            } catch (SQLException e) {
					// TODO Auto-generated catch block
					System.out.println("can't create stmt");
					System.exit(-1);
				}
					query = queryStr(fileName, field);
					// System.out.println(query);
					try {
						stmt.executeQuery(query);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						System.out.println("can't execute query");
						System.out.println(query);
						System.exit(-1);
					}
					try {
						stmt.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						System.out.println("can't close stmt");
						System.exit(-1);
					}
	            
	        }
	
	    } catch (IOException e) {
	        System.out.println("I/O ERROR: Couldn't open, or couldn't read "
	                         + "from, the CSV file.");
	        System.exit(-1);
	    }
	
	                // We're done reading the CSV file, time to close it up.
	
	    try {
	        reader.close();
	    } catch (IOException e) {
	        System.out.println("VERY STRANGE I/O ERROR: Couldn't close "
	                         + "the CSV file!");
	        System.exit(-1);
	    }
	}
	
	
	public static void main (String [] args)
    {

        final String oracleURL =   // Magic lectura -> aloe access spell
                        "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";

        String username = null,    // Oracle DBMS username
                password = null;    // Oracle DBMS password


         if (args.length == 2) {    // get username/password from cmd line args
             username = args[0];
             password = args[1];
         } else {
             System.out.println("\nUsage:  java JDBC <username> <password>\n"
                              + "    where <username> is your Oracle DBMS"
                              + " username,\n    and <password> is your Oracle"
                              + " password (not your system password).\n");
             System.exit(-1);
         }


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
        
        // change the names of the files that you would like to insert here
        String[] files = {"lesson"};
        for(int i = 0; i < files.length; i++) {
        	System.out.println(files[i]);
        	readCSVFile(files[i], dbconn);
        }
        System.out.println("Added records to the tables!");
        
        try {
			dbconn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
}
