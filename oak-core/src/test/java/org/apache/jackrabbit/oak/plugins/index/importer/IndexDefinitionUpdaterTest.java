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
name|importer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|inventory
operator|.
name|Format
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
name|plugins
operator|.
name|index
operator|.
name|inventory
operator|.
name|IndexDefinitionPrinter
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
name|tree
operator|.
name|factories
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
name|EmptyHook
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
name|spi
operator|.
name|state
operator|.
name|NodeStateUtils
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
name|json
operator|.
name|simple
operator|.
name|JSONObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|IndexDefinitionUpdaterTest
block|{
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|folder
init|=
operator|new
name|TemporaryFolder
argument_list|(
operator|new
name|File
argument_list|(
literal|"target"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|NodeStore
name|store
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|update
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"fooIndex"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"fooIndex"
argument_list|)
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo2"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|String
name|json
init|=
name|createIndexDefn
argument_list|()
decl_stmt|;
name|applyJson
argument_list|(
name|json
argument_list|)
expr_stmt|;
name|NodeState
name|root
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|root
argument_list|,
literal|"/oak:index/fooIndex"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|root
argument_list|,
literal|"/oak:index/fooIndex/b"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|root
argument_list|,
literal|"/oak:index/fooIndex/a"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|root
argument_list|,
literal|"/oak:index/barIndex"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|updateNonExistingParent
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"fooIndex"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder2
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder2
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"barIndex"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|String
name|json
init|=
name|createIndexDefn
argument_list|(
name|builder2
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/oak:index/fooIndex"
argument_list|,
literal|"/a/oak:index/barIndex"
argument_list|)
decl_stmt|;
name|applyJson
argument_list|(
name|json
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|invalidJson
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"a2"
argument_list|,
literal|"b2"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|json
init|=
name|JSONObject
operator|.
name|toJSONString
argument_list|(
name|map
argument_list|)
decl_stmt|;
name|applyJson
argument_list|(
name|json
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|applyToIndexPath
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|json
init|=
literal|"{\"/oak:index/barIndex\": {\n"
operator|+
literal|"    \"compatVersion\": 2,\n"
operator|+
literal|"    \"type\": \"lucene\",\n"
operator|+
literal|"    \"barIndexProp\": \"barbar\",\n"
operator|+
literal|"    \"async\": \"async\",\n"
operator|+
literal|"    \"jcr:primaryType\": \"oak:QueryIndexDefinition\"\n"
operator|+
literal|"  }}"
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|IndexDefinitionUpdater
name|updater
init|=
operator|new
name|IndexDefinitionUpdater
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|builder
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|NodeBuilder
name|idxBuilder
init|=
name|updater
operator|.
name|apply
argument_list|(
name|builder
argument_list|,
literal|"/oak:index/barIndex"
argument_list|)
decl_stmt|;
comment|//Check builder returned is of /oak:index/barIndex
name|assertTrue
argument_list|(
name|idxBuilder
operator|.
name|hasProperty
argument_list|(
literal|"barIndexProp"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|newIndexAndOrderableChildren
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|json
init|=
literal|"{\"/oak:index/barIndex\": {\n"
operator|+
literal|"    \"compatVersion\": 2,\n"
operator|+
literal|"    \"type\": \"lucene\",\n"
operator|+
literal|"    \"barIndexProp\": \"barbar\",\n"
operator|+
literal|"    \"async\": \"async\",\n"
operator|+
literal|"    \"jcr:primaryType\": \"oak:QueryIndexDefinition\"\n"
operator|+
literal|"  }}"
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|Tree
name|root
init|=
name|TreeFactory
operator|.
name|createTree
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|Tree
name|oakIndex
init|=
name|root
operator|.
name|addChild
argument_list|(
literal|"oak:index"
argument_list|)
decl_stmt|;
name|oakIndex
operator|.
name|setOrderableChildren
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Tree
name|fooIndex
init|=
name|oakIndex
operator|.
name|addChild
argument_list|(
literal|"fooIndex"
argument_list|)
decl_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|IndexDefinitionUpdater
name|updater
init|=
operator|new
name|IndexDefinitionUpdater
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|builder
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|NodeBuilder
name|idxBuilder
init|=
name|updater
operator|.
name|apply
argument_list|(
name|builder
argument_list|,
literal|"/oak:index/barIndex"
argument_list|)
decl_stmt|;
name|PropertyState
name|childOrder
init|=
name|builder
operator|.
name|getChildNode
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|":childOrder"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|childOrder
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|asList
argument_list|(
literal|"fooIndex"
argument_list|,
literal|"barIndex"
argument_list|)
argument_list|,
name|names
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|applyJson
parameter_list|(
name|String
name|json
parameter_list|)
throws|throws
name|IOException
throws|,
name|CommitFailedException
block|{
name|IndexDefinitionUpdater
name|update
init|=
operator|new
name|IndexDefinitionUpdater
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|update
operator|.
name|apply
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|createIndexDefn
parameter_list|()
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"fooIndex"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"fooIndex"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo3"
argument_list|,
literal|"bar3"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"fooIndex"
argument_list|)
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo4"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"barIndex"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
operator|.
name|child
argument_list|(
literal|"barIndex"
argument_list|)
operator|.
name|child
argument_list|(
literal|"c"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo5"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
return|return
name|createIndexDefn
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/oak:index/fooIndex"
argument_list|,
literal|"/oak:index/barIndex"
argument_list|)
return|;
block|}
specifier|private
name|String
name|createIndexDefn
parameter_list|(
name|NodeState
name|nodeState
parameter_list|,
name|String
modifier|...
name|indexPaths
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeStore
name|store
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|cne
range|:
name|nodeState
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|builder
operator|.
name|setChildNode
argument_list|(
name|cne
operator|.
name|getName
argument_list|()
argument_list|,
name|cne
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|IndexDefinitionPrinter
name|printer
init|=
operator|new
name|IndexDefinitionPrinter
argument_list|(
name|store
argument_list|,
parameter_list|()
lambda|->
name|asList
argument_list|(
name|indexPaths
argument_list|)
argument_list|)
decl_stmt|;
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|printer
operator|.
name|print
argument_list|(
name|pw
argument_list|,
name|Format
operator|.
name|JSON
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|pw
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|sw
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

