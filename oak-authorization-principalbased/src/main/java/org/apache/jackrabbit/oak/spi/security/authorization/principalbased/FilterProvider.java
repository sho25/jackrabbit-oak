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
name|authorization
operator|.
name|principalbased
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
name|Root
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
name|namepath
operator|.
name|NamePathMapper
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
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|annotation
operator|.
name|versioning
operator|.
name|ProviderType
import|;
end_import

begin_comment
comment|/**  * Interface that allows to define the principals for which principal based access control management and permission  * evaluation can be executed. For any other principals this module would never take effect.  */
end_comment

begin_interface
annotation|@
name|ProviderType
specifier|public
interface|interface
name|FilterProvider
block|{
comment|/**      * Reveals if the given implementation is able to handle access control at the tree defined by the given {@code oakPath}.      *      * @param oakPath The absolute oak path to be tested.      * @return {@code true} if the given path is supported by this implememntation, {@code false} otherwise.      */
name|boolean
name|handlesPath
parameter_list|(
annotation|@
name|NotNull
name|String
name|oakPath
parameter_list|)
function_decl|;
comment|/**      * Returns the root path of handled by the filer. In case multiple paths are supported this method returns the common      * ancestor path.      *      * @return An absolute oak path.      */
annotation|@
name|NotNull
name|String
name|getFilterRoot
parameter_list|()
function_decl|;
comment|/**      * Returns the {@link Filter} associated with this provider implementation.      *      * @param securityProvider The security provider.      * @param root The reading/editing root.      * @param namePathMapper The name path mapper.      * @return A new filter associated with the given parameters.      */
annotation|@
name|NotNull
name|Filter
name|getFilter
parameter_list|(
annotation|@
name|NotNull
name|SecurityProvider
name|securityProvider
parameter_list|,
annotation|@
name|NotNull
name|Root
name|root
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

