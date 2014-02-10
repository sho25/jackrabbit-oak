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
name|external
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * The external identity provider management.  *  * The default manager is registered as OSGi service and can also be retrieved via  * {@link org.apache.jackrabbit.oak.spi.security.SecurityProvider#getConfiguration(Class)}  */
end_comment

begin_interface
specifier|public
interface|interface
name|ExternalIdentityProviderManager
block|{
comment|/**      * Returns the registered identity provider with the given name.      * @param name the provider name      * @return the registered provider or {@code null}      */
annotation|@
name|CheckForNull
name|ExternalIdentityProvider
name|getProvider
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

