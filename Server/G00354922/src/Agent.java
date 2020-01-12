public class Agent {
	String aName, aId, aEmail;

	public Agent(String name, String agentID, String email) {
		super();
		this.aName = name;
		this.aId = agentID;
		this.aEmail = email;
	}

	public String getName() {
		return aName;
	}

	public void setName(String name) {
		this.aName = name;
	}

	public String getAgentID() {
		return aId;
	}

	public void setAgentID(String agentID) {
		this.aId = agentID;
	}

	public String getEmail() {
		return aEmail;
	}

	public void setEmail(String email) {
		this.aEmail = email;
	}
	
	
}