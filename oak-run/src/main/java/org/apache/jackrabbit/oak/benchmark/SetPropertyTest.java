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
name|benchmark
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

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
name|RepositoryException
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

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeTypeManager
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_comment
comment|/**  * Test for measuring the performance of setting a single property and  * saving the change.  */
end_comment

begin_class
specifier|public
class|class
name|SetPropertyTest
extends|extends
name|AbstractTest
block|{
specifier|private
name|Map
argument_list|<
name|Thread
argument_list|,
name|Node
argument_list|>
name|nodes
init|=
name|Maps
operator|.
name|newIdentityHashMap
argument_list|()
decl_stmt|;
name|String
name|testNodeName
init|=
literal|"test"
operator|+
name|TEST_ID
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|beforeSuite
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getRepository
argument_list|()
operator|.
name|login
argument_list|(
name|getCredentials
argument_list|()
argument_list|)
decl_stmt|;
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
name|testNodeName
argument_list|,
name|getUnstructuredNodeType
argument_list|(
name|session
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeTest
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Thread
name|t
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
name|Node
name|node
init|=
name|nodes
operator|.
name|get
argument_list|(
name|t
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
name|Session
name|s
init|=
name|getRepository
argument_list|()
operator|.
name|login
argument_list|(
name|getCredentials
argument_list|()
argument_list|)
decl_stmt|;
name|node
operator|=
name|s
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
name|testNodeName
argument_list|)
operator|.
name|addNode
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|setProperty
argument_list|(
literal|"count"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|s
operator|.
name|save
argument_list|()
expr_stmt|;
name|nodes
operator|.
name|put
argument_list|(
name|t
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|runTest
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|node
init|=
name|nodes
operator|.
name|get
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
decl_stmt|;
name|Session
name|session
init|=
name|node
operator|.
name|getSession
argument_list|()
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|node
operator|.
name|setProperty
argument_list|(
literal|"count"
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterSuite
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getRepository
argument_list|()
operator|.
name|login
argument_list|(
name|getCredentials
argument_list|()
argument_list|)
decl_stmt|;
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
name|testNodeName
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|session
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
specifier|private
name|String
name|getUnstructuredNodeType
parameter_list|(
name|Session
name|s
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|NodeTypeManager
name|ntMgr
init|=
name|s
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getNodeTypeManager
argument_list|()
decl_stmt|;
if|if
condition|(
name|ntMgr
operator|.
name|hasNodeType
argument_list|(
literal|"oak:Unstructured"
argument_list|)
condition|)
block|{
return|return
literal|"oak:Unstructured"
return|;
block|}
else|else
block|{
return|return
literal|"nt:unstructured"
return|;
block|}
block|}
block|}
end_class

end_unit

