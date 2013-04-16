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
name|mongomk
operator|.
name|impl
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|AbstractMongoConnectionTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|json
operator|.
name|simple
operator|.
name|JSONArray
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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

begin_comment
comment|/**  * Tests for getRevisionHistory  */
end_comment

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"MongoMK does not implement getRevisionHistory()"
argument_list|)
specifier|public
class|class
name|MongoMKGetRevisionHistoryTest
extends|extends
name|AbstractMongoConnectionTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|maxEntriesZero
parameter_list|()
throws|throws
name|Exception
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|JSONArray
name|array
init|=
name|parseJSONArray
argument_list|(
name|mk
operator|.
name|getRevisionHistory
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|array
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|maxEntriesLimitless
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|count
init|=
literal|10
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a"
operator|+
name|i
operator|+
literal|"\" : {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|JSONArray
name|array
init|=
name|parseJSONArray
argument_list|(
name|mk
operator|.
name|getRevisionHistory
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|count
operator|+
literal|1
argument_list|,
name|array
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|maxEntriesLimited
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|count
init|=
literal|10
decl_stmt|;
name|int
name|limit
init|=
literal|4
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a"
operator|+
name|i
operator|+
literal|"\" : {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|JSONArray
name|array
init|=
name|parseJSONArray
argument_list|(
name|mk
operator|.
name|getRevisionHistory
argument_list|(
literal|0
argument_list|,
name|limit
argument_list|,
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|limit
argument_list|,
name|array
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|path
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|count1
init|=
literal|5
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
name|count1
condition|;
name|i
operator|++
control|)
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a"
operator|+
name|i
operator|+
literal|"\" : {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|JSONArray
name|array
init|=
name|parseJSONArray
argument_list|(
name|mk
operator|.
name|getRevisionHistory
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|count1
operator|+
literal|1
argument_list|,
name|array
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|count2
init|=
literal|5
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
name|count2
condition|;
name|i
operator|++
control|)
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/a1"
argument_list|,
literal|"+\"b"
operator|+
name|i
operator|+
literal|"\" : {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|array
operator|=
name|parseJSONArray
argument_list|(
name|mk
operator|.
name|getRevisionHistory
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|count1
operator|+
literal|1
operator|+
name|count2
argument_list|,
name|array
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|array
operator|=
name|parseJSONArray
argument_list|(
name|mk
operator|.
name|getRevisionHistory
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|"/a1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|count2
operator|+
literal|1
argument_list|,
name|array
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|since
parameter_list|()
throws|throws
name|Exception
block|{
comment|// To make sure there's a little delay since the initial commit.
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|long
name|since1
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|int
name|count1
init|=
literal|6
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
name|count1
condition|;
name|i
operator|++
control|)
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a"
operator|+
name|i
operator|+
literal|"\" : {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|JSONArray
name|array
init|=
name|parseJSONArray
argument_list|(
name|mk
operator|.
name|getRevisionHistory
argument_list|(
name|since1
argument_list|,
operator|-
literal|1
argument_list|,
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|count1
argument_list|,
name|array
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|long
name|since2
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|int
name|count2
init|=
literal|4
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
name|count2
condition|;
name|i
operator|++
control|)
block|{
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"b"
operator|+
name|i
operator|+
literal|"\" : {}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|array
operator|=
name|parseJSONArray
argument_list|(
name|mk
operator|.
name|getRevisionHistory
argument_list|(
name|since2
argument_list|,
operator|-
literal|1
argument_list|,
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|count2
argument_list|,
name|array
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|array
operator|=
name|parseJSONArray
argument_list|(
name|mk
operator|.
name|getRevisionHistory
argument_list|(
name|since1
argument_list|,
operator|-
literal|1
argument_list|,
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|count1
operator|+
name|count2
argument_list|,
name|array
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

