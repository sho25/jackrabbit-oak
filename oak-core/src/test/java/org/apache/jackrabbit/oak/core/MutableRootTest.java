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
name|security
operator|.
name|Principal
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
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
name|MemoryNodeStore
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
name|security
operator|.
name|OpenSecurityProvider
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
name|SecurityProvider
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
name|authentication
operator|.
name|AuthInfoImpl
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
name|AuthorizationConfiguration
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
name|OpenAuthorizationConfiguration
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
name|TreePermission
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
name|MutableRootTest
block|{
specifier|private
specifier|final
name|NodeStore
name|store
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|TestPermissionProvider
name|permissionProvider
init|=
operator|new
name|TestPermissionProvider
argument_list|()
decl_stmt|;
specifier|private
name|MutableRoot
name|root
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|SecurityProvider
name|sp
init|=
operator|new
name|OpenSecurityProvider
argument_list|()
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|getConfiguration
parameter_list|(
annotation|@
name|Nonnull
name|Class
argument_list|<
name|T
argument_list|>
name|configClass
parameter_list|)
block|{
if|if
condition|(
name|AuthorizationConfiguration
operator|.
name|class
operator|==
name|configClass
condition|)
block|{
return|return
operator|(
name|T
operator|)
operator|new
name|OpenAuthorizationConfiguration
argument_list|()
block|{
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|PermissionProvider
name|getPermissionProvider
parameter_list|(
annotation|@
name|Nonnull
name|Root
name|root
parameter_list|,
annotation|@
name|Nonnull
name|String
name|workspaceName
parameter_list|,
annotation|@
name|Nonnull
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
return|return
name|permissionProvider
return|;
block|}
block|}
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|getConfiguration
argument_list|(
name|configClass
argument_list|)
return|;
block|}
block|}
block|}
decl_stmt|;
name|ContentSessionImpl
name|cs
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ContentSessionImpl
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|cs
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"contentSession"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|cs
operator|.
name|getAuthInfo
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|AuthInfoImpl
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|cs
operator|.
name|getWorkspaceName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"default"
argument_list|)
expr_stmt|;
name|root
operator|=
operator|new
name|MutableRoot
argument_list|(
name|store
argument_list|,
operator|new
name|EmptyHook
argument_list|()
argument_list|,
literal|"default"
argument_list|,
operator|new
name|Subject
argument_list|()
argument_list|,
name|sp
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|cs
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see<a href"https://issues.apache.org/jira/browse/OAK-5355">OAK-5355</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testCommit
parameter_list|()
throws|throws
name|Exception
block|{
name|MutableTree
name|t
init|=
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
decl_stmt|;
name|NodeBuilder
name|nb
init|=
name|t
operator|.
name|getNodeBuilder
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|nb
operator|instanceof
name|SecureNodeBuilder
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|canReadRootTree
argument_list|(
name|t
argument_list|)
argument_list|,
name|nb
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|// commit resets the permissionprovider, which in our test scenario alters
comment|// the 'denyAll' flag.
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|canReadRootTree
argument_list|(
name|t
argument_list|)
argument_list|,
name|nb
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|MutableTree
name|t2
init|=
name|root
operator|.
name|getTree
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|)
decl_stmt|;
name|NodeBuilder
name|nb2
init|=
name|t
operator|.
name|getNodeBuilder
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|canReadRootTree
argument_list|(
name|t2
argument_list|)
argument_list|,
name|nb
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|nb2
operator|.
name|exists
argument_list|()
argument_list|,
name|nb
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|canReadRootTree
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|t
parameter_list|)
block|{
return|return
name|permissionProvider
operator|.
name|getTreePermission
argument_list|(
name|t
argument_list|,
name|TreePermission
operator|.
name|EMPTY
argument_list|)
operator|.
name|canRead
argument_list|()
return|;
block|}
block|}
end_class

end_unit

