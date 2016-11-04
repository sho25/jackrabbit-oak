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
name|secondary
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|CommitFailedException
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
name|AbstractDocumentNodeState
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
name|NodeStateDiffer
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
name|PathFilter
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
name|spi
operator|.
name|commit
operator|.
name|CommitInfo
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
name|spi
operator|.
name|commit
operator|.
name|EmptyHook
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
name|spi
operator|.
name|commit
operator|.
name|Observer
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
name|spi
operator|.
name|state
operator|.
name|ApplyDiff
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
name|spi
operator|.
name|state
operator|.
name|NodeBuilder
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
name|spi
operator|.
name|state
operator|.
name|NodeState
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
name|spi
operator|.
name|state
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|stats
operator|.
name|StatsOptions
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
name|TimerStats
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

begin_class
class|class
name|SecondaryStoreObserver
implements|implements
name|Observer
block|{
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
name|NodeStore
name|nodeStore
decl_stmt|;
specifier|private
specifier|final
name|PathFilter
name|pathFilter
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|metaPropNames
decl_stmt|;
specifier|private
specifier|final
name|SecondaryStoreRootObserver
name|secondaryObserver
decl_stmt|;
specifier|private
specifier|final
name|NodeStateDiffer
name|differ
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|local
decl_stmt|;
specifier|private
specifier|final
name|TimerStats
name|external
decl_stmt|;
specifier|private
name|boolean
name|firstEventProcessed
decl_stmt|;
specifier|public
name|SecondaryStoreObserver
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|metaPropNames
parameter_list|,
name|NodeStateDiffer
name|differ
parameter_list|,
name|PathFilter
name|pathFilter
parameter_list|,
name|StatisticsProvider
name|statisticsProvider
parameter_list|,
name|SecondaryStoreRootObserver
name|secondaryObserver
parameter_list|)
block|{
name|this
operator|.
name|nodeStore
operator|=
name|nodeStore
expr_stmt|;
name|this
operator|.
name|pathFilter
operator|=
name|pathFilter
expr_stmt|;
name|this
operator|.
name|secondaryObserver
operator|=
name|secondaryObserver
expr_stmt|;
name|this
operator|.
name|differ
operator|=
name|differ
expr_stmt|;
name|this
operator|.
name|metaPropNames
operator|=
name|metaPropNames
expr_stmt|;
name|this
operator|.
name|local
operator|=
name|statisticsProvider
operator|.
name|getTimer
argument_list|(
literal|"DOCUMENT_CACHE_SEC_LOCAL"
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|external
operator|=
name|statisticsProvider
operator|.
name|getTimer
argument_list|(
literal|"DOCUMENT_CACHE_SEC_EXTERNAL"
argument_list|,
name|StatsOptions
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|contentChanged
parameter_list|(
annotation|@
name|Nonnull
name|NodeState
name|root
parameter_list|,
annotation|@
name|Nullable
name|CommitInfo
name|info
parameter_list|)
block|{
comment|//Diff here would also be traversing non visible areas and there
comment|//diffManyChildren might pose problem for e.g. data under uuid index
if|if
condition|(
operator|!
name|firstEventProcessed
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Starting initial sync"
argument_list|)
expr_stmt|;
block|}
name|Stopwatch
name|w
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|AbstractDocumentNodeState
name|target
init|=
operator|(
name|AbstractDocumentNodeState
operator|)
name|root
decl_stmt|;
name|NodeState
name|secondaryRoot
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|NodeState
name|base
init|=
name|DelegatingDocumentNodeState
operator|.
name|wrapIfPossible
argument_list|(
name|secondaryRoot
argument_list|,
name|differ
argument_list|)
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|secondaryRoot
operator|.
name|builder
argument_list|()
decl_stmt|;
name|ApplyDiff
name|diff
init|=
operator|new
name|PathFilteringDiff
argument_list|(
name|builder
argument_list|,
name|pathFilter
argument_list|,
name|metaPropNames
argument_list|,
name|target
argument_list|)
decl_stmt|;
comment|//Copy the root node meta properties
name|PathFilteringDiff
operator|.
name|copyMetaProperties
argument_list|(
name|target
argument_list|,
name|builder
argument_list|,
name|metaPropNames
argument_list|)
expr_stmt|;
comment|//Apply the rest of properties
name|target
operator|.
name|compareAgainstBaseState
argument_list|(
name|base
argument_list|,
name|diff
argument_list|)
expr_stmt|;
try|try
block|{
name|NodeState
name|updatedSecondaryRoot
init|=
name|nodeStore
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|secondaryObserver
operator|.
name|contentChanged
argument_list|(
name|DelegatingDocumentNodeState
operator|.
name|wrap
argument_list|(
name|updatedSecondaryRoot
argument_list|,
name|differ
argument_list|)
argument_list|)
expr_stmt|;
name|TimerStats
name|timer
init|=
name|info
operator|==
literal|null
condition|?
name|external
else|:
name|local
decl_stmt|;
name|timer
operator|.
name|update
argument_list|(
name|w
operator|.
name|elapsed
argument_list|(
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|firstEventProcessed
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Time taken for initial sync {}"
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|firstEventProcessed
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|//TODO
name|log
operator|.
name|warn
argument_list|(
literal|"Commit to secondary store failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

