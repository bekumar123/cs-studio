package org.csstudio.ams.performancetesttool;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.csstudio.nams.application.department.decision.office.decision.DecisionDepartment;
import org.csstudio.nams.common.DefaultExecutionService;
import org.csstudio.nams.common.decision.CasefileId;
import org.csstudio.nams.common.decision.DefaultDocumentBox;
import org.csstudio.nams.common.decision.Inbox;
import org.csstudio.nams.common.decision.MessageCasefile;
import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.AlarmMessage;
import org.csstudio.nams.common.material.FilterId;
import org.csstudio.nams.common.material.regelwerk.DefaultFilter;
import org.csstudio.nams.common.material.regelwerk.Filter;
import org.csstudio.nams.common.material.regelwerk.FilterCondition;
import org.csstudio.nams.common.material.regelwerk.OrFilterCondition;
import org.csstudio.nams.common.material.regelwerk.StringFilterCondition;
import org.csstudio.nams.common.material.regelwerk.StringFilterConditionOperator;
import org.csstudio.nams.common.material.regelwerk.WeiteresVersandVorgehen;
import org.csstudio.nams.service.logging.declaration.ILogger;
import org.csstudio.nams.service.logging.impl.LoggerImpl;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class SelfContainedPerformanceTest {

	private static final int PERCENTAGE_USER_INFO = 10; // 0 < PERCENTAGE_USER_INFO <= 100 

	private volatile long[] erzeugungsZeiten;
	private long[] empfangsZeiten;
	private final SelfContainedCommandLineArgs arguments;
	
	private int messageCounter;
	
	public SelfContainedPerformanceTest(final SelfContainedCommandLineArgs arguments) {
		this.arguments = arguments;
		erzeugungsZeiten = new long[arguments.messageCount];
		empfangsZeiten = new long[arguments.messageCount];
	}
	
	public void run() throws UnknownHostException, InterruptedException {
		printConfig();
		messageCounter = 0;
		final Inbox<MessageCasefile> alarmVorgangEingangskorb = new DefaultDocumentBox<MessageCasefile>();
		final DefaultDocumentBox<MessageCasefile> alarmVorgangAusgangskorb = new DefaultDocumentBox<MessageCasefile>();
		ILogger logger = new LoggerImpl();
		
		DecisionDepartment alarmEntscheidungsBuero = new DecisionDepartment(new DefaultExecutionService(), 
																erzeugeRegelwerke(arguments.ruleCount), 
																alarmVorgangEingangskorb, 
																alarmVorgangAusgangskorb, 
																arguments.threads, logger);
		
		final MessageCasefile[] vorgangsmappen = erzeugeVorgangsmappen(arguments.messageCount);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int index = 0; index < vorgangsmappen.length; index++) {
					try {
						MessageCasefile vorgangsmappe = vorgangsmappen[index];
						erzeugungsZeiten[index] = System.currentTimeMillis();
						alarmVorgangEingangskorb.put(vorgangsmappe);
						if (arguments.rate > 0) {
							Thread.sleep(1000 / arguments.rate);
						}
						else {
							Thread.yield();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		System.out.println("\nReceiving messages");
		while(messageCounter<arguments.messageCount) {
			alarmVorgangAusgangskorb.takeDocument(); // blocks
			empfangsZeiten[messageCounter] = System.currentTimeMillis();
			messageCounter += 1;
			printProgressBar();
		}
		System.out.print("\n");
		printMeasurements();
		alarmEntscheidungsBuero.beendeArbeitUndSendeSofortAlleOffeneneVorgaenge();
		System.exit(0);
	}

	
	private MessageCasefile[] erzeugeVorgangsmappen(int anzahlAnMappen) {
		MessageCasefile[] result = new MessageCasefile[anzahlAnMappen];
		for(int index = 0; index < anzahlAnMappen; index++) {
			HashMap<MessageKeyEnum, String> mapMessage = new HashMap<MessageKeyEnum, String>(1);
			mapMessage.put(MessageKeyEnum.NAME, "TEST");
			result[index] = new MessageCasefile(CasefileId.createNew(), new AlarmMessage(mapMessage));
		}
		return result;
	}
	
	private List<Filter> erzeugeRegelwerke(int anzahlAnRegelwerken) {
		assert anzahlAnRegelwerken >= 1;
		List<Filter> result = new ArrayList<Filter>(anzahlAnRegelwerken);
		for(int index = 0; index < anzahlAnRegelwerken; index++) {
			FilterCondition stringRegel1 = new StringFilterCondition(StringFilterConditionOperator.OPERATOR_TEXT_EQUAL, MessageKeyEnum.SEVERITY, "Sehr hoch", null);
			FilterCondition stringRegel2 = new StringFilterCondition(StringFilterConditionOperator.OPERATOR_NUMERIC_GT, MessageKeyEnum.EVENTTIME, "" + System.currentTimeMillis(), null);
			FilterCondition oderRegel = new OrFilterCondition(Arrays.asList(stringRegel2, stringRegel1));
			result.add(new DefaultFilter(FilterId.valueOf(index), oderRegel));
		}
		result.set(anzahlAnRegelwerken-1, new DefaultFilter(FilterId.valueOf(anzahlAnRegelwerken-1), new StringFilterCondition(StringFilterConditionOperator.OPERATOR_TEXT_EQUAL, MessageKeyEnum.NAME, "TEST", null)));
		return result;
	}
	
	private void printMeasurements() {
		long maxLatency = 0;
		long minLatency = Long.MAX_VALUE;
		long latencySum = 0;
		for (int index = 0; index < empfangsZeiten.length; index++) {
			long latency = empfangsZeiten[index] - erzeugungsZeiten[index];
			maxLatency = Math.max(maxLatency, latency);
			minLatency = Math.min(minLatency, latency);
			latencySum += latency;
		}
		System.out.println("Max latency: "+maxLatency + " ms");
		System.out.println("Min latency: "+minLatency + " ms");
		System.out.println("Average latency: "+latencySum/empfangsZeiten.length + " ms");
		System.out.println("Average number of messages/second: " + 1000.0/(1.0*(empfangsZeiten[empfangsZeiten.length-1]-erzeugungsZeiten[0])/empfangsZeiten.length));
	}

	private void printProgressBar() {
		double previousPercent = (1.0 * (messageCounter -1) / arguments.messageCount) * 100.0;
		double percent = (1.0 * messageCounter / arguments.messageCount) * 100.0;
		if (Math.floor(previousPercent) < Math.floor(percent)) {
			int percentInt = (int)percent;
			if (percentInt == 100 || ((int)previousPercent/PERCENTAGE_USER_INFO) < (percentInt/PERCENTAGE_USER_INFO)) {
				System.out.print(percentInt + "%\n");
			}
			else {
				System.out.print(".");
			}
		}
	}
	
	private void printConfig() {
		System.out.println(arguments.threads + " threads");
		System.out.println(arguments.ruleCount + " filters");
		if(arguments.rate == 0) {
			System.out.println("Unlimited messages/second");
		}
		else {
			System.out.println(arguments.rate + " messages/second");
		}
		System.out.println(arguments.messageCount + " alarm messages");
	}
	
	public static void main(String[] args) {
		SelfContainedCommandLineArgs arguments = new SelfContainedCommandLineArgs();
		new JCommander(arguments, args);
        try {
            new SelfContainedPerformanceTest(arguments).run();
        } catch (Exception e) {
            System.err.println("An error occured, the test was aborted");
            e.printStackTrace();
        }
	}
	
	private static class SelfContainedCommandLineArgs {
		
	    @Parameter(names = { "--threads","-t" }, description = "Number of threads to process alarm filters")
	    public int threads = 1;
	    
	    @Parameter(names = { "--messagecount","-mc" }, description = "Number of messages to send")
	    public int messageCount = 1;
	    
	    @Parameter(names = { "--rate","-r" }, description = "Limit the send rate of messages to less than this number of messages per second. (0..1000, 0 = unlimited)")
	    public int rate = 0;
	    
	    @Parameter(names = { "--rulecount","-rc" }, description = "Number of filter rules to be used by filter manager. (>=1)")
	    public int ruleCount = 1;
	}
	
}
