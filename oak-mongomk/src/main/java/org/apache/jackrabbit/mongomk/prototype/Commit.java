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
name|mongomk
operator|.
name|prototype
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|mongomk
operator|.
name|prototype
operator|.
name|DocumentStore
operator|.
name|Collection
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
comment|/**      * The maximum size of a document. If it is larger, it is split.      */
comment|// TODO check which value is the best one
specifier|private
specifier|static
specifier|final
name|int
name|MAX_DOCUMENT_SIZE
init|=
literal|16
operator|*
literal|1024
decl_stmt|;
comment|/**      * Whether to purge old revisions if a node gets too large. If false, old      * revisions are stored in a separate document. If true, old revisions are      * removed (purged).      */
specifier|private
specifier|static
specifier|final
name|boolean
name|PURGE_OLD_REVISIONS
init|=
literal|true
decl_stmt|;
specifier|private
specifier|final
name|MongoMK
name|mk
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
specifier|private
name|HashSet
argument_list|<
name|String
argument_list|>
name|changedNodes
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
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|writeCounts
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
name|Commit
parameter_list|(
name|MongoMK
name|mk
parameter_list|,
name|Revision
name|revision
parameter_list|)
block|{
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
specifier|private
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
name|Revision
name|getRevision
parameter_list|()
block|{
return|return
name|revision
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
name|addMapEntry
argument_list|(
name|key
operator|+
literal|"."
operator|+
name|revision
operator|.
name|toString
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|long
name|increment
init|=
name|mk
operator|.
name|getWriteCountIncrement
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|op
operator|.
name|increment
argument_list|(
name|UpdateOp
operator|.
name|WRITE_COUNT
argument_list|,
literal|1
operator|+
name|increment
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
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Apply the changes to the document store (to update MongoDB).      *       * @param store the store      */
name|void
name|applyToDocumentStore
parameter_list|()
block|{
name|DocumentStore
name|store
init|=
name|mk
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
name|String
name|commitRoot
init|=
literal|null
decl_stmt|;
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
name|commitRoot
operator|==
literal|null
condition|)
block|{
name|commitRoot
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
name|commitRoot
argument_list|,
name|p
argument_list|)
condition|)
block|{
name|commitRoot
operator|=
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|commitRoot
argument_list|)
expr_stmt|;
if|if
condition|(
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|commitRoot
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
block|}
block|}
comment|// create a "root of the commit" if there is none
name|UpdateOp
name|root
init|=
name|getUpdateOperationForNode
argument_list|(
name|commitRoot
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
name|op
operator|==
name|root
condition|)
block|{
comment|// apply at the end
block|}
elseif|else
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
if|if
condition|(
name|changedNodes
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|&&
name|root
operator|.
name|isNew
condition|)
block|{
comment|// no updates, so we just add the root like the others
name|newNodes
operator|.
name|add
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|root
operator|=
literal|null
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
for|for
control|(
name|UpdateOp
name|op
range|:
name|newNodes
control|)
block|{
name|op
operator|.
name|unset
argument_list|(
name|UpdateOp
operator|.
name|ID
argument_list|)
expr_stmt|;
name|op
operator|.
name|addMapEntry
argument_list|(
name|UpdateOp
operator|.
name|DELETED
operator|+
literal|"."
operator|+
name|revision
operator|.
name|toString
argument_list|()
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|op
operator|.
name|increment
argument_list|(
name|UpdateOp
operator|.
name|WRITE_COUNT
argument_list|,
literal|1
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
name|createOrUpdateNode
argument_list|(
name|store
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|root
operator|!=
literal|null
condition|)
block|{
name|long
name|increment
init|=
name|mk
operator|.
name|getWriteCountIncrement
argument_list|(
name|commitRoot
argument_list|)
decl_stmt|;
name|root
operator|.
name|increment
argument_list|(
name|UpdateOp
operator|.
name|WRITE_COUNT
argument_list|,
literal|1
operator|+
name|increment
argument_list|)
expr_stmt|;
name|root
operator|.
name|addMapEntry
argument_list|(
name|UpdateOp
operator|.
name|REVISIONS
operator|+
literal|"."
operator|+
name|revision
operator|.
name|toString
argument_list|()
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|createOrUpdateNode
argument_list|(
name|store
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|operations
operator|.
name|put
argument_list|(
name|commitRoot
argument_list|,
name|root
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
name|error
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
name|createOrUpdateNode
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|UpdateOp
name|op
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
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
name|int
name|size
init|=
name|Utils
operator|.
name|getMapSize
argument_list|(
name|map
argument_list|)
decl_stmt|;
if|if
condition|(
name|size
operator|>
name|MAX_DOCUMENT_SIZE
condition|)
block|{
name|UpdateOp
index|[]
name|split
init|=
name|splitDocument
argument_list|(
name|map
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
comment|// TODO detect conflicts here
name|Long
name|count
init|=
operator|(
name|Long
operator|)
name|map
operator|.
name|get
argument_list|(
name|UpdateOp
operator|.
name|WRITE_COUNT
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|null
condition|)
block|{
name|count
operator|=
literal|0L
expr_stmt|;
block|}
name|String
name|path
init|=
name|op
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|writeCounts
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
specifier|private
name|UpdateOp
index|[]
name|splitDocument
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
block|{
name|String
name|id
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
name|UpdateOp
operator|.
name|ID
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|Utils
operator|.
name|getPathFromId
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|Long
name|previous
init|=
operator|(
name|Long
operator|)
name|map
operator|.
name|get
argument_list|(
name|UpdateOp
operator|.
name|PREVIOUS
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|==
literal|null
condition|)
block|{
name|previous
operator|=
literal|0L
expr_stmt|;
block|}
else|else
block|{
name|previous
operator|++
expr_stmt|;
block|}
name|UpdateOp
name|old
init|=
operator|new
name|UpdateOp
argument_list|(
name|path
argument_list|,
name|id
operator|+
literal|"/"
operator|+
name|previous
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|UpdateOp
name|main
init|=
operator|new
name|UpdateOp
argument_list|(
name|path
argument_list|,
name|id
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|main
operator|.
name|set
argument_list|(
name|UpdateOp
operator|.
name|PREVIOUS
argument_list|,
name|previous
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|e
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|UpdateOp
operator|.
name|ID
argument_list|)
condition|)
block|{
comment|// ok
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|UpdateOp
operator|.
name|PREVIOUS
argument_list|)
condition|)
block|{
comment|// ok
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|UpdateOp
operator|.
name|WRITE_COUNT
argument_list|)
condition|)
block|{
comment|// only maintain the write count on the main document
name|main
operator|.
name|set
argument_list|(
name|UpdateOp
operator|.
name|WRITE_COUNT
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// UpdateOp.DELETED,
comment|// UpdateOp.REVISIONS,
comment|// and regular properties
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
name|valueMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Revision
name|latestRev
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|r
range|:
name|valueMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Revision
name|propRev
init|=
name|Revision
operator|.
name|fromString
argument_list|(
name|r
argument_list|)
decl_stmt|;
if|if
condition|(
name|latestRev
operator|==
literal|null
operator|||
name|mk
operator|.
name|isRevisionNewer
argument_list|(
name|propRev
argument_list|,
name|latestRev
argument_list|)
condition|)
block|{
name|latestRev
operator|=
name|propRev
expr_stmt|;
block|}
block|}
for|for
control|(
name|String
name|r
range|:
name|valueMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Revision
name|propRev
init|=
name|Revision
operator|.
name|fromString
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|Object
name|v
init|=
name|valueMap
operator|.
name|get
argument_list|(
name|r
argument_list|)
decl_stmt|;
if|if
condition|(
name|propRev
operator|.
name|equals
argument_list|(
name|latestRev
argument_list|)
condition|)
block|{
name|main
operator|.
name|setMapEntry
argument_list|(
name|key
operator|+
literal|"."
operator|+
name|propRev
operator|.
name|toString
argument_list|()
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|old
operator|.
name|addMapEntry
argument_list|(
name|key
operator|+
literal|"."
operator|+
name|propRev
operator|.
name|toString
argument_list|()
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|PURGE_OLD_REVISIONS
condition|)
block|{
name|old
operator|=
literal|null
expr_stmt|;
block|}
return|return
operator|new
name|UpdateOp
index|[]
block|{
name|old
block|,
name|main
block|}
return|;
block|}
comment|/**      * Apply the changes to the MongoMK (to update the cache).      */
specifier|public
name|void
name|applyToCache
parameter_list|()
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
name|changedNodes
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
name|long
name|writeCountInc
init|=
name|mk
operator|.
name|getWriteCountIncrement
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|Long
name|writeCount
init|=
name|writeCounts
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|writeCount
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|isNew
condition|)
block|{
name|writeCount
operator|=
literal|0L
expr_stmt|;
name|writeCountInc
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|writeCountInc
operator|++
expr_stmt|;
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
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|mk
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|Long
name|oldWriteCount
init|=
operator|(
name|Long
operator|)
name|map
operator|.
name|get
argument_list|(
name|UpdateOp
operator|.
name|WRITE_COUNT
argument_list|)
decl_stmt|;
name|writeCount
operator|=
name|oldWriteCount
operator|==
literal|null
condition|?
literal|0
else|:
name|oldWriteCount
expr_stmt|;
block|}
block|}
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
name|writeCount
argument_list|,
name|writeCountInc
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
specifier|public
name|JsopWriter
name|getDiff
parameter_list|()
block|{
return|return
name|diff
return|;
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
name|changedNodes
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
name|addMapEntry
argument_list|(
name|UpdateOp
operator|.
name|DELETED
operator|+
literal|"."
operator|+
name|revision
operator|.
name|toString
argument_list|()
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|long
name|increment
init|=
name|mk
operator|.
name|getWriteCountIncrement
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|op
operator|.
name|increment
argument_list|(
name|UpdateOp
operator|.
name|WRITE_COUNT
argument_list|,
literal|1
operator|+
name|increment
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

