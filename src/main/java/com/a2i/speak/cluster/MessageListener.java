/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.a2i.speak.cluster;

/**
 *
 * @author atrimble
 */
public interface MessageListener<T> {
    public void receive(T message);
}
