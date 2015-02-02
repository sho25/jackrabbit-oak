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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
import|;
end_import

begin_comment
comment|/**  * NodeTypeConstants... TODO  */
end_comment

begin_interface
specifier|public
interface|interface
name|NodeTypeConstants
extends|extends
name|JcrConstants
block|{
name|String
name|JCR_NODE_TYPES
init|=
literal|"jcr:nodeTypes"
decl_stmt|;
name|String
name|NODE_TYPES_PATH
init|=
literal|'/'
operator|+
name|JcrConstants
operator|.
name|JCR_SYSTEM
operator|+
literal|'/'
operator|+
name|JCR_NODE_TYPES
decl_stmt|;
name|String
name|JCR_IS_ABSTRACT
init|=
literal|"jcr:isAbstract"
decl_stmt|;
name|String
name|JCR_IS_QUERYABLE
init|=
literal|"jcr:isQueryable"
decl_stmt|;
name|String
name|JCR_IS_FULLTEXT_SEARCHABLE
init|=
literal|"jcr:isFullTextSearchable"
decl_stmt|;
name|String
name|JCR_IS_QUERY_ORDERABLE
init|=
literal|"jcr:isQueryOrderable"
decl_stmt|;
name|String
name|JCR_AVAILABLE_QUERY_OPERATORS
init|=
literal|"jcr:availableQueryOperators"
decl_stmt|;
comment|/**      * Constants for built-in repository defined node type names      */
name|String
name|NT_REP_ROOT
init|=
literal|"rep:root"
decl_stmt|;
name|String
name|NT_REP_SYSTEM
init|=
literal|"rep:system"
decl_stmt|;
name|String
name|NT_REP_NODE_TYPES
init|=
literal|"rep:nodeTypes"
decl_stmt|;
comment|/**      * @since oak 1.0      */
name|String
name|NT_REP_UNSTRUCTURED
init|=
literal|"rep:Unstructured"
decl_stmt|;
comment|/**      * @since oak 1.0      */
name|String
name|NT_OAK_UNSTRUCTURED
init|=
literal|"oak:Unstructured"
decl_stmt|;
name|String
name|NT_REP_NODE_TYPE
init|=
literal|"rep:NodeType"
decl_stmt|;
name|String
name|NT_REP_NAMED_PROPERTY_DEFINITIONS
init|=
literal|"rep:NamedPropertyDefinitions"
decl_stmt|;
name|String
name|NT_REP_PROPERTY_DEFINITIONS
init|=
literal|"rep:PropertyDefinitions"
decl_stmt|;
name|String
name|NT_REP_PROPERTY_DEFINITION
init|=
literal|"rep:PropertyDefinition"
decl_stmt|;
name|String
name|NT_REP_NAMED_CHILD_NODE_DEFINITIONS
init|=
literal|"rep:NamedChildNodeDefinitions"
decl_stmt|;
name|String
name|NT_REP_CHILD_NODE_DEFINITIONS
init|=
literal|"rep:ChildNodeDefinitions"
decl_stmt|;
name|String
name|NT_REP_CHILD_NODE_DEFINITION
init|=
literal|"rep:ChildNodeDefinition"
decl_stmt|;
comment|/**      * Additional name constants not present in JcrConstants      */
name|String
name|JCR_CREATEDBY
init|=
literal|"jcr:createdBy"
decl_stmt|;
name|String
name|JCR_LASTMODIFIEDBY
init|=
literal|"jcr:lastModifiedBy"
decl_stmt|;
name|String
name|MIX_CREATED
init|=
literal|"mix:created"
decl_stmt|;
name|String
name|MIX_LASTMODIFIED
init|=
literal|"mix:lastModified"
decl_stmt|;
comment|/**      * Merge conflict handling      */
name|String
name|MIX_REP_MERGE_CONFLICT
init|=
literal|"rep:MergeConflict"
decl_stmt|;
name|String
name|REP_OURS
init|=
literal|"rep:ours"
decl_stmt|;
name|String
name|RESIDUAL_NAME
init|=
literal|"*"
decl_stmt|;
comment|// Pre-compiled Oak type information fields
name|String
name|REP_SUPERTYPES
init|=
literal|"rep:supertypes"
decl_stmt|;
name|String
name|REP_PRIMARY_SUBTYPES
init|=
literal|"rep:primarySubtypes"
decl_stmt|;
name|String
name|REP_MIXIN_SUBTYPES
init|=
literal|"rep:mixinSubtypes"
decl_stmt|;
name|String
name|REP_MANDATORY_PROPERTIES
init|=
literal|"rep:mandatoryProperties"
decl_stmt|;
name|String
name|REP_MANDATORY_CHILD_NODES
init|=
literal|"rep:mandatoryChildNodes"
decl_stmt|;
name|String
name|REP_PROTECTED_PROPERTIES
init|=
literal|"rep:protectedProperties"
decl_stmt|;
name|String
name|REP_PROTECTED_CHILD_NODES
init|=
literal|"rep:protectedChildNodes"
decl_stmt|;
name|String
name|REP_HAS_PROTECTED_RESIDUAL_PROPERTIES
init|=
literal|"rep:hasProtectedResidualProperties"
decl_stmt|;
name|String
name|REP_HAS_PROTECTED_RESIDUAL_CHILD_NODES
init|=
literal|"rep:hasProtectedResidualChildNodes"
decl_stmt|;
name|String
name|REP_NAMED_SINGLE_VALUED_PROPERTIES
init|=
literal|"rep:namedSingleValuedProperties"
decl_stmt|;
name|String
name|REP_RESIDUAL_CHILD_NODE_DEFINITIONS
init|=
literal|"rep:residualChildNodeDefinitions"
decl_stmt|;
name|String
name|REP_NAMED_CHILD_NODE_DEFINITIONS
init|=
literal|"rep:namedChildNodeDefinitions"
decl_stmt|;
name|String
name|REP_RESIDUAL_PROPERTY_DEFINITIONS
init|=
literal|"rep:residualPropertyDefinitions"
decl_stmt|;
name|String
name|REP_NAMED_PROPERTY_DEFINITIONS
init|=
literal|"rep:namedPropertyDefinitions"
decl_stmt|;
name|String
name|REP_DECLARING_NODE_TYPE
init|=
literal|"rep:declaringNodeType"
decl_stmt|;
name|String
name|REP_PRIMARY_TYPE
init|=
literal|"rep:primaryType"
decl_stmt|;
name|String
name|REP_MIXIN_TYPES
init|=
literal|"rep:mixinTypes"
decl_stmt|;
name|String
name|REP_UUID
init|=
literal|"rep:uuid"
decl_stmt|;
comment|/**      * mixin to enable the AtomicCounterEditor.      */
name|String
name|MIX_ATOMIC_COUNTER
init|=
literal|"mix:atomicCounter"
decl_stmt|;
block|}
end_interface

end_unit

