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
name|privilege
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|ImmutableSet
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
name|security
operator|.
name|privilege
operator|.
name|PrivilegeDefinition
import|;
end_import

begin_comment
comment|/**  * PrivilegeDefinitionImpl... TODO  */
end_comment

begin_class
class|class
name|PrivilegeDefinitionImpl
implements|implements
name|PrivilegeDefinition
block|{
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isAbstract
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|declaredAggregateNames
decl_stmt|;
name|PrivilegeDefinitionImpl
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isAbstract
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|declaredAggregateNames
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|isAbstract
operator|=
name|isAbstract
expr_stmt|;
name|this
operator|.
name|declaredAggregateNames
operator|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|declaredAggregateNames
argument_list|)
expr_stmt|;
block|}
name|PrivilegeDefinitionImpl
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|isAbstract
parameter_list|,
name|String
modifier|...
name|declaredAggregateNames
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|isAbstract
operator|=
name|isAbstract
expr_stmt|;
name|this
operator|.
name|declaredAggregateNames
operator|=
operator|(
name|declaredAggregateNames
operator|==
literal|null
operator|)
condition|?
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
else|:
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|declaredAggregateNames
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------< PrivilegeDefinition>---
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAbstract
parameter_list|()
block|{
return|return
name|isAbstract
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getDeclaredAggregateNames
parameter_list|()
block|{
return|return
name|declaredAggregateNames
return|;
block|}
comment|//-------------------------------------------------------------< Object>---
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|name
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|isAbstract
condition|?
literal|1
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|declaredAggregateNames
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|instanceof
name|PrivilegeDefinitionImpl
condition|)
block|{
name|PrivilegeDefinitionImpl
name|other
init|=
operator|(
name|PrivilegeDefinitionImpl
operator|)
name|o
decl_stmt|;
return|return
name|name
operator|.
name|equals
argument_list|(
name|other
operator|.
name|name
argument_list|)
operator|&&
name|isAbstract
operator|==
name|other
operator|.
name|isAbstract
operator|&&
name|declaredAggregateNames
operator|.
name|equals
argument_list|(
name|other
operator|.
name|declaredAggregateNames
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
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"PrivilegeDefinition: "
operator|+
name|name
return|;
block|}
block|}
end_class

end_unit

