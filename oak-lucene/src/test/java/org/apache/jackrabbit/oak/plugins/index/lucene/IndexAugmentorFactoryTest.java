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
name|document
operator|.
name|StringField
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
name|sling
operator|.
name|testing
operator|.
name|mock
operator|.
name|osgi
operator|.
name|MockOsgi
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sling
operator|.
name|testing
operator|.
name|mock
operator|.
name|osgi
operator|.
name|junit
operator|.
name|OsgiContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
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
name|junit
operator|.
name|Rule
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
name|assertThat
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|IndexAugmentorFactoryTest
block|{
specifier|private
name|IndexAugmentorFactory
name|indexAugmentorFactory
init|=
operator|new
name|IndexAugmentorFactory
argument_list|()
decl_stmt|;
annotation|@
name|Rule
specifier|public
specifier|final
name|OsgiContext
name|context
init|=
operator|new
name|OsgiContext
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|compositeIndexProvider
parameter_list|()
block|{
specifier|final
name|String
name|typeA
init|=
literal|"type:A"
decl_stmt|;
specifier|final
name|String
name|typeB
init|=
literal|"type:B"
decl_stmt|;
specifier|final
name|String
name|typeC
init|=
literal|"type:C"
decl_stmt|;
specifier|final
name|String
name|typeD
init|=
literal|"type:D"
decl_stmt|;
name|context
operator|.
name|registerInjectActivateService
argument_list|(
name|indexAugmentorFactory
argument_list|)
expr_stmt|;
operator|new
name|IdentifiableIndexFiledProvider
argument_list|(
literal|"1"
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|typeA
argument_list|,
name|typeB
argument_list|)
argument_list|)
expr_stmt|;
operator|new
name|IdentifiableIndexFiledProvider
argument_list|(
literal|"2"
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|typeC
argument_list|)
argument_list|)
expr_stmt|;
operator|new
name|IdentifiableIndexFiledProvider
argument_list|(
literal|"3"
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|typeA
argument_list|,
name|typeB
argument_list|)
argument_list|)
expr_stmt|;
comment|//register an instance which would be unregistered before validation
name|IndexFieldProvider
name|unreg
init|=
operator|new
name|IdentifiableIndexFiledProvider
argument_list|(
literal|"4"
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|typeD
argument_list|)
argument_list|)
decl_stmt|;
name|indexAugmentorFactory
operator|.
name|unbindIndexFieldProvider
argument_list|(
name|unreg
argument_list|)
expr_stmt|;
name|validateComposedFields
argument_list|(
name|typeA
argument_list|,
literal|"1"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|validateComposedFields
argument_list|(
name|typeC
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|validateComposedFields
argument_list|(
name|typeD
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|deactivate
argument_list|(
name|indexAugmentorFactory
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|Collections
operator|.
name|EMPTY_MAP
argument_list|)
expr_stmt|;
name|validateDeactivatedService
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|compositeQueryTermsProvider
parameter_list|()
block|{
specifier|final
name|String
name|typeA
init|=
literal|"type:A"
decl_stmt|;
specifier|final
name|String
name|typeB
init|=
literal|"type:B"
decl_stmt|;
specifier|final
name|String
name|typeC
init|=
literal|"type:C"
decl_stmt|;
specifier|final
name|String
name|typeD
init|=
literal|"type:D"
decl_stmt|;
specifier|final
name|String
name|typeE
init|=
literal|"type:E"
decl_stmt|;
name|context
operator|.
name|registerInjectActivateService
argument_list|(
name|indexAugmentorFactory
argument_list|)
expr_stmt|;
operator|new
name|IdentifiableQueryTermsProvider
argument_list|(
literal|"1"
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|typeA
argument_list|,
name|typeB
argument_list|)
argument_list|)
expr_stmt|;
operator|new
name|IdentifiableQueryTermsProvider
argument_list|(
literal|"2"
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|typeC
argument_list|)
argument_list|)
expr_stmt|;
operator|new
name|IdentifiableQueryTermsProvider
argument_list|(
literal|"3"
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|typeA
argument_list|,
name|typeB
argument_list|)
argument_list|)
expr_stmt|;
operator|new
name|IdentifiableQueryTermsProvider
argument_list|(
literal|null
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|typeE
argument_list|)
argument_list|)
expr_stmt|;
comment|//register an instance which would be unregistered before validation
name|FulltextQueryTermsProvider
name|unreg
init|=
operator|new
name|IdentifiableQueryTermsProvider
argument_list|(
literal|"4"
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|typeD
argument_list|)
argument_list|)
decl_stmt|;
name|indexAugmentorFactory
operator|.
name|unbindFulltextQueryTermsProvider
argument_list|(
name|unreg
argument_list|)
expr_stmt|;
name|validateComposedQueryTerms
argument_list|(
name|typeA
argument_list|,
literal|"1"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|validateComposedQueryTerms
argument_list|(
name|typeC
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|validateComposedQueryTerms
argument_list|(
name|typeD
argument_list|)
expr_stmt|;
name|validateComposedQueryTerms
argument_list|(
name|typeE
argument_list|)
expr_stmt|;
name|MockOsgi
operator|.
name|deactivate
argument_list|(
name|indexAugmentorFactory
argument_list|,
name|context
operator|.
name|bundleContext
argument_list|()
argument_list|,
name|Collections
operator|.
name|EMPTY_MAP
argument_list|)
expr_stmt|;
name|validateDeactivatedService
argument_list|()
expr_stmt|;
block|}
name|void
name|validateComposedFields
parameter_list|(
name|String
name|type
parameter_list|,
name|String
modifier|...
name|expected
parameter_list|)
block|{
name|IndexFieldProvider
name|compositeIndexProvider
init|=
name|indexAugmentorFactory
operator|.
name|getIndexFieldProvider
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|expected
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Composed index field provider doesn't declare correct supported type"
argument_list|,
name|compositeIndexProvider
operator|.
name|getSupportedTypes
argument_list|()
operator|.
name|contains
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Iterable
argument_list|<
name|Field
argument_list|>
name|fields
init|=
name|compositeIndexProvider
operator|.
name|getAugmentedFields
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|Field
name|f
range|:
name|fields
control|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
operator|.
name|length
argument_list|,
name|Iterables
operator|.
name|size
argument_list|(
name|ids
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ids
argument_list|,
name|CoreMatchers
operator|.
name|hasItems
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|void
name|validateComposedQueryTerms
parameter_list|(
name|String
name|type
parameter_list|,
name|String
modifier|...
name|expected
parameter_list|)
block|{
name|FulltextQueryTermsProvider
name|compositeQueryTermsProvider
init|=
name|indexAugmentorFactory
operator|.
name|getFulltextQueryTermsProvider
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|expected
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Composed query terms provider doesn't declare correct supported type"
argument_list|,
name|compositeQueryTermsProvider
operator|.
name|getSupportedTypes
argument_list|()
operator|.
name|contains
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Query
name|q
init|=
name|compositeQueryTermsProvider
operator|.
name|getQueryTerm
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|q
operator|==
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
literal|"No query terms generated for "
operator|+
name|type
operator|+
literal|"."
argument_list|,
literal|0
argument_list|,
name|expected
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
if|if
condition|(
name|q
operator|instanceof
name|BooleanQuery
condition|)
block|{
name|BooleanQuery
name|query
init|=
operator|(
name|BooleanQuery
operator|)
name|q
decl_stmt|;
name|List
argument_list|<
name|BooleanClause
argument_list|>
name|clauses
init|=
name|query
operator|.
name|clauses
argument_list|()
decl_stmt|;
for|for
control|(
name|BooleanClause
name|clause
range|:
name|clauses
control|)
block|{
name|assertEquals
argument_list|(
name|SHOULD
argument_list|,
name|clause
operator|.
name|getOccur
argument_list|()
argument_list|)
expr_stmt|;
name|Query
name|subQuery
init|=
name|clause
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|String
name|subQueryStr
init|=
name|subQuery
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ids
operator|.
name|add
argument_list|(
name|subQueryStr
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|subQueryStr
operator|.
name|indexOf
argument_list|(
literal|":1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|String
name|subQueryStr
init|=
name|q
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ids
operator|.
name|add
argument_list|(
name|subQueryStr
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|subQueryStr
operator|.
name|indexOf
argument_list|(
literal|":1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expected
operator|.
name|length
argument_list|,
name|Iterables
operator|.
name|size
argument_list|(
name|ids
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ids
argument_list|,
name|CoreMatchers
operator|.
name|hasItems
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|validateDeactivatedService
parameter_list|()
block|{
name|assertTrue
argument_list|(
literal|"All data structures must be empty after deactivate"
argument_list|,
name|indexAugmentorFactory
operator|.
name|isStateEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
class|class
name|IdentifiableIndexFiledProvider
implements|implements
name|IndexFieldProvider
block|{
specifier|private
specifier|final
name|Field
name|id
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|nodeTypes
decl_stmt|;
name|IdentifiableIndexFiledProvider
parameter_list|(
name|String
name|id
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|nodeTypes
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeTypes
operator|=
name|nodeTypes
expr_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|IndexFieldProvider
operator|.
name|class
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|Field
argument_list|>
name|getAugmentedFields
parameter_list|(
name|String
name|path
parameter_list|,
name|NodeState
name|document
parameter_list|,
name|NodeState
name|indexDefinition
parameter_list|)
block|{
return|return
name|Lists
operator|.
name|newArrayList
argument_list|(
name|id
argument_list|)
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
name|nodeTypes
return|;
block|}
block|}
class|class
name|IdentifiableQueryTermsProvider
implements|implements
name|FulltextQueryTermsProvider
block|{
specifier|private
specifier|final
name|Query
name|id
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|nodeTypes
decl_stmt|;
name|IdentifiableQueryTermsProvider
parameter_list|(
name|String
name|id
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|nodeTypes
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
operator|(
name|id
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|id
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeTypes
operator|=
name|nodeTypes
expr_stmt|;
name|context
operator|.
name|registerService
argument_list|(
name|FulltextQueryTermsProvider
operator|.
name|class
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Query
name|getQueryTerm
parameter_list|(
name|String
name|text
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|,
name|NodeState
name|indexDefinition
parameter_list|)
block|{
return|return
name|id
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
name|nodeTypes
return|;
block|}
block|}
block|}
end_class

end_unit

