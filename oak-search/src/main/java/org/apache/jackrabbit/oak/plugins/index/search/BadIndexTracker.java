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
name|index
operator|.
name|search
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Stopwatch
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
name|Throwables
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
name|Ticker
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

begin_comment
comment|/**  * Track of bad (corrupt) indexes.  *  * An index can be corrupt for reads (an exception was thrown when index was  * opened for query), and persistent (an exception was thrown when index is  * reopened after an update).  *  * Indexes marked bad for reads might become good again later, if another  * cluster node fixed the corruption (eg. by reindexing).  */
end_comment

begin_class
specifier|public
class|class
name|BadIndexTracker
block|{
comment|/**      * Time interval in millis after which a bad index would be accessed again      * to check if it has been fixed      */
specifier|private
specifier|static
specifier|final
name|long
name|DEFAULT_RECHECK_INTERVAL
init|=
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|15
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|BadIndexInfo
argument_list|>
name|badIndexesForRead
init|=
name|Maps
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|BadIndexInfo
argument_list|>
name|badPersistedIndexes
init|=
name|Maps
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|long
name|recheckIntervalMillis
decl_stmt|;
specifier|private
name|Ticker
name|ticker
init|=
name|Ticker
operator|.
name|systemTicker
argument_list|()
decl_stmt|;
specifier|private
name|int
name|indexerCycleCount
decl_stmt|;
specifier|public
name|BadIndexTracker
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_RECHECK_INTERVAL
argument_list|)
expr_stmt|;
block|}
specifier|public
name|BadIndexTracker
parameter_list|(
name|long
name|recheckIntervalMillis
parameter_list|)
block|{
name|this
operator|.
name|recheckIntervalMillis
operator|=
name|recheckIntervalMillis
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Bad Index recheck interval set to {} seconds"
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toSeconds
argument_list|(
name|recheckIntervalMillis
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|markGoodIndexes
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|updatedIndexPaths
parameter_list|)
block|{
name|indexerCycleCount
operator|++
expr_stmt|;
for|for
control|(
name|String
name|indexPath
range|:
name|updatedIndexPaths
control|)
block|{
name|markGoodIndex
argument_list|(
name|indexPath
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|markGoodIndex
parameter_list|(
name|String
name|indexPath
parameter_list|)
block|{
name|BadIndexInfo
name|info
init|=
name|badIndexesForRead
operator|.
name|remove
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
name|badPersistedIndexes
operator|.
name|remove
argument_list|(
name|indexPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Index [{}] which was not working {} is found to be healthy again"
argument_list|,
name|indexPath
argument_list|,
name|info
operator|.
name|getStats
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Invoked to mark a persisted index as bad i.e. where exception is thrown when index is reopened      * after update      *      * @param path index path      * @param e exception      */
specifier|public
name|void
name|markBadPersistedIndex
parameter_list|(
name|String
name|path
parameter_list|,
name|Throwable
name|e
parameter_list|)
block|{
name|BadIndexInfo
name|badIndex
init|=
name|badPersistedIndexes
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|badIndex
operator|==
literal|null
condition|)
block|{
name|badPersistedIndexes
operator|.
name|put
argument_list|(
name|path
argument_list|,
operator|new
name|BadIndexInfo
argument_list|(
name|path
argument_list|,
name|e
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"Could not open the Fulltext index at [{}]"
argument_list|,
name|path
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|badIndex
operator|.
name|failedAccess
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"Could not open the Fulltext index at [{}] . {}"
argument_list|,
name|path
argument_list|,
name|badIndex
operator|.
name|getStats
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Invoked to mark a local index as bad i.e. where exception was thrown when index was      * opened for query. It can h      */
specifier|public
name|void
name|markBadIndexForRead
parameter_list|(
name|String
name|path
parameter_list|,
name|Throwable
name|e
parameter_list|)
block|{
name|BadIndexInfo
name|badIndex
init|=
name|badIndexesForRead
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|badIndex
operator|==
literal|null
condition|)
block|{
name|badIndexesForRead
operator|.
name|put
argument_list|(
name|path
argument_list|,
operator|new
name|BadIndexInfo
argument_list|(
name|path
argument_list|,
name|e
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"Could not access the Fulltext index at [{}]"
argument_list|,
name|path
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|badIndex
operator|.
name|failedAccess
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"Could not access the Fulltext index at [{}] . {}"
argument_list|,
name|path
argument_list|,
name|badIndex
operator|.
name|getStats
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|isIgnoredBadIndex
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|BadIndexInfo
name|badIdx
init|=
name|badIndexesForRead
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|badIdx
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
operator|!
name|badIdx
operator|.
name|tryAgain
argument_list|()
return|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getIndexPaths
parameter_list|()
block|{
return|return
name|badIndexesForRead
operator|.
name|keySet
argument_list|()
return|;
block|}
specifier|public
name|BadIndexInfo
name|getInfo
parameter_list|(
name|String
name|indexPath
parameter_list|)
block|{
return|return
name|badIndexesForRead
operator|.
name|get
argument_list|(
name|indexPath
argument_list|)
return|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getBadPersistedIndexPaths
parameter_list|()
block|{
return|return
name|badPersistedIndexes
operator|.
name|keySet
argument_list|()
return|;
block|}
specifier|public
name|BadIndexInfo
name|getPersistedIndexInfo
parameter_list|(
name|String
name|indexPath
parameter_list|)
block|{
return|return
name|badPersistedIndexes
operator|.
name|get
argument_list|(
name|indexPath
argument_list|)
return|;
block|}
specifier|public
name|long
name|getRecheckIntervalMillis
parameter_list|()
block|{
return|return
name|recheckIntervalMillis
return|;
block|}
specifier|public
name|void
name|setTicker
parameter_list|(
name|Ticker
name|ticker
parameter_list|)
block|{
name|this
operator|.
name|ticker
operator|=
name|ticker
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasBadIndexes
parameter_list|()
block|{
return|return
operator|!
operator|(
name|badIndexesForRead
operator|.
name|isEmpty
argument_list|()
operator|&&
name|badPersistedIndexes
operator|.
name|isEmpty
argument_list|()
operator|)
return|;
block|}
specifier|public
class|class
name|BadIndexInfo
block|{
specifier|public
specifier|final
name|String
name|path
decl_stmt|;
specifier|final
name|int
name|lastIndexerCycleCount
init|=
name|indexerCycleCount
decl_stmt|;
specifier|private
specifier|final
name|long
name|createdTime
init|=
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toMillis
argument_list|(
name|ticker
operator|.
name|read
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|persistedIndex
decl_stmt|;
specifier|private
specifier|final
name|Stopwatch
name|created
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|(
name|ticker
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Stopwatch
name|watch
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|(
name|ticker
argument_list|)
decl_stmt|;
specifier|private
name|String
name|exception
decl_stmt|;
specifier|private
name|int
name|accessCount
decl_stmt|;
specifier|private
name|int
name|failedAccessCount
decl_stmt|;
specifier|public
name|BadIndexInfo
parameter_list|(
name|String
name|path
parameter_list|,
name|Throwable
name|e
parameter_list|,
name|boolean
name|persistedIndex
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|exception
operator|=
name|Throwables
operator|.
name|getStackTraceAsString
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|this
operator|.
name|persistedIndex
operator|=
name|persistedIndex
expr_stmt|;
block|}
specifier|public
name|boolean
name|tryAgain
parameter_list|()
block|{
name|accessCount
operator|++
expr_stmt|;
if|if
condition|(
name|watch
operator|.
name|elapsed
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|>
name|recheckIntervalMillis
condition|)
block|{
name|watch
operator|.
name|reset
argument_list|()
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Ignoring index [{}] which is not working correctly {}"
argument_list|,
name|path
argument_list|,
name|getStats
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|String
name|getStats
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"since %s ,%d indexing cycles, accessed %d times"
argument_list|,
name|created
argument_list|,
name|getCycleCount
argument_list|()
argument_list|,
name|accessCount
argument_list|)
return|;
block|}
specifier|public
name|int
name|getFailedAccessCount
parameter_list|()
block|{
return|return
name|failedAccessCount
return|;
block|}
specifier|public
name|int
name|getAccessCount
parameter_list|()
block|{
return|return
name|accessCount
return|;
block|}
specifier|public
name|String
name|getException
parameter_list|()
block|{
return|return
name|exception
return|;
block|}
specifier|public
name|long
name|getCreatedTime
parameter_list|()
block|{
return|return
name|createdTime
return|;
block|}
specifier|public
name|boolean
name|isPersistedIndex
parameter_list|()
block|{
return|return
name|persistedIndex
return|;
block|}
specifier|private
name|int
name|getCycleCount
parameter_list|()
block|{
return|return
name|indexerCycleCount
operator|-
name|lastIndexerCycleCount
return|;
block|}
specifier|public
name|void
name|failedAccess
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|failedAccessCount
operator|++
expr_stmt|;
name|exception
operator|=
name|Throwables
operator|.
name|getStackTraceAsString
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

