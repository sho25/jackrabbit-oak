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
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|Nonnull
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
name|spi
operator|.
name|commit
operator|.
name|CommitInfo
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

begin_comment
comment|/**  * A provider for Lucene indexes.  *   * @see LuceneIndex  */
end_comment

begin_class
specifier|public
class|class
name|LuceneIndexProvider
implements|implements
name|QueryIndexProvider
implements|,
name|Observer
implements|,
name|Closeable
block|{
specifier|protected
specifier|final
name|IndexTracker
name|tracker
decl_stmt|;
specifier|protected
specifier|volatile
name|Analyzer
name|analyzer
init|=
name|LuceneIndexConstants
operator|.
name|ANALYZER
decl_stmt|;
specifier|protected
specifier|volatile
name|NodeAggregator
name|aggregator
init|=
literal|null
decl_stmt|;
specifier|public
name|LuceneIndexProvider
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|IndexTracker
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LuceneIndexProvider
parameter_list|(
name|IndexTracker
name|tracker
parameter_list|)
block|{
name|this
operator|.
name|tracker
operator|=
name|tracker
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
name|tracker
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//----------------------------------------------------------< Observer>--
annotation|@
name|Override
specifier|public
name|void
name|contentChanged
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
block|{
name|tracker
operator|.
name|update
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------< QueryIndexProvider>--
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|List
argument_list|<
name|QueryIndex
argument_list|>
name|getQueryIndexes
parameter_list|(
name|NodeState
name|nodeState
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
expr|<
name|QueryIndex
operator|>
name|of
argument_list|(
name|newLuceneIndex
argument_list|()
argument_list|,
name|newLucenePropertyIndex
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|LuceneIndex
name|newLuceneIndex
parameter_list|()
block|{
return|return
operator|new
name|LuceneIndex
argument_list|(
name|tracker
argument_list|,
name|analyzer
argument_list|,
name|aggregator
argument_list|)
return|;
block|}
specifier|protected
name|LucenePropertyIndex
name|newLucenePropertyIndex
parameter_list|()
block|{
return|return
operator|new
name|LucenePropertyIndex
argument_list|(
name|tracker
argument_list|,
name|analyzer
argument_list|,
name|aggregator
argument_list|)
return|;
block|}
comment|/**      * sets the default analyzer that will be used at query time      */
specifier|public
name|void
name|setAnalyzer
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
block|}
comment|/**      * sets the default node aggregator that will be used at query time      */
specifier|public
name|void
name|setAggregator
parameter_list|(
name|NodeAggregator
name|aggregator
parameter_list|)
block|{
name|this
operator|.
name|aggregator
operator|=
name|aggregator
expr_stmt|;
block|}
comment|// ----- helper builder method
specifier|public
name|LuceneIndexProvider
name|with
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|setAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|LuceneIndexProvider
name|with
parameter_list|(
name|NodeAggregator
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|setAggregator
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
name|IndexTracker
name|getTracker
parameter_list|()
block|{
return|return
name|tracker
return|;
block|}
block|}
end_class

end_unit

