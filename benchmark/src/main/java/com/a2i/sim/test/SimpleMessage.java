/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.a2i.sim.test;

import com.a2i.sim.core.Message;

/**
 *
 * @author atrimble
 */
@Message
public class SimpleMessage {
    private String string;

    private long sequence;

    private String hugeString = 
            "lkdsjfljsdflkdsjflksdjflskajflfjsdlkfjsldfjksldfkjsadlfkjlsadjfsldfjslafjsdlfkjslfjsdfljsdflskdjfslkjfslakdjfsdlfkjsldfjksdlfjsj" +
            "lkdsjfljsdflkdsjflksdjflskajflfjsdlkfjsldfjksldfkjsadlfkjlsadjfsldfjslafjsdlfkjslfjsdfljsdflskdjfslkjfslakdjfsdlfkjsldfjksdlfjsj" +
            "lkdsjfljsdflkdsjflksdjflskajflfjsdlkfjsldfjksldfkjsadlfkjlsadjfsldfjslafjsdlfkjslfjsdfljsdflskdjfslkjfslakdjfsdlfkjsldfjksdlfjsj" +
            "lkdsjfljsdflkdsjflksdjflskajflfjsdlkfjsldfjksldfkjsadlfkjlsadjfsldfjslafjsdlfkjslfjsdfljsdflskdjfslkjfslakdjfsdlfkjsldfjksdlfjsl";

    public SimpleMessage() {

    }

    public SimpleMessage(String string, long sequence) {
        this.string = string;
        this.sequence = sequence;
    }

    /**
     * @return the string
     */
    public String getString() {
        return string;
    }

    /**
     * @param string the string to set
     */
    public void setString(String string) {
        this.string = string;
    }

    /**
     * @return the sequence
     */
    public long getSequence() {
        return sequence;
    }

    /**
     * @param sequence the sequence to set
     */
    public void setSequence(long sequence) {
        this.sequence = sequence;
    }
}