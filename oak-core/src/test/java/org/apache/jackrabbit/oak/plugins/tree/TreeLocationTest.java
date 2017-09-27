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
name|tree
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
name|Type
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
name|memory
operator|.
name|PropertyStates
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
name|org
operator|.
name|mockito
operator|.
name|Mockito
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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
specifier|public
class|class
name|TreeLocationTest
block|{
specifier|private
name|Tree
name|rootTree
decl_stmt|;
specifier|private
name|Tree
name|nonExisting
decl_stmt|;
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
name|Exception
block|{
name|rootTree
operator|=
name|mockTree
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rootTree
operator|.
name|hasProperty
argument_list|(
literal|"p"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rootTree
operator|.
name|getProperty
argument_list|(
literal|"p"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"p"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|nonExisting
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|nonExisting
operator|.
name|exists
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|nonExisting
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"nonExisting"
argument_list|)
expr_stmt|;
name|Tree
name|x
init|=
name|mockTree
argument_list|(
literal|"/x"
argument_list|,
name|rootTree
argument_list|)
decl_stmt|;
name|Tree
name|subTree
init|=
name|mockTree
argument_list|(
literal|"/z"
argument_list|,
name|rootTree
argument_list|)
decl_stmt|;
name|Tree
name|child
init|=
name|mockTree
argument_list|(
literal|"/z/child"
argument_list|,
name|subTree
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|child
operator|.
name|hasProperty
argument_list|(
literal|"p"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|child
operator|.
name|getProperty
argument_list|(
literal|"p"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"p"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|subTree
operator|.
name|getChild
argument_list|(
literal|"child"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|child
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rootTree
operator|.
name|getChild
argument_list|(
literal|"z"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|subTree
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rootTree
operator|.
name|getChild
argument_list|(
literal|"x"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|x
argument_list|)
expr_stmt|;
name|root
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Root
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|rootTree
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Tree
name|mockTree
parameter_list|(
name|String
name|path
parameter_list|,
name|Tree
name|parent
parameter_list|)
block|{
name|Tree
name|t
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|t
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|t
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|PathUtils
operator|.
name|getName
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|when
argument_list|(
name|t
operator|.
name|getParent
argument_list|()
argument_list|)
operator|.
name|thenThrow
argument_list|(
name|IllegalStateException
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|t
operator|.
name|isRoot
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|when
argument_list|(
name|t
operator|.
name|getParent
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|t
operator|.
name|isRoot
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|when
argument_list|(
name|t
operator|.
name|exists
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|t
operator|.
name|hasProperty
argument_list|(
literal|"nonExisting"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|t
operator|.
name|hasChild
argument_list|(
literal|"nonExisting"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|t
operator|.
name|getChild
argument_list|(
literal|"nonExisting"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|nonExisting
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNullLocation
parameter_list|()
block|{
name|TreeLocation
name|nullLocation
init|=
name|TreeLocation
operator|.
name|create
argument_list|(
name|root
argument_list|)
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|nullLocation
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nullLocation
operator|.
name|getName
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|TreeLocation
name|child
init|=
name|nullLocation
operator|.
name|getChild
argument_list|(
literal|"any"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|child
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|child
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|child
operator|.
name|getProperty
argument_list|()
argument_list|)
expr_stmt|;
name|TreeLocation
name|child2
init|=
name|nullLocation
operator|.
name|getChild
argument_list|(
literal|"x"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|child2
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|child
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|child
operator|.
name|getProperty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParentOfRoot
parameter_list|()
block|{
name|TreeLocation
name|nullLocation
init|=
name|TreeLocation
operator|.
name|create
argument_list|(
name|root
argument_list|)
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|nullLocation
argument_list|,
name|nullLocation
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nullLocation
operator|.
name|getName
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNodeLocation
parameter_list|()
block|{
name|TreeLocation
name|x
init|=
name|TreeLocation
operator|.
name|create
argument_list|(
name|root
argument_list|,
literal|"/x"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|x
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rootTree
argument_list|,
name|x
operator|.
name|getParent
argument_list|()
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPropertyLocation
parameter_list|()
block|{
name|TreeLocation
name|propLocation
init|=
name|TreeLocation
operator|.
name|create
argument_list|(
name|root
argument_list|,
literal|"/p"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|propLocation
operator|.
name|getProperty
argument_list|()
argument_list|)
expr_stmt|;
name|TreeLocation
name|abc
init|=
name|propLocation
operator|.
name|getChild
argument_list|(
literal|"b"
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"c"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"/p/b/c"
argument_list|,
name|abc
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|abc
operator|.
name|getProperty
argument_list|()
argument_list|)
expr_stmt|;
name|TreeLocation
name|ab
init|=
name|abc
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"/p/b"
argument_list|,
name|ab
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|ab
operator|.
name|getProperty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|propLocation
operator|.
name|getProperty
argument_list|()
argument_list|,
name|ab
operator|.
name|getParent
argument_list|()
operator|.
name|getProperty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getDeepLocation
parameter_list|()
block|{
name|TreeLocation
name|child
init|=
name|TreeLocation
operator|.
name|create
argument_list|(
name|root
argument_list|,
literal|"/z/child"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|child
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|child
operator|.
name|getProperty
argument_list|()
argument_list|)
expr_stmt|;
name|TreeLocation
name|p
init|=
name|TreeLocation
operator|.
name|create
argument_list|(
name|root
argument_list|,
literal|"/z/child/p"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|p
operator|.
name|getProperty
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|p
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/z/child/p"
argument_list|,
name|p
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|TreeLocation
name|n
init|=
name|TreeLocation
operator|.
name|create
argument_list|(
name|root
argument_list|,
literal|"/z/child/p/3/4"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|n
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|n
operator|.
name|getProperty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/z/child/p/3/4"
argument_list|,
name|n
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|TreeLocation
name|t
init|=
name|n
operator|.
name|getParent
argument_list|()
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|t
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|t
operator|.
name|getProperty
argument_list|()
argument_list|)
expr_stmt|;
name|TreeLocation
name|t2
init|=
name|t
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|t2
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/z/child"
argument_list|,
name|t2
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

