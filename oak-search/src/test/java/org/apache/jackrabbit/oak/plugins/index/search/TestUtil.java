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
name|search
package|;
end_package

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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|Repository
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|JcrConstants
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
name|JackrabbitRepository
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
name|Blob
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
name|CommitFailedException
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
name|search
operator|.
name|FulltextIndexConstants
operator|.
name|IndexingMode
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
name|search
operator|.
name|util
operator|.
name|IndexHelper
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
name|MemoryNodeStore
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
name|ModifiedNodeState
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
name|name
operator|.
name|NamespaceEditorProvider
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
name|nodetype
operator|.
name|TypeEditorProvider
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
name|nodetype
operator|.
name|write
operator|.
name|NodeTypeRegistry
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
name|factories
operator|.
name|RootFactory
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
name|commit
operator|.
name|CommitInfo
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
name|commit
operator|.
name|CompositeEditorProvider
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
name|commit
operator|.
name|Editor
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
name|commit
operator|.
name|EditorHook
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
name|commit
operator|.
name|EditorProvider
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
name|ApplyDiff
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
name|spi
operator|.
name|state
operator|.
name|NodeStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
operator|.
name|of
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
name|JCR_CONTENT
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
name|index
operator|.
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
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
name|index
operator|.
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NODE_TYPE
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
name|index
operator|.
name|IndexConstants
operator|.
name|REINDEX_PROPERTY_NAME
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
name|index
operator|.
name|IndexConstants
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyStates
operator|.
name|createProperty
import|;
end_import

