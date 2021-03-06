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
name|authentication
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
name|security
operator|.
name|PrivilegedAction
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
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|GuestCredentials
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
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|AppConfigurationEntry
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginException
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
name|AuthInfo
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
name|ContentSession
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
name|AuthInfoImpl
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
name|assertNotSame
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
name|assertSame
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
name|PreAuthTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
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
name|principals
operator|=
name|Collections
operator|.
expr|<
name|Principal
operator|>
name|singleton
argument_list|(
operator|new
name|TestPrincipal
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
operator|new
name|Configuration
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AppConfigurationEntry
index|[]
name|getAppConfigurationEntry
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
operator|new
name|AppConfigurationEntry
index|[
literal|0
index|]
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValidSubject
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|(
literal|true
argument_list|,
name|principals
argument_list|,
name|Collections
operator|.
expr|<
name|Object
operator|>
name|emptySet
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|Object
operator|>
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|ContentSession
name|cs
init|=
name|Subject
operator|.
name|doAsPrivileged
argument_list|(
name|subject
argument_list|,
operator|new
name|PrivilegedAction
argument_list|<
name|ContentSession
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ContentSession
name|run
parameter_list|()
block|{
try|try
block|{
return|return
name|login
argument_list|(
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|AuthInfo
name|authInfo
init|=
name|cs
operator|.
name|getAuthInfo
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
name|AuthInfo
operator|.
name|EMPTY
argument_list|,
name|authInfo
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|principals
argument_list|,
name|authInfo
operator|.
name|getPrincipals
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|authInfo
operator|.
name|getUserID
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValidSubjectWithCredentials
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|SimpleCredentials
argument_list|>
name|publicCreds
init|=
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"testUserId"
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|(
literal|false
argument_list|,
name|principals
argument_list|,
name|publicCreds
argument_list|,
name|Collections
operator|.
expr|<
name|Object
operator|>
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|ContentSession
name|cs
init|=
name|Subject
operator|.
name|doAsPrivileged
argument_list|(
name|subject
argument_list|,
operator|new
name|PrivilegedAction
argument_list|<
name|ContentSession
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ContentSession
name|run
parameter_list|()
block|{
try|try
block|{
return|return
name|login
argument_list|(
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|AuthInfo
name|authInfo
init|=
name|cs
operator|.
name|getAuthInfo
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
name|AuthInfo
operator|.
name|EMPTY
argument_list|,
name|authInfo
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|principals
argument_list|,
name|authInfo
operator|.
name|getPrincipals
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"testUserId"
argument_list|,
name|authInfo
operator|.
name|getUserID
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValidReadSubjectWithCredentials
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|SimpleCredentials
argument_list|>
name|publicCreds
init|=
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|SimpleCredentials
argument_list|(
literal|"testUserId"
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|(
literal|true
argument_list|,
name|principals
argument_list|,
name|publicCreds
argument_list|,
name|Collections
operator|.
expr|<
name|Object
operator|>
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|ContentSession
name|cs
init|=
name|Subject
operator|.
name|doAsPrivileged
argument_list|(
name|subject
argument_list|,
operator|new
name|PrivilegedAction
argument_list|<
name|ContentSession
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ContentSession
name|run
parameter_list|()
block|{
try|try
block|{
return|return
name|login
argument_list|(
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|AuthInfo
name|authInfo
init|=
name|cs
operator|.
name|getAuthInfo
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
name|AuthInfo
operator|.
name|EMPTY
argument_list|,
name|authInfo
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|principals
argument_list|,
name|authInfo
operator|.
name|getPrincipals
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"testUserId"
argument_list|,
name|authInfo
operator|.
name|getUserID
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValidSubjectWithAuthInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|AuthInfo
name|info
init|=
operator|new
name|AuthInfoImpl
argument_list|(
literal|"testUserId"
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|emptyMap
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|Principal
operator|>
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|AuthInfo
argument_list|>
name|publicCreds
init|=
name|Collections
operator|.
name|singleton
argument_list|(
name|info
argument_list|)
decl_stmt|;
specifier|final
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|(
literal|false
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|TestPrincipal
argument_list|()
argument_list|)
argument_list|,
name|publicCreds
argument_list|,
name|Collections
operator|.
expr|<
name|Object
operator|>
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|ContentSession
name|cs
init|=
name|Subject
operator|.
name|doAsPrivileged
argument_list|(
name|subject
argument_list|,
operator|new
name|PrivilegedAction
argument_list|<
name|ContentSession
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ContentSession
name|run
parameter_list|()
block|{
try|try
block|{
return|return
name|login
argument_list|(
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|assertSame
argument_list|(
name|info
argument_list|,
name|cs
operator|.
name|getAuthInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSubjectAndCredentials
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|(
literal|true
argument_list|,
name|principals
argument_list|,
name|Collections
operator|.
expr|<
name|Object
operator|>
name|emptySet
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|Object
operator|>
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|ContentSession
name|cs
init|=
name|Subject
operator|.
name|doAsPrivileged
argument_list|(
name|subject
argument_list|,
operator|new
name|PrivilegedAction
argument_list|<
name|ContentSession
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ContentSession
name|run
parameter_list|()
block|{
name|ContentSession
name|cs
decl_stmt|;
try|try
block|{
name|cs
operator|=
name|login
argument_list|(
operator|new
name|GuestCredentials
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|cs
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"Login should have failed."
argument_list|,
name|cs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNullLogin
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|cs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cs
operator|=
name|login
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Null login without pre-auth subject should fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LoginException
name|e
parameter_list|)
block|{
comment|// success
block|}
finally|finally
block|{
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSystemSubject
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentSession
name|cs
init|=
name|Subject
operator|.
name|doAsPrivileged
argument_list|(
name|SystemSubject
operator|.
name|INSTANCE
argument_list|,
operator|new
name|PrivilegedAction
argument_list|<
name|ContentSession
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ContentSession
name|run
parameter_list|()
block|{
try|try
block|{
return|return
name|login
argument_list|(
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|AuthInfo
name|authInfo
init|=
name|cs
operator|.
name|getAuthInfo
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
name|AuthInfo
operator|.
name|EMPTY
argument_list|,
name|authInfo
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|SystemSubject
operator|.
name|INSTANCE
operator|.
name|getPrincipals
argument_list|()
argument_list|,
name|authInfo
operator|.
name|getPrincipals
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|authInfo
operator|.
name|getUserID
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|cs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
class|class
name|TestPrincipal
implements|implements
name|Principal
block|{
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"test"
return|;
block|}
block|}
block|}
end_class

end_unit

