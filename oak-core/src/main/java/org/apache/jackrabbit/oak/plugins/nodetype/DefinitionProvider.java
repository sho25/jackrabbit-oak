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
name|plugins
operator|.
name|nodetype
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
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|ConstraintViolationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeDefinition
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|PropertyDefinition
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
name|PropertyState
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
name|Tree
import|;
end_import

begin_comment
comment|/**  * DefinitionProvider... TODO  */
end_comment

begin_interface
specifier|public
interface|interface
name|DefinitionProvider
block|{
annotation|@
name|Nonnull
name|NodeDefinition
name|getRootDefinition
parameter_list|()
throws|throws
name|RepositoryException
function_decl|;
comment|/**      * Returns the node definition for a child node of {@code parent} named      * {@code nodeName} with a default primary type. First the non-residual      * child node definitions of {@code parent} are checked matching the      * given node name. Then the residual definitions are checked.      *      * @param parent   the parent node.      * @param nodeName The internal oak name of the child node.      * @return the applicable node definition.      * @throws ConstraintViolationException If no matching definition can be found.      * @throws RepositoryException If another error occurs.      */
annotation|@
name|Nonnull
name|NodeDefinition
name|getDefinition
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|parent
parameter_list|,
annotation|@
name|Nonnull
name|String
name|nodeName
parameter_list|)
throws|throws
name|ConstraintViolationException
throws|,
name|RepositoryException
function_decl|;
comment|/**      * Calculates the applicable definition for the child node under the given      * parent node.      *      * @param parent The parent node.      * @param targetNode The child node for which the definition is calculated.      * @return the definition of the target node.      * @throws ConstraintViolationException If no matching definition can be found.      * @throws RepositoryException If another error occurs.      */
annotation|@
name|Nonnull
name|NodeDefinition
name|getDefinition
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|Tree
name|targetNode
parameter_list|)
throws|throws
name|ConstraintViolationException
throws|,
name|RepositoryException
function_decl|;
comment|/**      * Calculates the applicable definition for the property state under the      * given parent tree.      *      * @param parent The parent tree.      * @param propertyState The target property.      * @return the definition for the target property.      * @throws ConstraintViolationException If no matching definition can be found.      * @throws RepositoryException If another error occurs.      */
annotation|@
name|Nonnull
name|PropertyDefinition
name|getDefinition
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|PropertyState
name|propertyState
parameter_list|,
name|boolean
name|exactTypeMatch
parameter_list|)
throws|throws
name|ConstraintViolationException
throws|,
name|RepositoryException
function_decl|;
block|}
end_interface

end_unit

