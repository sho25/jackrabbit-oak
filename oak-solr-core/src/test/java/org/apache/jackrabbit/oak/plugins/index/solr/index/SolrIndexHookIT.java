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
name|index
operator|.
name|solr
operator|.
name|index
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
name|oak
operator|.
name|plugins
operator|.
name|index
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
name|plugins
operator|.
name|index
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
name|query
operator|.
name|ast
operator|.
name|Operator
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
name|index
operator|.
name|FilterImpl
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
name|solr
operator|.
name|SolrBaseTest
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
name|solr
operator|.
name|query
operator|.
name|SolrQueryIndex
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
name|query
operator|.
name|Cursor
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
name|Filter
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
name|IndexRow
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
name|PropertyValues
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
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertTrue
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
class|class
name|SolrIndexHookIT
extends|extends
name|SolrBaseTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testSingleNodeCreation
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|EMPTY_NODE
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|root
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
literal|"solr"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
literal|"oak:queryIndexDefinition"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"solr"
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"newnode"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"prop"
argument_list|,
literal|"val"
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|EditorProvider
name|provider
init|=
operator|new
name|EditorProvider
argument_list|()
block|{
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
parameter_list|)
block|{
return|return
operator|new
name|SolrIndexHook
argument_list|(
literal|"/"
argument_list|,
name|builder
argument_list|,
name|server
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|EditorHook
name|hook
init|=
operator|new
name|EditorHook
argument_list|(
name|provider
argument_list|)
decl_stmt|;
name|NodeState
name|indexed
init|=
name|hook
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|IndexDefinition
name|testDef
init|=
operator|new
name|IndexDefinitionImpl
argument_list|(
literal|"solr"
argument_list|,
literal|"solr"
argument_list|,
literal|"/oak:index/solr"
argument_list|)
decl_stmt|;
name|QueryIndex
name|queryIndex
init|=
operator|new
name|SolrQueryIndex
argument_list|(
name|testDef
argument_list|,
name|server
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|filter
operator|.
name|restrictPath
argument_list|(
literal|"newnode"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|EXACT
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"prop"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"val"
argument_list|)
argument_list|)
expr_stmt|;
name|Cursor
name|cursor
init|=
name|queryIndex
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|indexed
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|cursor
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"no results found"
argument_list|,
name|cursor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|IndexRow
name|next
init|=
name|cursor
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"first returned item should not be null"
argument_list|,
name|next
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"newnode"
argument_list|,
name|next
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cursor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPropertyAddition
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|EMPTY_NODE
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|root
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
literal|"solr"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
literal|"oak:queryIndexDefinition"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"solr"
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|EditorProvider
name|provider
init|=
operator|new
name|EditorProvider
argument_list|()
block|{
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
parameter_list|)
block|{
return|return
operator|new
name|SolrIndexHook
argument_list|(
literal|"/"
argument_list|,
name|builder
argument_list|,
name|server
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|EditorHook
name|hook
init|=
operator|new
name|EditorHook
argument_list|(
name|provider
argument_list|)
decl_stmt|;
name|NodeState
name|indexed
init|=
name|hook
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|IndexDefinition
name|testDef
init|=
operator|new
name|IndexDefinitionImpl
argument_list|(
literal|"solr"
argument_list|,
literal|"solr"
argument_list|,
literal|"/oak:index/solr"
argument_list|)
decl_stmt|;
name|QueryIndex
name|queryIndex
init|=
operator|new
name|SolrQueryIndex
argument_list|(
name|testDef
argument_list|,
name|server
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"foo"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|Cursor
name|cursor
init|=
name|queryIndex
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|indexed
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|cursor
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"no results found"
argument_list|,
name|cursor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|IndexRow
name|next
init|=
name|cursor
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"first returned item should not be null"
argument_list|,
name|next
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/"
argument_list|,
name|next
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|next
operator|.
name|getValue
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"[bar]"
argument_list|)
argument_list|,
name|next
operator|.
name|getValue
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cursor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSomeNodesCreationWithFullText
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|EMPTY_NODE
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|root
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
literal|"solr"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
literal|"oak:queryIndexDefinition"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"type"
argument_list|,
literal|"solr"
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|builder
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
literal|"a"
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
literal|"a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"b"
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
literal|"a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|child
argument_list|(
literal|"c"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|EditorProvider
name|provider
init|=
operator|new
name|EditorProvider
argument_list|()
block|{
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
parameter_list|)
block|{
return|return
operator|new
name|SolrIndexHook
argument_list|(
literal|"/"
argument_list|,
name|builder
argument_list|,
name|server
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|EditorHook
name|hook
init|=
operator|new
name|EditorHook
argument_list|(
name|provider
argument_list|)
decl_stmt|;
name|NodeState
name|indexed
init|=
name|hook
operator|.
name|processCommit
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|IndexDefinition
name|testDef
init|=
operator|new
name|IndexDefinitionImpl
argument_list|(
literal|"solr"
argument_list|,
literal|"solr"
argument_list|,
literal|"/oak:index/solr"
argument_list|)
decl_stmt|;
name|QueryIndex
name|queryIndex
init|=
operator|new
name|SolrQueryIndex
argument_list|(
name|testDef
argument_list|,
name|server
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
name|FilterImpl
name|filter
init|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"foo"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictFulltextCondition
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|Cursor
name|cursor
init|=
name|queryIndex
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|indexed
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cursor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/"
argument_list|,
name|cursor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|cursor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a/b"
argument_list|,
name|cursor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a/b/c"
argument_list|,
name|cursor
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cursor
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

