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
name|mk
operator|.
name|model
operator|.
name|tree
package|;
end_package

begin_comment
comment|/**  * Abstract base class for {@link NodeState} implementations.  * This base class contains default implementations of the  * {@link #equals(Object)} and {@link #hashCode()} methods based on  * the implemented interface.  *<p>  * This class also implements trivial (and potentially very slow) versions of  * the {@link #getProperty(String)} and {@link #getPropertyCount()} methods  * based on {@link #getProperties()}. The {@link #getChildNode(String)} and  * {@link #getChildNodeCount()} methods are similarly implemented based on  * {@link #getChildNodeEntries(long, int)}. Subclasses should normally  * override these method with a more efficient alternatives.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractNodeState
implements|implements
name|NodeState
block|{
annotation|@
name|Override
specifier|public
name|PropertyState
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|PropertyState
name|property
range|:
name|getProperties
argument_list|()
control|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|property
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|public
name|long
name|getPropertyCount
parameter_list|()
block|{
name|long
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|PropertyState
name|property
range|:
name|getProperties
argument_list|()
control|)
block|{
name|count
operator|++
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeState
name|getChildNode
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|ChildNode
name|entry
range|:
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
control|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|entry
operator|.
name|getNode
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|public
name|long
name|getChildNodeCount
parameter_list|()
block|{
name|long
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ChildNode
name|entry
range|:
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
control|)
block|{
name|count
operator|++
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
comment|/**      * Checks whether the given object is equal to this one. Two node states      * are considered equal if all their properties and child nodes match,      * regardless of ordering. Subclasses may override this method with a      * more efficient equality check if one is available.      *      * @param that target of the comparison      * @return {@code true} if the objects are equal,      *         {@code false} otherwise      */
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
operator|==
literal|null
operator|||
operator|!
operator|(
name|that
operator|instanceof
name|NodeState
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|NodeState
name|other
init|=
operator|(
name|NodeState
operator|)
name|that
decl_stmt|;
name|long
name|propertyCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|PropertyState
name|property
range|:
name|getProperties
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|property
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getProperty
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|propertyCount
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|propertyCount
operator|!=
name|other
operator|.
name|getPropertyCount
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|long
name|childNodeCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ChildNode
name|entry
range|:
name|getChildNodeEntries
argument_list|(
literal|0
argument_list|,
operator|-
literal|1
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|entry
operator|.
name|getNode
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getChildNode
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|childNodeCount
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|childNodeCount
operator|!=
name|other
operator|.
name|getChildNodeCount
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Returns a hash code that's compatible with how the      * {@link #equals(Object)} method is implemented. The current      * implementation simply returns zero for everything since      * {@link NodeState} instances are not intended for use as hash keys.      *      * @return hash code      */
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

