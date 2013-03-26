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
name|plugins
operator|.
name|index
operator|.
name|solr
operator|.
name|server
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
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|NodeState
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
name|spi
operator|.
name|state
operator|.
name|NodeStore
import|;
end_import

begin_comment
comment|/**  * A {@link OakSolrNodeStateConfiguration} whose {@link NodeState} is retrieved  * via the {@link NodeStore} and a given<code>String</code> path.  */
end_comment

begin_class
specifier|public
class|class
name|UpToDateNodeStateConfiguration
extends|extends
name|OakSolrNodeStateConfiguration
block|{
specifier|private
specifier|final
name|NodeStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|public
name|UpToDateNodeStateConfiguration
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|NodeState
name|getConfigurationNodeState
parameter_list|()
block|{
name|NodeState
name|currentState
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|child
range|:
name|path
operator|.
name|split
argument_list|(
literal|"/"
argument_list|)
control|)
block|{
name|currentState
operator|=
name|currentState
operator|.
name|getChildNode
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
return|return
name|currentState
return|;
block|}
block|}
end_class

end_unit

