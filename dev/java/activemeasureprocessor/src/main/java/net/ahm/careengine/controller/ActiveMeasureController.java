package net.ahm.careengine.controller;

import java.io.InterruptedIOException;
import java.util.concurrent.atomic.AtomicInteger;

import net.ahm.careengine.common.EngineType;
import net.ahm.careengine.handler.ConditionStateChangeNotificationHandler;
import net.ahm.careengine.message.CareEngineMessage;
import net.ahm.careengine.message.CareEngineMessageIF;
import net.ahm.careengine.util.CacheManagerImpl;

import org.apache.log4j.Logger;

public class ActiveMeasureController implements Runnable {
	private static Logger log = Logger.getLogger(ActiveMeasureController.class);
	private MessageServiceIF msgService;
	private MessageProcessorPool processor; 
	private ConditionStateChangeNotificationHandler chnHandler;

    // Do not pull more messages off the inbound queue if there are no free
    // worker threads. This way, another instance of CE that may be free can
    // have a chance to pull the message and process it while all of OUR
    // worker threads are busy.
    //
    // The counter is declared in CareEngineController so it can be installed
    // in both MessageProcessorPool and AQMessenger, but is inc/decremented in 
    // MessageProcessorPool.Worker.run().
	private AtomicInteger numFreeWorkers;
		
	public void run() {
		init();
		executeService();
	}
	
	private void init() {
        //Initializing data cache
        CacheManagerImpl.getInstance();
		PostProcessorCallbackIF callback = new PostProcessorCallbackIF() {
			public void doPostProcessing(CareEngineMessageIF messageWrapper, Object output) {
				msgService.doPostProcessing(messageWrapper, output);
			}

			public void handleError(CareEngineMessageIF messageWrapper, Throwable t) {
				msgService.handleException(messageWrapper, t);
			}
		};
		processor.setCallback(callback);
		processor.init();
	}
	
	private void setUpWorkBusyFlags() {
		numFreeWorkers = new AtomicInteger(this.processor.getNumWorkerThreads());
		processor.setWorkerBusyFlagReference(numFreeWorkers);
		msgService.setWorkerBusyFlagReference(numFreeWorkers);
	}
	
	private void executeService() {
		setUpWorkBusyFlags();
		CareEngineMessageIF messageWrapper = null;
		long start = 0, elapsed = 0;
		try {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				messageWrapper = null;
				start = System.currentTimeMillis();
				log.debug("calling method : msgService.getCareEngineMessage(EngineType.ActiveMeasureEngine);");
				messageWrapper = msgService.getCareEngineMessage(EngineType.ActiveMeasureEngine);
				if (messageWrapper == null) {
					continue;
				}
				processor.processMessage(messageWrapper);
				elapsed = System.currentTimeMillis() - start;
				
				logProcessingTimeElapsed(messageWrapper, elapsed);
				
			} catch (Exception e) {
				log.error("Exception", e);
				if (messageWrapper != null)
					msgService.handleException(messageWrapper, e);
				
				if (e instanceof InterruptedIOException) {
					throw (InterruptedIOException)e;
				}
				if (e instanceof InterruptedException) {
					throw (InterruptedException)e;
				} 
			}	
		}
		} catch (InterruptedIOException ie) {
			log.error("CareEngineController.executeService interrupted exception.", ie);
		}	catch (InterruptedException ie) {
			log.error("CareEngineController.executeService interrupted exception.", ie);
		} catch (Error e) {
			log.error("Error", e);
			if (messageWrapper != null)
				msgService.handleException(messageWrapper, e);
		} finally {
			processor.shutdown();
			msgService.shutdown();
			chnHandler.shutdown();
		}
	}

	/**
	 * @param messageWrapper
	 * @param elapsed
	 */
	public void logProcessingTimeElapsed(CareEngineMessageIF messageWrapper, long elapsed) {
		switch (messageWrapper.getCareEngineMessageCategory())
		{
		case mhs:
		{
			CareEngineMessage cemsg = (CareEngineMessage) messageWrapper.getPayload();
			if (cemsg!= null && cemsg.getMemberIds() != null && cemsg.getMemberIds().length > 0) {
				log.info(cemsg.getMemberIds()[0] + "," + elapsed);
			}
			break;
		}
		case careplan:
		{
			log.info("," + elapsed);
			break;
		}
		}
	}
	
	

	public MessageServiceIF getMsgService() {
		return msgService;
	}

	public void setMsgService(MessageServiceIF msgService) {
		this.msgService = msgService;
	}


	public MessageProcessorPool getProcessor() {
		return processor;
	}


	public void setProcessor(MessageProcessorPool processor) {
		this.processor = processor;
	}

	public void setChnHandler(ConditionStateChangeNotificationHandler chnHandler) {
		this.chnHandler = chnHandler;
	}	
}
