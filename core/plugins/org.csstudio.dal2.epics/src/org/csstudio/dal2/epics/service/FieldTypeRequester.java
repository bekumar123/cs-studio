package org.csstudio.dal2.epics.service;

import gov.aps.jca.Context;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.event.ConnectionEvent;

import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.DalException;
import org.csstudio.dal2.service.cs.ICsResponseListener;

class FieldTypeRequester extends AbstractChannelOperator {

	private ICsResponseListener<Type<?>> _callback;

	public FieldTypeRequester(Context context, PvAddress pv,
			ICsResponseListener<Type<?>> callback) throws DalException {
		super(context, pv);
		_callback = callback;
	}

	@Override
	protected void onFirstConnect(ConnectionEvent ev) {
		try {
			DBRType dbrType = getChannel().getFieldType();
			Type<?> type = TypeMapper.getType(dbrType);
			_callback.onSuccess(type);
		} catch (Exception e) {
			_callback.onFailure(e);
		} finally {
			dispose();
		}
	}
}