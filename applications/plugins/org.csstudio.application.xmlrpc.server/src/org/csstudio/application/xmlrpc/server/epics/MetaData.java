
package org.csstudio.application.xmlrpc.server.epics;

public class MetaData {

    public static final String NOT_AVAILABLE = "N/A";

    public static final String UNKNOWN = "UNKNOWN";

	private String name;

	private Integer prec;

	private String egu;

	public MetaData(String name, String prec, String egu) {
	    this.name = UNKNOWN;
		if (name != null) {
		    if (!name.trim().isEmpty()) {
		        this.name = name.trim();
		    }
		}
		this.prec = new Integer(0);
		if (prec != null) {
    		try {
    		    this.prec = new Integer(Integer.parseInt(prec));
    		} catch (NumberFormatException e) {
    		    this.prec = new Integer(0);
    		}
		}
        this.egu = NOT_AVAILABLE;
        if (egu != null) {
            if (!egu.trim().isEmpty()) {
                this.egu = egu.trim();
            }
        }
	}

	public boolean isValid() {
	    return this.name.compareToIgnoreCase(UNKNOWN) != 0;
	}

	public String getName() {
		return name;
	}

	public Integer getPrec() {
		return prec;
	}

	public String getEgu() {
		return egu;
	}
}
