/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.a2i.speak.cluster;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author atrimble
 */
public interface Member {
    
    public AtomicInteger getSequence();

    public MemberStatus getStatus();

    public void setStatus(MemberStatus status);

    public Map<String, String> getTags();

    public String getIp();

    public void setIp(String ip);

    public int getDataPort();

    public void setPort(int dataPort);

    public int getCommandPort();

    public void setCommandPort(int commandPort);
}
