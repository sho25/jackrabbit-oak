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
operator|.
name|util
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
name|Reader
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
name|Nullable
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
name|io
operator|.
name|Closer
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
name|io
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|lucene
operator|.
name|FieldNames
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
name|IndexReader
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
name|spell
operator|.
name|Dictionary
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
name|spell
operator|.
name|LuceneDictionary
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
name|suggest
operator|.
name|Lookup
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
name|suggest
operator|.
name|analyzing
operator|.
name|AnalyzingInfixSuggester
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
name|store
operator|.
name|Directory
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
name|store
operator|.
name|FSDirectory
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
name|util
operator|.
name|Version
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

begin_comment
comment|/**  * Helper class for getting suggest results for a given term, calling a {@link org.apache.lucene.search.suggest.Lookup}  * implementation under the hood.  */
end_comment

begin_class
specifier|public
class|class
name|SuggestHelper
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SuggestHelper
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Analyzer
name|analyzer
init|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Analyzer
operator|.
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Reader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|Analyzer
operator|.
name|TokenStreamComponents
argument_list|(
operator|new
name|CRTokenizer
argument_list|(
name|Version
operator|.
name|LUCENE_47
argument_list|,
name|reader
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|public
specifier|static
name|void
name|updateSuggester
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|Closer
name|closer
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|tempDir
init|=
literal|null
decl_stmt|;
name|boolean
name|shouldCloseDirectory
init|=
literal|true
decl_stmt|;
try|try
block|{
comment|//Analyzing infix suggester takes a file parameter. It uses its path to getDirectory()
comment|//for actual storage of suggester data. BUT, while building it also does getDirectory() to
comment|//a temporary location (original path + ".tmp"). So, instead we create a temp dir and also
comment|//create a placeholder non-existing-sub-child which would mark the location when we want to return
comment|//our internal suggestion OakDirectory. After build is done, we'd delete the temp directory
comment|//thereby removing any temp stuff that suggester created in the interim.
name|tempDir
operator|=
name|Files
operator|.
name|createTempDir
argument_list|()
expr_stmt|;
name|File
name|tempSubChild
init|=
operator|new
name|File
argument_list|(
name|tempDir
argument_list|,
literal|"non-existing-sub-child"
argument_list|)
decl_stmt|;
if|if
condition|(
name|reader
operator|.
name|getDocCount
argument_list|(
name|FieldNames
operator|.
name|SUGGEST
argument_list|)
operator|>
literal|0
condition|)
block|{
name|Dictionary
name|dictionary
init|=
operator|new
name|LuceneDictionary
argument_list|(
name|reader
argument_list|,
name|FieldNames
operator|.
name|SUGGEST
argument_list|)
decl_stmt|;
name|AnalyzingInfixSuggester
name|suggester
init|=
name|closer
operator|.
name|register
argument_list|(
name|getLookup
argument_list|(
name|directory
argument_list|,
name|analyzer
argument_list|,
name|tempSubChild
argument_list|)
argument_list|)
decl_stmt|;
name|shouldCloseDirectory
operator|=
literal|false
expr_stmt|;
name|suggester
operator|.
name|build
argument_list|(
name|dictionary
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"could not update the suggester"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|shouldCloseDirectory
condition|)
block|{
name|closer
operator|.
name|register
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
comment|//cleanup temp dir
if|if
condition|(
name|tempDir
operator|!=
literal|null
operator|&&
operator|!
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|tempDir
argument_list|)
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cleanup failed for temp dir {}"
argument_list|,
name|tempDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|static
name|List
argument_list|<
name|Lookup
operator|.
name|LookupResult
argument_list|>
name|getSuggestions
parameter_list|(
name|AnalyzingInfixSuggester
name|suggester
parameter_list|,
annotation|@
name|Nullable
name|SuggestQuery
name|suggestQuery
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|suggester
operator|!=
literal|null
operator|&&
name|suggester
operator|.
name|getCount
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|suggester
operator|.
name|lookup
argument_list|(
name|suggestQuery
operator|.
name|getText
argument_list|()
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"could not handle Suggest query "
operator|+
name|suggestQuery
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
name|SuggestQuery
name|getSuggestQuery
parameter_list|(
name|String
name|suggestQueryString
parameter_list|)
block|{
try|try
block|{
name|String
name|text
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|param
range|:
name|suggestQueryString
operator|.
name|split
argument_list|(
literal|"&"
argument_list|)
control|)
block|{
name|String
index|[]
name|keyValuePair
init|=
name|param
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
if|if
condition|(
name|keyValuePair
operator|.
name|length
operator|!=
literal|2
operator|||
name|keyValuePair
index|[
literal|0
index|]
operator|==
literal|null
operator|||
name|keyValuePair
index|[
literal|1
index|]
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unparsable native Lucene Suggest query: "
operator|+
name|suggestQueryString
argument_list|)
throw|;
block|}
else|else
block|{
if|if
condition|(
literal|"term"
operator|.
name|equals
argument_list|(
name|keyValuePair
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
name|text
operator|=
name|keyValuePair
index|[
literal|1
index|]
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|text
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|SuggestQuery
argument_list|(
name|text
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"could not build SuggestQuery "
operator|+
name|suggestQueryString
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
name|AnalyzingInfixSuggester
name|getLookup
parameter_list|(
specifier|final
name|Directory
name|suggestDirectory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getLookup
argument_list|(
name|suggestDirectory
argument_list|,
name|SuggestHelper
operator|.
name|analyzer
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|AnalyzingInfixSuggester
name|getLookup
parameter_list|(
specifier|final
name|Directory
name|suggestDirectory
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getLookup
argument_list|(
name|suggestDirectory
argument_list|,
name|analyzer
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|AnalyzingInfixSuggester
name|getLookup
parameter_list|(
specifier|final
name|Directory
name|suggestDirectory
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
specifier|final
name|File
name|tempDir
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|AnalyzingInfixSuggester
argument_list|(
name|Version
operator|.
name|LUCENE_47
argument_list|,
name|tempDir
argument_list|,
name|analyzer
argument_list|,
name|analyzer
argument_list|,
literal|3
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Directory
name|getDirectory
parameter_list|(
name|File
name|path
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|tempDir
operator|==
literal|null
operator|||
name|tempDir
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|equals
argument_list|(
name|path
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|suggestDirectory
return|;
comment|// use oak directory for writing suggest index
block|}
else|else
block|{
return|return
name|FSDirectory
operator|.
name|open
argument_list|(
name|path
argument_list|)
return|;
comment|// use FS for temp index used at build time
block|}
block|}
block|}
return|;
block|}
specifier|public
specifier|static
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|analyzer
return|;
block|}
specifier|public
specifier|static
class|class
name|SuggestQuery
block|{
specifier|private
specifier|final
name|String
name|text
decl_stmt|;
specifier|public
name|SuggestQuery
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
block|}
specifier|public
name|String
name|getText
parameter_list|()
block|{
return|return
name|text
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SuggestQuery{"
operator|+
literal|"text='"
operator|+
name|text
operator|+
literal|'\''
operator|+
literal|'}'
return|;
block|}
block|}
block|}
end_class

end_unit

