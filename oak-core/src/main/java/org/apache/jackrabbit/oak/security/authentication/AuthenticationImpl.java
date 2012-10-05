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
specifier|final
name|String
name|userID
decl_stmt|;
specifier|private
specifier|final
name|UserProvider
name|userProvider
decl_stmt|;
specifier|public
name|AuthenticationImpl
parameter_list|(
name|String
name|userID
parameter_list|,
name|UserProvider
name|userProvider
parameter_list|)
block|{
name|this
operator|.
name|userID
operator|=
name|userID
expr_stmt|;
name|this
operator|.
name|userProvider
operator|=
name|userProvider
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
comment|//        if (userProvider == null || userID == null) {
comment|//            return false;
comment|//        }
comment|//
comment|//        if (credentials instanceof SimpleCredentials) {
comment|//            SimpleCredentials creds = (SimpleCredentials) credentials;
comment|//            return userID.equals(creds.getUserID())&&
comment|//                    PasswordUtility.isSame(userProvider.getPassword(userID), creds.getPassword());
comment|//        } else if (credentials instanceof GuestCredentials) {
comment|//            return userProvider.getAuthorizable(userID) != null;
comment|//        } else {
comment|//            // unsupported credentials object
comment|//            return false;
comment|//        }
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|impersonate
parameter_list|(
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|)
block|{
comment|// TODO
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

