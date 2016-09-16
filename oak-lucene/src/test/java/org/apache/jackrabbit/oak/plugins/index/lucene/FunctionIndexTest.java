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
name|NO_BINDINGS
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
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|containsString
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
name|PropertyType
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
name|nodetype
operator|.
name|NodeTypeIndexProvider
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
name|NodeStore
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|FunctionIndexTest
extends|extends
name|AbstractQueryTest
block|{
specifier|private
name|LuceneIndexEditorProvider
name|editorProvider
decl_stmt|;
specifier|private
name|NodeStore
name|nodeStore
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|ContentRepository
name|createRepository
parameter_list|()
block|{
name|editorProvider
operator|=
operator|new
name|LuceneIndexEditorProvider
argument_list|()
expr_stmt|;
name|LuceneIndexProvider
name|provider
init|=
operator|new
name|LuceneIndexProvider
argument_list|()
decl_stmt|;
name|nodeStore
operator|=
operator|new
name|MemoryNodeStore
argument_list|()
expr_stmt|;
return|return
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
operator|(
name|QueryIndexProvider
operator|)
name|provider
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
name|editorProvider
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
name|NodeTypeIndexProvider
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
name|noIndexTest
parameter_list|()
throws|throws
name|Exception
block|{
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
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
literal|3
condition|;
name|idx
operator|++
control|)
block|{
name|Tree
name|low
init|=
name|test
operator|.
name|addChild
argument_list|(
literal|""
operator|+
call|(
name|char
call|)
argument_list|(
literal|'a'
operator|+
name|idx
argument_list|)
argument_list|)
decl_stmt|;
name|low
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:unstructured"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|Tree
name|up
init|=
name|test
operator|.
name|addChild
argument_list|(
literal|""
operator|+
call|(
name|char
call|)
argument_list|(
literal|'A'
operator|+
name|idx
argument_list|)
argument_list|)
decl_stmt|;
name|up
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:unstructured"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|String
name|query
init|=
literal|"select [jcr:path] from [nt:base] where lower(localname()) = 'b'"
decl_stmt|;
name|assertThat
argument_list|(
name|explain
argument_list|(
name|query
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"traverse"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|query
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"/test/b"
argument_list|,
literal|"/test/B"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|queryXPath
init|=
literal|"/jcr:root/test//*[fn:lower-case(fn:local-name()) = 'b']"
decl_stmt|;
name|assertThat
argument_list|(
name|explainXpath
argument_list|(
name|queryXPath
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"traverse"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|queryXPath
argument_list|,
literal|"xpath"
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"/test/b"
argument_list|,
literal|"/test/B"
argument_list|)
argument_list|)
expr_stmt|;
name|queryXPath
operator|=
literal|"/jcr:root/test//*[fn:lower-case(fn:local-name())> 'b']"
expr_stmt|;
name|assertThat
argument_list|(
name|explainXpath
argument_list|(
name|queryXPath
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"traverse"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|queryXPath
argument_list|,
literal|"xpath"
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"/test/c"
argument_list|,
literal|"/test/C"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|=
literal|"select [jcr:path] from [nt:base] where lower(localname()) = 'B'"
expr_stmt|;
name|assertThat
argument_list|(
name|explain
argument_list|(
name|query
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"traverse"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|query
argument_list|,
name|Lists
operator|.
expr|<
name|String
operator|>
name|newArrayList
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|lowerCaseLocalName
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|luceneIndex
init|=
name|createIndex
argument_list|(
literal|"lowerLocalName"
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|luceneIndex
operator|.
name|setProperty
argument_list|(
literal|"excludedPaths"
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"/jcr:system"
argument_list|,
literal|"/oak:index"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|Tree
name|func
init|=
name|luceneIndex
operator|.
name|addChild
argument_list|(
name|LuceneIndexConstants
operator|.
name|INDEX_RULES
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|addChild
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_NODE
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"lowerLocalName"
argument_list|)
decl_stmt|;
name|func
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_FUNCTION
argument_list|,
literal|"lower(localname())"
argument_list|)
expr_stmt|;
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
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
literal|3
condition|;
name|idx
operator|++
control|)
block|{
name|Tree
name|low
init|=
name|test
operator|.
name|addChild
argument_list|(
literal|""
operator|+
call|(
name|char
call|)
argument_list|(
literal|'a'
operator|+
name|idx
argument_list|)
argument_list|)
decl_stmt|;
name|low
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:unstructured"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|Tree
name|up
init|=
name|test
operator|.
name|addChild
argument_list|(
literal|""
operator|+
call|(
name|char
call|)
argument_list|(
literal|'A'
operator|+
name|idx
argument_list|)
argument_list|)
decl_stmt|;
name|up
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:unstructured"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|String
name|query
init|=
literal|"select [jcr:path] from [nt:base] where lower(localname()) = 'b'"
decl_stmt|;
name|assertThat
argument_list|(
name|explain
argument_list|(
name|query
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"lucene:lowerLocalName"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|query
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"/test/b"
argument_list|,
literal|"/test/B"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|queryXPath
init|=
literal|"/jcr:root//*[fn:lower-case(fn:local-name()) = 'b']"
decl_stmt|;
name|assertThat
argument_list|(
name|explainXpath
argument_list|(
name|queryXPath
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"lucene:lowerLocalName"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|queryXPath
argument_list|,
literal|"xpath"
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"/test/b"
argument_list|,
literal|"/test/B"
argument_list|)
argument_list|)
expr_stmt|;
name|queryXPath
operator|=
literal|"/jcr:root//*[fn:lower-case(fn:local-name())> 'b']"
expr_stmt|;
name|assertThat
argument_list|(
name|explainXpath
argument_list|(
name|queryXPath
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"lucene:lowerLocalName"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|queryXPath
argument_list|,
literal|"xpath"
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"/test/c"
argument_list|,
literal|"/test/C"
argument_list|,
literal|"/test"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|=
literal|"select [jcr:path] from [nt:base] where lower(localname()) = 'B'"
expr_stmt|;
name|assertThat
argument_list|(
name|explain
argument_list|(
name|query
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"lucene:lowerLocalName"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|query
argument_list|,
name|Lists
operator|.
expr|<
name|String
operator|>
name|newArrayList
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|lengthName
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|luceneIndex
init|=
name|createIndex
argument_list|(
literal|"lengthName"
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|luceneIndex
operator|.
name|setProperty
argument_list|(
literal|"excludedPaths"
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"/jcr:system"
argument_list|,
literal|"/oak:index"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|Tree
name|func
init|=
name|luceneIndex
operator|.
name|addChild
argument_list|(
name|LuceneIndexConstants
operator|.
name|INDEX_RULES
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|addChild
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_NODE
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"lengthName"
argument_list|)
decl_stmt|;
name|func
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
name|func
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_TYPE
argument_list|,
name|PropertyType
operator|.
name|TYPENAME_LONG
argument_list|)
expr_stmt|;
name|func
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_FUNCTION
argument_list|,
literal|"fn:string-length(fn:name())"
argument_list|)
expr_stmt|;
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
for|for
control|(
name|int
name|idx
init|=
literal|1
init|;
name|idx
operator|<
literal|1000
condition|;
name|idx
operator|*=
literal|10
control|)
block|{
name|Tree
name|testNode
init|=
name|test
operator|.
name|addChild
argument_list|(
literal|"test"
operator|+
name|idx
argument_list|)
decl_stmt|;
name|testNode
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:unstructured"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|String
name|query
init|=
literal|"select [jcr:path] from [nt:base] where length(name()) = 6"
decl_stmt|;
name|assertThat
argument_list|(
name|explain
argument_list|(
name|query
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"lucene:lengthName"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|query
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"/test/test10"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|queryXPath
init|=
literal|"/jcr:root//*[fn:string-length(fn:name()) = 7]"
decl_stmt|;
name|assertThat
argument_list|(
name|explainXpath
argument_list|(
name|queryXPath
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"lucene:lengthName"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|queryXPath
argument_list|,
literal|"xpath"
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"/test/test100"
argument_list|)
argument_list|)
expr_stmt|;
name|queryXPath
operator|=
literal|"/jcr:root//* order by fn:string-length(fn:name())"
expr_stmt|;
name|assertThat
argument_list|(
name|explainXpath
argument_list|(
name|queryXPath
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"lucene:lengthName"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|queryXPath
argument_list|,
literal|"xpath"
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"/test"
argument_list|,
literal|"/test/test1"
argument_list|,
literal|"/test/test10"
argument_list|,
literal|"/test/test100"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|length
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|luceneIndex
init|=
name|createIndex
argument_list|(
literal|"length"
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|luceneIndex
operator|.
name|setProperty
argument_list|(
literal|"excludedPaths"
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"/jcr:system"
argument_list|,
literal|"/oak:index"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|Tree
name|func
init|=
name|luceneIndex
operator|.
name|addChild
argument_list|(
name|LuceneIndexConstants
operator|.
name|INDEX_RULES
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|addChild
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_NODE
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"lengthName"
argument_list|)
decl_stmt|;
name|func
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_FUNCTION
argument_list|,
literal|"fn:string-length(@value)"
argument_list|)
expr_stmt|;
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
for|for
control|(
name|int
name|idx
init|=
literal|1
init|;
name|idx
operator|<=
literal|1000
condition|;
name|idx
operator|*=
literal|10
control|)
block|{
name|Tree
name|testNode
init|=
name|test
operator|.
name|addChild
argument_list|(
literal|"test"
operator|+
name|idx
argument_list|)
decl_stmt|;
name|testNode
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:unstructured"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|testNode
operator|.
name|setProperty
argument_list|(
literal|"value"
argument_list|,
operator|new
name|byte
index|[
name|idx
index|]
argument_list|)
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|String
name|query
init|=
literal|"select [jcr:path] from [nt:base] where length([value]) = 100"
decl_stmt|;
name|assertThat
argument_list|(
name|explain
argument_list|(
name|query
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"lucene:length"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|query
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"/test/test100"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|queryXPath
init|=
literal|"/jcr:root//*[fn:string-length(@value) = 10]"
decl_stmt|;
name|assertThat
argument_list|(
name|explainXpath
argument_list|(
name|queryXPath
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"lucene:length"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|queryXPath
argument_list|,
literal|"xpath"
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"/test/test10"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|upperCase
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|luceneIndex
init|=
name|createIndex
argument_list|(
literal|"upper"
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|Tree
name|func
init|=
name|luceneIndex
operator|.
name|addChild
argument_list|(
name|LuceneIndexConstants
operator|.
name|INDEX_RULES
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|addChild
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_NODE
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"upperName"
argument_list|)
decl_stmt|;
name|func
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_FUNCTION
argument_list|,
literal|"fn:upper-case(@name)"
argument_list|)
expr_stmt|;
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
name|test
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:unstructured"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
literal|15
condition|;
name|idx
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
literal|"n"
operator|+
name|idx
argument_list|)
decl_stmt|;
name|a
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:unstructured"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|a
operator|.
name|setProperty
argument_list|(
literal|"name"
argument_list|,
literal|"10% foo"
argument_list|)
expr_stmt|;
name|paths
operator|.
name|add
argument_list|(
literal|"/test/n"
operator|+
name|idx
argument_list|)
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|String
name|query
init|=
literal|"select [jcr:path] from [nt:unstructured] where upper([name]) = '10% FOO'"
decl_stmt|;
name|assertThat
argument_list|(
name|explain
argument_list|(
name|query
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"lucene:upper"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|query
argument_list|,
name|paths
argument_list|)
expr_stmt|;
name|query
operator|=
literal|"select [jcr:path] from [nt:unstructured] where upper([name]) like '10\\% FOO'"
expr_stmt|;
name|assertThat
argument_list|(
name|explain
argument_list|(
name|query
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"lucene:upper"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|query
argument_list|,
name|paths
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|upperCaseRelative
parameter_list|()
throws|throws
name|Exception
block|{
name|Tree
name|luceneIndex
init|=
name|createIndex
argument_list|(
literal|"upper"
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|Tree
name|func
init|=
name|luceneIndex
operator|.
name|addChild
argument_list|(
name|LuceneIndexConstants
operator|.
name|INDEX_RULES
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|addChild
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_NODE
argument_list|)
operator|.
name|addChild
argument_list|(
literal|"upperName"
argument_list|)
decl_stmt|;
name|func
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_FUNCTION
argument_list|,
literal|"upper([data/name])"
argument_list|)
expr_stmt|;
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
name|test
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:unstructured"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
literal|15
condition|;
name|idx
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
literal|"n"
operator|+
name|idx
argument_list|)
decl_stmt|;
name|a
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:unstructured"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|Tree
name|b
init|=
name|a
operator|.
name|addChild
argument_list|(
literal|"data"
argument_list|)
decl_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:unstructured"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
literal|"name"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|paths
operator|.
name|add
argument_list|(
literal|"/test/n"
operator|+
name|idx
argument_list|)
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|String
name|query
init|=
literal|"select [jcr:path] from [nt:unstructured] where upper([data/name]) = 'FOO'"
decl_stmt|;
name|assertThat
argument_list|(
name|explain
argument_list|(
name|query
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"lucene:upper"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|query
argument_list|,
name|paths
argument_list|)
expr_stmt|;
name|String
name|queryXPath
init|=
literal|"/jcr:root//element(*, nt:unstructured)[fn:upper-case(data/@name) = 'FOO']"
decl_stmt|;
name|assertThat
argument_list|(
name|explainXpath
argument_list|(
name|queryXPath
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"lucene:upper"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|queryXPath
argument_list|,
literal|"xpath"
argument_list|,
name|paths
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
literal|15
condition|;
name|idx
operator|++
control|)
block|{
name|Tree
name|a
init|=
name|test
operator|.
name|getChild
argument_list|(
literal|"n"
operator|+
name|idx
argument_list|)
decl_stmt|;
name|Tree
name|b
init|=
name|a
operator|.
name|getChild
argument_list|(
literal|"data"
argument_list|)
decl_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
literal|"name"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
block|}
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|query
operator|=
literal|"select [jcr:path] from [nt:unstructured] where upper([data/name]) = 'BAR'"
expr_stmt|;
name|assertThat
argument_list|(
name|explain
argument_list|(
name|query
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"lucene:upper"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|query
argument_list|,
name|paths
argument_list|)
expr_stmt|;
name|queryXPath
operator|=
literal|"/jcr:root//element(*, nt:unstructured)[fn:upper-case(data/@name) = 'BAR']"
expr_stmt|;
name|assertThat
argument_list|(
name|explainXpath
argument_list|(
name|queryXPath
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"lucene:upper"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
name|queryXPath
argument_list|,
literal|"xpath"
argument_list|,
name|paths
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
name|explain
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|String
name|explain
init|=
literal|"explain "
operator|+
name|query
decl_stmt|;
return|return
name|executeQuery
argument_list|(
name|explain
argument_list|,
literal|"JCR-SQL2"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
specifier|protected
name|String
name|explainXpath
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|ParseException
block|{
name|String
name|explain
init|=
literal|"explain "
operator|+
name|query
decl_stmt|;
name|Result
name|result
init|=
name|executeQuery
argument_list|(
name|explain
argument_list|,
literal|"xpath"
argument_list|,
name|NO_BINDINGS
argument_list|)
decl_stmt|;
name|ResultRow
name|row
init|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|result
operator|.
name|getRows
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|plan
init|=
name|row
operator|.
name|getValue
argument_list|(
literal|"plan"
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
return|return
name|plan
return|;
block|}
specifier|protected
name|Tree
name|createIndex
parameter_list|(
name|String
name|name
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|propNames
parameter_list|)
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
return|return
name|createIndex
argument_list|(
name|index
argument_list|,
name|name
argument_list|,
name|propNames
argument_list|)
return|;
block|}
specifier|static
name|Tree
name|createIndex
parameter_list|(
name|Tree
name|index
parameter_list|,
name|String
name|name
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|propNames
parameter_list|)
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
name|LuceneIndexConstants
operator|.
name|TYPE_LUCENE
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
name|LuceneIndexConstants
operator|.
name|FULL_TEXT_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|def
operator|.
name|setProperty
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|INCLUDE_PROPERTY_NAMES
argument_list|,
name|propNames
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
name|def
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|SAVE_DIR_LISTING
argument_list|,
literal|true
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
block|}
end_class

end_unit

