begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Set
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
name|TimeUnit
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
name|base
operator|.
name|Function
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
name|Joiner
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
name|Predicate
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
name|StandardSystemProperty
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
name|ImmutableList
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
name|Iterables
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
name|Lists
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
name|ReadPreference
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
name|RevisionVector
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
name|SplitDocumentCleanUp
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
name|VersionGCSupport
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
name|VersionGarbageCollector
operator|.
name|VersionGCStats
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
name|util
operator|.
name|CloseableIterable
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
name|util
operator|.
name|Utils
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
name|stats
operator|.
name|Clock
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
operator|.
name|filter
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
operator|.
name|transform
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|mongodb
operator|.
name|QueryBuilder
operator|.
name|start
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonList
import|;
end_import

begin_import
import|import static
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
operator|.
name|NODES
import|;
end_import

begin_import
import|import static
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
operator|.
name|ID
import|;
end_import

begin_import
import|import static
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
operator|.
name|PATH
import|;
end_import

begin_import
import|import static
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
operator|.
name|SD_MAX_REV_TIME_IN_SECS
import|;
end_import

begin_import
import|import static
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
operator|.
name|SD_TYPE
import|;
end_import

begin_import
import|import static
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
operator|.
name|SplitDocType
import|;
end_import

begin_import
import|import static
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
operator|.
name|SplitDocType
operator|.
name|DEFAULT_NO_BRANCH
import|;
end_import

begin_import
import|import static
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
operator|.
name|getModifiedInSecs
import|;
end_import

begin_comment
comment|/**  * Mongo specific version of VersionGCSupport which uses mongo queries  * to fetch required NodeDocuments  *  *<p>Version collection involves looking into old record and mostly unmodified  * documents. In such case read from secondaries are preferred</p>  */
end_comment

