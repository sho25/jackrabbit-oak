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
name|user
operator|.
name|action
package|;
end_package

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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|Root
import|;
end_import

begin_comment
comment|/**  * Abstract implementation of the {@code AuthorizableAction} interface that  * doesn't perform any action. This is a convenience implementation allowing  * subclasses to only implement methods that need extra attention.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractAuthorizableAction
implements|implements
name|AuthorizableAction
block|{
comment|/**      * Doesn't perform any action.      *      * @see AuthorizableAction#onCreate(org.apache.jackrabbit.api.security.user.Group, Root)      */
annotation|@
name|Override
specifier|public
name|void
name|onCreate
parameter_list|(
name|Group
name|group
parameter_list|,
name|Root
name|root
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// nothing to do
block|}
comment|/**      * Doesn't perform any action.      *      * @see AuthorizableAction#onCreate(org.apache.jackrabbit.api.security.user.User, String, Root)      */
annotation|@
name|Override
specifier|public
name|void
name|onCreate
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|password
parameter_list|,
name|Root
name|root
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// nothing to do
block|}
comment|/**      * Doesn't perform any action.      *      * @see AuthorizableAction#onRemove(org.apache.jackrabbit.api.security.user.Authorizable, Root)      */
annotation|@
name|Override
specifier|public
name|void
name|onRemove
parameter_list|(
name|Authorizable
name|authorizable
parameter_list|,
name|Root
name|root
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// nothing to do
block|}
comment|/**      * Doesn't perform any action.      *      * @see AuthorizableAction#onPasswordChange(org.apache.jackrabbit.api.security.user.User, String, Root)      */
annotation|@
name|Override
specifier|public
name|void
name|onPasswordChange
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|newPassword
parameter_list|,
name|Root
name|root
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// nothing to do
block|}
block|}
end_class

end_unit

