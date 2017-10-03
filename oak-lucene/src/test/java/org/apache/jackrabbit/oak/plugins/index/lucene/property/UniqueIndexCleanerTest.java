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
name|property
package|;
end_package

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
name|spi
operator|.
name|state
operator|.
name|NodeBuilder
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
operator|.
name|copyOf
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MILLISECONDS
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
name|property
operator|.
name|HybridPropertyIndexUtil
operator|.
name|PROP_CREATED
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|containsInAnyOrder
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
name|UniqueIndexCleanerTest
block|{
specifier|private
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|nothingCleaned
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|PROP_CREATED
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|PROP_CREATED
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
name|UniqueIndexCleaner
name|cleaner
init|=
operator|new
name|UniqueIndexCleaner
argument_list|(
name|MILLISECONDS
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|cleaner
operator|.
name|clean
argument_list|(
name|builder
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|copyOf
argument_list|(
name|builder
operator|.
name|getChildNodeNames
argument_list|()
argument_list|)
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|cleanWithMargin
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|PROP_CREATED
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|PROP_CREATED
argument_list|,
literal|200
argument_list|)
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
name|UniqueIndexCleaner
name|cleaner
init|=
operator|new
name|UniqueIndexCleaner
argument_list|(
name|MILLISECONDS
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|cleaner
operator|.
name|clean
argument_list|(
name|builder
argument_list|,
literal|200
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|copyOf
argument_list|(
name|builder
operator|.
name|getChildNodeNames
argument_list|()
argument_list|)
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|refresh
parameter_list|()
block|{
name|builder
operator|=
name|builder
operator|.
name|getNodeState
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

