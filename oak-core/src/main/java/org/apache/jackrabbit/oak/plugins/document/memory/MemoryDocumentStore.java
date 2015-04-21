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
operator|.
name|memory
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
name|Comparator
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
name|ConcurrentNavigableMap
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
name|ConcurrentSkipListMap
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReadWriteLock
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
name|ReentrantReadWriteLock
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|cache
operator|.
name|CacheStats
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
name|plugins
operator|.
name|document
operator|.
name|Document
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
name|DocumentStore
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
name|DocumentStoreException
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
name|NodeDocument
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
name|Revision
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
name|StableRevisionComparator
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
name|UpdateOp
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
name|UpdateUtils
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
name|Splitter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|ReadPreference
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|WriteConcern
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
name|cache
operator|.
name|CacheInvalidationStats
import|;
end_import

begin_comment
comment|/**  * Emulates a MongoDB store (possibly consisting of multiple shards and  * replicas).  */
end_comment

begin_class
specifier|public
class|class
name|MemoryDocumentStore
implements|implements
name|DocumentStore
block|{
comment|/**      * The 'nodes' collection.      */
specifier|private
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|NodeDocument
argument_list|>
name|nodes
init|=
operator|new
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|NodeDocument
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * The 'clusterNodes' collection.      */
specifier|private
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|Document
argument_list|>
name|clusterNodes
init|=
operator|new
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|Document
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * The 'settings' collection.      */
specifier|private
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|Document
argument_list|>
name|settings
init|=
operator|new
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|Document
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ReadWriteLock
name|rwLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
comment|/**      * Comparator for maps with {@link Revision} keys. The maps are ordered      * descending, newest revisions first!      */
specifier|private
specifier|final
name|Comparator
argument_list|<
name|Revision
argument_list|>
name|comparator
init|=
name|StableRevisionComparator
operator|.
name|REVERSE
decl_stmt|;
specifier|private
name|ReadPreference
name|readPreference
decl_stmt|;
specifier|private
name|WriteConcern
name|writeConcern
decl_stmt|;
specifier|private
name|Object
name|lastReadWriteMode
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
decl_stmt|;
specifier|public
name|MemoryDocumentStore
parameter_list|()
block|{
name|metadata
operator|=
name|ImmutableMap
operator|.
expr|<
name|String
operator|,
name|String
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
literal|"memory"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|find
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|key
parameter_list|,
name|int
name|maxCacheAge
parameter_list|)
block|{
return|return
name|find
argument_list|(
name|collection
argument_list|,
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|find
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|Lock
name|lock
init|=
name|rwLock
operator|.
name|readLock
argument_list|()
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|map
init|=
name|getMap
argument_list|(
name|collection
argument_list|)
decl_stmt|;
return|return
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|query
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|fromKey
parameter_list|,
name|String
name|toKey
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
return|return
name|query
argument_list|(
name|collection
argument_list|,
name|fromKey
argument_list|,
name|toKey
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
name|limit
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|query
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|fromKey
parameter_list|,
name|String
name|toKey
parameter_list|,
name|String
name|indexedProperty
parameter_list|,
name|long
name|startValue
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
name|Lock
name|lock
init|=
name|rwLock
operator|.
name|readLock
argument_list|()
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|map
init|=
name|getMap
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|ConcurrentNavigableMap
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|sub
init|=
name|map
operator|.
name|subMap
argument_list|(
name|fromKey
operator|+
literal|"\0"
argument_list|,
name|toKey
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|T
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|T
name|doc
range|:
name|sub
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|indexedProperty
operator|!=
literal|null
condition|)
block|{
name|Object
name|value
init|=
name|doc
operator|.
name|get
argument_list|(
name|indexedProperty
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|Boolean
condition|)
block|{
name|long
name|test
init|=
operator|(
operator|(
name|Boolean
operator|)
name|value
operator|)
condition|?
literal|1
else|:
literal|0
decl_stmt|;
if|if
condition|(
name|test
operator|<
name|startValue
condition|)
block|{
continue|continue;
block|}
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Long
condition|)
block|{
if|if
condition|(
operator|(
name|Long
operator|)
name|value
operator|<
name|startValue
condition|)
block|{
continue|continue;
block|}
block|}
elseif|else
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
literal|"unexpected type for property "
operator|+
name|indexedProperty
operator|+
literal|": "
operator|+
name|value
operator|.
name|getClass
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|list
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|>=
name|limit
condition|)
block|{
break|break;
block|}
block|}
return|return
name|list
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|void
name|remove
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|Lock
name|lock
init|=
name|rwLock
operator|.
name|writeLock
argument_list|()
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|getMap
argument_list|(
name|collection
argument_list|)
operator|.
name|remove
argument_list|(
name|path
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
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|void
name|remove
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|keys
parameter_list|)
block|{
for|for
control|(
name|String
name|key
range|:
name|keys
control|)
block|{
name|remove
argument_list|(
name|collection
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|createOrUpdate
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|UpdateOp
name|update
parameter_list|)
block|{
return|return
name|internalCreateOrUpdate
argument_list|(
name|collection
argument_list|,
name|update
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|findAndUpdate
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|UpdateOp
name|update
parameter_list|)
block|{
return|return
name|internalCreateOrUpdate
argument_list|(
name|collection
argument_list|,
name|update
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**      * Get the in-memory map for this collection.      *      * @param collection the collection      * @return the map      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|protected
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|getMap
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|==
name|Collection
operator|.
name|NODES
condition|)
block|{
return|return
operator|(
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
operator|)
name|nodes
return|;
block|}
elseif|else
if|if
condition|(
name|collection
operator|==
name|Collection
operator|.
name|CLUSTER_NODES
condition|)
block|{
return|return
operator|(
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
operator|)
name|clusterNodes
return|;
block|}
elseif|else
if|if
condition|(
name|collection
operator|==
name|Collection
operator|.
name|SETTINGS
condition|)
block|{
return|return
operator|(
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
operator|)
name|settings
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown collection: "
operator|+
name|collection
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|CheckForNull
specifier|private
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|internalCreateOrUpdate
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|UpdateOp
name|update
parameter_list|,
name|boolean
name|checkConditions
parameter_list|)
block|{
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|map
init|=
name|getMap
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|T
name|oldDoc
decl_stmt|;
name|Lock
name|lock
init|=
name|rwLock
operator|.
name|writeLock
argument_list|()
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// get the node if it's there
name|oldDoc
operator|=
name|map
operator|.
name|get
argument_list|(
name|update
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|T
name|doc
init|=
name|collection
operator|.
name|newDocument
argument_list|(
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldDoc
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|update
operator|.
name|isNew
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
literal|"Document does not exist: "
operator|+
name|update
operator|.
name|getId
argument_list|()
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|oldDoc
operator|.
name|deepCopy
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|checkConditions
operator|&&
operator|!
name|UpdateUtils
operator|.
name|checkConditions
argument_list|(
name|doc
argument_list|,
name|update
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// update the document
name|UpdateUtils
operator|.
name|applyChanges
argument_list|(
name|doc
argument_list|,
name|update
argument_list|,
name|comparator
argument_list|)
expr_stmt|;
name|doc
operator|.
name|seal
argument_list|()
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|update
operator|.
name|getId
argument_list|()
argument_list|,
name|doc
argument_list|)
expr_stmt|;
return|return
name|oldDoc
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|boolean
name|create
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|updateOps
parameter_list|)
block|{
name|Lock
name|lock
init|=
name|rwLock
operator|.
name|writeLock
argument_list|()
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|map
init|=
name|getMap
argument_list|(
name|collection
argument_list|)
decl_stmt|;
for|for
control|(
name|UpdateOp
name|op
range|:
name|updateOps
control|)
block|{
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
name|op
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
for|for
control|(
name|UpdateOp
name|op
range|:
name|updateOps
control|)
block|{
name|internalCreateOrUpdate
argument_list|(
name|collection
argument_list|,
name|op
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|void
name|update
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|keys
parameter_list|,
name|UpdateOp
name|updateOp
parameter_list|)
block|{
name|Lock
name|lock
init|=
name|rwLock
operator|.
name|writeLock
argument_list|()
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|T
argument_list|>
name|map
init|=
name|getMap
argument_list|(
name|collection
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|keys
control|)
block|{
if|if
condition|(
operator|!
name|map
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|internalCreateOrUpdate
argument_list|(
name|collection
argument_list|,
name|updateOp
operator|.
name|shallowCopy
argument_list|(
name|key
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|"Nodes:\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|p
range|:
name|nodes
operator|.
name|keySet
argument_list|()
control|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"Path: "
argument_list|)
operator|.
name|append
argument_list|(
name|p
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|NodeDocument
name|doc
init|=
name|nodes
operator|.
name|get
argument_list|(
name|p
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|prop
range|:
name|doc
operator|.
name|keySet
argument_list|()
control|)
block|{
name|buff
operator|.
name|append
argument_list|(
name|prop
argument_list|)
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
operator|.
name|append
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|prop
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|CacheInvalidationStats
name|invalidateCache
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|()
block|{
comment|// ignore
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|T
name|getIfCached
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|key
parameter_list|)
block|{
return|return
name|find
argument_list|(
name|collection
argument_list|,
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|Document
parameter_list|>
name|void
name|invalidateCache
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|String
name|key
parameter_list|)
block|{
comment|// ignore
block|}
annotation|@
name|Override
specifier|public
name|void
name|setReadWriteMode
parameter_list|(
name|String
name|readWriteMode
parameter_list|)
block|{
if|if
condition|(
name|readWriteMode
operator|==
literal|null
operator|||
name|readWriteMode
operator|.
name|equals
argument_list|(
name|lastReadWriteMode
argument_list|)
condition|)
block|{
return|return;
block|}
name|lastReadWriteMode
operator|=
name|readWriteMode
expr_stmt|;
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
name|Splitter
operator|.
name|on
argument_list|(
literal|", "
argument_list|)
operator|.
name|withKeyValueSeparator
argument_list|(
literal|":"
argument_list|)
operator|.
name|split
argument_list|(
name|readWriteMode
argument_list|)
decl_stmt|;
name|String
name|read
init|=
name|map
operator|.
name|get
argument_list|(
literal|"read"
argument_list|)
decl_stmt|;
if|if
condition|(
name|read
operator|!=
literal|null
condition|)
block|{
name|ReadPreference
name|readPref
init|=
name|ReadPreference
operator|.
name|valueOf
argument_list|(
name|read
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|readPref
operator|.
name|equals
argument_list|(
name|this
operator|.
name|readPreference
argument_list|)
condition|)
block|{
name|this
operator|.
name|readPreference
operator|=
name|readPref
expr_stmt|;
block|}
block|}
name|String
name|write
init|=
name|map
operator|.
name|get
argument_list|(
literal|"write"
argument_list|)
decl_stmt|;
if|if
condition|(
name|write
operator|!=
literal|null
condition|)
block|{
name|WriteConcern
name|writeConcern
init|=
name|WriteConcern
operator|.
name|valueOf
argument_list|(
name|write
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|writeConcern
operator|.
name|equals
argument_list|(
name|this
operator|.
name|writeConcern
argument_list|)
condition|)
block|{
name|this
operator|.
name|writeConcern
operator|=
name|writeConcern
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// unsupported or parse error - ignore
block|}
block|}
specifier|public
name|ReadPreference
name|getReadPreference
parameter_list|()
block|{
return|return
name|readPreference
return|;
block|}
specifier|public
name|WriteConcern
name|getWriteConcern
parameter_list|()
block|{
return|return
name|writeConcern
return|;
block|}
annotation|@
name|Override
specifier|public
name|CacheStats
name|getCacheStats
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getMetadata
parameter_list|()
block|{
return|return
name|metadata
return|;
block|}
block|}
end_class

end_unit

