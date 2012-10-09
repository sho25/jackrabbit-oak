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
name|GuestCredentials
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
name|Tree
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
name|Authentication
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
name|PrincipalProvider
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
name|user
operator|.
name|AuthorizableType
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
name|user
operator|.
name|PasswordUtility
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
name|user
operator|.
name|UserProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * AuthenticationImpl...  */
end_comment

begin_class
specifier|public
class|class
name|AuthenticationImpl
implements|implements
name|Authentication
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AuthenticationImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|userId
decl_stmt|;
specifier|private
specifier|final
name|UserProvider
name|userProvider
decl_stmt|;
specifier|private
specifier|final
name|PrincipalProvider
name|principalProvider
decl_stmt|;
specifier|private
name|Tree
name|userTree
decl_stmt|;
specifier|public
name|AuthenticationImpl
parameter_list|(
name|String
name|userId
parameter_list|,
name|UserProvider
name|userProvider
parameter_list|,
name|PrincipalProvider
name|principalProvider
parameter_list|)
block|{
name|this
operator|.
name|userId
operator|=
name|userId
expr_stmt|;
name|this
operator|.
name|userProvider
operator|=
name|userProvider
expr_stmt|;
name|this
operator|.
name|principalProvider
operator|=
name|principalProvider
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|authenticate
parameter_list|(
name|Credentials
name|credentials
parameter_list|)
block|{
comment|// TODO
return|return
literal|true
return|;
comment|//        Tree userTree = getUserTree();
comment|//        if (userTree == null || userProvider.isDisabled(userTree)) {
comment|//            return false;
comment|//        }
comment|//
comment|//        if (credentials instanceof SimpleCredentials) {
comment|//            SimpleCredentials creds = (SimpleCredentials) credentials;
comment|//            return PasswordUtility.isSame(userProvider.getPasswordHash(userTree), creds.getPassword());
comment|//        } else {
comment|//            return credentials instanceof GuestCredentials;
comment|//        }
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|impersonate
parameter_list|(
name|Subject
name|subject
parameter_list|)
block|{
comment|// TODO
return|return
literal|true
return|;
comment|//        Tree userTree = getUserTree();
comment|//        if (userTree == null || userProvider.isDisabled(userTree)) {
comment|//            return false;
comment|//        } else {
comment|//            try {
comment|//                return userProvider.getImpersonation(userTree, principalProvider).allows(subject);
comment|//            } catch (RepositoryException e) {
comment|//                log.debug("Error while validating impersonation", e.getMessage());
comment|//                return false;
comment|//            }
comment|//        }
block|}
comment|//--------------------------------------------------------------------------
specifier|private
name|Tree
name|getUserTree
parameter_list|()
block|{
if|if
condition|(
name|userProvider
operator|==
literal|null
operator|||
name|userId
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|userTree
operator|==
literal|null
condition|)
block|{
name|userTree
operator|=
name|userProvider
operator|.
name|getAuthorizable
argument_list|(
name|userId
argument_list|,
name|AuthorizableType
operator|.
name|USER
argument_list|)
expr_stmt|;
block|}
return|return
name|userTree
return|;
block|}
block|}
end_class

end_unit

