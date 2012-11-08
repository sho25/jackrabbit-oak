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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

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

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_comment
comment|/**  * PrivilegeDefinitionProvider... TODO  */
end_comment

begin_interface
specifier|public
interface|interface
name|PrivilegeDefinitionProvider
block|{
comment|/**      * Returns all privilege definitions accessible to this provider.      *      * @return all privilege definitions.      */
annotation|@
name|Nonnull
name|PrivilegeDefinition
index|[]
name|getPrivilegeDefinitions
parameter_list|()
function_decl|;
comment|/**      * Returns the privilege definition with the specified internal name.      *      * @param name The internal name of the privilege definition to be      * retrieved.      * @return The privilege definition with the given name or {@code null} if      * no such definition exists.      */
annotation|@
name|CheckForNull
name|PrivilegeDefinition
name|getPrivilegeDefinition
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Creates and registers a new custom privilege definition with the specified      * characteristics. If the registration succeeds the new definition is      * returned; otherwise an {@code RepositoryException} is thrown.      *      * @param privilegeName The name of the definition.      * @param isAbstract {@code true} if the privilege is abstract.      * @param declaredAggregateNames The set of declared aggregate privilege names.      * @return The new definition.      * @throws RepositoryException If the definition could not be registered.      */
annotation|@
name|Nonnull
name|PrivilegeDefinition
name|registerDefinition
parameter_list|(
name|String
name|privilegeName
parameter_list|,
name|boolean
name|isAbstract
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|declaredAggregateNames
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
block|}
end_interface

end_unit