begin_class
specifier|public
class|class
name|TestUtil
block|{
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|COUNTER
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NT_TEST
init|=
literal|"oak:TestNode"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST_NODE_TYPE
init|=
literal|"[oak:TestNode]\n"
operator|+
literal|" - * (UNDEFINED) multiple\n"
operator|+
literal|" - * (UNDEFINED)\n"
operator|+
literal|" + * (nt:base) = oak:TestNode VERSION"
decl_stmt|;
specifier|static
name|void
name|useV2
parameter_list|(
name|NodeBuilder
name|idxNb
parameter_list|)
block|{
if|if
condition|(
operator|!
name|IndexFormatVersion
operator|.
name|getDefault
argument_list|()
operator|.
name|isAtLeast
argument_list|(
name|IndexFormatVersion
operator|.
name|V2
argument_list|)
condition|)
block|{
name|idxNb
operator|.
name|setProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|COMPAT_MODE
argument_list|,
name|IndexFormatVersion
operator|.
name|V2
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
name|void
name|useV2
parameter_list|(
name|Tree
name|idxTree
parameter_list|)
block|{
if|if
condition|(
operator|!
name|IndexFormatVersion
operator|.
name|getDefault
argument_list|()
operator|.
name|isAtLeast
argument_list|(
name|IndexFormatVersion
operator|.
name|V2
argument_list|)
condition|)
block|{
name|idxTree
operator|.
name|setProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|COMPAT_MODE
argument_list|,
name|IndexFormatVersion
operator|.
name|V2
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|NodeBuilder
name|newFTIndexDefinitionV2
parameter_list|(
annotation|@
name|NotNull
name|NodeBuilder
name|index
parameter_list|,
annotation|@
name|NotNull
name|String
name|name
parameter_list|,
name|String
name|type
parameter_list|,
annotation|@
name|Nullable
name|Set
argument_list|<
name|String
argument_list|>
name|propertyTypes
parameter_list|)
block|{
name|NodeBuilder
name|nb
init|=
name|IndexHelper
operator|.
name|newFTIndexDefinition
argument_list|(
name|index
argument_list|,
name|name
argument_list|,
name|type
argument_list|,
name|propertyTypes
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|useV2
argument_list|(
name|nb
argument_list|)
expr_stmt|;
return|return
name|nb
return|;
block|}
specifier|public
specifier|static
name|Tree
name|enableForFullText
parameter_list|(
name|Tree
name|props
parameter_list|,
name|String
name|propName
parameter_list|)
block|{
return|return
name|enableForFullText
argument_list|(
name|props
argument_list|,
name|propName
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Tree
name|enableForFullText
parameter_list|(
name|Tree
name|props
parameter_list|,
name|String
name|propName
parameter_list|,
name|boolean
name|regex
parameter_list|)
block|{
name|Tree
name|prop
init|=
name|props
operator|.
name|addChild
argument_list|(
name|unique
argument_list|(
literal|"prop"
argument_list|)
argument_list|)
decl_stmt|;
name|prop
operator|.
name|setProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|PROP_NAME
argument_list|,
name|propName
argument_list|)
expr_stmt|;
name|prop
operator|.
name|setProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|PROP_PROPERTY_INDEX
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|prop
operator|.
name|setProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|PROP_IS_REGEX
argument_list|,
name|regex
argument_list|)
expr_stmt|;
name|prop
operator|.
name|setProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|PROP_NODE_SCOPE_INDEX
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|prop
operator|.
name|setProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|PROP_ANALYZED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|prop
operator|.
name|setProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|PROP_USE_IN_EXCERPT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|prop
operator|.
name|setProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|PROP_USE_IN_SPELLCHECK
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|prop
return|;
block|}
specifier|public
specifier|static
name|Tree
name|enableForOrdered
parameter_list|(
name|Tree
name|props
parameter_list|,
name|String
name|propName
parameter_list|)
block|{
name|Tree
name|prop
init|=
name|enablePropertyIndex
argument_list|(
name|props
argument_list|,
name|propName
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|prop
operator|.
name|setProperty
argument_list|(
literal|"ordered"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|prop
return|;
block|}
specifier|public
specifier|static
name|Tree
name|enablePropertyIndex
parameter_list|(
name|Tree
name|props
parameter_list|,
name|String
name|propName
parameter_list|,
name|boolean
name|regex
parameter_list|)
block|{
name|Tree
name|prop
init|=
name|props
operator|.
name|addChild
argument_list|(
name|unique
argument_list|(
literal|"prop"
argument_list|)
argument_list|)
decl_stmt|;
name|prop
operator|.
name|setProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|PROP_NAME
argument_list|,
name|propName
argument_list|)
expr_stmt|;
name|prop
operator|.
name|setProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|PROP_PROPERTY_INDEX
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|prop
operator|.
name|setProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|PROP_IS_REGEX
argument_list|,
name|regex
argument_list|)
expr_stmt|;
name|prop
operator|.
name|setProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|PROP_NODE_SCOPE_INDEX
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|prop
operator|.
name|setProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|PROP_ANALYZED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|prop
return|;
block|}
specifier|public
specifier|static
name|Tree
name|enableFunctionIndex
parameter_list|(
name|Tree
name|props
parameter_list|,
name|String
name|function
parameter_list|)
block|{
name|Tree
name|prop
init|=
name|props
operator|.
name|addChild
argument_list|(
name|unique
argument_list|(
literal|"prop"
argument_list|)
argument_list|)
decl_stmt|;
name|prop
operator|.
name|setProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|PROP_FUNCTION
argument_list|,
name|function
argument_list|)
expr_stmt|;
return|return
name|prop
return|;
block|}
specifier|public
specifier|static
name|AggregatorBuilder
name|newNodeAggregator
parameter_list|(
name|Tree
name|indexDefn
parameter_list|)
block|{
return|return
operator|new
name|AggregatorBuilder
argument_list|(
name|indexDefn
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Tree
name|newRulePropTree
parameter_list|(
name|Tree
name|indexDefn
parameter_list|,
name|String
name|typeName
parameter_list|)
block|{
name|Tree
name|rules
init|=
name|indexDefn
operator|.
name|addChild
argument_list|(
name|FulltextIndexConstants
operator|.
name|INDEX_RULES
argument_list|)
decl_stmt|;
name|rules
operator|.
name|setOrderableChildren
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Tree
name|rule
init|=
name|rules
operator|.
name|addChild
argument_list|(
name|typeName
argument_list|)
decl_stmt|;
name|Tree
name|props
init|=
name|rule
operator|.
name|addChild
argument_list|(
name|FulltextIndexConstants
operator|.
name|PROP_NODE
argument_list|)
decl_stmt|;
name|props
operator|.
name|setOrderableChildren
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|props
return|;
block|}
specifier|public
specifier|static
name|NodeBuilder
name|child
parameter_list|(
name|NodeBuilder
name|nb
parameter_list|,
name|String
name|path
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|checkNotNull
argument_list|(
name|path
argument_list|)
argument_list|)
control|)
block|{
name|nb
operator|=
name|nb
operator|.
name|child
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|nb
return|;
block|}
specifier|static
class|class
name|AggregatorBuilder
block|{
specifier|private
specifier|final
name|Tree
name|aggs
decl_stmt|;
specifier|private
name|AggregatorBuilder
parameter_list|(
name|Tree
name|indexDefn
parameter_list|)
block|{
name|this
operator|.
name|aggs
operator|=
name|indexDefn
operator|.
name|addChild
argument_list|(
name|FulltextIndexConstants
operator|.
name|AGGREGATES
argument_list|)
expr_stmt|;
block|}
name|AggregatorBuilder
name|newRuleWithName
parameter_list|(
name|String
name|primaryType
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|includes
parameter_list|)
block|{
name|Tree
name|agg
init|=
name|aggs
operator|.
name|addChild
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
name|agg
operator|.
name|addChild
argument_list|(
name|unique
argument_list|(
literal|"include"
argument_list|)
argument_list|)
operator|.
name|setProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|AGG_PATH
argument_list|,
name|include
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
block|}
specifier|static
name|String
name|unique
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|+
name|COUNTER
operator|.
name|getAndIncrement
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|NodeBuilder
name|registerTestNodeType
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|registerNodeType
argument_list|(
name|builder
argument_list|,
name|TEST_NODE_TYPE
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
specifier|public
specifier|static
name|void
name|registerNodeType
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|nodeTypeDefn
parameter_list|)
block|{
comment|//Taken from org.apache.jackrabbit.oak.plugins.nodetype.write.InitialContent
name|NodeState
name|base
init|=
name|ModifiedNodeState
operator|.
name|squeeze
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|NodeStore
name|store
init|=
operator|new
name|MemoryNodeStore
argument_list|(
name|base
argument_list|)
decl_stmt|;
name|Root
name|root
init|=
name|RootFactory
operator|.
name|createSystemRoot
argument_list|(
name|store
argument_list|,
operator|new
name|EditorHook
argument_list|(
operator|new
name|CompositeEditorProvider
argument_list|(
operator|new
name|NamespaceEditorProvider
argument_list|()
argument_list|,
operator|new
name|TypeEditorProvider
argument_list|()
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|NodeTypeRegistry
operator|.
name|register
argument_list|(
name|root
argument_list|,
name|IOUtils
operator|.
name|toInputStream
argument_list|(
name|nodeTypeDefn
argument_list|)
argument_list|,
literal|"test node types"
argument_list|)
expr_stmt|;
name|NodeState
name|target
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|target
operator|.
name|compareAgainstBaseState
argument_list|(
name|base
argument_list|,
operator|new
name|ApplyDiff
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|Tree
name|createNodeWithType
parameter_list|(
name|Tree
name|t
parameter_list|,
name|String
name|nodeName
parameter_list|,
name|String
name|typeName
parameter_list|)
block|{
name|t
operator|=
name|t
operator|.
name|addChild
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|typeName
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
specifier|public
specifier|static
name|NodeBuilder
name|createNodeWithType
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|nodeName
parameter_list|,
name|String
name|typeName
parameter_list|)
block|{
name|builder
operator|=
name|builder
operator|.
name|child
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|typeName
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
specifier|public
specifier|static
name|Tree
name|createFileNode
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|String
name|name
parameter_list|,
name|Blob
name|content
parameter_list|,
name|String
name|mimeType
parameter_list|)
block|{
name|Tree
name|fileNode
init|=
name|tree
operator|.
name|addChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|fileNode
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|JcrConstants
operator|.
name|NT_FILE
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|Tree
name|jcrContent
init|=
name|fileNode
operator|.
name|addChild
argument_list|(
name|JCR_CONTENT
argument_list|)
decl_stmt|;
name|jcrContent
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_DATA
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|jcrContent
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIMETYPE
argument_list|,
name|mimeType
argument_list|)
expr_stmt|;
return|return
name|jcrContent
return|;
block|}
specifier|public
specifier|static
name|Tree
name|createFulltextIndex
parameter_list|(
name|Tree
name|index
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|type
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|Tree
name|def
init|=
name|index
operator|.
name|addChild
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|addChild
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|def
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|def
operator|.
name|setProperty
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|def
operator|.
name|setProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|def
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|INCLUDE_PROPERTY_TYPES
argument_list|,
name|of
argument_list|(
name|PropertyType
operator|.
name|TYPENAME_STRING
argument_list|,
name|PropertyType
operator|.
name|TYPENAME_BINARY
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|index
operator|.
name|getChild
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|getChild
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|(
name|Repository
name|repository
parameter_list|)
block|{
if|if
condition|(
name|repository
operator|instanceof
name|JackrabbitRepository
condition|)
block|{
operator|(
operator|(
name|JackrabbitRepository
operator|)
name|repository
operator|)
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|NodeBuilder
name|enableIndexingMode
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|IndexingMode
name|indexingMode
parameter_list|)
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|createAsyncProperty
argument_list|(
name|indexingMode
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
specifier|public
specifier|static
name|Tree
name|enableIndexingMode
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|IndexingMode
name|indexingMode
parameter_list|)
block|{
name|tree
operator|.
name|setProperty
argument_list|(
name|createAsyncProperty
argument_list|(
name|indexingMode
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|tree
return|;
block|}
specifier|private
specifier|static
name|PropertyState
name|createAsyncProperty
parameter_list|(
name|String
name|indexingMode
parameter_list|)
block|{
return|return
name|createProperty
argument_list|(
name|IndexConstants
operator|.
name|ASYNC_PROPERTY_NAME
argument_list|,
name|of
argument_list|(
name|indexingMode
argument_list|,
literal|"async"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|PropertyState
name|createAsyncProperty
parameter_list|(
name|IndexingMode
name|indexingMode
parameter_list|)
block|{
switch|switch
condition|(
name|indexingMode
condition|)
block|{
case|case
name|SYNC
case|:
return|return
name|createAsyncProperty
argument_list|(
name|indexingMode
operator|.
name|asyncValueName
argument_list|()
argument_list|)
return|;
case|case
name|ASYNC
case|:
return|return
name|createProperty
argument_list|(
name|IndexConstants
operator|.
name|ASYNC_PROPERTY_NAME
argument_list|,
name|of
argument_list|(
literal|"async"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown mode "
operator|+
name|indexingMode
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
class|class
name|OptionalEditorProvider
implements|implements
name|EditorProvider
block|{
specifier|public
name|EditorProvider
name|delegate
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Editor
name|getRootEditor
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|delegate
operator|!=
literal|null
condition|)
block|{
return|return
name|delegate
operator|.
name|getRootEditor
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|builder
argument_list|,
name|info
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

