import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Scanner;
//test
public class Client 
{
	
	private Socket connection;
	private String message;
	private Scanner console;
	private String ipaddress;
	private int portaddress;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	private String loginOrRegister;
	private String clubName;
	private String clubPass;
	private String clubEmail;
	private String clubId;
	private double fundsAvailable;
	
	private String agentName;
	private String agentPass;
	private String agentEmail;
	
	private String userCategory;
	private boolean isLoggedIn;
	private int choice;
	
	private String tempName; 
	private String tempClubId; 
	private String tempAgentId;
	private int tempAge;
	private int tempStatus;
	private int tempPosition; 
	private int tempValue;

	public Client()
	{
		console = new Scanner(System.in);
		
		System.out.println("Enter the IP Address of the server");
		ipaddress = console.nextLine();
		
		System.out.println("Enter the TCP Port");
		portaddress  = console.nextInt();
		
	}
	
	private void sendMessage(String msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
			System.out.println("client>" + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	private void sendMessage(double msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
			System.out.println("client>" + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	private void sendMessage(int msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
			System.out.println("client>" + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) 
	{
			Client temp = new Client();
			temp.clientapp();
	}

	public void clientapp()
	{
		
		try 
		{
			connection = new Socket(ipaddress,portaddress);
		
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
			System.out.println("Client Side ready to communicate");
		
		
		    /// Client App.
			
			message = (String)in.readObject();
			System.out.println(message);
			loginOrRegister = chooseLogin();
			sendMessage(loginOrRegister);
			
			//	Login
			if (loginOrRegister.equals("1")) // login
			{				
				message = (String)in.readObject();
				System.out.println(message);
				userCategory = clubOrAgent();	
				sendMessage(userCategory);
				
				login(userCategory);
			}
			//	Register
			else if (loginOrRegister.equals("2")) 
			{
				message = (String)in.readObject();
				System.out.println(message);
				userCategory = clubOrAgent();	
				sendMessage(userCategory);
				
				register(userCategory);
			}
							
			message = (String) in.readObject();
			System.out.println(message);		
			
			if (userCategory.equals("1")) // club
			{
				// club
				clubOptions();
				
				choice = (int) in.readObject();
				System.out.println("choice= " + choice);
				
				if(choice == 1) {
					
				}
				else if(choice == 2) {
					
				}
				else if(choice == 3)
				{
					
				}
			} 
			else if (userCategory.equals("2")) // agent 
			{
				agentOptions(); 
				
				choice = (int) in.readObject();
				System.out.println("choice= " + choice);
				
				if (choice == 1) {
					addPlayer();
					message = (String) in.readObject();
					System.out.println(message);
				}
				else if (choice == 2 ) {
					updateValuation();
				}
				else if (choice == 3) {
					updateStatus();
				}
				else {
					System.out.println("Error, invalid option");
				}	
			}
								
			out.close();
			in.close();
			connection.close();
		} 
		
		catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	//	Decides if user is a club or agent
	private String clubOrAgent() throws ClassNotFoundException, IOException {
		String choice;
		int num=1;
		
		do
		{
			choice = console.next();
			if(num >= 1) {
				System.out.println("try again");
			}
			num+=1;
			
		}while(!choice.equals("1")&&!choice.equals("2"));
		
		return choice;
	}
	
	//	Decides whether user is going to login or register
	private String chooseLogin() throws ClassNotFoundException, IOException {
		String choice;
		int num = 1;
		
		do
		{
			choice = console.next();
			if(num >= 1) {
				System.out.println("try again");
			}
			num+=1;
		}while(!choice.equals("1")&&!choice.equals("2"));
		
		return choice;		
	}
	
	//	Handles login for users
	private void login(String choice) throws ClassNotFoundException, IOException {
		isLoggedIn = false;
		
		do
		{
			if (choice.equals("1")) {
				// Club name
				message = (String)in.readObject(); 
				System.out.println(message);
				clubName = console.next();
				sendMessage(clubName);
				
				// Club password
				message = (String)in.readObject(); 
				System.out.println(message);
				clubPass = console.next();
				sendMessage(clubPass);
			}
			// agent name
			else if (choice.equals("2")) {
				message = (String)in.readObject(); 
				System.out.println(message);
				agentName = console.next();
				sendMessage(agentName);
				
				// agent id
				message = (String)in.readObject(); 
				System.out.println(message);
				agentPass = console.next();
				sendMessage(agentPass);
				
			}
			isLoggedIn = (boolean) in.readObject();
			System.out.println("Logged In: " + isLoggedIn);
	
		} while (isLoggedIn == false);
		
	}
	
	//	Register a new user
	public void register(String option) throws IOException, ClassNotFoundException {
		
		boolean regSuccessful = true;
			
		do
		{
			if (option.equals("1")) { 
				registerClub();
			}
			else if (option.equals("2")) {
				registerAgent();
			}
				
			regSuccessful = (boolean) in.readObject();
			System.out.println(regSuccessful);
			
			if (regSuccessful == false) {
				message = (String) in.readObject();
				System.out.println(message);
			}
		} while(regSuccessful == false);
			
		message = (String) in.readObject();
		System.out.println(message);
		
		message = (String)in.readObject();
		System.out.println(message);
		
		login(option);
	}
	
	//	Register a new club
	private void registerClub() throws ClassNotFoundException, IOException
	{
		// Club username
		message = (String)in.readObject(); 
		System.out.println(message);					
		clubName = console.next(); 
		sendMessage(clubName);
		
		// Club password
		message = (String)in.readObject(); 
		System.out.println(message);					
		clubPass = console.next(); 
		sendMessage(clubPass);
		
		//	Club id
		message = (String) in.readObject();
		System.out.println(message);
		clubId = console.next();
		sendMessage(clubId);
		
		// Club email
		message = (String)in.readObject(); 
		System.out.println(message);					
		clubEmail = console.next(); 
		sendMessage(clubEmail);
		
		// Funds
		message = (String)in.readObject(); 
		System.out.println(message);					
		fundsAvailable = console.nextDouble(); 
		sendMessage(fundsAvailable);
	}
	
	//	Register a new Agent
	private void registerAgent() throws ClassNotFoundException, IOException
	{
		// Agent name
		message = (String)in.readObject(); 
		System.out.println(message);					
		agentName = console.next(); 
		sendMessage(agentName);
		
		// Agent password
		message = (String)in.readObject(); 
		System.out.println(message);					
		agentPass = console.next(); 
		sendMessage(agentPass);
		
		// Agent email
		message = (String)in.readObject(); 
		System.out.println(message);					
		agentEmail = console.next(); 
		sendMessage(agentEmail);
	}
	
	//	Provides an options menu to agents
	private void agentOptions() throws ClassNotFoundException, IOException
	{
		int choice;
		message = (String) in.readObject();
		System.out.println(message);
		
		choice = console.nextInt();
		sendMessage(choice);
	}
	
	//	Provides an options menu to agents
	private void clubOptions() throws ClassNotFoundException, IOException
	{
		int choice;
		message = (String) in.readObject();
		System.out.println(message);
		
		choice = console.nextInt();
		sendMessage(choice);
	}
	
	//	Add a new player
	private void addPlayer() throws ClassNotFoundException, IOException {
		message = (String) in.readObject();
		System.out.println(message);
		tempName = console.next();
		sendMessage(tempName);
		
		message = (String) in.readObject();
		System.out.println(message);
		tempClubId = console.next();
		sendMessage(tempClubId);
		
		message = (String) in.readObject();
		System.out.println(message);
		tempAgentId = console.next();
		sendMessage(tempAgentId);
		
		message = (String) in.readObject();
		System.out.println(message);
		tempAge = console.nextInt();
		sendMessage(tempAge);
		
		message = (String) in.readObject();
		System.out.println(message);
		tempStatus = console.nextInt();
		sendMessage(tempStatus);
		
		message = (String) in.readObject();
		System.out.println(message);
		tempPosition = console.nextInt();
		sendMessage(tempPosition);
		
		message = (String) in.readObject();
		System.out.println(message);
		tempValue = console.nextInt();
		sendMessage(tempValue);
	}
	
	//	Update player's valuation
	private void updateValuation() throws ClassNotFoundException, IOException {
		int playerID;
		double value;
		boolean isPlayer;
		
		message = (String) in.readObject();
		System.out.println(message);
		playerID = console.nextInt();
		sendMessage(playerID);
		
		message = (String) in.readObject();
		System.out.println(message);
		value = console.nextDouble();
		sendMessage(value);
		
		isPlayer = (boolean) in.readObject();
		
		if (isPlayer) {
			message = (String) in.readObject();
			System.out.println(message);
		}
	}
	
	//	update Player status
	private void updateStatus() throws ClassNotFoundException, IOException {
		int playerID;
		int status;
		boolean isPlayer;		
		
		message = (String) in.readObject();
		System.out.println(message);
		playerID = console.nextInt();
		sendMessage(playerID);
		
		message = (String) in.readObject();
		System.out.println(message);
		status = console.nextInt();
		sendMessage(status);
		
		isPlayer = (boolean) in.readObject();
		
		if (isPlayer) {
			message = (String) in.readObject();
			System.out.println(message);
		}
	}
	
	//	Search for player by position
	public void searchByPosition() throws ClassNotFoundException, IOException {
		int pos;
		@SuppressWarnings("unused")
		boolean isPlayer;
		
		message = (String) in.readObject();
		System.out.println(message);
		pos = console.nextInt();
		sendMessage(pos);
		
		isPlayer = (boolean) in.readObject();
		
		if (isPlayer = true) {
		message = (String) in.readObject();
		System.out.println(message);
		}
	}
	
	//	Search for players that are for sale
	public void searchForSale() throws ClassNotFoundException, IOException {
		boolean isPlayer = false;
		
		isPlayer = (boolean) in.readObject();
		
		if(isPlayer == true) {
			message = (String) in.readObject();
			System.out.println(message);
		}
		
	}	//	end of searchForSale()
	
	
}	//	end of Client