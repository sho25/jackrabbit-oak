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
name|BaseMongoMicroKernelTest
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
name|json
operator|.
name|simple
operator|.
name|JSONObject
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
comment|/**  * Tests for {@code MongoMicroKernel#getJournal(String, String, String)}  */
end_comment

begin_class
specifier|public
class|class
name|MongoMKGetJournalTest
extends|extends
name|BaseMongoMicroKernelTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|simple
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|fromDiff
init|=
literal|"+\"a\" : {}"
decl_stmt|;
name|String
name|fromMsg
init|=
literal|"Add /a"
decl_stmt|;
name|String
name|fromRev
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
name|fromDiff
argument_list|,
literal|null
argument_list|,
name|fromMsg
argument_list|)
decl_stmt|;
name|String
name|toDiff
init|=
literal|"+\"b\" : {}"
decl_stmt|;
name|String
name|toMsg
init|=
literal|"Add /b"
decl_stmt|;
name|String
name|toRev
init|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
name|toDiff
argument_list|,
literal|null
argument_list|,
name|toMsg
argument_list|)
decl_stmt|;
name|JSONArray
name|array
init|=
name|parseJSONArray
argument_list|(
name|mk
operator|.
name|getJournal
argument_list|(
name|fromRev
argument_list|,
name|toRev
argument_list|,
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|array
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|JSONObject
name|rev
init|=
name|getObjectArrayEntry
argument_list|(
name|array
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertPropertyExists
argument_list|(
name|rev
argument_list|,
literal|"id"
argument_list|,
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertPropertyExists
argument_list|(
name|rev
argument_list|,
literal|"ts"
argument_list|,
name|Long
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertPropertyValue
argument_list|(
name|rev
argument_list|,
literal|"msg"
argument_list|,
name|fromMsg
argument_list|)
expr_stmt|;
name|assertPropertyValue
argument_list|(
name|rev
argument_list|,
literal|"changes"
argument_list|,
name|fromDiff
argument_list|)
expr_stmt|;
name|rev
operator|=
name|getObjectArrayEntry
argument_list|(
name|array
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertPropertyExists
argument_list|(
name|rev
argument_list|,
literal|"id"
argument_list|,
name|String
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertPropertyExists
argument_list|(
name|rev
argument_list|,
literal|"ts"
argument_list|,
name|Long
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertPropertyValue
argument_list|(
name|rev
argument_list|,
literal|"msg"
argument_list|,
name|toMsg
argument_list|)
expr_stmt|;
name|assertPropertyValue
argument_list|(
name|rev
argument_list|,
literal|"changes"
argument_list|,
name|toDiff
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

