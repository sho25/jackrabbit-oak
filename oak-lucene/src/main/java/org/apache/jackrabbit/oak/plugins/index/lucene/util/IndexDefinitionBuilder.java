begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
operator|.
name|lucene
operator|.
name|util
package|;
end_package

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
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
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
name|index
operator|.
name|PathFilter
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
name|index
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
name|plugins
operator|.
name|tree
operator|.
name|TreeFactory
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
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
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
name|NAME
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
name|STRINGS
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
name|plugins
operator|.
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
import|;
end_import

begin_class
specifier|public
specifier|final
class|class
name|IndexDefinitionBuilder
block|{
specifier|private
specifier|final
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Tree
name|tree
init|=
name|TreeFactory
operator|.
name|createTree
argument_list|(
name|builder
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|IndexRule
argument_list|>
name|rules
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|AggregateRule
argument_list|>
name|aggRules
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Tree
name|indexRule
decl_stmt|;
specifier|private
name|Tree
name|aggregatesTree
decl_stmt|;
specifier|public
name|IndexDefinitionBuilder
parameter_list|()
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|COMPAT_MODE
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"async"
argument_list|,
literal|"async"
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
literal|"reindex"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
argument_list|,
literal|"lucene"
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
literal|"oak:QueryIndexDefinition"
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|indexRule
operator|=
name|createChild
argument_list|(
name|tree
argument_list|,
name|LuceneIndexConstants
operator|.
name|INDEX_RULES
argument_list|)
expr_stmt|;
block|}
specifier|public
name|IndexDefinitionBuilder
name|evaluatePathRestrictions
parameter_list|()
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|EVALUATE_PATH_RESTRICTION
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|IndexDefinitionBuilder
name|includedPaths
parameter_list|(
name|String
modifier|...
name|paths
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|PathFilter
operator|.
name|PROP_INCLUDED_PATHS
argument_list|,
name|asList
argument_list|(
name|paths
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|IndexDefinitionBuilder
name|excludedPaths
parameter_list|(
name|String
modifier|...
name|paths
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|PathFilter
operator|.
name|PROP_EXCLUDED_PATHS
argument_list|,
name|asList
argument_list|(
name|paths
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|NodeState
name|build
parameter_list|()
block|{
return|return
name|builder
operator|.
name|getNodeState
argument_list|()
return|;
block|}
specifier|public
name|Tree
name|build
parameter_list|(
name|Tree
name|tree
parameter_list|)
block|{
name|NodeStateCopyUtils
operator|.
name|copyToTree
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|tree
argument_list|)
expr_stmt|;
return|return
name|tree
return|;
block|}
specifier|public
name|Node
name|build
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|NodeStateCopyUtils
operator|.
name|copyToNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|node
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
comment|//~--------------------------------------< IndexRule>
specifier|public
name|IndexRule
name|indexRule
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|IndexRule
name|rule
init|=
name|rules
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|rule
operator|==
literal|null
condition|)
block|{
name|rule
operator|=
operator|new
name|IndexRule
argument_list|(
name|createChild
argument_list|(
name|indexRule
argument_list|,
name|type
argument_list|)
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|rules
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|rule
argument_list|)
expr_stmt|;
block|}
return|return
name|rule
return|;
block|}
specifier|public
specifier|static
class|class
name|IndexRule
block|{
specifier|private
specifier|final
name|Tree
name|indexRule
decl_stmt|;
specifier|private
specifier|final
name|Tree
name|propsTree
decl_stmt|;
specifier|private
specifier|final
name|String
name|ruleName
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|PropertyRule
argument_list|>
name|props
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|propNodeNames
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
specifier|private
name|IndexRule
parameter_list|(
name|Tree
name|indexRule
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|indexRule
operator|=
name|indexRule
expr_stmt|;
name|this
operator|.
name|propsTree
operator|=
name|createChild
argument_list|(
name|indexRule
argument_list|,
name|LuceneIndexConstants
operator|.
name|PROP_NODE
argument_list|)
expr_stmt|;
name|this
operator|.
name|ruleName
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|IndexRule
name|indexNodeName
parameter_list|()
block|{
name|indexRule
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|INDEX_NODE_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|PropertyRule
name|property
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|property
argument_list|(
name|name
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|public
name|PropertyRule
name|property
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|regex
parameter_list|)
block|{
name|PropertyRule
name|propRule
init|=
name|props
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|propRule
operator|==
literal|null
condition|)
block|{
name|propRule
operator|=
operator|new
name|PropertyRule
argument_list|(
name|this
argument_list|,
name|createChild
argument_list|(
name|propsTree
argument_list|,
name|createPropNodeName
argument_list|(
name|name
argument_list|,
name|regex
argument_list|)
argument_list|)
argument_list|,
name|name
argument_list|,
name|regex
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|propRule
argument_list|)
expr_stmt|;
block|}
return|return
name|propRule
return|;
block|}
specifier|private
name|String
name|createPropNodeName
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|regex
parameter_list|)
block|{
name|name
operator|=
name|regex
condition|?
literal|"prop"
else|:
name|getSafePropName
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|name
operator|=
literal|"prop"
expr_stmt|;
block|}
if|if
condition|(
name|propNodeNames
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|name
operator|=
name|name
operator|+
literal|"_"
operator|+
name|propNodeNames
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|propNodeNames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|name
return|;
block|}
specifier|public
name|String
name|getRuleName
parameter_list|()
block|{
return|return
name|ruleName
return|;
block|}
block|}
specifier|public
specifier|static
class|class
name|PropertyRule
block|{
specifier|private
specifier|final
name|IndexRule
name|indexRule
decl_stmt|;
specifier|private
specifier|final
name|Tree
name|propTree
decl_stmt|;
specifier|private
name|PropertyRule
parameter_list|(
name|IndexRule
name|indexRule
parameter_list|,
name|Tree
name|propTree
parameter_list|,
name|String
name|name
parameter_list|,
name|boolean
name|regex
parameter_list|)
block|{
name|this
operator|.
name|indexRule
operator|=
name|indexRule
expr_stmt|;
name|this
operator|.
name|propTree
operator|=
name|propTree
expr_stmt|;
name|propTree
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_NAME
argument_list|,
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|regex
condition|)
block|{
name|propTree
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_IS_REGEX
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|PropertyRule
name|useInExcerpt
parameter_list|()
block|{
name|propTree
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_USE_IN_EXCERPT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|PropertyRule
name|analyzed
parameter_list|()
block|{
name|propTree
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_ANALYZED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|PropertyRule
name|nodeScopeIndex
parameter_list|()
block|{
name|propTree
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_NODE_SCOPE_INDEX
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|PropertyRule
name|ordered
parameter_list|()
block|{
name|propTree
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_ORDERED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|PropertyRule
name|ordered
parameter_list|(
name|String
name|type
parameter_list|)
block|{
comment|//This would throw an IAE if type is invalid
name|PropertyType
operator|.
name|valueFromName
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|propTree
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_ORDERED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|propTree
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_TYPE
argument_list|,
name|type
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|PropertyRule
name|propertyIndex
parameter_list|()
block|{
name|propTree
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_PROPERTY_INDEX
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|PropertyRule
name|nullCheckEnabled
parameter_list|()
block|{
name|propTree
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_NULL_CHECK_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|PropertyRule
name|notNullCheckEnabled
parameter_list|()
block|{
name|propTree
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_NOT_NULL_CHECK_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|IndexRule
name|enclosingRule
parameter_list|()
block|{
return|return
name|indexRule
return|;
block|}
block|}
comment|//~--------------------------------------< Aggregates>
specifier|public
name|AggregateRule
name|aggregateRule
parameter_list|(
name|String
name|type
parameter_list|)
block|{
if|if
condition|(
name|aggregatesTree
operator|==
literal|null
condition|)
block|{
name|aggregatesTree
operator|=
name|createChild
argument_list|(
name|tree
argument_list|,
name|LuceneIndexConstants
operator|.
name|AGGREGATES
argument_list|)
expr_stmt|;
block|}
name|AggregateRule
name|rule
init|=
name|aggRules
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|rule
operator|==
literal|null
condition|)
block|{
name|rule
operator|=
operator|new
name|AggregateRule
argument_list|(
name|createChild
argument_list|(
name|aggregatesTree
argument_list|,
name|type
argument_list|)
argument_list|)
expr_stmt|;
name|aggRules
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|rule
argument_list|)
expr_stmt|;
block|}
return|return
name|rule
return|;
block|}
specifier|public
name|AggregateRule
name|aggregateRule
parameter_list|(
name|String
name|primaryType
parameter_list|,
name|String
modifier|...
name|includes
parameter_list|)
block|{
name|AggregateRule
name|rule
init|=
name|aggregateRule
argument_list|(
name|primaryType
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|include
range|:
name|includes
control|)
block|{
name|rule
operator|.
name|include
argument_list|(
name|include
argument_list|)
expr_stmt|;
block|}
return|return
name|rule
return|;
block|}
specifier|public
specifier|static
class|class
name|AggregateRule
block|{
specifier|private
specifier|final
name|Tree
name|aggregate
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Include
argument_list|>
name|includes
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
specifier|private
name|AggregateRule
parameter_list|(
name|Tree
name|aggregate
parameter_list|)
block|{
name|this
operator|.
name|aggregate
operator|=
name|aggregate
expr_stmt|;
block|}
specifier|public
name|Include
name|include
parameter_list|(
name|String
name|includePath
parameter_list|)
block|{
name|Include
name|include
init|=
name|includes
operator|.
name|get
argument_list|(
name|includePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|include
operator|==
literal|null
condition|)
block|{
name|include
operator|=
operator|new
name|Include
argument_list|(
name|createChild
argument_list|(
name|aggregate
argument_list|,
literal|"include"
operator|+
name|includes
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|includes
operator|.
name|put
argument_list|(
name|includePath
argument_list|,
name|include
argument_list|)
expr_stmt|;
block|}
name|include
operator|.
name|path
argument_list|(
name|includePath
argument_list|)
expr_stmt|;
return|return
name|include
return|;
block|}
specifier|public
specifier|static
class|class
name|Include
block|{
specifier|private
specifier|final
name|Tree
name|include
decl_stmt|;
specifier|private
name|Include
parameter_list|(
name|Tree
name|include
parameter_list|)
block|{
name|this
operator|.
name|include
operator|=
name|include
expr_stmt|;
block|}
specifier|public
name|Include
name|path
parameter_list|(
name|String
name|includePath
parameter_list|)
block|{
name|include
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|AGG_PATH
argument_list|,
name|includePath
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Include
name|relativeNode
parameter_list|()
block|{
name|include
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|AGG_RELATIVE_NODE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
block|}
specifier|private
specifier|static
name|Tree
name|createChild
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|Tree
name|child
init|=
name|tree
operator|.
name|addChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|child
operator|.
name|setOrderableChildren
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|child
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_UNSTRUCTURED
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
return|return
name|child
return|;
block|}
specifier|static
name|String
name|getSafePropName
parameter_list|(
name|String
name|relativePropName
parameter_list|)
block|{
name|String
name|propName
init|=
name|PathUtils
operator|.
name|getName
argument_list|(
name|relativePropName
argument_list|)
decl_stmt|;
name|int
name|indexOfColon
init|=
name|propName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexOfColon
operator|>
literal|0
condition|)
block|{
name|propName
operator|=
name|propName
operator|.
name|substring
argument_list|(
name|indexOfColon
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|//Just keep ascii chars
name|propName
operator|=
name|propName
operator|.
name|replaceAll
argument_list|(
literal|"\\W"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
return|return
name|propName
return|;
block|}
block|}
end_class

end_unit

