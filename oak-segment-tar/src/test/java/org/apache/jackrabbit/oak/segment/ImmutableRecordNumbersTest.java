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
name|segment
package|;
end_package

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
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|segment
operator|.
name|RecordNumbers
operator|.
name|Entry
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

begin_class
specifier|public
class|class
name|ImmutableRecordNumbersTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|tableShouldBeCorrectlyInitialized
parameter_list|()
block|{
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|entries
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|entries
operator|.
name|put
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|entries
operator|.
name|put
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|entries
operator|.
name|put
argument_list|(
literal|5
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|ImmutableRecordNumbers
name|table
init|=
operator|new
name|ImmutableRecordNumbers
argument_list|(
name|entries
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|table
operator|.
name|getOffset
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|table
operator|.
name|getOffset
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|table
operator|.
name|getOffset
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|changingInitializationMapShouldBeSafe
parameter_list|()
block|{
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|entries
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|entries
operator|.
name|put
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|entries
operator|.
name|put
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|entries
operator|.
name|put
argument_list|(
literal|5
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|ImmutableRecordNumbers
name|table
init|=
operator|new
name|ImmutableRecordNumbers
argument_list|(
name|entries
argument_list|)
decl_stmt|;
name|entries
operator|.
name|put
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|entries
operator|.
name|put
argument_list|(
literal|7
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|entries
operator|.
name|remove
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|table
operator|.
name|getOffset
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|table
operator|.
name|getOffset
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|table
operator|.
name|getOffset
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|iteratingShouldBeCorrect
parameter_list|()
block|{
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|entries
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|entries
operator|.
name|put
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|entries
operator|.
name|put
argument_list|(
literal|3
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|entries
operator|.
name|put
argument_list|(
literal|5
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|ImmutableRecordNumbers
name|table
init|=
operator|new
name|ImmutableRecordNumbers
argument_list|(
name|entries
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|iterated
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
name|entry
range|:
name|table
control|)
block|{
name|iterated
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getRecordNumber
argument_list|()
argument_list|,
name|entry
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|entries
argument_list|,
name|iterated
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

