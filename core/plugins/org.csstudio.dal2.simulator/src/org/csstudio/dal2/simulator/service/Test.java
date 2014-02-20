package org.csstudio.dal2.simulator.service;

import org.csstudio.dal2.dv.ListenerType;
import org.csstudio.dal2.dv.PvAddress;
import org.csstudio.dal2.dv.Type;
import org.csstudio.dal2.service.DalException;
import org.csstudio.dal2.service.cs.CsPvData;
import org.csstudio.dal2.service.cs.ICsPvAccess;
import org.csstudio.dal2.service.cs.ICsPvListener;
import org.csstudio.dal2.service.cs.ICsResponseListener;

public class Test {

	public static void main(String[] args) throws DalException, InterruptedException {
		SimulatorPvAccessFactory factory = new SimulatorPvAccessFactory();
		
		PvAddress address = PvAddress.getValue("local://abc RND:1:1000:10");
		ICsPvAccess<Integer> access = factory.createPVAccess(address, Type.LONG);
		
		access.getValue(new ICsResponseListener<CsPvData<Integer>>() {
			@Override
			public void onSuccess(CsPvData<Integer> response) {
				System.out.println(response.getValue());
			}
			@Override
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
			}
		});
		
		Thread.sleep(100);
		
		access.getValue(new ICsResponseListener<CsPvData<Integer>>() {
			@Override
			public void onSuccess(CsPvData<Integer> response) {
				System.out.println(response.getValue());
			}
			@Override
			public void onFailure(Throwable throwable) {
				throwable.printStackTrace();
			}
		});
		
		Thread.sleep(2000);

		access.initMonitor(new ICsPvListener<Integer>() {
			
			@Override
			public void valueChanged(CsPvData<Integer> value) {
				System.out.println(value);
			}
			
			@Override
			public ListenerType getType() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void errorReceived(String message) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void disconnected(String pvName) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void connected(String pvName, Type<?> nativeType) {
				// TODO Auto-generated method stub
				
			}
		});
	}

}
