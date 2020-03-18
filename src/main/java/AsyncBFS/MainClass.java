package AsyncBFS;

import java.io.File;
import java.io.FileNotFoundException;
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

      num_of_processes = fileScanner.nextInt();
      processObject = new Process[num_of_processes];
      int rootId = fileScanner.nextInt();

      for (int i = 0; i < num_of_processes; i++) {
        processObject[i] = new Process(i, i == rootId ? true : false);
      }

      for (int i = 0; i < num_of_processes; i++) {
        Process p = processObject[i];
        for (int j = 0; j < num_of_processes; j++) {
          if (fileScanner.nextInt() == 1 && i != j) {
            p.addNeighbours(processObject[j]);
          }
        }
      }
      
      //close the scanner
      fileScanner.close();

      AsynchBFS bfs = new AsynchBFSImpl(rootId, processObject);
      Process rootProcess = bfs.constructBFS();
      

    }
    else {
      System.err.println("Please enter a valid input.txt file");
    }
  }
}
