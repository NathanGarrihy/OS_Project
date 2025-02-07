import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Server {

	public static void main(String[] args) {
		
		ServerSocket listener;
		int clientid=0;
		try 
		{
			 listener = new ServerSocket(10000,10);
			 
			 while(true)
			 {
				System.out.println("Main thread listening for incoming new connections");
				Socket newconnection = listener.accept();
				
				System.out.println("New connection received and spanning a thread");
				Connecthandler t = new Connecthandler(newconnection, clientid);
				clientid++;
				t.start();
			 }
			
		} 
		
		catch (IOException e) 
		{
			System.out.println("Socket not opened");
			e.printStackTrace();
		}
	}

}


class Connecthandler extends Thread
{

	Socket individualconnection;
	private int socketid;
	ObjectOutputStream out;
	ObjectInputStream in;
	
	private	String loginOrReg;
	private String userType;
	private	String clubEmail;
	
	private	String name;
	private String password;
	private	double fundsAvailable;
	private String agentEmail;
	
	private	boolean loggedIn;
	private boolean regComplete;
	private	int choice;
	private	String tempName;
	private	String tempClubID;
	private	String tempAgentID;
	
	private int tempAge;
	private int tempStatus;
	private int tempPosition;
	private int tempValue;
	
	ArrayList<String> arrClubPasswords = new ArrayList<String>();
	ArrayList<String> arrAgentUsernames = new ArrayList<String>();
	ArrayList<String> arrAgentPasswords = new ArrayList<String>();
	ArrayList<String> arrClubUsernames = new ArrayList<String>();
	
	ArrayList<String> clubIDs = new ArrayList<String>();
	ArrayList<Player> players = new ArrayList<Player>();
	ArrayList<Club> clubs = new ArrayList<Club>();
	ArrayList<Agent> agents = new ArrayList<Agent>();
	
	public Connecthandler(Socket s, int i)
	{
		individualconnection = s;
		socketid = i;
	}
	
	void sendMessage(String msg)
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
	void sendMessage(boolean msg)
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
	void sendMessage(int msg)
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
	
