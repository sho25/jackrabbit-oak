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
name|jcr
operator|.
name|security
operator|.
name|authorization
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|AccessDeniedException
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
name|Workspace
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
name|AccessControlManager
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
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|Privilege
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
name|JackrabbitWorkspace
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
name|test
operator|.
name|NotExecutableException
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

begin_comment
comment|/**  * PrivilegeManagementTest... TODO  */
end_comment

begin_class
specifier|public
class|class
name|PrivilegeManagementTest
extends|extends
name|AbstractEvaluationTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|REP_PRIVILEGE_MANAGEMENT
init|=
literal|"rep:privilegeManagement"
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
comment|// test user must not be allowed
name|assertHasPrivilege
argument_list|(
literal|null
argument_list|,
name|REP_PRIVILEGE_MANAGEMENT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
for|for
control|(
name|AccessControlPolicy
name|policy
range|:
name|acMgr
operator|.
name|getPolicies
argument_list|(
literal|null
argument_list|)
control|)
block|{
name|acMgr
operator|.
name|removePolicy
argument_list|(
literal|null
argument_list|,
name|policy
argument_list|)
expr_stmt|;
block|}
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|getNewPrivilegeName
parameter_list|(
name|Workspace
name|wsp
parameter_list|)
throws|throws
name|RepositoryException
throws|,
name|NotExecutableException
block|{
name|String
name|privName
init|=
literal|null
decl_stmt|;
name|AccessControlManager
name|acMgr
init|=
name|wsp
operator|.
name|getSession
argument_list|()
operator|.
name|getAccessControlManager
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|Privilege
name|p
init|=
name|acMgr
operator|.
name|privilegeFromName
argument_list|(
name|privName
argument_list|)
decl_stmt|;
name|privName
operator|=
literal|"privilege-"
operator|+
name|i
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|privName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotExecutableException
argument_list|(
literal|"failed to define new privilege name."
argument_list|)
throw|;
block|}
return|return
name|privName
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRegisterPrivilege
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Workspace
name|testWsp
init|=
name|testSession
operator|.
name|getWorkspace
argument_list|()
decl_stmt|;
operator|(
operator|(
name|JackrabbitWorkspace
operator|)
name|testWsp
operator|)
operator|.
name|getPrivilegeManager
argument_list|()
operator|.
name|registerPrivilege
argument_list|(
name|getNewPrivilegeName
argument_list|(
name|testWsp
argument_list|)
argument_list|,
literal|false
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Privilege registration should be denied."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccessDeniedException
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
name|testModifyPrivilegeMgtPrivilege
parameter_list|()
throws|throws
name|Exception
block|{
name|modify
argument_list|(
literal|null
argument_list|,
name|REP_PRIVILEGE_MANAGEMENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertHasPrivilege
argument_list|(
literal|null
argument_list|,
name|REP_PRIVILEGE_MANAGEMENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|modify
argument_list|(
literal|null
argument_list|,
name|REP_PRIVILEGE_MANAGEMENT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertHasPrivilege
argument_list|(
literal|null
argument_list|,
name|REP_PRIVILEGE_MANAGEMENT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRegisterPrivilegeWithPrivilege
parameter_list|()
throws|throws
name|Exception
block|{
name|modify
argument_list|(
literal|null
argument_list|,
name|REP_PRIVILEGE_MANAGEMENT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|Workspace
name|testWsp
init|=
name|testSession
operator|.
name|getWorkspace
argument_list|()
decl_stmt|;
operator|(
operator|(
name|JackrabbitWorkspace
operator|)
name|testWsp
operator|)
operator|.
name|getPrivilegeManager
argument_list|()
operator|.
name|registerPrivilege
argument_list|(
name|getNewPrivilegeName
argument_list|(
name|testWsp
argument_list|)
argument_list|,
literal|false
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|modify
argument_list|(
literal|null
argument_list|,
name|REP_PRIVILEGE_MANAGEMENT
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

