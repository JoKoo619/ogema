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
package org.ogema.app.coolcontrol;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.ResourceValueListener;
import org.ogema.model.devices.whitegoods.CoolingDevice;

/**
 * Two-point controller for a given cooling space. A cooling space described by a {@link CoolspacePattern} 
 * is passed to this controller, which then tries to keep the temperatures within the set limits.
 *
 * @author Timo Fischer, Fraunhofer IWES
 */
public class CoolspaceController {

	private final OgemaLogger m_logger;
	private CoolspacePattern m_device;

	/**
	 * Creates and starts a new controller for a cooling device that was found.
	 *
	 * @param device Completed resource pattern for the cooling device
	 * @param appMan Reference to the application manager (as entry point to
	 * OGEMA)
	 */
	public CoolspaceController(CoolspacePattern device, ApplicationManager appMan) {
		m_logger = appMan.getLogger();
		m_device = device;
		// Add a resource listener to the temperature reading, which is invoked whenever the temperature of the device changes.
		m_device.temperature.addValueListener(m_tempChange);
	}

	/**
	 * Gets the device controlled by this controller.
	 */
	CoolingDevice getDevice() {
		return m_device.model;
	}

	/**
	 * Listener for a temperature change. Called whenever the temperature of the
	 * cooling device changes and possibly changes the on/off settings if the
	 * temperature left the set constraints.
	 */
	private final ResourceValueListener<Resource> m_tempChange = new ResourceValueListener<Resource>() {

		@Override
		public void resourceChanged(Resource resource) {

			// read out current temperature and limits.
			final float T = m_device.temperature.getValue();
			final float min = m_device.m_minTemp.getValue();
			final float max = m_device.m_maxTemp.getValue();
			m_logger.debug(m_device + "::T = " + T);

			if (max < min) { // just a sanity check, in case the limits are non-sensible
				m_logger.warn("Temperature limits set for device " + m_device + " are not sensible: Tmax=" + max
						+ " < Tmin=" + min + ". Will not control device this time");
				return;
			}

			// react if limits are violated
			if (T > max) {
				if (m_device.stateControl.getValue() != true) {
					m_logger.debug("turning device " + m_device + " on");
					m_device.stateControl.setValue(true);
				}
				return;
			}
			if (T < min) {
				if (m_device.stateControl.getValue() != false) {
					m_logger.debug("turning device " + m_device + " off");
					m_device.stateControl.setValue(false);
				}
				return;
			}

			// current control settings are fine for current value: do nothing.
		}
	};

	// Cleanup method in case the controller is removed (e.g. the device pattern was no longer valid): Unregisters the listener on the temperature reading.
	void stopListener() {
		m_device.temperature.removeValueListener(m_tempChange);
	}
}
