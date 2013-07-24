package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterAction2FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.HasManuallyJoinedElements;

/**
 * Dieses Daten-Transfer-Objekt stellt hält die Konfiguration eines Filters dar
 * 
 * Das Create-Statement für die Datenbank hat folgendes Aussehen:
 * 
 * <pre>
 * Create table AMS_Filter
 * 
 * iFilterID		INT,
 * iGroupRef		INT default -1 NOT NULL,
 * cName			VARCHAR(128),
 * cDefaultMessage	VARCHAR(1024),
 * cFilterType   VARCHAR(200) default 'default' NOT NULL,
 * PRIMARY KEY (iFilterID)
 * ;
 * </pre>
 */
@Entity
@SequenceGenerator(name="filter_id", sequenceName="AMS_Filter_ID", allocationSize=1)
@Table(name = "AMS_Filter")
@Inheritance()
@DiscriminatorColumn(name="CFILTERTYPE")
public abstract class FilterDTO implements NewAMSConfigurationElementDTO, HasManuallyJoinedElements {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="filter_id")
	@Column(name = "iFilterID")
	private int iFilterID; // INT,

	@Column(name = "iGroupRef", nullable = false)
	private int iGroupRef = -1; // INT default -1 NOT NULL,

	@Column(name = "cName", length = 128)
	private String name; // VARCHAR(128),

	@Column(name = "cDefaultMessage", length = 1024)
	private String defaultMessage; // VARCHAR(1024),

	public String getDefaultMessage() {
		return this.defaultMessage;
	}
	
	public void setDefaultMessage(final String defaultMessage) {
		this.defaultMessage = defaultMessage;
	}

	public int getIFilterID() {
		return this.iFilterID;
	}
	
	@Transient
	private List<FilterActionDTO> filterActions = new LinkedList<FilterActionDTO>();

	public List<FilterActionDTO> getFilterActions() {
		return this.filterActions;
	}

	public void setFilterActions(List<FilterActionDTO> filterActions) {
		this.filterActions = filterActions;
	}
	
	/**
	 * Kategorie
	 */
	public int getIGroupRef() {
		return this.iGroupRef;
	}

	public void setIGroupRef(final int groupRef) {
		this.iGroupRef = groupRef;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public boolean isInCategory(final int categoryDBId) {
		return false;
	}

	@Override
	public String getUniqueHumanReadableName() {
		return this.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((defaultMessage == null) ? 0 : defaultMessage.hashCode());
		result = prime * result + iFilterID;
		result = prime * result + iGroupRef;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FilterDTO other = (FilterDTO) obj;
		if (defaultMessage == null) {
			if (other.defaultMessage != null)
				return false;
		} else if (!defaultMessage.equals(other.defaultMessage))
			return false;
		if (iFilterID != other.iFilterID)
			return false;
		if (iGroupRef != other.iGroupRef)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(this.getClass()
				.getSimpleName());
		builder.append(": ");
		builder.append("iFilterID: ");
		builder.append(this.iFilterID);
		builder.append(", iGroupRef: ");
		builder.append(this.iGroupRef);
		builder.append(", cName: ");
		builder.append(this.name);
		return builder.toString();
	}
	
	@Override
	public void loadJoinData(Mapper mapper) throws Throwable {
		final List<FilterAction2FilterDTO> actionJoins = mapper.loadAll(
				FilterAction2FilterDTO.class, false);
	
		Collections.sort(actionJoins, new Comparator<FilterAction2FilterDTO>() {
			public int compare(final FilterAction2FilterDTO o1,
					final FilterAction2FilterDTO o2) {
				return o1.getIPos() - o2.getIPos();
			}
		});
		
		this.filterActions.clear();
		
		for (final FilterAction2FilterDTO actionJoin : actionJoins) {
			if (actionJoin.getId().getIFilterRef() == this.getIFilterID()) {
				final FilterActionDTO foundAction = mapper.findForId(
						FilterActionDTO.class, actionJoin.getId()
								.getIFilterActionRef(), true);
				assert foundAction != null : "Es existiert eine Action mit der ID "
						+ actionJoin.getId().getIFilterActionRef();
	
				this.filterActions.add(foundAction);
			}
		}
	}
	
	@Override
	public void storeJoinLinkData(Mapper mapper) throws Throwable {
		// Actionen speichern
		Map<FilterActionDTO, FilterAction2FilterDTO> noNeedToSave = new HashMap<FilterActionDTO, FilterAction2FilterDTO>();
		List<FilterAction2FilterDTO> allActionJoins = mapper.loadAll(FilterAction2FilterDTO.class, false);
		for (FilterAction2FilterDTO filterAction2FilterDTO : allActionJoins) {
			if (filterAction2FilterDTO.getId().getIFilterRef() == this.getIFilterID()) {
				FilterActionDTO filterActionDTO = mapper.findForId(FilterActionDTO.class, filterAction2FilterDTO.getId().getIFilterActionRef(), true);
				if (!getFilterActions().contains(filterActionDTO)) {
					mapper.delete(filterAction2FilterDTO);
					mapper.delete(filterActionDTO);
				} else {
					noNeedToSave.put(filterActionDTO, filterAction2FilterDTO);
				}
			}
		}
		
		int iPos = 0;
		for (FilterActionDTO actionDTO : getFilterActions()) {
			FilterAction2FilterDTO filterAction2FilterDTO = noNeedToSave.get(actionDTO);
			if (filterAction2FilterDTO == null) {
				mapper.save(actionDTO);
				mapper.save(new FilterAction2FilterDTO(actionDTO, this, iPos));
			} else {
				filterAction2FilterDTO.setIPos(iPos);
				mapper.save(filterAction2FilterDTO);
			}
			iPos++;
		}		
	}
	
	@Override
	public void deleteJoinLinkData(Mapper mapper) throws Throwable {
		int iPos = 0;
		for (FilterActionDTO action : getFilterActions()) {
			mapper.delete(new FilterAction2FilterDTO(action, this, iPos));
			mapper.delete(mapper.findForId(FilterActionDTO.class, action
					.getIFilterActionID(), false));
			iPos++;
		}
		
	}
}
