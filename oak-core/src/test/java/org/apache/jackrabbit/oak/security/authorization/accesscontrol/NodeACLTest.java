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
name|accesscontrol
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
name|JackrabbitAccessControlManager
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
name|commons
operator|.
name|jackrabbit
operator|.
name|authorization
operator|.
name|AccessControlUtils
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
name|principal
operator|.
name|EveryonePrincipal
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
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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
name|security
operator|.
name|AccessControlList
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlPolicy
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
name|assertNotEquals
import|;
end_import

begin_class
specifier|public
class|class
name|NodeACLTest
extends|extends
name|AbstractAccessControlTest
block|{
specifier|private
name|ACL
name|nodeAcl
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
name|JackrabbitAccessControlManager
name|acMgr
init|=
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|AccessControlList
name|policy
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
name|TEST_PATH
argument_list|)
decl_stmt|;
name|policy
operator|.
name|addAccessControlEntry
argument_list|(
name|testPrincipal
argument_list|,
name|testPrivileges
argument_list|)
expr_stmt|;
name|policy
operator|.
name|addAccessControlEntry
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|testPrivileges
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|TEST_PATH
argument_list|,
name|policy
argument_list|)
expr_stmt|;
name|nodeAcl
operator|=
name|getNodeAcl
argument_list|(
name|acMgr
argument_list|,
name|TEST_PATH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
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
name|refresh
argument_list|()
expr_stmt|;
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
name|NotNull
specifier|private
specifier|static
name|ACL
name|getNodeAcl
parameter_list|(
annotation|@
name|NotNull
name|JackrabbitAccessControlManager
name|acMgr
parameter_list|,
annotation|@
name|Nullable
name|String
name|path
parameter_list|)
throws|throws
name|RepositoryException
block|{
for|for
control|(
name|AccessControlPolicy
name|acp
range|:
name|acMgr
operator|.
name|getPolicies
argument_list|(
name|path
argument_list|)
control|)
block|{
if|if
condition|(
name|acp
operator|instanceof
name|ACL
condition|)
block|{
return|return
operator|(
name|ACL
operator|)
name|acp
return|;
block|}
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"no node acl found"
argument_list|)
throw|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEquals
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|nodeAcl
argument_list|,
name|nodeAcl
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|nodeAcl
argument_list|,
name|getNodeAcl
argument_list|(
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
argument_list|,
name|TEST_PATH
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEqualsDifferentPath
parameter_list|()
throws|throws
name|Exception
block|{
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|AccessControlList
name|acl
init|=
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|acl
operator|.
name|addAccessControlEntry
argument_list|(
name|testPrincipal
argument_list|,
name|testPrivileges
argument_list|)
expr_stmt|;
name|acl
operator|.
name|addAccessControlEntry
argument_list|(
name|EveryonePrincipal
operator|.
name|getInstance
argument_list|()
argument_list|,
name|testPrivileges
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|nodeAcl
argument_list|,
name|acl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEqualsDifferentEntries
parameter_list|()
throws|throws
name|Exception
block|{
name|ACL
name|acl
init|=
name|getNodeAcl
argument_list|(
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
argument_list|,
name|TEST_PATH
argument_list|)
decl_stmt|;
name|acl
operator|.
name|removeAccessControlEntry
argument_list|(
name|acl
operator|.
name|getAccessControlEntries
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|nodeAcl
argument_list|,
name|acl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEqualsDifferentAcessControlList
parameter_list|()
block|{
name|assertNotEquals
argument_list|(
name|nodeAcl
argument_list|,
name|createACL
argument_list|(
name|TEST_PATH
argument_list|,
name|nodeAcl
operator|.
name|getEntries
argument_list|()
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
name|getRestrictionProvider
argument_list|()
argument_list|)
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
literal|0
argument_list|,
name|nodeAcl
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

