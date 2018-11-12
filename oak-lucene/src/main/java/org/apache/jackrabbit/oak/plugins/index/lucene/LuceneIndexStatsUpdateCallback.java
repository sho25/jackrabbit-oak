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
name|CommitFailedException
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
name|api
operator|.
name|PropertyState
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
name|PropertyUpdateCallback
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
name|stats
operator|.
name|HistogramStats
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
name|stats
operator|.
name|StatisticsProvider
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
name|stats
operator|.
name|StatsOptions
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
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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

begin_comment
comment|/**  * {@link PropertyUpdateCallback} that records statistics about a certain index size (on disk) and number of documents.  */
end_comment

begin_class
specifier|public
class|class
name|LuceneIndexStatsUpdateCallback
implements|implements
name|PropertyUpdateCallback
block|{
specifier|private
specifier|static
specifier|final
name|String
name|NO_DOCS
init|=
literal|"_NO_DOCS"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|INDEX_SIZE
init|=
literal|"_INDEX_SIZE"
decl_stmt|;
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|indexPath
decl_stmt|;
specifier|private
specifier|final
name|LuceneIndexMBean
name|luceneIndexMBean
decl_stmt|;
specifier|private
specifier|final
name|StatisticsProvider
name|statisticsProvider
decl_stmt|;
name|LuceneIndexStatsUpdateCallback
parameter_list|(
name|String
name|indexPath
parameter_list|,
annotation|@
name|NotNull
name|LuceneIndexMBean
name|luceneIndexMBean
parameter_list|,
annotation|@
name|NotNull
name|StatisticsProvider
name|statisticsProvider
parameter_list|)
block|{
name|this
operator|.
name|indexPath
operator|=
name|indexPath
expr_stmt|;
name|this
operator|.
name|luceneIndexMBean
operator|=
name|luceneIndexMBean
expr_stmt|;
name|this
operator|.
name|statisticsProvider
operator|=
name|statisticsProvider
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyUpdated
parameter_list|(
name|String
name|nodePath
parameter_list|,
name|String
name|propertyRelativePath
parameter_list|,
name|PropertyDefinition
name|pd
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|before
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|after
parameter_list|)
block|{
comment|// do nothing
block|}
annotation|@
name|Override
specifier|public
name|void
name|done
parameter_list|()
throws|throws
name|CommitFailedException
block|{
try|try
block|{
name|int
name|docCount
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|luceneIndexMBean
operator|.
name|getDocCount
argument_list|(
name|indexPath
argument_list|)
argument_list|)
decl_stmt|;
name|HistogramStats
name|docCountHistogram
init|=
name|statisticsProvider
operator|.
name|getHistogram
argument_list|(
name|indexPath
operator|+
name|NO_DOCS
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
decl_stmt|;
name|docCountHistogram
operator|.
name|update
argument_list|(
name|docCount
argument_list|)
expr_stmt|;
name|long
name|indexSize
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|luceneIndexMBean
operator|.
name|getSize
argument_list|(
name|indexPath
argument_list|)
argument_list|)
decl_stmt|;
name|HistogramStats
name|indexSizeHistogram
init|=
name|statisticsProvider
operator|.
name|getHistogram
argument_list|(
name|indexPath
operator|+
name|INDEX_SIZE
argument_list|,
name|StatsOptions
operator|.
name|METRICS_ONLY
argument_list|)
decl_stmt|;
name|indexSizeHistogram
operator|.
name|update
argument_list|(
name|indexSize
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"{} stats updated; docCount {}, size {}"
argument_list|,
name|indexPath
argument_list|,
name|docCount
argument_list|,
name|indexSize
argument_list|)
expr_stmt|;
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
literal|"could not update no_docs/index_size stats for index at {}"
argument_list|,
name|indexPath
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

