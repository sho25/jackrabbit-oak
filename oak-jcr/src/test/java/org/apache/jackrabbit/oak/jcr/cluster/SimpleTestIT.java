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
name|jcr
operator|.
name|cluster
package|;
end_package

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
comment|/**  * A simple cluster test.  */
end_comment

begin_class
specifier|public
class|class
name|SimpleTestIT
extends|extends
name|AbstractClusterTest
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|s1
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|s1
operator|.
name|getRootNode
argument_list|()
operator|.
name|hasNode
argument_list|(
literal|"test"
argument_list|)
condition|)
block|{
name|s1
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|s1
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|s1
operator|.
name|save
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|x
range|:
name|seconds
argument_list|(
literal|5
argument_list|)
control|)
block|{
name|s2
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|s2
operator|.
name|getRootNode
argument_list|()
operator|.
name|hasNode
argument_list|(
literal|"test"
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
name|s2
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|s2
operator|.
name|save
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|x
range|:
name|seconds
argument_list|(
literal|5
argument_list|)
control|)
block|{
name|s1
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|s1
operator|.
name|getRootNode
argument_list|()
operator|.
name|hasNode
argument_list|(
literal|"test"
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
name|s1
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|s1
operator|.
name|save
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|x
range|:
name|seconds
argument_list|(
literal|5
argument_list|)
control|)
block|{
name|s2
operator|.
name|refresh
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|s2
operator|.
name|getRootNode
argument_list|()
operator|.
name|hasNode
argument_list|(
literal|"test"
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
name|s2
operator|.
name|getRootNode
argument_list|()
operator|.
name|getNode
argument_list|(
literal|"test"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|s2
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

