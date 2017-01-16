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
name|lucene
operator|.
name|hybrid
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|concurrent
operator|.
name|TimeUnit
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|LinkedListMultimap
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
name|ListMultimap
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
name|index
operator|.
name|lucene
operator|.
name|IndexCopier
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
name|index
operator|.
name|lucene
operator|.
name|IndexDefinition
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|stats
operator|.
name|StatisticsProvider
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
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
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
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_class
specifier|public
class|class
name|NRTIndexFactory
implements|implements
name|Closeable
block|{
comment|/**      * Maximum numbers of NRTIndex to keep at a time. At runtime for a given index      * /oak:index/fooIndex at max 2 IndexNode would be opened at a time and those 2      * IndexNode would keep reference to at max 3 NRT Indexes      */
specifier|private
specifier|static
specifier|final
name|int
name|MAX_INDEX_COUNT
init|=
literal|3
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|REFRESH_DELTA_IN_SECS
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"oak.lucene.refreshDeltaSecs"
argument_list|,
literal|1
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
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|NRTIndex
argument_list|>
name|indexes
init|=
name|LinkedListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|IndexCopier
name|indexCopier
decl_stmt|;
specifier|private
specifier|final
name|Clock
name|clock
decl_stmt|;
specifier|private
specifier|final
name|long
name|refreshDeltaInSecs
decl_stmt|;
specifier|private
specifier|final
name|StatisticsProvider
name|statisticsProvider
decl_stmt|;
specifier|public
name|NRTIndexFactory
parameter_list|(
name|IndexCopier
name|indexCopier
parameter_list|,
name|StatisticsProvider
name|statisticsProvider
parameter_list|)
block|{
name|this
argument_list|(
name|indexCopier
argument_list|,
name|Clock
operator|.
name|SIMPLE
argument_list|,
name|REFRESH_DELTA_IN_SECS
argument_list|,
name|statisticsProvider
argument_list|)
expr_stmt|;
block|}
specifier|public
name|NRTIndexFactory
parameter_list|(
name|IndexCopier
name|indexCopier
parameter_list|,
name|Clock
name|clock
parameter_list|,
name|long
name|refreshDeltaInSecs
parameter_list|,
name|StatisticsProvider
name|statisticsProvider
parameter_list|)
block|{
name|this
operator|.
name|indexCopier
operator|=
name|checkNotNull
argument_list|(
name|indexCopier
argument_list|)
expr_stmt|;
name|this
operator|.
name|clock
operator|=
name|clock
expr_stmt|;
name|this
operator|.
name|refreshDeltaInSecs
operator|=
name|refreshDeltaInSecs
expr_stmt|;
name|this
operator|.
name|statisticsProvider
operator|=
name|statisticsProvider
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Refresh delta set to {} secs"
argument_list|,
name|refreshDeltaInSecs
argument_list|)
expr_stmt|;
block|}
comment|//This would not be invoked concurrently
comment|// but still mark it synchronized for safety
annotation|@
name|CheckForNull
specifier|public
specifier|synchronized
name|NRTIndex
name|createIndex
parameter_list|(
name|IndexDefinition
name|definition
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|definition
operator|.
name|isNRTIndexingEnabled
argument_list|()
operator|||
name|definition
operator|.
name|isSyncIndexingEnabled
argument_list|()
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|indexPath
init|=
name|definition
operator|.
name|getIndexPath
argument_list|()
decl_stmt|;
name|NRTIndex
name|current
init|=
operator|new
name|NRTIndex
argument_list|(
name|definition
argument_list|,
name|indexCopier
argument_list|,
name|getRefreshPolicy
argument_list|(
name|definition
argument_list|)
argument_list|,
name|getPrevious
argument_list|(
name|indexPath
argument_list|)
argument_list|,
name|statisticsProvider
argument_list|)
decl_stmt|;
name|indexes
operator|.
name|put
argument_list|(
name|indexPath
argument_list|,
name|current
argument_list|)
expr_stmt|;
name|closeLast
argument_list|(
name|indexPath
argument_list|)
expr_stmt|;
return|return
name|current
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|NRTIndex
name|index
range|:
name|indexes
operator|.
name|values
argument_list|()
control|)
block|{
name|index
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|indexes
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|List
argument_list|<
name|NRTIndex
argument_list|>
name|getIndexes
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|indexes
operator|.
name|get
argument_list|(
name|path
argument_list|)
return|;
block|}
specifier|private
name|void
name|closeLast
parameter_list|(
name|String
name|indexPath
parameter_list|)
block|{
name|List
argument_list|<
name|NRTIndex
argument_list|>
name|existing
init|=
name|indexes
operator|.
name|get
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|existing
operator|.
name|size
argument_list|()
operator|<=
name|MAX_INDEX_COUNT
condition|)
block|{
return|return;
block|}
name|NRTIndex
name|oldest
init|=
name|existing
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
try|try
block|{
name|oldest
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error occurred while closing index [{}]"
argument_list|,
name|oldest
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|NRTIndex
name|getPrevious
parameter_list|(
name|String
name|indexPath
parameter_list|)
block|{
name|List
argument_list|<
name|NRTIndex
argument_list|>
name|existing
init|=
name|indexes
operator|.
name|get
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|existing
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|checkArgument
argument_list|(
name|existing
operator|.
name|size
argument_list|()
operator|<=
name|MAX_INDEX_COUNT
argument_list|,
literal|"Found [%s] more than 3 index"
argument_list|,
name|existing
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|existing
operator|.
name|get
argument_list|(
name|existing
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
specifier|private
name|IndexUpdateListener
name|getRefreshPolicy
parameter_list|(
name|IndexDefinition
name|definition
parameter_list|)
block|{
if|if
condition|(
name|definition
operator|.
name|isSyncIndexingEnabled
argument_list|()
condition|)
block|{
return|return
operator|new
name|RefreshOnWritePolicy
argument_list|()
return|;
comment|//return new RefreshOnReadPolicy(clock, TimeUnit.SECONDS, refreshDeltaInSecs);
block|}
return|return
operator|new
name|TimedRefreshPolicy
argument_list|(
name|clock
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
name|refreshDeltaInSecs
argument_list|)
return|;
block|}
block|}
end_class

end_unit

