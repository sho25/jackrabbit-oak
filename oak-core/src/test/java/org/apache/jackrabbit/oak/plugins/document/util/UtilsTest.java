begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
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
name|commons
operator|.
name|PathUtils
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
name|DocumentMK
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
name|DocumentNodeStore
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
name|junit
operator|.
name|Ignore
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
name|assertNull
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
name|assertSame
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

begin_comment
comment|/**  * Tests for {@link Utils}.  */
end_comment

begin_class
specifier|public
class|class
name|UtilsTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|getPreviousIdFor
parameter_list|()
block|{
name|Revision
name|r
init|=
operator|new
name|Revision
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"2:p/"
operator|+
name|r
operator|.
name|toString
argument_list|()
operator|+
literal|"/0"
argument_list|,
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
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3:p/test/"
operator|+
name|r
operator|.
name|toString
argument_list|()
operator|+
literal|"/1"
argument_list|,
name|Utils
operator|.
name|getPreviousIdFor
argument_list|(
literal|"/test"
argument_list|,
name|r
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"15:p/a/b/c/d/e/f/g/h/i/j/k/l/m/"
operator|+
name|r
operator|.
name|toString
argument_list|()
operator|+
literal|"/3"
argument_list|,
name|Utils
operator|.
name|getPreviousIdFor
argument_list|(
literal|"/a/b/c/d/e/f/g/h/i/j/k/l/m"
argument_list|,
name|r
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getParentIdFromLowerLimit
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|"1:/foo"
argument_list|,
name|Utils
operator|.
name|getParentIdFromLowerLimit
argument_list|(
name|Utils
operator|.
name|getKeyLowerLimit
argument_list|(
literal|"/foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1:/foo"
argument_list|,
name|Utils
operator|.
name|getParentIdFromLowerLimit
argument_list|(
literal|"2:/foo/bar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getParentId
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|longPath
init|=
name|PathUtils
operator|.
name|concat
argument_list|(
literal|"/"
operator|+
name|Strings
operator|.
name|repeat
argument_list|(
literal|"p"
argument_list|,
name|Utils
operator|.
name|PATH_LONG
operator|+
literal|1
argument_list|)
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Utils
operator|.
name|isLongPath
argument_list|(
name|longPath
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|Utils
operator|.
name|getParentId
argument_list|(
name|Utils
operator|.
name|getIdFromPath
argument_list|(
name|longPath
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|Utils
operator|.
name|getParentId
argument_list|(
name|Utils
operator|.
name|getIdFromPath
argument_list|(
literal|"/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1:/foo"
argument_list|,
name|Utils
operator|.
name|getParentId
argument_list|(
literal|"2:/foo/bar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getDepthFromId
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|Utils
operator|.
name|getDepthFromId
argument_list|(
literal|"1:/x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|Utils
operator|.
name|getDepthFromId
argument_list|(
literal|"2:/x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|Utils
operator|.
name|getDepthFromId
argument_list|(
literal|"10:/x"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"Performance test"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|performance_getPreviousIdFor
parameter_list|()
block|{
name|Revision
name|r
init|=
operator|new
name|Revision
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|String
name|path
init|=
literal|"/some/test/path/foo"
decl_stmt|;
comment|// warm up
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1
operator|*
literal|1000
operator|*
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|Utils
operator|.
name|getPreviousIdFor
argument_list|(
name|path
argument_list|,
name|r
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
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
literal|10
operator|*
literal|1000
operator|*
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|Utils
operator|.
name|getPreviousIdFor
argument_list|(
name|path
argument_list|,
name|r
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|time
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|time
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|time
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"Performance test"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|performance_revisionToString
parameter_list|()
block|{
name|Revision
name|r
init|=
operator|new
name|Revision
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// warm up
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1
operator|*
literal|1000
operator|*
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|r
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
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
literal|30
operator|*
literal|1000
operator|*
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|r
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|time
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|time
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|time
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|max
parameter_list|()
block|{
name|Revision
name|a
init|=
operator|new
name|Revision
argument_list|(
literal|42
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Revision
name|b
init|=
operator|new
name|Revision
argument_list|(
literal|43
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|b
argument_list|,
name|Utils
operator|.
name|max
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
argument_list|)
expr_stmt|;
name|Revision
name|a1
init|=
operator|new
name|Revision
argument_list|(
literal|42
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|a1
argument_list|,
name|Utils
operator|.
name|max
argument_list|(
name|a
argument_list|,
name|a1
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|a
argument_list|,
name|Utils
operator|.
name|max
argument_list|(
name|a
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|a
argument_list|,
name|Utils
operator|.
name|max
argument_list|(
literal|null
argument_list|,
name|a
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|Utils
operator|.
name|max
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getAllDocuments
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|DocumentNodeStore
name|store
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
try|try
block|{
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"test-"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|store
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
expr_stmt|;
name|assertEquals
argument_list|(
literal|1001
comment|/* root + 1000 children */
argument_list|,
name|Iterables
operator|.
name|size
argument_list|(
name|Utils
operator|.
name|getAllDocuments
argument_list|(
name|store
operator|.
name|getDocumentStore
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|store
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|getMaxExternalRevisionTime
parameter_list|()
block|{
name|int
name|localClusterId
init|=
literal|1
decl_stmt|;
name|List
argument_list|<
name|Revision
argument_list|>
name|revs
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
name|long
name|revTime
init|=
name|Utils
operator|.
name|getMaxExternalTimestamp
argument_list|(
name|revs
argument_list|,
name|localClusterId
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|,
name|revTime
argument_list|)
expr_stmt|;
name|revs
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|Revision
operator|.
name|fromString
argument_list|(
literal|"r1-0-1"
argument_list|)
argument_list|)
expr_stmt|;
name|revTime
operator|=
name|Utils
operator|.
name|getMaxExternalTimestamp
argument_list|(
name|revs
argument_list|,
name|localClusterId
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Long
operator|.
name|MIN_VALUE
argument_list|,
name|revTime
argument_list|)
expr_stmt|;
name|revs
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|Revision
operator|.
name|fromString
argument_list|(
literal|"r1-0-1"
argument_list|)
argument_list|,
name|Revision
operator|.
name|fromString
argument_list|(
literal|"r2-0-2"
argument_list|)
argument_list|)
expr_stmt|;
name|revTime
operator|=
name|Utils
operator|.
name|getMaxExternalTimestamp
argument_list|(
name|revs
argument_list|,
name|localClusterId
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|revTime
argument_list|)
expr_stmt|;
name|revs
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|Revision
operator|.
name|fromString
argument_list|(
literal|"r3-0-1"
argument_list|)
argument_list|,
name|Revision
operator|.
name|fromString
argument_list|(
literal|"r2-0-2"
argument_list|)
argument_list|)
expr_stmt|;
name|revTime
operator|=
name|Utils
operator|.
name|getMaxExternalTimestamp
argument_list|(
name|revs
argument_list|,
name|localClusterId
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|revTime
argument_list|)
expr_stmt|;
name|revs
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|Revision
operator|.
name|fromString
argument_list|(
literal|"r1-0-1"
argument_list|)
argument_list|,
name|Revision
operator|.
name|fromString
argument_list|(
literal|"r2-0-2"
argument_list|)
argument_list|,
name|Revision
operator|.
name|fromString
argument_list|(
literal|"r2-0-3"
argument_list|)
argument_list|)
expr_stmt|;
name|revTime
operator|=
name|Utils
operator|.
name|getMaxExternalTimestamp
argument_list|(
name|revs
argument_list|,
name|localClusterId
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|revTime
argument_list|)
expr_stmt|;
name|revs
operator|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|Revision
operator|.
name|fromString
argument_list|(
literal|"r1-0-1"
argument_list|)
argument_list|,
name|Revision
operator|.
name|fromString
argument_list|(
literal|"r3-0-2"
argument_list|)
argument_list|,
name|Revision
operator|.
name|fromString
argument_list|(
literal|"r2-0-3"
argument_list|)
argument_list|)
expr_stmt|;
name|revTime
operator|=
name|Utils
operator|.
name|getMaxExternalTimestamp
argument_list|(
name|revs
argument_list|,
name|localClusterId
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|revTime
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

