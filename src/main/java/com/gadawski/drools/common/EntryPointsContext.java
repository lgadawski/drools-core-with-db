package com.gadawski.drools.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.WorkingMemoryEntryPoint;

/**
 * @author l.gadawski@gmail.com
 * 
 */
public class EntryPointsContext {
    /**
     * 
     */
    private static EntryPointsContext INSTANCE = null;
    /**
     * 
     */
    private Map<String, WorkingMemoryEntryPoint> m_entryPoints = new ConcurrentHashMap<String, WorkingMemoryEntryPoint>();

    /**
     * 
     */
    private EntryPointsContext() {

    }

    /**
     * @return
     * 
     */
    public static synchronized EntryPointsContext getInstance() {
        if (INSTANCE == null) {
            synchronized (EntryPointsContext.class) {
                if (INSTANCE == null) {
                    INSTANCE = new EntryPointsContext();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * @param entryPoints
     */
    public void setUp(Map<String, WorkingMemoryEntryPoint> entryPoints) {
        this.m_entryPoints = entryPoints;
    }

    /**
     * @param name
     * @return
     */
    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String name) {
        WorkingMemoryEntryPoint wmEntryPoint = this.m_entryPoints.get(name);
        return wmEntryPoint;
    }
}
