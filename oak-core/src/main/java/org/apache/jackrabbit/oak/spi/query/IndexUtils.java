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
name|query
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
name|spi
operator|.
name|query
operator|.
name|IndexDefinition
operator|.
name|INDEX_DATA_CHILD_NAME
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
name|spi
operator|.
name|query
operator|.
name|IndexDefinition
operator|.
name|TYPE_PROPERTY_NAME
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
name|spi
operator|.
name|query
operator|.
name|IndexDefinition
operator|.
name|UNIQUE_PROPERTY_NAME
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|commons
operator|.
name|PathUtils
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
name|lucene
operator|.
name|LuceneIndexConstants
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

begin_class
specifier|public
class|class
name|IndexUtils
block|{
comment|/**      * switch to "oak:index" as soon as it is possible      */
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_INDEX_HOME
init|=
literal|"/oak-index"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TYPE_UNKNOWN
init|=
literal|"unknown"
decl_stmt|;
comment|/**      * Builds an {@link IndexDefinition} out of a {@link ChildNodeEntry}      *       */
specifier|public
specifier|static
name|IndexDefinition
name|getDefinition
parameter_list|(
name|String
name|path
parameter_list|,
name|ChildNodeEntry
name|def
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
argument_list|()
operator|.
name|getString
argument_list|()
expr_stmt|;
block|}
name|boolean
name|unique
init|=
literal|false
decl_stmt|;
name|PropertyState
name|uniqueProp
init|=
name|ns
operator|.
name|getProperty
argument_list|(
name|UNIQUE_PROPERTY_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|uniqueProp
operator|!=
literal|null
operator|&&
operator|!
name|uniqueProp
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|unique
operator|=
name|uniqueProp
operator|.
name|getValue
argument_list|()
operator|.
name|getBoolean
argument_list|()
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|PropertyState
name|ps
range|:
name|ns
operator|.
name|getProperties
argument_list|()
control|)
block|{
if|if
condition|(
name|ps
operator|!=
literal|null
operator|&&
operator|!
name|ps
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|String
name|v
init|=
name|ps
operator|.
name|getValue
argument_list|()
operator|.
name|getString
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
name|ps
operator|.
name|getName
argument_list|()
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
block|}
comment|// TODO hack to circumvent observation events
if|if
condition|(
name|ns
operator|.
name|hasChildNode
argument_list|(
name|INDEX_DATA_CHILD_NAME
argument_list|)
condition|)
block|{
name|PropertyState
name|ps
init|=
name|ns
operator|.
name|getChildNode
argument_list|(
name|INDEX_DATA_CHILD_NAME
argument_list|)
operator|.
name|getProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|INDEX_UPDATE
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|!=
literal|null
operator|&&
name|ps
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|props
operator|.
name|put
argument_list|(
name|LuceneIndexConstants
operator|.
name|INDEX_UPDATE
argument_list|,
name|ps
operator|.
name|getValue
argument_list|()
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|IndexDefinitionImpl
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
argument_list|,
name|unique
argument_list|,
name|props
argument_list|)
return|;
block|}
comment|/**      * @return the 'destination' node if the path exists, null if otherwise      */
specifier|public
specifier|static
name|NodeState
name|getNode
parameter_list|(
name|NodeState
name|nodeState
parameter_list|,
name|String
name|destination
parameter_list|)
block|{
name|NodeState
name|retval
init|=
name|nodeState
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|pathIterator
init|=
name|PathUtils
operator|.
name|elements
argument_list|(
name|destination
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|pathIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|path
init|=
name|pathIterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|retval
operator|.
name|hasChildNode
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|retval
operator|=
name|retval
operator|.
name|getChildNode
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
name|retval
return|;
block|}
comment|/**      * Builds a list of the existing index definitions from the repository      *       */
specifier|public
specifier|static
name|List
argument_list|<
name|IndexDefinition
argument_list|>
name|buildIndexDefinitions
parameter_list|(
name|NodeState
name|nodeState
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
name|getNode
argument_list|(
name|nodeState
argument_list|,
name|indexConfigPath
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
argument_list|)
decl_stmt|;
if|if
condition|(
name|def
operator|==
literal|null
operator|||
operator|(
name|typeFilter
operator|!=
literal|null
operator|&&
operator|!
name|typeFilter
operator|.
name|equals
argument_list|(
name|def
operator|.
name|getType
argument_list|()
argument_list|)
operator|)
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
specifier|public
specifier|static
name|NodeBuilder
name|getChildBuilder
parameter_list|(
name|NodeState
name|state
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|NodeBuilder
name|builder
init|=
name|state
operator|.
name|getBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|path
argument_list|)
control|)
block|{
name|builder
operator|=
name|builder
operator|.
name|getChildBuilder
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

