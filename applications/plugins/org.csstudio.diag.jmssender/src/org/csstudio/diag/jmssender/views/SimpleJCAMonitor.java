package org.csstudio.diag.jmssender.views;

import gov.aps.jca.*;
import gov.aps.jca.dbr.*;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;

/**
 * Monitors a process variable
 */
public class SimpleJCAMonitor 
{
  private static final double TIMEOUT=10.0;

  private double timeout=TIMEOUT;
  private String name=null;
  private boolean pvSpecified=false;
  private int connectionCounter=0;
  private int monitorCounter=0;
  private boolean nameFound=false;
  
  /**
   * Default constructor
   */
  public SimpleJCAMonitor()
  {
  }

  /**
   * Implementation of Connection Listener class
   */
  private class SJCAConnectionListener implements ConnectionListener {
    public void connectionChanged(ConnectionEvent ev) {
      onConnectionChanged(ev);
    }
  };

  /**
   * Implementation of MonitorListener class
   */
  private class SJCAMonitorListener implements MonitorListener {
    public void monitorChanged(MonitorEvent ev) {
      onValueChanged(ev);
    }
  };

  /**
   * Main entry point
   * @param args arguments
 * @throws TimeoutException 
 * @throws IllegalStateException 
   */
  public static void startEpicsTest() throws IllegalStateException, TimeoutException
  {
	  
//	printInfoTestIgor();
	
    synchGetTestJan();
    	      
    SimpleJCAMonitor sjcam = new SimpleJCAMonitor();
    JCALibrary jca=null;
    Context ctxt=null;
    Channel chan=null;
  
    System.out.println(SJCAUtils.timeStamp() + " Starting Simple JCA Monitor");

  // Parse the command line
//    if(!sjcam.parseCommand(args)) System.exit(1);
//    if(!sjcam.pvSpecified) {
//      System.err.println("No PV specified\n");
//      System.exit(1);
//    }


    sjcam.name = "krykWeather:Temp_ai";
    // Initialize JCA
    try {
      // Get the JCALibrary instance
      jca=JCALibrary.getInstance();

      // Create a thread safe context with default configuration values
      ctxt=jca.createContext(JCALibrary.JNI_THREAD_SAFE);
    } catch(Exception ex) {
      System.err.println("Initialization failed for " + sjcam.name +
       ":\n" + ex);
      System.exit(1);
    }

  // Search
    try {
      // Search
      chan=ctxt.createChannel(sjcam.name,sjcam.new SJCAConnectionListener());
      ctxt.flushIO();

    } catch(Exception ex) {
      System.err.println("Search failed for " + sjcam.name +
       ":\n" + ex);
      System.exit(1);
    }

    // Main loop
    try {
      long timeoutms=(long)(1000.0*sjcam.getTimeout());
      Thread.sleep(timeoutms);
    } catch(Exception ex) {
      System.err.println(ex);
    }
    if(!sjcam.isNameFound()) {
      System.out.println(sjcam.getName() + " not found");
    }

    // Clean up
    try {
      // Clear the channel
      chan.destroy();
      
      // Destroy the context
      ctxt.destroy();
      
    } catch(Exception ex) {
      System.err.println("Clean up failed for " + sjcam.name + ":\n" +
       ex);
      System.exit(1);
    }

    // Exit
    System.out.println(SJCAUtils.timeStamp() + " All Done");
    System.exit(0);

  }

private static void synchGetTestJan() {
	try {
        // Get the JCALibrary instance.
        JCALibrary jca= JCALibrary.getInstance();
        
        // Create a single threaded context with default configuration values.
        Context ctxt= jca.createContext(JCALibrary.JNI_SINGLE_THREADED);
        
        // Display basic information about the context.
        ctxt.printInfo();
        
        // Create the Channel to connect to the PV.
//        Channel ch= ctxt.createChannel("krykWeather:vWind_ai");
        Channel ch= ctxt.createChannel("krykWeather:vWindBoe_ai");

        Thread.sleep(500);
        // send the request and wait 5.0 seconds for the channel to connect to the PV.
        ctxt.pendIO(5.0);
        Thread.sleep(500);
   
        // If we're here, then everything went fine.
        // Display basic information about the channel.
        ch.printInfo();
        DBR dbr = ch.get();
        
        
        DBR_Double dbrDouble = (DBR_Double) dbr;
        double[] doubleValue = dbrDouble.getDoubleValue();
  	System.out.println(doubleValue[0]);
        
        // Disconnect the channel.
        ch.destroy();
   
        // Destroy the context.
        ctxt.destroy();


      } catch(Exception ex) {
        System.err.println(ex);
      }
}

private static void printInfoTestIgor() throws TimeoutException {
	try {
		Context context;
		context = JCALibrary.getInstance().createContext(
			        JCALibrary.JNI_SINGLE_THREADED);
		Channel channel = context.createChannel("krykWeather:Temp_ai");
		context.printInfo();
		context.pendIO(5.0);
		channel.printInfo();
		channel.destroy();
		context.destroy();
		
	} catch (CAException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
  
  /**
   * Parse the command line 
   * @param args
   * @return success or failure
   */
  private boolean parseCommand(String[] args)
  {
    int i;
  
    for(i=0; i < args.length; i++) {
      if (args[i].startsWith("-")) {
        switch(args[i].charAt(1)) {
        case 'h':
          usage();
          System.exit(0);
        case 't':
          try {
            timeout=Double.valueOf(args[++i]).doubleValue();
          } catch(NumberFormatException ex)  {
            System.err.println("\n\nInvalid timeout: " + args[i]);
            usage();
            return false;
          }
          break;
        default:
          System.err.println("\n\nInvalid option: " + args[i]);
          usage();
          return false;
        }
      } else {
        if(!pvSpecified) {
          name=args[i];
          pvSpecified=true;
        } else {
          System.err.println("\n\nInvalid option: " + args[i]);
          usage();
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Print usage
   */
  private void usage()
  {
    System.err.println("\nUsage: java SimpleJCAMonitor [Options] pvname\n" +
     "  Connects to pvname and gets the value\n" +
     "\n" +
     "  Options:\n" +
     "    -h help      This message\n" +
     "    -t float     Timeout in seconds (Default: " + TIMEOUT + ")\n");
  }


  /**
   * Set process variable name
   * @param name process variable name
   */
  public void setName(String name) {
    this.name = name;
  }

// Accessors //////////////////////////////////////////////////////////////////

  /**
   * Get process variable name
   * @return process variable name
   */
  public String getName() {
    return name;
  }


  /**
   * Set timeout
   * @param timeout timeout in sec
   */
  public void setTimeout(double timeout) {
    this.timeout = timeout;
  }


  /**
   * Get timeout
   * @return timeout in sec
   */
  public double getTimeout() {
    return timeout;
  }
  
  /**
   * 
   * @return if PV name has been found
   */
  public boolean isNameFound() {
    return nameFound;
  }
  
  
// Callbacks //////////////////////////////////////////////////////////////////

  /**
   * Connection callback
   * @param ev
   */
  private void onConnectionChanged(ConnectionEvent ev) {
    Channel ch=(Channel)ev.getSource();
    Context ctxt=ch.getContext();
/*
    System.out.println(SJCAUtils.timeStamp() + " ConnectionEvent for: \n  " +
     ch.getName() + " [" + getName() + "]");
*/
    // Start a monitor on the first connection    
    if(connectionCounter == 0 && ch.getConnectionState() == Channel.CONNECTED) {
      // This is the first connection.
      try {
        // Print some information
        nameFound=true;
        System.out.println(SJCAUtils.timeStamp() + " Search successful for: " +
         getName());
        ch.printInfo();

      // Add a monitor listener
        ch.addMonitor(DBRType.STRING,1,Monitor.VALUE|Monitor.LOG|Monitor.ALARM,
         new SJCAMonitorListener());
        ctxt.flushIO();
      } catch(Exception ex) {
        ex.printStackTrace();
      }
    }

    // Print connection state
    System.out.print(SJCAUtils.timeStamp() + " ");
    if(ch.getConnectionState() == Channel.CONNECTED) {
      System.out.println(ch.getName() + " is connected");
      connectionCounter++;
    } else if(ch.getConnectionState() == Channel.CLOSED) {
      System.out.println(ch.getName() + " is closed");
    } else if(ch.getConnectionState() == Channel.DISCONNECTED) {
      System.out.println(ch.getName() + " is disconnected");
    } else if(ch.getConnectionState() == Channel.NEVER_CONNECTED) {
      System.out.println(ch.getName() + " is never connected");
    } else {
      // Shouldn't happen
      System.out.println(ch.getName() + " is in an unknown state");
    }
  }

  /**
   * Monitor callback
   * @param ev
   */
  private void onValueChanged(MonitorEvent ev) {
    Channel ch=(Channel)ev.getSource();
    Context ctxt=ch.getContext();

    monitorCounter++;
/*
    System.out.println(SJCAUtils.timeStamp() + " MonitorEvent for: \n  " +
     ch.getName() + " [" + sjcam.getName() + "]");
*/

  // Check the status
    if (ev.getStatus() != CAStatus.NORMAL) {
      System.err.println("monitorChanged: Bad status for: " +
       getName());
    }
    
    // Get the value from the DBR
    try {
      DBR dbr=ev.getDBR();
      String [] value=((STRING)dbr).getStringValue();
      System.out.print(SJCAUtils.timeStamp() + " " + getName() +
       ": ");
      dbr.printValue(value);
      System.out.println();

/*      
      System.out.println(" Information for " + ch.getName() + ":");
      dbr.printInfo();
*/      
    } catch(Exception ex) {
      System.err.println("monitorChanged: Bad value for " +
      ch.getName() + ":\n " + ex);
      return;
    }
  }
  
}
