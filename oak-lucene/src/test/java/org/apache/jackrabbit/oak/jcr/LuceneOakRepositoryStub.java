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
name|jcr
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
name|Lists
operator|.
name|newArrayList
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
name|JcrConstants
operator|.
name|JCR_CONTENT
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
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
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
name|JcrConstants
operator|.
name|NT_FILE
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
name|NAME
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|RepositoryException
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
name|aggregate
operator|.
name|NodeAggregator
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
name|aggregate
operator|.
name|SimpleNodeAggregator
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
name|LuceneIndexConstants
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
name|LuceneIndexEditorProvider
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
name|LuceneIndexProvider
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
name|LuceneInitializerHelper
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
name|state
operator|.
name|NodeBuilder
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneOakRepositoryStub
extends|extends
name|OakTarMKRepositoryStub
block|{
specifier|public
name|LuceneOakRepositoryStub
parameter_list|(
name|Properties
name|settings
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|preCreateRepository
parameter_list|(
name|Jcr
name|jcr
parameter_list|)
block|{
name|LuceneIndexProvider
name|provider
init|=
operator|new
name|LuceneIndexProvider
argument_list|()
operator|.
name|with
argument_list|(
name|getNodeAggregator
argument_list|()
argument_list|)
decl_stmt|;
name|jcr
operator|.
name|with
argument_list|(
operator|new
name|LuceneCompatModeInitializer
argument_list|(
literal|"luceneGlobal"
argument_list|,
operator|(
name|Set
argument_list|<
name|String
argument_list|>
operator|)
literal|null
argument_list|)
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
name|withFastQueryResultSize
argument_list|(
literal|true
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|LuceneIndexEditorProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|NodeAggregator
name|getNodeAggregator
parameter_list|()
block|{
return|return
operator|new
name|SimpleNodeAggregator
argument_list|()
operator|.
name|newRuleWithName
argument_list|(
name|NT_FILE
argument_list|,
name|newArrayList
argument_list|(
name|JCR_CONTENT
argument_list|,
name|JCR_CONTENT
operator|+
literal|"/*"
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|LuceneCompatModeInitializer
extends|extends
name|LuceneInitializerHelper
block|{
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
name|LuceneCompatModeInitializer
parameter_list|(
name|String
name|name
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|propertyTypes
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|propertyTypes
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
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
if|if
condition|(
name|builder
operator|.
name|hasChildNode
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|&&
name|builder
operator|.
name|getChildNode
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// do nothing
block|}
else|else
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
operator|.
name|child
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|,
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|,
name|TYPE_LUCENE
argument_list|)
operator|.
name|setProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|TEST_MODE
argument_list|,
literal|true
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|EVALUATE_PATH_RESTRICTION
argument_list|,
literal|true
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|SUGGEST_UPDATE_FREQUENCY_MINUTES
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|NodeBuilder
name|rules
init|=
name|index
operator|.
name|child
argument_list|(
name|LuceneIndexConstants
operator|.
name|INDEX_RULES
argument_list|)
decl_stmt|;
name|rules
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
literal|"nt:unstructured"
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|NodeBuilder
name|ntBase
init|=
name|rules
operator|.
name|child
argument_list|(
literal|"nt:base"
argument_list|)
decl_stmt|;
name|ntBase
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
literal|"nt:unstructured"
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
comment|//Enable nodeName index support
name|ntBase
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|INDEX_NODE_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|NodeBuilder
name|props
init|=
name|ntBase
operator|.
name|child
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_NODE
argument_list|)
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
literal|"nt:unstructured"
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|enableFulltextIndex
argument_list|(
name|props
operator|.
name|child
argument_list|(
literal|"allProps"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|enableFulltextIndex
parameter_list|(
name|NodeBuilder
name|propNode
parameter_list|)
block|{
name|propNode
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
literal|"nt:unstructured"
argument_list|,
name|NAME
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_ANALYZED
argument_list|,
literal|true
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_NODE_SCOPE_INDEX
argument_list|,
literal|true
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_USE_IN_EXCERPT
argument_list|,
literal|true
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_PROPERTY_INDEX
argument_list|,
literal|true
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_USE_IN_SPELLCHECK
argument_list|,
literal|true
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_USE_IN_SUGGEST
argument_list|,
literal|true
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_NAME
argument_list|,
name|LuceneIndexConstants
operator|.
name|REGEX_ALL_PROPS
argument_list|)
operator|.
name|setProperty
argument_list|(
name|LuceneIndexConstants
operator|.
name|PROP_IS_REGEX
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

