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
name|impl
package|;
end_package

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
name|Collections
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|util
operator|.
name|SimpleLRUCache
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
name|api
operator|.
name|command
operator|.
name|Command
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
name|command
operator|.
name|CommandExecutor
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
name|model
operator|.
name|Commit
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
name|model
operator|.
name|Node
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
name|action
operator|.
name|FetchCommitAction
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
name|command
operator|.
name|CommitCommandNew
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
name|command
operator|.
name|DefaultCommandExecutor
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
name|command
operator|.
name|DiffCommand
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
name|command
operator|.
name|GetHeadRevisionCommand
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
name|command
operator|.
name|GetJournalCommand
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
name|command
operator|.
name|GetNodesCommandNew
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
name|command
operator|.
name|GetRevisionHistoryCommand
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
name|command
operator|.
name|MergeCommand
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
name|command
operator|.
name|NodeExistsCommand
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
name|command
operator|.
name|WaitForCommitCommand
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
name|MongoNode
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
name|mongomk
operator|.
name|util
operator|.
name|MongoUtil
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

begin_comment
comment|/**  * Implementation of {@link NodeStore} for the {@code MongoDB}.  */
end_comment

begin_class
specifier|public
class|class
name|MongoNodeStore
implements|implements
name|NodeStore
block|{
specifier|public
specifier|static
specifier|final
name|String
name|INITIAL_COMMIT_MESSAGE
init|=
literal|"This is an autogenerated initial commit"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|INITIAL_COMMIT_PATH
init|=
literal|""
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|INITIAL_COMMIT_DIFF
init|=
literal|"+\"/\" : {}"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|COLLECTION_COMMITS
init|=
literal|"commits"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|COLLECTION_NODES
init|=
literal|"nodes"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|COLLECTION_SYNC
init|=
literal|"sync"
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
name|MongoNodeStore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|CommandExecutor
name|commandExecutor
decl_stmt|;
specifier|private
specifier|final
name|DB
name|db
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|Long
argument_list|,
name|MongoCommit
argument_list|>
name|commitCache
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
name|SimpleLRUCache
operator|.
expr|<
name|Long
argument_list|,
name|MongoCommit
operator|>
name|newInstance
argument_list|(
literal|1000
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|MongoNode
argument_list|>
name|nodeCache
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
name|SimpleLRUCache
operator|.
expr|<
name|String
argument_list|,
name|MongoNode
operator|>
name|newInstance
argument_list|(
literal|10000
argument_list|)
argument_list|)
decl_stmt|;
comment|/**      * Constructs a new {@code NodeStoreMongo}.      *      * @param db Mongo DB.      */
specifier|public
name|MongoNodeStore
parameter_list|(
name|DB
name|db
parameter_list|)
block|{
name|commandExecutor
operator|=
operator|new
name|DefaultCommandExecutor
argument_list|()
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|commit
parameter_list|(
name|Commit
name|commit
parameter_list|)
throws|throws
name|Exception
block|{
name|Command
argument_list|<
name|Long
argument_list|>
name|command
init|=
operator|new
name|CommitCommandNew
argument_list|(
name|this
argument_list|,
name|commit
argument_list|)
decl_stmt|;
name|Long
name|revisionId
init|=
name|commandExecutor
operator|.
name|execute
argument_list|(
name|command
argument_list|)
decl_stmt|;
return|return
name|MongoUtil
operator|.
name|fromMongoRepresentation
argument_list|(
name|revisionId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|diff
parameter_list|(
name|String
name|fromRevision
parameter_list|,
name|String
name|toRevision
parameter_list|,
name|String
name|path
parameter_list|,
name|int
name|depth
parameter_list|)
throws|throws
name|Exception
block|{
name|Command
argument_list|<
name|String
argument_list|>
name|command
init|=
operator|new
name|DiffCommand
argument_list|(
name|this
argument_list|,
name|fromRevision
argument_list|,
name|toRevision
argument_list|,
name|path
argument_list|,
name|depth
argument_list|)
decl_stmt|;
return|return
name|commandExecutor
operator|.
name|execute
argument_list|(
name|command
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getHeadRevision
parameter_list|()
throws|throws
name|Exception
block|{
name|GetHeadRevisionCommand
name|command
init|=
operator|new
name|GetHeadRevisionCommand
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|long
name|revisionId
init|=
name|commandExecutor
operator|.
name|execute
argument_list|(
name|command
argument_list|)
decl_stmt|;
return|return
name|MongoUtil
operator|.
name|fromMongoRepresentation
argument_list|(
name|revisionId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|getNodes
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|revisionId
parameter_list|,
name|int
name|depth
parameter_list|,
name|long
name|offset
parameter_list|,
name|int
name|maxChildNodes
parameter_list|,
name|String
name|filter
parameter_list|)
throws|throws
name|Exception
block|{
name|GetNodesCommandNew
name|command
init|=
operator|new
name|GetNodesCommandNew
argument_list|(
name|this
argument_list|,
name|path
argument_list|,
name|MongoUtil
operator|.
name|toMongoRepresentation
argument_list|(
name|revisionId
argument_list|)
argument_list|)
decl_stmt|;
name|command
operator|.
name|setBranchId
argument_list|(
name|getBranchId
argument_list|(
name|revisionId
argument_list|)
argument_list|)
expr_stmt|;
name|command
operator|.
name|setDepth
argument_list|(
name|depth
argument_list|)
expr_stmt|;
return|return
name|commandExecutor
operator|.
name|execute
argument_list|(
name|command
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|merge
parameter_list|(
name|String
name|branchRevisionId
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|MergeCommand
name|command
init|=
operator|new
name|MergeCommand
argument_list|(
name|this
argument_list|,
name|branchRevisionId
argument_list|,
name|message
argument_list|)
decl_stmt|;
return|return
name|commandExecutor
operator|.
name|execute
argument_list|(
name|command
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|nodeExists
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|revisionId
parameter_list|)
throws|throws
name|Exception
block|{
name|NodeExistsCommand
name|command
init|=
operator|new
name|NodeExistsCommand
argument_list|(
name|this
argument_list|,
name|path
argument_list|,
name|MongoUtil
operator|.
name|toMongoRepresentation
argument_list|(
name|revisionId
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|branchId
init|=
name|getBranchId
argument_list|(
name|revisionId
argument_list|)
decl_stmt|;
name|command
operator|.
name|setBranchId
argument_list|(
name|branchId
argument_list|)
expr_stmt|;
return|return
name|commandExecutor
operator|.
name|execute
argument_list|(
name|command
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getJournal
parameter_list|(
name|String
name|fromRevisionId
parameter_list|,
name|String
name|toRevisionId
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|GetJournalCommand
name|command
init|=
operator|new
name|GetJournalCommand
argument_list|(
name|this
argument_list|,
name|fromRevisionId
argument_list|,
name|toRevisionId
argument_list|,
name|path
argument_list|)
decl_stmt|;
return|return
name|commandExecutor
operator|.
name|execute
argument_list|(
name|command
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRevisionHistory
parameter_list|(
name|long
name|since
parameter_list|,
name|int
name|maxEntries
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|GetRevisionHistoryCommand
name|command
init|=
operator|new
name|GetRevisionHistoryCommand
argument_list|(
name|this
argument_list|,
name|since
argument_list|,
name|maxEntries
argument_list|,
name|path
argument_list|)
decl_stmt|;
return|return
name|commandExecutor
operator|.
name|execute
argument_list|(
name|command
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|waitForCommit
parameter_list|(
name|String
name|oldHeadRevisionId
parameter_list|,
name|long
name|timeout
parameter_list|)
throws|throws
name|Exception
block|{
name|WaitForCommitCommand
name|command
init|=
operator|new
name|WaitForCommitCommand
argument_list|(
name|this
argument_list|,
name|oldHeadRevisionId
argument_list|,
name|timeout
argument_list|)
decl_stmt|;
name|long
name|revisionId
init|=
name|commandExecutor
operator|.
name|execute
argument_list|(
name|command
argument_list|)
decl_stmt|;
return|return
name|MongoUtil
operator|.
name|fromMongoRepresentation
argument_list|(
name|revisionId
argument_list|)
return|;
block|}
comment|/**      * Returns the commit {@link DBCollection}.      *      * @return The commit {@link DBCollection}.      */
specifier|public
name|DBCollection
name|getCommitCollection
parameter_list|()
block|{
name|DBCollection
name|commitCollection
init|=
name|db
operator|.
name|getCollection
argument_list|(
name|COLLECTION_COMMITS
argument_list|)
decl_stmt|;
name|commitCollection
operator|.
name|setObjectClass
argument_list|(
name|MongoCommit
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|commitCollection
return|;
block|}
comment|/**      * Returns the sync {@link DBCollection}.      *      * @return The sync {@link DBCollection}.      */
specifier|public
name|DBCollection
name|getSyncCollection
parameter_list|()
block|{
name|DBCollection
name|syncCollection
init|=
name|db
operator|.
name|getCollection
argument_list|(
name|COLLECTION_SYNC
argument_list|)
decl_stmt|;
name|syncCollection
operator|.
name|setObjectClass
argument_list|(
name|MongoSync
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|syncCollection
return|;
block|}
comment|/**      * Returns the node {@link DBCollection}.      *      * @return The node {@link DBCollection}.      */
specifier|public
name|DBCollection
name|getNodeCollection
parameter_list|()
block|{
name|DBCollection
name|nodeCollection
init|=
name|db
operator|.
name|getCollection
argument_list|(
name|COLLECTION_NODES
argument_list|)
decl_stmt|;
name|nodeCollection
operator|.
name|setObjectClass
argument_list|(
name|MongoNode
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|nodeCollection
return|;
block|}
comment|/**      * Caches the commit.      *      * @param commit Commit to cache.      */
specifier|public
name|void
name|cache
parameter_list|(
name|Commit
name|commit
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Adding commit {} to cache"
argument_list|,
name|commit
operator|.
name|getRevisionId
argument_list|()
argument_list|)
expr_stmt|;
name|commitCache
operator|.
name|put
argument_list|(
name|commit
operator|.
name|getRevisionId
argument_list|()
argument_list|,
operator|(
name|MongoCommit
operator|)
name|commit
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the commit from the cache or null if the commit is not in the cache.      *      * @param revisionId Commit revision id.      * @return Commit from cache or null if commit is not in the cache.      */
specifier|public
name|MongoCommit
name|getFromCache
parameter_list|(
name|long
name|revisionId
parameter_list|)
block|{
name|MongoCommit
name|commit
init|=
name|commitCache
operator|.
name|get
argument_list|(
name|revisionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|commit
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Returning commit {} from cache"
argument_list|,
name|revisionId
argument_list|)
expr_stmt|;
block|}
return|return
name|commit
return|;
block|}
comment|/**      * Caches the node.      *      * @param node Node to cache.      */
specifier|public
name|void
name|cache
parameter_list|(
name|MongoNode
name|node
parameter_list|)
block|{
name|long
name|revisionId
init|=
name|node
operator|.
name|getRevisionId
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|node
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|branchId
init|=
name|node
operator|.
name|getBranchId
argument_list|()
decl_stmt|;
name|String
name|key
init|=
name|path
operator|+
literal|"@"
operator|+
name|branchId
operator|+
literal|"@"
operator|+
name|revisionId
decl_stmt|;
if|if
condition|(
operator|!
name|nodeCache
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Adding node to cache: {}"
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|nodeCache
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns the node from the cache or null if the node is not in the cache.      *      * @param path Path      * @param branchId Branch id      * @param revisionId Revision id      * @return      */
specifier|public
name|MongoNode
name|getFromCache
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|branchId
parameter_list|,
name|long
name|revisionId
parameter_list|)
block|{
name|String
name|key
init|=
name|path
operator|+
literal|"@"
operator|+
name|branchId
operator|+
literal|"@"
operator|+
name|revisionId
decl_stmt|;
name|MongoNode
name|node
init|=
name|nodeCache
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Returning node from cache: {}"
argument_list|,
name|key
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
specifier|private
name|void
name|init
parameter_list|()
block|{
name|initCommitCollection
argument_list|()
expr_stmt|;
name|initNodeCollection
argument_list|()
expr_stmt|;
name|initSyncCollection
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|initCommitCollection
parameter_list|()
block|{
if|if
condition|(
name|db
operator|.
name|collectionExists
argument_list|(
name|COLLECTION_COMMITS
argument_list|)
condition|)
block|{
return|return;
block|}
name|DBCollection
name|commitCollection
init|=
name|getCommitCollection
argument_list|()
decl_stmt|;
name|DBObject
name|index
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
name|index
operator|.
name|put
argument_list|(
name|MongoCommit
operator|.
name|KEY_REVISION_ID
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|index
operator|.
name|put
argument_list|(
name|MongoCommit
operator|.
name|KEY_BRANCH_ID
argument_list|,
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
name|TRUE
argument_list|)
expr_stmt|;
name|commitCollection
operator|.
name|ensureIndex
argument_list|(
name|index
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|MongoCommit
name|commit
init|=
operator|new
name|MongoCommit
argument_list|()
decl_stmt|;
name|commit
operator|.
name|setAffectedPaths
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"/"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setBaseRevisionId
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setDiff
argument_list|(
name|INITIAL_COMMIT_DIFF
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setMessage
argument_list|(
name|INITIAL_COMMIT_MESSAGE
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setRevisionId
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setPath
argument_list|(
name|INITIAL_COMMIT_PATH
argument_list|)
expr_stmt|;
name|commitCollection
operator|.
name|insert
argument_list|(
name|commit
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|initNodeCollection
parameter_list|()
block|{
if|if
condition|(
name|db
operator|.
name|collectionExists
argument_list|(
name|COLLECTION_NODES
argument_list|)
condition|)
block|{
return|return;
block|}
name|DBCollection
name|nodeCollection
init|=
name|getNodeCollection
argument_list|()
decl_stmt|;
name|DBObject
name|index
init|=
operator|new
name|BasicDBObject
argument_list|()
decl_stmt|;
name|index
operator|.
name|put
argument_list|(
name|MongoNode
operator|.
name|KEY_PATH
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|index
operator|.
name|put
argument_list|(
name|MongoNode
operator|.
name|KEY_REVISION_ID
argument_list|,
operator|-
literal|1L
argument_list|)
expr_stmt|;
name|index
operator|.
name|put
argument_list|(
name|MongoNode
operator|.
name|KEY_BRANCH_ID
argument_list|,
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
name|TRUE
argument_list|)
expr_stmt|;
name|nodeCollection
operator|.
name|ensureIndex
argument_list|(
name|index
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|MongoNode
name|root
init|=
operator|new
name|MongoNode
argument_list|()
decl_stmt|;
name|root
operator|.
name|setRevisionId
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|root
operator|.
name|setPath
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|nodeCollection
operator|.
name|insert
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|initSyncCollection
parameter_list|()
block|{
if|if
condition|(
name|db
operator|.
name|collectionExists
argument_list|(
name|COLLECTION_SYNC
argument_list|)
condition|)
block|{
return|return;
block|}
name|DBCollection
name|headCollection
init|=
name|getSyncCollection
argument_list|()
decl_stmt|;
name|MongoSync
name|headMongo
init|=
operator|new
name|MongoSync
argument_list|()
decl_stmt|;
name|headMongo
operator|.
name|setHeadRevisionId
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|headMongo
operator|.
name|setNextRevisionId
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
name|headCollection
operator|.
name|insert
argument_list|(
name|headMongo
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|getBranchId
parameter_list|(
name|String
name|revisionId
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|revisionId
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|MongoCommit
name|baseCommit
init|=
operator|new
name|FetchCommitAction
argument_list|(
name|this
argument_list|,
name|MongoUtil
operator|.
name|toMongoRepresentation
argument_list|(
name|revisionId
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
decl_stmt|;
return|return
name|baseCommit
operator|.
name|getBranchId
argument_list|()
return|;
block|}
block|}
end_class

end_unit

