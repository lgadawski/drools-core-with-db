package com.gadawski.db;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.common.AgendaItem;
import org.drools.common.InternalAgendaGroup;
import org.drools.common.InternalRuleBase;
import org.drools.spi.Activation;
import org.drools.spi.PropagationContext;

public class DbAgendaItemGroup implements InternalAgendaGroup {
    /**
     * 
     */
    private final IAgendaItemManager m_dbAgendaItemManager = DbAgendaItemManager
            .getInstance();
    /**
     * 
     */
    private final String m_name;
    /**
     * 
     */
    private boolean m_active;
    /**
     * 
     */
    private PropagationContext m_autoFocusActivator;

    /**
     * @param name
     * @param ruleBase
     */
    public DbAgendaItemGroup(final String name, final InternalRuleBase ruleBase) {
        this.m_name = name;
    }

    @Override
    public void readExternal(final ObjectInput arg) throws IOException,
            ClassNotFoundException {
    }

    @Override
    public void writeExternal(final ObjectOutput arg) throws IOException {
    }

    @Override
    public Activation[] getActivations() {
        return m_dbAgendaItemManager.getActications();
    }

    @Override
    public int size() {
        return m_dbAgendaItemManager.getNumberOfAgendaItems();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Activation getNext() {
        return m_dbAgendaItemManager.getNextAgendaItem();
    }

    @Override
    public void add(final org.drools.spi.Activation activation) {
        m_dbAgendaItemManager.saveAgendaItem((AgendaItem) activation);
    }

    @Override
    public void remove(final AgendaItem agendaItem) {
        m_dbAgendaItemManager.removeAgendaItem(agendaItem);
    }

    @Override
    public boolean isActive() {
        return m_active;
    }

    @Override
    public void setActive(final boolean activate) {
        this.m_active = activate;
    }

    @Override
    public void clear() {
        m_dbAgendaItemManager.clearAgenda();
    }

    @Override
    public String getName() {
        return this.m_name;
    }

    @Override
    public Activation[] getAndClear() {
        Activation[] activations = m_dbAgendaItemManager.getActications();
        m_dbAgendaItemManager.clearAgenda();
        return activations;
    }

    @Override
    public void setFocus() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAutoFocusActivator(final PropagationContext ctx) {
        this.m_autoFocusActivator = ctx;
    }

    @Override
    public PropagationContext getAutoFocusActivator() {
        return m_autoFocusActivator;
    }
}
