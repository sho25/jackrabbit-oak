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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|permission
operator|.
name|PermissionConstants
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|PolicyOwnerImplTest
extends|extends
name|AbstractAccessControlTest
block|{
specifier|private
name|AccessControlManagerImpl
name|acMgr
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
name|acMgr
operator|=
operator|new
name|AccessControlManagerImpl
argument_list|(
name|root
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
name|getSecurityProvider
argument_list|()
argument_list|)
expr_stmt|;
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
name|acMgr
operator|.
name|setPolicy
argument_list|(
name|TEST_PATH
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
name|testDefines
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
name|acMgr
operator|.
name|defines
argument_list|(
name|TEST_PATH
argument_list|,
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
name|TEST_PATH
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesReadPolicy
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|readPath
init|=
name|PermissionConstants
operator|.
name|DEFAULT_READ_PATHS
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|acMgr
operator|.
name|defines
argument_list|(
name|readPath
argument_list|,
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
name|readPath
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesWrongPath
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|readPath
init|=
name|PermissionConstants
operator|.
name|DEFAULT_READ_PATHS
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|acMgr
operator|.
name|defines
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|,
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
name|TEST_PATH
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|acMgr
operator|.
name|defines
argument_list|(
name|TEST_PATH
argument_list|,
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
name|readPath
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesDifferentPolicy
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|acMgr
operator|.
name|defines
argument_list|(
name|TEST_PATH
argument_list|,
operator|new
name|AccessControlPolicy
argument_list|()
block|{}
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefinesWithRelPath
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|acMgr
operator|.
name|defines
argument_list|(
literal|"testPath"
argument_list|,
name|AccessControlUtils
operator|.
name|getAccessControlList
argument_list|(
name|acMgr
argument_list|,
name|TEST_PATH
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

