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
name|base
operator|.
name|Preconditions
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

begin_class
specifier|public
class|class
name|DocumentBundlor
block|{
specifier|public
specifier|static
specifier|final
name|String
name|PROP_PATTERN
init|=
literal|"pattern"
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Include
argument_list|>
name|includes
decl_stmt|;
specifier|public
name|DocumentBundlor
parameter_list|(
name|List
argument_list|<
name|Include
argument_list|>
name|includes
parameter_list|)
block|{
comment|//TODO Have assertion for that all intermediate paths are included
name|this
operator|.
name|includes
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|includes
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|DocumentBundlor
name|from
parameter_list|(
name|NodeState
name|nodeState
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|nodeState
operator|.
name|hasProperty
argument_list|(
name|PROP_PATTERN
argument_list|)
argument_list|,
literal|"NodeStated [%s] does not have required "
operator|+
literal|"property [%s]"
argument_list|,
name|nodeState
argument_list|,
name|PROP_PATTERN
argument_list|)
expr_stmt|;
return|return
name|DocumentBundlor
operator|.
name|from
argument_list|(
name|nodeState
operator|.
name|getStrings
argument_list|(
name|PROP_PATTERN
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|DocumentBundlor
name|from
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|includeStrings
parameter_list|)
block|{
name|List
argument_list|<
name|Include
argument_list|>
name|includes
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|i
range|:
name|includeStrings
control|)
block|{
name|includes
operator|.
name|add
argument_list|(
operator|new
name|Include
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|DocumentBundlor
argument_list|(
name|includes
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isBundled
parameter_list|(
name|String
name|relativePath
parameter_list|)
block|{
for|for
control|(
name|Include
name|include
range|:
name|includes
control|)
block|{
if|if
condition|(
name|include
operator|.
name|match
argument_list|(
name|relativePath
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
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
name|includes
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

