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
name|VersionGarbageCollector
operator|.
name|VersionGCStats
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
name|assertFalse
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
name|assertTrue
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

begin_class
specifier|public
class|class
name|VersionGarbageCollectorTest
block|{
specifier|private
name|Clock
name|clock
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|store
decl_stmt|;
specifier|private
name|VersionGarbageCollector
name|gc
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|clock
operator|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
expr_stmt|;
name|store
operator|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|clock
argument_list|(
name|clock
argument_list|)
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
name|gc
operator|=
name|store
operator|.
name|getVersionGarbageCollector
argument_list|()
expr_stmt|;
comment|//Baseline the clock
name|clock
operator|.
name|waitUntil
argument_list|(
name|Revision
operator|.
name|getCurrentTimestamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|gcIgnoredForCheckpoint
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|expiryTime
init|=
literal|100
decl_stmt|,
name|maxAge
init|=
literal|20
decl_stmt|;
name|Revision
name|cp
init|=
name|Revision
operator|.
name|fromString
argument_list|(
name|store
operator|.
name|checkpoint
argument_list|(
name|expiryTime
argument_list|)
argument_list|)
decl_stmt|;
name|gc
operator|.
name|setMaxRevisionAge
argument_list|(
name|maxAge
argument_list|)
expr_stmt|;
comment|//Fast forward time to future but before expiry of checkpoint
name|clock
operator|.
name|waitUntil
argument_list|(
name|cp
operator|.
name|getTimestamp
argument_list|()
operator|+
name|expiryTime
operator|-
name|maxAge
argument_list|)
expr_stmt|;
name|VersionGCStats
name|stats
init|=
name|gc
operator|.
name|gc
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|stats
operator|.
name|ignoredGCDueToCheckPoint
argument_list|)
expr_stmt|;
comment|//Fast forward time to future such that checkpoint get expired
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|expiryTime
operator|+
literal|1
argument_list|)
expr_stmt|;
name|stats
operator|=
name|gc
operator|.
name|gc
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"GC should be performed"
argument_list|,
name|stats
operator|.
name|ignoredGCDueToCheckPoint
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

