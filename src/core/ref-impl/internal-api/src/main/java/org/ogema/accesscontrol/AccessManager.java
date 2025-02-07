/**
 * This file is part of OGEMA.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 */
package org.ogema.accesscontrol;

import static java.lang.System.getProperty;
import java.util.List;
import java.util.Map;

import org.ogema.core.application.AppID;
import org.ogema.core.security.AppPermission;
import org.osgi.service.useradmin.User;

public interface AccessManager {

	/**
	 * An user is created and and added to the user management. The user could represent an App on any machine or a
	 * natural person which has to be authenticated on web interface. This property is given by the value of
	 * {@link isNatural}.
	 * 
	 * The added user doesn't be decorated with any app or resource permissions as default at the creation time. They
	 * could be granted later on the administration interface.
	 * 
	 * @param userName
	 *            name of the entity which is added to the system as user.
	 * @param isNatural
	 *            indicates if the user is a natural one or it represent a machine
	 * @return true if a new user is created or false if the user is already exists.
	 */
	public boolean createUser(String userName, boolean isNatural);

	/**
	 * Removes the user from the user management.
	 * 
	 * @param userName
	 *            name of the user to be removed.
	 */
	public void removeUser(String userName);

	/**
	 * Get the user object from the user management representing the specified user name.
	 * 
	 * @param userName
	 *            Name of the user
	 * @return The reference to the user object registered with the given name. null if no such user name is known by
	 *         the user management.
	 */
	public User getUser(String userName);

	/**
	 * Get all natural user registered to the AccessManager. Natural users are entities that can be logged in the system
	 * and access the web or rest interface.
	 * 
	 * @return List of name strings of the users registered in the system.
	 */
	public List<String> getAllUsers();

	/**
	 * Extend the authorization of the given user by the rights to access the web resources of the app specified by its
	 * AppPreperties.
	 * 
	 * @param user
	 *            Name of the user it's rights are extended.
	 * @param props
	 *            A set of properties related to the application that should be granted to access to.
	 * 
	 */
	public void addPermission(String user, AppPermissionFilter props);

	/**
	 * Extend the authorization of the given user by the rights to access the web resources of apps specified each by an
	 * entry of the list of AppPermissionFilter.
	 * 
	 * @param user
	 *            Name of the user it's rights are extended.
	 * @param props
	 *            A list of AppPermissionFilter where each of them contains a set of properties related to an
	 *            application that should be granted to access to.
	 * 
	 */
	public void addPermission(String user, List<AppPermissionFilter> props);

	/**
	 * Remove the authorization of the given user to access resources of the given app.
	 * 
	 * @param user
	 *            Name of the user it's access rights are changed.
	 * @param properties
	 *            The applications properties it's resources will be no longer accessible for the user.
	 */
	public void removePermission(String user, AppPermissionFilter properties);

	/**
	 * Change the persistently stored password information of the natural user.
	 * 
	 * @param user
	 *            Users name.
	 * @param newPassword
	 *            The new password.
	 */
	public void setNewPassword(String user, String newPassword);

	/**
	 * Change the persistently stored information of any user registered to the AccessManager.
	 * 
	 * @param user
	 *            Users name.
	 * @param credential
	 *            The name string of the credential its value is to be changed.
	 * @param value
	 *            The value of the credential to be set.
	 */
	public void setCredential(String user, String credential, String value);

	/**
	 * Get a list of all applications their registered resources are accessible partially or entirely by the given user.
	 * 
	 * @param user
	 *            Name of the user.
	 * @return List of AppID objects of the permitted apps
	 */
	public List<AppID> getAppsPermitted(String user);

	/**
	 * Check if the user has permission to access (web)resources of the application specified by its given AppID
	 * reference. This method is called by WebResourceManager during security handling.
	 * 
	 * @param user
	 *            The user object.
	 * @param app
	 *            application id reference.
	 * 
	 * @return true if the user is permitted to access the resources registered by the application, false otherwise.
	 */
	public boolean isAppPermitted(User user, AppID app);

