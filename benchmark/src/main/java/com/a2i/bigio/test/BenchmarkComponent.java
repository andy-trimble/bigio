/*
 * Copyright (c) 2014, Archarithms Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the FreeBSD Project.
 */

package com.a2i.bigio.test;

import com.a2i.bigio.Inject;
import com.a2i.bigio.Parameters;
import com.a2i.bigio.Speaker;
import com.a2i.bigio.core.Envelope;
import com.a2i.bigio.core.MessageListener;
import com.a2i.bigio.core.codec.EnvelopeEncoder;
import com.a2i.bigio.core.codec.GenericEncoder;
import com.a2i.bigio.util.TimeUtil;
import java.io.IOException;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author atrimble
 */
//@Component
public class BenchmarkComponent {

    private static final Logger LOG = LoggerFactory.getLogger(BenchmarkComponent.class);
    
    @Inject
    private Speaker speaker;

    private boolean running = true;
    private long time;
    private long messageCount = 0;
    private long sendCount = 0;

    private int messagesPerSecond = 100000;

    Thread senderThread = new Thread() {
        @Override
        public void run() {
            try {
                Thread.sleep(1000l);
            } catch(Exception ex) {

            }

            time = System.currentTimeMillis();
            while(running) {
                try {
                    for(int i = 0; i < messagesPerSecond; ++i) {
                        speaker.send("HelloWorld", new SimpleMessage("This message should be en/decoded", ++sendCount, System.currentTimeMillis()));
                    }
                    Thread.sleep(1000l);
                } catch(Exception ex) {
                    LOG.debug("Error", ex);
                }
            }
        }
    };

    Thread localThread = new Thread() {
        @Override
        public void run() {
            time = System.currentTimeMillis();
            while(running) {
                try {
//                    Thread.sleep(100l);
                    speaker.send("HelloWorldLocal", new SimpleMessage("This message should be en/decoded", ++sendCount, System.currentTimeMillis()));
                } catch(Exception ex) {
                    LOG.debug("Error", ex);
                }
            }
        }
    };

    public BenchmarkComponent() {
        SimpleMessage m = new SimpleMessage("This message should be en/decoded", 0, System.currentTimeMillis());
        try {
            byte[] payload = GenericEncoder.encode(m);
            Envelope envelope = new Envelope();
            envelope.setDecoded(false);
            envelope.setExecuteTime(0);
            envelope.setMillisecondsSinceMidnight(TimeUtil.getMillisecondsSinceMidnight());
            envelope.setSenderKey("192.168.1.1:55200:55200");
            envelope.setTopic("HelloWorld");
            envelope.setClassName(SimpleMessage.class.getName());
            envelope.setPayload(payload);
            envelope.setDecoded(false);

            byte[] bytes = EnvelopeEncoder.encode(envelope);
            LOG.info("Typical message size: " + bytes.length);
            LOG.info("Typical payload size: " + payload.length);
            LOG.info("Typical header size: " + (bytes.length - payload.length));
        } catch (IOException ex) {
            LOG.error("IOException", ex);
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                running = false;
                
                try {
                    senderThread.join();
                    localThread.join();
                } catch(InterruptedException ex) {
                    ex.printStackTrace();
                }

                time = System.currentTimeMillis() - time;

                long count = messageCount;
                long send = sendCount;

                long seconds = time / 1000;
                long bandwidth = count / seconds;
                long sendBandwidth = send / seconds;

                LOG.info("Received " + count + " messages in " + seconds + 
                        " seconds for a bandwidth of " + bandwidth + " m/s");

                LOG.info("Sent " + send + " messages in " + seconds + 
                        " seconds for a bandwidth of " + sendBandwidth + " m/s");
            }
        });
    }

    @PostConstruct
    public void go() {
        String role = Parameters.INSTANCE.getProperty("com.a2i.benchmark.role", "local");

        if(role.equals("producer")) {
            LOG.info("Running as a producer");
            senderThread.start();
        } else if(role.equals("consumer")) {
            LOG.info("Running as a consumer");
            speaker.addListener("HelloWorld", new MessageListener<SimpleMessage>() {
                long lastReceived = 0;
                @Override
                public void receive(SimpleMessage message) {
                    if(messageCount == 0) {
                        time = System.currentTimeMillis();
                    } else {
                        if(message.getSequence() - lastReceived > 1) {
                            LOG.info("Dropped " + (message.getSequence() - lastReceived) + " messages");
                        }
                        lastReceived = message.getSequence();
                    }
                    ++messageCount;
                }
            });
        } else {
            LOG.info("Running in VM only");
            speaker.addListener("HelloWorldLocal", new MessageListener<SimpleMessage>() {
                @Override
                public void receive(SimpleMessage message) {
                    ++messageCount;
                }
            });

            localThread.start();
        }
    }
}