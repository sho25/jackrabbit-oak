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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
comment|/**  * Tests getChildNodeCount.  */
end_comment

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"MongoMK does not implement getChildNodeCount()"
argument_list|)
specifier|public
class|class
name|MongoMKGetChildCountTest
extends|extends
name|AbstractMongoConnectionTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|noChild
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|childCount
init|=
name|mk
operator|.
name|getChildNodeCount
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|childCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|singleChild
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
name|long
name|childCount
init|=
name|mk
operator|.
name|getChildNodeCount
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|childCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|multipleChilden
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
literal|"+\"a\" : { \"b\": {}, \"c\": {}, \"d\" : {} }"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|long
name|childCount
init|=
name|mk
operator|.
name|getChildNodeCount
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|childCount
argument_list|)
expr_stmt|;
name|childCount
operator|=
name|mk
operator|.
name|getChildNodeCount
argument_list|(
literal|"/a"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|childCount
argument_list|)
expr_stmt|;
name|childCount
operator|=
name|mk
operator|.
name|getChildNodeCount
argument_list|(
literal|"/a/b"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|childCount
argument_list|)
expr_stmt|;
name|childCount
operator|=
name|mk
operator|.
name|getChildNodeCount
argument_list|(
literal|"/a/c"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|childCount
argument_list|)
expr_stmt|;
name|childCount
operator|=
name|mk
operator|.
name|getChildNodeCount
argument_list|(
literal|"/a/d"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|childCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|multipleNestedChildren
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
literal|"+\"a\" : { \"b\": { \"c\" : { \"d\" : {} } } }"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|long
name|childCount
init|=
name|mk
operator|.
name|getChildNodeCount
argument_list|(
literal|"/"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|childCount
argument_list|)
expr_stmt|;
name|childCount
operator|=
name|mk
operator|.
name|getChildNodeCount
argument_list|(
literal|"/a"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|childCount
argument_list|)
expr_stmt|;
name|childCount
operator|=
name|mk
operator|.
name|getChildNodeCount
argument_list|(
literal|"/a/b"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|childCount
argument_list|)
expr_stmt|;
name|childCount
operator|=
name|mk
operator|.
name|getChildNodeCount
argument_list|(
literal|"/a/b/c"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|childCount
argument_list|)
expr_stmt|;
name|childCount
operator|=
name|mk
operator|.
name|getChildNodeCount
argument_list|(
literal|"/a/b/c/d"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|childCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nonExistingPath
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
try|try
block|{
name|mk
operator|.
name|getChildNodeCount
argument_list|(
literal|"/nonexisting"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected: non-existing path exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|expected
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
end_class

end_unit