	/**
	 * Check if the user has permission to access (web)resources of the application specified by its given AppID
	 * reference.
	 * 
	 * @param user
	 *            Name of the user.
	 * @param app
	 *            application id reference.
	 * 
	 * @return true if the user is permitted to access the resources registered by the application, false otherwise.
	 */
	boolean isAppPermitted(String user, AppID app);

	/**
	 * Check if the user has permission to access (web)resources of the application specified by its given bundle
	 * reference.
	 * 
	 * @param user
	 *            Name of the user.
	 * @param app
	 *            bundle reference.
	 * 
	 * @return true if the user is permitted to access the resources registered by the application that corresponds to
	 *         the bundle reference, false otherwise.
	 */
	// public boolean isAppPermitted(String user, Bundle bundle);

	/**
	 * Check if the specified user is permitted to log into the system with the specified password.
	 * 
	 * @param remoteUser
	 *            name of the user.
	 * @param remotePasswd
	 *            password given by the user.
	 * @param isNatural
	 *            the user is a natural user (false for machine user)
	 * @return true if the user authentication succeeds false otherwise.
	 */
	public boolean authenticate(String remoteUser, String remotePasswd, boolean isNatural);

	/**
	 * Register a new installed app to the user management sub system. This is necessary to set user as permitted to
	 * access the apps resources.
	 * 
	 * @param id
	 *            application id reference to be registered.
	 */
	public void registerApp(AppID id);

	/**
	 * Remove an uninstalled app from the user management sub system.
	 * 
	 * @param id
	 *            application id reference to be unregistered.
	 */
	public void unregisterApp(AppID id);

	/**
	 * Check if the specified user has the specified role. If the roleName equals the application id string, this method
	 * behave the same as isAppPermitted. roleName could alternatively be the name of a ogema resource where the access
	 * right of the user to this resource is checked.
	 * 
	 * @param userName
	 *            name String of the user.
	 * @param roleName
	 *            role name describing the the protected resource.
	 * @return true if the user is permitted to access the resource specified with the role name, false otherwise.
	 */
	// public boolean checkPermission(String userName, String roleName);

	/**
	 * Get the UserRightsProxy object linked to the given user.
	 * 
	 * @param usr
	 *            the users name
	 * @return the UserRightsProxy object
	 */
	public UserRightsProxy getUrp(String usr);

	/**
	 * Get an instance of AppPermission that holds the current permission granted to the specified user. This object can
	 * be used to manipulate the permission configuration of the user.
	 * 
	 * @param user
	 *            The name of the user
	 * @return the AppPermission object
	 */
	public AppPermission getPolicies(String user);

	/**
	 * Set a property that is registered by the user management. This can be read with the method {@link getProperty}.
	 * User properties are public. Unlike the user credentials there is no need of any permissions to read them.
	 * 
	 * @param user
	 *            Name of the user
	 * @param propName
	 *            Name of the property
	 * @param propValue
	 *            Value of the property
	 */
	public void setProperty(String user, String propName, String propValue);

	/**
	 * Get the value of a property set for the specified user
	 * 
	 * @param user
	 *            Name of the user
	 * @param propName
	 *            Name of the property
	 * @return Value string of the property
	 */
	public String getProperty(String user, String propName);

	/**
	 * Check if the specified user represents a natural person or a machine.
	 * 
	 * @param user
	 *            Name of the user
	 * @return true if the user is a natural person, false otherwise
	 */
	public boolean isNatural(String user);

	/**
	 * Checks if the user has unrestricted access to the web interfaces of all apps.
	 *
	 * @param user
	 *            the user name.
	 * @return true, if the user is permitted to access all apps, or false, otherwise.
	 */
	public boolean isAllAppsPermitted(String user);

	/**
	 * Checks if the user has access to the web interfaces of any app.
	 *
	 * @param user
	 *            the user name.
	 * @return true, if the user is not permitted to access any app or false, otherwise.
	 */
	public boolean isNoAppPermitted(String user);
}
