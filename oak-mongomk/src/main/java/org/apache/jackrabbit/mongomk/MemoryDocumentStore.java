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
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|nodes
init|=
operator|new
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * The 'clusterNodes' collection.      */
specifier|private
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|clusterNodes
init|=
operator|new
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|find
parameter_list|(
name|Collection
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
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|find
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|map
init|=
name|getMap
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|n
init|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|copy
init|=
name|Utils
operator|.
name|newMap
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|n
init|)
block|{
name|Utils
operator|.
name|deepCopyMap
argument_list|(
name|n
argument_list|,
name|copy
argument_list|)
expr_stmt|;
block|}
return|return
name|copy
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|query
parameter_list|(
name|Collection
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
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
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
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
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
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|n
range|:
name|sub
operator|.
name|values
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|copy
init|=
name|Utils
operator|.
name|newMap
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|n
init|)
block|{
name|Utils
operator|.
name|deepCopyMap
argument_list|(
name|n
argument_list|,
name|copy
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|copy
argument_list|)
expr_stmt|;
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|>
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
comment|/**      * Get the in-memory map for this collection.      *      * @param collection the collection      * @return the map      */
specifier|private
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|getMap
parameter_list|(
name|Collection
name|collection
parameter_list|)
block|{
switch|switch
condition|(
name|collection
condition|)
block|{
case|case
name|NODES
case|:
return|return
name|nodes
return|;
case|case
name|CLUSTER_NODES
case|:
return|return
name|clusterNodes
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|collection
operator|.
name|name
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|CheckForNull
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|internalCreateOrUpdate
parameter_list|(
name|Collection
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
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|map
init|=
name|getMap
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|n
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|oldNode
decl_stmt|;
comment|// get the node if it's there
name|oldNode
operator|=
name|n
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
if|if
condition|(
name|n
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
comment|// for a new node, add it (without synchronization)
name|n
operator|=
name|Utils
operator|.
name|newMap
argument_list|()
expr_stmt|;
name|oldNode
operator|=
name|map
operator|.
name|putIfAbsent
argument_list|(
name|update
operator|.
name|key
argument_list|,
name|n
argument_list|)
expr_stmt|;
if|if
condition|(
name|oldNode
operator|!=
literal|null
condition|)
block|{
comment|// somebody else added it at the same time
name|n
operator|=
name|oldNode
expr_stmt|;
block|}
block|}
synchronized|synchronized
init|(
name|n
init|)
block|{
if|if
condition|(
name|checkConditions
operator|&&
operator|!
name|checkConditions
argument_list|(
name|n
argument_list|,
name|update
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|oldNode
operator|!=
literal|null
condition|)
block|{
comment|// clone the old node
comment|// (document level operations are synchronized)
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|oldNode2
init|=
name|Utils
operator|.
name|newMap
argument_list|()
decl_stmt|;
name|Utils
operator|.
name|deepCopyMap
argument_list|(
name|oldNode
argument_list|,
name|oldNode2
argument_list|)
expr_stmt|;
name|oldNode
operator|=
name|oldNode2
expr_stmt|;
block|}
comment|// to return the new document:
comment|// update the document
comment|// (document level operations are synchronized)
name|applyChanges
argument_list|(
name|n
argument_list|,
name|update
argument_list|)
expr_stmt|;
block|}
return|return
name|oldNode
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|createOrUpdate
parameter_list|(
name|Collection
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
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|findAndUpdate
parameter_list|(
name|Collection
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
specifier|private
specifier|static
name|boolean
name|checkConditions
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|target
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
name|target
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
name|java
operator|.
name|util
operator|.
name|Collection
condition|)
block|{
name|java
operator|.
name|util
operator|.
name|Collection
argument_list|<
name|?
argument_list|>
name|col
init|=
operator|(
name|java
operator|.
name|util
operator|.
name|Collection
argument_list|<
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
name|col
operator|.
name|contains
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
name|col
operator|.
name|contains
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
comment|/**      * Apply the changes to the in-memory map.      *       * @param target the target map      * @param update the changes to apply      */
specifier|public
specifier|static
name|void
name|applyChanges
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|target
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
name|target
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
name|target
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
name|target
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
name|Object
name|old
init|=
name|target
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
name|target
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
name|Object
name|old
init|=
name|target
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
name|Object
name|old
init|=
name|target
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
name|target
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
annotation|@
name|Override
specifier|public
name|boolean
name|create
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|List
argument_list|<
name|UpdateOp
argument_list|>
name|updateOps
parameter_list|)
block|{
name|ConcurrentSkipListMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
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
name|createOrUpdate
argument_list|(
name|collection
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
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
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|e
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
name|e
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
name|e
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
block|}
end_class

end_unit

