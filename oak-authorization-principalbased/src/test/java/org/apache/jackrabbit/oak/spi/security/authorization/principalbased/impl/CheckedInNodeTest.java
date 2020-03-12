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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|principalbased
operator|.
name|impl
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
name|plugins
operator|.
name|version
operator|.
name|ReadWriteVersionManager
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
name|nodetype
operator|.
name|NodeTypeConstants
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
name|Value
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
name|Collections
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
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_ISCHECKEDOUT
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
name|JcrConstants
operator|.
name|MIX_VERSIONABLE
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
name|security
operator|.
name|authorization
operator|.
name|accesscontrol
operator|.
name|AccessControlConstants
operator|.
name|REP_GLOB
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
name|security
operator|.
name|authorization
operator|.
name|accesscontrol
operator|.
name|AccessControlConstants
operator|.
name|REP_NT_NAMES
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
name|security
operator|.
name|authorization
operator|.
name|principalbased
operator|.
name|impl
operator|.
name|Constants
operator|.
name|MIX_REP_PRINCIPAL_BASED_MIXIN
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
name|security
operator|.
name|privilege
operator|.
name|PrivilegeConstants
operator|.
name|JCR_READ
import|;
end_import

begin_class
specifier|public
class|class
name|CheckedInNodeTest
extends|extends
name|AbstractPrincipalBasedTest
block|{
specifier|private
name|ReadWriteVersionManager
name|versionManager
decl_stmt|;
specifier|private
name|Principal
name|testPrincipal
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
name|User
name|u
init|=
name|getTestSystemUser
argument_list|()
decl_stmt|;
name|testPrincipal
operator|=
name|u
operator|.
name|getPrincipal
argument_list|()
expr_stmt|;
name|setupContentTrees
argument_list|(
name|TEST_OAK_PATH
argument_list|)
expr_stmt|;
name|Tree
name|userTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|getNamePathMapper
argument_list|()
operator|.
name|getOakPath
argument_list|(
name|u
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|TreeUtil
operator|.
name|addMixin
argument_list|(
name|userTree
argument_list|,
name|MIX_REP_PRINCIPAL_BASED_MIXIN
argument_list|,
name|root
operator|.
name|getTree
argument_list|(
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|TreeUtil
operator|.
name|addMixin
argument_list|(
name|userTree
argument_list|,
name|MIX_VERSIONABLE
argument_list|,
name|root
operator|.
name|getTree
argument_list|(
name|NodeTypeConstants
operator|.
name|NODE_TYPES_PATH
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|userTree
operator|.
name|setProperty
argument_list|(
name|JCR_ISCHECKEDOUT
argument_list|,
literal|false
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
name|Tree
name|userTree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|getNamePathMapper
argument_list|()
operator|.
name|getOakPath
argument_list|(
name|getTestSystemUser
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|userTree
operator|.
name|setProperty
argument_list|(
name|JCR_ISCHECKEDOUT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
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
name|Test
specifier|public
name|void
name|testAddEmptyPolicy
parameter_list|()
throws|throws
name|Exception
block|{
name|JackrabbitAccessControlManager
name|acMgr
init|=
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|PrincipalPolicyImpl
name|policy
init|=
name|getPrincipalPolicyImpl
argument_list|(
name|testPrincipal
argument_list|,
name|acMgr
argument_list|)
decl_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|policy
operator|.
name|getPath
argument_list|()
argument_list|,
name|policy
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddEntry
parameter_list|()
throws|throws
name|Exception
block|{
name|PrincipalPolicyImpl
name|policy
init|=
name|setupPrincipalBasedAccessControl
argument_list|(
name|testPrincipal
argument_list|,
name|getNamePathMapper
argument_list|()
operator|.
name|getJcrPath
argument_list|(
name|TEST_OAK_PATH
argument_list|)
argument_list|,
name|JCR_READ
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAddEntryWithRestrictions
parameter_list|()
throws|throws
name|Exception
block|{
name|JackrabbitAccessControlManager
name|acMgr
init|=
name|getAccessControlManager
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|PrincipalPolicyImpl
name|policy
init|=
name|getPrincipalPolicyImpl
argument_list|(
name|testPrincipal
argument_list|,
name|acMgr
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Value
argument_list|>
name|restrictions
init|=
name|Collections
operator|.
name|singletonMap
argument_list|(
name|getNamePathMapper
argument_list|()
operator|.
name|getJcrName
argument_list|(
name|REP_GLOB
argument_list|)
argument_list|,
name|getValueFactory
argument_list|(
name|root
argument_list|)
operator|.
name|createValue
argument_list|(
literal|"/*"
argument_list|)
argument_list|)
decl_stmt|;
name|policy
operator|.
name|addEntry
argument_list|(
name|getNamePathMapper
argument_list|()
operator|.
name|getJcrPath
argument_list|(
name|TEST_OAK_PATH
argument_list|)
argument_list|,
name|privilegesFromNames
argument_list|(
name|JCR_READ
argument_list|)
argument_list|,
name|restrictions
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|)
expr_stmt|;
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|policy
operator|.
name|getPath
argument_list|()
argument_list|,
name|policy
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

