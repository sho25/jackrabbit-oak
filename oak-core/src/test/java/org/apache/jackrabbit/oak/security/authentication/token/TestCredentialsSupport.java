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
operator|.
name|token
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|annotation
operator|.
name|CheckForNull
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
name|Credentials
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
name|credentials
operator|.
name|CredentialsSupport
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
name|token
operator|.
name|TokenConstants
import|;
end_import

begin_comment
comment|/**  * Dummy implementation of {@link CredentialsSupport} that only supports  * {@link org.apache.jackrabbit.oak.security.authentication.token.TestCredentialsSupport.Creds}  * and always returns the same user ID upon {@link CredentialsSupport#getUserId(Credentials)}.  */
end_comment

begin_class
specifier|public
class|class
name|TestCredentialsSupport
implements|implements
name|CredentialsSupport
block|{
specifier|private
specifier|final
name|String
name|uid
decl_stmt|;
name|TestCredentialsSupport
parameter_list|()
block|{
name|this
operator|.
name|uid
operator|=
literal|null
expr_stmt|;
block|}
name|TestCredentialsSupport
parameter_list|(
annotation|@
name|Nonnull
name|String
name|uid
parameter_list|)
block|{
name|this
operator|.
name|uid
operator|=
name|uid
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Class
argument_list|>
name|getCredentialClasses
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
name|Creds
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
annotation|@
name|Override
specifier|public
name|String
name|getUserId
parameter_list|(
annotation|@
name|Nonnull
name|Credentials
name|credentials
parameter_list|)
block|{
if|if
condition|(
name|credentials
operator|instanceof
name|Creds
condition|)
block|{
return|return
name|uid
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|getAttributes
parameter_list|(
annotation|@
name|Nonnull
name|Credentials
name|credentials
parameter_list|)
block|{
if|if
condition|(
name|credentials
operator|instanceof
name|Creds
condition|)
block|{
return|return
operator|(
operator|(
name|Creds
operator|)
name|credentials
operator|)
operator|.
name|attributes
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|setAttributes
parameter_list|(
annotation|@
name|Nonnull
name|Credentials
name|credentials
parameter_list|,
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
name|attributes
parameter_list|)
block|{
if|if
condition|(
name|credentials
operator|instanceof
name|Creds
condition|)
block|{
operator|(
operator|(
name|Creds
operator|)
name|credentials
operator|)
operator|.
name|attributes
operator|.
name|putAll
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
block|}
specifier|static
specifier|final
class|class
name|Creds
implements|implements
name|Credentials
block|{
specifier|private
specifier|final
name|Map
name|attributes
decl_stmt|;
name|Creds
parameter_list|()
block|{
name|attributes
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
name|attributes
operator|.
name|put
argument_list|(
name|TokenConstants
operator|.
name|TOKEN_ATTRIBUTE
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

