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
name|user
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
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
name|JackrabbitSession
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
name|principal
operator|.
name|PrincipalManager
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

begin_class
specifier|public
class|class
name|PrincipalLookupTest
extends|extends
name|AbstractUserTest
block|{
specifier|private
specifier|static
name|PrincipalManager
name|getPrincipalManager
parameter_list|(
annotation|@
name|Nonnull
name|Session
name|session
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
operator|(
name|session
operator|instanceof
name|JackrabbitSession
operator|)
condition|)
block|{
throw|throw
operator|new
name|NotExecutableException
argument_list|(
literal|"JackrabbitSession expected"
argument_list|)
throw|;
block|}
return|return
operator|(
operator|(
name|JackrabbitSession
operator|)
name|session
operator|)
operator|.
name|getPrincipalManager
argument_list|()
return|;
block|}
specifier|public
name|void
name|testPrincipalManager
parameter_list|()
throws|throws
name|Exception
block|{
name|Principal
name|p
init|=
name|getPrincipalManager
argument_list|(
name|superuser
argument_list|)
operator|.
name|getPrincipal
argument_list|(
name|group
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|group
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPrincipalManagerOtherSession
parameter_list|()
throws|throws
name|Exception
block|{
name|Session
name|s2
init|=
literal|null
decl_stmt|;
try|try
block|{
name|s2
operator|=
name|getHelper
argument_list|()
operator|.
name|getSuperuserSession
argument_list|()
expr_stmt|;
name|Principal
name|p
init|=
name|getPrincipalManager
argument_list|(
name|s2
argument_list|)
operator|.
name|getPrincipal
argument_list|(
name|group
operator|.
name|getPrincipal
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|group
operator|.
name|getPrincipal
argument_list|()
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|s2
operator|!=
literal|null
condition|)
block|{
name|s2
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