	public void run()
	{
		
		try 
		{
		
			out = new ObjectOutputStream(individualconnection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(individualconnection.getInputStream());
			System.out.println("Connection"+ socketid+" from IP address "+individualconnection.getInetAddress());
				
			//Scanner console = new Scanner(System.in);		
			Scanner agentS = new Scanner(new File("agentLogin.txt"));
			Scanner clubS = new Scanner(new File("clubLogin.txt"));
			Scanner playerS = new Scanner(new File("players.txt"));
			Scanner clubIdS = new Scanner(new File("clubId.txt"));
			
			//	Populate arrays with txt files
			while(agentS.hasNext()) {
				arrAgentUsernames.add(agentS.next());
				arrAgentPasswords.add(agentS.next());
			}
			
			while(clubS.hasNext()) {
				arrClubUsernames.add(clubS.next());
				clubIDs.add(clubS.next());
			}
			
			while(playerS.hasNext()) {
				players.add(new Player(playerS.next(), playerS.nextInt(), playerS.next(), playerS.next(), playerS.nextInt(),
						playerS.nextInt(), playerS.nextInt(), playerS.nextDouble()));
			}
			
			while (clubIdS.hasNext()) {
				clubIDs.add(clubIdS.next());
			}
			
			
			loginOrReg = decideLogin();
			//	Login 
			if(loginOrReg.equals("1")) 
			{	
				userType = clubOrAgent();
				
				login(userType);
			}	
			//	Register
			else if(loginOrReg.equals("2")) 
			{			
				userType = clubOrAgent();
				
				register(userType);
			}
			
			if (loggedIn)
			{
				if (userType.equals("1"))
				{
					sendMessage("Club menu\n");
					choice = clubOptions();
					sendMessage(choice);
					
					if(choice == 1)
					{
						//	Search for all players in a given position
						searchByPosition();
					}
					else if (choice == 2) {
						searchForSale();
					}
					else if (choice == 3) {
						//	Suspend / resume the sale of a player in their clubs
					}
					else if (choice == 4) {
						//	Purchase a player
						//	if player is valid
						//	the club has the funds required
						//	change players status to sold
						//	player gets new club id
						//	the purchasing clubs balance should reduce by the valuation
						//	The selling club's balance should increase by the valuation
					}
				}
				else if (userType.equals("2"))
				{
					sendMessage("Agent menu\n");
					choice = agentOptions();
					sendMessage(choice);
					
					if(choice == 1)
					{
						addPlayer();
					}
					else if (choice == 2) {
						updateValuation();
					}
					else if (choice == 3) {
						updateStatus();
					}
				}
			}	
			
			agentS.close();
			clubS.close();
			playerS.close();
			clubIdS.close();
		}
		
		catch (IOException e) 
		{	
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		finally
		{
			try 
			{
				out.close();
				in.close();
				individualconnection.close();
			}
			
	
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		
	}	//	end of run()
	
	//	decide if user wants to login or register
	public String decideLogin() throws ClassNotFoundException, IOException {
		String choice;
		do
		{
			sendMessage("Enter 1 login Or 2 to register");
			choice = (String)in.readObject();
		}while(!choice.equals("1")&&!choice.equals("2"));
		
		return choice;
	}
	
	//	checks if user is club or agent
	public String clubOrAgent() throws ClassNotFoundException, IOException {
		String choice;
		
		do
		{
			sendMessage("Enter 1 for club Or 2 for agent.");
			choice = (String)in.readObject();
		} while (!choice.equals("1") && !choice.equals("2"));
		
		return choice;
	}	//	end of clubOrAgent()
	
	//	login function
	public void login(String choice) throws ClassNotFoundException, IOException {
		loggedIn = false;
		
		do
		{			 
			sendMessage("Enter your name:");
			name = (String)in.readObject();
			sendMessage("Enter your Password:");
			password = (String)in.readObject();
			for (int i = 0; i < arrClubUsernames.size(); i++) {
				sendMessage(arrClubUsernames.get(i));
				sendMessage(arrClubPasswords.get(i));
				
			}
			if (choice.equals("1")) {
				for (int i = 0; i < arrClubUsernames.size(); i++) {
					if (name.equalsIgnoreCase(arrClubUsernames.get(i))
							&& password.equalsIgnoreCase(arrClubPasswords.get(i))) {
						loggedIn = true;
					}
				}
			}
			else if (choice.equals("2")) {
				for (int j = 0; j < arrAgentUsernames.size(); j++) {
					if (name.equalsIgnoreCase(arrAgentUsernames.get(j))
							&& password.equalsIgnoreCase(arrAgentPasswords.get(j))) {
						loggedIn = true;
					}
				}
			}
					
			sendMessage(loggedIn);
		} while (loggedIn == false);
	}	//	end of login()
	
	//	Register function
	public void register(String option) throws ClassNotFoundException, IOException {
				
		regComplete = false;
		
		do
		{	
			//	Club 
			if (option.equals("1")) { 
				registerClub();
			}
			//	Agent 
			else if (option.equals("2")) { // agent  
				registerAgent();
			}
			
			regComplete = checkReg(name, password, option);
			
			sendMessage(regComplete);
			
			if (regComplete == false) {
				sendMessage("Name or id already in use.");
			}
		} while(regComplete == false);
		
		addToFile(option);
		
		sendMessage("Registration Successful");
				
		if (option.equals("1")) {
			sendMessage("\nName: " + name + "\nID: " + password +"\nEmail: " + 
					clubEmail + "\nFunds Available for transfer: " + fundsAvailable);
			
			addClub(password, name, clubEmail, fundsAvailable);
		}
		else if (option.equals("2")) {
			sendMessage("\nName: " + name + "\nID: " + password + "\nEmail: " + agentEmail);
			
			addAgent(name, password, agentEmail);
		}
				
		login(option);
	}	//	end of register()
	
	//	Add a new club
	public void registerClub() throws ClassNotFoundException, IOException
	{
		sendMessage("Enter club name:"); 
		name = (String)in.readObject();
		sendMessage("Enter club ID:"); 
		password = (String)in.readObject();
		sendMessage("Enter club email:"); 
		clubEmail = (String)in.readObject();
		sendMessage("Enter available funds:"); 
		fundsAvailable = (double) in.readObject();
	}
	
	//	Add a new agent
	public void registerAgent() throws ClassNotFoundException, IOException
	{
		sendMessage("Enter agent name:");
		name = (String)in.readObject();
		sendMessage("Enter agent ID:");
		password = (String)in.readObject();
		sendMessage("Enter agent email:");
		agentEmail = (String)in.readObject();
	}
	
	//	Adds club to list
	private void addClub(String id, String name, String email, double funds) {
		
		clubs.add(new Club(id, name, email, funds));

	}	
	
	//	Add agent to list
	private void addAgent(String name, String id, String email) {
		agents.add(new Agent(name, id, email));
	}	
	
	//	Check if  club / agent already exists / can be registered
	public boolean checkReg(String name, String id, String u) {
		boolean isUnique = true;
		
		if (u.equals("1")) {
			for (int i = 0; i < arrClubUsernames.size(); i++) {
				if (name.equals(arrClubUsernames.get(i))) {
					isUnique = false;
				}
			}
			
			for (int i = 0; i < clubIDs.size(); i++) {
				if (id.equals(clubIDs.get(i))) {
					isUnique = false;
				}
			}
		}
		else if (u.equals("2")) {
			for (int i = 0; i < arrAgentUsernames.size(); i++) {
				if (name.equals(arrAgentUsernames.get(i))) {
					isUnique = false;
				}
			}
			
			for (int i = 0; i < arrAgentPasswords.size(); i++) {
				if (id.equals(arrAgentPasswords.get(i))) {
					isUnique = false;
				}
			}
		}
		
		return isUnique;
		
	}	//	end of checkReg()
	
	//	Options for club
	public int clubOptions() throws ClassNotFoundException, IOException {
		int choice = 0;
		
		sendMessage("Please choose from the following: \n1. to Search for players using a given position\n"
				+ "2. to Search for all players for sale in their club\n"
				+ " 3. Suspend/Resume the sale of a player in their club NOT IMPLEMENTED\n" + 
				   "4. Purchase a player NOT IMPLEMENTED\n");
		choice = (int) in.readObject();
		
		return choice;	
	}	//	end of clubOptions()
	
	//	Options for agent
	public int agentOptions() throws ClassNotFoundException, IOException
	{
		int choice;
		sendMessage("Please choose from the following: \n1. to Add a new player \n2. to Update a players "
				+ "valuation \n3. to Update a players status");
		choice = (int) in.readObject();
		
		return choice;
	}	//	end of agentOptions()

	//	Add  new player to list
	public void addPlayer() throws IOException, ClassNotFoundException
	{
		FileWriter fw = new FileWriter("players.txt", true);
		BufferedWriter bw = new BufferedWriter(fw);
		
		int cId = players.size() + 1;
				
		playerDetails();
		
		Player player = new Player(tempName, cId, tempClubID, tempAgentID, tempAge, tempStatus, tempPosition, tempValue);
		players.add(player);
		
		bw.write("\n" + player.name + "\n" + player.playerID + "\n" + player.clubID + "\n" + player.agentID
				 + "\n" + player.age + "\n" + player.status + "\n" + player.position + "\n" + player.valuation);
		
		sendMessage(player.name + " " + player.playerID + " " + player.valuation);
		
		bw.close();
		fw.close();
		
	}	//	end of addPlayer()
	
	//	Takes in player details
	public void playerDetails() throws ClassNotFoundException, IOException {
		sendMessage("Enter the players full name e.g. CristianoRonaldo: ");
		tempName = (String) in.readObject();
		
		sendMessage("Enter the Club ID: ");
		tempClubID = (String) in.readObject();
		
		sendMessage("Enter the Agent ID: ");
		tempAgentID = (String) in.readObject();
		
		sendMessage("Enter the player's age: ");
		tempAge = (int) in.readObject();
		
		sendMessage("Enter the player's status: \n 1.) For Sale \n 2.) Sold \n 3.) Sale Suspended ");
		tempStatus = (int) in.readObject();
		
		sendMessage("Enter the player's position: \n 1.) Goalkeeper \n 2.) Defender \n 3.) Midfielder \n 4.) Attacker ");
		tempPosition = (int) in.readObject();
		
		sendMessage("Enter the player's valuation: ");
		tempValue = (int) in.readObject();
	}	//	end of playerDetails()
	
	//	Adds new user login details to files
	public void addToFile(String option) throws IOException {
		
		FileWriter aFw = new FileWriter("agentLogin.txt", true);
		BufferedWriter aBw = new BufferedWriter(aFw);
		FileWriter cFw = new FileWriter("clubLogin.txt", true);
		BufferedWriter cBw = new BufferedWriter(cFw);
		
		if (option.equals("1")) {
			arrClubUsernames.add(name);
			clubIDs.add(password);	
			cBw.write(name + "\n" + password);
		}
		
		else if (option.equals("2")) {
			arrAgentUsernames.add(name);
			arrAgentPasswords.add(password);	
			aBw.write(name + "\n" + password);
		}
		
		aBw.close();
		cBw.close();
		aFw.close();
		cFw.close();
	}	//	end of addToSystem
	
	//	Updates player valuation
	public void updateValuation() throws ClassNotFoundException, IOException {
		int pId;
		double val;
		boolean isPlayer = false;
		int num = 0;
		
		FileWriter fr = new FileWriter("players.txt"+ "", false);
		BufferedWriter br = new BufferedWriter(fr);
		
		sendMessage("Enter the id of the player: ");
		pId = (int) in.readObject();
		sendMessage("Enter the new valuation for the player: ");
		val = (double) in.readObject();
		
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).playerID == pId) {
				players.get(i).valuation = val;
				isPlayer = true;
				num = i;
			}
		}
		
		sendMessage(isPlayer);
		
		if (isPlayer) {
			sendMessage("\nYou chose " + players.get(num).name + "\nHis new valuation is " + players.get(num).valuation
					 + "\n");
		}
		
		//	Writes updated information to the file
		for (int i = 0; i < players.size(); i++) {
			br.write("\n" + players.get(i).name + "\n" + players.get(i).playerID + "\n" + players.get(i).clubID 
					+ "\n" + players.get(i).agentID + "\n" + players.get(i).age + "\n" + players.get(i).status + "\n" 
					+ players.get(i).position + "\n" + players.get(i).valuation);
		}
		
		br.close();
		fr.close();
	}	//	end of updateValuation()
	
