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
name|privilege
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|authorization
operator|.
name|PrivilegeManager
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
name|lifecycle
operator|.
name|RepositoryInitializer
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
name|SecurityConfiguration
import|;
end_import

begin_comment
comment|/**  * Interface for the privilege management configuration.  */
end_comment

begin_interface
specifier|public
interface|interface
name|PrivilegeConfiguration
extends|extends
name|SecurityConfiguration
block|{
comment|/**      * Creates a new instance of {@link PrivilegeManager}.      *      * @param root           The root for which the privilege manager should be created.      * @param namePathMapper The name and path mapper to be used.      * @return A new {@code PrivilegeManager}.      */
annotation|@
name|Nonnull
name|PrivilegeManager
name|getPrivilegeManager
parameter_list|(
name|Root
name|root
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
function_decl|;
comment|/**      * Returns the privilege specific repository initializer.      *      * @return An instance of {@code RepositoryInitializer} that initializes      *         the built-in privileges.      */
annotation|@
name|Nonnull
name|RepositoryInitializer
name|getPrivilegeInitializer
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

