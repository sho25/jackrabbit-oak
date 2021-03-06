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
package|;
end_package

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
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MICROSECONDS
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

begin_class
specifier|public
class|class
name|VersionGCStatsTest
block|{
specifier|private
specifier|static
specifier|final
name|Callable
name|START
init|=
operator|new
name|Callable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|call
parameter_list|(
name|Stopwatch
name|watch
parameter_list|)
block|{
name|watch
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Callable
name|STOP
init|=
operator|new
name|Callable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|call
parameter_list|(
name|Stopwatch
name|watch
parameter_list|)
block|{
name|watch
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|private
name|VersionGCStats
name|stats
init|=
operator|new
name|VersionGCStats
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|forEachStopwatch
argument_list|(
name|stats
argument_list|,
name|START
argument_list|)
expr_stmt|;
while|while
condition|(
name|stats
operator|.
name|updateResurrectedDocuments
operator|.
name|elapsed
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|<
literal|10
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|forEachStopwatch
argument_list|(
name|stats
argument_list|,
name|STOP
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addRun
parameter_list|()
block|{
name|VersionGCStats
name|cumulative
init|=
operator|new
name|VersionGCStats
argument_list|()
decl_stmt|;
name|cumulative
operator|.
name|addRun
argument_list|(
name|stats
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|active
operator|.
name|elapsed
argument_list|(
name|MICROSECONDS
argument_list|)
argument_list|,
name|cumulative
operator|.
name|activeElapsed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|collectDeletedDocs
operator|.
name|elapsed
argument_list|(
name|MICROSECONDS
argument_list|)
argument_list|,
name|cumulative
operator|.
name|collectDeletedDocsElapsed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|checkDeletedDocs
operator|.
name|elapsed
argument_list|(
name|MICROSECONDS
argument_list|)
argument_list|,
name|cumulative
operator|.
name|checkDeletedDocsElapsed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|deleteDeletedDocs
operator|.
name|elapsed
argument_list|(
name|MICROSECONDS
argument_list|)
argument_list|,
name|cumulative
operator|.
name|deleteDeletedDocsElapsed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|collectAndDeleteSplitDocs
operator|.
name|elapsed
argument_list|(
name|MICROSECONDS
argument_list|)
argument_list|,
name|cumulative
operator|.
name|collectAndDeleteSplitDocsElapsed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|sortDocIds
operator|.
name|elapsed
argument_list|(
name|MICROSECONDS
argument_list|)
argument_list|,
name|cumulative
operator|.
name|sortDocIdsElapsed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|updateResurrectedDocuments
operator|.
name|elapsed
argument_list|(
name|MICROSECONDS
argument_list|)
argument_list|,
name|cumulative
operator|.
name|updateResurrectedDocumentsElapsed
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addRunCumulative
parameter_list|()
block|{
name|VersionGCStats
name|cumulative
init|=
operator|new
name|VersionGCStats
argument_list|()
decl_stmt|;
name|cumulative
operator|.
name|addRun
argument_list|(
name|stats
argument_list|)
expr_stmt|;
comment|// double stats by adding to itself
name|cumulative
operator|.
name|addRun
argument_list|(
name|cumulative
argument_list|)
expr_stmt|;
comment|// now the stats must have doubled
name|assertEquals
argument_list|(
name|stats
operator|.
name|active
operator|.
name|elapsed
argument_list|(
name|MICROSECONDS
argument_list|)
operator|*
literal|2
argument_list|,
name|cumulative
operator|.
name|activeElapsed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|collectDeletedDocs
operator|.
name|elapsed
argument_list|(
name|MICROSECONDS
argument_list|)
operator|*
literal|2
argument_list|,
name|cumulative
operator|.
name|collectDeletedDocsElapsed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|checkDeletedDocs
operator|.
name|elapsed
argument_list|(
name|MICROSECONDS
argument_list|)
operator|*
literal|2
argument_list|,
name|cumulative
operator|.
name|checkDeletedDocsElapsed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|deleteDeletedDocs
operator|.
name|elapsed
argument_list|(
name|MICROSECONDS
argument_list|)
operator|*
literal|2
argument_list|,
name|cumulative
operator|.
name|deleteDeletedDocsElapsed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|collectAndDeleteSplitDocs
operator|.
name|elapsed
argument_list|(
name|MICROSECONDS
argument_list|)
operator|*
literal|2
argument_list|,
name|cumulative
operator|.
name|collectAndDeleteSplitDocsElapsed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|sortDocIds
operator|.
name|elapsed
argument_list|(
name|MICROSECONDS
argument_list|)
operator|*
literal|2
argument_list|,
name|cumulative
operator|.
name|sortDocIdsElapsed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|stats
operator|.
name|updateResurrectedDocuments
operator|.
name|elapsed
argument_list|(
name|MICROSECONDS
argument_list|)
operator|*
literal|2
argument_list|,
name|cumulative
operator|.
name|updateResurrectedDocumentsElapsed
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|forEachStopwatch
parameter_list|(
name|VersionGCStats
name|stats
parameter_list|,
name|Callable
name|c
parameter_list|)
block|{
name|c
operator|.
name|call
argument_list|(
name|stats
operator|.
name|active
argument_list|)
expr_stmt|;
name|c
operator|.
name|call
argument_list|(
name|stats
operator|.
name|collectDeletedDocs
argument_list|)
expr_stmt|;
name|c
operator|.
name|call
argument_list|(
name|stats
operator|.
name|checkDeletedDocs
argument_list|)
expr_stmt|;
name|c
operator|.
name|call
argument_list|(
name|stats
operator|.
name|deleteDeletedDocs
argument_list|)
expr_stmt|;
name|c
operator|.
name|call
argument_list|(
name|stats
operator|.
name|collectAndDeleteSplitDocs
argument_list|)
expr_stmt|;
name|c
operator|.
name|call
argument_list|(
name|stats
operator|.
name|sortDocIds
argument_list|)
expr_stmt|;
name|c
operator|.
name|call
argument_list|(
name|stats
operator|.
name|updateResurrectedDocuments
argument_list|)
expr_stmt|;
block|}
specifier|private
interface|interface
name|Callable
block|{
name|void
name|call
parameter_list|(
name|Stopwatch
name|watch
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

