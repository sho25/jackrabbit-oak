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
name|index
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|annotation
operator|.
name|Nullable
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
name|Type
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyStates
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
name|state
operator|.
name|ChildNodeEntry
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
name|state
operator|.
name|NodeBuilder
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
name|state
operator|.
name|NodeState
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
name|util
operator|.
name|NodeUtil
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
import|;
end_import

begin_import
import|import static
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
name|Type
operator|.
name|STRING
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|PathUtils
operator|.
name|concat
import|;
end_import

begin_comment
comment|/**  * TODO document  */
end_comment

begin_class
specifier|public
class|class
name|IndexUtils
implements|implements
name|IndexConstants
block|{
specifier|public
specifier|static
name|NodeBuilder
name|getOrCreateOakIndex
parameter_list|(
name|NodeBuilder
name|root
parameter_list|)
block|{
name|NodeBuilder
name|index
decl_stmt|;
if|if
condition|(
operator|!
name|root
operator|.
name|hasChildNode
argument_list|(
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|)
condition|)
block|{
name|index
operator|=
name|root
operator|.
name|child
argument_list|(
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|)
expr_stmt|;
comment|// TODO: use property node type name
name|index
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_UNSTRUCTURED
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|index
operator|=
name|root
operator|.
name|child
argument_list|(
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|)
expr_stmt|;
block|}
return|return
name|index
return|;
block|}
comment|/**      * Create a new property2 index definition below the given {@code indexNode}.      *      * @param index         The oak:index node builder      * @param indexDefName  The name of the new property index.      * @param reindex       {@code true} if the the reindex flag should be turned on.      * @param unique        {@code true} if the index is expected the assert property      *                      uniqueness.      * @param propertyNames The property names that should be indexed.      */
specifier|public
specifier|static
name|void
name|createIndexDefinition
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|index
parameter_list|,
annotation|@
name|Nonnull
name|String
name|indexDefName
parameter_list|,
name|boolean
name|reindex
parameter_list|,
name|boolean
name|unique
parameter_list|,
annotation|@
name|Nonnull
name|List
argument_list|<
name|String
argument_list|>
name|propertyNames
parameter_list|,
annotation|@
name|Nullable
name|List
argument_list|<
name|String
argument_list|>
name|declaringNodeTypeNames
parameter_list|)
block|{
name|NodeBuilder
name|entry
init|=
name|index
operator|.
name|child
argument_list|(
name|indexDefName
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
argument_list|,
literal|"p2"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|REINDEX_PROPERTY_NAME
argument_list|,
name|reindex
argument_list|)
decl_stmt|;
if|if
condition|(
name|unique
condition|)
block|{
name|entry
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|UNIQUE_PROPERTY_NAME
argument_list|,
name|unique
argument_list|)
expr_stmt|;
block|}
name|entry
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|IndexConstants
operator|.
name|PROPERTY_NAMES
argument_list|,
name|propertyNames
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|declaringNodeTypeNames
operator|!=
literal|null
operator|&&
operator|!
name|declaringNodeTypeNames
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|entry
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|IndexConstants
operator|.
name|DECLARING_NODE_TYPES
argument_list|,
name|declaringNodeTypeNames
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Create a new property2 index definition below the given {@code indexNode}.      *      * @param indexNode      * @param indexDefName      * @param unique      * @param propertyNames      * @param declaringNodeTypeNames      */
specifier|public
specifier|static
name|void
name|createIndexDefinition
parameter_list|(
annotation|@
name|Nonnull
name|NodeUtil
name|indexNode
parameter_list|,
annotation|@
name|Nonnull
name|String
name|indexDefName
parameter_list|,
name|boolean
name|unique
parameter_list|,
annotation|@
name|Nonnull
name|String
index|[]
name|propertyNames
parameter_list|,
annotation|@
name|Nullable
name|String
index|[]
name|declaringNodeTypeNames
parameter_list|)
block|{
name|NodeUtil
name|entry
init|=
name|indexNode
operator|.
name|getOrAddChild
argument_list|(
name|indexDefName
argument_list|,
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|)
decl_stmt|;
name|entry
operator|.
name|setString
argument_list|(
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
argument_list|,
literal|"p2"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|setBoolean
argument_list|(
name|IndexConstants
operator|.
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|unique
condition|)
block|{
name|entry
operator|.
name|setBoolean
argument_list|(
name|IndexConstants
operator|.
name|UNIQUE_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|declaringNodeTypeNames
operator|!=
literal|null
operator|&&
name|declaringNodeTypeNames
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|entry
operator|.
name|setStrings
argument_list|(
name|IndexConstants
operator|.
name|DECLARING_NODE_TYPES
argument_list|,
name|declaringNodeTypeNames
argument_list|)
expr_stmt|;
block|}
name|entry
operator|.
name|setStrings
argument_list|(
name|IndexConstants
operator|.
name|PROPERTY_NAMES
argument_list|,
name|propertyNames
argument_list|)
expr_stmt|;
block|}
comment|/**      * Builds a list of the existing index definitions.      *<p/>      * Checks only children of the provided state for an index definitions      * container node, aka a node named {@link #INDEX_DEFINITIONS_NAME}      *      * @return      */
specifier|public
specifier|static
name|List
argument_list|<
name|IndexDefinition
argument_list|>
name|buildIndexDefinitions
parameter_list|(
name|NodeState
name|state
parameter_list|,
name|String
name|indexConfigPath
parameter_list|,
name|String
name|typeFilter
parameter_list|)
block|{
name|NodeState
name|definitions
init|=
name|state
operator|.
name|getChildNode
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|definitions
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|indexConfigPath
operator|=
name|concat
argument_list|(
name|indexConfigPath
argument_list|,
name|INDEX_DEFINITIONS_NAME
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|IndexDefinition
argument_list|>
name|defs
init|=
operator|new
name|ArrayList
argument_list|<
name|IndexDefinition
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|c
range|:
name|definitions
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|IndexDefinition
name|def
init|=
name|getDefinition
argument_list|(
name|indexConfigPath
argument_list|,
name|c
argument_list|,
name|typeFilter
argument_list|)
decl_stmt|;
if|if
condition|(
name|def
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|defs
operator|.
name|add
argument_list|(
name|def
argument_list|)
expr_stmt|;
block|}
return|return
name|defs
return|;
block|}
comment|/**      * Builds an {@link IndexDefinition} out of a {@link ChildNodeEntry}      */
specifier|private
specifier|static
name|IndexDefinition
name|getDefinition
parameter_list|(
name|String
name|path
parameter_list|,
name|ChildNodeEntry
name|def
parameter_list|,
name|String
name|typeFilter
parameter_list|)
block|{
name|String
name|name
init|=
name|def
operator|.
name|getName
argument_list|()
decl_stmt|;
name|NodeState
name|ns
init|=
name|def
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|PropertyState
name|typeProp
init|=
name|ns
operator|.
name|getProperty
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|)
decl_stmt|;
name|String
name|type
init|=
name|TYPE_UNKNOWN
decl_stmt|;
if|if
condition|(
name|typeProp
operator|!=
literal|null
operator|&&
operator|!
name|typeProp
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|type
operator|=
name|typeProp
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|typeFilter
operator|!=
literal|null
operator|&&
operator|!
name|typeFilter
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|IndexDefinitionImpl
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

