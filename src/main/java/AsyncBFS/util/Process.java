package AsyncBFS.util;

import java.util.ArrayList;
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
  
  List<Integer> neighbours;
  
  List<Process> childProcess;
  
  public Process(int id,boolean isRoot) {
    this.processId = id;
    this.isRoot = isRoot;
  }
  
  public int getProcessId() {
    return processId;
  }

  public boolean isRoot() {
    return isRoot;
  }

  
  public void addNeighbours(Integer id) {
    if(neighbours == null) {
      neighbours = new ArrayList<Integer>();
    }
    neighbours.add(id);
  }

  public List<Integer> getNeighbours(){
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
  
  

}
