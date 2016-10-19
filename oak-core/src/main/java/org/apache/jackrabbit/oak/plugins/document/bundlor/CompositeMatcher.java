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
name|plugins
operator|.
name|document
operator|.
name|bundlor
package|;
end_package

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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

begin_class
class|class
name|CompositeMatcher
implements|implements
name|Matcher
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|Matcher
argument_list|>
name|matchers
decl_stmt|;
specifier|public
specifier|static
name|Matcher
name|compose
parameter_list|(
name|List
argument_list|<
name|Matcher
argument_list|>
name|matchers
parameter_list|)
block|{
switch|switch
condition|(
name|matchers
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
return|return
name|Matcher
operator|.
name|NON_MATCHING
return|;
case|case
literal|1
case|:
return|return
name|matchers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
default|default:
return|return
operator|new
name|CompositeMatcher
argument_list|(
name|matchers
argument_list|)
return|;
block|}
block|}
comment|/**      * A CompositeMatcher must only be constructed when all passed      * matchers are matching      */
specifier|private
name|CompositeMatcher
parameter_list|(
name|List
argument_list|<
name|Matcher
argument_list|>
name|matchers
parameter_list|)
block|{
for|for
control|(
name|Matcher
name|m
range|:
name|matchers
control|)
block|{
name|checkArgument
argument_list|(
name|m
operator|.
name|isMatch
argument_list|()
argument_list|,
literal|"Non matching matcher found in [%s]"
argument_list|,
name|matchers
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|matchers
operator|=
name|matchers
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Matcher
name|next
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|List
argument_list|<
name|Matcher
argument_list|>
name|nextSet
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|matchers
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Matcher
name|current
range|:
name|matchers
control|)
block|{
name|Matcher
name|next
init|=
name|current
operator|.
name|next
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|next
operator|.
name|isMatch
argument_list|()
condition|)
block|{
name|nextSet
operator|.
name|add
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|compose
argument_list|(
name|nextSet
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMatch
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getMatchedPath
parameter_list|()
block|{
comment|//All matchers would have traversed same path. So use any one to
comment|//determine the matching path
return|return
name|matchers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getMatchedPath
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|depth
parameter_list|()
block|{
return|return
name|matchers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|depth
argument_list|()
return|;
block|}
block|}
end_class

end_unit

