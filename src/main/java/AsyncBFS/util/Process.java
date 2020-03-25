package AsyncBFS.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import AsyncBFS.impl.AsynchBFSImpl.Inte;

/*
 *  
 *  Representation of Process Entity
 *  
 *  @author varadhan
 *  
 */
public class Process {
  
  int processId;
  int distanceFromRoot;
  Process parent;
  
  boolean isRoot = false;
  
  List<Process> neighbours;
  
  List<Process> childProcess;
  
  BlockingQueue<Message> inMessages; //FIFO queue containing all incoming messages
  
  public Process(int id,boolean isRoot) {
	distanceFromRoot = Integer.MAX_VALUE;
    this.processId = id;
    this.isRoot = isRoot;
    this.parent = null;
    inMessages = new ArrayBlockingQueue<Message>(1000);
  }
  
  public int getProcessId() {
    return processId;
  }

  public boolean isRoot() {
    return isRoot;
  }
  
  public int getDistanceFromRoot() {
	  return distanceFromRoot;
  }
  
  public void setDistanceFromRoot(int distance) {
	  this.distanceFromRoot = distance;
  }
  
  public Process getParent() {
	  return parent;
  }
  
  public void setParent(Process parent) {
	  this.parent = parent;
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
  
  public void addMessage(Message message) {
	  try {
		  this.inMessages.put(message);
	  }
	  catch(InterruptedException e) {
		  System.out.println(e.toString());
	  }
	  
  }
  
  
  //message handling
  
  /**Sends a message to all neighbors, adding random delays in the time
   * range of 1 -> 15 milliseconds
   * 
   * @param message the message to send to all neighbors
 * @throws InterruptedException since the Thread.sleep() function is being called
   */
  public void sendMessageToNeighbours(Message message, Inte inte) throws InterruptedException {
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
			if (delays[j] == i && neighbours.get(j) != this.getParent()) {
				inte.noOfMessages++;
				sendMessage(message, neighbours.get(j));
			}
		}
		Thread.sleep(10);
	  }
  }
  
  /**Private helper method - sends a message to a single process
   * @param message the message to send
   * @param process the process to receive the message
   */
  public void sendMessageToSender(Message message, Process process) throws InterruptedException {
	  int delay = (int) ((Math.random() * 15) + 1);
	  Thread.sleep(delay*10);
	  try {
		  process.inMessages.put(message);
	  }
	  catch(InterruptedException e) {
		  System.out.println(e.toString());
	  }
  }
  
  /**Private helper method - sends a message to a single process
   * @param message the message to send
   * @param process the process to receive the message
   */
  public void sendMessage(Message message, Process process) {
	  try {
		  process.inMessages.put(message);
	  }
	  catch(InterruptedException e) {
		  System.out.println(e.toString());
	  }
  }

  /** Returns the message at the head of the FIFO queue */
  public Message getFirstMessage(){
	  if (!inMessages.isEmpty()) {
		  try {
			  return inMessages.take();
		  }
		  catch(InterruptedException e) {
			  System.out.println(e.toString());
		  }
	  }
	  return null;
  }
}
