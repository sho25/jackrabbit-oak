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
name|benchmark
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_comment
comment|/**  * {@code GetDeepNodeTest} implements a performance test, which reads  * a node deep down in the hierarchy.  */
end_comment

begin_class
specifier|public
class|class
name|GetDeepNodeTest
extends|extends
name|AbstractTest
block|{
specifier|private
specifier|static
specifier|final
name|int
name|DEPTH
init|=
literal|20
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|Node
name|testRoot
decl_stmt|;
specifier|private
name|String
name|testPath
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|beforeSuite
parameter_list|()
throws|throws
name|Exception
block|{
name|session
operator|=
name|loginWriter
argument_list|()
expr_stmt|;
name|testRoot
operator|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|GetNodeTest
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
literal|"nt:unstructured"
argument_list|)
expr_stmt|;
name|Node
name|node
init|=
name|testRoot
decl_stmt|;
name|testPath
operator|=
literal|""
expr_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|DEPTH
condition|;
name|k
operator|++
control|)
block|{
name|node
operator|=
name|node
operator|.
name|addNode
argument_list|(
literal|"node"
operator|+
name|k
argument_list|)
expr_stmt|;
block|}
name|testPath
operator|=
name|node
operator|.
name|getPath
argument_list|()
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|runTest
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|session
operator|.
name|getNode
argument_list|(
name|testPath
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|afterSuite
parameter_list|()
throws|throws
name|Exception
block|{
name|testRoot
operator|.
name|remove
argument_list|()
expr_stmt|;
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

