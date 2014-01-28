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
name|plugins
operator|.
name|document
package|;
end_package

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

begin_comment
comment|/**  * Test case for OAK-1170.  */
end_comment

begin_class
specifier|public
class|class
name|ClusterJoinTest
extends|extends
name|AbstractMongoConnectionTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|nodeJoins
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|rev1
decl_stmt|,
name|rev2
decl_stmt|,
name|rev3
decl_stmt|;
comment|// perform manual background ops
name|mk
operator|.
name|getNodeStore
argument_list|()
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|rev1
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"foo\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// start a new DocumentMK instance. this instance sees /foo
comment|// because it started after the commit on the first DocumentMK
name|DocumentMK
name|mk2
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|setAsyncDelay
argument_list|(
literal|0
argument_list|)
operator|.
name|setMongoDB
argument_list|(
name|MongoUtils
operator|.
name|getConnection
argument_list|()
operator|.
name|getDB
argument_list|()
argument_list|)
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
comment|// this creates a first commit from the second DocumentMK instance
comment|// the first DocumentMK instance does not see this yet
name|mk2
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"bar\":{}+\"bla\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// create a commit on the first DocumentMK. this commit revision
comment|// is higher than the previous commit on the second DocumentMK
name|rev2
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+\"baz\":{}+\"qux\":{}"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// @rev1 must only see /foo
name|assertChildNodeCount
argument_list|(
literal|"/"
argument_list|,
name|rev1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// read children @rev2, should not contain /bar, /bla
comment|// because there was no background read yet
name|JSONObject
name|obj
init|=
name|parseJSONObject
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
name|rev2
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
comment|// make changes from second DocumentMK visible
name|mk2
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
name|mk
operator|.
name|runBackgroundOperations
argument_list|()
expr_stmt|;
comment|// check child nodes of previous getNodes() call
for|for
control|(
name|Object
name|key
range|:
name|obj
operator|.
name|keySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|key
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|":"
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|assertNodesExist
argument_list|(
name|rev2
argument_list|,
literal|"/"
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
comment|// must only see /foo, /baz and /qux @rev2
name|assertEquals
argument_list|(
literal|3L
argument_list|,
name|obj
operator|.
name|get
argument_list|(
literal|":childNodeCount"
argument_list|)
argument_list|)
expr_stmt|;
comment|// @rev3 is after background read
name|rev3
operator|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
expr_stmt|;
comment|// now all nodes must be visible
name|obj
operator|=
name|parseJSONObject
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/"
argument_list|,
name|rev3
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|key
range|:
name|obj
operator|.
name|keySet
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|key
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|":"
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|assertNodesExist
argument_list|(
name|rev3
argument_list|,
literal|"/"
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
comment|// must now see all nodes @rev3
name|assertEquals
argument_list|(
literal|5L
argument_list|,
name|obj
operator|.
name|get
argument_list|(
literal|":childNodeCount"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|mk2
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

