/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.domain.desy.types;

import java.io.Serializable;

import javax.annotation.Nonnull;

/**
 * Abstract base class for any kind of immutable triple.
 *
 * Implements {@link java.io.Serializable}, i.e. for any implementation to be serializable any
 * member should be serializable. But that's not forced (cmp to Collections, which are also
 * themselves serializable without ensuring any collection element to be serializable.)
 *
 * @author bknerr
 * @since 12.05.2011

 * @param <A> type of the 1st member
 * @param <B> type of the 2nd member
 *
 */
public class Tuple<A, B> implements Serializable {

    private static final long serialVersionUID = -7291262798373517336L;

    private final A _first;
    private final B _second;

    /**
     * Constructor.
     */
    public Tuple(@Nonnull  final A first,
                            @Nonnull final B second) {
        _first = first;
        _second = second;
    }
    
    @Nonnull
    public A getFirst() {
        return _first;
    }
    
    @Nonnull
    public B getSecond() {
        return _second;
    }
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_first == null) ? 0 : _first.hashCode());
		result = prime * result + ((_second == null) ? 0 : _second.hashCode());
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
		Tuple other = (Tuple) obj;
		if (_first == null) {
			if (other._first != null)
				return false;
		} else if (!_first.equals(other._first))
			return false;
		if (_second == null) {
			if (other._second != null)
				return false;
		} else if (!_second.equals(other._second))
			return false;
		return true;
	}
}
