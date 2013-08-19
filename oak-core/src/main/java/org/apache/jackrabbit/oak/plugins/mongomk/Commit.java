begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|mongomk
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|AtomicReference
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|api
operator|.
name|MicroKernelException
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
name|json
operator|.
name|JsopStream
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
name|json
operator|.
name|JsopWriter
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
name|oak
operator|.
name|plugins
operator|.
name|mongomk
operator|.
name|util
operator|.
name|Utils
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
name|oak
operator|.
name|commons
operator|.
name|PathUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * A higher level object representing a commit.  */
end_comment

begin_class
specifier|public
class|class
name|Commit
block|{
comment|/**      * Whether to purge old revisions if a node gets too large. If false, old      * revisions are stored in a separate document. If true, old revisions are      * removed (purged).      * TODO: enable once document split and garbage collection implementation is complete.      */
specifier|static
specifier|final
name|boolean
name|PURGE_OLD_REVISIONS
init|=
literal|false
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|Commit
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * The maximum size of a document. If it is larger, it is split.      * TODO: check which value is the best one      *       Document splitting is currently disabled until the implementation      *       is complete.      */
specifier|private
specifier|static
specifier|final
name|int
name|MAX_DOCUMENT_SIZE
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
specifier|private
specifier|final
name|MongoMK
name|mk
decl_stmt|;
specifier|private
specifier|final
name|Revision
name|baseRevision
decl_stmt|;
specifier|private
specifier|final
name|Revision
name|revision
decl_stmt|;
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|UpdateOp
argument_list|>
name|operations
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|UpdateOp
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|JsopWriter
name|diff
init|=
operator|new
name|JsopStream
argument_list|()
decl_stmt|;
comment|/**      * List of all node paths which have been modified in this commit. In addition to the nodes      * which are actually changed it also contains there parent node paths      */
specifier|private
name|HashSet
argument_list|<
name|String
argument_list|>
name|modifiedNodes
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|HashSet
argument_list|<
name|String
argument_list|>
name|addedNodes
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|HashSet
argument_list|<
name|String
argument_list|>
name|removedNodes
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Commit
parameter_list|(
name|MongoMK
name|mk
parameter_list|,
name|Revision
name|baseRevision
parameter_list|,
name|Revision
name|revision
parameter_list|)
block|{
name|this
operator|.
name|baseRevision
operator|=
name|baseRevision
expr_stmt|;
name|this
operator|.
name|revision
operator|=
name|revision
expr_stmt|;
name|this
operator|.
name|mk
operator|=
name|mk
expr_stmt|;
block|}
name|UpdateOp
name|getUpdateOperationForNode
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|UpdateOp
name|op
init|=
name|operations
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|op
operator|==
literal|null
condition|)
block|{
name|String
name|id
init|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|op
operator|=
operator|new
name|UpdateOp
argument_list|(
name|path
argument_list|,
name|id
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|NodeDocument
operator|.
name|setModified
argument_list|(
name|op
argument_list|,
name|revision
argument_list|)
expr_stmt|;
name|operations
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
return|return
name|op
return|;
block|}
specifier|public
specifier|static
name|long
name|getModified
parameter_list|(
name|long
name|timestamp
parameter_list|)
block|{
comment|// 5 second resolution
return|return
name|timestamp
operator|/
literal|1000
operator|/
literal|5
return|;
block|}
comment|/**      * The revision for this new commit. That is, the changes within this commit      * will be visible with this revision.      *      * @return the revision for this new commit.      */
annotation|@
name|Nonnull
name|Revision
name|getRevision
parameter_list|()
block|{
return|return
name|revision
return|;
block|}
comment|/**      * Returns the base revision for this commit. That is, the revision passed      * to {@link MongoMK#commit(String, String, String, String)}. The base      * revision may be<code>null</code>, e.g. for the initial commit of the      * root node, when there is no base revision.      *      * @return the base revision of this commit or<code>null</code>.      */
annotation|@
name|CheckForNull
name|Revision
name|getBaseRevision
parameter_list|()
block|{
return|return
name|baseRevision
return|;
block|}
name|void
name|addNodeDiff
parameter_list|(
name|Node
name|n
parameter_list|)
block|{
name|diff
operator|.
name|tag
argument_list|(
literal|'+'
argument_list|)
operator|.
name|key
argument_list|(
name|n
operator|.
name|path
argument_list|)
expr_stmt|;
name|diff
operator|.
name|object
argument_list|()
expr_stmt|;
name|n
operator|.
name|append
argument_list|(
name|diff
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|diff
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|diff
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|touchNode
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|UpdateOp
name|op
init|=
name|getUpdateOperationForNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|op
operator|.
name|setMapEntry
argument_list|(
name|NodeDocument
operator|.
name|LAST_REV
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|revision
operator|.
name|getClusterId
argument_list|()
argument_list|)
argument_list|,
name|revision
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|void
name|updateProperty
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|UpdateOp
name|op
init|=
name|getUpdateOperationForNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|String
name|key
init|=
name|Utils
operator|.
name|escapePropertyName
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
name|op
operator|.
name|setMapEntry
argument_list|(
name|key
argument_list|,
name|revision
operator|.
name|toString
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|void
name|addNode
parameter_list|(
name|Node
name|n
parameter_list|)
block|{
if|if
condition|(
name|operations
operator|.
name|containsKey
argument_list|(
name|n
operator|.
name|path
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
literal|"Node already added: "
operator|+
name|n
operator|.
name|path
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|MicroKernelException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
name|operations
operator|.
name|put
argument_list|(
name|n
operator|.
name|path
argument_list|,
name|n
operator|.
name|asOperation
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|addedNodes
operator|.
name|add
argument_list|(
name|n
operator|.
name|path
argument_list|)
expr_stmt|;
block|}
comment|/**      * Apply the changes to the document store and the cache.      */
name|void
name|apply
parameter_list|()
block|{
if|if
condition|(
operator|!
name|operations
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|applyToDocumentStore
argument_list|()
expr_stmt|;
name|applyToCache
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|prepare
parameter_list|(
name|Revision
name|baseRevision
parameter_list|)
block|{
if|if
condition|(
operator|!
name|operations
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|applyToDocumentStore
argument_list|(
name|baseRevision
argument_list|)
expr_stmt|;
name|applyToCache
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Apply the changes to the document store (to update MongoDB).      */
name|void
name|applyToDocumentStore
parameter_list|()
block|{
name|applyToDocumentStore
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Apply the changes to the document store (to update MongoDB).      *      * @param baseBranchRevision the base revision of this commit. Currently only      *                     used for branch commits.      */
name|void
name|applyToDocumentStore
parameter_list|(
name|Revision
name|baseBranchRevision
parameter_list|)
block|{
comment|// the value in _revisions.<revision> property of the commit root node
comment|// regular commits use "c", which makes the commit visible to
comment|// other readers. branch commits use the base revision to indicate
comment|// the visibility of the commit
name|String
name|commitValue
init|=
name|baseBranchRevision
operator|!=
literal|null
condition|?
name|baseBranchRevision
operator|.
name|toString
argument_list|()
else|:
literal|"c"
decl_stmt|;
name|DocumentStore
name|store
init|=
name|mk
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
name|String
name|commitRootPath
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|baseBranchRevision
operator|!=
literal|null
condition|)
block|{
comment|// branch commits always use root node as commit root
name|commitRootPath
operator|=
literal|"/"
expr_stmt|;
block|}
name|ArrayList
argument_list|<
name|UpdateOp
argument_list|>
name|newNodes
init|=
operator|new
name|ArrayList
argument_list|<
name|UpdateOp
argument_list|>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|UpdateOp
argument_list|>
name|changedNodes
init|=
operator|new
name|ArrayList
argument_list|<
name|UpdateOp
argument_list|>
argument_list|()
decl_stmt|;
comment|// operations are added to this list before they are executed,
comment|// so that all operations can be rolled back if there is a conflict
name|ArrayList
argument_list|<
name|UpdateOp
argument_list|>
name|opLog
init|=
operator|new
name|ArrayList
argument_list|<
name|UpdateOp
argument_list|>
argument_list|()
decl_stmt|;
comment|//Compute the commit root
for|for
control|(
name|String
name|p
range|:
name|operations
operator|.
name|keySet
argument_list|()
control|)
block|{
name|markChanged
argument_list|(
name|p
argument_list|)
expr_stmt|;
if|if
condition|(
name|commitRootPath
operator|==
literal|null
condition|)
block|{
name|commitRootPath
operator|=
name|p
expr_stmt|;
block|}
else|else
block|{
while|while
condition|(
operator|!
name|PathUtils
operator|.
name|isAncestor
argument_list|(
name|commitRootPath
argument_list|,
name|p
argument_list|)
condition|)
block|{
name|commitRootPath
operator|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|commitRootPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|commitRootPath
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
block|}
block|}
name|int
name|commitRootDepth
init|=
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|commitRootPath
argument_list|)
decl_stmt|;
comment|// create a "root of the commit" if there is none
name|UpdateOp
name|commitRoot
init|=
name|getUpdateOperationForNode
argument_list|(
name|commitRootPath
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|operations
operator|.
name|keySet
argument_list|()
control|)
block|{
name|UpdateOp
name|op
init|=
name|operations
operator|.
name|get
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|baseBranchRevision
operator|==
literal|null
condition|)
block|{
comment|// only apply _lastRev for trunk commits, _lastRev for
comment|// branch commits only become visible on merge
name|op
operator|.
name|setMapEntry
argument_list|(
name|NodeDocument
operator|.
name|LAST_REV
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|revision
operator|.
name|getClusterId
argument_list|()
argument_list|)
argument_list|,
name|revision
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|op
operator|.
name|isNew
condition|)
block|{
name|op
operator|.
name|setMapEntry
argument_list|(
name|NodeDocument
operator|.
name|DELETED
argument_list|,
name|revision
operator|.
name|toString
argument_list|()
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|op
operator|==
name|commitRoot
condition|)
block|{
comment|// apply at the end
block|}
else|else
block|{
name|op
operator|.
name|setMapEntry
argument_list|(
name|NodeDocument
operator|.
name|COMMIT_ROOT
argument_list|,
name|revision
operator|.
name|toString
argument_list|()
argument_list|,
name|commitRootDepth
argument_list|)
expr_stmt|;
if|if
condition|(
name|op
operator|.
name|isNew
argument_list|()
condition|)
block|{
name|newNodes
operator|.
name|add
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|changedNodes
operator|.
name|add
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|changedNodes
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|&&
name|commitRoot
operator|.
name|isNew
condition|)
block|{
comment|// no updates and root of commit is also new. that is,
comment|// it is the root of a subtree added in a commit.
comment|// so we try to add the root like all other nodes
name|commitRoot
operator|.
name|setMapEntry
argument_list|(
name|NodeDocument
operator|.
name|REVISIONS
argument_list|,
name|revision
operator|.
name|toString
argument_list|()
argument_list|,
name|commitValue
argument_list|)
expr_stmt|;
name|newNodes
operator|.
name|add
argument_list|(
name|commitRoot
argument_list|)
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
name|newNodes
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// set commit root on new nodes
if|if
condition|(
operator|!
name|store
operator|.
name|create
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|newNodes
argument_list|)
condition|)
block|{
comment|// some of the documents already exist:
comment|// try to apply all changes one by one
for|for
control|(
name|UpdateOp
name|op
range|:
name|newNodes
control|)
block|{
if|if
condition|(
name|op
operator|==
name|commitRoot
condition|)
block|{
comment|// don't write the commit root just yet
comment|// (because there might be a conflict)
name|commitRoot
operator|.
name|unsetMapEntry
argument_list|(
name|NodeDocument
operator|.
name|REVISIONS
argument_list|,
name|revision
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|changedNodes
operator|.
name|add
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
name|newNodes
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|UpdateOp
name|op
range|:
name|changedNodes
control|)
block|{
comment|// set commit root on changed nodes
name|op
operator|.
name|setMapEntry
argument_list|(
name|NodeDocument
operator|.
name|COMMIT_ROOT
argument_list|,
name|revision
operator|.
name|toString
argument_list|()
argument_list|,
name|commitRootDepth
argument_list|)
expr_stmt|;
name|opLog
operator|.
name|add
argument_list|(
name|op
argument_list|)
expr_stmt|;
name|createOrUpdateNode
argument_list|(
name|store
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
comment|// finally write the commit root, unless it was already written
comment|// with added nodes (the commit root might be written twice,
comment|// first to check if there was a conflict, and only then to commit
comment|// the revision, with the revision property set)
if|if
condition|(
name|changedNodes
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|||
operator|!
name|commitRoot
operator|.
name|isNew
condition|)
block|{
name|commitRoot
operator|.
name|setMapEntry
argument_list|(
name|NodeDocument
operator|.
name|REVISIONS
argument_list|,
name|revision
operator|.
name|toString
argument_list|()
argument_list|,
name|commitValue
argument_list|)
expr_stmt|;
name|opLog
operator|.
name|add
argument_list|(
name|commitRoot
argument_list|)
expr_stmt|;
name|createOrUpdateNode
argument_list|(
name|store
argument_list|,
name|commitRoot
argument_list|)
expr_stmt|;
name|operations
operator|.
name|put
argument_list|(
name|commitRootPath
argument_list|,
name|commitRoot
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|MicroKernelException
name|e
parameter_list|)
block|{
name|rollback
argument_list|(
name|newNodes
argument_list|,
name|opLog
argument_list|)
expr_stmt|;
name|String
name|msg
init|=
literal|"Exception committing "
operator|+
name|diff
operator|.
name|toString
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|MicroKernelException
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|rollback
parameter_list|(
name|ArrayList
argument_list|<
name|UpdateOp
argument_list|>
name|newDocuments
parameter_list|,
name|ArrayList
argument_list|<
name|UpdateOp
argument_list|>
name|changed
parameter_list|)
block|{
name|DocumentStore
name|store
init|=
name|mk
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
for|for
control|(
name|UpdateOp
name|op
range|:
name|changed
control|)
block|{
name|UpdateOp
name|reverse
init|=
name|op
operator|.
name|getReverseOperation
argument_list|()
decl_stmt|;
name|store
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|reverse
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|UpdateOp
name|op
range|:
name|newDocuments
control|)
block|{
name|store
operator|.
name|remove
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|op
operator|.
name|key
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Try to create or update the node. If there was a conflict, this method      * throws an exception, even though the change is still applied.      *       * @param store the store      * @param op the operation      */
specifier|public
name|void
name|createOrUpdateNode
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|UpdateOp
name|op
parameter_list|)
block|{
name|NodeDocument
name|doc
init|=
name|store
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|op
argument_list|)
decl_stmt|;
if|if
condition|(
name|baseRevision
operator|!=
literal|null
condition|)
block|{
specifier|final
name|AtomicReference
argument_list|<
name|List
argument_list|<
name|Revision
argument_list|>
argument_list|>
name|collisions
init|=
operator|new
name|AtomicReference
argument_list|<
name|List
argument_list|<
name|Revision
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|Revision
name|newestRev
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|newestRev
operator|=
name|doc
operator|.
name|getNewestRevision
argument_list|(
name|mk
argument_list|,
name|store
argument_list|,
name|revision
argument_list|,
operator|new
name|CollisionHandler
argument_list|()
block|{
annotation|@
name|Override
name|void
name|concurrentModification
parameter_list|(
name|Revision
name|other
parameter_list|)
block|{
if|if
condition|(
name|collisions
operator|.
name|get
argument_list|()
operator|==
literal|null
condition|)
block|{
name|collisions
operator|.
name|set
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Revision
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|collisions
operator|.
name|get
argument_list|()
operator|.
name|add
argument_list|(
name|other
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|String
name|conflictMessage
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|newestRev
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|op
operator|.
name|isDelete
operator|||
operator|!
name|op
operator|.
name|isNew
condition|)
block|{
name|conflictMessage
operator|=
literal|"The node "
operator|+
name|op
operator|.
name|path
operator|+
literal|" does not exist or is already deleted"
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|op
operator|.
name|isNew
condition|)
block|{
name|conflictMessage
operator|=
literal|"The node "
operator|+
name|op
operator|.
name|path
operator|+
literal|" was already added in revision\n"
operator|+
name|newestRev
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|mk
operator|.
name|isRevisionNewer
argument_list|(
name|newestRev
argument_list|,
name|baseRevision
argument_list|)
operator|&&
operator|(
name|op
operator|.
name|isDelete
operator|||
name|isConflicting
argument_list|(
name|doc
argument_list|,
name|op
argument_list|)
operator|)
condition|)
block|{
name|conflictMessage
operator|=
literal|"The node "
operator|+
name|op
operator|.
name|path
operator|+
literal|" was changed in revision\n"
operator|+
name|newestRev
operator|+
literal|", which was applied after the base revision\n"
operator|+
name|baseRevision
expr_stmt|;
block|}
block|}
if|if
condition|(
name|conflictMessage
operator|!=
literal|null
condition|)
block|{
name|conflictMessage
operator|+=
literal|", before\n"
operator|+
name|revision
operator|+
literal|"; document:\n"
operator|+
name|Utils
operator|.
name|formatDocument
argument_list|(
name|doc
argument_list|)
operator|+
literal|",\nrevision order:\n"
operator|+
name|mk
operator|.
name|getRevisionComparator
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|MicroKernelException
argument_list|(
name|conflictMessage
argument_list|)
throw|;
block|}
comment|// if we get here the modification was successful
comment|// -> check for collisions and conflict (concurrent updates
comment|// on a node are possible if property updates do not overlap)
if|if
condition|(
name|collisions
operator|.
name|get
argument_list|()
operator|!=
literal|null
operator|&&
name|isConflicting
argument_list|(
name|doc
argument_list|,
name|op
argument_list|)
condition|)
block|{
for|for
control|(
name|Revision
name|r
range|:
name|collisions
operator|.
name|get
argument_list|()
control|)
block|{
comment|// mark collisions on commit root
operator|new
name|Collision
argument_list|(
name|doc
argument_list|,
name|r
argument_list|,
name|op
argument_list|,
name|revision
argument_list|)
operator|.
name|mark
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|int
name|size
init|=
name|Utils
operator|.
name|estimateMemoryUsage
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
operator|&&
name|size
operator|>
name|MAX_DOCUMENT_SIZE
condition|)
block|{
name|UpdateOp
index|[]
name|split
init|=
name|doc
operator|.
name|splitDocument
argument_list|(
name|mk
argument_list|,
name|revision
argument_list|,
name|mk
operator|.
name|getSplitDocumentAgeMillis
argument_list|()
argument_list|)
decl_stmt|;
comment|// TODO check if the new main document is actually smaller;
comment|// otherwise, splitting doesn't make sense
comment|// the old version
name|UpdateOp
name|old
init|=
name|split
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
condition|)
block|{
name|store
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|old
argument_list|)
expr_stmt|;
block|}
comment|// the (shrunken) main document
name|UpdateOp
name|main
init|=
name|split
index|[
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|main
operator|!=
literal|null
condition|)
block|{
name|store
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|main
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Checks whether the given<code>UpdateOp</code> conflicts with the      * existing content in<code>doc</code>. The check is done based on the      * {@link #baseRevision} of this commit. An<code>UpdateOp</code> conflicts      * when there were changes after {@link #baseRevision} on properties also      * contained in<code>UpdateOp</code>.      *      * @param doc the contents of the nodes before the update.      * @param op the update to perform.      * @return<code>true</code> if the update conflicts;<code>false</code>      *         otherwise.      */
specifier|private
name|boolean
name|isConflicting
parameter_list|(
annotation|@
name|Nullable
name|NodeDocument
name|doc
parameter_list|,
annotation|@
name|Nonnull
name|UpdateOp
name|op
parameter_list|)
block|{
if|if
condition|(
name|baseRevision
operator|==
literal|null
operator|||
name|doc
operator|==
literal|null
condition|)
block|{
comment|// no conflict is possible when there is no baseRevision
comment|// or document did not exist before
return|return
literal|false
return|;
block|}
comment|// did existence of node change after baseRevision?
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|deleted
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
operator|)
name|doc
operator|.
name|get
argument_list|(
name|NodeDocument
operator|.
name|DELETED
argument_list|)
decl_stmt|;
if|if
condition|(
name|deleted
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|deleted
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|mk
operator|.
name|isRevisionNewer
argument_list|(
name|Revision
operator|.
name|fromString
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|baseRevision
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|UpdateOp
operator|.
name|Operation
argument_list|>
name|entry
range|:
name|op
operator|.
name|changes
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|type
operator|!=
name|UpdateOp
operator|.
name|Operation
operator|.
name|Type
operator|.
name|SET_MAP_ENTRY
condition|)
block|{
continue|continue;
block|}
name|int
name|idx
init|=
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
decl_stmt|;
if|if
condition|(
name|NodeDocument
operator|.
name|DELETED
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// existence of node changed, this always conflicts with
comment|// any other concurrent change
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
name|Utils
operator|.
name|isPropertyName
argument_list|(
name|name
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|// was this property touched after baseRevision?
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|changes
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|doc
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|changes
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|String
name|rev
range|:
name|changes
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|mk
operator|.
name|isRevisionNewer
argument_list|(
name|Revision
operator|.
name|fromString
argument_list|(
name|rev
argument_list|)
argument_list|,
name|baseRevision
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Apply the changes to the MongoMK (to update the cache).      *       * @param isBranchCommit whether this is a commit to a branch      */
specifier|public
name|void
name|applyToCache
parameter_list|(
name|boolean
name|isBranchCommit
parameter_list|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|>
name|nodesWithChangedChildren
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|addOrRemove
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|addOrRemove
operator|.
name|addAll
argument_list|(
name|addedNodes
argument_list|)
expr_stmt|;
name|addOrRemove
operator|.
name|addAll
argument_list|(
name|removedNodes
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|p
range|:
name|addOrRemove
control|)
block|{
name|String
name|parent
init|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|list
init|=
name|nodesWithChangedChildren
operator|.
name|get
argument_list|(
name|parent
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|nodesWithChangedChildren
operator|.
name|put
argument_list|(
name|parent
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|path
range|:
name|modifiedNodes
control|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|added
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|removed
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|changed
init|=
name|nodesWithChangedChildren
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|changed
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|s
range|:
name|changed
control|)
block|{
if|if
condition|(
name|addedNodes
operator|.
name|contains
argument_list|(
name|s
argument_list|)
condition|)
block|{
name|added
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|removedNodes
operator|.
name|contains
argument_list|(
name|s
argument_list|)
condition|)
block|{
name|removed
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|UpdateOp
name|op
init|=
name|operations
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|boolean
name|isNew
init|=
name|op
operator|!=
literal|null
operator|&&
name|op
operator|.
name|isNew
decl_stmt|;
name|boolean
name|isWritten
init|=
name|op
operator|!=
literal|null
decl_stmt|;
name|boolean
name|isDelete
init|=
name|op
operator|!=
literal|null
operator|&&
name|op
operator|.
name|isDelete
decl_stmt|;
name|mk
operator|.
name|applyChanges
argument_list|(
name|revision
argument_list|,
name|path
argument_list|,
name|isNew
argument_list|,
name|isDelete
argument_list|,
name|isWritten
argument_list|,
name|isBranchCommit
argument_list|,
name|added
argument_list|,
name|removed
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|moveNode
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|targetPath
parameter_list|)
block|{
name|diff
operator|.
name|tag
argument_list|(
literal|'>'
argument_list|)
operator|.
name|key
argument_list|(
name|sourcePath
argument_list|)
operator|.
name|value
argument_list|(
name|targetPath
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|copyNode
parameter_list|(
name|String
name|sourcePath
parameter_list|,
name|String
name|targetPath
parameter_list|)
block|{
name|diff
operator|.
name|tag
argument_list|(
literal|'*'
argument_list|)
operator|.
name|key
argument_list|(
name|sourcePath
argument_list|)
operator|.
name|value
argument_list|(
name|targetPath
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|markChanged
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|path
argument_list|)
operator|&&
operator|!
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|path
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"path: "
operator|+
name|path
argument_list|)
throw|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
name|modifiedNodes
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|path
argument_list|)
condition|)
block|{
break|break;
block|}
name|path
operator|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|updatePropertyDiff
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|diff
operator|.
name|tag
argument_list|(
literal|'^'
argument_list|)
operator|.
name|key
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|propertyName
argument_list|)
argument_list|)
operator|.
name|value
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeNodeDiff
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|diff
operator|.
name|tag
argument_list|(
literal|'-'
argument_list|)
operator|.
name|value
argument_list|(
name|path
argument_list|)
operator|.
name|newline
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|removeNode
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|removedNodes
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|UpdateOp
name|op
init|=
name|getUpdateOperationForNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|op
operator|.
name|setDelete
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|op
operator|.
name|setMapEntry
argument_list|(
name|NodeDocument
operator|.
name|DELETED
argument_list|,
name|revision
operator|.
name|toString
argument_list|()
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

