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
name|counter
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
name|QueryEngine
operator|.
name|NO_MAPPINGS
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
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
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
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
name|ContentSession
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
name|PropertyValue
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
name|QueryEngine
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
name|Result
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
name|ResultRow
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
name|commons
operator|.
name|json
operator|.
name|JsonObject
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
name|json
operator|.
name|JsopTokenizer
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
name|AsyncIndexUpdate
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
name|property
operator|.
name|PropertyIndexEditorProvider
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|whiteboard
operator|.
name|Whiteboard
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
name|whiteboard
operator|.
name|WhiteboardUtils
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
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_comment
comment|/**  * A test case for the node counter index.  */
end_comment

begin_class
specifier|public
class|class
name|NodeCounterIndexTest
block|{
name|Whiteboard
name|wb
decl_stmt|;
name|NodeStore
name|nodeStore
decl_stmt|;
name|Root
name|root
decl_stmt|;
name|QueryEngine
name|qe
decl_stmt|;
name|ContentSession
name|session
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|session
operator|=
name|createRepository
argument_list|()
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|root
operator|=
name|session
operator|.
name|getLatestRoot
argument_list|()
expr_stmt|;
name|qe
operator|=
name|root
operator|.
name|getQueryEngine
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotUsedBeforeValid
parameter_list|()
throws|throws
name|Exception
block|{
name|root
operator|.
name|getTree
argument_list|(
literal|"/oak:index/counter"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"resolution"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// no index data before indexing
name|assertFalse
argument_list|(
name|nodeExists
argument_list|(
literal|"oak:index/counter/:index"
argument_list|)
argument_list|)
expr_stmt|;
comment|// so, cost for traversal is high
name|assertTrue
argument_list|(
name|getCost
argument_list|(
literal|"/jcr:root//*"
argument_list|)
operator|>=
literal|1.0E8
argument_list|)
expr_stmt|;
name|runAsyncIndex
argument_list|()
expr_stmt|;
comment|// sometimes, the :index node doesn't exist because there are very few
comment|// nodes (randomly, because the seed value of the node counter is random
comment|// by design) - so we create nodes until the index exists
comment|// (we could use a fixed seed to ensure this is not the case,
comment|// but creating nodes has the same effect)
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
operator|!
name|nodeExists
argument_list|(
literal|"oak:index/counter/:index"
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"index not ready after 100 iterations"
argument_list|,
name|i
operator|<
literal|100
argument_list|)
expr_stmt|;
name|Tree
name|t
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
literal|"test"
operator|+
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|t
operator|.
name|addChild
argument_list|(
literal|"n"
operator|+
name|j
argument_list|)
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|runAsyncIndex
argument_list|()
expr_stmt|;
block|}
comment|// because we do have node counter data,
comment|// cost for traversal is low
name|assertTrue
argument_list|(
name|getCost
argument_list|(
literal|"/jcr:root//*"
argument_list|)
operator|<
literal|1.0E8
argument_list|)
expr_stmt|;
comment|// remove the counter index
name|root
operator|.
name|getTree
argument_list|(
literal|"/oak:index/counter"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|nodeExists
argument_list|(
literal|"oak:index/counter"
argument_list|)
argument_list|)
expr_stmt|;
comment|// so, cost for traversal is high again
name|assertTrue
argument_list|(
name|getCost
argument_list|(
literal|"/jcr:root//*"
argument_list|)
operator|>=
literal|1.0E8
argument_list|)
expr_stmt|;
block|}
specifier|private
name|double
name|getCost
parameter_list|(
name|String
name|xpath
parameter_list|)
throws|throws
name|ParseException
block|{
name|String
name|plan
init|=
name|executeXPathQuery
argument_list|(
literal|"explain measure "
operator|+
name|xpath
argument_list|)
decl_stmt|;
name|String
name|cost
init|=
name|plan
operator|.
name|substring
argument_list|(
name|plan
operator|.
name|lastIndexOf
argument_list|(
literal|'{'
argument_list|)
argument_list|)
decl_stmt|;
name|JsonObject
name|json
init|=
name|parseJson
argument_list|(
name|cost
argument_list|)
decl_stmt|;
name|double
name|c
init|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|json
operator|.
name|getProperties
argument_list|()
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|c
return|;
block|}
specifier|private
specifier|static
name|JsonObject
name|parseJson
parameter_list|(
name|String
name|json
parameter_list|)
block|{
name|JsopTokenizer
name|t
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|t
operator|.
name|read
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
return|return
name|JsonObject
operator|.
name|create
argument_list|(
name|t
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|nodeExists
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|NodeStateUtils
operator|.
name|getNode
argument_list|(
name|nodeStore
operator|.
name|getRoot
argument_list|()
argument_list|,
name|path
argument_list|)
operator|.
name|exists
argument_list|()
return|;
block|}
specifier|protected
name|String
name|executeXPathQuery
parameter_list|(
name|String
name|statement
parameter_list|)
throws|throws
name|ParseException
block|{
name|Result
name|result
init|=
name|qe
operator|.
name|executeQuery
argument_list|(
name|statement
argument_list|,
literal|"xpath"
argument_list|,
literal|null
argument_list|,
name|NO_MAPPINGS
argument_list|)
decl_stmt|;
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|ResultRow
name|row
range|:
name|result
operator|.
name|getRows
argument_list|()
control|)
block|{
for|for
control|(
name|PropertyValue
name|v
range|:
name|row
operator|.
name|getValues
argument_list|()
control|)
block|{
name|buff
operator|.
name|append
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|protected
name|ContentRepository
name|createRepository
parameter_list|()
block|{
name|nodeStore
operator|=
operator|new
name|MemoryNodeStore
argument_list|()
expr_stmt|;
name|Oak
name|oak
init|=
operator|new
name|Oak
argument_list|(
name|nodeStore
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|InitialContent
argument_list|()
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
operator|new
name|PropertyIndexEditorProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|NodeCounterEditorProvider
argument_list|()
argument_list|)
comment|//Effectively disable async indexing auto run
comment|//such that we can control run timing as per test requirement
operator|.
name|withAsyncIndexing
argument_list|(
literal|"async"
argument_list|,
name|TimeUnit
operator|.
name|DAYS
operator|.
name|toSeconds
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|wb
operator|=
name|oak
operator|.
name|getWhiteboard
argument_list|()
expr_stmt|;
return|return
name|oak
operator|.
name|createContentRepository
argument_list|()
return|;
block|}
specifier|private
name|void
name|runAsyncIndex
parameter_list|()
block|{
name|Runnable
name|async
init|=
name|WhiteboardUtils
operator|.
name|getService
argument_list|(
name|wb
argument_list|,
name|Runnable
operator|.
name|class
argument_list|,
operator|new
name|Predicate
argument_list|<
name|Runnable
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|test
parameter_list|(
annotation|@
name|Nullable
name|Runnable
name|input
parameter_list|)
block|{
return|return
name|input
operator|instanceof
name|AsyncIndexUpdate
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|async
argument_list|)
expr_stmt|;
name|async
operator|.
name|run
argument_list|()
expr_stmt|;
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

