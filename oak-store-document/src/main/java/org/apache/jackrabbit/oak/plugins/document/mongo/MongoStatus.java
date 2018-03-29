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
name|mongo
package|;
end_package

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
name|ImmutableSet
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
name|MongoClient
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoQueryException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|ReadConcern
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|MongoCollection
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|MongoCursor
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_class
specifier|public
class|class
name|MongoStatus
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
name|MongoStatus
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|SERVER_DETAIL_FIELD_NAMES
init|=
name|ImmutableSet
operator|.
expr|<
name|String
operator|>
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"host"
argument_list|,
literal|"process"
argument_list|,
literal|"connections"
argument_list|,
literal|"repl"
argument_list|,
literal|"storageEngine"
argument_list|,
literal|"mem"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|MongoClient
name|client
decl_stmt|;
specifier|private
specifier|final
name|String
name|dbName
decl_stmt|;
specifier|private
specifier|final
name|ClusterDescriptionProvider
name|descriptionProvider
decl_stmt|;
specifier|private
name|BasicDBObject
name|serverStatus
decl_stmt|;
specifier|private
name|BasicDBObject
name|buildInfo
decl_stmt|;
specifier|private
name|String
name|version
decl_stmt|;
specifier|private
name|Boolean
name|majorityReadConcernSupported
decl_stmt|;
specifier|private
name|Boolean
name|majorityReadConcernEnabled
decl_stmt|;
specifier|public
name|MongoStatus
parameter_list|(
annotation|@
name|Nonnull
name|MongoClient
name|client
parameter_list|,
annotation|@
name|Nonnull
name|String
name|dbName
parameter_list|)
block|{
name|this
argument_list|(
name|client
argument_list|,
name|dbName
argument_list|,
parameter_list|()
lambda|->
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MongoStatus
parameter_list|(
annotation|@
name|Nonnull
name|MongoClient
name|client
parameter_list|,
annotation|@
name|Nonnull
name|String
name|dbName
parameter_list|,
annotation|@
name|Nonnull
name|ClusterDescriptionProvider
name|descriptionProvider
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|dbName
operator|=
name|dbName
expr_stmt|;
name|this
operator|.
name|descriptionProvider
operator|=
name|descriptionProvider
expr_stmt|;
block|}
specifier|public
name|void
name|checkVersion
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isVersion
argument_list|(
literal|2
argument_list|,
literal|6
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
literal|"MongoDB version 2.6.0 or higher required. "
operator|+
literal|"Currently connected to a MongoDB with version: "
operator|+
name|version
decl_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
comment|/**      * Check if the majority read concern is supported by this storage engine.      * The fact that read concern is supported doesn't it can be used - it also      * has to be enabled.      *      * @return true if the majority read concern is supported      */
specifier|public
name|boolean
name|isMajorityReadConcernSupported
parameter_list|()
block|{
if|if
condition|(
name|majorityReadConcernSupported
operator|==
literal|null
condition|)
block|{
name|BasicDBObject
name|stat
init|=
name|getServerStatus
argument_list|()
decl_stmt|;
if|if
condition|(
name|stat
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"User doesn't have privileges to get server status; falling back to the isMajorityReadConcernEnabled()"
argument_list|)
expr_stmt|;
return|return
name|isMajorityReadConcernEnabled
argument_list|()
return|;
block|}
else|else
block|{
if|if
condition|(
name|stat
operator|.
name|containsField
argument_list|(
literal|"storageEngine"
argument_list|)
condition|)
block|{
name|BasicDBObject
name|storageEngine
init|=
operator|(
name|BasicDBObject
operator|)
name|stat
operator|.
name|get
argument_list|(
literal|"storageEngine"
argument_list|)
decl_stmt|;
name|majorityReadConcernSupported
operator|=
name|storageEngine
operator|.
name|getBoolean
argument_list|(
literal|"supportsCommittedReads"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|majorityReadConcernSupported
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
return|return
name|majorityReadConcernSupported
return|;
block|}
comment|/**      * Check if the majority read concern is enabled and can be used for queries.      *      * @return true if the majority read concern is enabled      */
specifier|public
name|boolean
name|isMajorityReadConcernEnabled
parameter_list|()
block|{
if|if
condition|(
name|majorityReadConcernEnabled
operator|==
literal|null
condition|)
block|{
comment|// Mongo API doesn't seem to provide an option to check whether the
comment|// majority read concern has been enabled, so we have to try to use
comment|// it and optionally catch the exception.
name|MongoCollection
argument_list|<
name|?
argument_list|>
name|emptyCollection
init|=
name|client
operator|.
name|getDatabase
argument_list|(
name|dbName
argument_list|)
operator|.
name|getCollection
argument_list|(
literal|"emptyCollection-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|MongoCursor
name|cursor
init|=
name|emptyCollection
operator|.
name|withReadConcern
argument_list|(
name|ReadConcern
operator|.
name|MAJORITY
argument_list|)
operator|.
name|find
argument_list|(
operator|new
name|BasicDBObject
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
init|)
block|{
name|cursor
operator|.
name|hasNext
argument_list|()
expr_stmt|;
name|majorityReadConcernEnabled
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MongoQueryException
decl||
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|majorityReadConcernEnabled
operator|=
literal|false
expr_stmt|;
block|}
block|}
return|return
name|majorityReadConcernEnabled
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|String
name|getServerDetails
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|details
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|key
range|:
name|SERVER_DETAIL_FIELD_NAMES
control|)
block|{
name|Object
name|value
init|=
name|getServerStatus
argument_list|()
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|details
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|details
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
if|if
condition|(
name|version
operator|==
literal|null
condition|)
block|{
name|String
name|v
init|=
name|getServerStatus
argument_list|()
operator|.
name|getString
argument_list|(
literal|"version"
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
comment|// OAK-4841: serverStatus was probably unauthorized,
comment|// use buildInfo command to get version
name|v
operator|=
name|getBuildInfo
argument_list|()
operator|.
name|getString
argument_list|(
literal|"version"
argument_list|)
expr_stmt|;
block|}
name|version
operator|=
name|v
expr_stmt|;
block|}
return|return
name|version
return|;
block|}
name|boolean
name|isVersion
parameter_list|(
name|int
name|requiredMajor
parameter_list|,
name|int
name|requiredMinor
parameter_list|)
block|{
name|String
name|v
init|=
name|getVersion
argument_list|()
decl_stmt|;
name|Matcher
name|m
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^(\\d+)\\.(\\d+)\\..*"
argument_list|)
operator|.
name|matcher
argument_list|(
name|v
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Malformed MongoDB version: "
operator|+
name|v
argument_list|)
throw|;
block|}
name|int
name|major
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|minor
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|major
operator|>
name|requiredMajor
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|major
operator|==
name|requiredMajor
condition|)
block|{
return|return
name|minor
operator|>=
name|requiredMinor
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|private
name|BasicDBObject
name|getServerStatus
parameter_list|()
block|{
if|if
condition|(
name|serverStatus
operator|==
literal|null
condition|)
block|{
name|serverStatus
operator|=
name|client
operator|.
name|getDatabase
argument_list|(
name|dbName
argument_list|)
operator|.
name|runCommand
argument_list|(
operator|new
name|BasicDBObject
argument_list|(
literal|"serverStatus"
argument_list|,
literal|1
argument_list|)
argument_list|,
name|BasicDBObject
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
return|return
name|serverStatus
return|;
block|}
specifier|private
name|BasicDBObject
name|getBuildInfo
parameter_list|()
block|{
if|if
condition|(
name|buildInfo
operator|==
literal|null
condition|)
block|{
name|buildInfo
operator|=
name|client
operator|.
name|getDatabase
argument_list|(
name|dbName
argument_list|)
operator|.
name|runCommand
argument_list|(
operator|new
name|BasicDBObject
argument_list|(
literal|"buildInfo"
argument_list|,
literal|1
argument_list|)
argument_list|,
name|BasicDBObject
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
return|return
name|buildInfo
return|;
block|}
comment|// for testing purposes
name|void
name|setVersion
parameter_list|(
name|String
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
name|void
name|setServerStatus
parameter_list|(
name|BasicDBObject
name|serverStatus
parameter_list|)
block|{
name|this
operator|.
name|majorityReadConcernSupported
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|serverStatus
operator|=
name|serverStatus
expr_stmt|;
block|}
block|}
end_class

end_unit

