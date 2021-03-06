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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Stopwatch
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
name|AbstractIterator
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
name|IndexDefinition
operator|.
name|SecureFacetConfiguration
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
name|facet
operator|.
name|FacetResult
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
name|facet
operator|.
name|FacetsCollector
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
name|facet
operator|.
name|FacetsCollector
operator|.
name|MatchingDocs
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
name|facet
operator|.
name|LabelAndValue
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
name|facet
operator|.
name|sortedset
operator|.
name|DefaultSortedSetDocValuesReaderState
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
name|facet
operator|.
name|sortedset
operator|.
name|SortedSetDocValuesFacetCounts
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
name|DocIdSetIterator
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
name|java
operator|.
name|util
operator|.
name|Random
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
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
import|;
end_import

begin_comment
comment|/**  * ACL filtered version of {@link SortedSetDocValuesFacetCounts}  */
end_comment

begin_class
class|class
name|StatisticalSortedSetDocValuesFacetCounts
extends|extends
name|SortedSetDocValuesFacetCounts
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|StatisticalSortedSetDocValuesFacetCounts
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|FacetsCollector
name|facetsCollector
decl_stmt|;
specifier|private
specifier|final
name|Filter
name|filter
decl_stmt|;
specifier|private
specifier|final
name|IndexReader
name|reader
decl_stmt|;
specifier|private
specifier|final
name|SecureFacetConfiguration
name|secureFacetConfiguration
decl_stmt|;
specifier|private
specifier|final
name|DefaultSortedSetDocValuesReaderState
name|state
decl_stmt|;
specifier|private
name|FacetResult
name|facetResult
init|=
literal|null
decl_stmt|;
name|StatisticalSortedSetDocValuesFacetCounts
parameter_list|(
name|DefaultSortedSetDocValuesReaderState
name|state
parameter_list|,
name|FacetsCollector
name|facetsCollector
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|SecureFacetConfiguration
name|secureFacetConfiguration
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|state
argument_list|,
name|facetsCollector
argument_list|)
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|reader
operator|=
name|state
operator|.
name|origReader
expr_stmt|;
name|this
operator|.
name|facetsCollector
operator|=
name|facetsCollector
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
name|this
operator|.
name|secureFacetConfiguration
operator|=
name|secureFacetConfiguration
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|FacetResult
name|getTopChildren
parameter_list|(
name|int
name|topN
parameter_list|,
name|String
name|dim
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|facetResult
operator|==
literal|null
condition|)
block|{
name|facetResult
operator|=
name|getTopChildren0
argument_list|(
name|topN
argument_list|,
name|dim
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
return|return
name|facetResult
return|;
block|}
specifier|private
name|FacetResult
name|getTopChildren0
parameter_list|(
name|int
name|topN
parameter_list|,
name|String
name|dim
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|FacetResult
name|topChildren
init|=
name|super
operator|.
name|getTopChildren
argument_list|(
name|topN
argument_list|,
name|dim
argument_list|,
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|topChildren
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|LabelAndValue
index|[]
name|labelAndValues
init|=
name|topChildren
operator|.
name|labelValues
decl_stmt|;
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|matchingDocsList
init|=
name|facetsCollector
operator|.
name|getMatchingDocs
argument_list|()
decl_stmt|;
name|int
name|hitCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|MatchingDocs
name|matchingDocs
range|:
name|matchingDocsList
control|)
block|{
name|hitCount
operator|+=
name|matchingDocs
operator|.
name|totalHits
expr_stmt|;
block|}
name|int
name|sampleSize
init|=
name|secureFacetConfiguration
operator|.
name|getStatisticalFacetSampleSize
argument_list|()
decl_stmt|;
comment|// In case the hit count is less than sample size(A very small reposiotry perhaps)
comment|// Delegate getting FacetResults to SecureSortedSetDocValuesFacetCounts to get the exact count
comment|// instead of statistical count.<OAK-8138>
if|if
condition|(
name|hitCount
operator|<
name|sampleSize
condition|)
block|{
return|return
operator|new
name|SecureSortedSetDocValuesFacetCounts
argument_list|(
name|state
argument_list|,
name|facetsCollector
argument_list|,
name|filter
argument_list|)
operator|.
name|getTopChildren
argument_list|(
name|topN
argument_list|,
name|dim
argument_list|,
name|path
argument_list|)
return|;
block|}
name|long
name|randomSeed
init|=
name|secureFacetConfiguration
operator|.
name|getRandomSeed
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Sampling facet dim {}; hitCount: {}, sampleSize: {}, seed: {}"
argument_list|,
name|dim
argument_list|,
name|hitCount
argument_list|,
name|sampleSize
argument_list|,
name|randomSeed
argument_list|)
expr_stmt|;
name|Stopwatch
name|w
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|docIterator
init|=
name|getMatchingDocIterator
argument_list|(
name|matchingDocsList
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|sampleIterator
init|=
name|docIterator
decl_stmt|;
if|if
condition|(
name|sampleSize
operator|<
name|hitCount
condition|)
block|{
name|sampleIterator
operator|=
name|getSampledMatchingDocIterator
argument_list|(
name|docIterator
argument_list|,
name|randomSeed
argument_list|,
name|hitCount
argument_list|,
name|sampleSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sampleSize
operator|=
name|hitCount
expr_stmt|;
block|}
name|int
name|accessibleSampleCount
init|=
name|getAccessibleSampleCount
argument_list|(
name|dim
argument_list|,
name|sampleIterator
argument_list|)
decl_stmt|;
name|w
operator|.
name|stop
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Evaluated accessible samples {} in {}"
argument_list|,
name|accessibleSampleCount
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|labelAndValues
operator|=
name|updateLabelAndValueIfRequired
argument_list|(
name|labelAndValues
argument_list|,
name|sampleSize
argument_list|,
name|accessibleSampleCount
argument_list|)
expr_stmt|;
name|int
name|childCount
init|=
name|labelAndValues
operator|.
name|length
decl_stmt|;
name|Number
name|value
init|=
literal|0
decl_stmt|;
for|for
control|(
name|LabelAndValue
name|lv
range|:
name|labelAndValues
control|)
block|{
name|value
operator|=
name|value
operator|.
name|longValue
argument_list|()
operator|+
name|lv
operator|.
name|value
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|FacetResult
argument_list|(
name|dim
argument_list|,
name|path
argument_list|,
name|value
argument_list|,
name|labelAndValues
argument_list|,
name|childCount
argument_list|)
return|;
block|}
specifier|private
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|getMatchingDocIterator
parameter_list|(
specifier|final
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|matchingDocsList
parameter_list|)
block|{
name|Iterator
argument_list|<
name|MatchingDocs
argument_list|>
name|matchingDocsListIterator
init|=
name|matchingDocsList
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|AbstractIterator
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
name|MatchingDocs
name|matchingDocs
init|=
literal|null
decl_stmt|;
name|DocIdSetIterator
name|docIdSetIterator
init|=
literal|null
decl_stmt|;
name|int
name|nextDocId
init|=
name|NO_MORE_DOCS
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|Integer
name|computeNext
parameter_list|()
block|{
try|try
block|{
name|loadNextMatchingDocsIfRequired
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextDocId
operator|==
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|endOfData
argument_list|()
return|;
block|}
else|else
block|{
name|int
name|ret
init|=
name|nextDocId
decl_stmt|;
name|nextDocId
operator|=
name|docIdSetIterator
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
return|return
name|matchingDocs
operator|.
name|context
operator|.
name|docBase
operator|+
name|ret
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|loadNextMatchingDocsIfRequired
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
name|nextDocId
operator|==
name|NO_MORE_DOCS
condition|)
block|{
if|if
condition|(
name|matchingDocsListIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|matchingDocs
operator|=
name|matchingDocsListIterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|docIdSetIterator
operator|=
name|matchingDocs
operator|.
name|bits
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|nextDocId
operator|=
name|docIdSetIterator
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
else|else
block|{
return|return;
block|}
block|}
block|}
block|}
return|;
block|}
specifier|private
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|getSampledMatchingDocIterator
parameter_list|(
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|matchingDocs
parameter_list|,
name|long
name|randomdSeed
parameter_list|,
name|int
name|hitCount
parameter_list|,
name|int
name|sampleSize
parameter_list|)
block|{
name|TapeSampling
argument_list|<
name|Integer
argument_list|>
name|tapeSampling
init|=
operator|new
name|TapeSampling
argument_list|<>
argument_list|(
operator|new
name|Random
argument_list|(
name|randomdSeed
argument_list|)
argument_list|,
name|matchingDocs
argument_list|,
name|hitCount
argument_list|,
name|sampleSize
argument_list|)
decl_stmt|;
return|return
name|tapeSampling
operator|.
name|getSamples
argument_list|()
return|;
block|}
specifier|private
name|int
name|getAccessibleSampleCount
parameter_list|(
name|String
name|dim
parameter_list|,
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|sampleIterator
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|sampleIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|int
name|docId
init|=
name|sampleIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|reader
operator|.
name|document
argument_list|(
name|docId
argument_list|)
decl_stmt|;
if|if
condition|(
name|filter
operator|.
name|isAccessible
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|FieldNames
operator|.
name|PATH
argument_list|)
operator|.
name|stringValue
argument_list|()
operator|+
literal|"/"
operator|+
name|dim
argument_list|)
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
specifier|private
name|LabelAndValue
index|[]
name|updateLabelAndValueIfRequired
parameter_list|(
name|LabelAndValue
index|[]
name|labelAndValues
parameter_list|,
name|int
name|sampleSize
parameter_list|,
name|int
name|accessibleCount
parameter_list|)
block|{
if|if
condition|(
name|accessibleCount
operator|<
name|sampleSize
condition|)
block|{
name|int
name|numZeros
init|=
literal|0
decl_stmt|;
name|LabelAndValue
index|[]
name|newValues
decl_stmt|;
block|{
name|LabelAndValue
index|[]
name|proportionedLVs
init|=
operator|new
name|LabelAndValue
index|[
name|labelAndValues
operator|.
name|length
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
name|labelAndValues
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|LabelAndValue
name|lv
init|=
name|labelAndValues
index|[
name|i
index|]
decl_stmt|;
name|long
name|count
init|=
name|lv
operator|.
name|value
operator|.
name|longValue
argument_list|()
operator|*
name|accessibleCount
operator|/
name|sampleSize
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
name|numZeros
operator|++
expr_stmt|;
block|}
name|proportionedLVs
index|[
name|i
index|]
operator|=
operator|new
name|LabelAndValue
argument_list|(
name|lv
operator|.
name|label
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
name|labelAndValues
operator|=
name|proportionedLVs
expr_stmt|;
block|}
if|if
condition|(
name|numZeros
operator|>
literal|0
condition|)
block|{
name|newValues
operator|=
operator|new
name|LabelAndValue
index|[
name|labelAndValues
operator|.
name|length
operator|-
name|numZeros
index|]
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|LabelAndValue
name|lv
range|:
name|labelAndValues
control|)
block|{
if|if
condition|(
name|lv
operator|.
name|value
operator|.
name|longValue
argument_list|()
operator|>
literal|0
condition|)
block|{
name|newValues
index|[
name|i
operator|++
index|]
operator|=
name|lv
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|newValues
operator|=
name|labelAndValues
expr_stmt|;
block|}
return|return
name|newValues
return|;
block|}
else|else
block|{
return|return
name|labelAndValues
return|;
block|}
block|}
block|}
end_class

end_unit

