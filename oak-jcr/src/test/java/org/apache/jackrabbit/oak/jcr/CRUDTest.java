begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|jcr
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|containsString
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
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|InvalidItemStateException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PathNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|ConstraintViolationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeType
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
name|fixture
operator|.
name|NodeStoreFixture
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
name|CRUDTest
extends|extends
name|AbstractRepositoryTest
block|{
specifier|public
name|CRUDTest
parameter_list|(
name|NodeStoreFixture
name|fixture
parameter_list|)
block|{
name|super
argument_list|(
name|fixture
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/OAK-2488">OAK-2488</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testMixins
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|String
name|nodename
init|=
literal|"mixintest"
decl_stmt|;
name|Node
name|mixinTest
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|nodename
argument_list|,
literal|"nt:folder"
argument_list|)
decl_stmt|;
name|NodeType
index|[]
name|types
decl_stmt|;
name|types
operator|=
name|mixinTest
operator|.
name|getMixinNodeTypes
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|types
argument_list|)
argument_list|,
literal|0
argument_list|,
name|types
operator|.
name|length
argument_list|)
expr_stmt|;
name|mixinTest
operator|.
name|addMixin
argument_list|(
literal|"mix:versionable"
argument_list|)
expr_stmt|;
name|types
operator|=
name|mixinTest
operator|.
name|getMixinNodeTypes
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|types
argument_list|)
argument_list|,
literal|1
argument_list|,
name|types
operator|.
name|length
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|mixinTest
operator|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
name|nodename
argument_list|)
expr_stmt|;
name|mixinTest
operator|.
name|remove
argument_list|()
expr_stmt|;
name|mixinTest
operator|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|nodename
argument_list|,
literal|"nt:folder"
argument_list|)
expr_stmt|;
name|types
operator|=
name|mixinTest
operator|.
name|getMixinNodeTypes
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|types
argument_list|)
argument_list|,
literal|0
argument_list|,
name|types
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|// OAK-7652
annotation|@
name|Test
specifier|public
name|void
name|testMixinsDescendant
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|String
name|parentName
init|=
literal|"parent"
decl_stmt|;
name|String
name|nodeName
init|=
literal|"mixintest"
decl_stmt|;
name|String
name|nodeType
init|=
literal|"nt:folder"
decl_stmt|;
name|Node
name|mixinTest
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|parentName
argument_list|,
name|nodeType
argument_list|)
operator|.
name|addNode
argument_list|(
name|nodeName
argument_list|,
name|nodeType
argument_list|)
decl_stmt|;
name|NodeType
index|[]
name|types
decl_stmt|;
name|types
operator|=
name|mixinTest
operator|.
name|getMixinNodeTypes
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|types
argument_list|)
argument_list|,
literal|0
argument_list|,
name|types
operator|.
name|length
argument_list|)
expr_stmt|;
name|mixinTest
operator|.
name|addMixin
argument_list|(
literal|"mix:versionable"
argument_list|)
expr_stmt|;
name|types
operator|=
name|mixinTest
operator|.
name|getMixinNodeTypes
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|types
argument_list|)
argument_list|,
literal|1
argument_list|,
name|types
operator|.
name|length
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
name|parentName
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|mixinTest
operator|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|parentName
argument_list|,
name|nodeType
argument_list|)
operator|.
name|addNode
argument_list|(
name|nodeName
argument_list|,
name|nodeType
argument_list|)
expr_stmt|;
name|types
operator|=
name|mixinTest
operator|.
name|getMixinNodeTypes
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|types
argument_list|)
argument_list|,
literal|0
argument_list|,
name|types
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCRUD
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
comment|// Create
name|Node
name|hello
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"hello"
argument_list|)
decl_stmt|;
name|hello
operator|.
name|setProperty
argument_list|(
literal|"world"
argument_list|,
literal|"hello world"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// Read
name|assertEquals
argument_list|(
literal|"hello world"
argument_list|,
name|session
operator|.
name|getProperty
argument_list|(
literal|"/hello/world"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Update
name|session
operator|.
name|getNode
argument_list|(
literal|"/hello"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"world"
argument_list|,
literal|"Hello, World!"
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Hello, World!"
argument_list|,
name|session
operator|.
name|getProperty
argument_list|(
literal|"/hello/world"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Delete
name|session
operator|.
name|getNode
argument_list|(
literal|"/hello"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|session
operator|.
name|propertyExists
argument_list|(
literal|"/hello/world"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveBySetProperty
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
try|try
block|{
name|root
operator|.
name|setProperty
argument_list|(
literal|"test"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|root
operator|.
name|setProperty
argument_list|(
literal|"test"
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveBySetMVProperty
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
try|try
block|{
name|root
operator|.
name|setProperty
argument_list|(
literal|"test"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"abc"
block|,
literal|"def"
block|}
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|root
operator|.
name|setProperty
argument_list|(
literal|"test"
argument_list|,
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
comment|// success
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveMissingProperty
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
name|Property
name|p
init|=
name|root
operator|.
name|setProperty
argument_list|(
literal|"missing"
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|p
argument_list|)
expr_stmt|;
try|try
block|{
name|p
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"must throw InvalidItemStateException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidItemStateException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveMissingMVProperty
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
name|Property
name|p
init|=
name|root
operator|.
name|setProperty
argument_list|(
literal|"missing"
argument_list|,
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|p
argument_list|)
expr_stmt|;
try|try
block|{
name|p
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"must throw InvalidItemStateException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidItemStateException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRootPropertyPath
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Property
name|property
init|=
name|getAdminSession
argument_list|()
operator|.
name|getRootNode
argument_list|()
operator|.
name|getProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"/jcr:primaryType"
argument_list|,
name|property
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ConstraintViolationException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|nodeType
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|s
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|s
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"a"
argument_list|,
literal|"nt:folder"
argument_list|)
operator|.
name|addNode
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|s
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getNodeWithRelativePath
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|s
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|s
operator|.
name|getNode
argument_list|(
literal|"some-relative-path"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Session.getNode() with relative path must throw a RepositoryException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|getPropertyWithRelativePath
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|s
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|s
operator|.
name|getProperty
argument_list|(
literal|"some-relative-path"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Session.getProperty() with relative path must throw a RepositoryException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|getItemWithRelativePath
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|s
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
try|try
block|{
name|s
operator|.
name|getItem
argument_list|(
literal|"some-relative-path"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Session.getItem() with relative path must throw a RepositoryException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetPropertyDateWithTimeZone
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
specifier|final
name|Calendar
name|cal
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|cal
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"America/Chicago"
argument_list|)
argument_list|)
expr_stmt|;
name|cal
operator|.
name|setTimeInMillis
argument_list|(
literal|1239902100000L
argument_list|)
expr_stmt|;
name|root
operator|.
name|setProperty
argument_list|(
literal|"start"
argument_list|,
name|cal
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|root
operator|.
name|getProperty
argument_list|(
literal|"start"
argument_list|)
operator|.
name|getDate
argument_list|()
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|getProperty
argument_list|(
literal|"start"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|checkPathInInvalidItemStateException
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|s1
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|Node
name|root
init|=
name|s1
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
name|Node
name|a
init|=
name|root
operator|.
name|addNode
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|a
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|s1
operator|.
name|save
argument_list|()
expr_stmt|;
name|Session
name|s2
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|s2
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
literal|"a"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|s2
operator|.
name|save
argument_list|()
expr_stmt|;
name|s1
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|a
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidItemStateException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

