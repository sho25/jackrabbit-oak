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
name|callback
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|callback
operator|.
name|Callback
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

begin_comment
comment|/**  * Callback implementation used to pass a {@link UserManager} to the  * login module.  */
end_comment

begin_class
specifier|public
class|class
name|UserManagerCallback
implements|implements
name|Callback
block|{
specifier|private
name|UserManager
name|userManager
decl_stmt|;
comment|/**      * Returns the user provider as set using      * {@link #setUserManager(org.apache.jackrabbit.api.security.user.UserManager)}      * or {@code null}.      *      * @return an instance of {@code UserManager} or {@code null} if no      * provider has been set before.      */
specifier|public
name|UserManager
name|getUserManager
parameter_list|()
block|{
return|return
name|userManager
return|;
block|}
comment|/**      * Sets the {@code UserManager} that is being used during the      * authentication process.      *      * @param userManager The user provider to use during the      * authentication process.      */
specifier|public
name|void
name|setUserManager
parameter_list|(
name|UserManager
name|userManager
parameter_list|)
block|{
name|this
operator|.
name|userManager
operator|=
name|userManager
expr_stmt|;
block|}
block|}
end_class

end_unit

