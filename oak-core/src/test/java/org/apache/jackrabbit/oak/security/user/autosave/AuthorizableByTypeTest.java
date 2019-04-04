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
operator|.
name|autosave
package|;
end_package

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
name|AuthorizableTypeException
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
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|AuthorizableByTypeTest
extends|extends
name|AbstractAutoSaveTest
block|{
specifier|private
name|User
name|user
decl_stmt|;
specifier|private
name|Group
name|group
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
name|user
operator|=
name|autosaveMgr
operator|.
name|getAuthorizable
argument_list|(
name|getTestUser
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|,
name|User
operator|.
name|class
argument_list|)
expr_stmt|;
name|group
operator|=
name|autosaveMgr
operator|.
name|createGroup
argument_list|(
literal|"testGroup"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
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
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
block|{
name|group
operator|.
name|remove
argument_list|()
expr_stmt|;
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
name|Test
specifier|public
name|void
name|testUserByIdAndType
parameter_list|()
throws|throws
name|Exception
block|{
name|User
name|u
init|=
name|autosaveMgr
operator|.
name|getAuthorizable
argument_list|(
name|user
operator|.
name|getID
argument_list|()
argument_list|,
name|User
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|u
operator|instanceof
name|UserImpl
argument_list|)
expr_stmt|;
name|Authorizable
name|auth
init|=
name|autosaveMgr
operator|.
name|getAuthorizable
argument_list|(
name|user
operator|.
name|getID
argument_list|()
argument_list|,
name|user
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|auth
operator|instanceof
name|UserImpl
argument_list|)
expr_stmt|;
name|auth
operator|=
name|autosaveMgr
operator|.
name|getAuthorizable
argument_list|(
name|user
operator|.
name|getID
argument_list|()
argument_list|,
name|Authorizable
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|auth
operator|instanceof
name|AuthorizableImpl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByIdAndType
parameter_list|()
throws|throws
name|Exception
block|{
name|Group
name|g
init|=
name|autosaveMgr
operator|.
name|getAuthorizable
argument_list|(
name|group
operator|.
name|getID
argument_list|()
argument_list|,
name|Group
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|g
operator|instanceof
name|GroupImpl
argument_list|)
expr_stmt|;
name|Authorizable
name|auth
init|=
name|autosaveMgr
operator|.
name|getAuthorizable
argument_list|(
name|group
operator|.
name|getID
argument_list|()
argument_list|,
name|group
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|auth
operator|instanceof
name|GroupImpl
argument_list|)
expr_stmt|;
name|auth
operator|=
name|autosaveMgr
operator|.
name|getAuthorizable
argument_list|(
name|group
operator|.
name|getID
argument_list|()
argument_list|,
name|Authorizable
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|auth
operator|instanceof
name|AuthorizableImpl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AuthorizableTypeException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testUserByIdAndWrongType
parameter_list|()
throws|throws
name|Exception
block|{
name|autosaveMgr
operator|.
name|getAuthorizable
argument_list|(
name|user
operator|.
name|getID
argument_list|()
argument_list|,
name|Group
operator|.
name|class
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Wrong Authorizable type is not detected."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|AuthorizableTypeException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testGroupByIdAndWrongType
parameter_list|()
throws|throws
name|Exception
block|{
name|autosaveMgr
operator|.
name|getAuthorizable
argument_list|(
name|group
operator|.
name|getID
argument_list|()
argument_list|,
name|User
operator|.
name|class
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Wrong Authorizable type is not detected."
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

