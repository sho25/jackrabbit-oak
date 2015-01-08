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
name|com
operator|.
name|mongodb
operator|.
name|WriteResult
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
name|NodeDocument
operator|.
name|SplitDocType
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
specifier|final
name|MongoDocumentStore
name|store
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
name|lastModifiedTime
parameter_list|)
block|{
comment|//_deletedOnce == true&& _modified< lastModifiedTime
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
name|lessThan
argument_list|(
name|NodeDocument
operator|.
name|getModifiedInSecs
argument_list|(
name|lastModifiedTime
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
name|oldestRevTimeStamp
argument_list|,
name|stats
argument_list|)
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
specifier|protected
name|MongoSplitDocCleanUp
parameter_list|(
name|Set
argument_list|<
name|SplitDocType
argument_list|>
name|gcTypes
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
name|gcTypes
argument_list|,
name|oldestRevTimeStamp
argument_list|,
name|stats
argument_list|)
expr_stmt|;
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
argument_list|()
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
name|WriteResult
name|writeResult
init|=
name|getNodeCollection
argument_list|()
operator|.
name|remove
argument_list|(
name|query
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
comment|//TODO This might be temporary error or we fail fast and let next cycle try again
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error occurred while deleting old split documents from Mongo {}"
argument_list|,
name|writeResult
operator|.
name|getError
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|writeResult
operator|.
name|getN
argument_list|()
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
parameter_list|()
block|{
return|return
name|transform
argument_list|(
name|getNodeCollection
argument_list|()
operator|.
name|find
argument_list|(
name|createQuery
argument_list|()
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
return|;
block|}
specifier|private
name|DBObject
name|createQuery
parameter_list|()
block|{
comment|//OR condition has to be first as we have a index for that
comment|//((type == DEFAULT_NO_CHILD || type == PROP_COMMIT_ONLY ..)&& _sdMaxRevTime< oldestRevTimeStamp(in secs)
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
name|orClause
operator|.
name|or
argument_list|(
name|start
argument_list|(
name|NodeDocument
operator|.
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
expr_stmt|;
block|}
return|return
name|start
argument_list|()
operator|.
name|and
argument_list|(
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
block|}
block|}
end_class

end_unit

