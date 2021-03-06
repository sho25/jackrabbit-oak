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
name|exercise
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
name|javax
operator|.
name|jcr
operator|.
name|SimpleCredentials
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
name|spi
operator|.
name|security
operator|.
name|principal
operator|.
name|PrincipalImpl
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

begin_class
specifier|public
specifier|final
class|class
name|ExerciseUtility
block|{
specifier|public
specifier|static
specifier|final
name|String
name|TEST_USER_HINT
init|=
literal|"testUser"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_GROUP_HINT
init|=
literal|"testGroup"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_PRINCIPAL_HINT
init|=
literal|"testPrincipal"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_GROUP_PRINCIPAL_HINT
init|=
literal|"testGroupPrincipal"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_PW
init|=
literal|"pw"
decl_stmt|;
specifier|private
name|ExerciseUtility
parameter_list|()
block|{}
specifier|public
specifier|static
name|String
name|getTestId
parameter_list|(
annotation|@
name|NotNull
name|String
name|hint
parameter_list|)
block|{
return|return
name|hint
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|Principal
name|getTestPrincipal
parameter_list|(
annotation|@
name|NotNull
name|String
name|hint
parameter_list|)
block|{
name|String
name|name
init|=
name|hint
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
return|return
operator|new
name|PrincipalImpl
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|User
name|createTestUser
parameter_list|(
annotation|@
name|NotNull
name|UserManager
name|userMgr
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|userMgr
operator|.
name|createUser
argument_list|(
name|getTestId
argument_list|(
name|TEST_USER_HINT
argument_list|)
argument_list|,
name|TEST_PW
argument_list|,
name|getTestPrincipal
argument_list|(
name|TEST_PRINCIPAL_HINT
argument_list|)
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Group
name|createTestGroup
parameter_list|(
annotation|@
name|NotNull
name|UserManager
name|userMgr
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|userMgr
operator|.
name|createGroup
argument_list|(
name|getTestId
argument_list|(
name|TEST_GROUP_HINT
argument_list|)
argument_list|,
name|getTestPrincipal
argument_list|(
name|TEST_GROUP_PRINCIPAL_HINT
argument_list|)
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|SimpleCredentials
name|getTestCredentials
parameter_list|(
annotation|@
name|NotNull
name|String
name|userID
parameter_list|)
block|{
return|return
operator|new
name|SimpleCredentials
argument_list|(
name|userID
argument_list|,
name|TEST_PW
operator|.
name|toCharArray
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

