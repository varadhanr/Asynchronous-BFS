/**
 * Utility class representing a message from one process to another.
 * @author Madison Pickering
 */
package AsyncBFS.util;


public class Message {

	int messageType; //0 if it is a test message, 1 if it is a response to a test message
	int distanceFromRoot;
	Process initProcess; //the process id corresponding to the initial sender of the message
	
	public Message(int messageType, int distanceFromRoot, Process initProcessId){
		this.messageType = messageType;
		this.distanceFromRoot = distanceFromRoot;
		this.initProcess = initProcessId;
	}
	
	public int getMessageType(){
		return messageType;
	}
	
	public int getDistanceFromRoot(){
		return distanceFromRoot;
	}
	
	public Process getInitProcess(){
		return initProcess;
	}
}
