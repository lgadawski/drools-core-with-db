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

package org.drools.reteoo.builder;


import org.drools.common.BaseNode;
import org.drools.common.BetaConstraints;
import org.drools.reteoo.AlphaNode;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.FromNode;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.reteoo.QueryElementNode;
import org.drools.reteoo.QueryTerminalNode;
import org.drools.reteoo.TerminalNode;
import org.drools.rule.From;
import org.drools.rule.GroupElement;
import org.drools.rule.QueryElement;
import org.drools.rule.Rule;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.DataProvider;
import org.drools.spi.ObjectType;

import com.gadawski.drools.reteoo.builder.NodeContext;

import java.io.Serializable;

public class DefaultNodeFactory implements NodeFactory, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * 
     */
    private NodeContext nodes = NodeContext.getInstance();

    public AlphaNode buildAlphaNode( int id, AlphaNodeFieldConstraint constraint, ObjectSource objectSource, BuildContext context ) {
        AlphaNode node = new AlphaNode( id, constraint, objectSource, context );
        nodes.addNode(node);
        return node;
    }

    public TerminalNode buildTerminalNode( int id, LeftTupleSource source, Rule rule, GroupElement subrule, int subruleIndex, BuildContext context ) {
        RuleTerminalNode node = new RuleTerminalNode( id, source, rule, subrule, subruleIndex, context );
        nodes.addNode(node);
        return node;
    }

    public ObjectTypeNode buildObjectTypeNode( int id, EntryPointNode objectSource, ObjectType objectType, BuildContext context ) {
        ObjectTypeNode node = new ObjectTypeNode( id, objectSource, objectType, context );
        nodes.addNode(node);
        return node;
    }

    public JoinNode buildJoinNode( int id, LeftTupleSource leftInput, ObjectSource rightInput, BetaConstraints binder, BuildContext context ) {
        JoinNode node = new JoinNode( id, leftInput, rightInput, binder, context );
        nodes.addNode(node);
        return node;
    }

    public LeftInputAdapterNode buildLeftInputAdapterNode( int id, ObjectSource objectSource, BuildContext context ) {
        LeftInputAdapterNode node = new LeftInputAdapterNode( id, objectSource, context );
        nodes.addNode(node);
        return node;
    }

    public TerminalNode buildQueryTerminalNode(int id, LeftTupleSource source, Rule rule, GroupElement subrule, int subruleIndex, BuildContext context) {
        QueryTerminalNode node = new QueryTerminalNode( id, source, rule, subrule, subruleIndex, context );
        nodes.addNode(node);
        return node;
    }

    public QueryElementNode buildQueryElementNode( int id, LeftTupleSource tupleSource, QueryElement qe, boolean tupleMemoryEnabled, boolean openQuery, BuildContext context ) {
        QueryElementNode node = new QueryElementNode( id, tupleSource, qe, tupleMemoryEnabled, openQuery, context );
        nodes.addNode(node);
        return node;
    }

    public BaseNode buildFromNode(int id, DataProvider dataProvider, LeftTupleSource tupleSource, AlphaNodeFieldConstraint[] alphaNodeFieldConstraints, BetaConstraints betaConstraints, boolean tupleMemoryEnabled, BuildContext context, From from) {
        FromNode node = new FromNode( id, dataProvider, tupleSource, alphaNodeFieldConstraints, betaConstraints, tupleMemoryEnabled, context, from );
        nodes.addNode(node);
        return node;
    }

}
