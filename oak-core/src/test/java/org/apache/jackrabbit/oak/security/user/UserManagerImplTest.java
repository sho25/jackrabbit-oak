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
name|namepath
operator|.
name|NamePathMapper
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
name|spi
operator|.
name|security
operator|.
name|user
operator|.
name|UserConfiguration
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
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|user
operator|.
name|util
operator|.
name|PasswordUtility
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
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
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
name|fail
import|;
end_import

begin_comment
comment|/**  * UserManagerImplTest...  */
end_comment

begin_class
specifier|public
class|class
name|UserManagerImplTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|UserConfiguration
name|uc
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
name|uc
operator|=
name|getSecurityProvider
argument_list|()
operator|.
name|getUserConfiguration
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSetPassword
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|root
init|=
name|admin
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|UserManagerImpl
name|userMgr
init|=
operator|(
name|UserManagerImpl
operator|)
name|uc
operator|.
name|getUserManager
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|User
name|user
init|=
name|userMgr
operator|.
name|createUser
argument_list|(
literal|"a"
argument_list|,
literal|"pw"
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|pwds
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|pwds
operator|.
name|add
argument_list|(
literal|"pw"
argument_list|)
expr_stmt|;
name|pwds
operator|.
name|add
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|pwds
operator|.
name|add
argument_list|(
literal|"{sha1}pw"
argument_list|)
expr_stmt|;
name|Tree
name|userTree
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
for|for
control|(
name|String
name|pw
range|:
name|pwds
control|)
block|{
name|userMgr
operator|.
name|setPassword
argument_list|(
name|userTree
argument_list|,
name|pw
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|String
name|pwHash
init|=
name|userTree
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|pwHash
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|PasswordUtility
operator|.
name|isSame
argument_list|(
name|pwHash
argument_list|,
name|pw
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|pw
range|:
name|pwds
control|)
block|{
name|userMgr
operator|.
name|setPassword
argument_list|(
name|userTree
argument_list|,
name|pw
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|String
name|pwHash
init|=
name|userTree
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|pwHash
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|pw
operator|.
name|startsWith
argument_list|(
literal|"{"
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|PasswordUtility
operator|.
name|isSame
argument_list|(
name|pwHash
argument_list|,
name|pw
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|PasswordUtility
operator|.
name|isSame
argument_list|(
name|pwHash
argument_list|,
name|pw
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|pw
argument_list|,
name|pwHash
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|setPasswordNull
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|root
init|=
name|admin
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|UserManagerImpl
name|userMgr
init|=
operator|(
name|UserManagerImpl
operator|)
name|uc
operator|.
name|getUserManager
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|User
name|user
init|=
name|userMgr
operator|.
name|createUser
argument_list|(
literal|"a"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Tree
name|userTree
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
try|try
block|{
name|userMgr
operator|.
name|setPassword
argument_list|(
name|userTree
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"setting null password should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|userMgr
operator|.
name|setPassword
argument_list|(
name|userTree
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"setting null password should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
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
name|testGetPasswordHash
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|root
init|=
name|admin
operator|.
name|getLatestRoot
argument_list|()
decl_stmt|;
name|UserManager
name|userMgr
init|=
name|uc
operator|.
name|getUserManager
argument_list|(
name|root
argument_list|,
name|NamePathMapper
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|User
name|user
init|=
name|userMgr
operator|.
name|createUser
argument_list|(
literal|"a"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|Tree
name|userTree
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
name|assertNull
argument_list|(
name|userTree
operator|.
name|getProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

