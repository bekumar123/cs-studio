package org.csstudio.utility.toolbox.services;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.csstudio.utility.toolbox.entities.OrderPos;
import org.csstudio.utility.toolbox.entities.OrderPosFinder;
import org.csstudio.utility.toolbox.guice.ClearPersistenceContextOnReturn;
import org.csstudio.utility.toolbox.types.OrderId;

import com.google.inject.Inject;

public class OrderPosService implements OrderPosFinder {
	
	@Inject
	private EntityManager em;

	@ClearPersistenceContextOnReturn
	public List<OrderPos> findByGruppeArtikel(BigDecimal gruppeArtikel) {
		TypedQuery<OrderPos> query = em.createNamedQuery(OrderPos.FIND_IN_ARTIKEL_DATEN_ID, OrderPos.class);
		query.setParameter("artikelDatenId", gruppeArtikel);
		return query.getResultList();		
	}
	
	@ClearPersistenceContextOnReturn
	@Override
	public List<OrderPos> findPositions(OrderId baId) {
		TypedQuery<OrderPos> query = em.createNamedQuery(OrderPos.FIND_BY_PARENT_ID, OrderPos.class);
		query.setParameter("baId", baId.getValue());
		return query.getResultList();		
	}
}
