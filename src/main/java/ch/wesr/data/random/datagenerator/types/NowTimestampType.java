/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.wesr.data.random.datagenerator.types;

/**
 *
 * @author andrewserff
 */
public class NowTimestampType extends NowBaseType {
    public static final String TYPE_NAME = "nowTimestamp";
    public static final String TYPE_DISPLAY_NAME = "Now Timestamp";

    @Override
    public Long getNextRandomValue() {
        return getNextDate().getTime();
    }
            
    @Override
    public String getName() {
        return TYPE_NAME;
    }
}
