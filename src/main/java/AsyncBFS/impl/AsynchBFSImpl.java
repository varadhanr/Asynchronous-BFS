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
  HashMap<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
  
  public class Inte{
      public int noOfMessages = 0;
  }
  
  Inte inte = new Inte();

  public AsynchBFSImpl(int rootId,Process[] processess) {
    this.rootId = rootId;
    this.processes = processess;
  }

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
      Message myMessage = new Message(0, 0, process);
      try {
    	int noOfAckNack = process.getNeighbours().size()-1;
    	if (process.isRoot()) {
    		noOfAckNack = process.getNeighbours().size();
    		process.setDistanceFromRoot(0);
    		process.sendMessageToNeighbours(myMessage, inte);
    	}
    	
		//read all received message in FIFO order
		while (!done) {
			Message thisMessage = process.getFirstMessage();
			if (thisMessage != null) {
				//Process receives response from neighbor
				if (thisMessage.getMessageType() == -1 || thisMessage.getMessageType() == 1) {
					//Process has updated the parent, as a result ignores response from previously set child
					if (thisMessage.getDistanceFromRoot() != process.getDistanceFromRoot()) {
						System.out.println("Process id " + process.getProcessId() +" ignores the acnowledgment from " + thisMessage.getInitProcess().getProcessId());
					}
					//Process receives response from child
					else {
						if (thisMessage.getMessageType() == -1)
							System.out.println("Process id " + process.getProcessId() + " received NACK from " 
				    			  + thisMessage.getInitProcess().getProcessId());
						if (thisMessage.getMessageType() == 1)
							System.out.println("Process id " + process.getProcessId() + " received ACK from " 
					    			  + thisMessage.getInitProcess().getProcessId());
						noOfAckNack--;
						//Received response from every child
						if (noOfAckNack == 0) {
							//Terminate if it is root
							if (process.isRoot()) {
								System.out.println("No of messages sent are" + inte.noOfMessages + " , no of links are " + noOfLinks);
								System.out.println("Average no of messages sent are " + (float)inte.noOfMessages/noOfLinks);
								System.out.println("Process id " + process.getProcessId() + " has succesfully built BFS.");
								//Print BFS tree
								for (int i=0;i<processes.length;i++) {
									for (int j=0;j<processes.length;j++) {
										if (processes[i] == processes[j].getParent() || processes[j] == processes[i].getParent()) {
											if (map.get(processes[i].getProcessId()) == null) //gets the value for an id)
											    map.put(processes[i].getProcessId(), new ArrayList<Integer>()); //no ArrayList assigned, create new ArrayList

											map.get(processes[i].getProcessId()).add(processes[j].getProcessId()); //adds value to list.
										}
									}
								}
								done =  true;
							}
							//Send ACK to parent
							else {
								Message message = new Message(1, thisMessage.getDistanceFromRoot()-1, process);
								inte.noOfMessages++;
								process.sendMessageToSender(message, process.getParent());
							}
						}
					}
				}
				
				//Test message from a neighbor with fewer no of nodes from root
				else if (process.getDistanceFromRoot() > thisMessage.getDistanceFromRoot()+1) {
					System.out.println("Process id " + process.getProcessId() + " received message from " 
			    			  + thisMessage.getInitProcess().getProcessId());
					if (process.getParent() != null) {
						System.out.println("Process id " + process.getProcessId() + " rejected message from " 
				    			  + process.getParent().getProcessId());
						Message message = new Message(-1, process.getDistanceFromRoot()-1, process);
						inte.noOfMessages++;
						process.sendMessageToSender(message, process.getParent());
					}
					process.setDistanceFromRoot(thisMessage.getDistanceFromRoot()+1);
					process.setParent(thisMessage.getInitProcess());
					Message message = new Message(0, thisMessage.getDistanceFromRoot()+1, process);
					if (process.getNeighbours().size() != 1)
						process.sendMessageToNeighbours(message, inte);
					else {
						message = new Message(1, thisMessage.getDistanceFromRoot(), process);
						inte.noOfMessages++;
						process.sendMessageToSender(message, process.getParent());
					}
				}
				//Test message from neighbor is not useful
				else{
					System.out.println("Process id " + process.getProcessId() + " rejected message from " 
			    			  + thisMessage.getInitProcess().getProcessId());
					Message message = new Message(-1, thisMessage.getDistanceFromRoot(), process);
					inte.noOfMessages++;
					process.sendMessageToSender(message, thisMessage.getInitProcess());
				}
			}
		}
      } 
      catch (InterruptedException e) {
    	  System.out.println(e.toString());
		e.printStackTrace();
      }
      
    }
  }

  @Override
  public HashMap<Integer, List<Integer>> constructBFS(int totalLinks) {
	  List<ProcessThreadObject> threads = new ArrayList<AsynchBFSImpl.ProcessThreadObject>();
	    noOfLinks = totalLinks;
	    for (int i = 0; i < processes.length; i++) {
	      ProcessThreadObject threadObj = new ProcessThreadObject(processes[i]);
	      threads.add(threadObj);
	      threadObj.start();
	    }

	    for (int i = 0; i < processes.length; i++) {
	      try {
	        threads.get(i).join();
	      } catch (InterruptedException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	      }
	    }
	    System.out.println("Thread execution done");
	    // Need to return root process
	    return map;
  }

}
