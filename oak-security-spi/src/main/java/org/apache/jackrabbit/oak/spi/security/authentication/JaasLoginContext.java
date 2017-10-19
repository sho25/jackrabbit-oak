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
name|Subject
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
name|callback
operator|.
name|CallbackHandler
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
name|login
operator|.
name|Configuration
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
name|login
operator|.
name|LoginException
import|;
end_import

begin_comment
comment|/**  * Bridge class that connects the JAAS {@link javax.security.auth.login.LoginContext} class with the  * {@link LoginContext} interface used by Oak.  */
end_comment

begin_class
specifier|public
class|class
name|JaasLoginContext
extends|extends
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginContext
implements|implements
name|LoginContext
block|{
specifier|public
name|JaasLoginContext
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|LoginException
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JaasLoginContext
parameter_list|(
name|String
name|name
parameter_list|,
name|Subject
name|subject
parameter_list|)
throws|throws
name|LoginException
block|{
name|super
argument_list|(
name|name
argument_list|,
name|subject
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JaasLoginContext
parameter_list|(
name|String
name|name
parameter_list|,
name|CallbackHandler
name|handler
parameter_list|)
throws|throws
name|LoginException
block|{
name|super
argument_list|(
name|name
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JaasLoginContext
parameter_list|(
name|String
name|name
parameter_list|,
name|Subject
name|subject
parameter_list|,
name|CallbackHandler
name|handler
parameter_list|)
throws|throws
name|LoginException
block|{
name|super
argument_list|(
name|name
argument_list|,
name|subject
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
specifier|public
name|JaasLoginContext
parameter_list|(
name|String
name|name
parameter_list|,
name|Subject
name|subject
parameter_list|,
name|CallbackHandler
name|handler
parameter_list|,
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|LoginException
block|{
name|super
argument_list|(
name|name
argument_list|,
name|subject
argument_list|,
name|handler
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
