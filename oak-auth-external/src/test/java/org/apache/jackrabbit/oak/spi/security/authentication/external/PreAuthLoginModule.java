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
name|authentication
operator|.
name|external
package|;
end_package

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
name|Credentials
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|AbstractLoginModule
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
name|PreAuthenticatedLogin
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
name|PreAuthLoginModule
extends|extends
name|AbstractLoginModule
block|{
specifier|public
name|PreAuthLoginModule
parameter_list|()
block|{}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|protected
name|Set
argument_list|<
name|Class
argument_list|>
name|getSupportedCredentials
parameter_list|()
block|{
return|return
name|ImmutableSet
operator|.
expr|<
name|Class
operator|>
name|of
argument_list|(
name|PreAuthCredentials
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|login
parameter_list|()
block|{
name|Credentials
name|credentials
init|=
name|getCredentials
argument_list|()
decl_stmt|;
if|if
condition|(
name|credentials
operator|instanceof
name|PreAuthCredentials
condition|)
block|{
name|PreAuthCredentials
name|pac
init|=
operator|(
name|PreAuthCredentials
operator|)
name|credentials
decl_stmt|;
name|String
name|userId
init|=
name|pac
operator|.
name|getUserId
argument_list|()
decl_stmt|;
if|if
condition|(
name|userId
operator|==
literal|null
condition|)
block|{
name|pac
operator|.
name|setMessage
argument_list|(
name|PreAuthCredentials
operator|.
name|PRE_AUTH_FAIL
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sharedState
operator|.
name|put
argument_list|(
name|SHARED_KEY_PRE_AUTH_LOGIN
argument_list|,
operator|new
name|PreAuthenticatedLogin
argument_list|(
name|userId
argument_list|)
argument_list|)
expr_stmt|;
name|sharedState
operator|.
name|put
argument_list|(
name|SHARED_KEY_CREDENTIALS
argument_list|,
operator|new
name|SimpleCredentials
argument_list|(
name|userId
argument_list|,
operator|new
name|char
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|sharedState
operator|.
name|put
argument_list|(
name|SHARED_KEY_LOGIN_NAME
argument_list|,
name|userId
argument_list|)
expr_stmt|;
name|pac
operator|.
name|setMessage
argument_list|(
name|PreAuthCredentials
operator|.
name|PRE_AUTH_DONE
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|commit
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|logout
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

