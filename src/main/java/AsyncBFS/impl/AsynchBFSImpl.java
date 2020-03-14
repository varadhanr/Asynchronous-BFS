package AsyncBFS.impl;

import AsyncBFS.interfaces.AsynchBFS;
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
      System.out.println(process.getProcessId());
      System.out.println(process.getNeighbours());
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
