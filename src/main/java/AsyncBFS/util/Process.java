package AsyncBFS.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/*
 *  
 *  Representation of Process Entity
 *  
 *  @author varadhan
 *  
 */
public class Process {
  
  int processId;
  
  boolean isRoot = false;
  
  List<Process> neighbours;
  
  List<Process> childProcess;
  
  Deque<Message> inMessages; //FIFO queue containing all incoming messages
  
  public Process(int id,boolean isRoot) {
    this.processId = id;
    this.isRoot = isRoot;
    inMessages = new ArrayDeque<Message>();
  }
  
  public int getProcessId() {
    return processId;
  }

  public boolean isRoot() {
    return isRoot;
  }

  
  public void addNeighbours(Process newNeighbor) {
    if(neighbours == null) {
      neighbours = new ArrayList<Process>();
    }
    neighbours.add(newNeighbor);
  }

  public List<Process> getNeighbours(){
    return neighbours;
  }
  
  public void addChildProcess(Process child) {
    if(this.childProcess == null) {
      this.childProcess = new ArrayList<Process>();
    }
    this.childProcess.add(child);
  }
  
  public List<Process> getChildProcesses() {
    return this.childProcess;
  }
  
  
  //message handling
  
  /**Sends a message to all neighbors, adding random delays in the time
   * range of 1 -> 15 milliseconds
   * 
   * @param message the message to send to all neighbors
 * @throws InterruptedException since the Thread.sleep() function is being called
   */
  public void sendMessageToNeighbours(Message message) throws InterruptedException {
	  int numNeighbors = neighbours.size();
	  
	  //calculate a delay for each neighbor
	  int[] delays = new int[numNeighbors];
	  for (int i = 0; i < delays.length; i++){
		  int thisDelay = (int)((Math.random() * 15) + 1);
		  delays[i] = thisDelay;
	  }
	  
	  //send messages based on delays
	  for (int i = 1; i < 16; i++){
		for (int j = 0; j < delays.length; j++){
			if (delays[j] == i)
				sendMessage(message, neighbours.get(j));
		}
		Thread.sleep(1);
	  }
  }
  
  /**Private helper method - sends a message to a single process
   * @param message the message to send
   * @param process the process to receive the message
   */
  private void sendMessage(Message message, Process process) {
	  process.inMessages.add(message);
  }

  /** Returns the message at the head of the FIFO queue */
  public Message getFirstMessage(){
	  return inMessages.poll();
  }
}
