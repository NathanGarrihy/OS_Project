public class Player {
	
	String name, clubID, agentID;
	int age, status, position, playerID;
	double valuation;
	
	public Player(String name, int playerID, String clubID, String agentID, int age, int status, int position,
			double valuation) {
		super();
		this.name = name;
		this.playerID = playerID;
		this.clubID = clubID;
		this.agentID = agentID;
		this.age = age;
		this.status = status;
		this.position = position;
		this.valuation = valuation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

	public String getClubID() {
		return clubID;
	}

	public void setClubID(String clubID) {
		this.clubID = clubID;
	}

	public String getAgentID() {
		return agentID;
	}

	public void setAgentID(String agentID) {
		this.agentID = agentID;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public double getValuation() {
		return valuation;
	}

	public void setValuation(double valuation) {
		this.valuation = valuation;
	}	
}