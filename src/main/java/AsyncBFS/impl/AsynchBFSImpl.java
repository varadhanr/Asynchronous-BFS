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
      for (int i = 0; i < process.getNeighbours().size(); i++)
    	  System.out.println("my id: " + process.getProcessId() + " neighbor id: " 
    			  + process.getNeighbours().get(i).getProcessId());
      
      //check message passing
      Message myMessage = new Message(-1, -1, process.getProcessId()); //test message
      try {
		process.sendMessageToNeighbours(myMessage);
		Thread.sleep(1000); //sleep for a second so that everyone gets messages - this is only here to test and should be deleted
		
		//read all received message in FIFO order
		boolean done = false;
		while (!done) {
			Message thisMessage = process.getFirstMessage();
			if (thisMessage != null)
				System.out.println("My Id is " + process.getProcessId() + 
						" and I got a message from: " + thisMessage.getInitProcessId());
			else
				done = true;
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
