package org.csstudio.ams.jmsutilities;

import com.beust.jcommander.Parameter;

public class AmsJmsSenderCommandLineArgs {
    @Parameter(names = "-uri", description = "URI of JMS server", required = true)
    public String uri;
    
    @Parameter(names = "-topic", description = "JMS Topic", required = true)
    public String topic;
    
    @Parameter(names = "-template", description = "Message template file", required = false)
    public String templateFile = "templates/defaultSend.txt";
}