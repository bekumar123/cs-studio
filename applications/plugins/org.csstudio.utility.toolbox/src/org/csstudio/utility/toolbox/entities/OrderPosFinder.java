package org.csstudio.utility.toolbox.entities;

import java.util.List;

import org.csstudio.utility.toolbox.types.OrderId;

public interface OrderPosFinder {
	List<OrderPos> findPositions(OrderId baNr);
}
