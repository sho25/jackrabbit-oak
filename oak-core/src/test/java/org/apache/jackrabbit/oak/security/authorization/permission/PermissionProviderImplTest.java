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
name|permission
package|;
end_package

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
name|Map
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
name|ImmutableMap
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
name|JcrConstants
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
name|Group
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
name|UserManager
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
name|core
operator|.
name|ImmutableRoot
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
name|core
operator|.
name|TreeTypeProvider
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
name|name
operator|.
name|NamespaceConstants
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
name|spi
operator|.
name|security
operator|.
name|ConfigurationParameters
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
name|authorization
operator|.
name|AccessControlConfiguration
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
name|authorization
operator|.
name|AccessControlConstants
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
name|authorization
operator|.
name|permission
operator|.
name|PermissionProvider
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
name|authorization
operator|.
name|permission
operator|.
name|ReadStatus
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
name|privilege
operator|.
name|PrivilegeConstants
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

begin_class
specifier|public
class|class
name|PermissionProviderImplTest
extends|extends
name|AbstractSecurityTest
implements|implements
name|AccessControlConstants
block|{
specifier|private
specifier|static
specifier|final
name|String
name|ADMINISTRATOR_GROUP
init|=
literal|"administrators"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|READ_PATHS
init|=
operator|new
name|String
index|[]
block|{
name|NamespaceConstants
operator|.
name|NAMESPACES_PATH
block|,
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
block|,
name|PrivilegeConstants
operator|.
name|PRIVILEGES_PATH
block|,
literal|"/test"
block|}
decl_stmt|;
specifier|private
name|Group
name|adminstrators
decl_stmt|;
annotation|@
name|Override
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
operator|.
name|addChild
argument_list|(
literal|"test"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|UserManager
name|uMgr
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|adminstrators
operator|=
name|uMgr
operator|.
name|createGroup
argument_list|(
name|ADMINISTRATOR_GROUP
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|root
operator|.
name|getTree
argument_list|(
literal|"/test"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|UserManager
name|uMgr
init|=
name|getUserManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
if|if
condition|(
name|adminstrators
operator|!=
literal|null
condition|)
block|{
name|uMgr
operator|.
name|getAuthorizable
argument_list|(
name|adminstrators
operator|.
name|getID
argument_list|()
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|root
operator|.
name|hasPendingChanges
argument_list|()
condition|)
block|{
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|super
operator|.
name|after
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|ConfigurationParameters
name|getSecurityConfigParameters
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|PARAM_READ_PATHS
argument_list|,
name|READ_PATHS
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|PARAM_ADMINISTRATOR_PRINCIPALS
argument_list|,
operator|new
name|String
index|[]
block|{
name|ADMINISTRATOR_GROUP
block|}
argument_list|)
expr_stmt|;
name|ConfigurationParameters
name|acConfig
init|=
operator|new
name|ConfigurationParameters
argument_list|(
name|map
argument_list|)
decl_stmt|;
return|return
operator|new
name|ConfigurationParameters
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
name|AccessControlConfiguration
operator|.
name|NAME
argument_list|,
name|acConfig
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadPath
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|testSession
init|=
name|createTestSession
argument_list|()
decl_stmt|;
try|try
block|{
name|Root
name|r
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Root
name|immutableRoot
init|=
operator|new
name|ImmutableRoot
argument_list|(
name|r
argument_list|,
name|TreeTypeProvider
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|PermissionProvider
name|pp
init|=
operator|new
name|PermissionProviderImpl
argument_list|(
name|testSession
operator|.
name|getLatestRoot
argument_list|()
argument_list|,
name|testSession
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|r
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|ReadStatus
operator|.
name|DENY_THIS
argument_list|,
name|pp
operator|.
name|getReadStatus
argument_list|(
name|immutableRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
name|READ_PATHS
control|)
block|{
name|assertTrue
argument_list|(
name|r
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_ALL_REGULAR
argument_list|,
name|pp
operator|.
name|getReadStatus
argument_list|(
name|immutableRoot
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|testSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAdministatorConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|adminstrators
operator|.
name|addMember
argument_list|(
name|getTestUser
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|ContentSession
name|testSession
init|=
name|createTestSession
argument_list|()
decl_stmt|;
try|try
block|{
name|Root
name|r
init|=
name|testSession
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|Root
name|immutableRoot
init|=
operator|new
name|ImmutableRoot
argument_list|(
name|r
argument_list|,
name|TreeTypeProvider
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|PermissionProvider
name|pp
init|=
operator|new
name|PermissionProviderImpl
argument_list|(
name|testSession
operator|.
name|getLatestRoot
argument_list|()
argument_list|,
name|testSession
operator|.
name|getAuthInfo
argument_list|()
operator|.
name|getPrincipals
argument_list|()
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|r
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_ALL
argument_list|,
name|pp
operator|.
name|getReadStatus
argument_list|(
name|immutableRoot
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
name|READ_PATHS
control|)
block|{
name|assertTrue
argument_list|(
name|r
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|ReadStatus
operator|.
name|ALLOW_ALL
argument_list|,
name|pp
operator|.
name|getReadStatus
argument_list|(
name|immutableRoot
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|testSession
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

