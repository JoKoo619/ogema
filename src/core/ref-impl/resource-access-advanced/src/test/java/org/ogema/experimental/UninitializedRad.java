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
package org.ogema.experimental;

import org.ogema.core.model.Resource;
import org.ogema.core.model.units.PowerResource;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.model.connections.ThermalConnection;
import org.ogema.model.devices.generators.HeatPump;
import org.ogema.model.sensors.ThermalPowerSensor;

public class UninitializedRad extends ResourcePattern<HeatPump> {

	public ThermalConnection conn = model.thermalConnection();
	public ThermalPowerSensor sensor = conn.powerSensor();
	public PowerResource power;

	public UninitializedRad(Resource match) {
		super(match);
	}

}
