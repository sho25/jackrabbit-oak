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
name|UnsupportedRepositoryOperationException
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
name|NodeType
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
comment|/**  * EffectiveNodeType... TODO  */
end_comment

begin_interface
specifier|public
interface|interface
name|EffectiveNodeType
block|{
name|Iterable
argument_list|<
name|NodeType
argument_list|>
name|getAllNodeTypes
parameter_list|()
function_decl|;
comment|//Iterable<NodeType> getInheritedNodeTypes();
comment|//Iterable<NodeType> getMergedNodeTypes();
comment|/**      * Determines whether this effective node type representation includes      * (either through inheritance or aggregation) the given node type.      *      * @param nodeTypeName name of node type      * @return {@code true} if the given node type is included, otherwise {@code false}.      */
name|boolean
name|includesNodeType
parameter_list|(
name|String
name|nodeTypeName
parameter_list|)
function_decl|;
comment|/**      * Determines whether this effective node type representation includes      * (either through inheritance or aggregation) all of the given node types.      *      * @param nodeTypeNames array of node type names      * @return {@code true} if all of the given node types are included,      *         otherwise {@code false}      */
name|boolean
name|includesNodeTypes
parameter_list|(
name|String
index|[]
name|nodeTypeNames
parameter_list|)
function_decl|;
comment|/**      * Determines whether this effective node type supports adding      * the specified mixin.      * @param mixin name of mixin type      * @return {@code true} if the mixin type is supported, otherwise {@code false}      */
name|boolean
name|supportsMixin
parameter_list|(
name|String
name|mixin
parameter_list|)
function_decl|;
name|Iterable
argument_list|<
name|NodeDefinition
argument_list|>
name|getNodeDefinitions
parameter_list|()
function_decl|;
name|Iterable
argument_list|<
name|PropertyDefinition
argument_list|>
name|getPropertyDefinitions
parameter_list|()
function_decl|;
name|Iterable
argument_list|<
name|NodeDefinition
argument_list|>
name|getAutoCreateNodeDefinitions
parameter_list|()
function_decl|;
name|Iterable
argument_list|<
name|PropertyDefinition
argument_list|>
name|getAutoCreatePropertyDefinitions
parameter_list|()
function_decl|;
name|Iterable
argument_list|<
name|NodeDefinition
argument_list|>
name|getMandatoryNodeDefinitions
parameter_list|()
function_decl|;
name|Iterable
argument_list|<
name|PropertyDefinition
argument_list|>
name|getMandatoryPropertyDefinitions
parameter_list|()
function_decl|;
comment|/**      * Return all node definitions that match the specified oak name.      *      * @param oakName An internal oak name.      * @return All node definitions that match the given internal oak name.      */
annotation|@
name|Nonnull
name|Iterable
argument_list|<
name|NodeDefinition
argument_list|>
name|getNamedNodeDefinitions
parameter_list|(
name|String
name|oakName
parameter_list|)
function_decl|;
comment|/**      * Return all property definitions that match the specified oak name.      *      * @param oakName An internal oak name.      * @return All property definitions that match the given internal oak name.      */
annotation|@
name|Nonnull
name|Iterable
argument_list|<
name|PropertyDefinition
argument_list|>
name|getNamedPropertyDefinitions
parameter_list|(
name|String
name|oakName
parameter_list|)
function_decl|;
comment|/**      * Return all residual node definitions.      *      * @return All residual node definitions.      */
annotation|@
name|Nonnull
name|Iterable
argument_list|<
name|NodeDefinition
argument_list|>
name|getResidualNodeDefinitions
parameter_list|()
function_decl|;
comment|/**      * Return all residual property definitions.      *      * @return All residual property definitions.      */
annotation|@
name|Nonnull
name|Iterable
argument_list|<
name|PropertyDefinition
argument_list|>
name|getResidualPropertyDefinitions
parameter_list|()
function_decl|;
name|void
name|checkSetProperty
parameter_list|(
name|PropertyState
name|property
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
name|void
name|checkRemoveProperty
parameter_list|(
name|PropertyState
name|property
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
name|void
name|checkAddChildNode
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeType
name|nodeType
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
name|void
name|checkRemoveNode
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeType
name|nodeType
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
name|void
name|checkMandatoryItems
parameter_list|(
name|Tree
name|tree
parameter_list|)
throws|throws
name|ConstraintViolationException
function_decl|;
name|void
name|checkOrderableChildNodes
parameter_list|()
throws|throws
name|UnsupportedRepositoryOperationException
function_decl|;
block|}
end_interface

end_unit

