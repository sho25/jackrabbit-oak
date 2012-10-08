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
name|NodeState
import|;
end_import

begin_class
specifier|public
class|class
name|IndexUtils
implements|implements
name|IndexConstants
block|{
comment|/**      * Builds a list of the existing index definitions.      *       * Checks only children of the provided state for an index definitions      * container node, aka a node named {@link INDEX_DEFINITIONS_NAME}      *       * @return      */
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
name|definitions
operator|==
literal|null
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
comment|/**      * Builds an {@link IndexDefinition} out of a {@link ChildNodeEntry}      *       */
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
argument_list|,
name|ns
argument_list|)
return|;
block|}
block|}
end_class

end_unit

