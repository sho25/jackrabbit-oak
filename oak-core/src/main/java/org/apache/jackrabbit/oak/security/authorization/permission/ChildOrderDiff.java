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
name|oak
operator|.
name|security
operator|.
name|authorization
operator|.
name|permission
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

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
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|PropertyState
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
name|api
operator|.
name|Type
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
name|plugins
operator|.
name|tree
operator|.
name|TreeConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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
name|newLinkedHashSet
import|;
end_import

begin_comment
comment|/**  * Helper class to handle modifications to the hidden  * {@link TreeConstants#OAK_CHILD_ORDER} property.  */
end_comment

begin_class
specifier|final
class|class
name|ChildOrderDiff
block|{
specifier|private
name|ChildOrderDiff
parameter_list|()
block|{}
comment|/**      * Tests if there was any user-supplied reordering involved with the      * modification of the {@link TreeConstants#OAK_CHILD_ORDER}      * property.      *      * @param before      * @param after      * @return the name of the first reordered child if any user-supplied node      * reorder happened; {@code null} otherwise.      */
annotation|@
name|Nullable
specifier|static
name|String
name|firstReordered
parameter_list|(
annotation|@
name|NotNull
name|PropertyState
name|before
parameter_list|,
annotation|@
name|NotNull
name|PropertyState
name|after
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|afterNames
init|=
name|newLinkedHashSet
argument_list|(
name|after
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|beforeNames
init|=
name|newLinkedHashSet
argument_list|(
name|before
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|NAMES
argument_list|)
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|a
init|=
name|afterNames
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|b
init|=
name|beforeNames
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|a
operator|.
name|hasNext
argument_list|()
operator|&&
name|b
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|aName
init|=
name|a
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|bName
init|=
name|b
operator|.
name|next
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|aName
operator|.
name|equals
argument_list|(
name|bName
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|beforeNames
operator|.
name|contains
argument_list|(
name|aName
argument_list|)
condition|)
block|{
if|if
condition|(
name|a
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|aName
operator|=
name|a
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|afterNames
operator|.
name|contains
argument_list|(
name|bName
argument_list|)
condition|)
block|{
if|if
condition|(
name|b
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|bName
operator|=
name|b
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
return|return
name|aName
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

