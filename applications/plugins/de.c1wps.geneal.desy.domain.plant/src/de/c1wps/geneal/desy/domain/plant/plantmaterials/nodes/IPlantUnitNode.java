package de.c1wps.geneal.desy.domain.plant.plantmaterials.nodes;

import java.util.List;

import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantNodeId;
import de.c1wps.geneal.desy.domain.plant.domainvalues.PlantUnitId;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.IPlantUnit;
import de.c1wps.geneal.desy.domain.plant.plantmaterials.PlantUnitReference;

public interface IPlantUnitNode {

	public PlantNodeId getId();

	public String getDisplayName();

	public String getReferenceCode();

	public String getTypeName();

	PlantUnitId getTypeId();
	
	boolean hasType();

	public IPlantUnitNode getParent();

	public boolean hasParent();

	public boolean hasChildPlantUnitNodes();

	public List<IPlantUnitNode> getChildPlantUnitNodes();

	public IPlantUnitNode copyDeep();

	public IPlantUnit getPlantUnit();

	public boolean canAddChildPlantUnitNode(IPlantUnitNode node);

	public void addChildPlantUnitNode(IPlantUnitNode node);

	public IPlantUnitNode createClearCopy();

	public void delete();

	PlantUnitReference getPlantUnitReference();

	boolean hasPlantUnitReference();
}
