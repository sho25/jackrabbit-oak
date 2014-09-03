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
name|document
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|ConcurrentMap
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
name|locks
operator|.
name|Lock
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
name|document
operator|.
name|util
operator|.
name|MapFactory
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
name|document
operator|.
name|util
operator|.
name|Utils
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_import
import|import static
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
name|document
operator|.
name|Collection
operator|.
name|NODES
import|;
end_import

begin_comment
comment|/**  * Keeps track of when nodes where last modified. To be persisted later by  * a background thread.  */
end_comment

begin_class
class|class
name|UnsavedModifications
block|{
comment|/**      * The maximum number of document to update at once in a multi update.      */
specifier|static
specifier|final
name|int
name|BACKGROUND_MULTI_UPDATE_LIMIT
init|=
literal|10000
decl_stmt|;
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|Revision
argument_list|>
name|map
init|=
name|MapFactory
operator|.
name|getInstance
argument_list|()
operator|.
name|create
argument_list|()
decl_stmt|;
comment|/**      * Puts a revision for the given path. The revision for the given path is      * only put if there is no modification present for the revision or if the      * current modification revision is older than the passed revision.      *      * @param path the path of the modified node.      * @param revision the revision of the modification.      * @return the previously set revision for the given path or null if there      *          was none or the current revision is newer.      */
annotation|@
name|CheckForNull
specifier|public
name|Revision
name|put
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
name|Revision
name|revision
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|revision
argument_list|)
expr_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|Revision
name|previous
init|=
name|map
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|map
operator|.
name|putIfAbsent
argument_list|(
name|path
argument_list|,
name|revision
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|previous
operator|.
name|compareRevisionTime
argument_list|(
name|revision
argument_list|)
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|map
operator|.
name|replace
argument_list|(
name|path
argument_list|,
name|previous
argument_list|,
name|revision
argument_list|)
condition|)
block|{
return|return
name|previous
return|;
block|}
block|}
else|else
block|{
comment|// revision is earlier, do not update
return|return
literal|null
return|;
block|}
block|}
block|}
block|}
annotation|@
name|CheckForNull
specifier|public
name|Revision
name|get
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|map
operator|.
name|get
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getPaths
parameter_list|()
block|{
return|return
name|map
operator|.
name|keySet
argument_list|()
return|;
block|}
comment|/**      * Applies all modifications from this instance to the<code>other</code>.      * A modification is only applied if there is no modification in other      * for a given path or if the other modification is earlier than the      * {@code commit} revision.      *      * @param other the other<code>UnsavedModifications</code>.      * @param commit the commit revision.      */
specifier|public
name|void
name|applyTo
parameter_list|(
name|UnsavedModifications
name|other
parameter_list|,
name|Revision
name|commit
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Revision
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|other
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|commit
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns all paths of nodes with modifications at the start revision      * (inclusive) or later.      *      * @param start the start revision (inclusive).      * @return matching paths with pending modifications.      */
annotation|@
name|Nonnull
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getPaths
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|Revision
name|start
parameter_list|)
block|{
if|if
condition|(
name|map
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|Iterables
operator|.
name|filter
argument_list|(
name|map
operator|.
name|entrySet
argument_list|()
argument_list|,
operator|new
name|Predicate
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Revision
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Revision
argument_list|>
name|input
parameter_list|)
block|{
return|return
name|start
operator|.
name|compareRevisionTime
argument_list|(
name|input
operator|.
name|getValue
argument_list|()
argument_list|)
operator|<
literal|1
return|;
block|}
block|}
argument_list|)
argument_list|,
operator|new
name|Function
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Revision
argument_list|>
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Revision
argument_list|>
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getKey
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
comment|/**      * Persist the pending changes to _lastRev to the given store. This method      * will persist a snapshot of the pending revisions by acquiring the passed      * lock for a short period of time.      *      * @param store the document node store.      * @param lock the lock to acquire to get a consistent snapshot of the      *             revisions to write back.      */
specifier|public
name|void
name|persist
parameter_list|(
annotation|@
name|Nonnull
name|DocumentNodeStore
name|store
parameter_list|,
annotation|@
name|Nonnull
name|Lock
name|lock
parameter_list|)
block|{
if|if
condition|(
name|map
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|checkNotNull
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|lock
argument_list|)
expr_stmt|;
comment|// get a copy of the map while holding the lock
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Revision
argument_list|>
name|pending
decl_stmt|;
try|try
block|{
name|pending
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|ArrayList
argument_list|<
name|String
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|pending
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
comment|// sort by depth (high depth first), then path
name|Collections
operator|.
name|sort
argument_list|(
name|paths
argument_list|,
name|PathComparator
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|UpdateOp
name|updateOp
init|=
literal|null
decl_stmt|;
name|Revision
name|lastRev
init|=
literal|null
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|pathList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|paths
operator|.
name|size
argument_list|()
condition|;
control|)
block|{
name|String
name|p
init|=
name|paths
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Revision
name|r
init|=
name|pending
operator|.
name|get
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|null
condition|)
block|{
name|i
operator|++
expr_stmt|;
continue|continue;
block|}
name|int
name|size
init|=
name|pathList
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|updateOp
operator|==
literal|null
condition|)
block|{
comment|// create UpdateOp
name|Commit
name|commit
init|=
operator|new
name|Commit
argument_list|(
name|store
argument_list|,
literal|null
argument_list|,
name|r
argument_list|)
decl_stmt|;
name|updateOp
operator|=
name|commit
operator|.
name|getUpdateOperationForNode
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|NodeDocument
operator|.
name|setLastRev
argument_list|(
name|updateOp
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|lastRev
operator|=
name|r
expr_stmt|;
name|pathList
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|r
operator|.
name|equals
argument_list|(
name|lastRev
argument_list|)
condition|)
block|{
comment|// use multi update when possible
name|pathList
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
comment|// call update if any of the following is true:
comment|// - this is the second-to-last or last path (update last path, the
comment|//   root document, individually)
comment|// - revision is not equal to last revision (size of ids didn't change)
comment|// - the update limit is reached
if|if
condition|(
name|i
operator|+
literal|2
operator|>
name|paths
operator|.
name|size
argument_list|()
operator|||
name|size
operator|==
name|pathList
operator|.
name|size
argument_list|()
operator|||
name|pathList
operator|.
name|size
argument_list|()
operator|>=
name|BACKGROUND_MULTI_UPDATE_LIMIT
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|ids
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|pathList
control|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|Utils
operator|.
name|getIdFromPath
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|update
argument_list|(
name|NODES
argument_list|,
name|ids
argument_list|,
name|updateOp
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
name|pathList
control|)
block|{
name|map
operator|.
name|remove
argument_list|(
name|path
argument_list|,
name|lastRev
argument_list|)
expr_stmt|;
block|}
name|pathList
operator|.
name|clear
argument_list|()
expr_stmt|;
name|updateOp
operator|=
literal|null
expr_stmt|;
name|lastRev
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|map
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