	//	updates player status
	public void updateStatus() throws IOException, ClassNotFoundException {
		int pId;
		int status;
		boolean isPlayer = false;
		int num = 0;
		
		FileWriter fr = new FileWriter("players.txt", false);
		BufferedWriter br = new BufferedWriter(fr);
		
		sendMessage("Enter the id of the player: ");
		pId = (int) in.readObject();
		sendMessage("Enter the new status for the player: \n 1.) For Sale \n 2.) Sold \n 3.) Sale Suspended ");
		status = (int) in.readObject();
		
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).playerID == pId) {
				players.get(i).status = status;
				isPlayer = true;
				num = i;
			}
		}
		
		sendMessage(isPlayer);
		
		if (isPlayer) {
			sendMessage("\nYou chose " + players.get(num).name + "\nHis new status is " + players.get(num).status + "\n");
		}
		
		// overwrite file with updated info
		for (int i = 0; i < players.size(); i++) {
			br.write("\n" + players.get(i).name + "\n" + players.get(i).playerID + "\n" + players.get(i).clubID 
					+ "\n" + players.get(i).agentID + "\n" + players.get(i).age + "\n" + players.get(i).status + "\n" 
					+ players.get(i).position + "\n" + players.get(i).valuation);
		}
		
		br.close();
		fr.close();
	} //	end of updateStatus()
	
	//	Search for player by position
	public void searchByPosition() throws ClassNotFoundException, IOException {
		int pos;
		boolean isPlayer = false;
		
		sendMessage("Please enter a number from the following to choose the players position:"
				+ " 1) Goalkeeper, 2) Defender, 3) Midfielder, 4) Forward");

		pos = (int) in.readObject();
		
		for (int i=0; i < players.size(); i++)
		{
			if (players.get(i).position == pos)
			{
				isPlayer = true;
				sendMessage("\n" + players.get(i).name + "\n" + players.get(i).playerID + "\n" + players.get(i).clubID 
						+ "\n" + players.get(i).agentID + "\n" + players.get(i).age + "\n" + players.get(i).status + "\n" 
						+ players.get(i).position + "\n" + players.get(i).valuation);
			}
		}
		sendMessage(isPlayer);
		
	}
	
	//	Search for players that are for sale
	public void searchForSale() throws ClassNotFoundException, IOException {
		boolean isPlayer = false;
		
		for (int i=0; i < players.size(); i++)
		{
			if (players.get(i).status == 1)
			{
				isPlayer = true;
				sendMessage("\n" + players.get(i).name + "\n" + players.get(i).playerID + "\n" + players.get(i).clubID 
						+ "\n" + players.get(i).agentID + "\n" + players.get(i).age + "\n" + players.get(i).status + "\n" 
						+ players.get(i).position + "\n" + players.get(i).valuation);
			}
		}
		sendMessage(isPlayer);
	}	//	end of searchForSale()
	
}	//	end of Server