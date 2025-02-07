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
package org.ogema.driver.homematic.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.ogema.driver.homematic.Activator;
import org.ogema.driver.homematic.manager.RemoteDevice.InitStates;
import org.ogema.driver.homematic.manager.messages.CmdMessage;
import org.ogema.driver.homematic.manager.messages.Message;
import org.slf4j.Logger;

/**
 * This class handles outgoing messages that need a response.
 * 
 * @author baerthbn
 * 
 */
public class MessageHandler {
	private LocalDevice localDevice;
	public long MIN_WAITING_TIME = 500;
	private volatile List<Long> sentMessageAwaitingResponse = new ArrayList<Long>(); // <Token>
	private volatile Map<String, SendThread> runningThreads = new ConcurrentHashMap<String, SendThread>(); // <Deviceaddress,
	// Thread>
	HashMap<String, CmdMessage> sentCommands = new HashMap<String, CmdMessage>();
	private final Logger logger = org.slf4j.LoggerFactory.getLogger("homematic-driver");

	public MessageHandler(LocalDevice device) {
		this.localDevice = device;
	}

	public void messageReceived(StatusMessage msg) {
		RemoteDevice device = localDevice.getDevices().get(msg.source);
		logger.debug("Received ?-token: " + msg.rtoken);
		if (msg.type == 'E') { // Must be parsed
			// check if a the request message registered.
			CmdMessage cmd;
			String key;
			synchronized (sentCommands) {
				key = msg.source + "" + device.sentMsgNum;
				cmd = sentCommands.get(key);
			}
			logger.debug("Received message assigned to " + key);
			device.parseMsg(msg, cmd);
		}
		else { // is "R"
			logger.debug("Received R-token: " + msg.rtoken);
			if (sentMessageAwaitingResponse.contains(msg.rtoken)) {
				if (runningThreads.containsKey(msg.source) && ((msg.cond & (byte) 0x80) == 0)) {
					SendThread sendThread = runningThreads.get(msg.source);
					sentMessageAwaitingResponse.remove(msg.rtoken);
					logger.debug("sentMessageAwaitingResponse removed " + msg.rtoken);
					sendThread.interrupt();
					logger.debug("Thread has been notified");
				}
			}
		}
	}

	public void sendMessage(Message message) {
		SendThread sendThread = runningThreads.get(message.getDest());
		if (sendThread == null) {
			sendThread = new SendThread(message.getDest());
			runningThreads.put(sendThread.getDest(), sendThread);
			sendThread.start();
		}
		sendThread.addMessage(message);
	}

	public class SendThread extends Thread {

		private static final int HM_SENT_RETRIES = 4;
		private String dest;
		private int tries;

		private volatile Map<Long, Message> unsentMessageQueue; // Messages waiting to be sent

		public SendThread(String dest) {
			this.dest = dest;
			unsentMessageQueue = new ConcurrentHashMap<Long, Message>();
		}

		@Override
		public void run() {
			while (Activator.bundleIsRunning) {
				Message entry = null;
				logger.debug("Try: " + tries);
				synchronized (unsentMessageQueue) {
					try {
						entry = this.unsentMessageQueue.get(getSmallestKey());
						if (entry == null) {
							unsentMessageQueue.wait();
							entry = this.unsentMessageQueue.get(getSmallestKey());
						}
					} catch (InterruptedException e) {
					}
				}
				long token = entry.getToken();
				sentMessageAwaitingResponse.add(token);
				logger.debug("sentMessageAwaitingResponse added " + token);
				// register command message to assign additional info about the request message to the receiver of
				// the response
				if (entry instanceof CmdMessage) {
					int num = entry.refreshMsg_num();
					String key = entry.getDest() + "" + num;
					synchronized (sentCommands) {
						entry.getDevice().sentMsgNum = num;
						((CmdMessage) entry).sentNum = num;
						sentCommands.put(key, (CmdMessage) entry);
					}
					System.out.println("Sent command registered with  key: " + key);
				}

				while (tries < HM_SENT_RETRIES) {
					if (sentMessageAwaitingResponse.contains(token)) {
						localDevice.sendFrame(entry.getFrame());
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							break;
						}
						logger
								.debug(String.format("Response from %s for the message %d took to long ...", dest,
										token));
						tries++;
					}
					else {
						logger.debug("unsentMessageQueue removed " + token);
						break;
					}
				}
				RemoteDevice device = localDevice.getDevices().get(dest);
				if (!sentMessageAwaitingResponse.contains(token) && tries <= HM_SENT_RETRIES) {
					if (device.getInitState() == InitStates.PAIRING) {
						device.setInitState(InitStates.PAIRED);
						logger.info("Device " + dest + " paired");
					}
				}
				else if (device.getInitState() == InitStates.PAIRING) { // here we aren't sure that the device is no
					// longer present. In case of configuration
					// request,
					// the device wouldn't react, if the activation button is not pressed. Removing of devices
					// should be done actively by the user/administrator.
					device.setInitState(InitStates.UNKNOWN);
					localDevice.getDevices().remove(device.getAddress());
					logger.warn("Device " + dest + " removed!");
				}
				this.unsentMessageQueue.remove(token);
				tries = 0;
			}
		}

		public String getDest() {
			return dest;
		}

		public void addMessage(Message message) {
			synchronized (unsentMessageQueue) {
				this.unsentMessageQueue.put(message.getToken(), message);
				unsentMessageQueue.notify();
			}
			logger.debug("unsentMessageQueue added " + message.getToken());
		}

		private long getSmallestKey() {
			Set<Long> keys = this.unsentMessageQueue.keySet();
			long min = 0xffffffffL;
			for (long key : keys) {
				if (key < min)
					min = key;
			}
			return min;
		}
	}
}
