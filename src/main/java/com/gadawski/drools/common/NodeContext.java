package com.gadawski.drools.common;

import java.util.HashMap;
import java.util.Map;

import org.drools.common.BaseNode;
import org.drools.reteoo.Sink;

/**
 * Holds all nodes.
 * 
 * @author l.gadawski@gmail.com
 * 
 */
public class NodeContext {
    /**
     * 
     */
    private Map<Integer, BaseNode> nodes = new HashMap<Integer, BaseNode>();
    /**
     * 
     */
    private static NodeContext INSTANCE = null;

    /**
     * 
     */
    private NodeContext() {

    }

    /**
     * @return
     * 
     */
    public static synchronized NodeContext getInstance() {
        if (INSTANCE == null) {
            synchronized (NodeContext.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NodeContext();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * @param node
     */
    public void addNode(Sink node) {
        this.nodes.put(node.getId(), (BaseNode) node);
    }

    /**
     * @param sinkId
     * @return
     */
    public Sink getNode(int sinkId) {
        return (Sink) nodes.get(sinkId);
    }
}
