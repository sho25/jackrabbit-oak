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
operator|.
name|lucene
package|;
end_package

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
name|checkArgument
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
name|ImmutableList
operator|.
name|of
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
name|Lists
operator|.
name|newArrayList
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
name|JCR_SYSTEM
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
name|NT_FILE
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
name|lucene
operator|.
name|TestUtil
operator|.
name|newNodeAggregator
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
name|lucene
operator|.
name|TestUtil
operator|.
name|useV2
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|JCR_NODE_TYPES
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|oak
operator|.
name|Oak
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
name|ContentRepository
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
name|plugins
operator|.
name|index
operator|.
name|aggregate
operator|.
name|SimpleNodeAggregator
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
name|InitialContent
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
name|query
operator|.
name|AbstractQueryTest
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
name|Observer
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
name|QueryIndex
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
name|QueryIndexProvider
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
name|security
operator|.
name|OpenSecurityProvider
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
name|TreeUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|base
operator|.
name|Strings
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
name|Lists
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneIndexAggregationTest2
extends|extends
name|AbstractQueryTest
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LuceneIndexAggregationTest2
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|NT_TEST_PAGE
init|=
literal|"test:Page"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|NT_TEST_PAGECONTENT
init|=
literal|"test:PageContent"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|NT_TEST_ASSET
init|=
literal|"test:Asset"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|NT_TEST_ASSETCONTENT
init|=
literal|"test:AssetContent"
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|ContentRepository
name|createRepository
parameter_list|()
block|{
name|LuceneIndexProvider
name|provider
init|=
operator|new
name|LuceneIndexProvider
argument_list|()
decl_stmt|;
return|return
operator|new
name|Oak
argument_list|()
operator|.
name|with
argument_list|(
operator|new
name|InitialContent
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|super
operator|.
name|initialize
argument_list|(
name|builder
argument_list|)
expr_stmt|;
comment|// registering additional node types for wider testing
name|InputStream
name|stream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|stream
operator|=
name|LuceneIndexAggregationTest2
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"test_nodetypes.cnd"
argument_list|)
expr_stmt|;
name|NodeState
name|base
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
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
name|stream
argument_list|,
literal|"testing node types"
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
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error while registering required node types. Failing here"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Error while registering required node types"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|printNodeTypes
argument_list|(
name|builder
argument_list|)
expr_stmt|;
if|if
condition|(
name|stream
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Ignoring exception on stream closing."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|(
operator|(
name|QueryIndexProvider
operator|)
name|provider
operator|.
name|with
argument_list|(
name|getNodeAggregator
argument_list|()
argument_list|)
operator|)
argument_list|)
operator|.
name|with
argument_list|(
operator|(
name|Observer
operator|)
name|provider
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|LuceneIndexEditorProvider
argument_list|()
argument_list|)
operator|.
name|createContentRepository
argument_list|()
return|;
block|}
comment|/**      * convenience method for printing on logs the currently registered node types.      *       * @param builder      */
specifier|private
specifier|static
name|void
name|printNodeTypes
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|NodeBuilder
name|namespace
init|=
name|builder
operator|.
name|child
argument_list|(
name|JCR_SYSTEM
argument_list|)
operator|.
name|child
argument_list|(
name|JCR_NODE_TYPES
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|nodes
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|namespace
operator|.
name|getChildNodeNames
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|node
range|:
name|nodes
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|createTestIndexNode
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|index
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|Tree
name|indexDefn
init|=
name|createTestIndexNode
argument_list|(
name|index
argument_list|,
name|LuceneIndexConstants
operator|.
name|TYPE_LUCENE
argument_list|)
decl_stmt|;
name|useV2
argument_list|(
name|indexDefn
argument_list|)
expr_stmt|;
comment|//Aggregates
name|newNodeAggregator
argument_list|(
name|indexDefn
argument_list|)
operator|.
name|newRuleWithName
argument_list|(
name|NT_FILE
argument_list|,
name|newArrayList
argument_list|(
literal|"jcr:content"
argument_list|)
argument_list|)
operator|.
name|newRuleWithName
argument_list|(
name|NT_TEST_PAGE
argument_list|,
name|newArrayList
argument_list|(
literal|"jcr:content"
argument_list|)
argument_list|)
operator|.
name|newRuleWithName
argument_list|(
name|NT_TEST_PAGECONTENT
argument_list|,
name|newArrayList
argument_list|(
literal|"*"
argument_list|,
literal|"*/*"
argument_list|,
literal|"*/*/*"
argument_list|,
literal|"*/*/*/*"
argument_list|)
argument_list|)
operator|.
name|newRuleWithName
argument_list|(
name|NT_TEST_ASSET
argument_list|,
name|newArrayList
argument_list|(
literal|"jcr:content"
argument_list|)
argument_list|)
operator|.
name|newRuleWithName
argument_list|(
name|NT_TEST_ASSETCONTENT
argument_list|,
name|newArrayList
argument_list|(
literal|"metadata"
argument_list|,
literal|"renditions"
argument_list|,
literal|"renditions/original"
argument_list|,
literal|"comments"
argument_list|,
literal|"renditions/original/jcr:content"
argument_list|)
argument_list|)
operator|.
name|newRuleWithName
argument_list|(
literal|"rep:User"
argument_list|,
name|newArrayList
argument_list|(
literal|"profile"
argument_list|)
argument_list|)
expr_stmt|;
name|Tree
name|originalInclude
init|=
name|indexDefn
operator|.
name|getChild
argument_list|(
name|LuceneIndexConstants
operator|.
name|AGGREGATES
argument_list|)
operator|.
name|getChild
argument_list|(
name|NT_TEST_ASSET
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"includeOriginal"
argument_list|)
decl_stmt|;
name|originalInclude
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
name|originalInclude
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|AGG_PATH
argument_list|,
literal|"jcr:content/renditions/original"
argument_list|)
expr_stmt|;
name|Tree
name|includeSingleRel
init|=
name|indexDefn
operator|.
name|getChild
argument_list|(
name|LuceneIndexConstants
operator|.
name|AGGREGATES
argument_list|)
operator|.
name|getChild
argument_list|(
name|NT_TEST_ASSET
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"includeFirstLevelChild"
argument_list|)
decl_stmt|;
name|includeSingleRel
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
name|includeSingleRel
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|AGG_PATH
argument_list|,
literal|"firstLevelChild"
argument_list|)
expr_stmt|;
comment|//Include all properties
name|Tree
name|props
init|=
name|TestUtil
operator|.
name|newRulePropTree
argument_list|(
name|indexDefn
argument_list|,
literal|"test:Asset"
argument_list|)
decl_stmt|;
name|TestUtil
operator|.
name|enableForFullText
argument_list|(
name|props
argument_list|,
literal|"jcr:content/metadata/format"
argument_list|)
expr_stmt|;
name|TestUtil
operator|.
name|enableForFullText
argument_list|(
name|props
argument_list|,
name|LuceneIndexConstants
operator|.
name|REGEX_ALL_PROPS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|QueryIndex
operator|.
name|NodeAggregator
name|getNodeAggregator
parameter_list|()
block|{
return|return
operator|new
name|SimpleNodeAggregator
argument_list|()
operator|.
name|newRuleWithName
argument_list|(
name|NT_FILE
argument_list|,
name|newArrayList
argument_list|(
literal|"jcr:content"
argument_list|)
argument_list|)
operator|.
name|newRuleWithName
argument_list|(
name|NT_TEST_PAGE
argument_list|,
name|newArrayList
argument_list|(
literal|"jcr:content"
argument_list|)
argument_list|)
operator|.
name|newRuleWithName
argument_list|(
name|NT_TEST_PAGECONTENT
argument_list|,
name|newArrayList
argument_list|(
literal|"*"
argument_list|,
literal|"*/*"
argument_list|,
literal|"*/*/*"
argument_list|,
literal|"*/*/*/*"
argument_list|)
argument_list|)
operator|.
name|newRuleWithName
argument_list|(
name|NT_TEST_ASSET
argument_list|,
name|newArrayList
argument_list|(
literal|"jcr:content"
argument_list|)
argument_list|)
operator|.
name|newRuleWithName
argument_list|(
name|NT_TEST_ASSETCONTENT
argument_list|,
name|newArrayList
argument_list|(
literal|"metadata"
argument_list|,
literal|"renditions"
argument_list|,
literal|"renditions/original"
argument_list|,
literal|"comments"
argument_list|,
literal|"renditions/original/jcr:content"
argument_list|)
argument_list|)
operator|.
name|newRuleWithName
argument_list|(
literal|"rep:User"
argument_list|,
name|newArrayList
argument_list|(
literal|"profile"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|oak2226
parameter_list|()
throws|throws
name|Exception
block|{
name|setTraversalEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
name|statement
init|=
literal|"/jcr:root/content//element(*, test:Asset)["
operator|+
literal|"(jcr:contains(., 'mountain')) "
operator|+
literal|"and (jcr:contains(jcr:content/metadata/@format, 'image'))]"
decl_stmt|;
name|Tree
name|content
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"content"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
comment|/*          * creating structure          *  "/content" : {          *      "node" : {          *          "jcr:primaryType" : "test:Asset",          *          "jcr:content" : {          *              "jcr:primaryType" : "test:AssetContent",          *              "metadata" : {          *                  "jcr:primaryType" : "nt:unstructured",          *                  "title" : "Lorem mountain ipsum",          *                  "format" : "image/jpeg"          *              }          *          }          *      },          *      "mountain-node" : {          *          "jcr:primaryType" : "test:Asset",          *          "jcr:content" : {          *              "jcr:primaryType" : "test:AssetContent",          *              "metadata" : {          *                  "jcr:primaryType" : "nt:unstructured",          *                  "format" : "image/jpeg"          *              }          *          }          *      }          *  }          */
comment|// adding a node with 'mountain' property
name|Tree
name|node
init|=
name|content
operator|.
name|addChild
argument_list|(
literal|"node"
argument_list|)
decl_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_TEST_ASSET
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|addChild
argument_list|(
literal|"jcr:content"
argument_list|)
expr_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_TEST_ASSETCONTENT
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|addChild
argument_list|(
literal|"metadata"
argument_list|)
expr_stmt|;
name|node
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
name|node
operator|.
name|setProperty
argument_list|(
literal|"title"
argument_list|,
literal|"Lorem mountain ipsum"
argument_list|,
name|STRING
argument_list|)
expr_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
literal|"format"
argument_list|,
literal|"image/jpeg"
argument_list|,
name|STRING
argument_list|)
expr_stmt|;
comment|// adding a node with 'mountain' name but not property
name|node
operator|=
name|content
operator|.
name|addChild
argument_list|(
literal|"mountain-node"
argument_list|)
expr_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_TEST_ASSET
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
name|node
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|addChild
argument_list|(
literal|"jcr:content"
argument_list|)
expr_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_TEST_ASSETCONTENT
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|addChild
argument_list|(
literal|"metadata"
argument_list|)
expr_stmt|;
name|node
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
name|node
operator|.
name|setProperty
argument_list|(
literal|"format"
argument_list|,
literal|"image/jpeg"
argument_list|,
name|STRING
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertQuery
argument_list|(
name|statement
argument_list|,
literal|"xpath"
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|setTraversalEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|oak2249
parameter_list|()
throws|throws
name|Exception
block|{
name|setTraversalEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
name|statement
init|=
literal|"//element(*, test:Asset)[ "
operator|+
literal|"( "
operator|+
literal|"jcr:contains(., 'summer') "
operator|+
literal|"or "
operator|+
literal|"jcr:content/metadata/@tags = 'namespace:season/summer' "
operator|+
literal|") and "
operator|+
literal|"jcr:contains(jcr:content/metadata/@format, 'image') "
operator|+
literal|"]"
decl_stmt|;
name|Tree
name|content
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"content"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|Tree
name|metadata
init|=
name|createAssetStructure
argument_list|(
name|content
argument_list|,
literal|"tagged"
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|setProperty
argument_list|(
literal|"tags"
argument_list|,
name|of
argument_list|(
literal|"namespace:season/summer"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|setProperty
argument_list|(
literal|"format"
argument_list|,
literal|"image/jpeg"
argument_list|,
name|STRING
argument_list|)
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
literal|"/content/tagged"
argument_list|)
expr_stmt|;
name|metadata
operator|=
name|createAssetStructure
argument_list|(
name|content
argument_list|,
literal|"titled"
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|setProperty
argument_list|(
literal|"title"
argument_list|,
literal|"Lorem summer ipsum"
argument_list|,
name|STRING
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|setProperty
argument_list|(
literal|"format"
argument_list|,
literal|"image/jpeg"
argument_list|,
name|STRING
argument_list|)
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
literal|"/content/titled"
argument_list|)
expr_stmt|;
name|metadata
operator|=
name|createAssetStructure
argument_list|(
name|content
argument_list|,
literal|"summer-node"
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|setProperty
argument_list|(
literal|"format"
argument_list|,
literal|"image/jpeg"
argument_list|,
name|STRING
argument_list|)
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
literal|"/content/summer-node"
argument_list|)
expr_stmt|;
comment|// the following is NOT expected
name|metadata
operator|=
name|createAssetStructure
argument_list|(
name|content
argument_list|,
literal|"winter-node"
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|setProperty
argument_list|(
literal|"tags"
argument_list|,
name|of
argument_list|(
literal|"namespace:season/winter"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|setProperty
argument_list|(
literal|"title"
argument_list|,
literal|"Lorem winter ipsum"
argument_list|,
name|STRING
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|setProperty
argument_list|(
literal|"format"
argument_list|,
literal|"image/jpeg"
argument_list|,
name|STRING
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertQuery
argument_list|(
name|statement
argument_list|,
literal|"xpath"
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|setTraversalEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|indexRelativeNode
parameter_list|()
throws|throws
name|Exception
block|{
name|setTraversalEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
name|statement
init|=
literal|"//element(*, test:Asset)[ "
operator|+
literal|"jcr:contains(., 'summer') "
operator|+
literal|"and jcr:contains(jcr:content/renditions/original, 'fox')"
operator|+
literal|"and jcr:contains(jcr:content/metadata/@format, 'image') "
operator|+
literal|"]"
decl_stmt|;
name|Tree
name|content
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"content"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|Tree
name|metadata
init|=
name|createAssetStructure
argument_list|(
name|content
argument_list|,
literal|"tagged"
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|setProperty
argument_list|(
literal|"tags"
argument_list|,
name|of
argument_list|(
literal|"namespace:season/summer"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|setProperty
argument_list|(
literal|"format"
argument_list|,
literal|"image/jpeg"
argument_list|,
name|STRING
argument_list|)
expr_stmt|;
name|Tree
name|original
init|=
name|metadata
operator|.
name|getParent
argument_list|()
operator|.
name|addChild
argument_list|(
literal|"renditions"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"original"
argument_list|)
decl_stmt|;
name|original
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
argument_list|)
expr_stmt|;
name|original
operator|.
name|addChild
argument_list|(
literal|"jcr:content"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"jcr:data"
argument_list|,
literal|"fox jumps"
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
literal|"/content/tagged"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertQuery
argument_list|(
name|statement
argument_list|,
literal|"xpath"
argument_list|,
name|expected
argument_list|)
expr_stmt|;
comment|//Update the reaggregated node and with that parent should be get updated
name|Tree
name|originalContent
init|=
name|TreeUtil
operator|.
name|getTree
argument_list|(
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
argument_list|,
literal|"/content/tagged/jcr:content/renditions/original/jcr:content"
argument_list|)
decl_stmt|;
name|originalContent
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"jcr:data"
argument_list|,
literal|"kiwi jumps"
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertQuery
argument_list|(
name|statement
argument_list|,
literal|"xpath"
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
name|setTraversalEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|indexSingleRelativeNode
parameter_list|()
throws|throws
name|Exception
block|{
name|setTraversalEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|String
name|statement
init|=
literal|"//element(*, test:Asset)[ "
operator|+
literal|"jcr:contains(firstLevelChild, 'summer') ]"
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|Tree
name|content
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"content"
argument_list|)
decl_stmt|;
name|Tree
name|page
init|=
name|content
operator|.
name|addChild
argument_list|(
literal|"pages"
argument_list|)
decl_stmt|;
name|page
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_TEST_ASSET
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|Tree
name|child
init|=
name|page
operator|.
name|addChild
argument_list|(
literal|"firstLevelChild"
argument_list|)
decl_stmt|;
name|child
operator|.
name|setProperty
argument_list|(
literal|"tag"
argument_list|,
literal|"summer is here"
argument_list|,
name|STRING
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
literal|"/content/pages"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|statement
argument_list|,
literal|"xpath"
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/**      *<p>      * convenience method that create an "asset" structure like      *</p>      *       *<pre>      *  "parent" : {      *      "nodeName" : {      *          "jcr:primaryType" : "test:Asset",      *          "jcr:content" : {      *              "jcr:primaryType" : "test:AssetContent",      *              "metatada" : {      *                  "jcr:primaryType" : "nt:unstructured"      *              }      *          }      *      }      *  }      *</pre>      *       *<p>      *  and returns the {@code metadata} node      *</p>      *       * @param parent the parent under which creating the node      * @param nodeName the node name to be used      * @return the {@code metadata} node. See above for details      */
specifier|private
specifier|static
name|Tree
name|createAssetStructure
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|Tree
name|parent
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|String
name|nodeName
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|nodeName
argument_list|)
argument_list|,
literal|"nodeName cannot be null or empty"
argument_list|)
expr_stmt|;
name|Tree
name|node
init|=
name|parent
operator|.
name|addChild
argument_list|(
name|nodeName
argument_list|)
decl_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_TEST_ASSET
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|addChild
argument_list|(
name|JCR_CONTENT
argument_list|)
expr_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_TEST_ASSETCONTENT
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|addChild
argument_list|(
literal|"metadata"
argument_list|)
expr_stmt|;
name|node
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
name|node
return|;
block|}
block|}
end_class

end_unit

