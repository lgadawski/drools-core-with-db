/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.common;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.gadawski.drools.common.AgendaGroupContext;
import com.gadawski.drools.config.MyAppConfig;
import com.gadawski.drools.db.DbAgendaItemGroup;


public class PriorityQueueAgendaGroupFactory implements AgendaGroupFactory, Externalizable {
    private static final AgendaGroupFactory INSTANCE = new PriorityQueueAgendaGroupFactory();

    private AgendaGroupContext m_agendaGroup = AgendaGroupContext.getInstance();
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public static AgendaGroupFactory getInstance() {
        return INSTANCE;
    }

    public InternalAgendaGroup createAgendaGroup(String name, InternalRuleBase ruleBase) {
//        return new SimpleAgendaGroup(name, ruleBase);
        InternalAgendaGroup agendaGroup = null;
        if (MyAppConfig.USE_DB) {
            agendaGroup = new DbAgendaItemGroup(name, ruleBase);
        } else {
            agendaGroup = new BinaryHeapQueueAgendaGroup(name, ruleBase);
        }
        m_agendaGroup .addAgendaGroup(name, agendaGroup);
        return agendaGroup;
    }
}
