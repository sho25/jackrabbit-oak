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
name|segment
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
name|assertNotNull
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
name|ArrayList
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
name|HashSet
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
name|Random
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
name|Callable
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
name|Executors
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
name|Future
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
name|Predicate
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
name|segment
operator|.
name|memory
operator|.
name|MemoryStore
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
name|SegmentIdTableTest
block|{
comment|/**      * OAK-2752      */
annotation|@
name|Test
specifier|public
name|void
name|endlessSearchLoop
parameter_list|()
throws|throws
name|IOException
block|{
name|MemoryStore
name|store
init|=
operator|new
name|MemoryStore
argument_list|()
decl_stmt|;
specifier|final
name|SegmentIdTable
name|tbl
init|=
operator|new
name|SegmentIdTable
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SegmentId
argument_list|>
name|refs
init|=
operator|new
name|ArrayList
argument_list|<
name|SegmentId
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1024
condition|;
name|i
operator|++
control|)
block|{
name|refs
operator|.
name|add
argument_list|(
name|tbl
operator|.
name|getSegmentId
argument_list|(
name|i
argument_list|,
name|i
operator|%
literal|64
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Callable
argument_list|<
name|SegmentId
argument_list|>
name|c
init|=
operator|new
name|Callable
argument_list|<
name|SegmentId
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SegmentId
name|call
parameter_list|()
throws|throws
name|Exception
block|{
comment|// (2,1) doesn't exist
return|return
name|tbl
operator|.
name|getSegmentId
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|Future
argument_list|<
name|SegmentId
argument_list|>
name|f
init|=
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
operator|.
name|submit
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|SegmentId
name|s
init|=
literal|null
decl_stmt|;
try|try
block|{
name|s
operator|=
name|f
operator|.
name|get
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|s
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|s
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|randomized
parameter_list|()
throws|throws
name|IOException
block|{
name|MemoryStore
name|store
init|=
operator|new
name|MemoryStore
argument_list|()
decl_stmt|;
specifier|final
name|SegmentIdTable
name|tbl
init|=
operator|new
name|SegmentIdTable
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SegmentId
argument_list|>
name|refs
init|=
operator|new
name|ArrayList
argument_list|<
name|SegmentId
argument_list|>
argument_list|()
decl_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|16
operator|*
literal|1024
condition|;
name|i
operator|++
control|)
block|{
name|refs
operator|.
name|add
argument_list|(
name|tbl
operator|.
name|getSegmentId
argument_list|(
name|r
operator|.
name|nextLong
argument_list|()
argument_list|,
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|16
operator|*
literal|1024
argument_list|,
name|tbl
operator|.
name|getEntryCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|16
operator|*
literal|2048
argument_list|,
name|tbl
operator|.
name|getMapSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|tbl
operator|.
name|getMapRebuildCount
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|=
operator|new
name|Random
argument_list|(
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|16
operator|*
literal|1024
condition|;
name|i
operator|++
control|)
block|{
name|refs
operator|.
name|add
argument_list|(
name|tbl
operator|.
name|getSegmentId
argument_list|(
name|r
operator|.
name|nextLong
argument_list|()
argument_list|,
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|16
operator|*
literal|1024
argument_list|,
name|tbl
operator|.
name|getEntryCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|16
operator|*
literal|2048
argument_list|,
name|tbl
operator|.
name|getMapSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|tbl
operator|.
name|getMapRebuildCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|clearTable
parameter_list|()
throws|throws
name|IOException
block|{
name|MemoryStore
name|store
init|=
operator|new
name|MemoryStore
argument_list|()
decl_stmt|;
specifier|final
name|SegmentIdTable
name|tbl
init|=
operator|new
name|SegmentIdTable
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SegmentId
argument_list|>
name|refs
init|=
operator|new
name|ArrayList
argument_list|<
name|SegmentId
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|originalCount
init|=
literal|8
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|originalCount
condition|;
name|i
operator|++
control|)
block|{
name|refs
operator|.
name|add
argument_list|(
name|tbl
operator|.
name|getSegmentId
argument_list|(
name|i
argument_list|,
name|i
operator|%
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|originalCount
argument_list|,
name|tbl
operator|.
name|getEntryCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tbl
operator|.
name|getMapRebuildCount
argument_list|()
argument_list|)
expr_stmt|;
name|tbl
operator|.
name|clearSegmentIdTables
argument_list|(
operator|new
name|Predicate
argument_list|<
name|SegmentId
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|SegmentId
name|id
parameter_list|)
block|{
return|return
name|id
operator|.
name|getMostSignificantBits
argument_list|()
operator|<
literal|4
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tbl
operator|.
name|getEntryCount
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|SegmentId
name|id
range|:
name|refs
control|)
block|{
if|if
condition|(
name|id
operator|.
name|getMostSignificantBits
argument_list|()
operator|>=
literal|4
condition|)
block|{
name|SegmentId
name|id2
init|=
name|tbl
operator|.
name|getSegmentId
argument_list|(
name|id
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|,
name|id
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SegmentId
argument_list|>
name|list
init|=
name|tbl
operator|.
name|getRawSegmentIdList
argument_list|()
decl_stmt|;
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|!=
operator|new
name|HashSet
argument_list|<
name|SegmentId
argument_list|>
argument_list|(
name|list
argument_list|)
operator|.
name|size
argument_list|()
condition|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"duplicate entry "
operator|+
name|list
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|id
operator|==
name|id2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|justHashCollisions
parameter_list|()
throws|throws
name|IOException
block|{
name|MemoryStore
name|store
init|=
operator|new
name|MemoryStore
argument_list|()
decl_stmt|;
specifier|final
name|SegmentIdTable
name|tbl
init|=
operator|new
name|SegmentIdTable
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SegmentId
argument_list|>
name|refs
init|=
operator|new
name|ArrayList
argument_list|<
name|SegmentId
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|originalCount
init|=
literal|1024
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|originalCount
condition|;
name|i
operator|++
control|)
block|{
comment|// modulo 128 to ensure we have conflicts
name|refs
operator|.
name|add
argument_list|(
name|tbl
operator|.
name|getSegmentId
argument_list|(
name|i
argument_list|,
name|i
operator|%
literal|128
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|originalCount
argument_list|,
name|tbl
operator|.
name|getEntryCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tbl
operator|.
name|getMapRebuildCount
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|SegmentId
argument_list|>
name|refs2
init|=
operator|new
name|ArrayList
argument_list|<
name|SegmentId
argument_list|>
argument_list|()
decl_stmt|;
name|tbl
operator|.
name|collectReferencedIds
argument_list|(
name|refs2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|refs
operator|.
name|size
argument_list|()
argument_list|,
name|refs2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|originalCount
argument_list|,
name|tbl
operator|.
name|getEntryCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// we don't expect that there was a refresh,
comment|// because there were just hash collisions
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tbl
operator|.
name|getMapRebuildCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|gc
parameter_list|()
throws|throws
name|IOException
block|{
name|MemoryStore
name|store
init|=
operator|new
name|MemoryStore
argument_list|()
decl_stmt|;
specifier|final
name|SegmentIdTable
name|tbl
init|=
operator|new
name|SegmentIdTable
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|SegmentId
argument_list|>
name|refs
init|=
operator|new
name|ArrayList
argument_list|<
name|SegmentId
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|originalCount
init|=
literal|1024
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|originalCount
condition|;
name|i
operator|++
control|)
block|{
comment|// modulo 128 to ensure we have conflicts
name|refs
operator|.
name|add
argument_list|(
name|tbl
operator|.
name|getSegmentId
argument_list|(
name|i
argument_list|,
name|i
operator|%
literal|128
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|originalCount
argument_list|,
name|tbl
operator|.
name|getEntryCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tbl
operator|.
name|getMapRebuildCount
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|refs
operator|.
name|size
argument_list|()
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
comment|// we need to remove the first entries,
comment|// because if we remove the last entries, then
comment|// getSegmentId would not detect that entries were freed up
name|refs
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|gcCalls
init|=
literal|0
init|;
condition|;
name|gcCalls
operator|++
control|)
block|{
comment|// needed here, so some entries can be garbage collected
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
for|for
control|(
name|SegmentId
name|id
range|:
name|refs
control|)
block|{
name|SegmentId
name|id2
init|=
name|tbl
operator|.
name|getSegmentId
argument_list|(
name|id
operator|.
name|getMostSignificantBits
argument_list|()
argument_list|,
name|id
operator|.
name|getLeastSignificantBits
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|id2
operator|==
name|id
argument_list|)
expr_stmt|;
block|}
comment|// because we found each entry, we expect the refresh count is the same
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tbl
operator|.
name|getMapRebuildCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// even thought this does not increase the entry count a lot,
comment|// it is supposed to detect that entries were removed,
comment|// and force a refresh, which would get rid of the unreferenced ids
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|tbl
operator|.
name|getSegmentId
argument_list|(
name|i
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tbl
operator|.
name|getEntryCount
argument_list|()
operator|<
name|originalCount
condition|)
block|{
break|break;
block|}
elseif|else
if|if
condition|(
name|gcCalls
operator|>
literal|10
condition|)
block|{
name|fail
argument_list|(
literal|"No entries were garbage collected after 10 times System.gc()"
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|tbl
operator|.
name|getMapRebuildCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

