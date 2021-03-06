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
name|lucene
operator|.
name|util
operator|.
name|fv
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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|search
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
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|search
operator|.
name|PropertyDefinition
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|document
operator|.
name|Document
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
name|search
operator|.
name|BooleanClause
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
name|BooleanQuery
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
name|ConstantScoreQuery
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
name|ScoreDoc
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
import|;
end_import

begin_comment
comment|/**  * Utility methods for indexing and searching for similar feature vectors  */
end_comment

begin_class
specifier|public
class|class
name|SimSearchUtils
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
name|SimSearchUtils
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|String
name|toDoubleString
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|double
index|[]
name|a
init|=
name|toDoubleArray
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Double
name|d
range|:
name|a
control|)
block|{
if|if
condition|(
name|builder
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|List
argument_list|<
name|Double
argument_list|>
name|toDoubles
parameter_list|(
name|byte
index|[]
name|array
parameter_list|)
block|{
name|int
name|blockSize
init|=
name|Double
operator|.
name|SIZE
operator|/
name|Byte
operator|.
name|SIZE
decl_stmt|;
name|ByteBuffer
name|wrap
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|array
argument_list|)
decl_stmt|;
name|int
name|capacity
init|=
name|array
operator|.
name|length
operator|/
name|blockSize
decl_stmt|;
name|List
argument_list|<
name|Double
argument_list|>
name|doubles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|capacity
argument_list|)
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
name|capacity
condition|;
name|i
operator|++
control|)
block|{
name|double
name|e
init|=
name|wrap
operator|.
name|getDouble
argument_list|(
name|i
operator|*
name|blockSize
argument_list|)
decl_stmt|;
name|doubles
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|doubles
return|;
block|}
specifier|private
specifier|static
name|double
index|[]
name|toDoubleArray
parameter_list|(
name|byte
index|[]
name|array
parameter_list|)
block|{
name|int
name|blockSize
init|=
name|Double
operator|.
name|SIZE
operator|/
name|Byte
operator|.
name|SIZE
decl_stmt|;
name|ByteBuffer
name|wrap
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|array
argument_list|)
decl_stmt|;
name|int
name|capacity
init|=
name|array
operator|.
name|length
operator|/
name|blockSize
decl_stmt|;
name|double
index|[]
name|doubles
init|=
operator|new
name|double
index|[
name|capacity
index|]
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
name|capacity
condition|;
name|i
operator|++
control|)
block|{
name|double
name|e
init|=
name|wrap
operator|.
name|getDouble
argument_list|(
name|i
operator|*
name|blockSize
argument_list|)
decl_stmt|;
name|doubles
index|[
name|i
index|]
operator|=
name|e
expr_stmt|;
block|}
return|return
name|doubles
return|;
block|}
specifier|private
specifier|static
name|Collection
argument_list|<
name|BytesRef
argument_list|>
name|getTokens
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|field
parameter_list|,
name|String
name|sampleTextString
parameter_list|)
throws|throws
name|IOException
block|{
name|Collection
argument_list|<
name|BytesRef
argument_list|>
name|tokens
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
name|sampleTextString
argument_list|)
decl_stmt|;
name|ts
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|ts
operator|.
name|reset
argument_list|()
expr_stmt|;
while|while
condition|(
name|ts
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|CharTermAttribute
name|charTermAttribute
init|=
name|ts
operator|.
name|getAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|token
init|=
operator|new
name|String
argument_list|(
name|charTermAttribute
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|charTermAttribute
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|tokens
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|token
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ts
operator|.
name|end
argument_list|()
expr_stmt|;
name|ts
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|tokens
return|;
block|}
specifier|static
name|Query
name|getSimQuery
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|createLSHQuery
argument_list|(
name|fieldName
argument_list|,
name|getTokens
argument_list|(
name|analyzer
argument_list|,
name|fieldName
argument_list|,
name|text
argument_list|)
argument_list|,
literal|1f
argument_list|,
literal|1f
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|byte
index|[]
name|toByteArray
parameter_list|(
name|List
argument_list|<
name|Double
argument_list|>
name|values
parameter_list|)
block|{
name|int
name|blockSize
init|=
name|Double
operator|.
name|SIZE
operator|/
name|Byte
operator|.
name|SIZE
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|values
operator|.
name|size
argument_list|()
operator|*
name|blockSize
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|j
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
operator|,
name|j
operator|+=
name|blockSize
control|)
block|{
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|bytes
argument_list|,
name|j
argument_list|,
name|blockSize
argument_list|)
operator|.
name|putDouble
argument_list|(
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|bytes
return|;
block|}
specifier|public
specifier|static
name|byte
index|[]
name|toByteArray
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|List
argument_list|<
name|Double
argument_list|>
name|doubles
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|dv
range|:
name|value
operator|.
name|split
argument_list|(
literal|","
argument_list|)
control|)
block|{
name|doubles
operator|.
name|add
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|dv
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|toByteArray
argument_list|(
name|doubles
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Query
name|getSimilarityQuery
parameter_list|(
name|List
argument_list|<
name|PropertyDefinition
argument_list|>
name|sp
parameter_list|,
name|IndexReader
name|reader
parameter_list|,
name|String
name|queryString
parameter_list|)
block|{
try|try
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"parsing similarity query on {}"
argument_list|,
name|queryString
argument_list|)
expr_stmt|;
name|Query
name|similarityQuery
init|=
literal|null
decl_stmt|;
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
name|queryString
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
literal|"Unparsable native Lucene query for fv similarity: "
operator|+
name|queryString
argument_list|)
throw|;
block|}
else|else
block|{
if|if
condition|(
literal|"stream.body"
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
break|break;
block|}
block|}
block|}
if|if
condition|(
name|text
operator|!=
literal|null
operator|&&
operator|!
name|sp
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"generating similarity query for {}"
argument_list|,
name|text
argument_list|)
expr_stmt|;
name|BooleanQuery
name|booleanQuery
init|=
operator|new
name|BooleanQuery
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|LSHAnalyzer
name|analyzer
init|=
operator|new
name|LSHAnalyzer
argument_list|()
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|TermQuery
name|q
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FieldNames
operator|.
name|PATH
argument_list|,
name|text
argument_list|)
argument_list|)
decl_stmt|;
name|TopDocs
name|top
init|=
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|top
operator|.
name|totalHits
operator|>
literal|0
condition|)
block|{
name|ScoreDoc
name|d
init|=
name|top
operator|.
name|scoreDocs
index|[
literal|0
index|]
decl_stmt|;
name|Document
name|doc
init|=
name|reader
operator|.
name|document
argument_list|(
name|d
operator|.
name|doc
argument_list|)
decl_stmt|;
for|for
control|(
name|PropertyDefinition
name|pd
range|:
name|sp
control|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"adding similarity clause for property {}"
argument_list|,
name|pd
operator|.
name|name
argument_list|)
expr_stmt|;
name|String
name|similarityFieldName
init|=
name|FieldNames
operator|.
name|createSimilarityFieldName
argument_list|(
name|pd
operator|.
name|name
argument_list|)
decl_stmt|;
name|String
name|fvString
init|=
name|doc
operator|.
name|get
argument_list|(
name|similarityFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fvString
operator|!=
literal|null
operator|&&
name|fvString
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|log
operator|.
name|trace
argument_list|(
literal|"generating sim query on field {} and text {}"
argument_list|,
name|similarityFieldName
argument_list|,
name|fvString
argument_list|)
expr_stmt|;
name|Query
name|simQuery
init|=
name|SimSearchUtils
operator|.
name|getSimQuery
argument_list|(
name|analyzer
argument_list|,
name|similarityFieldName
argument_list|,
name|fvString
argument_list|)
decl_stmt|;
name|booleanQuery
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
name|simQuery
argument_list|,
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|binaryTags
init|=
name|doc
operator|.
name|getValues
argument_list|(
name|FieldNames
operator|.
name|SIMILARITY_TAGS
argument_list|)
decl_stmt|;
if|if
condition|(
name|binaryTags
operator|!=
literal|null
operator|&&
name|binaryTags
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|BooleanQuery
name|tagQuery
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|brt
range|:
name|binaryTags
control|)
block|{
name|tagQuery
operator|.
name|add
argument_list|(
operator|new
name|BooleanClause
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|FieldNames
operator|.
name|SIMILARITY_TAGS
argument_list|,
name|brt
argument_list|)
argument_list|)
argument_list|,
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|tagQuery
operator|.
name|setBoost
argument_list|(
literal|0.5f
argument_list|)
expr_stmt|;
name|booleanQuery
operator|.
name|add
argument_list|(
name|tagQuery
argument_list|,
name|SHOULD
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|trace
argument_list|(
literal|"similarity query generated for {}"
argument_list|,
name|pd
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"could not create query for similarity field {}"
argument_list|,
name|similarityFieldName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|booleanQuery
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|similarityQuery
operator|=
name|booleanQuery
expr_stmt|;
name|log
operator|.
name|trace
argument_list|(
literal|"final similarity query is {}"
argument_list|,
name|similarityQuery
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|similarityQuery
return|;
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
literal|"could not handle similarity query "
operator|+
name|queryString
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|Query
name|createLSHQuery
parameter_list|(
name|String
name|field
parameter_list|,
name|Collection
argument_list|<
name|BytesRef
argument_list|>
name|minhashes
parameter_list|,
name|float
name|similarity
parameter_list|,
name|float
name|expectedTruePositive
parameter_list|)
block|{
name|int
name|bandSize
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|expectedTruePositive
operator|<
literal|1
condition|)
block|{
name|bandSize
operator|=
name|computeBandSize
argument_list|(
name|minhashes
operator|.
name|size
argument_list|()
argument_list|,
name|similarity
argument_list|,
name|expectedTruePositive
argument_list|)
expr_stmt|;
block|}
name|BooleanQuery
name|builder
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|BooleanQuery
name|childBuilder
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|int
name|rowInBand
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BytesRef
name|minHash
range|:
name|minhashes
control|)
block|{
name|TermQuery
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|minHash
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|bandSize
operator|==
literal|1
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|tq
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|childBuilder
operator|.
name|add
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|tq
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|rowInBand
operator|++
expr_stmt|;
if|if
condition|(
name|rowInBand
operator|==
name|bandSize
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|childBuilder
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|childBuilder
operator|=
operator|new
name|BooleanQuery
argument_list|()
expr_stmt|;
name|rowInBand
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
comment|// Avoid a dubious narrow band, wrap around and pad with the start
if|if
condition|(
name|childBuilder
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|BytesRef
name|token
range|:
name|minhashes
control|)
block|{
name|TermQuery
name|tq
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|token
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|childBuilder
operator|.
name|add
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|tq
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
name|rowInBand
operator|++
expr_stmt|;
if|if
condition|(
name|rowInBand
operator|==
name|bandSize
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|childBuilder
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|expectedTruePositive
operator|>=
literal|1.0
operator|&&
name|similarity
operator|<
literal|1
condition|)
block|{
name|builder
operator|.
name|setMinimumNumberShouldMatch
argument_list|(
call|(
name|int
call|)
argument_list|(
name|Math
operator|.
name|ceil
argument_list|(
name|minhashes
operator|.
name|size
argument_list|()
operator|*
name|similarity
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|trace
argument_list|(
literal|"similarity query with bands : {}, minShouldMatch : {}, no. of clauses : {}"
argument_list|,
name|bandSize
argument_list|,
name|builder
operator|.
name|getMinimumNumberShouldMatch
argument_list|()
argument_list|,
name|builder
operator|.
name|clauses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
specifier|private
specifier|static
name|int
name|computeBandSize
parameter_list|(
name|int
name|numHash
parameter_list|,
name|double
name|similarity
parameter_list|,
name|double
name|expectedTruePositive
parameter_list|)
block|{
for|for
control|(
name|int
name|bands
init|=
literal|1
init|;
name|bands
operator|<=
name|numHash
condition|;
name|bands
operator|++
control|)
block|{
name|int
name|rowsInBand
init|=
name|numHash
operator|/
name|bands
decl_stmt|;
name|double
name|truePositive
init|=
literal|1
operator|-
name|Math
operator|.
name|pow
argument_list|(
literal|1
operator|-
name|Math
operator|.
name|pow
argument_list|(
name|similarity
argument_list|,
name|rowsInBand
argument_list|)
argument_list|,
name|bands
argument_list|)
decl_stmt|;
if|if
condition|(
name|truePositive
operator|>
name|expectedTruePositive
condition|)
block|{
return|return
name|rowsInBand
return|;
block|}
block|}
return|return
literal|1
return|;
block|}
specifier|public
specifier|static
name|void
name|bruteForceFVRerank
parameter_list|(
name|List
argument_list|<
name|PropertyDefinition
argument_list|>
name|sp
parameter_list|,
name|TopDocs
name|docs
parameter_list|,
name|IndexSearcher
name|indexSearcher
parameter_list|)
throws|throws
name|IOException
block|{
name|double
name|distSum
init|=
literal|0d
decl_stmt|;
name|double
name|counter
init|=
literal|0d
decl_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|Double
argument_list|>
name|distances
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|k
init|=
literal|15
decl_stmt|;
name|ScoreDoc
name|inputDoc
init|=
name|docs
operator|.
name|scoreDocs
index|[
literal|0
index|]
decl_stmt|;
comment|// we assume the input doc is the first one returned
name|List
argument_list|<
name|Integer
argument_list|>
name|toDiscard
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|PropertyDefinition
name|pd
range|:
name|sp
control|)
block|{
name|String
name|fieldName
init|=
name|FieldNames
operator|.
name|createBinSimilarityFieldName
argument_list|(
name|pd
operator|.
name|name
argument_list|)
decl_stmt|;
name|BytesRef
name|binaryValue
init|=
name|indexSearcher
operator|.
name|doc
argument_list|(
name|inputDoc
operator|.
name|doc
argument_list|)
operator|.
name|getBinaryValue
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|binaryValue
operator|!=
literal|null
condition|)
block|{
name|double
index|[]
name|inputVector
init|=
name|toDoubleArray
argument_list|(
name|binaryValue
operator|.
name|bytes
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
name|docs
operator|.
name|scoreDocs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|BytesRef
name|featureVectorBinary
init|=
name|indexSearcher
operator|.
name|doc
argument_list|(
name|docs
operator|.
name|scoreDocs
index|[
name|j
index|]
operator|.
name|doc
argument_list|)
operator|.
name|getBinaryValue
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|featureVectorBinary
operator|!=
literal|null
condition|)
block|{
name|double
index|[]
name|currentVector
init|=
name|toDoubleArray
argument_list|(
name|featureVectorBinary
operator|.
name|bytes
argument_list|)
decl_stmt|;
name|double
name|distance
init|=
name|dist
argument_list|(
name|inputVector
argument_list|,
name|currentVector
argument_list|)
operator|+
literal|1e-10
decl_stmt|;
comment|// constant term to avoid division by zero
if|if
condition|(
name|Double
operator|.
name|isNaN
argument_list|(
name|distance
argument_list|)
operator|||
name|Double
operator|.
name|isInfinite
argument_list|(
name|distance
argument_list|)
condition|)
block|{
name|toDiscard
operator|.
name|add
argument_list|(
name|docs
operator|.
name|scoreDocs
index|[
name|j
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|distSum
operator|+=
name|distance
expr_stmt|;
name|counter
operator|++
expr_stmt|;
name|distances
operator|.
name|put
argument_list|(
name|docs
operator|.
name|scoreDocs
index|[
name|j
index|]
operator|.
name|doc
argument_list|,
name|distance
argument_list|)
expr_stmt|;
name|docs
operator|.
name|scoreDocs
index|[
name|j
index|]
operator|.
name|score
operator|=
call|(
name|float
call|)
argument_list|(
literal|1d
operator|/
name|distance
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|// remove docs having invalid distance
if|if
condition|(
operator|!
name|toDiscard
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|docs
operator|.
name|scoreDocs
operator|=
name|Arrays
operator|.
name|stream
argument_list|(
name|docs
operator|.
name|scoreDocs
argument_list|)
operator|.
name|filter
argument_list|(
name|e
lambda|->
operator|!
name|toDiscard
operator|.
name|contains
argument_list|(
name|e
operator|.
name|doc
argument_list|)
argument_list|)
operator|.
name|toArray
argument_list|(
name|ScoreDoc
index|[]
operator|::
operator|new
argument_list|)
expr_stmt|;
block|}
comment|// remove docs whose distance is one order of magnitude higher than average distance
specifier|final
name|double
name|distanceThreshold
init|=
literal|10
operator|*
name|distSum
operator|/
name|counter
decl_stmt|;
name|docs
operator|.
name|scoreDocs
operator|=
name|Arrays
operator|.
name|stream
argument_list|(
name|docs
operator|.
name|scoreDocs
argument_list|)
operator|.
name|filter
argument_list|(
name|e
lambda|->
name|distances
operator|.
name|containsKey
argument_list|(
name|e
operator|.
name|doc
argument_list|)
operator|&&
name|distances
operator|.
name|get
argument_list|(
name|e
operator|.
name|doc
argument_list|)
operator|<
name|distanceThreshold
argument_list|)
operator|.
name|toArray
argument_list|(
name|ScoreDoc
index|[]
operator|::
operator|new
argument_list|)
expr_stmt|;
comment|// rerank scoreDocs
name|Arrays
operator|.
name|parallelSort
argument_list|(
name|docs
operator|.
name|scoreDocs
argument_list|,
literal|0
argument_list|,
name|docs
operator|.
name|scoreDocs
operator|.
name|length
argument_list|,
parameter_list|(
name|o1
parameter_list|,
name|o2
parameter_list|)
lambda|->
block|{
return|return
operator|-
literal|1
operator|*
name|Double
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|score
argument_list|,
name|o2
operator|.
name|score
argument_list|)
return|;
block|}
argument_list|)
expr_stmt|;
comment|// retain only the top k nearest neighbours
if|if
condition|(
name|docs
operator|.
name|scoreDocs
operator|.
name|length
operator|>
name|k
condition|)
block|{
name|docs
operator|.
name|scoreDocs
operator|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|docs
operator|.
name|scoreDocs
argument_list|,
literal|0
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|docs
operator|.
name|scoreDocs
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|docs
operator|.
name|setMaxScore
argument_list|(
name|docs
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|score
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|double
name|dist
parameter_list|(
name|double
index|[]
name|x
parameter_list|,
name|double
index|[]
name|y
parameter_list|)
block|{
comment|// euclidean distance
name|double
name|d
init|=
literal|0
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
name|x
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|d
operator|+=
name|Math
operator|.
name|pow
argument_list|(
name|y
index|[
name|i
index|]
operator|-
name|x
index|[
name|i
index|]
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
return|return
name|Math
operator|.
name|sqrt
argument_list|(
name|d
argument_list|)
return|;
block|}
block|}
end_class

end_unit

