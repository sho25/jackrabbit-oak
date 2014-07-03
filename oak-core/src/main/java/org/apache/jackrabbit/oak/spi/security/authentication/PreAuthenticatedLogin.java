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
name|jcr
operator|.
name|Credentials
import|;
end_import

begin_comment
comment|/**  * {@code PreAuthenticatedLogin} is used as marker in the shared map of the login context. it indicates that the  * respective user is pre authenticated on an external system. Note that is class is only used internally by the  * login modules and cannot be "abused" from outside.  */
end_comment

begin_class
specifier|final
specifier|public
class|class
name|PreAuthenticatedLogin
block|{
specifier|public
specifier|static
specifier|final
name|Credentials
name|PRE_AUTHENTICATED
init|=
operator|new
name|Credentials
argument_list|()
block|{ }
decl_stmt|;
specifier|private
specifier|final
name|String
name|userId
decl_stmt|;
specifier|public
name|PreAuthenticatedLogin
parameter_list|(
name|String
name|userId
parameter_list|)
block|{
name|this
operator|.
name|userId
operator|=
name|userId
expr_stmt|;
block|}
specifier|public
name|String
name|getUserId
parameter_list|()
block|{
return|return
name|userId
return|;
block|}
block|}
end_class

end_unit

