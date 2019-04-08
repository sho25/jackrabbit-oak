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
name|Impersonation
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
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|SystemSubject
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
name|java
operator|.
name|security
operator|.
name|Principal
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
name|assertFalse
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
name|mock
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
name|never
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
name|spy
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
name|times
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
name|verify
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
name|UserImplTest
extends|extends
name|AbstractAutoSaveTest
block|{
specifier|private
name|User
name|dlg
decl_stmt|;
specifier|private
name|UserImpl
name|user
decl_stmt|;
specifier|private
name|Impersonation
name|impersonationMock
init|=
name|mock
argument_list|(
name|Impersonation
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Before
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
name|dlg
operator|=
name|spy
argument_list|(
name|getTestUser
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|dlg
operator|instanceof
name|UserImpl
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|dlg
operator|.
name|getImpersonation
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|impersonationMock
argument_list|)
expr_stmt|;
name|user
operator|=
operator|new
name|UserImpl
argument_list|(
name|dlg
argument_list|,
name|autosaveMgr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsAdmin
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|dlg
operator|.
name|isAdmin
argument_list|()
argument_list|,
name|user
operator|.
name|isAdmin
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|dlg
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|isAdmin
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|autosaveMgr
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|autosave
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsSystemUser
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|dlg
operator|.
name|isSystemUser
argument_list|()
argument_list|,
name|user
operator|.
name|isSystemUser
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|dlg
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|isSystemUser
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|autosaveMgr
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|autosave
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetCredentials
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|assertEquals
argument_list|(
name|dlg
operator|.
name|getCredentials
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|user
operator|.
name|getCredentials
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|dlg
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|getCredentials
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|autosaveMgr
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|autosave
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetImpersonation
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|user
operator|.
name|getImpersonation
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|dlg
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|getImpersonation
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|autosaveMgr
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|autosave
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChancePassword
parameter_list|()
throws|throws
name|Exception
block|{
name|user
operator|.
name|changePassword
argument_list|(
literal|"newPw"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|dlg
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|changePassword
argument_list|(
literal|"newPw"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|autosaveMgr
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|autosave
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChancePasswordOldPw
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|oldpw
init|=
name|user
operator|.
name|getID
argument_list|()
decl_stmt|;
name|user
operator|.
name|changePassword
argument_list|(
literal|"newPw"
argument_list|,
name|oldpw
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|dlg
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|changePassword
argument_list|(
literal|"newPw"
argument_list|,
name|oldpw
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|autosaveMgr
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|autosave
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDisable
parameter_list|()
throws|throws
name|Exception
block|{
name|user
operator|.
name|disable
argument_list|(
literal|"disable"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|dlg
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|disable
argument_list|(
literal|"disable"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|autosaveMgr
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|autosave
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsDisabled
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|dlg
operator|.
name|isDisabled
argument_list|()
argument_list|,
name|user
operator|.
name|isDisabled
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|dlg
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|isDisabled
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|autosaveMgr
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|autosave
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetDisabledReason
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|dlg
operator|.
name|getDisabledReason
argument_list|()
argument_list|,
name|user
operator|.
name|getDisabledReason
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|dlg
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|getDisabledReason
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|autosaveMgr
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|autosave
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetImpersonators
parameter_list|()
throws|throws
name|Exception
block|{
name|user
operator|.
name|getImpersonation
argument_list|()
operator|.
name|getImpersonators
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|impersonationMock
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|getImpersonators
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|autosaveMgr
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|autosave
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGrantImpersonation
parameter_list|()
throws|throws
name|Exception
block|{
name|Principal
name|principal
init|=
operator|new
name|PrincipalImpl
argument_list|(
literal|"any"
argument_list|)
decl_stmt|;
name|user
operator|.
name|getImpersonation
argument_list|()
operator|.
name|grantImpersonation
argument_list|(
name|principal
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|impersonationMock
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|grantImpersonation
argument_list|(
name|principal
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|autosaveMgr
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|autosave
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRevokeImpersonation
parameter_list|()
throws|throws
name|Exception
block|{
name|Principal
name|principal
init|=
operator|new
name|PrincipalImpl
argument_list|(
literal|"any"
argument_list|)
decl_stmt|;
name|user
operator|.
name|getImpersonation
argument_list|()
operator|.
name|revokeImpersonation
argument_list|(
name|principal
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|impersonationMock
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|revokeImpersonation
argument_list|(
name|principal
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|autosaveMgr
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|autosave
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testImpersonation
parameter_list|()
throws|throws
name|Exception
block|{
name|user
operator|.
name|getImpersonation
argument_list|()
operator|.
name|allows
argument_list|(
name|SystemSubject
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|impersonationMock
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|allows
argument_list|(
name|SystemSubject
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|autosaveMgr
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|autosave
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit
