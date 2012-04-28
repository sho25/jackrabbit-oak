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
name|core
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|CoreValue
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
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|HashMap
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
name|Map
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

begin_class
specifier|public
class|class
name|RootImplTest
extends|extends
name|AbstractOakTest
block|{
annotation|@
name|Override
specifier|protected
name|NodeState
name|createInitialState
parameter_list|(
name|MicroKernel
name|microKernel
parameter_list|)
block|{
name|String
name|jsop
init|=
literal|"+\"test\":{\"a\":1,\"b\":2,\"c\":3,"
operator|+
literal|"\"x\":{},\"y\":{},\"z\":{}}"
decl_stmt|;
name|microKernel
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
name|jsop
argument_list|,
name|microKernel
operator|.
name|getHeadRevision
argument_list|()
argument_list|,
literal|"test data"
argument_list|)
expr_stmt|;
return|return
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|"test"
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getChild
parameter_list|()
block|{
name|RootImpl
name|root
init|=
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
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
name|RootImpl
name|root
init|=
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
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
name|PropertyType
operator|.
name|LONG
argument_list|,
name|propertyState
operator|.
name|getValue
argument_list|()
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|propertyState
operator|.
name|getValue
argument_list|()
operator|.
name|getLong
argument_list|()
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
name|RootImpl
name|root
init|=
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
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
literal|"x"
argument_list|,
literal|"y"
argument_list|,
literal|"z"
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
name|RootImpl
name|root
init|=
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
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
name|Map
argument_list|<
name|String
argument_list|,
name|CoreValue
argument_list|>
name|expectedProperties
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|CoreValue
argument_list|>
argument_list|()
decl_stmt|;
name|expectedProperties
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|expectedProperties
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|expectedProperties
operator|.
name|put
argument_list|(
literal|"c"
argument_list|,
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
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
name|CoreValue
name|value
init|=
name|expectedProperties
operator|.
name|remove
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|property
operator|.
name|isArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|,
name|property
operator|.
name|getValue
argument_list|()
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
name|RootImpl
name|root
init|=
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
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
name|RootImpl
name|root
init|=
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
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
name|RootImpl
name|root
init|=
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
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
name|removeChild
argument_list|(
literal|"x"
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
name|RootImpl
name|root
init|=
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
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
name|CoreValue
name|value
init|=
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"value"
argument_list|)
decl_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"new"
argument_list|,
name|value
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
name|value
argument_list|,
name|property
operator|.
name|getValue
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
name|value
argument_list|,
name|property
operator|.
name|getValue
argument_list|()
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
name|RootImpl
name|root
init|=
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
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
name|move
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|RootImpl
name|root
init|=
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
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
name|y
init|=
name|tree
operator|.
name|getChild
argument_list|(
literal|"y"
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
name|root
operator|.
name|move
argument_list|(
literal|"x"
argument_list|,
literal|"y/xx"
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
name|assertTrue
argument_list|(
name|y
operator|.
name|hasChild
argument_list|(
literal|"xx"
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
name|assertTrue
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|getChild
argument_list|(
literal|"y"
argument_list|)
operator|.
name|hasChild
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|rename
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|RootImpl
name|root
init|=
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
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
name|root
operator|.
name|move
argument_list|(
literal|"x"
argument_list|,
literal|"xx"
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
name|assertTrue
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"xx"
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
name|assertTrue
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|copy
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|RootImpl
name|root
init|=
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
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
name|y
init|=
name|tree
operator|.
name|getChild
argument_list|(
literal|"y"
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
name|root
operator|.
name|copy
argument_list|(
literal|"x"
argument_list|,
literal|"y/xx"
argument_list|)
expr_stmt|;
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
name|assertTrue
argument_list|(
name|y
operator|.
name|hasChild
argument_list|(
literal|"xx"
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
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|getChild
argument_list|(
literal|"y"
argument_list|)
operator|.
name|hasChild
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|deepCopy
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|RootImpl
name|root
init|=
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
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
name|y
init|=
name|tree
operator|.
name|getChild
argument_list|(
literal|"y"
argument_list|)
decl_stmt|;
name|root
operator|.
name|getTree
argument_list|(
literal|"x"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"x1"
argument_list|)
expr_stmt|;
name|root
operator|.
name|copy
argument_list|(
literal|"x"
argument_list|,
literal|"y/xx"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|y
operator|.
name|hasChild
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|y
operator|.
name|getChild
argument_list|(
literal|"xx"
argument_list|)
operator|.
name|hasChild
argument_list|(
literal|"x1"
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
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|hasChild
argument_list|(
literal|"y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|getChild
argument_list|(
literal|"y"
argument_list|)
operator|.
name|hasChild
argument_list|(
literal|"xx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tree
operator|.
name|getChild
argument_list|(
literal|"y"
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"xx"
argument_list|)
operator|.
name|hasChild
argument_list|(
literal|"x1"
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|x
init|=
name|tree
operator|.
name|getChild
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|Tree
name|xx
init|=
name|tree
operator|.
name|getChild
argument_list|(
literal|"y"
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"xx"
argument_list|)
decl_stmt|;
name|checkEqual
argument_list|(
name|x
argument_list|,
name|xx
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
name|RootImpl
name|root
init|=
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
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
name|removeChild
argument_list|(
literal|"x"
argument_list|)
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
name|RootImpl
name|root
init|=
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
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
name|CoreValue
name|value
init|=
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"a"
argument_list|,
name|value
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
name|value
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
name|value
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
name|RootImpl
name|root
init|=
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
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
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"V1"
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
name|RootImpl
name|root
init|=
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
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
name|Status
operator|.
name|NEW
argument_list|,
name|tree
operator|.
name|getChildStatus
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
name|Status
operator|.
name|EXISTING
argument_list|,
name|tree
operator|.
name|getChildStatus
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
name|Status
operator|.
name|MODIFIED
argument_list|,
name|tree
operator|.
name|getChildStatus
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
name|Status
operator|.
name|EXISTING
argument_list|,
name|tree
operator|.
name|getChildStatus
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
name|removeChild
argument_list|(
literal|"another"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Status
operator|.
name|MODIFIED
argument_list|,
name|tree
operator|.
name|getChildStatus
argument_list|(
literal|"new"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Status
operator|.
name|REMOVED
argument_list|,
name|tree
operator|.
name|getChild
argument_list|(
literal|"new"
argument_list|)
operator|.
name|getChildStatus
argument_list|(
literal|"another"
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
name|Status
operator|.
name|EXISTING
argument_list|,
name|tree
operator|.
name|getChildStatus
argument_list|(
literal|"new"
argument_list|)
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
name|assertNull
argument_list|(
name|tree
operator|.
name|getChild
argument_list|(
literal|"new"
argument_list|)
operator|.
name|getChildStatus
argument_list|(
literal|"another"
argument_list|)
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
name|RootImpl
name|root
init|=
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
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
name|CoreValue
name|value1
init|=
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"V1"
argument_list|)
decl_stmt|;
name|CoreValue
name|value2
init|=
name|valueFactory
operator|.
name|createValue
argument_list|(
literal|"V2"
argument_list|)
decl_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"new"
argument_list|,
name|value1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
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
name|value2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
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
name|RootImpl
name|root
init|=
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
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
name|Status
operator|.
name|EXISTING
argument_list|,
name|tree
operator|.
name|getChildStatus
argument_list|(
literal|"one"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
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
name|getChildStatus
argument_list|(
literal|"two"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"WIP"
argument_list|)
comment|// TODO: move to oak-bench
specifier|public
name|void
name|largeChildList
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|RootImpl
name|root
init|=
operator|new
name|RootImpl
argument_list|(
name|store
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
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
specifier|private
specifier|static
name|void
name|checkEqual
parameter_list|(
name|Tree
name|tree1
parameter_list|,
name|Tree
name|tree2
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|tree1
operator|.
name|getChildrenCount
argument_list|()
argument_list|,
name|tree2
operator|.
name|getChildrenCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|tree1
operator|.
name|getPropertyCount
argument_list|()
argument_list|,
name|tree2
operator|.
name|getPropertyCount
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|PropertyState
name|property1
range|:
name|tree1
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|property1
argument_list|,
name|tree2
operator|.
name|getProperty
argument_list|(
name|property1
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Tree
name|child1
range|:
name|tree1
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|checkEqual
argument_list|(
name|child1
argument_list|,
name|tree2
operator|.
name|getChild
argument_list|(
name|child1
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

