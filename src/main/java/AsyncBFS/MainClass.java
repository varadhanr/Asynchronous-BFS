package AsyncBFS;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import AsyncBFS.impl.AsynchBFSImpl;
import AsyncBFS.interfaces.AsynchBFS;
import AsyncBFS.util.Process;
/*
 *  
 *  Java Main Class which takes input from a file
 *  
 *  @author varadhan
 * 
 * 
 */
public class MainClass {

  public static void main(String[] args) {
    int num_of_processes;
    int noOfLinks = 0;

    Process[] processObject;

    if (args.length == 1) {

      Scanner fileScanner = null;

      try {
        fileScanner = new Scanner(new File(args[0]));
      }
      catch (FileNotFoundException e) {
        System.err.println("Error while opening the input file :" + args[0]);
        return;
      }

      num_of_processes = Integer.valueOf(fileScanner.nextLine());
      processObject = new Process[num_of_processes];
      int rootId = Integer.valueOf(fileScanner.nextLine());

      for (int i = 0; i < num_of_processes; i++) {
        processObject[i] = new Process(i, i == rootId ? true : false);
      }

      for (int i = 0; i < num_of_processes; i++) {
        Process p = processObject[i];
        if(fileScanner.hasNextLine()) {
          String[] nextLine = fileScanner.nextLine().split(",");
          for(int j=0;j<nextLine.length;j++) {
            if(nextLine[j].trim().equals("1") && i !=j) {
              noOfLinks++;
              p.addNeighbours(processObject[j]);
            }
          }
        }
      }
      
      //close the scanner
      fileScanner.close();

      AsynchBFS bfs = new AsynchBFSImpl(rootId, processObject);
      HashMap<Integer, List<Integer>> map = bfs.constructBFS(noOfLinks/2);
      
      System.out.println("BFS tree rooted at " + rootId +" is:");
      for(int i=0;i<num_of_processes;i++) {
    	  List<Integer>childNodes = map.get(processObject[i].getProcessId());
    	  for (int j=0;j<num_of_processes;j++) {
    		  if (childNodes.contains(processObject[j].getProcessId()))
    			  System.out.print("1");
    		  else
    			  System.out.print("0");
   	      }
    	  System.out.println();
      }
      
      System.exit(0);
    }
    else {
      System.err.println("Please enter a valid input.txt file");
    }
  }
}
