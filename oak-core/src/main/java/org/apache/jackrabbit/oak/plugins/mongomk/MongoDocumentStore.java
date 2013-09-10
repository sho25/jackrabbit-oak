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
name|Arrays
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
name|Callable
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
name|ExecutionException
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
name|cache
operator|.
name|CacheStats
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|cache
operator|.
name|Cache
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|BasicDBObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DB
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBCollection
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBCursor
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|DBObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|QueryBuilder
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
name|com
operator|.
name|mongodb
operator|.
name|WriteResult
import|;
end_import

begin_comment
comment|/**  * A document store that uses MongoDB as the backend.  */
end_comment

begin_class
specifier|public
class|class
name|MongoDocumentStore
implements|implements
name|DocumentStore
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
name|MongoDocumentStore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|LOG_TIME
init|=
literal|false
decl_stmt|;
specifier|private
specifier|final
name|DBCollection
name|nodes
decl_stmt|;
specifier|private
specifier|final
name|DBCollection
name|clusterNodes
decl_stmt|;
comment|/**      * The sum of all milliseconds this class waited for MongoDB.      */
specifier|private
name|long
name|timeSum
decl_stmt|;
specifier|private
specifier|final
name|Cache
argument_list|<
name|String
argument_list|,
name|NodeDocument
argument_list|>
name|nodesCache
decl_stmt|;
specifier|private
specifier|final
name|CacheStats
name|cacheStats
decl_stmt|;
specifier|public
name|MongoDocumentStore
parameter_list|(
name|DB
name|db
parameter_list|,
name|MongoMK
operator|.
name|Builder
name|builder
parameter_list|)
block|{
name|nodes
operator|=
name|db
operator|.
name|getCollection
argument_list|(
name|Collection
operator|.
name|NODES
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|clusterNodes
operator|=
name|db
operator|.
name|getCollection
argument_list|(
name|Collection
operator|.
name|CLUSTER_NODES
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// indexes:
comment|// the _id field is the primary key, so we don't need to define it
name|DBObject
name|index
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
comment|// modification time (descending)
name|index
operator|.
name|put
argument_list|(
name|NodeDocument
operator|.
name|MODIFIED
argument_list|,
operator|-
literal|1L
argument_list|)
expr_stmt|;
name|DBObject
name|options
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"unique"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|ensureIndex
argument_list|(
name|index
argument_list|,
name|options
argument_list|)
expr_stmt|;
comment|// TODO expire entries if the parent was changed
name|nodesCache
operator|=
name|builder
operator|.
name|buildCache
argument_list|(
name|builder
operator|.
name|getDocumentCacheSize
argument_list|()
argument_list|)
expr_stmt|;
name|cacheStats
operator|=
operator|new
name|CacheStats
argument_list|(
name|nodesCache
argument_list|,
literal|"MongoMk-Documents"
argument_list|,
name|builder
operator|.
name|getWeigher
argument_list|()
argument_list|,
name|builder
operator|.
name|getDocumentCacheSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|long
name|start
parameter_list|()
block|{
return|return
name|LOG_TIME
condition|?
name|System
operator|.
name|currentTimeMillis
argument_list|()
else|:
literal|0
return|;
block|}
specifier|private
name|void
name|end
parameter_list|(
name|String
name|message
parameter_list|,
name|long
name|start
parameter_list|)
block|{
if|if
condition|(
name|LOG_TIME
condition|)
block|{
name|long
name|t
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
if|if
condition|(
name|t
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|message
operator|+
literal|": "
operator|+
name|t
argument_list|)
expr_stmt|;
block|}
name|timeSum
operator|+=
name|t
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|finalize
parameter_list|()
throws|throws
name|Throwable
block|{
name|super
operator|.
name|finalize
argument_list|()
expr_stmt|;
comment|// TODO should not be needed, but it seems
comment|// oak-jcr doesn't call dispose()
name|dispose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|invalidateCache
parameter_list|()
block|{
name|nodesCache
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
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
if|if
condition|(
name|collection
operator|==
name|Collection
operator|.
name|NODES
condition|)
block|{
name|nodesCache
operator|.
name|invalidate
argument_list|(
name|key
argument_list|)
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
return|return
name|find
argument_list|(
name|collection
argument_list|,
name|key
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
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
specifier|final
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
specifier|final
name|String
name|key
parameter_list|,
name|int
name|maxCacheAge
parameter_list|)
block|{
if|if
condition|(
name|collection
operator|!=
name|Collection
operator|.
name|NODES
condition|)
block|{
return|return
name|findUncached
argument_list|(
name|collection
argument_list|,
name|key
argument_list|)
return|;
block|}
try|try
block|{
name|NodeDocument
name|doc
decl_stmt|;
if|if
condition|(
name|maxCacheAge
operator|==
literal|0
condition|)
block|{
name|nodesCache
operator|.
name|invalidate
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
name|doc
operator|=
name|nodesCache
operator|.
name|get
argument_list|(
name|key
argument_list|,
operator|new
name|Callable
argument_list|<
name|NodeDocument
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeDocument
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeDocument
name|doc
init|=
operator|(
name|NodeDocument
operator|)
name|findUncached
argument_list|(
name|collection
argument_list|,
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
name|doc
operator|=
name|NodeDocument
operator|.
name|NULL
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxCacheAge
operator|==
literal|0
operator|||
name|maxCacheAge
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|doc
operator|.
name|getCreated
argument_list|()
operator|<
name|maxCacheAge
condition|)
block|{
break|break;
block|}
comment|// too old: invalidate, try again
name|nodesCache
operator|.
name|invalidate
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|doc
operator|==
name|NodeDocument
operator|.
name|NULL
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
comment|//noinspection unchecked
return|return
operator|(
name|T
operator|)
name|doc
return|;
block|}
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Failed to load document with "
operator|+
name|key
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|CheckForNull
argument_list|<
name|T
extends|extends
name|Document
argument_list|>
name|T
name|findUncached
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
name|DBCollection
name|dbCollection
init|=
name|getDBCollection
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|long
name|start
init|=
name|start
argument_list|()
decl_stmt|;
try|try
block|{
name|DBObject
name|obj
init|=
name|dbCollection
operator|.
name|findOne
argument_list|(
name|getByKeyQuery
argument_list|(
name|key
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|T
name|doc
init|=
name|convertFromDBObject
argument_list|(
name|collection
argument_list|,
name|obj
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|seal
argument_list|()
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
finally|finally
block|{
name|end
argument_list|(
literal|"findUncached"
argument_list|,
name|start
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
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
name|Nonnull
annotation|@
name|Override
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
name|log
argument_list|(
literal|"query"
argument_list|,
name|fromKey
argument_list|,
name|toKey
argument_list|,
name|limit
argument_list|)
expr_stmt|;
name|DBCollection
name|dbCollection
init|=
name|getDBCollection
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|QueryBuilder
name|queryBuilder
init|=
name|QueryBuilder
operator|.
name|start
argument_list|(
name|Document
operator|.
name|ID
argument_list|)
decl_stmt|;
name|queryBuilder
operator|.
name|greaterThanEquals
argument_list|(
name|fromKey
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|lessThan
argument_list|(
name|toKey
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexedProperty
operator|!=
literal|null
condition|)
block|{
name|queryBuilder
operator|.
name|and
argument_list|(
name|indexedProperty
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|greaterThanEquals
argument_list|(
name|startValue
argument_list|)
expr_stmt|;
block|}
name|DBObject
name|query
init|=
name|queryBuilder
operator|.
name|get
argument_list|()
decl_stmt|;
name|long
name|start
init|=
name|start
argument_list|()
decl_stmt|;
try|try
block|{
name|DBCursor
name|cursor
init|=
name|dbCollection
operator|.
name|find
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|List
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
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|limit
operator|&&
name|cursor
operator|.
name|hasNext
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|DBObject
name|o
init|=
name|cursor
operator|.
name|next
argument_list|()
decl_stmt|;
name|T
name|doc
init|=
name|convertFromDBObject
argument_list|(
name|collection
argument_list|,
name|o
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|==
name|Collection
operator|.
name|NODES
operator|&&
name|doc
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|seal
argument_list|()
expr_stmt|;
name|nodesCache
operator|.
name|put
argument_list|(
name|doc
operator|.
name|getId
argument_list|()
argument_list|,
operator|(
name|NodeDocument
operator|)
name|doc
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
finally|finally
block|{
name|end
argument_list|(
literal|"query"
argument_list|,
name|start
argument_list|)
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
name|key
parameter_list|)
block|{
name|log
argument_list|(
literal|"remove"
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|DBCollection
name|dbCollection
init|=
name|getDBCollection
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|long
name|start
init|=
name|start
argument_list|()
decl_stmt|;
try|try
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
name|nodesCache
operator|.
name|invalidate
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
name|WriteResult
name|writeResult
init|=
name|dbCollection
operator|.
name|remove
argument_list|(
name|getByKeyQuery
argument_list|(
name|key
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|WriteConcern
operator|.
name|SAFE
argument_list|)
decl_stmt|;
if|if
condition|(
name|writeResult
operator|.
name|getError
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|MicroKernelException
argument_list|(
literal|"Remove failed: "
operator|+
name|writeResult
operator|.
name|getError
argument_list|()
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|end
argument_list|(
literal|"remove"
argument_list|,
name|start
argument_list|)
expr_stmt|;
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
name|findAndModify
parameter_list|(
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
name|UpdateOp
name|updateOp
parameter_list|,
name|boolean
name|upsert
parameter_list|,
name|boolean
name|checkConditions
parameter_list|)
block|{
name|DBCollection
name|dbCollection
init|=
name|getDBCollection
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|QueryBuilder
name|query
init|=
name|getByKeyQuery
argument_list|(
name|updateOp
operator|.
name|key
argument_list|)
decl_stmt|;
name|BasicDBObject
name|setUpdates
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
name|BasicDBObject
name|incUpdates
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
name|BasicDBObject
name|unsetUpdates
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Operation
argument_list|>
name|entry
range|:
name|updateOp
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
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|k
operator|.
name|equals
argument_list|(
name|Document
operator|.
name|ID
argument_list|)
condition|)
block|{
comment|// avoid exception "Mod on _id not allowed"
continue|continue;
block|}
name|Operation
name|op
init|=
name|entry
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
name|setUpdates
operator|.
name|append
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
name|incUpdates
operator|.
name|append
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
name|SET_MAP_ENTRY
case|:
block|{
name|setUpdates
operator|.
name|append
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
name|REMOVE_MAP_ENTRY
case|:
block|{
name|unsetUpdates
operator|.
name|append
argument_list|(
name|k
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
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
name|k
operator|.
name|split
argument_list|(
literal|"\\."
argument_list|)
decl_stmt|;
name|BasicDBObject
name|sub
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
name|sub
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
name|setUpdates
operator|.
name|append
argument_list|(
name|kv
index|[
literal|0
index|]
argument_list|,
name|sub
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|CONTAINS_MAP_ENTRY
case|:
block|{
if|if
condition|(
name|checkConditions
condition|)
block|{
name|query
operator|.
name|and
argument_list|(
name|k
argument_list|)
operator|.
name|exists
argument_list|(
name|op
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
block|}
name|BasicDBObject
name|update
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|setUpdates
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|update
operator|.
name|append
argument_list|(
literal|"$set"
argument_list|,
name|setUpdates
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|incUpdates
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|update
operator|.
name|append
argument_list|(
literal|"$inc"
argument_list|,
name|incUpdates
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|unsetUpdates
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|update
operator|.
name|append
argument_list|(
literal|"$unset"
argument_list|,
name|unsetUpdates
argument_list|)
expr_stmt|;
block|}
comment|// dbCollection.update(query, update, true /*upsert*/, false /*multi*/,
comment|//         WriteConcern.SAFE);
comment|// return null;
name|long
name|start
init|=
name|start
argument_list|()
decl_stmt|;
try|try
block|{
name|DBObject
name|oldNode
init|=
name|dbCollection
operator|.
name|findAndModify
argument_list|(
name|query
operator|.
name|get
argument_list|()
argument_list|,
literal|null
comment|/*fields*/
argument_list|,
literal|null
comment|/*sort*/
argument_list|,
literal|false
comment|/*remove*/
argument_list|,
name|update
argument_list|,
literal|false
comment|/*returnNew*/
argument_list|,
name|upsert
comment|/*upsert*/
argument_list|)
decl_stmt|;
if|if
condition|(
name|checkConditions
operator|&&
name|oldNode
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|T
name|oldDoc
init|=
name|convertFromDBObject
argument_list|(
name|collection
argument_list|,
name|oldNode
argument_list|)
decl_stmt|;
comment|// cache the new document
if|if
condition|(
name|collection
operator|==
name|Collection
operator|.
name|NODES
condition|)
block|{
name|T
name|newDoc
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
operator|!=
literal|null
condition|)
block|{
name|oldDoc
operator|.
name|deepCopy
argument_list|(
name|newDoc
argument_list|)
expr_stmt|;
name|oldDoc
operator|.
name|seal
argument_list|()
expr_stmt|;
block|}
name|String
name|key
init|=
name|updateOp
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|MemoryDocumentStore
operator|.
name|applyChanges
argument_list|(
name|newDoc
argument_list|,
name|updateOp
argument_list|)
expr_stmt|;
name|newDoc
operator|.
name|seal
argument_list|()
expr_stmt|;
name|nodesCache
operator|.
name|put
argument_list|(
name|key
argument_list|,
operator|(
name|NodeDocument
operator|)
name|newDoc
argument_list|)
expr_stmt|;
block|}
return|return
name|oldDoc
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MicroKernelException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|end
argument_list|(
literal|"findAndModify"
argument_list|,
name|start
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
throws|throws
name|MicroKernelException
block|{
name|log
argument_list|(
literal|"createOrUpdate"
argument_list|,
name|update
argument_list|)
expr_stmt|;
name|T
name|doc
init|=
name|findAndModify
argument_list|(
name|collection
argument_list|,
name|update
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|log
argument_list|(
literal|"createOrUpdate returns "
argument_list|,
name|doc
argument_list|)
expr_stmt|;
return|return
name|doc
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
name|log
argument_list|(
literal|"findAndUpdate"
argument_list|,
name|update
argument_list|)
expr_stmt|;
name|T
name|doc
init|=
name|findAndModify
argument_list|(
name|collection
argument_list|,
name|update
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|log
argument_list|(
literal|"findAndUpdate returns "
argument_list|,
name|doc
argument_list|)
expr_stmt|;
return|return
name|doc
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
name|log
argument_list|(
literal|"create"
argument_list|,
name|updateOps
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|T
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
name|DBObject
index|[]
name|inserts
init|=
operator|new
name|DBObject
index|[
name|updateOps
operator|.
name|size
argument_list|()
index|]
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
name|updateOps
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|inserts
index|[
name|i
index|]
operator|=
operator|new
name|BasicDBObject
argument_list|()
expr_stmt|;
name|UpdateOp
name|update
init|=
name|updateOps
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|T
name|target
init|=
name|collection
operator|.
name|newDocument
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|MemoryDocumentStore
operator|.
name|applyChanges
argument_list|(
name|target
argument_list|,
name|update
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|target
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Operation
argument_list|>
name|entry
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
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Operation
name|op
init|=
name|entry
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
case|case
name|INCREMENT
case|:
block|{
name|inserts
index|[
name|i
index|]
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
name|SET_MAP
case|:
case|case
name|SET_MAP_ENTRY
case|:
block|{
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
name|DBObject
name|value
init|=
operator|new
name|BasicDBObject
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
decl_stmt|;
name|inserts
index|[
name|i
index|]
operator|.
name|put
argument_list|(
name|kv
index|[
literal|0
index|]
argument_list|,
name|value
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|REMOVE_MAP_ENTRY
case|:
comment|// nothing to do for new entries
break|break;
case|case
name|CONTAINS_MAP_ENTRY
case|:
comment|// no effect
break|break;
block|}
block|}
block|}
name|DBCollection
name|dbCollection
init|=
name|getDBCollection
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|long
name|start
init|=
name|start
argument_list|()
decl_stmt|;
try|try
block|{
try|try
block|{
name|WriteResult
name|writeResult
init|=
name|dbCollection
operator|.
name|insert
argument_list|(
name|inserts
argument_list|,
name|WriteConcern
operator|.
name|SAFE
argument_list|)
decl_stmt|;
if|if
condition|(
name|writeResult
operator|.
name|getError
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|collection
operator|==
name|Collection
operator|.
name|NODES
condition|)
block|{
for|for
control|(
name|T
name|doc
range|:
name|docs
control|)
block|{
name|doc
operator|.
name|seal
argument_list|()
expr_stmt|;
name|nodesCache
operator|.
name|put
argument_list|(
name|doc
operator|.
name|getId
argument_list|()
argument_list|,
operator|(
name|NodeDocument
operator|)
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|MongoException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
finally|finally
block|{
name|end
argument_list|(
literal|"create"
argument_list|,
name|start
argument_list|)
expr_stmt|;
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
name|convertFromDBObject
parameter_list|(
annotation|@
name|Nonnull
name|Collection
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|,
annotation|@
name|Nullable
name|DBObject
name|n
parameter_list|)
block|{
name|T
name|copy
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|n
operator|!=
literal|null
condition|)
block|{
name|copy
operator|=
name|collection
operator|.
name|newDocument
argument_list|(
name|this
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|key
range|:
name|n
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Object
name|o
init|=
name|n
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|String
condition|)
block|{
name|copy
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|Long
condition|)
block|{
name|copy
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|BasicDBObject
condition|)
block|{
name|copy
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|copy
return|;
block|}
specifier|private
name|DBCollection
name|getDBCollection
parameter_list|(
name|Collection
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
specifier|private
specifier|static
name|QueryBuilder
name|getByKeyQuery
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|QueryBuilder
operator|.
name|start
argument_list|(
name|Document
operator|.
name|ID
argument_list|)
operator|.
name|is
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|()
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"MongoDB time: "
operator|+
name|timeSum
argument_list|)
expr_stmt|;
block|}
name|nodes
operator|.
name|getDB
argument_list|()
operator|.
name|getMongo
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|CacheStats
name|getCacheStats
parameter_list|()
block|{
return|return
name|cacheStats
return|;
block|}
specifier|private
specifier|static
name|void
name|log
parameter_list|(
name|String
name|message
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|String
name|argList
init|=
name|Arrays
operator|.
name|toString
argument_list|(
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|argList
operator|.
name|length
argument_list|()
operator|>
literal|10000
condition|)
block|{
name|argList
operator|=
name|argList
operator|.
name|length
argument_list|()
operator|+
literal|": "
operator|+
name|argList
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
name|message
operator|+
name|argList
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|collection
operator|!=
name|Collection
operator|.
name|NODES
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|nodesCache
operator|.
name|getIfPresent
argument_list|(
name|key
argument_list|)
operator|!=
literal|null
return|;
block|}
block|}
end_class

end_unit

