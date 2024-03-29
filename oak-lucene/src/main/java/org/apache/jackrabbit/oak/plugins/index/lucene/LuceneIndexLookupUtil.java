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
name|plugins
operator|.
name|index
operator|.
name|search
operator|.
name|IndexFormatVersion
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
name|IndexLookup
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
name|state
operator|.
name|NodeState
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
name|TYPE_LUCENE
import|;
end_import

begin_class
class|class
name|LuceneIndexLookupUtil
block|{
specifier|static
specifier|final
name|Predicate
argument_list|<
name|NodeState
argument_list|>
name|LUCENE_INDEX_DEFINITION_PREDICATE
init|=
name|state
lambda|->
name|TYPE_LUCENE
operator|.
name|equals
argument_list|(
name|state
operator|.
name|getString
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|LuceneIndexLookupUtil
parameter_list|()
block|{     }
comment|/**      * Returns the path of the first Lucene index node which supports      * fulltext search      */
specifier|public
specifier|static
name|String
name|getOldFullTextIndexPath
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|IndexTracker
name|tracker
parameter_list|)
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|indexPaths
init|=
name|getLuceneIndexLookup
argument_list|(
name|root
argument_list|)
operator|.
name|collectIndexNodePaths
argument_list|(
name|filter
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|LuceneIndexNode
name|indexNode
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|indexPaths
control|)
block|{
try|try
block|{
name|indexNode
operator|=
name|tracker
operator|.
name|acquireIndexNode
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexNode
operator|!=
literal|null
operator|&&
name|indexNode
operator|.
name|getDefinition
argument_list|()
operator|.
name|isFullTextEnabled
argument_list|()
operator|&&
name|indexNode
operator|.
name|getDefinition
argument_list|()
operator|.
name|getVersion
argument_list|()
operator|==
name|IndexFormatVersion
operator|.
name|V1
condition|)
block|{
return|return
name|path
return|;
block|}
block|}
finally|finally
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
block|}
return|return
literal|null
return|;
block|}
specifier|public
specifier|static
name|IndexLookup
name|getLuceneIndexLookup
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
return|return
operator|new
name|IndexLookup
argument_list|(
name|root
argument_list|,
name|LUCENE_INDEX_DEFINITION_PREDICATE
argument_list|)
return|;
block|}
block|}
end_class

end_unit

