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
name|cluster
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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
name|client
operator|.
name|Client
import|;
end_import

begin_class
specifier|public
class|class
name|ClusterNode
block|{
specifier|private
name|MicroKernel
name|mk
decl_stmt|;
specifier|private
name|String
name|lastRevision
decl_stmt|;
specifier|public
name|ClusterNode
parameter_list|(
name|MicroKernel
name|mk
parameter_list|)
block|{
name|this
operator|.
name|mk
operator|=
name|mk
expr_stmt|;
block|}
specifier|public
name|void
name|join
parameter_list|(
name|InetSocketAddress
name|addr
parameter_list|)
block|{
name|Client
name|client
init|=
operator|new
name|Client
argument_list|(
name|addr
argument_list|)
decl_stmt|;
name|String
name|headRevision
init|=
name|client
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastRevision
operator|==
literal|null
condition|)
block|{
name|lastRevision
operator|=
name|headRevision
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

