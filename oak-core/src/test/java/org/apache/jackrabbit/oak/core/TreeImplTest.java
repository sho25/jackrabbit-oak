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
name|core
package|;
end_package

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
name|Sets
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
name|api
operator|.
name|ContentSession
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
name|PropertyState
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
name|Root
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
name|api
operator|.
name|Tree
operator|.
name|Status
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
name|LongPropertyState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|Type
operator|.
name|LONG
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
name|Type
operator|.
name|STRING
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * TreeImplTest...  */
end_comment

begin_class
specifier|public
class|class
name|TreeImplTest
block|{
specifier|private
name|Root
name|root
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
name|ContentSession
name|session
init|=
operator|new
name|Oak
argument_list|()
operator|.
name|createContentSession
argument_list|()
decl_stmt|;
comment|// Add test content
name|root
operator|=
name|session
operator|.
name|getLatestRoot
argument_list|()
expr_stmt|;
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"b"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"c"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|tree
operator|.
name|addChild
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
name|tree
operator|.
name|addChild
argument_list|(
literal|"y"
argument_list|)
expr_stmt|;
name|tree
operator|.
name|addChild
argument_list|(
literal|"z"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// Acquire a fresh new root to avoid problems from lingering state
name|root
operator|=
name|session
operator|.
name|getLatestRoot
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|root
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getChild
parameter_list|()
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Tree
name|child
init|=
name|tree
operator|.
name|getChild
argument_list|(
literal|"any"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|child
argument_list|)
expr_stmt|;
name|child
operator|=
name|tree
operator|.
name|getChild
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getProperty
parameter_list|()
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|PropertyState
name|propertyState
init|=
name|tree
operator|.
name|getProperty
argument_list|(
literal|"any"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|propertyState
argument_list|)
expr_stmt|;
name|propertyState
operator|=
name|tree
operator|.
name|getProperty
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|propertyState
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|propertyState
operator|.
name|isArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|LONG
argument_list|,
name|propertyState
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
name|long
operator|)
name|propertyState
operator|.
name|getValue
argument_list|(
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getChildren
parameter_list|()
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Iterable
argument_list|<
name|Tree
argument_list|>
name|children
init|=
name|tree
operator|.
name|getChildren
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expectedPaths
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|expectedPaths
argument_list|,
literal|"/x"
argument_list|,
literal|"/y"
argument_list|,
literal|"/z"
argument_list|)
expr_stmt|;
for|for
control|(
name|Tree
name|child
range|:
name|children
control|)
block|{
name|assertTrue
argument_list|(
name|expectedPaths
operator|.
name|remove
argument_list|(
name|child
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|expectedPaths
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tree
operator|.
name|getChildrenCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getProperties
parameter_list|()
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|PropertyState
argument_list|>
name|expectedProperties
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|LongPropertyState
operator|.
name|createLongProperty
argument_list|(
literal|"a"
argument_list|,
literal|1L
argument_list|)
argument_list|,
name|LongPropertyState
operator|.
name|createLongProperty
argument_list|(
literal|"b"
argument_list|,
literal|2L
argument_list|)
argument_list|,
name|LongPropertyState
operator|.
name|createLongProperty
argument_list|(
literal|"c"
argument_list|,
literal|3L
argument_list|)
argument_list|)
decl_stmt|;
name|Iterable
argument_list|<
name|?
extends|extends
name|PropertyState
argument_list|>
name|properties
init|=
name|tree
operator|.
name|getProperties
argument_list|()
decl_stmt|;
for|for
control|(
name|PropertyState
name|property
range|:
name|properties
control|)
block|{
name|assertTrue
argument_list|(
name|expectedProperties
operator|.
name|remove
argument_list|(
name|property
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|expectedProperties
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tree
operator|.
name|getPropertyCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addChild
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|added
init|=
name|tree
operator|.
name|addChild
argument_list|(
literal|"new"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|added
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"new"
argument_list|,
name|added
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|tree
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
name|tree
operator|.
name|getChild
argument_list|(
literal|"new"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"more"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|getChild
argument_list|(
literal|"new"
argument_list|)
operator|.
name|hasChild
argument_list|(
literal|"more"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addExistingChild
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
name|tree
operator|.
name|addChild
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|tree
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|added
init|=
name|tree
operator|.
name|addChild
argument_list|(
literal|"new"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|added
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"new"
argument_list|,
name|added
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeChild
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|tree
operator|.
name|getChild
argument_list|(
literal|"x"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|tree
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|setProperty
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|tree
operator|.
name|hasProperty
argument_list|(
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"new"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|PropertyState
name|property
init|=
name|tree
operator|.
name|getProperty
argument_list|(
literal|"new"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|property
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"new"
argument_list|,
name|property
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value"
argument_list|,
name|property
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|tree
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|property
operator|=
name|tree
operator|.
name|getProperty
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|property
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"new"
argument_list|,
name|property
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value"
argument_list|,
name|property
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeProperty
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|hasProperty
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|tree
operator|.
name|removeProperty
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tree
operator|.
name|hasProperty
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|tree
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tree
operator|.
name|hasProperty
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getChildrenCount
parameter_list|()
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tree
operator|.
name|getChildrenCount
argument_list|()
argument_list|)
expr_stmt|;
name|tree
operator|.
name|getChild
argument_list|(
literal|"x"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|tree
operator|.
name|getChildrenCount
argument_list|()
argument_list|)
expr_stmt|;
name|tree
operator|.
name|addChild
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tree
operator|.
name|getChildrenCount
argument_list|()
argument_list|)
expr_stmt|;
name|tree
operator|.
name|addChild
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tree
operator|.
name|getChildrenCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getPropertyCount
parameter_list|()
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tree
operator|.
name|getPropertyCount
argument_list|()
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tree
operator|.
name|getPropertyCount
argument_list|()
argument_list|)
expr_stmt|;
name|tree
operator|.
name|removeProperty
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|tree
operator|.
name|getPropertyCount
argument_list|()
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"x"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tree
operator|.
name|getPropertyCount
argument_list|()
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|tree
operator|.
name|getPropertyCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addAndRemoveProperty
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"P0"
argument_list|,
literal|"V1"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|tree
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|hasProperty
argument_list|(
literal|"P0"
argument_list|)
argument_list|)
expr_stmt|;
name|tree
operator|.
name|removeProperty
argument_list|(
literal|"P0"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|tree
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tree
operator|.
name|hasProperty
argument_list|(
literal|"P0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodeStatus
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|tree
operator|.
name|addChild
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Tree
operator|.
name|Status
operator|.
name|NEW
argument_list|,
name|tree
operator|.
name|getChild
argument_list|(
literal|"new"
argument_list|)
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|tree
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Tree
operator|.
name|Status
operator|.
name|EXISTING
argument_list|,
name|tree
operator|.
name|getChild
argument_list|(
literal|"new"
argument_list|)
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|Tree
name|added
init|=
name|tree
operator|.
name|getChild
argument_list|(
literal|"new"
argument_list|)
decl_stmt|;
name|added
operator|.
name|addChild
argument_list|(
literal|"another"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Tree
operator|.
name|Status
operator|.
name|MODIFIED
argument_list|,
name|tree
operator|.
name|getChild
argument_list|(
literal|"new"
argument_list|)
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|tree
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Tree
operator|.
name|Status
operator|.
name|EXISTING
argument_list|,
name|tree
operator|.
name|getChild
argument_list|(
literal|"new"
argument_list|)
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|tree
operator|.
name|getChild
argument_list|(
literal|"new"
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"another"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|Tree
operator|.
name|Status
operator|.
name|MODIFIED
argument_list|,
name|tree
operator|.
name|getChild
argument_list|(
literal|"new"
argument_list|)
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|tree
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Tree
operator|.
name|Status
operator|.
name|EXISTING
argument_list|,
name|tree
operator|.
name|getChild
argument_list|(
literal|"new"
argument_list|)
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|tree
operator|.
name|getChild
argument_list|(
literal|"new"
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"another"
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|x
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/x"
argument_list|)
decl_stmt|;
name|Tree
name|y
init|=
name|x
operator|.
name|addChild
argument_list|(
literal|"y"
argument_list|)
decl_stmt|;
name|x
operator|.
name|remove
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|Status
operator|.
name|REMOVED
argument_list|,
name|x
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Status
operator|.
name|REMOVED
argument_list|,
name|y
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|propertyStatus
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"new"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Tree
operator|.
name|Status
operator|.
name|NEW
argument_list|,
name|tree
operator|.
name|getPropertyStatus
argument_list|(
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|tree
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Tree
operator|.
name|Status
operator|.
name|EXISTING
argument_list|,
name|tree
operator|.
name|getPropertyStatus
argument_list|(
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"new"
argument_list|,
literal|"value2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Tree
operator|.
name|Status
operator|.
name|MODIFIED
argument_list|,
name|tree
operator|.
name|getPropertyStatus
argument_list|(
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|tree
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Tree
operator|.
name|Status
operator|.
name|EXISTING
argument_list|,
name|tree
operator|.
name|getPropertyStatus
argument_list|(
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
name|tree
operator|.
name|removeProperty
argument_list|(
literal|"new"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Tree
operator|.
name|Status
operator|.
name|REMOVED
argument_list|,
name|tree
operator|.
name|getPropertyStatus
argument_list|(
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|tree
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|tree
operator|.
name|getPropertyStatus
argument_list|(
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|x
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/x"
argument_list|)
decl_stmt|;
name|x
operator|.
name|setProperty
argument_list|(
literal|"y"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|x
operator|.
name|remove
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|Status
operator|.
name|REMOVED
argument_list|,
name|x
operator|.
name|getPropertyStatus
argument_list|(
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|noTransitiveModifiedStatus
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|tree
operator|.
name|addChild
argument_list|(
literal|"one"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"two"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|tree
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|tree
operator|.
name|getChild
argument_list|(
literal|"one"
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"two"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"three"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Tree
operator|.
name|Status
operator|.
name|EXISTING
argument_list|,
name|tree
operator|.
name|getChild
argument_list|(
literal|"one"
argument_list|)
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Tree
operator|.
name|Status
operator|.
name|MODIFIED
argument_list|,
name|tree
operator|.
name|getChild
argument_list|(
literal|"one"
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"two"
argument_list|)
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|largeChildList
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|added
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|tree
operator|.
name|addChild
argument_list|(
literal|"large"
argument_list|)
expr_stmt|;
name|tree
operator|=
name|tree
operator|.
name|getChild
argument_list|(
literal|"large"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
literal|10000
condition|;
name|c
operator|++
control|)
block|{
name|String
name|name
init|=
literal|"n"
operator|+
name|c
decl_stmt|;
name|added
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|tree
operator|.
name|addChild
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|tree
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|tree
operator|=
name|tree
operator|.
name|getChild
argument_list|(
literal|"large"
argument_list|)
expr_stmt|;
for|for
control|(
name|Tree
name|child
range|:
name|tree
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|added
operator|.
name|remove
argument_list|(
name|child
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|added
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

