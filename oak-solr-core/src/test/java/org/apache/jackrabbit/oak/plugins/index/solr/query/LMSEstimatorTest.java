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
name|solr
operator|.
name|query
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
name|query
operator|.
name|fulltext
operator|.
name|FullTextExpression
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
name|fulltext
operator|.
name|FullTextTerm
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
name|solr
operator|.
name|common
operator|.
name|SolrDocumentList
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
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_comment
comment|/**  * Tests for {@link org.apache.jackrabbit.oak.plugins.index.solr.query.LMSEstimator}  */
end_comment

begin_class
specifier|public
class|class
name|LMSEstimatorTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testUpdate
parameter_list|()
throws|throws
name|Exception
block|{
name|LMSEstimator
name|lmsEstimator
init|=
operator|new
name|LMSEstimator
argument_list|()
decl_stmt|;
name|Filter
name|filter
init|=
name|mock
argument_list|(
name|Filter
operator|.
name|class
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|docs
init|=
name|mock
argument_list|(
name|SolrDocumentList
operator|.
name|class
argument_list|)
decl_stmt|;
name|lmsEstimator
operator|.
name|update
argument_list|(
name|filter
argument_list|,
name|docs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultipleUpdates
parameter_list|()
throws|throws
name|Exception
block|{
name|LMSEstimator
name|lmsEstimator
init|=
operator|new
name|LMSEstimator
argument_list|()
decl_stmt|;
name|Filter
name|filter
init|=
name|mock
argument_list|(
name|Filter
operator|.
name|class
argument_list|)
decl_stmt|;
name|FullTextExpression
name|fte
init|=
operator|new
name|FullTextTerm
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|filter
operator|.
name|getFullTextConstraint
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|fte
argument_list|)
expr_stmt|;
name|SolrDocumentList
name|docs
init|=
operator|new
name|SolrDocumentList
argument_list|()
decl_stmt|;
name|lmsEstimator
operator|.
name|update
argument_list|(
name|filter
argument_list|,
name|docs
argument_list|)
expr_stmt|;
name|long
name|actualCount
init|=
literal|10
decl_stmt|;
name|docs
operator|.
name|setNumFound
argument_list|(
name|actualCount
argument_list|)
expr_stmt|;
name|long
name|estimate
init|=
name|lmsEstimator
operator|.
name|estimate
argument_list|(
name|filter
argument_list|)
decl_stmt|;
name|long
name|diff
init|=
name|actualCount
operator|-
name|estimate
decl_stmt|;
comment|// update causes weights adjustment
name|lmsEstimator
operator|.
name|update
argument_list|(
name|filter
argument_list|,
name|docs
argument_list|)
expr_stmt|;
name|long
name|estimate2
init|=
name|lmsEstimator
operator|.
name|estimate
argument_list|(
name|filter
argument_list|)
decl_stmt|;
name|long
name|diff2
init|=
name|actualCount
operator|-
name|estimate2
decl_stmt|;
name|assertTrue
argument_list|(
name|diff2
operator|<
name|diff
argument_list|)
expr_stmt|;
comment|// new estimate is more accurate than previous one
comment|// update doesn't cause weight adjustments therefore estimates stays unchanged
name|lmsEstimator
operator|.
name|update
argument_list|(
name|filter
argument_list|,
name|docs
argument_list|)
expr_stmt|;
name|long
name|estimate3
init|=
name|lmsEstimator
operator|.
name|estimate
argument_list|(
name|filter
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|estimate3
argument_list|,
name|estimate2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEstimate
parameter_list|()
throws|throws
name|Exception
block|{
name|LMSEstimator
name|lmsEstimator
init|=
operator|new
name|LMSEstimator
argument_list|()
decl_stmt|;
name|Filter
name|filter
init|=
name|mock
argument_list|(
name|Filter
operator|.
name|class
argument_list|)
decl_stmt|;
name|long
name|estimate
init|=
name|lmsEstimator
operator|.
name|estimate
argument_list|(
name|filter
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|estimate
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

