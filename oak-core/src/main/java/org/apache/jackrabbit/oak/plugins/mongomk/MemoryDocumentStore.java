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
name|Map
operator|.
name|Entry
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
name|oak
operator|.
name|plugins
operator|.
name|mongomk
operator|.
name|UpdateOp
operator|.
name|Operation
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
specifier|private
specifier|final
name|ReadWriteLock
name|rwLock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
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
name|Long
name|value
init|=
operator|(
name|Long
operator|)
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
operator|<
name|startValue
condition|)
block|{
continue|continue;
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
name|void
name|remove
parameter_list|(
name|Collection
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
throws|throws
name|MicroKernelException
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
throws|throws
name|MicroKernelException
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
specifier|private
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
name|key
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
condition|)
block|{
throw|throw
operator|new
name|MicroKernelException
argument_list|(
literal|"Document does not exist: "
operator|+
name|update
operator|.
name|key
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
name|applyChanges
argument_list|(
name|doc
argument_list|,
name|update
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
name|key
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
specifier|private
specifier|static
name|boolean
name|checkConditions
parameter_list|(
name|Document
name|doc
parameter_list|,
name|UpdateOp
name|update
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
name|Operation
argument_list|>
name|change
range|:
name|update
operator|.
name|changes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Operation
name|op
init|=
name|change
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|op
operator|.
name|type
operator|==
name|Operation
operator|.
name|Type
operator|.
name|CONTAINS_MAP_ENTRY
condition|)
block|{
name|String
name|k
init|=
name|change
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
index|[]
name|kv
init|=
name|k
operator|.
name|split
argument_list|(
literal|"\\."
argument_list|)
decl_stmt|;
name|Object
name|value
init|=
name|doc
operator|.
name|get
argument_list|(
name|kv
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|Boolean
operator|.
name|TRUE
operator|.
name|equals
argument_list|(
name|op
operator|.
name|value
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|value
operator|instanceof
name|Map
condition|)
block|{
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|map
init|=
operator|(
name|Map
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
operator|)
name|value
decl_stmt|;
if|if
condition|(
name|Boolean
operator|.
name|TRUE
operator|.
name|equals
argument_list|(
name|op
operator|.
name|value
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|map
operator|.
name|containsKey
argument_list|(
name|kv
index|[
literal|1
index|]
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
name|kv
index|[
literal|1
index|]
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Apply the changes to the in-memory document.      *       * @param doc the target document.      * @param update the changes to apply      */
specifier|public
specifier|static
name|void
name|applyChanges
parameter_list|(
name|Document
name|doc
parameter_list|,
name|UpdateOp
name|update
parameter_list|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Operation
argument_list|>
name|e
range|:
name|update
operator|.
name|changes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|k
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Operation
name|op
init|=
name|e
operator|.
name|getValue
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|op
operator|.
name|type
condition|)
block|{
case|case
name|SET
case|:
block|{
name|doc
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|op
operator|.
name|value
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|INCREMENT
case|:
block|{
name|Object
name|old
init|=
name|doc
operator|.
name|get
argument_list|(
name|k
argument_list|)
decl_stmt|;
name|Long
name|x
init|=
operator|(
name|Long
operator|)
name|op
operator|.
name|value
decl_stmt|;
if|if
condition|(
name|old
operator|==
literal|null
condition|)
block|{
name|old
operator|=
literal|0L
expr_stmt|;
block|}
name|doc
operator|.
name|put
argument_list|(
name|k
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|old
operator|)
operator|+
name|x
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|SET_MAP_ENTRY
case|:
block|{
name|String
index|[]
name|kv
init|=
name|splitInTwo
argument_list|(
name|k
argument_list|,
literal|'.'
argument_list|)
decl_stmt|;
name|Object
name|old
init|=
name|doc
operator|.
name|get
argument_list|(
name|kv
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
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
name|m
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|old
decl_stmt|;
if|if
condition|(
name|m
operator|==
literal|null
condition|)
block|{
name|m
operator|=
name|Utils
operator|.
name|newMap
argument_list|()
expr_stmt|;
name|doc
operator|.
name|put
argument_list|(
name|kv
index|[
literal|0
index|]
argument_list|,
name|m
argument_list|)
expr_stmt|;
block|}
name|m
operator|.
name|put
argument_list|(
name|kv
index|[
literal|1
index|]
argument_list|,
name|op
operator|.
name|value
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|REMOVE_MAP_ENTRY
case|:
block|{
name|String
index|[]
name|kv
init|=
name|splitInTwo
argument_list|(
name|k
argument_list|,
literal|'.'
argument_list|)
decl_stmt|;
name|Object
name|old
init|=
name|doc
operator|.
name|get
argument_list|(
name|kv
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
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
name|m
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|old
decl_stmt|;
if|if
condition|(
name|m
operator|!=
literal|null
condition|)
block|{
name|m
operator|.
name|remove
argument_list|(
name|kv
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
case|case
name|SET_MAP
case|:
block|{
name|String
index|[]
name|kv
init|=
name|splitInTwo
argument_list|(
name|k
argument_list|,
literal|'.'
argument_list|)
decl_stmt|;
name|Object
name|old
init|=
name|doc
operator|.
name|get
argument_list|(
name|kv
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
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
name|m
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|old
decl_stmt|;
if|if
condition|(
name|m
operator|==
literal|null
condition|)
block|{
name|m
operator|=
name|Utils
operator|.
name|newMap
argument_list|()
expr_stmt|;
name|doc
operator|.
name|put
argument_list|(
name|kv
index|[
literal|0
index|]
argument_list|,
name|m
argument_list|)
expr_stmt|;
block|}
name|m
operator|.
name|put
argument_list|(
name|kv
index|[
literal|1
index|]
argument_list|,
name|op
operator|.
name|value
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|CONTAINS_MAP_ENTRY
case|:
comment|// no effect
break|break;
block|}
block|}
block|}
specifier|private
specifier|static
name|String
index|[]
name|splitInTwo
parameter_list|(
name|String
name|s
parameter_list|,
name|char
name|separator
parameter_list|)
block|{
name|int
name|index
init|=
name|s
operator|.
name|indexOf
argument_list|(
name|separator
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
return|return
operator|new
name|String
index|[]
block|{
name|s
block|}
return|;
block|}
return|return
operator|new
name|String
index|[]
block|{
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|index
argument_list|)
block|,
name|s
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|)
block|}
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
name|key
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
name|void
name|invalidateCache
parameter_list|()
block|{
comment|// there is no cache, so nothing to invalidate
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
name|boolean
name|isCached
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|String
name|key
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|invalidateCache
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|String
name|key
parameter_list|)
block|{
comment|// ignore
block|}
block|}
end_class

end_unit

