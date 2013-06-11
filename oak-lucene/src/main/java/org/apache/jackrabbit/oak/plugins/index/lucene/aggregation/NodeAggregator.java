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
name|lucene
operator|.
name|aggregation
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_CONTENT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|NT_FILE
import|;
end_import

begin_import
import|import static
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
name|IndexUtils
operator|.
name|getString
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|NodeState
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
name|ImmutableList
import|;
end_import

begin_class
specifier|public
class|class
name|NodeAggregator
block|{
specifier|public
name|List
argument_list|<
name|AggregatedState
argument_list|>
name|getAggregates
parameter_list|(
name|NodeState
name|state
parameter_list|)
block|{
comment|// FIXME remove hardcoded aggregates
name|String
name|type
init|=
name|getString
argument_list|(
name|state
argument_list|,
name|JCR_PRIMARYTYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|NT_FILE
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
comment|// include jcr:content node
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|AggregatedState
argument_list|(
name|JCR_CONTENT
argument_list|,
name|state
operator|.
name|getChildNode
argument_list|(
name|JCR_CONTENT
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
block|}
end_class

end_unit

