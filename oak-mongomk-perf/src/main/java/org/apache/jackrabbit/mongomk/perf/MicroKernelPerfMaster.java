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
name|perf
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|blobs
operator|.
name|BlobStore
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
name|api
operator|.
name|NodeStore
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
name|impl
operator|.
name|MongoConnection
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
name|impl
operator|.
name|MongoMicroKernel
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
name|impl
operator|.
name|MongoNodeStore
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
name|impl
operator|.
name|json
operator|.
name|DefaultJsopHandler
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
name|impl
operator|.
name|json
operator|.
name|JsopParser
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
name|impl
operator|.
name|model
operator|.
name|MongoCommit
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
name|impl
operator|.
name|model
operator|.
name|MongoSync
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
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|JSONArray
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|JSONObject
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

begin_class
specifier|public
class|class
name|MicroKernelPerfMaster
block|{
specifier|private
class|class
name|ContinousHandler
extends|extends
name|DefaultJsopHandler
block|{
specifier|private
specifier|final
name|JSONObject
name|jsonObject
decl_stmt|;
specifier|private
name|ContinousHandler
parameter_list|()
block|{
name|this
operator|.
name|jsonObject
operator|=
operator|new
name|JSONObject
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|nodeAdded
parameter_list|(
name|String
name|parentPath
parameter_list|,
name|String
name|name
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|JSONObject
name|parent
init|=
name|this
operator|.
name|getObjectByPath
argument_list|(
name|parentPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|parent
operator|.
name|has
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|parent
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|JSONObject
argument_list|()
argument_list|)
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
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertySet
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|JSONObject
name|element
init|=
name|this
operator|.
name|getObjectByPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|element
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
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|JSONObject
name|getObjectByPath
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|JSONObject
name|jsonObject
init|=
name|this
operator|.
name|jsonObject
decl_stmt|;
if|if
condition|(
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
for|for
control|(
name|String
name|segment
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Checking segment %s of path %s in object %s"
argument_list|,
name|segment
argument_list|,
name|path
argument_list|,
name|jsonObject
argument_list|)
argument_list|)
expr_stmt|;
name|jsonObject
operator|=
name|jsonObject
operator|.
name|optJSONObject
argument_list|(
name|segment
argument_list|)
expr_stmt|;
if|if
condition|(
name|jsonObject
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"The path %s was not found in the current state"
argument_list|,
name|path
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|jsonObject
return|;
block|}
block|}
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|MicroKernelPerfMaster
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Config
name|config
decl_stmt|;
specifier|private
name|ContinousHandler
name|handler
decl_stmt|;
specifier|private
name|long
name|lastCommitRevId
decl_stmt|;
specifier|private
name|long
name|lastHeadRevId
decl_stmt|;
specifier|private
name|long
name|lastRevId
decl_stmt|;
specifier|private
name|MongoMicroKernel
name|microKernel
decl_stmt|;
specifier|private
name|MongoConnection
name|mongoConnection
decl_stmt|;
specifier|public
name|MicroKernelPerfMaster
parameter_list|(
name|Config
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|initMongo
argument_list|()
expr_stmt|;
name|this
operator|.
name|initMicroKernel
argument_list|()
expr_stmt|;
name|this
operator|.
name|initHandler
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting server..."
argument_list|)
expr_stmt|;
name|this
operator|.
name|startVerifying
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|initHandler
parameter_list|()
block|{
name|this
operator|.
name|handler
operator|=
operator|new
name|ContinousHandler
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|initMicroKernel
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeStore
name|nodeStore
init|=
operator|new
name|MongoNodeStore
argument_list|(
name|mongoConnection
operator|.
name|getDB
argument_list|()
argument_list|)
decl_stmt|;
name|BlobStore
name|blobStore
init|=
operator|new
name|BlobStoreFS
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|)
decl_stmt|;
name|microKernel
operator|=
operator|new
name|MongoMicroKernel
argument_list|(
name|mongoConnection
argument_list|,
name|nodeStore
argument_list|,
name|blobStore
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|initMongo
parameter_list|()
throws|throws
name|Exception
block|{
name|this
operator|.
name|mongoConnection
operator|=
operator|new
name|MongoConnection
argument_list|(
name|this
operator|.
name|config
operator|.
name|getMongoHost
argument_list|()
argument_list|,
name|this
operator|.
name|config
operator|.
name|getMongoPort
argument_list|()
argument_list|,
name|this
operator|.
name|config
operator|.
name|getMongoDatabase
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|startVerifying
parameter_list|()
throws|throws
name|Exception
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|List
argument_list|<
name|MongoCommit
argument_list|>
name|commitMongos
init|=
name|this
operator|.
name|waitForCommit
argument_list|()
decl_stmt|;
for|for
control|(
name|MongoCommit
name|commitMongo
range|:
name|commitMongos
control|)
block|{
if|if
condition|(
name|commitMongo
operator|.
name|isFailed
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Skipping commit %d because it failed"
argument_list|,
name|commitMongo
operator|.
name|getRevisionId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|lastRevId
operator|=
name|commitMongo
operator|.
name|getRevisionId
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Verifying commit %d"
argument_list|,
name|commitMongo
operator|.
name|getRevisionId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|verifyCommit
argument_list|(
name|commitMongo
argument_list|)
expr_stmt|;
name|this
operator|.
name|verifyCommitOrder
argument_list|(
name|commitMongo
argument_list|)
expr_stmt|;
name|this
operator|.
name|lastRevId
operator|=
name|commitMongo
operator|.
name|getRevisionId
argument_list|()
expr_stmt|;
name|this
operator|.
name|lastCommitRevId
operator|=
name|commitMongo
operator|.
name|getRevisionId
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|void
name|verifyCommit
parameter_list|(
name|MongoCommit
name|commitMongo
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|path
init|=
name|commitMongo
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|jsop
init|=
name|commitMongo
operator|.
name|getDiff
argument_list|()
decl_stmt|;
name|JsopParser
name|jsopParser
init|=
operator|new
name|JsopParser
argument_list|(
name|path
argument_list|,
name|jsop
argument_list|,
name|this
operator|.
name|handler
argument_list|)
decl_stmt|;
name|jsopParser
operator|.
name|parse
argument_list|()
expr_stmt|;
name|String
name|json
init|=
name|this
operator|.
name|microKernel
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|commitMongo
operator|.
name|getRevisionId
argument_list|()
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|JSONObject
name|resultJson
init|=
operator|new
name|JSONObject
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|this
operator|.
name|verifyEquality
argument_list|(
name|this
operator|.
name|handler
operator|.
name|jsonObject
argument_list|,
name|resultJson
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Successfully verified commit %d"
argument_list|,
name|commitMongo
operator|.
name|getRevisionId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|verifyCommitOrder
parameter_list|(
name|MongoCommit
name|commitMongo
parameter_list|)
throws|throws
name|Exception
block|{
name|long
name|baseRevId
init|=
name|commitMongo
operator|.
name|getBaseRevisionId
argument_list|()
decl_stmt|;
name|long
name|revId
init|=
name|commitMongo
operator|.
name|getRevisionId
argument_list|()
decl_stmt|;
if|if
condition|(
name|baseRevId
operator|!=
name|this
operator|.
name|lastCommitRevId
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Revision %d has a base revision of %d but last successful commit was %d"
argument_list|,
name|revId
argument_list|,
name|baseRevId
argument_list|,
name|this
operator|.
name|lastCommitRevId
argument_list|)
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|verifyEquality
parameter_list|(
name|JSONObject
name|expected
parameter_list|,
name|JSONObject
name|actual
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Verifying for equality %s (expected) vs %s (actual)"
argument_list|,
name|expected
argument_list|,
name|actual
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|expected
operator|.
name|length
argument_list|()
operator|!=
operator|(
name|actual
operator|.
name|length
argument_list|()
operator|-
literal|1
operator|)
condition|)
block|{
comment|// substract 1 b/c of :childCount
throw|throw
operator|new
name|Exception
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Unequal number of children/properties: %d (expected) vs %d (actual)"
argument_list|,
name|expected
operator|.
name|length
argument_list|()
argument_list|,
name|actual
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
throw|;
block|}
name|JSONArray
name|expectedNames
init|=
name|expected
operator|.
name|names
argument_list|()
decl_stmt|;
if|if
condition|(
name|expectedNames
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expectedNames
operator|.
name|length
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|String
name|name
init|=
name|expectedNames
operator|.
name|getString
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Object
name|expectedValue
init|=
name|expected
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|Object
name|actualValue
init|=
name|actual
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|expectedValue
operator|instanceof
name|JSONObject
operator|)
operator|&&
operator|(
name|actualValue
operator|instanceof
name|JSONObject
operator|)
condition|)
block|{
name|this
operator|.
name|verifyEquality
argument_list|(
operator|(
name|JSONObject
operator|)
name|expectedValue
argument_list|,
operator|(
name|JSONObject
operator|)
name|actualValue
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|expectedValue
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|actualValue
operator|!=
literal|null
operator|)
condition|)
block|{
if|if
condition|(
operator|!
name|expectedValue
operator|.
name|equals
argument_list|(
name|actualValue
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Key %s: Expected value '%s' does not macht actual value '%s'"
argument_list|,
name|name
argument_list|,
name|expectedValue
argument_list|,
name|actualValue
argument_list|)
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|expectedValue
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Key %s: Did not find an actual value for expected value '%s'"
argument_list|,
name|name
argument_list|,
name|expectedValue
argument_list|)
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|actualValue
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Key %s: Did not find an expected value for actual value '%s'"
argument_list|,
name|name
argument_list|,
name|actualValue
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Verificytion for equality failed: %s (expected) vs %s (actual)"
argument_list|,
name|expected
argument_list|,
name|actual
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
specifier|private
name|List
argument_list|<
name|MongoCommit
argument_list|>
name|waitForCommit
parameter_list|()
block|{
comment|// TODO Change this to MicroKernel#waitForCommit
name|List
argument_list|<
name|MongoCommit
argument_list|>
name|commitMongos
init|=
operator|new
name|LinkedList
argument_list|<
name|MongoCommit
argument_list|>
argument_list|()
decl_stmt|;
name|this
operator|.
name|lastHeadRevId
operator|=
literal|0L
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Waiting for commit..."
argument_list|)
expr_stmt|;
name|DBCollection
name|headCollection
init|=
operator|(
operator|(
name|MongoNodeStore
operator|)
name|microKernel
operator|.
name|getNodeStore
argument_list|()
operator|)
operator|.
name|getSyncCollection
argument_list|()
decl_stmt|;
name|MongoSync
name|syncMongo
init|=
operator|(
name|MongoSync
operator|)
name|headCollection
operator|.
name|findOne
argument_list|()
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|lastHeadRevId
operator|<
name|syncMongo
operator|.
name|getHeadRevisionId
argument_list|()
condition|)
block|{
name|DBCollection
name|commitCollection
init|=
operator|(
operator|(
name|MongoNodeStore
operator|)
name|microKernel
operator|.
name|getNodeStore
argument_list|()
operator|)
operator|.
name|getCommitCollection
argument_list|()
decl_stmt|;
name|DBObject
name|query
init|=
name|QueryBuilder
operator|.
name|start
argument_list|(
name|MongoCommit
operator|.
name|KEY_REVISION_ID
argument_list|)
operator|.
name|greaterThan
argument_list|(
name|this
operator|.
name|lastRevId
argument_list|)
operator|.
name|and
argument_list|(
name|MongoCommit
operator|.
name|KEY_REVISION_ID
argument_list|)
operator|.
name|lessThanEquals
argument_list|(
name|syncMongo
operator|.
name|getHeadRevisionId
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|DBObject
name|sort
init|=
name|QueryBuilder
operator|.
name|start
argument_list|(
name|MongoCommit
operator|.
name|KEY_REVISION_ID
argument_list|)
operator|.
name|is
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|DBCursor
name|dbCursor
init|=
name|commitCollection
operator|.
name|find
argument_list|(
name|query
argument_list|)
operator|.
name|sort
argument_list|(
name|sort
argument_list|)
decl_stmt|;
while|while
condition|(
name|dbCursor
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|commitMongos
operator|.
name|add
argument_list|(
operator|(
name|MongoCommit
operator|)
name|dbCursor
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|commitMongos
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Found %d new commits"
argument_list|,
name|commitMongos
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
name|this
operator|.
name|lastHeadRevId
operator|=
name|syncMongo
operator|.
name|getHeadRevisionId
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// noop
block|}
block|}
return|return
name|commitMongos
return|;
block|}
block|}
end_class

end_unit

