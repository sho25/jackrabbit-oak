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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|user
operator|.
name|UserConstants
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
name|util
operator|.
name|Text
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
name|fail
import|;
end_import

begin_comment
comment|/**  * UserValidatorTest  */
end_comment

begin_class
specifier|public
class|class
name|UserValidatorTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|String
name|userPath
decl_stmt|;
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
name|userPath
operator|=
name|getTestUser
argument_list|()
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|removePassword
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
name|userPath
argument_list|)
decl_stmt|;
name|userTree
operator|.
name|removeProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"removing password should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// expected
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|removePrincipalName
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
name|userPath
argument_list|)
decl_stmt|;
name|userTree
operator|.
name|removeProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"removing principal name should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// expected
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|removeAuthorizableId
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
name|userPath
argument_list|)
decl_stmt|;
name|userTree
operator|.
name|removeProperty
argument_list|(
name|UserConstants
operator|.
name|REP_AUTHORIZABLE_ID
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"removing authorizable id should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// expected
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|createWithoutPrincipalName
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|User
name|user
init|=
name|getUserManager
argument_list|()
operator|.
name|createUser
argument_list|(
literal|"withoutPrincipalName"
argument_list|,
literal|"pw"
argument_list|)
decl_stmt|;
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|userPath
argument_list|)
decl_stmt|;
name|tree
operator|.
name|removeProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"creating user with invalid jcr:uuid should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// expected
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|createWithInvalidUUID
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|User
name|user
init|=
name|getUserManager
argument_list|()
operator|.
name|createUser
argument_list|(
literal|"withInvalidUUID"
argument_list|,
literal|"pw"
argument_list|)
decl_stmt|;
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|userPath
argument_list|)
decl_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"creating user with invalid jcr:uuid should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// expected
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|changeUUID
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
name|userPath
argument_list|)
decl_stmt|;
name|userTree
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"changing jcr:uuid should fail if it the uuid valid is invalid"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// expected
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|changePrincipalName
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
name|userPath
argument_list|)
decl_stmt|;
name|userTree
operator|.
name|setProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|,
literal|"another"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"changing the principal name should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// expected
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|changeAuthorizableId
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
name|userPath
argument_list|)
decl_stmt|;
name|userTree
operator|.
name|setProperty
argument_list|(
name|UserConstants
operator|.
name|REP_AUTHORIZABLE_ID
argument_list|,
literal|"modified"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"changing the authorizable id should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// expected
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|changePasswordToPlainText
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
name|userPath
argument_list|)
decl_stmt|;
name|userTree
operator|.
name|setProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|,
literal|"plaintext"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"storing a plaintext password should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// expected
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRemoveAdminUser
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|String
name|adminId
init|=
name|getConfig
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_ADMIN_ID
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_ADMIN_ID
argument_list|)
decl_stmt|;
name|UserManager
name|userMgr
init|=
name|getUserManager
argument_list|()
decl_stmt|;
name|Authorizable
name|admin
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|adminId
argument_list|)
decl_stmt|;
if|if
condition|(
name|admin
operator|==
literal|null
condition|)
block|{
name|admin
operator|=
name|userMgr
operator|.
name|createUser
argument_list|(
name|adminId
argument_list|,
name|adminId
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|root
operator|.
name|getTree
argument_list|(
name|admin
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Admin user cannot be removed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDisableAdminUser
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|String
name|adminId
init|=
name|getConfig
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_ADMIN_ID
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_ADMIN_ID
argument_list|)
decl_stmt|;
name|UserManager
name|userMgr
init|=
name|getUserManager
argument_list|()
decl_stmt|;
name|Authorizable
name|admin
init|=
name|userMgr
operator|.
name|getAuthorizable
argument_list|(
name|adminId
argument_list|)
decl_stmt|;
if|if
condition|(
name|admin
operator|==
literal|null
condition|)
block|{
name|admin
operator|=
name|userMgr
operator|.
name|createUser
argument_list|(
name|adminId
argument_list|,
name|adminId
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
name|root
operator|.
name|getTree
argument_list|(
name|admin
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|setProperty
argument_list|(
name|UserConstants
operator|.
name|REP_DISABLED
argument_list|,
literal|"disabled"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Admin user cannot be disabled"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEnforceHierarchy
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|CommitFailedException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|invalid
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|invalid
operator|.
name|add
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|invalid
operator|.
name|add
argument_list|(
literal|"/jcr:system"
argument_list|)
expr_stmt|;
name|String
name|groupRoot
init|=
name|getConfig
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_GROUP_PATH
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_GROUP_PATH
argument_list|)
decl_stmt|;
name|invalid
operator|.
name|add
argument_list|(
name|groupRoot
argument_list|)
expr_stmt|;
name|String
name|userRoot
init|=
name|getConfig
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_USER_PATH
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
argument_list|)
decl_stmt|;
name|invalid
operator|.
name|add
argument_list|(
name|Text
operator|.
name|getRelativeParent
argument_list|(
name|userRoot
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|invalid
operator|.
name|add
argument_list|(
name|userPath
argument_list|)
expr_stmt|;
name|invalid
operator|.
name|add
argument_list|(
name|userPath
operator|+
literal|"/folder"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|path
range|:
name|invalid
control|)
block|{
try|try
block|{
name|Tree
name|parent
init|=
name|root
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
name|String
index|[]
name|segments
init|=
name|Text
operator|.
name|explode
argument_list|(
name|path
argument_list|,
literal|'/'
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|parent
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|segment
range|:
name|segments
control|)
block|{
name|Tree
name|next
init|=
name|parent
operator|.
name|getChild
argument_list|(
name|segment
argument_list|)
decl_stmt|;
if|if
condition|(
name|next
operator|==
literal|null
condition|)
block|{
name|next
operator|=
name|parent
operator|.
name|addChild
argument_list|(
name|segment
argument_list|)
expr_stmt|;
name|next
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|UserConstants
operator|.
name|NT_REP_AUTHORIZABLE_FOLDER
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|parent
operator|=
name|next
expr_stmt|;
block|}
block|}
block|}
name|Tree
name|userTree
init|=
name|parent
operator|.
name|addChild
argument_list|(
literal|"testUser"
argument_list|)
decl_stmt|;
name|userTree
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|UserConstants
operator|.
name|NT_REP_USER
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|userTree
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|,
name|UserProvider
operator|.
name|getContentID
argument_list|(
literal|"testUser"
argument_list|)
argument_list|)
expr_stmt|;
name|userTree
operator|.
name|setProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PRINCIPAL_NAME
argument_list|,
literal|"testUser"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Invalid hierarchy should be detected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|ConfigurationParameters
name|getConfig
parameter_list|()
block|{
return|return
name|getUserConfiguration
argument_list|()
operator|.
name|getConfigurationParameters
argument_list|()
return|;
block|}
block|}
end_class

end_unit

