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
name|solr
operator|.
name|configuration
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ThreadLeakScope
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
name|analysis
operator|.
name|BaseTokenStreamTestCase
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
name|TokenStream
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
name|Tokenizer
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
name|core
operator|.
name|KeywordTokenizer
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
name|miscellaneous
operator|.
name|LengthFilter
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
name|miscellaneous
operator|.
name|RemoveDuplicatesTokenFilter
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
name|path
operator|.
name|PathHierarchyTokenizer
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
name|pattern
operator|.
name|PatternCaptureGroupTokenFilter
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
name|pattern
operator|.
name|PatternReplaceFilter
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
name|reverse
operator|.
name|ReverseStringFilter
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
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
name|runner
operator|.
name|RunWith
import|;
end_import

begin_comment
comment|/**  * Testcase for checking default analyzers configurations behave as expected with regards to path related restrictions  *  * Note that default Solr analyzers for Oak should be equivalent to the ones programmatically defined here.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedRunner
operator|.
name|class
argument_list|)
annotation|@
name|ThreadLeakScope
argument_list|(
name|ThreadLeakScope
operator|.
name|Scope
operator|.
name|NONE
argument_list|)
specifier|public
class|class
name|DefaultAnalyzersConfigurationTest
extends|extends
name|BaseTokenStreamTestCase
block|{
specifier|private
name|Analyzer
name|parentPathIndexingAnalyzer
decl_stmt|;
specifier|private
name|Analyzer
name|parentPathSearchingAnalyzer
decl_stmt|;
specifier|private
name|Analyzer
name|exactPathAnalyzer
decl_stmt|;
specifier|private
name|Analyzer
name|directChildrenPathIndexingAnalyzer
decl_stmt|;
specifier|private
name|Analyzer
name|directChildrenPathSearchingAnalyzer
decl_stmt|;
specifier|private
name|Analyzer
name|allChildrenPathIndexingAnalyzer
decl_stmt|;
specifier|private
name|Analyzer
name|allChildrenPathSearchingAnalyzer
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|this
operator|.
name|exactPathAnalyzer
operator|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|source
init|=
operator|new
name|KeywordTokenizer
argument_list|()
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|)
return|;
block|}
block|}
expr_stmt|;
name|this
operator|.
name|parentPathIndexingAnalyzer
operator|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|source
init|=
operator|new
name|KeywordTokenizer
argument_list|()
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|)
return|;
block|}
block|}
expr_stmt|;
name|this
operator|.
name|parentPathSearchingAnalyzer
operator|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|source
init|=
operator|new
name|KeywordTokenizer
argument_list|()
decl_stmt|;
name|TokenStream
name|filter
init|=
operator|new
name|ReverseStringFilter
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|filter
operator|=
operator|new
name|PatternReplaceFilter
argument_list|(
name|filter
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[^\\/]+\\/"
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|filter
operator|=
operator|new
name|ReverseStringFilter
argument_list|(
name|filter
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|,
name|filter
argument_list|)
return|;
block|}
block|}
expr_stmt|;
name|this
operator|.
name|directChildrenPathIndexingAnalyzer
operator|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|source
init|=
operator|new
name|KeywordTokenizer
argument_list|()
decl_stmt|;
name|TokenStream
name|filter
init|=
operator|new
name|ReverseStringFilter
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|filter
operator|=
operator|new
name|LengthFilter
argument_list|(
name|filter
argument_list|,
literal|2
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|filter
operator|=
operator|new
name|PatternReplaceFilter
argument_list|(
name|filter
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|"([^\\/]+)(\\/)"
argument_list|)
argument_list|,
literal|"$2"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|filter
operator|=
operator|new
name|PatternReplaceFilter
argument_list|(
name|filter
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(\\/)(.+)"
argument_list|)
argument_list|,
literal|"$2"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|filter
operator|=
operator|new
name|ReverseStringFilter
argument_list|(
name|filter
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|,
name|filter
argument_list|)
return|;
block|}
block|}
expr_stmt|;
name|this
operator|.
name|directChildrenPathSearchingAnalyzer
operator|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|source
init|=
operator|new
name|KeywordTokenizer
argument_list|()
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|)
return|;
block|}
block|}
expr_stmt|;
name|this
operator|.
name|allChildrenPathIndexingAnalyzer
operator|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|source
init|=
operator|new
name|PathHierarchyTokenizer
argument_list|()
decl_stmt|;
name|TokenStream
name|filter
init|=
operator|new
name|PatternCaptureGroupTokenFilter
argument_list|(
name|source
argument_list|,
literal|false
argument_list|,
name|Pattern
operator|.
name|compile
argument_list|(
literal|"((\\/).*)"
argument_list|)
argument_list|)
decl_stmt|;
name|filter
operator|=
operator|new
name|RemoveDuplicatesTokenFilter
argument_list|(
name|filter
argument_list|)
expr_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|,
name|filter
argument_list|)
return|;
block|}
block|}
expr_stmt|;
name|this
operator|.
name|allChildrenPathSearchingAnalyzer
operator|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|source
init|=
operator|new
name|KeywordTokenizer
argument_list|()
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|source
argument_list|)
return|;
block|}
block|}
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|this
operator|.
name|exactPathAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|parentPathIndexingAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|parentPathSearchingAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|directChildrenPathIndexingAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|directChildrenPathSearchingAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|allChildrenPathIndexingAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
name|this
operator|.
name|allChildrenPathSearchingAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAllChildrenIndexingTokenization
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|TokenStream
name|ts
init|=
name|allChildrenPathIndexingAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"text"
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"/jcr:a/jcr:b/c/jcr:d"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/jcr:a"
block|,
literal|"/"
block|,
literal|"/jcr:a/jcr:b"
block|,
literal|"/jcr:a/jcr:b/c"
block|,
literal|"/jcr:a/jcr:b/c/jcr:d"
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|allChildrenPathIndexingAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAllChildrenSearchingTokenization
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|TokenStream
name|ts
init|=
name|allChildrenPathSearchingAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"text"
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"/jcr:a/jcr:b/jcr:c"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/jcr:a/jcr:b/jcr:c"
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|allChildrenPathSearchingAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDirectChildrenPathIndexingTokenization
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|TokenStream
name|ts
init|=
name|directChildrenPathIndexingAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"text"
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"/jcr:a/b/jcr:c"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/jcr:a/b"
block|}
argument_list|)
expr_stmt|;
name|ts
operator|=
name|directChildrenPathIndexingAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"text"
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"/jcr:a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/"
block|}
argument_list|)
expr_stmt|;
name|ts
operator|=
name|directChildrenPathIndexingAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"text"
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|directChildrenPathIndexingAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDirectChildrenPathSearchingTokenization
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|TokenStream
name|ts
init|=
name|directChildrenPathSearchingAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"text"
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"/jcr:a/jcr:b"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/jcr:a/jcr:b"
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|directChildrenPathSearchingAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExactPathIndexingTokenizationAndSearch
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|TokenStream
name|ts
init|=
name|exactPathAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"text"
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"/jcr:a/jcr:b/c"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/jcr:a/jcr:b/c"
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|exactPathAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParentPathSearchingTokenization
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|TokenStream
name|ts
init|=
name|parentPathSearchingAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"text"
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"/jcr:a/b/jcr:c"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/jcr:a/b"
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|parentPathSearchingAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParentPathIndexingTokenization
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|TokenStream
name|ts
init|=
name|parentPathIndexingAnalyzer
operator|.
name|tokenStream
argument_list|(
literal|"text"
argument_list|,
operator|new
name|StringReader
argument_list|(
literal|"/a/b"
argument_list|)
argument_list|)
decl_stmt|;
name|assertTokenStreamContents
argument_list|(
name|ts
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/a/b"
block|}
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|parentPathIndexingAnalyzer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Ignore
argument_list|(
literal|"wrong endOffset"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testAllChildrenPathMatching
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|nodePath
init|=
literal|"/jcr:a/jcr:b/c"
decl_stmt|;
name|String
name|descendantPath
init|=
name|nodePath
operator|+
literal|"/d/jcr:e"
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|allChildrenPathIndexingAnalyzer
argument_list|,
name|descendantPath
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/jcr:a"
block|,
literal|"/"
block|,
literal|"/jcr:a/jcr:b"
block|,
literal|"/jcr:a/jcr:b/c"
block|,
literal|"/jcr:a/jcr:b/c/d"
block|,
literal|"/jcr:a/jcr:b/c/d/jcr:e"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|allChildrenPathSearchingAnalyzer
argument_list|,
name|nodePath
argument_list|,
operator|new
name|String
index|[]
block|{
name|nodePath
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|allChildrenPathSearchingAnalyzer
argument_list|,
literal|"/jcr:a"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/jcr:a"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|allChildrenPathSearchingAnalyzer
argument_list|,
literal|"/jcr:a/b"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/jcr:a/b"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|allChildrenPathSearchingAnalyzer
argument_list|,
literal|"/a/b/c"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/a/b/c"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|allChildrenPathSearchingAnalyzer
argument_list|,
literal|"/a/b/c/d"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/a/b/c/d"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|allChildrenPathSearchingAnalyzer
argument_list|,
literal|"/a/b/c/d/jcr:e"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/a/b/c/d/jcr:e"
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|allChildrenPathSearchingAnalyzer
argument_list|,
literal|"/"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"wrong endOffset"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testAllChildrenPathMatchingOnRootNode
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|nodePath
init|=
literal|"/"
decl_stmt|;
name|String
name|descendantPath
init|=
name|nodePath
operator|+
literal|"jcr:a/jcr:b"
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|allChildrenPathIndexingAnalyzer
argument_list|,
name|descendantPath
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"/jcr:a"
block|,
literal|"/"
block|,
literal|"/jcr:a/jcr:b"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDirectChildrenPathMatching
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|nodePath
init|=
literal|"/a/b/c"
decl_stmt|;
name|String
name|childPath
init|=
name|nodePath
operator|+
literal|"/d"
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|directChildrenPathIndexingAnalyzer
argument_list|,
name|childPath
argument_list|,
operator|new
name|String
index|[]
block|{
name|nodePath
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|directChildrenPathSearchingAnalyzer
argument_list|,
name|nodePath
argument_list|,
operator|new
name|String
index|[]
block|{
name|nodePath
block|}
argument_list|)
expr_stmt|;
name|nodePath
operator|=
literal|"/"
expr_stmt|;
name|childPath
operator|=
name|nodePath
operator|+
literal|"/jcr:a"
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|directChildrenPathIndexingAnalyzer
argument_list|,
name|childPath
argument_list|,
operator|new
name|String
index|[]
block|{
name|nodePath
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|directChildrenPathSearchingAnalyzer
argument_list|,
name|nodePath
argument_list|,
operator|new
name|String
index|[]
block|{
name|nodePath
block|}
argument_list|)
expr_stmt|;
name|String
name|childPath1
init|=
literal|"/test/jcr:resource"
decl_stmt|;
name|String
name|childPath2
init|=
literal|"/test/resource"
decl_stmt|;
name|nodePath
operator|=
literal|"/test"
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|directChildrenPathIndexingAnalyzer
argument_list|,
name|childPath1
argument_list|,
operator|new
name|String
index|[]
block|{
name|nodePath
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|directChildrenPathIndexingAnalyzer
argument_list|,
name|childPath2
argument_list|,
operator|new
name|String
index|[]
block|{
name|nodePath
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|directChildrenPathSearchingAnalyzer
argument_list|,
name|nodePath
argument_list|,
operator|new
name|String
index|[]
block|{
name|nodePath
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParentPathMatching
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|parentPath
init|=
literal|"/a/b"
decl_stmt|;
name|String
name|nodePath
init|=
name|parentPath
operator|+
literal|"/jcr:c"
decl_stmt|;
name|assertAnalyzesTo
argument_list|(
name|parentPathIndexingAnalyzer
argument_list|,
name|parentPath
argument_list|,
operator|new
name|String
index|[]
block|{
name|parentPath
block|}
argument_list|)
expr_stmt|;
name|assertAnalyzesTo
argument_list|(
name|parentPathSearchingAnalyzer
argument_list|,
name|nodePath
argument_list|,
operator|new
name|String
index|[]
block|{
name|parentPath
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

