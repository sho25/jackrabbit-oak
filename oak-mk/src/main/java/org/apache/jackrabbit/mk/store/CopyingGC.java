begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|store
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|model
operator|.
name|ChildNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|model
operator|.
name|ChildNodeEntriesMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|model
operator|.
name|Id
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|model
operator|.
name|MutableCommit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|model
operator|.
name|MutableNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|model
operator|.
name|NodeState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|model
operator|.
name|StoredCommit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|model
operator|.
name|StoredNode
import|;
end_import

begin_comment
comment|/**  * Revision garbage collector that copies reachable revisions from a "from" revision  * store to a "to" revision store. It assumes that both stores share the same blob  * store.  *   * In the current design, the head revision and all the nodes it references are  * reachable.  */
end_comment

begin_class
specifier|public
class|class
name|CopyingGC
implements|implements
name|RevisionStore
block|{
comment|/**      * From store.      */
specifier|private
name|RevisionStore
name|rsFrom
decl_stmt|;
comment|/**      * To store.      */
specifier|private
name|RevisionStore
name|rsTo
decl_stmt|;
comment|/**      * GC run state constants.      */
specifier|private
specifier|static
specifier|final
name|int
name|STOPPED
init|=
literal|0
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|STARTING
init|=
literal|1
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|STARTED
init|=
literal|2
decl_stmt|;
comment|/**      * GC run state.      */
specifier|private
specifier|final
name|AtomicInteger
name|runState
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
comment|/**      * Create a new instance of this class.      *       * @param rsFrom from store      * @param rsTo to store       */
specifier|public
name|CopyingGC
parameter_list|(
name|RevisionStore
name|rsFrom
parameter_list|,
name|RevisionStore
name|rsTo
parameter_list|)
block|{
name|this
operator|.
name|rsFrom
operator|=
name|rsFrom
expr_stmt|;
name|this
operator|.
name|rsTo
operator|=
name|rsTo
expr_stmt|;
block|}
specifier|public
name|void
name|gc
parameter_list|()
block|{
if|if
condition|(
operator|!
name|runState
operator|.
name|compareAndSet
argument_list|(
name|STOPPED
argument_list|,
name|STARTING
argument_list|)
condition|)
block|{
comment|/* already running */
return|return;
block|}
try|try
block|{
comment|/* copy head commit */
name|MutableCommit
name|commitTo
init|=
operator|new
name|MutableCommit
argument_list|(
name|rsFrom
operator|.
name|getHeadCommit
argument_list|()
argument_list|)
decl_stmt|;
name|commitTo
operator|.
name|setParentId
argument_list|(
name|rsTo
operator|.
name|getHeadCommitId
argument_list|()
argument_list|)
expr_stmt|;
name|rsTo
operator|.
name|lockHead
argument_list|()
expr_stmt|;
try|try
block|{
name|rsTo
operator|.
name|putHeadCommit
argument_list|(
name|commitTo
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|rsTo
operator|.
name|unlockHead
argument_list|()
expr_stmt|;
block|}
comment|/* now start putting all further changes to the "to" store */
name|runState
operator|.
name|set
argument_list|(
name|STARTED
argument_list|)
expr_stmt|;
comment|/* copy node hierarchy */
name|copy
argument_list|(
name|rsFrom
operator|.
name|getNode
argument_list|(
name|commitTo
operator|.
name|getRootNodeId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|/* unable to perform GC */
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|runState
operator|.
name|set
argument_list|(
name|STOPPED
argument_list|)
expr_stmt|;
return|return;
block|}
comment|/* switch from and to space */
name|rsFrom
operator|=
name|rsTo
expr_stmt|;
name|runState
operator|.
name|set
argument_list|(
name|STOPPED
argument_list|)
expr_stmt|;
name|rsTo
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * Copy a node and all its descendants into a target store      * @param node source node      * @throws Exception if an error occurs      */
specifier|private
name|void
name|copy
parameter_list|(
name|StoredNode
name|node
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|rsTo
operator|.
name|getNode
argument_list|(
name|node
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|NotFoundException
name|e
parameter_list|)
block|{
comment|// ignore, better add a has() method
block|}
name|rsTo
operator|.
name|putNode
argument_list|(
operator|new
name|MutableNode
argument_list|(
name|node
argument_list|,
name|rsTo
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|ChildNode
argument_list|>
name|iter
init|=
name|node
operator|.
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ChildNode
name|c
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|copy
argument_list|(
name|rsFrom
operator|.
name|getNode
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// ---------------------------------------------------------- RevisionStore
specifier|public
name|NodeState
name|getNodeState
parameter_list|(
name|StoredNode
name|node
parameter_list|)
block|{
return|return
operator|new
name|StoredNodeAsState
argument_list|(
name|node
argument_list|,
name|this
argument_list|)
return|;
block|}
specifier|public
name|Id
name|getId
parameter_list|(
name|NodeState
name|node
parameter_list|)
block|{
return|return
operator|(
operator|(
name|StoredNodeAsState
operator|)
name|node
operator|)
operator|.
name|getId
argument_list|()
return|;
block|}
specifier|public
name|StoredNode
name|getNode
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
if|if
condition|(
name|runState
operator|.
name|get
argument_list|()
operator|==
name|STARTED
condition|)
block|{
try|try
block|{
return|return
name|rsTo
operator|.
name|getNode
argument_list|(
name|id
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NotFoundException
name|e
parameter_list|)
block|{
comment|/* ignore */
block|}
block|}
try|try
block|{
return|return
name|rsFrom
operator|.
name|getNode
argument_list|(
name|id
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NotFoundException
name|e
parameter_list|)
block|{
comment|//            System.out.println(rsFrom + " --> " + id + " failed!");
throw|throw
name|e
throw|;
block|}
block|}
specifier|public
name|StoredCommit
name|getCommit
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
return|return
name|rsFrom
operator|.
name|getCommit
argument_list|(
name|id
argument_list|)
return|;
block|}
specifier|public
name|ChildNodeEntriesMap
name|getCNEMap
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
return|return
name|rsFrom
operator|.
name|getCNEMap
argument_list|(
name|id
argument_list|)
return|;
block|}
specifier|public
name|StoredNode
name|getRootNode
parameter_list|(
name|Id
name|commitId
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
return|return
name|rsFrom
operator|.
name|getRootNode
argument_list|(
name|commitId
argument_list|)
return|;
block|}
specifier|public
name|StoredCommit
name|getHeadCommit
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|runState
operator|.
name|get
argument_list|()
operator|==
name|STARTED
condition|?
name|rsTo
operator|.
name|getHeadCommit
argument_list|()
else|:
name|rsFrom
operator|.
name|getHeadCommit
argument_list|()
return|;
block|}
specifier|public
name|Id
name|getHeadCommitId
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|runState
operator|.
name|get
argument_list|()
operator|==
name|STARTED
condition|?
name|rsTo
operator|.
name|getHeadCommitId
argument_list|()
else|:
name|rsFrom
operator|.
name|getHeadCommitId
argument_list|()
return|;
block|}
specifier|public
name|Id
name|putNode
parameter_list|(
name|MutableNode
name|node
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|runState
operator|.
name|get
argument_list|()
operator|==
name|STARTED
condition|)
block|{
name|Id
name|id
init|=
name|rsTo
operator|.
name|putNode
argument_list|(
name|node
argument_list|)
decl_stmt|;
comment|//            System.out.println(rsTo + "<-- " + node.toString() + "(" + id + ")");
return|return
name|id
return|;
block|}
else|else
block|{
name|Id
name|id
init|=
name|rsFrom
operator|.
name|putNode
argument_list|(
name|node
argument_list|)
decl_stmt|;
comment|//            System.out.println(rsFrom + "<-- " + node.toString() + "(" + id + ")");
return|return
name|id
return|;
block|}
block|}
specifier|public
name|Id
name|putCNEMap
parameter_list|(
name|ChildNodeEntriesMap
name|map
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|runState
operator|.
name|get
argument_list|()
operator|==
name|STARTED
condition|?
name|rsTo
operator|.
name|putCNEMap
argument_list|(
name|map
argument_list|)
else|:
name|rsFrom
operator|.
name|putCNEMap
argument_list|(
name|map
argument_list|)
return|;
block|}
comment|// TODO: potentially dangerous, if lock& unlock interfere with GC start
specifier|public
name|void
name|lockHead
parameter_list|()
block|{
if|if
condition|(
name|runState
operator|.
name|get
argument_list|()
operator|==
name|STARTED
condition|)
block|{
name|rsTo
operator|.
name|lockHead
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|rsFrom
operator|.
name|lockHead
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|Id
name|putHeadCommit
parameter_list|(
name|MutableCommit
name|commit
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|runState
operator|.
name|get
argument_list|()
operator|==
name|STARTED
condition|?
name|rsTo
operator|.
name|putHeadCommit
argument_list|(
name|commit
argument_list|)
else|:
name|rsFrom
operator|.
name|putHeadCommit
argument_list|(
name|commit
argument_list|)
return|;
block|}
comment|// TODO: potentially dangerous, if lock& unlock interfere with GC start
specifier|public
name|void
name|unlockHead
parameter_list|()
block|{
if|if
condition|(
name|runState
operator|.
name|get
argument_list|()
operator|==
name|STARTED
condition|)
block|{
name|rsTo
operator|.
name|unlockHead
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|rsFrom
operator|.
name|unlockHead
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

