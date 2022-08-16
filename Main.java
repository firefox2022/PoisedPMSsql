
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/** 
 * The program is meant to manage construction projects in a small engineering company.  
 * <p>
 * The projects' data is retrieved from a SQL DB PoisePMS, modified, and saved back in the DB.

 *  @author ATraut
 * */
public class Main 
/**
 * Main class to manage the projects: add all the projects in the ArrayList. The menu allows a user to choose what to do.
 * */
{
	public static void main(String[] args) throws IOException, SQLException 
	
	{
		ResultSet dataset;
		Statement statement = null;
		Connection connection = null;
		
		/**
		 * menu allows user to choose a function 
		 * */
		
		Scanner s = new Scanner(System.in);
				
		while (true)			
			{
			String textBlock = """				
					0. Search for a project or generate a list				
					1. Add a new project 
					2. Search for a person or generate a list 
					3. Edit project details  
					4. Edit person details 
					5. Generate invoice
					6. Delayed projects' list 
					7. Incomplete projects' list				
					8. Add a new person
					'exit' = Exit """;			
			System.out.println("\n"+textBlock);
			String menu = s.next();
			
			switch(menu)
			{		
			/** adds a new project to the DB PoisePMS */		
			case "0": 
				try 
				{	
					/**
					 * call the function to connect to the data base
					 * */			
					connection = connectDB();
					
					statement = connection.createStatement();
					System.out.println("To search a project by its number, enter 'num', to search a project by its name, enter 'name', to display a list, enter 'l': ");
					String choice = s.next();
					/**
					 * call the function to search for a project
					 * */
					projectSearch(statement, s, choice);
				}			
				catch(SQLException e)
				{
					e.printStackTrace();
				}
				finally 
				{
					connection.close();
					statement.close();
				}
				break;
				
			/** adds new project data to the DB PoisePMS */				
			case "1": 
				try 
				{
					/**
					 * call the function to connect to the data base
					 * */
					connection = connectDB();
					
					statement = connection.createStatement();
					/**
					 * call the function to add a project to the data base
					 * */
					addProject(statement, s);																					
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
				finally 
				{
					connection.close();
					statement.close();
				}
				break;	
				
			/** searches DB PoisePMS for a person or generates the list of people involved in the company */			
			case "2":			
				try 
				{
					/**
					 * call the function to connect to the data base
					 * */
					connection = connectDB();							
					statement = connection.createStatement();
					System.out.println("To search for a person, enter '1', to generate a list of persons, enter '2': ");
					String option = s.next();
					/**
					 * call the function to search for a person
					 * */
					searchPerson(statement, s, option);
				}
				
				catch(SQLException e)
				{
					e.printStackTrace();
				}
				finally 
				{
					connection.close();
					statement.close();
				}						
				break;
				
			/** edits details of a project in the DB */			
			case "3":
				try 
				{
					/**
					 * call the function to connect to the data base
					 * */
					connection = connectDB();				
					statement = connection.createStatement();
					/**
					 * call the function to edit details of a project
					 * */
					editProjectDetails(statement, s);						
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}			
				finally 
				{
					connection.close();
					statement.close();
				}
				break;
				
			/** edits details of a person in the DB */			
			case "4":
				try 
				{
					/**
					 * call the function to connect to the data base
					 * */
					connection = connectDB();				
					statement = connection.createStatement();
					/**
					 * call the function to edit details of a person
					 * */
					editPersonDetails(statement, s);				
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}			
				finally 
				{
					connection.close();
					statement.close();
				}
				break;
				
			/** generates invoice account is an outstanding balance. 
			 * If the a is settled, the appropriate message is printed and text file is generated */			
			case "5":
				try 
				{
					/**
					 * call the function to connect to the data base
					 * */
					connection = connectDB();				
					statement = connection.createStatement();
					System.out.println("Enter project number: ");
					String projNum = s.next();
					
					/**
					 * execute update to calculate the balance
					 */
					
					statement.executeUpdate("UPDATE projects SET balance = totSum - paidSum");							
					String genInvoice = "SELECT *  FROM projects LEFT JOIN persons on projects.clientID = persons.personID WHERE projects.projectID = "+projNum;			
					dataset = statement.executeQuery(genInvoice);				
					while (dataset.next())
					{
						/**
						 * call the function to calculate the outstanding balance or print out the statement in the text file
						 * */
						getBalance(dataset);
					}
				
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
				break;
				
				/** prints out the projects past their due date */			
			case "6":
				try			
				{
					connection = connectDB();				
					statement = connection.createStatement();
					Date currentDate = new Date();		
					DateFormat formatter = new SimpleDateFormat ("YYYY-MM-dd");
					String todayDate = formatter.format(currentDate);			
					String delayedProjects = "SELECT * from projects WHERE dueDate < "+"'"+todayDate+"'"; // todayDate in in single quote according to SQL Syntax (it is not a string) 
					dataset = statement.executeQuery(delayedProjects);
					while (dataset.next())
					{
						printDelayIncompleteProject(dataset);
					}				
				}
				catch(SQLException e)
				{
					e.printStackTrace();
				}
				break;
				
				/** prints out the projects, where there is an outstanding balance */
			case "7":
				try 
				{
					connection = connectDB();				
					statement = connection.createStatement();		
					String incompleteProjects = "SELECT * from projects WHERE totSum > paidSum ";
					dataset = statement.executeQuery(incompleteProjects);				
					while (dataset.next())
					{
						printDelayIncompleteProject(dataset);
					}
				
					}
					catch(SQLException e)
					{
						e.printStackTrace();
					}
					break;
				
				/** captures details of a new person and adds them to the DB PoisePMS */			
			case "8":
				try 
				{
					/**
					 * call the function to connect to the data base
					 * */
					connection = connectDB();				
					statement = connection.createStatement();
					/**
					 * call the function to add new person details to the data base
					 * */
					addPeson(statement, s);								
				}			
				catch(SQLException e)
				{
					e.printStackTrace();
				}
				finally 
				{
					connection.close();
					statement.close();
				}
				break;
			case "exit":
				return;
			}
		}		
}
	/**
	 * the function calculates the outstanding balance for the project of interest
	 * @param dataset
	 * @throws SQLException
	 * @throws IOException
	 */
	private static void getBalance(ResultSet dataset) throws SQLException, IOException {
		if(dataset.getFloat("Balance") > 0)					
			{
				System.out.println("\nProject ID: "+dataset.getInt("projectID") + "\nClient ID: "
			+dataset.getString("clientID")+"\nERF number: "
			+dataset.getString("ErfNum")+"\nTotal: "
			+dataset.getDouble("totSum")+"\nAmount paid so far: "+dataset.getDouble("paidSum")+"\nDue date: "
			+dataset.getString("dueDate")+"\nLast Name: "+ dataset.getString("lName")+"\nFirst Name: "
			+dataset.getString("fName")+"\nTel number: "+dataset.getString("telNum")+"\nemail: "+dataset.getString("email")+"\nAddress: "+dataset.getString("address")+
			"\nOutstanding bill: "+Math.round(dataset.getFloat("Balance"))*100/100.0);
			}

		else if (dataset.getFloat("Balance") < 0.1)
		{
			System.out.println("The bill is settled. Check the completed projects file");
			String output = invoiceString(dataset);											
			String path = System.getProperty("user.dir");
			
			// to combine path with text file the backslash should be double in Windows	
			
			FileWriter fileWriter = new FileWriter(path+"\\completedProjects.txt");						
			fileWriter.write(output);
			fileWriter.close();
		}
	}
	/**
	 * the function adds person's details from a user's input
	 * @param statement
	 * @param s
	 * @throws SQLException
	 */
	private static void addPeson(Statement statement, Scanner s) throws SQLException {
		System.out.println("Enter person ID: ");
		String personID = s.next().toUpperCase();
					
		System.out.println("Enter role: ");
		String role = s.next().toLowerCase();
												
		System.out.println("Enter last name: ");
		String lName = s.next().toLowerCase();
		
		System.out.println("Enter first name: ");
		String fName = s.next().toLowerCase();
												
		System.out.println("Enter tel number: ");
		String telNum = s.next().toLowerCase();
		
		System.out.println("Enter email: ");
		String email = s.next().toLowerCase();
		
		System.out.println("Enter address using underscore (_) instead of spaces: ");
		String address = s.next().toLowerCase();
		
		System.out.println("Enter projectID: ");
		String projectID = s.next();
		
		String addPerson = "INSERT INTO persons VALUES("+"'"+personID+"'"+", "+ "'"+role+"'"+", " +"'"+lName+"'"+", "+"'"+fName+"'"+", "+"'"+
				telNum+"'"+", "+"'"+email+"'"+", "+"'"+address+"'"+", "+projectID+")";														
		statement.executeUpdate(addPerson);
	}
	
	/**
	 * the function searches for a person in the data base using last name or prints out a list of people involved with the company
	 * @param statement
	 * @param s
	 * @param option
	 * @throws SQLException
	 */
	private static void searchPerson(Statement statement, Scanner s, String option) throws SQLException {
		ResultSet dataset;
		if (option.equals("1"))
		{
			System.out.println("Enter the person's last name: ");
			String lname = s.next();
			dataset = statement.executeQuery("SELECT personID, role, lName, fName, email, telNum, address "
					+ "FROM persons WHERE lName = "+"'"+lname+"'");
			while (dataset.next())			
				{			
				System.out.println("\nPerson ID: "+dataset.getString("personID") + "\nRole: " +dataset.getString("role")
				+"\nLast name: "+ dataset.getString("lName")+"\nFirst name: "+dataset.getString("fName")+
				"\nemail: "+dataset.getString("email")+"\nTel number: "+dataset.getString("telNum")+"\nAddress: "+dataset.getString("address"));
				}
		}
		else if (option.equals("2"))
		{
		dataset = statement.executeQuery("SELECT personID, role, lName, fName, email, telNum, address FROM persons");
		while (dataset.next())			
			{			
			System.out.println("\nPerson ID: "+dataset.getString("personID") + "\nRole: " +dataset.getString("role")
			+"\nLast name: "+ dataset.getString("lName")+"\nFirst name: "+dataset.getString("fName")+
			"\nemail: "+dataset.getString("email")+"\nTel number: "+dataset.getString("telNum")+"\nAddress: "+dataset.getString("address"));
			}																			
		}
	}
	/**
	 * the function searches for a project based on the project Id or name of the project 
	 * @param statement
	 * @param s
	 * @param choice
	 * @throws SQLException
	 */
	private static void projectSearch(Statement statement, Scanner s, String choice) throws SQLException {
		ResultSet dataset;
		if (choice.equals("num"))			
		{
			System.out.println("Enter the project's number: ");
			String projectNum = s.next();
			
			dataset = statement.executeQuery("Select * from projects WHERE projectID = "+projectNum);
			
			while (dataset.next())			
				{
					printProjectDetails(dataset);
				}					
		}
		else  if(choice.equals("name"))
		{
			System.out.println("Enter the project's name in the format (buildingtype_lastname): ");
			String projectName = s.next();
			String getProjectData = "Select * from projects WHERE projects.projectName = "+"'"+projectName+"'";
			dataset = statement.executeQuery(getProjectData);			
			while (dataset.next())			
				{
				printProjectDetails(dataset);
				}
		}			
		else if (choice.equals("l"))
		{			
			dataset = statement.executeQuery("Select * from projects");
			while (dataset.next())			
			{
				printProjectDetails(dataset);
			}								
		}
	}
/**
 * the function opens connection to the DB
 * @return
 * @throws SQLException
 */
	private static Connection connectDB() throws SQLException {
		Connection connection;
		String userUrl = "jdbc:mysql://localhost:3306/PoisePMS?allowPublicKeyRetrieval=true&useSSL = false";
		String userName = "newuser";
		String userPass = "fish";
		connection = DriverManager.getConnection (userUrl, userName, userPass);
		return connection;
	}
	
/**
 * the function generates an invoice string 
 * @param dataset
 * @return
 * @throws SQLException
 */
	
	private static String invoiceString(ResultSet dataset) throws SQLException {
		return "\nProject ID: "+dataset.getInt("projectID") + "\nClient ID: "+dataset.getString("clientID")+"\nERF number: "
		+dataset.getString("ErfNum")+"\nTotal amount paid: "+dataset.getDouble("totSum")+"\nAmount paid so far: "
		+dataset.getDouble("paidSum")+"\nDue date: "+dataset.getString("dueDate")+"\nLast Name: "
		+ dataset.getString("lName")+"\nFirst Name: "+dataset.getString("fName")+"\nTel number: "+dataset.getString("telNum")+"\nemail: "
		+dataset.getString("email")+"\nAddress: "+dataset.getString("address")+"\nOutstanding bill: "
		+Math.round(dataset.getFloat("balance")*100/100.0);
	}
	
/**
 * the function edits details of a projects
 * @param statement
 * @param s
 * @throws SQLException
 */
	
	private static void editProjectDetails(Statement statement, Scanner s) throws SQLException {
		System.out.println("Enter the project's number: ");
		int newEntry = s.nextInt();
		String textBlock ="""
				'p'- update paid amount
				'd'- update deadline
				'e' - update engineer's ID
				'a' - update architector's ID
				'm' - update managers"s ID """;
		
		System.out.println(textBlock+"\n");
		String option = s.next();
		editingDetails(statement, s, newEntry, option);
	}
	
	private static void editingDetails(Statement statement, Scanner s, int newEntry, String option) throws SQLException {
		if (option.equals("e"))
		{
			System.out.println("Enter new engineer's ID: ");
			String newEnID = s.next();
			String engUpdate = "UPDATE projects SET strEngID = "+"'"+newEnID+"'" + " "+"WHERE projectID = "+ newEntry;
			statement.executeUpdate(engUpdate);			
		}
		else if(option.equals("a"))
		{
			System.out.println("Enter new architector's ID: ");
			String newArchID = s.next();
			String archUpdate = "UPDATE projects SET archID = "+"'"+newArchID+"'" + " "+"WHERE projectID = "+ newEntry;
			statement.executeUpdate(archUpdate);			
		}
		else if(option.equals("m"))
		{
			System.out.println("Enter new manager's ID: ");
			String newMgID = s.next();
			String managerUpdate = "UPDATE projects SET projMgID = "+"'"+newMgID+"'" + " "+"WHERE projectID = "+ newEntry;
			statement.executeUpdate(managerUpdate);			
		}
		else if (option.equals("p"))
		{
			System.out.println("Enter new amount paid: ");
			String newPayment = s.next();
			String paymentUpdate = "UPDATE projects SET paidSum = paidSum+ "+newPayment+ " "+"WHERE projectID = "+ newEntry;
			statement.executeUpdate(paymentUpdate);			
		}
		else if(option.equals("d"))
		{
			System.out.println("Enter new deadline (yyyy-mm-dd): ");
			String newDueDate = s.next();
			String dateUpdate = "UPDATE projects SET dueDate = "+"'"+newDueDate+"'" + " "+"WHERE projectID = "+ newEntry;
			statement.executeUpdate(dateUpdate);			
		}
}
	
/**
 * the function adds new project to the DB PoisePMS
 * @param statement
 * @param s
 * @throws SQLException
 */
	
	private static void addProject(Statement statement, Scanner s) throws SQLException {
		System.out.println("Enter  project number: ");
		String projectID = s.next();
		
		System.out.println("Enter  project name with an underscore instead of space bar (ex: castle_potter): ");
		String projectName = s.next();
		
		System.out.println("Enter client's ID: ");
		String clientID = s.next().toUpperCase();
		
		System.out.println("Enter engineer's ID: ");
		String strEngID = s.next().toUpperCase();
		
		System.out.println("Enter PM ID: ");
		String projMgID = s.next().toUpperCase();
		
		System.out.println("Enter architect's ID: ");
		String archID= s.next().toUpperCase();
																	
		System.out.println("Enter ERF number: ");
		String erfNum = s.next().toUpperCase();
				
		System.out.println("Enter total amount: ");
		Double totSum = s.nextDouble();
			
		System.out.println("Enter paid amount: ");
		Double paidSum = s.nextDouble();
								
		System.out.println("Enter balance:  ");
		String balance = s.next();
		
		System.out.println("Enter buidling type: ");
		String buildType= s.next().toLowerCase();
		
		System.out.println("Enter deadline:  ");
		String dueDate = s.next();
	
		String addProject = "INSERT INTO projects VALUES("+projectID+", "+ "'"+projectName+"'"+", " +"'"+clientID+"'"+", "+"'"+strEngID+"'"+", "+"'"+
		projMgID+"'"+", "+"'"+archID+"'"+", "+"'"+erfNum+"'"+", "+totSum+", "+paidSum+", "+balance+","+"'"+buildType+"'"+", "+"'"+dueDate+"'"+")";				
			
		statement.executeUpdate(addProject);				
	}
	
/**
 * the function edits details of a person searched based on last name
 * @param statement
 * @param s
 * @throws SQLException
 */
	
	private static void editPersonDetails(Statement statement, Scanner s) throws SQLException {
		System.out.println("Enter a person's last name: ");
		String lNameSearch = s.next();
		System.out.println("Enter the role of a person (client, pm (project manager), engineer, architect): ");
		String roleSearch = s.next();
		System.out.println("To change telephone number, enter 't', to change email addresss, enter 'e', to change address, enter 'a'");
		String option = s.next();
		
		if (option.equals("t"))
		{
			System.out.println("Enter new tel number: ");
			String newTel = s.next();
			String telUpdate = "UPDATE persons SET telNum = "+ newTel+" "+"WHERE lName = "+"'"+lNameSearch+"'" + " AND role = " +"'"+roleSearch+"'";
			statement.executeUpdate(telUpdate);			
		}
		else if (option.equals("e"))
		{
			System.out.println("Enter new email: ");
			String newEmail = s.next();
			String emailUpdate = "UPDATE persons SET email = "+ "'"+newEmail+"'"+" "+"WHERE lName = "+"'"+lNameSearch+"'" + " AND role = "+"'"+roleSearch+"'";
			statement.executeUpdate(emailUpdate);		
		}
		else if (option.equals("a"))
		{
			System.out.println("Enter new address using underscore instead of space bar: ");
			String newAddress = s.next();
			String addressUpdate = "UPDATE persons SET address = "+ "'"+newAddress+"'"+" "+"WHERE lName = "+"'"+lNameSearch+"'"+" AND role = " + "'"+ roleSearch+"'";
			statement.executeUpdate(addressUpdate);			
		}
	}
	
/**
 * the function prints out the list of delayed or incomplete projects
 * @param dataset
 * @throws SQLException
 */
	
	private static void printDelayIncompleteProject(ResultSet dataset) throws SQLException {
		System.out.println("\nProject ID: "+dataset.getInt("projectID") + "\nClient's ID: " 
				+dataset.getString("clientID")+"\nEngineer's ID: "+ dataset.getString("strEngID")+"\nManager's ID:"
				+dataset.getString("projMgID")+"\nArchitector's ID :"+dataset.getString("archID")+"\nERF number: "
				+dataset.getString("ErfNum")+"\nTotal paid: "+dataset.getDouble("totSum")+"\nPaid: "
				+dataset.getDouble("paidSum")+"\nDue date: "+dataset.getString("dueDate"));
	}
	
/**
 * the function prints out a project details after a search in case(0)
 * @param dataset
 * @throws SQLException
 */
	
	private static void printProjectDetails(ResultSet dataset) throws SQLException {
		System.out.println("\nProject ID: "+dataset.getInt("projectID") + "\nClient's ID: " 
				+dataset.getString("clientID")+"\nEngineer's ID: "+ dataset.getString("strEngID")+"\nManager's ID:"
				+dataset.getString("projMgID")+"\nArchitector's ID :"+dataset.getString("archID")+"\nERF number: "
				+dataset.getString("ErfNum")+"\nTotal paid: "+dataset.getDouble("totSum")+"\nPaid: "
				+dataset.getDouble("paidSum")+"\nDue date: "
				+dataset.getString("dueDate")+"\nBuilding type: "+dataset.getString("buildType"));
	}			
}
/*end of the program*/

	

	

		
        
    
