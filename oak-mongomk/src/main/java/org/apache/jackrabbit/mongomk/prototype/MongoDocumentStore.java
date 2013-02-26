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
name|mongomk
operator|.
name|prototype
operator|.
name|MongoMK
operator|.
name|Cache
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
name|UpdateOp
operator|.
name|Operation
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
specifier|public
specifier|static
specifier|final
name|String
name|KEY_PATH
init|=
literal|"_id"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|LOG
init|=
literal|false
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|LOG_TIME
init|=
literal|true
decl_stmt|;
specifier|private
specifier|final
name|DBCollection
name|nodesCollection
decl_stmt|;
specifier|private
name|long
name|time
decl_stmt|;
specifier|private
name|Cache
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
name|cache
init|=
operator|new
name|Cache
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
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
specifier|public
name|MongoDocumentStore
parameter_list|(
name|DB
name|db
parameter_list|)
block|{
name|nodesCollection
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
name|ensureIndex
argument_list|()
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
name|long
name|start
parameter_list|)
block|{
if|if
condition|(
name|LOG_TIME
condition|)
block|{
name|time
operator|+=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|finalize
parameter_list|()
block|{
comment|// TODO should not be needed, but it seems
comment|// oak-jcr doesn't call dispose()
name|dispose
argument_list|()
expr_stmt|;
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
name|find
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|result
decl_stmt|;
synchronized|synchronized
init|(
name|cache
init|)
block|{
name|result
operator|=
name|cache
operator|.
name|get
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
name|log
argument_list|(
literal|"find"
argument_list|,
name|path
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
name|DBObject
name|doc
init|=
name|dbCollection
operator|.
name|findOne
argument_list|(
name|getByPathQuery
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|result
operator|=
name|convertFromDBObject
argument_list|(
name|doc
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|cache
init|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
finally|finally
block|{
name|end
argument_list|(
name|start
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
name|KEY_PATH
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
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|convertFromDBObject
argument_list|(
name|o
argument_list|)
decl_stmt|;
name|String
name|path
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"_id"
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|cache
init|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|map
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
name|path
parameter_list|)
block|{
name|log
argument_list|(
literal|"remove"
argument_list|,
name|path
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
synchronized|synchronized
init|(
name|cache
init|)
block|{
name|cache
operator|.
name|remove
argument_list|(
name|path
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
name|getByPathQuery
argument_list|(
name|path
argument_list|)
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
name|start
argument_list|)
expr_stmt|;
block|}
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
name|createOrUpdate
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|UpdateOp
name|updateOp
parameter_list|)
block|{
name|log
argument_list|(
literal|"createOrUpdate"
argument_list|,
name|updateOp
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
name|ADD_MAP_ENTRY
case|:
block|{
name|setUpdates
operator|.
name|append
argument_list|(
name|k
operator|+
literal|"."
operator|+
name|op
operator|.
name|subKey
operator|.
name|toString
argument_list|()
argument_list|,
name|op
operator|.
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|REMOVE_MAP_ENTRY
case|:
block|{
comment|// TODO
break|break;
block|}
block|}
block|}
name|DBObject
name|query
init|=
name|getByPathQuery
argument_list|(
name|updateOp
operator|.
name|key
argument_list|)
decl_stmt|;
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
comment|//        dbCollection.update(query, update, true /*upsert*/, false /*multi*/,
comment|//                WriteConcern.SAFE);
comment|//        return null;
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
literal|true
comment|/*returnNew*/
argument_list|,
literal|true
comment|/*upsert*/
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
name|convertFromDBObject
argument_list|(
name|oldNode
argument_list|)
decl_stmt|;
name|String
name|path
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"_id"
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|cache
init|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
return|return
name|map
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
name|start
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
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
name|log
argument_list|(
literal|"create"
argument_list|,
name|updateOps
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|maps
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
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|target
init|=
name|Utils
operator|.
name|newMap
argument_list|()
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
name|maps
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
name|ADD_MAP_ENTRY
case|:
block|{
name|DBObject
name|value
init|=
operator|new
name|BasicDBObject
argument_list|(
name|op
operator|.
name|subKey
operator|.
name|toString
argument_list|()
argument_list|,
name|op
operator|.
name|value
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|inserts
index|[
name|i
index|]
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|value
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|REMOVE_MAP_ENTRY
case|:
block|{
comment|// TODO
break|break;
block|}
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
throw|throw
operator|new
name|MicroKernelException
argument_list|(
literal|"Batch create failed: "
operator|+
name|writeResult
operator|.
name|getError
argument_list|()
argument_list|)
throw|;
block|}
synchronized|synchronized
init|(
name|cache
init|)
block|{
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
range|:
name|maps
control|)
block|{
name|String
name|path
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"_id"
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|cache
init|)
block|{
name|cache
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|end
argument_list|(
name|start
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|ensureIndex
parameter_list|()
block|{
comment|// the _id field is the primary key, so we don't need to define it
comment|// the following code is just a template in case we need more indexes
comment|// DBObject index = new BasicDBObject();
comment|// index.put(KEY_PATH, 1L);
comment|// DBObject options = new BasicDBObject();
comment|// options.put("unique", Boolean.TRUE);
comment|// nodesCollection.ensureIndex(index, options);
block|}
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|convertFromDBObject
parameter_list|(
name|DBObject
name|n
parameter_list|)
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
if|if
condition|(
name|n
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|n
init|)
block|{
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
name|copy
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|n
operator|.
name|get
argument_list|(
name|key
argument_list|)
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
switch|switch
condition|(
name|collection
condition|)
block|{
case|case
name|NODES
case|:
return|return
name|nodesCollection
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
specifier|private
specifier|static
name|DBObject
name|getByPathQuery
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|QueryBuilder
operator|.
name|start
argument_list|(
name|KEY_PATH
argument_list|)
operator|.
name|is
argument_list|(
name|path
argument_list|)
operator|.
name|get
argument_list|()
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
name|LOG_TIME
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MongoDB time: "
operator|+
name|time
argument_list|)
expr_stmt|;
block|}
name|nodesCollection
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
specifier|private
specifier|static
name|void
name|log
parameter_list|(
name|Object
modifier|...
name|args
parameter_list|)
block|{
if|if
condition|(
name|LOG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

