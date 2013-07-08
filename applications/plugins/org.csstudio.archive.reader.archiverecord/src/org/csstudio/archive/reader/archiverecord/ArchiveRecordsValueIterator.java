package org.csstudio.archive.reader.archiverecord;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.archive.reader.ValueIterator;
import org.csstudio.data.values.ISeverity;
import org.epics.util.text.NumberFormats;
import org.epics.util.time.Timestamp;
import org.epics.vtype.Display;
import org.epics.vtype.Time;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;

public class ArchiveRecordsValueIterator implements ValueIterator {

	private final String _name;
	private final Timestamp _start;
	private final Timestamp _end;

	private List<VType> _result = new ArrayList<VType>();

	public ArchiveRecordsValueIterator(String name, Timestamp start,
			Timestamp end) {
				_name = name;
				_start = start;
				_end = end;
	}
	@Override
	public boolean hasNext() {
		if (_result.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public VType next() throws Exception {
		if (_result.size() > 0) {
			//TODO (jhatje): implement vType
			//return null;
			return _result.remove(0);
		}
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	public void getData() {
		int error = 0;
        	final ArchiveRecord ar = new ArchiveRecord(_name);
    		int dim = 0;
                try {
					dim = ar.getDimension();
                if (dim <= 0) {
                    error = -1;
                    dim = 0;
                }
                if (dim > 0) {
                    ar.getAllFromCA();
                }
                } catch (Exception e) {
                	// TODO Auto-generated catch block
                	e.printStackTrace();
                }
			final int count = 1; // do not use WF answerClass.getCount();
			final int num_samples = dim;
//			final IValue samples[]= new IValue[num_samples];
			final Display meta = ValueFactory.newDisplay(new Double(-100), -new Double(-90), -new Double(-80), "", NumberFormats.toStringFormat(), new Double(80), new Double(90), new Double(100), -new Double(-100), new Double(100));
			for (int si=0; si<num_samples; si++) {
				final long secs = ar.getTime()[si];
				final long nano = ar.getNsec()[si];
				final Time time =   ValueFactory.newTime(Timestamp.of(secs, (int)nano));

				final ISeverity sevClass= new SeverityImpl("",true,true);
				final double values[] = new double[count]; // count=1
			    for (int vi=0; vi<count; ++vi) {
                    values[vi] = ar.getVal()[si];
                }
			   
//				samples[si] = ValueFactory.createDoubleValue(time, sevClass,"", meta,IValue.Quality.Original, values);
				_result.add(ValueFactory.newVDoubleArray(values, ValueFactory.alarmNone(), time, meta));
			}


	}

}
