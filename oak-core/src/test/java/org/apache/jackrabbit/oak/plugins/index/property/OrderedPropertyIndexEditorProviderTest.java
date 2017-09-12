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
name|property
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
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
operator|.
name|INDEX_CONTENT_NODE_NAME
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
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|hasItem
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
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
name|assertEquals
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
name|assertFalse
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
name|ImmutableList
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
name|junit
operator|.
name|LogCustomizer
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
name|IndexUpdateProvider
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
name|IndexUtils
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
name|commit
operator|.
name|CommitHook
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|ch
operator|.
name|qos
operator|.
name|logback
operator|.
name|classic
operator|.
name|Level
import|;
end_import

begin_class
specifier|public
class|class
name|OrderedPropertyIndexEditorProviderTest
block|{
specifier|private
specifier|final
name|CommitHook
name|hook
init|=
operator|new
name|EditorHook
argument_list|(
operator|new
name|IndexUpdateProvider
argument_list|(
operator|new
name|OrderedPropertyIndexEditorProvider
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|LogCustomizer
name|custom
init|=
name|LogCustomizer
operator|.
name|forLogger
argument_list|(
name|OrderedPropertyIndexEditorProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|enable
argument_list|(
name|Level
operator|.
name|WARN
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|String
name|indexName
init|=
literal|"mickey"
decl_stmt|;
specifier|private
specifier|final
name|String
name|indexedProperty
init|=
literal|"mouse"
decl_stmt|;
specifier|private
specifier|final
name|String
name|DEPRECATION_MESSAGE
init|=
name|OrderedIndex
operator|.
name|DEPRECATION_MESSAGE
operator|.
name|replace
argument_list|(
literal|"{}"
argument_list|,
literal|"/"
operator|+
name|INDEX_DEFINITIONS_NAME
operator|+
literal|"/"
operator|+
name|indexName
argument_list|)
decl_stmt|;
specifier|private
name|Tree
name|createIndexDef
parameter_list|(
name|NodeBuilder
name|root
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|IndexUtils
operator|.
name|createIndexDefinition
argument_list|(
name|TreeFactory
operator|.
name|createTree
argument_list|(
name|root
operator|.
name|child
argument_list|(
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|)
argument_list|,
name|indexName
argument_list|,
literal|false
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|indexedProperty
argument_list|)
argument_list|,
literal|null
argument_list|,
name|OrderedIndex
operator|.
name|TYPE
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|withIndexDefSingleNode
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|CommitFailedException
block|{
name|NodeBuilder
name|root
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|createIndexDef
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|root
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|root
operator|.
name|child
argument_list|(
literal|"n1"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|indexedProperty
argument_list|,
literal|"dead"
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|root
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|custom
operator|.
name|starting
argument_list|()
expr_stmt|;
name|root
operator|=
name|hook
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
operator|.
name|builder
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|custom
operator|.
name|getLogs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|custom
operator|.
name|getLogs
argument_list|()
argument_list|,
name|hasItem
argument_list|(
name|DEPRECATION_MESSAGE
argument_list|)
argument_list|)
expr_stmt|;
name|custom
operator|.
name|finished
argument_list|()
expr_stmt|;
name|NodeBuilder
name|b
init|=
name|root
operator|.
name|getChildNode
argument_list|(
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|)
operator|.
name|getChildNode
argument_list|(
name|indexName
argument_list|)
operator|.
name|getChildNode
argument_list|(
name|IndexConstants
operator|.
name|INDEX_CONTENT_NODE_NAME
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"nothing should have been touched under the actual index"
argument_list|,
name|b
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|withIndexMultipleNodes
parameter_list|()
throws|throws
name|RepositoryException
throws|,
name|CommitFailedException
block|{
specifier|final
name|int
name|threshold
init|=
literal|5
decl_stmt|;
specifier|final
name|int
name|nodes
init|=
literal|16
decl_stmt|;
specifier|final
name|int
name|traces
init|=
literal|1
operator|+
operator|(
name|nodes
operator|-
literal|1
operator|)
operator|/
name|threshold
decl_stmt|;
name|OrderedPropertyIndexEditorProvider
operator|.
name|setThreshold
argument_list|(
name|threshold
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|Collections
operator|.
name|nCopies
argument_list|(
name|traces
argument_list|,
name|DEPRECATION_MESSAGE
argument_list|)
decl_stmt|;
name|NodeBuilder
name|root
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|createIndexDef
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|custom
operator|.
name|starting
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodes
condition|;
name|i
operator|++
control|)
block|{
name|NodeState
name|before
init|=
name|root
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|root
operator|.
name|child
argument_list|(
literal|"n"
operator|+
name|i
argument_list|)
operator|.
name|setProperty
argument_list|(
name|indexedProperty
argument_list|,
literal|"dead"
operator|+
name|i
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|root
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|root
operator|=
name|hook
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
operator|.
name|builder
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|custom
operator|.
name|getLogs
argument_list|()
argument_list|,
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
name|custom
operator|.
name|finished
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|root
operator|.
name|getChildNode
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|getChildNode
argument_list|(
name|indexName
argument_list|)
operator|.
name|getChildNode
argument_list|(
name|INDEX_CONTENT_NODE_NAME
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|providerShouldBeAvailable
parameter_list|()
throws|throws
name|Exception
block|{
name|CommitHook
name|hook
init|=
operator|new
name|EditorHook
argument_list|(
operator|new
name|IndexUpdateProvider
argument_list|(
operator|new
name|OrderedPropertyIndexEditorProvider
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|NodeBuilder
name|root
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|createIndexDef
argument_list|(
name|root
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"reindex"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|root
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|root
operator|.
name|child
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|root
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|hook
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

