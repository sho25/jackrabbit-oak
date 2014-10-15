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
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
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
name|apache
operator|.
name|jackrabbit
operator|.
name|test
operator|.
name|ISO8601
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|classic
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|classic
operator|.
name|QueryParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|IndexSearcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|NumericRangeQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|TermQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|TopDocs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
operator|.
name|TYPENAME_STRING
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
name|lucene
operator|.
name|FieldNames
operator|.
name|PATH
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
name|LuceneIndexConstants
operator|.
name|INCLUDE_PROPERTY_NAMES
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
name|LuceneIndexConstants
operator|.
name|VERSION
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
name|util
operator|.
name|LuceneIndexHelper
operator|.
name|newLuceneIndexDefinition
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
operator|.
name|INITIAL_CONTENT
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
name|assertNull
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

begin_class
specifier|public
class|class
name|LuceneIndexEditorTest
block|{
specifier|private
specifier|static
specifier|final
name|Analyzer
name|analyzer
init|=
name|LuceneIndexConstants
operator|.
name|ANALYZER
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|EditorHook
name|HOOK
init|=
operator|new
name|EditorHook
argument_list|(
operator|new
name|IndexUpdateProvider
argument_list|(
operator|new
name|LuceneIndexEditorProvider
argument_list|()
operator|.
name|with
argument_list|(
name|analyzer
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|NodeState
name|root
init|=
name|INITIAL_CONTENT
decl_stmt|;
specifier|private
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|private
name|IndexTracker
name|tracker
init|=
operator|new
name|IndexTracker
argument_list|()
decl_stmt|;
specifier|private
name|IndexNode
name|indexNode
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testLuceneWithFullText
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|index
init|=
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
decl_stmt|;
name|newLuceneIndexDefinition
argument_list|(
name|index
argument_list|,
literal|"lucene"
argument_list|,
name|of
argument_list|(
name|TYPENAME_STRING
argument_list|)
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
literal|"test"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"fox is jumping"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"price"
argument_list|,
literal|100
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
name|NodeState
name|indexed
init|=
name|HOOK
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
decl_stmt|;
name|tracker
operator|.
name|update
argument_list|(
name|indexed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|query
argument_list|(
literal|"foo:fox"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Non string properties not indexed by default"
argument_list|,
name|getPath
argument_list|(
name|NumericRangeQuery
operator|.
name|newLongRange
argument_list|(
literal|"price"
argument_list|,
literal|100L
argument_list|,
literal|100L
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLuceneWithNonFullText
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|index
init|=
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
decl_stmt|;
name|NodeBuilder
name|nb
init|=
name|newLuceneIndexDefinition
argument_list|(
name|index
argument_list|,
literal|"lucene"
argument_list|,
name|of
argument_list|(
name|TYPENAME_STRING
argument_list|)
argument_list|)
decl_stmt|;
name|nb
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
name|nb
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|INCLUDE_PROPERTY_NAMES
argument_list|,
name|of
argument_list|(
literal|"foo"
argument_list|,
literal|"price"
argument_list|,
literal|"weight"
argument_list|,
literal|"bool"
argument_list|,
literal|"creationTime"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
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
literal|"test"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"fox is jumping"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"bar"
argument_list|,
literal|"kite is flying"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"price"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"weight"
argument_list|,
literal|10.0
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"bool"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"truth"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"creationTime"
argument_list|,
name|createCal
argument_list|(
literal|"05/06/2014"
argument_list|)
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
name|NodeState
name|indexed
init|=
name|HOOK
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
decl_stmt|;
name|tracker
operator|.
name|update
argument_list|(
name|indexed
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"Fulltext search should not work"
argument_list|,
name|query
argument_list|(
literal|"foo:fox"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|getPath
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"foo"
argument_list|,
literal|"fox is jumping"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"bar must NOT be indexed"
argument_list|,
name|getPath
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"bar"
argument_list|,
literal|"kite is flying"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//Long
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|getPath
argument_list|(
name|NumericRangeQuery
operator|.
name|newDoubleRange
argument_list|(
literal|"weight"
argument_list|,
literal|8D
argument_list|,
literal|12D
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//Double
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|getPath
argument_list|(
name|NumericRangeQuery
operator|.
name|newLongRange
argument_list|(
literal|"price"
argument_list|,
literal|100L
argument_list|,
literal|100L
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//Boolean
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|getPath
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"bool"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"truth must NOT be indexed"
argument_list|,
name|getPath
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"truth"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//Date
name|assertEquals
argument_list|(
literal|"/test"
argument_list|,
name|getPath
argument_list|(
name|NumericRangeQuery
operator|.
name|newLongRange
argument_list|(
literal|"creationTime"
argument_list|,
name|dateToTime
argument_list|(
literal|"05/05/2014"
argument_list|)
argument_list|,
name|dateToTime
argument_list|(
literal|"05/07/2014"
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|noOfDocsIndexedNonFullText
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|index
init|=
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
decl_stmt|;
name|NodeBuilder
name|nb
init|=
name|newLuceneIndexDefinition
argument_list|(
name|index
argument_list|,
literal|"lucene"
argument_list|,
name|of
argument_list|(
name|TYPENAME_STRING
argument_list|)
argument_list|)
decl_stmt|;
name|nb
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
name|nb
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|INCLUDE_PROPERTY_NAMES
argument_list|,
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
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
literal|"test"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"fox is jumping"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"bar"
argument_list|,
literal|"kite is flying"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"test3"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"wind is blowing"
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
name|NodeState
name|indexed
init|=
name|HOOK
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
decl_stmt|;
name|tracker
operator|.
name|update
argument_list|(
name|indexed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|getSearcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|releaseIndexNode
parameter_list|()
block|{
if|if
condition|(
name|indexNode
operator|!=
literal|null
condition|)
block|{
name|indexNode
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|query
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|QueryParser
name|queryParser
init|=
operator|new
name|QueryParser
argument_list|(
name|VERSION
argument_list|,
literal|""
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
return|return
name|getPath
argument_list|(
name|queryParser
operator|.
name|parse
argument_list|(
name|query
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|String
name|getPath
parameter_list|(
name|Query
name|query
parameter_list|)
throws|throws
name|IOException
block|{
name|TopDocs
name|td
init|=
name|getSearcher
argument_list|()
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|100
argument_list|)
decl_stmt|;
if|if
condition|(
name|td
operator|.
name|totalHits
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|td
operator|.
name|totalHits
operator|>
literal|1
condition|)
block|{
name|fail
argument_list|(
literal|"More than 1 result found for query "
operator|+
name|query
argument_list|)
expr_stmt|;
block|}
return|return
name|getSearcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
operator|.
name|document
argument_list|(
name|td
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
operator|.
name|get
argument_list|(
name|PATH
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|IndexSearcher
name|getSearcher
parameter_list|()
block|{
if|if
condition|(
name|indexNode
operator|==
literal|null
condition|)
block|{
name|indexNode
operator|=
name|tracker
operator|.
name|acquireIndexNode
argument_list|(
literal|"/oak:index/lucene"
argument_list|)
expr_stmt|;
block|}
return|return
name|indexNode
operator|.
name|getSearcher
argument_list|()
return|;
block|}
specifier|static
name|Calendar
name|createCal
parameter_list|(
name|String
name|dt
parameter_list|)
throws|throws
name|java
operator|.
name|text
operator|.
name|ParseException
block|{
name|SimpleDateFormat
name|sdf
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"dd/MM/yyyy"
argument_list|)
decl_stmt|;
name|Calendar
name|cal
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|cal
operator|.
name|setTime
argument_list|(
name|sdf
operator|.
name|parse
argument_list|(
name|dt
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|cal
return|;
block|}
specifier|static
name|long
name|dateToTime
parameter_list|(
name|String
name|dt
parameter_list|)
throws|throws
name|java
operator|.
name|text
operator|.
name|ParseException
block|{
return|return
name|FieldFactory
operator|.
name|dateToLong
argument_list|(
name|ISO8601
operator|.
name|format
argument_list|(
name|createCal
argument_list|(
name|dt
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

