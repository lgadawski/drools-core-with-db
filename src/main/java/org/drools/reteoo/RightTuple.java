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

package org.drools.reteoo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.WorkingMemoryEntryPoint;
import org.drools.common.InternalFactHandle;
import org.drools.core.util.Entry;
import org.drools.core.util.index.RightTupleList;

import com.gadawski.drools.common.NodeContext;
import com.gadawski.drools.db.tuple.DbTupleManager;
import com.gadawski.drools.db.tuple.IDbTupleManager;

public class RightTuple
    implements
    Entry, Externalizable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * 
     */
    private Integer tupleId;
    /**
     * 
     */
    private Integer handleId;
    
    protected InternalFactHandle handle;

    private RightTuple           handlePrevious;
    private RightTuple           handleNext;

    private RightTupleList       memory;

    private Entry                previous;
    private Entry                next;

    public LeftTuple             firstChild;
    public LeftTuple             lastChild;

    private LeftTuple            blocked;

    /**
     * 
     */
    protected int                         sinkId;
    protected transient RightTupleSink     sink;
    /**
     * 
     */
    private NodeContext m_nodeContext = NodeContext.getInstance();

    public RightTuple() {

    }
    
    public RightTuple(InternalFactHandle handle) {
        // This constructor is here for DSL testing
        this.handle = handle;
    }

    public RightTuple(InternalFactHandle handle,
                      RightTupleSink sink) {
        setUpHandleAndSink(handle, sink);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        this.tupleId = (Integer) in.readObject();
        this.handleId = (Integer) in.readObject();
//        restoreHandle();
        this.handle = (InternalFactHandle) in.readObject();
        this.handlePrevious = (RightTuple) in.readObject();
        this.handleNext = (RightTuple) in.readObject();
        this.memory = (RightTupleList) in.readObject(); // TODO test if this
                                                        // works!
        this.previous = (Entry) in.readObject();
        this.next = (Entry) in.readObject();
        this.firstChild = (LeftTuple) in.readObject();
        this.lastChild = (LeftTuple) in.readObject();
        this.blocked = (LeftTuple) in.readObject();
        this.sinkId = in.readInt();
        this.setSink((RightTupleSink) m_nodeContext.getNode(this.sinkId));
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.tupleId);
        out.writeObject(this.handleId);
        out.writeObject(this.handle);
        out.writeObject(this.handlePrevious);
        out.writeObject(this.handleNext);
        out.writeObject(this.memory); // TODO test if this work for large number
                                      // of objects!
        out.writeObject(this.previous);
        out.writeObject(this.next);
        out.writeObject(this.firstChild);
        out.writeObject(this.lastChild);
        out.writeObject(this.blocked);
        out.writeInt(this.sinkId);
    }
    
    /**
     * Sets up handle and sink.
     * 
     * @param handle
     * @param sink
     * @return 
     */
    private RightTuple setUpHandleAndSink(InternalFactHandle handle,
            RightTupleSink sink) {
        this.handle = handle;
        this.handleId = handle.getId();
        this.sink = sink;
        if (sink != null) {
            this.sinkId = sink.getId();
        }

        // add to end of RightTuples on handle
        handle.addLastRightTuple(this);
        return this;
    }

    /**
     * Creates {@link RightTuple}. Same as RightTuple(InternalFactHandle,
     * RightTupleSink) but don't saves right tuple to db.
     * 
     * @param handle
     * @param sink
     * @return new {@link RightTuple}.
     */
    public static RightTuple createRightTupleFromDb(InternalFactHandle handle,
            RightTupleSink sink) {
        return new RightTuple().setUpHandleAndSink(handle, sink);
    }

    public RightTupleSink getRightTupleSink() {
        return this.sink;
    }
    
    public void reAdd() {
        handle.addLastRightTuple( this );
    }

    public void unlinkFromRightParent() {
        this.handle.removeRightTuple( this );
        this.handle = null;
        this.handlePrevious = null;
        this.handleNext = null;
        this.blocked = null;
        this.previous = null;
        this.next = null;
        this.memory = null;
        this.firstChild = null;
        this.lastChild = null;
        this.sink = null;
    }

    public InternalFactHandle getFactHandle() {
        return this.handle;
    }

    public LeftTuple getBlocked() {
        return this.blocked;
    }
    
    public void nullBlocked() {
        this.blocked = null;
    }
    
    public void setLeftTuple(LeftTuple leftTuple) {
        this.blocked = leftTuple;
    }
    
    public LeftTuple getLeftTuple() {
        return this.blocked;
    }

    public void addBlocked(LeftTuple leftTuple) {
        if ( this.blocked != null && leftTuple != null ) {
            leftTuple.setBlockedNext( this.blocked );
            this.blocked.setBlockedPrevious( leftTuple );
        }
        this.blocked = leftTuple;
    }

    public void removeBlocked(LeftTuple leftTuple) {
        LeftTuple previous =  leftTuple.getBlockedPrevious();
        LeftTuple next =  leftTuple.getBlockedNext();
        if ( previous != null && next != null ) {
            //remove  from middle
            previous.setBlockedNext( next );
            next.setBlockedPrevious( previous );
        } else if ( next != null ) {
            //remove from first
            this.blocked = next;
            next.setBlockedPrevious( null );
        } else if ( previous != null ) {
            //remove from end
            previous.setBlockedNext( null );
        } else {
            this.blocked = null;
        }
    }

    public RightTupleList getMemory() {
        return memory;
    }

    public void setMemory(RightTupleList memory) {
        this.memory = memory;
    }

    public Entry getPrevious() {
        return previous;
    }

    public void setPrevious(Entry previous) {
        this.previous = previous;
    }

    public RightTuple getHandlePrevious() {
        return handlePrevious;
    }

    public void setHandlePrevious(RightTuple handlePrevious) {
        this.handlePrevious = handlePrevious;
    }

    public RightTuple getHandleNext() {
        return handleNext;
    }

    public void setHandleNext(RightTuple handleNext) {
        this.handleNext = handleNext;
    }

    public Entry getNext() {
        return next;
    }

    public void setNext(Entry next) {
        this.next = next;
    }

    //    public LeftTuple getFirstChild() {
    //        return firstChild;
    //    }
    //
    //    public void setFirstChildren(LeftTuple betachildren) {
    //        this.firstChild = betachildren;
    //    }

    public int hashCode() {
        return this.handle.hashCode();
    }

    public String toString() {
        return this.handle.toString() + "\n";
    }

    public boolean equals(RightTuple other) {
        // we know the object is never null and always of the  type ReteTuple
        if ( other == this ) {
            return true;
        }

        // A ReteTuple is  only the same if it has the same hashCode, factId and parent
        if ( (other == null) || (hashCode() != other.hashCode()) ) {
            return false;
        }

        return this.handle == other.handle;
    }

    public boolean equals(Object object) {
        return equals( (RightTuple) object );
    }

    /**
     * @return
     */
    public String getHandleEntryPointId() {
        if (handle != null) {
            return handle.getEntryPointId();
        }
        return "";
    }

    /**
     * @param tupleEntryPoint
     */
    public void setHandleEntryPoint(WorkingMemoryEntryPoint tupleEntryPoint) {
        if (handle != null) {
            handle.setEntryPoint(tupleEntryPoint);
        }
    }

    /**
     * @return
     */
    public int getSinkId() {
        return sinkId;
    }

    /**
     * @param sink
     */
    public void setSink(RightTupleSink sink) {
        this.sink = sink;
    }

    /**
     * @param workingMemory
     * @param sink
     */
    public void restoreTupleAfterSerialization() {
//        restoreHandle();
    }

    /**
     * Based on handleId restore handle from db.
     * 
     * @param workingMemory
     */
    private void restoreHandle() {
        IDbTupleManager tupleManager = DbTupleManager.getInstance();
        this.setHandle((InternalFactHandle) tupleManager.getFactHandle(this.handleId));
    }

    /**
     * @param handle
     */
    private void setHandle(InternalFactHandle handle) {
        this.handle = handle;
        this.handleId = handle.getId();
    }

    /**
     * @return the tupleId
     */
    public Integer getTupleId() {
        return tupleId;
    }

    /**
     * @param tupleId the tupleId to set
     */
    public void setTupleId(Integer tupleId) {
        this.tupleId = tupleId;
    }

    /**
     * @return handle's id.
     */
    public Integer getHandleId() {
        return handleId;
    }
}
