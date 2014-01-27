package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.MapKey;

@Entity
@SequenceGenerator(name="AMS_MSG_EXT_ID", sequenceName="AMS_MSG_EXT_ID", allocationSize=1)
@Table(name="AMS_MSG_EXT_PVS")
public class ExtendedMessagePvDTO implements NewAMSConfigurationElementDTO {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="AMS_MSG_EXT_ID")
	@Column(name="ID")
	private int id;
	
	
	@Column(name="CPVNAME", unique=true)
	private String pvName;
	
	@Column(name="IGROUPREF")
	private int groupRef;

	@CollectionOfElements(targetElement=java.lang.String.class, fetch=FetchType.EAGER)
	@JoinTable(name="AMS_MSG_EXTENSIONS", joinColumns=@JoinColumn(name="IDREF"))
	@MapKey(columns=@Column(name="CMESSAGEKEY"))
	@Column(name="CMESSAGEVALUE")
	private Map<String, String> messageExtensions;
	
	public Map<String, String> getMessageExtensions() {
		return messageExtensions;
	}
	
	public void setMessageExtensions(Map<String, String> messageExtensions) {
		this.messageExtensions = messageExtensions;
	}
	
	public String getPvName() {
		return pvName;
	}
	
	public void setPvName(String pvName) {
		this.pvName = pvName;
	}
	
	public int getGroupRef() {
		return groupRef;
	}
	
	public void setGroupRef(int groupRef) {
		this.groupRef = groupRef;
	}
	
	@Override
	public String getUniqueHumanReadableName() {
		return getPvName();
	}

	@Override
	public boolean isInCategory(int categoryDBId) {
		return false;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "PV: " + pvName + " (Group #" + groupRef + ") " + messageExtensions;
	}

	
	
}
