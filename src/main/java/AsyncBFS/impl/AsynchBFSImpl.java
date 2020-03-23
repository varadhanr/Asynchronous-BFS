package AsyncBFS.impl;

import java.util.*;

import AsyncBFS.interfaces.AsynchBFS;
import AsyncBFS.util.Message;
import AsyncBFS.util.Process;

public class AsynchBFSImpl implements AsynchBFS {

  int rootId;
  Process[] processes;
  boolean done = false;
  int noOfMessages;
  int noOfLinks;
  HashMap<Integer, ArrayList<Integer>> map = new HashMap<>(); 
//  int[][] childMatrix;
  
  public class Inte{
      public int noOfMessages = 0;
  }

  public AsynchBFSImpl(int rootId,Process[] processess) {
    this.rootId = rootId;
    this.processes = processess;
//    childMatrix = new int[processess.length][processess.length];
  }
  
  Inte inte=new Inte();
  class ProcessThreadObject extends Thread {
    
    Process process;
    
    public ProcessThreadObject(Process process) {
      this.process = process;
    }
    
    @Override
    public void run() {
    //print my id & the id of my neighbors
      for (int i = 0; i < process.getNeighbours().size(); i++) {
    	  System.out.println("my id: " + process.getProcessId() + " neighbor id: " 
    			  + process.getNeighbours().get(i).getProcessId());
      }
      
      //check message passing
      Message myMessage = new Message(0, 0, process); //test message
      try {
    	int noOfAckNack = process.getNeighbours().size()-1;
    	if (process.isRoot()) {
    		System.out.println("Send message once" + process.getProcessId());
    		noOfAckNack = process.getNeighbours().size();
    		process.setDistanceFromRoot(0);
    		process.sendMessageToNeighbours(myMessage, inte);
    	}
		//Thread.sleep(1000); //sleep for a second so that everyone gets messages - this is only here to test and should be deleted
		
		//read all received message in FIFO order
    	
		while (!done) {
			Message thisMessage = process.getFirstMessage();
			if (thisMessage != null) {
				if (thisMessage.getMessageType() == -1 || thisMessage.getMessageType() == 1) {
					if (thisMessage.getDistanceFromRoot() != process.getDistanceFromRoot()) {
						System.out.println("my id: " + process.getProcessId() +" ignores the acnowledgment from " + thisMessage.getInitProcess().getProcessId());
						System.out.println("Process' distance from root is " + process.getDistanceFromRoot() +" acknowledgements'  distance from root is " + thisMessage.getDistanceFromRoot());
					}
					else {
						if (thisMessage.getMessageType() == -1)
							System.out.println("my id: " + process.getProcessId() + " received NACK from: " 
				    			  + thisMessage.getInitProcess().getProcessId());
						if (thisMessage.getMessageType() == 1)
							System.out.println("my id: " + process.getProcessId() + " received ACK from: " 
					    			  + thisMessage.getInitProcess().getProcessId());
						noOfAckNack--;
						if (noOfAckNack == 0) {
							if (process.isRoot()) {
								
								System.out.println("Average no of messages sent are " + (float)inte.noOfMessages/noOfLinks + ", " + inte.noOfMessages + ", " + noOfLinks);
								System.out.println("my id: " + process.getProcessId() + " has succesfully built BFS and the tree is:");
								for (int i=0;i<processes.length;i++) {
									for (int j=0;j<processes.length;j++) {
										if (processes[i] == processes[j].getParent() || processes[j] == processes[i].getParent()) {
											if (map.get(processes[i].getProcessId()) == null)  //gets the value for an id)
											    map.put(processes[i].getProcessId(), new ArrayList<Integer>()); //no ArrayList assigned, create new ArrayList

											map.get(processes[i].getProcessId()).add(processes[j].getProcessId()); //adds value to list.
											System.out.print("1");
										}
										else {
											System.out.print("0");
										}
									}
									System.out.println();
								}
								done =  true;
							}
							Message message = new Message(1, thisMessage.getDistanceFromRoot()-1, process);
							inte.noOfMessages++;
							process.sendMessageToParent(message);
						}
					}
				}
				else if (process.getDistanceFromRoot() > thisMessage.getDistanceFromRoot()+1) {
					System.out.println("my id: " + process.getProcessId() + " received message from: " 
			    			  + thisMessage.getInitProcess().getProcessId());
					if (process.getDistanceFromRoot() != Integer.MAX_VALUE) {
						System.out.println("my id: " + process.getProcessId() + " rejected message from: " 
				    			  + process.getParent().getProcessId());
						Message message = new Message(-1, process.getDistanceFromRoot()-1, process.getParent());
						inte.noOfMessages++;
						process.sendRejectMessageToSender(message);
					}
					process.setDistanceFromRoot(thisMessage.getDistanceFromRoot()+1);
					process.setParent(thisMessage.getInitProcess());
					Message message = new Message(0, thisMessage.getDistanceFromRoot()+1, process);
					if (process.getNeighbours().size() != 1)
						process.sendMessageToNeighbours(message, inte);
					else {
						message = new Message(1, thisMessage.getDistanceFromRoot(), process);
						inte.noOfMessages++;
						process.sendMessageToParent(message);
					}
				}
				else{
					System.out.println("my id: " + process.getProcessId() + " rejected message from: " 
			    			  + thisMessage.getInitProcess().getProcessId());
					Message message = new Message(-1, thisMessage.getDistanceFromRoot(), thisMessage.getInitProcess());
					inte.noOfMessages++;
					process.sendRejectMessageToSender(message);
				}
			}
		}
      } 
      catch (InterruptedException e) {
		e.printStackTrace();
      }
      
    }
  }

  @Override
  public HashMap<Integer, ArrayList<Integer>> constructBFS(int totalLinks) {
    
    noOfLinks = totalLinks;
    for(int i=0;i<processes.length;i++) {
      ProcessThreadObject threadObj = new ProcessThreadObject(processes[i]);
      threadObj.start();
    }

    //Need to return root process
    return map;
  }

}
