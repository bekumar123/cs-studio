package org.csstudio.utility.toolbox.services;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.csstudio.utility.toolbox.entities.OrderType;
import org.csstudio.utility.toolbox.guice.ClearPersistenceContextOnReturn;

import com.google.inject.Inject;

public class OrderTypeService {

   @Inject
   private EntityManager em;

   @ClearPersistenceContextOnReturn
   public List<OrderType> findAll() {
      TypedQuery<OrderType> query = em.createNamedQuery(OrderType.FIND_ALL, OrderType.class);
      return query.getResultList();
   }

   public OrderType findByText(String text) {
      TypedQuery<OrderType> query = em.createNamedQuery(OrderType.FIND_BY_TEXT, OrderType.class);
      query.setParameter("text", text);
      return query.getSingleResult();
   }

   public OrderType findById(BigDecimal id) {
      return em.find(OrderType.class, id);
   }

}
