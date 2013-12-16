package org.remotercp.domain.authorization.service;

import org.eclipse.ecf.core.identity.ID;

public interface IAuthorizationService {

	public boolean isAdmin(ID userID);
}
