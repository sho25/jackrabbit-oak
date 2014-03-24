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
name|Set
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
name|security
operator|.
name|AccessControlPolicy
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlPolicyIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|Privilege
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
name|JackrabbitAccessControlManager
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
name|JackrabbitAccessControlPolicy
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

begin_comment
comment|/**  * This implementation of {@code JackrabbitAccessControlManager} delegates back to a  * delegatee wrapping each call into a {@link SessionOperation} closure.  *  * @see SessionDelegate#perform(SessionOperation)  */
end_comment

begin_class
specifier|public
class|class
name|JackrabbitAccessControlManagerDelegator
implements|implements
name|JackrabbitAccessControlManager
block|{
specifier|private
specifier|final
name|JackrabbitAccessControlManager
name|jackrabbitACManager
decl_stmt|;
specifier|private
specifier|final
name|SessionDelegate
name|delegate
decl_stmt|;
specifier|private
specifier|final
name|AccessControlManagerDelegator
name|jcrACManager
decl_stmt|;
specifier|public
name|JackrabbitAccessControlManagerDelegator
parameter_list|(
name|SessionDelegate
name|delegate
parameter_list|,
name|JackrabbitAccessControlManager
name|acManager
parameter_list|)
block|{
name|this
operator|.
name|jackrabbitACManager
operator|=
name|acManager
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|jcrACManager
operator|=
operator|new
name|AccessControlManagerDelegator
argument_list|(
name|delegate
argument_list|,
name|acManager
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|JackrabbitAccessControlPolicy
index|[]
name|getApplicablePolicies
parameter_list|(
specifier|final
name|Principal
name|principal
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|delegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|JackrabbitAccessControlPolicy
index|[]
argument_list|>
argument_list|(
literal|"getApplicablePolicies"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|JackrabbitAccessControlPolicy
index|[]
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|jackrabbitACManager
operator|.
name|getApplicablePolicies
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
name|JackrabbitAccessControlPolicy
index|[]
name|getPolicies
parameter_list|(
specifier|final
name|Principal
name|principal
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|delegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|JackrabbitAccessControlPolicy
index|[]
argument_list|>
argument_list|(
literal|"getPolicies"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|JackrabbitAccessControlPolicy
index|[]
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|jackrabbitACManager
operator|.
name|getPolicies
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
name|AccessControlPolicy
index|[]
name|getEffectivePolicies
parameter_list|(
specifier|final
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|delegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|AccessControlPolicy
index|[]
argument_list|>
argument_list|(
literal|"getEffectivePolicies"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|AccessControlPolicy
index|[]
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|jackrabbitACManager
operator|.
name|getEffectivePolicies
argument_list|(
name|principals
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
name|hasPrivileges
parameter_list|(
specifier|final
name|String
name|absPath
parameter_list|,
specifier|final
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|,
specifier|final
name|Privilege
index|[]
name|privileges
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|delegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Boolean
argument_list|>
argument_list|(
literal|"hasPrivileges"
argument_list|)
block|{
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
name|jackrabbitACManager
operator|.
name|hasPrivileges
argument_list|(
name|absPath
argument_list|,
name|principals
argument_list|,
name|privileges
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
name|Privilege
index|[]
name|getPrivileges
parameter_list|(
specifier|final
name|String
name|absPath
parameter_list|,
specifier|final
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|delegate
operator|.
name|perform
argument_list|(
operator|new
name|SessionOperation
argument_list|<
name|Privilege
index|[]
argument_list|>
argument_list|(
literal|"getPrivileges"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Privilege
index|[]
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|jackrabbitACManager
operator|.
name|getPrivileges
argument_list|(
name|absPath
argument_list|,
name|principals
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
name|Privilege
index|[]
name|getSupportedPrivileges
parameter_list|(
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|jcrACManager
operator|.
name|getSupportedPrivileges
argument_list|(
name|absPath
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Privilege
name|privilegeFromName
parameter_list|(
name|String
name|privilegeName
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|jcrACManager
operator|.
name|privilegeFromName
argument_list|(
name|privilegeName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasPrivileges
parameter_list|(
name|String
name|absPath
parameter_list|,
name|Privilege
index|[]
name|privileges
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|jcrACManager
operator|.
name|hasPrivileges
argument_list|(
name|absPath
argument_list|,
name|privileges
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Privilege
index|[]
name|getPrivileges
parameter_list|(
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|jcrACManager
operator|.
name|getPrivileges
argument_list|(
name|absPath
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|AccessControlPolicy
index|[]
name|getPolicies
parameter_list|(
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|jcrACManager
operator|.
name|getPolicies
argument_list|(
name|absPath
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|AccessControlPolicy
index|[]
name|getEffectivePolicies
parameter_list|(
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|jcrACManager
operator|.
name|getEffectivePolicies
argument_list|(
name|absPath
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|AccessControlPolicyIterator
name|getApplicablePolicies
parameter_list|(
name|String
name|absPath
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|jcrACManager
operator|.
name|getApplicablePolicies
argument_list|(
name|absPath
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setPolicy
parameter_list|(
name|String
name|absPath
parameter_list|,
name|AccessControlPolicy
name|policy
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|jcrACManager
operator|.
name|setPolicy
argument_list|(
name|absPath
argument_list|,
name|policy
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removePolicy
parameter_list|(
name|String
name|absPath
parameter_list|,
name|AccessControlPolicy
name|policy
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|jcrACManager
operator|.
name|removePolicy
argument_list|(
name|absPath
argument_list|,
name|policy
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

