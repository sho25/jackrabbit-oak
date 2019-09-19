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
name|search
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|QueryImpl
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
name|SQL2Parser
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
name|SQL2ParserTest
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
name|spi
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
name|List
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
comment|/**  * Tests for {@link LMSEstimator}  */
end_comment

begin_class
specifier|public
class|class
name|LMSEstimatorTest
block|{
specifier|private
specifier|static
specifier|final
name|SQL2Parser
name|p
init|=
name|SQL2ParserTest
operator|.
name|createTestSQL2Parser
argument_list|()
decl_stmt|;
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
name|long
name|numDocs
init|=
literal|100L
decl_stmt|;
name|lmsEstimator
operator|.
name|update
argument_list|(
name|filter
argument_list|,
name|numDocs
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
name|lmsEstimator
operator|.
name|update
argument_list|(
name|filter
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|long
name|actualCount
init|=
literal|10
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
name|estimate
argument_list|,
name|lmsEstimator
operator|.
name|estimate
argument_list|(
name|filter
argument_list|)
argument_list|)
expr_stmt|;
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
name|actualCount
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
name|assertEquals
argument_list|(
name|estimate2
argument_list|,
name|lmsEstimator
operator|.
name|estimate
argument_list|(
name|filter
argument_list|)
argument_list|)
expr_stmt|;
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
name|actualCount
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
name|lmsEstimator
operator|.
name|estimate
argument_list|(
name|filter
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|diff3
init|=
name|actualCount
operator|-
name|estimate3
decl_stmt|;
name|assertTrue
argument_list|(
name|diff3
operator|<
name|diff2
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
literal|0L
argument_list|,
name|estimate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvergence
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
name|long
name|mse
init|=
name|getMSE
argument_list|(
name|lmsEstimator
argument_list|)
decl_stmt|;
name|int
name|epochs
init|=
literal|15
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|epochs
condition|;
name|i
operator|++
control|)
block|{
name|train
argument_list|(
name|lmsEstimator
argument_list|)
expr_stmt|;
name|long
name|currentMSE
init|=
name|getMSE
argument_list|(
name|lmsEstimator
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|currentMSE
operator|<=
name|mse
argument_list|)
expr_stmt|;
name|mse
operator|=
name|currentMSE
expr_stmt|;
block|}
block|}
specifier|private
name|long
name|getMSE
parameter_list|(
name|LMSEstimator
name|lmsEstimator
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|n
init|=
literal|0
decl_stmt|;
name|long
name|mse
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|line
range|:
name|IOUtils
operator|.
name|readLines
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/lms-data.tsv"
argument_list|)
argument_list|)
control|)
block|{
name|String
index|[]
name|entries
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\t"
argument_list|)
decl_stmt|;
name|long
name|numDocs
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|entries
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|QueryImpl
name|q
init|=
operator|(
name|QueryImpl
operator|)
name|p
operator|.
name|parse
argument_list|(
name|entries
index|[
literal|2
index|]
argument_list|)
decl_stmt|;
name|Filter
name|filter
init|=
name|q
operator|.
name|getSource
argument_list|()
operator|.
name|createFilter
argument_list|(
literal|true
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
name|mse
operator|+=
name|Math
operator|.
name|pow
argument_list|(
name|numDocs
operator|-
name|estimate
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|n
operator|++
expr_stmt|;
block|}
name|mse
operator|/=
name|n
expr_stmt|;
return|return
name|mse
return|;
block|}
specifier|private
name|void
name|train
parameter_list|(
name|LMSEstimator
name|lmsEstimator
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|strings
init|=
name|IOUtils
operator|.
name|readLines
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/lms-data.tsv"
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|line
range|:
name|strings
control|)
block|{
name|String
index|[]
name|entries
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\t"
argument_list|)
decl_stmt|;
name|long
name|numDocs
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|entries
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|QueryImpl
name|q
init|=
operator|(
name|QueryImpl
operator|)
name|p
operator|.
name|parse
argument_list|(
name|entries
index|[
literal|2
index|]
argument_list|)
decl_stmt|;
name|Filter
name|filter
init|=
name|q
operator|.
name|getSource
argument_list|()
operator|.
name|createFilter
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|lmsEstimator
operator|.
name|update
argument_list|(
name|filter
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

