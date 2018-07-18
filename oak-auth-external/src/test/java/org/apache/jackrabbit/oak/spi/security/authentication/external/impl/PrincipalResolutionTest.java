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
operator|.
name|impl
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
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|external
operator|.
name|ExternalIdentity
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
name|external
operator|.
name|ExternalIdentityException
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
name|external
operator|.
name|ExternalIdentityProvider
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
name|external
operator|.
name|ExternalIdentityRef
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
name|external
operator|.
name|PrincipalNameResolver
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
name|external
operator|.
name|TestIdentityProvider
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
class|class
name|PrincipalResolutionTest
extends|extends
name|DynamicSyncContextTest
block|{
annotation|@
name|Override
specifier|protected
name|ExternalIdentityProvider
name|createIDP
parameter_list|()
block|{
return|return
operator|new
name|PrincipalResolvingIDP
argument_list|()
return|;
block|}
specifier|private
specifier|final
class|class
name|PrincipalResolvingIDP
extends|extends
name|TestIdentityProvider
implements|implements
name|PrincipalNameResolver
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|String
name|fromExternalIdentityRef
parameter_list|(
annotation|@
name|NotNull
name|ExternalIdentityRef
name|externalIdentityRef
parameter_list|)
throws|throws
name|ExternalIdentityException
block|{
name|ExternalIdentity
name|identity
init|=
name|getIdentity
argument_list|(
name|externalIdentityRef
argument_list|)
decl_stmt|;
if|if
condition|(
name|identity
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ExternalIdentityException
argument_list|()
throw|;
block|}
else|else
block|{
return|return
name|identity
operator|.
name|getPrincipalName
argument_list|()
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

