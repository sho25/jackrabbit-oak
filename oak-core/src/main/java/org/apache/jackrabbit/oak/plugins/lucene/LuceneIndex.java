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
name|plugins
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
name|lucene
operator|.
name|FieldNames
operator|.
name|PATH_SELECTOR
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
name|lucene
operator|.
name|TermFactory
operator|.
name|newPathTerm
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
name|Collection
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
name|Iterator
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
name|query
operator|.
name|index
operator|.
name|IndexRowImpl
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
name|Filter
operator|.
name|PropertyRestriction
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
name|lucene
operator|.
name|index
operator|.
name|DirectoryReader
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
operator|.
name|Occur
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
name|PrefixQuery
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
name|TermRangeQuery
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
name|store
operator|.
name|Directory
import|;
end_import

begin_comment
comment|/**  * This index uses internally runs a query against a Lucene index.  */
end_comment

begin_class
specifier|public
class|class
name|LuceneIndex
implements|implements
name|QueryIndex
block|{
specifier|private
specifier|final
name|NodeStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|LuceneIndexInfo
name|index
decl_stmt|;
specifier|public
name|LuceneIndex
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|LuceneIndexInfo
name|index
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getIndexName
parameter_list|()
block|{
return|return
name|index
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getCost
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
return|return
literal|1.0
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPlan
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
return|return
name|getQuery
argument_list|(
name|filter
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Cursor
name|query
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|String
name|revisionId
parameter_list|,
name|NodeState
name|root
parameter_list|)
block|{
try|try
block|{
name|Directory
name|directory
init|=
operator|new
name|OakDirectory
argument_list|(
name|store
argument_list|,
name|root
argument_list|,
name|index
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
try|try
block|{
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Query
name|query
init|=
name|getQuery
argument_list|(
name|filter
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
name|TopDocs
name|docs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|doc
range|:
name|docs
operator|.
name|scoreDocs
control|)
block|{
name|String
name|path
init|=
name|reader
operator|.
name|document
argument_list|(
name|doc
operator|.
name|doc
argument_list|,
name|PATH_SELECTOR
argument_list|)
operator|.
name|get
argument_list|(
name|PATH
argument_list|)
decl_stmt|;
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|paths
operator|.
name|add
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
operator|new
name|PathCursor
argument_list|(
name|paths
argument_list|)
return|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
operator|new
name|PathCursor
argument_list|(
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|Query
name|getQuery
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|List
argument_list|<
name|Query
argument_list|>
name|qs
init|=
operator|new
name|ArrayList
argument_list|<
name|Query
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|filter
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|path
operator|=
literal|""
expr_stmt|;
block|}
switch|switch
condition|(
name|filter
operator|.
name|getPathRestriction
argument_list|()
condition|)
block|{
case|case
name|ALL_CHILDREN
case|:
name|qs
operator|.
name|add
argument_list|(
operator|new
name|PrefixQuery
argument_list|(
name|newPathTerm
argument_list|(
name|path
operator|+
literal|"/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|DIRECT_CHILDREN
case|:
comment|// FIXME
name|qs
operator|.
name|add
argument_list|(
operator|new
name|PrefixQuery
argument_list|(
name|newPathTerm
argument_list|(
name|path
operator|+
literal|"/"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|EXACT
case|:
name|qs
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|newPathTerm
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|PARENT
case|:
name|int
name|slash
init|=
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|slash
operator|!=
operator|-
literal|1
condition|)
block|{
name|String
name|parent
init|=
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|slash
argument_list|)
decl_stmt|;
name|qs
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|newPathTerm
argument_list|(
name|parent
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// there's no parent of the root node
return|return
literal|null
return|;
block|}
break|break;
block|}
for|for
control|(
name|PropertyRestriction
name|pr
range|:
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|pr
operator|.
name|propertyName
decl_stmt|;
name|String
name|first
init|=
literal|null
decl_stmt|;
name|String
name|last
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|pr
operator|.
name|first
operator|!=
literal|null
condition|)
block|{
name|first
operator|=
name|pr
operator|.
name|first
operator|.
name|getString
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|pr
operator|.
name|last
operator|!=
literal|null
condition|)
block|{
name|last
operator|=
name|pr
operator|.
name|last
operator|.
name|getString
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|first
operator|.
name|equals
argument_list|(
name|last
argument_list|)
operator|&&
name|pr
operator|.
name|firstIncluding
operator|&&
name|pr
operator|.
name|lastIncluding
condition|)
block|{
name|qs
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|name
argument_list|,
name|first
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|qs
operator|.
name|add
argument_list|(
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
name|name
argument_list|,
name|first
argument_list|,
name|last
argument_list|,
name|pr
operator|.
name|firstIncluding
argument_list|,
name|pr
operator|.
name|lastIncluding
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|qs
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|Query
name|q
range|:
name|qs
control|)
block|{
name|bq
operator|.
name|add
argument_list|(
name|q
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
return|return
name|bq
return|;
block|}
else|else
block|{
return|return
name|qs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
block|}
comment|/**      * A cursor over the resulting paths.      */
specifier|private
specifier|static
class|class
name|PathCursor
implements|implements
name|Cursor
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
decl_stmt|;
specifier|private
name|String
name|path
decl_stmt|;
specifier|public
name|PathCursor
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|)
block|{
name|this
operator|.
name|iterator
operator|=
name|paths
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|next
parameter_list|()
block|{
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|path
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|path
operator|=
literal|null
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|IndexRow
name|currentRow
parameter_list|()
block|{
comment|// TODO support jcr:score and possibly rep:exceprt
return|return
operator|new
name|IndexRowImpl
argument_list|(
name|path
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

