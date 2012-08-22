begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|lucene
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
name|HashMap
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
name|CoreValueFactory
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
name|spi
operator|.
name|query
operator|.
name|IndexDefinition
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
name|query
operator|.
name|IndexDefinitionImpl
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
name|LuceneIndexUtils
block|{
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_INDEX_NAME
init|=
literal|"default-lucene"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_DATA_CHILD_NAME
init|=
literal|":data"
decl_stmt|;
comment|// public static final String[] DEFAULT_INDEX_PATH = { "oak-index",
comment|// "default",
comment|// ":data" };
specifier|private
name|LuceneIndexUtils
parameter_list|()
block|{      }
comment|/**      *       * You still need to call #commit afterwards to persist the changes      *       * @param index      * @param indexName      * @return      */
specifier|public
specifier|static
name|Tree
name|createIndexNode
parameter_list|(
name|Tree
name|index
parameter_list|,
name|String
name|indexName
parameter_list|,
name|CoreValueFactory
name|vf
parameter_list|)
block|{
if|if
condition|(
name|index
operator|.
name|hasChild
argument_list|(
name|indexName
argument_list|)
condition|)
block|{
name|index
operator|=
name|index
operator|.
name|getChild
argument_list|(
name|indexName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|index
operator|=
name|index
operator|.
name|addChild
argument_list|(
name|indexName
argument_list|)
expr_stmt|;
block|}
name|index
operator|.
name|setProperty
argument_list|(
name|IndexDefinition
operator|.
name|TYPE_PROPERTY_NAME
argument_list|,
name|vf
operator|.
name|createValue
argument_list|(
name|LuceneIndexFactory
operator|.
name|TYPE
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|index
operator|.
name|hasChild
argument_list|(
name|INDEX_DATA_CHILD_NAME
argument_list|)
condition|)
block|{
name|index
operator|.
name|addChild
argument_list|(
name|INDEX_DATA_CHILD_NAME
argument_list|)
expr_stmt|;
block|}
return|return
name|index
return|;
block|}
comment|/**      *       * Checks if any of the index's children qualifies as an index node, and      * returns the list of good candidates.      *       * For now each child that has a "type=lucene" property and a ":data" node      * is considered to be a potential index      *       * @param indexHome      *            the location of potential index nodes      * @return the list of existing indexes      */
specifier|public
specifier|static
name|List
argument_list|<
name|IndexDefinition
argument_list|>
name|getIndexInfos
parameter_list|(
name|NodeState
name|indexHome
parameter_list|,
name|String
name|parentPath
parameter_list|)
block|{
if|if
condition|(
name|indexHome
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
expr|<
name|IndexDefinition
operator|>
name|emptyList
argument_list|()
return|;
block|}
name|List
argument_list|<
name|IndexDefinition
argument_list|>
name|tempIndexes
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
name|indexHome
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|NodeState
name|child
init|=
name|c
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|PropertyState
name|type
init|=
name|child
operator|.
name|getProperty
argument_list|(
name|IndexDefinition
operator|.
name|TYPE_PROPERTY_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
operator|||
name|type
operator|.
name|isArray
argument_list|()
operator|||
operator|!
name|LuceneIndexFactory
operator|.
name|TYPE
operator|.
name|equals
argument_list|(
name|type
operator|.
name|getValue
argument_list|()
operator|.
name|getString
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|child
operator|.
name|hasChildNode
argument_list|(
name|INDEX_DATA_CHILD_NAME
argument_list|)
condition|)
block|{
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
name|child
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
name|tempIndexes
operator|.
name|add
argument_list|(
operator|new
name|IndexDefinitionImpl
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|,
name|type
operator|.
name|getValue
argument_list|()
operator|.
name|getString
argument_list|()
argument_list|,
name|PathUtils
operator|.
name|concat
argument_list|(
name|parentPath
argument_list|,
name|c
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|tempIndexes
return|;
block|}
block|}
end_class

end_unit

