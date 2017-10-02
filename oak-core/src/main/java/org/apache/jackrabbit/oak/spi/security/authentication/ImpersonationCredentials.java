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
name|AuthInfo
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

begin_comment
comment|/**  * Implementation of the JCR {@code Credentials} interface used to distinguish  * a regular login request from {@link javax.jcr.Session#impersonate(javax.jcr.Credentials)}.  */
end_comment

begin_class
specifier|public
class|class
name|ImpersonationCredentials
implements|implements
name|Credentials
block|{
specifier|private
specifier|final
name|Credentials
name|baseCredentials
decl_stmt|;
specifier|private
specifier|final
name|AuthInfo
name|authInfo
decl_stmt|;
specifier|public
name|ImpersonationCredentials
parameter_list|(
name|Credentials
name|baseCredentials
parameter_list|,
name|AuthInfo
name|authInfo
parameter_list|)
block|{
name|this
operator|.
name|baseCredentials
operator|=
name|baseCredentials
expr_stmt|;
name|this
operator|.
name|authInfo
operator|=
name|authInfo
expr_stmt|;
block|}
comment|/**      * Returns the {@code Credentials} originally passed to      * {@link javax.jcr.Session#impersonate(javax.jcr.Credentials)}.      *      * @return the {@code Credentials} originally passed to      * {@link javax.jcr.Session#impersonate(javax.jcr.Credentials)}.      */
specifier|public
name|Credentials
name|getBaseCredentials
parameter_list|()
block|{
return|return
name|baseCredentials
return|;
block|}
comment|/**      * Returns the {@code AuthInfo} present with the editing session that want      * to impersonate.      *      * @return {@code AuthInfo} present with the editing session that want      * to impersonate.      * @see org.apache.jackrabbit.oak.api.ContentSession#getAuthInfo()      */
specifier|public
name|AuthInfo
name|getImpersonatorInfo
parameter_list|()
block|{
return|return
name|authInfo
return|;
block|}
block|}
end_class

end_unit

