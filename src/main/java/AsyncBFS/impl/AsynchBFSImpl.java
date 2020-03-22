package AsyncBFS.impl;

import AsyncBFS.interfaces.AsynchBFS;
import AsyncBFS.util.Message;
import AsyncBFS.util.Process;

public class AsynchBFSImpl implements AsynchBFS {

  int rootId;
  Process[] processes;

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
      Message myMessage = new Message(0, 0, process.getProcessId()); //test message
      try {
    	if (process.isRoot())
    		process.sendMessageToNeighbours(myMessage);
		//Thread.sleep(1000); //sleep for a second so that everyone gets messages - this is only here to test and should be deleted
		
    	int noOfAckNack = process.getNeighbours().size();
		//read all received message in FIFO order
		while (true) {
			Message thisMessage = process.getFirstMessage();
			if (thisMessage != null) {
				if (thisMessage.getMessageType() == 1) {
					System.out.println("my id: " + process.getProcessId() + " received reject message from: " 
			    			  + thisMessage.getInitProcessId());
					noOfAckNack--;
					if (noOfAckNack == 0) {
						if (process.isRoot()) {
							System.out.println("my id: " + process.getProcessId() + " has succesfully built BFS.");
							break;
						}
						Message message = new Message(1, thisMessage.getDistanceFromRoot(), thisMessage.getInitProcessId());
						process.sendMessageToParent(message);
					}
				}
				else if (process.getDistanceFromRoot() > thisMessage.getDistanceFromRoot()+1) {
					System.out.println("my id: " + process.getProcessId() + " received message from: " 
			    			  + thisMessage.getInitProcessId());
					process.setDistanceFromRoot(thisMessage.getDistanceFromRoot()+1);
					process.setParent(processes[thisMessage.getInitProcessId()]);
					Message message = new Message(0, thisMessage.getDistanceFromRoot()+1, process.getProcessId());
					if (process.getNeighbours().size() != 1)
						process.sendMessageToNeighbours(message);
					else {
						message = new Message(1, thisMessage.getDistanceFromRoot(), thisMessage.getInitProcessId());
						process.sendMessageToParent(message);
					}
				}
				else{
					System.out.println("my id: " + process.getProcessId() + " rejected message from: " 
			    			  + thisMessage.getInitProcessId());
					Message message = new Message(1, thisMessage.getDistanceFromRoot(), thisMessage.getInitProcessId());
					process.sendMessageToParent(message);
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
  public Process constructBFS() {
    
    
    for(int i=0;i<processes.length;i++) {
      ProcessThreadObject threadObj = new ProcessThreadObject(processes[i]);
      threadObj.start();
    }
    
    //Need to return root process
    return new Process(0,true);

  }

}
