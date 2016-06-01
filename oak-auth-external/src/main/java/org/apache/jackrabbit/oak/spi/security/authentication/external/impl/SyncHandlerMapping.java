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
operator|.
name|impl
package|;
end_package

begin_comment
comment|/**  * Marker interface identifying classes that map a given  * {@link org.apache.jackrabbit.oak.spi.security.authentication.external.SyncHandler SyncHandler}  * to an {@link org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentityProvider ExternalIdentityProvider}  * where both are identified by their name.  *  * @see org.apache.jackrabbit.oak.spi.security.authentication.external.SyncManager#getSyncHandler(String)  * @see org.apache.jackrabbit.oak.spi.security.authentication.external.SyncHandler#getName()  * @see org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentityProviderManager#getProvider(String)  * @see org.apache.jackrabbit.oak.spi.security.authentication.external.ExternalIdentityProvider#getName()  * @see ExternalLoginModuleFactory  */
end_comment

begin_interface
specifier|public
interface|interface
name|SyncHandlerMapping
block|{
comment|/**      * Name of the parameter that configures the name of the external identity provider.      */
name|String
name|PARAM_IDP_NAME
init|=
literal|"idp.name"
decl_stmt|;
comment|/**      * Name of the parameter that configures the name of the synchronization handler.      */
name|String
name|PARAM_SYNC_HANDLER_NAME
init|=
literal|"sync.handlerName"
decl_stmt|;
block|}
end_interface

end_unit

