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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|BadIndexTracker
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|hasItem
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
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|BadIndexTrackerTest
block|{
specifier|private
name|VirtualTicker
name|ticker
init|=
operator|new
name|VirtualTicker
argument_list|()
decl_stmt|;
specifier|private
name|BadIndexTracker
name|tracker
init|=
operator|new
name|BadIndexTracker
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|basics
parameter_list|()
throws|throws
name|Exception
block|{
name|tracker
operator|.
name|markBadIndexForRead
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|Exception
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tracker
operator|.
name|getIndexPaths
argument_list|()
argument_list|,
name|hasItem
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tracker
operator|.
name|isIgnoredBadIndex
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|markGoodIndex
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tracker
operator|.
name|isIgnoredBadIndex
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|updatedIndexesMakesGood
parameter_list|()
throws|throws
name|Exception
block|{
name|tracker
operator|.
name|markBadIndexForRead
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|Exception
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tracker
operator|.
name|isIgnoredBadIndex
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|markGoodIndexes
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tracker
operator|.
name|isIgnoredBadIndex
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|recheckDelay
parameter_list|()
throws|throws
name|Exception
block|{
name|tracker
operator|=
operator|new
name|BadIndexTracker
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|setTicker
argument_list|(
name|ticker
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|markBadIndexForRead
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|Exception
argument_list|()
argument_list|)
expr_stmt|;
name|ticker
operator|.
name|addTime
argument_list|(
literal|50
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tracker
operator|.
name|isIgnoredBadIndex
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|ticker
operator|.
name|addTime
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|tracker
operator|.
name|isIgnoredBadIndex
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
comment|//Now cross the threshold
name|ticker
operator|.
name|addTime
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|tracker
operator|.
name|isIgnoredBadIndex
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
comment|//However index is still considered bad
name|assertThat
argument_list|(
name|tracker
operator|.
name|getIndexPaths
argument_list|()
argument_list|,
name|hasItem
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

