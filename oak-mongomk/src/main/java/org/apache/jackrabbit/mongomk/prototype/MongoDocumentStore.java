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
name|String
name|KEY_PATH
init|=
literal|"_path"
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
name|MongoDocumentStore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|DBCollection
name|nodesCollection
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
name|DBObject
name|n
init|=
name|getNode
argument_list|(
name|collection
argument_list|,
name|path
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
return|return
name|convertFromDBObject
argument_list|(
name|n
argument_list|)
return|;
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
name|DBCollection
name|dbCollection
init|=
name|getDBCollection
argument_list|(
name|collection
argument_list|)
decl_stmt|;
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Remove failed: {}"
argument_list|,
name|writeResult
operator|.
name|getError
argument_list|()
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
operator|(
name|Long
operator|)
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
literal|false
comment|/*returnNew*/
argument_list|,
literal|true
comment|/*upsert*/
argument_list|)
decl_stmt|;
return|return
name|convertFromDBObject
argument_list|(
name|oldNode
argument_list|)
return|;
block|}
specifier|private
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
return|return
name|copy
return|;
block|}
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
name|DBObject
name|getNode
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|String
name|path
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
return|return
name|dbCollection
operator|.
name|findOne
argument_list|(
name|getByPathQuery
argument_list|(
name|path
argument_list|)
argument_list|)
return|;
block|}
specifier|private
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
block|}
end_class

end_unit

