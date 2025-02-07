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
package org.ogema.resourcemanager.impl.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.channelmanager.measurements.Value;
import org.ogema.core.model.Resource;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.resourcemanager.ResourceValueListener;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

/**
 *
 * @author jlapp
 */
@ExamReactorStrategy(PerClass.class)
public class ValueListenerScheduleTest extends OsgiTestBase {
    
	private final Map<Class<? extends Resource>, Schedule> _schedules = new HashMap<>();
    
  	/**
	 * Returns the test m_floatSchedule used in this test. Creates it, if it does not exist, yet.
	 *
	 * @return
	 */
	Schedule getSchedule(Class<? extends Resource> primitiveClass) {
		if (_schedules.containsKey(primitiveClass)) {
			return _schedules.get(primitiveClass);
		}
		final Resource simple = resMan.createResource(newResourceName(), primitiveClass);
		final Schedule schedule = simple.addDecorator("schedule", Schedule.class);
		_schedules.put(primitiveClass, schedule);
		return schedule;
	}


    @Test
	public void addValueCausesCallback() throws InterruptedException {
		final CountDownLatch callbackCount = new CountDownLatch(1);
		final ResourceValueListener<Schedule> listener = new ResourceValueListener<Schedule>() {

			@Override
			public void resourceChanged(Schedule resource) {
				callbackCount.countDown();
			}
		};

		final Schedule schedule = getSchedule(FloatResource.class);
		schedule.activate(true);
		schedule.addValueListener(listener, false);

		schedule.addValue(26, new FloatValue(4.3f));
		assertTrue("did not receive update callback", callbackCount.await(5, TimeUnit.SECONDS));
	}
    
    @Test
	public void addValuesCausesCallback() throws InterruptedException {
		final CountDownLatch callbackCount = new CountDownLatch(1);
		final ResourceValueListener<Schedule> listener = new ResourceValueListener<Schedule>() {

			@Override
			public void resourceChanged(Schedule resource) {
				callbackCount.countDown();
			}
		};

		final Schedule schedule = getSchedule(FloatResource.class);
		schedule.activate(true);
		schedule.addValueListener(listener, false);

		schedule.addValues(Collections.singleton(new SampledValue(new FloatValue(47.11f), 42, Quality.GOOD)));
		assertTrue("did not receive update callback", callbackCount.await(5, TimeUnit.SECONDS));
	}
    
    @Test
	public void deleteValuesCausesCallback() throws InterruptedException {
		final CountDownLatch callbackCount = new CountDownLatch(1);
		final ResourceValueListener<Schedule> listener = new ResourceValueListener<Schedule>() {

			@Override
			public void resourceChanged(Schedule resource) {
				callbackCount.countDown();
			}
		};

		final Schedule schedule = getSchedule(FloatResource.class);
        schedule.addValues(Collections.singleton(new SampledValue(new FloatValue(47.11f), 42, Quality.GOOD)));
		schedule.activate(true);
        
		schedule.addValueListener(listener, false);
		schedule.deleteValues(40, 45);
		assertTrue("did not receive update callback", callbackCount.await(5, TimeUnit.SECONDS));
	}
    
    @Test
	public void replaceValuesCausesCallback() throws InterruptedException {
		final CountDownLatch callbackCount = new CountDownLatch(1);
		final ResourceValueListener<Schedule> listener = new ResourceValueListener<Schedule>() {

			@Override
			public void resourceChanged(Schedule resource) {
				callbackCount.countDown();
			}
		};

		final Schedule schedule = getSchedule(FloatResource.class);
        schedule.addValues(Collections.singleton(new SampledValue(new FloatValue(47.11f), 42, Quality.GOOD)));
		schedule.activate(true);
        
		schedule.addValueListener(listener, false);
		schedule.replaceValues(40, 45, Collections.singleton(new SampledValue(new FloatValue(47.11f), 42, Quality.GOOD)));
		assertTrue("did not receive update callback", callbackCount.await(5, TimeUnit.SECONDS));
	}
    
    @Test
	public void replaceValuesFixedStepCausesCallback() throws InterruptedException {
		final CountDownLatch callbackCount = new CountDownLatch(1);
		final ResourceValueListener<Schedule> listener = new ResourceValueListener<Schedule>() {

			@Override
			public void resourceChanged(Schedule resource) {
				callbackCount.countDown();
			}
		};

		final Schedule schedule = getSchedule(FloatResource.class);
        schedule.addValues(Collections.singleton(new SampledValue(new FloatValue(47.11f), 42, Quality.GOOD)));
		schedule.activate(true);
        
		schedule.addValueListener(listener, false);
		schedule.replaceValuesFixedStep(40, Arrays.asList((Value)new FloatValue(1), new FloatValue(2)), 10);
		assertTrue("did not receive update callback", callbackCount.await(5, TimeUnit.SECONDS));
	}
    
}
