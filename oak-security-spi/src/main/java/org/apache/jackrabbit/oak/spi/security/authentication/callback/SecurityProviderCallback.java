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
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|SecurityProvider
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
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Callback implementation to set and get the {@link SecurityProvider}.  */
end_comment

begin_class
specifier|public
class|class
name|SecurityProviderCallback
implements|implements
name|Callback
block|{
specifier|private
name|SecurityProvider
name|securityProvider
decl_stmt|;
annotation|@
name|Nullable
specifier|public
name|SecurityProvider
name|getSecurityProvider
parameter_list|()
block|{
return|return
name|securityProvider
return|;
block|}
specifier|public
name|void
name|setSecurityProvider
parameter_list|(
name|SecurityProvider
name|securityProvider
parameter_list|)
block|{
name|this
operator|.
name|securityProvider
operator|=
name|securityProvider
expr_stmt|;
block|}
block|}
end_class

end_unit

