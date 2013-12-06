package org.csstudio.dal2.epics.service;

import gov.aps.jca.Context;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.GetEvent;
import gov.aps.jca.event.GetListener;

import org.csstudio.dal2.dv.Characteristics;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.DalException;
import org.csstudio.dal2.service.cs.CsPvData;
import org.csstudio.dal2.service.cs.ICsResponseListener;

/**
 * The requester opens a channel to a process variable and receives it's value  
 *
 * @param <T>
 */
class GetValueRequester<T> extends AbstractChannelOperator implements
		GetListener {

	private ICsResponseListener<CsPvData<T>> _callback;
	private Type<T> _type;

	public GetValueRequester(Context context, PvAddress pv, Type<T> type,
			ICsResponseListener<CsPvData<T>> callback)
			throws DalException {
		super(context, pv);

		_callback = callback;
		_type = type;
	}

	@Override
	protected void onFirstConnect(ConnectionEvent ev) {

		DBRType dbrType = TypeMapper.getMapper(_type, getNativeType()).getDBRCtrlType();
		int elementCount = getChannel().getElementCount();

		try {
			getChannel().get(dbrType, elementCount, this);
			getContext().flushIO();
		} catch (Exception e) {
			dispose();
			_callback.onFailure(e);
		}
	}

	@Override
	public void getCompleted(GetEvent ev) {
		try {
			if (!ev.getStatus().isSuccessful()) {
				throw new DalException("Error reading value from channel: "
						+ ev.getStatus());
			} else if (ev.getDBR() == null) {
				throw new DalException(
						"Error reading value from channel: received null");
			} else {
				DBR dbr = ev.getDBR();
				T value = TypeMapper.getMapper(_type, getNativeType()).mapValue(dbr);
				String hostname = getChannel().getHostName();
				Characteristics characteristics = new CharacteristicsService()
						.newCharacteristics(dbr, hostname);
				CsPvData<T> pvData = new CsPvData<T>(value, characteristics, getNativeType());

				dispose();

				_callback.onSuccess(pvData);
			}
		} catch (Exception e) {
			dispose();
			_callback.onFailure(e);
		}
	}
}