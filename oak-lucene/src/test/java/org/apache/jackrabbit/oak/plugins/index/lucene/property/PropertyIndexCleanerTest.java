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
name|property
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
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
name|ImmutableList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonObject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|JsonParser
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
name|index
operator|.
name|AsyncIndexInfo
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
name|AsyncIndexInfoService
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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|PropertyDefinition
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
name|PropertyUpdateCallback
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
name|property
operator|.
name|PropertyIndexCleaner
operator|.
name|CleanupStats
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
name|util
operator|.
name|IndexDefinitionBuilder
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyValues
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
name|query
operator|.
name|index
operator|.
name|FilterImpl
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
name|NodeStateUtils
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
name|json
operator|.
name|simple
operator|.
name|parser
operator|.
name|ParseException
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
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
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
name|api
operator|.
name|CommitFailedException
operator|.
name|CONSTRAINT
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
name|commons
operator|.
name|PathUtils
operator|.
name|getName
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
name|commons
operator|.
name|PathUtils
operator|.
name|getParentPath
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
name|index
operator|.
name|lucene
operator|.
name|TestUtil
operator|.
name|child
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
name|memory
operator|.
name|PropertyStates
operator|.
name|createProperty
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
name|spi
operator|.
name|state
operator|.
name|NodeStateUtils
operator|.
name|getNode
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsInAnyOrder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|empty
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|PropertyIndexCleanerTest
block|{
specifier|private
name|NodeStore
name|nodeStore
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
specifier|private
name|SimpleAsyncInfoService
name|asyncService
init|=
operator|new
name|SimpleAsyncInfoService
argument_list|()
decl_stmt|;
specifier|private
name|Clock
name|clock
init|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|nb
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
operator|new
name|InitialContent
argument_list|()
operator|.
name|initialize
argument_list|(
name|nb
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|nb
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|syncIndexPaths
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDefinitionBuilder
name|defnb
init|=
operator|new
name|IndexDefinitionBuilder
argument_list|()
decl_stmt|;
name|defnb
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|propertyIndex
argument_list|()
operator|.
name|sync
argument_list|()
expr_stmt|;
name|String
name|indexPath
init|=
literal|"/oak:index/foo"
decl_stmt|;
name|addIndex
argument_list|(
name|indexPath
argument_list|,
name|defnb
argument_list|)
expr_stmt|;
name|PropertyIndexCleaner
name|cleaner
init|=
operator|new
name|PropertyIndexCleaner
argument_list|(
name|nodeStore
argument_list|,
parameter_list|()
lambda|->
name|asList
argument_list|(
literal|"/oak:index/uuid"
argument_list|,
name|indexPath
argument_list|)
argument_list|,
name|asyncService
argument_list|,
name|StatisticsProvider
operator|.
name|NOOP
argument_list|)
decl_stmt|;
comment|//As index is yet not update it would not show up in sync index paths
name|assertThat
argument_list|(
name|cleaner
operator|.
name|getSyncIndexPaths
argument_list|()
argument_list|,
name|empty
argument_list|()
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|PropertyIndexUpdateCallback
name|cb
init|=
name|newCallback
argument_list|(
name|builder
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
name|propertyUpdated
argument_list|(
name|cb
argument_list|,
name|indexPath
argument_list|,
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|builder
argument_list|)
expr_stmt|;
comment|//Post update it would show up
name|assertThat
argument_list|(
name|cleaner
operator|.
name|getSyncIndexPaths
argument_list|()
argument_list|,
name|containsInAnyOrder
argument_list|(
name|indexPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|simplePropertyIndexCleaning
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDefinitionBuilder
name|defnb
init|=
operator|new
name|IndexDefinitionBuilder
argument_list|()
decl_stmt|;
name|defnb
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|propertyIndex
argument_list|()
operator|.
name|sync
argument_list|()
expr_stmt|;
name|String
name|indexPath
init|=
literal|"/oak:index/foo"
decl_stmt|;
name|addIndex
argument_list|(
name|indexPath
argument_list|,
name|defnb
argument_list|)
expr_stmt|;
name|PropertyIndexCleaner
name|cleaner
init|=
operator|new
name|PropertyIndexCleaner
argument_list|(
name|nodeStore
argument_list|,
parameter_list|()
lambda|->
name|asList
argument_list|(
literal|"/oak:index/uuid"
argument_list|,
name|indexPath
argument_list|)
argument_list|,
name|asyncService
argument_list|,
name|StatisticsProvider
operator|.
name|NOOP
argument_list|)
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|PropertyIndexUpdateCallback
name|cb
init|=
name|newCallback
argument_list|(
name|builder
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
name|propertyUpdated
argument_list|(
name|cb
argument_list|,
name|indexPath
argument_list|,
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|(
name|indexPath
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
comment|//------------------------ Run 1
name|asyncService
operator|.
name|addInfo
argument_list|(
literal|"async"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|assertCleanUpPerformed
argument_list|(
name|cleaner
operator|.
name|performCleanup
argument_list|(
literal|false
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|(
name|indexPath
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|cb
operator|=
name|newCallback
argument_list|(
name|builder
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
name|propertyUpdated
argument_list|(
name|cb
argument_list|,
name|indexPath
argument_list|,
literal|"/b"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|(
name|indexPath
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/a"
argument_list|,
literal|"/b"
argument_list|)
argument_list|)
expr_stmt|;
comment|//------------------------ Run 2
name|asyncService
operator|.
name|addInfo
argument_list|(
literal|"async"
argument_list|,
literal|2000
argument_list|)
expr_stmt|;
name|assertCleanUpPerformed
argument_list|(
name|cleaner
operator|.
name|performCleanup
argument_list|(
literal|false
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//Now /a would be part of removed bucket
name|assertThat
argument_list|(
name|query
argument_list|(
name|indexPath
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/b"
argument_list|)
argument_list|)
expr_stmt|;
comment|//------------------------ Run 3
name|asyncService
operator|.
name|addInfo
argument_list|(
literal|"async"
argument_list|,
literal|3000
argument_list|)
expr_stmt|;
name|assertCleanUpPerformed
argument_list|(
name|cleaner
operator|.
name|performCleanup
argument_list|(
literal|false
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//With another run /b would also be removed
name|assertThat
argument_list|(
name|query
argument_list|(
name|indexPath
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
name|empty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|uniqueIndexCleaning
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDefinitionBuilder
name|defnb
init|=
operator|new
name|IndexDefinitionBuilder
argument_list|()
decl_stmt|;
name|defnb
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|propertyIndex
argument_list|()
operator|.
name|unique
argument_list|()
expr_stmt|;
name|String
name|indexPath
init|=
literal|"/oak:index/foo"
decl_stmt|;
name|addIndex
argument_list|(
name|indexPath
argument_list|,
name|defnb
argument_list|)
expr_stmt|;
name|PropertyIndexCleaner
name|cleaner
init|=
operator|new
name|PropertyIndexCleaner
argument_list|(
name|nodeStore
argument_list|,
parameter_list|()
lambda|->
name|asList
argument_list|(
literal|"/oak:index/uuid"
argument_list|,
name|indexPath
argument_list|)
argument_list|,
name|asyncService
argument_list|,
name|StatisticsProvider
operator|.
name|NOOP
argument_list|)
decl_stmt|;
name|cleaner
operator|.
name|setCreatedTimeThreshold
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|clock
operator|.
name|waitUntil
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|PropertyIndexUpdateCallback
name|cb
init|=
name|newCallback
argument_list|(
name|builder
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
name|propertyUpdated
argument_list|(
name|cb
argument_list|,
name|indexPath
argument_list|,
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|done
argument_list|()
expr_stmt|;
name|merge
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|clock
operator|.
name|waitUntil
argument_list|(
literal|1150
argument_list|)
expr_stmt|;
name|builder
operator|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|cb
operator|=
name|newCallback
argument_list|(
name|builder
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
name|propertyUpdated
argument_list|(
name|cb
argument_list|,
name|indexPath
argument_list|,
literal|"/b"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar2"
argument_list|)
expr_stmt|;
name|cb
operator|.
name|done
argument_list|()
expr_stmt|;
name|merge
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|(
name|indexPath
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|(
name|indexPath
argument_list|,
literal|"foo"
argument_list|,
literal|"bar2"
argument_list|)
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/b"
argument_list|)
argument_list|)
expr_stmt|;
comment|//------------------------ Run 1
name|asyncService
operator|.
name|addInfo
argument_list|(
literal|"async"
argument_list|,
literal|1200
argument_list|)
expr_stmt|;
name|assertCleanUpPerformed
argument_list|(
name|cleaner
operator|.
name|performCleanup
argument_list|(
literal|false
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertJsonInfo
argument_list|(
name|indexPath
argument_list|,
literal|"{\n"
operator|+
literal|"  \"foo\": {\n"
operator|+
literal|"    \"entryCount\": 1,\n"
operator|+
literal|"    \"unique\": true\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|// /a would be purged, /b would be retained as its created time 1150 is not older than 100 wrt
comment|// indexer time of 1200
name|assertThat
argument_list|(
name|query
argument_list|(
name|indexPath
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
name|empty
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|(
name|indexPath
argument_list|,
literal|"foo"
argument_list|,
literal|"bar2"
argument_list|)
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/b"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|cb
operator|=
name|newCallback
argument_list|(
name|builder
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
name|propertyUpdated
argument_list|(
name|cb
argument_list|,
name|indexPath
argument_list|,
literal|"/c"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar2"
argument_list|)
expr_stmt|;
try|try
block|{
name|cb
operator|.
name|done
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|CONSTRAINT
argument_list|,
name|e
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//------------------------ Run 2
name|asyncService
operator|.
name|addInfo
argument_list|(
literal|"async"
argument_list|,
literal|1400
argument_list|)
expr_stmt|;
name|assertCleanUpPerformed
argument_list|(
name|cleaner
operator|.
name|performCleanup
argument_list|(
literal|false
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//Both entries would have been purged
name|assertThat
argument_list|(
name|query
argument_list|(
name|indexPath
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
name|empty
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|(
name|indexPath
argument_list|,
literal|"foo"
argument_list|,
literal|"bar2"
argument_list|)
argument_list|,
name|empty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|noRunPerformedIfNoChangeInAsync
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDefinitionBuilder
name|defnb
init|=
operator|new
name|IndexDefinitionBuilder
argument_list|()
decl_stmt|;
name|defnb
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|propertyIndex
argument_list|()
operator|.
name|sync
argument_list|()
expr_stmt|;
name|String
name|indexPath
init|=
literal|"/oak:index/foo"
decl_stmt|;
name|addIndex
argument_list|(
name|indexPath
argument_list|,
name|defnb
argument_list|)
expr_stmt|;
name|PropertyIndexCleaner
name|cleaner
init|=
operator|new
name|PropertyIndexCleaner
argument_list|(
name|nodeStore
argument_list|,
parameter_list|()
lambda|->
name|asList
argument_list|(
literal|"/oak:index/uuid"
argument_list|,
name|indexPath
argument_list|)
argument_list|,
name|asyncService
argument_list|,
name|StatisticsProvider
operator|.
name|NOOP
argument_list|)
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|PropertyIndexUpdateCallback
name|cb
init|=
name|newCallback
argument_list|(
name|builder
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
name|propertyUpdated
argument_list|(
name|cb
argument_list|,
name|indexPath
argument_list|,
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|(
name|indexPath
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
comment|//------------------------ Run 1
name|asyncService
operator|.
name|addInfo
argument_list|(
literal|"async"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|assertCleanUpPerformed
argument_list|(
name|cleaner
operator|.
name|performCleanup
argument_list|(
literal|false
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertJsonInfo
argument_list|(
name|indexPath
argument_list|,
literal|"{\n"
operator|+
literal|"  \"foo\": {\n"
operator|+
literal|"    \"1\": {\n"
operator|+
literal|"      \"type\": \"previous\",\n"
operator|+
literal|"      \"keyCount\": 1,\n"
operator|+
literal|"      \"entryCount\": 1,\n"
operator|+
literal|"      \"totalCount\": 3\n"
operator|+
literal|"    },\n"
operator|+
literal|"    \"2\": {\n"
operator|+
literal|"      \"type\": \"head\",\n"
operator|+
literal|"      \"keyCount\": 0,\n"
operator|+
literal|"      \"entryCount\": 0,\n"
operator|+
literal|"      \"totalCount\": 1\n"
operator|+
literal|"    }\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
argument_list|)
expr_stmt|;
comment|//Second run should not run
name|assertCleanUpPerformed
argument_list|(
name|cleaner
operator|.
name|performCleanup
argument_list|(
literal|false
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|recursiveDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexDefinitionBuilder
name|defnb
init|=
operator|new
name|IndexDefinitionBuilder
argument_list|()
decl_stmt|;
name|defnb
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|propertyIndex
argument_list|()
operator|.
name|sync
argument_list|()
expr_stmt|;
name|String
name|indexPath
init|=
literal|"/oak:index/foo"
decl_stmt|;
name|addIndex
argument_list|(
name|indexPath
argument_list|,
name|defnb
argument_list|)
expr_stmt|;
name|PropertyIndexCleaner
name|cleaner
init|=
operator|new
name|PropertyIndexCleaner
argument_list|(
name|nodeStore
argument_list|,
parameter_list|()
lambda|->
name|asList
argument_list|(
literal|"/oak:index/uuid"
argument_list|,
name|indexPath
argument_list|)
argument_list|,
name|asyncService
argument_list|,
name|StatisticsProvider
operator|.
name|NOOP
argument_list|)
decl_stmt|;
name|cleaner
operator|.
name|setRecursiveDelete
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|PropertyIndexUpdateCallback
name|cb
init|=
name|newCallback
argument_list|(
name|builder
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
name|propertyUpdated
argument_list|(
name|cb
argument_list|,
name|indexPath
argument_list|,
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|(
name|indexPath
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
comment|//------------------------ Run 1
name|asyncService
operator|.
name|addInfo
argument_list|(
literal|"async"
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|CleanupStats
name|stats
init|=
name|cleaner
operator|.
name|performCleanup
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|assertCleanUpPerformed
argument_list|(
name|stats
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|asyncService
operator|.
name|addInfo
argument_list|(
literal|"async"
argument_list|,
literal|2000
argument_list|)
expr_stmt|;
name|stats
operator|=
name|cleaner
operator|.
name|performCleanup
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|//1 - for bucket
comment|//1 - for indexed value 'bar'
comment|//1 - for indexed path 'a'
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|stats
operator|.
name|numOfNodesDeleted
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertCleanUpPerformed
parameter_list|(
name|CleanupStats
name|stats
parameter_list|,
name|boolean
name|expected
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|stats
operator|.
name|cleanupPerformed
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertJsonInfo
parameter_list|(
name|String
name|indexPath
parameter_list|,
name|String
name|expectedJson
parameter_list|)
throws|throws
name|ParseException
block|{
name|NodeState
name|idx
init|=
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|nodeStore
operator|.
name|getRoot
argument_list|()
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
name|String
name|json
init|=
operator|new
name|HybridPropertyIndexInfo
argument_list|(
name|idx
argument_list|)
operator|.
name|getInfoAsJson
argument_list|()
decl_stmt|;
name|JsonObject
name|j1
init|=
operator|(
name|JsonObject
operator|)
operator|new
name|JsonParser
argument_list|()
operator|.
name|parse
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|JsonObject
name|j2
init|=
operator|(
name|JsonObject
operator|)
operator|new
name|JsonParser
argument_list|()
operator|.
name|parse
argument_list|(
name|expectedJson
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|j1
operator|.
name|equals
argument_list|(
name|j2
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|j1
argument_list|,
name|j2
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|addIndex
parameter_list|(
name|String
name|indexPath
parameter_list|,
name|IndexDefinitionBuilder
name|defnb
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|nb
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|child
argument_list|(
name|nb
argument_list|,
name|getParentPath
argument_list|(
name|indexPath
argument_list|)
argument_list|)
operator|.
name|setChildNode
argument_list|(
name|getName
argument_list|(
name|indexPath
argument_list|)
argument_list|,
name|defnb
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|merge
argument_list|(
name|nb
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|propertyUpdated
parameter_list|(
name|PropertyUpdateCallback
name|callback
parameter_list|,
name|String
name|indexPath
parameter_list|,
name|String
name|nodePath
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|callback
operator|.
name|propertyUpdated
argument_list|(
name|nodePath
argument_list|,
name|propertyName
argument_list|,
name|pd
argument_list|(
name|indexPath
argument_list|,
name|propertyName
argument_list|)
argument_list|,
literal|null
argument_list|,
name|createProperty
argument_list|(
name|propertyName
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|PropertyIndexUpdateCallback
name|newCallback
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|indexPath
parameter_list|)
block|{
return|return
operator|new
name|PropertyIndexUpdateCallback
argument_list|(
name|indexPath
argument_list|,
name|child
argument_list|(
name|builder
argument_list|,
name|indexPath
argument_list|)
argument_list|,
name|nodeStore
operator|.
name|getRoot
argument_list|()
argument_list|,
name|clock
argument_list|)
return|;
block|}
specifier|private
name|PropertyDefinition
name|pd
parameter_list|(
name|String
name|indexPath
parameter_list|,
name|String
name|propName
parameter_list|)
block|{
name|NodeState
name|root
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|IndexDefinition
name|defn
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|getNode
argument_list|(
name|root
argument_list|,
name|indexPath
argument_list|)
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
return|return
name|defn
operator|.
name|getApplicableIndexingRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|getConfig
argument_list|(
name|propName
argument_list|)
return|;
block|}
specifier|private
name|void
name|merge
parameter_list|(
name|NodeBuilder
name|nb
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|nodeStore
operator|.
name|merge
argument_list|(
name|nb
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|query
parameter_list|(
name|String
name|indexPath
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|NodeState
name|root
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|HybridPropertyIndexLookup
name|lookup
init|=
operator|new
name|HybridPropertyIndexLookup
argument_list|(
name|indexPath
argument_list|,
name|getNode
argument_list|(
name|root
argument_list|,
name|indexPath
argument_list|)
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
name|FilterImpl
operator|.
name|newTestInstance
argument_list|()
decl_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|lookup
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|propertyName
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
name|value
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|paths
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|SimpleAsyncInfoService
implements|implements
name|AsyncIndexInfoService
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|AsyncIndexInfo
argument_list|>
name|infos
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getAsyncLanes
parameter_list|()
block|{
return|return
name|infos
operator|.
name|keySet
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getAsyncLanes
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|AsyncIndexInfo
name|getInfo
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|infos
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|AsyncIndexInfo
name|getInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|root
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|getIndexedUptoPerLane
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AsyncIndexInfo
name|info
range|:
name|infos
operator|.
name|values
argument_list|()
control|)
block|{
name|result
operator|.
name|put
argument_list|(
name|info
operator|.
name|getName
argument_list|()
argument_list|,
name|info
operator|.
name|getLastIndexedTo
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|getIndexedUptoPerLane
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|void
name|addInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|lastIndexedTo
parameter_list|)
block|{
name|infos
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|AsyncIndexInfo
argument_list|(
name|name
argument_list|,
name|lastIndexedTo
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

