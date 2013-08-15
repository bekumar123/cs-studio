/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.archive.common.service.util;

import javax.annotation.Nonnull;

import org.csstudio.domain.desy.typesupport.TypeSupportException;

/**
 * Type conversions for {@link Integer}.
 *
 * @author bknerr
 * @since 10.12.2010
 */
public class IntegerArchiveTypeConversionSupport extends AbstractNumberArchiveTypeConversionSupport<Integer> {


    /**
     * Constructor.
     * @param type
     */
    IntegerArchiveTypeConversionSupport() {
        super(Integer.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Integer convertFromArchiveString(@Nonnull final String value) throws TypeSupportException {

        try {
            int index=value.indexOf("(");
            if(index>0){
                final String subStr=value.substring(index+1, value.indexOf(")"));
                return  Integer.parseInt(subStr,10);
            }
            index=value.indexOf(":");
            if(index>0){
                final String subStr=value.substring(index+1, value.length());
                return  Integer.parseInt(subStr,10);
            }

            return  Integer.parseInt(value,10);
        } catch (final NumberFormatException e) {
            throw new TypeSupportException("Parsing failed.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Integer convertFromDouble(@Nonnull final Double value) throws TypeSupportException {
        return value.intValue();
    }
}
