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
name|property
package|;
end_package

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
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|IndexUtils
operator|.
name|getOrCreateOakIndex
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
name|IndexUtils
operator|.
name|createIndexDefinition
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
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|Query
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
name|nodetype
operator|.
name|write
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
name|query
operator|.
name|QueryEngineImpl
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
name|lifecycle
operator|.
name|RepositoryInitializer
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

begin_class
specifier|public
class|class
name|MultipleIndicesTest
extends|extends
name|AbstractQueryTest
block|{
annotation|@
name|Override
specifier|protected
name|ContentRepository
name|createRepository
parameter_list|()
block|{
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
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|RepositoryInitializer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeState
name|initialize
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
name|NodeBuilder
name|root
init|=
name|state
operator|.
name|builder
argument_list|()
decl_stmt|;
name|createIndexDefinition
argument_list|(
name|getOrCreateOakIndex
argument_list|(
name|root
argument_list|)
argument_list|,
literal|"pid"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"pid"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|createIndexDefinition
argument_list|(
name|getOrCreateOakIndex
argument_list|(
name|root
operator|.
name|child
argument_list|(
literal|"content"
argument_list|)
argument_list|)
argument_list|,
literal|"pid"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"pid"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
name|root
operator|.
name|getNodeState
argument_list|()
return|;
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
operator|new
name|PropertyIndexProvider
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
name|createContentRepository
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|query
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|t
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|t
operator|.
name|setProperty
argument_list|(
literal|"pid"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|t
operator|.
name|addChild
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"pid"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|t
operator|.
name|addChild
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"pid"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|t
operator|.
name|addChild
argument_list|(
literal|"c"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"pid"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|t
operator|.
name|addChild
argument_list|(
literal|"d"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"cid"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|Tree
name|content
init|=
name|t
operator|.
name|addChild
argument_list|(
literal|"content"
argument_list|)
decl_stmt|;
name|content
operator|.
name|addChild
argument_list|(
literal|"x"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"pid"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|content
operator|.
name|addChild
argument_list|(
literal|"y"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"pid"
argument_list|,
literal|"baz"
argument_list|)
expr_stmt|;
name|content
operator|.
name|addChild
argument_list|(
literal|"z"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"pid"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|setTravesalFallback
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"select [jcr:path] from [nt:base] where [cid] = 'foo'"
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"select [jcr:path] from [nt:base] where [pid] = 'foo'"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/"
argument_list|,
literal|"/a"
argument_list|,
literal|"/c"
argument_list|,
literal|"/content/x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"select [jcr:path] from [nt:base] where [pid] = 'bar'"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/b"
argument_list|,
literal|"/content/z"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"select [jcr:path] from [nt:base] where [pid] = 'baz'"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/content/y"
argument_list|)
argument_list|)
expr_stmt|;
name|setTravesalFallback
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

