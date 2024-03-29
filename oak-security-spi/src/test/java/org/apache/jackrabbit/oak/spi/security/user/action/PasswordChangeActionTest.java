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
name|user
operator|.
name|action
package|;
end_package

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
name|plugins
operator|.
name|memory
operator|.
name|PropertyStates
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
name|PasswordUtil
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
name|PasswordChangeActionTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|USER_PATH
init|=
literal|"/userpath"
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|NamePathMapper
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|PasswordChangeAction
name|pwChangeAction
decl_stmt|;
specifier|private
name|User
name|user
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
name|pwChangeAction
operator|=
operator|new
name|PasswordChangeAction
argument_list|()
expr_stmt|;
name|pwChangeAction
operator|.
name|init
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|SecurityProvider
operator|.
name|class
argument_list|)
argument_list|,
name|ConfigurationParameters
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|user
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|User
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|user
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|USER_PATH
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Root
name|createRoot
parameter_list|(
annotation|@
name|Nullable
name|String
name|pw
parameter_list|)
throws|throws
name|Exception
block|{
name|Tree
name|userTree
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|pw
operator|!=
literal|null
condition|)
block|{
name|String
name|pwHash
init|=
name|PasswordUtil
operator|.
name|buildPasswordHash
argument_list|(
name|pw
argument_list|)
decl_stmt|;
name|when
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
operator|.
name|thenReturn
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|UserConstants
operator|.
name|REP_PASSWORD
argument_list|,
name|pwHash
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Root
name|root
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Root
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
name|USER_PATH
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|userTree
argument_list|)
expr_stmt|;
return|return
name|root
return|;
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
name|testNullPassword
parameter_list|()
throws|throws
name|Exception
block|{
name|pwChangeAction
operator|.
name|onPasswordChange
argument_list|(
name|user
argument_list|,
literal|null
argument_list|,
name|createRoot
argument_list|(
literal|null
argument_list|)
argument_list|,
name|namePathMapper
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
name|testSamePassword
parameter_list|()
throws|throws
name|Exception
block|{
name|pwChangeAction
operator|.
name|onPasswordChange
argument_list|(
name|user
argument_list|,
literal|"pw"
argument_list|,
name|createRoot
argument_list|(
literal|"pw"
argument_list|)
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPasswordChange
parameter_list|()
throws|throws
name|Exception
block|{
name|pwChangeAction
operator|.
name|onPasswordChange
argument_list|(
name|user
argument_list|,
literal|"changedPassword"
argument_list|,
name|createRoot
argument_list|(
literal|"pw"
argument_list|)
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUserWithoutPassword
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|pwChangeAction
operator|.
name|onPasswordChange
argument_list|(
name|user
argument_list|,
literal|"changedPassword"
argument_list|,
name|createRoot
argument_list|(
literal|null
argument_list|)
argument_list|,
name|namePathMapper
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|user
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

