package com.gadawski.drools.common;

import java.util.HashMap;
import java.util.Map;

import org.drools.common.InternalAgendaGroup;

/**
 * @author l.gadawski@gmail.com
 * 
 */
public class AgendaGroupContext {
    /**
     * 
     */
    private static AgendaGroupContext INSTANCE = null;
    /**
     * 
     */
    private Map<String, InternalAgendaGroup> m_agendaGroups = new HashMap<String, InternalAgendaGroup>();

    /**
     * 
     */
    private AgendaGroupContext() {
    }

    /**
     * @return
     */
    public static AgendaGroupContext getInstance() {
        if (INSTANCE == null) {
            synchronized (AgendaGroupContext.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AgendaGroupContext();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * @param agendaGroup
     * @param name
     */
    public void addAgendaGroup(String name, InternalAgendaGroup agendaGroup) {
        m_agendaGroups.put(name, agendaGroup);
    }

    /**
     * @param agendaGroup
     * @return
     */
    public InternalAgendaGroup get(String agendaGroup) {
        return m_agendaGroups.get(agendaGroup);
    }

}
