begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|delegate
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
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
name|security
operator|.
name|auth
operator|.
name|Subject
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
name|PrincipalIterator
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
name|oak
operator|.
name|jcr
operator|.
name|session
operator|.
name|operation
operator|.
name|SessionOperation
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

begin_comment
comment|/**  * This implementation of {@code Impersonation} delegates back to a  * delegatee wrapping each call into a {@link SessionOperation} closure.  *  * @see SessionDelegate#perform(SessionOperation)  */
end_comment

begin_class
specifier|final
class|class
name|ImpersonationDelegator
implements|implements
name|Impersonation
block|{
specifier|private
specifier|final
name|SessionDelegate
name|sessionDelegate
decl_stmt|;
specifier|private
specifier|final
name|Impersonation
name|impersonationDelegate
decl_stmt|;
specifier|private
name|ImpersonationDelegator
parameter_list|(
name|SessionDelegate
name|sessionDelegate
parameter_list|,
name|Impersonation
name|impersonationDelegate
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|!
operator|(
name|impersonationDelegate
operator|instanceof
name|ImpersonationDelegator
operator|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|sessionDelegate
operator|=
name|sessionDelegate
expr_stmt|;
name|this
operator|.
name|impersonationDelegate
operator|=
name|impersonationDelegate
expr_stmt|;
block|}
specifier|static
name|Impersonation
name|wrap
parameter_list|(
name|SessionDelegate
name|sessionDelegate
parameter_list|,
name|Impersonation
name|impersonation
parameter_list|)
block|{
if|if
condition|(
name|impersonation
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|new
name|ImpersonationDelegator
argument_list|(
name|sessionDelegate
argument_list|,
name|impersonation
argument_list|)
return|;
block|}
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|PrincipalIterator
name|getImpersonators
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|PrincipalIterator
argument_list|>
argument_list|(
literal|"getImpersonators"
argument_list|)
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|PrincipalIterator
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|impersonationDelegate
operator|.
name|getImpersonators
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|grantImpersonation
parameter_list|(
annotation|@
name|NotNull
specifier|final
name|Principal
name|principal
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Boolean
argument_list|>
argument_list|(
literal|"grantImpersonation"
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Boolean
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|impersonationDelegate
operator|.
name|grantImpersonation
argument_list|(
name|principal
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|revokeImpersonation
parameter_list|(
annotation|@
name|NotNull
specifier|final
name|Principal
name|principal
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Boolean
argument_list|>
argument_list|(
literal|"revokeImpersonation"
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Boolean
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|impersonationDelegate
operator|.
name|revokeImpersonation
argument_list|(
name|principal
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|allows
parameter_list|(
annotation|@
name|NotNull
specifier|final
name|Subject
name|subject
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Boolean
argument_list|>
argument_list|(
literal|"allows"
argument_list|)
block|{
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Boolean
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|impersonationDelegate
operator|.
name|allows
argument_list|(
name|subject
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
end_class

end_unit

