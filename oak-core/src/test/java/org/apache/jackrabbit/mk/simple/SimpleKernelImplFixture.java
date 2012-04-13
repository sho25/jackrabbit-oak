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
name|mk
operator|.
name|simple
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|mk
operator|.
name|test
operator|.
name|MicroKernelFixture
import|;
end_import

begin_class
specifier|public
class|class
name|SimpleKernelImplFixture
implements|implements
name|MicroKernelFixture
block|{
annotation|@
name|Override
specifier|public
name|void
name|setUpCluster
parameter_list|(
name|MicroKernel
index|[]
name|cluster
parameter_list|)
block|{
name|MicroKernel
name|mk
init|=
operator|new
name|SimpleKernelImpl
argument_list|(
literal|"mem:SimpleKernelImplFixture"
argument_list|)
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
name|cluster
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|cluster
index|[
name|i
index|]
operator|=
name|mk
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|syncMicroKernelCluster
parameter_list|(
name|MicroKernel
modifier|...
name|nodes
parameter_list|)
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|tearDownCluster
parameter_list|(
name|MicroKernel
index|[]
name|cluster
parameter_list|)
block|{     }
block|}
end_class

end_unit

