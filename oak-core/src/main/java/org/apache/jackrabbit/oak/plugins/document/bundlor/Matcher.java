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

begin_interface
specifier|public
interface|interface
name|Matcher
block|{
name|Matcher
name|NON_MATCHING
init|=
operator|new
name|Matcher
argument_list|()
block|{
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
name|NON_MATCHING
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
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getMatchedPath
parameter_list|()
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No matching path for non matching matcher"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|depth
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matchesAllChildren
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"NON_MATCHING"
return|;
block|}
block|}
decl_stmt|;
comment|/**      * Returns a matcher for given child node name based on current state      *      * @param name child node name      * @return child matcher      */
name|Matcher
name|next
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Returns true if there was a match wrt current child node path      */
name|boolean
name|isMatch
parameter_list|()
function_decl|;
comment|/**      * Relative node path from the bundling root if      * there was a match      */
name|String
name|getMatchedPath
parameter_list|()
function_decl|;
comment|/**      * Matcher depth. For match done for 'x/y' depth is 2      */
name|int
name|depth
parameter_list|()
function_decl|;
comment|/**      * Returns true if matcher for all immediate child node      * would also be a matching matcher. This would be the      * case if IncludeMatcher with '*' or '**' as pattern for      * child nodes      */
name|boolean
name|matchesAllChildren
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

