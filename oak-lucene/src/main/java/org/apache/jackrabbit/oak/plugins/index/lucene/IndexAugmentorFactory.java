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
package|;
end_package

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
name|ImmutableMap
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
name|LinkedListMultimap
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
name|ListMultimap
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Deactivate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Reference
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|ReferenceCardinality
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|ReferencePolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|References
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Service
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
name|commons
operator|.
name|PerfLogger
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
name|spi
operator|.
name|FulltextQueryTermsProvider
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
name|spi
operator|.
name|IndexFieldProvider
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
name|document
operator|.
name|Field
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
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
name|Map
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

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
annotation|@
name|Component
annotation|@
name|Service
argument_list|(
name|value
operator|=
name|IndexAugmentorFactory
operator|.
name|class
argument_list|)
annotation|@
name|References
argument_list|(
block|{
annotation|@
name|Reference
argument_list|(
name|name
operator|=
literal|"IndexFieldProvider"
argument_list|,
name|policy
operator|=
name|ReferencePolicy
operator|.
name|DYNAMIC
argument_list|,
name|cardinality
operator|=
name|ReferenceCardinality
operator|.
name|OPTIONAL_MULTIPLE
argument_list|,
name|referenceInterface
operator|=
name|IndexFieldProvider
operator|.
name|class
argument_list|)
block|,
annotation|@
name|Reference
argument_list|(
name|name
operator|=
literal|"FulltextQueryTermsProvider"
argument_list|,
name|policy
operator|=
name|ReferencePolicy
operator|.
name|DYNAMIC
argument_list|,
name|cardinality
operator|=
name|ReferenceCardinality
operator|.
name|OPTIONAL_MULTIPLE
argument_list|,
name|referenceInterface
operator|=
name|FulltextQueryTermsProvider
operator|.
name|class
argument_list|)
block|}
argument_list|)
specifier|public
class|class
name|IndexAugmentorFactory
block|{
specifier|private
specifier|static
specifier|final
name|PerfLogger
name|PERFLOG
init|=
operator|new
name|PerfLogger
argument_list|(
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|IndexAugmentorFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".perf"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|IndexFieldProvider
argument_list|>
name|indexFieldProviders
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|FulltextQueryTermsProvider
argument_list|>
name|fulltextQueryTermsProviders
decl_stmt|;
specifier|private
specifier|volatile
name|Map
argument_list|<
name|String
argument_list|,
name|CompositeIndexFieldProvider
argument_list|>
name|indexFieldProviderMap
decl_stmt|;
specifier|private
specifier|volatile
name|Map
argument_list|<
name|String
argument_list|,
name|CompositeFulltextQueryTermsProvider
argument_list|>
name|fulltextQueryTermsProviderMap
decl_stmt|;
specifier|public
name|IndexAugmentorFactory
parameter_list|()
block|{
name|indexFieldProviders
operator|=
name|Sets
operator|.
name|newIdentityHashSet
argument_list|()
expr_stmt|;
name|fulltextQueryTermsProviders
operator|=
name|Sets
operator|.
name|newIdentityHashSet
argument_list|()
expr_stmt|;
name|resetState
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Deactivate
specifier|private
specifier|synchronized
name|void
name|deactivate
parameter_list|()
block|{
name|resetState
argument_list|()
expr_stmt|;
block|}
annotation|@
name|NotNull
specifier|public
name|IndexFieldProvider
name|getIndexFieldProvider
parameter_list|(
name|String
name|nodeType
parameter_list|)
block|{
name|IndexFieldProvider
name|provider
init|=
name|indexFieldProviderMap
operator|.
name|get
argument_list|(
name|nodeType
argument_list|)
decl_stmt|;
return|return
operator|(
name|provider
operator|!=
literal|null
operator|)
condition|?
name|provider
else|:
name|IndexFieldProvider
operator|.
name|DEFAULT
return|;
block|}
annotation|@
name|NotNull
specifier|public
name|FulltextQueryTermsProvider
name|getFulltextQueryTermsProvider
parameter_list|(
name|String
name|nodeType
parameter_list|)
block|{
name|FulltextQueryTermsProvider
name|provider
init|=
name|fulltextQueryTermsProviderMap
operator|.
name|get
argument_list|(
name|nodeType
argument_list|)
decl_stmt|;
return|return
operator|(
name|provider
operator|!=
literal|null
operator|)
condition|?
name|provider
else|:
name|FulltextQueryTermsProvider
operator|.
name|DEFAULT
return|;
block|}
specifier|synchronized
name|void
name|bindIndexFieldProvider
parameter_list|(
name|IndexFieldProvider
name|indexFieldProvider
parameter_list|)
block|{
name|indexFieldProviders
operator|.
name|add
argument_list|(
name|indexFieldProvider
argument_list|)
expr_stmt|;
name|refreshIndexFieldProviders
argument_list|()
expr_stmt|;
block|}
specifier|synchronized
name|void
name|unbindIndexFieldProvider
parameter_list|(
name|IndexFieldProvider
name|indexFieldProvider
parameter_list|)
block|{
name|indexFieldProviders
operator|.
name|remove
argument_list|(
name|indexFieldProvider
argument_list|)
expr_stmt|;
name|refreshIndexFieldProviders
argument_list|()
expr_stmt|;
block|}
specifier|synchronized
name|void
name|bindFulltextQueryTermsProvider
parameter_list|(
name|FulltextQueryTermsProvider
name|fulltextQueryTermsProvider
parameter_list|)
block|{
name|fulltextQueryTermsProviders
operator|.
name|add
argument_list|(
name|fulltextQueryTermsProvider
argument_list|)
expr_stmt|;
name|refreshFulltextQueryTermsProviders
argument_list|()
expr_stmt|;
block|}
specifier|synchronized
name|void
name|unbindFulltextQueryTermsProvider
parameter_list|(
name|FulltextQueryTermsProvider
name|fulltextQueryTermsProvider
parameter_list|)
block|{
name|fulltextQueryTermsProviders
operator|.
name|remove
argument_list|(
name|fulltextQueryTermsProvider
argument_list|)
expr_stmt|;
name|refreshFulltextQueryTermsProviders
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|refreshIndexFieldProviders
parameter_list|()
block|{
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|IndexFieldProvider
argument_list|>
name|providerMultimap
init|=
name|LinkedListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexFieldProvider
name|provider
range|:
name|indexFieldProviders
control|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|supportedNodeTypes
init|=
name|provider
operator|.
name|getSupportedTypes
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|nodeType
range|:
name|supportedNodeTypes
control|)
block|{
name|providerMultimap
operator|.
name|put
argument_list|(
name|nodeType
argument_list|,
name|provider
argument_list|)
expr_stmt|;
block|}
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|CompositeIndexFieldProvider
argument_list|>
name|providerMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|nodeType
range|:
name|providerMultimap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|IndexFieldProvider
argument_list|>
name|providers
init|=
name|providerMultimap
operator|.
name|get
argument_list|(
name|nodeType
argument_list|)
decl_stmt|;
name|CompositeIndexFieldProvider
name|compositeIndexFieldProvider
init|=
operator|new
name|CompositeIndexFieldProvider
argument_list|(
name|nodeType
argument_list|,
name|providers
argument_list|)
decl_stmt|;
name|providerMap
operator|.
name|put
argument_list|(
name|nodeType
argument_list|,
name|compositeIndexFieldProvider
argument_list|)
expr_stmt|;
block|}
name|indexFieldProviderMap
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|providerMap
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|refreshFulltextQueryTermsProviders
parameter_list|()
block|{
name|ListMultimap
argument_list|<
name|String
argument_list|,
name|FulltextQueryTermsProvider
argument_list|>
name|providerMultimap
init|=
name|LinkedListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|FulltextQueryTermsProvider
name|provider
range|:
name|fulltextQueryTermsProviders
control|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|supportedNodeTypes
init|=
name|provider
operator|.
name|getSupportedTypes
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|nodeType
range|:
name|supportedNodeTypes
control|)
block|{
name|providerMultimap
operator|.
name|put
argument_list|(
name|nodeType
argument_list|,
name|provider
argument_list|)
expr_stmt|;
block|}
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|CompositeFulltextQueryTermsProvider
argument_list|>
name|providerMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|nodeType
range|:
name|providerMultimap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|FulltextQueryTermsProvider
argument_list|>
name|providers
init|=
name|providerMultimap
operator|.
name|get
argument_list|(
name|nodeType
argument_list|)
decl_stmt|;
name|CompositeFulltextQueryTermsProvider
name|compositeFulltextQueryTermsProvider
init|=
operator|new
name|CompositeFulltextQueryTermsProvider
argument_list|(
name|nodeType
argument_list|,
name|providers
argument_list|)
decl_stmt|;
name|providerMap
operator|.
name|put
argument_list|(
name|nodeType
argument_list|,
name|compositeFulltextQueryTermsProvider
argument_list|)
expr_stmt|;
block|}
name|fulltextQueryTermsProviderMap
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|providerMap
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|resetState
parameter_list|()
block|{
name|indexFieldProviders
operator|.
name|clear
argument_list|()
expr_stmt|;
name|fulltextQueryTermsProviders
operator|.
name|clear
argument_list|()
expr_stmt|;
name|indexFieldProviderMap
operator|=
name|Collections
operator|.
name|EMPTY_MAP
expr_stmt|;
name|fulltextQueryTermsProviderMap
operator|=
name|Collections
operator|.
name|EMPTY_MAP
expr_stmt|;
block|}
name|boolean
name|isStateEmpty
parameter_list|()
block|{
return|return
name|indexFieldProviders
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|&&
name|indexFieldProviderMap
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|&&
name|fulltextQueryTermsProviders
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|&&
name|fulltextQueryTermsProviderMap
operator|.
name|size
argument_list|()
operator|==
literal|0
return|;
block|}
class|class
name|CompositeIndexFieldProvider
implements|implements
name|IndexFieldProvider
block|{
specifier|private
specifier|final
name|String
name|nodeType
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|IndexFieldProvider
argument_list|>
name|providers
decl_stmt|;
name|CompositeIndexFieldProvider
parameter_list|(
name|String
name|nodeType
parameter_list|,
name|List
argument_list|<
name|IndexFieldProvider
argument_list|>
name|providers
parameter_list|)
block|{
name|this
operator|.
name|nodeType
operator|=
name|nodeType
expr_stmt|;
name|this
operator|.
name|providers
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|providers
argument_list|)
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Field
argument_list|>
name|getAugmentedFields
parameter_list|(
specifier|final
name|String
name|path
parameter_list|,
specifier|final
name|NodeState
name|document
parameter_list|,
specifier|final
name|NodeState
name|indexDefinition
parameter_list|)
block|{
name|List
argument_list|<
name|Field
argument_list|>
name|fields
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexFieldProvider
name|indexFieldProvider
range|:
name|providers
control|)
block|{
specifier|final
name|long
name|start
init|=
name|PERFLOG
operator|.
name|start
argument_list|()
decl_stmt|;
name|Iterable
argument_list|<
name|Field
argument_list|>
name|providedFields
init|=
name|indexFieldProvider
operator|.
name|getAugmentedFields
argument_list|(
name|path
argument_list|,
name|document
argument_list|,
name|indexDefinition
argument_list|)
decl_stmt|;
name|PERFLOG
operator|.
name|end
argument_list|(
name|start
argument_list|,
literal|1
argument_list|,
literal|"indexFieldProvider: {}, path: {}, doc: {}, indexDef: {}"
argument_list|,
name|indexFieldProvider
argument_list|,
name|path
argument_list|,
name|document
argument_list|,
name|indexDefinition
argument_list|)
expr_stmt|;
for|for
control|(
name|Field
name|f
range|:
name|providedFields
control|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|fields
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getSupportedTypes
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|nodeType
argument_list|)
return|;
block|}
block|}
class|class
name|CompositeFulltextQueryTermsProvider
implements|implements
name|FulltextQueryTermsProvider
block|{
specifier|private
specifier|final
name|String
name|nodeType
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|FulltextQueryTermsProvider
argument_list|>
name|providers
decl_stmt|;
name|CompositeFulltextQueryTermsProvider
parameter_list|(
name|String
name|nodeType
parameter_list|,
name|List
argument_list|<
name|FulltextQueryTermsProvider
argument_list|>
name|providers
parameter_list|)
block|{
name|this
operator|.
name|nodeType
operator|=
name|nodeType
expr_stmt|;
name|this
operator|.
name|providers
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|providers
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Query
name|getQueryTerm
parameter_list|(
specifier|final
name|String
name|text
parameter_list|,
specifier|final
name|Analyzer
name|analyzer
parameter_list|,
name|NodeState
name|indexDefinition
parameter_list|)
block|{
name|List
argument_list|<
name|Query
argument_list|>
name|subQueries
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|FulltextQueryTermsProvider
name|fulltextQueryTermsProvider
range|:
name|providers
control|)
block|{
specifier|final
name|long
name|start
init|=
name|PERFLOG
operator|.
name|start
argument_list|()
decl_stmt|;
name|Query
name|subQuery
init|=
name|fulltextQueryTermsProvider
operator|.
name|getQueryTerm
argument_list|(
name|text
argument_list|,
name|analyzer
argument_list|,
name|indexDefinition
argument_list|)
decl_stmt|;
name|PERFLOG
operator|.
name|end
argument_list|(
name|start
argument_list|,
literal|1
argument_list|,
literal|"fulltextQueryTermsProvider: {}, text: {}"
argument_list|,
name|fulltextQueryTermsProvider
argument_list|,
name|text
argument_list|)
expr_stmt|;
if|if
condition|(
name|subQuery
operator|!=
literal|null
condition|)
block|{
name|subQueries
operator|.
name|add
argument_list|(
name|subQuery
argument_list|)
expr_stmt|;
block|}
block|}
name|Query
name|ret
decl_stmt|;
if|if
condition|(
name|subQueries
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|ret
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|subQueries
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|ret
operator|=
name|subQueries
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|BooleanQuery
name|query
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|Query
name|subQuery
range|:
name|subQueries
control|)
block|{
name|query
operator|.
name|add
argument_list|(
name|subQuery
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
name|ret
operator|=
name|query
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getSupportedTypes
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|nodeType
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

