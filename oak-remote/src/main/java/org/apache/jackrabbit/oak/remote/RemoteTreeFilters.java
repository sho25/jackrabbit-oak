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
name|remote
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_comment
comment|/**  * Represents a set of filters that can be applied when a subtree is read from  * the repository.  */
end_comment

begin_class
specifier|public
class|class
name|RemoteTreeFilters
block|{
comment|/**      * Return the depth of the tree to read. This method returns {@code 0} by      * default, meaning that only the root of the subtree will be returned. The      * default value makes read operation for a subtree look like the read      * operation for a single node.      */
specifier|public
name|int
name|getDepth
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/**      * Return the property filters. This method returns {@code {"*"}} by      * default, meaning that every property will be returned for every node      * included in the subtree.      */
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getPropertyFilters
parameter_list|()
block|{
return|return
name|newHashSet
argument_list|(
literal|"*"
argument_list|)
return|;
block|}
comment|/**      * Return the node filters. This method returns a value of {@code {"*"}} by      * default, meaning that every descendant node of the root of the tree wil      * be returned.      */
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getNodeFilters
parameter_list|()
block|{
return|return
name|newHashSet
argument_list|(
literal|"*"
argument_list|)
return|;
block|}
comment|/**      * Return the binary threshold. This method returns {@code 0} by default,      * meaning that by default binary properties will be returned as references      * to binary objects, instead of being returned as proper binary objects.      */
specifier|public
name|long
name|getBinaryThreshold
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/**      * Return the start index for children. This method returns {@code 0} by      * default, meaning that children will be read from the beginning when      * reading the root node and every descendant.      */
specifier|public
name|int
name|getChildrenStart
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/**      * Return the maximum number of children to return. This method returns      * {@code -1} by default, meaning that every children will be returned when      * reading the root node and every descendant.      */
specifier|public
name|int
name|getChildrenCount
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
end_class

end_unit

