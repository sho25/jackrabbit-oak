begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law  * or agreed to in writing, software distributed under the License is  * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied. See the License for the specific language  * governing permissions and limitations under the License.  */
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
name|query
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|InitialContent
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
name|Oak
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
name|ContentRepository
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
name|Result
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
name|Tree
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
name|AsyncIndexUpdate
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
name|counter
operator|.
name|NodeCounterEditorProvider
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
name|memory
operator|.
name|MemoryNodeStore
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
name|query
operator|.
name|Cursor
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
name|query
operator|.
name|Filter
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
name|query
operator|.
name|IndexRow
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
name|query
operator|.
name|QueryIndex
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
name|query
operator|.
name|QueryIndexProvider
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
name|security
operator|.
name|OpenSecurityProvider
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
name|spi
operator|.
name|whiteboard
operator|.
name|Whiteboard
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
name|whiteboard
operator|.
name|WhiteboardUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|Lists
import|;
end_import

begin_class
specifier|public
class|class
name|TraversalAvoidanceTest
extends|extends
name|AbstractQueryTest
block|{
name|Whiteboard
name|wb
decl_stmt|;
name|NodeStore
name|nodeStore
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|QUERY
init|=
literal|"SELECT * FROM [nt:base]"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PATH_RESTRICTED_QUERY
init|=
literal|"SELECT * FROM [nt:base] WHERE ISDESCENDANTNODE('/content/test0')"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PATH_RESTRICTED_SLOW_TRAVERSAL_QUERY
init|=
literal|"SELECT * FROM [nt:base] WHERE ISDESCENDANTNODE('/content')"
decl_stmt|;
specifier|private
name|TestQueryIndexProvider
name|testIndexProvider
init|=
operator|new
name|TestQueryIndexProvider
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|ContentRepository
name|createRepository
parameter_list|()
block|{
name|nodeStore
operator|=
operator|new
name|MemoryNodeStore
argument_list|()
expr_stmt|;
name|Oak
name|oak
init|=
operator|new
name|Oak
argument_list|(
name|nodeStore
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|InitialContent
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|NodeCounterEditorProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|testIndexProvider
argument_list|)
comment|//Effectively disable async indexing auto run
comment|//such that we can control run timing as per test requirement
operator|.
name|withAsyncIndexing
argument_list|(
literal|"async"
argument_list|,
name|TimeUnit
operator|.
name|DAYS
operator|.
name|toSeconds
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|wb
operator|=
name|oak
operator|.
name|getWhiteboard
argument_list|()
expr_stmt|;
return|return
name|oak
operator|.
name|createContentRepository
argument_list|()
return|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|session
operator|=
name|createRepository
argument_list|()
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|root
operator|=
name|session
operator|.
name|getLatestRoot
argument_list|()
expr_stmt|;
name|qe
operator|=
name|root
operator|.
name|getQueryEngine
argument_list|()
expr_stmt|;
name|root
operator|.
name|getTree
argument_list|(
literal|"/oak:index/counter"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"resolution"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|root
operator|.
name|getTree
argument_list|(
literal|"/oak:index/counter"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"seed"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Tree
name|content
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"content"
argument_list|)
decl_stmt|;
comment|// add 200'000 nodes under /content
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2000
condition|;
name|i
operator|++
control|)
block|{
name|Tree
name|t
init|=
name|content
operator|.
name|addChild
argument_list|(
literal|"test"
operator|+
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|t
operator|.
name|addChild
argument_list|(
literal|"n"
operator|+
name|j
argument_list|)
expr_stmt|;
block|}
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|runAsyncIndex
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|runAsyncIndex
parameter_list|()
block|{
name|Runnable
name|async
init|=
name|WhiteboardUtils
operator|.
name|getService
argument_list|(
name|wb
argument_list|,
name|Runnable
operator|.
name|class
argument_list|,
operator|new
name|Predicate
argument_list|<
name|Runnable
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
annotation|@
name|Nullable
name|Runnable
name|input
parameter_list|)
block|{
return|return
name|input
operator|instanceof
name|AsyncIndexUpdate
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|async
argument_list|)
expr_stmt|;
name|async
operator|.
name|run
argument_list|()
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|noPlansLetTraversalWin
parameter_list|()
block|{
name|assertPlanSelection
argument_list|(
name|QUERY
argument_list|,
literal|"traverse"
argument_list|,
literal|"Traversal must be used if nothing else participates"
argument_list|)
expr_stmt|;
name|assertPlanSelection
argument_list|(
name|PATH_RESTRICTED_QUERY
argument_list|,
literal|"traverse"
argument_list|,
literal|"Traversal must be used if nothing"
operator|+
literal|" else participates"
argument_list|)
expr_stmt|;
name|assertPlanSelection
argument_list|(
name|PATH_RESTRICTED_SLOW_TRAVERSAL_QUERY
argument_list|,
literal|"traverse"
argument_list|,
literal|"Traversal must be"
operator|+
literal|" used if nothing else participates"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|singlePlanWithoutPathRestrictionWins
parameter_list|()
block|{
name|testIndexProvider
operator|.
name|addPlan
argument_list|(
literal|"plan1"
argument_list|,
literal|10000
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertPlanSelection
argument_list|(
name|QUERY
argument_list|,
literal|"plan1"
argument_list|,
literal|"Valid plan without path restriction must win"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|singlePlanWithPathRestriction
parameter_list|()
block|{
name|testIndexProvider
operator|.
name|addPlan
argument_list|(
literal|"plan1"
argument_list|,
literal|10000
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertPlanSelection
argument_list|(
name|PATH_RESTRICTED_QUERY
argument_list|,
literal|"plan1"
argument_list|,
literal|"Valid plan which evaluate path"
operator|+
literal|" restrictions wins with query having path restriction"
argument_list|)
expr_stmt|;
name|testIndexProvider
operator|.
name|restPlans
argument_list|()
expr_stmt|;
name|testIndexProvider
operator|.
name|addPlan
argument_list|(
literal|"plan1"
argument_list|,
literal|10000
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertPlanSelection
argument_list|(
name|PATH_RESTRICTED_QUERY
argument_list|,
literal|"traverse"
argument_list|,
literal|"Valid plan which evaluate path"
operator|+
literal|" restrictions wins with query having path restriction"
argument_list|)
expr_stmt|;
name|testIndexProvider
operator|.
name|restPlans
argument_list|()
expr_stmt|;
name|testIndexProvider
operator|.
name|addPlan
argument_list|(
literal|"plan1"
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertPlanSelection
argument_list|(
name|PATH_RESTRICTED_SLOW_TRAVERSAL_QUERY
argument_list|,
literal|"plan1"
argument_list|,
literal|"cost wars still prevail"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|competingPlans
parameter_list|()
block|{
name|testIndexProvider
operator|.
name|addPlan
argument_list|(
literal|"plan1"
argument_list|,
literal|100000
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|testIndexProvider
operator|.
name|addPlan
argument_list|(
literal|"plan2"
argument_list|,
literal|100
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertPlanSelection
argument_list|(
name|QUERY
argument_list|,
literal|"plan2"
argument_list|,
literal|"Low cost must win"
argument_list|)
expr_stmt|;
name|assertPlanSelection
argument_list|(
name|PATH_RESTRICTED_QUERY
argument_list|,
literal|"plan2"
argument_list|,
literal|"Low cost must win"
argument_list|)
expr_stmt|;
name|assertPlanSelection
argument_list|(
name|PATH_RESTRICTED_SLOW_TRAVERSAL_QUERY
argument_list|,
literal|"plan2"
argument_list|,
literal|"Low cost must win"
argument_list|)
expr_stmt|;
name|testIndexProvider
operator|.
name|restPlans
argument_list|()
expr_stmt|;
name|testIndexProvider
operator|.
name|addPlan
argument_list|(
literal|"plan1"
argument_list|,
literal|100000
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testIndexProvider
operator|.
name|addPlan
argument_list|(
literal|"plan2"
argument_list|,
literal|100
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertPlanSelection
argument_list|(
name|QUERY
argument_list|,
literal|"plan2"
argument_list|,
literal|"Low cost must win"
argument_list|)
expr_stmt|;
name|assertPlanSelection
argument_list|(
name|PATH_RESTRICTED_QUERY
argument_list|,
literal|"plan2"
argument_list|,
literal|"Low cost must win"
argument_list|)
expr_stmt|;
name|assertPlanSelection
argument_list|(
name|PATH_RESTRICTED_SLOW_TRAVERSAL_QUERY
argument_list|,
literal|"plan2"
argument_list|,
literal|"Low cost must win"
argument_list|)
expr_stmt|;
name|testIndexProvider
operator|.
name|restPlans
argument_list|()
expr_stmt|;
name|testIndexProvider
operator|.
name|addPlan
argument_list|(
literal|"plan1"
argument_list|,
literal|200000
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|testIndexProvider
operator|.
name|addPlan
argument_list|(
literal|"plan2"
argument_list|,
literal|10000
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertPlanSelection
argument_list|(
name|QUERY
argument_list|,
literal|"plan2"
argument_list|,
literal|"Low cost must win"
argument_list|)
expr_stmt|;
name|assertPlanSelection
argument_list|(
name|PATH_RESTRICTED_QUERY
argument_list|,
literal|"traverse"
argument_list|,
literal|"Low cost must win"
argument_list|)
expr_stmt|;
name|assertPlanSelection
argument_list|(
name|PATH_RESTRICTED_SLOW_TRAVERSAL_QUERY
argument_list|,
literal|"plan2"
argument_list|,
literal|"Low cost must win"
argument_list|)
expr_stmt|;
name|testIndexProvider
operator|.
name|restPlans
argument_list|()
expr_stmt|;
name|testIndexProvider
operator|.
name|addPlan
argument_list|(
literal|"plan1"
argument_list|,
literal|200000
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testIndexProvider
operator|.
name|addPlan
argument_list|(
literal|"plan2"
argument_list|,
literal|1000
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertPlanSelection
argument_list|(
name|QUERY
argument_list|,
literal|"plan2"
argument_list|,
literal|"Low cost must win"
argument_list|)
expr_stmt|;
name|assertPlanSelection
argument_list|(
name|PATH_RESTRICTED_QUERY
argument_list|,
literal|"traverse"
argument_list|,
literal|"Low cost must win"
argument_list|)
expr_stmt|;
name|assertPlanSelection
argument_list|(
name|PATH_RESTRICTED_SLOW_TRAVERSAL_QUERY
argument_list|,
literal|"plan2"
argument_list|,
literal|"Low cost must win"
argument_list|)
expr_stmt|;
block|}
class|class
name|TestQueryIndexProvider
implements|implements
name|QueryIndexProvider
block|{
specifier|private
specifier|final
name|TestQueryIndex
name|queryIndex
init|=
operator|new
name|TestQueryIndex
argument_list|()
decl_stmt|;
name|void
name|addPlan
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|cost
parameter_list|,
name|boolean
name|supportsPathRestriction
parameter_list|)
block|{
name|queryIndex
operator|.
name|addPlan
argument_list|(
name|name
argument_list|,
name|cost
argument_list|,
name|supportsPathRestriction
argument_list|)
expr_stmt|;
block|}
name|void
name|restPlans
parameter_list|()
block|{
name|queryIndex
operator|.
name|resetPlans
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|QueryIndex
argument_list|>
name|getQueryIndexes
parameter_list|(
name|NodeState
name|nodeState
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|queryIndex
argument_list|)
return|;
block|}
block|}
class|class
name|TestQueryIndex
implements|implements
name|QueryIndex
implements|,
name|QueryIndex
operator|.
name|AdvancedQueryIndex
block|{
specifier|final
name|String
name|name
decl_stmt|;
specifier|final
name|List
argument_list|<
name|IndexPlan
argument_list|>
name|plans
decl_stmt|;
name|TestQueryIndex
parameter_list|()
block|{
name|this
argument_list|(
literal|"EmptyName"
argument_list|)
expr_stmt|;
block|}
name|TestQueryIndex
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|plans
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|5
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getMinimumCost
parameter_list|()
block|{
return|return
literal|1000
return|;
comment|//some high number
block|}
annotation|@
name|Override
specifier|public
name|double
name|getCost
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|NodeState
name|rootState
parameter_list|)
block|{
return|return
name|getCost
argument_list|()
return|;
block|}
specifier|private
name|double
name|getCost
parameter_list|()
block|{
return|return
literal|500
return|;
comment|//arbitrary cost - useless as we are AdvanceQueryIndex
block|}
annotation|@
name|Override
specifier|public
name|Cursor
name|query
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|NodeState
name|rootState
parameter_list|)
block|{
return|return
name|query
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Cursor
name|query
parameter_list|(
name|IndexPlan
name|plan
parameter_list|,
name|NodeState
name|rootState
parameter_list|)
block|{
return|return
name|query
argument_list|()
return|;
block|}
specifier|private
name|Cursor
name|query
parameter_list|()
block|{
return|return
operator|new
name|TestEmptyCursor
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPlan
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|NodeState
name|rootState
parameter_list|)
block|{
return|return
literal|"Unimportant plan"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getIndexName
parameter_list|()
block|{
return|return
literal|"test index"
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|IndexPlan
argument_list|>
name|getPlans
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|List
argument_list|<
name|OrderEntry
argument_list|>
name|sortOrder
parameter_list|,
name|NodeState
name|rootState
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|plans
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPlanDescription
parameter_list|(
name|IndexPlan
name|plan
parameter_list|,
name|NodeState
name|root
parameter_list|)
block|{
return|return
literal|"plan="
operator|+
name|plan
operator|.
name|getPlanName
argument_list|()
return|;
block|}
name|void
name|addPlan
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|cost
parameter_list|,
name|boolean
name|supportsPathRestriction
parameter_list|)
block|{
name|plans
operator|.
name|add
argument_list|(
operator|new
name|IndexPlan
operator|.
name|Builder
argument_list|()
operator|.
name|setCostPerEntry
argument_list|(
literal|1
argument_list|)
operator|.
name|setCostPerExecution
argument_list|(
literal|1
argument_list|)
operator|.
name|setEstimatedEntryCount
argument_list|(
name|cost
argument_list|)
operator|.
name|setSupportsPathRestriction
argument_list|(
name|supportsPathRestriction
argument_list|)
operator|.
name|setPlanName
argument_list|(
name|name
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|void
name|resetPlans
parameter_list|()
block|{
name|plans
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
class|class
name|TestEmptyCursor
implements|implements
name|Cursor
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|IndexRow
name|next
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getSize
parameter_list|(
name|Result
operator|.
name|SizePrecision
name|precision
parameter_list|,
name|long
name|max
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
block|}
specifier|private
name|String
name|explain
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|String
name|explain
init|=
literal|"explain "
operator|+
name|query
decl_stmt|;
return|return
name|executeQuery
argument_list|(
name|explain
argument_list|,
name|SQL2
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
specifier|private
name|void
name|assertPlanSelection
parameter_list|(
name|String
name|query
parameter_list|,
name|String
name|expectedPlan
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|String
name|explain
init|=
name|explain
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|message
operator|+
literal|", but got: "
operator|+
name|explain
argument_list|,
name|explain
operator|.
name|contains
argument_list|(
name|expectedPlan
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

