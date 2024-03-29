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
name|spi
operator|.
name|state
package|;
end_package

begin_comment
comment|/**  * Abstract base class for {@link ChildNodeEntry} implementations.  * This base class contains default implementations of the  * {@link #equals(Object)} and {@link #hashCode()} methods based on  * the implemented interface.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractChildNodeEntry
implements|implements
name|ChildNodeEntry
block|{
comment|/**      * Returns a string representation of this child node entry.      *      * @return string representation      */
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|name
init|=
name|getName
argument_list|()
decl_stmt|;
name|NodeState
name|state
init|=
name|getNodeState
argument_list|()
decl_stmt|;
if|if
condition|(
name|state
operator|.
name|getChildNodeCount
argument_list|(
literal|1
argument_list|)
operator|==
literal|0
condition|)
block|{
return|return
name|name
operator|+
literal|" : "
operator|+
name|state
return|;
block|}
else|else
block|{
return|return
name|name
operator|+
literal|" = { ... }"
return|;
block|}
block|}
comment|/**      * Checks whether the given object is equal to this one. Two child node      * entries are considered equal if both their names and referenced node      * states match. Subclasses may override this method with a more efficient      * equality check if one is available.      *      * @param that target of the comparison      * @return {@code true} if the objects are equal,      *         {@code false} otherwise      */
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|that
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|that
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|that
operator|instanceof
name|ChildNodeEntry
condition|)
block|{
name|ChildNodeEntry
name|other
init|=
operator|(
name|ChildNodeEntry
operator|)
name|that
decl_stmt|;
return|return
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getName
argument_list|()
argument_list|)
operator|&&
name|getNodeState
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getNodeState
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**      * Returns a hash code that's compatible with how the      * {@link #equals(Object)} method is implemented. The current      * implementation simply returns the hash code of the child node name      * since {@link ChildNodeEntry} instances are not intended for use as      * hash keys.      *      * @return hash code      */
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getName
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

