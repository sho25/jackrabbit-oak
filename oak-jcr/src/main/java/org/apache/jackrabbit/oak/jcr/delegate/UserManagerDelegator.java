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
name|Iterator
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
name|UnsupportedRepositoryOperationException
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
name|AuthorizableExistsException
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
name|Query
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
name|jcr
operator|.
name|operation
operator|.
name|UserManagerOperation
import|;
end_import

begin_comment
comment|/**  * This implementation of {@code UserManager} delegates back to a  * delegatee wrapping each call into a {@link UserManager} closure.  *  * @see SessionDelegate#perform(org.apache.jackrabbit.oak.jcr.operation.SessionOperation)  */
end_comment

begin_class
specifier|public
class|class
name|UserManagerDelegator
implements|implements
name|UserManager
block|{
specifier|private
specifier|final
name|UserManager
name|userManagerDelegate
decl_stmt|;
specifier|private
specifier|final
name|SessionDelegate
name|sessionDelegate
decl_stmt|;
specifier|public
name|UserManagerDelegator
parameter_list|(
specifier|final
name|SessionDelegate
name|sessionDelegate
parameter_list|,
name|UserManager
name|userManagerDelegate
parameter_list|)
block|{
name|this
operator|.
name|userManagerDelegate
operator|=
name|userManagerDelegate
expr_stmt|;
name|this
operator|.
name|sessionDelegate
operator|=
name|sessionDelegate
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Authorizable
name|getAuthorizable
parameter_list|(
specifier|final
name|String
name|id
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
name|UserManagerOperation
argument_list|<
name|Authorizable
argument_list|>
argument_list|(
name|sessionDelegate
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Authorizable
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|userManagerDelegate
operator|.
name|getAuthorizable
argument_list|(
name|id
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
name|Authorizable
name|getAuthorizable
parameter_list|(
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
name|UserManagerOperation
argument_list|<
name|Authorizable
argument_list|>
argument_list|(
name|sessionDelegate
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Authorizable
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|userManagerDelegate
operator|.
name|getAuthorizable
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
name|Authorizable
name|getAuthorizableByPath
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
throws|throws
name|UnsupportedRepositoryOperationException
throws|,
name|RepositoryException
block|{
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|UserManagerOperation
argument_list|<
name|Authorizable
argument_list|>
argument_list|(
name|sessionDelegate
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Authorizable
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|userManagerDelegate
operator|.
name|getAuthorizableByPath
argument_list|(
name|path
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
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|findAuthorizables
parameter_list|(
specifier|final
name|String
name|relPath
parameter_list|,
specifier|final
name|String
name|value
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
name|UserManagerOperation
argument_list|<
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
argument_list|>
argument_list|(
name|sessionDelegate
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|userManagerDelegate
operator|.
name|findAuthorizables
argument_list|(
name|relPath
argument_list|,
name|value
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
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|findAuthorizables
parameter_list|(
specifier|final
name|String
name|relPath
parameter_list|,
specifier|final
name|String
name|value
parameter_list|,
specifier|final
name|int
name|searchType
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
name|UserManagerOperation
argument_list|<
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
argument_list|>
argument_list|(
name|sessionDelegate
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|userManagerDelegate
operator|.
name|findAuthorizables
argument_list|(
name|relPath
argument_list|,
name|value
argument_list|,
name|searchType
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
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|findAuthorizables
parameter_list|(
specifier|final
name|Query
name|query
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
name|UserManagerOperation
argument_list|<
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
argument_list|>
argument_list|(
name|sessionDelegate
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Authorizable
argument_list|>
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|userManagerDelegate
operator|.
name|findAuthorizables
argument_list|(
name|query
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
name|User
name|createUser
parameter_list|(
specifier|final
name|String
name|userID
parameter_list|,
specifier|final
name|String
name|password
parameter_list|)
throws|throws
name|AuthorizableExistsException
throws|,
name|RepositoryException
block|{
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|UserManagerOperation
argument_list|<
name|User
argument_list|>
argument_list|(
name|sessionDelegate
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|User
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|userManagerDelegate
operator|.
name|createUser
argument_list|(
name|userID
argument_list|,
name|password
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
name|User
name|createUser
parameter_list|(
specifier|final
name|String
name|userID
parameter_list|,
specifier|final
name|String
name|password
parameter_list|,
specifier|final
name|Principal
name|principal
parameter_list|,
specifier|final
name|String
name|intermediatePath
parameter_list|)
throws|throws
name|AuthorizableExistsException
throws|,
name|RepositoryException
block|{
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|UserManagerOperation
argument_list|<
name|User
argument_list|>
argument_list|(
name|sessionDelegate
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|User
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|userManagerDelegate
operator|.
name|createUser
argument_list|(
name|userID
argument_list|,
name|password
argument_list|,
name|principal
argument_list|,
name|intermediatePath
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
name|Group
name|createGroup
parameter_list|(
specifier|final
name|String
name|groupID
parameter_list|)
throws|throws
name|AuthorizableExistsException
throws|,
name|RepositoryException
block|{
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|UserManagerOperation
argument_list|<
name|Group
argument_list|>
argument_list|(
name|sessionDelegate
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Group
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|userManagerDelegate
operator|.
name|createGroup
argument_list|(
name|groupID
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
name|Group
name|createGroup
parameter_list|(
specifier|final
name|Principal
name|principal
parameter_list|)
throws|throws
name|AuthorizableExistsException
throws|,
name|RepositoryException
block|{
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|UserManagerOperation
argument_list|<
name|Group
argument_list|>
argument_list|(
name|sessionDelegate
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Group
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|userManagerDelegate
operator|.
name|createGroup
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
name|Group
name|createGroup
parameter_list|(
specifier|final
name|Principal
name|principal
parameter_list|,
specifier|final
name|String
name|intermediatePath
parameter_list|)
throws|throws
name|AuthorizableExistsException
throws|,
name|RepositoryException
block|{
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|UserManagerOperation
argument_list|<
name|Group
argument_list|>
argument_list|(
name|sessionDelegate
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Group
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|userManagerDelegate
operator|.
name|createGroup
argument_list|(
name|principal
argument_list|,
name|intermediatePath
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
name|Group
name|createGroup
parameter_list|(
specifier|final
name|String
name|groupID
parameter_list|,
specifier|final
name|Principal
name|principal
parameter_list|,
specifier|final
name|String
name|intermediatePath
parameter_list|)
throws|throws
name|AuthorizableExistsException
throws|,
name|RepositoryException
block|{
return|return
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|UserManagerOperation
argument_list|<
name|Group
argument_list|>
argument_list|(
name|sessionDelegate
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Group
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|userManagerDelegate
operator|.
name|createGroup
argument_list|(
name|groupID
argument_list|,
name|principal
argument_list|,
name|intermediatePath
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
name|isAutoSave
parameter_list|()
block|{
return|return
name|sessionDelegate
operator|.
name|safePerform
argument_list|(
operator|new
name|UserManagerOperation
argument_list|<
name|Boolean
argument_list|>
argument_list|(
name|sessionDelegate
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|perform
parameter_list|()
block|{
return|return
name|userManagerDelegate
operator|.
name|isAutoSave
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
name|void
name|autoSave
parameter_list|(
specifier|final
name|boolean
name|enable
parameter_list|)
throws|throws
name|UnsupportedRepositoryOperationException
throws|,
name|RepositoryException
block|{
name|sessionDelegate
operator|.
name|perform
argument_list|(
operator|new
name|UserManagerOperation
argument_list|<
name|Void
argument_list|>
argument_list|(
name|sessionDelegate
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Void
name|perform
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|userManagerDelegate
operator|.
name|autoSave
argument_list|(
name|enable
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

