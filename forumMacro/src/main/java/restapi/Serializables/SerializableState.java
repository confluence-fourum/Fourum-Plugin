package restapi.Serializables;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import forummacro.forum.ao.StateAO;
/**
 * 
 * @author Nico
 *
 */
@XmlRootElement
public class SerializableState {

	private boolean state;
	private String userKey;
	private Date date;
	private int ID;
	
	public SerializableState() {
	}
	
	public SerializableState(boolean state, String userKey, Date date, int ID) {
		this.state = state;
		this.userKey = userKey;
		this.date = date;
		this.ID = ID;
	}
	
	public static SerializableState build(StateAO stateAO) {
		return new SerializableState(stateAO.getClosed(), stateAO.getUserKey(), stateAO.getDate(), stateAO.getID());
	}
	
	@XmlElement
	public boolean getState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}
	
	@XmlElement
	public String getUserKey() {
		return userKey;
	}
	public void setUserKey(String user) {
		this.userKey = user;
	}

	@XmlElement
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	@XmlElement
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}

}
