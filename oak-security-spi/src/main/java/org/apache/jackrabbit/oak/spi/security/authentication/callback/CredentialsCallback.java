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
name|jcr
operator|.
name|Credentials
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
name|Callback
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
comment|/**  * Callback implementation to retrieve {@code Credentials}.  */
end_comment

begin_class
specifier|public
class|class
name|CredentialsCallback
implements|implements
name|Callback
block|{
specifier|private
name|Credentials
name|credentials
decl_stmt|;
comment|/**      * Returns the {@link Credentials} that have been set before using      * {@link #setCredentials(javax.jcr.Credentials)}.      *      * @return The {@link Credentials} to be used for authentication or {@code null}.      */
annotation|@
name|Nullable
specifier|public
name|Credentials
name|getCredentials
parameter_list|()
block|{
return|return
name|credentials
return|;
block|}
comment|/**      * Set the credentials.      *      * @param credentials The credentials to be used in the authentication      * process. They may be null if no credentials have been specified in      * {@link org.apache.jackrabbit.oak.api.ContentRepository#login(javax.jcr.Credentials, String)}      */
specifier|public
name|void
name|setCredentials
parameter_list|(
name|Credentials
name|credentials
parameter_list|)
block|{
name|this
operator|.
name|credentials
operator|=
name|credentials
expr_stmt|;
block|}
block|}
end_class

end_unit