begin_class
specifier|public
class|class
name|MongoVersionGCSupport
extends|extends
name|VersionGCSupport
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
name|MongoVersionGCSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|DBObject
name|SD_TYPE_HINT
init|=
name|start
argument_list|(
name|SD_TYPE
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
specifier|private
specifier|final
name|MongoDocumentStore
name|store
decl_stmt|;
comment|/**      * The batch size for the query of possibly deleted docs.      */
specifier|private
specifier|final
name|int
name|batchSize
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"oak.mongo.queryDeletedDocsBatchSize"
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
specifier|public
name|MongoVersionGCSupport
parameter_list|(
name|MongoDocumentStore
name|store
parameter_list|)
block|{
name|super
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|CloseableIterable
argument_list|<
name|NodeDocument
argument_list|>
name|getPossiblyDeletedDocs
parameter_list|(
specifier|final
name|long
name|fromModified
parameter_list|,
specifier|final
name|long
name|toModified
parameter_list|)
block|{
comment|//_deletedOnce == true&& _modified>= fromModified&& _modified< toModified
name|DBObject
name|query
init|=
name|start
argument_list|(
name|NodeDocument
operator|.
name|DELETED_ONCE
argument_list|)
operator|.
name|is
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
operator|.
name|put
argument_list|(
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
argument_list|)
operator|.
name|greaterThanEquals
argument_list|(
name|NodeDocument
operator|.
name|getModifiedInSecs
argument_list|(
name|fromModified
argument_list|)
argument_list|)
operator|.
name|lessThan
argument_list|(
name|NodeDocument
operator|.
name|getModifiedInSecs
argument_list|(
name|toModified
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|DBCursor
name|cursor
init|=
name|getNodeCollection
argument_list|()
operator|.
name|find
argument_list|(
name|query
argument_list|)
operator|.
name|setReadPreference
argument_list|(
name|ReadPreference
operator|.
name|secondaryPreferred
argument_list|()
argument_list|)
decl_stmt|;
name|cursor
operator|.
name|batchSize
argument_list|(
name|batchSize
argument_list|)
expr_stmt|;
return|return
name|CloseableIterable
operator|.
name|wrap
argument_list|(
name|transform
argument_list|(
name|cursor
argument_list|,
operator|new
name|Function
argument_list|<
name|DBObject
argument_list|,
name|NodeDocument
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeDocument
name|apply
parameter_list|(
name|DBObject
name|input
parameter_list|)
block|{
return|return
name|store
operator|.
name|convertFromDBObject
argument_list|(
name|NODES
argument_list|,
name|input
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|,
name|cursor
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getDeletedOnceCount
parameter_list|()
block|{
name|DBObject
name|query
init|=
name|start
argument_list|(
name|NodeDocument
operator|.
name|DELETED_ONCE
argument_list|)
operator|.
name|is
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
return|return
name|getNodeCollection
argument_list|()
operator|.
name|count
argument_list|(
name|query
argument_list|,
name|ReadPreference
operator|.
name|secondaryPreferred
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|SplitDocumentCleanUp
name|createCleanUp
parameter_list|(
name|Set
argument_list|<
name|SplitDocType
argument_list|>
name|gcTypes
parameter_list|,
name|RevisionVector
name|sweepRevs
parameter_list|,
name|long
name|oldestRevTimeStamp
parameter_list|,
name|VersionGCStats
name|stats
parameter_list|)
block|{
return|return
operator|new
name|MongoSplitDocCleanUp
argument_list|(
name|gcTypes
argument_list|,
name|sweepRevs
argument_list|,
name|oldestRevTimeStamp
argument_list|,
name|stats
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Iterable
argument_list|<
name|NodeDocument
argument_list|>
name|identifyGarbage
parameter_list|(
specifier|final
name|Set
argument_list|<
name|SplitDocType
argument_list|>
name|gcTypes
parameter_list|,
specifier|final
name|RevisionVector
name|sweepRevs
parameter_list|,
specifier|final
name|long
name|oldestRevTimeStamp
parameter_list|)
block|{
return|return
name|filter
argument_list|(
name|transform
argument_list|(
name|getNodeCollection
argument_list|()
operator|.
name|find
argument_list|(
name|createQuery
argument_list|(
name|gcTypes
argument_list|,
name|sweepRevs
argument_list|,
name|oldestRevTimeStamp
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Function
argument_list|<
name|DBObject
argument_list|,
name|NodeDocument
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeDocument
name|apply
parameter_list|(
name|DBObject
name|input
parameter_list|)
block|{
return|return
name|store
operator|.
name|convertFromDBObject
argument_list|(
name|NODES
argument_list|,
name|input
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|,
operator|new
name|Predicate
argument_list|<
name|NodeDocument
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|NodeDocument
name|input
parameter_list|)
block|{
return|return
operator|!
name|isDefaultNoBranchSplitNewerThan
argument_list|(
name|input
argument_list|,
name|sweepRevs
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getOldestDeletedOnceTimestamp
parameter_list|(
name|Clock
name|clock
parameter_list|,
name|long
name|precisionMs
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"getOldestDeletedOnceTimestamp()<- start"
argument_list|)
expr_stmt|;
name|DBObject
name|query
init|=
name|start
argument_list|(
name|NodeDocument
operator|.
name|DELETED_ONCE
argument_list|)
operator|.
name|is
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|DBCursor
name|cursor
init|=
name|getNodeCollection
argument_list|()
operator|.
name|find
argument_list|(
name|query
argument_list|)
operator|.
name|sort
argument_list|(
name|start
argument_list|(
name|NodeDocument
operator|.
name|MODIFIED_IN_SECS
argument_list|)
operator|.
name|is
argument_list|(
literal|1
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|limit
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|CloseableIterable
argument_list|<
name|NodeDocument
argument_list|>
name|results
init|=
name|CloseableIterable
operator|.
name|wrap
argument_list|(
name|transform
argument_list|(
name|cursor
argument_list|,
operator|new
name|Function
argument_list|<
name|DBObject
argument_list|,
name|NodeDocument
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeDocument
name|apply
parameter_list|(
name|DBObject
name|input
parameter_list|)
block|{
return|return
name|store
operator|.
name|convertFromDBObject
argument_list|(
name|NODES
argument_list|,
name|input
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|,
name|cursor
argument_list|)
decl_stmt|;
try|try
block|{
name|Iterator
argument_list|<
name|NodeDocument
argument_list|>
name|i
init|=
name|results
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|NodeDocument
name|doc
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|long
name|modifiedMs
init|=
name|doc
operator|.
name|getModified
argument_list|()
operator|*
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
decl_stmt|;
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
literal|"getOldestDeletedOnceTimestamp() -> {}"
argument_list|,
name|Utils
operator|.
name|timestampToString
argument_list|(
name|modifiedMs
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|modifiedMs
return|;
block|}
block|}
finally|finally
block|{
name|Utils
operator|.
name|closeIfCloseable
argument_list|(
name|results
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"getOldestDeletedOnceTimestamp() -> none found, return current time"
argument_list|)
expr_stmt|;
return|return
name|clock
operator|.
name|getTime
argument_list|()
return|;
block|}
specifier|private
name|DBObject
name|createQuery
parameter_list|(
name|Set
argument_list|<
name|SplitDocType
argument_list|>
name|gcTypes
parameter_list|,
name|RevisionVector
name|sweepRevs
parameter_list|,
name|long
name|oldestRevTimeStamp
parameter_list|)
block|{
name|QueryBuilder
name|orClause
init|=
name|start
argument_list|()
decl_stmt|;
for|for
control|(
name|SplitDocType
name|type
range|:
name|gcTypes
control|)
block|{
for|for
control|(
name|DBObject
name|query
range|:
name|queriesForType
argument_list|(
name|type
argument_list|,
name|sweepRevs
argument_list|)
control|)
block|{
name|orClause
operator|.
name|or
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|start
argument_list|()
operator|.
name|and
argument_list|(
name|start
argument_list|(
name|SD_TYPE
argument_list|)
operator|.
name|exists
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|orClause
operator|.
name|get
argument_list|()
argument_list|,
name|start
argument_list|(
name|NodeDocument
operator|.
name|SD_MAX_REV_TIME_IN_SECS
argument_list|)
operator|.
name|lessThan
argument_list|(
name|NodeDocument
operator|.
name|getModifiedInSecs
argument_list|(
name|oldestRevTimeStamp
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|private
name|Iterable
argument_list|<
name|DBObject
argument_list|>
name|queriesForType
parameter_list|(
name|SplitDocType
name|type
parameter_list|,
name|RevisionVector
name|sweepRevs
parameter_list|)
block|{
if|if
condition|(
name|type
operator|!=
name|DEFAULT_NO_BRANCH
condition|)
block|{
return|return
name|singletonList
argument_list|(
name|start
argument_list|(
name|SD_TYPE
argument_list|)
operator|.
name|is
argument_list|(
name|type
operator|.
name|typeCode
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
comment|// default split type is special because we can only remove those
comment|// older than sweep rev
name|List
argument_list|<
name|DBObject
argument_list|>
name|queries
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Revision
name|r
range|:
name|sweepRevs
control|)
block|{
name|String
name|idSuffix
init|=
name|Utils
operator|.
name|getPreviousIdFor
argument_list|(
literal|"/"
argument_list|,
name|r
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|idSuffix
operator|=
name|idSuffix
operator|.
name|substring
argument_list|(
name|idSuffix
operator|.
name|lastIndexOf
argument_list|(
literal|'-'
argument_list|)
argument_list|)
expr_stmt|;
comment|// id/path constraint for previous documents
name|QueryBuilder
name|idPathClause
init|=
name|start
argument_list|()
decl_stmt|;
name|idPathClause
operator|.
name|or
argument_list|(
name|start
argument_list|(
name|ID
argument_list|)
operator|.
name|regex
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|".*"
operator|+
name|idSuffix
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|// previous documents with long paths do not have a '-' in the id
name|idPathClause
operator|.
name|or
argument_list|(
name|start
argument_list|(
name|ID
argument_list|)
operator|.
name|regex
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[^-]*"
argument_list|)
argument_list|)
operator|.
name|and
argument_list|(
name|PATH
argument_list|)
operator|.
name|regex
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|".*"
operator|+
name|idSuffix
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|queries
operator|.
name|add
argument_list|(
name|start
argument_list|(
name|SD_TYPE
argument_list|)
operator|.
name|is
argument_list|(
name|type
operator|.
name|typeCode
argument_list|()
argument_list|)
operator|.
name|and
argument_list|(
name|idPathClause
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|and
argument_list|(
name|SD_MAX_REV_TIME_IN_SECS
argument_list|)
operator|.
name|lessThan
argument_list|(
name|getModifiedInSecs
argument_list|(
name|r
operator|.
name|getTimestamp
argument_list|()
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|queries
return|;
block|}
specifier|private
name|void
name|logSplitDocIdsTobeDeleted
parameter_list|(
name|DBObject
name|query
parameter_list|)
block|{
comment|// Fetch only the id
specifier|final
name|BasicDBObject
name|keys
init|=
operator|new
name|BasicDBObject
argument_list|(
name|Document
operator|.
name|ID
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|ids
decl_stmt|;
name|DBCursor
name|cursor
init|=
name|getNodeCollection
argument_list|()
operator|.
name|find
argument_list|(
name|query
argument_list|,
name|keys
argument_list|)
operator|.
name|setReadPreference
argument_list|(
name|store
operator|.
name|getConfiguredReadPreference
argument_list|(
name|NODES
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|ids
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|cursor
argument_list|,
operator|new
name|Function
argument_list|<
name|DBObject
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|DBObject
name|input
parameter_list|)
block|{
return|return
operator|(
name|String
operator|)
name|input
operator|.
name|get
argument_list|(
name|Document
operator|.
name|ID
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|cursor
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Split documents with following ids were deleted as part of GC \n"
argument_list|)
decl_stmt|;
name|Joiner
operator|.
name|on
argument_list|(
name|StandardSystemProperty
operator|.
name|LINE_SEPARATOR
operator|.
name|value
argument_list|()
argument_list|)
operator|.
name|appendTo
argument_list|(
name|sb
argument_list|,
name|ids
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|DBCollection
name|getNodeCollection
parameter_list|()
block|{
return|return
name|store
operator|.
name|getDBCollection
argument_list|(
name|NODES
argument_list|)
return|;
block|}
specifier|private
class|class
name|MongoSplitDocCleanUp
extends|extends
name|SplitDocumentCleanUp
block|{
specifier|final
name|Set
argument_list|<
name|SplitDocType
argument_list|>
name|gcTypes
decl_stmt|;
specifier|final
name|RevisionVector
name|sweepRevs
decl_stmt|;
specifier|final
name|long
name|oldestRevTimeStamp
decl_stmt|;
name|MongoSplitDocCleanUp
parameter_list|(
name|Set
argument_list|<
name|SplitDocType
argument_list|>
name|gcTypes
parameter_list|,
name|RevisionVector
name|sweepRevs
parameter_list|,
name|long
name|oldestRevTimeStamp
parameter_list|,
name|VersionGCStats
name|stats
parameter_list|)
block|{
name|super
argument_list|(
name|MongoVersionGCSupport
operator|.
name|this
operator|.
name|store
argument_list|,
name|stats
argument_list|,
name|identifyGarbage
argument_list|(
name|gcTypes
argument_list|,
name|sweepRevs
argument_list|,
name|oldestRevTimeStamp
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|gcTypes
operator|=
name|gcTypes
expr_stmt|;
name|this
operator|.
name|sweepRevs
operator|=
name|sweepRevs
expr_stmt|;
name|this
operator|.
name|oldestRevTimeStamp
operator|=
name|oldestRevTimeStamp
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|collectIdToBeDeleted
parameter_list|(
name|String
name|id
parameter_list|)
block|{
comment|// nothing to do here, as we're overwriting deleteSplitDocuments()
block|}
annotation|@
name|Override
specifier|protected
name|int
name|deleteSplitDocuments
parameter_list|()
block|{
name|DBObject
name|query
init|=
name|createQuery
argument_list|(
name|gcTypes
argument_list|,
name|sweepRevs
argument_list|,
name|oldestRevTimeStamp
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
comment|//if debug level logging is on then determine the id of documents to be deleted
comment|//and log them
name|logSplitDocIdsTobeDeleted
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
return|return
name|getNodeCollection
argument_list|()
operator|.
name|remove
argument_list|(
name|query
argument_list|)
operator|.
name|getN
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

