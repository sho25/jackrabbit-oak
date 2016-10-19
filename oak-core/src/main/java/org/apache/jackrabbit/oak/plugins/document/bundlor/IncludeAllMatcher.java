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
name|commons
operator|.
name|PathUtils
operator|.
name|concat
import|;
end_import

begin_comment
comment|/**  * Matcher which matches all child nodes  */
end_comment

begin_class
class|class
name|IncludeAllMatcher
implements|implements
name|Matcher
block|{
specifier|private
specifier|final
name|String
name|matchingPath
decl_stmt|;
specifier|private
specifier|final
name|int
name|depth
decl_stmt|;
name|IncludeAllMatcher
parameter_list|(
name|String
name|matchingPath
parameter_list|,
name|int
name|depth
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|depth
operator|>
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|matchingPath
operator|=
name|matchingPath
expr_stmt|;
name|this
operator|.
name|depth
operator|=
name|depth
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
return|return
operator|new
name|IncludeAllMatcher
argument_list|(
name|concat
argument_list|(
name|matchingPath
argument_list|,
name|name
argument_list|)
argument_list|,
name|depth
operator|+
literal|1
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
return|return
name|matchingPath
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
name|depth
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matchesChildren
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

