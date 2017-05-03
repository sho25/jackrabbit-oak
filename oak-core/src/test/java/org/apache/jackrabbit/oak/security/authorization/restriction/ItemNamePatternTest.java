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
name|security
operator|.
name|authorization
operator|.
name|restriction
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
name|ImmutableSet
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
name|AbstractSecurityTest
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
name|memory
operator|.
name|PropertyStates
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
name|nodetype
operator|.
name|NodeTypeConstants
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
name|util
operator|.
name|NodeUtil
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
name|assertNotEquals
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

begin_class
specifier|public
class|class
name|ItemNamePatternTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ItemNamePattern
name|pattern
init|=
operator|new
name|ItemNamePattern
argument_list|(
name|names
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testMatchesItem
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeUtil
name|rootTree
init|=
operator|new
name|NodeUtil
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|matching
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|,
literal|"d/e/a"
argument_list|,
literal|"a/b/c/d/b"
argument_list|,
literal|"test/c"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|relPath
range|:
name|matching
control|)
block|{
name|Tree
name|testTree
init|=
name|rootTree
operator|.
name|getOrAddTree
argument_list|(
name|relPath
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
operator|.
name|getTree
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|pattern
operator|.
name|matches
argument_list|(
name|testTree
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pattern
operator|.
name|matches
argument_list|(
name|testTree
argument_list|,
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"a"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pattern
operator|.
name|matches
argument_list|(
name|testTree
argument_list|,
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"f"
argument_list|,
literal|"anyval"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|testTree
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|notMatching
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"d"
argument_list|,
literal|"b/d"
argument_list|,
literal|"d/e/f"
argument_list|,
literal|"c/b/abc"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|relPath
range|:
name|notMatching
control|)
block|{
name|Tree
name|testTree
init|=
name|rootTree
operator|.
name|getOrAddTree
argument_list|(
name|relPath
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
operator|.
name|getTree
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|pattern
operator|.
name|matches
argument_list|(
name|testTree
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pattern
operator|.
name|matches
argument_list|(
name|testTree
argument_list|,
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"a"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pattern
operator|.
name|matches
argument_list|(
name|testTree
argument_list|,
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"f"
argument_list|,
literal|"anyval"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|testTree
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchesPath
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|matching
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/a"
argument_list|,
literal|"/b"
argument_list|,
literal|"/c"
argument_list|,
literal|"/d/e/a"
argument_list|,
literal|"/a/b/c/d/b"
argument_list|,
literal|"/test/c"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|matching
control|)
block|{
name|assertTrue
argument_list|(
name|pattern
operator|.
name|matches
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|notMatching
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/"
argument_list|,
literal|"/d"
argument_list|,
literal|"/b/d"
argument_list|,
literal|"/d/e/f"
argument_list|,
literal|"/c/b/abc"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|notMatching
control|)
block|{
name|assertFalse
argument_list|(
name|pattern
operator|.
name|matches
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMatchesNull
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|pattern
operator|.
name|matches
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testToString
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|names
operator|.
name|toString
argument_list|()
argument_list|,
name|pattern
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHashCode
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|names
operator|.
name|hashCode
argument_list|()
argument_list|,
name|pattern
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|pattern
argument_list|,
name|pattern
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pattern
argument_list|,
operator|new
name|ItemNamePattern
argument_list|(
name|names
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotEquals
parameter_list|()
block|{
name|assertNotEquals
argument_list|(
name|pattern
argument_list|,
operator|new
name|ItemNamePattern
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|pattern
argument_list|,
operator|new
name|PrefixPattern
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

