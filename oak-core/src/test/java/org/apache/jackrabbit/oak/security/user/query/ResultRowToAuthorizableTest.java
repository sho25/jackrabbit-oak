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
name|user
operator|.
name|query
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|Authorizable
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
name|api
operator|.
name|security
operator|.
name|user
operator|.
name|User
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
name|PropertyValue
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
name|ResultRow
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
name|plugins
operator|.
name|tree
operator|.
name|TreeUtil
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
name|security
operator|.
name|user
operator|.
name|UserManagerImpl
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
name|query
operator|.
name|QueryConstants
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
name|security
operator|.
name|user
operator|.
name|AuthorizableType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
name|javax
operator|.
name|jcr
operator|.
name|SimpleCredentials
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
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
name|mockito
operator|.
name|Mockito
operator|.
name|mock
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
name|ResultRowToAuthorizableTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|ResultRowToAuthorizable
name|groupRrta
decl_stmt|;
specifier|private
name|ResultRowToAuthorizable
name|userRrta
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
name|groupRrta
operator|=
operator|new
name|ResultRowToAuthorizable
argument_list|(
operator|new
name|UserManagerImpl
argument_list|(
name|root
argument_list|,
name|getPartialValueFactory
argument_list|()
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|)
argument_list|,
name|root
argument_list|,
name|AuthorizableType
operator|.
name|GROUP
argument_list|)
expr_stmt|;
name|userRrta
operator|=
operator|new
name|ResultRowToAuthorizable
argument_list|(
operator|new
name|UserManagerImpl
argument_list|(
name|root
argument_list|,
name|getPartialValueFactory
argument_list|()
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|)
argument_list|,
name|root
argument_list|,
name|AuthorizableType
operator|.
name|USER
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|ResultRow
name|createResultRow
parameter_list|(
annotation|@
name|NotNull
name|String
name|path
parameter_list|)
block|{
name|PropertyValue
name|propValue
init|=
name|PropertyValues
operator|.
name|newPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|when
argument_list|(
name|mock
argument_list|(
name|ResultRow
operator|.
name|class
argument_list|)
operator|.
name|getValue
argument_list|(
name|QueryConstants
operator|.
name|JCR_PATH
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|propValue
argument_list|)
operator|.
name|getMock
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testApplyNullRow
parameter_list|()
block|{
name|assertNull
argument_list|(
name|groupRrta
operator|.
name|apply
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRowToNonExistingTree
parameter_list|()
block|{
name|PropertyValue
name|propValue
init|=
name|PropertyValues
operator|.
name|newPath
argument_list|(
literal|"/path/to/nonExisting/tree"
argument_list|)
decl_stmt|;
name|ResultRow
name|row
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|ResultRow
operator|.
name|class
argument_list|)
operator|.
name|getValue
argument_list|(
name|QueryConstants
operator|.
name|JCR_PATH
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|propValue
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
name|groupRrta
operator|.
name|apply
argument_list|(
name|row
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRowToRootTree
parameter_list|()
block|{
name|assertNull
argument_list|(
name|groupRrta
operator|.
name|apply
argument_list|(
name|createResultRow
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRowToUserTree
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|user
init|=
name|getTestUser
argument_list|()
decl_stmt|;
name|ResultRow
name|row
init|=
name|createResultRow
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|groupRrta
operator|.
name|apply
argument_list|(
name|row
argument_list|)
argument_list|)
expr_stmt|;
name|Authorizable
name|a
init|=
name|userRrta
operator|.
name|apply
argument_list|(
name|row
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|user
operator|.
name|getID
argument_list|()
argument_list|,
name|a
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRowToUserSubTree
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|user
init|=
name|getTestUser
argument_list|()
decl_stmt|;
name|Tree
name|t
init|=
name|root
operator|.
name|getTree
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|t
operator|=
name|TreeUtil
operator|.
name|addChild
argument_list|(
name|t
argument_list|,
literal|"child"
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|ResultRow
name|row
init|=
name|createResultRow
argument_list|(
name|t
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|groupRrta
operator|.
name|apply
argument_list|(
name|row
argument_list|)
argument_list|)
expr_stmt|;
name|Authorizable
name|a
init|=
name|userRrta
operator|.
name|apply
argument_list|(
name|row
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|user
operator|.
name|getID
argument_list|()
argument_list|,
name|a
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRowToNonExistingUserSubTree
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|user
init|=
name|getTestUser
argument_list|()
decl_stmt|;
name|ResultRow
name|row
init|=
name|createResultRow
argument_list|(
name|PathUtils
operator|.
name|concat
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|,
literal|"child"
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|userRrta
operator|.
name|apply
argument_list|(
name|row
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRowNonAccessibleUserTree
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|user
init|=
name|getTestUser
argument_list|()
decl_stmt|;
name|String
name|userPath
init|=
name|user
operator|.
name|getPath
argument_list|()
decl_stmt|;
try|try
init|(
name|ContentSession
name|cs
init|=
name|login
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
name|user
operator|.
name|getID
argument_list|()
argument_list|,
name|user
operator|.
name|getID
argument_list|()
operator|.
name|toCharArray
argument_list|()
argument_list|)
argument_list|)
init|)
block|{
name|Root
name|r
init|=
name|cs
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|ResultRowToAuthorizable
name|rrta
init|=
operator|new
name|ResultRowToAuthorizable
argument_list|(
operator|new
name|UserManagerImpl
argument_list|(
name|r
argument_list|,
name|getPartialValueFactory
argument_list|()
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|)
argument_list|,
name|r
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|rrta
operator|.
name|apply
argument_list|(
name|createResultRow
argument_list|(
name|userPath
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

