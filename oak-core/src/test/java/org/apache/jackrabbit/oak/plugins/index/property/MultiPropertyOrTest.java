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
name|PropertyStates
operator|.
name|createProperty
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
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|matchers
operator|.
name|JUnitMatchers
operator|.
name|containsString
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  *<code>RelativePathTest</code>...  */
end_comment

begin_class
specifier|public
class|class
name|MultiPropertyOrTest
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
name|void
name|initialize
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|builder
parameter_list|)
block|{
name|NodeBuilder
name|index
init|=
name|IndexUtils
operator|.
name|getOrCreateOakIndex
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|IndexUtils
operator|.
name|createIndexDefinition
argument_list|(
name|index
argument_list|,
literal|"xyz"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"x"
argument_list|,
literal|"y"
argument_list|,
literal|"z"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
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
name|addChild
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"x"
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
literal|"y"
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
literal|"z"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|setTraversalEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"select [jcr:path] from [nt:base] where [x] is not null"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|lines
init|=
name|executeQuery
argument_list|(
literal|"explain select [jcr:path] from [nt:base] where [x] is not null"
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|lines
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// make sure it used the property index
name|assertTrue
argument_list|(
name|lines
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|contains
argument_list|(
literal|"property xyz IS NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|lines
operator|=
name|executeQuery
argument_list|(
literal|"explain select [jcr:path] from [nt:base] where [x] = 'foo' OR [y] = 'foo'"
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|lines
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// make sure it used the property index
name|assertTrue
argument_list|(
name|lines
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|contains
argument_list|(
literal|"property xyz = foo"
argument_list|)
argument_list|)
expr_stmt|;
name|lines
operator|=
name|executeQuery
argument_list|(
literal|"explain select [jcr:path] from [nt:base] where [x] = 'foo' OR [y] = 'bar'"
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|lines
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// make sure it used the property index
name|assertTrue
argument_list|(
name|lines
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|lines
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|contains
argument_list|(
literal|"property xyz IN (foo, bar)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"select [jcr:path] from [nt:base] where [x] = 'foo' OR [y] = 'foo'"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"select [jcr:path] from [nt:base] where [x] = 'foo' OR [z] = 'foo'"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/a"
argument_list|,
literal|"/c"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"select [jcr:path] from [nt:base] where [x] = 'foo' OR [y] = 'bar'"
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"/a"
argument_list|,
literal|"/b"
argument_list|)
argument_list|)
expr_stmt|;
name|setTraversalEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|unionSortResultCount
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create test data
name|Tree
name|test
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
argument_list|)
decl_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|nodes
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|int
name|seed
init|=
operator|-
literal|2
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|Tree
name|a
init|=
name|test
operator|.
name|addChild
argument_list|(
literal|"a"
operator|+
name|i
argument_list|)
decl_stmt|;
name|a
operator|.
name|setProperty
argument_list|(
literal|"x"
argument_list|,
literal|"fooa"
argument_list|)
expr_stmt|;
name|seed
operator|+=
literal|2
expr_stmt|;
name|int
name|num
init|=
name|r
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|a
operator|.
name|setProperty
argument_list|(
literal|"z"
argument_list|,
name|num
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|num
argument_list|)
expr_stmt|;
block|}
name|seed
operator|=
operator|-
literal|1
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|Tree
name|a
init|=
name|test
operator|.
name|addChild
argument_list|(
literal|"b"
operator|+
name|i
argument_list|)
decl_stmt|;
name|a
operator|.
name|setProperty
argument_list|(
literal|"y"
argument_list|,
literal|"foob"
argument_list|)
expr_stmt|;
name|seed
operator|+=
literal|2
expr_stmt|;
name|int
name|num
init|=
literal|100
operator|+
name|r
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|a
operator|.
name|setProperty
argument_list|(
literal|"z"
argument_list|,
name|num
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|num
argument_list|)
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// scan count scans the whole result set
name|String
name|query
init|=
literal|"measure /jcr:root//element(*, nt:base)[(@x = 'fooa' or @y = 'foob')] order by @z"
decl_stmt|;
name|assertThat
argument_list|(
name|measureWithLimit
argument_list|(
name|query
argument_list|,
name|XPATH
argument_list|,
literal|100
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"scanCount: 2000"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|measureWithLimit
parameter_list|(
name|String
name|query
parameter_list|,
name|String
name|lang
parameter_list|,
name|int
name|limit
parameter_list|)
throws|throws
name|ParseException
block|{
name|List
argument_list|<
name|?
extends|extends
name|ResultRow
argument_list|>
name|result
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|qe
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|,
name|lang
argument_list|,
name|limit
argument_list|,
literal|0
argument_list|,
name|Maps
operator|.
expr|<
name|String
argument_list|,
name|PropertyValue
operator|>
name|newHashMap
argument_list|()
argument_list|,
name|NO_MAPPINGS
argument_list|)
operator|.
name|getRows
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|measure
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|measure
operator|=
name|result
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|measure
return|;
block|}
block|}
end_class

end_unit

