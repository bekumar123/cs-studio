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

import org.csstudio.domain.desy.epics.types.EpicsEnum;
import org.csstudio.domain.desy.typesupport.TypeSupportException;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 15.12.2010
 */
public class EnumArchiveTypeConversionSupport extends ArchiveTypeConversionSupport<EpicsEnum> {

    /**
     * Constructor.
     */
    EnumArchiveTypeConversionSupport() {
        super(EpicsEnum.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public EpicsEnum convertFromDouble(@Nonnull final Double value) throws TypeSupportException {
        throw new TypeSupportException("Enum shall not be converted from Double.", null);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String convertToArchiveString(@Nonnull final EpicsEnum value) throws TypeSupportException {
        return value.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public EpicsEnum convertFromArchiveString(@Nonnull final String value) throws TypeSupportException {
        if ("".equals(value)) {
            throw new TypeSupportException("EpicsEnum '" + value + "' is empty string.", null);
        }
        try {
            return EpicsEnum.createFromString(value);
        } catch (final NumberFormatException e) {
            throw new TypeSupportException(value + " cannot be parsed into " + EpicsEnum.class.getSimpleName(), e);
        } catch (final IllegalArgumentException e) {
            throw new TypeSupportException(value + " cannot be parsed into " + EpicsEnum.class.getSimpleName(), e);
        }
    }
}
