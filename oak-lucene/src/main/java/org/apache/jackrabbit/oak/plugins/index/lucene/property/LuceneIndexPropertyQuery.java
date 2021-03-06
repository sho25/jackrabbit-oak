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
name|property
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
name|IndexTracker
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
name|LuceneIndexNode
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
name|util
operator|.
name|PathStoredFieldVisitor
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
comment|/**  * Performs simple property=value query against a Lucene index  */
end_comment

begin_class
specifier|public
class|class
name|LuceneIndexPropertyQuery
implements|implements
name|PropertyQuery
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
name|LuceneIndexPropertyQuery
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|IndexTracker
name|tracker
decl_stmt|;
specifier|private
specifier|final
name|String
name|indexPath
decl_stmt|;
specifier|public
name|LuceneIndexPropertyQuery
parameter_list|(
name|IndexTracker
name|tracker
parameter_list|,
name|String
name|indexPath
parameter_list|)
block|{
name|this
operator|.
name|tracker
operator|=
name|tracker
expr_stmt|;
name|this
operator|.
name|indexPath
operator|=
name|indexPath
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getIndexedPaths
parameter_list|(
name|String
name|propertyRelativePath
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|indexPaths
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|LuceneIndexNode
name|indexNode
init|=
name|tracker
operator|.
name|acquireIndexNode
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexNode
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|TermQuery
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|propertyRelativePath
argument_list|,
name|value
argument_list|)
argument_list|)
decl_stmt|;
comment|//By design such query should not result in more than 1 result.
comment|//So just use 10 as batch size
name|TopDocs
name|docs
init|=
name|indexNode
operator|.
name|getSearcher
argument_list|()
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|IndexReader
name|reader
init|=
name|indexNode
operator|.
name|getSearcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|d
range|:
name|docs
operator|.
name|scoreDocs
control|)
block|{
name|PathStoredFieldVisitor
name|visitor
init|=
operator|new
name|PathStoredFieldVisitor
argument_list|()
decl_stmt|;
name|reader
operator|.
name|document
argument_list|(
name|d
operator|.
name|doc
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
name|indexPaths
operator|.
name|add
argument_list|(
name|visitor
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error occurred while checking index {} for unique value "
operator|+
literal|"[{}] for [{}]"
argument_list|,
name|indexPath
argument_list|,
name|value
argument_list|,
name|propertyRelativePath
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|indexNode
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|indexPaths
return|;
block|}
block|}
end_class

end_unit

